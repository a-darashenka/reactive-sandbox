package com.epam.demo.webclient;

import reactor.core.publisher.Mono;

public class WebClient {

    public Mono<String> getResult(){
        System.out.println("getResult() in " + Thread.currentThread().getName());
        return Mono.just("Result");
    }

    public Mono<String> getAnotherResult(){
        System.out.println("getAnotherResult() in " + Thread.currentThread().getName());
        return Mono.just("Another Result");
    }

    public Mono<String> getEmptyResult(){
        System.out.println("getEmptyResult() in " + Thread.currentThread().getName());
        return Mono.just("");
    }
}
