(ns decomatic.path)

;; ## Path Trees
;; Path trees are represented as nested hashes. When the value of a key is
;; {}, the decoration target has been reached.

(defn merge-path-trees
  [& maps]
  (apply merge-with merge-path-trees maps))

(defn path-to-tree
  "Converts a seq of steps into a path tree."
  [steps]
  (if-let [[step & more] steps]
    {step (path-to-tree more)}
    {}))

(defn path-tree
  [paths]
  (->> paths
       (map path-to-tree)
       (reduce merge-path-trees)))
