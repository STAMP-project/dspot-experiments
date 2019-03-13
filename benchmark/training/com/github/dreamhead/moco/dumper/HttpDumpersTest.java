package com.github.dreamhead.moco.dumper;


import com.github.dreamhead.moco.model.DefaultHttpResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import io.netty.util.internal.StringUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpDumpersTest {
    private static final String MESSAGE_BODY = "test message body";

    private static final String EXPECTED_MESSAGE_BODY = ((StringUtil.NEWLINE) + (StringUtil.NEWLINE)) + (HttpDumpersTest.MESSAGE_BODY);

    private static final String BINARY_CONTENT_MESSAGE = ((StringUtil.NEWLINE) + (StringUtil.NEWLINE)) + "<content is binary>";

    @Test
    public void should_parse_plain_text_media_type() throws Exception {
        assertMessageContent("text/html", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_text_media_type() throws Exception {
        assertMessageContent("text/html; charset=ISO-8859-1", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_plain_json_media_type() throws Exception {
        assertMessageContent("application/json", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_json_media_type() throws Exception {
        assertMessageContent("application/json; charset=ISO-8859-1", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_plain_javascript_media_type() throws Exception {
        assertMessageContent("text/javascript", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_javascript_media_type() throws Exception {
        assertMessageContent("text/javascript; charset=UTF-8", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_plain_xml_media_type() throws Exception {
        assertMessageContent("application/xml", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_xml_media_type() throws Exception {
        assertMessageContent("application/rss+xml", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_not_parse_binary_media_type() throws Exception {
        assertMessageContent("image/jpeg", HttpDumpersTest.BINARY_CONTENT_MESSAGE);
    }

    @Test
    public void should_parse_complete_form_urlencoded_media_type() {
        assertMessageContent("application/x-www-form-urlencoded;charset=UTF-8", HttpDumpersTest.EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_content_when_content_length_not_set() {
        Assert.assertThat(HttpDumpers.asContent(messageWithHeaders(ImmutableMap.of(HttpHeaders.CONTENT_TYPE, "text/plain"))), CoreMatchers.is(HttpDumpersTest.EXPECTED_MESSAGE_BODY));
    }

    @Test
    public void should_not_parse_content_when_content_length_not_set() {
        Assert.assertThat(HttpDumpers.asContent(DefaultHttpResponse.builder().withHeaders(ImmutableMap.of(HttpHeaders.CONTENT_TYPE, "text/plain")).withContent("").build()), CoreMatchers.is(""));
    }
}

