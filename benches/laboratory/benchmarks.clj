(ns laboratory.benchmarks
  (:require [criterium.core :as criterium]
            [laboratory.experiment :as science]))

(defmacro bench [bench-name & body]
  `(do
     (println ~bench-name)
     (println "======================================================")
     (println)
     (criterium/bench
       ~@body)))

(def experiment-with-broken-candidate
  {:use (fn [] 1)
   :try (fn [] 2)})

(def with-one-arg
  {:use (fn [user] (:email user))
   :try (fn [user] (:email user))})

(def with-two-args
  {:use (fn [user _] (:email user))
   :try (fn [user _] (:email user))})

(def with-three-args
  {:use (fn [user _ _] (:email user))
   :try (fn [user _ _] (:email user))})

(defn -main [& args]
  (bench "with-no-args-map"
    (science/run experiment-with-broken-candidate))

  (let [faster-experiment (science/make-it-faster! experiment-with-broken-candidate)]
    (bench "with-no-args-record"
      (science/run faster-experiment)))

  (bench "with-one-arg-map"
         (science/run with-one-arg {:email "tom@tcrayford.com"}))

  (let [faster-experiment (science/make-it-faster! with-one-arg)]
    (bench "with-one-arg-record"
      (science/run faster-experiment {:email "tom@tcrayford.com"})))

  (bench "with-two-arg-map"
         (science/run with-two-args {:email "tom@tcrayford.com"} 1))

  (let [faster-experiment (science/make-it-faster! with-two-args)]
    (bench "with-two-arg-record"
      (science/run faster-experiment {:email "tom@tcrayford.com"} 1)))

  (bench "with-three-args"
         (science/run with-three-args {:email "tom@tcrayford.com"} 1 2))

  (let [faster-experiment (science/make-it-faster! with-three-args)]
    (bench "with-three-args-record"
      (science/run faster-experiment {:email "tom@tcrayford.com"} 1 2))))
