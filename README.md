### Proof of concept - Asynchronous request handling using spring mvc

##### Testing by invoking a new thread:
1. Run the app: `mvn jetty:run` and notice the logs (on the console itself)
1. Open a new terminal and split it to four panes (like in iterm cmd+t and then cmd+d 3 times)
1. Execute the following 4 curl requests from those 4 respective terminal panes at the same time (i do this in iterm using alt+cmd+i -> input goes to all open terminals)
  * curl localhost:8888/violet
  * curl localhost:8888/yellow
  * curl localhost:8888/guitar
  * curl localhost:8888/music
1. Notice that 4 logs of the kind "Handling request for violet" show up on the jetty console immediately
1. After 2.5 seconds logs of the kind "Setting result for violet" shows up for all the 4 requests on the jetty logs
1. At this point, i.e after 2.5 secs, all the curl commands return with the response from server with a text reponse like "Result for violet"

##### Conclusion:
Support for asynchronous request handling allows you to defer the response computation to another thread, and frees up servlet thread to handle consequtive requests. After a thread is done with its computation, the response is correctly mapped back to the original request.

References:
* http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-async
* http://stackoverflow.com/questions/13646428/issue-with-making-a-controller-method-asynchronous-with-spring-3-2-rc2

##### Update (29 Oct, 2014)

* Added an async http call (via spring's http client) to an external service (wiki API)
* If i set the number worker threads (that handle request) to 1, i can see that all the requests are handled immediately but:
  * The callback from async http request is being handled by a new thread every time
  * Who is spawning this new thread?
  * Is not this async http call supposed to be via evented io?
