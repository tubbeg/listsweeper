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
  [coll xlim ylim exceptions]
  (loop []
    (let [randPos (generateRandomPosition xlim ylim)]
      (if
       (and (not (hasElem coll randPos))
            (not (hasElem exceptions randPos)))
        randPos
        (recur)))))

(defn generateXuniquePositions [x size exceptions]
  (loop [n x
         l []]
    (if (< n 1)
      l
      (let [p (generateUniquePosition
               l (:x size) (:y size) exceptions)
            updatedList (conj l p)
            nextIter (- n 1)]
        (recur nextIter updatedList)))))


(defn _safeGenerateXuniquePositions [x size exceptions]
  (let [xlim (:x size)
        ylim (:y size)
        upperLim (< (+ x 1) (* xlim ylim))
        lowerLim (> x 0)
        numIsValid (and upperLim lowerLim)]
    (if numIsValid
      {:numbers (generateXuniquePositions
                 x size exceptions)}
      {:numbers []})))




(defn safeGenerateXuniquePositions
  ([x size] (_safeGenerateXuniquePositions x size []))
  ([x size exceptions]
   (_safeGenerateXuniquePositions x size exceptions)))

