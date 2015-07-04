(ns fineman.experiment)

(defrecord ExperimentSideResult [duration value])

(defrecord ExperimentResult [name control candidate])

(defn always-enabled [& _]
  true)

(defn publish-nowhere [_])

(defn nanos->ms [nanos])

(defn run-with-result [f & args]
  (let [t0 (System/nanoTime)
        result (try (apply f args) (catch Exception e e))
        t1 (System/nanoTime)]
    (->ExperimentSideResult (nanos->ms (- t1 t0)) result)))

(defn make-result [experiment control-result candidate-result]
  (->ExperimentResult (:name experiment) control-result candidate-result))

(defn run [experiment & args]
  (if ((or (:enabled experiment) always-enabled))
    (let [control-result (apply run-with-result (:use experiment) args)
          candidate-result (apply run-with-result (:try experiment) args)]
      ((or (:publish experiment) publish-nowhere) (make-result experiment control-result candidate-result))
      (if (instance? Throwable (:value control-result))
        (throw (:value control-result)))
      (:value control-result))

    (apply (:use experiment) args)))

(defrecord FasterExperiment [enabled publish use try])

(defn make-it-faster! [experiment]
  (map->FasterExperiment experiment))

; TODO unroll `run`
