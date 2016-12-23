(defproject re-learn "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/oliyh/re-learn"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[re-frame "0.8.0"]
                 [prismatic/dommy "1.1.0"]
                 [prismatic/schema "1.1.3"]]
  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-doo "0.1.6"]
            [lein-figwheel "0.5.4-7"]]
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.8.0"]
                                       [org.clojure/clojurescript "1.9.229"]]}
             :dev {:source-paths ["dev"]
                   :resource-paths ["dev-resources"]
                   :exclusions [[org.clojure/tools.reader]]
                   :dependencies [[org.clojure/tools.reader "0.10.0"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.2"]
                                  [devcards "0.2.2"]

                                  ;; todomvc
                                  [secretary "1.2.2"]
                                  [alandipert/storage-atom "1.2.4"]]
                   :repl-options {:init-ns user
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  :aliases {"test" ["do" ["clean"] ["test"] ["doo" "nashorn" "test" "once"]]}
  :figwheel {:css-dirs ["dev-resources/public/css"]}
  :cljsbuild {:builds [{:id "checkout"
                        :source-paths ["src/" "example/checkout"]
                        :figwheel {:on-jsload "checkout.app/on-figwheel-reload"}
                        :compiler {:main "checkout.app"
                                   :asset-path "js/out"
                                   :output-to "dev-resources/public/checkout/js/app.js"
                                   :output-dir "dev-resources/public/checkout/js/out"}}

                       {:id "todomvc"
                        :source-paths ["src/" "example/todomvc"]
                        :figwheel {:on-jsload "todomvc.core/on-figwheel-reload"}
                        :compiler {:main "todomvc.core"
                                   :asset-path "js/out"
                                   :output-to "dev-resources/public/todomvc/js/app.js"
                                   :output-dir "dev-resources/public/todomvc/js/out"}}

                       {:id "devcards"
                        :figwheel {:devcards true}
                        :source-paths ["src" "test"]
                        :compiler {:main "re-learn.runner"
                                   :asset-path "js/devcards"
                                   :output-to "dev-resources/public/devcards/js/devcards.js"
                                   :output-dir "dev-resources/public/devcards/js/devcards"
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "target/unit-test.js"
                                   :main 're-learn.runner
                                   :optimizations :whitespace}}]})
