(ns store.customer.model_test
  (:require [clojure.test :refer :all]
            [store.customer.model :refer :all]
            [schema.core :as s]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]])
  (:use clojure.pprint))

(s/set-fn-validation! true)

(deftest build-new-customer-test
  (testing "gets a new instance of customer data passing 3 parameters"
    (let [id #uuid"ffbccd34-c07d-44b6-acf2-ac53908056ba"
          name "José da Silva"
          email "jose.silva@email.com"
          customer (build-new-customer id name email)]
      (is (= id (:customer/id customer)))
      (is (= name (:customer/name customer)))
      (is (= email (:customer/email customer)))))

  (testing "gets a new instance of customer data passing 2 parameters"
    (let [name "José da Silva"
          email "jose.silva@email.com"
          customer (build-new-customer name email)]
      (is (= java.util.UUID (class (:customer/id customer))))
      (is (= name (:customer/name customer)))
      (is (= email (:customer/email customer)))))

  (testing "fails to get a new instance of customer data due to invalid parameters"
    (let [name nil
          email nil]
      (is (try
            (build-new-customer name email)
            false
            (catch clojure.lang.ExceptionInfo e
              (= :schema.core/error (:type (ex-data e)))
              )))))
  )

(defn- roundtrip
  [customer]
  (-> customer
      customer->str
      str->customer))

(defspec serialization-deserealization-test 50
         (prop/for-all [id gen/uuid
                        name (gen/not-empty gen/string-alphanumeric)
                        email (gen/not-empty gen/string-alphanumeric)]
                       (let [customer (build-new-customer id name email)]
                         ;(println customer)
                         (= customer (roundtrip customer))))
         )
