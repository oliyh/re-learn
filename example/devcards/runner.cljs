(ns devcards.runner
  (:require [devcards.core :as dc :refer-macros [defcard-rg]]
            [reagent.core :as reagent]
            [re-learn.views :as views]))

(defcard-rg lesson-bubble
  (views/lesson-bubble (reagent/atom {:current-lesson {:id :abc
                                                       :description "Hello"}})))
