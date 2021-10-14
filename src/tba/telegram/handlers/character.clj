(ns tba.telegram.handlers.character
  (:require [clojure.string :as str]
            [tba.domain.character :as domain.character]
            [tba.engine.character :as engine.character]
            [tba.telegram :as telegram]
            [tba.telegram.update-map :as um]
            [tba.telegram.handlers.character.me :as me]))

(defn- ck-upper [points stat expr]
  (when (and (> points 0)
             (< stat domain.character/+create-stat-max+))
    expr))

(defn- ck-lower [points stat expr]
  (when (and (< points domain.character/+base-stat+)
             (> stat domain.character/+create-stat-min+))
    expr))

(def building-characters (atom {}))

(def ^:private character-build-template
  "Hail, %s!

Str: %d
Dex: %d
Con: %d
Wis: %d
Int: %d
Cha: %d

Points Remaining: %d")

(defn- character-build-ui [points {:keys [name str dex con wis int cha]}]
  [(format character-build-template name str dex con wis int cha points)
   (filter
    not-empty
    (map
     (partial filter
              (comp not nil?))
     [[(ck-lower points str ["Str -" "character_build_strdn"])
       (ck-upper points str ["Str +" "character_build_strup"])]
      [(ck-lower points dex ["Dex -" "character_build_dexdn"])
       (ck-upper points dex ["Dex +" "character_build_dexup"])]
      [(ck-lower points con ["Con -" "character_build_condn"])
       (ck-upper points con ["Con +" "character_build_conup"])]
      [(ck-lower points wis ["Wis -" "character_build_wisdn"])
       (ck-upper points wis ["Wis +" "character_build_wisup"])]
      [(ck-lower points int ["Int -" "character_build_intdn"])
       (ck-upper points int ["Int +" "character_build_intup"])]
      [(ck-lower points cha ["Cha -" "character_build_chadn"])
       (ck-upper points cha ["Cha +" "character_build_chaup"])]
      [(when (= points 0) ["Done" "character_done"])]]))])

(defn handle-new-character
  [{user-id :user-id params :params chat-id :chat-id}]
  (let [character (domain.character/base user-id (str/join " " (map name (rest params))))
        [text kb] (character-build-ui domain.character/+create-points+ character)]
    (swap! building-characters assoc user-id [domain.character/+create-points+ character])
    (telegram/->CreateResponse chat-id
                               text
                               kb)))

(defn- apply-action [character action points]
  (case action
    :strup [(dec points) (update character :str inc)]
    :strdn [(inc points) (update character :str dec)]
    :dexup [(dec points) (update character :dex inc)]
    :dexdn [(inc points) (update character :dex dec)]
    :conup [(dec points) (update character :con inc)]
    :condn [(inc points) (update character :con dec)]
    :wisup [(dec points) (update character :wis inc)]
    :wisdn [(inc points) (update character :wis dec)]
    :intup [(dec points) (update character :int inc)]
    :intdn [(inc points) (update character :int dec)]
    :chaup [(dec points) (update character :cha inc)]
    :chadn [(inc points) (update character :cha dec)]))

(defn handle-build-callback [{:keys [user-id
                                     chat-id
                                     message-id]
                              [_ action] :params}]
  (let [[points character] (@building-characters user-id)
        [points character :as build-data] (apply-action character action points)
        [text kb] (character-build-ui points character)]
    (swap! building-characters assoc user-id build-data)
    (telegram/->EditResponse chat-id
                             message-id
                             text
                             kb)))

(defn handle-done-callback [{:keys [user-id chat-id message-id]}]
  (let [[_ {character-name :name :as character}] (@building-characters user-id)]
    (engine.character/create character)
    (telegram/map->EditResponse {:chat-id chat-id
                                 :message-id message-id
                                 :text (format "%s's journey begins!" character-name)})))

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

(defmulti handler um/curr-step)
(defmethod handler [:command :new] [um] (handle-new-character um))
(defmethod handler [:command :me] [um] (handle-me um))
(defmethod handler [:callback :build] [um] (handle-build-callback um))
(defmethod handler [:callback :done] [um] (handle-done-callback um))
(defmethod handler :default [& _] nil)
