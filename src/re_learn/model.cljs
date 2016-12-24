(ns re-learn.model
  (:require [re-frame.core :as re-frame]
            [re-frame.std-interceptors :refer [trim-v]]
            [re-learn.local-storage :as local-storage]
            [re-learn.schema :refer [ReLearnModel]]
            [dommy.core :as dom]
            [schema.core :as s]))

(def state ::state)

(defn validate-schema [db event]
  (let [res (s/check ReLearnModel db)]
    (when (some? res)
      (.warn js/console (str "re-learn schema violated: " (pr-str res))))))

(def ^:private interceptors
  [(re-frame/path state)
   ;;re-frame/debug
   re-frame/trim-v
   (re-frame/after validate-schema)])

(re-frame/reg-event-fx ::init
                       (conj interceptors (re-frame/inject-cofx ::local-storage/load :re-learn/lessons-learned))
                       (fn [{:keys [db local-storage]}]
                         {:db (assoc db
                                     :help-mode? false
                                     :highlighted-lesson-id nil
                                     :lessons-learned (:re-learn/lessons-learned local-storage {}))}))

(re-frame/reg-event-fx ::hard-reset
                       interceptors
                       (fn [{:keys [db]}]
                         {:db (assoc db :lessons-learned {})
                          ::local-storage/save [:re-learn/lessons-learned {}]}))

(re-frame/reg-fx ::on-dom-event
                 (fn [[action selector on-event]]
                   (dom/listen-once! (dom/sel1 selector) action on-event)))

(re-frame/reg-fx ::trigger-dom-event
                 (fn [[action selector]]
                   (.dispatchEvent (dom/sel1 selector)
                                   (js/MouseEvent. (name action) #js {:bubbles true}))))

(re-frame/reg-event-db ::highlighted-lesson
                       interceptors
                       (fn [db [lesson-id]]
                         (assoc db :highlighted-lesson-id lesson-id)))

(re-frame/reg-event-db ::help-mode
                       interceptors
                       (fn [db [enabled?]]
                         (assoc db :help-mode? enabled?)))

(def ^:private lesson-defaults {:position :right
                                :version 1})

(def ^:private tutorial-defaults {:precedence 1})

(defn- add-lesson [lessons {:keys [id] :as lesson}]
  (assoc lessons id (merge lesson-defaults lesson)))

(defn- add-lessons [lessons new-lessons]
  (reduce add-lesson lessons new-lessons))

(re-frame/reg-event-db ::register-lesson
                       interceptors
                       (fn [db [{:keys [id] :as lesson}]]
                         (update db :lessons add-lesson lesson)))

(re-frame/reg-event-db ::deregister-lesson
                       interceptors
                       (fn [db [lesson-id]]
                         (update db :lessons dissoc lesson-id)))

(re-frame/reg-event-fx ::lesson-learned
                       interceptors
                       (fn [{:keys [db]} lesson-ids]
                         (let [lessons-learned (reduce (fn [learned lesson-id]
                                                         (assoc learned lesson-id
                                                                (get-in db [:lessons lesson-id :version])))
                                                       (:lessons-learned db)
                                                       lesson-ids)]
                           {:db (assoc db :lessons-learned lessons-learned)
                            ::local-storage/save [:re-learn/lessons-learned lessons-learned]})))

(re-frame/reg-event-fx ::trigger-lesson-learned
                       interceptors
                       (fn [{:keys [db]} [lesson-id]]
                         (let [{:keys [continue]} (get-in db [:lessons lesson-id])]
                           (if continue
                             {::trigger-dom-event [:click continue]
                              :db db}
                             {:dispatch [::lesson-learned lesson-id]
                              :db db}))))

(re-frame/reg-event-fx ::lesson-unlearned
                       interceptors
                       (fn [{:keys [db]} [lesson-id]]
                         (let [lessons-learned (dissoc (:lessons-learned db) lesson-id)]
                           {:db (assoc db :lessons-learned lessons-learned)
                            ::local-storage/save [:re-learn/lessons-learned lessons-learned]})))

(defn- ->lesson-id [lesson]
  (cond
    (keyword? lesson)
    lesson

    (instance? MetaFn lesson)
    (:re-learn.core/lesson-id (meta lesson))

    (map? lesson)
    (:id lesson)))

(re-frame/reg-event-db ::register-tutorial
                       interceptors
                       (fn [db [{:keys [id lessons] :as tutorial}]]
                         (let [inline-lessons (filter map? lessons)]
                           (-> db
                               (update :lessons add-lessons inline-lessons)
                               (update :tutorials assoc id
                                       (-> (merge tutorial-defaults tutorial)
                                           (update :lessons #(map ->lesson-id %))))))))

(re-frame/reg-event-db ::deregister-tutorial
                       interceptors
                       (fn [db [tutorial-id]]
                         (update db :tutorials dissoc tutorial-id)))

(re-frame/reg-event-fx ::prepare-lesson
                       interceptors
                       (fn [{:keys [db]} [lesson-id]]
                         (if-let [continue (get-in db [:lessons lesson-id :continue])]
                           {:db db
                            ::on-dom-event [:click continue #(re-frame/dispatch [::lesson-learned lesson-id])]}
                           {:db db})))

(defn- already-learned?
  ([lessons-learned]
   (fn [{:keys [id version]}]
     (and (contains? lessons-learned id)
          (<= version (get lessons-learned id)))))
  ([lessons-learned lesson]
   ((already-learned? lessons-learned) lesson)))

(re-frame/reg-sub
 ::current-lesson
 (fn [db]
   (->> (state db)
        :lessons
        vals
        (remove (already-learned? (:lessons-learned (state db))))
        first)))

(re-frame/reg-sub
 ::current-tutorial
 (fn [db]
   (let [state (state db)]
     (first (for [tutorial (->> (:tutorials state) vals (sort-by :precedence))
                  :let [lessons (keep (:lessons state) (:lessons tutorial))
                        [learned to-learn] (split-with #(already-learned? (:lessons-learned state) %) lessons)]
                  lesson to-learn
                  :let [total (count lessons)]]
              {:tutorial tutorial
               :learned learned
               :to-learn to-learn
               :completion {:ratio (/ (+ (count learned) 0.5) total)
                            :total total
                            :learned (inc (count learned))
                            :to-learn (count to-learn)}
               :current-lesson lesson
               :previous-lesson (last learned)})))))

(re-frame/reg-sub
 ::all-lessons
 (fn [db]
   (-> db state :lessons vals)))

(re-frame/reg-sub
 ::highlighted-lesson-id
 (fn [db]
   (-> db state :highlighted-lesson-id)))

(re-frame/reg-sub
 ::help-mode?
 (fn [db]
   (-> db state :help-mode?)))
