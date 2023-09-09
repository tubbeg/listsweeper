(ns sweeper.board
  (:require [sweeper.positionGen :refer [hasElem
                                         safeGenerateXuniquePositions]]))


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
         (fn [e] (notoutbnds (:x e) (:y e) xlimit ylimit))
         nVector)))


(defn generatePositions [x y]
  (for [a (->> (range x)
               (map inc))
        b (->> (range y)
               (map inc))]
    {:x a :y b}))

(defn convertPosToKey [pos]
  (keyword (str pos)))

; much better solution
; instant lookup because of hash-map
(defn buildEmptyBoard [pos]
  (loop [p pos
         hm {}]
    (if (< (count p) 1)
      hm
      (let [firstKey (convertPosToKey (first p))
            remainder (rest p) 
            emptyCell {:cell :empty :isVisible :no :marker :no}
            updatedMap (assoc hm firstKey emptyCell)]
        (recur remainder updatedMap)))))

(defn board [size]
  (buildEmptyBoard (generatePositions (:x size) (:y size))))

(defn _getPos [board x y]
  (let [pos ((convertPosToKey {:x x :y y}) board)]
    ;(if (= pos nil) (println "position is nil: " x y) ())
   (if (not (= pos nil))
     pos
     :notfound)))

(defn getPos
  ([board x y] (_getPos board x y)) 
  ([board pos] (_getPos board (:x pos) (:y pos))))

(defn isError [r]
  (= (:result r) :error))

(defn setPos [board x y newval]
  (let [key (convertPosToKey {:x x :y y})
        c (key board)]
    (if (= c nil)
      {:result {:error "cell is nil"} :board {}}
      {:result :ok :board (assoc board key newval)})))

(defn createCell [type visibility x y board marked]
  (setPos
   board x y
   {:cell type
    :isVisible visibility
    :marker marked}))

(defn setProxy [board x y num]
  (createCell {:proxy num} :no x y board :no))

(defn getProxyNum [cell]
  (:proxy (:cell cell)))

(defn setMine [board x y]
  (createCell :mine :no x y board :no))

(defn isVisible [cell] 
  (= (:isVisible cell) :yes))

(defn isMarked [cell]
  (= (:marker cell) :yes))

(defn isEmpty [cell]
  (= (:cell cell) :empty))

(defn toggleVisibility [cell]
  (if (isVisible cell)
    {:cell (:cell cell) :isVisible :no :marker (:marker cell)} 
    {:cell (:cell cell) :isVisible :yes :marker (:marker cell)}))

(defn toggleMarker [cell]
  (if (isMarked cell)
    {:cell (:cell cell) :isVisible (:isVisible cell) :marker :no} 
    {:cell (:cell cell) :isVisible (:isVisible cell) :marker :yes}))

(defn isProxy [cell]
  (contains? (:cell cell) :proxy))

(defn isMine [cell]
  (= (:cell cell) :mine))

(defn notFound [pos]
  (= pos :notfound))

(defn placeProxy [board x y]
  (let [p (getPos board x y)]
    (if (notFound p)
      {:result {:error "pos not found"} :board board}
        (if (isMine p) 
          {:result {:error "pos is mine"} :board board}
          (if (isProxy p) 
            (setProxy board x y (+ (getProxyNum p) 1)) 
            (setProxy board x y 1))))))

(defn isZero [vector] 
  (zero? (count vector)))

(defn placeMine [board x y size]
  (let [a (:x size)
        b (:y size)
        neighbours (-> (calculateNeighbours x y)
                       (filterOutOfBounds a b))
        res (setMine board x y)
        updatedBoard (:board res)]
    (if (not (isError res))
      (loop [n neighbours 
             upb updatedBoard]  
        (if (isZero n) 
          {:result :ok :board upb}
          (let [firstN (first n) 
                newBoard (placeProxy 
                          upb 
                          (:x firstN) 
                          (:y firstN))]
            (if (isError newBoard)
              {:result
               {:error "error after placing proxy"}
               :board
               newBoard}
              (recur (rest n) (:board newBoard))))
          ))  
      {:result
       {:error "error after setting mine"}
       :board {}})))


(defn placeGeneratedMines [mines board size]
  (loop [m mines
         b board]
    (if (not (isZero m))
      (let [firstEl (first m)
            res (placeMine b (:x firstEl) (:y firstEl) size)]
        ;(println "placeMine generateMines result: " res)
        ;(println (:board res))
         (if (not (isError res)) 
           (recur (rest m) (:board res))
           {:result {:error (str "error after placing mine" ": former" (:result res))} :board b}))
      {:result :ok :board b})))

(defn generateAndPlaceMines [n size board]
  (-> (:numbers (safeGenerateXuniquePositions n size))
      (placeGeneratedMines board size)))

(def mySize {:x 3 :y 3})
(def myBoard (board mySize))
myBoard
(getPos myBoard 1 2)
(def upd (placeMine myBoard 1 2 mySize))
upd
(def up3 (generateAndPlaceMines 5 mySize myBoard))
;(def firstMine (first mines))
(placeMine myBoard 3 3 mySize)
(placeProxy myBoard 3 3)
;mines
(:board up3)
(println "hello")