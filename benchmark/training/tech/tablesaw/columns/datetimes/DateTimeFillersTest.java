package tech.tablesaw.columns.datetimes;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateTimeColumn;


public class DateTimeFillersTest {
    @Test
    public void testFromToBy() {
        // year,
        // month,
        // day,
        // hour,
        // minute
        assertContentEquals(DateTimeColumn.create("datetimes", new LocalDateTime[5]).fillWith(// year, month, day, hour,
        // minute
        range(LocalDateTime.of(2018, 3, 1, 12, 30), LocalDateTime.of(2019, 3, 1, 12, 30), 1, ChronoUnit.DAYS)), LocalDateTime.of(2018, 3, 1, 12, 30), LocalDateTime.of(2018, 3, 2, 12, 30), LocalDateTime.of(2018, 3, 3, 12, 30), LocalDateTime.of(2018, 3, 4, 12, 30), LocalDateTime.of(2018, 3, 5, 12, 30));
        // year,
        // month,
        // day,
        // hour,
        // minute
        assertContentEquals(DateTimeColumn.create("datetimes", new LocalDateTime[5]).fillWith(// year, month, day, hour,
        // minute
        range(LocalDateTime.of(2018, 3, 1, 12, 30), LocalDateTime.of(2019, 3, 1, 12, 30), 1, ChronoUnit.MONTHS)), LocalDateTime.of(2018, 3, 1, 12, 30), LocalDateTime.of(2018, 4, 1, 12, 30), LocalDateTime.of(2018, 5, 1, 12, 30), LocalDateTime.of(2018, 6, 1, 12, 30), LocalDateTime.of(2018, 7, 1, 12, 30));
        // year,
        // month,
        // day,
        // hour,
        // minute
        assertContentEquals(DateTimeColumn.create("datetimes", new LocalDateTime[5]).fillWith(// year, month, day, hour,
        // minute
        range(LocalDateTime.of(2018, 3, 1, 12, 30), LocalDateTime.of(2019, 3, 1, 12, 30), 2, ChronoUnit.HOURS)), LocalDateTime.of(2018, 3, 1, 12, 30), LocalDateTime.of(2018, 3, 1, 14, 30), LocalDateTime.of(2018, 3, 1, 16, 30), LocalDateTime.of(2018, 3, 1, 18, 30), LocalDateTime.of(2018, 3, 1, 20, 30));
    }
}

