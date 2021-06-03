(ns store.customer.model_test
  (:require [clojure.test :refer :all]
            [store.customer.model :refer :all]))

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
      (println customer)
      (is (= java.util.UUID (class (:customer/id customer))))
      (is (= name (:customer/name customer)))
      (is (= email (:customer/email customer))))))

