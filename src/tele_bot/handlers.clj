(ns tele-bot.handlers
  (:refer-clojure :exclude [empty])
  (:require [clojure.string :as string]
            [tele-bot.utils :as log]
            ))

(defn no-access-f [allow-users-id-f msg]
  (let
      [msg-user-id (:chat-id msg)
       users-id (seq (allow-users-id-f))]
    (log/debug (str "Access handler. UserId: '"
                    msg-user-id
                    "', UserSId: '"
                    users-id
                    ""))

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
  (log/debug "Empty handler")
  (send-f msg
          (str "I can't understand message: '" (:message msg) "'."))
  true)
