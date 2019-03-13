package io.dropwizard.jdbi;


import StringColumnMapper.INSTANCE;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.setup.Environment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;


public class JDBITest {
    private final DataSourceFactory hsqlConfig = new DataSourceFactory();

    {
        BootstrapLogging.bootstrap();
        hsqlConfig.setUrl(("jdbc:h2:mem:JDBITest-" + (System.currentTimeMillis())));
        hsqlConfig.setUser("sa");
        hsqlConfig.setDriverClass("org.h2.Driver");
        hsqlConfig.setValidationQuery("SELECT 1");
    }

    private final HealthCheckRegistry healthChecks = Mockito.mock(HealthCheckRegistry.class);

    private final LifecycleEnvironment lifecycleEnvironment = Mockito.mock(LifecycleEnvironment.class);

    private final Environment environment = Mockito.mock(Environment.class);

    private final DBIFactory factory = new DBIFactory();

    private final List<Managed> managed = new ArrayList<>();

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private DBI dbi = Mockito.mock(DBI.class);

    @Test
    public void createsAValidDBI() throws Exception {
        final Handle handle = dbi.open();
        final Query<String> names = handle.createQuery("SELECT name FROM people WHERE age < ?").bind(0, 50).map(INSTANCE);
        assertThat(names).containsOnly("Coda Hale", "Kris Gale");
    }

    @Test
    public void managesTheDatabaseWithTheEnvironment() throws Exception {
        Mockito.verify(lifecycleEnvironment).manage(ArgumentMatchers.any(ManagedDataSource.class));
    }

    @Test
    public void sqlObjectsCanAcceptOptionalParams() throws Exception {
        final PersonDAO dao = dbi.open(PersonDAO.class);
        assertThat(dao.findByName(Optional.of("Coda Hale"))).isEqualTo("Coda Hale");
    }

    @Test
    public void sqlObjectsCanReturnImmutableLists() throws Exception {
        final PersonDAO dao = dbi.open(PersonDAO.class);
        assertThat(dao.findAllNames()).containsOnly("Coda Hale", "Kris Gale", "Old Guy", "Alice Example");
    }

    @Test
    public void sqlObjectsCanReturnImmutableSets() throws Exception {
        final PersonDAO dao = dbi.open(PersonDAO.class);
        assertThat(dao.findAllUniqueNames()).containsOnly("Coda Hale", "Kris Gale", "Old Guy", "Alice Example");
    }

    @Test
    public void sqlObjectsCanReturnOptional() throws Exception {
        final PersonDAO dao = dbi.open(PersonDAO.class);
        final Optional<String> found = dao.findByEmail("chale@yammer-inc.com");
        assertThat(found).isNotNull();
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo("Coda Hale");
        final Optional<String> missing = dao.findByEmail("cemalettin.koc@gmail.com");
        assertThat(missing).isNotNull();
        assertThat(missing.isPresent()).isFalse();
        assertThat(missing.orElse(null)).isNull();
    }

    @Test
    public void sqlObjectsCanReturnJodaDateTime() throws Exception {
        final PersonDAO dao = dbi.open(PersonDAO.class);
        final DateTime found = dao.getLatestCreatedAt(new DateTime(1365465077000L));
        assertThat(found).isNotNull();
        assertThat(found.getMillis()).isEqualTo(1365465078000L);
        assertThat(found).isEqualTo(new DateTime(1365465078000L));
        final DateTime notFound = dao.getCreatedAtByEmail("alice@example.org");
        assertThat(notFound).isNull();
        final Optional<DateTime> absentDateTime = dao.getCreatedAtByName("Alice Example");
        assertThat(absentDateTime).isNotNull();
        assertThat(absentDateTime.isPresent()).isFalse();
        final Optional<DateTime> presentDateTime = dao.getCreatedAtByName("Coda Hale");
        assertThat(presentDateTime).isNotNull();
        assertThat(presentDateTime.isPresent()).isTrue();
    }
}

