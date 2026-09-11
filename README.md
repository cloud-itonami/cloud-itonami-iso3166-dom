# cloud-itonami-iso3166-dom

Open ISO 3166 Blueprint for **DOM**: Dominican Republic.

- DGCP (Dirección General de Contrataciones Públicas) / SECP (Sistema
  Electrónico de Contrataciones Públicas) public-procurement compliance,
  under the new Ley 47-25 de Contrataciones Públicas (28 July 2025) and
  its Reglamento de Aplicación, Decreto 52-26 (28 January 2026)
- RPE (Registro de Proveedores del Estado) supplier registration + RNC
  (DGII) tax registration + Registro Mercantil (Cámara de Comercio y
  Producción) business registration; Ley 47-25 Art. 172 MIPYME
  reserved-contract eligibility gate

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as `cloud-itonami-iso3166-caf`/`-btn`/`-bwa`:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the new (28
  July 2025) Ley 47-25 de Contrataciones Públicas, which repeals the
  old Ley 340-06 (Art. 247), and names DGCP (Dirección General de
  Contrataciones Públicas, Art. 8: órgano rector, dependiente del
  Ministerio de Hacienda) as the procurement authority; RPE (Registro
  de Proveedores del Estado) supplier registration, RNC (Dirección
  General de Impuestos Internos, per el Código Tributario Ley 11-92)
  tax registration, and Registro Mercantil (Cámara de Comercio y
  Producción) business registration. `governor.cljc`'s flagship check
  independently recomputes Ley 47-25 Art. 172's MIPYME
  reserved-contract eligibility (a mandatory >=30% procurement-plan
  budget-line reservation for MIPYMEs, 20 points general + 10 points
  earmarked for women-led MIPYMEs) -- a CERTIFICATION-GATED, DUAL-CLOCK
  eligibility test (MICM certification gate + women-led claim-
  consistency check + a 6-month fiscal-arrears grace clock + a
  12-month grave-sanction forfeiture clock, per Ley 47-25 Art. 172 and
  its Decreto 52-26 Arts. 222-223), a check shape genuinely different
  from every other iso3166 sibling's (see the namespace docstrings for
  the full research trail and honestly-narrowed scope, including facts
  this iteration could NOT verify, such as a citable Registro Mercantil
  law number and a separately-delegated RPE-specific reglamento).
  `facts.cljc` also POPULATES `rep-spec-basis` with Ley 47-25's own
  Art. 38/39 inhabilidades/prohibiciones (public-official conflict-of-
  interest exclusion, tiered 1-year/6-month by official rank plus
  consortium-contamination) -- unlike CAF/BTN/BWA's sampled siblings,
  which all leave this honestly nil.
- `src/statute/facts.cljk` -- general-law catalog: Ley 16-92 (Código de
  Trabajo, read directly off the Ministerio de Trabajo's own hosted
  primary text) and Ley 479-08 (Ley General de las Sociedades
  Comerciales, modificada por la Ley 31-11, cited via DGII's own
  official page -- this iteration did not independently fetch the
  Act's own primary text this session, an honest gap; see the
  namespace docstring).

Every citation is curl/WebFetch-verified against an official source
(dgcp.gob.do, dgii.gov.do, onapi.gob.do, mt.gob.do,
camarasantodomingo.do, formalizate.gob.do); the Ley 47-25 and Decreto
52-26 PDFs were downloaded and read directly (page 1 of each was also
rendered and zoomed as an image to independently confirm the law/
decree number beyond OCR-garble risk) -- see `marketentry.facts`'s
docstring for exactly which facts are HIGH-confidence (read directly
off primary text or a zoomed image) vs. an honestly-flagged gap (e.g.
the Registro Mercantil's own law number, and whether the RPE's own
delegated reglamento has since been issued).

## Culture catalog

This repo carries a **country-level regional-culture catalog**
(ADR-2607171400 addendum 2, `cloud-itonami-municipality-culture-catalog`
Wave 1, in `com-junkawasaki/root`) — national dishes, protected products,
beverages, crafts, festivals and heritage sites for the Dominican
Republic:

- `src/culture/facts.cljk` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring the fleet's `statute.facts` convention).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
