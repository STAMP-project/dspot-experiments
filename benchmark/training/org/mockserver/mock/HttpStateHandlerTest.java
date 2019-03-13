package org.mockserver.mock;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.log.model.MessageLogEntry;
import org.mockserver.log.model.RequestLogEntry;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.scheduler.Scheduler;
import org.mockserver.serialization.ExpectationSerializer;
import org.mockserver.serialization.HttpRequestSerializer;
import org.mockserver.serialization.LogEntrySerializer;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.java.ExpectationToJavaSerializer;
import org.mockserver.serialization.java.HttpRequestToJavaSerializer;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpStateHandlerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private HttpRequestSerializer httpRequestSerializer = new HttpRequestSerializer(new MockServerLogger());

    private LogEntrySerializer logEntrySerializer = new LogEntrySerializer(new MockServerLogger());

    private HttpRequestToJavaSerializer httpRequestToJavaSerializer = new HttpRequestToJavaSerializer();

    private ExpectationSerializer httpExpectationSerializer = new ExpectationSerializer(new MockServerLogger());

    private ExpectationToJavaSerializer httpExpectationToJavaSerializer = new ExpectationToJavaSerializer();

    private Scheduler scheduler;

    @Mock
    private MockServerLogger mockLogFormatter;

    @InjectMocks
    private HttpStateHandler httpStateHandler;

    @Test
    public void shouldAllowAddingOfExceptionsWithNullFields() {
        // given - some existing expectations
        Expectation expectationOne = new Expectation(null).thenRespond(HttpResponse.response("response_one"));
        Expectation expectationTwo = thenRespond(((HttpResponse) (null)));
        // when
        httpStateHandler.add(expectationOne);
        httpStateHandler.add(expectationTwo);
        // then - correct expectations exist
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(null), Is.is(expectationOne));
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_two")), Is.is(expectationOne));
    }

    @Test
    public void shouldClearLogsAndExpectationsForNullRequestMatcher() {
        // given - a request
        HttpRequest request = HttpRequest.request();
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        // given - some log entries
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        // when
        httpStateHandler.clear(request);
        // then - correct log entries removed
        MatcherAssert.assertThat(httpStateHandler.retrieve(request), Is.is(HttpResponse.response().withBody("[]", MediaType.JSON_UTF_8).withStatusCode(200)));
        // then - correct expectations removed
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_one")), IsNull.nullValue());
        // then - activity logged
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CLEARED, ((HttpRequest) (null)), "clearing expectations and request logs that match:{}", "{}");
    }

    @Test
    public void shouldClearLogsAndExpectations() {
        // given - a request
        HttpRequest request = HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        // given - some log entries
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_two")));
        // when
        httpStateHandler.clear(request);
        // then - correct log entries removed
        MatcherAssert.assertThat(httpStateHandler.retrieve(HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")))), Is.is(HttpResponse.response().withBody("[]", MediaType.JSON_UTF_8).withStatusCode(200)));
        MatcherAssert.assertThat(httpStateHandler.retrieve(HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_two")))), Is.is(HttpResponse.response().withBody(httpRequestSerializer.serialize(Collections.singletonList(HttpRequest.request("request_two"))), MediaType.JSON_UTF_8).withStatusCode(200)));
        // then - correct expectations removed
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_one")), IsNull.nullValue());
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_two")), Is.is(expectationTwo));
        // then - activity logged
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(CLEARED, HttpRequest.request("request_one"), "clearing expectations and request logs that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldClearLogsOnly() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("type", "log").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        // given - some log entries
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_two")));
        // when
        httpStateHandler.clear(request);
        // then - correct log entries removed
        MatcherAssert.assertThat(httpStateHandler.retrieve(HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")))), Is.is(HttpResponse.response().withBody("[]", MediaType.JSON_UTF_8).withStatusCode(200)));
        MatcherAssert.assertThat(httpStateHandler.retrieve(HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_two")))), Is.is(HttpResponse.response().withBody(httpRequestSerializer.serialize(Collections.singletonList(HttpRequest.request("request_two"))), MediaType.JSON_UTF_8).withStatusCode(200)));
        // then - correct expectations removed
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_one")), Is.is(expectationOne));
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_two")), Is.is(expectationTwo));
        // then - activity logged
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(CLEARED, HttpRequest.request("request_one"), "clearing recorded requests and logs that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldClearExpectationsOnly() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("type", "expectations").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        // given - some log entries
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_two")));
        // when
        httpStateHandler.clear(request);
        // then - correct log entries removed
        MatcherAssert.assertThat(httpStateHandler.retrieve(HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")))), Is.is(HttpResponse.response().withBody(httpRequestSerializer.serialize(Collections.singletonList(HttpRequest.request("request_one"))), MediaType.JSON_UTF_8).withStatusCode(200)));
        MatcherAssert.assertThat(httpStateHandler.retrieve(HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_two")))), Is.is(HttpResponse.response().withBody(httpRequestSerializer.serialize(Collections.singletonList(HttpRequest.request("request_two"))), MediaType.JSON_UTF_8).withStatusCode(200)));
        // then - correct expectations removed
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_one")), IsNull.nullValue());
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_two")), Is.is(expectationTwo));
        // then - activity logged
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(CLEARED, HttpRequest.request("request_one"), "clearing expectations that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldThrowExceptionForInvalidClearType() {
        // given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("\"invalid\" is not a valid value for \"type\" parameter, only the following values are supported [log, expectations, all]"));
        // when
        httpStateHandler.clear(HttpRequest.request().withQueryStringParameter("type", "invalid"));
    }

    @Test
    public void shouldRetrieveRecordedRequestsAsJson() {
        // given - a request
        HttpRequest request = HttpRequest.request().withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        // given - some log entries
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_two")));
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(httpRequestSerializer.serialize(Arrays.asList(HttpRequest.request("request_one"), HttpRequest.request("request_one"))), MediaType.JSON_UTF_8).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving requests in json that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldRetrieveRecordedRequestsAsLogEntries() throws JsonProcessingException {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("format", "log_entries").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        // given - some log entries
        final RequestLogEntry logEntryOne = new RequestLogEntry(HttpRequest.request("request_one"));
        httpStateHandler.log(logEntryOne);
        final RequestLogEntry logEntryTwo = new RequestLogEntry(HttpRequest.request("request_two"));
        httpStateHandler.log(logEntryTwo);
        final RequestLogEntry logEntryThree = new RequestLogEntry(HttpRequest.request("request_one"));
        httpStateHandler.log(logEntryThree);
        ObjectWriter objectWriter = ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter();
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(objectWriter.writeValueAsString(Arrays.asList(ImmutableMap.of("httpRequest", logEntryOne.getHttpRequest(), "timestamp", logEntryOne.getTimestamp()), ImmutableMap.of("httpRequest", logEntryThree.getHttpRequest(), "timestamp", logEntryThree.getTimestamp()))), MediaType.JSON_UTF_8).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving requests in log_entries that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldRetrieveRecordedRequestsAsJava() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("format", "java").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        // given - some log entries
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_two")));
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(httpRequestToJavaSerializer.serialize(Arrays.asList(HttpRequest.request("request_one"), HttpRequest.request("request_one"))), MediaType.create("application", "java").withCharset(StandardCharsets.UTF_8)).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving requests in java that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldRetrieveRecordedExpectationsAsJson() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("type", "recorded_expectations").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some log entries
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")));
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(HttpRequest.request("request_two"), HttpResponse.response("response_two")));
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("request_three")));
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(httpExpectationSerializer.serialize(Arrays.asList(new Expectation(HttpRequest.request("request_one"), Times.once(), null).thenRespond(HttpResponse.response("response_one")), new Expectation(HttpRequest.request("request_one"), Times.once(), null).thenRespond(HttpResponse.response("request_three")))), MediaType.JSON_UTF_8).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving recorded_expectations in json that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldRetrieveRecordedExpectationsAsJava() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("type", "recorded_expectations").withQueryStringParameter("format", "java").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some log entries
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("response_one")));
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(HttpRequest.request("request_two"), HttpResponse.response("response_two")));
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(HttpRequest.request("request_one"), HttpResponse.response("request_three")));
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(httpExpectationToJavaSerializer.serialize(Arrays.asList(new Expectation(HttpRequest.request("request_one"), Times.once(), null).thenRespond(HttpResponse.response("response_one")), new Expectation(HttpRequest.request("request_one"), Times.once(), null).thenRespond(HttpResponse.response("request_three")))), MediaType.create("application", "java").withCharset(StandardCharsets.UTF_8)).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving recorded_expectations in java that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldRetrieveActiveExpectationsAsJson() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("type", "active_expectations").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        Expectation expectationThree = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("request_three"));
        httpStateHandler.add(expectationThree);
        // given - some log entries
        httpStateHandler.log(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("request_one"), expectationOne));
        httpStateHandler.log(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("request_two"), expectationTwo));
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(httpExpectationSerializer.serialize(Arrays.asList(expectationOne, expectationThree)), MediaType.JSON_UTF_8).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationThree);
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving active_expectations in json that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldRetrieveActiveExpectationsAsJava() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("type", "active_expectations").withQueryStringParameter("format", "java").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        Expectation expectationTwo = new Expectation(HttpRequest.request("request_two")).thenRespond(HttpResponse.response("response_two"));
        httpStateHandler.add(expectationTwo);
        Expectation expectationThree = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("request_three"));
        httpStateHandler.add(expectationThree);
        // given - some log entries
        httpStateHandler.log(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("request_one"), expectationOne));
        httpStateHandler.log(new org.mockserver.log.model.ExpectationMatchLogEntry(HttpRequest.request("request_two"), expectationTwo));
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(httpExpectationToJavaSerializer.serialize(Arrays.asList(expectationOne, expectationThree)), MediaType.create("application", "java").withCharset(StandardCharsets.UTF_8)).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_two"), "creating expectation:{}", expectationTwo);
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationThree);
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving active_expectations in java that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldRetrieveLogs() {
        // given - a request
        HttpRequest request = HttpRequest.request().withQueryStringParameter("type", "logs").withBody(httpRequestSerializer.serialize(HttpRequest.request("request_one")));
        // given - some log messages
        MessageLogEntry logEntryOne = new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_one");
        httpStateHandler.log(logEntryOne);
        MessageLogEntry logEntryTwo = new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_two");
        httpStateHandler.log(logEntryTwo);
        MessageLogEntry logEntryThree = new MessageLogEntry(TRACE, null, HttpRequest.request("request_one"), "message_three");
        httpStateHandler.log(logEntryThree);
        // when
        HttpResponse response = httpStateHandler.retrieve(request);
        // then
        MatcherAssert.assertThat(response, Is.is(HttpResponse.response().withBody(((((((((((((((((logEntryOne.getTimestamp()) + " - ") + (logEntryOne.getMessage())) + (NEW_LINE)) + "------------------------------------") + (NEW_LINE)) + (logEntryTwo.getTimestamp())) + " - ") + (logEntryTwo.getMessage())) + (NEW_LINE)) + "------------------------------------") + (NEW_LINE)) + (logEntryThree.getTimestamp())) + " - ") + (logEntryThree.getMessage())) + (NEW_LINE)), MediaType.PLAIN_TEXT_UTF_8).withStatusCode(200)));
        Mockito.verify(mockLogFormatter).info(RETRIEVED, HttpRequest.request("request_one"), "retrieving logs that match:{}", HttpRequest.request("request_one"));
    }

    @Test
    public void shouldThrowExceptionForInvalidRetrieveType() {
        // given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("\"invalid\" is not a valid value for \"type\" parameter, only the following values are supported [logs, requests, recorded_expectations, active_expectations]"));
        // when
        httpStateHandler.retrieve(HttpRequest.request().withQueryStringParameter("type", "invalid"));
    }

    @Test
    public void shouldThrowExceptionForInvalidRetrieveFormat() {
        // given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("\"invalid\" is not a valid value for \"format\" parameter, only the following values are supported [java, json, log_entries]"));
        // when
        httpStateHandler.retrieve(HttpRequest.request().withQueryStringParameter("format", "invalid"));
    }

    @Test
    public void shouldReset() {
        // given - a request
        HttpRequest request = HttpRequest.request();
        // given - some existing expectations
        Expectation expectationOne = new Expectation(HttpRequest.request("request_one")).thenRespond(HttpResponse.response("response_one"));
        httpStateHandler.add(expectationOne);
        // given - some log entries
        httpStateHandler.log(new RequestLogEntry(HttpRequest.request("request_one")));
        // when
        httpStateHandler.reset();
        // then - correct log entries removed
        MatcherAssert.assertThat(httpStateHandler.retrieve(request), Is.is(HttpResponse.response().withBody("[]", MediaType.JSON_UTF_8).withStatusCode(200)));
        // then - correct expectations removed
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(HttpRequest.request("request_one")), IsNull.nullValue());
        // then - activity logged
        Mockito.verify(mockLogFormatter).info(CREATED_EXPECTATION, HttpRequest.request("request_one"), "creating expectation:{}", expectationOne);
        Mockito.verify(mockLogFormatter).info(RETRIEVED, ((HttpRequest) (null)), "retrieving requests in json that match:{}", HttpRequest.request());
        Mockito.verify(mockLogFormatter).info(EXPECTATION_NOT_MATCHED, HttpRequest.request("request_one"), "no active expectations");
        Mockito.verify(mockLogFormatter).info(CLEARED, "resetting all expectations and request logs");
    }
}

