(defproject supper "0.0.1-SNAPSHOT"
  :description "A pure-ClojureScript isomorphic rendering project, using Om/React, Sablono and Node.js"
  :url "https://github.com/Sparrho/supper"

  :clean-targets ^{:protect false} ;Unprotected to allow deletion of file outside project target dir
  ["out-client" "out-server" "out-test" "deploy/index.js" "resources/js/supper.compiled.js"
   "supper.compiled.test.js" "target"]

  :profiles {:default [:cljs-shared]

             :cljs-shared
             {:dependencies [[org.clojure/clojure "1.6.0"]
                             [org.clojure/clojurescript "0.0-3058"]
                             [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                             [org.omcljs/om "0.8.8"]
                             [sablono "0.3.3"]
                             [hiccups "0.3.0"]
                             [cljs-http "0.1.25"]

                             [prismatic/dommy "1.0.0"] ;NB: Included for testing ONLY. Don't use this inside actual app code!
                             ]

              :plugins [[lein-cljsbuild "1.0.5"]
                        [com.cemerick/clojurescript.test "0.3.3"]]

              :cljsbuild
              {:builds [{:id "test"
                         :source-paths ["src-cljs" "test"]
                         :compiler
                         {:output-to "supper.compiled.test.js"
                          :output-dir "out-test"
                          :optimizations :advanced}}

                        {:id "server"
                         :source-paths ["src-cljs"]
                         :compiler
                         {:main "supper.core-server"
                          :target :nodejs
                          :output-to "deploy/index.js"
                          :output-dir "out-server"
                          :preamble ["include.js"]
                          :optimizations :simple
                          :language-in :ecmascript5
                          :language-out :ecmascript5}}

                        {:id "client"
                         :source-paths ["src-cljs"]
                         :compiler
                         {:main "supper.core-client"
                          :output-to "resources/js/supper.compiled.js"
                          :output-dir "out-client"
                          :externs ["externs.js"]
                          :optimizations :advanced
                          :pretty-print false}}]

               :test-commands {"unit-tests" ["phantomjs" :runner
                                             "test/es5-shim.js" ;Polyfills to account for dated webkit in PhantomJS
                                             "test/es5-sham.js"
                                             "test/console-polyfill.js"
                                             "supper.compiled.test.js"]}}}

             :android-shared
             [:android-common
              {:global-vars {*warn-on-reflection* true}
               :source-paths ["src-droid/clojure"]
               :java-source-paths ["src-droid/java"]
               :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]

               :dependencies [[org.clojure-android/clojure "1.7.0-alpha6" :use-resources true]
                              [neko/neko "3.2.0"]]
               :plugins [[lein-droid "0.4.0-alpha2"]]
               :android {;; Specify the path to the Android SDK directory.
                         ;; :sdk-path "/home/user/path/to/android-sdk/"

                         ;; Try increasing this value if dexer fails with
                         ;; OutOfMemoryException. Set the value according to your
                         ;; available RAM.
                         :dex-opts ["-JXmx4096M"]

                         ;; If previous option didn't work, uncomment this as well.
                         ;; :force-dex-optimize true

                         :assets-paths ["resources"]
                         :target-version "22"
                         :aot-exclude-ns ["clojure.parallel" "clojure.core.reducers"]}}]

             :android-dev
             [:android-shared
              {:dependencies [[org.clojure-android/tools.nrepl "0.2.6-lollipop"]]
               :target-path "target/debug"
               :android {:aot :all-with-unused
                         :rename-manifest-package "com.sparrho.supperdemo.debug"
                         :manifest-options {:app-name "SupperDemo - debug"}}}]

             :android-dev-lean
             [:android-dev
              {:dependencies ^:replace [[org.skummet/clojure "1.7.0-beta3-r1" :use-resources true]
                                        [neko/neko "3.2.0"]]
               :exclusions [[org.clojure/clojure]
                            [org.clojure-android/clojure]]
               :global-vars ^:replace {clojure.core/*warn-on-reflection* true}
               :jvm-opts ["-Dclojure.compile.ignore-lean-classes=true"]
               :android {:aot :all
                         :proguard-execute true
                         :proguard-conf-path "proguard.conf"
                         :lean-compile true
                         :skummet-skip-vars ["#'neko.init/init"
                                             "#'neko.context/context"
                                             "#'neko.resource/package-name"
                                             "#'neko.-utils/keyword->static-field"
                                             "#'neko.-utils/keyword->setter"
                                             "#'neko.ui.traits/get-display-metrics"
                                             "#'com.sparrho.supperdemo.main/InitActivity-onCreate"
                                             "#'com.sparrho.supperdemo.main/InitActivity-init"]}}]

             :android-release
             [:android-shared
              {:target-path "target/release"
               :android
               { ;; Specify the path to your private keystore
                ;; and the the alias of the key you want to
                ;; sign APKs with.
                ;; :keystore-path "/home/user/.android/private.keystore"
                ;; :key-alias "mykeyalias"

                :ignore-log-priority [:debug :verbose]
                :aot :all
                :build-type :release}}]})
