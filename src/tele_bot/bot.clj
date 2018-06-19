(ns tele-bot.bot
  (:require [clojure.string :as string]
            [clojure.core :as core]
            [tele-bot.config :as cfg]
            [tele-bot.utils :as ut]
            [tele-bot.utils :as log]
            [tele-bot.telegapi :as tg]
            [tele-bot.handlers :as hrs]
            [tele-bot.plugins.youtube :as ytb]
            [tele-bot.utils :refer [tee]]))

(defn handlers []
  (list
   (partial hrs/check-access #(cfg/users-id))
   ytb/handle
   hrs/empty))

;; Собственно пытается обработать сообщение
(defn handle [hdrs send-f msg]
  (try
    (log/debug (str "Start handle MESSAGE: " msg))
 
    (some
     #(% msg send-f)
     hdrs)

    (catch Exception e
      (log/error e)
      (log/error (first (.getStackTrace e)))
      (send-f msg e))))

(defn- reply-to [orig text]
  (log/debug (str "Reply message (On '" (:message-id orig) "'): " text))
  (tg/send-message (:chat-id orig) (:message-id orig) text))

(defn- send-msg [orig text]
  (log/debug (str "Send message: " text))
  (tg/send-message (:chat-id orig) text))

;; Запускает обработку принятых сообщений
(defn- process [msgs]
  (log/debug "Start process")
  (doall
   (map (partial handle (handlers) reply-to) msgs))
  (log/debug "End process"))

;; ============================================
;; Telegram API Integration functions

;; ->
(defn run []
  (log/info "Tele-bot Started!")
  (cfg/init)
  (let [*update-id* (atom 0)]
    (while true
      (try
        (let [items (tg/get-update-map @*update-id*)
              msgs (map tg/get-response-info items)]

          (if (not (empty? msgs))
            (do
              (reset! *update-id* (tg/get-next-offset msgs))
              (log/debug (str "UPDATE-ID: " @*update-id*))
              (future
                (process msgs)
                )))
          
          (Thread/sleep @cfg/timeout-ms))

        (catch com.fasterxml.jackson.core.JsonParseException e
          (log/error e))
        ))))
