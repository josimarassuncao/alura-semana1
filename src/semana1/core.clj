(ns semana1.core
  (:require [semana1.config.db :as config.db]
            [semana1.reports.logic :as r])
  (:use clojure.pprint))

(defn -main [& args]
  (println "starting service...")
  (config.db/start-db-and-connection!)
  (config.db/start-dbs!))

(-main)

;; Getting all data and merging into one list
(def full-list (r/merge-all-data))

;; Getting data from one customer
(println "\nCustomer Marta")
(def customer-marta #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9")
(println (->> full-list
              (filter #(= (:customer/id %) customer-marta))))
;;Customer 200
;;({:customer/id 200, :name Marta, :email marta@email.com, :orders ({:customer/id 200, :order-id 131298, :bought_at 2021-05-03, :total-price 91.97, :establishment Renner, :category Clothes, :payment {:by CC, :ref 1637, :details {:number 1637, :cvv 10, :expires_at 2029-03, :limit 520}}} {:customer/id 200, :order-id 907421, :bought_at 2021-04-12, :total-price 1515.0, :establishment Nike, :category Sport, :payment {:by CC, :ref 1637, :details {:number 1637, :cvv 10, :expires_at 2029-03, :limit 520}}})})

(println "\nCostumer non-existent")
(def customer-non-existent #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158babc")
(println (->> full-list
              (filter #(= (:customer/id %) customer-non-existent))))
;;Costumer 201
;;()

(println "\nCostumer Jose")
(def customer-Jose #uuid"3659e5b1-34ac-498a-a899-6eef06d441f7")
(println (->> full-list
              (filter #(= (:customer/id %) customer-Jose))))
;;Costumer 831
;;({:customer/id 831, :name Jose, :email jose@email.com, :orders ()})

;; Total per customer
(println "\nTotal spent per customer")
(def customer-total-report
  (->> full-list
       (map r/total-per-customer)
       ))

(println customer-total-report)
;;Total spent per customer
;;({:customer/id 301, :name Angelo, :total 0} {:customer/id 235, :name Erika, :total 123.0} {:customer/id 234, :name Hellen, :total 12.98} {:customer/id 831, :name Jose, :total 0} {:customer/id 411, :name Maria, :total 575.97} {:customer/id 200, :name Marta, :total 1606.97})

;; Orders per category
(println "\nAll orders summed per category")
(def category-total-report (r/report-grouped-by-category full-list))
(println category-total-report)
;;All orders summed per category
;;({:category Clothes, :total 91.97} {:category Market, :total 711.95} {:category Sport, :total 1515.0})

(def spent-per-month (r/amount-spent-per-month-per-customer full-list))
(println "\nTotal spent per month per customer")
;(println spent-per-month)

(def customer-Maria #uuid"63c03785-9b0f-46b8-be86-8a072a99439f")
;(println (get-customer-month-expense customer-Maria))
(println (r/get-month-value-for-customer spent-per-month customer-Maria "2021-04"))
;;Total spent per month per customer
;;{:customer/id 411, :name Maria, :expense {:month 2021-04, :total 515.98}}

(def may-1-to-11 (r/orders-between-date full-list "2021-05-01" "2021-05-11"))
(println "\nReporting orders between May 1st and 11th")
(println may-1-to-11)
;;Reporting orders between May 1st and 11th
;;({:customer/id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer/id 200, :order-id 131298, :bought_at 2021-05-03, :total-price 91.97, :establishment Renner, :category Clothes, :payment {:by CC, :ref 1637, :details {:number 1637, :cvv 10, :expires_at 2029-03, :limit 520}}} {:customer/id 234, :order-id 123425, :bought_at 2021-05-11, :total-price 12.98, :establishment Extra, :category Market, :payment {:by CC, :ref 3104, :details {:number 3104, :cvv 36, :expires_at 2022-01, :limit 1650}}})

(println "\nReporting orders with total value between 100 and 900")
(def order-100-to-900 (r/orders-between-values full-list 100 900))
(println order-100-to-900)
;;Reporting orders with total value between 100 and 900
;;({:customer/id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer/id 411, :order-id 135425, :bought_at 2021-04-05, :total-price 515.98, :establishment Extra, :category Market, :payment {:by CC, :ref 3104, :details {:number 3104, :cvv 36, :expires_at 2022-01, :limit 1650}}})

(println "\nReporting orders from Mercado D'Avó")
(def davo-orders (r/establishment-orders full-list "Mercado D'Avó"))
(println davo-orders)
;;Reporting orders from Mercado D'Avó
;;({:customer/id 235, :order-id 544321, :bought_at 2021-05-01, :total-price 123.0, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1774, :details {:number 1774, :cvv 91, :expires_at 2021-10, :limit 140}}} {:customer/id 411, :order-id 765920, :bought_at 2021-05-12, :total-price 59.99, :establishment Mercado D'Avó, :category Market, :payment {:by CC, :ref 1534, :details {:number 1534, :cvv 12, :expires_at 2026-02, :limit 50}}})

;; Customers who purchased more times
(println "\nReporting customers who bought more times")
(def most-purchases (r/customers-most-purchases))
(println most-purchases)
;; Reporting customers who bought more times
;; {:quantity 3, :customers (#:customer{:name Maria, :id #uuid "63c03785-9b0f-46b8-be86-8a072a99439f"})}

;; Customers that made the purchase with highest value
(println "\nReporting customers with highest order value")
(def highest-priced (r/customers-highest-purchase-value))
(println highest-priced)
;; Reporting customers with highest order value
;; {:high-value 1515.00M, :customers (#:customer{:name Maria, :email maria@email.com} #:customer{:name Marta, :email marta@email.com})}

;; Customers that made the purchase with lowest value
(println "\nReporting customers with highest order value")
(def lowest-priced (r/customers-lowest-purchase-value))
(println lowest-priced)
;; Reporting customers with highest order value
;; {:low-value 12.98M, :customers (#:customer{:name Hellen, :email hellen@email.com})}

;; Customers that made no purchase at all
(println "\nWho did not made a purchase")
(def no-order-at-all (r/customers-with-no-order))
(println no-order-at-all)
;; Who did not made a purchase
;; (#:customer{:name Angelo, :email angelo@email.com} #:customer{:name Jose, :email jose@email.com})
