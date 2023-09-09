(ns sweeper.inspect
  (:require [sweeper.positionGen :refer [hasElem]]
            [sweeper.board :refer [getPos
                                   createCell
                                   board
                                   isError
                                   convertPosToKey
                                   isProxy
                                   isEmpty
                                   isMine
                                   isZero
                                   filterOutOfBounds
                                   calculateNeighbours
                                   notFound
                                   toggleVisibility
                                   isVisible]]))



(defn posNotFound
  ([x y b]  (notFound (getPos b x y)))
  ([p] (notFound p)))

(defn getNeighboursPos [pos size]
  (-> (calculateNeighbours (:x pos) (:y pos))
      (filterOutOfBounds (:x size) (:y size))))


(defn addNeighbours [pos size remainder]
  (concat remainder (getNeighboursPos pos size)))

(defn filterDuplicates [queue]
  ())

(defn filterFoundCells [queue])

; posQueue [{:x x :y y} ...]
(defn _inspectPosition [p board]
  (let [x (:x p)
        y (:y p)
        p (getPos board x y)
        empty (isEmpty p)
        mine (isMine p)]
    (if (posNotFound p)
      {:result {:error "not found"} :board board}
      (if (isVisible p)
        {:result :ok :board board} 
        (let [updatedBoard
              (createCell (:cell p)
                          :yes x y board 
                          (:marker p))
              newBoard (:board updatedBoard)]
          (cond
            (isError updatedBoard) {:result
                                    {:error "inspection error!"}
                                    :board board} 
            mine {:result {:ok :mine}
                   :board newBoard}
            empty {:result {:ok :empty} 
                   :board newBoard}
            :else {:result :ok :board newBoard}))))))

(defn isHiddenEmpty [res]
  (= (:ok (:result res)) :empty))

(defn isHiddenMine [res]
  (= (:ok (:result res)) :mine))

(defn inspectPosition [board pos size] 
  (loop [b board 
         queue [pos]
         ret :none] 
    (if (isZero queue) 
      {:result {:ok ret} :board b} 
      (let [el (first queue)  
            remainder (rest queue) 
            res (_inspectPosition el b)]
        (if (isHiddenEmpty res) 
          (recur (:board res) (addNeighbours el size remainder) ret)
          (if (isHiddenMine res) 
            (recur (:board res) remainder :mine) 
            (recur (:board res) remainder ret)))))))



(def s {:x 5 :y 5})
(def myPos {:x 1 :y 1})
(def testPos (getPos (board s) 3 3))
(def queue [myPos])
(getNeighboursPos {:x 3 :y 5} s)
(addNeighbours {:x 1 :y 3} s [{:x 34 :y 23231}])
(def updatedVis (:board (_inspectPosition myPos (board s))))
(def update2 (inspectPosition (board s) myPos s))
updatedVis
(:board update2)
(def myAtom (atom {}))
@myAtom
(defn pseudoInpsect [n]
  {:c 234 :y n})
(swap! myAtom assoc :a :3)
(swap! myAtom (fn [e] {:stuff e}))
@myAtom