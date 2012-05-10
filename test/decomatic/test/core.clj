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
