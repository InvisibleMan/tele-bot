(ns tele-bot.bot
  (:require [clojure.string :as string]
            [tele-bot.utils :as ut]
            [tele-bot.telegapi :as tg]
            [tele-bot.utils :refer [tee]]))

;; String -> String
(defn- build-response [msg]
  (str "Sorry, I'm dummy bot. Echo:\n\n"
    msg))

;; ============================================
;; Telegram API Integration functions

;; {} -> {}
;; Assocs to the message the appropiate response
(defn- process-update [element]
  (ut/logger (str "RECEIVED MESSAGE: " (:message element)))
  (-> element
      (assoc :message
        (build-response
          (:message element)))))


;; {} -> {}
;; Map adapter for send-message
(defn- map-send [element]
  (tee element
       (#(tg/send-message (get % :chat-id) (get % :message)))))


;; Seq -> Natural
;; Processes each element of the given sequence
;; and performs the appropiate action
;;
;; Returns next update id to request to the server
(defn- seq->response [update-seq]
  (->> update-seq
       (map tg/get-response-info)
       (map process-update)
       (map map-send)
       tg/get-next-offset))


;; ============================================
;; Telegram API Integration functions

(def ^:private timeout-ms
  10000)

;; ->
(defn run []
  (let [welcome_msg "Tele-bot Started!!"]
    (ut/logger welcome_msg))
  (let [*update-id* (atom (tg/get-last-offset))]
    (while true
      (try
        (if (seq (tg/get-update-map @*update-id*))
          (->> (seq->response (tg/get-update-map @*update-id*))
               (reset! *update-id*))
          (Thread/sleep timeout-ms))

        (catch com.fasterxml.jackson.core.JsonParseException e
          (ut/logger e))
        ))))
