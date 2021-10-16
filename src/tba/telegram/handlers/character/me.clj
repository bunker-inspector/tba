(ns tba.telegram.handlers.character.me
  (:require [tba.telegram.update-map :as um]
            [tba.telegram :as telegram]
            [tba.engine.character :as engine.character]))

(def ^:private +me-template+
  "
Name: %s
Level: %d
Exp: %d
Str: %d
Dex: %d
Con: %d
Wis: %d
Int: %d
Cha: %d
")

(defn handle-me [{:keys [user-id chat-id message-id]}]
  (let [character (engine.character/fetch user-id)
        response-text (if character
                        (format +me-template+
                                (:characters/name character)
                                (:characters/level character)
                                (:characters/exp character)
                                (:characters/str character)
                                (:characters/dex character)
                                (:characters/con character)
                                (:characters/wis character)
                                (:characters/int character)
                                (:characters/cha character))
                        "You have not created a character.")]
    (telegram/map->CreateResponse {:chat-id chat-id
                                   :message-id message-id
                                   :text response-text})))

(defn delete-me [{:keys [user-id chat-id message-id]}]
  (engine.character/delete {:user-id user-id})
  (telegram/map->CreateResponse {:chat-id chat-id
                                 :message-id message-id
                                 :text "Your character has been deleted."}))

(defmulti handler um/curr-step)
(defmethod handler [:command :done] [um] (handle-me um))
(defmethod handler [:command :delete] [um] (delete-me um))
