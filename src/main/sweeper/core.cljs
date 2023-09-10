(ns sweeper.core
  (:require [sweeper.tableView :refer [makeTable]] 
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.dom :as gdom]
            [sweeper.util :as ht]
            [sweeper.board :refer [board generateAndPlaceMines]]
            ["react-dom/client" :refer [createRoot]] 
            [react :as react]
            [uix.core :refer [defui $]]
            [uix.dom]))

(defn getTime [s] 
  [:div
   {:class "box has-background-dark has-text-white"
    :style {:margin-right "90%"}} "seconds: " s])

(defn isRunning [gamestate]
  (= (:state @gamestate) :running))

(defn isLose [gamestate]
  (= (:state @gamestate) :lose))

(defn counter2 [gamestate]
  (if (isRunning gamestate)
    (let [s (r/atom 0)] 
      (react/useEffect 
       (fn [] (js/setTimeout
              (fn [] (swap! s inc)) 1000)))
        (if (not (isLose gamestate))
          (getTime @s)
          (getTime 0)))
    (getTime "_")))

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
  [:div [counter2 g]
  [:div [makeTable s b g]]])

(def size {:x 5 :y 5})
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

;elements can contain key parameter
; <div key = ... ></div>

;notice key parameter
(defui boardCell [{:keys [c key setGameState]}]
  ($ :td {:key key
          :value c
          :on-click
          (fn [e] (if (= (.-detail e) 2)
                     (setGameState :idle)
                     (println "one click")))}
     "stuff"))

(defui counter3 [s]
  (let [[c setC] (uix.core/use-state 0)]
    (uix.core/use-effect
     (fn [] (js/setTimeout
              (fn [] (setC (inc c))) 1000
             (js/clearTimeout c))))
    (if (= (:s s) :running)
      ($ :div c)
      ($ :div 0))))

(defui boardBody [{:keys [r x setGameState]}]
  (for [i r]
       ($ :tr {:key (str i (rand-int 5))} 
          (for [n x]
            ;pass key paramter here in for loops
            ;must be named 'key'
          ($ boardCell {:c n :key (str n (rand-int 8))
                        :setGameState setGameState})))))

(defui boardTable [{:keys [r x setGameState]}]
  ($ :table
     ($ :tbody
        ($ boardBody {:r r :x x :setGameState setGameState}))))

(defui app []
  (let [[b setB] (uix.core/use-state _myBoard)
        [s setS] (uix.core/use-state {:x 5 :y 5})
        [gamestate setGamestate] (uix.core/use-state :running)]
    ($ :<>
       ($ counter3 {:s gamestate})
      ; ($ tableBoard b s)
       ($ boardTable  {:r (->> (range (:x s)) 
                                 (map inc)) 
                       :x (->> (range (:y s))
                               (map inc))
                       :setGameState setGamestate}))))



(defonce root
  (uix.dom/create-root myElem))

;(uix.dom/render-root ($ app) root)

(defn initFn []
 ; (init) 
  (uix.dom/render-root ($ app) root))