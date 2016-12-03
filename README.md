# re-learn

**Learn them good**

Data-driven tutorials for reagent components with no changes required to your dom.

![](documentation/re-learn.gif?raw=true)

## Usage

Annotate reagent render functions with lessons

```clojure
(require [re-learn.core :as re-learn]
         [re-learn.utils :as rlu])

(def purchase-button
  (rlu/with-lesson
    {:id :purchase-button-lesson
     :description "When you're ready, click here to purchase"
     :position :bottom         ;; optional, defaults to :right. values are :left, :right, :bottom, :unattached and :top (experimental)
     :version 2                ;; optional, defaults to 1
     :attach [:button#some-id] ;; optional, attach lesson to a dommy selector, see https://github.com/plumatic/dommy for use
     :continue [:table :.tr]   ;; optional, continue when this dommy selector is clicked
     }

    (fn [] [:button.mdl-button.mdl-button--raised "Purchase"])))
```

Combine lessons into tutorials and attach them to views

```clojure
(def checkout
  (rlu/with-tutorial
    {:id :checkout-tutorial
     :lessons [{:id :welcome-lesson                              ;; this is an inline lesson, not attached to anything
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
    (reagent/render [checkout app-state] app-root)
    (reagent/render [re-learn/tutorial-view] tutorial-root)))
```

[Look at the working example](example/checkout/app.cljs) for more details.

## Development

```clojure
user> (dev)
dev> (start)
;; visit http://localhost:3449
dev> (cljs)
cljs.user>
```

## To do
- Can unlearn individual tutorials rather than unlearning everything
- Live demo

## License

Copyright © 2016 oliyh

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
