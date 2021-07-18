(ns tba.domain.character)

(def +base-stat+ 10)
(def +create-stat-max+ 16)
(def +create-stat-min+ 6)
(def +create-points+ 10)

(defrecord Char [name level exp str dex con wis int cha])

(defn default [name]
   (Char.
    name
    1
    0
    +base-stat+
    +base-stat+
    +base-stat+
    +base-stat+
    +base-stat+
    +base-stat+))
