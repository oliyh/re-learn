(ns re-learn.schema
  (:require [schema.core :as s]))

(def ^:private LessonId s/Keyword)
(def ^:private TutorialId s/Keyword)

(def ^:private Html (s/cond-pre (s/pred vector?) s/Str))
(def ^:private DommySelector [s/Keyword])

(def Lesson {:id                         LessonId
             :description                Html
             :version                    s/Int
             :position                   (s/enum :left :right :top :bottom :unattached :top-left :top-right :bottom-left :bottom-right)
             (s/optional-key :dom-node)  s/Any
             (s/optional-key :attach)    DommySelector
             (s/optional-key :continue)  {:event                         s/Keyword
                                          (s/optional-key :selector)     DommySelector
                                          (s/optional-key :event-filter) (s/pred fn?)}
             (s/optional-key :on-appear) (s/pred #(fn? %))})

(def Tutorial
  {:id          TutorialId
   :name        s/Str
   :description Html
   :lessons     [LessonId]
   :precedence  s/Int})

(def ReLearnModel
  {:help-mode?                       s/Bool
   :highlighted-lesson-id            (s/maybe LessonId)
   (s/optional-key :lessons-learned) {LessonId s/Int}
   (s/optional-key :lessons)         {LessonId Lesson}
   (s/optional-key :tutorials)       {TutorialId Tutorial}})
