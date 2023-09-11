(ns sweeper.core
  (:require [sweeper.tableView :refer [boardTable
                                       counter]] 
            [sweeper.util :as ht]
            [sweeper.board :refer [board createBoardWithMines]]
            [uix.core :refer [defui $]]
            [uix.dom]))

(def defaultTotalMines 5)
(def defaultSize {:x 8 :y 8})
(def defaultBoard
  (createBoardWithMines defaultSize defaultTotalMines))


(defui stateUI [s]
  (let [stle {:class "box has-background-dark has-text-white"
              :style {:margin-right "92%"
                      :margin-top "2%"
                      :margin-left "1%"}}] 
    ($ :div stle  "State " (str (:s s)))))


(defn restartBoard [size totalMines setB setGameState]
  (println "You clicked me >:(")
  (setGameState :init)
  (setB (createBoardWithMines size totalMines)))


(defui restartGame [{:keys [totalMines size
                            setGameState setB]}]
  ($ :div 
     ($ :button
        {:class "button is-info" 
         :on-click #(restartBoard
                     size totalMines setB setGameState)}
                  "Start New Game!")))



(defui app []
  (let [[b setB] (uix.core/use-state defaultBoard)
        [s setS] (uix.core/use-state defaultSize)
        [c setC] (uix.core/use-state 0)
        [totalMines setTotalMines] (uix.core/use-state
                                    defaultTotalMines)
        [gameState setGamestate] (uix.core/use-state :init)]
  ;  (println "root gamestate is" gameState)
    ($ :<> 
       ($ counter {:gameState gameState :c c :setC setC}) 
       ($ stateUI { :s gameState})
       ($ restartGame {:totalMines totalMines
                       :size s
                       :setGameState setGamestate
                       :setB setB})
       ($ boardTable  {:board b
                       :size s
                       :gameState gameState
                       :setGameState setGamestate
                       :setBoard setB
                       :totalMines totalMines}))))




(def myElem (ht/createElem "div"))
(ht/appendElem myElem ht/dBody)
(defonce root
  (uix.dom/create-root myElem))

(defn initFn []
  (uix.dom/render-root ($ app) root))