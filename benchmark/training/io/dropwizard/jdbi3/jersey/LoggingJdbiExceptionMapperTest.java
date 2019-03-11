package io.dropwizard.jdbi3.jersey;


import java.sql.SQLException;
import org.jdbi.v3.core.JdbiException;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.transaction.TransactionException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class LoggingJdbiExceptionMapperTest {
    private LoggingJdbiExceptionMapper jdbiExceptionMapper;

    private Logger logger;

    @Test
    public void testSqlExceptionIsCause() throws Exception {
        StatementContext statementContext = Mockito.mock(StatementContext.class);
        RuntimeException runtimeException = new RuntimeException("DB is down");
        SQLException sqlException = new SQLException("DB error", runtimeException);
        JdbiException jdbiException = new org.jdbi.v3.core.result.NoResultsException("Unable get a result set", sqlException, statementContext);
        jdbiExceptionMapper.logException(9812, jdbiException);
        Mockito.verify(logger).error("Error handling a request: 0000000000002654", sqlException);
        Mockito.verify(logger).error("Error handling a request: 0000000000002654", runtimeException);
        Mockito.verify(logger, Mockito.never()).error("Error handling a request: 0000000000002654", jdbiException);
    }

    @Test
    public void testPlainJdbiException() throws Exception {
        JdbiException jdbiException = new TransactionException("Transaction failed for unknown reason");
        jdbiExceptionMapper.logException(9812, jdbiException);
        Mockito.verify(logger).error("Error handling a request: 0000000000002654", jdbiException);
    }
}

