(ns re-learn.utils
  (:require [re-learn.core :as re-learn]))

(defn with-lesson [{:keys [id] :as lesson} f]
  (vary-meta
   f
   #(merge % {:component-did-mount (re-learn/register-lesson lesson)
              :component-will-unmount (re-learn/deregister-lesson id)})))
