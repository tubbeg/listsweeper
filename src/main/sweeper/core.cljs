(ns sweeper.core
  (:require [sweeper.tableView :refer [boardTable
                                       counter
                                       isSmallScreen
                                       getScreenSize]] 
            [sweeper.util :as ht]
            [sweeper.board :refer [ createBoardWithMines]]
            [uix.core :refer [use-state defui $]]
            [uix.dom :refer [create-root render-root]]
            [cljs.core :as c]))

(def defaultTotalMines 5)
(def defaultSize {:x 8 :y 8})
(def defaultBoard
  (createBoardWithMines defaultSize defaultTotalMines))

(defui stateUI [{:keys [s c]}]
  (let [stle {:class "box has-background-dark has-text-white"
              :style {:margin-right "86%"
                      :margin-top "2%"
                      :margin-left "1%"}}]
    (if (= s :win) 
      ($ :div stle (str "You won in " c " seconds!")) 
      ($ :div stle  "State " (str s)))))


(defn restartBoard [setC size totalMines setB setGameState]
  (println "You clicked me >:(")
  (setGameState :init)
  (setC 0)
  (setB (createBoardWithMines size totalMines)))


(defui restartGame [{:keys [setC totalMines size
                            setGameState setB]}]
  ($ :div  {:class "box has-background-dark 
                    has-text-white"
            
            :style {:width (if (isSmallScreen)
                             "98%"
                             "600px")
                    :margin-right "1%" 
                    :margin-top "1%"
                    :margin-left "1%"}}
     "Left click once to mark a cell. Double-click to inspect it!"
     ($ :button
        {:class "button is-info" 
         :style {:width (if (isSmallScreen)
                          "96%"
                          "500px")
                 :margin-right  "1%"
                 :margin-top "4%"
                 :margin-left "1%"}
         :on-click #(restartBoard
                     setC size totalMines setB setGameState)}
                  "Start New Game!")))


(defn changeToDefault [setSize setB setGameState setC setM]
  (setSize defaultSize)
  (setM defaultTotalMines)
  (setB defaultBoard)
  (setGameState :init)
  (setC 0))

(defn changeToMedium [setSize setB setGameState setC setM] 
  (let [s {:x 9 :y 9}
        m 15] 
    (setSize s)
    (setM m)
    (setB (createBoardWithMines s m)) 
    (setGameState :init) 
    (setC 0)))


(defn getHardSize []
 (if (isSmallScreen)
   {:x 9 :y 9}
   {:x 12 :y 12}))

(defn getHardMines []
  (if (isSmallScreen)
    25
    33))


(defn changeToHard [setSize setB setGameState setC setM] 
  (let [s (getHardSize)
        m (getHardMines)] 
    (setSize s)
    (setM m) 
    (setB (createBoardWithMines s m))
    (setGameState :init)
    (setC 0)))


(defn changeToVeryHard [setSize setB setGameState setC setM]
  (let [s {:x 9 :y 9}
       m 33]
   (setSize s)
   (setM m)
   (setB (createBoardWithMines s m))
   (setGameState :init)
   (setC 0)))


(defn changeDifficulty [e setGameState setSize setB setC setM]
  (case (.. e -target -value)
    "medium" (changeToMedium setSize setB setGameState setC setM)
    "hard" (changeToHard setSize setB setGameState setC setM)
    "very hard" (changeToVeryHard setSize setB setGameState setC setM)
    (changeToDefault setSize setB setGameState setC setM))
  (println "Changed difficulty"))
  ;(println "e is " e)
  ;(println (.. e -target -value)))

(defui selectDifficulty [{:keys [setGameState setSize
                                 setB setC setM]}]
  ($ :div {:class "box has-background-dark"
           :style {:width (if (isSmallScreen)
                            "96%"
                            "500px")
                   :margin-right "1%"
                   :margin-top "1%"
                   :margin-left "1%"}}
   ($ :div {:class "select"}
     ($ :select {
                 :on-change #(changeDifficulty
                              % setGameState setSize
                              setB setC setM)}
        ($ :option "easy")
        ($ :option "medium")
        ($ :option "hard")
        ($ :option "very hard")))))



(defui widthIs []
  ($ :div (str "your width is: "
               (getScreenSize) " "
               (isSmallScreen))))

(defui app []
  (let [[b setB] (use-state defaultBoard)
        [s setS] (use-state defaultSize)
        [c setC] (use-state 0)
        [totalMines setTotalMines] (use-state
                                    defaultTotalMines)
        [gameState setGamestate] (use-state :init)]
  ;  (println "root gamestate is" gameState)
    ($ :<> 
        ($ boardTable  {:board b
                          :size s
                          :gameState gameState
                          :setGameState setGamestate
                          :setBoard setB
                          :totalMines totalMines})
       ($ counter {:gameState gameState :c c :setC setC}) 
       ;($ stateUI { :s gameState :c c})
       ($ restartGame {:setC setC
                       :totalMines totalMines
                       :size s
                       :setGameState setGamestate
                       :setB setB})
       ($ selectDifficulty {
                            :setGameState setGamestate
                            :setSize setS
                            :setB setB
                            :setC setC
                            :setM setTotalMines})
       ;($ widthIs)
        )))
      

(def myElem (ht/createElem "div"))
(ht/appendElem myElem ht/dBody)
(defonce root 
  (create-root myElem))

(defn initFn []
  (render-root ($ app) root))