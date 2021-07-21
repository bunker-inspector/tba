(ns tba.db.character
  (:require [tba.db :as db]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [honey.sql :as honey]))

(comment
  (jdbc/execute! (-> db/config jdbc/get-datasource)
                 (honey/format {:select [:*] :from [:characters]})))
