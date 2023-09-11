(ns sweeper.core
  (:require [sweeper.tableView :refer [boardTable
                                       counter]] 
            [sweeper.util :as ht]
            [sweeper.board :refer [board createBoardWithMines]]
            [uix.core :refer [defui $]]
            [uix.dom]
            [cljs.core :as c]))

(def defaultTotalMines 5)
(def defaultSize {:x 8 :y 8})
(def defaultBoard
  (createBoardWithMines defaultSize defaultTotalMines))

(defui stateUI [{:keys [s c]}]
  (let [stle {:class "box has-background-dark has-text-white"
              :style {:margin-right "86%"
                      :margin-top "2%"
                      :margin-left "1%"}}]
    (if (= s :win) 
      ($ :div stle (str "You won in " c " seconds!")) 
      ($ :div stle  "State " (str s)))))


(defn restartBoard [setC size totalMines setB setGameState]
  (println "You clicked me >:(")
  (setGameState :init)
  (setC 0)
  (setB (createBoardWithMines size totalMines)))


(defui restartGame [{:keys [setC totalMines size
                            setGameState setB]}]
  ($ :div  {:class "box has-background-dark"
            :style {:margin-right "86%"
                    :margin-top "2%"
                    :margin-left "1%"}} 
     ($ :button
        {:class "button is-info" 
         :style {:margin-right "82%"
                 :margin-top "2%"
                 :margin-left "1%"}
         :on-click #(restartBoard
                     setC size totalMines setB setGameState)}
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
       ($ stateUI { :s gameState :c c})
       ($ restartGame {:setC setC
                       :totalMines totalMines
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