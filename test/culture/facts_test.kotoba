(ns culture.facts-test
  (:require [clojure.edn :as edn]
            [kotoba.lang.text :as str]
            [clojure.test :refer [deftest is]]
            [culture.facts :as facts]))

(deftest dom-has-culture-basis
  (let [sb (facts/spec-basis "DOM")]
    (is (= 7 (count sb)))
    (is (= (count sb) (count (set (map :culture/id sb)))))
    (is (every? #(str/starts-with? (:culture/url %) "https://") sb))
    (is (every? #(= "DOM" (:culture/country %)) sb))
    (is (every? #(nil? (:culture/municipality %)) sb))
    (is (every? #(seq (:culture/summary %)) sb))
    (is (every? #(string? (:culture/retrieved-at %)) sb))))

(deftest unknown-jurisdiction-has-no-basis
  ;; DMA is Dominica -- a different country, never covered here.
  (is (nil? (facts/spec-basis "DMA")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["DOM" "DMA"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["DMA"] (:missing-jurisdictions c)))))

(deftest by-kind-filters
  (is (= 3 (count (facts/by-kind "DOM" :dish))))
  (is (= ["dom.product.dominican-amber"]
         (mapv :culture/id (facts/by-kind "DOM" :product))))
  (is (empty? (facts/by-kind "DOM" :other)))
  (is (empty? (facts/by-kind "DMA" :dish))))

(deftest tx-file-matches-catalog
  (let [tx (edn/read-string (slurp "data/culture-tx.edn"))
        flat (mapcat val (sort-by key facts/catalog))]
    (is (= (vec flat) (vec tx)))))
