(ns tba.telegram.update-map
  "Functions for the 'update map', the clojure-ified version of the
  org.telegram.telegrambots.meta.api.objects.Update. We do this to prevent
  propogation of it as a dependency and outside of the namespaces that handle
  the telegram API directly and also to have a data structure that is
  more ergonomic to work with in clojure code. The symbol `um` will be used
  canonically in other contexts to refer to an update map")

(defn param-root
  [{[root & _] :params}]
  root)

(defn curr-step
  [{update-type :update-type params :params}]
  (if (empty? params)
    [update-type :done]
    [update-type (first params)]))

(defn advance
  [um]
  (update um :params rest))
