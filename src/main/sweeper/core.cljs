(ns sweeper.core
  (:require [sweeper.tableView :refer [makeTable]] 
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.dom :as gdom]
            [sweeper.util :as ht]
            [sweeper.board :refer [board generateAndPlaceMines]]
            ["react-dom/client" :refer [createRoot]] ))


(def s {:x 12 :y 12})
(def _myBoard (->> (board s)
                   (generateAndPlaceMines 4 s)
                   :board))
(def myBoard (r/atom _myBoard))

(def myElem (ht/createElem "div"))
(ht/appendElem myElem ht/dBody)
(defonce root (createRoot myElem))
(defn init
  []
  (.render root (r/as-element [makeTable s myBoard])))

(defn initFn []
  (init))