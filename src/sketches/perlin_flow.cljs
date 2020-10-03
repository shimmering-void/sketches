(ns sketches.perlin-flow
  (:require [quil.core :as q]
            [quil.middleware :as middleware]
            [sketches.palette :as pal]))

(println "Perlin flow hellooooo!")

(def body (.-body js/document))
(def w (.-clientWidth body))
(def h (.-clientHeight body))

(def palette pal/eighties)

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

(defn sketch-setup []
  []
  {:first-render true
   :particles (map particle (range 0 2000))})

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
  [{particles :particles
    first-render :first-render}]

  {:first-render false
   :particles (map (fn [p]
                 (assoc p
                   :x         (position (:x p) (:vx p) w)
                   :y         (position (:y p) (:vy p) h)
                   :direction (direction (:x p) (:y p) (:id p))
                   :vx        (velocity (:vx p) (Math/cos (:direction p)))
                   :vy        (velocity (:vy p) (* 2 (:direction p)  (Math/sin (:x p)) (Math/sin (:direction p))))))
               particles)})


(defn sketch-draw [{particles :particles
                    first-render :first-render}]
  (when first-render (apply q/background (:background palette)))
  ;(q/no-stroke)
  (doseq [p particles]
    (apply q/stroke (conj (:color p) 20))
    (apply q/fill (conj (:color p) 20))
    (q/stroke-weight 2)
    (q/line (:x p) (:y p) (+ (:x p) (* (Math/cos (:direction p)) (:size p))) (+ (:y p) (* (Math/sin (:direction p)) (:size p))))
    ))


(defn create [canvas]
  (q/sketch
    :host canvas
    :size [w h]
    :draw #'sketch-draw
    :setup #'sketch-setup
    :update #'sketch-update
    :middleware [middleware/fun-mode]
    :settings (fn []
                (q/random-seed 666)
                (q/noise-seed 666))))

(defonce sketch (create "sketch"))