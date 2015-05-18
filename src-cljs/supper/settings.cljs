(ns supper.settings)

(def static-url "/static/")

(def api-base "/api/v2/")

(def default-api-host "http://api.sparrho.com")

(def default-port 3000)

(def uri-params
  ;List of URI parameters to look for, noting whether to expect a single param or an array.
  [{:name :search :array? true}])

(def blank-state
  {:page {:heading "Supper Demo - Sparrho Search"

          :uri-params {:search #{}}}

   :sparrho-content []})
