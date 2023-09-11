(ns sweeper.tableView
  (:require [sweeper.board :refer [board 
                                   getPos 
                                   getProxyNum
                                   generateAndPlaceMines 
                                   isProxy 
                                   isError
                                   isVisible 
                                   isMarked
                                   isMine
                                   isNotFound
                                   hasWinCondition]] 
            [sweeper.util :as ht] 
            [reagent.core :as r] 
            [reagent.dom :as rdom] 
            [goog.dom :as gdom] 
            ["react-dom/client" :refer [createRoot]] 
            [cljs.core :as c] 
            [sweeper.inspect :refer [inspectPosition
                                     markPosition
                                     posNotFound
                                     hasHitMine]]
            
            [uix.core :refer [defui $]]
            [uix.dom]))


(defn matchCellType [cell]
  (let [visible {:c (str " ")
                 :s "has-background-dark"}
        invisible {:c (str " ")
                   :s "has-background-grey"}
        marked {:c (str "^^")
                :s "has-background-black has-text-danger"}
        mine {:c "M"
              :s " has-background-black has-text-danger"}]
  (cond 
    (and (not (isVisible cell)) (isMarked cell)) marked
    (not (isVisible cell)) visible
    (isMine cell) mine
    (isProxy cell) {:c (str (getProxyNum cell))
                    :s "has-background-light has-text-black"} 
    (isVisible cell) invisible
    :else "has-background-red")))

(defn getWidth [s]
  (let [y (:y s)]
    (if (> y 0) 
      (str (/ 100 y) "%")
      "40px")))


(defn isRunning [gameState]
  ;(println "s is " gameState)
  ;(println "gamestate is" gameState)
  (if (not (= gameState :lose))
    (=  gameState :running)
    false))

(defn isGameOver [gameState]
  ;(println "gamestate is" gameState)
  (let [res (or (= gameState :lose)
                (= gameState :win)
                (= gameState :idle))]
;    (println "res is" res)
    res))


(defn myCallback [e gameState setGameState board
                  setBoard size pos totalMines] 
  (if (not (isGameOver gameState)) 
    (if (= (.-detail e) 2) 
      (let [res (inspectPosition board pos size)] 
        (cond 
          (hasWinCondition
           (:board res) totalMines) (setGameState :win) 
          (hasHitMine res)(setGameState :lose) 
          :else (setGameState :running)) 
        (setBoard (:board res))) 
    (let [res (markPosition board pos size)]
      (cond
        (isError res) (println "Error: " res)
        (hasWinCondition
         (:board res) totalMines) (setGameState :win)
        :else (setBoard (:board res)))))
    (println "game over :)")))

(defui boardCell [{:keys [board row col key ; notice key parameter 
                          gameState setGameState
                          size setBoard
                          totalMines ]}]
  (let [pos {:x row :y col}
        c (getPos board row col)
        errorCell {:cell :error
                   :isVisible :yes
                   :marker :yes}
        tpe (matchCellType c)]
  ($ :td {:key key
          :value row
          :class (str "button is-grey " (:s tpe))
          :style {:width (getWidth size)}
          :on-click
          #(myCallback % gameState setGameState
                       board setBoard size pos
                       totalMines)}
     (if (isNotFound c)
       errorCell
       (:c tpe)))))

(defui boardBody [{:keys [rows cols board
                          gameState
                          setGameState
                          size
                          setBoard
                          totalMines]}]
  (for [i cols] 
    ($ :tr {:width (getWidth size)
            :key (str "R" i )}  
       (for [n rows]  
         ($ boardCell
            {:board board :row n :col i :key (str "C" n i)
             :gameState gameState
             :setGameState setGameState
             :size size
             :setBoard setBoard
             :totalMines totalMines})))))

(defui boardTable [{:keys [board size gameState
                           setGameState setBoard
                           totalMines]}]
  (let [rows (->> (range (:x size))
                  (map inc))
        cols (->> (range (:y size))
                  (map inc))]
   ($ :div  {:class "box has-background-grey-dark"
           :style {:margin-left "30%"
                   :margin-right "40%"
                   :margin-top "1%"}}
     ($ :table 
        {:width "70%"
         :class "table is-bordered"
                :style {:margin-left "1%"
                        :margin-right "1%"
                        :margin-top "1%"}} 
        ($ :tbody 
           ($ boardBody
              {:rows rows
               :cols cols
               :board board 
               :gameState gameState
               :setGameState setGameState
               :size size
               :setBoard setBoard
               :totalMines totalMines}))))))



(defn doMath [setC c gameState] 
  (if (isRunning gameState) 
    (setC (inc c)) 
    c))

(defui counter [{:keys [gameState c setC]}]
  (let [
        stle {:class
              "box has-background-dark
               has-text-white"
              :style {:margin-right "92%"
                      :margin-top "2%"
                      :margin-left "1%"}}] 
    (uix.core/use-effect 
     #(js/setTimeout 
       (fn [] (doMath setC c gameState)) 1000 
       (js/clearTimeout c)))
    ($ :div stle "Seconds: " c)))
