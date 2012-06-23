(ns decomatic.test.core
  (:use decomatic.core
        midje.sweet
        [midje.util :only (expose-testables)]))

(expose-testables decomatic.core)

(facts "about deco-keys-one-path"
  (deco-keys-one-path {..k.. ..v..} [..k..])
  => #{..v..}
  (deco-keys-one-path {..k1.. {..k2.. ..v..}} [..k1.. ..k2..])
  => #{..v..}
  (deco-keys-one-path {..k1.. {..k2.. ..v..}} [..k3.. ..k2..])
  => #{}
  (deco-keys-one-path {..k1.. {..k2.. ..v1..}} [..k1.. ..k3..])
  => #{}
  (deco-keys-one-path {..k1.. [..v1.. ..v2..]} [..k1..])
  => #{[..v1.. ..v2..]}
  (deco-keys-one-path {..k1.. [..v1.. ..v2..]} [..k1.. 1])
  => #{..v2..}
  (deco-keys-one-path {..k1.. [..v1.. ..v2..]} [..k1.. :*])
  => #{..v1.. ..v2..}
  (deco-keys-one-path {..k1.. [{..k2.. ..v1..}
                               {..k2.. ..v2..}]}
                      [..k1.. :* ..k2..])
  => #{..v1.. ..v2..}
  (deco-keys-one-path {..k1.. {..k2.. ..v1..
                               ..k3.. ..v2..}}
                      [..k1.. :*])
  => #{..v1.. ..v2..})

(unfinished xform)
(fact "about xform-values"
  (xform-values {..k1.. ..v1.., ..k2.. ..v2..} xform)
  => {..k1.. ..x-v1.., ..k2.. ..x-v2..}
  (provided (xform ..k1.. ..v1..) => ..x-v1..
            (xform ..k2.. ..v2..) => ..x-v2..))

(facts "about replace-deco-keys-one-path"
  (replace-deco-keys-one-path {..k.. ..v..} [..k..] {..v.. ..deco-v..})
  => {..k.. ..deco-v..}
  (replace-deco-keys-one-path {..k1.. {..k2.. ..v..}}
                              [..k1.. ..k2..]
                              {..v.. ..deco-v..})
  => {..k1.. {..k2.. ..deco-v..}}
  (replace-deco-keys-one-path {..k1.. [..v1.. ..v2..]}
                              [..k1.. 1]
                              {..v2.. ..deco-v2..})
  => {..k1.. [..v1.. ..deco-v2..]}
  "seq wildcard at end of path"
  (replace-deco-keys-one-path {..k1.. [..v1.. ..v2..]}
                              [..k1.. :*]
                              {..v1.. ..deco-v1.., ..v2.. ..deco-v2..})
  => {..k1.. [..deco-v1.. ..deco-v2..]}
  "seq wildcard within path"
  (replace-deco-keys-one-path {..k1.. [{..k2.. ..v1..}
                                       {..k2.. ..v2..}]}
                              [..k1.. :* ..k2..]
                              {..v1.. ..deco-v1.., ..v2.. ..deco-v2..})
  => {..k1.. [{..k2.. ..deco-v1..}, {..k2.. ..deco-v2..}]}
  "map wildcard at end of path"
  (replace-deco-keys-one-path {..k1.. ..v1.., ..k2.. ..v2..}
                              [:*]
                              {..v1.. ..deco-v1.., ..v2.. ..deco-v2..})
  => {..k1.. ..deco-v1.. ..k2.. ..deco-v2..}
  "map wildcard within path"
  (replace-deco-keys-one-path {..k1.. {..k3.. ..v1..}, ..k2.. {..k3.. ..v2..}}
                              [:* ..k3..]
                              {..v1.. ..deco-v1.., ..v2.. ..deco-v2..})
  => {..k1.. {..k3.. ..deco-v1..}, ..k2.. {..k3.. ..deco-v2..}})

(unfinished lookup)
(fact "about decorate"
  (decorate lookup xform [[..k1.. :*]] {..k1.. [..v1.. ..v2..]
                                        ..k2.. [..undecorated..]})
  => {..k1.. [..deco-v1.. ..deco-v2..]
      ..k2.. [..undecorated..]}
  (provided (lookup #{..v1.. ..v2..}) => {..v1.. ..lk-v1..
                                          ..v2.. ..lk-v2..}
            (xform ..v1.. ..lk-v1..) => ..deco-v1..
            (xform ..v2.. ..lk-v2..) => ..deco-v2..)

  (decorate lookup xform [[..k1.. :*]] {})
  => {}
  (decorate lookup xform [[:* ..k1..]] [])
  => [])

(fact "the type of a seq is preserved by decorate"
  (decorate lookup clobber [[:*]] (list ..k1..))
  => list?
  (provided (lookup #{..k1..}) => {..k1.. ..v1..})

  (decorate lookup clobber [[:*]] [..k1..])
  => vector?
  (provided (lookup #{..k1..}) => {..k1.. ..v1..})

  (decorate lookup clobber [[:*]] #{..k1..})
  => set?
  (provided (lookup #{..k1..}) => {..k1.. ..v1..}))