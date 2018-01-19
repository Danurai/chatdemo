(ns solo.system
  (:require [org.httpkit.server :refer [run-server]]
			  [com.stuartsierra.component :as component]
           [solo.web :refer [app]]))

(defn- start-server [handler port]
  (let [server (run-server handler {:port port})]
    (println (str "Server started on http://localhost:" port))
    server)) ;; return itself

(defn- stop-server [server]
  (when server
    (server))) ;; run-server returns a fn that stops itself
    
(defrecord Solo []
  component/Lifecycle
  (start [this]
    (assoc this :server (start-server #'app 9009)))
  (stop [this]
    (stop-server (:server this))
    (dissoc this :server)))

(defn create-system []
  (Solo.))

(defn -main [& args]   
  (.start (create-system)))