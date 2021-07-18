(ns tba.telegram.handlers
  (:require [tba.telegram.handlers
             [character :as character]
             [util :refer [find-relevant-key]]]))

(defmulti router (comp second (partial find-relevant-key 0)))
(defmethod router :character [u] (character/handler u))
(defmethod router :default [& _] nil)
