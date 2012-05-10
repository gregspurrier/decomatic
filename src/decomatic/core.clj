(ns decomatic.core)

(defn- ^:testable deco-keys-one-path
  "Given a datastructure and a path describing a location--or locations--within
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