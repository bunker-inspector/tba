(ns tba.db.character
  (:require [tba.db :as db]))

(defn delete [params]
  (db/delete :characters params))

(defn save [character]
  (delete (select-keys character [:user-id]))
  (db/execute-one! {:insert-into :characters
                    :values [character]}))

(defn fetch [user-id]
  (db/execute-one! {:select [:*]
                    :from [:characters]
                    :where [:= :user-id user-id]}))

(comment
  (db/execute-one! {:select [:*] :from [:characters]}))
