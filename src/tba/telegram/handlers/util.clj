(ns tba.telegram.handlers.util
  (:require [clojure.string :as str])
  (:import org.telegram.telegrambots.meta.api.objects.Update))

(defn split-ws [s]
  (str/split s #"\s+"))

(defn cmd-key
  "Util method that gets the nth token from message text as a key.
  Eg. Given `character new Craig`, (cmd-key u 1) gives `:new`"
  [n ^Update u]
  (let [msg-text (subs (-> u .getMessage .getText) 1)
        cmd-base (nth (split-ws msg-text) n)]
    (keyword cmd-base)))
