# clojchimp
[![Clojars Project](http://clojars.org/clojchimp/latest-version.svg)](http://clojars.org/clojchimp)

[![Build Status](https://travis-ci.org/bradcypert/clojchimp.svg?branch=master)](https://travis-ci.org/bradcypert/clojchimp)

A lightweight Clojure library designed to interface with MailChimp's API.

## Usage

```clojure
;; Defining the client
(def client (create-client "flarb@flarb.com" "api-key"))

;; Request all campaigns
(get-campaigns client)

;; Create a new campaign
(create-campaign client {:name "campaign-name"})
```

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
