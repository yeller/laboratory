(defproject laboratory "0.1.0-SNAPSHOT"
  :description "do science in production"
  :url "https://github.com/yeller/laboratory"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [bolth "0.2.0-SNAPSHOT"]]}
             :benches {:dependencies [[criterium "0.4.1"]]
                       :source-paths ["src" "benches"]}})
