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
  "starts the elements of the service that require db connection"
  [conn]
  (cc.db/init-entity! conn)
  (cust.db/init-entity! conn)
  (o.db/init-entity! conn)
  (println "entities started"))

(defn drop-database!
  "Resets the data doing the deletion of the database"
  []
  ; This is a secret and should not be as plain text here
  ; TODO: Add environment variable reader
  (let [db-uri "datomic:dev://localhost:4334/semana1"]
  (d/delete-database db-uri)))
