package org.mockserver.filters;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.scheduler.Scheduler;
import org.mockserver.verify.VerificationSequence;


/**
 *
 *
 * @author jamesdbloom
 */
public class LogFilterExpectationMatchLogEntryVerificationSequenceTest {
    private static Scheduler scheduler = new Scheduler();

    @Test
    public void shouldPassVerificationWithNullRequest() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then
        Assert.assertThat(logFilter.verify(((VerificationSequence) (null))), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationSequenceWithNoRequest() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests()), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationSequenceWithOneRequest() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("one"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("three"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("four"))), CoreMatchers.is(""));
    }

    @Test
    public void shouldPassVerificationSequenceWithTwoRequests() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then - next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("multi"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"), HttpRequest.request("three"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("three"), HttpRequest.request("multi"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"), HttpRequest.request("four"))), CoreMatchers.is(""));
        // then - not next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("three"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("four"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"), HttpRequest.request("multi"))), CoreMatchers.is(""));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("three"), HttpRequest.request("four"))), CoreMatchers.is(""));
    }

    @Test
    public void shouldFailVerificationSequenceWithOneRequest() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("five"))), CoreMatchers.is((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"five\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationSequenceWithTwoRequestsWrongOrder() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then - next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"), HttpRequest.request("one"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("four"), HttpRequest.request("multi"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        // then - not next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("three"), HttpRequest.request("one"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("four"), HttpRequest.request("one"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("four"), HttpRequest.request("three"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationSequenceWithTwoRequestsFirstIncorrect() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then - next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("zero"), HttpRequest.request("multi"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"zero\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("zero"), HttpRequest.request("three"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"zero\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("zero"), HttpRequest.request("four"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"zero\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationSequenceWithTwoRequestsSecondIncorrect() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then - next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("five"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"five\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"), HttpRequest.request("five"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"five\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("three"), HttpRequest.request("five"))), CoreMatchers.is((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"five\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationSequenceWithThreeRequestsWrongOrder() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then - next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("four"), HttpRequest.request("multi"))), CoreMatchers.is((((((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("one"), HttpRequest.request("multi"), HttpRequest.request("one"))), CoreMatchers.is((((((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        // then - not next to each other
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("four"), HttpRequest.request("one"), HttpRequest.request("multi"))), CoreMatchers.is((((((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"), HttpRequest.request("three"), HttpRequest.request("one"))), CoreMatchers.is((((((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
    }

    @Test
    public void shouldFailVerificationSequenceWithThreeRequestsDuplicateMissing() {
        // given
        MockServerEventLog logFilter = new MockServerEventLog(Mockito.mock(MockServerLogger.class), LogFilterExpectationMatchLogEntryVerificationSequenceTest.scheduler);
        // when
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("one"), new org.mockserver.mock.Expectation(HttpRequest.request("one")).thenRespond(HttpResponse.response("one"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("three"), new org.mockserver.mock.Expectation(HttpRequest.request("three")).thenRespond(HttpResponse.response("three"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("multi"), new org.mockserver.mock.Expectation(HttpRequest.request("multi")).thenRespond(HttpResponse.response("multi"))));
        logFilter.add(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("four"), new org.mockserver.mock.Expectation(HttpRequest.request("four")).thenRespond(HttpResponse.response("four"))));
        // then
        Assert.assertThat(logFilter.verify(new VerificationSequence().withRequests(HttpRequest.request("multi"), HttpRequest.request("multi"), HttpRequest.request("multi"))), CoreMatchers.is((((((((((((((((((((((((((((((((("Request sequence not found, expected:<[ {" + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "} ]> but was:<[ {") + (NEW_LINE)) + "  \"path\" : \"one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"three\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"multi\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"path\" : \"four\"") + (NEW_LINE)) + "} ]>")));
    }
}

