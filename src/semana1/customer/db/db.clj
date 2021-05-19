(ns semana1.customer.db.db)

(def list-of-customers [])

(def default-list [
                   {:customer-id 301, :name "Angelo", :email "angelo@email.com"}
                   {:customer-id 411, :name "Maria", :email "maria@email.com"}
                   {:customer-id 831, :name "Jose", :email "jose@email.com"}
                   {:customer-id 200, :name "Marta", :email "marta@email.com"}
                   {:customer-id 234, :name "Hellen", :email "hellen@email.com"}
                   {:customer-id 235, :name "Erika", :email "erika@email.com"}
                   ])

(defn get-all-customers []
  "returns the whole list of customers"
  list-of-customers)

(defn get-customer-info
  "retrieves customer info using id"
  [id]
  (->> list-of-customers
       (filter #(= (:customer-id %) id))
       first))

(defn init-data!
  "starts the data to test the movements"
  [conn]
  (def list-of-customers default-list))
