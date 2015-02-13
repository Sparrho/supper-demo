(ns supper.http-async
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [clojure.set]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [supper.util :as util]
            [supper.settings :as settings]
            [supper.state :as state]))

(def api-base (str state/api-host settings/api-base))

(defn- api!
  "Send a request to the API, immediately returning a core.async channel which will contain the response"
  [{:keys [method params with-credentials? session-id] :or {method :get}}]
  (let [http-methods {:get http/get
                      :put http/put
                      :post http/post
                      :delete http/delete}
        url api-base]
    ((get http-methods method) url {:headers {"X-CSRFToken" (util/get-cookie "csrftoken")
                                              "Accept" "*/*"
                                              "sessionid" session-id}
                                    :with-credentials? with-credentials?
                                    (if (= method :get)
                                      :query-params
                                      :json-params) params})))

(defn search-wiki!
  [search-term]
  (api! {:method :get
         :params {:format "json"
                  :language "en"
                  :action "wbsearchentities"
                  :search search-term}}))

(defn append-new-search!
  [search-term cursor]
  (let [result-chan (search-wiki! search-term)]
    (go
     (let [result (<! result-chan)]
       (if (= (:status result) 200)
         (om/transact! (util/content-ref-cursor cursor) #(conj % (:body result))))))))

(defn initialise-search-results
  "Initialise the state atom without relying on any Om functionality. This allows the state to be pre-built
   for the server side."
  [state-atom callback]
  (let [search-set (get-in @state-atom [:page :uri-params :search])
        result-channels (for
                          [search-term search-set]
                          (search-wiki! search-term))]
    (go
     (doseq
       [result-chan result-channels]
       (let [result (<! result-chan)]
         (if (= (:status result) 200)
           (swap! state-atom
                  (fn [av result-body]
                    (assoc av :wiki-content (conj (:wiki-content av) result-body)))
                  (:body result)))))
     (callback))))
