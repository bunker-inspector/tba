(ns tba.telegram.handlers.util
  (:require [clojure.string :as str])
  (:import org.telegram.telegrambots.meta.api.objects.Update))

(defn current
  [{kind :kind tokens :tokens}]
  [kind (or (first tokens) :done)])

(defn next
  [handleable]
  (update handleable :tokens rest))

(defn params
  [{tokens :tokens}]
  (rest tokens))