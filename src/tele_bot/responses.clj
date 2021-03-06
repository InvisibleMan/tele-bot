(ns tele-bot.responses
  (:require [tele-bot.response-format :as fmt]))

(def start-message
  (str "Hello there!\n\n"
       "This is ClojureBot - evaluate your clojure code whenever you go.\n\n"
       "To get started, type /eval <code>"))

(def error-message
  (str "I'm sorry, my responses are limited.\n"
       "You must ask the right questions."))

(def ^:private eval-format
  fmt/with-input-format)

(defn eval-response [sexpr]
  (eval-format sexpr))
