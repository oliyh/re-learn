(ns devcards.context
  (:require [devcards.core :as dc :refer-macros [defcard-rg]]
            [reagent.core :as reagent]
            [re-learn.views :as views]))

(def step-names ["conflogulate-bannisters"
                 "reticulate-splines"
                 "bamboozle-conifers"
                 "combobulate-spades"
                 "francolate-camburies"
                 "scubify-scones"
                 "perongulate-kots"
                 "flibberwizzle-spatulas"
                 "pongolino-pipes"])

(defn- generate-lessons []
  (let [lessons (take (rand-int (count step-names))
                      (distinct (repeatedly #(hash-map :id (rand-nth step-names)))))
        [learned to-learn] (split-at (rand-int (dec (count lessons))) lessons)]
    {:learned learned
     :to-learn to-learn
     :completion {:learned (count learned)
                  :total (count lessons)
                  :ratio (/ (+ 0.5 (count learned)) (count lessons))}}))

(def context (reagent/atom (merge {:tutorial {:name "Tutorial name"
                                              :description "Description of the tutorial goes here"}}
                                  (generate-lessons))))

(defn- fiddle-with-context [context]
  (swap! context #(merge % (generate-lessons))))

(defonce updates (js/setInterval #(fiddle-with-context context) 2000))

(defcard-rg lesson-bubble
  [views/lesson-context context])
