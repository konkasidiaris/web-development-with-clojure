(ns html-templating.core
  (:require [selmer.parser :as selmer]
            [selmer.filters :as filters]
            [selmer.middleware :refer [wrap-error-page]]))

(filters/add-filter! :empty? empty?)
(filters/add-filter! :foo
                     (fn [x] [:safe (.toUpperCase x)]))

(selmer.parser/cache-off!)

(selmer/render
 "{% if files|empty? %}no files{% else %}files{% endif %}"
 {:files []})

(selmer/add-tag!
 :image
 (fn [args context-map]
   (str "<img src=" (first args) "/>")))

(selmer/add-tag!
 :uppercase
 (fn [args context-map block]
   (.toUpperCase (get-in block [:uppercase :content])))
 :enduppercase)

(defn renderer []
  (wrap-error-page
   (fn [template]
     {:status 200
      :body (selmer/render-file template {})})))

(comment
  (selmer/render
   "{% uppercase %} foo {{bar}} baz{% enduppercase %}"
   {:bar "injected"})

  (selmer/render "{{content|safea}}" {})

  (selmer/render-file "hello.html" {})

  ((renderer) "hello.html")
  ((renderer) "error.html")
  ())