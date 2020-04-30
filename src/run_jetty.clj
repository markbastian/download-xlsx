(ns run-jetty
  (:require [download-xlsx.core :as dxl]
            [ring.adapter.jetty :as jetty]
            [clojure.java.browse :refer [browse-url]]))

(defn -main [& args]
  (jetty/run-jetty #'dxl/handler {:host  "0.0.0.0"
                                  :port  3000
                                  :join? false})
  (browse-url "http://localhost:3000/api-docs"))