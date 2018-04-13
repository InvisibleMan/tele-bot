(ns tele-bot.app
  (:gen-class)
  (:require [tele-bot.bot :as bot]))

(defn -main [& args]
  (bot/run))
