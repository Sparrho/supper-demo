(ns supper.components.search-results
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn result-row
  [search-result owner]
  (reify
    om/IRender
    (render [_]
            (html
             [:a.list-group-item {:href (:url search-result) :target "_blank"}
              [:h4.list-group-item-heading
               (:label search-result) " - " (:id search-result)]
              [:p.list-group-item-text
               (or (:description search-result) "Description not found")]]))))

(defn search-container
  [search owner]
  (reify
    om/IRender
    (render [_]
            (html
             [:div.panel.panel-default
              [:div.panel-heading
               [:h2 (str "Results for \"" (get-in search [:searchinfo :search]) "\"")]]
              [:div.panel-body
               [:div.list-group
                (om/build-all result-row (:search search))]]]))))
