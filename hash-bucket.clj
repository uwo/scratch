(def ^:constant PARALLELISM 256)
(def IN_QUEUE_SIZE 32)
(def BUCKET_QUEUE_SIZE 8)

(defonce process-msg!
  (let [=in= (a/chan IN_QUEUE_SIZE)
        p (a/pub =in= (fn [msg] (-> msg thread-id hash (mod PARALLELISM))))
        =sink= (a/chan (a/dropping-buffer 0))]
    (doseq [i (range PARALLELISM)]
      (let [=ch= (a/chan BUCKET_QUEUE_SIZE)]
        (a/pipeline-async 1 =sink= #(af-handle-msg %1 %2) =ch=)
        (a/sub p i =ch=) ;; subscribing to the i-th hash bucket
        ))
    (fn process-msg! [obj]
      (a/>!! =in= obj))))
