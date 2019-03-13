package org.mockserver.filters;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.scheduler.Scheduler;
import org.mockserver.verify.Verification;
import org.mockserver.verify.VerificationTimes;


/**
 *
 *
 * @author jamesdbloom
 */
public class LogFilterRequestLogEntryVerificationTest {
    private static Scheduler scheduler = new Scheduler();

    @Test
    public void shouldPassVerificationWithNullRequest() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(((Verification) (null))), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationWithDefaultTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_path"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_other_path"))), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationWithAtLeastTwoTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_path")).withTimes(VerificationTimes.atLeast(2))), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationWithAtLeastZeroTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_non_matching_path")).withTimes(VerificationTimes.atLeast(0))), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationWithExactlyTwoTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_path")).withTimes(VerificationTimes.exactly(2))), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationWithExactlyZeroTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_non_matching_path")).withTimes(VerificationTimes.exactly(0))), CoreMatchers.is(""));
    }

    @Test
    public void shouldFailVerificationWithNullRequest() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // then
        Assert.assertThat(logFilter.verify(((Verification) (null))), CoreMatchers.is(""));
    }

    @Test
    public void shouldFailVerificationWithDefaultTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_non_matching_path"))), CoreMatchers.is((((((((((((((((("Request not found at least once, expected:<{" + (NEW_LINE)) + "  \"path\" : \"some_non_matching_path\"") + (NEW_LINE)) + "}> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationWithAtLeastTwoTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_other_path")).withTimes(VerificationTimes.atLeast(2))), CoreMatchers.is((((((((((((((((("Request not found at least 2 times, expected:<{" + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationWithExactTwoTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_other_path")).withTimes(VerificationTimes.exactly(2))), CoreMatchers.is((((((((((((((((("Request not found exactly 2 times, expected:<{" + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationWithExactOneTime() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_other_path")).withTimes(VerificationTimes.exactly(1))), CoreMatchers.is((((("Request not found exactly once, expected:<{" + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}> but was:<[]>")));
    }

    @Test
    public void shouldFailVerificationWithExactZeroTimes() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("some_path");
        HttpRequest otherHttpRequest = new HttpRequest().withPath("some_other_path");
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(otherHttpRequest));
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(new HttpRequest().withPath("some_other_path")).withTimes(VerificationTimes.exactly(0))), CoreMatchers.is((((((((((((((((("Request not found exactly 0 times, expected:<{" + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_other_path\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"some_path\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationWithNoInteractions() {
        // given
        HttpRequest httpRequest = new HttpRequest();
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterRequestLogEntryVerificationTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.RequestLogEntry(httpRequest));
        // then
        Assert.assertThat(logFilter.verify(Verification.verification().withRequest(HttpRequest.request()).withTimes(VerificationTimes.exactly(0))), CoreMatchers.is("Request not found exactly 0 times, expected:<{ }> but was:<{ }>"));
    }
}

