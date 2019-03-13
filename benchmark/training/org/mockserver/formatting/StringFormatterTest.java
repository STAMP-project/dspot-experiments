package org.mockserver.formatting;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.HttpForward;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class StringFormatterTest {
    @Test
    public void shouldFormatLogMessageWithMultipleParameters() {
        // when
        String logMessage = StringFormatter.formatLogMessage("returning response:{}for request:{}for action:{}", HttpResponse.response("response_body"), HttpRequest.request("request_path"), HttpForward.forward());
        // then
        Assert.assertThat(logMessage, Is.is(((((((((((((((((((((((((((((((((((((((("returning response:" + (NEW_LINE)) + "") + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"statusCode\" : 200,") + (NEW_LINE)) + "\t  \"reasonPhrase\" : \"OK\",") + (NEW_LINE)) + "\t  \"body\" : \"response_body\"") + (NEW_LINE)) + "\t}") + (NEW_LINE)) + "") + (NEW_LINE)) + " for request:") + (NEW_LINE)) + "") + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"path\" : \"request_path\"") + (NEW_LINE)) + "\t}") + (NEW_LINE)) + "") + (NEW_LINE)) + " for action:") + (NEW_LINE)) + "") + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"port\" : 80,") + (NEW_LINE)) + "\t  \"scheme\" : \"HTTP\"") + (NEW_LINE)) + "\t}") + (NEW_LINE))));
    }

    @Test
    public void shouldFormatLogMessageWithASingleParameter() {
        // when
        String logMessage = StringFormatter.formatLogMessage("returning response:{}", HttpResponse.response("response_body"));
        // then
        Assert.assertThat(logMessage, Is.is((((((((((((("returning response:" + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"statusCode\" : 200,") + (NEW_LINE)) + "\t  \"reasonPhrase\" : \"OK\",") + (NEW_LINE)) + "\t  \"body\" : \"response_body\"") + (NEW_LINE)) + "\t}") + (NEW_LINE))));
    }

    @Test
    public void shouldIgnoreExtraParameters() {
        // when
        String logMessage = StringFormatter.formatLogMessage("returning response:{}", HttpResponse.response("response_body"), HttpRequest.request("request_path"), HttpForward.forward());
        // then
        Assert.assertThat(logMessage, Is.is((((((((((((("returning response:" + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"statusCode\" : 200,") + (NEW_LINE)) + "\t  \"reasonPhrase\" : \"OK\",") + (NEW_LINE)) + "\t  \"body\" : \"response_body\"") + (NEW_LINE)) + "\t}") + (NEW_LINE))));
    }

    @Test
    public void shouldIgnoreTooFewParameters() {
        // when
        String logMessage = StringFormatter.formatLogMessage("returning response:{}for request:{}for action:{}", HttpResponse.response("response_body"));
        // then
        Assert.assertThat(logMessage, Is.is((((((((((((((((((("returning response:" + (NEW_LINE)) + "") + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"statusCode\" : 200,") + (NEW_LINE)) + "\t  \"reasonPhrase\" : \"OK\",") + (NEW_LINE)) + "\t  \"body\" : \"response_body\"") + (NEW_LINE)) + "\t}") + (NEW_LINE)) + "") + (NEW_LINE)) + " for request:") + (NEW_LINE)) + " for action:")));
    }
}

