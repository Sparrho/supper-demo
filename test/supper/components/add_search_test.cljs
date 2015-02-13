(ns supper.components.add-search-test
  (:require-macros [cemerick.cljs.test :refer (is deftest are use-fixtures testing)]
                   [dommy.core :refer (sel sel1)])
  (:require [cemerick.cljs.test :as t]
            [dommy.core :as dommy]
            [om.core :as om :include-macros true]
            [supper.core-test :as core]
            [supper.test-data :as t-data]
            [supper.components.add-search :as as]))
