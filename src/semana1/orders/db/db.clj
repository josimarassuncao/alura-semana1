(ns semana1.orders.db.db)

(def list-of-orders [])

(def default-list [{
                      :customer-id #uuid"d3996403-e4c3-46e9-935f-1d4b47df50ae", :order-id 544321,
                      :bought_at "2021-05-01", :total-price 123.00,
                      :establishment "Mercado D'Avó", :category "Market",
                      :payment {:by "CC" :ref 1774 }
                    },
                   {
                    :customer-id #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9", :order-id 131298,
                    :bought_at "2021-05-03", :total-price 91.97,
                    :establishment "Renner", :category "Clothes",
                    :payment {:by "CC" :ref 1637 }
                    },
                   {
                    :customer-id #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9", :order-id 907421,
                    :bought_at "2021-04-12", :total-price 1515.00,
                    :establishment "Nike", :category "Sport",
                    :payment {:by "CC" :ref 1637 }
                    },
                   {
                    :customer-id #uuid"e42dca1e-69db-45bb-88d5-5225a49ebed0", :order-id 123425,
                    :bought_at "2021-05-11", :total-price 12.98,
                    :establishment "Extra", :category "Market",
                    :payment {:by "CC" :ref 3104 }
                    },
                   {
                    :customer-id #uuid"63c03785-9b0f-46b8-be86-8a072a99439f", :order-id 765920,
                    :bought_at "2021-05-12", :total-price 59.99,
                    :establishment "Mercado D'Avó", :category "Market",
                    :payment {:by "CC" :ref 1534 }
                    },
                   {
                    :customer-id #uuid"63c03785-9b0f-46b8-be86-8a072a99439f", :order-id 135425,
                    :bought_at "2021-04-05", :total-price 515.98,
                    :establishment "Extra", :category "Market",
                    :payment {:by "CC" :ref 3104 }
                    }
                   ])

(defn get-all-orders []
  "returns the whole list of customers"
  list-of-orders)

(defn filter-by-customer-id
  "mounts and returns a fn to compare the value id with :customer-id property of a <future value>"
  [id]
  (fn [item] (= (:customer-id item) id)))

(defn get-orders-by-customer
  "retrieves the orders from a customer"
  [customer-id]
  (->> list-of-orders
       (filter (filter-by-customer-id customer-id))))

(defn init-entity!
  "starts the data to test the movements"
  [conn]
  (def list-of-orders default-list))
