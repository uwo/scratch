(ns scratch.lnl.transducers
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :as a]))

(comment ;; HOW:

  (def lnls
    [{::name "Brian" ::topic "NLP" ::date #inst "2017-02-07"}
     {::name "Tyler" ::topic "transducers" ::date #inst "2017-11-27"}
     {::name "Phillip" ::topic "Dragon Burritos" ::date #inst "2017-12-25"}])

  ;; get a transducer
  (def xf (map ::topic))

  ;; use it in a transducing context
  #_(into [] xf lnls) ; => ["NLP" "transducers" "Dragon Burritos" "pIgs"]

  ;; Transducers compose with `comp` and are executed from left to right
  ;; like the thread first macro. Why is `comp` 'reversed'? We'll see
  ;; later.

  (def xf0
    (comp
      (filter odd?)
      (map inc)
      ))

  #_(transduce xf0 + (range 5))

  )

(comment ;; WHAT:

  ;; A process abstraction; a recipe or set of instructions
  ;; We don't care about input or output, we're just describing a step in a process

  ;; Most of the functions below have arities that return a transducer

  ;; one to one
  map map-indexed replace

  ;; elision - less than we got
  filter remove distinct dedupe random-sample partition-by partition-all

  ;; expansion - more than we got
  mapcat interpose cat

  ;; abortive - early termination, bailing out
  take take-while take-nth drop drop-while keep keep-indexed halt-when

  ;; -not- transducers:
  ;; both of these need to materialize the entire result
  group-by sort

  ;; `cat` is a transducer
  (into [] cat [[1 2 3 4] [5 6 7]]) ; => [1 2 3 4 5 6 7]
  (into [] cat [[[1 2 3 4]] [[5 6 7]]]) ; => [[1 2 3 4] [5 6 7]]
  (flatten [[[1 2 3 4]] [[5 6 7]]]) ; => '(1 2 3 4 5 6 7)

  )

(comment ;; WHERE:

  ;; Transducing contexts
  into transduce sequence eduction

  a/chan a/pipeline-blocking a/pipeline

  )

(comment ;; WHO:

  ;; as in, "I don't know who handed me this transducer"

  ;; aka Composing:

  ;; Transducers can be built and composed later

  (defn occurred?
    [inst]
    (let [now (java.util.Date.)]
      (= -1 (compare inst now))))

  (def xf1
    (comp
      (remove #(occurred? (::date %)))
      (map ::name)))

  (def updated-lnls (conj lnls {::name "Brandon" ::topic "pIgs" ::date #inst "2017-02-11"}))

  #_(clojure.pprint/pprint
      (transduce xf1 conj [] updated-lnls))

  (s/def ::name string?)

  (s/def ::topic string?)

  (s/def ::date inst?)

  (s/def ::lnl
    (s/keys :req [::name ::topic ::date]))

  (def tap
    "use to inpsect values between transduction steps"
    (map (fn [x] (do (prn x) x))))

  (def xf2
    (comp
      (map #(s/conform ::lnl %))
      ;(remove #(occurred? (::date %)))
      (map ::name)))
      ))

  #_(clojure.pprint/pprint
      (transduce xf2 conj [] updated-lnls))
  )

(comment ;; WHY:

  ;; need eagerness

  ;; abstraction
  ;; - context independent: transducers fully decoupled from input and output
  ;; - don't need versions of map, filter, etc for collection, channels, parallel contexts
  ;;   observables (reactive programming)

  ;; performance
  ;; - remove intermediate seqs and caching required by lazy processing
  ;; - leverage reducible inputs
  ;;   https://www.youtube.com/watch?v=FjKnlzQfAPc

  ;; design
  ;; - affects the way you think about problems
  ;; - you can write and share a process, which others can compose into
  ;;   their own process. You don't need to know how it'll be used.
  ;; - by removing code that deals with input and output you're left with
  ;;   less to think about:
  ;;   accidental complexity has fewer places to hide

  )

(comment ;; WHENCE:
  ;; writing a transducer

  ;; motivation
  (defn map*
    [f coll]
    (reduce
      (fn [result input]
        (conj result (f input)))
      []
      coll))

  (mapping inc [1 2 3])

  (defn filter*
    [f coll]
    (reduce
      (fn [result input]
        (if (f input)
          (conj result input)
          result))
      []
      coll))

  (filtering odd? [1 2 3])

  ;; recall types: can call 0, 1, or more times: elision, one to one, expansion
  (defn mapping [f]
    (fn [step]
      (fn [result input]
        (step result (f input)))))

  (defn catting
    (fn [step]
      (fn [result input]
        (reduce step result input))))

  (defn filtering
    (fn [step]
      (fn [result input]
        (if (f input)
          (step result input)
          result))))

  (reduce ((mapping inc) conj) [] [1 2 3])
    
  (clojure.repl/source map)

  (defn map-core
    [f]
    (fn [rf]
      (fn
        ([] (rf)) ;; init: fabricate the initial result, e.g. + => 0, conj => []
        ([result] (rf result)) ;; completion, if none flow thru
        ([result input] ;; standard reduce step
         (rf result (f input))))))

    (clojure.repl/source filter)

    ;; stateful transducers

    (clojure.repl/source take)

    (clojure.repl/source partition-by)
    
    (clojure.repl/source mapcat)

  )

(comment ;; WHEN:
  ;; async
  ;; see tx-pipeline
  ;; note about blocking inside channel transducers, i.e. don't

  (defn some-process []
    (let [ch (a/chan 1 xf2)]
      (a/onto-chan ch updated-lnls)
      (a/go-loop []
        (a/<! (a/timeout 1000))
        (when-let [lnl (a/<! ch)]
          (clojure.pprint/pprint lnl)
          (recur)))))

  )

(comment ;; WHEN?!:

  ;; Regaining laziness
  ;; see sequence tab
  ;; pull - lazy sequences
  ;; push - transducers - supply input and then the logic run
  
  ;; if in a single step you return an infinite sequence, or something
  ;; that consumes all of memory, this won't work

)

(comment ;; WHO?!

  ;; Passing the recipe with the ingredients
  (clojure.repl/source eduction)
  )

(comment ;; WHETHER:
  ;; Difference with reducers: apples and oranges

  ;; Transducers v. seqs
  ;; https://clojure.org/guides/faq#transducers_vs_seqs
  )

(comment ;; I DON'T KNOW
  ;; https://github.com/cgrand/xforms

  ;; a talk on building your own transducers
  ;; https://www.youtube.com/watch?v=XiCwN-fv7os

  ;; Inside Transducers (RH)
  ;; https://www.youtube.com/watch?v=4KqUvG8HPYo

  ;; Original Strangeloop talk (RH)
  ;; https://www.youtube.com/watch?v=6mTbuzafcII

  ;; an aside on the new halt-when
  ;; https://medium.com/@chpill_/deep-dive-into-a-clojure-transducer-3d4117784fa6
  )

