(ns todomvc.components.todos-list
  (:require [todomvc.components.todo-item :as todo-item]
            [re-learn.core :as re-learn]))

(def component
  (re-learn/with-lesson
    {:id :todo-item-lesson
     :description "These are the items in your todo list"
     :position :left
     :attach [:#todo-list :li]}
    (fn [todos]
      [:ul#todo-list
       (for [todo todos]
         ^{:key (:id todo)}
         [todo-item/component todo])])))
