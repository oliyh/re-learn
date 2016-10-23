(ns re-learn.utils
  (:require [re-learn.core :as re-learn]))

(defn with-lesson [{:keys [id] :as lesson} f]
  (vary-meta
   f
   #(merge % {::re-learn/lesson-id id
              :component-did-mount (re-learn/register-lesson lesson)
              :component-will-unmount (re-learn/deregister-lesson id)})))


(defn- ->lesson-id [lesson]
  (cond
    (keyword? lesson)
    lesson

    (instance? MetaFn lesson)
    (::re-learn/lesson-id (meta lesson))

    :else
    lesson))

(defn with-tutorial [{:keys [id] :as tutorial} f]
  (vary-meta
   f
   #(merge % {::re-learn/tutorial-id id
              :component-did-mount (re-learn/register-tutorial (update tutorial :lessons (fn [lessons] (map ->lesson-id lessons))))
              :component-will-unmount (re-learn/deregister-tutorial id)})))
