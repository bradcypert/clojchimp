(ns clojchimp.client
  (require [clj-http.client :as httpclient]))

(defprotocol Client
  "Client for interfacing with MailChimp API."
  (GET [this url] "Makes a GET Request to url")
  (DELETE [this url] "Makes a DELETE Request to url")
  (POST [this url body] "Makes a POST Request to url with given data body")
  (PATCH [this url body] "Makes a PATCH Request to url with given data body")
  (generate-api-url [this api-key] "Returns the data-center prepended url from the Api-Key")
  (get-campaigns [this] "Returns all campaigns for the user")
  (get-campaign [this id] "Returns a specific campaign by ID")
  (delete-campaign [this id] "Deletes a current campaign by ID")
  (cancel-campaign [this id body] "Cancels a current campaign. MailChimp Pro only")
  (get-campaign-feedback [this id] "Gets feedback for a campign by its campaign ID")
  (get-campaign-feedback-by-id [this campId id] "Gets a specific feedback from a campaign by campaignID & feedbackID")
  (delete-campaign-feedback [this campId id] "Deletes a specific feedback item from a campaign by campaignID & feedbackID")
  (get-conversations [this] "Returns all conversations for the user")
  (get-conversation[this id] "Returns a specific conversation by ID")
  (get-conversation-messages [this campId] "Returns all messages for a given campaign ID")
  (get-conversation-message [this campId id] "Returns a conversation for a campaign, given campaignID and the ID of the conversation")
  (get-lists [this] "Returns all lists for the user")
  (get-list [this id] "Returns a specific list by it's ID")
  (delete-list [this id] "Deletes a list by ID")
  (create-list [this body] "Creates a new list")
  (update-list [this id body] "Updates a list by it's ID.")
  (get-list-abuse-reports [this id] "Gets the abuse reports for a specific list by it's ID.")
  (get-list-abuse-report [this listId id] "Get details for a specific report by ListID & ReportID")
  (get-list-activity [this id] "Gets activity for a specific list")
  (get-list-clients [this id] "Gets top email clients for a specific list")
  (get-list-growth-history [this id] "Gets growth history for a specific list")
  (get-list-growth-history-for-month [this listId month] "Gets growth history for a list for a specific month.")
  (create-member-for-list [this listId body] "Creates a new member and associates them with the provided listId")
  (get-members-for-list [this listId] "Gets all members for a given list"))

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
              :as :clojure
              :form-params body
              :content-type :json})))

  (PATCH [this endpoint body]
    (:body (httpclient/patch
             (apply str (generate-api-url this api-key) endpoint)
             {:basic-auth [user api-key]
              :as :clojure
              :form-params body
              :content-type :json})))

  (generate-api-url [_ api-key]
    (str "https://" (subs api-key
                          (.indexOf api-key "us")
                          (count api-key)) ".api.mailchimp.com/3.0"))

  (get-campaigns [this]
    (GET this "/campaigns"))

  (get-campaign [this id]
    (GET this '("/campaigns/" id)))

  (delete-campaign [this id]
    (DELETE this '("/campaigns/" id)))

  (cancel-campaign [this id body]
    (POST this '( "/campaigns/" id "/actions/cancel-send") body))

  (get-campaign-feedback [this id]
    (GET this '("/campaigns/" id "/feedback")))

  (get-campaign-feedback-by-id [this campId id]
    (GET this '("/campaigns/" campId "/feedback/" id)))

  (delete-campaign-feedback [this campId id]
    (DELETE this '("/campaigns/" campId "/feedback/" id)))

  (get-conversations [this]
    (GET this "/conversations"))

  (get-conversation [this id]
    (GET this '("/conversations/" id)))

  (get-conversation-messages [this campId]
    (GET this '("/conversations/" campId "/messages")))

  (get-conversation-message [this campId id]
    (GET this '("/conversations/" campId "/messages/" id)))

  (get-lists [this]
    (GET this "/lists"))

  (get-list [this id]
    (GET this '("/lists/" id)))

  (delete-list [this id]
    (DELETE this '("/lists/" id)))

  (create-list [this body]
    (POST this "/lists" body))

  (update-list [this id body]
    (PATCH this '("/lists/" id) body))

  (get-list-abuse-reports [this id]
    (GET this '("/lists/" id "/abuse-reports")))

  (get-list-abuse-report [this listId id]
    (GET this '("/lists/" listId "/abuse-reports/" id)))

  (get-list-activity [this id]
    (GET this '("/lists/" id "/activity")))

  (get-list-clients [this id]
    (GET this '("/list/" id "/clients")))

  (get-list-growth-history [this id]
    (GET this '("/list" id "/growth-history")))

  (get-list-growth-history-for-month [this listId id]
    (GET this '("/list" listId "/growth-history/" id)))

  (create-member-for-list [this listId body]
    (POST this '("/list" listId "/members") body))

  (get-members-for-list [this listId]
    (GET this '("/list" listId "/members"))))

(defn create-client [user api-key]
  (->ChimpClient user api-key))