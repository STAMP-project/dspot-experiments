package org.mockserver.codec;


import io.netty.handler.codec.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.ConnectionOptions;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.StringBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseEncoderContentLengthTest {
    private MockServerResponseEncoder mockServerResponseEncoder;

    private List<Object> output;

    private HttpResponse httpResponse;

    @Test
    public void shouldSetContentLengthForStringBody() {
        // given - a request
        String body = "some_content";
        httpResponse = HttpResponse.response().withBody(StringBody.exact(body));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Content-Length"), Matchers.containsInAnyOrder(("" + (body.length()))));
    }

    @Test
    public void shouldSetContentLengthForBinaryBody() {
        // given - a request
        byte[] body = "some_binary_content".getBytes(StandardCharsets.UTF_8);
        httpResponse = HttpResponse.response().withBody(BinaryBody.binary(body));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Content-Length"), Matchers.containsInAnyOrder(("" + (body.length))));
    }

    @Test
    public void shouldSetContentLengthForJsonBody() {
        // given - a request
        String body = "{ \"message\": \"some_json_content\" }";
        httpResponse = HttpResponse.response().withBody(JsonBody.json(body));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Content-Length"), Matchers.containsInAnyOrder(("" + (body.length()))));
    }

    @Test
    public void shouldSetContentLengthForNullBody() {
        // given - a request
        httpResponse = HttpResponse.response();
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Content-Length"), Matchers.containsInAnyOrder("0"));
    }

    @Test
    public void shouldSetContentLengthForEmptyBody() {
        // given - a request
        httpResponse = HttpResponse.response();
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Content-Length"), Matchers.containsInAnyOrder("0"));
    }

    @Test
    public void shouldSuppressContentLengthViaConnectionOptions() {
        // given - a request
        httpResponse = HttpResponse.response().withBody("some_content").withConnectionOptions(new ConnectionOptions().withSuppressContentLengthHeader(true));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.contains("Content-Length"), Is.is(false));
    }

    @Test
    public void shouldOverrideContentLengthViaConnectionOptions() {
        // given - a request
        httpResponse = HttpResponse.response().withBody("some_content").withConnectionOptions(new ConnectionOptions().withContentLengthHeaderOverride(50));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Content-Length"), Matchers.containsInAnyOrder("50"));
    }
}

