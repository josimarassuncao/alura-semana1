(ns semana1.orders.db.db
  (:require [datomic.api :as d]))

(def conn nil)

(def schema [{:db/ident       :order/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "The order id to use throughout the application"}
             {:db/ident       :order/customer
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/doc         "The customer who have made the order"}
             {:db/ident       :order/bought-at
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The data when the order happened"}
             {:db/ident       :order/total-price
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "The total value of the order"}
             {:db/ident       :order/establishment
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Place and merchant where the order occurred"}
             {:db/ident       :order/category
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Defines the category of the purchase"}
             ; TODO: I could not use it as a sort of ValueObject
             ;{:db/ident       :order/payment
             ; :db/valueType   :db.type/ref
             ; :db/cardinality :db.cardinality/many
             ; :db/doc         "Describes how the payment of the order happened"}
             {:db/ident       :order/payment-by
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Indicates how payment had occurred for the order"}
             {:db/ident       :order/payment-ref
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/doc         "Reference for the credit card used on the order"}
             ])

;; Previous order-id - current order/id
;; 544321 - #uuid"df78cdc4-4f61-4965-bece-c6d27ac7e30c"
;; 131298 - #uuid"98055656-84d5-4796-872a-a21ec6fff0d1"
;; 907421 - #uuid"c83a32b9-c4b4-4c88-92d3-a1d1c5b372e8"
;; 123425 - #uuid"cdf4cb00-c8de-4259-8a8b-719dbc1606a9"
;; 765920 - #uuid"10ecd706-35c0-4c17-9491-80098aae1c8d"
;; 135425 - #uuid"8b5baf65-1749-4cf5-8709-6f3810cfd807"
;; ------ - #uuid"4d652f49-37da-4175-9918-e93d9243096c"

(def default-data [{
                    :order/id            #uuid"df78cdc4-4f61-4965-bece-c6d27ac7e30c",
                    :order/customer      [:customer/id #uuid"d3996403-e4c3-46e9-935f-1d4b47df50ae"],
                    :order/bought-at     "2021-05-01", :order/total-price 123.00M,
                    :order/establishment "Mercado D'Avó", :order/category "Market",
                    :order/payment-by    "CC",
                    :order/payment-ref   [:credit-card/id #uuid"3bf97e13-8cb9-46a2-8070-5f0e777ded81"]
                    },
                   {
                    :order/id            #uuid"98055656-84d5-4796-872a-a21ec6fff0d1",
                    :order/customer      [:customer/id #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9"],
                    :order/bought-at     "2021-05-03", :order/total-price 91.97M,
                    :order/establishment "Renner", :order/category "Clothes",
                    :order/payment-by    "CC",
                    :order/payment-ref   [:credit-card/id #uuid"4cbadf39-e1f9-4922-95a0-759743d93604"]
                    },
                   {
                    :order/id            #uuid"c83a32b9-c4b4-4c88-92d3-a1d1c5b372e8",
                    :order/customer      [:customer/id #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9"],
                    :order/bought-at     "2021-04-12", :order/total-price 1515.00M,
                    :order/establishment "Nike", :order/category "Sport",
                    :order/payment-by    "CC",
                    :order/payment-ref   [:credit-card/id #uuid"4cbadf39-e1f9-4922-95a0-759743d93604"]
                    },
                   {
                    :order/id            #uuid"cdf4cb00-c8de-4259-8a8b-719dbc1606a9",
                    :order/customer      [:customer/id #uuid"e42dca1e-69db-45bb-88d5-5225a49ebed0"],
                    :order/bought-at     "2021-05-11", :order/total-price 12.98M,
                    :order/establishment "Extra", :order/category "Market",
                    :order/payment-by    "CC",
                    :order/payment-ref   [:credit-card/id #uuid"4940b83b-9ea1-4e73-ac7e-95b0dd116d50"]
                    },
                   {
                    :order/id            #uuid"10ecd706-35c0-4c17-9491-80098aae1c8d",
                    :order/customer      [:customer/id #uuid"63c03785-9b0f-46b8-be86-8a072a99439f"],
                    :order/bought-at     "2021-05-12", :order/total-price 59.99M,
                    :order/establishment "Mercado D'Avó", :order/category "Market",
                    :order/payment-by    "CC",
                    :order/payment-ref   [:credit-card/id #uuid"cd335e7b-9d79-4ffb-84e3-05cd8514ba20"]
                    },
                   {
                    :order/id            #uuid"8b5baf65-1749-4cf5-8709-6f3810cfd807",
                    :order/customer      [:customer/id #uuid"63c03785-9b0f-46b8-be86-8a072a99439f"],
                    :order/bought-at     "2021-04-05", :order/total-price 515.98M,
                    :order/establishment "Extra", :order/category "Market",
                    :order/payment-by    "CC",
                    :order/payment-ref   [:credit-card/id #uuid"4940b83b-9ea1-4e73-ac7e-95b0dd116d50"]
                    },
                   {
                    :order/id            #uuid"4d652f49-37da-4175-9918-e93d9243096c",
                    :order/customer      [:customer/id #uuid"63c03785-9b0f-46b8-be86-8a072a99439f"],
                    :order/bought-at     "2021-04-07", :order/total-price 1515.00M,
                    :order/establishment "Negreiros", :order/category "Market",
                    :order/payment-by    "CC",
                    :order/payment-ref   [:credit-card/id #uuid"4940b83b-9ea1-4e73-ac7e-95b0dd116d50"]
                    }
                   ])

(defn init-entity!
  "starts the data to test the movements"
  [db-conn]
  (alter-var-root #'conn (constantly db-conn))
  @(d/transact conn schema)
  @(d/transact conn default-data))

(defn get-all-orders
  "returns the whole list of customers"
  []
  (->> (d/q '[:find (pull ?order [*])
              :where [?order :order/id]]
            (d/db conn))
       (map first)))

(defn filter-by-customer-id
  "mounts and returns a fn to compare the value id with :order/customer property of a <future value>"
  [id]
  (fn [item] (= (:order/customer item) id)))

(defn get-orders-by-customer
  "retrieves the orders from a customer"
  [customer-id]
  (->> (d/q '[:find (pull ?order [*, {:order/payment-ref [:credit-card/id]}])
              :in $ ?id-to-search
              :where [?customer :customer/id ?id-to-search]
              [?order :order/customer ?customer]]
            (d/db conn) customer-id)
       (map first)))

(defn get-customer-with-most-orders
  "retrieves the customer with the most number of orders"
  []
  (->> (d/q '[:find (pull ?customer [:customer/name :customer/id] :as customer), (count ?order)
          :keys customer, quantity
          :where [?order :order/customer ?customer]
          ] (d/db conn))
       (sort-by :quantity)
       (last)))

(defn get-highest-priced-orders
  "retrieves the orders most priced"
  []
  (d/q '[:find ?high-priced, (pull ?customer [:customer/name :customer/email])
         :keys :high-value, :customer
         :where [(q '[:find (max ?high-priced)
                      :where [_ :order/total-price ?high-priced]
                      ] $)  [[ ?high-priced ]]]
         [?order :order/total-price ?high-priced]
         [?order :order/customer ?customer]]
       (d/db conn))
  )

(defn get-lowest-priced-orders
  "retrieves the orders most priced"
  []
  (d/q '[:find ?low-priced, (pull ?customer [:customer/name :customer/email])
         :keys :low-value, :customer
         :where [(q '[:find (min ?low-priced)
                      :where [_ :order/total-price ?low-priced]
                      ] $)  [[ ?low-priced ]]]
         [?order :order/total-price ?low-priced]
         [?order :order/customer ?customer]]
       (d/db conn))
  )
