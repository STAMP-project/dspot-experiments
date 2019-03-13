package com.baeldung.clientrx;


import MediaType.APPLICATION_JSON;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.rx.rxjava.RxObservableInvoker;
import org.glassfish.jersey.client.rx.rxjava.RxObservableInvokerProvider;
import org.glassfish.jersey.client.rx.rxjava2.RxFlowableInvoker;
import org.glassfish.jersey.client.rx.rxjava2.RxFlowableInvokerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;


/**
 *
 *
 * @author baeldung
 */
public class ClientOrchestrationIntegrationTest {
    private Client client = ClientBuilder.newClient();

    private WebTarget userIdService = client.target("http://localhost:8080/id-service/ids");

    private WebTarget nameService = client.target("http://localhost:8080/name-service/users/{userId}/name");

    private WebTarget hashService = client.target("http://localhost:8080/hash-service/{rawValue}");

    private Logger logger = LoggerFactory.getLogger(ClientOrchestrationIntegrationTest.class);

    private String expectedUserIds = "[1,2,3,4,5,6]";

    private List<String> expectedNames = Arrays.asList("n/a", "Thor", "Hulk", "BlackWidow", "BlackPanther", "TheTick", "Hawkeye");

    private List<String> expectedHashValues = Arrays.asList("roht1", "kluh2", "WodiwKcalb3", "RehtnapKclab4", "kciteht5", "eyekwah6");

    @Rule
    public WireMockRule wireMockServer = new WireMockRule();

