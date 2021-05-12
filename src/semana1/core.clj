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
       )
  )

(def full-list (merge-all-data))

(println "\ncliente 200")
(println (->> full-list
              (filter #(= (:customer-id %) 200))))

(println "\ncliente 201")
(println (->> full-list
              (filter #(= (:customer-id %) 201))))

(println "\ncliente 831")
(println (->> full-list
              (filter #(= (:customer-id %) 831))))

(defn main [& args]
  (println "starting service...")
  (start-dbs!))
