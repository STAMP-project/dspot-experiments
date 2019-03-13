package brave.p6spy;


import Sampler.ALWAYS_SAMPLE;
import Span.Kind.CLIENT;
import brave.ScopedSpan;
import brave.Tracing;
import java.sql.Connection;
import java.util.ArrayList;
import org.junit.Test;
import zipkin2.Span;


public class ITTracingP6Factory {
    static final String URL = "jdbc:p6spy:derby:memory:p6spy;create=true";

    static final String QUERY = "SELECT 1 FROM SYSIBM.SYSDUMMY1";

    // Get rid of annoying derby.log
    static {
        DerbyUtils.disableLog();
    }

    /**
     * JDBC is synchronous and we aren't using thread pools: everything happens on the main thread
     */
    ArrayList<Span> spans = new ArrayList<>();

    Tracing tracing = ITTracingP6Factory.tracingBuilder(ALWAYS_SAMPLE, spans).build();

    Connection connection;

    @Test
    public void makesChildOfCurrentSpan() throws Exception {
        ScopedSpan parent = tracing.tracer().startScopedSpan("test");
        try {
            prepareExecuteSelect(ITTracingP6Factory.QUERY);
        } finally {
            parent.finish();
        }
        assertThat(spans).hasSize(2);
    }

    @Test
    public void reportsClientKindToZipkin() throws Exception {
        prepareExecuteSelect(ITTracingP6Factory.QUERY);
        assertThat(spans).flatExtracting(Span::kind).containsExactly(CLIENT);
    }

    @Test
    public void defaultSpanNameIsOperationName() throws Exception {
        prepareExecuteSelect(ITTracingP6Factory.QUERY);
        assertThat(spans).extracting(Span::name).containsExactly("select");
    }

    @Test
    public void addsQueryTag() throws Exception {
        prepareExecuteSelect(ITTracingP6Factory.QUERY);
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).containsExactly(entry("sql.query", ITTracingP6Factory.QUERY));
    }

    @Test
    public void reportsServerAddress() throws Exception {
        prepareExecuteSelect(ITTracingP6Factory.QUERY);
        assertThat(spans).flatExtracting(Span::remoteServiceName).containsExactly("myservice");
    }
}

