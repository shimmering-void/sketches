(ns sketches.function-plot
  (:require [quil.core :as q]
            [quil.middleware :as middleware]
            [sketches.palette :as pal]
            [save-image :as f]
            [plot :as p]))

;(def body (.-body js/document))
;(def w (.-clientWidth body))
;(def h (.-clientHeight body))

(def w 4096)
(def h 4096)
(def unit (/ w 100))

(def palette pal/eighties-light)
(def color (rand-nth (:colors palette)))
(def per-row 5)
(def radius 16)
(def size (* (/ 90 per-row) unit))
(def spacing (* (/ 4.5 (- per-row 1)) unit))
(def margin (* 2.75 unit))

(defn f [t] (+ (Math/sin (Math/log (Math/cos t)))))

(defn polar [radius winding f t]
  {:x (* radius (f t) (Math/cos (* winding t)))
   :y (* radius (f t) (Math/sin (* winding t)))})

(defn g [w t] (polar radius w f t))

;;;;;;;;

(defn plot [idx w]
  {:samples (p/sample #(g w %) [0 (* 8 Math/PI)] 0.02)
   :position [(* (mod idx per-row) (+ size spacing)) (* (quot idx per-row) (+ size spacing))]})

(defn sketch-setup []
  []
  {:origin [margin margin]
   :plots (map-indexed plot (range 0 Math/PI (/ Math/PI (* per-row per-row))))
   :bounds [[(- radius) radius] [(- radius) radius]]
   :size [[0 size] [0 size]]})

(defn sketch-update
  "Returns the next state to render. Receives the current state as a paramter."
  [state]
  state)


(defn sketch-draw [{[ox oy] :origin
                    plots :plots
                    [bx by] :bounds
                    [sx sy] :size}]
  (apply q/background (:background palette))
  (doseq [{samples :samples
           [px py] :position} plots]
    (doseq [[p1 p2] (map vector samples (drop 1 samples))]
      (apply q/stroke color)
      (apply q/fill color)
      (q/stroke-weight (* 0.2 unit))
      (let [x1 (+ (p/map-range (:x p1) bx sx) ox px)
            y1 (+ (p/map-range (:y p1) by sy) oy py)
            x2 (+ (p/map-range (:x p2) bx sx) ox px)
            y2 (+ (p/map-range (:y p2) by sy) oy py)]
        (q/line x1 y1 x2 y2)))))



(defn create [canvas]
  (q/sketch
    :host canvas
    :size [w h]
    :draw #'sketch-draw
    :setup #'sketch-setup
    :update #'sketch-update
    :key-pressed (f/save-image "function-plot.png")
    :middleware [middleware/fun-mode]
    :settings (fn []
                (q/random-seed 666)
                (q/noise-seed 666))))

(defonce sketch (create "sketch"))