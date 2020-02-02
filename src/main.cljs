(ns main
  (:require [reagent.core :as r]
            [cljs.core.async :refer (chan put! <! go go-loop timeout)]
            [cljsjs.d3]
            ))

(defn append-svg-logistic-map []
  [:div
   {:id "logistic-map"}

   [:svg
    {:width 960
     :height 500}]])

(defn main-component []
  [:div
   [:h1 "Logistics map"]
   [append-svg-logistic-map]])


(defn mount [c]
  (r/render-component [c] (.getElementById js/document "app"))
  )

(defn reload! []
  (mount main-component)
  (print "Reloaded!!"))

(defn main! []
  (mount main-component)
  (print "Main Component"))

(defn close! []
  (print "Close!!"))
