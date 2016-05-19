(ns tele-bot.test.utils
    (:require [tele-bot.utils :as u])
    (:use [clojure.test]))

(deftest unixt->utc
  (let [t 1457089214]
    (is (= "2016-03-04 14:00:14" (u/unixt->utc t)))))
