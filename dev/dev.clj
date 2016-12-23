(ns dev
  (:require [figwheel-sidecar.repl-api :as ra]))

(defn start []
  (ra/start-figwheel!)
  (ra/start-autobuild :checkout :todomvc :devcards))

(defn stop [] (ra/stop-figwheel!))

(defn cljs
  ([] (cljs "todomvc"))
  ([build-id] (ra/cljs-repl build-id)))
