package org.testcontainers.junit.wait.strategy;


import org.junit.Test;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;


/**
 * Tests for {@link HttpWaitStrategy}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class HttpWaitStrategyTest extends AbstractWaitStrategyTest<HttpWaitStrategy> {
    /**
     * newline sequence indicating end of the HTTP header.
     */
    private static final String NEWLINE = "\r\n";

    private static final String GOOD_RESPONSE_BODY = "Good Response Body";

    /**
     * Expects that the WaitStrategy returns successfully after receiving an HTTP 200 response from the container.
     */
    @Test
    public void testWaitUntilReadyWithSuccess() {
        waitUntilReadyAndSucceed(createShellCommand("200 OK", HttpWaitStrategyTest.GOOD_RESPONSE_BODY));
    }

    /**
     * Expects that the WaitStrategy returns successfully after receiving an HTTP 401 response from the container.
     * This 401 response is checked with a lambda using {@link HttpWaitStrategy#forStatusCodeMatching(Predicate)}
     */
    @Test
    public void testWaitUntilReadyWithUnauthorizedWithLambda() {
        waitUntilReadyAndSucceed(startContainerWithCommand(createShellCommand("401 UNAUTHORIZED", HttpWaitStrategyTest.GOOD_RESPONSE_BODY), createHttpWaitStrategy(ready).forStatusCodeMatching(( it) -> ((it >= 200) && (it < 300)) || (it == 401))));
    }

    /**
     * Expects that the WaitStrategy returns successfully after receiving an HTTP 401 response from the container.
     * This 401 response is checked with many status codes using {@link HttpWaitStrategy#forStatusCode(int)}
     */
    @Test
    public void testWaitUntilReadyWithManyStatusCodes() {
        waitUntilReadyAndSucceed(startContainerWithCommand(createShellCommand("401 UNAUTHORIZED", HttpWaitStrategyTest.GOOD_RESPONSE_BODY), createHttpWaitStrategy(ready).forStatusCode(300).forStatusCode(401).forStatusCode(500)));
    }

    /**
     * Expects that the WaitStrategy returns successfully after receiving an HTTP 401 response from the container.
     * This 401 response is checked with with many status codes using {@link HttpWaitStrategy#forStatusCode(int)}
     * and a lambda using {@link HttpWaitStrategy#forStatusCodeMatching(Predicate)}
     */
    @Test
    public void testWaitUntilReadyWithManyStatusCodesAndLambda() {
        waitUntilReadyAndSucceed(startContainerWithCommand(createShellCommand("401 UNAUTHORIZED", HttpWaitStrategyTest.GOOD_RESPONSE_BODY), createHttpWaitStrategy(ready).forStatusCode(300).forStatusCode(500).forStatusCodeMatching(( it) -> it == 401)));
    }

    /**
     * Expects that the WaitStrategy throws a {@link RetryCountExceededException} after not receiving any of the
     * error code defined with {@link HttpWaitStrategy#forStatusCode(int)}
     * and {@link HttpWaitStrategy#forStatusCodeMatching(Predicate)}
     */
    @Test
    public void testWaitUntilReadyWithTimeoutAndWithManyStatusCodesAndLambda() {
        waitUntilReadyAndTimeout(startContainerWithCommand(createShellCommand("401 UNAUTHORIZED", HttpWaitStrategyTest.GOOD_RESPONSE_BODY), createHttpWaitStrategy(ready).forStatusCode(300).forStatusCodeMatching(( it) -> it == 500)));
    }

    /**
     * Expects that the WaitStrategy throws a {@link RetryCountExceededException} after not receiving any of the
     * error code defined with {@link HttpWaitStrategy#forStatusCode(int)}
     * and {@link HttpWaitStrategy#forStatusCodeMatching(Predicate)}. Note that a 200 status code should not
     * be considered as a successful return as not explicitly set.
     * Test case for: https://github.com/testcontainers/testcontainers-java/issues/880
     */
    @Test
    public void testWaitUntilReadyWithTimeoutAndWithLambdaShouldNotMatchOk() {
        waitUntilReadyAndTimeout(startContainerWithCommand(createShellCommand("200 OK", HttpWaitStrategyTest.GOOD_RESPONSE_BODY), createHttpWaitStrategy(ready).forStatusCodeMatching(( it) -> it >= 300)));
    }

    /**
     * Expects that the WaitStrategy throws a {@link RetryCountExceededException} after not receiving an HTTP 200
     * response from the container within the timeout period.
     */
    @Test
    public void testWaitUntilReadyWithTimeout() {
        waitUntilReadyAndTimeout(createShellCommand("400 Bad Request", HttpWaitStrategyTest.GOOD_RESPONSE_BODY));
    }

    /**
     * Expects that the WaitStrategy throws a {@link RetryCountExceededException} after not the expected response body
     * from the container within the timeout period.
     */
    @Test
    public void testWaitUntilReadyWithTimeoutAndBadResponseBody() {
        waitUntilReadyAndTimeout(createShellCommand("200 OK", "Bad Response"));
    }

    /**
     * Expects the WaitStrategy probing the right port.
     */
    @Test
    public void testWaitUntilReadyWithSpecificPort() {
        waitUntilReadyAndSucceed(startContainerWithCommand(createShellCommand("200 OK", HttpWaitStrategyTest.GOOD_RESPONSE_BODY, 9090), createHttpWaitStrategy(ready).forPort(9090), 7070, 8080, 9090));
    }
}

