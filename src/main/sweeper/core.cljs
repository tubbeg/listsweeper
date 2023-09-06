(ns sweeper.core
  (:require [sweeper.board :refer [mine]]))


(defn initFn []
  (println "hello initFn"))


(defn setStyle [elem style]
  (set! (.-style elem) style))

(defn setClass [elem class]
  (println "setting class" class "on element" elem)
  (set! (.-className elem) class))

(def dBody(.-body js/document))

(defn getElem [id]
  (.getElementById js/document id))

(defn createElem [id]
  (.createElement js/document id))

(defn changeId [element newId]
  (set! (.-id element) newId))

(def myDiv (getElem "divRoot"))

(def newElem (createElem "div"))

(defn appendElem [child parent]
  (.appendChild parent child))

(set! (.-id myDiv) "root2")

(changeId myDiv "root5")
(changeId newElem "myNewElement")
(appendElem newElem dBody)


(defn createTable [id]
  (let [t (createElem "table")
        tbody (createElem "tbody")]
    (changeId t id)
    (appendElem tbody t)
    t))


(defn addc [row id]
  (let [c (createElem "td")] 
    (-> c 
        (appendElem row) 
        (changeId id))
    c))


(defn setText [elem text]
  (set! (.-innerHTML elem) text))

(defn addCell
  ([row id] (addc row id))
  ([row id content]
   (-> (addc row id)
       (setText content)))
  ([row id content class]
   (let [c (addc row id)] 
     (setText c content)
     (setClass c class)
     c)))


(defn addRow [table id] 
  (let [tbody (.-firstChild table)
        tr (createElem "tr")]
    (appendElem tr tbody)
    (changeId tr id)
    tr))


(def myTable (createTable "myTable"))
(appendElem myTable myDiv)
(def myRow (addRow myTable "MyRow"))
(addCell myRow "myIdForCell")
(addCell myRow "secondCell" "myInnerText")
(addCell myRow "2" "myInnerText" "button")
(addCell myRow "3" "myInnerText" "button")
(addCell myRow "4" "myInnerText" "button")
(def secondRow (addRow myTable "MyRow"))
(addCell secondRow "myIdForCell")
(addCell secondRow "secondCell" "myInnerText")
(addCell secondRow "2" "myInnerText" "button")
(addCell secondRow "3" "myInnerText" "button")
(addCell secondRow "4" "myInnerText" "button")
(addCell secondRow "5" "myInnerText" "button")
(setClass myTable "table")
;(setClass myDiv "box")


(def myMines mine)