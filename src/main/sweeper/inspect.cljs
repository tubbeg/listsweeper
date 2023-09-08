(ns sweeper.inspect
  (:require [sweeper.positionGen :refer [hasElem]]
             [sweeper.board :refer [getPos
                                    board
                                    convertPosToKey
                                    isProxy
                                    isMine
                                    isZero
                                    filterOutOfBounds
                                    calculateNeighbours]]))



(defn minesHasPos [x y board]
  (let []))


(defn inspectEmpty [x y board]
  ())

(defn notFound [x y board]
  (= (getPos board x y) :notfound))

(defn addInspectedPos [pos cell visible]
  (assoc visible (convertPosToKey pos) cell))

(defn getNeighboursPos [pos size]
 (-> (calculateNeighbours (:x pos) (:y pos)) 
     (filterOutOfBounds (:x size) (:y size))))


(defn addNeighbours [pos size remainder]
  (concat remainder (getNeighboursPos pos size)))

; posQueue [{:x x :y y} ...]
(defn inspectPosition [posQueue board visible size] 
    (loop [p posQueue
           v visible]
      (if (isZero p)
        v
        (let [firstEl (first p)
              x (:x firstEl)
              y (:y firstEl)
              remainder (rest p)
              c (getPos board x y)
              isMineOrProx (or (isMine c) (isProxy c))
              notFnd (notFound x y v)]
          (cond 
            (and notFnd (not isMineOrProx)) 
            (recur (addNeighbours
                    firstEl
                    size
                    remainder) (addInspectedPos
                                 firstEl
                                 (getPos board x y)
                                 v))
            notFnd
            (recur remainder (addInspectedPos
                              firstEl 
                              (getPos board x y) 
                              v))
            :else
            (recur remainder v))))))

(def s {:x 5 :y 5})
(def myPos {:x 3 :y 4})
(def queue [myPos])
(getNeighboursPos {:x 3 :y 5} s)
(addNeighbours {:x 1 :y 3} s [{:x 34 :y 23231}])
(count (inspectPosition queue (board s) {} s))