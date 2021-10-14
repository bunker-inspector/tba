(ns tba.telegram
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log])
  (:import org.telegram.telegrambots.bots.TelegramLongPollingBot
           org.telegram.telegrambots.meta.api.objects.Update
           org.telegram.telegrambots.meta.TelegramBotsApi
           org.telegram.telegrambots.updatesreceivers.DefaultBotSession
           org.telegram.telegrambots.meta.api.methods.send.SendMessage
           org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
           org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
           org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton))

(defrecord CreateResponse [chat-id text kb])
(defrecord EditResponse [chat-id message-id text kb])

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

(defmethod respond-with EditResponse [{:keys [chat-id message-id text kb]}]
  (let [msg (doto (EditMessageText.)
              (.setChatId chat-id)
              (.setMessageId message-id)
              (.setText text))]
    (when kb
      (.setReplyMarkup msg (build-kb kb)))
    msg))

(defmethod respond-with :default [msg]
  (log/infof "Not responding to %s" msg))

(defn- split-whitespace [s]
  (str/split s #"\s+"))

(defn- split-underscores [s]
  (str/split s #"_"))

(defn- command-attributes
  [^Update u]
  (let [cmd-tokens (-> u .getMessage .getText (subs 1) split-whitespace)]
    {:update-type :command
     :message-id (-> u .getMessage .getMessageId)
     :user-id (-> u .getMessage .getFrom .getId)
     :chat-id (-> u .getMessage .getChatId str)
     :raw-message (-> u .getMessage .getText (subs 1))
     :params (map keyword cmd-tokens)}))

(defn- callback-attributes
  [^Update u]
  {:update-type :callback
   :user-id (-> u .getCallbackQuery .getFrom .getId)
   :chat-id (-> u .getCallbackQuery .getMessage .getChatId str)
   :message-id (-> u .getCallbackQuery .getMessage .getMessageId)
   :raw-message (->> u .getCallbackQuery .getData split-underscores)
   :params (->> u .getCallbackQuery .getData split-underscores (map keyword))})

(defn- update-type-attributes
  [^Update u]
  (if (.hasCallbackQuery u)
    (callback-attributes u)
    (command-attributes u)))

(defn ->update-map
  [^Update u]
  (merge
   {:update u}
   (update-type-attributes u)))

(defn start [name token
             & {:keys [update-fn]
                :or {update-fn (fn [_] nil)}}]
  (let [bot (proxy
             [TelegramLongPollingBot]

             []

              (onUpdateReceived [^Update u]
                (tap> [:u u])
                (try
                  (when-let [response (update-fn (->update-map u))]
                    (.execute this (respond-with response)))
                  (catch Exception e
                    (tap> [:e e])
                    (log/errorf "Caught exception: %s" e))))

              (getBotUsername []
                name)

              (getBotToken []
                token))]
    (.registerBot (TelegramBotsApi. DefaultBotSession) bot)))
