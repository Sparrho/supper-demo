(defproject supper "0.0.1-SNAPSHOT"
  :description "A pure-ClojureScript isomorphic rendering project, using Om/React, Sablono and Node.js"
  :url "https://github.com/Sparrho/supper"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3058"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "0.8.8"]
                 [sablono "0.3.3"]
                 [hiccups "0.3.0"]
                 [cljs-http "0.1.25"]

                 [prismatic/dommy "1.0.0"] ;NB: Included for testing ONLY. Don't use this inside actual app code!
                 ]

  :plugins [[lein-cljsbuild "1.0.4"]
            [com.cemerick/clojurescript.test "0.3.3"]]

  :clean-targets ^{:protect false} ;Unprotected to allow deletion of file outside project target dir
  ["out-client" "out-server" "out-test" "deploy/index.js" "resources/js/supper.compiled.js"
   "supper.compiled.test.js"]

  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler
             {:output-to "supper.compiled.test.js"
              :output-dir "out-test"
              :optimizations :advanced}}

            {:id "server"
             :source-paths ["src" "src-server"]
             :compiler
             {:target :nodejs
              :output-to "deploy/index.js"
              :output-dir "out-server"
              :preamble ["include.js"]
              :optimizations :simple
              :language-in :ecmascript5
              :language-out :ecmascript5}}

            {:id "client"
             :source-paths ["src" "src-client"]
             :compiler
             {:output-to "resources/js/supper.compiled.js"
              :output-dir "out-client"
              :optimizations :advanced
              :pretty-print false}}]

   :test-commands {"unit-tests" ["phantomjs" :runner
                                 "test/es5-shim.js" ;Polyfills to account for dated webkit in PhantomJS
                                 "test/es5-sham.js"
                                 "test/console-polyfill.js"
                                 "supper.compiled.test.js"]}})
