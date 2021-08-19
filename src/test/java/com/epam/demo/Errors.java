package com.epam.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.test.StepVerifier;

class Errors {

    @Test
    void staticFallbackValue() {
        Mono<Integer> num = Mono.just("num")
            .map(Integer::parseInt)
            .onErrorReturn(0);

        StepVerifier.create(num)
            .expectNext(0)
            .verifyComplete();
    }

    @Test
    void staticMethod() {
        Mono<Integer> num = Mono.just("num")
            .map(Integer::parseInt)
            .onErrorResume(ex -> findAnotherNumber());

        StepVerifier.create(num)
            .expectNext(10)
            .verifyComplete();
    }

    @Test
    void catchAndRethrow() {
        Mono<Integer> num = Mono.just("num")
            .map(Integer::parseInt)
            .onErrorMap(ex -> new RuntimeException("something went wrong!", ex));

        StepVerifier.create(num)
            .verifyErrorMatches(ex ->
                ex.getClass().equals(RuntimeException.class) &&
                ex.getCause().getClass().equals(NumberFormatException.class)
            );
    }

    @Test
    void doOnError() {
        Mono<Integer> num = Mono.just("num")
            .map(Integer::parseInt)
            .doOnError(ex -> notifyMyBoss());

        StepVerifier.create(num)
            .verifyError(NumberFormatException.class);
    }

    @Test
    void doFinally() {
        Mono<Integer> num = Mono.just("num")
            .map(Integer::parseInt)
            .doFinally(signalType -> {
                releaseResource();
            });

        StepVerifier.create(num)
            .verifyError(NumberFormatException.class);
    }


    private Mono<Integer> findAnotherNumber() {
        return Mono.just(10);
    }

    private void notifyMyBoss(){
        System.out.println("Error!!!");
    }

    private void releaseResource(){
        System.out.println("Release resource");
    }


}
