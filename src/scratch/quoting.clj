(ns scratch.quoting)

(comment
  `[:find ~'?e
    :in ~'$ ~'?term
    :where
    (~'or-join ~'[?e ?term]
               ~@(map (fn [attr]
                        (let [sym (gensym "?val")]
                          `(~'and [~'?e ~attr ~sym]
                                  [(~'harmonium.ui.server.queries/match? ~'?term ~sym)])))
                      attrs))]
  )
