#!/usr/bin/env bb

(require '[clojure.pprint :refer [pprint]]
         '[clojure.string :as str]
         '[org.httpkit.server :as server])

(def debug? true)

;; hiccup-like
(defn html [v]
  (cond (vector? v)
        (let [tag (first v)
              attrs (second v)
              attrs (when (map? attrs) attrs)
              elts (if attrs (nnext v) (next v))
              tag-name (name tag)]
          (format "<%s%s>%s</%s>\n" tag-name (html attrs) (html elts) tag-name))
        (map? v)
        (str/join ""
                  (map (fn [[k v]]
                         (format " %s=\"%s\"" (name k) v)) v))
        (seq? v)
        (str/join " " (map html v))
        :else (str v)))

;; the home page
(defn home-response []
  {:status 200
   :body (str
          "<!DOCTYPE html>\n"
          (html
           [:html
            [:head
             [:title "bb-SVG"]]
            [:body
             [:h1 "bb-SVG"]
             [:svg {:width 300 :height 200}
             [:rect {:width "100%" :height "100%" :fill "red"}]
              [:circle {:cx 150 :cy 100 :r 80 :fill "green"}]
             [:text {:x "50%" :y "50%" :font-size 40 :text-anchor "middle" :fill "white"}
                    "bb-SVG"]]]]))})

;; run the server
(defn handler [req]
  (when debug?
    (println "Request:")
    (pprint req))
  (let [body (some-> req :body slurp java.net.URLDecoder/decode)
        _ (when (and debug? body)
            (println "Request body:" body))
        response (home-response)]
    (when debug?
      (println "Response:")
      (pprint (dissoc response :body))
      (println))
    response))

(server/run-server handler {:port 8080})
(println "Server started on port 8080.")
@(promise) ;; wait until SIGINT
