(ns tele-bot.app
  (:require [tele-bot.bot :as bot]))

(defn -main [& args]
  (bot/run))
