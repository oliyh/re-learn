(ns re-learn.schema
  (:require [schema.core :as s]))

(def ^:private LessonId s/Keyword)

(def ^:private Html (s/cond-pre (s/pred vector?) s/Str))
(def ^:private DommySelector [s/Keyword])

(def Lesson {:id                        LessonId
             :description               Html
             :version                   s/Int
             :position                  (s/enum :left :right :top :bottom :unattached)
             (s/optional-key :dom-node) s/Any
             (s/optional-key :attach)   DommySelector
             (s/optional-key :continue) DommySelector})

(def LessonReference
  (s/conditional
   keyword? LessonId

   #(instance? MetaFn %) (s/pred #(:re-learn.core/lesson-id (meta %)))

   map? {:id LessonId
         s/Any s/Any}))

(def ReLearnModel
  {:help-mode?                       s/Bool
   :highlighted-lesson-id            (s/maybe LessonId)
   (s/optional-key :lessons-learned) {LessonId s/Int}
   (s/optional-key :lessons)         {LessonId Lesson}
   (s/optional-key :tutorials)       {s/Keyword {:id s/Keyword
                                                 :name s/Str
                                                 :description Html
                                                 :lessons [LessonReference]}}})
