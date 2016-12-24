(ns re-learn.runner
  (:require [cljs.test :as test]
            [doo.runner :refer-macros [doo-all-tests doo-tests]]
            [re-learn.model-test]))

(doo-tests 're-learn.model-test)
