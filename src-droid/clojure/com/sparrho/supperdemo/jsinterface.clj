(ns com.sparrho.supperdemo.jsinterface
  (:require [neko.notify :refer [toast]])
  (:import [android.webkit JavascriptInterface]))

(definterface interop-interface
  (showToast [^String msg]))

(deftype interop [context]
  interop-interface
  (^{JavascriptInterface true}
   showToast
   [this ^String msg]
   (toast context msg :long)))

