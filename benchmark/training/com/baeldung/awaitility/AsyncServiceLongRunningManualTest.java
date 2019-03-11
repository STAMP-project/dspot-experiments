package com.baeldung.awaitility;


import Duration.FIVE_HUNDRED_MILLISECONDS;
import Duration.FIVE_SECONDS;
import Duration.ONE_HUNDRED_MILLISECONDS;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Test;


public class AsyncServiceLongRunningManualTest {
    private AsyncService asyncService;

    @Test
    public void givenAsyncService_whenInitialize_thenInitOccurs1() {
        asyncService.initialize();
        Callable<Boolean> isInitialized = asyncService::isInitialized;
        await().until(isInitialized);
    }

    @Test
    public void givenAsyncService_whenInitialize_thenInitOccurs2() {
        asyncService.initialize();
        Callable<Boolean> isInitialized = asyncService::isInitialized;
        await().atLeast(ONE_HUNDRED_MILLISECONDS).atMost(FIVE_SECONDS).with().pollInterval(ONE_HUNDRED_MILLISECONDS).until(isInitialized);
    }

    @Test
    public void givenAsyncService_whenInitialize_thenInitOccurs_withDefualts() {
        setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
        setDefaultPollDelay(Duration.ZERO);
        setDefaultTimeout(Duration.ONE_MINUTE);
        asyncService.initialize();
        await().until(asyncService::isInitialized);
    }

    @Test
    public void givenAsyncService_whenInitialize_thenInitOccurs_withProxy() {
        asyncService.initialize();
        await().untilCall(to(asyncService).isInitialized(), Matchers.equalTo(true));
    }

    @Test
    public void givenAsyncService_whenInitialize_thenInitOccurs3() {
        asyncService.initialize();
        await().until(fieldIn(asyncService).ofType(boolean.class).andWithName("initialized"), Matchers.equalTo(true));
    }

    @Test
    public void givenValue_whenAddValue_thenValueAdded() {
        asyncService.initialize();
        await().until(asyncService::isInitialized);
        long value = 5;
        asyncService.addValue(value);
        await().until(asyncService::getValue, Matchers.equalTo(value));
    }

    @Test
    public void givenAsyncService_whenGetValue_thenExceptionIgnored() {
        asyncService.initialize();
        await().atMost(FIVE_SECONDS).atLeast(FIVE_HUNDRED_MILLISECONDS).until(asyncService::getValue, Matchers.equalTo(0L));
    }
}

