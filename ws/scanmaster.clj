;; gorilla-repl.fileformat = 1

;; **
;;; # Loading a ScanMaster file
;;; 
;;; Proof-of-principle code for loading ScanMaster scans.
;;; 
;;; The `clojure.data.xml` docs say it uses a stream parser, and generates a lazy data structure, so we should be able to use it to process a large scan without loading it all in to memory, if we're careful.
;; **

;; @@
(require '[clojure.data.xml :as xml])
(require '[gorilla-plot.core :as plot])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; Use java interop to load a zipfile and feed the stream of the `average.xml` file to the xml parser. Note that the file will still be open, and this code gives us no way to close it (we should retain a reference to the `ZipFile`).
;; **

;; @@
(defn average-stream
  [filename]
  (let [z (java.util.zip.ZipFile. filename)
        ze (.getEntry z "average.xml")]
    (xml/parse (.getInputStream z ze))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/average-stream</span>","value":"#'user/average-stream"}
;; <=

;; **
;;; Load some example data. The larger of these files is about 3.2MB, zipped, and it contains 20 scans.
;; **

;; @@
(def x (average-stream #_"data/09Apr1300_0.zip" "data/01May1306.zip"))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/x</span>","value":"#'user/x"}
;; <=

;; **
;;; To use the data structure returned from the xml library we navigate by accessing the `:content` item, and selecting the `nth` part of the resulting arrays. This means you need to know the structure of the xml document. I guess it's done this way to be compatible with the streaming parser (rather than generating a map structure with keys named after the tags, which would be much easier to work with, but would probably require pre-loading the contents, I guess).
;;; 
;;; This function gets the scan point array (the children of the first (starting at zero) child of the root element).
;; **

;; @@
(defn scan-points
  [scan]
  (:content (nth (:content scan) 1)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/scan-points</span>","value":"#'user/scan-points"}
;; <=

;; **
;;; And this extracts the scan parameter from a scan point.
;; **

;; @@
(defn scan-parameter
  [scan-point]
  (read-string (nth (:content (nth (:content scan-point) 0)) 0)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/scan-parameter</span>","value":"#'user/scan-parameter"}
;; <=

;; **
;;; Get the first TOF from the on shot. This could be improved to return on or off, any TOF.
;; **

;; @@
(defn on-shot-first-tof
  [scan-point]
  (:content 
   (nth 
    (:content (nth (:content (nth (:content (nth (:content (nth (:content scan-point) 1)) 0)) 0)) 0)) 1)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/on-shot-first-tof</span>","value":"#'user/on-shot-first-tof"}
;; <=

;; **
;;; Get the data from a TOF.
;; **

;; @@
(defn tof-data
  [tof]
  (map #(read-string (nth (:content %) 0)) tof))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/tof-data</span>","value":"#'user/tof-data"}
;; <=

;; **
;;; Putting the above two together, get the integral (full gates) of the on shot's first TOF.
;; **

;; @@
(defn integrated-on-shot-first-tof
  [scan-point]
  (apply + (tof-data (on-shot-first-tof scan-point))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/integrated-on-shot-first-tof</span>","value":"#'user/integrated-on-shot-first-tof"}
;; <=

;; **
;;; And finally, get an xy pair of `[scan-parameter integrated-signal]` for a given scan point.
;; **

;; @@
(defn xy
  [scan-point]
  [(scan-parameter scan-point) (integrated-on-shot-first-tof scan-point)])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/xy</span>","value":"#'user/xy"}
;; <=

;; **
;;; Mapping `xy` over the points gives us our plot data. Note that because the data structure is lazy, this call doesn't really do the calculation.
;; **

;; @@
(def dat-int (map xy (scan-points x)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/dat-int</span>","value":"#'user/dat-int"}
;; <=

;; **
;;; We can force the calculation to be done, for the purposes of timing. The `(do ... 1)` is just to stop the data from printing to the worksheet.
;; **

;; @@
(do
  (time (doall dat-int))
  1)
;; @@
;; ->
;;; &quot;Elapsed time: 865.621 msecs&quot;
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}
;; <=

;; **
;;; Not super fast, but it works ...
;; **

;; @@
(plot/list-plot dat-int)
;; @@
;; =>
;;; {"type":"vega","content":{"axes":[{"type":"x","scale":"x"},{"type":"y","scale":"y"}],"scales":[{"name":"x","type":"linear","range":"width","zero":false,"domain":{"data":"61391b7f-c431-4d46-a0cb-437fb58242ad","field":"data.x"}},{"name":"y","type":"linear","range":"height","nice":true,"zero":false,"domain":{"data":"61391b7f-c431-4d46-a0cb-437fb58242ad","field":"data.y"}}],"marks":[{"type":"symbol","from":{"data":"61391b7f-c431-4d46-a0cb-437fb58242ad"},"properties":{"enter":{"x":{"field":"data.x","scale":"x"},"y":{"field":"data.y","scale":"y"},"fill":{"value":"steelblue"},"fillOpacity":{"value":1}},"update":{"shape":"circle","size":{"value":70},"stroke":{"value":"transparent"}},"hover":{"size":{"value":210},"stroke":{"value":"white"}}}}],"data":[{"name":"61391b7f-c431-4d46-a0cb-437fb58242ad","values":[{"x":0.03125,"y":26.492855674342103},{"x":0.09375,"y":27.376965974506593},{"x":0.15625,"y":28.34999486019738},{"x":0.21875,"y":29.292377672697363},{"x":0.28125,"y":30.168200041118435},{"x":0.34375,"y":31.03554173519736},{"x":0.40625,"y":31.16647820723685},{"x":0.46875,"y":31.32773951480263},{"x":0.53125,"y":31.376439144736835},{"x":0.59375,"y":30.69567228618422},{"x":0.65625,"y":30.09964792351973},{"x":0.71875,"y":29.09070466694079},{"x":0.78125,"y":28.6134097450658},{"x":0.84375,"y":26.915283203124993},{"x":0.90625,"y":25.910002055921055},{"x":0.96875,"y":24.716282894736846},{"x":1.03125,"y":23.538946854440795},{"x":1.09375,"y":22.32164884868421},{"x":1.15625,"y":21.28038908305921},{"x":1.21875,"y":20.041825143914473},{"x":1.28125,"y":19.15347450657894},{"x":1.34375,"y":18.231265419407894},{"x":1.40625,"y":17.964766652960527},{"x":1.46875,"y":17.617315995065777},{"x":1.53125,"y":17.636654502467106},{"x":1.59375,"y":17.915360300164476},{"x":1.65625,"y":18.207172594572363},{"x":1.71875,"y":19.015278063322373},{"x":1.78125,"y":19.897910670230267},{"x":1.84375,"y":20.870232833059216},{"x":1.90625,"y":21.896201685855274},{"x":1.96875,"y":23.303222656250004},{"x":2.03125,"y":24.352834601151304},{"x":2.09375,"y":25.639712685032894},{"x":2.15625,"y":26.90108449835526},{"x":2.21875,"y":27.77369449013158},{"x":2.28125,"y":28.917429070723667},{"x":2.34375,"y":29.89373458059212},{"x":2.40625,"y":30.413625616776322},{"x":2.46875,"y":30.872031763980257},{"x":2.53125,"y":31.280067845394747},{"x":2.59375,"y":31.230854235197373},{"x":2.65625,"y":30.818899054276304},{"x":2.71875,"y":30.30427631578948},{"x":2.78125,"y":29.7775750411184},{"x":2.84375,"y":29.00307103207237},{"x":2.90625,"y":27.932064658717106},{"x":2.96875,"y":26.527228104440795},{"x":3.03125,"y":25.31365645559212},{"x":3.09375,"y":24.089226973684216},{"x":3.15625,"y":22.937718441611835},{"x":3.21875,"y":21.658807051809216},{"x":3.28125,"y":20.445942125822366},{"x":3.34375,"y":19.725727282072366},{"x":3.40625,"y":18.659282483552634},{"x":3.46875,"y":18.329949629934216},{"x":3.53125,"y":17.934698807565795},{"x":3.59375,"y":17.572342722039487},{"x":3.65625,"y":17.824771278782904},{"x":3.71875,"y":18.083881578947363},{"x":3.78125,"y":18.531429893092117},{"x":3.84375,"y":19.45640162417763},{"x":3.90625,"y":20.184518914473685},{"x":3.96875,"y":21.296579461348685},{"x":4.03125,"y":22.470703124999993},{"x":4.09375,"y":23.71492084703948},{"x":4.15625,"y":24.933439555921048},{"x":4.21875,"y":26.265547902960524},{"x":4.28125,"y":27.448601973684205},{"x":4.34375,"y":28.395417865953934},{"x":4.40625,"y":29.36491313733553},{"x":4.46875,"y":29.94410464638157},{"x":4.53125,"y":30.982666015625007},{"x":4.59375,"y":30.805599814967106},{"x":4.65625,"y":31.18151212993422},{"x":4.71875,"y":31.130049856085513},{"x":4.78125,"y":30.754523026315784},{"x":4.84375,"y":30.16376696134869},{"x":4.90625,"y":29.190481085526322},{"x":4.96875,"y":28.344790810032908}]}],"width":400,"height":247.2187957763672,"padding":{"bottom":20,"top":10,"right":10,"left":50}},"value":"#gorilla_repl.vega.VegaView{:content {:axes [{:type \"x\", :scale \"x\"} {:type \"y\", :scale \"y\"}], :scales [{:name \"x\", :type \"linear\", :range \"width\", :zero false, :domain {:data \"61391b7f-c431-4d46-a0cb-437fb58242ad\", :field \"data.x\"}} {:name \"y\", :type \"linear\", :range \"height\", :nice true, :zero false, :domain {:data \"61391b7f-c431-4d46-a0cb-437fb58242ad\", :field \"data.y\"}}], :marks [{:type \"symbol\", :from {:data \"61391b7f-c431-4d46-a0cb-437fb58242ad\"}, :properties {:enter {:x {:field \"data.x\", :scale \"x\"}, :y {:field \"data.y\", :scale \"y\"}, :fill {:value \"steelblue\"}, :fillOpacity {:value 1}}, :update {:shape \"circle\", :size {:value 70}, :stroke {:value \"transparent\"}}, :hover {:size {:value 210}, :stroke {:value \"white\"}}}}], :data [{:name \"61391b7f-c431-4d46-a0cb-437fb58242ad\", :values ({:x 0.03125, :y 26.492855674342103} {:x 0.09375, :y 27.376965974506593} {:x 0.15625, :y 28.34999486019738} {:x 0.21875, :y 29.292377672697363} {:x 0.28125, :y 30.168200041118435} {:x 0.34375, :y 31.03554173519736} {:x 0.40625, :y 31.16647820723685} {:x 0.46875, :y 31.32773951480263} {:x 0.53125, :y 31.376439144736835} {:x 0.59375, :y 30.69567228618422} {:x 0.65625, :y 30.09964792351973} {:x 0.71875, :y 29.09070466694079} {:x 0.78125, :y 28.6134097450658} {:x 0.84375, :y 26.915283203124993} {:x 0.90625, :y 25.910002055921055} {:x 0.96875, :y 24.716282894736846} {:x 1.03125, :y 23.538946854440795} {:x 1.09375, :y 22.32164884868421} {:x 1.15625, :y 21.28038908305921} {:x 1.21875, :y 20.041825143914473} {:x 1.28125, :y 19.15347450657894} {:x 1.34375, :y 18.231265419407894} {:x 1.40625, :y 17.964766652960527} {:x 1.46875, :y 17.617315995065777} {:x 1.53125, :y 17.636654502467106} {:x 1.59375, :y 17.915360300164476} {:x 1.65625, :y 18.207172594572363} {:x 1.71875, :y 19.015278063322373} {:x 1.78125, :y 19.897910670230267} {:x 1.84375, :y 20.870232833059216} {:x 1.90625, :y 21.896201685855274} {:x 1.96875, :y 23.303222656250004} {:x 2.03125, :y 24.352834601151304} {:x 2.09375, :y 25.639712685032894} {:x 2.15625, :y 26.90108449835526} {:x 2.21875, :y 27.77369449013158} {:x 2.28125, :y 28.917429070723667} {:x 2.34375, :y 29.89373458059212} {:x 2.40625, :y 30.413625616776322} {:x 2.46875, :y 30.872031763980257} {:x 2.53125, :y 31.280067845394747} {:x 2.59375, :y 31.230854235197373} {:x 2.65625, :y 30.818899054276304} {:x 2.71875, :y 30.30427631578948} {:x 2.78125, :y 29.7775750411184} {:x 2.84375, :y 29.00307103207237} {:x 2.90625, :y 27.932064658717106} {:x 2.96875, :y 26.527228104440795} {:x 3.03125, :y 25.31365645559212} {:x 3.09375, :y 24.089226973684216} {:x 3.15625, :y 22.937718441611835} {:x 3.21875, :y 21.658807051809216} {:x 3.28125, :y 20.445942125822366} {:x 3.34375, :y 19.725727282072366} {:x 3.40625, :y 18.659282483552634} {:x 3.46875, :y 18.329949629934216} {:x 3.53125, :y 17.934698807565795} {:x 3.59375, :y 17.572342722039487} {:x 3.65625, :y 17.824771278782904} {:x 3.71875, :y 18.083881578947363} {:x 3.78125, :y 18.531429893092117} {:x 3.84375, :y 19.45640162417763} {:x 3.90625, :y 20.184518914473685} {:x 3.96875, :y 21.296579461348685} {:x 4.03125, :y 22.470703124999993} {:x 4.09375, :y 23.71492084703948} {:x 4.15625, :y 24.933439555921048} {:x 4.21875, :y 26.265547902960524} {:x 4.28125, :y 27.448601973684205} {:x 4.34375, :y 28.395417865953934} {:x 4.40625, :y 29.36491313733553} {:x 4.46875, :y 29.94410464638157} {:x 4.53125, :y 30.982666015625007} {:x 4.59375, :y 30.805599814967106} {:x 4.65625, :y 31.18151212993422} {:x 4.71875, :y 31.130049856085513} {:x 4.78125, :y 30.754523026315784} {:x 4.84375, :y 30.16376696134869} {:x 4.90625, :y 29.190481085526322} {:x 4.96875, :y 28.344790810032908})}], :width 400, :height 247.2188, :padding {:bottom 20, :top 10, :right 10, :left 50}}}"}
;; <=

;; **
;;; It would be interesting to look at what's using up the time.
;; **
