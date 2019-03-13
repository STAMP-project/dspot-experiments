/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.io.csv;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.AddCellToColumnException;


/**
 * Tests for CSV Reading
 */
public class CsvReaderTest {
    private static final String LINE_END = System.lineSeparator();

    private final ColumnType[] bus_types = new ColumnType[]{ ColumnType.SHORT, ColumnType.STRING, ColumnType.STRING, ColumnType.FLOAT, ColumnType.FLOAT };

    private final ColumnType[] bus_types_with_SKIP = new ColumnType[]{ ColumnType.SHORT, ColumnType.STRING, ColumnType.SKIP, ColumnType.DOUBLE, ColumnType.DOUBLE };

    @Test
    public void testWithBusData() throws IOException {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bus_stop_test.csv").columnTypes(bus_types));
        // Look at the column names
        Assertions.assertEquals("[stop_id, stop_name, stop_desc, stop_lat, stop_lon]", table.columnNames().toString());
        table = table.sortDescendingOn("stop_id");
        table.removeColumns("stop_desc");
    }

    @Test
    public void testWithColumnSKIP() throws IOException {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bus_stop_test.csv").columnTypes(bus_types_with_SKIP));
        Assertions.assertEquals(4, table.columnCount());
        // Look at the column names
        Assertions.assertEquals("[stop_id, stop_name, stop_lat, stop_lon]", table.columnNames().toString());
    }

    @Test
    public void testWithColumnSKIPWithoutHeader() throws IOException {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bus_stop_noheader_test.csv").header(false).columnTypes(bus_types_with_SKIP));
        Assertions.assertEquals(4, table.columnCount());
        // Look at the column names
        Assertions.assertEquals("[C0, C1, C3, C4]", table.columnNames().toString());
    }

    @Test
    public void testWithBushData() throws IOException {
        // Read the CSV file
        ColumnType[] types = new ColumnType[]{ ColumnType.LOCAL_DATE, ColumnType.DOUBLE, ColumnType.STRING };
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").columnTypes(types));
        Assertions.assertEquals(323, table.rowCount());
        // Look at the column names
        Assertions.assertEquals("[date, approval, who]", table.columnNames().toString());
    }

    @Test
    public void testBushDataWithoutSamplingForTypeDetection() throws IOException {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").sample(false));
        Assertions.assertEquals(323, table.rowCount());
        // Look at the column names
        Assertions.assertEquals("[date, approval, who]", table.columnNames().toString());
    }

    @Test
    public void testDataTypeDetection() throws IOException {
        Reader reader = new FileReader("../data/bus_stop_test.csv");
        CsvReadOptions options = CsvReadOptions.builder(reader).header(true).minimizeColumnSizes(true).separator(',').sample(false).locale(Locale.getDefault()).build();
        ColumnType[] columnTypes = new CsvReader().detectColumnTypes(reader, options);
        Assertions.assertArrayEquals(bus_types, columnTypes);
    }

