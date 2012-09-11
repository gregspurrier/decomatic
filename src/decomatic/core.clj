(ns decomatic.core
  "Decoration of data structures"
  (:require [decomatic.path :as path]
            [clojure.set :as set]
            [clojure.walk :as w]))

(def ^{:doc "The symbol used to indicate a wildcard step in a path."
       :dynamic true}
  *wildcard-sym* :*)

(defn- wildcard?
  [x]
  ;; Explicity check for keyword in order to play nicely with
  ;; metaconstants in the tests.
  (and (keyword? x) (= x *wildcard-sym*)))


(declare deco-keys-from-path-tree)

(defn- deco-keys-for-wildcard-step
  [coll subtree key-set]
  (->> (cond (map? coll) (vals coll)
             (seq coll)  coll
             :else nil)
       (reduce (fn [ks c] (deco-keys-from-path-tree c subtree ks)) key-set)))

(defn- deco-keys-from-path-tree
  [coll path-tree key-set]
  (if (empty? path-tree)
    (conj key-set coll)
    (reduce (fn [ks [step subtree]]
              (if (wildcard? step)
                (deco-keys-for-wildcard-step coll subtree ks)
                (if (contains? coll step)
                  (deco-keys-from-path-tree (coll step) subtree ks)
                  ks)))
            key-set
            path-tree)))

(defn- ^:testable deco-keys
  "Given a collection and a seq of paths into that collection, returns the set
of decoration keys that are found a the end of the paths."
  [x path-tree]
  (or (deco-keys-from-path-tree x path-tree #{})
      #{}))

(defn- ^:testable xform-values
  "Updates m by replacing each value with the result of applying f to its key
and value."
  [m f]
  (into {} (map (fn [[k v]] [k (f k v)]) m)))

(defn- ^:testable update-values
  "Updates m by replacing each value with the result of applying f to it."
  [f m]
  (into {} (map (fn [[k v]] [k (f v)]) m)))

(declare ^:private replace-deco-keys-one-path)

(defn- replace-deco-keys-for-inner-wildcard
  [x path-rest results]
  (if (map? x)
    (update-values #(replace-deco-keys-one-path % path-rest results) x)
    (w/walk #(replace-deco-keys-one-path % path-rest results) identity x)))

(defn- replace-deco-keys-for-terminal-wildcard
  [x results]
  (if (map? x)
    (update-values results x)
    (w/walk results identity x)))

(defn- replace-deco-keys-for-wildcard
  [x path-rest results]
  (if (seq path-rest)
    (replace-deco-keys-for-inner-wildcard x path-rest results)
    (replace-deco-keys-for-terminal-wildcard x results)))

(defn- ^:testable replace-deco-keys-one-path
  "Given a data structure, a path describing a location--or locations--within
it, a map containing the results of looking up the decoration keys located
at those locations and transforming them, returns an updated datastructure
in which each path location has been replaced with its decorated version."
  [x path results]
  (let [[step & more] path]
    (cond (wildcard? step)
          (replace-deco-keys-for-wildcard x more results)
          (contains? x step)
          (if (seq more)
            (assoc x step (replace-deco-keys-one-path (x step) more results))
            ;; At the deco-key, replace it with the result
            (let [deco-key (x step)]
              (assoc x step (results deco-key))))
          :else x)))

(defn- replace-deco-keys
  [x paths results]
  (reduce (fn [x path] (replace-deco-keys-one-path x path results))
          x
          paths))

(defn- lookup-if-keys
  [keys f]
  (when (seq keys)
    (f keys)))

(defn clobber
  "Convenience xform fn. Returns the looked up value, ignoring the original."
  [_ x]
  x)

(defn decorate
  "Decorates a data structure x, given a lookup function, an optional
transformation function, and a seq of paths describing locations within x.
lookup-fn is a function that takes a set of decoration keys and returns a map
from decoration key to corresponding value. xform-fn is a function that takes a
decoration key and its looked-up value and returns the value with which the
decoration key will be replaced in the decorated data structure. xform-fn is
assumed to be a pure function and will only be called once for each decoration
key, regardless of how many times that key occurs within x."
  ([lookup-fn paths x]
     (decorate lookup-fn clobber paths x))
  ([lookup-fn xform-fn paths x]
     (decorate lookup-fn xform-fn paths (path/path-tree paths) x))
  ([lookup-fn xform-fn paths path-tree x]
     (let [results (-> (deco-keys x path-tree)
                       (lookup-if-keys lookup-fn)
                       (xform-values xform-fn))]
       (replace-deco-keys x paths results))))
