(ns checkout.app
  (:require [re-learn.core :as re-learn]
            [re-learn.views :as re-learn-views]
            [reagent.core :as reagent]))

(def purchase-button
  (re-learn/with-lesson
    {:id :purchase-button-lesson
     :description "When you're ready, click here to purchase"
     :position :top}

    (fn [] [:button.mdl-button.mdl-button--raised
            {:style {:margin-top 10}}
            "Purchase"])))

(defn actions []
  [:div [purchase-button]])

(def totals
  (re-learn/with-lesson
    {:id :totals-lesson
     :description "The total amount of your basket appears here"
     :position :right}

    (fn [items]
      [:tr
       [:td]
       [:td {:col-span 2} "Total"]
       [:td (reduce + (map :sub-total-price @items))]])))

(defn- basket-item [{:keys [name quantity unit-price sub-total-price]}]
  (let [selected? (reagent/atom false)]
    (fn []
      [:tr.basket-item {:on-click #(swap! selected? not)}
       [:td [:label {:class (str "mdl-checkbox mdl-data-table__select is-upgraded" (when @selected? " is-checked"))}
             [:input.mdl-checkbox__input {:type "checkbox"
                                          :on-click #(swap! selected? not)}]
             [:span.mdl-checkbox__focus-helper]
             [:span.mdl-checkbox__box-outline
              [:span.mdl-checkbox__tick-outline]]]]
       [:td name]
       [:td quantity " @ " unit-price]
       [:td sub-total-price]])))

(def basket
  (re-learn/with-lesson
    {:id          :basket-lesson
     :description "This is your basket where all the items you want to purchase appear. Click on an item to continue."
     :position    :left
     :attach      [:#basket :.basket-item]
     :continue    {:event :click
                   :selector [:#basket :.basket-item]}}
    (fn [items]
      [:table#basket.mdl-data-table
       [:thead
        [:tr
         [:th]
         [:th "Name"]
         [:th "Quantity"]
         [:th "Sub-total"]]]
       [:tbody
        (doall (for [{:keys [id] :as item} @items]
                 ^{:key id}
                 [basket-item item]))
        [totals items]]])))

(def re-learn-link
  (re-learn/with-lesson
    {:id :re-learn
     :description "Click here to run the tutorial again"
     :position :left}

    (fn []
      [:a {:href "#"
           :on-click re-learn/reset-education!}
       "Re-learn"])))

(def help-link
  (re-learn/with-lesson
    {:id :help
     :description "Click here to enter help mode and discover what everything does"
     :position :bottom}

    (fn []
      [:a {:href "#"
           :on-click re-learn/enable-help-mode!}
       "Help"])))

(def checkout
  (re-learn/with-tutorial
    {:id :checkout-tutorial
     :name "The checkout"
     :description "Review your basket, check the price and confirm your purchase"
     :lessons [{:id :welcome
                :description [:div
                              [:h2 "Welcome"]
                              "Welcome to the re-learn example"]}
               basket
               totals
               purchase-button
               re-learn-link
               :help]}

    (fn [app-state]
      [:div {:style {:position "absolute"
                     :left "50%"
                     :top "20%"
                     :transform "translate(-50%, -50%)"}}
       [basket app-state]
       [actions]
       [:ul {:style {:margin-top 24}}
        [:li [re-learn-link]]
        [:li [help-link]]]])))

(def app-db
  (reagent/atom [{:id "apples"
                  :name "Apples"
                  :quantity 2
                  :unit-price 0.3
                  :sub-total-price 0.6}

                 {:id "oranges"
                  :name "Oranges"
                  :quantity 5
                  :unit-price 0.25
                  :sub-total-price 1.25}]))

(defn- mount-all []
  (let [tutorial-root (js/document.getElementById "tutorial")
        app-root (js/document.getElementById "app")]

    (reagent/render [checkout app-db] app-root)
    (reagent/render [re-learn-views/tutorial {:context? true :auto-accept? true}] tutorial-root)))

(defn- on-figwheel-reload []
  (mount-all))

(defn- init []
  (re-learn/init)
  (mount-all))

(.addEventListener js/document "DOMContentLoaded" init)
