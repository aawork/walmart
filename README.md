
### URL and bookmarks

* search query with single term:
* search with sentence: 
    * [http://rockvillesoft.com/walmart/#/search/slim+shirt](http://rockvillesoft.com/walmart/#/search/slim+shirt)
    * __fake news__ :" [http://rockvillesoft.com/walmart/#/search/fake+news](http://rockvillesoft.com/walmart/#/search/fake+news)
    * __friends season #1__ : [http://rockvillesoft.com/walmart/#/search/friends+season+%231](http://rockvillesoft.com/walmart/#/search/friends+season+%231)
    * __©__ : [http://rockvillesoft.com/walmart/#/search/+©+](http://rockvillesoft.com/walmart/#/search/+©+)
     
    



### Loading Corner Cases

* Initial Loading

![Image of Loading](docs/load-process.png){:height="50%" width="50%"}

* Load completed successfully but without any information (empty response)

![Image of Loading](docs/load-empty.png)


* Communication Issues
    * Backend server (or cluster is down: in the non DEMO case suppose to be repeat-N logic instead of error)
    * Load balancer failures
    * Network issues

![Image of Server Failure](docs/load-server-error.png)

* Server API Issues (not applicable in current implementation)
    * Incorrect client implementation (applicable only during development or migrations)
    * Client (or 3d party system) passes wrong arguments or violate API protocol. While thinking on examples of incorrect API requests I found this one: `$#@` which broke ___walmart.com___
       
![Image of API Error on walmart.com](docs/load-api-error-walmart.png)

it should be handled properly, so user (or system) will be capable to continue

Here is example behavior in case when HTML input element has no length limitation:

![Image of API Error](docs/load-api-error.png)

* Reload Data
    
In this application it is possible to click on `recommendation` and navigate to another product



### Out of scope

* Localization and text/ui orientation
* Accessibility: label areas, 
       




    
    