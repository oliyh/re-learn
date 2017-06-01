(ns re-learn.dom
  (:require [dommy.core :as dom]))

(defn ->bounds [dom-node]
  (some-> dom-node dom/bounding-client-rect))

(defn ->absolute-bounds [dom-node]
  (let [elem-bounds (->bounds dom-node)]
    (merge-with + elem-bounds {:top js/window.scrollY
                               :left js/window.scrollX})))

(defn ->dimensions [dom-node]
  (select-keys (->bounds dom-node) [:height :width]))

(defn viewport-width []
  (or (.-innerWidth js/window)
      (.-clientWidth js/document.documentElement)))

(defn viewport-height []
  (or (.-innerHeight js/window)
      (.-clientHeight js/document.documentElement)))

(defn in-viewport? [dom-node]
  (let [{:keys [top left bottom right]} (->bounds dom-node)]
    (and (not (neg? top))
         (not (neg? left))
         (<= bottom (viewport-height))
         (<= right (viewport-width)))))
