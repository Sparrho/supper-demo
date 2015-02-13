# Supper

A pure-ClojureScript isomorphic rendering project, using Om/React, Sablono and Node.js

A public demo version, wrapping the Wikidata search API, lives at: http://supper-demo.herokuapp.com

Currently a working model, generalised from an internal project, and still a little rough around the edges. For now, consider it an interactive how-to rather than a finished product. Next steps will be breaking it down into a proper standalone library and a lein template.

## Installation

Can be deployed as-is to Heroku. Local testing requires Node.js to be installed on the system.

From the root of the project, build with `lein clean`, `lein cljsbuild once client`, `lein cljsbuild once server` and run `npm install` to add the required Node packages.

Once built, run with `node deploy/index.js` and navigate to `http://localhost:3000/`.

## Server Environment Variables

| Variable     | Default                   | Description  |
| ------------ | ------------------------- | ------------ |
| `PORT`       | `3000`                    | Server listen port |
| `API_HOST`   | `http://www.wikidata.org` | The host of the target API |
| `STATIC_URL` | `/resources/`             | Static file URL. Defaults to a relative path using Node itself, but should be swapped for your CDN of choice in production use. |

## A Note on Reference Cursors

Reference cursors were introduced in Om 0.8, and they vastly simplify some otherwise tricky tasks. Far better [descriptions](https://github.com/omcljs/om/wiki/Advanced-Tutorial#reference-cursors) already exist, but in short: a reference cursor allows a deeply nested component (e.g. a single Tweet's display row) to directly access another part of the state atom (e.g. overall user info) without needing to pass the entire state down to that nested component and through all of its ancestors.

Most existing examples take the following form:
```clojure
(def app-state
  (atom {:items [{:text "cat"} {:text "dog"} {:text "bird"}]}))

(defn items []
  (om/ref-cursor (:items (om/root-cursor app-state))))
```

Unlike `om/build` and `om/root`, which explicitly take the state as an argument, the unspoken implication is that there is a global app state which can be defined outside the scope of any given component. Perfectly reasonable for a client-side application, but problematic for a server which may be rendering many pages simultaneously, each modifying their own state atoms as http request callbacks are triggered.

Passing the state atom explicitly to `items` would essentially defeat the purpose of reference cursors - the deeply nested components would need to be passed the whole application state anyway, at which point they may as well operate on it directly. Luckily, however, all Om components, whatever their level of nesting, are aware of their underlying atom, which can be accessed with `om/state`. Modifying the above example gives:
```clojure
(defn generate-ref-cursor
  [cursor path-vec]
  (om/ref-cursor (get-in (om/root-cursor (om/state cursor)) path-vec))))

(defn items
  [cursor]
  (generate-ref-cursor cursor [:items])
```

The `items` function now takes _any_ cursor as an argument, correctly returning a reference cursor with no global state required. This does introduce the limitation that applications cannot use reference cursors across multiple state atoms within a single page (i.e. pages with multiple roots must use `core.async` communicate between roots), but this seems a worthwhile tradeoff to avoid the fragility of managing global server state across multiple http requests.

## Authentication

The proxy behaviour means that all cookies will be set from the domain of the Supper installation. There are various possibilities for passing along the authentication data when Supper intercepts a request in order to build a page - the simpliest is to simply grab the relevant cookies from the request and write them out into a new http header. There will always be a tradeoff here - either you need to accept non-cookie header authentication in your backing API (if you're doing this, make sure you communicate over https, and ideally whitelist the Supper server to prevent anyone else from using non-cookie auth and exposing themselves to security risks), or you have to essentially forge the cookie header, which can introduce its own problems.

## Why

Supper was built for a specific use case, and its design reflects this. Our faithful Django application was handling templating, rendering, and API exposure. On top of this, we had a client-side ClojureScript codebase which necessarily duplicated large chunks of the Django view and template logic. We needed smaller, lighter modules with more defined separation of concerns.

Shared, [isomorphic](http://www.oreilly.com/pub/e/3009) JS (or, in this case, cljs) emerged as by far the strongest contender for the rendering module. On top of this, it needed to be implemented in such a way that authentication was maintained, potentially across multiple APIs, and unchanged pages could still be served transparently from the existing platform.


## Thanks

Enormous thanks go to the devs behind [Om](https://github.com/omcljs/om), for the ecosystem that makes all of this possible; [Omelette](https://github.com/DomKM/omelette), an isomorphic example built using both Clojurescript and JVM Clojure, for providing heavy inspiration on structure; [matchcolor](https://github.com/seabre/matchcolor) for showing how easily Clojurescript can drive Node.js; and to the authors of all of the libraries without which this project would be impossible.
