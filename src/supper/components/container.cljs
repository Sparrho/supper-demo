(ns supper.components.container
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [supper.components.add-search :as add-search]
            [supper.components.search-results :as search-results]))

(defn main-component
  [app owner]
  (reify
    om/IRender
    (render [_]
            (html
             [:div.container
              [:h1 (get-in app [:page :heading])]

              (om/build add-search/search-bar (get-in app [:page :uri-params]))

              (om/build-all search-results/search-container (:wiki-content app))]))))
