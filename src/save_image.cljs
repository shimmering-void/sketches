(ns save-image
  (:require [quil.core :as q]))

(defn save-image
  [filename]
  (fn [state { :keys [key key-code] }]
    (case key
      (:s) (do
             (println "Saving file...")
             (q/save filename))
      state)))