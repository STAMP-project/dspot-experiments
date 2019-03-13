package com.codahale.metrics.jdbi3;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jdbi3.strategies.StatementNameStrategy;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.Test;
import org.mockito.Mockito;


public class InstrumentedSqlLoggerTest {
    @Test
    public void logsExecutionTime() {
        final MetricRegistry mockRegistry = Mockito.mock(MetricRegistry.class);
        final StatementNameStrategy mockNameStrategy = Mockito.mock(StatementNameStrategy.class);
        final InstrumentedSqlLogger logger = new InstrumentedSqlLogger(mockRegistry, mockNameStrategy);
        final StatementContext mockContext = Mockito.mock(StatementContext.class);
        final Timer mockTimer = Mockito.mock(Timer.class);
        final String statementName = "my-fake-name";
        final long fakeElapsed = 1234L;
        Mockito.when(mockNameStrategy.getStatementName(mockContext)).thenReturn(statementName);
        Mockito.when(mockRegistry.timer(statementName)).thenReturn(mockTimer);
        Mockito.when(mockContext.getElapsedTime(ChronoUnit.NANOS)).thenReturn(fakeElapsed);
        logger.logAfterExecution(mockContext);
        Mockito.verify(mockTimer).update(fakeElapsed, TimeUnit.NANOSECONDS);
    }

    @Test
    public void logsExceptionTime() {
        final MetricRegistry mockRegistry = Mockito.mock(MetricRegistry.class);
        final StatementNameStrategy mockNameStrategy = Mockito.mock(StatementNameStrategy.class);
        final InstrumentedSqlLogger logger = new InstrumentedSqlLogger(mockRegistry, mockNameStrategy);
        final StatementContext mockContext = Mockito.mock(StatementContext.class);
        final Timer mockTimer = Mockito.mock(Timer.class);
        final String statementName = "my-fake-name";
        final long fakeElapsed = 1234L;
        Mockito.when(mockNameStrategy.getStatementName(mockContext)).thenReturn(statementName);
        Mockito.when(mockRegistry.timer(statementName)).thenReturn(mockTimer);
        Mockito.when(mockContext.getElapsedTime(ChronoUnit.NANOS)).thenReturn(fakeElapsed);
        logger.logException(mockContext, new SQLException());
        Mockito.verify(mockTimer).update(fakeElapsed, TimeUnit.NANOSECONDS);
    }
}

