(ns semana1.core
  (:require [semana1.creditcard.db.db :as cc.db]
            [semana1.customer.db.db :as cust.db]
            [semana1.orders.db.db :as o.db]))

(defn start-dbs! []
  "starts environment"
  (cc.db/init-data)
  (cust.db/init-data)
  (o.db/init-data)
  (println "dbs started"))

;; Merging all data in a unified list
(defn append-card-info
  [order]
  (->> order
       (get-in order [:payment])
       (#(cc.db/get-card-info (:ref %)))
       ;(println)
       (#(assoc (:payment order) :details %1))
       (#(assoc order :payment %1))
       ;(println)
       )
  )

(defn append-orders-info
  [customer]
  ;(println customer)
  (->> (:customer-id customer)
       ;(println)
       (o.db/get-orders-by-customer)
       (map append-card-info)
       (#(assoc customer :orders %1))
       ;(println)
       ))

(defn merge-all-data
  []
  (->> (cust.db/get-all-customers)
       (map append-orders-info)
       (sort-by :name))
  )

(def full-list (merge-all-data))

;; Getting data from one customer
(println "\nCustomer 200")
(println (->> full-list
              (filter #(= (:customer-id %) 200))))

(println "\nCostumer 201")
(println (->> full-list
              (filter #(= (:customer-id %) 201))))

(println "\nCostumer 831")
(println (->> full-list
              (filter #(= (:customer-id %) 831))))

;; Total per customer
(println "\nTotal spent per customer")
(defn resume-customer
  [customer]
  ;(println "parametro - " customer)
  (fn [value] {:customer-id (:customer-id customer) :name (:name customer) :total value})
  )

(defn total-per-customer
  [customer]
  ;(println customer)
  (->> (:orders customer [])
       (map #(:total-price % 0))
       (reduce +)
       ;(println)
       ((resume-customer customer))
       ))

(def customer-total-report
  (->> full-list
       (map total-per-customer)
       ;(println)
       ))

(println customer-total-report)

;; Orders per category
(println "\nAll orders summed per category")
(defn resume-category
  [category]
  (fn [value] {:category category :total value})
  )

(defn sum-categories
  [[key list]]
  (->> list
       (map :total-price)
       (reduce +)
       ((resume-category key))
       ))

(def category-total-report
  (->> full-list
       (map :orders)
       (flatten)
       (#(group-by :category %))
       (map sum-categories)
       (sort-by :category)
       ))
(println category-total-report)

;; Amount spent in a month Y by customer X
(defn get-month-from-order
  [order-info]
  (subs (:bought_at order-info) 0 7))

(defn only-month-total
  [month]
  ;(println month)
  (fn [total] {:month month, :total total})
  )

(defn resume-monthly-customer-expense
  [[month orders]]
  ;((println month "-" orders))
  (->> orders
       ;(flatten)
       (map #(:total-price % 0))
       (flatten)
       ;(#(println month "-" (class %) "-" %))
       (reduce +)
       ;(println)
       ((only-month-total month))
       ))

(defn resume-customer-months
  [customer]
  (fn [months-data]
    {
     :customer-id (:customer-id customer)
     :name (:name customer)
     :months months-data
     }))

(defn organize-expenses-per-month
  [customer]
  (->> (:orders customer)
       (group-by get-month-from-order)
       (map resume-monthly-customer-expense)
       ((resume-customer-months customer))
       ))

(defn amount-spent-per-month-per-customer
  []
  (->> full-list
       (map organize-expenses-per-month)
       ))

(def spent-per-month (amount-spent-per-month-per-customer))
(println "\nTotal spent per month per customer")
;(println spent-per-month)

(defn get-customer-month-expense
  [cust-id] (
       ->> spent-per-month
           (filter #(= (:customer-id %) cust-id))
     ))

;(println (get-customer-month-expense 301))
;(println (get-customer-month-expense 235))

(defn resume-month-customer
  [list-data]
  (fn [month-data]
    {
     :customer-id (:customer-id list-data)
     :name (:name list-data)
     :expense month-data
     })
  )

(defn get-month-value-for-customer
  [cust-id month]
  ( let [monthly-list (first (get-customer-month-expense cust-id))]
   (->> monthly-list
        (:months)
        (filter #(= (:month %) month))
        (first)
        ((resume-month-customer monthly-list))
        )))

;(println (get-customer-month-expense 411))
(println (get-month-value-for-customer 411 "2021-04"))

;; Search for orders by establishment and by amount of the order


(defn -main [& args]
  (println "starting service...")
  (start-dbs!))
