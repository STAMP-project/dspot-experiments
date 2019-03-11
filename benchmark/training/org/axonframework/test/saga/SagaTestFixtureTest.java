package org.axonframework.test.saga;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SagaTestFixtureTest {
    private SagaTestFixture<SagaTestFixtureTest.MyTestSaga> fixture;

    private AtomicInteger startRecordingCount;

    @Test
    public void startRecordingCallbackIsInvokedOnWhenPublishingAnEvent() {
        fixture.givenAPublished(new SagaTestFixtureTest.MyTestSaga.MyEvent());
        Assert.assertThat(startRecordingCount.get(), CoreMatchers.equalTo(0));
        fixture.whenPublishingA(new SagaTestFixtureTest.MyTestSaga.MyEvent());
        Assert.assertThat(startRecordingCount.get(), CoreMatchers.equalTo(1));
    }

    @Test
    public void startRecordingCallbackIsInvokedOnWhenTimeAdvances() throws Exception {
        fixture.givenAPublished(new SagaTestFixtureTest.MyTestSaga.MyEvent());
        Assert.assertThat(startRecordingCount.get(), CoreMatchers.equalTo(0));
        fixture.whenTimeAdvancesTo(Instant.now());
        Assert.assertThat(startRecordingCount.get(), CoreMatchers.equalTo(1));
    }

    @Test
    public void startRecordingCallbackIsInvokedOnWhenTimeElapses() throws Exception {
        fixture.givenAPublished(new SagaTestFixtureTest.MyTestSaga.MyEvent());
        Assert.assertThat(startRecordingCount.get(), CoreMatchers.equalTo(0));
        fixture.whenTimeElapses(Duration.ofSeconds(5));
        Assert.assertThat(startRecordingCount.get(), CoreMatchers.equalTo(1));
    }

    public static class MyTestSaga {
        @StartSaga
        @SagaEventHandler(associationProperty = "id")
        public void handle(SagaTestFixtureTest.MyTestSaga.MyEvent e) {
            // don't care
        }

        public static class MyEvent {
            public String getId() {
                return "42";
            }
        }
    }
}

