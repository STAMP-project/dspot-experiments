package io.dropwizard.jdbi.timestamps;


import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;


public class GuavaOptionalDateTimeTest {
    private final Environment env = new Environment("test-guava-date-time", Jackson.newObjectMapper(), Validators.newValidator(), new MetricRegistry(), null);

    private GuavaOptionalDateTimeTest.TaskDao dao;

    @Test
    public void testPresent() {
        final DateTime startDate = DateTime.now();
        final DateTime endDate = startDate.plusDays(1);
        dao.insert(1, Optional.of("John Hughes"), startDate, Optional.of(endDate), Optional.absent());
        assertThat(dao.findEndDateById(1).get()).isEqualTo(endDate);
    }

    @Test
    public void testAbsent() {
        dao.insert(2, Optional.of("Kate Johansen"), DateTime.now(), Optional.absent(), Optional.of("To be done"));
        assertThat(dao.findEndDateById(2).isPresent()).isFalse();
    }

    interface TaskDao {
        @SqlUpdate("INSERT INTO tasks(id, assignee, start_date, end_date, comments) " + "VALUES (:id, :assignee, :start_date, :end_date, :comments)")
        void insert(@Bind("id")
        int id, @Bind("assignee")
        Optional<String> assignee, @Bind("start_date")
        DateTime startDate, @Bind("end_date")
        Optional<DateTime> endDate, @Bind("comments")
        Optional<String> comments);

        @SqlQuery("SELECT end_date FROM tasks WHERE id = :id")
        @SingleValueResult
        Optional<DateTime> findEndDateById(@Bind("id")
        int id);
    }
}

