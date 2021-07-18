(ns tba.telegram.handlers.character
  (:require [tba.domain.character :as character]
            [tba.telegram :as telegram]
            [tba.telegram.handlers.util :as hu])
  (:import org.telegram.telegrambots.meta.api.objects.Update))

(defn- ck-upper [points stat expr]
  (when (and (> points 0)
             (< stat character/+create-stat-max+))
    expr))

(defn- ck-lower [points stat expr]
  (when (and (< points character/+base-stat+)
             (> stat character/+create-stat-min+))
    expr))

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
    (map (partial filter
          (comp not nil?))
     [[(ck-upper points str ["Str +" "str_up"]) (ck-lower points str ["Str -" "str_dn"])]
      [(ck-upper points dex ["Dex +" "dex_up"]) (ck-lower points dex ["Dex -" "dex_dn"])]
      [(ck-upper points con ["Con +" "con_up"]) (ck-lower points con ["Con -" "con_dn"])]
      [(ck-upper points wis ["Wis +" "wis_up"]) (ck-lower points wis ["Wis -" "wis_dn"])]
      [(ck-upper points int ["Int +" "int_up"]) (ck-lower points int ["Int -" "int_dn"])]
      [(ck-upper points cha ["Cha +" "cha_up"]) (ck-lower points cha ["Cha -" "cha_dn"])]])])

(defn handle-new-character [^Update u]
  (let [[_ _ char-name] (-> u .getMessage .getText hu/split-ws)
        character (character/default char-name)
        [text kb] (character-build-ui character/+create-points+ character)]
    (telegram/->CreateResponse (-> u .getMessage .getChatId str)
                               text
                               kb)))

(defmulti handler (partial hu/cmd-key 1))
(defmethod handler :new [u] (handle-new-character u))
(defmethod handler :default [& _] nil)
