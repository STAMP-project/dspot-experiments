package tech.tablesaw.api;


import DateColumnType.DEFAULT_PARSER;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.dates.DateColumnType;


public class DateColumnTest {
    private DateColumn column1;

    @Test
    public void testCreate1() {
        LocalDate[] dates = new LocalDate[5];
        DateColumn column = DateColumn.create("Game date", dates);
        Assertions.assertEquals(DateColumnType.missingValueIndicator(), column.getIntInternal(0));
    }

    @Test
    public void testAddCell() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/23/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("12-Jan-2015");
        Assertions.assertEquals(4, column1.size());
        LocalDate date = LocalDate.now();
        column1.append(date);
        Assertions.assertEquals(5, column1.size());
    }

    @Test
    public void testPrint() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/23/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("12-Jan-2015");
        column1.setPrintFormatter(DateTimeFormatter.ofPattern("MMM~dd~yyyy"), "");
        Assertions.assertEquals(((((((((("Column: Game date" + (System.lineSeparator())) + "Oct~23~2013") + (System.lineSeparator())) + "Dec~23~1924") + (System.lineSeparator())) + "May~12~2015") + (System.lineSeparator())) + "Jan~12~2015") + (System.lineSeparator())), column1.print());
    }

    @Test
    public void testPrint2() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/23/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("12-Jan-2015");
        column1.setPrintFormatter(DateTimeFormatter.ofPattern("MMM~dd~yyyy"));
        Assertions.assertEquals(((((((((("Column: Game date" + (System.lineSeparator())) + "Oct~23~2013") + (System.lineSeparator())) + "Dec~23~1924") + (System.lineSeparator())) + "May~12~2015") + (System.lineSeparator())) + "Jan~12~2015") + (System.lineSeparator())), column1.print());
    }

    @Test
    public void testDayOfMonth() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        IntColumn c2 = column1.dayOfMonth();
        Assertions.assertEquals(23, c2.get(0), 1.0E-4);
        Assertions.assertEquals(24, c2.get(1), 1.0E-4);
        Assertions.assertEquals(12, c2.get(2), 1.0E-4);
        Assertions.assertEquals(14, c2.get(3), 1.0E-4);
    }

    @Test
    public void testMonth() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        IntColumn c2 = column1.monthValue();
        Assertions.assertEquals(10, c2.get(0), 1.0E-4);
        Assertions.assertEquals(12, c2.get(1), 1.0E-4);
        Assertions.assertEquals(5, c2.get(2), 1.0E-4);
        Assertions.assertEquals(1, c2.get(3), 1.0E-4);
    }

    @Test
    public void testYearMonthString() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        StringColumn c2 = column1.yearMonth();
        Assertions.assertEquals("2013-10", c2.get(0));
        Assertions.assertEquals("1924-12", c2.get(1));
        Assertions.assertEquals("2015-05", c2.get(2));
        Assertions.assertEquals("2015-01", c2.get(3));
    }

    @Test
    public void testYear() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        IntColumn c2 = column1.year();
        Assertions.assertEquals(2013, c2.get(0), 1.0E-4);
        Assertions.assertEquals(1924, c2.get(1), 1.0E-4);
        Assertions.assertEquals(2015, c2.get(2), 1.0E-4);
    }

    @Test
    public void testSummary() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        Table summary = column1.summary();
        Assertions.assertEquals(4, summary.rowCount());
        Assertions.assertEquals(2, summary.columnCount());
        Assertions.assertEquals("Measure", summary.column(0).name());
        Assertions.assertEquals("Value", summary.column(1).name());
    }

    @Test
    public void testMin() {
        column1.appendInternal(DateColumnType.missingValueIndicator());
        column1.appendCell("2013-10-23");
        LocalDate actual = column1.min();
        Assertions.assertEquals(DEFAULT_PARSER.parse("2013-10-23"), actual);
    }

    @Test
    public void testSortOn() {
        Table unsorted = Table.read().csv((((((((("Date,1 Yr Treasury Rate" + (System.lineSeparator())) + "\"01-01-1871\",4.44%") + (System.lineSeparator())) + "\"01-01-1920\",8.83%") + (System.lineSeparator())) + "\"01-01-1921\",7.11%") + (System.lineSeparator())) + "\"01-01-1919\",7.85%"), "1 Yr Treasury Rate");
        Table sorted = unsorted.sortOn("Date");
        Assertions.assertEquals(sorted.dateColumn("Date").asList().stream().sorted().collect(Collectors.toList()), sorted.dateColumn("Date").asList());
    }
}

