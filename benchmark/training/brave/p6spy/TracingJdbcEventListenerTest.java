package brave.p6spy;


import Sampler.ALWAYS_SAMPLE;
import brave.ScopedSpan;
import brave.Span;
import brave.Tracing;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.StatementInformation;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TracingJdbcEventListenerTest {
    @Mock
    Connection connection;

    @Mock
    DatabaseMetaData metaData;

    @Mock
    StatementInformation statementInformation;

    @Mock
    ConnectionInformation ci;

    @Mock
    Span span;

    String url = "jdbc:mysql://1.2.3.4:5555/mydatabase";

    String urlWithServiceName = (url) + "?zipkinServiceName=mysql_service&foo=bar";

    String urlWithEmptyServiceName = (url) + "?zipkinServiceName=&foo=bar";

    String urlWithWhiteSpace = "jdbc:sqlserver://1.2.3.4;databaseName=mydatabase;applicationName=Microsoft JDBC Driver for SQL Server";

    @Test
    public void parseServerIpAndPort_IpAndPortFromUrl() throws SQLException {
        Mockito.when(connection.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getURL()).thenReturn(url);
        new TracingJdbcEventListener("", false).parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_serviceNameFromDatabaseName() throws SQLException {
        Mockito.when(connection.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getURL()).thenReturn(url);
        Mockito.when(connection.getCatalog()).thenReturn("mydatabase");
        new TracingJdbcEventListener("", false).parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("mydatabase");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_serviceNameFromUrl() throws SQLException {
        Mockito.when(connection.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getURL()).thenReturn(urlWithServiceName);
        new TracingJdbcEventListener("", false).parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("mysql_service");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_emptyServiceNameFromUrl() throws SQLException {
        Mockito.when(connection.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getURL()).thenReturn(urlWithEmptyServiceName);
        Mockito.when(connection.getCatalog()).thenReturn("mydatabase");
        new TracingJdbcEventListener("", false).parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("mydatabase");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_overrideServiceName() throws SQLException {
        Mockito.when(connection.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getURL()).thenReturn(url);
        new TracingJdbcEventListener("foo", false).parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("foo");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_doesntCrash() throws SQLException {
        Mockito.when(connection.getMetaData()).thenThrow(new SQLException());
        new TracingJdbcEventListener("", false).parseServerIpAndPort(connection, span);
        Mockito.verifyNoMoreInteractions(span);
    }

    @Test
    public void parseServerIpAndPort_withWhiteSpace() throws SQLException {
        Mockito.when(connection.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getURL()).thenReturn(urlWithWhiteSpace);
        new TracingJdbcEventListener("foo", false).parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("foo");
    }

    @Test
    public void nullSqlWontNPE() throws SQLException {
        ArrayList<zipkin2.Span> spans = new ArrayList<>();
        try (Tracing tracing = ITTracingP6Factory.tracingBuilder(ALWAYS_SAMPLE, spans).build()) {
            Mockito.when(statementInformation.getSql()).thenReturn(null);
            TracingJdbcEventListener listener = new TracingJdbcEventListener("", false);
            listener.onBeforeAnyExecute(statementInformation);
            listener.onAfterAnyExecute(statementInformation, 1, null);
            assertThat(spans).isEmpty();
        }
    }

    @Test
    public void handleAfterExecute_without_beforeExecute_getting_called() {
        Tracing tracing = ITTracingP6Factory.tracingBuilder(ALWAYS_SAMPLE, new ArrayList()).build();
        ScopedSpan parent = tracing.tracer().startScopedSpan("test");
        try {
            TracingJdbcEventListener listener = new TracingJdbcEventListener("", false);
            listener.onAfterAnyExecute(statementInformation, 1, null);
            listener.onAfterAnyExecute(statementInformation, 1, null);
        } finally {
            parent.finish();
        }
    }
}

