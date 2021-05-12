(ns semana1.core
  (:require [semana1.creditcard.db.db :as cc.db]
            [semana1.customer.db.db :as cust.db]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn start []
  "starts environment"
  (cc.db/init-data)
  (cust.db/init-data))

(defn -main [& args]
  (println "starting service...")
  (start))
