(ns sketches.cellular-2d
  (:require [quil.core :as q]
            [quil.middleware :as middleware]
            [sketches.palette :as pal]
            [util.files :as f]))

(println "Perlin flow hellooooo!")

(def body (.-body js/document))
(def w (.-clientWidth body))
(def h (.-clientHeight body))

(def palette pal/flower)

(def default-cell
  {:value 0
   :color 0})

(defn make-grid [w h]
  (into []
        (take h (repeat
                 (into [] (take w (repeat default-cell)))))))

(defn get-cell [cells x y]
  (get-in cells [y x]))

(defn set-cell [cells x y v]
  (assoc-in cells [y x] v))

(defn test-rules [neighbours]
  (case neighbours

    [[1 0 0]
     [0 0 0]
     [0 0 0]] [5 1]

    [[0 0 1]
     [0 0 0]
     [0 0 0]] [5 1]

    [[0 0 0]
     [0 0 0]
     [1 0 0]] [5 1]

    [[0 0 0]
     [0 0 0]
     [0 0 1]] [5 1]

    [[1 1 0]
     [0 0 0]
     [0 0 0]] [4 1]

    [[0 0 0]
     [0 0 0]
     [0 1 1]] [4 1]

    [[0 1 1]
     [0 0 0]
     [0 0 0]] [4 1]

    [[0 0 0]
     [0 0 0]
     [1 1 0]] [4 1]


    [[0 1 0]
     [0 0 0]
     [0 0 0]] [3 1]

    [[0 0 0]
     [0 0 0]
     [0 1 0]] [3 1]

    [[0 0 0]
     [0 0 1]
     [0 0 0]] [3 1]

    [[0 0 0]
     [1 0 0]
     [0 0 0]] [3 1]

    [[1 0 1]
     [0 0 0]
     [0 0 0]] [1 1]

    ;; [[0 0 0]
    ;;  [0 0 0]
    ;;  [1 0 1]] [1 1]

    [[0 0 1]
     [0 0 0]
     [0 0 1]] [1 1]

    [[1 0 0]
     [0 0 0]
     [1 0 0]] [1 1]

    [[1 0 0]
     [1 0 1]
     [1 0 0]] [6 1]

    [[1 1 1]
     [0 0 0]
     [0 1 0]] [6 1]

    [[0 1 0]
     [0 0 0]
     [1 1 1]] [6 1]

    [[0 0 1]
     [1 0 1]
     [0 0 1]] [6 1]

    ;; [[0 0 0]
    ;;  [0 0 0]
    ;;  [1 0 0]] [6 1]

    ;; [[0 0 0]
    ;;  [0 0 0]
    ;;  [0 0 1]] [3 1]

    ;; [[0 0 1]
    ;;  [0 1 0]
    ;;  [1 0 0]] [6 1]


    ;; [[1 0 1]
    ;;  [0 0 0]
    ;;  [1 0 0]] [7 1]

    ;; [[0 0 0]
    ;;  [0 0 1]
    ;;  [0 0 0]] [1 1]

    ;; [[1 0 1]
    ;;  [0 0 0]
    ;;  [0 0 0]] [1 1]

    ;; [[1 0 1]
    ;;  [0 0 0]
    ;;  [1 0 1]] [1 1]

    ;; [[1 0 0]
    ;;  [0 0 0]
    ;;  [0 0 0]] [3 1]

    ;; [[0 0 1]
    ;;  [0 0 0]
    ;;  [0 0 0]] [5 1]

    ;; [[1 0 0]
    ;;  [0 0 0]
    ;;  [0 0 1]] [4 1]

    [2 0]))

