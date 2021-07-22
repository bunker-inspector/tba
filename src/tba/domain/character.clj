(ns tba.domain.character)

(def +base-stat+ 10)
(def +create-stat-max+ 16)
(def +create-stat-min+ 6)
(def +create-points+ 10)

(defrecord Char [user-id name level exp str dex con wis int cha])

(defn base [user-id name]
  (Char.
   user-id
   name
   1
   0
   +base-stat+
   +base-stat+
   +base-stat+
   +base-stat+
   +base-stat+
   +base-stat+))
