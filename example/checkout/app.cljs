(ns checkout.app
  (:require [re-learn.core :as re-learn]
            [re-learn.utils :as rlu]
            [reagent.core :as reagent]))

(def purchase-button
  (rlu/with-lesson
    {:id :purchase-button
     :description "When you're ready, click here to purchase"}

    (fn []
      [:button "Purchase"])))

(defn actions []
  [:div [purchase-button]])

(def totals
  (rlu/with-lesson
    {:id :totals
     :description "The total amount of your basket appears here"}

    (fn [items]
      [:div [:strong "Total: £" (reduce (comp + :sub-total-price) items)]])))

(defn- basket-item [{:keys [name quantity unit-price sub-total-price]}]
  [:div
   [:span name]
   [:span quantity " @ " unit-price]
   [:span sub-total-price]])

(def basket
  (rlu/with-lesson
    {:id :basket
     :description "This is your basket where all the items you want to purchase appear"
     :position :bottom}

    (fn [items]
      [:div
       [:span "Name"]
       [:span "Quantity"]
       [:span "Sub-total"]
       (for [{:keys [id] :as item} items]
         ^{:key id}
         [basket-item item])])))

(def checkout
  (fn [app-state]
    [:div {:style {:display "inline-block"}}
     [basket app-state]
     [totals app-state]
     [actions]]))

(def tute
  {:lessons [basket
             totals
             purchase-button]})

(defn- init []
  (let [app-root (js/document.getElementById "app")
        tutorial-root (js/document.getElementById "tutorial")
        app-state [{:id "apples"
                    :name "Apples"
                    :quantity 2
                    :unit-price 0.3
                    :sub-total-price 0.6}

                   {:id "oranges"
                    :name "Oranges"
                    :quantity 5
                    :unit-price 0.25
                    :sub-total-price 1.25}]]
    (reagent/render [checkout app-state] app-root)
    (reagent/render [re-learn/tutorial-view] tutorial-root)))

(.addEventListener js/document "DOMContentLoaded" init)
