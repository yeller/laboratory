(ns fineman.benchmarks
  (:require [criterium.core :refer [bench]]
            [fineman.experiment :as science]))

(def experiment-with-broken-candidate
  {:use (fn [] 1)
   :try (fn [] 2)})

(defn -main [& args]
  (println "without fastness")
  (println "======================================================")
  (println)
  (bench
    (science/run experiment-with-broken-candidate))

  (println "without fastness")
  (println "======================================================")
  (println)
  (let [faster-experiment (science/make-it-faster! experiment-with-broken-candidate)]
    (bench
      (science/run faster-experiment))))
