(ns util.plot)

(defn map-range [x [in-min in-max] [out-min out-max]]
  "map between two ranges, i.e. mapping 0.5 from [0 1] to [-1 1] gives 0."
  (+ (/ (* (- x in-min) (- out-max out-min)) (- in-max in-min)) out-min))

(defn project-point
  "maps 2D point between two 2D ranges, i.e. mapping [0.5 0.5] from [[0 1] [0 1]] to [[-1 1] [-1 1]] gives [0 0]."
  [[x y] [ix iy] [ox oy]]
  [(map-range x ix ox)
   (map-range y iy oy)])

(defn ^:private inner-add-points
  [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn add-points
  "add points together component-wise, [1 2] + [3 4] = [4 6]"
  [p & ps]
  (reduce inner-add-points p ps))


(defn sample [f [min max] precision]
  "sample the function f over the provided range"
  (map f (range min max precision)))

(defn to-segments
  "take a list of points of a function sample and turn them into plottable line segments"
  [samples]
  (map vector samples (drop 1 samples)))