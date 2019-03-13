package io.github.hidroh.materialistic.data;


import FeedbackClient.Callback;
import FeedbackClient.Impl.Issue;
import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import rx.Observable;


@RunWith(JUnit4.class)
public class FeedbackClientTest {
    @Inject
    RestServiceFactory factory;

    private FeedbackClient client;

    private Callback callback;

    @Test
    public void testSendSuccessful() {
        Mockito.when(TestRestServiceFactory.feedbackService.createGithubIssue(ArgumentMatchers.any(Issue.class))).thenReturn(Observable.just(null));
        client.send("title", "body", callback);
        Mockito.verify(TestRestServiceFactory.feedbackService).createGithubIssue(ArgumentMatchers.any(Issue.class));
        Mockito.verify(callback).onSent(ArgumentMatchers.eq(true));
    }

    @Test
    public void testSendFailed() {
        Mockito.when(TestRestServiceFactory.feedbackService.createGithubIssue(ArgumentMatchers.any(Issue.class))).thenReturn(Observable.error(new IOException()));
        client.send("title", "body", callback);
        Mockito.verify(TestRestServiceFactory.feedbackService).createGithubIssue(ArgumentMatchers.any(Issue.class));
        Mockito.verify(callback).onSent(ArgumentMatchers.eq(false));
    }

    @Module(injects = FeedbackClientTest.class, overrides = true)
    static class TestModule {
        @Provides
        @Singleton
        public RestServiceFactory provideRestServiceFactory() {
            return new TestRestServiceFactory();
        }
    }
}

