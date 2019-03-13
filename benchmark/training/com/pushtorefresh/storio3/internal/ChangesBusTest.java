package com.pushtorefresh.storio3.internal;


import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class ChangesBusTest {
    @Test
    public void asFlowableShouldNotReturnNullIfRxJavaInClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(true);
        assertThat(changesBus.asFlowable()).isNotNull();
    }

    @Test
    public void asFlowableShouldReturnNullIfRxJavaIsNotInTheClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(false);
        assertThat(changesBus.asFlowable()).isNull();
    }

    @Test
    public void onNextShouldNotThrowExceptionIfRxJavaIsNotInTheClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(false);
        try {
            changesBus.onNext("don't crash me bro");
        } catch (Exception e) {
            fail("Yo, WTF dude?");
        }
    }

    @Test
    public void onNextShouldSendMessagesToObserverIfRxJavaIsInTheClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(true);
        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
        Flowable<String> flowable = changesBus.asFlowable();
        assertThat(flowable).isNotNull();
        // noinspection ConstantConditions
        flowable.subscribe(testSubscriber);
        List<String> messages = Arrays.asList("My", "life", "my", "rules", "please?");
        for (String message : messages) {
            changesBus.onNext(message);
        }
        testSubscriber.assertValueSequence(messages);
        testSubscriber.assertNotTerminated();
    }
}

