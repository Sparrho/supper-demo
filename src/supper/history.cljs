(ns supper.history
  (:require [goog.events :as gevents]
            [goog.History :as ghistory]
            [goog.history.EventType :as history-event]
            [goog.history.Html5History :as history5]))

(def global-history (atom))

(defn build-transformer
  "Custom transformer is needed to replace query parameters, rather
  than adding to them.
  See: https://gist.github.com/pleasetrythisathome/d1d9b1d74705b6771c20"
  []
  (let [transformer (goog.history.Html5History.TokenTransformer.)]
    (set! (.. transformer -retrieveToken)
          (fn [path-prefix location]
            (str (.-pathname location) (.-search location))))
    (set! (.. transformer -createUrl)
          (fn [token path-prefix location]
            (str path-prefix token)))
    transformer))

(defn default-callback
  [e]
  (let [token (.-token e)]
    (println token)))

(defn init-history!
  "Based on the shoreleave implementation. Expects a single-arg callback function to handle
  navigation events. "
  ([]
   (init-history! default-callback))
  ([callback-fn]
   (let [html5? (.isSupported goog.history.Html5History)
         history (if html5?
                   (goog.history.Html5History. js/window (build-transformer))
                   (goog.History. true))]
     (.setEnabled history true)
     (when html5?
       (println "Using HTML5 history")

       ;Remove fragment in order to avoid /#/ URLs
       (.setUseFragment history false)

       ;Prefix should come in the string passed to set-token!
       (.setPathPrefix history ""))
     (gevents/unlisten (.-window_ history) (.-POPSTATE gevents/EventType) ; This is a patch-hack to ignore double events
                       (.-onHistoryEvent_ history), false, history)
     (gevents/listen history history-event/NAVIGATE (fn [e] (callback-fn e)))
     (reset! global-history history))))


(defn set-token!
  "Add new URL token to the history - will trigger a navigate event."
  [tok]
  (.setToken @global-history tok))

(defn get-token
  "Get the current URL token"
  []
  (.getToken @global-history))
