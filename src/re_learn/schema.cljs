(ns re-learn.schema
  (:require [schema.core :as s]))

(def ^:private Html (s/cond-pre (s/pred vector?) s/Str))

(def Lesson {:id                        s/Keyword
             :description               Html
             :version                   s/Int
             :position                  (s/enum :left :right :top :bottom :unattached)
             (s/optional-key :dom-node) s/Any
             (s/optional-key :attach)   [s/Keyword]
             (s/optional-key :continue) [s/Keyword]})

(def LessonReference
  (s/conditional
   keyword? s/Keyword

   #(instance? MetaFn %) (s/pred #(:re-learn.core/lesson-id (meta %)))

   map? {:id s/Keyword
         s/Any s/Any}))

(def ReLearnModel
  {(s/optional-key :lessons-learned) {s/Keyword s/Int}
   (s/optional-key :lessons)         {s/Keyword Lesson}
   (s/optional-key :tutorials)       {s/Keyword {:id s/Keyword
                                                 :name s/Str
                                                 :description Html
                                                 :lessons [LessonReference]}}})
