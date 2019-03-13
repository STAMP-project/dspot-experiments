/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import DecimalHandlingMode.DOUBLE;
import DecimalHandlingMode.STRING;
import MySqlConnectorConfig.DECIMAL_HANDLING_MODE;
import MySqlConnectorConfig.INCLUDE_SCHEMA_CHANGES;
import MySqlConnectorConfig.SNAPSHOT_MODE;
import MySqlConnectorConfig.SnapshotMode.NEVER;
import MySqlConnectorConfig.TABLE_WHITELIST;
import MySqlConnectorConfig.TIME_PRECISION_MODE;
import TemporalPrecisionMode.ADAPTIVE;
import TemporalPrecisionMode.CONNECT;
import Testing.Files;
import io.debezium.config.Configuration;
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import org.apache.kafka.connect.data.Struct;
import org.fest.assertions.Delta;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MySqlConnectorRegressionIT extends AbstractConnectorTest {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-regression.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("regression", "regression_test").withDbHistoryPath(MySqlConnectorRegressionIT.DB_HISTORY_PATH);

    private static final TemporalAdjuster ADJUSTER = MySqlValueConverters::adjustTemporal;

    private Configuration config;

    @Test
    @FixFor("DBZ-61")
    public void shouldConsumeAllEventsFromDatabaseUsingBinlogAndNoSnapshot() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(INCLUDE_SCHEMA_CHANGES, true).with(SNAPSHOT_MODE, NEVER).with("database.serverTimezone", DATABASE.timezone()).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 11;
        int numDataRecords = 20;
        int numCreateDefiner = 1;
        SourceRecords records = consumeRecordsByTopic((((numCreateDatabase + numCreateTables) + numDataRecords) + numCreateDefiner));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo(((numCreateDatabase + numCreateTables) + numCreateDefiner));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("t1464075356413_testtable6")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz84_integer_types_table")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_85_fractest")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_100_enumsettest")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_102_charsettest")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_114_zerovaluetest")).size()).isEqualTo(2);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_123_bitvaluetest")).size()).isEqualTo(2);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_104_customers")).size()).isEqualTo(4);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_147_decimalvalues")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_342_timetest")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo((numCreateTables + 1));
        assertThat(records.databaseNames().size()).isEqualTo(1);
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo(((numCreateDatabase + numCreateTables) + numCreateDefiner));
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_100_enumsettest")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                String c1 = after.getString("c1");
                String c2 = after.getString("c2");
                if (c1.equals("a")) {
                    assertThat(c2).isEqualTo("a,b,c");
                } else
                    if (c1.equals("b")) {
                        assertThat(c2).isEqualTo("a,b");
                    } else
                        if (c1.equals("c")) {
                            assertThat(c2).isEqualTo("a");
                        } else {
                            fail("c1 didn't match expected value");
                        }


            } else
                if (record.topic().endsWith("dbz_102_charsettest")) {
                    Struct after = value.getStruct(Envelope.FieldName.AFTER);
                    String text = after.getString("text");
                    assertThat(text).isEqualTo("??");
                } else
                    if (record.topic().endsWith("dbz_85_fractest")) {
                        // The microseconds of all three should be exactly 780
                        Struct after = value.getStruct(Envelope.FieldName.AFTER);
                        // c1 DATE,
                        // c2 TIME(2),
                        // c3 DATETIME(2),
                        // c4 TIMESTAMP(2)
                        // 
                        // {"c1" : "16321", "c2" : "17:51:04.777", "c3" : "1410198664780", "c4" : "2014-09-08T17:51:04.78-05:00"}
                        // '2014-09-08'
                        Integer c1 = after.getInt32("c1");// epoch days

                        LocalDate c1Date = LocalDate.ofEpochDay(c1);
                        assertThat(c1Date.getYear()).isEqualTo(2014);
                        assertThat(c1Date.getMonth()).isEqualTo(Month.SEPTEMBER);
                        assertThat(c1Date.getDayOfMonth()).isEqualTo(8);
                        assertThat(io.debezium.time.Date.toEpochDay(c1Date, ADJUSTER)).isEqualTo(c1);
                        // '17:51:04.777'
                        Long c2 = after.getInt64("c2");
                        Duration c2Time = Duration.ofNanos((c2 * 1000));
                        assertThat(c2Time.toHours()).isEqualTo(17);
                        assertThat(c2Time.toMinutes()).isEqualTo(1071);
                        assertThat(c2Time.getSeconds()).isEqualTo(64264);
                        assertThat(c2Time.getNano()).isEqualTo(780000000);
                        assertThat(c2Time.toNanos()).isEqualTo(64264780000000L);
                        assertThat(c2Time).isEqualTo(Duration.ofHours(17).plusMinutes(51).plusSeconds(4).plusMillis(780));
                        // '2014-09-08 17:51:04.777'
                        // DATETIME is a logical date and time, it doesn't contain any TZ information;
                        // it is mapped to a point on the time line by interpreting the value at UTC
                        Long c3 = after.getInt64("c3");// epoch millis

                        long c3Seconds = c3 / 1000;
                        long c3Millis = c3 % 1000;
                        LocalDateTime c3DateTime = LocalDateTime.ofEpochSecond(c3Seconds, ((int) (TimeUnit.MILLISECONDS.toNanos(c3Millis))), ZoneOffset.UTC);
                        assertThat(c3DateTime.getYear()).isEqualTo(2014);
                        assertThat(c3DateTime.getMonth()).isEqualTo(Month.SEPTEMBER);
                        assertThat(c3DateTime.getDayOfMonth()).isEqualTo(8);
                        assertThat(c3DateTime.getHour()).isEqualTo(17);
                        assertThat(c3DateTime.getMinute()).isEqualTo(51);
                        assertThat(c3DateTime.getSecond()).isEqualTo(4);
                        assertThat(c3DateTime.getNano()).isEqualTo(((int) (TimeUnit.MILLISECONDS.toNanos(780))));
                        assertThat(io.debezium.time.Timestamp.toEpochMillis(c3DateTime, ADJUSTER)).isEqualTo(c3);
                        // '2014-09-08 17:51:04.777'
                        String c4 = after.getString("c4");// timestamp

                        assertTimestamp(c4);
                    } else
                        if (record.topic().endsWith("dbz_114_zerovaluetest")) {
                            Struct after = value.getStruct(Envelope.FieldName.AFTER);
                            // c1 DATE,
                            // c2 TIME(2),
                            // c3 DATETIME(2),
                            // c4 TIMESTAMP(2)
                            // 
                            // INSERT IGNORE INTO dbz_114_zerovaluetest VALUES ('0000-00-00', '00:00:00.000', '0000-00-00 00:00:00.000',
                            // '0000-00-00 00:00:00.000');
                            // INSERT IGNORE INTO dbz_114_zerovaluetest VALUES ('0001-00-00', '00:01:00.000', '0001-00-00 00:00:00.000',
                            // '0001-00-00 00:00:00.000');
                            // 
                            // results in:
                            // 
                            // +------------+-------------+------------------------+------------------------+
                            // | c1 | c2 | c3 | c4 |
                            // +------------+-------------+------------------------+------------------------+
                            // | 0000-00-00 | 00:00:00.00 | 0000-00-00 00:00:00.00 | 0000-00-00 00:00:00.00 |
                            // | 0000-00-00 | 00:01:00.00 | 0000-00-00 00:00:00.00 | 0000-00-00 00:00:00.00 |
                            // +------------+-------------+------------------------+------------------------+
                            // 
                            // Note the '0001' years that were inserted are stored as '0000'
                            // 
                            assertThat(after.getInt32("c1")).isNull();// epoch days

                            // '00:00:00.000'
                            Long c2 = after.getInt64("c2");
                            Duration c2Time = Duration.ofNanos((c2 * 1000));
                            assertThat(c2Time.toHours()).isEqualTo(0);
                            assertThat((((c2Time.toMinutes()) == 1) || ((c2Time.toMinutes()) == 0))).isTrue();
                            assertThat((((c2Time.getSeconds()) == 0) || ((c2Time.getSeconds()) == 60))).isTrue();
                            assertThat(c2Time.getNano()).isEqualTo(0);
                            assertThat((((c2Time.toNanos()) == 0) || ((c2Time.toNanos()) == 60000000000L))).isTrue();
                            assertThat(((c2Time.equals(Duration.ofSeconds(0))) || (c2Time.equals(Duration.ofMinutes(1))))).isTrue();
                            assertThat(after.getInt64("c3")).isNull();// epoch millis

                            // '0000-00-00 00:00:00.00'
                            String c4 = after.getString("c4");// timestamp

                            OffsetDateTime c4DateTime = OffsetDateTime.parse(c4, ZonedTimestamp.FORMATTER);
                            // Timestamp is stored as UTC
                            assertThat(c4DateTime.getOffset()).isEqualTo(ZoneOffset.UTC);
                            // In case the timestamp string not in our timezone, convert to UTC so we can compare ...
                            c4DateTime = c4DateTime.withOffsetSameInstant(ZoneOffset.of("Z"));
                            assertThat(c4DateTime.getYear()).isEqualTo(1970);
                            assertThat(c4DateTime.getMonth()).isEqualTo(Month.JANUARY);
                            assertThat(c4DateTime.getDayOfMonth()).isEqualTo(1);
                            // Difference depends upon whether the zone we're in is also using DST as it is on the date in question ...
                            assertThat(c4DateTime.getHour()).isIn(0, 1);
                            assertThat(c4DateTime.getMinute()).isEqualTo(0);
                            assertThat(c4DateTime.getSecond()).isEqualTo(0);
                            assertThat(c4DateTime.getNano()).isEqualTo(0);
                        } else
                            if (record.topic().endsWith("dbz_123_bitvaluetest")) {
                                // All row events should have the same values ...
                                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                // c1 BIT, // 1 bit
                                // c2 BIT(2), // 2 bits
                                // c3 BIT(8) // 8 bits
                                // c4 BIT(64) // 64 bits
                                Boolean c1 = after.getBoolean("c1");
                                assertThat(c1).isEqualTo(Boolean.TRUE);
                                byte[] c2 = after.getBytes("c2");
                                assertThat(c2.length).isEqualTo(1);
                                assertThat(c2[0]).isEqualTo(((byte) (2)));
                                byte[] c3 = after.getBytes("c3");
                                assertThat(c3.length).isEqualTo(1);
                                assertThat(c3[0]).isEqualTo(((byte) (64)));
                                // 1011011100000111011011011 = 23989979
                                byte[] c4 = after.getBytes("c4");
                                assertThat(c4.length).isEqualTo(8);// bytes, little endian

                                assertThat(c4[0]).isEqualTo(((byte) (219)));// 11011011

                                assertThat(c4[1]).isEqualTo(((byte) (14)));// 00001110

                                assertThat(c4[2]).isEqualTo(((byte) (110)));// 01101110

                                assertThat(c4[3]).isEqualTo(((byte) (1)));// 1

                                assertThat(c4[4]).isEqualTo(((byte) (0)));
                                assertThat(c4[5]).isEqualTo(((byte) (0)));
                                assertThat(c4[6]).isEqualTo(((byte) (0)));
                                assertThat(c4[7]).isEqualTo(((byte) (0)));
                            } else
                                if (record.topic().endsWith("dbz_147_decimalvalues")) {
                                    Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                    Object decimalValue = after.get("decimal_value");
                                    assertThat(decimalValue).isInstanceOf(.class);
                                    BigDecimal bigValue = ((BigDecimal) (decimalValue));
                                    assertThat(bigValue.doubleValue()).isEqualTo(12345.67, Delta.delta(0.01));
                                } else
                                    if (record.topic().endsWith("dbz_342_timetest")) {
                                        Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                        // '517:51:04.777'
                                        long c1 = after.getInt64("c1");
                                        Duration c1Time = Duration.ofNanos((c1 * 1000));
                                        Duration c1ExpectedTime = toDuration("PT517H51M4.78S");
                                        assertEquals(c1ExpectedTime, c1Time);
                                        assertEquals(c1ExpectedTime.toNanos(), c1Time.toNanos());
                                        assertThat(c1Time.toNanos()).isEqualTo(1864264780000000L);
                                        assertThat(c1Time).isEqualTo(Duration.ofHours(517).plusMinutes(51).plusSeconds(4).plusMillis(780));
                                        // '-13:14:50'
                                        long c2 = after.getInt64("c2");
                                        Duration c2Time = Duration.ofNanos((c2 * 1000));
                                        Duration c2ExpectedTime = toDuration("-PT13H14M50S");
                                        assertEquals(c2ExpectedTime, c2Time);
                                        assertEquals(c2ExpectedTime.toNanos(), c2Time.toNanos());
                                        assertThat(c2Time.toNanos()).isEqualTo((-47690000000000L));
                                        assertTrue(c2Time.isNegative());
                                        assertThat(c2Time).isEqualTo(Duration.ofHours((-13)).minusMinutes(14).minusSeconds(50));
                                        // '-733:00:00.0011'
                                        long c3 = after.getInt64("c3");
                                        Duration c3Time = Duration.ofNanos((c3 * 1000));
                                        Duration c3ExpectedTime = toDuration("-PT733H0M0.001S");
                                        assertEquals(c3ExpectedTime, c3Time);
                                        assertEquals(c3ExpectedTime.toNanos(), c3Time.toNanos());
                                        assertThat(c3Time.toNanos()).isEqualTo((-2638800001000000L));
                                        assertTrue(c3Time.isNegative());
                                        assertThat(c3Time).isEqualTo(Duration.ofHours((-733)).minusMillis(1));
                                        // '-1:59:59.0011'
                                        long c4 = after.getInt64("c4");
                                        Duration c4Time = Duration.ofNanos((c4 * 1000));
                                        Duration c4ExpectedTime = toDuration("-PT1H59M59.001S");
                                        assertEquals(c4ExpectedTime, c4Time);
                                        assertEquals(c4ExpectedTime.toNanos(), c4Time.toNanos());
                                        assertThat(c4Time.toNanos()).isEqualTo((-7199001000000L));
                                        assertTrue(c4Time.isNegative());
                                        assertThat(c4Time).isEqualTo(Duration.ofHours((-1)).minusMinutes(59).minusSeconds(59).minusMillis(1));
                                        // '-838:59:58.999999'
                                        long c5 = after.getInt64("c5");
                                        Duration c5Time = Duration.ofNanos((c5 * 1000));
                                        Duration c5ExpectedTime = toDuration("-PT838H59M58.999999S");
                                        assertEquals(c5ExpectedTime, c5Time);
                                        assertEquals(c5ExpectedTime.toNanos(), c5Time.toNanos());
                                        assertThat(c5Time.toNanos()).isEqualTo((-3020398999999000L));
                                        assertTrue(c5Time.isNegative());
                                        assertThat(c5Time).isEqualTo(Duration.ofHours((-838)).minusMinutes(59).minusSeconds(58).minusNanos(999999000));
                                    }






        });
    }

    @Test
    @FixFor("DBZ-61")
    public void shouldConsumeAllEventsFromDatabaseUsingBinlogAndNoSnapshotAndConnectTimesTypes() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(INCLUDE_SCHEMA_CHANGES, true).with(INCLUDE_SCHEMA_CHANGES, true).with(SNAPSHOT_MODE, SnapshotMode.NEVER).with(TIME_PRECISION_MODE, CONNECT).with("database.serverTimezone", DATABASE.timezone()).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 11;
        int numDataRecords = 20;
        int numCreateDefiner = 1;
        SourceRecords records = consumeRecordsByTopic((((numCreateDatabase + numCreateTables) + numDataRecords) + numCreateDefiner));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo(((numCreateDatabase + numCreateTables) + numCreateDefiner));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("t1464075356413_testtable6")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz84_integer_types_table")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_85_fractest")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_100_enumsettest")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_102_charsettest")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_114_zerovaluetest")).size()).isEqualTo(2);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_123_bitvaluetest")).size()).isEqualTo(2);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_104_customers")).size()).isEqualTo(4);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_147_decimalvalues")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo((1 + numCreateTables));
        assertThat(records.databaseNames().size()).isEqualTo(1);
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo(((numCreateDatabase + numCreateTables) + numCreateDefiner));
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        // records.forEach(this::validate); // Can't run this with 0.10.0.1; see KAFKA-4183
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_100_enumsettest")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                String c1 = after.getString("c1");
                String c2 = after.getString("c2");
                if (c1.equals("a")) {
                    assertThat(c2).isEqualTo("a,b,c");
                } else
                    if (c1.equals("b")) {
                        assertThat(c2).isEqualTo("a,b");
                    } else
                        if (c1.equals("c")) {
                            assertThat(c2).isEqualTo("a");
                        } else {
                            fail("c1 didn't match expected value");
                        }


            } else
                if (record.topic().endsWith("dbz_102_charsettest")) {
                    Struct after = value.getStruct(Envelope.FieldName.AFTER);
                    String text = after.getString("text");
                    assertThat(text).isEqualTo("??");
                } else
                    if (record.topic().endsWith("dbz_85_fractest")) {
                        // The microseconds of all three should be exactly 780
                        Struct after = value.getStruct(Envelope.FieldName.AFTER);
                        // c1 DATE,
                        // c2 TIME(2),
                        // c3 DATETIME(2),
                        // c4 TIMESTAMP(2)
                        // 
                        // {"c1" : "16321", "c2" : "64264780", "c3" : "1410198664780", "c4" : "2014-09-08T17:51:04.78-05:00"}
                        // '2014-09-08'
                        Date c1 = ((Date) (after.get("c1")));// epoch days

                        LocalDate c1Date = LocalDate.ofEpochDay(((c1.getTime()) / (TimeUnit.DAYS.toMillis(1))));
                        assertThat(c1Date.getYear()).isEqualTo(2014);
                        assertThat(c1Date.getMonth()).isEqualTo(Month.SEPTEMBER);
                        assertThat(c1Date.getDayOfMonth()).isEqualTo(8);
                        // '17:51:04.777'
                        Date c2 = ((Date) (after.get("c2")));// milliseconds past midnight

                        LocalTime c2Time = LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.toNanos(c2.getTime()));
                        assertThat(c2Time.getHour()).isEqualTo(17);
                        assertThat(c2Time.getMinute()).isEqualTo(51);
                        assertThat(c2Time.getSecond()).isEqualTo(4);
                        assertThat(c2Time.getNano()).isEqualTo(((int) (TimeUnit.MILLISECONDS.toNanos(780))));
                        assertThat(io.debezium.time.Time.toMilliOfDay(c2Time, ADJUSTER)).isEqualTo(((int) (c2.getTime())));
                        // '2014-09-08 17:51:04.777'
                        // DATETIME is a logical date and time, it doesn't contain any TZ information;
                        // it is mapped to a point on the time line by interpreting the value at UTC
                        Date c3 = ((Date) (after.get("c3")));// epoch millis

                        long c3Seconds = (c3.getTime()) / 1000;
                        long c3Millis = (c3.getTime()) % 1000;
                        LocalDateTime c3DateTime = LocalDateTime.ofEpochSecond(c3Seconds, ((int) (TimeUnit.MILLISECONDS.toNanos(c3Millis))), ZoneOffset.UTC);
                        assertThat(c3DateTime.getYear()).isEqualTo(2014);
                        assertThat(c3DateTime.getMonth()).isEqualTo(Month.SEPTEMBER);
                        assertThat(c3DateTime.getDayOfMonth()).isEqualTo(8);
                        assertThat(c3DateTime.getHour()).isEqualTo(17);
                        assertThat(c3DateTime.getMinute()).isEqualTo(51);
                        assertThat(c3DateTime.getSecond()).isEqualTo(4);
                        assertThat(c3DateTime.getNano()).isEqualTo(((int) (TimeUnit.MILLISECONDS.toNanos(780))));
                        assertThat(io.debezium.time.Timestamp.toEpochMillis(c3DateTime, ADJUSTER)).isEqualTo(c3.getTime());
                        // '2014-09-08 17:51:04.777'
                        String c4 = after.getString("c4");// MySQL timestamp, so always ZonedTimestamp

                        assertTimestamp(c4);
                    } else
                        if (record.topic().endsWith("dbz_114_zerovaluetest")) {
                            Struct after = value.getStruct(Envelope.FieldName.AFTER);
                            // c1 DATE,
                            // c2 TIME(2),
                            // c3 DATETIME(2),
                            // c4 TIMESTAMP(2)
                            // 
                            // INSERT IGNORE INTO dbz_114_zerovaluetest VALUES ('0000-00-00', '00:00:00.000', '0000-00-00 00:00:00.000',
                            // '0000-00-00 00:00:00.000');
                            // INSERT IGNORE INTO dbz_114_zerovaluetest VALUES ('0001-00-00', '00:01:00.000', '0001-00-00 00:00:00.000',
                            // '0001-00-00 00:00:00.000');
                            // 
                            // results in:
                            // 
                            // +------------+-------------+------------------------+------------------------+
                            // | c1 | c2 | c3 | c4 |
                            // +------------+-------------+------------------------+------------------------+
                            // | 0000-00-00 | 00:00:00.00 | 0000-00-00 00:00:00.00 | 0000-00-00 00:00:00.00 |
                            // | 0000-00-00 | 00:01:00.00 | 0000-00-00 00:00:00.00 | 0000-00-00 00:00:00.00 |
                            // +------------+-------------+------------------------+------------------------+
                            // 
                            // Note the '0001' years that were inserted are stored as '0000'
                            // 
                            Date c1 = ((Date) (after.get("c1")));// epoch days

                            assertThat(c1).isNull();
                            // '00:00:00.000'
                            Date c2 = ((Date) (after.get("c2")));// milliseconds past midnight

                            LocalTime c2Time = LocalTime.ofNanoOfDay(TimeUnit.MILLISECONDS.toNanos(c2.getTime()));
                            assertThat(c2Time.getHour()).isEqualTo(0);
                            assertThat((((c2Time.getMinute()) == 0) || ((c2Time.getMinute()) == 1))).isTrue();
                            assertThat(c2Time.getSecond()).isEqualTo(0);
                            assertThat(c2Time.getNano()).isEqualTo(0);
                            assertThat(io.debezium.time.Time.toMilliOfDay(c2Time, ADJUSTER)).isEqualTo(((int) (c2.getTime())));
                            Date c3 = ((Date) (after.get("c3")));// epoch millis

                            assertThat(c3).isNull();
                            // '0000-00-00 00:00:00.00'
                            String c4 = after.getString("c4");// MySQL timestamp, so always ZonedTimestamp

                            OffsetDateTime c4DateTime = OffsetDateTime.parse(c4, ZonedTimestamp.FORMATTER);
                            // Timestamp is stored as UTC
                            assertThat(c4DateTime.getOffset()).isEqualTo(ZoneOffset.UTC);
                            // In case the timestamp string not in our timezone, convert to UTC so we can compare ...
                            c4DateTime = c4DateTime.withOffsetSameInstant(ZoneOffset.of("Z"));
                            assertThat(c4DateTime.getYear()).isEqualTo(1970);
                            assertThat(c4DateTime.getMonth()).isEqualTo(Month.JANUARY);
                            assertThat(c4DateTime.getDayOfMonth()).isEqualTo(1);
                            // Difference depends upon whether the zone we're in is also using DST as it is on the date in question ...
                            assertThat(c4DateTime.getHour()).isIn(0, 1);
                            assertThat(c4DateTime.getMinute()).isEqualTo(0);
                            assertThat(c4DateTime.getSecond()).isEqualTo(0);
                            assertThat(c4DateTime.getNano()).isEqualTo(0);
                        } else
                            if (record.topic().endsWith("dbz_123_bitvaluetest")) {
                                // All row events should have the same values ...
                                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                // c1 BIT, // 1 bit
                                // c2 BIT(2), // 2 bits
                                // c3 BIT(8) // 8 bits
                                // c4 BIT(64) // 64 bits
                                Boolean c1 = after.getBoolean("c1");
                                assertThat(c1).isEqualTo(Boolean.TRUE);
                                byte[] c2 = after.getBytes("c2");
                                assertThat(c2.length).isEqualTo(1);
                                assertThat(c2[0]).isEqualTo(((byte) (2)));
                                byte[] c3 = after.getBytes("c3");
                                assertThat(c3.length).isEqualTo(1);
                                assertThat(c3[0]).isEqualTo(((byte) (64)));
                                // 1011011100000111011011011 = 23989979
                                byte[] c4 = after.getBytes("c4");
                                assertThat(c4.length).isEqualTo(8);// bytes, little endian

                                assertThat(c4[0]).isEqualTo(((byte) (219)));// 11011011

                                assertThat(c4[1]).isEqualTo(((byte) (14)));// 00001110

                                assertThat(c4[2]).isEqualTo(((byte) (110)));// 01101110

                                assertThat(c4[3]).isEqualTo(((byte) (1)));// 1

                                assertThat(c4[4]).isEqualTo(((byte) (0)));
                                assertThat(c4[5]).isEqualTo(((byte) (0)));
                                assertThat(c4[6]).isEqualTo(((byte) (0)));
                                assertThat(c4[7]).isEqualTo(((byte) (0)));
                            } else
                                if (record.topic().endsWith("dbz_147_decimalvalues")) {
                                    Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                    Object decimalValue = after.get("decimal_value");
                                    assertThat(decimalValue).isInstanceOf(.class);
                                    BigDecimal bigValue = ((BigDecimal) (decimalValue));
                                    assertThat(bigValue.doubleValue()).isEqualTo(12345.67, Delta.delta(0.01));
                                }





        });
    }

    @Test
    public void shouldConsumeAllEventsFromDatabaseUsingSnapshot() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numTables = 11;
        int numDataRecords = 20;
        int numDdlRecords = (numTables * 2) + 3;// for each table (1 drop + 1 create) + for each db (1 create + 1 drop + 1 use)

        int numCreateDefiner = 1;
        int numSetVariables = 1;
        SourceRecords records = consumeRecordsByTopic(((numDdlRecords + numSetVariables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numDdlRecords + numSetVariables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("t1464075356413_testtable6")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz84_integer_types_table")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_85_fractest")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_100_enumsettest")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_102_charsettest")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_114_zerovaluetest")).size()).isEqualTo(2);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_123_bitvaluetest")).size()).isEqualTo(2);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_104_customers")).size()).isEqualTo(4);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_147_decimalvalues")).size()).isEqualTo(1);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_195_numvalues")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_342_timetest")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo((numTables + 1));
        assertThat(records.databaseNames().size()).isEqualTo(2);
        assertThat(records.databaseNames()).containsOnly(DATABASE.getDatabaseName(), "");
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo(numDdlRecords);
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("").size()).isEqualTo(1);// SET statement

        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_100_enumsettest")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                String c1 = after.getString("c1");
                String c2 = after.getString("c2");
                if (c1.equals("a")) {
                    assertThat(c2).isEqualTo("a,b,c");
                } else
                    if (c1.equals("b")) {
                        assertThat(c2).isEqualTo("a,b");
                    } else
                        if (c1.equals("c")) {
                            assertThat(c2).isEqualTo("a");
                        } else {
                            fail("c1 didn't match expected value");
                        }


            } else
                if (record.topic().endsWith("dbz_102_charsettest")) {
                    Struct after = value.getStruct(Envelope.FieldName.AFTER);
                    String text = after.getString("text");
                    assertThat(text).isEqualTo("??");
                } else
                    if (record.topic().endsWith("dbz_85_fractest")) {
                        // The microseconds of all three should be exactly 780
                        Struct after = value.getStruct(Envelope.FieldName.AFTER);
                        // c1 DATE,
                        // c2 TIME(2),
                        // c3 DATETIME(2),
                        // c4 TIMESTAMP(2)
                        // 
                        // Instant in UTC - 2014-09-09 04:51:04.777
                        // Instant in US/Samoa - 2014-09-08 17:51:04.777
                        // {"c1" : "16321", "c2" : "17:51:04.777", "c3" : "1410198664780", "c4" : "2014-09-08T17:51:04.78-05:00"}
                        // '2014-09-08' - date type is not dependent on timezone so no shift is needed
                        Integer c1 = after.getInt32("c1");// epoch days

                        LocalDate c1Date = LocalDate.ofEpochDay(c1);
                        assertThat(c1Date.getYear()).isEqualTo(2014);
                        assertThat(c1Date.getMonth()).isEqualTo(Month.SEPTEMBER);
                        assertThat(c1Date.getDayOfMonth()).isEqualTo(8);
                        assertThat(io.debezium.time.Date.toEpochDay(c1Date, ADJUSTER)).isEqualTo(c1);
                        // '17:51:04.777' - time is Duration so no timeshift is needed
                        Long c2 = after.getInt64("c2");
                        Duration c2Time = Duration.ofNanos((c2 * 1000));
                        assertThat(c2Time.toHours()).isEqualTo(17);
                        assertThat(c2Time.toMinutes()).isEqualTo(1071);
                        assertThat(c2Time.getSeconds()).isEqualTo(64264);
                        assertThat(c2Time.getNano()).isEqualTo(780000000);
                        assertThat(c2Time.toNanos()).isEqualTo(64264780000000L);
                        assertThat(c2Time).isEqualTo(Duration.ofHours(17).plusMinutes(51).plusSeconds(4).plusMillis(780));
                        // '2014-09-08 17:51:04.777'
                        // DATETIME is a logical date and time, it doesn't contain any TZ information;
                        // it is mapped to a point on the time line by interpreting the value at UTC
                        Long c3 = after.getInt64("c3");// epoch millis

                        long c3Seconds = c3 / 1000;
                        long c3Millis = c3 % 1000;
                        LocalDateTime c3DateTime = LocalDateTime.ofEpochSecond(c3Seconds, ((int) (TimeUnit.MILLISECONDS.toNanos(c3Millis))), ZoneOffset.UTC);
                        assertThat(c3DateTime.getYear()).isEqualTo(2014);
                        assertThat(c3DateTime.getMonth()).isEqualTo(Month.SEPTEMBER);
                        assertThat(c3DateTime.getDayOfMonth()).isEqualTo(8);
                        assertThat(c3DateTime.getHour()).isEqualTo(17);
                        assertThat(c3DateTime.getMinute()).isEqualTo(51);
                        assertThat(c3DateTime.getSecond()).isEqualTo(4);
                        assertThat(c3DateTime.getNano()).isEqualTo(((int) (TimeUnit.MILLISECONDS.toNanos(780))));
                        assertThat(io.debezium.time.Timestamp.toEpochMillis(c3DateTime, ADJUSTER)).isEqualTo(c3);
                        // '2014-09-08 17:51:04.777' -> '2014-09-09 04:51:04.78'
                        String c4 = after.getString("c4");// timestamp

                        OffsetDateTime c4DateTime = OffsetDateTime.parse(c4, ZonedTimestamp.FORMATTER);
                        // TIMESTAMP should be converted to UTC, the DBs TZ is US/Samoa (-11:00)
                        assertThat(c4DateTime.getYear()).isEqualTo(2014);
                        assertThat(c4DateTime.getMonth()).isEqualTo(Month.SEPTEMBER);
                        assertThat(c4DateTime.getDayOfMonth()).isEqualTo(9);
                        assertThat(c4DateTime.getHour()).isEqualTo(4);
                        assertThat(c4DateTime.getMinute()).isEqualTo(51);
                        assertThat(c4DateTime.getSecond()).isEqualTo(4);
                        assertThat(c4DateTime.getNano()).isEqualTo(((int) (TimeUnit.MILLISECONDS.toNanos(780))));
                        OffsetDateTime expected = ZonedDateTime.of(LocalDateTime.of(2014, 9, 8, 17, 51, 4, ((int) (TimeUnit.MILLISECONDS.toNanos(780)))), UniqueDatabase.TIMEZONE).withZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
                        assertThat(c4DateTime).isEqualTo(expected);
                    } else
                        if (record.topic().endsWith("dbz_123_bitvaluetest")) {
                            // All row events should have the same values ...
                            Struct after = value.getStruct(Envelope.FieldName.AFTER);
                            // c1 BIT, // 1 bit
                            // c2 BIT(2), // 2 bits
                            // c3 BIT(8) // 8 bits
                            // c4 BIT(64) // 64 bits
                            Boolean c1 = after.getBoolean("c1");
                            assertThat(c1).isEqualTo(Boolean.TRUE);
                            byte[] c2 = after.getBytes("c2");
                            assertThat(c2.length).isEqualTo(1);
                            assertThat(c2[0]).isEqualTo(((byte) (2)));
                            byte[] c3 = after.getBytes("c3");
                            assertThat(c3.length).isEqualTo(1);
                            assertThat(c3[0]).isEqualTo(((byte) (64)));
                            // 1011011100000111011011011 = 23989979
                            byte[] c4 = after.getBytes("c4");
                            assertThat(c4.length).isEqualTo(8);// bytes, little endian

                            assertThat(c4[0]).isEqualTo(((byte) (219)));// 11011011

                            assertThat(c4[1]).isEqualTo(((byte) (14)));// 00001110

                            assertThat(c4[2]).isEqualTo(((byte) (110)));// 01101110

                            assertThat(c4[3]).isEqualTo(((byte) (1)));// 1

                            assertThat(c4[4]).isEqualTo(((byte) (0)));
                            assertThat(c4[5]).isEqualTo(((byte) (0)));
                            assertThat(c4[6]).isEqualTo(((byte) (0)));
                            assertThat(c4[7]).isEqualTo(((byte) (0)));
                        } else
                            if (record.topic().endsWith("dbz_147_decimalvalues")) {
                                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                Object decimalValue = after.get("decimal_value");
                                assertThat(decimalValue).isInstanceOf(.class);
                                BigDecimal bigValue = ((BigDecimal) (decimalValue));
                                assertThat(bigValue.doubleValue()).isEqualTo(12345.67, Delta.delta(0.01));
                            } else
                                if (record.topic().endsWith("dbz_195_numvalues")) {
                                    Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                    Object searchVersion = after.get("search_version_read");
                                    assertThat(searchVersion).isInstanceOf(.class);
                                    Integer intValue = ((Integer) (searchVersion));
                                    if ((intValue.intValue()) < 0) {
                                        assertThat(intValue.intValue()).isEqualTo(-2147483648);
                                    } else
                                        if ((intValue.intValue()) > 0) {
                                            assertThat(intValue.intValue()).isEqualTo(2147483647);
                                        } else {
                                            assertThat(intValue.intValue()).isEqualTo(0);
                                        }

                                } else
                                    if (record.topic().endsWith("dbz_342_timetest")) {
                                        Struct after = value.getStruct(Envelope.FieldName.AFTER);
                                        // '517:51:04.777'
                                        long c1 = after.getInt64("c1");
                                        Duration c1Time = Duration.ofNanos((c1 * 1000));
                                        Duration c1ExpectedTime = toDuration("PT517H51M4.78S");
                                        assertEquals(c1ExpectedTime, c1Time);
                                        assertEquals(c1ExpectedTime.toNanos(), c1Time.toNanos());
                                        assertThat(c1Time.toNanos()).isEqualTo(1864264780000000L);
                                        assertThat(c1Time).isEqualTo(Duration.ofHours(517).plusMinutes(51).plusSeconds(4).plusMillis(780));
                                        // '-13:14:50'
                                        long c2 = after.getInt64("c2");
                                        Duration c2Time = Duration.ofNanos((c2 * 1000));
                                        Duration c2ExpectedTime = toDuration("-PT13H14M50S");
                                        assertEquals(c2ExpectedTime, c2Time);
                                        assertEquals(c2ExpectedTime.toNanos(), c2Time.toNanos());
                                        assertThat(c2Time.toNanos()).isEqualTo((-47690000000000L));
                                        assertTrue(c2Time.isNegative());
                                        assertThat(c2Time).isEqualTo(Duration.ofHours((-13)).minusMinutes(14).minusSeconds(50));
                                        // '-733:00:00.0011'
                                        long c3 = after.getInt64("c3");
                                        Duration c3Time = Duration.ofNanos((c3 * 1000));
                                        Duration c3ExpectedTime = toDuration("-PT733H0M0.001S");
                                        assertEquals(c3ExpectedTime, c3Time);
                                        assertEquals(c3ExpectedTime.toNanos(), c3Time.toNanos());
                                        assertThat(c3Time.toNanos()).isEqualTo((-2638800001000000L));
                                        assertTrue(c3Time.isNegative());
                                        assertThat(c3Time).isEqualTo(Duration.ofHours((-733)).minusMillis(1));
                                        // '-1:59:59.0011'
                                        long c4 = after.getInt64("c4");
                                        Duration c4Time = Duration.ofNanos((c4 * 1000));
                                        Duration c4ExpectedTime = toDuration("-PT1H59M59.001S");
                                        assertEquals(c4ExpectedTime, c4Time);
                                        assertEquals(c4ExpectedTime.toNanos(), c4Time.toNanos());
                                        assertThat(c4Time.toNanos()).isEqualTo((-7199001000000L));
                                        assertTrue(c4Time.isNegative());
                                        assertThat(c4Time).isEqualTo(Duration.ofHours((-1)).minusMinutes(59).minusSeconds(59).minusMillis(1));
                                        // '-838:59:58.999999'
                                        long c5 = after.getInt64("c5");
                                        Duration c5Time = Duration.ofNanos((c5 * 1000));
                                        Duration c5ExpectedTime = toDuration("-PT838H59M58.999999S");
                                        assertEquals(c5ExpectedTime, c5Time);
                                        assertEquals(c5ExpectedTime.toNanos(), c5Time.toNanos());
                                        assertThat(c5Time.toNanos()).isEqualTo((-3020398999999000L));
                                        assertTrue(c5Time.isNegative());
                                        assertThat(c5Time).isEqualTo(Duration.ofHours((-838)).minusMinutes(59).minusSeconds(58).minusNanos(999999000));
                                    }






        });
    }

    @Test
    @FixFor("DBZ-147")
    public void shouldConsumeAllEventsFromDecimalTableInDatabaseUsingBinlogAndNoSnapshot() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(TABLE_WHITELIST, DATABASE.qualifiedTableName("dbz_147_decimalvalues")).with(INCLUDE_SCHEMA_CHANGES, true).with(SNAPSHOT_MODE, SnapshotMode.NEVER.toString()).with(DECIMAL_HANDLING_MODE, DOUBLE).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 9;// still read DDL for all tables

        int numDataRecords = 1;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_147_decimalvalues")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo(2);// rather than 1+numCreateTables

        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_147_decimalvalues")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                Object decimalValue = after.get("decimal_value");
                assertThat(decimalValue).isInstanceOf(.class);
                Double doubleValue = ((Double) (decimalValue));
                assertThat(doubleValue).isEqualTo(12345.67, Delta.delta(0.01));
            }
        });
    }

    @Test
    @FixFor("DBZ-611")
    public void shouldConsumeDecimalAsStringFromBinlog() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(TABLE_WHITELIST, DATABASE.qualifiedTableName("dbz_147_decimalvalues")).with(INCLUDE_SCHEMA_CHANGES, true).with(SNAPSHOT_MODE, SnapshotMode.NEVER.toString()).with(DECIMAL_HANDLING_MODE, STRING).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 9;// still read DDL for all tables

        int numDataRecords = 1;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_147_decimalvalues")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo(2);// rather than 1+numCreateTables

        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_147_decimalvalues")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                Object decimalValue = after.get("decimal_value");
                assertThat(decimalValue).isInstanceOf(.class);
                assertThat(decimalValue).isEqualTo("12345.67");
            }
        });
    }

    @Test
    @FixFor("DBZ-611")
    public void shouldConsumeDecimalAsStringFromSnapshot() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(TABLE_WHITELIST, DATABASE.qualifiedTableName("dbz_147_decimalvalues")).with(INCLUDE_SCHEMA_CHANGES, true).with(DECIMAL_HANDLING_MODE, STRING).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int ddlRecords = 6;
        int numDataRecords = 1;
        SourceRecords records = consumeRecordsByTopic((ddlRecords + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo(ddlRecords);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_147_decimalvalues")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo(2);// rather than 1+numCreateTables

        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_147_decimalvalues")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                Object decimalValue = after.get("decimal_value");
                assertThat(decimalValue).isInstanceOf(.class);
                assertThat(decimalValue).isEqualTo("12345.67");
            }
        });
    }

    @Test
    @FixFor("DBZ-342")
    public void shouldReturnTimeColumnsAsMilliSecondsInAdaptivePrecisionMode() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(INCLUDE_SCHEMA_CHANGES, true).with(SNAPSHOT_MODE, NEVER).with(TIME_PRECISION_MODE, ADAPTIVE).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 11;
        int numDataRecords = 20;
        int numCreateDefiner = 1;
        SourceRecords records = consumeRecordsByTopic((((numCreateDatabase + numCreateTables) + numDataRecords) + numCreateDefiner));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo(((numCreateDatabase + numCreateTables) + numCreateDefiner));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_85_fractest")).size()).isEqualTo(1);
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_85_fractest")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                // c2 TIME(2),
                // { "c2" : "17:51:04.777" }
                Integer c2 = after.getInt32("c2");
                long expectedMillis = Duration.ofHours(17).plusMinutes(51).plusSeconds(4).plusMillis(780).toMillis();
                assertThat(c2).isEqualTo(((int) (expectedMillis)));
            }
        });
    }
}

