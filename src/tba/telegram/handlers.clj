(ns tba.telegram.handlers
  (:require [tba.telegram.handlers
             [character :as character]
             [util :as util]]))

(defmulti router (comp second util/current))
(defmethod router :character [h] (character/handler (util/next h)))
(defmethod router :default [& _] nil)