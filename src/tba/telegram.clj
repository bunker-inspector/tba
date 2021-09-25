(ns tba.telegram
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str])
  (:import org.telegram.telegrambots.bots.TelegramLongPollingBot
           org.telegram.telegrambots.meta.api.objects.Update
           org.telegram.telegrambots.meta.TelegramBotsApi
           org.telegram.telegrambots.updatesreceivers.DefaultBotSession
           org.telegram.telegrambots.meta.api.methods.send.SendMessage
           org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
           org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
           org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton))

(defrecord CreateResponse [chat-id text kb])
(defrecord EditResponse [chat-id msg-id text kb])

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

(defmethod respond-with EditResponse [{:keys [chat-id msg-id text kb]}]
  (let [msg (doto (EditMessageText.)
              (.setChatId chat-id)
              (.setMessageId msg-id)
              (.setText text))]
    (when kb
      (.setReplyMarkup msg (build-kb kb)))
    msg))

(defmethod respond-with :default [msg]
  (log/infof "Not responding to %s" msg))

(defn- split-ws [s]
  (str/split s #"\s+"))

(defn- split-us [s]
  (str/split s #"_"))

(defn- ->command-map
  [^Update u]
  {:kind   :command
   :tokens (-> u .getMessage .getText (subs 1) split-ws)})

(defn- ->callback-map
  [^Update u]
  {:kind   :callback
   :tokens (-> u .getCallbackQuery .getData split-us)})

(defn- ->handleable-map [u]
  (merge
   (if (.hasCallbackQuery u)
     (->callback-map u)
     (->command-map u))
   {:update u
    :chat-id (-> u .getMessage .getChatId str)
    :msg-id (-> u .getMessage .getMessageId)
    :user-id (-> u .getMessage .getFrom .getId)}))

(defn start [name token
             & {:keys [update-fn]
                :or {update-fn (fn [_] nil)}}]
  (let [bot (proxy
             [TelegramLongPollingBot]

             []

              (onUpdateReceived [^Update u]
                (when-let [response (update-fn (->handleable-map u))]
                  (.execute this (respond-with response))))

              (getBotUsername []
                name)

              (getBotToken []
                token))]
    (.registerBot (TelegramBotsApi. DefaultBotSession) bot)))
