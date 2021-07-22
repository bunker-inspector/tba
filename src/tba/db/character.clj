(ns tba.db.character
  (:require [tba.db :as db]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [honey.sql :as honey]))

(defn save [character]
  (sql/insert! db/config (honey/format {:insert-into :characters :values [character]})))

(comment
  (jdbc/execute! db/config (honey/format {:select [:*] :from [:characters]})))
