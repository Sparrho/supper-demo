(ns com.sparrho.supperdemo.main
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.find-view :refer [find-view]]
            [neko.threading :refer [on-ui]]
            [com.sparrho.supperdemo.jsinterface]))

;TODO: Type hinting!
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
         (.addJavascriptInterface (com.sparrho.supperdemo.jsinterface.interop. this) "Android")
         (.loadUrl "file:///android_asset/index.html"))
       ))))
