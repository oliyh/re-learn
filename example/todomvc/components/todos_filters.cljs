(ns todomvc.components.todos-filters
  (:require [todomvc.session :as session]
            [re-learn.core :as re-learn]))

(defn selected-class [display-type todos-display-type]
  (if (= display-type
         todos-display-type)
    "selected" ""))

(def component
  (re-learn/with-lesson
    {:id :filters-lesson
     :description "Filter your view of your list using these options"
     :position :bottom}
    (fn []
      [:ul#filters
       [:li [:a {:class (selected-class :all @session/todos-display-type)  :href "#/"} "All"]]
       [:li [:a {:class (selected-class :active @session/todos-display-type) :href "#/active"} "Active"]]
       [:li [:a {:class (selected-class :completed @session/todos-display-type) :href "#/completed"} "Completed"]]])))