(defn more-rules [neighbours]
  (case neighbours

    [[0 1 0]
     [0 0 0]
     [0 0 0]] [5 1]

    [[0 0 0]
     [0 0 1]
     [0 0 0]] [5 1]

    [[0 0 0]
     [1 0 0]
     [0 0 0]] [5 1]

    [[0 0 0]
     [0 0 0]
     [0 1 0]] [5 1]

    [[0 1 0]
     [1 0 0]
     [0 0 0]] [4 1]

    [[0 0 0]
     [0 0 1]
     [0 1 0]] [4 1]

    [[0 1 0]
     [0 0 1]
     [0 0 0]] [4 1]

    [[0 0 0]
     [1 0 0]
     [0 1 0]] [4 1]


    [[0 1 0]
     [0 1 0]
     [0 0 0]] [3 1]

    [[0 0 0]
     [0 1 0]
     [0 1 0]] [3 1]

    [[0 1 0]
     [0 1 1]
     [0 0 0]] [3 1]

    [[0 0 0]
     [1 1 0]
     [0 1 0]] [3 1]

    ;; [[1 0 1]
    ;;  [0 0 0]
    ;;  [0 0 0]] [1 1]

    ;; ;; [[0 0 0]
    ;; ;;  [0 0 0]
    ;; ;;  [1 0 1]] [1 1]

    ;; [[0 0 1]
    ;;  [0 0 0]
    ;;  [0 0 1]] [1 1]

    ;; [[1 0 0]
    ;;  [0 0 0]
    ;;  [1 0 0]] [1 1]

    ;; [[1 0 0]
    ;;  [1 0 1]
    ;;  [1 0 0]] [6 1]

    ;; [[1 1 1]
    ;;  [0 0 0]
    ;;  [0 1 0]] [6 1]

    ;; [[0 1 0]
    ;;  [0 0 0]
    ;;  [1 1 1]] [6 1]

    ;; [[0 0 1]
    ;;  [1 0 1]
    ;;  [0 0 1]] [6 1]

    ;; [[0 0 0]
    ;;  [0 0 0]
    ;;  [1 0 0]] [6 1]

    ;; [[0 0 0]
    ;;  [0 0 0]
    ;;  [0 0 1]] [3 1]

    ;; [[0 0 1]
    ;;  [0 1 0]
    ;;  [1 0 0]] [6 1]


    ;; [[1 0 1]
    ;;  [0 0 0]
    ;;  [1 0 0]] [7 1]

    ;; [[0 0 0]
    ;;  [0 0 1]
    ;;  [0 0 0]] [1 1]

    [[1 0 1]
     [0 0 0]
     [0 0 0]] [1 1]

    [[1 0 1]
     [0 0 0]
     [1 0 1]] [1 1]

    ;; [[1 0 0]
    ;;  [0 0 0]
    ;;  [0 0 0]] [3 1]

    ;; [[0 0 1]
    ;;  [0 0 0]
    ;;  [0 0 0]] [5 1]

    ;; [[1 0 0]
    ;;  [0 0 0]
    ;;  [0 0 1]] [4 1]

    [2 0]))

(defn one-or-zero []
  (rand-nth [0 1]))


(defn random-rule [id]
  [[[(one-or-zero) 0 (one-or-zero)]
    [0 (one-or-zero) 0]
    [(one-or-zero) 0 (one-or-zero) 0]] [id 1]])

(defn flip-x [[[[a b c]
                [d e f]
                [g h i]] v]]
  [[[c b a]
    [f e d]
    [i h g]] v])

(defn flip-y [[[[a b c]
                [d e f]
                [g h i]] v]]
  [[[g h i]
    [d e f]
    [a b c]] v])

(defn random-symmetric-ruleset [id]
  (let [rule (random-rule id)
        x (flip-x rule)
        y (flip-y rule)
        xy (flip-x y)
        yx (flip-y x)]
    [rule x y xy yx]))

