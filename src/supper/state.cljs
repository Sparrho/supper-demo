(ns supper.state
  (:require-macros [supper.detector :as d])
  (:require [cljs.nodejs :as nodejs]
            [cljs.reader]
            [supper.settings :as settings]))

(def is-server? (d/cs false true))

(def api-host
  (d/cs
   nil
   (or (aget cljs.nodejs/process "env" "API_HOST") settings/default-api-host)))

(enable-console-print!)

(def initial-state
  (d/cs
   (try
     (cljs.reader/read-string
      (.-textContent
       (. js/document (getElementById "supper-state"))))

     (catch js/Object e (println "No initial state provided: " e)
       settings/blank-state))

   settings/blank-state))

(defn new-state-atom
  []
  (atom initial-state))
