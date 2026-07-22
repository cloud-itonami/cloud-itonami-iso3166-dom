(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "DOM" 0)
        s (registry/register-submit "eng-1" "DOM" 0)]
    (is (= "DOM-DFT-000000" (get d "draft_number")))
    (is (= "DOM-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "DOM" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest mipyme-reserved-eligible-requires-certification
  (testing "no MICM certification -> never eligible, regardless of other facts"
    (is (false? (registry/mipyme-reserved-eligible? {:micm-certification-valid? false})))
    (is (false? (registry/mipyme-reserved-eligible? {})))))

(deftest mipyme-reserved-eligible-women-led-consistency
  (testing "claiming women-led without the certification itself recording it -> ineligible"
    (is (false? (registry/mipyme-reserved-eligible?
                 {:micm-certification-valid? true
                  :women-led-claim? true
                  :micm-certification-women-led? false}))))
  (testing "claiming women-led WITH the certification recording it -> eligible"
    (is (true? (registry/mipyme-reserved-eligible?
                {:micm-certification-valid? true
                 :women-led-claim? true
                 :micm-certification-women-led? true}))))
  (testing "not claiming women-led at all -> the certification's women-led flag is irrelevant"
    (is (true? (registry/mipyme-reserved-eligible?
                {:micm-certification-valid? true
                 :women-led-claim? false
                 :micm-certification-women-led? false})))))

(deftest mipyme-reserved-eligible-fiscal-arrears-grace
  (testing "arrears within the 6-month grace window are fine"
    (is (true? (registry/mipyme-reserved-eligible?
                {:micm-certification-valid? true :fiscal-arrears-months 6})))
    (is (true? (registry/mipyme-reserved-eligible?
                {:micm-certification-valid? true :fiscal-arrears-months 0})))
    (is (true? (registry/mipyme-reserved-eligible?
                {:micm-certification-valid? true :fiscal-arrears-months nil}))))
  (testing "arrears beyond 6 months -> ineligible"
    (is (false? (registry/mipyme-reserved-eligible?
                 {:micm-certification-valid? true :fiscal-arrears-months 7})))))

(deftest mipyme-reserved-eligible-grave-sanction-forfeiture
  (testing "within the 12-month forfeiture window after a grave sanction -> ineligible"
    (is (false? (registry/mipyme-reserved-eligible?
                 {:micm-certification-valid? true :months-since-grave-sanction 3})))
    (is (false? (registry/mipyme-reserved-eligible?
                 {:micm-certification-valid? true :months-since-grave-sanction 11}))))
  (testing "at/after the 12-month mark -> forfeiture has lapsed, eligible"
    (is (true? (registry/mipyme-reserved-eligible?
                {:micm-certification-valid? true :months-since-grave-sanction 12})))
    (is (true? (registry/mipyme-reserved-eligible?
                {:micm-certification-valid? true :months-since-grave-sanction nil})))))

(deftest mipyme-reserved-ineligible-claim-is-entity-scope-gated
  (testing "an engagement NOT declared :mipyme-reserved-award? is never flagged, even if it would fail eligibility"
    (is (false? (registry/mipyme-reserved-ineligible-claim?
                 {:mipyme-reserved-award? false :micm-certification-valid? false}))))
  (testing "a reserved-award engagement that fails eligibility -> ineligible claim"
    (is (true? (registry/mipyme-reserved-ineligible-claim?
                {:mipyme-reserved-award? true :micm-certification-valid? false}))))
  (testing "a reserved-award engagement that DOES satisfy eligibility -> not flagged"
    (is (false? (registry/mipyme-reserved-ineligible-claim?
                 {:mipyme-reserved-award? true :micm-certification-valid? true})))))
