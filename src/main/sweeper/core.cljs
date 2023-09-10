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
(defui app []
  (let [[b setB] (uix.core/use-state _myBoard)
        [s setS] (uix.core/use-state size)
        [gamestate setGamestate] (uix.core/use-state :running)]
    ($ :<>
       ($ counter {:gamestate gamestate})
       ($ boardTable  {:board b
                       :size s
                       :gameState gamestate
                       :setGameState setGamestate
                       :setBoard setB}))))


(def myElem (ht/createElem "div"))
(ht/appendElem myElem ht/dBody)
(defonce root
  (uix.dom/create-root myElem))

(defn initFn []
  (uix.dom/render-root ($ app) root))