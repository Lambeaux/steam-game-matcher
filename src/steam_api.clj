(ns steam-api
  (:require [clj-http.client :as client]
            [steam-api :as api]))

(comment
  (def my-steam-id
    "76561197997952617"))

(def steam-api-key
  "11C59A0C247E9DC81F655783D09CC625")

(defn get-owned-apps
  [steam-id]
  (let [response (client/get
                  "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/"
                  {:query-params {:key steam-api-key
                                  :steamid steam-id
                                  :include_appinfo false}
                   :as :json})
        apps (get-in response [:body :response :games])]
    apps))

(defn get-app-info
  [app-id]
  (println "Getting appinfo for "  app-id)
  (let [response (client/get
                  "https://store.steampowered.com/api/appdetails/"
                  {:query-params {:appids app-id}
                   :as :json})]
    (get-in response [:body (keyword (str app-id)) :data])))

(defn get-all-apps
  []
  (let [response (client/get
                  "https://api.steampowered.com/ISteamApps/GetAppList/v2/"
                  {:as :json})]
    (get-in response [:body :applist :apps])))
