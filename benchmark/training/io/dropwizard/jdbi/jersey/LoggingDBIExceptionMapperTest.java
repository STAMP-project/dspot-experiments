package io.dropwizard.jdbi.jersey;


import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.skife.jdbi.v2.exceptions.TransactionFailedException;
import org.slf4j.Logger;


public class LoggingDBIExceptionMapperTest {
    private LoggingDBIExceptionMapper dbiExceptionMapper;

    private Logger logger;

    @Test
    public void testSqlExceptionIsCause() throws Exception {
        StatementContext statementContext = Mockito.mock(StatementContext.class);
        RuntimeException runtimeException = new RuntimeException("DB is down");
        SQLException sqlException = new SQLException("DB error", runtimeException);
        DBIException dbiException = new org.skife.jdbi.v2.exceptions.NoResultsException("Unable get a result set", sqlException, statementContext);
        dbiExceptionMapper.logException(9812, dbiException);
        Mockito.verify(logger).error("Error handling a request: 0000000000002654", sqlException);
        Mockito.verify(logger).error("Error handling a request: 0000000000002654", runtimeException);
        Mockito.verify(logger, Mockito.never()).error("Error handling a request: 0000000000002654", dbiException);
    }

    @Test
    public void testPlainDBIException() throws Exception {
        DBIException dbiException = new TransactionFailedException("Transaction failed for unknown reason");
        dbiExceptionMapper.logException(9812, dbiException);
        Mockito.verify(logger).error("Error handling a request: 0000000000002654", dbiException);
    }
}

