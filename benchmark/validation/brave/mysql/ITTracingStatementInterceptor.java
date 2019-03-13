package brave.mysql;


import Sampler.ALWAYS_SAMPLE;
import Span.Kind.CLIENT;
import brave.ScopedSpan;
import brave.Tracing;
import java.sql.Connection;
import java.util.ArrayList;
import org.junit.Test;
import zipkin2.Span;


public class ITTracingStatementInterceptor {
    static final String QUERY = "select 'hello world'";

    /**
     * JDBC is synchronous and we aren't using thread pools: everything happens on the main thread
     */
    ArrayList<Span> spans = new ArrayList<>();

    Tracing tracing = tracingBuilder(ALWAYS_SAMPLE).build();

    Connection connection;

    @Test
    public void makesChildOfCurrentSpan() throws Exception {
        ScopedSpan parent = tracing.tracer().startScopedSpan("test");
        try {
            prepareExecuteSelect(ITTracingStatementInterceptor.QUERY);
        } finally {
            parent.finish();
        }
        assertThat(spans).hasSize(2);
    }

    @Test
    public void reportsClientKindToZipkin() throws Exception {
        prepareExecuteSelect(ITTracingStatementInterceptor.QUERY);
        assertThat(spans).extracting(Span::kind).containsExactly(CLIENT);
    }

    @Test
    public void defaultSpanNameIsOperationName() throws Exception {
        prepareExecuteSelect(ITTracingStatementInterceptor.QUERY);
        assertThat(spans).extracting(Span::name).containsExactly("select");
    }

    /**
     * This intercepts all SQL, not just queries. This ensures single-word statements work
     */
    @Test
    public void defaultSpanNameIsOperationName_oneWord() throws Exception {
        connection.setAutoCommit(false);
        connection.commit();
        assertThat(spans).extracting(Span::name).contains("commit");
    }

    @Test
    public void addsQueryTag() throws Exception {
        prepareExecuteSelect(ITTracingStatementInterceptor.QUERY);
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).containsExactly(entry("sql.query", ITTracingStatementInterceptor.QUERY));
    }

    @Test
    public void reportsServerAddress() throws Exception {
        prepareExecuteSelect(ITTracingStatementInterceptor.QUERY);
        assertThat(spans).extracting(Span::remoteServiceName).contains("myservice");
    }
}

