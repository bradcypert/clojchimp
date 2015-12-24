(ns clojchimp.client
  (require [clj-http.client :as httpclient]))

(defprotocol Client
  "Client for interfacing with MailChimp API."
  (GET [this url] "Makes a GET Request to url.")
  (DELETE [this url] "Makes a DELETE Request to url.")
  (POST [this url body] "Makes a POST Request to url with given data body.")
  (PATCH [this url body] "Makes a PATCH Request to url with given data body.")
  (generate-api-url [this api-key] "Returns the data-center prepended url from the Api-Key.")
  (get-campaigns [this] "Returns all campaigns for the user.")
  (get-campaign [this id] "Returns a specific campaign by ID.")
  (delete-campaign [this id] "Deletes a current campaign by ID.")
  (cancel-campaign [this id body] "Cancels a current campaign. MailChimp Pro only.")
  (get-campaign-feedback [this id] "Gets feedback for a campign by its campaign ID.")
  (get-campaign-feedback-by-id [this camp-id id] "Gets a specific feedback from a campaign by campaignID & feedbackID.")
  (delete-campaign-feedback [this camp-id id] "Deletes a specific feedback item from a campaign by campaignID & feedbackID.")
  (get-conversations [this] "Returns all conversations for the user.")
  (get-conversation [this id] "Returns a specific conversation by ID.")
  (get-conversation-messages [this camp-id] "Returns all messages for a given campaign ID.")
  (get-conversation-message [this camp-id id] "Returns a conversation for a campaign, given campaignID and the ID of the conversation.")
  (get-lists [this] "Returns all lists for the user.")
  (get-list [this id] "Returns a specific list by its ID.")
  (delete-list [this id] "Deletes a list by ID.")
  (create-list [this body] "Creates a new list.")
  (update-list [this id body] "Updates a list by its ID.")
  (get-list-abuse-reports [this id] "Gets the abuse reports for a specific list by its ID.")
  (get-list-abuse-report [this list-id id] "Get details for a specific report by ListID & ReportID.")
  (get-list-activity [this id] "Gets activity for a specific list.")
  (get-list-clients [this id] "Gets top email clients for a specific list.")
  (get-list-growth-history [this id] "Gets growth history for a specific list.")
  (get-list-growth-history-for-month [this list-id month] "Gets growth history for a list for a specific month.")
  (create-member-for-list [this list-id body] "Creates a new member and associates them with the provided listId.")
  (get-members-for-list [this list-id] "Gets all members for a given list."))

(defrecord ChimpClient [^String user ^String api-key]
  Client
  (GET [this endpoint]
    (:body (httpclient/get
             (apply str (generate-api-url this api-key) endpoint)
             {:basic-auth [user api-key]
              :as :json})))

  (DELETE [this endpoint]
    (:body (httpclient/delete
             (apply str (generate-api-url this api-key) endpoint)
             {:basic-auth [user api-key]
              :as :json})))

  (POST [this endpoint body]
    (:body (httpclient/post
             (apply str (generate-api-url this api-key) endpoint)
             {:basic-auth [user api-key]
              :as :json
              :form-params body
              :content-type :json})))

  (PATCH [this endpoint body]
    (:body (httpclient/patch
             (apply str (generate-api-url this api-key) endpoint)
             {:basic-auth [user api-key]
              :as :json
              :form-params body
              :content-type :json})))

  (generate-api-url [_ api-key]
    (str "https://" (subs api-key
                          (.indexOf api-key "us")
                          (count api-key)) ".api.mailchimp.com/3.0"))

  (get-campaigns [this]
    (GET this "/campaigns"))

  (get-campaign [this id]
    (GET this (list "/campaigns/" id)))

  (delete-campaign [this id]
    (DELETE this (list "/campaigns/" id)))

  (cancel-campaign [this id body]
    (POST this (list "/campaigns/" id "/actions/cancel-send") body))

  (get-campaign-feedback [this id]
    (GET this (list "/campaigns/" id "/feedback")))

  (get-campaign-feedback-by-id [this camp-id id]
    (GET this (list "/campaigns/" camp-id "/feedback/" id)))

  (delete-campaign-feedback [this camp-id id]
    (DELETE this (list "/campaigns/" camp-id "/feedback/" id)))

  (get-conversations [this]
    (GET this "/conversations"))

  (get-conversation [this id]
    (GET this (list "/conversations/" id)))

  (get-conversation-messages [this camp-id]
    (GET this (list "/conversations/" camp-id "/messages")))

  (get-conversation-message [this camp-id id]
    (GET this (list "/conversations/" camp-id "/messages/" id)))

  (get-lists [this]
    (GET this "/lists"))

  (get-list [this id]
    (GET this (list "/lists/" id)))

  (delete-list [this id]
    (DELETE this '("/lists/" id)))

  (create-list [this body]
    (POST this "/lists" body))

  (update-list [this id body]
    (PATCH this (list "/lists/" id) body))

  (get-list-abuse-reports [this id]
    (GET this (list "/lists/" id "/abuse-reports")))

  (get-list-abuse-report [this list-id id]
    (GET this (list "/lists/" list-id "/abuse-reports/" id)))

  (get-list-activity [this id]
    (GET this (list "/lists/" id "/activity")))

  (get-list-clients [this id]
    (GET this (list "/list/" id "/clients")))

  (get-list-growth-history [this id]
    (GET this (list "/list" id "/growth-history")))

  (get-list-growth-history-for-month [this list-id id]
    (GET this (list "/list" list-id "/growth-history/" id)))

  (create-member-for-list [this list-id body]
    (POST this (list "/list" list-id "/members") body))

  (get-members-for-list [this list-id]
    (GET this (list "/list" list-id "/members"))))

(defn create-client [user api-key]
  (->ChimpClient user api-key))
