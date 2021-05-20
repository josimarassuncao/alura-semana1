(ns semana1.creditcard.db.db
  (:require [datomic.api :as d]))

(def conn nil)
(def list-of-cards [])

(def schema [{:db/ident       :credit-card/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "The internal identity of a card - to be used inside the application"}
             {:db/ident       :credit-card/number
              :db/valueType   :db.type/tuple
              :db/tupleTypes  [:db.type/long :db.type/long :db.type/long :db.type/long]
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "The unique number of the credit card - to be used out of the application"}
             {:db/ident       :credit-card/cvv
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "The number to validate a certain card number"}
             {:db/ident       :credit-card/expires-at
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The date where the credit card becomes invalid"}
             {:db/ident       :credit-card/declared-limit
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "The limit defined for the credit card"}
             ])

(defn uuid
  []
  (java.util.UUID/randomUUID))

;; Previous card :number - current card number
;; 1534 112 2026-02 - [5148 7572 5686 0922] 200 2023-04-20
;; 1637 210 2029-03 - [5427 1395 4814 2967] 888 2022-12-20
;; 1774 491 2021-10 - [5279 0883 6158 8328] 718 2022-01-20
;; 1904 326 2022-01 - [5207 5199 3492 1569] 352 2023-02-20
;; 3104 536 2022-01 - [5196 7300 0367 7635] 181 2021-12-20

(def default-list [
                   {:credit-card/id #uuid"cd335e7b-9d79-4ffb-84e3-05cd8514ba20", :credit-card/number [5148 7572 5686 922], :credit-card/cvv 200, :credit-card/expires-at "2023-04-20", :credit-card/declared-limit 50M}
                   {:credit-card/id #uuid"4cbadf39-e1f9-4922-95a0-759743d93604", :credit-card/number [5427 1395 4814 2967], :credit-card/cvv 888, :credit-card/expires-at "2022-12-20", :credit-card/declared-limit 520M}
                   {:credit-card/id #uuid"3bf97e13-8cb9-46a2-8070-5f0e777ded81", :credit-card/number [5279 883 6158 8328], :credit-card/cvv 718, :credit-card/expires-at "2022-01-20", :credit-card/declared-limit 140M}
                   {:credit-card/id #uuid"8ad8d180-bc82-4115-9859-e7cd03966edd", :credit-card/number [5207 5199 3492 1569], :credit-card/cvv 352, :credit-card/expires-at "2023-02-20", :credit-card/declared-limit 1650M}
                   {:credit-card/id #uuid"4940b83b-9ea1-4e73-ac7e-95b0dd116d50", :credit-card/number [5196 7300 0367 7635], :credit-card/cvv 181, :credit-card/expires-at "2021-12-20", :credit-card/declared-limit 1650M}
                   ])

(defn get-all-cards []
  "returns the whole list of credit cards"
  (->> (d/q '[:find (pull ?creditcard [*])
              :where [?creditcard :credit-card/id]]
            (d/db conn))
       (map first)))

(defn get-card-info
  "returns a card info based on the number"
  [card-number]
  (->> (d/q '[:find (pull ?creditcard [*])
              :in $ ?find-card
              :where [?creditcard :credit-card/number ?find-card]]
            (d/db conn) card-number)
       (ffirst)))

(defn init-entity!
  "starts the data to test the movements"
  [db-conn]
  (def conn db-conn)
  @(d/transact conn schema)
  @(d/transact conn default-list)
  (def list-of-cards default-list))
