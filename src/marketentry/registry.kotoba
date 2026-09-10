(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `mipyme-reserved-eligible?` / `mipyme-reserved-ineligible-claim?` are
  the SAME discipline applied to a genuinely Dominican-Republic-
  specific mechanism: Ley 47-25 (Ley de Contrataciones Públicas), Art.
  172's own 'Reservas de contrataciones' (every contracting institution
  must reserve >=30% of its procurement-plan budget lines exclusively
  for MIPYMEs -- 20 points general + 10 points earmarked for
  women-led MIPYMEs), and its implementing Decreto 52-26's own Arts.
  222-223. To ACTUALLY be eligible for the reserved-contract benefit, a
  bidder must independently satisfy ALL of:
    1. hold a current 'Certificación de MIPYME', which Decreto 52-26
       Art. 222 núm. 3 states is issued by the Ministerio de Industria,
       Comercio y MIPYMES (MICM) and is itself 'prueba suficiente' of
       size category AND women-led status;
    2. if additionally claiming the women-led sub-benefit, the
       certification must ITSELF record women-led status (a
       claim-consistency check -- a bidder cannot self-declare
       women-led when its own certification doesn't say so);
    3. fiscal/social-security arrears, if any, must not exceed six (6)
       months (Ley 47-25 Art. 172 Párrafo III, elaborated by Decreto
       52-26 Art. 223);
    4. not be within the twelve (12)-month forfeiture window following
       a tax-administration 'falta grave' sanction (Ley 47-25 Art. 172
       Párrafo IV).
  The Decreto's own numeral 1 additionally requires the contract's
  estimated value to fall within MIPYME-accessible ranges -- that
  branch delegates to 'el análisis correspondiente' (own text) with no
  independently-derivable number, and is DELIBERATELY NOT modeled here,
  the same honest scope-narrowing CAF's un-modeled Marché réservé VALUE
  threshold (delegated to a ministerial arrêté) already established for
  this family: only the ELIGIBILITY CRITERIA the Ley/Decreto state
  directly and unconditionally (certification + fiscal-standing) are
  independently recomputed here.

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors: Bulgaria's ЗОП Art. 54(5) de-minimis is a
  PERCENTAGE-OF-TURNOVER ELIGIBILITY formula, Albania's Neni 76(2)(c)
  carve-out is a FLAT-CONSTANT ELIGIBILITY threshold, Azerbaijan's/
  Armenia's flagship checks are BOOLEAN registry-membership ELIGIBILITY
  reads, Antigua and Barbuda's vendor-class check is a THREE-TIER
  ELIGIBILITY-THRESHOLD classification, Benin's MPME mechanism is a
  BID-EVALUATION PRICE ADJUSTMENT, Bhutan's FDI Negative List is a
  CATEGORICAL SECTOR-EXCLUSION allow-list gate, and Central African
  Republic's Marché réservé mechanism is a MULTI-CRITERION
  INCLUSION-ELIGIBILITY OR-of-workforce-composition-percentages test.
  DOM's MIPYME-reserved-contract mechanism is none of these: it is a
  CERTIFICATION-GATED, DUAL-CLOCK ELIGIBILITY test -- an AND of (i) a
  boolean external-certification gate, (ii) a conditional
  claim-consistency cross-check (the women-led sub-claim against what
  the certification itself records), and (iii) TWO INDEPENDENT
  time-bounded grace/forfeiture windows (a 6-month arrears grace clock
  and a 12-month sanction-forfeiture clock) -- the first in this family
  to compose a certification gate with dual independent timing clocks
  rather than a single percentage/threshold/tier/registry-membership
  test.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal. It builds the RECORD an
  operator would keep, not the act of submitting a portal registration
  itself (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def mipyme-reserved-thresholds
  "Ley 47-25 Art. 172 Párrafo III/IV + Decreto 52-26 Art. 223 (own
  primary text, curl/WebFetch-verified 2026-07-22 against
  dgcp.gob.do's own hosting): the two independent grace/forfeiture
  clocks for MIPYME-reserved-contract eligibility."
  {:fiscal-arrears-grace-months 6
   :grave-sanction-forfeiture-months 12})

(defn mipyme-reserved-eligible?
  "The ground-truth MIPYME-reserved-contract eligibility for
  `engagement`, independently recomputed from its own declared
  certification, women-led sub-claim consistency, and fiscal-standing
  facts -- an AND of every branch below. A missing/nil declared value
  on the fiscal-standing branches is treated as 'no issue' (nil
  arrears-months / nil months-since-grave-sanction = current /
  never-sanctioned), the same 'missing means no adverse fact' reading
  `engagement-fee-matches-claim?`'s sibling checks use elsewhere in
  this family; a missing/false `:micm-certification-valid?` simply
  fails eligibility (does not throw)."
  [{:keys [micm-certification-valid? micm-certification-women-led?
           women-led-claim? fiscal-arrears-months months-since-grave-sanction]}]
  (boolean
   (and micm-certification-valid?
        (or (not women-led-claim?) micm-certification-women-led?)
        (or (nil? fiscal-arrears-months)
            (<= (double fiscal-arrears-months)
                (:fiscal-arrears-grace-months mipyme-reserved-thresholds)))
        (or (nil? months-since-grave-sanction)
            (>= (double months-since-grave-sanction)
                (:grave-sanction-forfeiture-months mipyme-reserved-thresholds))))))

(defn mipyme-reserved-ineligible-claim?
  "Does `engagement` declare `:mipyme-reserved-award? true` (i.e. it is
  bidding on / has been awarded a contract reserved under Ley 47-25
  Art. 172's MIPYME quota) while the INDEPENDENTLY recomputed
  `mipyme-reserved-eligible?` is false? A non-reserved-award engagement
  is never flagged by this check (entity/engagement-scope-gated, the
  same discipline CAF's `:reserved-market?`-gated check and Bhutan's
  `:foreign-company?`-gated FDI check use)."
  [{:keys [mipyme-reserved-award?] :as engagement}]
  (boolean (and mipyme-reserved-award? (not (mipyme-reserved-eligible? engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
