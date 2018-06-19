(ns tele-bot.config 
  (:refer-clojure :exclude [load])
  (:require 
            [clojure.core :as core]            
            [clojure.string :as str]
            [tele-bot.utils :as log]
            [environ.core :refer [env]]
            ))

(def ^:dynamic *CFG* (atom {}))
(def default-timeout 60)
(def default-save-path "./")

(defn users-id [] (:users-id @*CFG*))
(defn save-path [] (:save-path @*CFG*))
(defn api-key [] (:api-key @*CFG*))

(def timeout-ms (atom (* 1000 default-timeout)))

(defn- ^:private init-defaults []
  (swap! *CFG* assoc :save-path default-save-path)
  (swap! timeout-ms (fn [_] default-timeout)))

(defn- ^:private load []
  (log/info (str "Proxy host - " (System/getProperty "https.proxyHost")))
  (log/info (str "Proxy port - " (System/getProperty "https.proxyPort")))

  (init-defaults)

  (if-let [timeout (env :bot-timeout-sec)]
    (do
     (swap! timeout-ms (fn [_] (* (Integer. timeout) 1000)))
     (log/info (str "Load success timeout: " timeout " sec.")))
    (log/info (str "Can't load timeout ('timeout-sec'). Use default: " default-timeout " sec.")))

  (if-let [us-id (env :bot-users-id)]
    (do
     (swap! *CFG* assoc :users-id (str/split us-id #","))
     (log/info (str "Load success users-id: " (users-id))))
    (log/info "Can't load users allowed list ('users-id')"))

  (if-let [save-path (env :bot-save-path)]
    (do
     (swap! *CFG* assoc :save-path save-path)
     (log/info (str "Load success save-path: " save-path)))
     (do
      (log/info (str "Can't load save path ('save-path'). Use default: " default-save-path))))

    (if-let [api-key (env :bot-api-key)]
    (do
     (swap! *CFG* assoc :api-key api-key)
     (log/info (str "Load success api-key: " "HIDED...")))
    (log/info "Can't load output save path ('api-key')"))
  )

(defn init []
  (log/info (str "Load config... " (or (env :clj-env) "EMPTY profile!")))
  (load)
  (log/info "End load config..."))

(defn reload []
  (reset! *CFG* {})
  (log/info "ReLoad config...")
  (load)
  (log/info "End Reload config...")
  (deref *CFG*))


