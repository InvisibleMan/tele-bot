(ns tele-bot.telegapi-util
  (:require [tele-bot.utils :as ut]
            [cheshire.core :refer [parse-string]]
            [clojure.walk :refer [keywordize-keys]]))

;; Hash -> Hash
;; Log message if message not empty
(defn log-message [message]
  (if (empty? message)
    message
    (do
      (ut/logger message)
      message)))

;; String -> {}
;; Return a map from the incoming result
(defn get-result-from-response [response-string]
  ; (println (str "Real response: '" response-string "'."))
  (-> response-string
      parse-string
      keywordize-keys
      (#(if (:ok %) (log-message (:result %)) nil))))


;; Hash -> {}
;; Takes the body of the incoming response and parses the result
(defn get-result-from-telegram-response [response]
  (-> response
      (get :body)
      get-result-from-response))


;; {} -> Natural
;; Gets the update key of the incoming response
(defn get-update-key [response-block]
  (-> response-block
      (get :update_id)))

;; {} -> Natural
;; Gets the update key of the incoming response
(defn get-message-id [response-block]
  (-> response-block
      (get :message)
      (get :message_id)))

;; {} -> Natural
;; Gets the message key of the incoming response
(defn get-chat-key [response-block]
  (-> response-block
      (get :message)
      (get :chat)
      (get :id)))


;; {} -> String
;; Gets the message text of the incoming response
(defn get-response-text [response-block]
  (-> response-block
      (get :message)
      (get :text)))
