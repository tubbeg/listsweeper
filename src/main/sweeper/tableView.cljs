(ns sweeper.tableView
  (:require [sweeper.board :refer [board 
                                   getPos 
                                   getProxyNum
                                   generateAndPlaceMines
                                   createBoardWithMines
                                   createBoardWithMinesReservations
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

(defn getScreenSize []
  (. js/screen -width))

;(println "width is: " (getScreenSize))

(defn isSmallScreen []
  (let [size (getScreenSize)] 
    (< size 650)))

(println "Is screen small?: " (isSmallScreen))



(defn matchCellType [cell]
  (let [visible {:c (str " ")
                 :s "has-background-dark"}
        invisible {:c (str " ")
                   :s "has-background-grey"}
        marked {:c "🚩"
                :s "has-background-black has-text-danger"}
        mine {:c "💀"
              :s " has-background-black has-text-danger"}]
  (cond 
    (and (not (isVisible cell)) (isMarked cell)) marked
    (not (isVisible cell)) visible
    (isMine cell) mine
    (isProxy cell) {:c (str (getProxyNum cell))
                    :s "has-background-light
                         has-text-black"} 
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

(defn hasWon [gameState]
  (= gameState :win))

(defn isInit [gameState]
  ;(println "gamestate is" gameState)
  (= gameState :init))

; creates a new board during the start of the game
(defn inspectWhichBoard? [b pos size totalMines gameState]
  (if (isInit gameState)
    (createBoardWithMinesReservations size pos totalMines)
    b))

(defn inspectionAction [b pos size totalMines
                        setGameState setBoard gameState]
  (let [boardToInspect (inspectWhichBoard? b pos
                                           size totalMines
                                           gameState)
        res (inspectPosition boardToInspect pos size)] 
    (println "total mines are: " totalMines)
     (cond
       (hasWinCondition
        (:board res) totalMines) (setGameState :win)
       (hasHitMine res) (setGameState :lose)
       :else (setGameState :running))
     (setBoard (:board res))))



(defn markAction [b pos size totalMines setGameState setBoard] 
  (let [res (markPosition b pos size)] 
    (cond 
      (isError res) (println "Error: " res) 
      (hasWinCondition 
       (:board res) totalMines) (setGameState :win) 
      :else (setBoard (:board res)))))


(defn myCallback [e gameState setGameState board
                  setBoard size pos totalMines] 
  (when (not (isGameOver gameState))
    (if (= (.-detail e) 2) 
      (inspectionAction board pos size totalMines 
                        setGameState setBoard gameState)
      (markAction board pos size totalMines
                  setGameState setBoard))))

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
          :style {:width (getWidth size)
                  :length "400px"}
          :on-click
          #(myCallback % gameState setGameState
                       board setBoard size pos
                       totalMines)}
     (if (isNotFound c)
       errorCell
       ($ :p (:c tpe))))))

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

(def marginLeft "20%")
(def marginRight "20%")
(def mobileMargin "1%")

(defn getTableMarginRight []
  (if (isSmallScreen)
    mobileMargin
    marginRight))


(defn getTableMarginLeft []
  (if (isSmallScreen)
    mobileMargin
    marginLeft))

(defn getTableWidth []
  (if (isSmallScreen)
    "100%"
    "400px"))

(defn getBoxWidth []
  (if (isSmallScreen)
    "97%"
    "500px"))


(defui boardTable [{:keys [board size gameState
                           setGameState setBoard
                           totalMines]}]
  (let [rows (->> (range (:x size))
                  (map inc))
        cols (->> (range (:y size))
                  (map inc))]
   ($ :div  {:class "box has-background-grey-dark"
           :style {
                   :width (getBoxWidth)
                   :margin-left (getTableMarginLeft)
                   :margin-right (getTableMarginRight)
                   :margin-top "1%"}}
     ($ :table 
        {:width (getTableWidth)
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
              :style {:width (if (isSmallScreen)
                               "96%"
                               "600px")
                      :margin-right  "1%"
                      :margin-top "2%"
                      :margin-left "1%"}}] 
    (uix.core/use-effect 
     #(js/setTimeout 
       (fn [] (doMath setC c gameState)) 1000 
       (js/clearTimeout c))) 
    (if (hasWon gameState)
      ($ :div stle (str "You won in " c " seconds!")) 
      ($ :div stle "Seconds: " c))))
