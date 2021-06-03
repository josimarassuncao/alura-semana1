(ns store.customer.db.db
  (:require [datomic.api :as d]))

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

(defn add-data!
  [data]
  @(d/transact conn data))

(defn get-all-customers
  "returns the whole list of customers"
  []
  (->> (d/q '[:find (pull ?entity [*, {:order/customer [:order/id]} :as :orders])
              :where [?entity :customer/id]] (d/db conn))
       (map first)))

(defn get-customer-info
  "retrieves customer info using id"
  [id]
  (->> (d/q '[:find (pull ?entity [*])
              ;:keys customer
              :in $ ?filter-id
              :where [?entity :customer/id ?filter-id]] (d/db conn) id)
       ffirst))

(defn get-customer-with-no-order
  "retrieves customer that has no orders"
  []
  (->> (d/q '[:find (pull ?customer  [:customer/name :customer/email])
              :where [?customer :customer/id]
              (not [_ :order/customer ?customer])]
            (d/db conn))
       (map first)))

(defn init-entity!
  "starts the data to test the movements"
  [db-conn]
  ; TODO: This is something to think about, how to share a certain important component with other parts of the application?
  (alter-var-root #'conn (constantly db-conn))
  @(d/transact conn schema))
