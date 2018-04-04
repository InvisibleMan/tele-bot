(ns tele-bot.telegapi-test
  (:use clojure.test)
  (:require
   [clojure.string :as str]
   [tele-bot.telegapi :as tg]
   [tele-bot.telegapi-util :as tgu]
   ))

(def ^:private fixture_msg
  (tgu/get-result-from-response
   (slurp "./test/fixtures/messages2.json")))

(def ^:private first_msg
  (first fixture_msg))

(deftest get-response-info-test
  (let [msg (tg/get-response-info first_msg)]
    
    (testing "Проверка содержимого 1"
      (is (= (:update-id msg) 939571078)))

    (testing "Проверка содержимого 2"
      (is (= (:chat-id msg) 733168)))))

