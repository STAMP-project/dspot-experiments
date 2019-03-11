package org.mockserver.mock;


import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.callback.WebSocketClientRegistry;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.HttpRequestMatcher;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.scheduler.Scheduler;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerMatcherClearAndResetTest {
    private MockServerMatcher mockServerMatcher;

    private MockServerLogger logFormatter;

    private Scheduler scheduler;

    private WebSocketClientRegistry webSocketClientRegistry;

    @Test
    public void shouldRemoveExpectationWhenNoMoreTimes() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        Expectation expectation = new Expectation(new HttpRequest().withPath("somepath"), Times.exactly(2), TimeToLive.unlimited()).thenRespond(httpResponse);
        // when
        mockServerMatcher.add(expectation);
        // then
        Assert.assertEquals(expectation, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(expectation, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Is.is(Matchers.empty()));
        Assert.assertEquals(null, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
    }

    @Test
    public void shouldClearAllExpectations() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        // when
        mockServerMatcher.clear(new HttpRequest().withPath("somepath"));
        // then
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Is.is(Matchers.empty()));
    }

    @Test
    public void shouldResetAllExpectationsWhenHttpRequestNull() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        // when
        mockServerMatcher.clear(null);
        // then
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Is.is(Matchers.empty()));
    }

    @Test
    public void shouldResetAllExpectations() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        // when
        mockServerMatcher.reset();
        // then
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Is.is(Matchers.empty()));
    }

    @Test
    public void shouldClearMatchingExpectationsByPathOnly() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        mockServerMatcher.add(new Expectation(new HttpRequest().withPath("abc"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        Expectation expectation = new Expectation(new HttpRequest().withPath("def"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse);
        mockServerMatcher.add(expectation);
        // when
        mockServerMatcher.clear(new HttpRequest().withPath("abc"));
        // then
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Matchers.hasItems(new org.mockserver.matchers.MatcherBuilder(logFormatter).transformsToMatcher(expectation)));
    }

    @Test
    public void shouldClearMatchingExpectationsByMethodOnly() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        mockServerMatcher.add(new Expectation(new HttpRequest().withMethod("GET").withPath("abc"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        mockServerMatcher.add(new Expectation(new HttpRequest().withMethod("GET").withPath("def"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        Expectation expectation = new Expectation(new HttpRequest().withMethod("POST").withPath("def"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse);
        mockServerMatcher.add(expectation);
        // when
        mockServerMatcher.clear(new HttpRequest().withMethod("GET"));
        // then
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Matchers.hasItems(new org.mockserver.matchers.MatcherBuilder(logFormatter).transformsToMatcher(expectation)));
    }

    @Test
    public void shouldClearMatchingExpectationsByHeaderOnly() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        mockServerMatcher.add(new Expectation(new HttpRequest().withMethod("GET").withPath("abc").withHeader(new Header("headerOneName", "headerOneValue")), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        mockServerMatcher.add(new Expectation(new HttpRequest().withMethod("PUT").withPath("def").withHeaders(new Header("headerOneName", "headerOneValue")), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse));
        Expectation expectation = new Expectation(new HttpRequest().withMethod("POST").withPath("def"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse);
        mockServerMatcher.add(expectation);
        // when
        mockServerMatcher.clear(new HttpRequest().withHeader(new Header("headerOneName", "headerOneValue")));
        // then
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Matchers.hasItems(new org.mockserver.matchers.MatcherBuilder(logFormatter).transformsToMatcher(expectation)));
    }

    @Test
    public void shouldClearNoExpectations() {
        // given
        HttpResponse httpResponse = new HttpResponse().withBody("somebody");
        Expectation[] expectations = new Expectation[]{ new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse), new Expectation(new HttpRequest().withPath("somepath"), Times.unlimited(), TimeToLive.unlimited()).thenRespond(httpResponse) };
        for (Expectation expectation : expectations) {
            mockServerMatcher.add(expectation);
        }
        List<HttpRequestMatcher> httpRequestMatchers = new java.util.ArrayList(mockServerMatcher.httpRequestMatchers);
        // when
        mockServerMatcher.clear(new HttpRequest().withPath("foobar"));
        // then
        MatcherAssert.assertThat(mockServerMatcher.httpRequestMatchers, Is.is(httpRequestMatchers));
    }
}

