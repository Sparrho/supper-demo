(ns supper.util
  (:require [goog.net.cookies]
            [om.core :as om :include-macros true]))

(defn get-cookie
  [k]
  (try
    (.get goog.net.cookies k)

    (catch js/Object e (println "Couldn't get cookie: " e)
          nil)))

(defn in?
  "true if seq contains elm"
  [seq elm]
  (some #(= elm %) seq))

(defn value
  [e]
  (.. e -target -value))

(defn left-click?
  "Checks for an unmodified left click"
  [e]
  (and (= (.-button e) 0)
       (not (.-altKey e))
       (not (.-ctrlKey e))
       (not (.-metaKey e))
       (not (.-shiftKey e))))

(defn url-encode
  "Taken from cemerick/url library"
  [string]
  (some-> string str (js/encodeURIComponent) (.replace "+" "%20")))

(defn generate-ref-cursor
  "Takes a cursor (any cursor), and uses the om/state function to extract its root atom, basing
  the ref cursor on that. This allows multiple state atoms to safely exist in parallel, which
  is necessary for handling multiple server-side requests simultaneously."
  [cursor path-vec]
  (try
    (om/ref-cursor (get-in (om/root-cursor (om/state cursor)) path-vec))

    (catch js/Object e (println "Couldn't generate reference cursor: " e)
          (om/ref-cursor (om/root-cursor (atom {}))))))

(defn global-ref-cursor
  [cursor]
  (generate-ref-cursor cursor []))

(defn page-ref-cursor
  [cursor]
  (generate-ref-cursor cursor [:page]))

(defn params-ref-cursor
  [cursor]
  (generate-ref-cursor cursor [:page :uri-params]))

(defn content-ref-cursor
  [cursor]
  (generate-ref-cursor cursor [:sparrho-content]))
