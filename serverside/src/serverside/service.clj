(ns serverside.service
  (:require [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service.http.route :as route]            
            [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s" (clojure-version))))

(defn home-page
  [request]
  (ring-resp/response "Hello Cruel World!"))

(defn edn-page
  [{edn :edn-params :as req}]
  (let [x (:x edn)]
   (bootstrap/edn-response {:x edn :y 5})))


;; session-interceptor
(defroutes routes
  [[["/" ^:interceptors [(body-params/body-params)  bootstrap/html-body ]
     {:get home-page}     
     ;; Set default interceptors for /about and any other paths under /     
     ["/about" {:get about-page}]
     ["/edn" {:post edn-page}]
     ]]])

;; You can use this fn or a per-request fn via io.pedestal.service.http.route/url-for
(def url-for (route/url-for-routes routes))

;; Consumed by serverside.server/create-server
(def service {:env :prod              
              ::bootstrap/routes routes              
              ::bootstrap/allowed-origins ["http://localhost:8000"]              
              ::bootstrap/resource-path "/public"              
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
