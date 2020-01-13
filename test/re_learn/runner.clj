(ns re-learn.runner
  (:require [doo-chrome-devprotocol.core :as dc]
            [clojure.test :refer [deftest is]]))

(def doo-args
  {:chrome-args ["--headless" "--disable-gpu" "--no-sandbox"]})

(deftest cljs-tests
  (let [result (dc/run "out/unit-test.js" doo-args)]
    (is (:success? result) (:report result))))
