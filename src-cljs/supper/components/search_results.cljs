(ns supper.components.search-results
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn build-item-url
  [item]
  (str "http://www.sparrho.com/article/" (get-in item [:type :machine]) "/" (:id item) "/"))

(defn result-row
  [search-result owner]
  (reify
    om/IRender
    (render [_]
            (html
             [:a.list-group-item {:href (build-item-url search-result) :target "_blank"}
              [:h4.list-group-item-heading
               (:title search-result)]
              [:p.list-group-item-text
               (or (:source search-result) "Source not found")]]))))

(defn search-container
  [search owner]
  (reify
    om/IRender
    (render [_]
            (html
             [:div.panel.panel-default
              [:div.panel-heading
               [:h2 (str "Results for \"" (:kw search) "\"")]]
              [:div.panel-body
               [:div.list-group
                (om/build-all result-row (:content search))]]]))))
