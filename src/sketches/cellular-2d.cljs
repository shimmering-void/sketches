(ns sketches.cellular-2d
  (:require [quil.core :as q]
            [quil.middleware :as middleware]
            [sketches.palette :as pal]
            [sketches.celllar-2d-rules :as rules]
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


(def grid-size 128)
(def grid-width grid-size)
(def grid-height grid-size)

(defn sketch-setup []
  []
  {:first-render true
   :count 0
   :cells (set-cell (make-grid grid-width grid-height) (/ grid-width 2) (/ grid-width 2) {:value 1 :color 0})
   :rule-fn rules/more-rules
   :generations []})

(defn nil->zero [n]
  (if (nil? n) 0 n))

(defn default [v n]
  (if (nil? n) v n))

(defn cell->neighbours
  "Take a cell and return the 8 surrounding cells + itself.

   [[TL T TR]
    [L cell R]
    [BL B BR]]   
   "
  [cells x y]

  [(map (partial default 0) [(get-in cells [(dec x) (dec y) :value])
                             (get-in cells [x (dec y) :value])
                             (get-in cells [(inc x) (dec y) :value])])
   (map (partial default 0) [(get-in cells [(dec x) y :value])
                             (get-in cells [x y :value])
                             (get-in cells [(inc x) y :value])])
   (map (partial default 0) [(get-in cells [(dec x) (inc y) :value])
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

(defn sketch-update
  "Returns the next state to render. Receives the current state as a paramter."
  [{count :count
    cells :cells
    rule-fn :rule-fn
    generations :generations
    :as state}]

  (if (< count 1024)
    (let [next (next-gen cells rule-fn)]
      {:first-render false
       :count (inc count)
       :cells next
       :rule-fn rule-fn
       :generations (conj generations cells)})
    state))

(def size 4)

(defn draw-cells [cells]
  (doseq [[x column] (map-indexed vector cells)]
    (doseq [[y cell] (map-indexed vector column)]
      (when (= (:value cell) 1)
        (apply q/fill (get (:colors palette) (default 0 (:color cell))))
        (q/rect (* x size) (* y size) size size)))))

(defn sketch-draw [{_first-render :first-render
                    _count :count
                    cells :cells
                    _generations :generations}]
  (apply q/background (conj (:background palette) 120)) ;; keep a few onion skinned frames
  (q/stroke-weight 0)
  (draw-cells cells))


(defn create [canvas]
  (q/sketch
   :host canvas
   :size [w h]
   :draw #'sketch-draw
   :setup #'sketch-setup
   :update #'sketch-update
   :key-pressed (f/save-image "cells-2d.png")
   :middleware [middleware/fun-mode]
   :settings (fn []
               (q/random-seed 666)
               (q/noise-seed 666))))

(defonce sketch (create "sketch"))

