(defproject tele-bot "1.0.0-SNAPSHOT"
  :description "TeleBot - A Telegram Bot"
  :url ""
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [environ "1.0.1"]
                 [cheshire "5.5.0"]
                 [http-kit "2.1.19"]
                 ]
  :main ^:skip-aot tele-bot.app
  :uberjar "1.0.0-SNAPSHOT.jar"
  :plugins [[lein-environ "1.1.0"]]
  :profiles {
    :uberjar {:aot :all :env {:clj-env :production}}
    :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.15.0"]] :env {:clj-env :dev}}
    :test {:env {:clj-env :test}}
    :production {:env {:clj-env :production}}}
  )
