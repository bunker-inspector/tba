(ns tba.engine.character
  (:require [tba.db.character :as db]))

(defn create [character]
  (db/save character))

(defn delete [character]
  (db/delete character))

(defn fetch [user-id]
  (db/fetch user-id))
