(ns sweeper.core
  (:require [sweeper.tableView :refer [makeTable]] 
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.dom :as gdom]
            [sweeper.util :as ht]
            [sweeper.board :refer [board generateAndPlaceMines]]
            ["react-dom/client" :refer [createRoot]] ))

(defn getTime [s] 
  [:div
   {:class "box has-background-dark has-text-white"
    :style {:margin-right "90%"}} "seconds: " s])

(defn isRunning [gamestate]
  (= (:state @gamestate) :running))

(defn isLose [gamestate]
  (= (:state @gamestate) :lose))

(defn counter [gamestate]
  (if (isRunning gamestate)
    (let [s (r/atom 0)]
        (fn [] (js/setTimeout
                (fn [] (swap! s inc)) 1000) 
          (if (not (isLose gamestate))
            (getTime @s)
            (getTime 0))))
    (getTime "_")))

(defn react-app [s b g]
  [:div [counter g]
  [:div [makeTable s b g]]])

(def size {:x 12 :y 12})
(def _myBoard (->> (board size)
                   (generateAndPlaceMines 4 size)
                   :board))
(def myBoard (r/atom _myBoard))
(def myGameState (r/atom {}))
(def myElem (ht/createElem "div"))
(ht/appendElem myElem ht/dBody)
(defonce root (createRoot myElem))
(defn init
  []
  (.render root (r/as-element [react-app size myBoard myGameState])))

(defn initFn []
  (init))