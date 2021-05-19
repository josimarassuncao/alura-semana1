(ns semana1.core
  (:require [semana1.reports.logic :as r]))

(defn -main [& args]
  (println "starting service...")
  (r/start-dbs!))

(-main)

;; Getting all data and merging into one list
(def full-list (r/merge-all-data))

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
(def customer-total-report
  (->> full-list
       (map r/total-per-customer)
       ;(println)
       ))

(println customer-total-report)
;;Total spent per customer
;;({:customer-id 301, :name Angelo, :total 0} {:customer-id 235, :name Erika, :total 123.0} {:customer-id 234, :name Hellen, :total 12.98} {:customer-id 831, :name Jose, :total 0} {:customer-id 411, :name Maria, :total 575.97} {:customer-id 200, :name Marta, :total 1606.97})

;; Orders per category
(println "\nAll orders summed per category")
(def category-total-report
  (->> full-list
       (map :orders)
       (flatten)
       (#(group-by :category %))
       (map r/sum-categories)
       (sort-by :category)
       ))
(println category-total-report)
;;All orders summed per category
;;({:category Clothes, :total 91.97} {:category Market, :total 711.95} {:category Sport, :total 1515.0})

(def spent-per-month (r/amount-spent-per-month-per-customer full-list))
(println "\nTotal spent per month per customer")
;(println spent-per-month)

;(println (get-customer-month-expense 411))
(println (r/get-month-value-for-customer spent-per-month 411 "2021-04"))
;;Total spent per month per customer
;;{:customer-id 411, :name Maria, :expense {:month 2021-04, :total 515.98}}

(def may-1-to-11 (r/orders-between-date full-list "2021-05-01" "2021-05-11"))
(println "\nReporting orders between May 1st and 11th")
(println may-1-to-11)
;;Reporting orders between May 1st and 11th
;;({:customer-id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer-id 200, :order-id 131298, :bought_at 2021-05-03, :total-price 91.97, :establishment Renner, :category Clothes, :payment {:by CC, :ref 1637, :details {:number 1637, :cvv 10, :expires_at 2029-03, :limit 520}}} {:customer-id 234, :order-id 123425, :bought_at 2021-05-11, :total-price 12.98, :establishment Extra, :category Market, :payment {:by CC, :ref 3104, :details {:number 3104, :cvv 36, :expires_at 2022-01, :limit 1650}}})

(println "\nReporting orders with total value between 100 and 900")
(def order-100-to-900 (r/orders-between-values full-list 100 900))
(println order-100-to-900)
;;Reporting orders with total value between 100 and 900
;;({:customer-id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer-id 411, :order-id 135425, :bought_at 2021-04-05, :total-price 515.98, :establishment Extra, :category Market, :payment {:by CC, :ref 3104, :details {:number 3104, :cvv 36, :expires_at 2022-01, :limit 1650}}})

(println "\nReporting orders from Mercado D'Avó")
(def davo-orders (r/establishment-orders full-list "Mercado D'Avó"))
(println davo-orders)
;;Reporting orders from Mercado D'Avó
;;({:customer-id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer-id 411, :order-id 765920, :bought_at 2021-05-12, :total-price 59.99, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1534, :details {:number 1534, :cvv 12, :expires_at 2026-02, :limit 50}}})
