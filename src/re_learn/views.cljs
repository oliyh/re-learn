(ns re-learn.views
  (:require [goog.style :as gs]
            [re-frame.core :as re-frame]
            [reagent.impl.component :as rc]
            [dommy.core :as dom]))

(defn- ->bounds [dom-node]
  (when-let [bounds (and dom-node (gs/getBounds dom-node))]
    {:top (.-top bounds)
     :left (.-left bounds)
     :width (.-width bounds)
     :height (.-height bounds)}))

(defn- container-position-style [dom-node position]
  (let [{:keys [top left height width] :as bounds} (->bounds dom-node)]

    (cond-> {:position "absolute"}
      (or (nil? bounds) (= :unattached position))
      (assoc :top "20%"
             :left "50%")

      (= :top position)
      (assoc :top top
             :height 100
             :left (+ left (/ width 2)))

      (= :right position)
      (assoc :top (+ top (/ height 2))
             :left (+ left width))

      (= :left position)
      (assoc :top (+ top (/ height 2))
             :left left)

      (= :bottom position)
      (assoc :top (+ top height)
             :left (+ left (/ width 2))))))

(defn- bubble-position-style [dom-node position]
  (let [{:keys [top left height width] :as bounds} (->bounds dom-node)]

    (cond-> {}
      (or (nil? bounds) (= :unattached position))
      (assoc :left "-50%")

      (= :top position)
      (assoc :top "calc(-100% - 18px)"
             :height 90
             :left "-50%")

      (= :right position)
      (assoc :top -40
             :left 10)

      (= :left position)
      (assoc :top -40
             :left "calc(-100% - 10px)")

      (= :bottom position)
      (assoc :top 10
             :left "-50%"))))

(defn- extract-lesson [component]
  (deref (second (rc/get-argv component))))

(def lesson-bubble
  (with-meta
    (fn [lesson]
      (when @lesson
        (let [{:keys [id description dom-node position attach continue]} @lesson
              dom-node (if attach (dom/sel1 attach) dom-node)
              position (if dom-node position :unattached)]
          [:div.lesson-container {:style (container-position-style dom-node position)}
           [:div {:class (str "lesson " (name position))
                  :style (merge
                          (bubble-position-style dom-node position)
                          {:position "relative"
                           :display "inline-block"

                           :padding 8
                           :border-radius 4
                           :color "white"
                           :background-color "rgba(0, 0, 0, 0.8)"})}
            (if (string? description)
              [:p description]
              description)
            (when-not continue
              [:button.lesson-learned
               {:style {:float "right"}
                :on-click #(re-frame/dispatch [:tutorial/lesson-learned id])}
               (rand-nth ["Sweet!" "Cool!" "OK" "Got it"])])]])))
    {:component-will-update #(re-frame/dispatch [:tutorial/prepare-lesson (:id (extract-lesson %))])}))

(defn all-lessons []
  (let [current-lesson (re-frame/subscribe [:tutorial/current-lesson])]
    (fn []

      [lesson-bubble current-lesson])))

(defn tutorial []
  (let [current-lesson (re-frame/subscribe [:tutorial/current-tutorial])]
    (fn []
      [lesson-bubble current-lesson])))
