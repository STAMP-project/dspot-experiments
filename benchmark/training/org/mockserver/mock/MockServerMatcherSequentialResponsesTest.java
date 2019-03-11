package org.mockserver.mock;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.callback.WebSocketClientRegistry;
import org.mockserver.logging.MockServerLogger;
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
public class MockServerMatcherSequentialResponsesTest {
    private MockServerMatcher mockServerMatcher;

    private HttpResponse[] httpResponse;

    private MockServerLogger mockLogFormatter;

    private Scheduler scheduler;

    private WebSocketClientRegistry webSocketClientRegistry;

    @Test
    public void respondWhenPathMatchesExpectationWithMultipleResponses() {
        // when
        Expectation expectationZero = new Expectation(new HttpRequest().withPath("somepath"), Times.exactly(2), TimeToLive.unlimited()).thenRespond(httpResponse[0].withBody("somebody1"));
        mockServerMatcher.add(expectationZero);
        Expectation expectationOne = new Expectation(new HttpRequest().withPath("somepath"), Times.exactly(1), TimeToLive.unlimited()).thenRespond(httpResponse[1].withBody("somebody2"));
        mockServerMatcher.add(expectationOne);
        Expectation expectationTwo = new Expectation(new HttpRequest().withPath("somepath")).thenRespond(httpResponse[2].withBody("somebody3"));
        mockServerMatcher.add(expectationTwo);
        // then
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(expectationOne, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(expectationTwo, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
    }

    @Test
    public void respondWhenPathMatchesMultipleDifferentResponses() {
        // when
        Expectation expectationZero = new Expectation(new HttpRequest().withPath("somepath1")).thenRespond(httpResponse[0].withBody("somebody1"));
        mockServerMatcher.add(expectationZero);
        Expectation expectationOne = new Expectation(new HttpRequest().withPath("somepath2")).thenRespond(httpResponse[1].withBody("somebody2"));
        mockServerMatcher.add(expectationOne);
        // then
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath1")));
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath1")));
        Assert.assertEquals(expectationOne, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath2")));
        Assert.assertEquals(expectationOne, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath2")));
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath1")));
        Assert.assertEquals(expectationOne, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath2")));
    }

    @Test
    public void doesNotRespondAfterMatchesFinishedExpectedTimes() {
        // when
        Expectation expectationZero = new Expectation(new HttpRequest().withPath("somepath"), Times.exactly(2), TimeToLive.unlimited()).thenRespond(httpResponse[0].withBody("somebody"));
        mockServerMatcher.add(expectationZero);
        // then
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertEquals(expectationZero, mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
        Assert.assertNull(mockServerMatcher.firstMatchingExpectation(new HttpRequest().withPath("somepath")));
    }
}

