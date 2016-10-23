(ns re-learn.core
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [re-frame.std-interceptors :refer [trim-v]]
            [re-learn.views :as views]))

(re-frame/reg-event-db ::register-lesson [trim-v]
                       (fn [db [{:keys [id] :as lesson}]]
                         (update db :re-learn/lessons (fnil assoc {}) id lesson)))

(re-frame/reg-event-db ::deregister-lesson [trim-v]
                       (fn [db [lesson-id]]
                         (update db :re-learn/lessons dissoc lesson-id)))

(re-frame/reg-event-db :tutorial/lesson-learned [trim-v]
                       (fn [db [lesson-id]]
                         (update db :re-learn/lessons-learned (fnil conj #{}) lesson-id)))

(re-frame/reg-event-db ::register-tutorial [trim-v]
                       (fn [db [{:keys [id] :as tutorial}]]
                         (update db :re-learn/tutorials (fnil assoc {}) id tutorial)))

(re-frame/reg-event-db ::deregister-lesson [trim-v]
                       (fn [db [tutorial-id]]
                         (update db :re-learn/tutorials dissoc tutorial-id)))


(defn register-lesson [lesson]
  (fn [this] (re-frame/dispatch [::register-lesson (assoc lesson :dom-node (r/dom-node this))])))

(defn deregister-lesson [lesson-id]
  (fn [_] (re-frame/dispatch [::deregister-lesson lesson-id])))

(defn register-tutorial [tutorial]
  (fn [this] (re-frame/dispatch [::register-tutorial tutorial])))

(defn deregister-tutorial [tutorial-id]
  (fn [_] (re-frame/dispatch [::deregister-tutorial tutorial-id])))

(re-frame/reg-sub
 :tutorial/current-lesson
 (fn [db]
   (->> (:re-learn/lessons db)
        vals
        (remove (comp (or (:re-learn/lessons-learned db) #{}) :id))
        first)))

(re-frame/reg-sub
 :tutorial/current-tutorial
 (fn [db]
   (first (for [{:keys [lessons] :as tutorial} (vals (:re-learn/tutorials db))
                :let [lessons (keep (:re-learn/lessons db) lessons)]
                {:keys [id] :as lesson} lessons
                :when (not (contains? (:re-learn/lessons-learned db) id))]
            lesson))))

(def all-lessons-view views/all-lessons)
(def tutorial-view views/tutorial)
