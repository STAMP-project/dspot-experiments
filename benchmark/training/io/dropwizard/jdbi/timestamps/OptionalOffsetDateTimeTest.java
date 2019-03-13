package io.dropwizard.jdbi.timestamps;


import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;


public class OptionalOffsetDateTimeTest {
    private final Environment env = new Environment("test-optional-offset-date-time", Jackson.newObjectMapper(), Validators.newValidator(), new MetricRegistry(), null);

    private OptionalOffsetDateTimeTest.TaskDao dao;

    @Test
    public void testPresent() {
        final OffsetDateTime startDate = OffsetDateTime.now();
        final OffsetDateTime endDate = startDate.plusDays(1L);
        dao.insert(1, Optional.of("John Hughes"), startDate, Optional.of(endDate), Optional.empty());
        assertThat(dao.findEndDateById(1).get()).isEqualTo(endDate);
    }

    @Test
    public void testAbsent() {
        dao.insert(2, Optional.of("Kate Johansen"), OffsetDateTime.now(), Optional.empty(), Optional.of("To be done"));
        assertThat(dao.findEndDateById(2).isPresent()).isFalse();
    }

    interface TaskDao {
        @SqlUpdate("INSERT INTO tasks(id, assignee, start_date, end_date, comments) " + "VALUES (:id, :assignee, :start_date, :end_date, :comments)")
        void insert(@Bind("id")
        int id, @Bind("assignee")
        Optional<String> assignee, @Bind("start_date")
        OffsetDateTime startDate, @Bind("end_date")
        Optional<OffsetDateTime> endDate, @Bind("comments")
        Optional<String> comments);

        @SqlQuery("SELECT end_date FROM tasks WHERE id = :id")
        @SingleValueResult
        Optional<OffsetDateTime> findEndDateById(@Bind("id")
        int id);
    }
}

