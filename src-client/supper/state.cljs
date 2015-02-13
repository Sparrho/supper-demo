(ns supper.state
  (:require [cljs.reader]
            [supper.settings :as settings]))

(def is-server? false)

(def api-host nil)

(enable-console-print!)

(def initial-state
  (try
    (cljs.reader/read-string
     (.-textContent
      (. js/document (getElementById "supper-state"))))

    (catch js/Object e (println "No initial state provided: " e)
      settings/blank-state)))

(def app-state (atom initial-state))
