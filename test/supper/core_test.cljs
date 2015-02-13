(ns supper.core-test
  (:require-macros [cemerick.cljs.test :refer (is deftest are use-fixtures)]
                   [dommy.core :refer (sel sel1)])
  (:require [cemerick.cljs.test :as t]
            [dommy.core :as dommy]
            [om.core :as om :include-macros true]
            [supper.state :as state]
            [supper.test-data :as t-data]))

;Sanity check, as defined by cemerick.cljs.test
(deftest sanity
  (are [x y] (and (number? x) (number? y) (< x y))
       1 2
       (inc 1) (inc 2)))

;Helper functions below
(defn container-div []
  (let [id (str "container-" (gensym))]
    [(-> "div"
        (dommy/create-element)
        (dommy/set-attr! :id id))
     (str "#" id)]))

(defn insert-container! [container]
  (dommy/append! (sel1 js/document :body) container))

(defn new-container! []
  (let [[n s] (container-div)]
    (insert-container! n)
    (sel1 s)))

(defn reset-state-fixture
  "Reset the state atom before running each test"
  [f]
  (reset! state/app-state t-data/full-state)
  (dommy/clear! (sel1 js/document :body))
  (f))
