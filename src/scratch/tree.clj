(ns scratch.tree)

(def edges
  {:world     [:space :planets :stars]
   :space     [:spaceship :asteroid]
   :spaceship [:bob :spacesuit]
   :planets   [:mars]
   :mars      [:water :base]
   :base      [:garden :livingroom]
   :garden    [:mary]})

(defn tranform [node edges]
  (->> (edges node)
       (map #(tranform % edges))
       (into [node])))

(tranform :world edges)
