(ns decomatic.benchmark.basic
  (:use [criterium.core :only (benchmark report-result)])
  (:require [decomatic.core :as deco]
            [decomatic.path :as path]))

(def bottom {:x 1 :y 2})
(def mid {:a (vec (repeat 5 bottom))
          :b {:q {:r {:s bottom}}}
          :c {:d (vec (repeat 10 bottom))}})
(def sample-data (vec (repeat 20 mid)))

;; We know the keys, so use a hard-coded lookup function to
;; keep the benchmark about the non-lookup code.
(defn sample-lookup-fn
  [_]
  {1 :one
   2 :two})

(def paths [[:* :a :* :x]
            [:* :a :* :y]
            [:* :b :q :r :s :x]
            [:* :b :q :r :s :y]
            [:* :b :* :x]
            [:* :b :* :y]
            [:* :c :d :* :x]
            [:* :c :d :* :y]])

(def path-tree (path/path-tree paths))

(defn -main
  []
  (report-result
    (benchmark (deco/decorate sample-lookup-fn deco/clobber paths path-tree sample-data))))