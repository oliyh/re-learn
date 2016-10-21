(ns re-learn.views
  (:require [goog.style :as gs]
            [re-frame.core :as re-frame]))

(defn- bubble-position-style [dom-node position]
  (let [bounds (gs/getBounds dom-node)
        top (.-top bounds)
        left (.-left bounds)
        width (.-width bounds)
        height (.-height bounds)]

    (cond-> {:position "absolute"}

      (= :right position)
      (assoc :top top
             :left (+ left width))

      (= :bottom position)
      (assoc :top (+ top height)
             :left left))))

(defn- lesson-bubble [{:keys [id description dom-node position]
                       :or {position :right}}]
  (def dm dom-node)
  [:div.lesson {:style (merge
                        (bubble-position-style dom-node position)
                        {:padding 8
                         :border-radius 4
                         :color "white"
                         :background-color "rgba(0, 0, 0, 0.8)"})}
   [:p description]
   [:button.lesson-learned
    {:style {:float "right"}
     :on-click #(re-frame/dispatch [:tutorial/lesson-learned id])}
    (rand-nth ["Sweet!" "Cool!" "OK" "Got it"])]])

(defn tutorial []
  (let [current-lesson (re-frame/subscribe [:tutorial/current-lesson])]
    (fn []
      (when @current-lesson
        [lesson-bubble @current-lesson]))))
