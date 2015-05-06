(ns supper.core-server
  (:require [clojure.string :as string]
            [supper.http :as http]
            [supper.routes :as routes]
            [supper.settings :as settings]
            [supper.state :as state]))

(defn init
  []
  ; Node.js dirname
  (def __dirname (js* "__dirname"))

  (def express (cljs.nodejs/require "express"))
  (def logfmt (cljs.nodejs/require "logfmt"))
  (def cookie-parser (cljs.nodejs/require "cookie-parser"))
  (def http-proxy (cljs.nodejs/require "http-proxy"))

  (def app (express))

  (def port (or (aget cljs.nodejs/process "env" "PORT") settings/default-port))

  (def proxy-server (.createProxyServer http-proxy #js {}))

  ; Logger
  (.use app (.requestLogger logfmt))

  ; Cookie parser
  (.use app (cookie-parser))

  ; Set assets folder
  (.use app "/static" (.static express (str __dirname "/../resources")))

  ; Direct Supper routing
  (.get app "/"
        (fn [req resp]
          (let [state-atom (state/new-state-atom)
                url (.-originalUrl req)]
            (routes/set-atom-uri-params! url state-atom)
            (http/respond-with-sample req resp state-atom))))

  ; Set up API proxy
  (.all app "*"
        (fn [req resp]
          (let [original-host (aget (.-headers req) "host")
                new-host (string/replace state/api-host #"http://|https://" "")]
            (aset (.-headers req) "host" new-host)
            (.web proxy-server req resp #js {"target" state/api-host
                                             "hostRewrite" original-host})))))

; Define Node.js main function
(defn -main [& args]
  (init)
  (.listen app port))

(set! *main-cli-fn* -main)
