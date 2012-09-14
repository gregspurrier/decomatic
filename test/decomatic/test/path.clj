(ns decomatic.test.path
  (:use decomatic.path
        midje.sweet))

(facts "about path-trees"
  (path-tree [[:a :b :c]])
  => {:a {:b {:c {}}}}
  (path-tree [[:a :b :c]
              [:a :b :d]])
  => {:a {:b {:c {}
              :d {}}}})