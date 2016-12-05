(ns devcards.lessons
  (:require [devcards.core :as dc :refer-macros [defcard-rg]]
            [reagent.core :as reagent]
            [re-learn.views :as views]))

(defcard-rg lesson-bubble
  (let [attrs {:style {:display "inline-block"
                       :width "30%"
                       :margin-left "calc(1.5% - 1px)"
                       :margin-right "calc(1.5% - 1px)"
                       :height 100
                       :border "1px solid black"}}]

    [:div {:style {:padding-top 100 :padding-bottom 100}}
     [:div#lesson-top-left attrs "Top left"]
     [:div#lesson-top attrs "Top"]
     [:div#lesson-top-right attrs "Top right"]

     [:div#lesson-left attrs "Left"]
     [:div#lesson-unattached attrs "Unattached"]
     [:div#lesson-right attrs "Right"]

     [:div#lesson-bottom-left attrs "Bottom left"]
     [:div#lesson-bottom attrs "Bottom"]
     [:div#lesson-bottom-right attrs "Bottom right"]

     [:div#tutorial {:style {:position "fixed" :top 0 :left 0}}
      [views/lesson-view {:id :abc
                          :description "Unattached"
                          :position :unattached}]

      [views/lesson-view {:id :abc
                          :description "Left"
                          :attach [:#lesson-left]
                          :position :left}]

      [views/lesson-view {:id :abc
                          :description "Right"
                          :attach [:#lesson-right]
                          :position :right}]

      [views/lesson-view {:id :abc
                          :description "Top"
                          :attach [:#lesson-top]
                          :position :top}]

      [views/lesson-view {:id :abc
                          :description "Bottom"
                          :attach [:#lesson-bottom]
                          :position :bottom}]

      [views/lesson-view {:id :abc
                          :description "Bottom left"
                          :attach [:#lesson-bottom-left]
                          :position :bottom-left}]

      [views/lesson-view {:id :abc
                          :description "Bottom right"
                          :attach [:#lesson-bottom-right]
                          :position :bottom-right}]

      [views/lesson-view {:id :abc
                          :description "Top left"
                          :attach [:#lesson-top-left]
                          :position :top-left}]

      [views/lesson-view {:id :abc
                          :description "Top right"
                          :attach [:#lesson-top-right]
                          :position :top-right}]]]))

(defcard-rg lesson-size
  (let [attrs {:style {:display "inline-block"
                       :width "20%"
                       :margin-right "13%"
                       :border "1px solid black"}}]
    [:div
     [:div#lesson-1 attrs "1"]
     [:div#lesson-2 (assoc-in attrs [:style :height] 200) "2"]
     [:div#lesson-3 (assoc-in attrs [:style :height] 400) "3"]

     [:div#tutorial {:style {:position "fixed" :top 0 :left 0}}
      [views/lesson-view {:id "1"
                          :description "Short"
                          :attach [:#lesson-1]
                          :position :left}]

      [views/lesson-view {:id "2"
                          :description "A little bit longer"
                          :attach [:#lesson-2]
                          :position :left}]

      [views/lesson-view {:id "3"
                          :description [:p "Much, much longer than all the others."
                                        [:br]
                                        "In fact multiple lines and everything else"
                                        [:br]
                                        "That's what's going on"]
                          :attach [:#lesson-3]
                          :position :left}]]]))
