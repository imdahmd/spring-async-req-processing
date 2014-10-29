package com.imdhmd.spring.async.controllers;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import static java.lang.String.format;

@Controller
public class AsyncReqHandler {

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public DeferredResult<String> handle(@PathVariable("id") final String id) {
        System.out.println(format("Handling request for %s, thread id: %s", id, Thread.currentThread().getId()));
        final DeferredResult<String> deferredResult = new DeferredResult<>();

        defer(id, deferredResult);

        System.out.println(format("I'm returning from %s, thread id: %s", id, Thread.currentThread().getId()));
        return deferredResult;
    }

    private void defer(final String id, final DeferredResult<String> deferredResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    System.out.println(format("Setting result for %s, thread id: %s", id, Thread.currentThread().getId()));
                    deferredResult.setResult(format("Result for %s\r\n", id));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/wiki")
    @ResponseBody
    public DeferredResult<String> wiki(@RequestParam("q") final String q) throws URISyntaxException, IOException {
        System.out.println(format("Wiki query for %s, thread id: %s", q, Thread.currentThread().getId()));
        final DeferredResult<String> deferredResult = new DeferredResult<>();

        AsyncClientHttpRequest asyncRequest = new HttpComponentsAsyncClientHttpRequestFactory()
                .createAsyncRequest(new URI(format("http://en.wikipedia.org/w/api.php?format=json&action=query&titles=%s", q)), HttpMethod.GET);
        asyncRequest.executeAsync().addCallback(new SuccessCallback<ClientHttpResponse>() {
            @Override
            public void onSuccess(ClientHttpResponse clientHttpResponse) {
                try {
                    Thread thread = Thread.currentThread();
                    System.out.println(
                            format("Setting result for wiki query %s, thread id: %s, thread name: %s, thread: %s, isDaemon: %s",
                                    q, thread.getId(), thread.getName(), thread.toString(), thread.isDaemon()));

                    Scanner scanner = new Scanner(clientHttpResponse.getBody());
                    deferredResult.setResult(scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new FailureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                deferredResult.setErrorResult(throwable);
            }
        });

        System.out.println(format("I'm returning from %s, thread id: %s", q, Thread.currentThread().getId()));
        return deferredResult;
    }
}
