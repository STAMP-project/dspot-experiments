package example.armeria.server.sse;


import HttpStatus.OK;
import MediaType.EVENT_STREAM;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.server.Server;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class MainTest {
    private static Server server;

    private static HttpClient client;

    @Test
    public void testServerSentEvents() {
        StepVerifier.create(Flux.from(MainTest.client.get("/long")).log()).expectNext(HttpHeaders.of(OK).contentType(EVENT_STREAM)).expectNext(HttpData.ofUtf8("data:0\n\n")).expectNext(HttpData.ofUtf8("data:1\n\n")).expectNext(HttpData.ofUtf8("data:2\n\n")).expectNext(HttpData.ofUtf8("data:3\n\n")).expectNext(HttpData.ofUtf8("data:4\n\n")).assertNext(( o) -> assertThat(o.isEndOfStream())).expectComplete().verify();
        StepVerifier.create(Flux.from(MainTest.client.get("/short")).log()).expectNext(HttpHeaders.of(OK).contentType(EVENT_STREAM)).expectNext(HttpData.ofUtf8("id:0\ndata:5\nretry:5000\n\n")).expectNext(HttpData.ofUtf8("id:1\ndata:6\nretry:5000\n\n")).expectNext(HttpData.ofUtf8("id:2\ndata:7\nretry:5000\n\n")).expectNext(HttpData.ofUtf8("id:3\ndata:8\nretry:5000\n\n")).expectNext(HttpData.ofUtf8("id:4\ndata:9\nretry:5000\n\n")).assertNext(( o) -> assertThat(o.isEndOfStream())).expectComplete().verify();
    }
}

