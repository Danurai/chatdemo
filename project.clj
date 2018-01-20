(defproject chatdemo  "0.1.0-SNAPSHOT"
  :description       "Simple app written in Clojure and ClojureScript"
  :url              "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

;; Update to your namespace            
  :main chatdemo.system
  
  :dependencies [[org.clojure/clojure "1.8.0"]
               [org.clojure/clojurescript "1.9.946"]
               [http-kit "2.2.0"]
               [com.stuartsierra/component "0.3.2"]
               [compojure "1.6.0"]
					[reagent "0.7.0"]
					[jarohen/chord "0.8.1"]
					[org.clojure/core.async "0.3.465"]]
               
 	:profiles {:dev 
               {:plugins [[lein-cljsbuild "1.1.7"]
                         [lein-figwheel "0.5.14"]]
                :dependencies [[reloaded.repl "0.2.4"]]
                :source-paths ["dev"]
                :cljsbuild 
                   {:builds [{:id dev
                             :source-paths ["src" "dev"]
                             :figwheel true
                             :compiler {:output-to "target/classes/public/app.js"
                                       :output-dir "target/classes/public/out"
                                       :main "chatdemo.client"
                                       :asset-path "/out"
                                       :optimizations :none
                                       :recompile-dependents true
                                       :source-map true}}]}}})