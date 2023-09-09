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
  (cond 
    (not (isVisible cell)) {:c (str " ") :s "has-background-grey"}
    (isMine cell) {:c "M" :s " has-background-black has-text-danger"}
    (isProxy cell) {:c (str (getProxyNum cell)) :s "has-background-light has-text-black"} 
    (isVisible cell) {:c (str " ") :s "has-background-dark"}
    :else "has-background-red"))

(defn setGameStateToRunning [g]
  (println "current state: " @g)
  (swap! g (fn [current] (assoc current :state :running)))
  (println "state updated: " @g))

(defn hasLost [g res]
  (let [r (:result res)
        hasClickedMine (= (:ok r) :mine)]
    (println "has clicked mine is" hasClickedMine)
    (if hasClickedMine 
      (swap! g (fn [current] (assoc current :state :lose)))
      (println "not over yet")))
  (println "current game state" @g))

(defn inspectCell [e pos s b c g]
  (if (= (.-detail e) 2) 
    (let [res (inspectPosition @b pos s)]
      (println "double click on cell" pos "with type" c)
      (setGameStateToRunning g)
      (hasLost g res)
       (swap! b (fn [_] (:board res)))) 
    (println "one click on pos" pos)))

(defn makeCell [pos b size g]
  (let [c (getPos @b pos)
        res  (matchCellType c) 
        callback (fn [e] (inspectCell e pos size b c g))] 
    [:td
      {:class (str "button is-grey " (:s res))
       :id (str :C (:x pos) (:y pos))
       :value {:x (:x pos) :y (:y pos)}
       :onClick callback
       :style {:hover " background-color: black"
               :width "40px"}}
      (:c res)]))

(defn makeRow [row size b g]
  (let [x (:x size)
        r (->> (range x)
              (map inc))]
   [:tr
    (for [i r]
      (makeCell {:x i :y row} b size g))]))

(defn makeTable [size b g]
  (println "currennt board" b)
  (let [
        y (:y size)
        r (->> (range y)
               (map inc))]
    [:div
     {:class "box has-background-grey-dark"
      :style {:margin-left "25%" 
              :margin-right "42%"
              :margin-top "1%"}}
     [:table {:class "table is-bordered"  
              :style {:margin-left "6%" 
                      :margin-top "1%" 
                      :margin-right "6%"}} 
      [:tbody  
       (for [i r] 
         (makeRow i size b g))]]]))