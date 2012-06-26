# Decomatic
Decomatic relieves the tedium of decorating Clojure data structures with data retrieved from another source. 

For example, suppose you have a map representing a message between two users:

```clojure
(def msg {:from 1, :to 2, :text "Hello Mary!"})
```

The `:from` and `:to` fields contain user IDs, but you want a version of the data structure that has those IDs replaced with maps containing the users' data as retrieved from a database or API:

```clojure
{:from {:id 1, :name "Fred"}
 :to   {:id 2, :name "Mary"}
 :text "Hello Mary!"}}
```

This can be achieved with Decomatic's `decorate` function:

```clojure
(decomatic.core/decorate user-lookup-fn [[:from], [:to]] msg)
;; => {:text "Hello Mary!",:from {:name "Fred", :id 1}, :to {:name "Mary", :id 2}}
```

The first argument to `decorate` is a lookup function that takes a set of keys and returns a map from those keys to their corresponding values. The second argument is a sequence of paths describing the locations of the keys within the data structure to be decorated. See the examples below for more details about lookup functions and paths.

## Examples
For the purposes of the following examples, we will use an in-memory database of users represented as a map:

```clojure
(def user-database
  {1 {:id 1, :name "Fred"}
   2 {:id 2, :name "Mary"}
   3 {:id 3, :name "Amy"}
   4 {:id 4, :name "Mark"}})
```

Decomatic lookup functions take a set of keys as their argument. They return a map from those keys to their corresponding values. Here is one that works with our in-memory user database:

```clojure
(defn user-lookup-fn
  [ids]
  (into {} (map (juxt identity user-database) ids)))

(user-lookup-fn #{1 4})
;; => {1 {:name "Fred", :id 1}, 4 {:name "Mark", :id 4}}
```

### Simple Paths
`decorate` takes as its second argument a sequence of paths. Each path is a sequence of steps into the nested data structure, similar to the `ks` argument to Clojure's `get-in` function. The value found at the end of the path will be one of the keys passed to the lookup function.

```clojure
(def msg {:from 1, :to 2, :text "Hi Mary!"})

(decorate user-lookup-fn [[:from][:to]] msg)
;; => {:text "Hi Mary!",
;;     :from {:name "Fred", :id 1},
;;     :to {:name "Mary", :id 2}}
```

### Paths with Wildcards
`decorate` paths also support wildcards. Use the keyword `:*` in place of a specific step:

```clojure
(def post {:author 1
           :text "This a a post"
           :comments [{:user 2, :text "comment one"}
                      {:user 4, :text "comment two"}]})

(decorate user-lookup-fn [[:author] [:comments :* :user]] post)
;; => {:text "This a a post",
;;     :author {:name "Fred", :id 1},
;;     :comments
;;       [{:text "comment one", :user {:name "Mary", :id 2}}
;;        {:text "comment two", :user {:name "Mark", :id 4}}]}
```

## License
Copyright (c) 2012 Greg Spurrier.

Decomatic is distributed under the MIT license. Please see LICENSE.txt for the details.
