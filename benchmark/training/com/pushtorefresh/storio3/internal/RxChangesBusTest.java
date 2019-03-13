package com.pushtorefresh.storio3.internal;


import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class RxChangesBusTest {
    @Test
    public void onNextShouldSendMessagesToObserver() {
        RxChangesBus<String> rxChangesBus = new RxChangesBus<String>();
        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
        rxChangesBus.asFlowable().subscribe(testSubscriber);
        List<String> messages = Arrays.asList("yo", ",", "wanna", "some", "messages?");
        for (String message : messages) {
            rxChangesBus.onNext(message);
        }
        testSubscriber.assertValueSequence(messages);
        testSubscriber.assertNotTerminated();
    }
}

