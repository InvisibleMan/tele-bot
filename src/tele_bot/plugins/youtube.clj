(ns tele-bot.plugins.youtube
  (:require [clojure.string :as str]
            ;; [tele-bot.utils :as ut]
            ))


;; ============================================
;; Some descript


(defn is-youtube-link [word]
  "Проверяет является 'слово ссылкой youtube'"
  (not (nil? (cond
    (some? (re-find #"youtube\.com" word))
    (some? (re-find #"youtu\.be" word))
    :else nil))))

;; String -> String
(defn fetch-url [msg]
  "Извлекает http-адрес ссылки"
  (first (filter is-youtube-link (str/split msg #" "))))


;; String -> Bool
(defn try-handle [msg]
  "Пытается обработать сообщение обработчиком youtube"
  (let [url (fetch-url msg)]
    (if-not url
      false
      (do
        true))))



