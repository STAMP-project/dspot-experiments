package brave.jms;


import Sampler.NEVER_SAMPLE;
import brave.Span;
import javax.jms.CompletionListener;
import javax.jms.Message;
import org.junit.Test;
import org.mockito.Mockito;


public class TracingCompletionListenerTest extends JmsTest {
    @Test
    public void create_returns_input_on_noop() {
        Span span = tracing.tracer().withSampler(NEVER_SAMPLE).nextSpan();
        CompletionListener delegate = Mockito.mock(CompletionListener.class);
        CompletionListener tracingCompletionListener = TracingCompletionListener.create(delegate, span, current);
        assertThat(tracingCompletionListener).isSameAs(delegate);
    }

    @Test
    public void on_completion_should_finish_span() throws Exception {
        Message message = Mockito.mock(Message.class);
        Span span = tracing.tracer().nextSpan().start();
        CompletionListener tracingCompletionListener = TracingCompletionListener.create(Mockito.mock(CompletionListener.class), span, current);
        tracingCompletionListener.onCompletion(message);
        assertThat(takeSpan()).isNotNull();
    }

    @Test
    public void on_exception_should_tag_if_exception() throws Exception {
        Message message = Mockito.mock(Message.class);
        Span span = tracing.tracer().nextSpan().start();
        CompletionListener tracingCompletionListener = TracingCompletionListener.create(Mockito.mock(CompletionListener.class), span, current);
        tracingCompletionListener.onException(message, new Exception("Test exception"));
        assertThat(takeSpan().tags()).containsEntry("error", "Test exception");
    }

    @Test
    public void on_completion_should_forward_then_finish_span() throws Exception {
        Message message = Mockito.mock(Message.class);
        Span span = tracing.tracer().nextSpan().start();
        CompletionListener delegate = Mockito.mock(CompletionListener.class);
        CompletionListener tracingCompletionListener = TracingCompletionListener.create(delegate, span, current);
        tracingCompletionListener.onCompletion(message);
        Mockito.verify(delegate).onCompletion(message);
        assertThat(takeSpan()).isNotNull();
    }

    @Test
    public void on_completion_should_have_span_in_scope() throws Exception {
        Message message = Mockito.mock(Message.class);
        Span span = tracing.tracer().nextSpan().start();
        CompletionListener delegate = new CompletionListener() {
            @Override
            public void onCompletion(Message message) {
                assertThat(current.get()).isSameAs(span.context());
            }

            @Override
            public void onException(Message message, Exception exception) {
                throw new AssertionError();
            }
        };
        TracingCompletionListener.create(delegate, span, current).onCompletion(message);
        takeSpan();// consumer reported span

    }

    @Test
    public void on_exception_should_forward_then_tag() throws Exception {
        Message message = Mockito.mock(Message.class);
        Span span = tracing.tracer().nextSpan().start();
        CompletionListener delegate = Mockito.mock(CompletionListener.class);
        CompletionListener tracingCompletionListener = TracingCompletionListener.create(delegate, span, current);
        Exception e = new Exception("Test exception");
        tracingCompletionListener.onException(message, e);
        Mockito.verify(delegate).onException(message, e);
        assertThat(takeSpan().tags()).containsEntry("error", "Test exception");
    }
}

