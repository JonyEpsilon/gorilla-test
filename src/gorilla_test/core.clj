(ns gorilla-test.core
  (:gen-class))


(defn gaussian
  [x sigma mu]
  (* (/ 0.4 sigma) (Math/exp (- (/ (* (- x mu) (- x mu)) (* 2 (* sigma sigma)))))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
