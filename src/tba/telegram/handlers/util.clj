(ns tba.telegram.handlers.util
  (:require [clojure.string :as str])
  (:import org.telegram.telegrambots.meta.api.objects.Update))

(defn split-ws [s]
  (str/split s #"\s+"))

(defn split-us [s]
  (str/split s #"_"))

(defn cmd-key
  "Util method that gets the nth token from message text as a key.
  Eg. Given `character new Craig`, (cmd-key u 1) gives `:new`"
  [level ^Update u]
  (let [msg-text (subs (-> u .getMessage .getText) 1)
        cmd-base (nth (split-ws msg-text) level)]
    [:command (keyword cmd-base)]))

(defn callback-ctx
  "Util method to get the context that a callback applies to.
  Eg. Given the callback `character_new_done` and level 1 `:new` will be returned
  For this reason, all callback keys should be in snake_case with only lowecase, alphabetical characters="
  [level ^Update u]
  [:callback
   (-> u .getCallbackQuery .getData split-us (nth level) keyword)])

(defn find-relevant-key
  "If the update is a callback, uses the callback data to determine context
  otherwise uses the message text"
  [level u]
  (if (.hasCallbackQuery u)
    (callback-ctx level u)
    (cmd-key level u)))
