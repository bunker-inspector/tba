(ns tba.telegram.handlers
  (:require [tba.telegram.handlers
             [character :as character]
             [edit :as edit]
             [util :refer [cmd-key]]])
  (:import org.telegram.telegrambots.meta.api.objects.Update))

(defn select-handler [^Update u]
    (cond (.hasCallbackQuery u) :tbd
          :else (cmd-key 0 u)))

(defmulti router select-handler)
(defmethod router :character [u] (character/handler u))
(defmethod router :edit [u] (edit/handler u))
(defmethod router :default [& _] nil)
