(ns download-xlsx.core
  (:require [ring.util.http-response :refer [content-type file-response header not-found ok]]
            [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [reitit.coercion.spec :as rc-spec]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.swagger :as swagger]
            [ring.middleware.params :as params]
            [muuntaja.middleware :as middleware]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.multipart :as multipart]
            [clojure.java.io :as io]))

(def xlsx-mime-type
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

(defn download-handler [_request]
  (let [file (io/file (io/resource "book.xlsx"))]
    (-> (file-response (str file))
        (content-type xlsx-mime-type)
        (header "Content-Disposition" (str "attachment;filename=" (gensym "book") ".xlsx")))))

(def router
  (ring/router
    [["/download" {:get      download-handler
                   :summary  "downloads an xlsx file"
                   :produces ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]
                   :swagger  {:produces [xlsx-mime-type]}
                   ;:responses {200 {:headers {:content-type {:type "file"
                   ;                                          :default "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}}
                   ;                 ;:body {:type :file}
                   ;                 }
                   ;            }
                   }]
     ["" {:no-doc true}
      ["/swagger.json"
       {:get {:no-doc  true
              :swagger {:info     {:title "Download API"}
                        :basePath "/"}
              :handler (swagger/create-swagger-handler)}}]
      ["/api-docs*" {:get (swagger-ui/create-swagger-ui-handler
                            {:url "/swagger.json"})}]]]
    {:data {:coercion   rc-spec/coercion
            :middleware [params/wrap-params
                         middleware/wrap-format
                         parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware
                         multipart/multipart-middleware]}}))

(def handler
  (ring/ring-handler
    router
    (constantly (not-found "Not found"))))

(comment
  (defonce server
           (jetty/run-jetty #'handler {:host  "0.0.0.0"
                                       :port  3000
                                       :join? false}))
  ;Load the swagger UI
  (browse-url "http://localhost:3000/api-docs")
  ;This will download the file
  (browse-url "http://localhost:3000/download")
  (.stop server)
  )