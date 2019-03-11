package io.dropwizard.jdbi3.bundles;


import io.dropwizard.Configuration;
import io.dropwizard.jdbi3.jersey.LoggingJdbiExceptionMapper;
import io.dropwizard.jdbi3.jersey.LoggingSQLExceptionMapper;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JdbiExceptionsBundleTest {
    @Test
    public void test() {
        Environment environment = Mockito.mock(Environment.class);
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);
        Mockito.when(environment.jersey()).thenReturn(jerseyEnvironment);
        new JdbiExceptionsBundle().run(new Configuration(), environment);
        Mockito.verify(jerseyEnvironment).register(ArgumentMatchers.isA(LoggingSQLExceptionMapper.class));
        Mockito.verify(jerseyEnvironment).register(ArgumentMatchers.isA(LoggingJdbiExceptionMapper.class));
    }
}

