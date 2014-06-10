(defproject gorilla-test "0.1.0-SNAPSHOT"
  :description "A test project for the Gorilla REPL."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [hiccup "1.0.5"]
                 [expresso "0.2.0"]
                 [aysylu/loom "0.5.0"]
                 [loom-gorilla "0.1.0"]
                 [org.clojure/data.xml "0.0.7"]
                 [incanter-gorilla "0.1.0"]
                 [gorilla-renderable "1.0.0"]]
  :main ^:skip-aot gorilla-test.core
  :target-path "target/%s"
  :plugins [[lein-gorilla "0.3.1-SNAPSHOT"]]
  :profiles {:uberjar {:aot :all}})
