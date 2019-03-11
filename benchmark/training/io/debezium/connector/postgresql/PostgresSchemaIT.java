/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql;


import PostgresConnectorConfig.COLUMN_BLACKLIST;
import PostgresConnectorConfig.INCLUDE_UNKNOWN_DATATYPES;
import PostgresConnectorConfig.TABLE_BLACKLIST;
import Schema.BYTES_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_BYTES_SCHEMA;
import Schema.OPTIONAL_FLOAT32_SCHEMA;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT16_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import io.debezium.connector.postgresql.connection.PostgresConnection;
import io.debezium.data.Bits;
import io.debezium.data.Json;
import io.debezium.data.Uuid;
import io.debezium.data.VariableScaleDecimal;
import io.debezium.data.Xml;
import io.debezium.data.geometry.Geography;
import io.debezium.data.geometry.Geometry;
import io.debezium.data.geometry.Point;
import io.debezium.relational.TableId;
import io.debezium.relational.TableSchema;
import io.debezium.time.Date;
import io.debezium.time.MicroDuration;
import io.debezium.time.MicroTime;
import io.debezium.time.MicroTimestamp;
import io.debezium.time.ZonedTime;
import io.debezium.time.ZonedTimestamp;
import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link PostgresSchema}
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public class PostgresSchemaIT {
    private static final String[] TEST_TABLES = new String[]{ "public.numeric_table", "public.numeric_decimal_table", "public.string_table", "public.cash_table", "public.bitbin_table", "public.time_table", "public.text_table", "public.geom_table", "public.tstzrange_table", "public.array_table", "\"Quoted_\"\" . Schema\".\"Quoted_\"\" . Table\"", "public.custom_table" };

    private PostgresSchema schema;

    @Test
    public void shouldLoadSchemaForBuiltinPostgresTypes() throws Exception {
        TestHelper.executeDDL("postgres_create_tables.ddl");
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().build());
        schema = TestHelper.getSchema(config);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesIncluded(PostgresSchemaIT.TEST_TABLES);
            Arrays.stream(PostgresSchemaIT.TEST_TABLES).forEach(( tableId) -> assertKeySchema(tableId, "pk", INT32_SCHEMA));
            assertTableSchema("public.numeric_table", "si, i, bi, r, db, ss, bs, b", OPTIONAL_INT16_SCHEMA, OPTIONAL_INT32_SCHEMA, OPTIONAL_INT64_SCHEMA, OPTIONAL_FLOAT32_SCHEMA, OPTIONAL_FLOAT64_SCHEMA, INT16_SCHEMA, INT64_SCHEMA, OPTIONAL_BOOLEAN_SCHEMA);
            assertTableSchema("public.numeric_decimal_table", "d, dzs, dvs, n, nzs, nvs", Decimal.builder(2).parameter(TestHelper.PRECISION_PARAMETER_KEY, "3").optional().build(), Decimal.builder(0).parameter(TestHelper.PRECISION_PARAMETER_KEY, "4").optional().build(), VariableScaleDecimal.builder().optional().build(), Decimal.builder(4).parameter(TestHelper.PRECISION_PARAMETER_KEY, "6").optional().build(), Decimal.builder(0).parameter(TestHelper.PRECISION_PARAMETER_KEY, "4").optional().build(), VariableScaleDecimal.builder().optional().build());
            assertTableSchema("public.string_table", "vc, vcv, ch, c, t, ct", OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA);
            assertTableSchema("public.cash_table", "csh", Decimal.builder(2).optional().build());
            assertTableSchema("public.bitbin_table", "ba, bol, bs, bv", OPTIONAL_BYTES_SCHEMA, OPTIONAL_BOOLEAN_SCHEMA, Bits.builder(2).optional().build(), Bits.builder(2).optional().build());
            assertTableSchema("public.time_table", "ts, tz, date, ti, ttz, it", MicroTimestamp.builder().optional().build(), ZonedTimestamp.builder().optional().build(), Date.builder().optional().build(), MicroTime.builder().optional().build(), ZonedTime.builder().optional().build(), MicroDuration.builder().optional().build());
            assertTableSchema("public.text_table", "j, jb, x, u", Json.builder().optional().build(), Json.builder().optional().build(), Xml.builder().optional().build(), Uuid.builder().optional().build());
            assertTableSchema("public.geom_table", "p", Point.builder().optional().build());
            assertTableSchema("public.tstzrange_table", "unbounded_exclusive_range, bounded_inclusive_range", OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA);
            assertTableSchema("public.array_table", "int_array, bigint_array, text_array", SchemaBuilder.array(OPTIONAL_INT32_SCHEMA).optional().build(), SchemaBuilder.array(OPTIONAL_INT64_SCHEMA).optional().build(), SchemaBuilder.array(OPTIONAL_STRING_SCHEMA).optional().build());
            assertTableSchema("\"Quoted_\"\" . Schema\".\"Quoted_\"\" . Table\"", "\"Quoted_\"\" . Text_Column\"", OPTIONAL_STRING_SCHEMA);
            TableSchema tableSchema = schemaFor("public.custom_table");
            assertThat(tableSchema.valueSchema().field("lt")).isNull();
        }
    }

    @Test
    public void shouldLoadSchemaForExtensionPostgresTypes() throws Exception {
        TestHelper.executeDDL("postgres_create_tables.ddl");
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(INCLUDE_UNKNOWN_DATATYPES, true).build());
        schema = TestHelper.getSchema(config);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesIncluded(PostgresSchemaIT.TEST_TABLES);
            assertTableSchema("public.custom_table", "lt, i", OPTIONAL_BYTES_SCHEMA, BYTES_SCHEMA);
        }
    }

    @Test
    public void shouldLoadSchemaForPostgisTypes() throws Exception {
        TestHelper.executeDDL("init_postgis.ddl");
        TestHelper.executeDDL("postgis_create_tables.ddl");
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().build());
        schema = TestHelper.getSchema(config);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            final String[] testTables = new String[]{ "public.postgis_table" };
            assertTablesIncluded(testTables);
            Arrays.stream(testTables).forEach(( tableId) -> assertKeySchema(tableId, "pk", INT32_SCHEMA));
            assertTableSchema("public.postgis_table", "p, ml", Geometry.builder().optional().build(), Geography.builder().optional().build());
        }
    }

    @Test
    public void shouldApplyFilters() throws Exception {
        String statements = "CREATE SCHEMA s1; " + (((((((("CREATE SCHEMA s2; " + "DROP TABLE IF EXISTS s1.A;") + "DROP TABLE IF EXISTS s1.B;") + "DROP TABLE IF EXISTS s2.A;") + "DROP TABLE IF EXISTS s2.B;") + "CREATE TABLE s1.A (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "CREATE TABLE s1.B (pk SERIAL, ba integer, PRIMARY KEY(pk));") + "CREATE TABLE s2.A (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "CREATE TABLE s2.B (pk SERIAL, ba integer, PRIMARY KEY(pk));");
        TestHelper.execute(statements);
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(PostgresConnectorConfig.SCHEMA_BLACKLIST, "s1").build());
        final TypeRegistry typeRegistry = TestHelper.getTypeRegistry();
        schema = TestHelper.getSchema(config, typeRegistry);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesIncluded("s2.a", "s2.b");
            assertTablesExcluded("s1.a", "s1.b");
        }
        config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(PostgresConnectorConfig.SCHEMA_BLACKLIST, "s.*").build());
        schema = TestHelper.getSchema(config, typeRegistry);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesExcluded("s1.a", "s2.a", "s1.b", "s2.b");
        }
        config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(TABLE_BLACKLIST, "s1.A,s2.A").build());
        schema = TestHelper.getSchema(config, typeRegistry);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesIncluded("s1.b", "s2.b");
            assertTablesExcluded("s1.a", "s2.a");
        }
        config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(PostgresConnectorConfig.SCHEMA_BLACKLIST, "s2").with(TABLE_BLACKLIST, "s1.A").build());
        schema = TestHelper.getSchema(config, typeRegistry);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesIncluded("s1.b");
            assertTablesExcluded("s1.a", "s2.a", "s2.b");
        }
        config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(COLUMN_BLACKLIST, ".*aa").build());
        schema = TestHelper.getSchema(config, typeRegistry);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertColumnsExcluded("s1.a.aa", "s2.a.aa");
        }
    }

    @Test
    public void shouldDetectNewChangesAfterRefreshing() throws Exception {
        String statements = "CREATE SCHEMA IF NOT EXISTS public;" + ("DROP TABLE IF EXISTS table1;" + "CREATE TABLE table1 (pk SERIAL,  PRIMARY KEY(pk));");
        TestHelper.execute(statements);
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().build());
        schema = TestHelper.getSchema(config);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesIncluded("public.table1");
        }
        statements = "DROP TABLE IF EXISTS table1;" + ("DROP TABLE IF EXISTS table2;" + "CREATE TABLE table2 (pk SERIAL, strcol VARCHAR, PRIMARY KEY(pk));");
        TestHelper.execute(statements);
        String tableId = "public.table2";
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, false);
            assertTablesIncluded(tableId);
            assertTablesExcluded("public.table1");
            assertTableSchema(tableId, "strcol", OPTIONAL_STRING_SCHEMA);
        }
        statements = "ALTER TABLE table2 ADD COLUMN vc VARCHAR(2);" + ("ALTER TABLE table2 ADD COLUMN si SMALLINT;" + "ALTER TABLE table2 DROP COLUMN strcol;");
        TestHelper.execute(statements);
        try (PostgresConnection connection = TestHelper.create()) {
            schema.refresh(connection, TableId.parse(tableId, false), false);
            assertTablesIncluded(tableId);
            assertTablesExcluded("public.table1");
            assertTableSchema(tableId, "vc, si", OPTIONAL_STRING_SCHEMA, OPTIONAL_INT16_SCHEMA);
            assertColumnsExcluded((tableId + ".strcol"));
        }
    }

    @Test
    public void shouldPopulateToastableColumnsCache() throws Exception {
        String statements = "CREATE SCHEMA IF NOT EXISTS public;" + ("DROP TABLE IF EXISTS table1;" + "CREATE TABLE table1 (pk SERIAL,  toasted text, untoasted int, PRIMARY KEY(pk));");
        TestHelper.execute(statements);
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().build());
        schema = TestHelper.getSchema(config);
        TableId tableId = TableId.parse("public.table1", false);
        // Before refreshing, we should have an empty array for the table
        Assert.assertTrue(schema.getToastableColumnsForTableId(tableId).isEmpty());
        try (PostgresConnection connection = TestHelper.create()) {
            // Load up initial schema info. This should not populate the toastable columns cache, as the cache is loaded
            // on-demand per-table refresh.
            schema.refresh(connection, false);
            Assert.assertTrue(schema.getToastableColumnsForTableId(tableId).isEmpty());
            // After refreshing w/ toastable column refresh disabled, we should still have an empty array
            schema.refresh(connection, tableId, false);
            Assert.assertTrue(schema.getToastableColumnsForTableId(tableId).isEmpty());
            // After refreshing w/ toastable column refresh enabled, we should have only the 'toasted' column in the cache
            schema.refresh(connection, tableId, true);
            assertThat(schema.getToastableColumnsForTableId(tableId)).containsOnly("toasted");
        }
    }
}

