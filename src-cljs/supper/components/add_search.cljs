(ns supper.components.add-search
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [supper.util :as util]
            [supper.history :as history]))

(defn search-bar
  [uri-params owner]
  (reify
    om/IInitState
    (init-state [_]
                {:search-term nil})

    om/IRenderState
    (render-state [_ {:keys [search-term]}]
                  (html
                   [:div.panel.panel-default
                    [:div.panel-body
                     [:form.form-inline {:on-submit
                                         (fn [e]
                                           (.preventDefault e)
                                           (let [existing-searches (:search uri-params)
                                                 new-search (util/url-encode search-term)
                                                 current-url (str (.-location js/window))]
                                             ;This is a rather clunky function to update the URL history, and
                                             ;allow the state changes to cascade accordingly. It's OK for a
                                             ;demo, but production use should have more robust URL handling.
                                             (if-not (util/in? existing-searches new-search)
                                               (do
                                                 (try
                                                   (. js/Android (showToast "Searching..."))
                                                   (catch js/Object e
                                                     (println "Couldn't use Android interop: " e)))
                                                 (history/set-token!
                                                  (str
                                                   current-url
                                                   (if-not (re-find #"\?" current-url) "?")
                                                   "&search=" new-search))
                                                 (om/set-state! owner :search-term nil)))))}

                      [:div.form-group
                       [:input.form-control {:type "text" :value search-term :placeholder "Add search"
                                             :on-change #(om/set-state! owner :search-term (util/value %))}]
                       " "
                       [:input.btn.btn-primary {:type "submit"}]]]]]))))
