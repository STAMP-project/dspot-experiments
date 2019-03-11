package org.mockserver.filters;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.scheduler.Scheduler;


public class LogFilterTest {
    private static Function<LogEntry, LogEntry> logEntryToLogEntry = new Function<LogEntry, LogEntry>() {
        public LogEntry apply(LogEntry logEntry) {
            return logEntry;
        }
    };

    private MockServerEventLog.MockServerEventLog logFilter;

    private MockServerLogger mockLogFormatter;

    private Scheduler scheduler;

    @Test
    public void shouldClearWithNullRequestMatcher() {
        // given
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_two")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        // when
        logFilter.clear(null);
        // then
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, requestLogPredicate, LogFilterTest.logEntryToLogEntry), empty());
        MatcherAssert.assertThat(logFilter.retrieveRequests(null), empty());
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, expectationLogPredicate, LogFilterTest.logEntryToLogEntry), empty());
        MatcherAssert.assertThat(logFilter.retrieveExpectations(null), empty());
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, messageLogPredicate, LogFilterTest.logEntryToLogEntry), empty());
        MatcherAssert.assertThat(logFilter.retrieveMessages(null), empty());
    }

    @Test
    public void shouldClearWithRequestMatcher() {
        // given
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"));
        final RequestLogEntry requestLogEntry = new RequestLogEntry(HttpRequest.request("request_two"));
        logFilter.add(requestLogEntry);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"));
        final RequestResponseLogEntry requestResponseLogEntry = new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three"));
        logFilter.add(requestResponseLogEntry);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"));
        final ExpectationMatchLogEntry expectationMatchLogEntry = new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four")));
        logFilter.add(expectationMatchLogEntry);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        // when
        logFilter.clear(HttpRequest.request("request_one"));
        // then
        MatcherAssert.assertThat(logFilter.retrieveRequests(null), contains(HttpRequest.request("request_two"), HttpRequest.request("request_three"), HttpRequest.request("request_four")));
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, requestLogPredicate, LogFilterTest.logEntryToLogEntry), contains(requestLogEntry, requestResponseLogEntry, expectationMatchLogEntry));
        MatcherAssert.assertThat(logFilter.retrieveExpectations(null), contains(new org.mockserver.mock.Expectation(HttpRequest.request("request_three"), Times.once(), null).thenRespond(HttpResponse.response("response_three"))));
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, expectationLogPredicate, LogFilterTest.logEntryToLogEntry), IsIterableContainingInOrder.<LogEntry>contains(requestResponseLogEntry));
        List<String> logMessages = Lists.transform(logFilter.retrieveMessages(null), new Function<MessageLogEntry, String>() {
            public String apply(MessageLogEntry input) {
                return input.getMessage();
            }
        });
        MatcherAssert.assertThat(logMessages, contains(StringContains.containsString("message_two"), StringContains.containsString("message_three"), StringContains.containsString("message_four"), StringContains.containsString("message_five"), StringContains.containsString("message_six"), StringContains.containsString("message_seven")));
        List<LogEntry> logEntries = logFilter.retrieveLogEntries(null, messageLogPredicate, LogFilterTest.logEntryToLogEntry);
        List<MessageLogEntry> messageLogEntries = Arrays.asList(new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        for (int i = 0; i < (logEntries.size()); i++) {
            MessageLogEntry messageLogEntry = ((MessageLogEntry) (logEntries.get(i)));
            MatcherAssert.assertThat(messageLogEntry.getHttpRequests(), CoreMatchers.is(messageLogEntries.get(i).getHttpRequests()));
            MatcherAssert.assertThat(messageLogEntry.getMessage(), CoreMatchers.endsWith(messageLogEntries.get(i).getMessage()));
        }
    }

    @Test
    public void shouldReset() {
        // given
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_two")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        // when
        logFilter.reset();
        // then
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, requestLogPredicate, LogFilterTest.logEntryToLogEntry), empty());
        MatcherAssert.assertThat(logFilter.retrieveRequests(null), empty());
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, expectationLogPredicate, LogFilterTest.logEntryToLogEntry), empty());
        MatcherAssert.assertThat(logFilter.retrieveExpectations(null), empty());
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, messageLogPredicate, LogFilterTest.logEntryToLogEntry), empty());
        MatcherAssert.assertThat(logFilter.retrieveMessages(null), empty());
    }

    @Test
    public void shouldRetrieveRecordedRequests() {
        // when
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one"));
        final RequestLogEntry requestLogEntryOne = new RequestLogEntry(HttpRequest.request("request_one"));
        logFilter.add(requestLogEntryOne);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"));
        final RequestLogEntry requestLogEntryTwo = new RequestLogEntry(HttpRequest.request("request_two"));
        logFilter.add(requestLogEntryTwo);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"));
        final RequestResponseLogEntry requestResponseLogEntryOne = new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one"));
        logFilter.add(requestResponseLogEntryOne);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"));
        final RequestResponseLogEntry requestResponseLogEntryTwo = new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three"));
        logFilter.add(requestResponseLogEntryTwo);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"));
        final ExpectationMatchLogEntry expectationMatchLogEntryOne = new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two")));
        logFilter.add(expectationMatchLogEntryOne);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"));
        final ExpectationMatchLogEntry expectationMatchLogEntryTwo = new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four")));
        logFilter.add(expectationMatchLogEntryTwo);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        // then
        MatcherAssert.assertThat(logFilter.retrieveRequests(null), contains(HttpRequest.request("request_one"), HttpRequest.request("request_two"), HttpRequest.request("request_one"), HttpRequest.request("request_three"), HttpRequest.request("request_one"), HttpRequest.request("request_four")));
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, requestLogPredicate, LogFilterTest.logEntryToLogEntry), contains(requestLogEntryOne, requestLogEntryTwo, requestResponseLogEntryOne, requestResponseLogEntryTwo, expectationMatchLogEntryOne, expectationMatchLogEntryTwo));
    }

    @Test
    public void shouldRetrieveRecordedExpectations() {
        // when
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_two")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"));
        final RequestResponseLogEntry requestResponseLogEntryOne = new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one"));
        logFilter.add(requestResponseLogEntryOne);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"));
        final RequestResponseLogEntry requestResponseLogEntryTwo = new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three"));
        logFilter.add(requestResponseLogEntryTwo);
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        // then
        MatcherAssert.assertThat(logFilter.retrieveExpectations(null), contains(new org.mockserver.mock.Expectation(HttpRequest.request("request_one"), Times.once(), null).thenRespond(HttpResponse.response("response_one")), new org.mockserver.mock.Expectation(HttpRequest.request("request_three"), Times.once(), null).thenRespond(HttpResponse.response("response_three"))));
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, expectationLogPredicate, LogFilterTest.logEntryToLogEntry), IsIterableContainingInOrder.<LogEntry>contains(requestResponseLogEntryOne, requestResponseLogEntryTwo));
    }

    @Test
    public void shouldRetrieveMessages() {
        // when
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_two")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three")));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four"))));
        logFilter.add(new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        // then
        List<String> logMessages = Lists.transform(logFilter.retrieveMessages(null), new Function<MessageLogEntry, String>() {
            public String apply(MessageLogEntry input) {
                return input.getMessage();
            }
        });
        MatcherAssert.assertThat(logMessages, contains(StringContains.containsString("message_one"), StringContains.containsString("message_two"), StringContains.containsString("message_three"), StringContains.containsString("message_four"), StringContains.containsString("message_five"), StringContains.containsString("message_six"), StringContains.containsString("message_seven")));
        List<LogEntry> logEntries = logFilter.retrieveLogEntries(null, messageLogPredicate, LogFilterTest.logEntryToLogEntry);
        List<MessageLogEntry> messageLogEntries = Arrays.asList(new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_two"), "message_two"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_three"), "message_three"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_four"), "message_four"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_five"), "message_five"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_six"), "message_six"), new MessageLogEntry(TRACE, null, HttpRequest.request("request_seven"), "message_seven"));
        for (int i = 0; i < (logEntries.size()); i++) {
            MessageLogEntry messageLogEntry = ((MessageLogEntry) (logEntries.get(i)));
            MatcherAssert.assertThat(messageLogEntry.getHttpRequests(), CoreMatchers.is(messageLogEntries.get(i).getHttpRequests()));
            MatcherAssert.assertThat(messageLogEntry.getMessage(), CoreMatchers.is(messageLogEntries.get(i).getMessage()));
        }
    }

    @Test
    public void shouldRetrieveLogEntries() {
        // when
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_one")));
        logFilter.add(new RequestLogEntry(HttpRequest.request("request_two")));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")));
        logFilter.add(new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three")));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two"))));
        logFilter.add(new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four"))));
        // then
        MatcherAssert.assertThat(logFilter.retrieveLogEntries(null, requestLogPredicate, LogFilterTest.logEntryToLogEntry), contains(new RequestLogEntry(HttpRequest.request("request_one")), new RequestLogEntry(HttpRequest.request("request_two")), new RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")), new RequestResponseLogEntry(HttpRequest.request("request_three"), HttpResponse.response("response_three")), new ExpectationMatchLogEntry(HttpRequest.request("request_one"), new org.mockserver.mock.Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_two"))), new ExpectationMatchLogEntry(HttpRequest.request("request_four"), new org.mockserver.mock.Expectation(HttpRequest.request("request_four")).thenRespond(HttpResponse.response("response_four")))));
    }
}

