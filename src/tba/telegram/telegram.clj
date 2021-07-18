(ns tba.telegram
  (:import org.telegram.telegrambots.bots.TelegramLongPollingBot
           org.telegram.telegrambots.meta.api.objects.Update
           org.telegram.telegrambots.meta.TelegramBotsApi
           org.telegram.telegrambots.updatesreceivers.DefaultBotSession
           org.telegram.telegrambots.meta.api.methods.send.SendMessage))

(defrecord TextResponse [chat-id text])

(defmulti respond-with class)
(defmethod respond-with TextResponse [{chat-id :chat-id text :text}]
  (doto (SendMessage.)
              (.setChatId chat-id)
              (.setText text)))


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
