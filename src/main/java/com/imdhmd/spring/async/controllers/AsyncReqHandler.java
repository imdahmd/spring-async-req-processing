package com.imdhmd.spring.async.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import static java.lang.String.format;

@Controller
public class AsyncReqHandler {

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public DeferredResult<String> handle(@PathVariable("id") final String id){
        System.out.println(format("Handling request for %s", id));
        final DeferredResult<String> deferredResult = new DeferredResult<String>();

        defer(id, deferredResult);

        return deferredResult;
    }

    private void defer(final String id, final DeferredResult<String> deferredResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    System.out.println(format("Setting result for %s", id));
                    deferredResult.setResult(format("Result for %s\r\n", id));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
