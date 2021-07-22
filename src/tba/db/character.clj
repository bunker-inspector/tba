(ns tba.db.character
  (:require [tba.db :as db]))

(defn save [character]
  (db/execute-one! {:insert-into :characters
                                    :values [character]}))

(defn delete [{id :id}]
  (db/execute-one! {:delete-from :characters
                      :where [:= :id id]}))

(defn fetch [user-id]
  (db/execute-one! {:select [:*]
                    :from [:characters]
                    :where [:= :user-id user-id]}))

(comment
  (db/execute-one! {:select [:*] :from [:characters]}))
