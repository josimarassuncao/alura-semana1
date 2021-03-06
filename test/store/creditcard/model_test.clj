(ns store.creditcard.model-test
  (:require [clojure.test :refer :all]
            [store.creditcard.model :refer :all]
            [schema.core :as s]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(s/set-fn-validation! true)

(deftest build-new-creditcard-test
  (testing "gets a new instance of creditcard data passing all parameters"
    (let [id #uuid"cd335e7b-9d79-4ffb-84e3-05cd8514ba20"
          cardnumber [5148 7572 5686 922]
          cvv 200
          expiration "2023-04-20"
          limit 50M
          new-card (build-new-creditcard id cardnumber cvv expiration limit)]
      (is (= id (:credit-card/id new-card)))
      (is (= cardnumber (:credit-card/number new-card)))
      (is (= cvv (:credit-card/cvv new-card)))
      (is (= expiration (:credit-card/expires-at new-card)))
      (is (= limit (:credit-card/declared-limit new-card)))))

  (testing "gets a new instance of creditcard data having id generated"
    (let [cardnumber [5148 7572 5686 922]
          cvv 200
          expiration "2023-04-20"
          limit 50M
          new-card (build-new-creditcard cardnumber cvv expiration limit)]
      (is (= java.util.UUID (type (:credit-card/id new-card))))
      (is (= cardnumber (:credit-card/number new-card)))
      (is (= cvv (:credit-card/cvv new-card)))
      (is (= expiration (:credit-card/expires-at new-card)))
      (is (= limit (:credit-card/declared-limit new-card)))))

  (testing "gets an when not passing a valid card number"
    (let [cardnumber nil
          cvv nil
          expiration "2023-04-20"
          limit 50M]
      (is (try
            (build-new-creditcard cardnumber cvv expiration limit)
            false
            (catch clojure.lang.ExceptionInfo e
              (= :schema.core/error (:type (ex-data e)))
              )))))

  )

(defn- roundtrip
  [creditcard]
  (-> creditcard
      creditcard->str
      str->creditcard))

(defn- cardnumber-gen
  []
   (gen/tuple (gen/choose 10 9999)
              (gen/choose 10 9999)
              (gen/choose 10 9999)
              (gen/choose 10 9999)))

(defn- limit-gen
  []
  (gen/double* {:infinite? false :NaN? false :min 0.0 :max 60000.0}))

(defspec serialization-deserealization-test 250
         (prop/for-all [id gen/uuid
                        cardnumber (cardnumber-gen)
                        cvv (gen/choose 10 999)
                        expiration (gen/not-empty gen/string-alphanumeric)
                        limit (limit-gen)]
                       (let [card (build-new-creditcard id cardnumber cvv expiration limit)]
                         ;(println card)
                         (= card (roundtrip card))))
         )
