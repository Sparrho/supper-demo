(ns supper.http
  (:require [supper.render :as render]
            [supper.http-async :as http-async]))

(defn respond-with-sample
  [req client-res state-atom]
  (let [request-cookies (js->clj (.-cookies req) :keywordize-keys true)
        session-id (:sessionid request-cookies)]
    (http-async/initialise-search-results state-atom
                                          #(.send client-res (render/render-sample state-atom)))))
