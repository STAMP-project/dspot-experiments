package io.dropwizard.jdbi.args;


import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public class OptionalIntTest {
    private final Environment env = new Environment("test-optional-int", Jackson.newObjectMapper(), Validators.newValidator(), new MetricRegistry(), null);

    private OptionalIntTest.TestDao dao;

    @Test
    public void testPresent() {
        dao.insert(1, OptionalInt.of(42));
        assertThat(dao.findOptionalIntById(1).getAsInt()).isEqualTo(42);
    }

    @Test
    public void testAbsent() {
        dao.insert(2, OptionalInt.empty());
        assertThat(dao.findOptionalIntById(2).isPresent()).isFalse();
    }

    interface TestDao {
        @SqlUpdate("INSERT INTO test(id, optional) VALUES (:id, :optional)")
        void insert(@Bind("id")
        int id, @Bind("optional")
        OptionalInt optional);

        @SqlQuery("SELECT optional FROM test WHERE id = :id")
        OptionalInt findOptionalIntById(@Bind("id")
        int id);
    }
}

