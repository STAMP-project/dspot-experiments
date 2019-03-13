package tech.tablesaw.columns.times;


import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.TimeColumn;


public class TimeFillersTest {
    @Test
    public void testFromToBy() {
        // year, month, day, hour, minute
        assertContentEquals(TimeColumn.create("times", new LocalTime[5]).fillWith(// hour, minute
        range(LocalTime.of(12, 30), LocalTime.of(21, 30), 2, ChronoUnit.HOURS)), LocalTime.of(12, 30), LocalTime.of(14, 30), LocalTime.of(16, 30), LocalTime.of(18, 30), LocalTime.of(20, 30));
    }
}

