package com.epam.demo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class Other {

    @Test
    void zip2Mono() {
        Mono<String> string = getString();
        Mono<Integer> integer = getInteger();

        Mono<String> result = Mono.zip(string, integer)
            .map(tuple -> tuple.getT1() + " " + tuple.getT2());

        StepVerifier.create(result)
            .expectNext("String 1")
            .verifyComplete();
    }

    @Test
    void zip3Mono() {
        Mono<String> string = getString();
        Mono<Integer> integer = getInteger();
        Mono<Integer> integer2 = getInteger();

        Mono<String> result = Mono.zip(string, integer, integer2)
            .map(tuple -> tuple.getT1() + " " + tuple.getT2() + " " + tuple.getT3());

        StepVerifier.create(result)
            .expectNext("String 1 1")
            .verifyComplete();
    }


    @Test
    void zipList() {
        List<Mono<String>> monoList = Arrays.asList(
            Mono.just("1"),
            Mono.just("2"),
            Mono.just("3")
        );

        Mono<List<String>> listMono = Mono.zip(monoList,
            elements -> Arrays.stream(elements)
                .map(elem -> (String) elem)
                .collect(Collectors.toList())
        );

        StepVerifier.create(listMono)
            .expectNextMatches(list -> list.size() == 3)
            .verifyComplete();
    }

    @Test
    void emptyCase() {
        String key = "key";

        Mono<String> actualResult = getFromCache(key)
            .switchIfEmpty(Mono.defer(() -> getFromService()));

        StepVerifier.create(actualResult)
            .expectNext("value")
            .verifyComplete();
    }

    private Mono<String> getString() {
        System.out.println("getString method");
        return Mono.just("String");
    }

    private Mono<Integer> getInteger() {
        System.out.println("getInteger method");
        return Mono.just(1);
    }

    private Mono<String> getFromCache(String key) {
        System.out.println("getFromCache()");
        if ("key".equalsIgnoreCase(key)) {
            return Mono.just("value");
        }
        return Mono.empty();
    }

    private Mono<String> getFromService() {
        System.out.println("getFromService()");
        return Mono.just("another value");
    }
}
