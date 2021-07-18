(ns tba.telegram.handlers.edit
  (:import org.telegram.telegrambots.meta.api.objects.Update))

(defn handler [^Update u]
  (println (-> u .getCallbackQuery)))
