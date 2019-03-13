package org.mockserver.mock;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.callback.WebSocketClientRegistry;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.Cookie;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.scheduler.Scheduler;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerMatcherOverlappingRequestsTest {
    private MockServerMatcher mockServerMatcher;

    private HttpResponse[] httpResponse;

    private MockServerLogger mockLogFormatter;

    private static Scheduler scheduler = new Scheduler();

    private WebSocketClientRegistry webSocketClientRegistry;

    @Test
    public void respondWhenPathMatchesAlwaysReturnFirstMatching() {
        // when
        Expectation expectationZero = new Expectation(new HttpRequest().withPath("somepath").withCookies(new Cookie("name", "value"))).thenRespond(httpResponse[0].withBody("somebody1"));
        mockServerMatcher.add(expectationZero);
        Expectation expectationOne = new Expectation(new HttpRequest().withPath("somepath")).thenRespond(httpResponse[1].withBody("somebody2"));
        mockServerMatcher.add(expectationOne);
        // then
        Assert.assertEquals(expectationOne, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(expectationOne, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
    }

    @Test
    public void respondWhenPathMatchesReturnFirstMatchingWithRemainingTimes() {
        // when
        Expectation expectationZero = new Expectation(new HttpRequest().withPath("somepath").withCookies(new Cookie("name", "value")), Times.once(), TimeToLive.unlimited()).thenRespond(httpResponse[0].withBody("somebody1"));
        mockServerMatcher.add(expectationZero);
        Expectation expectationOne = new Expectation(new HttpRequest().withPath("somepath")).thenRespond(httpResponse[1].withBody("somebody2"));
        mockServerMatcher.add(expectationOne);
        // then
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath").withCookies(new Cookie("name", "value"))));
        Assert.assertEquals(expectationOne, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath").withCookies(new Cookie("name", "value"))));
    }
}

