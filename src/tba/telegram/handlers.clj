(ns tba.telegram.handlers
  (:require [tba.telegram.handlers
             [character :as character]]
            [tba.telegram.update-map :as um]))

(defmulti router um/param-root)
(defmethod router :character [um] (-> um um/advance character/handler))
(defmethod router :default [& _] nil)
