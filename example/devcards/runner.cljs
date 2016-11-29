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

     [:div#tutorial
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

(defcard-rg lesson-bubble-unattached
  [:div#tutorial
   [views/lesson-bubble (reagent/atom {:current-lesson {:id :abc
                                                        :description "Unattached"
                                                        :position :unattached}})]])
