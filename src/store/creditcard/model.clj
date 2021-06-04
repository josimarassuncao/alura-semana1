(ns store.creditcard.model
  (:require [schema.core :as s]
            [clojure.edn :as edn]))

(s/defn uuid :- s/Uuid
  []
  (java.util.UUID/randomUUID))

(defn- size-4?
  [coll]
  (= 4 (count coll)))

(defn- between-0099-9999?
  [coll]
  (->> coll
      (filter (fn [n] (<= 10 n 9999)))
       (count)
       (= 4)))

(defn- card-number-rules?
  [coll]
  (and (size-4? coll) (between-0099-9999? coll))
  )

;(s/defschema CardNumberSchema [s/Int s/Int s/Int s/Int])
(s/defschema CardNumberSchema (s/constrained [s/Int] card-number-rules?))

(s/defschema CreditCardSchema
  {:credit-card/id            s/Uuid
  :credit-card/number         CardNumberSchema
  :credit-card/cvv            s/Int
  :credit-card/expires-at     s/Str
  :credit-card/declared-limit s/Num})

(s/defn build-new-creditcard :- CreditCardSchema
  "builds a new creditcard data map"
  ([number-list :- CardNumberSchema
    cvv :- s/Int
    expiration :- s/Str
    limit :- s/Num] (build-new-creditcard (uuid) number-list cvv expiration limit))
  ([id :- s/Uuid
    number-list :- CardNumberSchema
    cvv :- s/Int
    expiration :- s/Str
    limit :- s/Num] {:credit-card/id             id,
                     :credit-card/number         number-list,
                     :credit-card/cvv            cvv,
                     :credit-card/expires-at     expiration,
                     :credit-card/declared-limit limit
                     }))

(s/defn creditcard->str :- s/Str
  "turns a customer data to string"
  [creditcard :- CreditCardSchema]
  (pr-str creditcard))

(s/defn str->creditcard :- CreditCardSchema
  "turns a customer string into a customer data"
  [creditcard-str]
  (edn/read-string creditcard-str))
