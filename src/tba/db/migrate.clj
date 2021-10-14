(ns tba.db.migrate
  (:require [ragtime.jdbc :as jdbc]
            [tba.db :as db]
            [ragtime.repl :as repl]))

(def config
  {:datastore (jdbc/sql-database db/config)
   :migrations (jdbc/load-directory "migrations")})

(defn migrate []
  (repl/migrate config))

(defn rollback []
  (repl/rollback config))
