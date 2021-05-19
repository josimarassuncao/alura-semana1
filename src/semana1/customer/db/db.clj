(ns semana1.customer.db.db
  (:require [datomic.api :as d]))

(def list-of-customers [])

(def conn nil)

(def schema [{:db/ident       :customer/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "Identify the customer throughout the system"}
             {:db/ident       :customer/name
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The name of the customer"}
             {:db/ident       :customer/email
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "The email of the customer"}
             ])

(defn uuid
  []
  (java.util.UUID/randomUUID))

;; previous Id - new Id
;; 301 - 902d739c-1226-4485-a109-2678c6c7a58f
;; 411 - 63c03785-9b0f-46b8-be86-8a072a99439f
;; 831 - 3659e5b1-34ac-498a-a899-6eef06d441f7
;; 200 - f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9
;; 234 - e42dca1e-69db-45bb-88d5-5225a49ebed0
;; 235 - d3996403-e4c3-46e9-935f-1d4b47df50ae

(def default-list [
                   {:customer/id #uuid"902d739c-1226-4485-a109-2678c6c7a58f", :customer/name "Angelo", :customer/email "angelo@email.com"}
                   {:customer/id #uuid"63c03785-9b0f-46b8-be86-8a072a99439f", :customer/name "Maria", :customer/email "maria@email.com"}
                   {:customer/id #uuid"3659e5b1-34ac-498a-a899-6eef06d441f7", :customer/name "Jose", :customer/email "jose@email.com"}
                   {:customer/id #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9", :customer/name "Marta", :customer/email "marta@email.com"}
                   {:customer/id #uuid"e42dca1e-69db-45bb-88d5-5225a49ebed0", :customer/name "Hellen", :customer/email "hellen@email.com"}
                   {:customer/id #uuid"d3996403-e4c3-46e9-935f-1d4b47df50ae", :customer/name "Erika", :customer/email "erika@email.com"}
                   ])

(defn get-all-customers []
  "returns the whole list of customers"
  list-of-customers)

(defn get-all-customers-by-qry
  []
  (->> (d/q '[:find (pull ?entity [*])
          :where [?entity :customer/id]] (d/db conn))
       (map first)))

(defn get-customer-info
  "retrieves customer info using id"
  [id]
  (->> list-of-customers
       (filter #(= (:customer-id %) id))
       first))

(defn init-entity!
  "starts the data to test the movements"
  [db-conn]
  (def conn db-conn)
  (d/transact conn schema)
  @(d/transact conn default-list)
  (def list-of-customers default-list))


