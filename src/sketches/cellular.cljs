(ns sketches.cellular
  (:require [quil.core :as q]
            [quil.middleware :as middleware]
            [sketches.palette :as pal]
            [util.files :as f]))

(println "Perlin flow hellooooo!")

(def body (.-body js/document))
(def w (.-clientWidth body))
(def h (.-clientHeight body))

(def palette pal/flower)

(defn particle
  [id]
  {:id id
   :vx 1
   :vy 1
   :size (q/random 8 16)
   :direction 0
   :x (q/random w)
   :y (q/random h)
   :color (rand-nth (:colors palette))})

(def default-cell
  {:value 0
   :color 0})

(defn sketch-setup []
  []
  {:first-render true
   :count 0
   :cells (assoc (into [] (take 1024 (repeat default-cell))) 511 {:value 1 :color 0})
   ;; https://mathworld.wolfram.com/ElementaryCellularAutomaton.html
   ;; sampling 2 binary cells gives us 8 possible states to map
  ;;  :rules [0 0 0 1 1 1 1 0] ;; rule 30
  ;;  :rules [0 0 1 0 1 1 0 1] ;; rule 45
   :rules [1 1 1 0 0 0 0 1] ;; rule 78
   :generations []})

(defn neighbourhood->rule [window]
  (case window
    [0 0 0] 7
    [0 0 1] 6
    [0 1 0] 5
    [0 1 1] 4
    [1 0 0] 3
    [1 0 1] 2
    [1 1 0] 1
    [1 1 1] 0))

(defn apply-rules [rules window]
  ;; (println rules window (get rules (neighbourhood->rule window)))
  {:value (get rules (neighbourhood->rule window))
   :color (neighbourhood->rule window)})

(defn nil->zero [n]
  (if (nil? n) 0 n))

(defn cell->neighbours [cells i]
  (map nil->zero [(get-in cells [(dec i) :value])
                  (get-in cells [i :value])
                  (get-in cells [(inc i) :value])]))

(defn next-gen [cells rules]
  (->> cells
       (map-indexed (fn [i _] (cell->neighbours cells i)))
       (map (partial apply-rules rules))
       (into [])))

(def noise-zoom
  "Noise zoom level."
  0.025)

(defn position
  "Calculates the next position based on the current, the speed and a max."
  [current delta max]
  (mod (+ current delta) max))


(defn direction
  "Calculates the next direction based on the previous position and id of each particle."
  [x y z]
  (* 2
     Math/PI
     (+ (q/noise (* x noise-zoom) (* y noise-zoom))
        (* 0.2 (q/noise (* x noise-zoom) (* y noise-zoom) (* z noise-zoom))))))

(defn velocity
  "Calculates the next velocity by averaging the current velocity and the added delta."
  [current delta]
  (/ (+ current delta) 2))


(defn sketch-update
  "Returns the next state to render. Receives the current state as a paramter."
  [{count :count
    cells :cells
    rules :rules
    generations :generations
    :as state}]

  (if (< count 1024)
    (let [next (next-gen cells rules)]
      ;; (println next)
      {:first-render false
       :count (inc count)
       :cells next
       :rules rules
       :generations (conj generations cells)})
    state))

(def size 2)

(defn draw-cells [cells y]
  (doseq [[i c] (map-indexed vector cells)]

    (when (= (:value c) 1)
      (apply q/fill (get (:colors palette) (nil->zero (:color c))))
      (q/rect (* i size) y size size))))

(defn sketch-draw [{first-render :first-render
                    count :count
                    cells :cells
                    generations :generations}]
  (when first-render (apply q/background (:background palette)))
  (q/stroke-weight 0)
  (draw-cells cells (* count size))
  ;; (doseq [[i g] (map-indexed vector generations)]
  ;;   ;; (println [i g])
  ;;   (draw-cells g (* (inc i) size)))
  )


(defn create [canvas]
  (q/sketch
   :host canvas
   :size [w h]
   :draw #'sketch-draw
   :setup #'sketch-setup
   :update #'sketch-update
   :key-pressed (f/save-image "cells.png")
   :middleware [middleware/fun-mode]
   :settings (fn []
               (q/random-seed 666)
               (q/noise-seed 666))))

(defonce sketch (create "sketch"))