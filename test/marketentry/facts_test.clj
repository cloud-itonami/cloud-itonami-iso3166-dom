(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest dom-has-spec-basis
  (let [sb (facts/spec-basis "DOM")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "DOM")))
    (is (some? (facts/mipyme-reserved-spec-basis "DOM")))))

(deftest dom-rep-spec-basis-is-populated
  (testing "Ley 47-25 Art. 38/39 (inhabilidades/prohibiciones) was independently read and is honestly cited -- unlike CAF/BTN/BWA's sampled nil siblings"
    (let [rep (facts/rep-spec-basis "DOM")]
      (is (some? rep))
      (is (string? (:rep-owner-authority rep)))
      (is (string? (:rep-legal-basis rep))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "DOM")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "DOM" all)))
    (is (not (facts/required-evidence-satisfied? "DOM" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["DOM" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest mipyme-reserved-spec-basis-criteria
  (let [rm (facts/mipyme-reserved-spec-basis "DOM")]
    (is (= 6 (get-in rm [:mipyme-reserved-criteria :fiscal-arrears-grace-months])))
    (is (= 12 (get-in rm [:mipyme-reserved-criteria :grave-sanction-forfeiture-months])))))
