(ns gorilla-test.expresso-latex
  (:require [clojure.string :as str]
            [numeric.expresso.core]
            [gorilla-renderable.core :as render]))

(defn latexify
  [expr]
  (if (seq? expr)
    (case (first expr)
      +  (str/join " + " (map latexify (rest expr)))
      *  (str/join " " (map latexify (rest expr)))
      ** (str (latexify (first (rest expr))) "^{" (latexify (second (rest expr))) "}"))
    (pr-str expr)))

(defrecord ExpressoLatexView [expr])

(defn mathematician-view
  [expr]
  (ExpressoLatexView. expr))

(extend-type ExpressoLatexView
  render/Renderable
  (render [self]
    {:type :latex
     :content (latexify (:expr self))
     :value (pr-str self)}))