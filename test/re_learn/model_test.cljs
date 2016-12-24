(ns re-learn.model-test
  (:require [re-learn.model :as model]
            [re-frame.core :as re-frame]
            [day8.re-frame.test :refer-macros [run-test-sync]]
            [cljs.test :refer-macros [deftest is testing run-tests]]
            [devcards.core :refer-macros [deftest]]))

(def ^:private a-lesson
  {:id :a-lesson
   :description "A test lesson"})

(deftest tutorial-precedence-test
  (run-test-sync
   (re-frame/dispatch [::model/init])
   (let [tutorial (re-frame/subscribe [::model/current-tutorial])]

     (testing "there are no tutorials at first"
       (is (= nil @tutorial)))

     (testing "a registered tutorial appears first"
       (re-frame/dispatch [::model/register-tutorial {:id :less-important
                                                      :name "Less important things"
                                                      :description "Learn about less important things"
                                                      :lessons [a-lesson]
                                                      :precedence 2}])
       (is (= :less-important (get-in @tutorial [:tutorial :id]))))

     (testing "a more important tutorial can take a higher precedence"
       (re-frame/dispatch [::model/register-tutorial {:id :important
                                                      :name "Important tutorial!"
                                                      :description "Learn about very important things"
                                                      :lessons [a-lesson]
                                                      :precedence 1}])
       (is (= :important (get-in @tutorial [:tutorial :id])))))))
