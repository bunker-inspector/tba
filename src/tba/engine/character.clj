(ns tba.engine.character
  (:require [tba.db.character :as db]))

(defn create-character [character]
  (db/save character))
