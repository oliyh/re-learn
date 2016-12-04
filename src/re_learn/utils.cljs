(ns re-learn.utils
  (:require [re-learn.core :as re-learn]))

(defn- do-fn [f1 f2]
  (fn [& args]
    (do (apply f1 args)
        (apply f2 args))))

(defn with-lesson [{:keys [id] :as lesson} f]
  (vary-meta
   f
   #(-> (merge-with do-fn % {:component-did-mount (re-learn/register-lesson lesson)
                             :component-will-unmount (re-learn/deregister-lesson id)})
        (assoc ::re-learn/lesson-id id))))

(defn with-tutorial [{:keys [id] :as tutorial} f]
  (vary-meta
   f
   #(-> (merge-with do-fn % {:component-did-mount (re-learn/register-tutorial tutorial)
                             :component-will-unmount (re-learn/deregister-tutorial id)})
        (assoc ::re-learn/tutorial-id id))))
