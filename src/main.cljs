(ns main
  (:require [reagent.core :as r]
            [cljs.core.async :refer (chan put! <! go go-loop timeout)]
            [cljsjs.d3]
            ))

(def outer-width 1200)
(def outer-height  (* outer-width 0.8))
(def margin {:top 100 :right 20 :bottom 80 :left 80})
(def width (- outer-width (:left margin) (:right margin)))
(def height (- outer-height (:top margin) (:bottom margin)))
(def data [ {:x 0 :y 0} {:x 1 :y 30} {:x 2 :y 40}
           {:x 3 :y 20} {:x 4 :y 90} {:x 5 :y 70} ])

(clj->js data)

(def init-state
  {:outer-width outer-width
   :outer-height outer-height
   :width width
   :height height})

;; Model
(defonce app-state
  (r/atom init-state))

(defn xvalue [p]
  (:x p))
(defn yvalue [p]
  (:y p))

(def xscale (-> (js/d3.scaleLinear)
                (.domain (js/d3.extent data xvalue))
                (.range (array 0 width))))

(def yscale (-> (js/d3.scaleLinear)
                (.domain (js/d3.extent data yvalue))
                (.range (array height 0))))

(def xAxis (-> (js/d3.axisBottom xscale)
               (.ticks 5)))

(def yAxis (-> (js/d3.axisLeft yscale)
               (.ticks 4)))


;; var line = d3.line()      // SVG line generator
;;            .x(function(d) { return x(d.x); } )
;;            .y(function(d) { return y(d.y); } );

(def line (->(js/d3.line)
             (.x #((xscale (first % ))))
             (.y #((yscale (second % ))))))

(defn container-enter [ratom]
  (let [translate (str "translate(" (:left margin) "," (:top margin) ")")
        svg (-> (js/d3.select "#logistic-map svg")
                (.attr "width" outer-width)
                (.attr "height" outer-height))
        g (.append svg "g")]
        (.attr g "transform" translate)
        (-> (.append g "g")
            (.attr "class" "y axis")
            (.call yAxis))
        (-> (.append g "g")
            (.attr "class" "x axis")
            (.attr "transform" (str "translate(0," height ")"))
            (.call xAxis))
        (-> (.append g "text")
            (.attr "class" " x label")
            (.attr "text-anchor", "end")
            (.attr "x" (/ width 2))
            (.attr "y" (+ height (/ (* (:bottom margin) 2) 4)))
            (.text "outer x-axis label"))
        (-> (.append g "text")
            (.attr "class" "x label")
            (.attr "text-anchor" "middle")
            (.attr "x" (/ width 2))
            (.attr "y" (- 0 (/ (:top margin) 2)))
            (.attr "dy" "+.75em")
            (.text "plot title"))
        (->(.append g "text")
           (.attr "class" "x label")
           (.attr "text-anchor" "middle")
           (.attr "x" (/ (- 0 (:height @ratom)) 2))
           (.attr "y" (- -6 (/ (:left margin) 3)))
           (.attr "dy" "-0.75em")
           (.attr "transform" "rotate(-90)")
           (.text "outer y-axis label"))
        (-> (.append g "path")
            (.datum data)
            (.attr "class" "line")
            (.attr "d" line)
            (.style "fill" "none")
            (.style "stroke" "#fff")
            (.transition)
            (.delay 500)
            (.duration 1000)
            (.style "stroke" "#000"))
        (-> (.selectAll g ".dot")
            (.data data)
            (.enter)
            (.append "circle")
            (.attr "class" "dot")
            (.attr "cx" #((xscale (first % ))))
            (.attr "cy" #((yscale (second % ))))
            (.attr "r" 5))
        ))

(defn container-did-mount [ratom]
  (container-enter ratom))

;; for d3: enter, update, exit
(defn map-enter [ratom]
  )

(defn map-update [ratom]
  )

(defn map-exit [ratom]
  )

(defn map-did-update [ratom]
     (map-enter ratom)
     (map-update atom)
     (map-exit ratom))

(defn map-did-mount [ratom]
  (map-did-update ratom))

(defn viz-render [ratom]
  (let [width  (:outer-width ratom)
        height (:outer-height ratom)]
    [:div
     {:id "logistic-map"}

     [:svg
      {:width  width
       :height height}]]))

;; react/reagent states mount, update

(defn viz-did-mount [ratom]
  (container-did-mount ratom)
  (map-did-mount ratom))

(defn viz-did-update [ratom]
  (map-did-update ratom))

(defn create-map [ratom]
  (r/create-class
   {:reagent-render      #(viz-render ratom)
    :component-did-mount #(viz-did-mount ratom)
    :component-did-update #(viz-did-update ratom)}))


(defn main-component []
  [:div
   [:h1 "Logistics map"]
   [create-map app-state]])


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
