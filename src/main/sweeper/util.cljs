(ns sweeper.util)

(defn setText [elem text]
  (set! (.-innerHTML elem) text))

(defn appendElem [child parent]
  (.appendChild parent child))

(defn setStyle [elem style]
  (set! (.-style elem) style))

(defn setClass [elem class]
  (println "setting class" class "on element" elem)
  (set! (.-className elem) class))

(def dBody (.-body js/document))

(defn getElem [id]
  (.getElementById js/document id))

(defn createElem [id]
  (.createElement js/document id))

(defn changeId [element newId]
  (set! (.-id element) newId))


(def log (.-log js/console))

(defn returnContext [] ())

(defn setOnClick [element callback]
  (set! (.-onclick element) callback))
