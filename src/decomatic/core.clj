(ns decomatic.core
  "Decoration of data structures"
  (:require [clojure.walk :as w]))

(defn- ^:testable deco-keys-one-path
  "Given a data structure and a path describing a location--or locations--within
it, returns the set of decoration keys found at the location(s)."
  ([x path] (deco-keys-one-path x path #{}))
  ([x path keys]
     (if (seq path)
       (let [step (first path)]
         (if (and (keyword? step) (= step :*)) ; Keep metaconstants happy
           ;; Wildcard. Take every step.
           (cond (map? x) (recur (vals x) path keys)
                 (seq x)  (recur (rest x)
                                 path
                                 (deco-keys-one-path (first x)
                                                     (rest path)
                                                     keys))
                 :else keys)
           ;; Take the specific step, if possible
           (if (contains? x step)
             (recur (x step) (rest path) keys)
             keys)))
       ;; End of path, x is a decoration key
       (conj keys x))))

(defn- ^:testable deco-keys
  "Given a datastructure and a seq of paths, returns the set of decoration keys
that are found a the end of the paths."
  [x paths]
  (reduce (fn [ks path]
            (into ks (deco-keys-one-path x path)))
          #{}
          paths))

(defn- ^:testable xform-values
  [m f]
  (reduce (fn [m [k v]] (assoc m k (f k v)))
          {}
          m))

(def ^:private replace-deco-keys-one-path)

(defn- replace-deco-keys-for-wildcard
  [x path-rest results]
  (if (map? x)
    (if (seq path-rest)
      (reduce (fn [m [k v]]
                (assoc m k (replace-deco-keys-one-path v path-rest results)))
              {}
              x)
      (reduce (fn [m [k v]] (assoc m k (results v)))
              {}
              x))
    (if (seq path-rest)
      (w/walk #(replace-deco-keys-one-path % path-rest results) identity x)
      (w/walk #(results %) identity x))))

(defn- ^:testable replace-deco-keys-one-path
  "Given a data structure, a path describing a location--or locations--within
it, a map containing the results of looking up the decoration keys located
at those locations and transforming them, returns an updated datastructure
in which each path location has been transformed by the xform function."
  [x path results]
  (let [[step & more] path]
    (cond (and (keyword? step) (= step :*))
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

(defn decorate
  "Decorates a data structure x, given a lookup function, a transformation
function, and a seq of paths describing locations within x. lookup-fn is a
function that takes a set of decoration keys and returns a map from decoration
key to corresponding value. xform-fn is a function that takes a decoration
key and its looked-up value and returns the value with which the decoration
key will be replaced in the decorated data structure. xform-fn is assumed to
be a pure function and will only be called once for each decoration key,
regardless of how many times that key occurs within x."
  [lookup-fn xform-fn paths x]
  (let [results (-> (deco-keys x paths)
                    (lookup-if-keys lookup-fn)
                    (xform-values xform-fn))]
    (replace-deco-keys x paths results)))

(defn clobber
  "Convenience xform fn. Returns the looked up value, ignoring the original."
  [_ x]
  x)