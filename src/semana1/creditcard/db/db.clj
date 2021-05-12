(ns semana1.creditcard.db.db)

(def list-of-cards [])

(def default-list [
                   {:number 1534, :cvv 12, :expires_at "2026-02", :limit 50}
                   {:number 1637, :cvv 10, :expires_at "2029-03", :limit 520}
                   {:number 1774, :cvv 91, :expires_at "2021-10", :limit 140}
                   {:number 1904, :cvv 26, :expires_at "2022-01", :limit 1650}
                   {:number 3104, :cvv 36, :expires_at "2022-01", :limit 1650}
                   ])

(defn get-all-cards []
  "returns the whole list of credit cards"
  list-of-cards)

(defn get-card-info
  "returns a card info"
  [card-id]
  (->> list-of-cards
       (filter #(= (:number %) card-id))
       (first)))

(defn init-data []
  "starts the data to test the movements"
  (def list-of-cards default-list))
