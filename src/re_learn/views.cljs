(ns re-learn.views
  (:require [goog.style :as gs]
            [re-frame.core :as re-frame]))

(defn- lesson-bubble [{:keys [description dom-node]}]
  (let [bounds (gs/getBounds dom-node)
        top (.-top bounds)
        left (.-left bounds)
        width (.-width bounds)]
    [:div {:style {:top top :left (+ left width) :position "absolute"}}
     [:p description]
     [:button (rand-nth ["Sweet!" "Cool!" "OK" "Got it"])]]))

(defn tutorial []
  (let [current-lesson (re-frame/subscribe [:tutorial/current-lesson])]
    (fn []
      (if @current-lesson
        [lesson-bubble @current-lesson]
        [:p "No lessons"]))))
