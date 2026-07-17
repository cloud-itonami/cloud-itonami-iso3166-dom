(ns culture.facts
  "Country-level regional-culture catalog for the Dominican Republic (DOM)
  -- national dishes, protected products, beverages, crafts, festivals and
  heritage sites, per ADR-2607171400 addendum 2 (cloud-itonami-
  municipality-culture-catalog Wave 1, in com-junkawasaki/root). First
  facts namespace in this blueprint-stage repo; the marketentry/statute
  catalogs land with :implemented (ADR-2607141700). City-level
  counterparts live in the cloud-itonami-municipality-* repos. (The
  Dominican Republic -- not Dominica, which is DMA.)

  Catalog is keyed by UPPERCASE ISO3 (mirrors the fleet's `statute.facts`
  convention); entries carry no :culture/municipality (that attribute is
  city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"DOM"
   [{:culture/id "dom.dish.mangu"
     :culture/name "Mangú"
     :culture/country "DOM"
     :culture/kind :dish
     :culture/summary "Mashed boiled green plantains topped with pickled red onions, typically served with fried cheese, salami and eggs; the national breakfast dish of the Dominican Republic, with origins linked to West African fufu."
     :culture/url "https://en.wikipedia.org/wiki/Mang%C3%BA"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dom.dish.sancocho"
     :culture/name "Sancocho"
     :culture/country "DOM"
     :culture/kind :dish
     :culture/summary "Traditional stew of Caribbean and Latin American cuisines whose Latin variations are popular national dishes in the Dominican Republic and several other countries."
     :culture/url "https://en.wikipedia.org/wiki/Sancocho"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dom.dish.habichuelas-con-dulce"
     :culture/name "Habichuelas con dulce"
     :culture/country "DOM"
     :culture/kind :dish
     :culture/summary "Sweet bean liquid dessert of red beans, coconut milk, evaporated milk and spices, from the Dominican Republic and especially popular around the Easter holiday."
     :culture/url "https://en.wikipedia.org/wiki/Habichuelas_con_dulce"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dom.beverage.mama-juana"
     :culture/name "Mama Juana"
     :culture/country "DOM"
     :culture/kind :beverage
     :culture/summary "Spiced alcoholic beverage made by infusing rum, red wine and honey with tree bark and herbs; it originates in the Dominican Republic."
     :culture/url "https://en.wikipedia.org/wiki/Mama_Juana"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dom.product.dominican-amber"
     :culture/name "Dominican amber"
     :culture/country "DOM"
     :culture/kind :product
     :culture/summary "Amber from the Dominican Republic derived from resin of the extinct tree Hymenaea protera; nearly always transparent, with a higher number of fossil inclusions than Baltic amber, and including the rare blue amber."
     :culture/url "https://en.wikipedia.org/wiki/Dominican_amber"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dom.festival.dominican-carnival"
     :culture/name "Carnival in the Dominican Republic"
     :culture/country "DOM"
     :culture/kind :festival
     :culture/summary "Carnival celebrated in most cities and towns of the Dominican Republic, with festivities throughout February coinciding with Independence Day; evidence from La Vega Vieja shows it has been celebrated since 1510."
     :culture/url "https://en.wikipedia.org/wiki/Carnival_in_the_Dominican_Republic"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dom.heritage.ciudad-colonial"
     :culture/name "Ciudad Colonial"
     :culture/name-local "Colonial City of Santo Domingo"
     :culture/country "DOM"
     :culture/kind :heritage
     :culture/summary "Historic central neighbourhood of Santo Domingo, the oldest continuously inhabited European-established settlement in the Americas; designated a UNESCO World Heritage Site in 1990."
     :culture/url "https://en.wikipedia.org/wiki/Ciudad_Colonial_(Santo_Domingo)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-dom culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "DOM"))
                 " DOM entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
