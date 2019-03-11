package brave.mysql;


import brave.Span;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TracingStatementInterceptorTest {
    @Mock
    Connection connection;

    @Mock
    DatabaseMetaData metaData;

    @Mock
    Span span;

    String url = "jdbc:mysql://myhost:5555/mydatabase";

    @Test
    public void parseServerIpAndPort_ipFromHost_portFromUrl() throws SQLException {
        setupAndReturnPropertiesForHost("1.2.3.4");
        TracingStatementInterceptor.parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("mysql");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_serviceNameFromDatabaseName() throws SQLException {
        setupAndReturnPropertiesForHost("1.2.3.4");
        Mockito.when(connection.getCatalog()).thenReturn("mydatabase");
        TracingStatementInterceptor.parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("mysql-mydatabase");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_propertiesOverrideServiceName() throws SQLException {
        setupAndReturnPropertiesForHost("1.2.3.4").setProperty("zipkinServiceName", "foo");
        TracingStatementInterceptor.parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("foo");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_emptyZipkinServiceNameIgnored() throws SQLException {
        setupAndReturnPropertiesForHost("1.2.3.4").setProperty("zipkinServiceName", "");
        TracingStatementInterceptor.parseServerIpAndPort(connection, span);
        Mockito.verify(span).remoteServiceName("mysql");
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 5555);
    }

    @Test
    public void parseServerIpAndPort_doesntCrash() throws SQLException {
        Mockito.when(connection.getMetaData()).thenThrow(new SQLException());
        TracingStatementInterceptor.parseServerIpAndPort(connection, span);
        Mockito.verifyNoMoreInteractions(span);
    }
}

