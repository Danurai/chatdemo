(ns chatdemo.web
   (:require [chord.http-kit :refer [with-channel]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found resources]]
            [clojure.core.async :refer [>! <! chan go go-loop mult tap]]))

(def chat-ch (chan))
(def chat-mult (mult chat-ch))
 
(defn ws-handler [req]
   (let [uuid (java.util.UUID/randomUUID)]
      (with-channel req ws-ch
         (prn (str uuid " connected"))
         (tap chat-mult ws-ch)
         (go
            (>! chat-ch (str uuid " connected"))
            (loop []
               (if-let [{:keys [message]} (<! ws-ch)]
                  (let [log (str uuid ": " message)]
                     (prn log)
                     (>! chat-ch log)
                     (recur))
                  (let [log (str uuid " disconnented")]
                     (prn log)
                     (>! chat-ch log))))))))
 
(defn page-not-found [req]
   {:status 404
    :headers {"Content-Type" "text/html"}
    :body "<p>404: That page doesn't exist, <a href='/'>go home</a>"})

(defroutes app
   (GET "/ws" [] ws-handler)
   (GET "/" [] (slurp (io/resource "public/index.html")))
   (resources "/")
   (not-found page-not-found))
   