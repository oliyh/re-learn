(ns re-learn.views
  (:require [goog.style :as gs]
            [goog.string :as gstring]
            [re-frame.core :as re-frame]
            [re-learn.model :as model]
            [re-learn.dom :refer [->absolute-bounds ->dimensions]]
            [reagent.impl.component :as rc]
            [dommy.core :as dom]
            [reagent.core :as r]))

(defn- container-position-style [dom-node position]
  (let [{:keys [top left height width] :as bounds} (->absolute-bounds dom-node)]
    {:position "absolute"
     :top top
     :left left
     :height 0
     :width 0}))

(def arrow-width 10)

(defn- bubble-position-style [dom-node position]
  (let [{:keys [height width] :as bounds} (->dimensions dom-node)]

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
      (assoc :top (+ height arrow-width)
             :left (/ width 2)
             :transform (str "translateX(-50%)"))

      (= :bottom-left position)
      (assoc :top (+ height (/ arrow-width 2))
             :left (- (/ arrow-width 2))
             :transform (str "translateX(-100%)"))

      (= :bottom-right position)
      (assoc :top (+ height (/ arrow-width 2))
             :left (+ width (/ arrow-width 2)))

      (= :top-left position)
      (assoc :top (- arrow-width)
             :left (- (/ arrow-width 2))
             :transform (str "translate(-100%, -100%)"))

      (= :top-right position)
      (assoc :top (- arrow-width)
             :left (+ width (/ arrow-width 2))
             :transform (str "translateY(-100%)")))))

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
        (let [lesson (get-in @context [:current-lesson])]
          [:a {:class (when (:continue lesson) "disabled")
               :on-click #(re-frame/dispatch [::model/lesson-learned (:id lesson)])}
           (gstring/unescapeEntities "&#10097;")])])

     [:div.context-controls
      [:a {:on-click #(re-frame/dispatch [::model/skip-tutorial (get-in @context [:tutorial :id])])}
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
  (let [selected-lesson-id (re-frame/subscribe [::model/highlighted-lesson-id])
        all-lessons (re-frame/subscribe [::model/all-lessons])]
    (fn []
      [:div
       (doall (for [{:keys [id description dom-node position attach continue] :as lesson} @all-lessons
                    :let [dom-node (if attach (dom/sel1 attach) dom-node)
                          position (if dom-node position :unattached)]
                    :when (not= :unattached position)
                    :let [bounds (->absolute-bounds dom-node)]]
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
          "CLOSE " (gstring/unescapeEntities "&#10060")]]]])))

(defn all-lessons []
  (let [current-lesson (re-frame/subscribe [::model/current-lesson])
        help-mode? (re-frame/subscribe [::model/help-mode?])]
    (fn []
      (if @help-mode?
        [help-mode]
        [lesson-view current-lesson]))))

(defn tutorial-toast [tutorial]
  [:div.toast
   [:h2
    [:span.tutorial-name-prefix "Tutorial: "]
    [:span.tutorial-name (get-in tutorial [:tutorial :name])]]
   [:p.tutorial-description (get-in tutorial [:tutorial :description])]

   [:div.actions
    [:button.accept {:on-click #(re-frame/dispatch [::model/accept-tutorial (get-in tutorial [:tutorial :id])])}
     "Start tutorial"]
    [:button.dismiss {:on-click #(re-frame/dispatch [::model/skip-tutorial (get-in tutorial [:tutorial :id])])}
     "Dismiss"]]])

(defn tutorial
  "Root view for displaying unlearned tutorials on the page.
   The :context? key allows you to turn on the context view which shows progress through the tutorial
   at the bottom of the screen.
   The :auto-accept? key when false shows a notification that lessons are available allowing the user to choose to start one,
   rather than starting the tutorial straight away when true (legacy behaviour)"
  [{:keys [context? auto-accept?]
    :or {context? false
         auto-accept? false}}]
  (let [tutorial (re-frame/subscribe [::model/current-tutorial])
        help-mode? (re-frame/subscribe [::model/help-mode?])]
    (fn []
      (cond @help-mode?
            [help-mode]

            (and (false? auto-accept?) (false? (:accepted? @tutorial)))
            [tutorial-toast @tutorial]

            (or auto-accept? (:accepted? @tutorial))
            [:div
             [lesson-view (:current-lesson @tutorial)]
             (when context?
               [lesson-context tutorial])]))))
