(ns sweeper.core
  (:require [sweeper.board :refer [mine
                                   generateMines
                                   board]]
            [sweeper.util :as ht]
            [reagent.core :as r]
            [reagent.dom :as rdom]))


(defn initFn []
  (println "hello initFn"))


(def myDiv (ht/getElem "divRoot"))

(def newElem (ht/createElem "div"))


(set! (.-id myDiv) "root2")

(ht/changeId myDiv "root5")
;(changeId newElem "myNewElement")
(ht/appendElem newElem ht/dBody)


(defn createTable [id]
  (let [t (ht/createElem "table")
        tbody (ht/createElem "tbody")]
    (ht/changeId t id)
    (ht/appendElem tbody t)
    t))


(defn addc [row id]
  (let [c (ht/createElem "td")] 
    (-> c 
        (ht/appendElem row) 
        (ht/changeId id))
    c))



(defn addCell
  ([row id] (addc row id))
  ([row id content]
   (-> (addc row id)
       (ht/setText content)))
  ([row id content class]
   (let [c (addc row id)] 
     (ht/setText c content)
     (ht/setClass c class)
     c))
   ([row id content class callback]
    (let [c (addc row id)]
      (ht/setText c content)
      (ht/setClass c class)
      (ht/setOnClick c callback)
      c)))


(defn addRow [table id] 
  (let [tbody (.-firstChild table)
        tr (ht/createElem "tr")]
    (ht/appendElem tr tbody)
    (ht/changeId tr id)
    tr))


(defn createNcells [root r]
  (repeat r (addCell
             root
             (str "1" "mycell")
             (str "2" "mycells")
             "button")))

; not sure why, but clojure did not seem to like repeat
; when adding elements
(defn createXcells [root x callback]
  (loop [n x]
    (if (< n 1) 
      n 
      (let [_ (addCell
               root
               (str :C n)
               (str :C n)
               "button has-background-grey is-primary"
               callback) 
            nextIter (- n 1)] 
        (recur nextIter)))))

(defn createXrowsWithNcells [root x ncells callback]
  (loop [n x]
    (if (< n 1)
      n
      (let [r (addRow root (str :R n))
            _ (createXcells r ncells callback)
            nextIter (- n 1)]
        (recur nextIter)))))

(def ccount (r/atom 0))



(def myBoard (board 5 5))
(def updatedBoard (generateMines myBoard 10))


(defn reagentCell [value]
  [:td
   [:div
   {:class "button is-primary"
    :id "this is definitely working :)"
    :value value}
    "reagent is great :)"]])

(def testCells [{:x 5 :y 7} {:x 2 :y 4}])
(def testCell {:x 5 :y 132})
(defn reagentRow [cell]
  [:tr
   (reagentCell cell)])

(defn reagentTable [cell]
  [:table {:class "table"}
   [:tbody
    (reagentRow cell)]])



(defn render-simple []
  (rdom/render
   [reagentTable testCell]
   (.-body js/document)))

(render-simple)