    @Test
    public void testMillis() {
        long[] times = new long[]{ 1530486314124L, 1530488214124L };
        LongColumn d = LongColumn.create("times", times);
        DateTimeColumn column = d.asDateTimes(ZoneOffset.UTC);
        Assertions.assertEquals(1530486314124L, column.get(0).toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Test
    public void testLocalDateDetectionEnglish() {
        final Reader reader = new StringReader(((((((((((((("Date" + (CsvReaderTest.LINE_END)) + "\"Nov 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"Oct 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"Sep 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"Aug 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"Jul 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"Jun 1, 2017\"") + (CsvReaderTest.LINE_END)));
        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;
        CsvReadOptions options = CsvReadOptions.builder(reader).header(header).separator(delimiter).sample(useSampling).locale(Locale.ENGLISH).build();
        final List<ColumnType> actual = Arrays.asList(new CsvReader().detectColumnTypes(reader, options));
        Assertions.assertEquals(actual, Collections.singletonList(ColumnType.LOCAL_DATE));
    }

    @Test
    public void testLocalDateTimeDetectionEnglish() {
        final Reader reader = new StringReader(((((((((((((("Date" + (CsvReaderTest.LINE_END)) + "09-Nov-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-Oct-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-Sep-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-Aug-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-Jul-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-Jun-2014 13:03") + (CsvReaderTest.LINE_END)));
        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;
        CsvReadOptions options = CsvReadOptions.builder(reader).header(header).separator(delimiter).sample(useSampling).locale(Locale.ENGLISH).build();
        final List<ColumnType> actual = Arrays.asList(new CsvReader().detectColumnTypes(reader, options));
        Assertions.assertEquals(actual, Collections.singletonList(ColumnType.LOCAL_DATE_TIME));
    }

    @Test
    public void testLocalDateDetectionFrench() {
        final Reader reader = new StringReader(((((((((((((("Date" + (CsvReaderTest.LINE_END)) + "\"nov. 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"oct. 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"sept. 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"ao\u00fbt 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"juil. 1, 2017\"") + (CsvReaderTest.LINE_END)) + "\"juin 1, 2017\"") + (CsvReaderTest.LINE_END)));
        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;
        CsvReadOptions options = CsvReadOptions.builder(reader).header(header).separator(delimiter).sample(useSampling).locale(Locale.FRENCH).build();
        final List<ColumnType> actual = Arrays.asList(new CsvReader().detectColumnTypes(reader, options));
        Assertions.assertEquals(actual, Collections.singletonList(ColumnType.LOCAL_DATE));
    }

    @Test
    public void testLocalDateTimeDetectionFrench() {
        final Reader reader = new StringReader(((((((((((((("Date" + (CsvReaderTest.LINE_END)) + "09-nov.-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-oct.-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-sept.-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-ao?t-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-juil.-2014 13:03") + (CsvReaderTest.LINE_END)) + "09-juin-2014 13:03") + (CsvReaderTest.LINE_END)));
        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;
        CsvReadOptions options = CsvReadOptions.builder(reader).header(header).separator(delimiter).sample(useSampling).locale(Locale.FRENCH).build();
        final List<ColumnType> actual = Arrays.asList(new CsvReader().detectColumnTypes(reader, options));
        Assertions.assertEquals(actual, Collections.singletonList(ColumnType.LOCAL_DATE_TIME));
    }

    @Test
    public void testWithMissingValue() throws IOException {
        CsvReadOptions options = CsvReadOptions.builder("../data/missing_values.csv").dateFormat("yyyy.MM.dd").header(true).missingValueIndicator("-").build();
        Table t = Table.read().csv(options);
        Assertions.assertEquals(1, t.stringColumn(0).countMissing());
        Assertions.assertEquals(1, t.numberColumn(1).countMissing());
        Assertions.assertEquals(1, t.numberColumn(2).countMissing());
    }

    @Test
    public void testWindowsAndLinuxLineEndings() throws IOException {
        Reader reader = new StringReader(("TestCol\n" + ((((("foobar1\n" + "foobar2\n") + "foobar3\n") + "foobar4\r\n") + "foobar5\r\n") + "foobar6\r\n")));
        Table t = Table.read().csv(reader);
        Assertions.assertEquals(1, t.columnCount());
        Assertions.assertEquals(6, t.rowCount());
    }

    @Test
    public void testCustomLineEndings() throws IOException {
        CsvReadOptions options = CsvReadOptions.builder("../data/alt_line_endings.csv").lineEnding("~").header(true).build();
        Table t = Table.read().csv(options);
        Assertions.assertEquals(2, t.columnCount());
        Assertions.assertEquals(2, t.rowCount());
    }

    @Test
    public void testDateWithFormatter2() throws IOException {
        final boolean header = false;
        final char delimiter = ',';
        final boolean useSampling = true;
        CsvReadOptions options = CsvReadOptions.builder("../data/date_format_test.txt").header(header).separator(delimiter).sample(useSampling).dateFormat("yyyy.MM.dd").build();
        final Table table = Table.read().csv(options);
        DateColumn date = table.dateColumn(0);
        Assertions.assertFalse(date.isEmpty());
    }

    @Test
    public void testPrintStructure() throws IOException {
        String output = (((((((("ColumnType[] columnTypes = {" + (CsvReaderTest.LINE_END)) + "LOCAL_DATE, // 0     date        ") + (CsvReaderTest.LINE_END)) + "INTEGER,    // 1     approval    ") + (CsvReaderTest.LINE_END)) + "STRING,     // 2     who         ") + (CsvReaderTest.LINE_END)) + "}") + (CsvReaderTest.LINE_END);
        Assertions.assertEquals(output, new CsvReader().printColumnTypes(CsvReadOptions.builder("../data/bush.csv").header(true).separator(',').locale(Locale.getDefault()).sample(true).build()));
    }

    @Test
    public void testDataTypeDetection2() throws IOException {
        Reader reader = new FileReader("../data/bush.csv");
        CsvReadOptions options = CsvReadOptions.builder(reader).header(true).separator(',').sample(false).locale(Locale.getDefault()).build();
        ColumnType[] columnTypes = new CsvReader().detectColumnTypes(reader, options);
        Assertions.assertEquals(ColumnType.LOCAL_DATE, columnTypes[0]);
        Assertions.assertEquals(ColumnType.INTEGER, columnTypes[1]);
        Assertions.assertEquals(ColumnType.STRING, columnTypes[2]);
    }

    @Test
    public void testLoadFromUrlWithColumnTypes() throws IOException {
        ColumnType[] types = new ColumnType[]{ ColumnType.LOCAL_DATE, ColumnType.DOUBLE, ColumnType.STRING };
        Table table;
        try (InputStream input = new File("../data/bush.csv").toURI().toURL().openStream()) {
            table = Table.read().csv(CsvReadOptions.builder(input).tableName("Bush approval ratings").columnTypes(types));
        }
        Assertions.assertNotNull(table);
        Assertions.assertEquals(3, table.columnCount());
    }

    /**
     * Read from a url while performing column type inference
     */
    @Test
    public void testLoadFromUrl() throws IOException {
        Table table;
        try (InputStream input = new File("../data/bush.csv").toURI().toURL().openStream()) {
            table = Table.read().csv(CsvReadOptions.builder(input).tableName("Bush approval ratings"));
        }
        Assertions.assertNotNull(table);
        Assertions.assertEquals(3, table.columnCount());
    }

    /**
     * Read from a file input stream while performing column type inference
     */
    @Test
    public void testLoadFromFileStream() throws IOException {
        String location = "../data/bush.csv";
        Table table;
        File file = Paths.get(location).toFile();
        try (InputStream input = new FileInputStream(file)) {
            table = Table.read().csv(CsvReadOptions.builder(input).tableName("Bush approval ratings"));
        }
        Assertions.assertNotNull(table);
        Assertions.assertEquals(3, table.columnCount());
    }

    /**
     * Read from a file input stream while performing column type inference
     */
    @Test
    public void testLoadFromFileStreamReader() throws IOException {
        String location = "../data/bush.csv";
        Table table;
        File file = Paths.get(location).toFile();
        try (Reader reader = new FileReader(file)) {
            table = Table.read().csv(CsvReadOptions.builder(reader).tableName("Bush approval ratings"));
        }
        Assertions.assertNotNull(table);
        Assertions.assertEquals(3, table.columnCount());
    }

    @Test
    public void testEmptyRow() throws IOException {
        Table table = Table.read().csv("../data/empty_row.csv");
        // Note: tried capturing std err output and asserting on it, but it failed when running as mvn target
        Assertions.assertEquals(5, table.rowCount());
    }

    @Test
    public void testShortRow() {
        Assertions.assertThrows(AddCellToColumnException.class, () -> {
            Table.read().csv("../data/short_row.csv");
        });
    }

    @Test
    public void testLongRow() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Table.read().csv("../data/long_row.csv");
        });
    }

    @Test
    public void testBoundary1() throws IOException {
        Table table = Table.read().csv("../data/boundaryTest1.csv");
        Assertions.assertEquals(2, table.rowCount());
    }

    @Test
    public void testBoundary2() throws IOException {
        Table table = Table.read().csv("../data/boundaryTest2.csv");
        Assertions.assertEquals(2, table.rowCount());
    }

    @Test
    public void testReadFailure() throws IOException {
        // TODO (lwhite): These tests don't fail. What was their intent?
        Table table1 = Table.read().csv(CsvReadOptions.builder("../data/read_failure_test.csv").minimizeColumnSizes(true));
        table1.structure();// just make sure the import completed

        ShortColumn test = table1.shortColumn("Test");
        // TODO(lwhite): Better tests
        Assertions.assertNotNull(test.summary());
    }

    @Test
    public void testReadFailure2() throws IOException {
        Table table1 = Table.read().csv(CsvReadOptions.builder("../data/read_failure_test2.csv").minimizeColumnSizes(true));
        table1.structure();// just make sure the import completed

        ShortColumn test = table1.shortColumn("Test");
        // TODO(lwhite): Better tests
        Assertions.assertNotNull(test.summary());
    }

    @Test
    public void testEmptyFileHeaderEnabled() throws IOException {
        Table table1 = Table.read().csv(CsvReadOptions.builder("../data/empty_file.csv").header(false));
        Assertions.assertEquals("0 rows X 0 cols", table1.shape());
    }

    @Test
    public void testEmptyFileHeaderDisabled() throws IOException {
        Table table1 = Table.read().csv(CsvReadOptions.builder("../data/empty_file.csv").header(false));
        Assertions.assertEquals("0 rows X 0 cols", table1.shape());
    }

    @Test
    public void testReadWithMaxColumnsSetting() throws IOException {
        Table table1 = Table.read().csv(CsvReadOptions.builder("../data/10001_columns.csv").maxNumberOfColumns(10001).header(false));
        Assertions.assertEquals("1 rows X 10001 cols", table1.shape());
    }

    @Test
    public void testSkipLinesWithComments() throws IOException {
        Table table1 = Table.read().csv(CsvReadOptions.builder("../data/with_comments.csv").maxNumberOfColumns(3).commentPrefix('#').header(true));
        Assertions.assertEquals("3 rows X 3 cols", table1.shape());
    }

    @Test
    public void carriageReturnLineEnding() throws IOException {
        Table table = Table.read().csv(CsvReadOptions.builder("../data/sacramento_real_estate_transactions.csv"));
        Assertions.assertEquals(985, table.rowCount());
    }
}

