(ns store.customer.model
  (:require [schema.core :as s]
            [clojure.edn :as edn]))

(defn- uuid
  []
  (java.util.UUID/randomUUID))

(s/defschema CustomerSchema
           {:customer/id s/Uuid
            :customer/name s/Str
            :customer/email s/Str
            })

(s/defn build-new-customer :- CustomerSchema
  "builds a new customer data"
  ([name :- s/Str email :- s/Str] (build-new-customer (uuid) name email))
  ([uuid :- s/Uuid name :- s/Str email :- s/Str]
   {:pre [(not (nil? name)), (not (nil? email))]}
   {:customer/id    uuid,
    :customer/name  name,
    :customer/email email}))

(s/defn customer->str :- s/Str
  "turns a customer data to string"
  [customer :- CustomerSchema]
  (pr-str customer))

(s/defn str->customer :- CustomerSchema
  "turns a customer string into a customer data"
  [customer-str]
  (edn/read-string customer-str))
