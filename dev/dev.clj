(ns dev
  (:require [figwheel-sidecar.repl-api :as ra]))

(defn start []
  (ra/start-figwheel!)
  (ra/start-autobuild :dev :devcards))

(defn stop [] (ra/stop-figwheel!))

(defn cljs [] (ra/cljs-repl "dev"))
