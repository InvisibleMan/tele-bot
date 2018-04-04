(ns tele-bot.handlers-test
  (:use clojure.test)
  (:require
   [clojure.string :as str]
   [tele-bot.handlers :as hrs]
   ))

(deftest empty-test
  (testing "Проверка обработчика по-умолчанию 1"
    (is (hrs/empty {:message "One"} #(println %2))))

  (testing "Проверка обработчика по-умолчанию 2"
    (let [at1 (atom "")]
      (is (hrs/empty {:message "One"} #(reset! at1 %2)))
      (is (= "I can't understand message: 'One'." @at1)))))

(deftest check-access-test
  (testing "Проверка обработчика по проверке прав 1"
    (is (hrs/check-access "One" #(comment %))))

  (testing "Проверка обработчика по проверке прав 2"
    (let [at1 (atom "")]
      (is (hrs/check-access "One" #(reset! at1 %)))
      (is (= "Access denied!" @at1)))))

(deftest check-access-f-test
  (let [no-access-f #'hrs/no-access-f]
    (testing "Проверка функции проверки доступа 1"
      (is (no-access-f #(identity (list)) {})))

    (testing "Проверка функции проверки доступа 2"
      (is (no-access-f #(identity (list)) {:chat-id "2"})))

    (testing "Проверка функции проверки доступа 3"
      (is (no-access-f #(identity (list nil)) {})))

    (testing "Проверка функции проверки доступа 4"
      (is (no-access-f #(identity (list nil)) {:chat-id "3"})))

    (testing "Проверка функции проверки доступа 5"
      (is (not (no-access-f #(identity (list "12")) {:chat-id "12"}))))

(testing "Проверка функции проверки доступа 5"
      (is (not (no-access-f #(identity (vec ["12"])) {:chat-id 12}))))
    ))

(deftest check-access-test
  (testing "Проверка обработчика по проверке прав 1"
    (let [at1 (atom "")]
      (is (hrs/check-access #(identity (list "1" "2")) {:chat-id "3"}  #(reset! at1 %2)))
      (is (= "Access denied!" @at1))))

  (testing "Проверка обработчика по проверке прав 2"
    (let [at1 (atom "")]
      (is (not (hrs/check-access #(identity (list "1" "3")) {:chat-id "3"}  #(reset! at1 %2)))))))
