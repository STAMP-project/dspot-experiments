package com.pushtorefresh.storio3.test;


import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.mockito.Mockito;


public class FlowableBehaviorCheckerTest {
    @Test
    public void assertThatFlowableEmitsOncePositive() throws Exception {
        final String testString = "Test string";
        final Flowable<String> testFlowable = Flowable.just(testString);
        // noinspection unchecked
        final Consumer<String> testAction = Mockito.mock(Consumer.class);
        new FlowableBehaviorChecker<String>().flowable(testFlowable).expectedNumberOfEmissions(1).testAction(testAction).checkBehaviorOfFlowable();
        Mockito.verify(testAction, Mockito.times(1)).accept(testString);
    }

    @Test(expected = IllegalStateException.class)
    public void assertThatFlowableEmitsOnceNegative() {
        final Flowable<Integer> testFlowable = Flowable.just(1, 2);
        new FlowableBehaviorChecker<Integer>().flowable(testFlowable).expectedNumberOfEmissions(1).testAction(new Consumer<Integer>() {
            final AtomicInteger numberOfInvocations = new AtomicInteger(0);

            @Override
            public void accept(@NonNull
            Integer integer) throws Exception {
                if ((numberOfInvocations.incrementAndGet()) > 1) {
                    fail("Should be called once");
                }
            }
        }).checkBehaviorOfFlowable();
    }

    @Test
    public void shouldDenyUsingNullFlowable() {
        try {
            // noinspection ConstantConditions
            new FlowableBehaviorChecker<Object>().flowable(null).expectedNumberOfEmissions(1).testAction(new Consumer<Object>() {
                @Override
                public void accept(@NonNull
                Object o) throws Exception {
                }
            }).checkBehaviorOfFlowable();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify fields");
        }
    }

    @Test
    public void shouldDenyUsingNullExpectedNumberOfEmissions() {
        try {
            // noinspection ConstantConditions
            new FlowableBehaviorChecker<Object>().flowable(Flowable.just(new Object())).testAction(new Consumer<Object>() {
                @Override
                public void accept(@NonNull
                Object o) throws Exception {
                }
            }).checkBehaviorOfFlowable();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify fields");
        }
    }

    @Test
    public void shouldDenyUsingNullTestAction() {
        try {
            // noinspection ConstantConditions
            new FlowableBehaviorChecker<Object>().flowable(Flowable.just(new Object())).expectedNumberOfEmissions(1).checkBehaviorOfFlowable();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Please specify fields");
        }
    }
}

