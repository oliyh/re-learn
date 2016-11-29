(ns re-learn.views
  (:require [goog.style :as gs]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [reagent.impl.component :as rc]
            [dommy.core :as dom]
            [re-learn.core :as re-learn]))

(defn- ->bounds [dom-node]
  (when-let [bounds (and dom-node (gs/getBounds dom-node))]
    {:top (.-top bounds)
     :left (.-left bounds)
     :width (.-width bounds)
     :height (.-height bounds)}))

(defn- container-position-style [dom-node position]
  (let [{:keys [top left height width] :as bounds} (->bounds dom-node)]

    (cond-> {:position "absolute"
             :height 0
             :width 0}
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
             :left left
             :width "initial")

      (= :bottom position)
      (assoc :top (+ top height)
             :left (+ left (/ width 2))
             :width "initial"))))

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
  (:current-lesson (deref (second (rc/get-argv component)))))

(def lesson-bubble
  (with-meta
    (fn [tutorial]
      (when (:current-lesson @tutorial)
        (let [{:keys [id description dom-node position attach continue]} (:current-lesson @tutorial)
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
                :on-click #(re-frame/dispatch [::re-learn/lesson-learned id])}
               (rand-nth ["Sweet!" "Cool!" "OK" "Got it"])])]])))
    {:component-will-update #(re-frame/dispatch [::re-learn/prepare-lesson (:id (extract-lesson %))])}))

(defn lesson-context [context]
  (when @context
    [:div.lesson-context-container {:style {:position "fixed"
                                            :bottom 0
                                            :width "100%"
                                            :box-sizing "border-box"
                                            :padding 24
                                            :background-color "rgba(0, 0, 0, 0.8)"
                                            :color "white"}}
     [:h2 (get-in @context [:tutorial :name])]
     [:p (get-in @context [:tutorial :description])]


     [:div {:style {:text-align "center"}}
      [:div {:style {:line-height "2em" :font-size "3em"}}
       [:a {:style {:cursor "pointer"}
            :on-click #(re-frame/dispatch [::re-learn/lesson-learned (get-in @context [:current-lesson :id])])}
        (gstring/unescapeEntities "&#10096;")]
       [:span {:style {:vertical-align "middle"}} (str (get-in @context [:completion :learned]) "/"  (get-in @context [:completion :total]))]
       [:a {:style {:cursor "pointer"}
            :on-click #(re-frame/dispatch [::re-learn/lesson-learned (get-in @context [:current-lesson :id])])}
        (gstring/unescapeEntities "&#10097;")]]]

     [:div

      [:div.tutorial-progress {:style {:width "100%"
                                       :height "10px"
                                       :border "1px solid lightgreen"
                                       :text-align "center"}}
       [:div {:style {:position "relative"
                      :background-color "green"
                      :transition "width 500ms ease-out"
                      :width (str (* 100 (get-in @context [:completion :ratio])) "%")
                      :height "100%"}}]]

      [:div {:style {:display "table" :width "100%" :table-layout "fixed" :margin-top -17}}
       (for [{:keys [id]} (:learned @context)]
         ^{:key (str "progress" id)}
         [:div {:style {:display "table-cell" :width "2%" :text-align "center"}}
          [:div {:style {:margin "0 auto" :width 20 :height 20 :border-radius 20 :border "1px solid lightgreen" :background-color "green"}}]
          id])

       (for [{:keys [id]} (:to-learn @context)]
         ^{:key (str "progress" id)}
         [:div {:style {:display "table-cell" :width "2%" :text-align "center"}}
          [:div {:style {:margin "0 auto" :width 20 :height 20 :border-radius 20 :border "1px solid pink" :background-color "red" :position "relative"}}]
          id])]]

]))

(defn all-lessons []
  (let [lesson (re-frame/subscribe [::re-learn/current-lesson])]
    (fn []
      [lesson-bubble lesson])))

(defn tutorial []
  (let [tutorial (re-frame/subscribe [::re-learn/current-tutorial])]
    (fn []
      [:div
       [lesson-bubble tutorial]
       [lesson-context tutorial]])))