(def rules (concat
            [[[[0 0 0]
               [0 0 0]
               [0 1 0]] [5 1]]

             [[[0 1 0]
               [0 0 0]
               [0 0 0] [5 1]]]

             [[[0 0 0]
               [1 0 0]
               [0 0 0]] [5 1]]

             [[[0 0 0]
               [0 0 1]
               [0 0 0]] [5 1]]]
            (random-symmetric-ruleset 1) (random-symmetric-ruleset 2) (random-symmetric-ruleset 3)
            (random-symmetric-ruleset 1) (random-symmetric-ruleset 2) (random-symmetric-ruleset 3)))
(println rules)

(defn random-rules [window]
  ;; (println window)
  (let [[_ v] (first (filter (fn [[rule _]] (= rule window)) rules))]
    ;; (println match)
    (or v [7 0])))

(def grid-size 128)
(def grid-width grid-size)
(def grid-height grid-size)

(defn sketch-setup []
  []
  {:first-render true
   :count 0
   :cells (set-cell (make-grid grid-width grid-height) (/ grid-width 2) (/ grid-width 2) {:value 1 :color 0})
   ;; https://mathworld.wolfram.com/ElementaryCellularAutomaton.html
   ;; sampling 2 binary cells gives us 8 possible states to map
  ;;  :rules [0 0 0 1 1 1 1 0] ;; rule 30
  ;;  :rules [0 0 1 0 1 1 0 1] ;; rule 45
   :rule-fn more-rules ;; rule 78
   :generations []})

(defn apply-rules [rule-fn window]
  ;; (println rules window (get rules (neighbourhood->rule window)))
  (let [[rule value] (rule-fn window)]
    {:value value
     :color rule}))

(defn nil->zero [n]
  (if (nil? n) 0 n))

(defn cell->neighbours [cells x y]
  [(map nil->zero [(get-in cells [(dec x) (dec y) :value])
                   (get-in cells [x (dec y) :value])
                   (get-in cells [(inc x) (dec y) :value])])
   (map nil->zero [(get-in cells [(dec x) y :value])
                   (get-in cells [x y :value])
                   (get-in cells [(inc x) y :value])])
   (map nil->zero [(get-in cells [(dec x) (inc y) :value])
                   (get-in cells [x (inc y) :value])
                   (get-in cells [(inc x) (inc y) :value])])])

(defn next-gen [cells rule-fn]
  ;; iterate entire grid -> map to new grid
  ;; map-indexed should get us there?
  (into [] (for [[x column] (map-indexed vector cells)]
             (into [] (for [[y _cell] (map-indexed vector column)]
                        (let [[rule-id val] (rule-fn (cell->neighbours cells x y))]
                          {:value val
                           :color rule-id}))))))

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
    rule-fn :rule-fn
    generations :generations
    :as state}]

  (if (< count 1024)
    (let [next (next-gen cells rule-fn)]
      ;; (println next)
      {:first-render false
       :count (inc count)
       :cells next
       :rule-fn rule-fn
       :generations (conj generations cells)})
    state))

(def size 4)

(defn draw-cells [cells]
  ;; (q/translate (/ w 2) 0)
  (doseq [[x column] (map-indexed vector cells)]
    (doseq [[y cell] (map-indexed vector column)]
      (when (= (:value cell) 1)
        (apply q/fill (get (:colors palette) (nil->zero (:color cell))))
        (q/rect (* x size) (* y size) size size)
        ;; (q/triangle (* x size) (* y size) (* (inc x) size) (* y size) (* (inc x) size) (* (inc y) size))
        ;; (q/ellipse (* x size) (* y size) (* 1.5 size) (* 1.5 size))
        ;; (q/translate 0.01 0.01)
        ;; (q/rect (* x size) (* y size) size size)
        ))))

(defn sketch-draw [{first-render :first-render
                    count :count
                    cells :cells
                    generations :generations}]
  (apply q/background (conj (:background palette) 120))
  (q/stroke-weight 0)
  ;; (q/translate (/ w 2) (/ h 2))
  ;; (q/rotate (* count 0.1))
  ;; (q/translate (/ w -2) (/ h -2))
  (draw-cells cells)
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

(defonce sketch (create "sketch")) nil
nil
