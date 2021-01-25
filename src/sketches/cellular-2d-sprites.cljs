(ns sketches.cellular-2d-sprites
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


(def grid-size 65)
(def grid-width grid-size)
(def grid-height grid-size)

(defn sketch-setup []
  []
  ;; (q/set-state! :image (q/load-image "https://placekitten.com/100/100"))
  {:first-render true
   :count 0
   :images [(q/load-image "/sprites/0.png") (q/load-image "/sprites/1.png") (q/load-image "/sprites/2.png") (q/load-image "/sprites/3.png") (q/load-image "/sprites/4.png") (q/load-image "/sprites/5.png") (q/load-image "/sprites/6.png") (q/load-image "/sprites/7.png")]
   :cells (set-cell (make-grid grid-width grid-height) (/ grid-width 2) (/ grid-width 2) {:value 1 :color 0})
   :rule-fn rules/test-rules
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
    images :images
    rule-fn :rule-fn
    generations :generations
    :as state}]

  (if (< count 31)
    (let [next (next-gen cells rule-fn)]
      {:first-render false
       :count (inc count)
       :cells next
       :images images
       :rule-fn rule-fn
       :generations (conj generations cells)})
    state))

(def size 16)

(defn draw-cells [cells images]
  (doseq [[x column] (map-indexed vector cells)]
    (doseq [[y cell] (map-indexed vector column)]
      ;; (when (= (:value cell) 0)
      ;;   (when (q/loaded? (get images 0)) (q/image (get images 0) (* x size) (* y size))))
      ;; (when (= (:value cell) 1))
      (when (q/loaded? (get images (:color cell))) (q/image (get images (:color cell)) (* x size) (* y size))))))

(defn sketch-draw [{_first-render :first-render
                    _count :count
                    cells :cells
                    images :images
                    _generations :generations}]
  (apply q/background (conj (:background palette) 120)) ;; keep a few onion skinned frames
  (q/stroke-weight 0)
  (draw-cells cells images))


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

