package io.dropwizard.jdbi.timestamps;


import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;


public class GuavaOptionalLocalDateTest {
    private final Environment env = new Environment("test-guava-local-date", Jackson.newObjectMapper(), Validators.newValidator(), new MetricRegistry(), null);

    private GuavaOptionalLocalDateTest.TaskDao dao;

    @Test
    public void testPresent() {
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.plusDays(1L);
        dao.insert(1, Optional.of("John Hughes"), startDate, Optional.of(endDate), Optional.absent());
        assertThat(dao.findEndDateById(1).get()).isEqualTo(endDate);
    }

    @Test
    public void testAbsent() {
        dao.insert(2, Optional.of("Kate Johansen"), LocalDate.now(), Optional.absent(), Optional.of("To be done"));
        assertThat(dao.findEndDateById(2).isPresent()).isFalse();
    }

    interface TaskDao {
        @SqlUpdate("INSERT INTO tasks(id, assignee, start_date, end_date, comments) " + "VALUES (:id, :assignee, :start_date, :end_date, :comments)")
        void insert(@Bind("id")
        int id, @Bind("assignee")
        Optional<String> assignee, @Bind("start_date")
        LocalDate startDate, @Bind("end_date")
        Optional<LocalDate> endDate, @Bind("comments")
        Optional<String> comments);

        @SqlQuery("SELECT end_date FROM tasks WHERE id = :id")
        @SingleValueResult
        Optional<LocalDate> findEndDateById(@Bind("id")
        int id);
    }
}

