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
  (take (rand-int (count step-names))
        (distinct (repeatedly #(hash-map :id (rand-nth step-names))))))

(def context (reagent/atom {:tutorial {:name "Tutorial name"
                                       :description "Description of the tutorial goes here"}
                            :current-lesson {:id :step-4}
                            :completion {:learned 3
                                         :total 8
                                         :ratio (/ 3.5 8)}
                            :learned [{:id :step-1}
                                      {:id :step-2}
                                      {:id :step-3}]
                            :to-learn [{:id :step-4}
                                       {:id :step-5}
                                       {:id :step-6}
                                       {:id :step-7}
                                       {:id :step-8}]}))

(defn- fiddle-with-context [context]
  (swap! context (fn [c]
                   (let [lessons (generate-lessons)
                         [learned to-learn] (split-at (rand-int (dec (count lessons))) lessons)]
                     (-> c
                         (assoc :learned learned
                                :to-learn to-learn
                                :completion {:learned (count learned)
                                             :total (count lessons)
                                             :ratio (/ (+ 0.5 (count learned)) (count lessons))}))))))

(defonce updates (js/setInterval #(fiddle-with-context context) 2000))

(defcard-rg lesson-bubble
  [views/lesson-context context])
