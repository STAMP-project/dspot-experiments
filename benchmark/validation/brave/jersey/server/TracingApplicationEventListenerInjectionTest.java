package brave.jersey.server;


import Reporter.NOOP;
import brave.Tracing;
import brave.http.HttpTracing;
import brave.propagation.ThreadLocalCurrentTraceContext;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;


public class TracingApplicationEventListenerInjectionTest {
    Tracing tracing = Tracing.newBuilder().currentTraceContext(ThreadLocalCurrentTraceContext.create()).spanReporter(NOOP).build();

    Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(HttpTracing.class).toInstance(HttpTracing.create(tracing));
        }
    });

    @Test
    public void onlyRequiresHttpTracing() {
        assertThat(injector.getInstance(TracingApplicationEventListener.class)).isNotNull();
    }
}

