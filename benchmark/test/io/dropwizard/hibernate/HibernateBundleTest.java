package io.dropwizard.hibernate;


import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Collections;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HibernateBundleTest {
    private final DataSourceFactory dbConfig = new DataSourceFactory();

    private final List<Class<?>> entities = Collections.singletonList(Person.class);

    private final SessionFactoryFactory factory = Mockito.mock(SessionFactoryFactory.class);

    private final SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);

    private final Configuration configuration = Mockito.mock(Configuration.class);

    private final HealthCheckRegistry healthChecks = Mockito.mock(HealthCheckRegistry.class);

    private final JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

    private final Environment environment = Mockito.mock(Environment.class);

    private final HibernateBundle<Configuration> bundle = new HibernateBundle<Configuration>(entities, factory) {
        @Override
        public DataSourceFactory getDataSourceFactory(Configuration configuration) {
            return dbConfig;
        }
    };

    @Test
    public void addsHibernateSupportToJackson() throws Exception {
        final ObjectMapper objectMapperFactory = Mockito.mock(ObjectMapper.class);
        final Bootstrap<?> bootstrap = Mockito.mock(Bootstrap.class);
        Mockito.when(bootstrap.getObjectMapper()).thenReturn(objectMapperFactory);
        bundle.initialize(bootstrap);
        final ArgumentCaptor<Module> captor = ArgumentCaptor.forClass(Module.class);
        Mockito.verify(objectMapperFactory).registerModule(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(Hibernate5Module.class);
    }

    @Test
    public void buildsASessionFactory() throws Exception {
        bundle.run(configuration, environment);
        Mockito.verify(factory).build(bundle, environment, dbConfig, entities, "hibernate");
    }

    @Test
    public void registersATransactionalListener() throws Exception {
        bundle.run(configuration, environment);
        final ArgumentCaptor<UnitOfWorkApplicationListener> captor = ArgumentCaptor.forClass(UnitOfWorkApplicationListener.class);
        Mockito.verify(jerseyEnvironment).register(captor.capture());
    }

    @Test
    public void registersASessionFactoryHealthCheck() throws Exception {
        dbConfig.setValidationQuery("SELECT something");
        bundle.run(configuration, environment);
        final ArgumentCaptor<SessionFactoryHealthCheck> captor = ArgumentCaptor.forClass(SessionFactoryHealthCheck.class);
        Mockito.verify(healthChecks).register(ArgumentMatchers.eq("hibernate"), captor.capture());
        assertThat(captor.getValue().getSessionFactory()).isEqualTo(sessionFactory);
        assertThat(captor.getValue().getValidationQuery()).isEqualTo("SELECT something");
    }

    @Test
    public void registersACustomNameOfHealthCheckAndDBPoolMetrics() throws Exception {
        final HibernateBundle<Configuration> customBundle = new HibernateBundle<Configuration>(entities, factory) {
            @Override
            public DataSourceFactory getDataSourceFactory(Configuration configuration) {
                return dbConfig;
            }

            @Override
            protected String name() {
                return "custom-hibernate";
            }
        };
        Mockito.when(factory.build(ArgumentMatchers.eq(customBundle), ArgumentMatchers.any(Environment.class), ArgumentMatchers.any(DataSourceFactory.class), ArgumentMatchers.anyList(), ArgumentMatchers.eq("custom-hibernate"))).thenReturn(sessionFactory);
        customBundle.run(configuration, environment);
        final ArgumentCaptor<SessionFactoryHealthCheck> captor = ArgumentCaptor.forClass(SessionFactoryHealthCheck.class);
        Mockito.verify(healthChecks).register(ArgumentMatchers.eq("custom-hibernate"), captor.capture());
    }

    @Test
    public void hasASessionFactory() throws Exception {
        bundle.run(configuration, environment);
        assertThat(bundle.getSessionFactory()).isEqualTo(sessionFactory);
    }
}

