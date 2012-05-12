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

(def apply-xforms-one-path)

(defn- apply-xforms-to-seq
  [x path-rest results xform]
  (cond (map? x)
        (if (seq path-rest)
          (reduce (fn [m [k v]]
                    (assoc m k (apply-xforms-one-path
                                v path-rest results xform)))
                  {}
                  x)
          (reduce (fn [m [k v]] (assoc m k (xform v (results v))))
                  {}
                  x))
        (seq x)
        (if (seq path-rest)
          (map #(apply-xforms-one-path % path-rest results xform) x)
          (map #(xform % (results %)) x))
        :else nil))

(defn- ^:testable apply-xforms-one-path
  [x path results xform]
  (let [[step & more] path]
    (cond (and (keyword? step) (= step :*))
          (apply-xforms-to-seq x more results xform)
          (contains? x step)
          (if (seq more)
            (assoc x step (apply-xforms-one-path (x step) more results xform))
            ;; At the deco-key, apply the xform
            (let [deco-key (x step)]
              (assoc x step (xform deco-key (results deco-key)))))
          :else x)))

(defn clobber
  "Convenience xform fn. Returns the looked up value, ignoring the original."
  [_ x]
  x)