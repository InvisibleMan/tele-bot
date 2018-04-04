(ns tele-bot.bot
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [tele-bot.utils :as ut]
            [tele-bot.utils :as log]
            [tele-bot.telegapi :as tg]
            [tele-bot.handlers :as hrs]
            [tele-bot.utils :refer [tee]]))

(def ^:private handlers
  (list
   (partial hrs/check-access #(env :users-id))
   hrs/empty))

;; Собственно пытается обработать сообщение
(defn handle [hdrs send-f msg]
  (some
   #(% msg send-f)
   hdrs))

(defn- reply-to [orig text]
  (tg/send-message (:chat-id orig) text))

;; Запускает обработку принятых сообщений
(defn- process [msgs]
  (log/debug "Start process")
  (try
    (log/debug (first msgs))
    (doall
         (map (partial handle handlers reply-to) msgs))
    (catch Exception e
          (log/error e)))
  (log/debug "End process"))

;; ============================================
;; Telegram API Integration functions

(def ^:private timeout-ms
  10000)

;; ->
(defn run []
  (log/info "Tele-bot Started!!")
  (let [*update-id* (atom 0)]
    (while true
      (try
        (let [items (tg/get-update-map @*update-id*)
              msgs (map tg/get-response-info items)]

          (if (not (empty? msgs))
            (do
              (log/debug (str "MSGS: " msgs))
              (reset! *update-id* (tg/get-next-offset msgs))
              (log/debug (str "UPDATE-ID: " @*update-id*))
              (future
                (process msgs)
                )))
          
          (Thread/sleep timeout-ms))

        (catch com.fasterxml.jackson.core.JsonParseException e
          (log/error e))
        ))))
