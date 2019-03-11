package tech.tablesaw.columns.dates;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateColumn;


public class DateFillersTest {
    @Test
    public void testFromToBy() {
        // year, month, day
        assertContentEquals(DateColumn.create("dates", new LocalDate[5]).fillWith(// year, month, day
        range(LocalDate.of(2018, 3, 1), LocalDate.of(2019, 3, 1), 1, ChronoUnit.DAYS)), LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 2), LocalDate.of(2018, 3, 3), LocalDate.of(2018, 3, 4), LocalDate.of(2018, 3, 5));
        // year, month, day
        assertContentEquals(DateColumn.create("dates", new LocalDate[5]).fillWith(// year, month, day
        range(LocalDate.of(2018, 3, 1), LocalDate.of(2019, 3, 1), 1, ChronoUnit.MONTHS)), LocalDate.of(2018, 3, 1), LocalDate.of(2018, 4, 1), LocalDate.of(2018, 5, 1), LocalDate.of(2018, 6, 1), LocalDate.of(2018, 7, 1));
    }
}

