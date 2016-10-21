(ns re-learn.core
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [re-frame.std-interceptors :refer [trim-v]]
            [re-learn.views :as views]))

(re-frame/reg-event-db ::register-lesson [trim-v]
                       (fn [db [lesson]]
                         (update db :re-learn/lessons (fnil conj #{}) lesson)))

(re-frame/reg-event-db ::deregister-lesson [trim-v]
                       (fn [db [lesson-id]]
                         (update db :re-learn/lessons #(remove (comp (partial = lesson-id) :id) %))))

(re-frame/reg-event-db :tutorial/lesson-learned [trim-v]
                       (fn [db [lesson-id]]
                         (update db :re-learn/lessons-learned (fnil conj #{}) lesson-id)))

(defn register-lesson [lesson]
  (fn [this] (re-frame/dispatch [::register-lesson (assoc lesson :dom-node (r/dom-node this))])))

(defn deregister-lesson [lesson-id]
  (fn [_] (re-frame/dispatch [::deregister-lesson lesson-id])))

(re-frame/reg-sub
 :tutorial/current-lesson
 (fn [db]
   (->> (:re-learn/lessons db)
        (remove (comp (or (:re-learn/lessons-learned db) #{}) :id))
        first)))

(def tutorial-view views/tutorial)
