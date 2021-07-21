(ns tba.core
  (:gen-class)
  (:require [tba.config :as config]
            [tba.telegram :as telegram]
            [tba.telegram.handlers :as handlers]))

(defn start [name token update-fn]
  (telegram/start name token :update-fn update-fn))

(defn -main []
  (start "tba" config/telegram-bot-token handlers/router))

(comment
  (def debug (atom {}))
  (add-tap (fn [[k v]] (swap! debug assoc k v)))

  ;; (tap> [:a 1])
  ;; @debug
  ;; => {:a 1}
  )
