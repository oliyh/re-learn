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

    {:position "absolute"
     :top top
     :left left
     :height 0
     :width 0}))

(def arrow-width 10)

(defn- bubble-position-style [dom-node position]
  (let [{:keys [top left height width] :as bounds} (->bounds dom-node)]

    (cond-> {:position "absolute"
             :display "inline-block"
             :padding 8
             :border-radius 4
             :color "white"
             :background-color "rgba(0, 0, 0, 0.8)"}
      (or (nil? bounds) (= :unattached position))
      (assoc :position "fixed" :left "50%" :top "20%" :transform (str "translate(-50%, -50%)"))

      (= :top position)
      (assoc :top -10
             :left (/ width 2)
             :transform (str "translate(-50%, -100%)"))

      (= :right position)
      (assoc :top (/ height 2)
             :transform "translateY(-50%)"
             :left (+ width arrow-width))

      (= :left position)
      (assoc :top (/ height 2)
             :left (- arrow-width)
             :transform "translate(-100%, -50%)")

      (= :bottom position)
      (assoc :top (+ height 10)
             :left (/ width 2)
             :transform (str "translateX(-50%)")))))

(defn- extract-lesson [component]
  (:current-lesson (deref (second (rc/get-argv component)))))

(def lesson-bubble
  (with-meta
    (fn [tutorial]
      (when (:current-lesson @tutorial)
        (let [{:keys [id description dom-node position attach continue]} (:current-lesson @tutorial)
              dom-node (if attach (dom/sel1 attach) dom-node)
              position (if dom-node position :unattached)]
          [:div {:id (str (name id) "-container")
                 :class (str "lesson-container " (name position))
                 :style (container-position-style dom-node position)}
           [:div {:id (name id)
                  :class (str "lesson " (name position))
                  :style (bubble-position-style dom-node position)}
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
    [:div.tutorial-context-container
     [:div.tutorial-description
      [:h2 (get-in @context [:tutorial :name])]
      [:p (get-in @context [:tutorial :description])]]

     (when (pos? (get-in @context [:completion :total]))
       [:div.tutorial-navigation
        [:a {:on-click #(re-frame/dispatch [::re-learn/lesson-unlearned (get-in @context [:previous-lesson :id])])}
         (gstring/unescapeEntities "&#10096;")]
        [:span (str (get-in @context [:completion :learned]) "/"  (get-in @context [:completion :total]))]
        [:a {:on-click #(re-frame/dispatch [::re-learn/lesson-learned (get-in @context [:current-lesson :id])])}
         (gstring/unescapeEntities "&#10097;")]])

     (when (pos? (get-in @context [:completion :total]))
       [:div.tutorial-completion
        [:div.tutorial-progress
         [:div.tutorial-progress-bar {:style {:width (str (* 100 (get-in @context [:completion :ratio])) "%")}}]]

        [:div.tutorial-progress-steps
         (for [{:keys [id]} (:learned @context)]
           ^{:key (str "progress" id)}
           [:div.progress-step.complete
            [:div] id])

         (for [{:keys [id]} (:to-learn @context)]
           ^{:key (str "progress" id)}
           [:div.progress-step.incomplete
            [:div] id])]])]))

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
