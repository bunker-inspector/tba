(ns tba.telegram
  (:require [clojure.tools.logging :as log])
  (:import org.telegram.telegrambots.bots.TelegramLongPollingBot
           org.telegram.telegrambots.meta.api.objects.Update
           org.telegram.telegrambots.meta.TelegramBotsApi
           org.telegram.telegrambots.updatesreceivers.DefaultBotSession
           org.telegram.telegrambots.meta.api.methods.send.SendMessage
           org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
           org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton))

(defrecord CreateResponse [chat-id text kb])
(defrecord EditResponse [chat-id text kb])

(defn- build-kb [kb]
  (let [built (map (fn [row]
                     (map (fn [[text callback-key]]
                            (doto (InlineKeyboardButton.)
                              (.setText text)
                              (.setCallbackData callback-key)))
                          row))
                   kb)]
    (doto (InlineKeyboardMarkup.)
      (.setKeyboard built))))

(defmulti respond-with class)

(defmethod respond-with CreateResponse [{:keys [chat-id text kb]}]
  (let [msg (doto (SendMessage.)
              (.setChatId chat-id)
              (.setText text))]
    (when kb
      (.setReplyMarkup msg (build-kb kb)))
    msg))

(defmethod respond-with :default [msg]
  (log/infof "Not responding to %s" msg))

(defn start [name token
             & {:keys [update-fn]
                :or {update-fn (fn [_] nil)}}]
  (let [bot (proxy
             [TelegramLongPollingBot]

             []

              (onUpdateReceived [^Update u]
                (when-let [response (update-fn u)]
                  (.execute this (respond-with response))))

              (getBotUsername []
                name)

              (getBotToken []
                token))]
    (.registerBot (TelegramBotsApi. DefaultBotSession) bot)))
