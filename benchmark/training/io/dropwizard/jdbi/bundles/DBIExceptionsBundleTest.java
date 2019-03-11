package io.dropwizard.jdbi.bundles;


import io.dropwizard.Configuration;
import io.dropwizard.jdbi.jersey.LoggingDBIExceptionMapper;
import io.dropwizard.jdbi.jersey.LoggingSQLExceptionMapper;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DBIExceptionsBundleTest {
    @Test
    public void test() {
        Environment environment = Mockito.mock(Environment.class);
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);
        Mockito.when(environment.jersey()).thenReturn(jerseyEnvironment);
        new DBIExceptionsBundle().run(new Configuration(), environment);
        Mockito.verify(jerseyEnvironment, Mockito.times(1)).register(ArgumentMatchers.isA(LoggingSQLExceptionMapper.class));
        Mockito.verify(jerseyEnvironment, Mockito.times(1)).register(ArgumentMatchers.isA(LoggingDBIExceptionMapper.class));
    }
}

