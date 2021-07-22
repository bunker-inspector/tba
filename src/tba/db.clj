(ns tba.db
  (:require [tba.config :as config]
            [next.jdbc :as jdbc]
            #_[next.jdbc.sql :as sql]
            [honey.sql :as honey]
            [inflections.core :as inflections]))

(def config
  {:dbtype config/db-type
   :classname config/db-classname
   :subprotocol config/db-subprotocol
   :subname config/db-path
   :dbname config/db-path})

(defn- clj-ify [result]
  (some->> result
           vec
           (map (fn [[a b]]
                  [(inflections/hyphenate a) b]))
           (into {})))

(defn execute-one! [sql]
  (clj-ify (jdbc/execute-one! config (honey/format sql))))
