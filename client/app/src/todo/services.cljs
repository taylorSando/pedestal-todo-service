(ns todo.services
  (:require
   [io.pedestal.app.net.xhr :as xhr]
   [io.pedestal.app.messages :as msg]))

(defn services-fn [message input-queue]
  (when-let [m message]
    (let [body (pr-str (:service-request m))          
          error-handler (fn [args] (.log js/console (:xhr args)))
          return-fn (:return-fn m)
          success-handler (fn [args]
                            (.log js/console "back from server")
                            (let [contents (-> :body args)]                              
                              (return-fn contents input-queue)))]
      (xhr/request (gensym)
                   "http://localhost:8080/edn"
                   :request-method "POST"
                   :headers {}
                   :body body
                   :on-success success-handler
                   :on-error error-handler)
      (.log js/console (str "Send to Server: " (pr-str body))))))


;; The output message's topic can be :server
;; Can have an attribute of :output-request
;; Should prepare an :return-message
;; Should set an input-type and input-topic
;; Should specify params that need to be filled
;; The on-success function should take the args, run (-> args :body) on it
;; Extracts the return map
;; A message should have been prepared before hand, which will be populated
;; with the values that are coming out of the return of args
;; This is then placed on the input queue
