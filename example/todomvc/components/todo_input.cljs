(ns todomvc.components.todo-input
  (:require [reagent.core :as reagent]
            [todomvc.actions :as actions]
            [todomvc.helpers :as helpers]
            [re-learn.core :as re-learn]))

(defn on-key-down [k title default]
  (let [key-pressed (.-which k)]
    (condp = key-pressed
      helpers/enter-key (actions/add-todo title default)
      nil)))

(def component
  (re-learn/with-lesson
    {:id :todo-input-lesson
     :description "Add new todo items by typing here, pressing Enter to save. Try it out now!"
     :position :bottom
     :continue {:event :keydown
                :event-filter (fn [e] (= helpers/enter-key (.-which e)))}}
    (with-meta
      (fn []
        (let [default ""
              title (reagent/atom default)]
          (fn []
            [:input#new-todo {:type "text"
                              :value @title
                              :placeholder "What needs to be done?"
                              :on-change #(reset! title (-> % .-target .-value))
                              :on-key-down #(on-key-down % title default)}])))
      {:component-did-mount #(.focus (reagent/dom-node %))})))
