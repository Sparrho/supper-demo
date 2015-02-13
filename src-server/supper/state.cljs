(ns supper.state
  (:require [cljs.nodejs :as nodejs]
            [supper.settings :as settings]))

(def is-server? true)

(def api-host (or (aget nodejs/process "env" "API_HOST") settings/default-api-host))

(defn new-state-atom
  []
  (atom settings/blank-state))
