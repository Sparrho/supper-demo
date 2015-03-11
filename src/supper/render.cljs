(ns supper.render
  (:require-macros [hiccups.core :as hiccups]
                   [supper.detector :as d])
  (:require [hiccups.runtime :as hiccupsrt]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [supper.state :as state]
            [supper.components.container :as container]
            [supper.settings :as settings]))

(def static-url (d/cs
                 settings/static-url
                 (or (aget cljs.nodejs/process "env" "STATIC_URL") settings/static-url)))

(defn render-to-string
  "Takes a state atom and returns the HTML for that state."
  [state-atom component]
  (->> state-atom
       (om/root-cursor)
       (om/build component)
       dom/render-to-str))

(hiccups/defhtml sample-template
  [state-atom]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]

    [:title "Supper Demo"]

    ;Bootstrap core CSS
    [:link {:href "//netdna.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" :rel "stylesheet" :type "text/css"}]]

   [:body
    [:div#supper-root
     ;React HTML is inserted here - comment out to run client-side only
     (render-to-string state-atom container/main-component)]

    [:script#supper-state {:type "application/edn"} (str @state-atom)]
    [:script {:type "text/javascript" :src (str static-url "js/supper.compiled.js")}]]])

(defn render-sample
  [state-atom]
  (str "<!DOCTYPE html>" (sample-template state-atom)))
