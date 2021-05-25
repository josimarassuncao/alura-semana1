(ns store.core
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
;;Customer Marta
;;({:db/id 17592186045421, :customer/id #uuid "f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9", :customer/name Marta, :customer/email marta@email.com, :orders ({:order/customer #:db{:id 17592186045421}, :order/id #uuid "98055656-84d5-4796-872a-a21ec6fff0d1", :order/total-price 91.97M, :order/payment-by CC, :order/category Clothes, :order/payment-ref #:credit-card{:id #uuid "4cbadf39-e1f9-4922-95a0-759743d93604"}, :payment-details {:db/id 17592186045427, :credit-card/id #uuid "4cbadf39-e1f9-4922-95a0-759743d93604", :credit-card/number [5427 1395 4814 2967], :credit-card/cvv 888, :credit-card/expires-at 2022-12-20, :credit-card/declared-limit 520M}, :order/establishment Renner, :order/bought-at 2021-05-03, :db/id 17592186045434} {:order/customer #:db{:id 17592186045421}, :order/id #uuid "c83a32b9-c4b4-4c88-92d3-a1d1c5b372e8", :order/total-price 1515.00M, :order/payment-by CC, :order/category Sport, :order/payment-ref #:credit-card{:id #uuid "4cbadf39-e1f9-4922-95a0-759743d93604"}, :payment-details {:db/id 17592186045427, :credit-card/id #uuid "4cbadf39-e1f9-4922-95a0-759743d93604", :credit-card/number [5427 1395 4814 2967], :credit-card/cvv 888, :credit-card/expires-at 2022-12-20, :credit-card/declared-limit 520M}, :order/establishment Nike, :order/bought-at 2021-04-12, :db/id 17592186045435})})

(println "\nCostumer non-existent")
(def customer-non-existent #uuid"f8ed5740-99d3-4e6b-a71e-aa2b9158babc")
(println (->> full-list
              (filter #(= (:customer/id %) customer-non-existent))))
;;Costumer non-existent
;;()

(println "\nCostumer Jose")
(def customer-Jose #uuid"3659e5b1-34ac-498a-a899-6eef06d441f7")
(println (->> full-list
              (filter #(= (:customer/id %) customer-Jose))))
;;Costumer Jose
;;({:db/id 17592186045420, :customer/id #uuid "3659e5b1-34ac-498a-a899-6eef06d441f7", :customer/name Jose, :customer/email jose@email.com, :orders ()})

;; Total per customer
(println "\nTotal spent per customer")
(def customer-total-report
  (->> full-list
       (map r/total-per-customer)
       ))

(println customer-total-report)
;;Total spent per customer
;;({:customer/id #uuid "902d739c-1226-4485-a109-2678c6c7a58f", :customer/name Angelo, :total 0} {:customer/id #uuid "d3996403-e4c3-46e9-935f-1d4b47df50ae", :customer/name Erika, :total 123.00M} {:customer/id #uuid "e42dca1e-69db-45bb-88d5-5225a49ebed0", :customer/name Hellen, :total 12.98M} {:customer/id #uuid "3659e5b1-34ac-498a-a899-6eef06d441f7", :customer/name Jose, :total 0} {:customer/id #uuid "63c03785-9b0f-46b8-be86-8a072a99439f", :customer/name Maria, :total 2090.97M} {:customer/id #uuid "f8ed5740-99d3-4e6b-a71e-aa2b9158bcd9", :customer/name Marta, :total 1606.97M})

;; Orders per category
(println "\nAll orders summed per category")
(def category-total-report (r/report-grouped-by-category full-list))
(println category-total-report)
;;All orders summed per category
;;({:order/category Clothes, :total 91.97M} {:order/category Market, :total 2226.95M} {:order/category Sport, :total 1515.00M})

(def spent-per-month (r/amount-spent-per-month-per-customer full-list))
(println "\nTotal spent per month per customer")

(def customer-Maria #uuid"63c03785-9b0f-46b8-be86-8a072a99439f")
(println (r/get-month-value-for-customer spent-per-month customer-Maria "2021-04"))
;;Total spent per month per customer
;;{:customer/id #uuid "63c03785-9b0f-46b8-be86-8a072a99439f", :customer/name Maria, :expense {:month 2021-04, :total 2030.98M}}

(def may-1-to-11 (r/orders-between-date full-list "2021-05-01" "2021-05-11"))
(println "\nReporting orders between May 1st and 11th")
(println may-1-to-11)
;;Reporting orders between May 1st and 11th
;;({:order/customer #:db{:id 17592186045423}, :order/id #uuid "df78cdc4-4f61-4965-bece-c6d27ac7e30c", :order/total-price 123.00M, :order/payment-by CC, :order/category Market, :order/payment-ref #:credit-card{:id #uuid "3bf97e13-8cb9-46a2-8070-5f0e777ded81"}, :payment-details {:db/id 17592186045428, :credit-card/id #uuid "3bf97e13-8cb9-46a2-8070-5f0e777ded81", :credit-card/number [5279 883 6158 8328], :credit-card/cvv 718, :credit-card/expires-at 2022-01-20, :credit-card/declared-limit 140M}, :order/establishment Mercado D'Avó, :order/bought-at 2021-05-01, :db/id 17592186045433} {:order/customer #:db{:id 17592186045421}, :order/id #uuid "98055656-84d5-4796-872a-a21ec6fff0d1", :order/total-price 91.97M, :order/payment-by CC, :order/category Clothes, :order/payment-ref #:credit-card{:id #uuid "4cbadf39-e1f9-4922-95a0-759743d93604"}, :payment-details {:db/id 17592186045427, :credit-card/id #uuid "4cbadf39-e1f9-4922-95a0-759743d93604", :credit-card/number [5427 1395 4814 2967], :credit-card/cvv 888, :credit-card/expires-at 2022-12-20, :credit-card/declared-limit 520M}, :order/establishment Renner, :order/bought-at 2021-05-03, :db/id 17592186045434} {:order/customer #:db{:id 17592186045422}, :order/id #uuid "cdf4cb00-c8de-4259-8a8b-719dbc1606a9", :order/total-price 12.98M, :order/payment-by CC, :order/category Market, :order/payment-ref #:credit-card{:id #uuid "4940b83b-9ea1-4e73-ac7e-95b0dd116d50"}, :payment-details {:db/id 17592186045430, :credit-card/id #uuid "4940b83b-9ea1-4e73-ac7e-95b0dd116d50", :credit-card/number [5196 7300 247 7635], :credit-card/cvv 181, :credit-card/expires-at 2021-12-20, :credit-card/declared-limit 1650M}, :order/establishment Extra, :order/bought-at 2021-05-11, :db/id 17592186045436})

(println "\nReporting orders with total value between 100 and 900")
(def order-100-to-900 (r/orders-between-values full-list 100 900))
(println order-100-to-900)
;;Reporting orders with total value between 100 and 900
;;({:order/customer #:db{:id 17592186045423}, :order/id #uuid "df78cdc4-4f61-4965-bece-c6d27ac7e30c", :order/total-price 123.00M, :order/payment-by CC, :order/category Market, :order/payment-ref #:credit-card{:id #uuid "3bf97e13-8cb9-46a2-8070-5f0e777ded81"}, :payment-details {:db/id 17592186045428, :credit-card/id #uuid "3bf97e13-8cb9-46a2-8070-5f0e777ded81", :credit-card/number [5279 883 6158 8328], :credit-card/cvv 718, :credit-card/expires-at 2022-01-20, :credit-card/declared-limit 140M}, :order/establishment Mercado D'Avó, :order/bought-at 2021-05-01, :db/id 17592186045433} {:order/customer #:db{:id 17592186045419}, :order/id #uuid "8b5baf65-1749-4cf5-8709-6f3810cfd807", :order/total-price 515.98M, :order/payment-by CC, :order/category Market, :order/payment-ref #:credit-card{:id #uuid "4940b83b-9ea1-4e73-ac7e-95b0dd116d50"}, :payment-details {:db/id 17592186045430, :credit-card/id #uuid "4940b83b-9ea1-4e73-ac7e-95b0dd116d50", :credit-card/number [5196 7300 247 7635], :credit-card/cvv 181, :credit-card/expires-at 2021-12-20, :credit-card/declared-limit 1650M}, :order/establishment Extra, :order/bought-at 2021-04-05, :db/id 17592186045438})

(println "\nReporting orders from Mercado D'Avó")
(def davo-orders (r/establishment-orders full-list "Mercado D'Avó"))
(println davo-orders)
;;Reporting orders from Mercado D'Avó
;;({:order/customer #:db{:id 17592186045423}, :order/id #uuid "df78cdc4-4f61-4965-bece-c6d27ac7e30c", :order/total-price 123.00M, :order/payment-by CC, :order/category Market, :order/payment-ref #:credit-card{:id #uuid "3bf97e13-8cb9-46a2-8070-5f0e777ded81"}, :payment-details {:db/id 17592186045428, :credit-card/id #uuid "3bf97e13-8cb9-46a2-8070-5f0e777ded81", :credit-card/number [5279 883 6158 8328], :credit-card/cvv 718, :credit-card/expires-at 2022-01-20, :credit-card/declared-limit 140M}, :order/establishment Mercado D'Avó, :order/bought-at 2021-05-01, :db/id 17592186045433} {:order/customer #:db{:id 17592186045419}, :order/id #uuid "10ecd706-35c0-4c17-9491-80098aae1c8d", :order/total-price 59.99M, :order/payment-by CC, :order/category Market, :order/payment-ref #:credit-card{:id #uuid "cd335e7b-9d79-4ffb-84e3-05cd8514ba20"}, :payment-details {:db/id 17592186045426, :credit-card/id #uuid "cd335e7b-9d79-4ffb-84e3-05cd8514ba20", :credit-card/number [5148 7572 5686 922], :credit-card/cvv 200, :credit-card/expires-at 2023-04-20, :credit-card/declared-limit 50M}, :order/establishment Mercado D'Avó, :order/bought-at 2021-05-12, :db/id 17592186045437})

;; Customers who purchased more times
(println "\nReporting customers who bought more times")
(def most-purchases (r/customers-most-purchases))
(println most-purchases)
;;Reporting customers who bought more times
;;{:quantity 3, :customers (#:customer{:name Maria, :id #uuid "63c03785-9b0f-46b8-be86-8a072a99439f"})}

;; Customers that made the purchase with highest value
(println "\nReporting customers with highest order value")
(def highest-priced (r/customers-highest-purchase-value))
(println highest-priced)
;;Reporting customers with highest order value
;;{:high-value 1515.00M, :customers (#:customer{:name Maria, :email maria@email.com} #:customer{:name Marta, :email marta@email.com})}

;; Customers that made the purchase with lowest value
(println "\nReporting customers with highest order value")
(def lowest-priced (r/customers-lowest-purchase-value))
(println lowest-priced)
;;Reporting customers with highest order value
;;{:low-value 12.98M, :customers (#:customer{:name Hellen, :email hellen@email.com})}

;; Customers that made no purchase at all
(println "\nWho did not made a purchase")
(def no-order-at-all (r/customers-with-no-order))
(println no-order-at-all)
;;Who did not made a purchase
;;(#:customer{:name Angelo, :email angelo@email.com} #:customer{:name Jose, :email jose@email.com})
