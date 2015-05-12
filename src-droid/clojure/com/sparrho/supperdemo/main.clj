(ns com.sparrho.supperdemo.main
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.find-view :refer [find-view]]
            [neko.threading :refer [on-ui]])
  (:import [android.webkit WebViewClient]
           [com.sparrho.supperdemo WebAppInterface]))

;Based on https://github.com/sattvik/Clojure-Android-Examples/
(defn make-webview-client
  "Constructs an instance of WebViewClient that allows us to manage the
  WebView."
  [web-view]
  (proxy [WebViewClient] []
    (shouldOverrideUrlLoading [view url]
                              (.loadUrl view url)
                              true)))

(defactivity com.sparrho.supperdemo.InitActivity
  :key :main
  :on-create
  (fn [this bundle]
    (neko.debug/keep-screen-on this)
    (on-ui
     (set-content-view! this
                        [:relative-layout {:layout-width :fill-parent
                                           :layout-height :fill-parent}
                         [:web-view {:id ::webview
                                     :layout-width :fill-parent
                                     :layout-height :fill-parent}]])
     (let [web-view (find-view this ::webview)]
       (doto (.getSettings web-view)
         (.setJavaScriptEnabled true))
       (doto web-view
         (.addJavascriptInterface (WebAppInterface. this) "Android")
         (.loadUrl "file:///android_asset/index.html")
         (.setWebViewClient (make-webview-client web-view)))
       ))))
