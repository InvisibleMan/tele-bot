(ns tele-bot.plugins.youtube
  (:require [clojure.string :as str]
            [clojure.java.shell :as shell]
            [tele-bot.utils :as log]
            [tele-bot.config :as cfg]
            ))

(def dl-options [
  ;"--restrict-filenames"
  "--no-progress" "-f mp4/best"])

(def dl-options-simulate
  (conj dl-options "-s"))

(def dl-test-link "https://youtube.com/watch?v=EUHcNeg_e9g")

(defn dl-make-cmd [url options]
   (apply
    conj
    ["youtube-dl"]
    (conj options url :dir (cfg/save-path))))

;; Непосредственое скачивание
(defn download
  ([url] (download url dl-options))
  ([url options]
   (let
       [cmd (dl-make-cmd url options)
        cmd-s (str/join " " cmd)]
     (log/debug (str "Start shell script: " cmd-s))
     (apply shell/sh cmd)
  )))

(defn simulate [url]
  (download url dl-options-simulate))

(defn do-sleep [url]
  (Thread/sleep 2000)
  {:exit 0, :err ""})

;; String -> Bool
(defn is-youtube-link [word]
  "Проверяет является 'слово ссылкой youtube'"
  (cond
    (some? (re-find #"youtube\.com" word)) true
    (some? (re-find #"youtu\.be" word)) true
    (some? (re-find #"vimeo\.com" word)) true
    :else false))

;; String -> String
(defn fetch-url [msg]
  "Извлекает http-адрес ссылки"
  (first (filter is-youtube-link (str/split msg #" "))))


(defn handle [msg send-f]
  "Пытается обработать сообщение обработчиком youtube"
  (log/debug (str "Youtube handle: " msg))
  true
  (let [url (fetch-url (:message msg))]
    (if-not url
      false
      (do
        (send-f msg (str "Start youtube download."))
        (let [res (download url)]
          (if
              (= (:exit res) 0)
              (send-f msg (str "Youtube download success"))
              (send-f msg (str "Youtube download fail. Error: " (:err res))))
        true)))))


;; Тестовый обработчик для отладки долгой загрузки
(defn handler-sleep1 [msg, send-f]
  (send-f msg
          (str "Start youtube download..."))
  (do-sleep "link")
  (send-f msg
          (str "End youtube download...")))

(defn handler-sleep2 [msg, send-f]
  (send-f (str "Start youtube download..."))
  (let [res (shell/sh "resources/sleep.sh")]
    (send-f (str "End youtube download...", res))))
