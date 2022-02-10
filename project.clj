(defproject re-learn "0.1.5-SNAPSHOT"
  :description "Data-driven tutorials for reagent UIs"
  :url "https://github.com/oliyh/re-learn"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy" "clojars"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :dependencies [[re-frame "0.10.9"]
                 [reagent "1.1.0"]
                 [prismatic/dommy "1.1.0"]
                 [prismatic/schema "1.1.12"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.10"]
            [lein-figwheel "0.5.4-7"]]
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.10.1"]
                                       [org.clojure/clojurescript "1.10.597"]]}
             :dev {:source-paths ["dev"]
                   :resource-paths ["dev-resources"]
                   :exclusions [[org.clojure/tools.reader]]
                   :dependencies [[org.clojure/tools.reader "1.3.2"]
                                  [cider/piggieback "0.4.2"]
                                  [figwheel-sidecar "0.5.19"]
                                  [devcards "0.2.6"]
                                  [binaryage/devtools "0.9.11"]
                                  [day8.re-frame/test "0.1.5"]
                                  [lein-doo "0.1.10"]

                                  ;; todomvc
                                  [secretary "1.2.3"]
                                  [alandipert/storage-atom "1.2.4"]

                                  ;; gh-pages deploy
                                  [leiningen-core "2.9.1"]

                                  [doo-chrome-devprotocol "0.1.1"]]
                   :repl-options {:init-ns user
                                  :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}}
  :aliases {"test" ["do" ["clean"] ["cljsbuild" "once" "test"] ["test"]]
            "build-pages" ["do"
                           ["run" "-m" "pages/build"]
                           ["cljsbuild" "once" "pages"]]
            "deploy-pages" ["run" "-m" "pages/push"]}
  :figwheel {:css-dirs ["dev-resources/public/css"]}
  :cljsbuild {:builds [{:id "checkout"
                        :source-paths ["src/" "example/checkout"]
                        :figwheel {:on-jsload "checkout.app/on-figwheel-reload"}
                        :compiler {:main "checkout.app"
                                   :asset-path "js/out"
                                   :output-to "dev-resources/public/checkout/js/app.js"
                                   :output-dir "dev-resources/public/checkout/js/out"
                                   :preloads [devtools.preload]
                                   :parallel-build true}}

                       {:id "todomvc"
                        :source-paths ["src/" "example/todomvc"]
                        :figwheel {:on-jsload "todomvc.core/on-figwheel-reload"}
                        :compiler {:main "todomvc.core"
                                   :asset-path "js/out"
                                   :output-to "dev-resources/public/todomvc/js/app.js"
                                   :output-dir "dev-resources/public/todomvc/js/out"
                                   :preloads [devtools.preload]
                                   :parallel-build true}}

                       {:id "pages"
                        :source-paths ["src/" "example/todomvc"]
                        :compiler {:main "todomvc.core"
                                   :output-to "dist/js/app.js"
                                   :output-dir "dist"
                                   :parallel-build true
                                   :optimizations :advanced}}

                       {:id "devcards"
                        :figwheel {:devcards true}
                        :source-paths ["src" "test"]
                        :compiler {:main "re-learn.all-tests"
                                   :asset-path "js/devcards"
                                   :output-to "dev-resources/public/devcards/js/devcards.js"
                                   :output-dir "dev-resources/public/devcards/js/devcards"
                                   :source-map-timestamp true
                                   :parallel-build true}}

                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "out/unit-test.js"
                                   :output-dir "out"
                                   :main "re-learn.runner"
                                   :optimizations :whitespace
                                   :parallel-build true}}]})
