(ns re-learn.core
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [re-learn.model :as model]
            [dommy.core :as dom]))

(defn register-lesson [lesson]
  (fn [this] (re-frame/dispatch [::model/register-lesson (assoc lesson :dom-node (r/dom-node this))])))

(defn deregister-lesson [lesson-id]
  (fn [_] (re-frame/dispatch [::model/deregister-lesson lesson-id])))

(defn register-tutorial [tutorial]
  (fn [this] (re-frame/dispatch [::model/register-tutorial tutorial])))

(defn deregister-tutorial [tutorial-id]
  (fn [_] (re-frame/dispatch [::model/deregister-tutorial tutorial-id])))

(defn init []
  (re-frame/dispatch-sync [::model/init]))

(defn reset-education! []
  (re-frame/dispatch [::model/hard-reset]))
