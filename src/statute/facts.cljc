(ns statute.facts
  "General-law compliance catalog for the Dominican Republic (DOM) --
  extends this repo's existing `marketentry.facts` (public-procurement
  market-entry only, narrow scope) with a second, orthogonal catalog of
  statutes a company operating in this jurisdiction must generally
  track for compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/
  -aze/-alb/-arm/-atg/-ben/-btn/-bwa/-caf's `statute.facts`
  (ADR-2607141700, cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL government-hosted URL -- never
  fabricated. Both entries below were curl/WebFetch-verified directly
  2026-07-22 (this session's WebSearch budget was exhausted; no
  DuckDuckGo HTML-search fallback was needed here because both laws
  were reachable by direct government-domain probing):

  - **Código de Trabajo (Labour Code), Ley núm. 16-92** -- this
    iteration independently fetched the Ministerio de Trabajo's OWN
    hosted primary text directly
    (`mt.gob.do/wp-content/uploads/2024/07/codigo_de_trabajo.pdf`,
    reached via the Ministry's own `sobre-nosotros/marco-legal-2/`
    page, NOT a secondary aggregator), a genuine machine-readable
    (Adobe InDesign-typeset, not scanned) 564-page PDF. Its own cover
    page reads, in full: 'CODIGO DE TRABAJO (LEY 16-92) ... MINISTERIO
    DE TRABAJO CODIGO DE TRABAJO DE LA REPUBLICA DOMINICANA (Ley 16-92
    Promulgada el 29 de mayo de 1992)' -- HIGH confidence, read
    directly off the Ministry's own primary text, not a citation of a
    citation. This iteration also specifically checked whether this
    citation is STALE: a Ministerio de Trabajo news item
    (`mt.gob.do/eddy-olivares-destaca-reforma-al-codigo-de-trabajo-...`,
    fetched directly, dated 2026-06-25) reports the Labour Minister
    speaking at a forum on labour-code reform ('Reforma al Código
    Laboral y Derechos del Trabajador') -- this is REFORM UNDER PUBLIC
    DISCUSSION, not an enacted replacement law; the article's own text
    describes a conference presentation, with no promulgated law
    number or repeal date given. This catalog therefore continues to
    cite Ley 16-92 as current and in force, the same discipline this
    family uses elsewhere of not assuming a law has been superseded
    without directly confirming a repeal provision (contrast the
    genuinely-confirmed Ley 340-06 -> Ley 47-25 procurement-law
    replacement this repo's `marketentry.facts` documents, where a
    dated Article 247 Derogación was actually read).
  - **Ley General de las Sociedades Comerciales y Empresas
    Individuales de Responsabilidad Limitada, Ley núm. 479-08
    (modificada por la Ley núm. 31-11)** -- this iteration confirmed
    the law's number, title and amending law directly from the
    Dirección General de Impuestos Internos' (DGII) own official page
    for registering 'Personas Jurídicas'
    (`dgii.gov.do/contribuyentesRegistrados/personasJuridicas/Paginas/default.aspx`,
    fetched directly), whose own text reads that commercial entities
    'constituyen de acuerdo a las disposiciones de la Ley No. 479-08
    sobre sociedades comerciales y empresas individuales de
    responsabilidad limitada (Modificada por la Ley 31-11)' and lists
    the seven legal forms with legal personality it recognizes
    (Sociedad Anónima, Sociedad Anónima Simplificada, Sociedad de
    Responsabilidad Limitada, Sociedad en Nombre Colectivo, Sociedad
    en Comandita Simple, Sociedad en Comandita por Acciones, Empresa
    Individual de Responsabilidad Limitada). This iteration did NOT
    independently fetch Ley 479-08's OWN primary legislative text this
    session -- every candidate official host tried returned a dead
    end: `micm.gob.do` (Ministerio de Industria, Comercio y MIPYMES,
    the ministry with policy responsibility for this law) returned
    HTTP 403 on every direct fetch attempt (curl and WebFetch alike);
    `simv.gob.do` (Superintendencia del Mercado de Valores, which
    regulates Sociedades Anónimas that issue public securities)
    likewise returned HTTP 403; the Consultoría Jurídica del Poder
    Ejecutivo's own law database (`consultoria.gov.do/consulta/`,
    confirmed to be a genuine, real, working 'Leyes y Decretos' search
    tool by title/number/date) is an ASP.NET AJAX form gated by a
    server-issued anti-forgery token this iteration could not complete
    a query through in this session; `camarasantodomingo.do` (the
    Cámara de Comercio y Producción de la República Dominicana, the
    Registro Mercantil authority) is a client-side-rendered
    single-page app whose `/registro-mercantil/requisitos` route
    (confirmed real via the site's own `sitemap.xml`, not guessed)
    returned no crawlable citation text to either curl or WebFetch.
    This is an honestly-reported GAP in HOW DEEP this iteration could
    verify (the law's number/title/amendment is HIGH confidence, an
    official .gov.do page's own primary text, not fabricated or
    copied from a sibling repo's citation) rather than a claim this
    iteration read the Act's own articles -- the same MODERATE/HIGH
    confidence split this family's siblings use (e.g. CAF's AUSCGIE
    entry, MODERATE on exact treaty-article wording, HIGH on the
    Act's own adoption/entry-into-force dates).

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"DOM"
   [{:statute/id "dom.codigo-trabajo"
     :statute/title "Código de Trabajo de la República Dominicana"
     :statute/jurisdiction "DOM"
     :statute/kind :law
     :statute/law-number "Ley núm. 16-92, promulgada el 29 de mayo de 1992 (read directly off the Ministerio de Trabajo's own hosted primary-text cover page; a labour-code reform is under public discussion as of a 2026-06-25 Ministry-hosted forum but is NOT yet a promulgated replacement, so this remains the current, in-force citation)"
     :statute/url "https://mt.gob.do/wp-content/uploads/2024/07/codigo_de_trabajo.pdf"
     :statute/url-provenance :official-ministerio-de-trabajo
     :statute/enacted-date "1992-05-29"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor}}
    {:statute/id "dom.ley-sociedades-comerciales"
     :statute/title "Ley General de las Sociedades Comerciales y Empresas Individuales de Responsabilidad Limitada"
     :statute/jurisdiction "DOM"
     :statute/kind :law
     :statute/law-number "Ley núm. 479-08, modificada por la Ley núm. 31-11 (title/number/amendment confirmed directly from DGII's own 'Personas Jurídicas' registration page, which cites this Act as the basis for the seven commercial legal forms it recognizes -- Sociedad Anónima, Sociedad Anónima Simplificada, Sociedad de Responsabilidad Limitada, Sociedad en Nombre Colectivo, Sociedad en Comandita Simple, Sociedad en Comandita por Acciones, Empresa Individual de Responsabilidad Limitada; this iteration did NOT independently fetch the Act's own primary legislative text this session -- every candidate official host tried (MICM, SIMV, the Consultoría Jurídica law database, the Cámara de Comercio's own site) was unreachable/blocked/un-crawlable in this session, see namespace docstring -- HIGH confidence on the law's own number/title/amendment as stated by DGII's official page, not on the Act's own article text)"
     :statute/url "https://dgii.gov.do/contribuyentesRegistrados/personasJuridicas/Paginas/default.aspx"
     :statute/url-provenance :official-dgii
     :statute/enacted-date "2008 (exact promulgation day not independently confirmed this session -- the '-08' in the law's own number confirms the year; DGII's own page states the number and amending law but not a day-level date, and this iteration could not reach the Act's own primary text to read a promulgation date directly, see namespace docstring)"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-dom statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "DOM")) " DOM statute(s) seeded with an "
                 "official citation. Extend `statute.facts/catalog`, never "
                 "fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :corporate-governance)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
