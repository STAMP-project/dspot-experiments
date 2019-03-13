package io.dropwizard.jdbi.args;


import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public class OptionalLongTest {
    private final Environment env = new Environment("test-optional-long", Jackson.newObjectMapper(), Validators.newValidator(), new MetricRegistry(), null);

    private OptionalLongTest.TestDao dao;

    @Test
    public void testPresent() {
        dao.insert(1, OptionalLong.of(42L));
        assertThat(dao.findOptionalLongById(1).getAsLong()).isEqualTo(42);
    }

    @Test
    public void testAbsent() {
        dao.insert(2, OptionalLong.empty());
        assertThat(dao.findOptionalLongById(2).isPresent()).isFalse();
    }

    interface TestDao {
        @SqlUpdate("INSERT INTO test(id, optional) VALUES (:id, :optional)")
        void insert(@Bind("id")
        int id, @Bind("optional")
        OptionalLong optional);

        @SqlQuery("SELECT optional FROM test WHERE id = :id")
        OptionalLong findOptionalLongById(@Bind("id")
        int id);
    }
}

