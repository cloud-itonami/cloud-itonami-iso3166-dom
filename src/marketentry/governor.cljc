(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Dominican Republic procurement law, whether a claimed
  engagement fee actually equals base + months x rate, whether the
  engagement's own declared MICM MIPYME certification and fiscal
  standing actually qualify it to bid on a contract it declares as
  reserved under Ley 47-25 Art. 172's MIPYME quota, whether an RNC
  (Registro Nacional de Contribuyentes) tax record has been verified
  for a filing that requires it, or when a draft stops being a draft
  and becomes a real-world dgcp.gob.do/comprasdominicana.gob.do
  submission, so this MUST be a separate system able to *reject* a
  proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. MIPYME-reserved ineligible  -- for `:filing/submit`, when the
                                       engagement declares
                                       `:mipyme-reserved-award? true`
                                       (i.e. it is bidding on a
                                       contract reserved under Ley
                                       47-25 Art. 172's MIPYME quota),
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       MICM certification, women-led
                                       sub-claim consistency, and
                                       fiscal-standing facts actually
                                       satisfy the Ley/Decreto's own
                                       eligibility criteria, and
                                       HARD-hold if not. FLAGSHIP
                                       genuinely new check for the
                                       iso3166 family (grep-verified
                                       absent as a governor check
                                       function name fleet-wide at
                                       build time) -- a
                                       CERTIFICATION-GATED, DUAL-CLOCK
                                       eligibility test (a boolean
                                       external-certification gate, a
                                       conditional claim-consistency
                                       cross-check, and two independent
                                       time-bounded grace/forfeiture
                                       windows), a check SHAPE
                                       genuinely different from every
                                       prior sibling's (turnover
                                       formula / flat threshold /
                                       boolean registry membership /
                                       3-tier value classification /
                                       bid-evaluation price adjustment
                                       / categorical sector-exclusion
                                       allow-list / CAF's
                                       workforce-composition-percentage
                                       OR-test).
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. RNC tax record unverified    -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-rnc-record? true`,
                                       INDEPENDENTLY check
                                       `:rnc-record-verified?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth. Grounded in
                                       the Dirección General de
                                       Impuestos Internos (DGII) and
                                       el Código Tributario (Ley No.
                                       11-92) -- see `marketentry.facts`.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(RPE登録/RNC税務記録/Registro Mercantil登録/MIPYME留保契約適格確認/代理人・inhabilidades確認等)が充足していない状態での提案"}]))))

(defn- mipyme-reserved-ineligible-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared MICM certification / women-led sub-claim /
  fiscal-standing facts satisfy Ley 47-25 Art. 172's MIPYME-reserved-
  contract eligibility criteria -- the flagship check this vertical
  adds. HARD-hold when the engagement declares
  `:mipyme-reserved-award? true` but is not independently eligible."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/mipyme-reserved-ineligible-claim? e)
        [{:rule :mipyme-reserved-ineligible
          :detail (str subject " はMIPYME留保契約(Ley 47-25 Art.172)への入札を宣言しているが、"
                      "独立再計算(MICM認証の有効性、女性主導サブクレームの整合性、"
                      "税務・社会保障未納期間6ヶ月以内、重大違反制裁後12ヶ月経過)"
                      "による適格性を満たさない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- rnc-record-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-rnc-record? true`, INDEPENDENTLY check
  `:rnc-record-verified?` -- CONDITIONAL on the engagement's own
  ground truth. Grounded in the Dirección General de Impuestos
  Internos (DGII), el Código Tributario (Ley No. 11-92)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-rnc-record? e))
                 (not (true? (:rnc-record-verified? e))))
        [{:rule :rnc-record-unverified
          :detail (str subject " はRNC(Registro Nacional de Contribuyentes)税務記録の確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (mipyme-reserved-ineligible-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (rnc-record-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
