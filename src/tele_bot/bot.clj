(ns tele-bot.bot
  (:require [clojure.string :as string]
            [tele-bot.utils :as ut]
            ;; [tele-bot.sandbox :as sb]
            [tele-bot.telegapi :as tg]
            [tele-bot.responses :as rsp]
            [tele-bot.utils :refer [tee]]))


;; ============================================
;; Command responses

(defn- eval-command [args fmt]
  ;; (-> args sb/eval-expr fmt)
  "Command eval result")


;; String -> {:command :args}
(defn- split-command-args
  "
   Splits the incoming command string
   into commands and arguments
  "
  [command]
  (let [commands (string/split command #" " 2)]
    {:command (string/trim (first commands))
     :args (when (second commands)
             (string/trim (second commands)))}))


;; String -> String
(defn- command->response [command-string fmt]
  (let [command-map (split-command-args command-string)
        command (:command command-map)]
    (cond
      (= "/eval" command) (eval-command
                            (:args command-map) fmt)
      (or (= "/start" command)
          (= "/help" command)) rsp/start-message
      :else rsp/error-message)))


;; String -> String
(defn- command->response2 [command-string fmt]
  (str "Sorry, I'm dummy bot. Echo:\n"
    command-string))

;; ============================================
;; Telegram API Integration functions

;; {} -> {}
;; Assocs to the message the appropiate response
(defn- process-update [element]
  (println element)
  (-> element
      (assoc :message
        (command->response2
          (:message element)
          rsp/eval-response))))


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
    (println welcome_msg)
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
