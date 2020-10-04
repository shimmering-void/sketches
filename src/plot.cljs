(ns plot)

(defn map-range [x [in-min in-max] [out-min out-max]]
  (+ (/ (* (- x in-min) (- out-max out-min)) (- in-max in-min)) out-min))

(defn sample [f [min max] precision]
  (map f (range min max precision)))