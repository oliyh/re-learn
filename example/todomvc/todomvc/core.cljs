(ns todomvc.core
  (:require [reagent.core :as reagent]
            [todomvc.routes]
            [todomvc.session :as session]
            [todomvc.actions :as actions]
            [todomvc.helpers :as helpers]
            [todomvc.components.title :as title]
            [todomvc.components.todo-input :as todo-input]
            [todomvc.components.footer :as footer]
            [todomvc.components.todos-toggle :as todos-toggle]
            [todomvc.components.todos-list :as todos-list]
            [todomvc.components.todos-count :as todos-count]
            [todomvc.components.todos-filters :as todos-filters]
            [todomvc.components.todos-clear :as todos-clear]
            [re-learn.core :as re-learn]
            [re-learn.views :as re-learn-views]))

(def re-learn-controls
  (re-learn/with-lesson
    {:id :re-learn-controls-lesson
     :description "Use these controls to run the tutorial again or activate help mode"
     :position :top}
    (fn []
      [:div.re-learn-controls
       [:h3 "Tutorial controls"]
       [:p [:button {:on-click re-learn/reset-education!} "Reset tutorial"]]
       [:p [:button {:on-click re-learn/enable-help-mode!} "Help mode"]]])))

(defn- re-learn-intro []
  [:aside#re-learn-intro
   [:div {:style {:padding 24
                  :background-color "#eee"}}
    [:h3 "Hello"]
    [:p "This is the re-learn live demo, where re-learn has been used to provide a tutorial for the classic to-do list."]
    [:p "re-learn gives you data-driven tutorials for reagent components with no changes required to your dom."]
    [:p "When you've worked through the tutorial you can use the controls below to reset the tutorial to try it again or try out help mode.
        You can also refer to the source code and documentation at "
     [:a {:href "https://github.com/oliyh/re-learn"
          :target "_blank"}
      "https://github.com/oliyh/re-learn"]]
    [re-learn-controls]]])

(def todo-app
  (re-learn/with-tutorial
    {:id :todomvc-tutorial
     :name "The todo list"
     :description "Create and manage your todos"
     :lessons [{:id :welcome
                :description [:div
                              [:h2 "Welcome"]
                              "Welcome to your todo list. Let's explore what you can do!"]}
               todo-input/component
               todos-list/component
               todos-filters/component
               re-learn-controls]}
    (fn []
      [:div
       [:section#todoapp
        [:header#header
         [title/component]
         [todo-input/component]]
        [:div {:style
               {:display (helpers/display-elem (helpers/todos-any?
                                                @session/todos))}}
         [:section#main
          [todos-toggle/component]
          [todos-list/component (helpers/todos-all @session/todos)]]
         [:footer#footer
          [todos-count/component]
          [todos-filters/component]
          [todos-clear/component]]]]
       [footer/component]
       [re-learn-intro]])))

(defn- mount-all []
  (reagent/render [todo-app] (js/document.getElementById "app"))
  (reagent/render [re-learn-views/tutorial {:context? true}] (js/document.getElementById "tutorial")))

(defn- on-figwheel-reload []
  (mount-all))

(defn ^:export run []
  (re-learn/init)
  (mount-all))
