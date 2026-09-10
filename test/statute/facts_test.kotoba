(ns statute.facts-test
  (:require [kotoba.lang.text :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest dom-has-spec-basis
  (let [sb (facts/spec-basis "DOM")]
    (is (= 2 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["DOM" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["dom.codigo-trabajo"]
         (mapv :statute/id (facts/by-topic "DOM" :labor))))
  (is (= ["dom.ley-sociedades-comerciales"]
         (mapv :statute/id (facts/by-topic "DOM" :corporate-governance))))
  (is (empty? (facts/by-topic "ATL" :labor))))
