(ns supper.state
  (:require [supper.test-data :as t-data]))

(def app-state (atom t-data/full-state))

(def api-host nil)
