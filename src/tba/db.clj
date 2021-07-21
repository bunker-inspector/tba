(ns tba.db
  (:require [tba.config :as config]))

(def config
  {:dbtype config/db-type
   :classname config/db-classname
   :subprotocol config/db-subprotocol
   :subname config/db-path
   :dbname config/db-path})
