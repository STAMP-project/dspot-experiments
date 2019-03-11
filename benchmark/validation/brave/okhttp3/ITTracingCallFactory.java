package brave.okhttp3;


import Span.Kind.CLIENT;
import brave.ScopedSpan;
import brave.Tracer;
import brave.test.http.ITHttpAsyncClient;
import java.util.Arrays;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;


public class ITTracingCallFactory extends ITHttpAsyncClient<Call.Factory> {
    @Test
    public void currentSpanVisibleToUserInterceptors() throws Exception {
        Tracer tracer = httpTracing.tracing().tracer();
        server.enqueue(new MockResponse());
        closeClient(client);
        client = TracingCallFactory.create(httpTracing, new OkHttpClient.Builder().addInterceptor(( chain) -> chain.proceed(chain.request().newBuilder().addHeader("my-id", currentTraceContext.get().traceIdString()).build())).build());
        ScopedSpan parent = tracer.startScopedSpan("test");
        try {
            get(client, "/foo");
        } finally {
            parent.finish();
        }
        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("x-b3-traceId")).isEqualTo(request.getHeader("my-id"));
        // we report one in-process and one RPC client span
        assertThat(Arrays.asList(takeSpan(), takeSpan())).extracting(Span::kind).containsOnly(null, CLIENT);
    }
}

