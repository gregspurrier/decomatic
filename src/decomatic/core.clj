(ns decomatic.core)

(defn- ^:testable deco-keys-one-path
  "Given a datastructure and path describing a location--or locations--within
it, returns the set of decoration keys found at the location(s)."
  ([x path] (deco-keys-one-path x path #{}))
  ([x path keys]
     (if (seq path)
       (let [step (first path)]
         (if (and (keyword? step) (= step :*)) ; Keep metaconstants happy
           (if (seq x)
             (recur (rest x)
                    path
                    (deco-keys-one-path (first x) (rest path) keys))
             keys)
           (recur (x (first path)) (rest path) keys)))
       ;; End of path, this is a key
       (conj keys x))))

(defn- ^:testable deco-keys
  "Given a datastructure and a seq of paths, returns the set of decoration keys
that are found a the end of the paths."
  [x paths]
  (reduce (fn [ks path]
            (into ks (deco-keys-one-path x path)))
          #{}
          paths))