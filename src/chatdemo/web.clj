(ns chatdemo.web
   (:require [chord.http-kit :refer [with-channel]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found resources]]
            [clojure.core.async :refer [>! <! chan go go-loop mult tap]]))

(def chat-ch (chan))
(def chat-mult (mult chat-ch))
(def usernames (atom {}))
(def msgid (atom 0))

(defn now [] (new java.util.Date))
(defn next-msg-id []
   (swap! msgid inc))
 
(defn ws-handler [req]
;; client connected
   (let [uuid (java.util.UUID/randomUUID)]
      (with-channel req ws-ch
         (prn (str uuid " connected"))
         (go
            (loop []
               (if-let [{:keys [message]} (<! ws-ch)]
                  (do
                     (prn message)
                     (case (:type message)
                        :register (do                          ;; Registration - connect to channel, assoc username and broadcast connection
                                    (swap! usernames assoc (keyword (str uuid)) (:value message))
                                    ;;(prn (deref usernames))
                                    (tap chat-mult ws-ch)
                                    (>! chat-ch {:msgid (next-msg-id) :timestamp (now) :chatname "system" :msg (str (:value message) " joined the chat.")}))
                        :message (>! chat-ch {:msgid (next-msg-id) :timestamp (now) :chatname ((keyword (str uuid)) (deref usernames)) :msg (:value message)})
                        :default)
                     (recur))
                     (let [username ((keyword (str uuid)) (deref usernames))]
                        (prn (str uuid " disconnected"))
                        (if username (>! chat-ch {:msgid (next-msg-id) :timestamp (now) :chatname "system" :msg (str username " left the chat.")})))))))))
 
;;(defn ws-handler [req]
;;   (let [uuid (java.util.UUID/randomUUID)]
;;      (with-channel req ws-ch
;;         (prn (str uuid " connected"))
;;         (tap chat-mult ws-ch)
;;         (go
;;            (>! chat-ch (str uuid " connected"))
;;            (loop []
;;               (if-let [{:keys [message]} (<! ws-ch)]
;;                  (let [log (str uuid ": " message)]
;;                     (prn log)
;;                     (>! chat-ch message)
;;                     (recur))
;;                  (let [log (str uuid " disconnented")]
;;                     (prn log)
;;                     (>! chat-ch log))))))))
 
(defn page-not-found [req]
   {:status 404
    :headers {"Content-Type" "text/html"}
    :body "<p>404: That page doesn't exist, <a href='/'>go home</a>"})

(defroutes app
   (GET "/ws" [] ws-handler)
   (GET "/" [] (slurp (io/resource "public/index.html")))
   (resources "/")
   (not-found page-not-found))
   