    @Test
    public void callBackOrchestrate() throws InterruptedException {
        List<String> receivedHashValues = new ArrayList<>();
        final CountDownLatch completionTracker = new CountDownLatch(expectedHashValues.size());// used to keep track of the progress of the subsequent calls

        userIdService.request().accept(APPLICATION_JSON).async().get(new javax.ws.rs.client.InvocationCallback<List<Long>>() {
            @Override
            public void completed(List<Long> employeeIds) {
                logger.info("[CallbackExample] id-service result: {}", employeeIds);
                employeeIds.forEach(( id) -> {
                    // for each employee ID, get the name
                    nameService.resolveTemplate("userId", id).request().async().get(new javax.ws.rs.client.InvocationCallback<String>() {
                        @Override
                        public void completed(String response) {
                            logger.info("[CallbackExample] name-service result: {}", response);
                            hashService.resolveTemplate("rawValue", (response + id)).request().async().get(new javax.ws.rs.client.InvocationCallback<String>() {
                                @Override
                                public void completed(String response) {
                                    logger.info("[CallbackExample] hash-service result: {}", response);
                                    receivedHashValues.add(response);
                                    completionTracker.countDown();
                                }

                                @Override
                                public void failed(Throwable throwable) {
                                    logger.warn("[CallbackExample] An error has occurred in the hashing request step!", throwable);
                                }
                            });
                        }

                        @Override
                        public void failed(Throwable throwable) {
                            logger.warn("[CallbackExample] An error has occurred in the username request step!", throwable);
                        }
                    });
                });
            }

            @Override
            public void failed(Throwable throwable) {
                logger.warn("[CallbackExample] An error has occurred in the userId request step!", throwable);
            }
        });
        // wait for async calls to complete
        try {
            // wait for inner requests to complete in 10 seconds
            if (!(completionTracker.await(10, TimeUnit.SECONDS))) {
                logger.warn("[CallbackExample] Some requests didn't complete within the timeout");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted!", e);
        }
        assertThat(receivedHashValues).containsAll(expectedHashValues);
    }

    @Test
    public void rxOrchestrate() throws InterruptedException {
        List<String> receivedHashValues = new ArrayList<>();
        final CountDownLatch completionTracker = new CountDownLatch(expectedHashValues.size());// used to keep track of the progress of the subsequent calls

        CompletionStage<List<Long>> userIdStage = userIdService.request().accept(APPLICATION_JSON).rx().get(new javax.ws.rs.core.GenericType<List<Long>>() {}).exceptionally(( throwable) -> {
            logger.warn("[CompletionStageExample] An error has occurred");
            return null;
        });
        userIdStage.thenAcceptAsync(( employeeIds) -> {
            logger.info("[CompletionStageExample] id-service result: {}", employeeIds);
            employeeIds.forEach((Long id) -> {
                CompletableFuture<String> completable = nameService.resolveTemplate("userId", id).request().rx().get(String.class).toCompletableFuture();
                completable.thenAccept((String userName) -> {
                    logger.info("[CompletionStageExample] name-service result: {}", userName);
                    hashService.resolveTemplate("rawValue", (userName + id)).request().rx().get(String.class).toCompletableFuture().thenAcceptAsync(( hashValue) -> {
                        logger.info("[CompletionStageExample] hash-service result: {}", hashValue);
                        receivedHashValues.add(hashValue);
                        completionTracker.countDown();
                    }).exceptionally(( throwable) -> {
                        logger.warn("[CompletionStageExample] Hash computation failed for {}", id);
                        completionTracker.countDown();
                        return null;
                    });
                });
            });
        });
        // wait for async calls to complete
        try {
            // wait for inner requests to complete in 10 seconds
            if (!(completionTracker.await(10, TimeUnit.SECONDS))) {
                logger.warn("[CallbackExample] Some requests didn't complete within the timeout");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted!", e);
        }
        assertThat(receivedHashValues).containsAll(expectedHashValues);
    }

    @Test
    public void observableJavaOrchestrate() throws InterruptedException {
        List<String> receivedHashValues = new ArrayList<>();
        final CountDownLatch completionTracker = new CountDownLatch(expectedHashValues.size());// used to keep track of the progress of the subsequent calls

        Observable<List<Long>> observableUserIdService = userIdService.register(RxObservableInvokerProvider.class).request().accept(APPLICATION_JSON).rx(RxObservableInvoker.class).get(new javax.ws.rs.core.GenericType<List<Long>>() {}).asObservable();
        observableUserIdService.subscribe((List<Long> employeeIds) -> {
            logger.info("[ObservableExample] id-service result: {}", employeeIds);
            Observable.from(employeeIds).subscribe(( id) -> // gotten the name for the given
            // userId
            nameService.register(.class).resolveTemplate("userId", id).request().rx(.class).get(.class).asObservable().doOnError(( throwable) -> {
                logger.warn("[ObservableExample] An error has occurred in the username request step {}", throwable.getMessage());
            }).subscribe(( userName) -> {
                logger.info("[ObservableExample] name-service result: {}", userName);
                // gotten the hash value for
                // userId+username
                hashService.register(.class).resolveTemplate("rawValue", (userName + id)).request().rx(.class).get(.class).asObservable().doOnError(( throwable) -> {
                    logger.warn("[ObservableExample] An error has occurred in the hashing request step {}", throwable.getMessage());
                }).subscribe(( hashValue) -> {
                    logger.info("[ObservableExample] hash-service result: {}", hashValue);
                    receivedHashValues.add(hashValue);
                    completionTracker.countDown();
                });
            }));
        });
        // wait for async calls to complete
        try {
            // wait for inner requests to complete in 10 seconds
            if (!(completionTracker.await(10, TimeUnit.SECONDS))) {
                logger.warn("[CallbackExample] Some requests didn't complete within the timeout");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted!", e);
        }
        assertThat(receivedHashValues).containsAll(expectedHashValues);
    }

    @Test
    public void flowableJavaOrchestrate() throws InterruptedException {
        List<String> receivedHashValues = new ArrayList<>();
        final CountDownLatch completionTracker = new CountDownLatch(expectedHashValues.size());// used to keep track of the progress of the subsequent calls

        Flowable<List<Long>> userIdFlowable = userIdService.register(RxFlowableInvokerProvider.class).request().rx(RxFlowableInvoker.class).get(new javax.ws.rs.core.GenericType<List<Long>>() {});
        userIdFlowable.subscribe((List<Long> employeeIds) -> {
            logger.info("[FlowableExample] id-service result: {}", employeeIds);
            Flowable.fromIterable(employeeIds).subscribe(( id) -> {
                // gotten the name for the given userId
                nameService.register(.class).resolveTemplate("userId", id).request().rx(.class).get(.class).doOnError(( throwable) -> {
                    logger.warn("[FlowableExample] An error has occurred in the username request step {}", throwable.getMessage());
                }).subscribe(( userName) -> {
                    logger.info("[FlowableExample] name-service result: {}", userName);
                    // gotten the hash value for userId+username
                    hashService.register(.class).resolveTemplate("rawValue", (userName + id)).request().rx(.class).get(.class).doOnError(( throwable) -> {
                        logger.warn(" [FlowableExample] An error has occurred in the hashing request step!", throwable);
                    }).subscribe(( hashValue) -> {
                        logger.info("[FlowableExample] hash-service result: {}", hashValue);
                        receivedHashValues.add(hashValue);
                        completionTracker.countDown();
                    });
                });
            });
        });
        // wait for async calls to complete
        try {
            // wait for inner requests to complete in 10 seconds
            if (!(completionTracker.await(10, TimeUnit.SECONDS))) {
                logger.warn("[CallbackExample] Some requests didn't complete within the timeout");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted!", e);
        }
        assertThat(receivedHashValues).containsAll(expectedHashValues);
    }
}

