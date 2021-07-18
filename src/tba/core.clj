(ns tba.core
  (:gen-class)
  (:require [tba.telegram :as telegram]))

(def ^:private +token+ (System/getenv "TELEGRAM_BOT_TOKEN"))

(defn character-handler [update]
  (telegram/->TextResponse
   (-> update .getMessage .getChatId str)
   (-> update .getMessage .getText)))

(defn select-handler [& _] :character)

(defmulti router select-handler)
(defmethod router :character [u]  (character-handler u))
(defmethod router :default [& _] nil)

(defn start [name token update-fn]
  (telegram/start name token :update-fn update-fn))

(defn -main []
  (start "tba" +token+ router))

(comment
  (def debug (atom {}))
  (add-tap (fn [[k v]] (swap! debug assoc k v)))

  ;; (tap> [:a 1])
  ;; @debug
  ;; => {:a 1}
  )
