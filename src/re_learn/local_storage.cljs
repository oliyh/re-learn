(ns re-learn.local-storage
  (:require [re-frame.core :as re-frame]
            [cljs.reader :as edn]))

(re-frame/reg-cofx ::load
                   (fn [coeffects k]
                     (if-let [value (some-> (.getItem js/localStorage (name k)) edn/read-string)]
                       (assoc-in coeffects [:local-storage k] value)
                       coeffects)))

(re-frame/reg-fx ::save
                 (fn [[k v]]
                   (.setItem js/localStorage (name k) (pr-str v))))
