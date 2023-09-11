(ns sweeper.core
  (:require [sweeper.tableView :refer [boardTable
                                       counter]] 
            [sweeper.util :as ht]
            [sweeper.board :refer [board generateAndPlaceMines]]
            [uix.core :refer [defui $]]
            [uix.dom]))


(def size {:x 8 :y 8})
(def _myBoard (->> (board size)
                   (generateAndPlaceMines 5 size)
                   :board))


(defui stateUI [s]
  (let [stle {:class "box has-background-dark has-text-white"
              :style {:margin-right "92%"
                      :margin-top "2%"
                      :margin-left "1%"}}] 
    ($ :div stle  "State " (str (:s s)))))




(defui app []
  (let [[b setB] (uix.core/use-state _myBoard)
        [s setS] (uix.core/use-state size)
        [c setC] (uix.core/use-state 0)
        [totalMines setTotalMines] (uix.core/use-state 5)
        [gameState setGamestate] (uix.core/use-state :init)]
    (println "root gamestate is" gameState)
    ($ :<>
       ($ counter {:gameState gameState :c c :setC setC}) 
       ($ stateUI { :s gameState})
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