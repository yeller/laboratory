(ns fineman.experiment-test
  (:require [clojure.test :refer :all]
            [fineman.experiment :as science]))

(deftest run-experiment-test
  (testing "always returns the control result"
    (is (= (science/run {:use (fn [] 1)
                         :try (fn [] 2)})
           1)))

  (testing "doesn't blow up if the candidate throws an exception"
    (is (= (science/run {:use (fn [] 1)
                         :try (fn [] (throw (ex-info "blowing up" {})))})
           1)))

  (testing "blows up if the control blows up"
    (is (thrown? Exception (science/run {:use (fn [] (throw (ex-info "blowing up" {})))
                                         :try (fn [] 1)}))))

  (testing "publishes the values"
    (let [published (atom nil)]
      (science/run {:use (fn [] 1)
                    :try (fn [] 2)
                    :publish (fn [result]
                               (reset! published result))})
      (is (= (-> @published :control :value) 1))
      (is (= (-> @published :candidate :value) 2))))

  (testing "doesn't publish if the experiment is disabled"
    (let [published (atom nil)]
      (science/run {:use (fn [] 1)
                    :try (fn [] 2)
                    :publish (fn [result] (reset! published result))
                    :enabled (fn [] false)})
      (is (= @published nil))))

  (testing "it returns the result after being made faster"
    (is (= (science/run (science/make-it-faster! {:use (fn [] 1)
                                                  :try (fn [] 2)}))
           1))))
