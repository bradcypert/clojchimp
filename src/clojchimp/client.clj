(ns clojchimp.client
  (require '[clj-http.client :as httpclient]))

(defprotocol Client
  "Client for interfacing with MailChimp API."
  (get [this url] "Makes a GET Request to url")
  (delete [this url] "Makes a DELETE Request to url")
  (post [this url body] "Makes a POST Request to url with given data body")
  (patch [this url body] "Makes a PATCH Request to url with given data body")
  (generate-api-url [this api-key] "Returns the data-center prepended url from the Api-Key")
  (get-campaigns [this] "Returns all campaigns for the user")
  (get-campaign [this id] "Returns a specific campaign by ID")
  (delete-campaign [this id] "Deletes a current campaign by ID")
  (cancel-campaign [this id] "Cancels a current campaign. MailChimp Pro only")
  (get-campaign-feedback [this id] "Gets feedback for a campign by its campaign ID")
  (get-campaign-feedback [this campId id] "Gets a specific feedback from a campaign by campaignID & feedbackID")
  (delete-campaign-feedback [this campId id] "Deletes a specific feedback item from a campaign by campaignID & feedbackID")
  (get-conversations [this] "Returns all conversations for the user")
  (get-conversation[this id] "Returns a specific conversation by ID")
  (get-conversation-messages [this campId] "Returns all messages for a given campaign ID")
  (get-conversation-message [this campId id] "Returns a conversation for a campaign, given campaignID and the ID of the conversation")
  (get-lists [this] "Returns all lists for the user")
  (get-list [this id] "Returns a specific list by it's ID")
  (delete-list [this id] "Deletes a list by ID"))

(defrecord ChimpClient [user api-key]
  Client
  (get [this url]
    httpclient/get url {:basic-auth [user api-key]
                        :as :clojure})

  (delete [this url]
    httpclient/delete url {:basic-auth [user api-key]
                           :as :clojure})

  (post [this url body]
    httpclient/post url {:basic-auth [user api-key]
                         :as :clojure
                         :form-params body
                         :content-type :json})

  (patch [this url body]
    httpclient/patch url {:basic-auth [user api-key]
                          :as :clojure
                          :form-params body
                          :content-type :json})

  (generate-api-url [this api-key]
    (str "https://" (subs api-key
                          (.indexOf api-key "us")
                          (count api-key)) ".api.mailchimp.com/3.0"))

  (get-campaigns [this]
    (get this
         (str (generate-api-url this api-key) "/campaigns")))

  (get-campaign [this id]
    (get this
         (str (generate-api-url this api-key) "/campaigns/" id)))

  (delete-campaign [this id]
    (delete this
            (str (generate-api-url this api-key) "/campaigns/" id)))

  (cancel-campaign [this id]
    (post this
          (str (generate-api-url this api-key) "/campaigns/" id "/actions/cancel-send") {}))

  (get-campaign-feedback [this id]
    (get this
         (str (generate-api-url this api-key) "/campaigns/" id "/feedback")))

  (get-campaign-feedback [this campId id]
    (get this
         (str (generate-api-url this api-key) "/campaigns/" campId "/feedback/" id)))

  (delete-campaign-feedback [this campId id]
    (delete this
            (str (generate-api-url this api-key) "/campaigns/" campId "/feedback/" id)))

  (get-conversations [this]
    (get this
         (str (generate-api-url this api-key) "/conversations")))

  (get-conversation [this id]
    (get this
         (str (generate-api-url this api-key) "/conversations/" id)))

  (get-conversation-messages [this campId]
    (get this
         (str (generate-api-url this api-key) "/conversations/" id "/messages")))

  (get-conversation-message [this campId id]
    (get this
         (str (generate-api-url this api-key) "/conversations/" campId "/messages/" id)))

  (get-lists [this]
    (get this
         (str (generate-api-url this api-key) "/lists")))

  (get-list [this id]
    (get this
         (str (generate-api-url this api-key) "/lists/" id)))

  (delete-list [this id]
    (delete this
            (str (generate-api-url this api-key) "/lists/" id))))

(defn create-client [user api-key]
  (-> Client user api-key))

;; Use Case
;; (get-campaigns (create-client "flarb@flarb.com" "ABC123us4"))
;;
;; (create-campaign
;;   (create-client "flarb@flarb.com" "ABC123us4) {:name "campaign-name"})
;;
;; (-> (create-client "flarb@flarb.com" "ABC123us4")
;;     (get-campaigns))
;;
;;
;;
;; Defining the client
;;
;; (def client (create-client "flarb@flarb.com" "ABC123us4"))
;;
;; (get-campaigns client)
;;
;; (create-campaign client {:name "campaign-name"})
