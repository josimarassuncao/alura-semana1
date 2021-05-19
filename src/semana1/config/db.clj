(ns semana1.config.db
  (:require [datomic.api :as d]
            [semana1.creditcard.db.db :as cc.db]
            [semana1.customer.db.db :as cust.db]
            [semana1.orders.db.db :as o.db]))

(defn start-db-and-connection!
  "Ensure database is on and gets a connection to it"
  []
  ; This is a secret and should not be as plain text here
  ; TODO: Add environment variable reader
  (let [db-uri "datomic:dev://localhost:4334/semana1"]
    (defn ensure-db-created!
      "Creates the database is created"
      []
      (d/create-database db-uri))
    (defn get-connection
      "Gets a connection to handle db operations"
      []
      (d/connect db-uri))
    )
  (ensure-db-created!)
  (get-connection))

(defn start-dbs!
  "starts environment"
  [conn]
  (cc.db/init-data! conn)
  (cust.db/init-data! conn)
  (o.db/init-data! conn)
  (println "dbs started"))
