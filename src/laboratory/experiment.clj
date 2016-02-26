(ns laboratory.experiment)

(defrecord Experiment [enabled publish metrics use try])
(defrecord ExperimentSideResult [value metrics])
(defrecord ExperimentResult [name experiment args control candidate])

(def always-enabled (constantly true))
(def publish-nowhere (constantly nil))

(defn nanos->ms [^long nanos]
  (float  (/  nanos 1000000)))

(defn run-with-result
  ([f metrics]
   (let [metrics0 (mapv #(%) (vals metrics))
         t0 (System/nanoTime)
         result (try (f) (catch Exception e e))
         t1 (System/nanoTime)
         metrics1 (mapv #(%) (vals metrics))]
     (->ExperimentSideResult result (merge {:duration-ns (unchecked-subtract t1 t0)}
                                           (zipmap (keys metrics) (map - metrics1 metrics0))))))
  ([f metrics arg1]
   (let [metrics0 (mapv #(%) (vals metrics))
         t0 (System/nanoTime)
         result (try (f arg1) (catch Exception e e))
         t1 (System/nanoTime)
         metrics1 (mapv #(%) (vals metrics))]
     (->ExperimentSideResult result (merge {:duration-ns (unchecked-subtract t1 t0)}
                                           (zipmap (keys metrics) (map - metrics1 metrics0))))))
  ([f metrics arg1 arg2]
   (let [metrics0 (mapv #(%) (vals metrics))
         t0 (System/nanoTime)
         result (try (f arg1 arg2) (catch Exception e e))
         t1 (System/nanoTime)
         metrics1 (mapv #(%) (vals metrics))]
     (->ExperimentSideResult result (merge {:duration-ns (unchecked-subtract t1 t0)}
                                           (zipmap (keys metrics) (map - metrics1 metrics0))))))
  ([f metrics arg1 arg2 arg3]
   (let [metrics0 (mapv #(%) (vals metrics))
         t0 (System/nanoTime)
         result (try (f arg1 arg2 arg3) (catch Exception e e))
         t1 (System/nanoTime)
         metrics1 (mapv #(%) (vals metrics))]
     (->ExperimentSideResult result (merge {:duration-ns (unchecked-subtract t1 t0)}
                                           (zipmap (keys metrics) (map - metrics1 metrics0))))))

  ([f metrics arg1 arg2 arg3 arg4]
   (let [metrics0 (mapv #(%) (vals metrics))
         t0 (System/nanoTime)
         result (try (f arg1 arg2 arg3 arg4) (catch Exception e e))
         t1 (System/nanoTime)
         metrics1 (mapv #(%) (vals metrics))]
     (->ExperimentSideResult result (merge {:duration-ns (unchecked-subtract t1 t0)}
                                           (zipmap (keys metrics) (map - metrics1 metrics0))))))
  ([f metrics arg1 arg2 arg3 arg4 & args]
   (let [metrics0 (mapv #(%) (vals metrics))
         t0 (System/nanoTime)
         result (try (apply f arg1 arg2 arg3 arg4 args) (catch Exception e e))
         t1 (System/nanoTime)
         metrics1 (mapv #(%) (vals metrics))]
     (->ExperimentSideResult result (merge {:duration-ns (unchecked-subtract t1 t0)}
                                           (zipmap (keys metrics) (map - metrics1 metrics0)))))))

(defn make-result [experiment args control-result candidate-result]
  (->ExperimentResult (:name experiment) experiment args control-result candidate-result))

(defn run
  "Given an experiment map
  Run the experiment, capturing the `:duration-ns` and other experiment `:metrics`.
  If the results aren't `:publish`ed, the experiment isn't run,
   if no one is looking at the results, the experiment isn't worth conducting."
  ([experiment]
   (if (and ((:enabled experiment always-enabled))
            (:publish experiment))
     (let [control-result (run-with-result (:use experiment) (:metrics experiment))
           candidate-result (run-with-result (:try experiment) (:metrics experiment))]
       ((:publish experiment publish-nowhere) (make-result experiment [] control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result))
         (:value control-result)))
     ((:use experiment))))

  ([experiment arg1]
   (if (and ((:enabled experiment always-enabled) arg1)
            (:publish experiment))
     (let [control-result (run-with-result (:use experiment) (:metrics experiment) arg1)
           candidate-result (run-with-result (:try experiment) (:metrics experiment) arg1)]
       ((:publish experiment publish-nowhere) (make-result experiment [arg1] control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result))
         (:value control-result)))
     ((:use experiment) arg1)))

  ([experiment arg1 arg2]
   (if (and ((:enabled experiment always-enabled) arg1 arg2)
            (:publish experiment))
     (let [control-result (run-with-result (:use experiment) (:metrics experiment) arg1 arg2)
           candidate-result (run-with-result (:try experiment) (:metrics experiment) arg1 arg2)]
       ((:publish experiment publish-nowhere) (make-result experiment [arg1 arg2] control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result))
         (:value control-result)))
     ((:use experiment) arg1 arg2)))


  ([experiment arg1 arg2 arg3]
   (if (and ((:enabled experiment always-enabled) arg1 arg2 arg3)
            (:publish experiment))
     (let [control-result (run-with-result (:use experiment) (:metrics experiment) arg1 arg2 arg3)
           candidate-result (run-with-result (:try experiment) (:metrics experiment) arg1 arg2 arg3)]
       ((:publish experiment publish-nowhere) (make-result experiment [arg1 arg2 arg3] control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result))
         (:value control-result)))
     ((:use experiment) arg1 arg2 arg3)))

  ([experiment arg1 arg2 arg3 arg4]
   (if (and ((:enabled experiment always-enabled) arg1 arg2 arg3 arg4)
            (:publish experiment))
     (let [control-result (run-with-result (:use experiment) (:metrics experiment) arg1 arg2 arg3 arg4)
           candidate-result (run-with-result (:try experiment) (:metrics experiment) arg1 arg2 arg3 arg4)]
       ((:publish experiment publish-nowhere) (make-result experiment [arg1 arg2 arg3 arg4] control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result))
         (:value control-result)))
     ((:use experiment) arg1 arg2 arg3 arg4)))

  ([experiment arg1 arg2 arg3 arg4 & args]
   (if (and (apply (:enabled experiment always-enabled) arg1 arg2 arg3 arg4 args)
            (:publish experiment))
     (let [control-result (apply run-with-result (:use experiment) (:metrics experiment) arg1 arg2 arg3 arg4 args)
           candidate-result (apply run-with-result (:try experiment) (:metrics experiment) arg1 arg2 arg3 arg4 args)]
       ((:publish experiment publish-nowhere) (make-result experiment (into [arg1 arg2 arg3 arg4] args) control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result))
         (:value control-result)))
     (apply (:use experiment) arg1 arg2 arg3 arg4 args))))


(comment

  (defn add5 [x]
    (+ x 5))

  (defn add5fast [^long x]
    (unchecked-add x 5))

  (def experiment {:name "Add5"
                   :use add5
                   :try add5fast
                   :publish prn
                   :metrics {:used-bytes #(- (.totalMemory (Runtime/getRuntime))
                                             (.freeMemory (Runtime/getRuntime)))}})

  (time (add5fast 1))
  (time (run experiment 1))
  )
