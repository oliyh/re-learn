# re-learn

**Learn them good**

Data-driven tutorials for reagent components with no changes required to your dom.

Live demo at [https://oliyh.github.io/re-learn/](https://oliyh.github.io/re-learn/).

![](documentation/re-learn.gif?raw=true)

## Why?

Well-designed UIs are intuitive but even the best may need to introduce new features or train new users.
Descriptions should be close to the element they are describing, both on screen and in your code.

Existing tutorial solutions rely on hooks into and mutation of your dom which does not work with React. Defining lessons separately from your code makes them subject to rot as behaviour changes and documentation is not updated.

re-learn allows you to describe lessons for your UI elements with data and generates walk-throughs for your users. It does not mutate your dom, making it React-friendly, and by writing the lesson right next to the code it describes you stand a better chance of keeping it up-to-date.

Versioning allows you to update descriptions when behaviour changes or is enhanced, ensuring that all your users are kept up-to-date with all the awesome features you're adding to your application.

## Usage

Add re-learn to your project's dependencies:
[![Clojars Project](https://img.shields.io/clojars/v/re-learn.svg)](https://clojars.org/re-learn)

Annotate reagent components with lessons describing the component and how to use it:

```clojure
(require [re-learn.core :as re-learn]
         [re-learn.views :as re-learn-views])

(def purchase-button
  (re-learn/with-lesson
    {:id          :purchase-button-lesson
     :description "When you're ready, click here to purchase"
     :position    :bottom                  ;; optional, defaults to :right. values are :left, :right, :bottom, :top, :unattached, :bottom-left etc
     :version     2                        ;; optional, defaults to 1
     :attach      [:button#some-id]        ;; optional, position lesson relative to a dommy selector, see https://github.com/plumatic/dommy for use
     :continue    {:event :click           ;; optional, continue when this event occurs
                   :selector [:table :.tr]
                   :event-filter (fn [e] ...)}}

    (fn [] [:button.mdl-button.mdl-button--raised "Purchase"])))
```

Combine lessons into tutorials and attach them to views:

```clojure
(def checkout
  (re-learn/with-tutorial
    {:id           :checkout-tutorial
     :name         "The checkout"
     :description  "Review your basket, check the price and confirm your purchase"
     :precedence   1 ;; optional, allows some tutorials to take precedence over others
     :auto-accept? false ;; optional, defaults to false
                         ;; when true will start the tutorial immediately when this component is rendered
                         ;; when false will display a snackbar indicating that a tutorial is available
     :lessons [{:id          :welcome-lesson ;; this is an inline lesson, not attached to anything
                :description "Welcome to the re-learn example"}
               basket
               totals
               purchase-button]}

    (fn [app-state]
      [:div
       [basket app-state]
       [purchase-button]])))
```

Let re-learn take care of everything else!

```clojure
(defn- init []
  (let [app-root (js/document.getElementById "app")
        tutorial-root (js/document.getElementById "tutorial")
        app-state (fn [] ...)
    (reagent.dom/render [checkout app-state] app-root)
    (reagent.dom/render [re-learn-views/tutorial-view {:context? true}] tutorial-root)))
```

[Look at the working examples](example) for more details.

## Style

re-learn has simple and structured markup making it easy for you to apply your own styles.
To get started it's easiest to copy the [reference version](dev-resources/public/css/re-learn.css) into your own project and adapt it as you wish.

## Development

```clojure
user> (dev)
dev> (start)
;; visit http://localhost:3449
dev> (cljs)
cljs.user>
```

[![CircleCI](https://circleci.com/gh/oliyh/re-learn.svg?style=svg)](https://circleci.com/gh/oliyh/re-learn)

## License

Copyright © 2016 oliyh

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
