package org.stagemonitor.jdbc;


import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;
import org.stagemonitor.tracing.RequestMonitor;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.profiler.CallStackElement;


@RunWith(Parameterized.class)
public class ConnectionMonitoringTransformerTest {
    private static final String DRIVER_CLASS_NAME = "org.hsqldb.jdbcDriver";

    private static final String URL = "jdbc:hsqldb:mem:test";

    private ConfigurationRegistry configuration;

    private DataSource dataSource;

    private RequestMonitor requestMonitor;

    private Metric2Registry metric2Registry;

    private ConnectionMonitoringTransformerTest.TestDao testDao;

    public ConnectionMonitoringTransformerTest(DataSource dataSource, Class<? extends DataSource> dataSourceClass) {
        this.dataSource = dataSource;
    }

    @Test
    public void monitorGetConnection() throws Exception {
        requestMonitor.monitor(new org.stagemonitor.tracing.MonitoredMethodRequest(configuration, "monitorGetConnectionUsernamePassword()", () -> {
            dataSource.getConnection().close();
        }));
        final Map<MetricName, Timer> timers = metric2Registry.getTimers();
        assertThat(timers).containsKey(name("get_jdbc_connection").tag("url", "SA@jdbc:hsqldb:mem:test").build());
    }

    @Test
    public void monitorGetConnectionUsernamePassword() throws Exception {
        try {
            dataSource.getConnection("sa", "").close();
        } catch (SQLFeatureNotSupportedException | UnsupportedOperationException e) {
            // ignore
            return;
        }
        requestMonitor.monitor(new org.stagemonitor.tracing.MonitoredMethodRequest(configuration, "monitorGetConnectionUsernamePassword()", () -> {
            dataSource.getConnection("sa", "").close();
        }));
        final Map<MetricName, Timer> timers = metric2Registry.getTimers();
        assertThat(timers).containsKey(name("get_jdbc_connection").tag("url", "SA@jdbc:hsqldb:mem:test").build());
    }

    @Test
    public void testRecordSqlPreparedStatement() throws Exception {
        final SpanContextInformation spanContext = requestMonitor.monitor(new org.stagemonitor.tracing.MonitoredMethodRequest(configuration, "testRecordSqlPreparedStatement", () -> testDao.executePreparedStatement()));
        final Map<MetricName, Timer> timers = metric2Registry.getTimers();
        assertThat(timers).isNotEmpty();
        assertThat(timers).containsKey(name("response_time").operationType("jdbc").operationName("All").build());
        assertThat(timers).containsKey(name("response_time").operationType("jdbc").operationName("ConnectionMonitoringTransformerTest$TestDao#executePreparedStatement").build());
        final Map<MetricName, Meter> meters = metric2Registry.getMeters();
        assertThat(meters).containsKey(name("external_requests_rate").operationName("testRecordSqlPreparedStatement").build());
        final CallStackElement callTree = spanContext.getCallTree();
        Assert.assertEquals("testRecordSqlPreparedStatement", callTree.getSignature());
        Assert.assertEquals(callTree.toString(), 1, callTree.getChildren().size());
        Assert.assertEquals("void org.stagemonitor.jdbc.ConnectionMonitoringTransformerTest$TestDao.executePreparedStatement()", callTree.getChildren().get(0).getSignature());
        Assert.assertEquals(callTree.toString(), "SELECT * from STAGEMONITOR ", callTree.getChildren().get(0).getChildren().get(0).getSignature());
    }

    @Test
    public void testRecordSqlStatement() throws Exception {
        final SpanContextInformation spanContext = requestMonitor.monitor(new org.stagemonitor.tracing.MonitoredMethodRequest(configuration, "testRecordSqlStatement", () -> {
            testDao.executeStatement();
        }));
        final Map<MetricName, Timer> timers = metric2Registry.getTimers();
        final String message = timers.keySet().toString();
        Assert.assertTrue(message, ((timers.size()) > 1));
        Assert.assertEquals(message, 1, timers.get(name("response_time").operationType("jdbc").operationName("ConnectionMonitoringTransformerTest$TestDao#executeStatement").build()).getCount());
        Assert.assertEquals(message, 1, timers.get(name("response_time").operationType("jdbc").operationName("All").build()).getCount());
        final CallStackElement callStack = spanContext.getCallTree();
        Assert.assertEquals("testRecordSqlStatement", callStack.getSignature());
        Assert.assertEquals("void org.stagemonitor.jdbc.ConnectionMonitoringTransformerTest$TestDao.executeStatement()", callStack.getChildren().get(0).getSignature());
        Assert.assertEquals("SELECT * from STAGEMONITOR ", callStack.getChildren().get(0).getChildren().get(0).getSignature());
    }

    public static class TestDao {
        private final DataSource dataSource;

        public TestDao(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        private void executePreparedStatement() throws SQLException {
            try (final Connection connection = dataSource.getConnection()) {
                final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from STAGEMONITOR");
                preparedStatement.execute();
                final ResultSet resultSet = preparedStatement.getResultSet();
                resultSet.next();
            }
        }

        private void executeStatement() throws SQLException {
            try (final Connection connection = dataSource.getConnection()) {
                connection.createStatement().execute("SELECT * from STAGEMONITOR");
            }
        }
    }
}

