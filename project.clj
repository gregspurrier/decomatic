(defproject decomatic "0.2.0-SNAPSHOT"
  :description "Datastructure decoration"
  :url "https://github.com/gregspurrier/decomatic"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/mit-license.php"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]
                                  [criterium "0.3.0"]]}}
  :min-lein-version "2.0.0")
