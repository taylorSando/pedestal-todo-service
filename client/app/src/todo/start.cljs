(ns todo.start
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.render.push :as push-render]
            [io.pedestal.app.render :as render]
            [io.pedestal.app.messages :as msg]            
            [todo.services :as services]
            [todo.behavior :as behavior]
            [todo.rendering :as rendering]))

(defn create-app [render-config]
  (let [;; Build the application described in the map
        ;; 'behavior/example-app'. The application is a record which
        ;; implements the Receiver protocol.
        app (app/build behavior/example-app)
        ;; Create the render function that will be used by this
        ;; application. A renderer function takes two arguments: the
        ;; application model deltas and the input queue.
        ;;
        ;; On the line below, we create a renderer that will help in
        ;; mapping UI data to the DOM. 
        ;;
        ;; The file, app/src/todo/rendering.cljs contains
        ;; the code which does all of the rendering as well as the
        ;; render-config which is used to map renderering data to
        ;; specific functions.
        render-fn (push-render/renderer "content" render-config render/log-fn)
        ;; This application does not yet have services, but if it did,
        ;; this would be a good place to create it.
        ;; services-fn (fn [message input-queue] ...)

        ;; Configure the application to send all rendering data to this
        ;; renderer.
        app-model (render/consume-app-model app render-fn)]
    ;; If services existed, configure the application to send all
    ;; effects there.
    ;; (app/consume-effect app services-fn)
    ;;
    ;; Start the application
    (app/begin app)    
    ;; Send a message to the application so that it does something.
    (p/put-message (:input app) {msg/type :create-todo msg/topic [:todo]})        
    ;(services-fn {:out-message {:x 5}})
    (app/consume-output app services/services-fn)
    (p/put-message (:output app) {msg/type msg/output msg/topic [:server]
                                  :service-request {:x :something}
                                  :return-fn (fn [response input-queue]
                                               (.log js/console "I am in the return fn")
                                               (.log js/console (pr-str response)))})
    {:app app :app-model app-model}))

(defn ^:export main []
  ;; config/config.clj refers to this namespace as a main namespace
  ;; for several aspects. A main namespace must have a no argument
  ;; main function. To tie into tooling, this function should return
  ;; the newly created app.
  (create-app (rendering/render-config)))


