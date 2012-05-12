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
(facts "about apply-xforms-one-path"
  (apply-xforms-one-path {..k.. ..v..} [..k..] {..v.. ..deco-v..} xform)
  => {..k.. ..deco-v..}
  (provided (xform ..v.. ..deco-v..) => ..deco-v..)

  (apply-xforms-one-path {..k1.. {..k2.. ..v..}}
                         [..k1.. ..k2..]
                         {..v.. ..deco-v..}
                         xform)
  => {..k1.. {..k2.. ..deco-v..}}
  (provided (xform ..v.. ..deco-v..) => ..deco-v..)

  (apply-xforms-one-path {..k1.. [..v1.. ..v2..]}
                         [..k1.. 1]
                         {..v2.. ..deco-v2..}
                         xform)
  => {..k1.. [..v1.. ..deco-v2..]}
  (provided (xform ..v2.. ..deco-v2..) => ..deco-v2..)

  "seq wildcard at end of path"
  (apply-xforms-one-path {..k1.. [..v1.. ..v2..]}
                         [..k1.. :*]
                         {..v1.. ..deco-v1.., ..v2.. ..deco-v2..}
                         xform)
  => {..k1.. [..deco-v1.. ..deco-v2..]}
  (provided (xform ..v1.. ..deco-v1..) => ..deco-v1..
            (xform ..v2.. ..deco-v2..) => ..deco-v2..)

  "seq wildcard within path"
  (apply-xforms-one-path {..k1.. [{..k2.. ..v1..}
                                  {..k2.. ..v2..}]}
                         [..k1.. :* ..k2..]
                         {..v1.. ..deco-v1.., ..v2.. ..deco-v2..}
                         xform)
  => {..k1.. [{..k2.. ..deco-v1..}, {..k2.. ..deco-v2..}]}
  (provided (xform ..v1.. ..deco-v1..) => ..deco-v1..
            (xform ..v2.. ..deco-v2..) => ..deco-v2..)
  
  )