(ns logistic)

;; Math.round(var)
;; Math.round((num + Number.EPSILON) * 100) / 100 ;; epsilon 0.00001

(defn roundit
  "Round a double to the given precision (number of significant digits)"
  ([d] (roundit 3 d))
  ([precision d]
   (let [factor (Math/pow 10 precision)]
     (/ (Math/round (* d factor)) factor))))

(roundit 3 0.7994567)
(roundit 0.79994567)

(defn population3 [rate x]
  (*(* rate x) (- 1  x ))) 

(defn seq-pop [rate x]
  (lazy-seq (cons (roundit 1 x)
                  (seq-pop rate (population3 rate x))))) 

(frequencies (take 1000 (seq-pop 0.5 0.5))) 
(frequencies (take 1000 (seq-pop 2.0 0.5))) 
(frequencies (take 1000 (seq-pop 3.0 0.5))) 
(frequencies (take 1000 (seq-pop 3.2 0.5))) 
(frequencies (take 1000 (seq-pop 3.8 0.5))) 
