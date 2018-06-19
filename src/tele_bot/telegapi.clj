(ns tele-bot.telegapi
  (:require [tele-bot.utils :as ut]
            [tele-bot.utils :as log]
            [tele-bot.config :as cfg]
            [clj-http.client :as http]
            [tele-bot.telegapi-util :as tgu]
            [tele-bot.response-format :as fmt]))


;; ============================================
;; Telegram API details
(def ^:private api-url "https://api.telegram.org/bot")

(defn- api-key [] (cfg/api-key))
(defn- bot-url [] (str api-url (api-key)))
(defn- self-url [] (str (bot-url) "/getMe"))
(defn- update-url [] (str (bot-url) "/getUpdates"))
(defn- send-message-url [] (str (bot-url) "/sendMessage"))

;; ============================================
;; Custom logger functions

;; String ->
(defn- send-success [response]
  (ut/logger (-> response
                 tgu/get-result-from-response
                 fmt/log-format)))

;; ============================================
;; Telegram API Integration functions

;; -> Natural
;; Gets the bot key
(defn- get-own-key []
  (-> (http/get (self-url))
      tgu/get-result-from-telegram-response
      (get :id)))


;; Seq -> Natural
;; Get the next telegram API offset
(defn get-next-offset [coll]
  (-> (last coll)
      (get :update-id)
      (+ 1)))


;; Maybe<Natural> -> IO [Maybe<{}>]
(defn get-update-map
  "
   Gets all the responses queued in the server
   starting from the given update key.

   If no offset is given, request the entire queue
  "
  ([] (get-update-map 0))
  ([offset]
   (log/debug (str "HTTP Request. GET: " (update-url)))
   (-> (http/get (update-url)
                          {:query-params {:offset offset}})
                (tgu/get-result-from-telegram-response))))


;; -> Natural
;; Gets the last update key in the server queue
(defn get-last-offset []
  (-> (last (get-update-map))
      tgu/get-update-key
      (#(if (nil? %) 0 %))))


;; Any -> {}
;; Converts the Response Element to a clojure hashmap
(defn get-response-info [response-block]
  (let [chat-id (tgu/get-chat-key response-block)
        update-id (tgu/get-update-key response-block)
        message-id (tgu/get-message-id response-block)
        message (tgu/get-response-text response-block)
        ]
    (hash-map
     :raw response-block
     :message-id message-id
     :chat-id chat-id
     :message message
     :update-id update-id)))

(defn send-msg- [options success-f error-f]
  (http/post (send-message-url) options
             (fn [{:keys [status headers body error]}]
               (if error
                 (error-f error)
                 (success-f body)))))

;; Natural String -!>
;; Natural String fn fn -!>
(defn send-message
  "
   Send the given message to the given chat
   If supplied, execute some functions as success / error callbacks
  "
  ([chat-id message]
   (let [options {:form-params {:chat_id chat-id
                                :text message}}]
     (send-msg- options send-success ut/log-error)))

  ([chat-id reply-to-id message] (send-message chat-id reply-to-id message
                                               send-success
                                               ut/log-error))
  
  ([chat-id reply-to-id message success-f error-f]
   (let [options {:form-params {:chat_id chat-id
                                :text message
                                :reply_to_message_id reply-to-id}}]
     (send-msg- options success-f error-f))))

