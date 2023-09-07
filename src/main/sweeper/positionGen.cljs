(ns sweeper.positionGen)



(defn hasElem [coll elm]
  (some #(= elm %) coll))

(defn generateRandomPosition [limx limy]
  {:x (+ (rand-int limx) 1) :y (+ (rand-int limy) 1)})

(generateRandomPosition 5 5)

(defn generateUniquePosition
  "coll : contains excepted numbers
   this function could run forever if the
   collection already has all possible positions"
  [coll xlim ylim]
  (loop []
    (let [randPos (generateRandomPosition xlim ylim)]
      (if
       (not (hasElem coll randPos))
        randPos
        (recur)))))

(defn generateXuniquePositions [x xlim ylim]
  (loop [n x
         l []]
    (if (< n 1)
      l
      (let [p (generateUniquePosition l xlim ylim)
            updatedList (conj l p)
            nextIter (- n 1)]
        (recur nextIter updatedList)))))

(defn safeGenerateXuniquePositions [x xlim ylim]
  (if (< (+ x 1) (* xlim ylim))
    {:numbers (generateXuniquePositions x xlim ylim)}
    {:numbers []}))

(safeGenerateXuniquePositions 9 4 4)