(ns fineman.experiment)

(defrecord ExperimentSideResult [duration value])

(defrecord ExperimentResult [name control candidate])

(defn always-enabled [& _]
  true)

(defn publish-nowhere [_])

(defn nanos->ms [nanos])

(defn run-with-result
  ([f]
   (let [t0 (System/nanoTime)
         result (try (f) (catch Exception e e))
         t1 (System/nanoTime)]
     (->ExperimentSideResult (nanos->ms (- t1 t0)) result)))
  ([f arg1]
   (let [t0 (System/nanoTime)
         result (try (f arg1) (catch Exception e e))
         t1 (System/nanoTime)]
     (->ExperimentSideResult (nanos->ms (- t1 t0)) result)))
  ([f arg1 arg2]
   (let [t0 (System/nanoTime)
         result (try (f arg1 arg2) (catch Exception e e))
         t1 (System/nanoTime)]
     (->ExperimentSideResult (nanos->ms (- t1 t0)) result)))
  ([f arg1 arg2 arg3]
   (let [t0 (System/nanoTime)
         result (try (f arg1 arg2 arg3) (catch Exception e e))
         t1 (System/nanoTime)]
     (->ExperimentSideResult (nanos->ms (- t1 t0)) result)))
  ([f arg1 arg2 arg3 & args]
   (let [t0 (System/nanoTime)
         result (try (apply f arg1 arg2 arg3 args) (catch Exception e e))
         t1 (System/nanoTime)]
     (->ExperimentSideResult (nanos->ms (- t1 t0)) result))))

(defn make-result [experiment control-result candidate-result]
  (->ExperimentResult (:name experiment) control-result candidate-result))

(defn run
  ([experiment]
   (if ((or (:enabled experiment) always-enabled))
     (let [control-result (run-with-result (:use experiment))
           candidate-result (run-with-result (:try experiment))]
       ((or (:publish experiment) publish-nowhere) (make-result experiment control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result)))
       (:value control-result))

     ((:use experiment))))

  ([experiment arg1]
   (if ((or (:enabled experiment) always-enabled) arg1)
     (let [control-result (run-with-result (:use experiment) arg1)
           candidate-result (run-with-result (:try experiment) arg1)]
       ((or (:publish experiment) publish-nowhere) (make-result experiment control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result)))
       (:value control-result))

     ((:use experiment) arg1)))

  ([experiment arg1 arg2]
   (if ((or (:enabled experiment) always-enabled) arg1 arg2)
     (let [control-result (run-with-result (:use experiment) arg1 arg2)
           candidate-result (run-with-result (:try experiment) arg1 arg2)]
       ((or (:publish experiment) publish-nowhere) (make-result experiment control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result)))
       (:value control-result))

     ((:use experiment) arg1 arg2)))


  ([experiment arg1 arg2 arg3]
   (if ((or (:enabled experiment) always-enabled) arg1 arg2 arg3)
     (let [control-result (run-with-result (:use experiment) arg1 arg2 arg3)
           candidate-result (run-with-result (:try experiment) arg1 arg2 arg3)]
       ((or (:publish experiment) publish-nowhere) (make-result experiment control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result)))
       (:value control-result))

     ((:use experiment) arg1 arg2 arg3)))


  ([experiment arg1 arg2 arg3 & args]
   (if (apply (or (:enabled experiment) always-enabled) arg1 arg2 arg3 args)
     (let [control-result (apply run-with-result (:use experiment) arg1 arg2 arg3 args)
           candidate-result (apply run-with-result (:try experiment) arg1 arg2 arg3 args)]
       ((or (:publish experiment) publish-nowhere) (make-result experiment control-result candidate-result))
       (if (instance? Throwable (:value control-result))
         (throw (:value control-result)))
       (:value control-result))

     (apply (:use experiment) arg1 arg2 arg3 args))))

(defrecord FasterExperiment [enabled publish use try])

(defn make-it-faster! [experiment]
  (map->FasterExperiment experiment))

; TODO unroll `run`
