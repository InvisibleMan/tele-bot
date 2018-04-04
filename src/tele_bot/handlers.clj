(ns tele-bot.handlers
  (:require [clojure.string :as string]
            [tele-bot.utils :as log]
            [environ.core :refer [env]]
            ))

(defn no-access-f [allow-users-id-f msg]
  (let
      [msg-user-id (:chat-id msg)
       users-id (seq (allow-users-id-f))]
    (println (str "Access handler. UserId: " msg-user-id))
    (println (str "Access handler. UserSId: " users-id))

    (cond
      (nil? msg-user-id) true
      (not (seq? users-id)) true
      (empty? users-id) true
      :else (not
             (some #{(str msg-user-id)} (map str users-id))))))

;; Msg -> Bool
(defn check-access [users-id-f msg send-f]
  (let [no-access (no-access-f users-id-f msg)]
    (if no-access (send-f msg "Access denied!"))
    no-access))

;; Msg -> Bool
(defn empty [msg send-f]
  (send-f msg
          (str "I can't understand message: '" (:message msg) "'."))
  true)
