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

(defn- do-fn [f1 f2]
  (fn [& args]
    (do (apply f1 args)
        (apply f2 args))))

(defn with-lesson
  "Attach a lesson, the basic building block of re-learn, to a UI component"
  [{:keys [id] :as lesson} f]
  (vary-meta
   f
   #(-> (merge-with do-fn % {:component-did-mount (register-lesson lesson)
                             :component-will-unmount (deregister-lesson id)})
        (assoc ::lesson-id id))))

(defn with-tutorial
  "Attach a tutorial (collection of lessons) to a UI element"
  [{:keys [id] :as tutorial} f]
  (vary-meta
   f
   #(-> (merge-with do-fn % {:component-did-mount (register-tutorial tutorial)
                             :component-will-unmount (deregister-tutorial id)})
        (assoc ::tutorial-id id))))

(defn init
  "Call when initialising your app to initialise re-learn from local storage"
  []
  (re-frame/dispatch-sync [::model/init]))

(defn reset-education!
  "Wipe all learning history from local storage in order to learn everything again"
  []
  (re-frame/dispatch [::model/hard-reset]))

(defn enable-help-mode!
  "Highlight UI elements which have lessons attached to let the user discover functionality"
  []
  (re-frame/dispatch [::model/help-mode true]))
