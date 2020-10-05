(ns sketches.dragon-curve
  (:require [quil.core :as q]
            [quil.middleware :as middleware]
            [sketches.palette :as pal]
            [util.files :as f]
            [util.plot :as p]))

(def body (.-body js/document))
;(def w (.-clientWidth body))
;(def h (.-clientHeight body))

(def w 4096)
(def h 4096)
(def unit (/ w 100))

(def palette pal/eighties)
(def color (rand-nth (:colors palette)))
(def per-row 5)
(def size (* (/ 90 per-row) unit))
(def margin (* 2.75 unit))


;;;;;;;;

(defn replace-at [s idx replacement]
  (str (subs s 0 idx) replacement (subs s (inc idx))))

(defn dragon-curve
  [c]
  (let [copy (replace-at c (/ (- (count c) 1) 2) "L")]
    (str c "R" copy)))

(defn dragon-curve-iteration
  [c iterations]
  (nth (iterate dragon-curve c) iterations))

(defn sketch-setup []
  []
  (q/angle-mode :degrees)
  {:origin [margin margin]
   :dragon (dragon-curve-iteration "R" 9)
   :size [[0 size] [0 size]]
   :time 0})

(defn sketch-update
  "Returns the next state to render. Receives the current state as a paramter."
  [state]
  (let [dragon (:dragon state)]
    (assoc state :dragon (dragon-curve dragon)))
  (assoc state :time (+ (:time state) 0.01)))


(defn dragon-draw
  [dragon t l color]
  (doseq [el dragon]
    (apply q/stroke color)
    (case el
      "R" (do
            (q/rotate (+ 90 (q/noise t)))
            (q/line 0 0 (* (Math/sin t) l) 0)
            (q/translate (* (Math/sin t) l) 0))
      "L" (do
            (q/rotate (- -90 (q/noise t)))
            (q/line 0 0 (* (q/noise t) l) 0)
            (q/translate (* (q/noise t) l) 0)))))

(defn sketch-draw [{origin :origin
                    dragon :dragon
                    size   :size
                    t      :time}]
  (apply q/background (:background palette))
  (q/translate (/ w 2) (/ h 2))
  (apply q/stroke color)
  (q/stroke-weight (* unit 0.1))

  (let [length (count dragon)
        l (* unit 2)]
    (when (> length) 1
                     (q/push-matrix)
                     (q/rotate -90)
                     (q/line 0 0 l 0)
                     (q/translate l 0)
                     (dragon-draw dragon t l color)
                     (q/pop-matrix)

                     (q/push-matrix)
                     (q/rotate -180)
                     (q/line 0 0 l 0)
                     (q/translate l 0)
                     (dragon-draw dragon t l color)
                     (q/pop-matrix)

                     (q/push-matrix)
                     (q/rotate -270)
                     (q/line 0 0 l 0)
                     (q/translate l 0)
                     (dragon-draw dragon t l color)
                     (q/pop-matrix)

                     (q/push-matrix)
                     (q/rotate 0)
                     (q/line 0 0 l 0)
                     (q/translate l 0)
                     (dragon-draw dragon t l color)
                     (q/pop-matrix))))



(defn create [canvas]
  (q/sketch
    :host canvas
    :size [w h]
    :draw #'sketch-draw
    :setup #'sketch-setup
    :update #'sketch-update
    :key-pressed (f/save-image "dragon-curve.png")
    :middleware [middleware/fun-mode]
    :settings (fn []
                (q/random-seed 666)
                (q/noise-seed 666))))

(defonce sketch (create "sketch"))