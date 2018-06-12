(ns scratch.parse
  (:require [instaparse.core :as insta]
            [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]))

(def grammar (io/resource "grammar.bnf"))

(def parser (insta/parser grammar))

(comment
  (parser "put pie in bag")
  (insta/parses parser "put pie in bag" :partial true)
  )
