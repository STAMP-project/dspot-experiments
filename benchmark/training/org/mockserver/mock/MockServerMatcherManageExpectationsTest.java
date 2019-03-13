package org.mockserver.mock;


import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.callback.WebSocketClientRegistry;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.scheduler.Scheduler;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerMatcherManageExpectationsTest {
    private MockServerMatcher mockServerMatcher;

    private HttpRequest httpRequest;

    private HttpResponse httpResponse;

    private Scheduler scheduler;

    private WebSocketClientRegistry webSocketClientRegistry;

    @Test
    public void shouldRemoveExpiredExpectationWhenMatching() {
        // when
        mockServerMatcher.add(new Expectation(httpRequest.withPath("somePath"), Times.unlimited(), TimeToLive.exactly(TimeUnit.MICROSECONDS, 0L)).thenRespond(httpResponse.withBody("someBody")));
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somePath")), Matchers.nullValue());
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, empty());
    }

    @Test
    public void shouldRemoveExpiredExpectationWhenNotMatching() {
        // when
        mockServerMatcher.add(new Expectation(httpRequest.withPath("somePath"), Times.unlimited(), TimeToLive.exactly(TimeUnit.MICROSECONDS, 0L)).thenRespond(httpResponse.withBody("someBody")));
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("someOtherPath")), Matchers.nullValue());
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, empty());
    }

    @Test
    public void shouldRemoveMultipleExpiredExpectations() throws InterruptedException {
        // when
        mockServerMatcher.add(new Expectation(httpRequest.withPath("somePath"), Times.unlimited(), TimeToLive.exactly(TimeUnit.MICROSECONDS, 0L)).thenRespond(httpResponse.withBody("someBody")));
        Expectation expectationToExpireAfter3Seconds = new Expectation(httpRequest.withPath("somePath"), Times.unlimited(), TimeToLive.exactly(TimeUnit.SECONDS, 3L));
        mockServerMatcher.add(expectationToExpireAfter3Seconds.thenRespond(httpResponse.withBody("someBody")));
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somePath")), Is.is(expectationToExpireAfter3Seconds));
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers.size(), Is.is(1));
        // when
        TimeUnit.SECONDS.sleep(3);
        // then - after 3 seconds
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("someOtherPath")), Matchers.nullValue());
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, empty());
    }

    @Test
    public void shouldNotRemoveNotExpiredExpectationsWhenMatching() {
        // when
        Expectation expectation = new Expectation(httpRequest.withPath("somePath"), Times.unlimited(), TimeToLive.exactly(TimeUnit.HOURS, 1L)).thenRespond(httpResponse.withBody("someBody"));
        mockServerMatcher.add(expectation);
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somePath")), Is.is(expectation));
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers.size(), Is.is(1));
    }

    @Test
    public void shouldNotRemoveNotExpiredExpectationsWhenNotMatching() {
        // when
        mockServerMatcher.add(new Expectation(httpRequest.withPath("somePath"), Times.unlimited(), TimeToLive.exactly(TimeUnit.HOURS, 1L)).thenRespond(httpResponse.withBody("someBody")));
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("someOtherPath")), Matchers.nullValue());
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers.size(), Is.is(1));
    }

    @Test
    public void shouldRemoveUsedExpectations() {
        // when
        Expectation expectation = new Expectation(httpRequest.withPath("somePath"), Times.exactly(1), TimeToLive.unlimited()).thenRespond(httpResponse.withBody("someBody"));
        mockServerMatcher.add(expectation);
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somePath")), Is.is(expectation));
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somePath")), Matchers.nullValue());
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, empty());
    }

    @Test
    public void shouldNotRemoveNotUsedExpectations() {
        // when
        Expectation expectation = new Expectation(httpRequest.withPath("somePath"), Times.exactly(2), TimeToLive.unlimited()).thenRespond(httpResponse.withBody("someBody"));
        mockServerMatcher.add(expectation);
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somePath")), Is.is(expectation));
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers.size(), Is.is(1));
    }

    @Test
    public void shouldNotRemoveAfterTimesComplete() {
        // when
        Expectation expectation = new Expectation(new HttpRequest().withPath("somepath"), Times.exactly(2), TimeToLive.unlimited()).thenRespond(httpResponse.withBody("someBody"));
        mockServerMatcher.add(expectation);
        Expectation notRemovedExpectation = new Expectation(httpRequest.withPath("someOtherPath"), Times.exactly(2), TimeToLive.unlimited());
        mockServerMatcher.add(notRemovedExpectation.thenRespond(HttpResponse.response().withBody("someOtherBody")));
        // then
        Assert.assertEquals(expectation, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(expectation, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(notRemovedExpectation, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("someOtherPath")));
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers.size(), Is.is(1));
        // then
        Assert.assertEquals(notRemovedExpectation, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("someOtherPath")));
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers.size(), Is.is(0));
    }

    @Test
    public void shouldNotRemoveAfterTimesCompleteOrExpired() {
        // when
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("somepath"), Times.exactly(0), TimeToLive.unlimited()).thenRespond(httpResponse.withBody("someBody")));
        mockServerMatcher.add(new Expectation(httpRequest.withPath("someOtherPath"), Times.unlimited(), TimeToLive.exactly(TimeUnit.MICROSECONDS, 0L)).thenRespond(HttpResponse.response().withBody("someOtherBody")));
        mockServerMatcher.add(new Expectation(httpRequest.withPath("someOtherPath"), Times.exactly(0), TimeToLive.exactly(TimeUnit.MICROSECONDS, 0L)).thenRespond(HttpResponse.response().withBody("someOtherBody")));
        // then
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")), Matchers.nullValue());
        MatcherAssert.assertThat(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("someOtherPath")), Matchers.nullValue());
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, empty());
    }
}

