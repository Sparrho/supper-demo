(ns supper.routes
  (:require [goog.Uri :as uri]
            [clojure.string :as string]
            [supper.util :as util]
            [supper.settings :as settings]))

(defn parse
  [uri-str]
  (let [uri-obj (goog.Uri. uri-str)]
    {:path (.getPath uri-obj)

     :params
     (into {}
           (for [param settings/uri-params
                 :let [param-key (:name param)
                       param-str (name param-key)
                       multiple? (:array? param)]]
             [param-key (if multiple?
                          (set (.getParameterValues uri-obj param-str))
                          (.getParameterValue uri-obj param-str))]
             ))}))

(defn set-atom-uri-params!
  [uri-str state-atom]
  (let [parsed-uri (parse uri-str)]
    (swap! state-atom assoc-in [:page :uri-params] (:params parsed-uri))
    (swap! state-atom assoc-in [:page :path] (:path parsed-uri))))
