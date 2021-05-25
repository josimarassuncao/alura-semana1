(ns store.config.db
  (:require [datomic.api :as d]
            [store.creditcard.db.db :as cc.db]
            [store.customer.db.db :as cust.db]
            [store.orders.db.db :as o.db]))

(def conn nil)

(defn get-connection
  "Returns a connection created to handle db operations"
  []
  conn)

(defn clear-connection!
  "Removes the current connection with the database"
  []
  (alter-var-root #'conn (constantly nil)))

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
    (defn create-connection!
      "Gets a connection to handle db operations"
      []
      (alter-var-root #'conn (constantly (d/connect db-uri))))
    )
  (ensure-db-created!)
  (create-connection!)
  (get-connection))

(defn start-dbs!
  "starts the elements of the service that require db connection"
  []
  (cust.db/init-entity! conn)
  (cc.db/init-entity! conn)
  (o.db/init-entity! conn)
  (println "entities started"))

(defn drop-database!
  "Resets the data doing the deletion of the database"
  []
  ; This is a secret and should not be as plain text here
  ; TODO: Add environment variable reader
  (let [db-uri "datomic:dev://localhost:4334/semana1"]
  (d/delete-database db-uri)))
