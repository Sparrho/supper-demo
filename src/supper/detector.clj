(ns supper.detector
  (:require [cljs.env]))

(def is-node? (= :nodejs (-> @cljs.env/*compiler* :options :target)))

(defmacro cs
  "Produces client or server specific code at compile time."
  [client server]
  (if is-node?
    server
    client))
