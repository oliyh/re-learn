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
                   :resource-paths ["example-resources"]
                   :exclusions [[org.clojure/tools.reader]]
                   :dependencies [[org.clojure/tools.reader "0.10.0"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.2"]
                                  [devcards "0.2.2"]]
                   :repl-options {:init-ns user
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  :aliases {"test" ["do" ["clean"] ["test"] ["doo" "nashorn" "test" "once"]]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/" "example/"]
                        :figwheel {:on-jsload "checkout.app/on-figwheel-reload"}
                        :compiler {:main "checkout.app"
                                   :asset-path "js/out"
                                   :output-to "example-resources/public/js/checkout.js"
                                   :output-dir "example-resources/public/js/out"}}
                       {:id "devcards"
                        :figwheel {:devcards true}
                        :source-paths ["src" "example"]
                        :compiler {:main "devcards.runner"
                                   :asset-path "js/devcards"
                                   :output-to "resources/public/js/devcards.js"
                                   :output-dir "resources/public/js/devcards"
                                   :source-map-timestamp true}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "target/unit-test.js"
                                   :main 're-learn.runner
                                   :optimizations :whitespace}}]})
