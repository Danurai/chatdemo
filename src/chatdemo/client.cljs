(ns chatdemo.client
   (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! put!]]
            [reagent.core :as r])
   (:require-macros [cljs.core.async.macros :refer [go]]))

;; define your app data so that it doesn't get over-written on reload
;; (defonce app-state (atom {:text "Hello world!"}))

(enable-console-print!)
(goog-define ws-uri "ws://localhost:9009/ws")
(def container (.getElementById js/document "main"))
(def app-data (r/atom {:chatlist ()}))
(def !input (r/atom ""))


(defn- render-register [ws-ch] 
   [:div#register {:class (if (:username @app-data) "div-hidden")}
      [:p "Enter your chat name"]
      [:input#chatname {:type "text"
                          :auto-focus (not (false? (:username @app-data)))
                          :on-key-press (fn [e]
                                            (when (= 13 (.-charCode e))
                                               (let [chatname (.-value (.getElementById js/document "chatname"))]
                                                  (swap! app-data assoc :username chatname)
                                                  ;;(prn app-data)
                                                  (put! ws-ch {:type :register :value chatname}))))}]])
(defn- render-chat [ws-ch]
         [:div#chat {:class (if-not (:username @app-data) "div-hidden")}
            [:div#messages (for [msg (:chatlist @app-data)]
               ^{:key (str (:msgid msg) (:timestamp msg))}[:p {:class (str "message "
                                       (case (:chatname msg)
                                          (str (:username @app-data)) "message-mymessage"
                                          "system" "message-system"
                                          "message-othermessage"))}
                           (str (if-not (= (:chatname msg) "system") (str (:chatname msg) ": ")) (:msg msg))])]
            [:div 
               [:p "Enter your message below and hit <Enter> to send."]
               [:input.message-input {:type "text"
                                   :value @!input
                                   :on-change #(reset! !input (-> % .-target .-value)) 
                                   :on-key-press (fn [e]
                                                   (when (and (not= (deref !input) "")(= 13 (.-charCode e)))
                                                      (do 
                                                         (put! ws-ch {:type :message :value (deref !input)})
                                                         (reset! !input ""))))}]]])
(defn Page [ws-ch]
   [:div 
      (render-register ws-ch)
      (render-chat ws-ch)])

(defonce run-once ;; do not reload on figwheel
   (go
      (let [{:keys [ws-channel error]} (<! (ws-ch ws-uri))]
         (if-not error
            (do 
               (loop []
                  (r/render [Page ws-channel] (.getElementById js/document "main"))
                  (when-let [{:keys [message]} (<! ws-channel)]
                     (swap! app-data update-in [:chatlist] conj message)
                     ;;(prn app-data)
                     (recur))))
          (r/render [:div [:p (str "Error connecting to the server " error)]] container))))
)

               
;;(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
;;)
