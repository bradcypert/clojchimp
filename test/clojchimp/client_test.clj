(ns clojchimp.client-test
  (:require [clojure.test :refer :all]
            [clojchimp.client :refer :all])
  (:use clj-http.fake))

(def client (create-client "flarb@flarb.com" "api-key-us1"))

(deftest clojchimp-tests
  (testing "generate-api-url should return datacenter prefixed url"
    (is (= (generate-api-url client "api-key-us1") "https://us1.api.mailchimp.com/3.0")))

  (testing "get-campaigns should call appropriate url"
    (with-fake-routes {"https://us1.api.mailchimp.com/3.0/campaigns"
                       (fn [_] {:status 200 :body "campaign url called"})}
                        (is (= (get-campaigns client) "campaign url called")))))