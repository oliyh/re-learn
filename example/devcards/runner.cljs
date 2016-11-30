(ns devcards.runner
  (:require [devcards.core :as dc :refer-macros [defcard-rg]]
            [reagent.core :as reagent]
            [re-learn.views :as views]))

(defcard-rg lesson-bubble
  (let [style {:style {:display "inline-block"
                       :width "20%"
                       :margin-right "13%"
                       :border "1px solid black"}}]
    [:div
     [:div#lesson-left style "Left"]
     [:div#lesson-right style "Right"]
     [:div#lesson-bottom style "Bottom"]

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
                                                           :description "Bottom"
                                                           :attach [:#lesson-bottom]
                                                           :position :bottom}})]]]))

(defcard-rg lesson-size
  (let [style {:style {:display "inline-block"
                       :width "20%"
                       :margin-right "13%"
                       :border "1px solid black"}}]
    [:div
     [:div#lesson-1 style "1"]
     [:div#lesson-2 style "2"]
     [:div#lesson-3 style "3"]

     [:div#tutorial {:style {:position "fixed" :top 0 :left 0}}
      [views/lesson-bubble (reagent/atom {:current-lesson {:id 1
                                                           :description "Short"
                                                           :attach [:#lesson-1]
                                                           :position :left}})]

      [views/lesson-bubble (reagent/atom {:current-lesson {:id 2
                                                           :description "A little bit longer"
                                                           :attach [:#lesson-2]
                                                           :position :left}})]

      [views/lesson-bubble (reagent/atom {:current-lesson {:id 3
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
