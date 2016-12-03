(ns re-learn.views
  (:require [goog.style :as gs]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [re-learn.model :as model]
            [reagent.impl.component :as rc]
            [dommy.core :as dom]
            [reagent.core :as r]))

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

    (cond-> {}
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

(defn- lesson-bubble [{:keys [id description dom-node position attach continue] :as lesson}]
  (let [dom-node (if attach (dom/sel1 attach) dom-node)
        position (if dom-node position :unattached)]
    [:div {:id (name id)
           :class (str "lesson " (name position))
           :style (bubble-position-style dom-node position)}
     (if (string? description)
       [:p description]
       description)
     (when-not continue
       [:button.lesson-learned
        {:style {:float "right"}
         :on-click #(re-frame/dispatch [::model/lesson-learned id])}
        (rand-nth ["Sweet!" "Cool!" "OK" "Got it"])])]))

(defn- extract-lesson [component]
  (second (rc/get-argv component)))

(def lesson-view
  (with-meta
    (fn [{:keys [id dom-node position attach] :as lesson}]
      (when lesson
        (let [dom-node (if attach (dom/sel1 attach) dom-node)
              position (if dom-node position :unattached)]
          [:div {:id (str (name id) "-container")
                 :class (str "lesson-container " (name position))
                 :style (container-position-style dom-node position)}
           [lesson-bubble lesson]])))
    {:component-did-update #(re-frame/dispatch [::model/prepare-lesson (:id (extract-lesson %))])}))

(defn lesson-context [context]
  (when @context
    [:div.context-container
     [:div.tutorial-description
      [:h2 (get-in @context [:tutorial :name])]
      [:p (get-in @context [:tutorial :description])]]

     (when (pos? (get-in @context [:completion :total]))
       [:div.lesson-navigation
        [:a {:on-click #(re-frame/dispatch [::model/lesson-unlearned (get-in @context [:previous-lesson :id])])}
         (gstring/unescapeEntities "&#10096;")]
        [:span (str (get-in @context [:completion :learned]) "/"  (get-in @context [:completion :total]))]
        [:a {:on-click #(re-frame/dispatch [::model/lesson-learned (get-in @context [:current-lesson :id])])}
         (gstring/unescapeEntities "&#10097;")]])

     [:div.context-controls
      [:a {:on-click #(re-frame/dispatch (into [::model/lesson-learned] (map :id (:to-learn @context))))}
       "SKIP " (gstring/unescapeEntities "&#10219")]]

     (when (pos? (get-in @context [:completion :total]))
       [:div.tutorial-completion
        [:div.tutorial-progress
         [:div.tutorial-progress-bar {:style {:width (str (* 100 (get-in @context [:completion :ratio])) "%")}}]]

        [:div.tutorial-progress-steps
         (for [{:keys [id]} (:learned @context)]
           ^{:key (str "progress" id)}
           [:div.progress-step.complete
            [:div]])

         (for [{:keys [id]} (:to-learn @context)]
           ^{:key (str "progress" id)}
           [:div.progress-step.incomplete
            [:div]])]])]))

(defn- help-mode []
  (let [help-mode? (re-frame/subscribe [::model/help-mode?])
        selected-lesson-id (re-frame/subscribe [::model/highlighted-lesson-id])
        all-lessons (re-frame/subscribe [::model/all-lessons])]
    (fn []
      (when @help-mode?
        [:div
         (doall (for [{:keys [id description dom-node position attach continue] :as lesson} @all-lessons
                      :let [dom-node (if attach (dom/sel1 attach) dom-node)
                            position (if dom-node position :unattached)]
                      :when (not= :unattached position)
                      :let [bounds (->bounds dom-node)]]
                  ^{:key id}
                  [:div {:id (str (name id) "-container")
                         :class (str "help-outline " (name position))
                         :on-mouse-over #(re-frame/dispatch [::model/highlighted-lesson id])
                         :on-mouse-out #(re-frame/dispatch [::model/highlighted-lesson nil])
                         :style bounds}
                   (when (= id @selected-lesson-id)
                     [lesson-bubble (assoc lesson :continue true)])]))
         [:div.context-container
          [:h2 "Help mode"]
          [:p "Move your mouse over any highlighted element to learn about it"]

          [:div.context-controls
           [:a {:on-click #(re-frame/dispatch [::model/help-mode false])}
            "CLOSE " (gstring/unescapeEntities "&#10060")]]]]))))

(defn all-lessons []
  (let [current-lesson (re-frame/subscribe [::model/current-lesson])]
    (fn []
      [:div
       [help-mode]
       [lesson-view current-lesson]])))

(defn tutorial [{:keys [context?]}]
  (let [tutorial (re-frame/subscribe [::model/current-tutorial])]
    (fn []
      [:div
       [help-mode]
       [lesson-view (:current-lesson @tutorial)]
       (when context?
         [lesson-context tutorial])])))
