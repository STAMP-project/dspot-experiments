package io.dropwizard.jdbi3;


import com.codahale.metrics.MetricRegistry;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.setup.Environment;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;


public class JdbiTest {
    static {
        BootstrapLogging.bootstrap();
    }

    private Environment environment;

    private Jdbi dbi;

    private GameDao dao;

    private MetricRegistry metricRegistry = new MetricRegistry();

    @Test
    public void fluentQueryWorks() {
        dbi.useHandle(( h) -> assertThat(h.createQuery(("SELECT id FROM games " + ("WHERE home_scored>visitor_scored " + "AND played_at > :played_at"))).bind("played_at", LocalDate.of(2016, 2, 15)).mapTo(.class).collect(Collectors.toList())).containsOnly(2, 5));
    }

    @Test
    public void canAcceptOptionalParams() {
        assertThat(dao.findHomeTeamByGameId(Optional.of(4))).contains("Dallas Stars");
        assertThat(metricRegistry.timer("game-dao.findHomeTeamByGameId").getCount()).isEqualTo(1);
    }

    @Test
    public void canAcceptEmptyOptionalParams() {
        assertThat(dao.findHomeTeamByGameId(Optional.empty())).isEmpty();
        assertThat(metricRegistry.timer("game-dao.findHomeTeamByGameId").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnImmutableLists() {
        assertThat(dao.findGameIds()).containsExactly(1, 2, 3, 4, 5);
        assertThat(metricRegistry.timer("game-dao.findGameIds").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnImmutableSets() {
        assertThat(dao.findAllUniqueHomeTeams()).containsOnly("NY Rangers", "Toronto Maple Leafs", "Dallas Stars");
        assertThat(metricRegistry.timer("game-dao.findAllUniqueHomeTeams").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnOptional() {
        assertThat(dao.findIdByTeamsAndDate("NY Rangers", "Vancouver Canucks", LocalDate.of(2016, 5, 14))).contains(2);
        assertThat(metricRegistry.timer("game-dao.findIdByTeamsAndDate").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnEmptyOptional() {
        assertThat(dao.findIdByTeamsAndDate("Vancouver Canucks", "NY Rangers", LocalDate.of(2016, 5, 14))).isEmpty();
        assertThat(metricRegistry.timer("game-dao.findIdByTeamsAndDate").getCount()).isEqualTo(1);
    }

    @Test
    public void worksWithDates() {
        assertThat(dao.getFirstPlayedSince(LocalDate.of(2016, 3, 1))).isEqualTo(LocalDate.of(2016, 2, 15));
        assertThat(metricRegistry.timer("game-dao.getFirstPlayedSince").getCount()).isEqualTo(1);
    }

    @Test
    public void worksWithOptionalDates() {
        Optional<LocalDate> date = dao.getLastPlayedDateByTeams("Toronto Maple Leafs", "Anaheim Ducks");
        assertThat(date).contains(LocalDate.of(2016, 2, 11));
        assertThat(metricRegistry.timer("game-dao.last-played-date").getCount()).isEqualTo(1);
    }

    @Test
    public void worksWithAbsentOptionalDates() {
        assertThat(dao.getLastPlayedDateByTeams("Vancouver Canucks", "NY Rangers")).isEmpty();
        assertThat(metricRegistry.timer("game-dao.last-played-date").getCount()).isEqualTo(1);
    }

    @Test
    public void testJodaTimeWorksForDateTimes() {
        dbi.useHandle(( h) -> assertThat(h.createQuery(("SELECT played_at FROM games " + ("WHERE home_scored > visitor_scored " + "AND played_at > :played_at"))).bind("played_at", org.joda.time.LocalDate.parse("2016-02-15").toDateTimeAtStartOfDay()).mapTo(.class).stream().map(DateTime::toLocalDate).collect(Collectors.toList())).containsOnly(org.joda.time.LocalDate.parse("2016-05-14"), org.joda.time.LocalDate.parse("2016-03-10")));
    }
}

