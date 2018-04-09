(ns tele-bot.config
  (:require [clojure.string :as str]
            [tele-bot.utils :as log]
            [environ.core :refer [env]]
            ))

(def ^:dynamic *CFG* (atom {}))

(defn init []
  (log/info "Load config...")
  (load)
  (log/info "End load config..."))

(defn- load []
  (if-let [users-id (env :users-id)]
    (do
     (swap! *CFG* assoc :users-id users-id)
     (log/info (str "Load success users-id: " users-id)))
    (log/info "Can't load users allowed list ('users-id')"))

  (if-let [save-path (env :save-path)]
    (do
     (swap! *CFG* assoc :save-pathd save-path)
     (log/info (str "Load success save-path: " save-path)))
    (log/info "Can't load output save path ('save-path')")))


(defn reload []
  (reset! *CFG* {})
  (log/info "ReLoad config...")
  (load)
  (log/info "End Reload config...")
  (deref *CFG*))

(defn users-id [] (:users-id @*CFG*))

(defn save-path [] (:save-path @*CFG*))
(defn save-path-t [] "/home/dlarionov/Downloads")


