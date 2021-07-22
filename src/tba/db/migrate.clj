(ns tba.db.migrate
  (:require [ragtime.jdbc :as jdbc]
            [tba.db :as db]))

(def config
  {:datastore (jdbc/sql-database db/config)
   :migrations (jdbc/load-directory "migrations")})

(comment
  (require '[ragtime.repl :as repl])
  (repl/migrate config)
  (repl/rollback config))
