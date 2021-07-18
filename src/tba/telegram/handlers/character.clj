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
   (filter
    not-empty
    (map
     (partial filter
              (comp not nil?))
     [[(ck-upper points str ["Str +" "char_create_str_up"])
       (ck-lower points str ["Str -" "char_create_str_dn"])]
      [(ck-upper points dex ["Dex +" "char_create_dex_up"])
       (ck-lower points dex ["Dex -" "char_create_dex_dn"])]
      [(ck-upper points con ["Con +" "char_create_con_up"])
       (ck-lower points con ["Con -" "char_create_con_dn"])]
      [(ck-upper points wis ["Wis +" "char_create_wis_up"])
       (ck-lower points wis ["Wis -" "char_create_wis_dn"])]
      [(ck-upper points int ["Int +" "char_create_int_up"])
       (ck-lower points int ["Int -" "char_create_int_dn"])]
      [(ck-upper points cha ["Cha +" "char_create_cha_up"])
       (ck-lower points cha ["Cha -" "char_create_cha_dn"])]
      [(when (= points 0) ["Done" "char_create_done"])]
      ]))])

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
