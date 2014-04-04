;; gorilla-repl.fileformat = 1

;; **
;;; # Gorilla REPL
;;; 
;;; Welcome to gorilla :-) Shift + enter evaluates code. Poke the question mark (top right) to learn more ...
;; **

;; @@
(use '[hiccup.core :as hiccup])
;; @@
;; =>
;;; {"content":"<span class='clj-nil'>nil</span>","type":"html","value":"nil"}
;; <=

;; @@
(defn circ
  [c]
  (hiccup/html [:svg {:height 100 :width 100}
                [:circle {:cx 50 :cy 50 :r 40 :stroke "black" :stroke-width 4 :fill c}]]))
;; @@
;; =>
;;; {"type":"html","content":"#'user/circ","value":"#'user/circ"}
;; <=

;; @@
(html (circ "red"))
;; @@
;; =>
;;; {"type":"text","content":"<svg height=\"100\" width=\"100\"><circle cx=\"50\" cy=\"50\" fill=\"red\" r=\"40\" stroke-width=\"4\" stroke=\"black\"></circle></svg>","value":"<svg height=\"100\" width=\"100\"><circle cx=\"50\" cy=\"50\" fill=\"red\" r=\"40\" stroke-width=\"4\" stroke=\"black\"></circle></svg>"}
;; <=

;; @@

;; @@
