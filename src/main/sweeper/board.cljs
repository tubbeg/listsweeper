(ns sweeper.board)


(def visibility [:visible :hidden])
(defn proxy [num] {:danger num})
(defn position [piece] piece)

(defn b
  ([x y mines proxies] {:x x :y y :mines mines :proxies proxies})
  ([x y mines] {:x x :y y :mines mines :proxies []})
  ([x y] {:x x :y y :mines [] :proxies []}))

(defn board
  ([x y mines proxies] (b x y mines proxies))
  ([x y mines] (b x y mines))
  ([x y] (b x y))
  ([size] (b (:x size) (:y size))))

(defn notoutbnds [x y a b]
  (let [r1 (< x (+ a 1))
        r2 (< y (+ b 1))
        r3 (> y 0)
        r4 (> x 0)]
    (and r1 r2 r3 r4)))

(defn isOutOfBounds
  ([x y board] (not (notoutbnds x y (:x board) (:y board))))
  ([x y xlimit ylimit] (not (notoutbnds x y xlimit ylimit))))

(defn isNotOutOfBounds
  ([x y a b] (notoutbnds x y a b)))

(defn matchesPos [x y a b]
  (let [r1 (= a x)
        r2 (= b y)
        res (and r1 r2)]
    res))

(defn filterMatch [x y proxyVector]
  (filter
   (fn [e]
     (let [a (:x e) 
           b (:y e)] 
       (matchesPos x y a b)))
   proxyVector)) 

(defn calculateNeighbours [x y]
  [{:x (+ x 1) :y (+ y 1)}
   {:x (- x 1) :y (- y 1)}
   {:x (- x 1) :y (+ y 1)}
   {:x (+ x 1) :y (- y 1)}
   {:x (+ x 1) :y y}
   {:x (- x 1) :y y}
   {:x x :y (+ y 1)}
   {:x x :y (- y 1)}])

(defn filterOutOfBounds [nVector xlimit ylimit]
  (into []
        (filter 
         (fn [e] (isNotOutOfBounds (:x e) (:y e) xlimit ylimit)) 
         nVector)))


(defn m
  ([x y xlimit ylimit]
   {:mine
   {:x x :y y :neighbours (-> (calculateNeighbours x y)
                              (filterOutOfBounds xlimit ylimit))}}))

(defn mine
  ([x y xlimit ylimit] (m x y xlimit ylimit))
  ([x y board] (m x y (:x board) (:y board))))

(defn addNewMineToColl [mine coll]
  (let [res (conj (:mines coll) mine)]
    res))


(defn hasElem [coll elm]
  (some #(= elm %) coll))


(defn buildProxyColl [coll]
  (loop [c coll
         l []]
    (let [e (peek c)
          remainder (pop c)])))

(defn addProxy [mine coll]
  (let [n (:neighbours (:mine mine))
        updatedColl (concat n coll)]
    updatedColl))


; assumes that mine is within bounds
; mines has to have unique position
(defn placeMine [x y oldboard]
  (let
   [newmine (mine x y oldboard)
    p (addProxy newmine (:proxies oldboard))
    updatedBoard (addNewMineToColl newmine oldboard)]
    (board (:x oldboard) (:y oldboard) updatedBoard p)))


; get all mines
;

(def myb (board 5 5))
(def ub (placeMine 5 5 myb))
ub
(def ub2 (placeMine 5 5 ub))
ub2

; {:proxies {:X :Y}}






