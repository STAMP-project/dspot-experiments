package io.dropwizard.jdbi.args;


import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public class OptionalDoubleTest {
    private final Environment env = new Environment("test-optional-double", Jackson.newObjectMapper(), Validators.newValidator(), new MetricRegistry(), null);

    private OptionalDoubleTest.TestDao dao;

    @Test
    public void testPresent() {
        dao.insert(1, OptionalDouble.of(123.456));
        assertThat(dao.findOptionalDoubleById(1).getAsDouble()).isEqualTo(123.456);
    }

    @Test
    public void testAbsent() {
        dao.insert(2, OptionalDouble.empty());
        assertThat(dao.findOptionalDoubleById(2).isPresent()).isFalse();
    }

    interface TestDao {
        @SqlUpdate("INSERT INTO test(id, optional) VALUES (:id, :optional)")
        void insert(@Bind("id")
        int id, @Bind("optional")
        OptionalDouble optional);

        @SqlQuery("SELECT optional FROM test WHERE id = :id")
        OptionalDouble findOptionalDoubleById(@Bind("id")
        int id);
    }
}

