(ns sweeper.tableView
  (:require [sweeper.board :refer [board 
                                   getPos 
                                   getProxyNum
                                   generateAndPlaceMines 
                                   isProxy 
                                   isVisible 
                                   isMine]] 
            [sweeper.util :as ht] 
            [reagent.core :as r] 
            [reagent.dom :as rdom] 
            [goog.dom :as gdom] 
            ["react-dom/client" :refer [createRoot]] 
            [cljs.core :as c] 
            [sweeper.inspect :refer [inspectPosition 
                                     posNotFound]]))


;(def newElem (ht/createElem "div"))

;(changeId newElem "myNewElement")
;(ht/appendElem newElem ht/dBody)

(def ccount (r/atom 0))


;(def updatedBoard (generateMines myBoard 10))

(defn matchCellType [cell]
  (println "cell is " cell)
  (cond 
    (not (isVisible cell)) (str " ")
    (isProxy cell) (str (getProxyNum cell))
    (isMine cell) "M"
    :else "E"))


(defn inspectCell [e pos s b c]
  (if (= (.-detail e) 2) 
    (let [res (inspectPosition @b pos s)]
      (println "double click on cell" pos "with type" c) 
      (swap! b (fn [_] (:board res)))) 
    (println "one click on pos" pos)))

(defn makeCell [pos b size]
  (let [c (getPos @b pos)
        s  (matchCellType c)
        callback (fn [e] (inspectCell e pos size b c))] 
    [:td {:class "has-background-dark"
          :style {:width "30px"}}
     [:button
      {:class "button is-primary has-background-dark"
       :id (str :C (:x pos) (:y pos))
       :value {:x (:x pos) :y (:y pos)}
       :onClick callback
       :style {:hover " background-color: black"
               :width "40px"}}
      s]]))

(defn makeRow [row size b]
  (let [x (:x size)
        r (->> (range x)
              (map inc))]
   [:tr
    (for [i r]
      (makeCell {:x i :y row} b size))]))

(defn makeTable [size b]
  (println "currennt board" b)
  (let [
        y (:y size)
        r (->> (range y)
               (map inc))]
    [:div
     {:class "box has-background-grey-lighter"
      :style {:margin-left "25%" 
              :margin-right "29%"
              :margin-top "1%"}}
     [:table {:class "table is-bordered"  
              :style {:margin-left "6%" 
                      :margin-top "1%" 
                      :margin-right "6%"}} 
      [:tbody  
       (for [i r] 
         (makeRow i size b))]]]))