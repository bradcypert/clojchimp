(ns clojchimp.client-test
  (:require [clojure.test :refer :all]
            [clojchimp.client :refer :all]
            [cheshire.core])
  (:use clj-http.fake))

(def client (create-client "flarb@flarb.com" "api-key-us1"))

(deftest clojchimp-tests
  (with-fake-routes {#"https:\/\/us1\.api\.mailchimp\.com\/3\.0\/([a-z\-\/\d]*)"
                     (fn [_] {:status 200 :body "{\"called\": true}"})}

    (testing "generate-api-url should return datacenter prefixed url"
      (is (= (generate-api-url client "api-key-us1") "https://us1.api.mailchimp.com/3.0")))

    (testing "get-campaigns should call appropriate url"
      (is (= (get-campaigns client) {:called true})))

    (testing "delete-campaigns should call appropriate url"
      (is (= (delete-campaign client 100) {:called true})))

    (testing "get-campaign should call appropriate url"
      (is (= (get-campaign client 100) {:called true})))

    (testing "cancel-campaign should call appropriate url"
      (is (= (cancel-campaign client 100 {}) {:called true})))))

