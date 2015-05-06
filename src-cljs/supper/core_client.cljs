(ns supper.core-client
  (:require-macros [supper.detector :as d])
  (:require [cljs.reader]
            [clojure.set]
            [om.core :as om :include-macros true]
            [supper.state :as state]
            [supper.settings :as settings]
            [supper.components.container :as container]
            [supper.http-async :as http-async]
            [supper.history :as history]
            [supper.routes :as routes]
            [supper.util :as util]))

(defn history-callback
  [e state-atom]
  (let [token (.-token e)
        parsed-url (routes/parse token)
        root-cursor (om/root-cursor state-atom)
        old-params-map @(util/params-ref-cursor root-cursor)
        new-params-map (:params parsed-url)
        new-search-terms (clojure.set/difference
                          (:search new-params-map) (:search old-params-map))]
    (om/update! (util/params-ref-cursor root-cursor) new-params-map)
    (om/update! (util/page-ref-cursor root-cursor) [:path] (:path parsed-url))
    (doseq [search-term new-search-terms]
      (http-async/append-new-search! search-term root-cursor))))

(defn main
  []
  (let [state-atom (state/new-state-atom)
        default-state? (= @state-atom settings/blank-state)]
    ;Initialise history management
    (history/init-history! #(history-callback % state-atom))
    ;(history/init-history!)

    ;Insert basic URL data into state atom
    (routes/set-atom-uri-params! (.-href (.-location js/window)) state-atom)

    ;Initialise root based on state
    (let [init-om-root #(do
                          (om/root
                           container/main-component
                           state-atom
                           {:target (. js/document (getElementById "supper-root"))}))]
      (cond
       (not default-state?)
       ;Inialise based on atom passed from server
       (init-om-root)

       :default
       ;Inialise from http responses
       (http-async/initialise-search-results state-atom init-om-root)
       ))))

(d/cs
 (main)
 nil)
