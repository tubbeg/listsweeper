(ns sweeper.board)


(def visibility [:visible :hidden])
(defn proxy [num] {:danger num})
(defn position [piece] piece)

(defn crteb
  ([x y mines proxies] {:x x :y y :mines mines :proxies proxies})
  ([x y mines] {:x x :y y :mines mines :proxies []})
  ([x y] {:x x :y y :mines [] :proxies []}))

(defn board
  ([x y mines proxies] (crteb x y mines proxies))
  ([x y mines] (crteb x y mines))
  ([x y] (crteb x y))
  ([size] (crteb (:x size) (:y size))))

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

(defn changePosToProxy
  ([pos proxy] {:x (:x pos) :y (:y pos) :num (+ 1 (:num proxy))})
  ([pos] {:x (:x pos) :y (:y pos) :num 1}))


(defn combineNwithProxies [mine coll]
  (let [n (:neighbours (:mine mine))
        updatedColl (concat n coll)]
    updatedColl))


(defn createProxy
  "input has the following format: [{:x 3 :y 2} 3]
  output: {:x 3 :y 2 :num 3}"
  [freq]
  (let [el (first freq)
        num (first (next freq))]
    {:x (:x el) :y (:y el) :num num}))

(defn extractPositionsFromProxy
  "input has the following format {:x 3 :y 4 :num 2}
  output : [{:x 3 :y 4} {:x 3 :y 4}]"
  [proxy]
  (->> (take (:num proxy) (repeat {:x (:x proxy) :y (:y proxy)})) 
       (into [])))

(defn extractPositionsFromProxies
  "input: [{:x 3 :y 3 :num 2} {:x 3 :y 3 :num 4}]"
  [proxies]
  (loop [p proxies
         l []]
    (if (< (count p) 1) 
      (into [] l) 
      (let [firstEl (first p) 
            remainder (rest p) 
            pos (extractPositionsFromProxy firstEl) 
            updatedVector (concat l pos)]
        (recur remainder updatedVector)))))


(def testProx [{:x 1 :y 4 :num 2} {:x 3 :y 3 :num 4}])
testProx
(extractPositionsFromProxies testProx)


(defn convertPositionsToProxies
  "coll has following format [{:x 3 y: 5} ...]
   output: [{:x 3 :y 5 :num 3} ...]"
  [coll]
  (let [freq (frequencies coll)]
    (loop [f freq
           l []]
      (if (< (count f) 1)
        l
        (let [firstEl (first f)
              remainder (rest f)
              proxy (createProxy firstEl)
              updatedList (conj l proxy)] 
          (recur remainder updatedList))))))


(defn mergePositions
  "input [] []
   output: []"
  [v1 v2]
  (->> (concat v1 v2) 
       (into [])))

; assumes that mine is within bounds
; mines has to have unique position
(defn placeMine [x y oldboard]
  (let [newmine (mine x y oldboard) 
        pos (:neighbours (:mine newmine)) 
        boardPos (extractPositionsFromProxies (:proxies oldboard)) 
        combo (mergePositions pos boardPos) 
        toProxy (convertPositionsToProxies combo) 
        updatedBoard (addNewMineToColl newmine oldboard)] 
    (board (:x oldboard) (:y oldboard) updatedBoard toProxy)))


(def myBoard (board 6 6))
(def pos
  (-> (:proxies myBoard) 
      (extractPositionsFromProxies)))
pos
(def pos2 (calculateNeighbours 4 4))
pos2
(def myProxies (-> (:neighbours (:mine (mine 4 4 myBoard)))
    (mergePositions pos2)))
myProxies
(convertPositionsToProxies myProxies)
myBoard
(def updatedBoard (placeMine 3 3 myBoard))
updatedBoard

(def newmine (mine 3 3 updatedBoard))
newmine
(def myPos (:neighbours (:mine newmine)))
myPos
(def boardPos (extractPositionsFromProxies (:proxies updatedBoard)))
boardPos
(def combo  (mergePositions myPos boardPos))
combo
(def toProxy (convertPositionsToProxies combo))
toProxy
