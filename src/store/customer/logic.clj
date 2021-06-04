(ns store.customer.logic
  (:require [store.customer.model :as c.model]
            [store.customer.db.db :as c.db]))

(def ^:private started? false)

(def ^:private data-list [[#uuid"902d739c-1226-4485-a109-2678c6c7a58f" "Angelo" "angelo@email.com"]
                          [#uuid"63c03785-9b0f-46b8-be86-8a072a99439f" "Maria" "maria@email.com"]
                          [#uuid"3659e5b1-34ac-498a-a899-6eef06d441f7" "Jose" "jose@email.com"]
                          [#uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9" "Marta" "marta@email.com"]
                          [#uuid"e42dca1e-69db-45bb-88d5-5225a49ebed0" "Hellen" "hellen@email.com"]
                          [#uuid"d3996403-e4c3-46e9-935f-1d4b47df50ae" "Erika" "erika@email.com"]])

;; previous customer-id - new customer/id
;; 301 - 902d739c-1226-4485-a109-2678c6c7a58f
;; 411 - 63c03785-9b0f-46b8-be86-8a072a99439f
;; 831 - 3659e5b1-34ac-498a-a899-6eef06d441f7
;; 200 - f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9
;; 234 - e42dca1e-69db-45bb-88d5-5225a49ebed0
;; 235 - d3996403-e4c3-46e9-935f-1d4b47df50ae
(defn add-default-data!
  []
  (if-not started?
    (let [default-data (reduce
                         (fn [previous [id name email]]
                           (conj previous (c.model/build-new-customer id name email)))
                         []
                         data-list)]
      ;; Adds default data to datomic
      (c.db/add-data! default-data)
      (alter-var-root #'started? (constantly true))
      )))

(defn is-it-started? [] started?)
