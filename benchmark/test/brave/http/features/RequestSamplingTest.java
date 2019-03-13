package brave.http.features;


import HttpSampler.NEVER_SAMPLE;
import brave.Tracing;
import brave.http.HttpAdapter;
import brave.http.HttpSampler;
import brave.http.HttpTracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import java.util.concurrent.ConcurrentLinkedDeque;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import zipkin2.Span;


/**
 * This is an example of http request sampling
 */
public class RequestSamplingTest {
    @Rule
    public MockWebServer server = new MockWebServer();

    ConcurrentLinkedDeque<Span> spans = new ConcurrentLinkedDeque<>();

    Tracing tracing = Tracing.newBuilder().localServiceName("server").currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build()).spanReporter(spans::push).build();

    HttpTracing httpTracing = // client doesn't start new traces
    // server starts traces under the path /api
    HttpTracing.newBuilder(tracing).serverSampler(new HttpSampler() {
        @Override
        public <Req> Boolean trySample(HttpAdapter<Req, ?> adapter, Req request) {
            return adapter.path(request).startsWith("/api");
        }
    }).clientSampler(NEVER_SAMPLE).build();

    OkHttpClient client = new OkHttpClient();

    @Test
    public void serverDoesntTraceFoo() throws Exception {
        callServer("/foo");
        assertThat(spans).isEmpty();
    }

    @Test
    public void clientTracedWhenServerIs() throws Exception {
        callServer("/api");
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).contains(entry("http.path", "/api"), entry("http.path", "/next"));
    }
}

