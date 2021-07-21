(ns tba.config
  (:require [environ.core :refer [env]]))

(def db-classname
  (or (env :db-classname) "org.sqlite.JDBC"))

(def db-path
  (or (env :db-path) "tba.db"))

(def db-subprotocol
  (or (env :db-subprotocol) "sqlite"))

(def db-type
  (or (env :db-type) "sqlite"))

(def telegram-bot-token
  (env :telegram-bot-token))
