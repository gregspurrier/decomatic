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

(facts "about apply-xforms-one-path"
  (apply-xforms-one-path {..k.. ..v..} [..k..] {..v.. ..deco-v..} clobber)
  => {..k.. ..deco-v..}
  (provided (clobber ..v.. ..deco-v..) => ..deco-v..)
  (apply-xforms-one-path {..k1.. {..k2.. ..v..}}
                         [..k1.. ..k2..]
                         {..v.. ..deco-v..}
                         clobber)
  => {..k1.. {..k2.. ..deco-v..}}
  (provided (clobber ..v.. ..deco-v..) => ..deco-v..)
  (apply-xforms-one-path {..k1.. [..v1.. ..v2..]}
                         [..k1.. 1]
                         {..v2.. ..deco-v2..}
                         clobber)
  => {..k1.. [..v1.. ..deco-v2..]}
  (provided (clobber ..v2.. ..deco-v2..) => ..deco-v2..))