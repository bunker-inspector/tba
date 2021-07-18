(ns tba.core
  (:gen-class)
  (:require [tba.telegram.handlers :as handlers]
   [tba.telegram :as telegram]))

(def ^:private +token+ (System/getenv "TELEGRAM_BOT_TOKEN"))

(defn start [name token update-fn]
  (telegram/start name token :update-fn update-fn))

(defn -main []
  (start "tba" +token+ handlers/router))

(comment
  (def debug (atom {}))
  (add-tap (fn [[k v]] (swap! debug assoc k v)))

  ;; (tap> [:a 1])
  ;; @debug
  ;; => {:a 1}
  )
