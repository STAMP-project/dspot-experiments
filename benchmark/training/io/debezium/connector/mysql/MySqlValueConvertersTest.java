/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import JdbcValueConverters.BigIntUnsignedMode;
import JdbcValueConverters.DecimalMode;
import io.debezium.connector.mysql.antlr.MySqlAntlrDdlParser;
import io.debezium.jdbc.TemporalPrecisionMode;
import io.debezium.relational.Column;
import io.debezium.relational.Table;
import io.debezium.relational.TableId;
import io.debezium.relational.Tables;
import io.debezium.relational.ddl.DdlParser;
import java.time.temporal.TemporalAdjuster;
import org.apache.kafka.connect.data.Field;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MySqlValueConvertersTest {
    private static final TemporalAdjuster ADJUSTER = MySqlValueConverters::adjustTemporal;

    @Test
    public void shouldAdjustLocalDateWithTwoDigitYears() {
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(0))).isEqualTo(localDateWithYear(2000));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(1))).isEqualTo(localDateWithYear(2001));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(10))).isEqualTo(localDateWithYear(2010));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(69))).isEqualTo(localDateWithYear(2069));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(70))).isEqualTo(localDateWithYear(1970));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(71))).isEqualTo(localDateWithYear(1971));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(99))).isEqualTo(localDateWithYear(1999));
    }

    @Test
    public void shouldAdjustLocalDateTimeWithTwoDigitYears() {
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(0))).isEqualTo(localDateTimeWithYear(2000));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(1))).isEqualTo(localDateTimeWithYear(2001));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(10))).isEqualTo(localDateTimeWithYear(2010));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(69))).isEqualTo(localDateTimeWithYear(2069));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(70))).isEqualTo(localDateTimeWithYear(1970));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(71))).isEqualTo(localDateTimeWithYear(1971));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(99))).isEqualTo(localDateTimeWithYear(1999));
    }

    @Test
    public void shouldNotAdjustLocalDateWithThreeDigitYears() {
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear((-1)))).isEqualTo(localDateWithYear((-1)));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateWithYear(100))).isEqualTo(localDateWithYear(100));
    }

    @Test
    public void shouldNotAdjustLocalDateTimeWithThreeDigitYears() {
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear((-1)))).isEqualTo(localDateTimeWithYear((-1)));
        assertThat(MySqlValueConvertersTest.ADJUSTER.adjustInto(localDateTimeWithYear(100))).isEqualTo(localDateTimeWithYear(100));
    }

    @Test
    public void testJsonValues() {
        String sql = "CREATE TABLE JSON_TABLE (" + (("    A JSON," + "    B JSON NOT NULL") + ");");
        MySqlValueConverters converters = new MySqlValueConverters(DecimalMode.DOUBLE, TemporalPrecisionMode.CONNECT, BigIntUnsignedMode.LONG);
        DdlParser parser = new MySqlAntlrDdlParser();
        Tables tables = new Tables();
        parser.parse(sql, tables);
        Table table = tables.forTable(new TableId(null, null, "JSON_TABLE"));
        // ColA -  Nullable column
        Column colA = table.columnWithName("A");
        Field fieldA = new Field(colA.name(), (-1), converters.schemaBuilder(colA).optional().build());
        assertThat(converters.converter(colA, fieldA).convert("{}")).isEqualTo("{}");
        assertThat(converters.converter(colA, fieldA).convert("[]")).isEqualTo("[]");
        assertThat(converters.converter(colA, fieldA).convert(new byte[0])).isNull();
        assertThat(converters.converter(colA, fieldA).convert(null)).isNull();
        assertThat(converters.converter(colA, fieldA).convert("{ \"key1\": \"val1\", \"key2\": {\"key3\":\"val3\"} }")).isEqualTo("{ \"key1\": \"val1\", \"key2\": {\"key3\":\"val3\"} }");
        // ColB - NOT NUll column
        Column colB = table.columnWithName("B");
        Field fieldB = new Field(colB.name(), (-1), converters.schemaBuilder(colB).build());
        assertThat(converters.converter(colB, fieldB).convert("{}")).isEqualTo("{}");
        assertThat(converters.converter(colB, fieldB).convert("[]")).isEqualTo("[]");
        assertThat(converters.converter(colB, fieldB).convert(new byte[0])).isEqualTo("{}");
        assertThat(converters.converter(colB, fieldB).convert(null)).isEqualTo("{}");
        assertThat(converters.converter(colB, fieldB).convert("{ \"key1\": \"val1\", \"key2\": {\"key3\":\"val3\"} }")).isEqualTo("{ \"key1\": \"val1\", \"key2\": {\"key3\":\"val3\"} }");
    }
}

