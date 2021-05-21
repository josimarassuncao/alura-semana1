(ns semana1.reports.logic
  (:require [datomic.api :as d]
            [semana1.creditcard.db.db :as cc.db]
            [semana1.customer.db.db :as cust.db]
            [semana1.orders.db.db :as o.db]))

;; Merging all data in a unified list
(defn append-card-info
  [order]
  (->> order
       (:order/payment-ref)
       (:credit-card/id)
       (cc.db/get-card-info)
       (assoc order :payment-details)
       ))

(defn append-orders-info
  [customer]
  (->> (:customer/id customer)
       (o.db/get-orders-by-customer)
       (map append-card-info)
       (#(assoc customer :orders %))
       ))

(defn merge-all-data
  []
  (->> (cust.db/get-all-customers)
       (map append-orders-info)
       (sort-by :customer/name)
       ))

(defn resume-customer
  [customer]
  (fn [value] {:customer/id (:customer/id customer) :customer/name (:customer/name customer) :total value})
  )

(defn total-per-customer
  [customer]
  (->> (:orders customer [])
       (map #(:order/total-price % 0))
       (reduce +)
       ;(println)
       ((resume-customer customer))
       ))

(defn resume-category
  [category]
  (fn [value] {:order/category category :total value})
  )

(defn sum-categories
  [[key list]]
  (->> list
       (map :order/total-price)
       (reduce +)
       ((resume-category key))
       ))

;; Amount spent in a month Y by customer X
(defn get-month-from-order
  [order-info]
  (subs (:order/bought-at order-info) 0 7))

(defn only-month-total
  [month]
  ;(println month)
  (fn [total] {:month month, :total total})
  )

(defn resume-monthly-customer-expense
  [[month orders]]
  (->> orders
       (map #(:order/total-price % 0))
       (flatten)
       (reduce +)
       ((only-month-total month))
       ))

(defn resume-customer-months
  [customer]
  (fn [months-data]
    {
     :customer/id   (:customer/id customer)
     :customer/name (:customer/name customer)
     :months        months-data
     }))

(defn organize-expenses-per-month
  [customer]
  (->> (:orders customer)
       (group-by get-month-from-order)
       (map resume-monthly-customer-expense)
       ((resume-customer-months customer))
       ))

(defn report-grouped-by-category
  [full-list]
  (->> full-list
       (map :orders)
       (flatten)
       (#(group-by :order/category %))
       (map sum-categories)
       (sort-by :order/category)
       ))

(defn amount-spent-per-month-per-customer
  [full-list]
  (->> full-list
       (map organize-expenses-per-month)
       ))

(defn get-customer-month-expense
  [spent-per-month cust-id] (->> spent-per-month
                                 (filter #(= (:customer/id %) cust-id))
                                 ))

(defn resume-month-customer
  [list-data]
  (fn [month-data]
    {
     :customer/id   (:customer/id list-data)
     :customer/name (:customer/name list-data)
     :expense       month-data
     }
    ))

(defn get-month-value-for-customer
  [spent-per-month cust-id month]
  (let [monthly-list (first (get-customer-month-expense spent-per-month cust-id))]
    (->> monthly-list
         (:months)
         (filter #(= (:month %) month))
         (first)
         ((resume-month-customer monthly-list))
         )))

;; Search for orders by establishment and by amount of the order
(defn orders-between-prop
  [full-list prop val-from val-to]
  (->> full-list
       (map :orders)
       (flatten)
       (filter #(>= (compare (prop %) val-from) 0))
       (filter #(<= (compare (prop %) val-to) 0))
       (sort-by prop)
       ))

(defn orders-between-date
  [full-list date-from date-to]
  (orders-between-prop full-list :order/bought-at date-from date-to)
  )

(defn orders-between-values
  [full-list amount-from amount-to]
  (orders-between-prop full-list :order/total-price amount-from amount-to)
  )

(defn establishment-orders
  [full-list merchant-name]
  (orders-between-prop full-list :order/establishment merchant-name merchant-name)
  )

(defn format-value-list
  "organises high priced orders with list of customer"
  [prop query-result]
  {prop (prop (first query-result))
    :customers (map :customer query-result)})

(defn customers-highest-purchase-value
  "retrieves the customers who shared the highest value for an order"
  []
  (->> (o.db/get-highest-priced-orders)
       (format-value-list :high-value)))

(defn customers-lowest-purchase-value
  "retrieves the customers who shared the lowest value for an order"
  []
  (->> (o.db/get-lowest-priced-orders)
       (format-value-list :low-value)))
