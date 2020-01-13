(ns re-learn.model
  (:require [re-frame.core :as re-frame]
            [re-learn.local-storage :as local-storage]
            [re-learn.schema :refer [ReLearnModel]]
            [re-learn.dom :refer [->absolute-bounds in-viewport? viewport-height viewport-width]]
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
                         {:db (assoc db :lessons-learned {} :accepted-tutorials #{})
                          ::local-storage/save [:re-learn/lessons-learned {}]}))

(re-frame/reg-fx ::on-dom-event
                 (fn [[action dom-node on-event]]
                   (dom/listen! dom-node action on-event)))

(re-frame/reg-fx ::unlisten-dom-event
                 (fn [[action dom-node on-event]]
                   (dom/unlisten! dom-node action on-event)))

(re-frame/reg-fx ::lesson-callback
                 (fn [[lesson callback-name]]
                   (when-let [callback (get lesson callback-name)]
                     (callback lesson))))

(re-frame/reg-fx ::scroll-to-dom-node
                 (fn [[dom-node]]
                   (when (not (in-viewport? dom-node))
                     (let [{:keys [top left]} (->absolute-bounds dom-node)]
                       (js/window.scrollTo (- left (/ (viewport-width) 2))
                                           (- top (/ (viewport-height) 2)))))))

(re-frame/reg-event-fx ::deregister-dom-event
                       interceptors
                       (fn [{:keys [db]} [event dom-node on-event]]
                         {:db db
                          ::unlisten-dom-event [event dom-node on-event]}))

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
                       (fn [{:keys [db]} [lesson-id-or-ids]]
                         (let [lesson-ids (if (coll? lesson-id-or-ids)
                                            lesson-id-or-ids
                                            [lesson-id-or-ids])
                               lessons-learned (reduce (fn [learned lesson-id]
                                                         (assoc learned lesson-id
                                                                (get-in db [:lessons lesson-id :version])))
                                                       (:lessons-learned db)
                                                       lesson-ids)]
                           {:db (assoc db :lessons-learned lessons-learned)
                            ::local-storage/save [:re-learn/lessons-learned lessons-learned]})))

(re-frame/reg-event-fx ::lesson-unlearned
                       interceptors
                       (fn [{:keys [db]} [lesson-id]]
                         (let [lessons-learned (dissoc (:lessons-learned db) lesson-id)]
                           {:db (assoc db :lessons-learned lessons-learned)
                            ::local-storage/save [:re-learn/lessons-learned lessons-learned]})))

(re-frame/reg-event-fx ::skip-tutorial
                       interceptors
                       (fn [{:keys [db]} [tutorial-id]]
                         (let [tutorial (get-in db [:tutorials tutorial-id])]
                           (js/console.log "tutorial" (pr-str tutorial-id) (pr-str tutorial))
                           {:dispatch [::lesson-learned (:lessons tutorial)]})))

(re-frame/reg-event-db ::accept-tutorial
                       interceptors
                       (fn [db [tutorial-id]]
                         (update db :accepted-tutorials (fnil conj #{}) tutorial-id)))

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
                         (let [lesson (get-in db [:lessons lesson-id])]
                           (merge
                            {:db db
                             ::lesson-callback [lesson :on-appear]
                             ::scroll-to-dom-node [(:dom-node lesson)]}

                            (if (:continue lesson)
                              (let [{:keys [event selector event-filter]
                                     :or {event-filter identity}} (:continue lesson)
                                    dom-node (if selector (dom/sel1 selector) (:dom-node lesson))]
                                {::scroll-to-dom-node [dom-node]
                                 ::on-dom-event [event
                                                 dom-node
                                                 (fn listener [e]
                                                   (when (event-filter e)
                                                     (re-frame/dispatch [::lesson-learned lesson-id])
                                                     (re-frame/dispatch [::deregister-dom-event event dom-node listener])))]}))))))

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
                        [learned to-learn] (split-with #(already-learned? (:lessons-learned state) %) lessons)
                        accepted? (contains? (:accepted-tutorials state) (:id tutorial))]
                  lesson to-learn
                  :let [total (count lessons)]]
              {:tutorial tutorial
               :accepted? accepted?
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
