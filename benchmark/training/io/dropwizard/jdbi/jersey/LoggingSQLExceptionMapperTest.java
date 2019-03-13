package io.dropwizard.jdbi.jersey;


import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class LoggingSQLExceptionMapperTest {
    @Test
    public void testLogException() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        LoggingSQLExceptionMapper sqlExceptionMapper = new LoggingSQLExceptionMapper(logger);
        RuntimeException runtimeException = new RuntimeException("DB is down");
        SQLException sqlException = new SQLException("DB error", runtimeException);
        sqlExceptionMapper.logException(4981, sqlException);
        Mockito.verify(logger).error("Error handling a request: 0000000000001375", sqlException);
        Mockito.verify(logger).error("Error handling a request: 0000000000001375", runtimeException);
    }
}

