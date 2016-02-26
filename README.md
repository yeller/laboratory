> This isn't SCIENCE this is PRODUCTION

# laboratory

A Clojure library designed to help you experiment in production.
https://github.com/github/scientist, but for Clojure (api and readme liberally stolen)

## Usage

```clojure
(require '[laboratory.experiment :as science])

(def my-experiment
  {:name "widget-permissions"
   :use (fn [widget user] (check-user? widget user))
   :try (fn [widget user] (allowed-to? :read user widget))
   :publish prn})

(science/run my-experiment widget user)
```

laboratory runs experiments as *functions*.
Whatever arguments you pass to `run` are also passed to `:try` and `:use`.
The function under `:use` is the "control" (the original code you used to have).
The function under `:try` is the "experiment" (the new code that you want to compare).

Experiments are just maps with some data and functions in them.
There are a lot of options though. `:use`, `:try` and `:name` are the only required keys,
but experiments won't run if their results aren't published.

### Making Science Useful

The above example will run, but it's only printing out results.
To make it more useful, the `:publish` function could be enhanced:

```clojure
:publish (fn [result] ; whatever you want to do on results happens here
         )
```

## Results

Results passed to `:publish` are a Record:

```
{:name "widget-permissions"
 :experiment {the original experiment map/record}
 :args [a vector of the args passed to the use/try functions]
  :control
  {
    :metrics {:duration-ns 10} ; number of nanoseconds the control took
    :value true  ; whatever value your :use function returned
  }
  :candidate
  {
    :metrics {:duration-ns 3}  ; number of nanoseconds the candidate took
    :value true  ; whatever value your :try function returned
  }
}
```

## Ramping up Experiments

### Deciding to enable an experiment

To control if your `:try` function runs, you can pass a `:enabled` function in an experiment:

```clojure
:enabled (fn [widget user] (is-staff? user))
```

Note that this function will be called for every invocation of every experiment.
Be very sensitive to it's performance.
Feature flags are recommended - consider using something like https://github.com/yeller/shoutout for this.

### User-defined metrics

Experiments can also execute user-defined metrics for the control and the candidate.
Metrics are 0-arity functions that return some number.  Metrics are called before
and after executing the function under inspection, and the difference is logged
in the result's `:metrics`.

An experiment will take a map of additional metrics.  Additional metrics will
have an impact on experiment execution time - be mindful of performance.
Here is an experiment that calculates a very crude memory metric:

```clojure
(require '[laboratory.experiment :as science])

(def my-experiment
  {:name "widget-permissions"
   :use (fn [widget user] (check-user? widget user))
   :try (fn [widget user] (allowed-to? :read user widget))
   :publish prn
   :metrics {:bytes-used #(- (.totalMemory (Runtime/getRuntime))
                             (.freeMemory (Runtime/getRuntime)))}})

(science/run my-experiment widget user)
```

## Faster, more Validated Science

Defining experiments as maps is easy and very flexible, however, their impact on the JVM will be noticeable.
You may optionally use the `Experiment` record to enhance performance, but you
must supply all experiment keys `[:enabled :publish :metrics :use :try]`:

```clojure
(science/map->Experiment my-experiment) ; returns a record
```

## Rationale

Feature flags are *great* for rolling out production code changes gradually.
But they don't go far enough for changes to critical paths - there's nothing in them about comparing results, or comparing performance of each side.
That's where laboratory comes in.

## Non-Goals

laboratory leaves choice of metrics system, how to record mismatches, how to enable experiments for particular cases all up to you.

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
