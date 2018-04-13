(ns tele-bot.config 
  (:refer-clojure :exclude [load])
  (:require 
            [clojure.core :as core]            
            [clojure.string :as str]
            [tele-bot.utils :as log]
            [environ.core :refer [env]]
            ))

(def ^:dynamic *CFG* (atom {}))
(defn users-id [] (:users-id @*CFG*))
(defn save-path [] (:save-path @*CFG*))
(defn api-key [] (:api-key @*CFG*))

(defn save-path-t [] "/home/dlarionov/Downloads")


(defn- ^:private load []
  (if-let [us-id (env :users-id)]
    (do
     (swap! *CFG* assoc :users-id (str/split us-id #","))
     (log/info (str "Load success users-id: " (users-id))))
    (log/info "Can't load users allowed list ('users-id')"))

  (if-let [save-path (env :save-path)]
    (do
     (swap! *CFG* assoc :save-path save-path)
     (log/info (str "Load success save-path: " save-path)))
    (log/info "Can't load output save path ('save-path')"))

    (if-let [api-key (env :api-key)]
    (do
     (swap! *CFG* assoc :api-key api-key)
     (log/info (str "Load success api-key: " "HIDED...")))
    (log/info "Can't load output save path ('api-key')"))
  )

(defn init []
  (log/info (str "Load config... " (or (env :clj-env) "EMPTY ENV!!!")))
  (load)
  (log/info "End load config..."))

(defn reload []
  (reset! *CFG* {})
  (log/info "ReLoad config...")
  (load)
  (log/info "End Reload config...")
  (deref *CFG*))


