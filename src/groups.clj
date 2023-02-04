(ns groups
  (:require [clojure.set :as set]
            [steam-api :as steam]))

(comment
  ;; This is the goal of the exercise
  {#{"bob" "steve" "george" "tom"} ["Game3" "Game4"]
   #{"bob" "steve"} ["Game2" "Game5" "Game6"]
   #{"greg" "tom"} ["Game1"]})

(def test-games (->> (range 1 15)
                     (map #(let [id (+ 2287419 %)]
                             [id (hash-map :name (str "Game" %)
                                           :steam_appid id
                                           :about_the_game (str "Some description for game " %))]))
                     (into (sorted-map))))

(def test-users [{:username "bob"
                  :games #{2287420 2287421 2287423 2287424 2287425 2287426 2287430 2287433}}
                 {:username "steve"
                  :games #{2287420 2287421 2287422 2287423 2287426 2287428 2287431 2287433}}
                 {:username "greg"
                  :games #{2287420 2287421 2287422 2287424 2287425 2287427 2287429 2287433}}
                 {:username "tom"
                  :games #{2287420 2287422 2287423 2287424 2287427 2287428 2287432 2287433}}])

(def wan-users [{:name "Steve"
                 :id "76561198083104344"}
                {:name "Scott"
                 :id "76561197960395655"}
                {:name "Phil"
                 :id "76561197971262476"}
                {:name "Joe"
                 :id "76561198002356078"}
                {:name "Travis"
                 :id "76561197997952617"}
                {:name "Yeats"
                 :id "76561197960443711"}
                {:name "Jacob"
                 :id "76561198066135100"}
                {:name "Walton"
                 :id "76561197979450876"}
                {:name "Brendan"
                 :id "76561198033963862"}])

(defn group-map
  "Holding onto this code for later -- it would be nice if the group map
   could be sorted, but need a good impl for sorting sets since they are
   the keys. Currently this impl is broken."
  []
  (sorted-map-by (fn [k1 k2]
                   (let [c1 (compare (count k1) (count k2))]
                     (if (zero? c1)
                       (compare (first k1) (first k2))
                       c1)))))

(defn test-fn
  [v]
  (println "Hi there")
  (println "The value is " v))

; API is rate limited to 200 requests in 5 mins
; About 1.5 requests / second
(defn detain
  "Wraps a function f with a brief pause before returning. Useful for
   API rate limits -- ensures API is callable again after this fn returns."
  [f]
  (fn [& args]
    (let [r (apply f args)]
      (println "Detaining execution at " (System/currentTimeMillis))
      (Thread/sleep 1500)
      r)))

(defn pairs->multi-map
  "Takes a seq of key-value pairs, where keys can be repeated, and produces
   a map of the keys to 'vectors of all values' for a key."
  [pair-seq]
  (reduce (fn [m [k v]]
            (if (contains? m k)
              (assoc m k (conj (m k) v))
              (assoc m k [v])))
          {}
          pair-seq))

(defn convert-game-ids->names
  "Takes a multi-map whose values are vectors of game ids and converts
   the game ids to game names."
  [m]
  (->> (seq m)
       (map (fn [[k v]] [k (vec (map #(:name (test-games %)) v))]))
       (into {})))

(defn game-groups
  [users]
  (let [all-games (apply set/union (map :games users))]
    (map (fn [game-id]
           [(->> users
                 (filter #(contains? (:games %) game-id))
                 (map :username)
                 (into (sorted-set)))
            game-id])
         all-games)))

