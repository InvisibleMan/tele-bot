(ns tele-bot.plugins.youtube-test
  (:use clojure.test)
  (:require
   [clojure.string :as str]
   [tele-bot.plugins.youtube :as ytb]
   ))

(deftest is-youtube-link-test
  (testing "Успешное содержимое ссылки"
  (is (ytb/is-youtube-link "https://www.youtube.com/watch?v=Af0RPneh1SQ")))

  (testing "Не успешное содержимое ссылки"
  (is (not (ytb/is-youtube-link "https://video.yandex.ru/watch?v=Af0RPneh1SQ")))))


(deftest fetch-url-test
  (testing "First test"
    (is (= (ytb/fetch-url "hha ddd youtube.com/watch second third") "youtube.com/watch")))
  (testing "Second test"
    (is (= (ytb/fetch-url "hha ddd second third") nil))))

