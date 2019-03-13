package brave.kafka.clients;


import Sampler.NEVER_SAMPLE;
import brave.Span;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;
import org.mockito.Mockito;


public class TracingCallbackTest extends BaseTracingTest {
    @Test
    public void create_returns_input_on_noop() {
        Span span = tracing.tracer().withSampler(NEVER_SAMPLE).nextSpan();
        Callback delegate = Mockito.mock(Callback.class);
        Callback tracingCallback = TracingCallback.create(delegate, span, current);
        assertThat(tracingCallback).isSameAs(delegate);
    }

    @Test
    public void on_completion_should_finish_span() {
        Span span = tracing.tracer().nextSpan().start();
        Callback tracingCallback = TracingCallback.create(null, span, current);
        tracingCallback.onCompletion(createRecordMetadata(), null);
        assertThat(spans.getFirst()).isNotNull();
    }

    @Test
    public void on_completion_should_tag_if_exception() {
        Span span = tracing.tracer().nextSpan().start();
        Callback tracingCallback = TracingCallback.create(null, span, current);
        tracingCallback.onCompletion(null, new Exception("Test exception"));
        assertThat(spans.getFirst().tags()).containsEntry("error", "Test exception");
    }

    @Test
    public void on_completion_should_forward_then_finish_span() {
        Span span = tracing.tracer().nextSpan().start();
        Callback delegate = Mockito.mock(Callback.class);
        Callback tracingCallback = TracingCallback.create(delegate, span, current);
        RecordMetadata md = createRecordMetadata();
        tracingCallback.onCompletion(md, null);
        Mockito.verify(delegate).onCompletion(md, null);
        assertThat(spans.getFirst()).isNotNull();
    }

    @Test
    public void on_completion_should_have_span_in_scope() {
        Span span = tracing.tracer().nextSpan().start();
        Callback delegate = ( metadata, exception) -> assertThat(current.get()).isSameAs(span.context());
        TracingCallback.create(delegate, span, current).onCompletion(createRecordMetadata(), null);
    }

    @Test
    public void on_completion_should_forward_then_tag_if_exception() {
        Span span = tracing.tracer().nextSpan().start();
        Callback delegate = Mockito.mock(Callback.class);
        Callback tracingCallback = TracingCallback.create(delegate, span, current);
        RecordMetadata md = createRecordMetadata();
        Exception e = new Exception("Test exception");
        tracingCallback.onCompletion(md, e);
        Mockito.verify(delegate).onCompletion(md, e);
        assertThat(spans.getFirst().tags()).containsEntry("error", "Test exception");
    }
}

