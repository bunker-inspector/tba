(ns tba.core
  (:gen-class)
  (:require [tba.config :as config]
            [tba.telegram :as telegram]
            [tba.db.migrate :as migrate]
            [tba.telegram.handlers :as handlers]))

(defn start [name token update-fn]
  (telegram/start name token :update-fn update-fn))

(defn -main []
  (migrate/migrate)
  (start "tba" config/telegram-bot-token handlers/router))

(comment
  (def debug (atom {}))
  (add-tap (fn [[k v]] (swap! debug assoc k v)))

  ;; (tap> [:a 1])
  ;; @debug
  ;; => {:a 1}
  )
