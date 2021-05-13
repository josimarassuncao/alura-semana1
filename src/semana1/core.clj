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
;;Customer 200
;;({:customer-id 200, :name Marta, :email marta@email.com, :orders ({:customer-id 200, :order-id 131298, :bought_at 2021-05-03, :total-price 91.97, :establishment Renner, :category Clothes, :payment {:by CC, :ref 1637, :details {:number 1637, :cvv 10, :expires_at 2029-03, :limit 520}}} {:customer-id 200, :order-id 907421, :bought_at 2021-04-12, :total-price 1515.0, :establishment Nike, :category Sport, :payment {:by CC, :ref 1637, :details {:number 1637, :cvv 10, :expires_at 2029-03, :limit 520}}})})


(println "\nCostumer 201")
(println (->> full-list
              (filter #(= (:customer-id %) 201))))
;;Costumer 201
;;()

(println "\nCostumer 831")
(println (->> full-list
              (filter #(= (:customer-id %) 831))))
;;Costumer 831
;;({:customer-id 831, :name Jose, :email jose@email.com, :orders ()})

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
;;Total spent per customer
;;({:customer-id 301, :name Angelo, :total 0} {:customer-id 235, :name Erika, :total 123.0} {:customer-id 234, :name Hellen, :total 12.98} {:customer-id 831, :name Jose, :total 0} {:customer-id 411, :name Maria, :total 575.97} {:customer-id 200, :name Marta, :total 1606.97})


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
;;All orders summed per category
;;({:category Clothes, :total 91.97} {:category Market, :total 711.95} {:category Sport, :total 1515.0})

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
     }
    ))

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
;;Total spent per month per customer
;;{:customer-id 411, :name Maria, :expense {:month 2021-04, :total 515.98}}

;; Search for orders by establishment and by amount of the order
(defn orders-between-prop
  [prop val-from val-to]
  (->> full-list
       (map :orders)
       (flatten)
       ;(filter (comp #(>= (compare (:bought_at %) val-from) 0) #(<= (compare (:bought_at %) val-to) 0)))
       (filter #(>= (compare (prop %) val-from) 0))
       (filter #(<= (compare (prop %) val-to) 0))
       (sort-by prop)
       ;(map prop)
      ))

(defn orders-between-date
  [date-from date-to]
  (orders-between-prop :bought_at date-from date-to)
  )
(def may-1-to-11 (orders-between-date "2021-05-01" "2021-05-11"))

(println "\nReporting orders between May 1st and 11th")
(println may-1-to-11)
;;Reporting orders between May 1st and 11th
;;({:customer-id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer-id 200, :order-id 131298, :bought_at 2021-05-03, :total-price 91.97, :establishment Renner, :category Clothes, :payment {:by CC, :ref 1637, :details {:number 1637, :cvv 10, :expires_at 2029-03, :limit 520}}} {:customer-id 234, :order-id 123425, :bought_at 2021-05-11, :total-price 12.98, :establishment Extra, :category Market, :payment {:by CC, :ref 3104, :details {:number 3104, :cvv 36, :expires_at 2022-01, :limit 1650}}})

(println "\nReporting orders with total value between 100 and 900")
(defn orders-between-values
  [amount-from amount-to]
  (orders-between-prop :total-price amount-from amount-to)
  )

(def order-100-to-900 (orders-between-values 100 900))
(println order-100-to-900)
;;Reporting orders with total value between 100 and 900
;;({:customer-id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer-id 411, :order-id 135425, :bought_at 2021-04-05, :total-price 515.98, :establishment Extra, :category Market, :payment {:by CC, :ref 3104, :details {:number 3104, :cvv 36, :expires_at 2022-01, :limit 1650}}})

(println "\nReporting orders from Mercado D'Avó")
(defn establishment-orders
  [merchant-name]
  (orders-between-prop :establishment merchant-name merchant-name)
  )
(def davo-orders (establishment-orders "Mercado D'Avó"))
(println davo-orders)
;;Reporting orders from Mercado D'Avó
;;({:customer-id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer-id 411, :order-id 765920, :bought_at 2021-05-12, :total-price 59.99, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1534, :details {:number 1534, :cvv 12, :expires_at 2026-02, :limit 50}}})


(defn -main [& args]
  (println "starting service...")
  (start-dbs!))
