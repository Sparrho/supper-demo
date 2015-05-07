(ns supper.settings)

(def static-url "/static/")

(def api-base "/w/api.php")

(def default-api-host "http://www.wikidata.org")

(def default-port 3000)

(def uri-params
  ;List of URI parameters to look for, noting whether to expect a single param or an array.
  [{:name :search :array? true}])

(def blank-state
  {:page {:heading "Supper Demo - Wikidata Search"

          :uri-params {:search #{}}}

   :wiki-content []})
