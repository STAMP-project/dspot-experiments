package example.armeria.server.annotated;


import HttpHeaderNames.COOKIE;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.BAD_REQUEST;
import HttpStatus.CONFLICT;
import HttpStatus.FORBIDDEN;
import HttpStatus.OK;
import HttpStatus.SERVICE_UNAVAILABLE;
import MediaType.JSON_UTF_8;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.server.Server;
import java.util.Arrays;
import org.junit.Test;


public class AnnotatedHttpServiceTest {
    private static Server server;

    private static HttpClient client;

    @Test
    public void testPathPatternService() {
        AggregatedHttpMessage res;
        res = AnnotatedHttpServiceTest.client.get("/pathPattern/path/armeria").aggregate().join();
        assertThat(res.contentUtf8()).isEqualTo("path: armeria");
        assertThat(res.status()).isEqualTo(OK);
        res = AnnotatedHttpServiceTest.client.get("/pathPattern/regex/armeria").aggregate().join();
        assertThat(res.contentUtf8()).isEqualTo("regex: armeria");
        assertThat(res.status()).isEqualTo(OK);
        res = AnnotatedHttpServiceTest.client.get("/pathPattern/glob/armeria").aggregate().join();
        assertThat(res.contentUtf8()).isEqualTo("glob: armeria");
        assertThat(res.status()).isEqualTo(OK);
    }

    @Test
    public void testInjectionService() {
        AggregatedHttpMessage res;
        res = AnnotatedHttpServiceTest.client.get("/injection/param/armeria/1?gender=male").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThatJson(res.contentUtf8()).isArray().ofLength(3).thatContains("armeria").thatContains(1).thatContains("MALE");
        final HttpHeaders headers = HttpHeaders.of(GET, "/injection/header").add(HttpHeaderNames.of("x-armeria-text"), "armeria").add(HttpHeaderNames.of("x-armeria-sequence"), "1").add(HttpHeaderNames.of("x-armeria-sequence"), "2").add(COOKIE, "a=1").add(COOKIE, "b=1");
        res = AnnotatedHttpServiceTest.client.execute(headers).aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThatJson(res.contentUtf8()).isArray().ofLength(3).thatContains("armeria").thatContains(Arrays.asList(1, 2)).thatContains(Arrays.asList("a", "b"));
    }

    @Test
    public void testMessageConverterService() {
        AggregatedHttpMessage res;
        String body;
        // JSON
        for (final String path : Arrays.asList("/messageConverter/node/node", "/messageConverter/node/obj", "/messageConverter/obj/obj", "/messageConverter/obj/future")) {
            res = AnnotatedHttpServiceTest.client.execute(HttpHeaders.of(POST, path).contentType(JSON_UTF_8), "{\"name\":\"armeria\"}").aggregate().join();
            assertThat(res.status()).isEqualTo(OK);
            assertThat(res.contentType().is(JSON_UTF_8)).isTrue();
            body = res.contentUtf8();
            assertThatJson(body).node("result").isStringEqualTo("success");
            assertThatJson(body).node("from").isStringEqualTo("armeria");
        }
        // custom(text protocol)
        res = AnnotatedHttpServiceTest.client.execute(HttpHeaders.of(POST, "/messageConverter/custom").contentType(PLAIN_TEXT_UTF_8), "armeria").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.contentType().is(PLAIN_TEXT_UTF_8)).isTrue();
        assertThat(res.contentUtf8()).isEqualTo("success:armeria");
    }

    @Test
    public void testExceptionHandlerService() {
        AggregatedHttpMessage res;
        res = AnnotatedHttpServiceTest.client.get("/exception/locallySpecific").aggregate().join();
        assertThat(res.status()).isEqualTo(SERVICE_UNAVAILABLE);
        res = AnnotatedHttpServiceTest.client.get("/exception/locallyGeneral").aggregate().join();
        assertThat(res.status()).isEqualTo(BAD_REQUEST);
        res = AnnotatedHttpServiceTest.client.get("/exception/globallyGeneral").aggregate().join();
        assertThat(res.status()).isEqualTo(FORBIDDEN);
        // IllegalArgumentException
        res = AnnotatedHttpServiceTest.client.get("/exception/default").aggregate().join();
        assertThat(res.status()).isEqualTo(BAD_REQUEST);
        // HttpStatusException
        res = AnnotatedHttpServiceTest.client.get("/exception/default/200").aggregate().join();
        assertThat(res.status()).isEqualTo(OK);
        res = AnnotatedHttpServiceTest.client.get("/exception/default/409").aggregate().join();
        assertThat(res.status()).isEqualTo(CONFLICT);
    }
}

