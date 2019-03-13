package io.dropwizard.jdbi3;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jdbi3.InstrumentedSqlLogger;
import com.codahale.metrics.jdbi3.strategies.StatementNameStrategy;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import java.util.UUID;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlStatements;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JdbiFactoryTest {
    @Test
    public void testBuild() {
        final Environment environment = Mockito.mock(Environment.class);
        final MetricRegistry metrics = Mockito.mock(MetricRegistry.class);
        final LifecycleEnvironment lifecycle = Mockito.mock(LifecycleEnvironment.class);
        final HealthCheckRegistry healthChecks = Mockito.mock(HealthCheckRegistry.class);
        final PooledDataSourceFactory configuration = Mockito.mock(PooledDataSourceFactory.class);
        final String name = UUID.randomUUID().toString();
        final ManagedDataSource dataSource = Mockito.mock(ManagedDataSource.class);
        final String validationQuery = UUID.randomUUID().toString();
        final Jdbi jdbi = Mockito.mock(Jdbi.class);
        final SqlStatements sqlStatements = new SqlStatements();
        Mockito.when(environment.metrics()).thenReturn(metrics);
        Mockito.when(environment.lifecycle()).thenReturn(lifecycle);
        Mockito.when(environment.healthChecks()).thenReturn(healthChecks);
        Mockito.when(configuration.build(metrics, name)).thenReturn(dataSource);
        Mockito.when(configuration.getValidationQuery()).thenReturn(validationQuery);
        Mockito.when(configuration.isAutoCommentsEnabled()).thenReturn(true);
        Mockito.when(jdbi.getConfig(SqlStatements.class)).thenReturn(sqlStatements);
        final JdbiFactory factory = Mockito.spy(new JdbiFactory());
        Mockito.when(factory.newInstance(dataSource)).thenReturn(jdbi);
        final Jdbi result = factory.build(environment, configuration, name);
        assertThat(result).isSameAs(jdbi);
        Mockito.verify(lifecycle).manage(dataSource);
        Mockito.verify(healthChecks).register(ArgumentMatchers.eq(name), ArgumentMatchers.any(JdbiHealthCheck.class));
        Mockito.verify(jdbi).setSqlLogger(ArgumentMatchers.any(InstrumentedSqlLogger.class));
        Mockito.verify(factory).buildSQLLogger(ArgumentMatchers.same(metrics), ArgumentMatchers.any(StatementNameStrategy.class));
        Mockito.verify(jdbi).setTemplateEngine(ArgumentMatchers.any(NamePrependingTemplateEngine.class));
        Mockito.verify(factory).configure(jdbi);
    }
}

