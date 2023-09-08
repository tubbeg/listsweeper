(ns sweeper.core
  (:require [sweeper.board :refer [board
                                   getPos
                                   generateAndPlaceMines
                                   isProxy
                                   isMine]]
            [sweeper.util :as ht]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.dom :as gdom]
            ["react-dom/client" :refer [createRoot]]
            [cljs.core :as c]))


(defn initFn []
  (println "hello initFn"))


(def newElem (ht/createElem "div"))

;(changeId newElem "myNewElement")
(ht/appendElem newElem ht/dBody)

(def ccount (r/atom 0))


(def mySize {:x 12 :y 12})
(def myBoard (->> (board mySize) 
                 (generateAndPlaceMines 10 mySize)
                 :board))
myBoard
(getPos myBoard 6 6)
;(def updatedBoard (generateMines myBoard 10))

(defn matchCellType [c]
  (cond
    (isProxy c) (str (:proxy c))
    (isMine c) "M" 
    :else "_"))



(defn makeCell [board x y]
  (let [c (getPos board x y)
        s (matchCellType c)
        callback (fn [e] (println "c" c))] 
    [:td {:class "has-background-dark"
          :style {:width "30px"}}
     [:button
      {:class "button is-primary has-background-dark"
       :id (str :C x y)
       :value {:x x :y y}
       :onClick callback
       :style {:hover " background-color: black"
               :width "40px"}}
      s]]))

(defn makeRow [board row size]
  (let [x (:x size)
        r (->> (range x)
              (map inc))]
   [:tr
    (for [i r]
      (makeCell board i row))]))
   

(defn makeTable [board size]
  (let [y (:y size)
        r (->> (range y)
               (map inc))]
   [:div
    {:class "box has-background-grey-lighter"
     :style {:margin-left "25%" 
             :margin-right "29%"
             :margin-top "1%"}}
    [:table {:class "table"  
             :style {:margin-left "6%" 
                     :margin-top "1%" 
                     :margin-right "6%"}} 
     [:tbody  
      (for [i r] 
        (makeRow board i size))]]]))

(defonce root (createRoot (gdom/getElement "divRoot")))
(defn init
  []
  (.render root (r/as-element [makeTable myBoard mySize])))

(init)