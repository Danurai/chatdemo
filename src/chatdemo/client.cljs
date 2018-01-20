(ns chatdemo.client
   (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! put!]]
            [reagent.core :as r])
   (:require-macros [cljs.core.async.macros :refer [go]]))

;; define your app data so that it doesn't get over-written on reload
;; (defonce app-state (atom {:text "Hello world!"}))

(def container (.getElementById js/document "main"))

(defn Page [message ws-ch]
   [:div
      [:p message]
      [:input {:type "text"
              :id "input"
              :on-key-press (fn [e]
                              (when (= 13 (.-charCode e))
                                 (put! ws-ch (.-value (.getElementById js/document "input")))))}]])

(defn render-page [message ws-ch]
   (r/render-component (Page message ws-ch) container))

(defonce run-once ;; do not reload on figwheel
   (go
      (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:9009/ws"))]
         (when error (throw error));;  (r/render-component ([:p (str "Error connecting to server: " error)]) container))
         (loop []
            (when-let [{:keys [message]} (<! ws-channel)]
               (js/console.log message)
               (render-page message ws-channel)
               (recur))))))

               
;;(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
;;)
