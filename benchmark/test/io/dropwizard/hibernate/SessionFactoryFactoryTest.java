package io.dropwizard.hibernate;


import DateTimeZone.UTC;
import EmptyInterceptor.INSTANCE;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedPooledDataSource;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.setup.Environment;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SessionFactoryFactoryTest {
    static {
        BootstrapLogging.bootstrap();
    }

    private final SessionFactoryFactory factory = new SessionFactoryFactory();

    private final HibernateBundle<?> bundle = Mockito.mock(HibernateBundle.class);

    private final LifecycleEnvironment lifecycleEnvironment = Mockito.mock(LifecycleEnvironment.class);

    private final Environment environment = Mockito.mock(Environment.class);

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private DataSourceFactory config = new DataSourceFactory();

    @Nullable
    private SessionFactory sessionFactory;

    @Test
    public void managesTheSessionFactory() throws Exception {
        build();
        Mockito.verify(lifecycleEnvironment).manage(ArgumentMatchers.any(SessionFactoryManager.class));
    }

    @Test
    public void callsBundleToConfigure() throws Exception {
        build();
        Mockito.verify(bundle).configure(ArgumentMatchers.any(Configuration.class));
    }

    @Test
    public void setsPoolName() {
        build();
        ArgumentCaptor<SessionFactoryManager> sessionFactoryManager = ArgumentCaptor.forClass(SessionFactoryManager.class);
        Mockito.verify(lifecycleEnvironment).manage(sessionFactoryManager.capture());
        ManagedPooledDataSource dataSource = ((ManagedPooledDataSource) (sessionFactoryManager.getValue().getDataSource()));
        assertThat(dataSource.getPool().getName()).isEqualTo("hibernate");
    }

    @Test
    public void setsACustomPoolName() {
        this.sessionFactory = factory.build(bundle, environment, config, Collections.singletonList(Person.class), "custom-hibernate-db");
        ArgumentCaptor<SessionFactoryManager> sessionFactoryManager = ArgumentCaptor.forClass(SessionFactoryManager.class);
        Mockito.verify(lifecycleEnvironment).manage(sessionFactoryManager.capture());
        ManagedPooledDataSource dataSource = ((ManagedPooledDataSource) (sessionFactoryManager.getValue().getDataSource()));
        assertThat(dataSource.getPool().getName()).isEqualTo("custom-hibernate-db");
    }

    @Test
    public void buildsAWorkingSessionFactory() throws Exception {
        build();
        try (Session session = Objects.requireNonNull(sessionFactory).openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createNativeQuery("DROP TABLE people IF EXISTS").executeUpdate();
            session.createNativeQuery("CREATE TABLE people (name varchar(100) primary key, email varchar(100), birthday timestamp(0))").executeUpdate();
            session.createNativeQuery("INSERT INTO people VALUES ('Coda', 'coda@example.com', '1979-01-02 00:22:00')").executeUpdate();
            transaction.commit();
            final Person entity = session.get(Person.class, "Coda");
            assertThat(entity.getName()).isEqualTo("Coda");
            assertThat(entity.getEmail()).isEqualTo("coda@example.com");
            assertThat(Objects.requireNonNull(entity.getBirthday()).toDateTime(UTC)).isEqualTo(new org.joda.time.DateTime(1979, 1, 2, 0, 22, DateTimeZone.UTC));
        }
    }

    @Test
    public void configureRunsBeforeSessionFactoryCreation() {
        final SessionFactoryFactory customFactory = new SessionFactoryFactory() {
            @Override
            protected void configure(Configuration configuration, ServiceRegistry registry) {
                super.configure(configuration, registry);
                configuration.setInterceptor(INSTANCE);
            }
        };
        sessionFactory = customFactory.build(bundle, environment, config, Collections.singletonList(Person.class));
        assertThat(sessionFactory.getSessionFactoryOptions().getInterceptor()).isSameAs(INSTANCE);
    }
}

