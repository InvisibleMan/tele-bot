(ns tele-bot.plugins.youtube
  (:require [clojure.string :as str]
            ;; [tele-bot.utils :as ut]
            [clojure.core.async :as async]
            [clojure.java.shell :as shell]
            ))

;; ============================================
;; Some descript

;; Объявляем chanel с буфером из 30 элементов
;; в него мы будем скидывать задачи на закачку
(def dowload-queue (async/chan 30))

;; Непосредственое скачивание
(defn do-simulate [url]
  (shell/sh "youtube-dl" "--no-progress -s -f mp4/best https://m.youtube.com/watch?v=fOaTUQqj2m0"))

(defn do-download [url]
  (shell/sh "youtube-dl" "--no-progress -s -f mp4/best" url))

(defn do-sleep [url]
  (Thread/sleep 2000)
  {:exit 0, :err ""})

;; Бесконечный цикл-обработчик задач на закачку
(defn start-bg-loop [f]
  (async/go-loop []
    (let
      [url (async/<! dowload-queue)]
      (println "Start downloading '" url  "' ...")
      (f url)
      (println "Stop downloading '" url  "'."))
    (recur)))

;; String -> Bool
(defn is-youtube-link [word]
  "Проверяет является 'слово ссылкой youtube'"
  (cond
    (some? (re-find #"youtube\.com" word)) true
    (some? (re-find #"youtu\.be" word)) true
    :else false))

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
        (async/>!! dowload-queue url)
        true))))

