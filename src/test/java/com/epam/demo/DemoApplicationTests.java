package com.epam.demo;

import com.epam.demo.webclient.WebClient;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

class DemoApplicationTests {

    WebClient webClient = new WebClient();

    @Test
    void createMono() {
        String source = "Test String";

        Mono<String> monoFromString = Mono.just(source)
            .log();
        Mono<String> monoFromCallable = Mono.fromCallable(() -> source)
            .log();
        Mono<String> monoFromCompletionStage = Mono.fromCompletionStage(CompletableFuture.completedFuture(source))
            .log();
    }

    @Test
    void safeMonoJust() {
        Mono<String> resultMono = webClient.getResult()
            .flatMap(result -> {
                if (result == null) {
                    return webClient.getAnotherResult();
                }
                return Mono.just(result);
            });

        StepVerifier.create(resultMono)
            .expectNext("Result")
            .verifyComplete();
    }

    @Test
    void unsafeMonoJust() {
        Mono<String> stringMono = webClient.getEmptyResult()
            .flatMap(result -> {
                if (result.isEmpty()) {
                    return Mono.just(blockingInvocation())
                        .doOnNext(res -> System.out.println("Completed"));
                }
                return Mono.just(result);
            });

        StepVerifier.create(stringMono)
            .expectNext("Result from external source")
            .verifyComplete();
    }

    @Test
    void publishAndSubscribe(){
        Mono<String> result = Mono.just("hello")
            .doOnNext(v -> System.out.println("just " + Thread.currentThread().getName()))
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(v -> System.out.println("publish " + Thread.currentThread().getName()))
            .subscribeOn(Schedulers.parallel());

        StepVerifier.create(result)
            .expectNext("hello")
            .verifyComplete();
    }

    @Test
    void blockingCase(){
        Mono<String> result = Mono.fromSupplier(() -> blockingInvocation())
            .subscribeOn(Schedulers.boundedElastic());

        StepVerifier.create(result)
            .expectNext("Result from external source")
            .verifyComplete();
    }


    private String blockingInvocation() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("blockingInvocation() in " + Thread.currentThread().getName());
        return "Result from external source";
    }

}
