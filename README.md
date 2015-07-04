> Richard was a fine man

> This isn't SCIENCE this is PRODUCTION

# fineman

A Clojure library designed to help you experiment in production.
https://github.com/github/scientist, but for Clojure (api and readme liberally stolen)

## Usage

```clojure
(require '[fineman.scientist :as science])

(def my-experiment
  {:name "widget-permissions"
   :use (fn [widget user] (check-user? widget user))
   :try (fn [widget user] (allowed-to? :read user widget))})

(science/run my-experiment widget user)
```

Fineman runs experiments as *functions*.
Whatever arguments you pass to `run` are also passed to `:try` and `:use`.
The function under `:use` is the "control" (the original code you used to have).
The function under `:try` is the "experiment" (the new code that you want to compare).

Experiments are just maps with some data and functions in them.
There are a lot of options though. `:use`, `:try` and `:name` are the only required keys.

### Making Science Useful

The above example will run, but it's not doing anything particularly useful.
To make it useful, the very minimum you'll need to do is to be able to `:publish` results:

```clojure
:publish (fn [result] ; whatever you want to do on results happens here
         )
```

## Results

Results passed to `:publish` are just a map (actually a record):

```
{:name "widget-permissions"
  :control
  {
    :duration 10 ; number of milliseconds the control took
    :value true  ; whatever value your :use function returned
  }
  :candidate
  {
    :duration 3  ; number of milliseconds the candidate took
    :value true  ; whatever value your :try function returned
  }
}
```

## Ramping up Experiments

To control if your `:try` function runs, you can pass a `:enabled` function in an experiment:

```clojure
:enabled (fn [widget user] (is-staff? user))
```

Note that this function will be called for every invocation of every experiment.
Be very sensitive to it's performance.
I'd recommend using something like https://github.com/yeller/shoutout for this.

## Faster, more Validated Science

Whilst running experiments as maps is easy, and very flexible, looking keys up in maps ain't the fastest thing for the JVM
to optimize. Instead fineman offers a simple record wrapper for experiments, which dramatically changes performance:

```clojure
(science/make-it-faster! experiment) ; returns a record
```

TODO: benchmark dat science

## Rationale

Feature flags are *great* for rolling out production code changes gradually.
But they don't go far enough for changes to critical paths - there's nothing in them about comparing results, or comparing performance of each side.
That's where Fineman comes in.

## Non-Goals

Fineman leaves choice of metrics system, how to record mismatches, how to enable experiments for particular cases all up to you.

## Punted On

See `Non-Goals`

## Future Work

See `Non-Goals`

## Open Questions

See `Non-Goals`

Shoutout to Brandon Bloom

## License

Copyright © 2015 Tom Crayford

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
