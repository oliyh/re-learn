(ns devcards.runner
  (:require [devcards.core :as dc :refer-macros [defcard-rg]]
            [reagent.core :as reagent]
            [re-learn.views :as views]))

(defcard-rg lesson-bubble
  (let [attrs {:style {:display "inline-block"
                       :width "20%"
                       :margin-right "calc(5% - 2px)"
                       :border "1px solid black"}}]
    [:div {:style {:padding-top 100 :padding-bottom 100}}
     [:div#lesson-left attrs "Left"]
     [:div#lesson-right (assoc-in attrs [:style :height] 100) "Right"]
     [:div#lesson-bottom (assoc-in attrs [:style :height] 200) "Bottom"]
     [:div#lesson-top (assoc-in attrs [:style :height] 100) "Top"]

     [:div#tutorial {:style {:position "fixed" :top 0 :left 0}}
      [views/lesson-bubble (reagent/atom {:current-lesson {:id :abc
                                                           :description "Left"
                                                           :attach [:#lesson-left]
                                                           :position :left}})]

      [views/lesson-bubble (reagent/atom {:current-lesson {:id :abc
                                                           :description "Right"
                                                           :attach [:#lesson-right]
                                                           :position :right}})]

      [views/lesson-bubble (reagent/atom {:current-lesson {:id :abc
                                                           :description "Top"
                                                           :attach [:#lesson-top]
                                                           :position :top}})]

      [views/lesson-bubble (reagent/atom {:current-lesson {:id :abc
                                                           :description "Bottom"
                                                           :attach [:#lesson-bottom]
                                                           :position :bottom}})]]]))

(defcard-rg lesson-size
  (let [attrs {:style {:display "inline-block"
                       :width "20%"
                       :margin-right "13%"
                       :border "1px solid black"}}]
    [:div
     [:div#lesson-1 attrs "1"]
     [:div#lesson-2 (assoc-in attrs [:style :height] 100) "2"]
     [:div#lesson-3 (assoc-in attrs [:style :height] 200) "3"]

     [:div#tutorial {:style {:position "fixed" :top 0 :left 0}}
      [views/lesson-bubble (reagent/atom {:current-lesson {:id "1"
                                                           :description "Short"
                                                           :attach [:#lesson-1]
                                                           :position :left}})]

      [views/lesson-bubble (reagent/atom {:current-lesson {:id "2"
                                                           :description "A little bit longer"
                                                           :attach [:#lesson-2]
                                                           :position :left}})]

      [views/lesson-bubble (reagent/atom {:current-lesson {:id "3"
                                                           :description [:p "Much, much longer than all the others."
                                                                         [:br]
                                                                         "In fact multiple lines and everything else"
                                                                         [:br]
                                                                         "That's what's going on"]
                                                           :attach [:#lesson-3]
                                                           :position :left}})]]]))

(defcard-rg lesson-bubble-unattached
  [:div#tutorial
   [views/lesson-bubble (reagent/atom {:current-lesson {:id :abc
                                                        :description "Unattached"
                                                        :position :unattached}})]])
