(ns todo.simulated.services
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.net.xhr :as xhr]
            [io.pedestal.app.util.platform :as platform]))

(defn receive-messages [app]
  (p/put-message (:input app) {msg/type :create-todo msg/topic [:todo]}))

(defrecord MockServices [app]
  p/Activity
  (start [this]
    (receive-messages app)    
    (services-fn {:out-message {:x 5}})
    )
  (stop [this]))

(defn services-fn [message _]
  (when-let [msg (:out-message message)]
    (let [body (pr-str msg)
          log (fn [args]
                (.log js/console (pr-str args))
                (.log js/console (:xhr args)))]
      (xhr/request (gensym)
                   "/edn"
                   :request-method "POST"
                   :headers {"Content-Type" "application/edn"}
                   :body body
                   :on-success log
                   :on-error log))
    (.log js/console (str "Send to Server: " (pr-str body)))))


