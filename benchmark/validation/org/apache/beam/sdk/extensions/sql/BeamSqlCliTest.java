/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql;


import Schema.FieldType;
import TestTableProvider.TableWithRows;
import java.util.stream.Stream;
import org.apache.beam.sdk.extensions.sql.impl.ParseException;
import org.apache.beam.sdk.extensions.sql.impl.utils.CalciteUtils;
import org.apache.beam.sdk.extensions.sql.meta.Table;
import org.apache.beam.sdk.extensions.sql.meta.provider.test.TestTableProvider;
import org.apache.beam.sdk.extensions.sql.meta.provider.text.TextTableProvider;
import org.apache.beam.sdk.extensions.sql.meta.store.InMemoryMetaStore;
import org.apache.beam.sdk.extensions.sql.utils.DateTimeUtils;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.Field;
import org.apache.beam.sdk.values.Row;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * UnitTest for {@link BeamSqlCli}.
 */
public class BeamSqlCliTest {
    @Test
    public void testExecute_createTextTable() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        metaStore.registerProvider(new TextTableProvider());
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE person (\n" + (((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\', \n") + "age int COMMENT \'age\') \n") + "TYPE \'text\' \n") + "COMMENT '' LOCATION '/home/admin/orders'")));
        Table table = metaStore.getTables().get("person");
        Assert.assertNotNull(table);
        Assert.assertEquals(Stream.of(Field.of("id", CalciteUtils.INTEGER).withDescription("id").withNullable(true), Field.of("name", CalciteUtils.VARCHAR).withDescription("name").withNullable(true), Field.of("age", CalciteUtils.INTEGER).withDescription("age").withNullable(true)).collect(toSchema()), table.getSchema());
    }

    @Test
    public void testExecute_createTableWithPrefixArrayField() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        metaStore.registerProvider(new TextTableProvider());
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE person (\n" + ((((((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\', \n") + "age int COMMENT \'age\', \n") + "tags ARRAY<VARCHAR>, \n") + "matrix ARRAY<ARRAY<INTEGER>> \n") + ") \n") + "TYPE \'text\' \n") + "COMMENT '' LOCATION '/home/admin/orders'")));
        Table table = metaStore.getTables().get("person");
        Assert.assertNotNull(table);
        Assert.assertEquals(Stream.of(Field.of("id", CalciteUtils.INTEGER).withDescription("id").withNullable(true), Field.of("name", CalciteUtils.VARCHAR).withDescription("name").withNullable(true), Field.of("age", CalciteUtils.INTEGER).withDescription("age").withNullable(true), Field.of("tags", FieldType.array(CalciteUtils.VARCHAR)).withNullable(true), Field.of("matrix", FieldType.array(FieldType.array(CalciteUtils.INTEGER))).withNullable(true)).collect(toSchema()), table.getSchema());
    }

    @Test
    public void testExecute_createTableWithPrefixMapField() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        metaStore.registerProvider(new TextTableProvider());
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE person (\n" + ((((((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\', \n") + "age int COMMENT \'age\', \n") + "tags MAP<VARCHAR, VARCHAR>, \n") + "nestedMap MAP<INTEGER, MAP<VARCHAR, INTEGER>> \n") + ") \n") + "TYPE \'text\' \n") + "COMMENT '' LOCATION '/home/admin/orders'")));
        Table table = metaStore.getTables().get("person");
        Assert.assertNotNull(table);
        Assert.assertEquals(Stream.of(Field.of("id", CalciteUtils.INTEGER).withDescription("id").withNullable(true), Field.of("name", CalciteUtils.VARCHAR).withDescription("name").withNullable(true), Field.of("age", CalciteUtils.INTEGER).withDescription("age").withNullable(true), Field.of("tags", FieldType.map(CalciteUtils.VARCHAR, CalciteUtils.VARCHAR)).withNullable(true), Field.of("nestedMap", FieldType.map(CalciteUtils.INTEGER, FieldType.map(CalciteUtils.VARCHAR, CalciteUtils.INTEGER))).withNullable(true)).collect(toSchema()), table.getSchema());
    }

    @Test
    public void testExecute_createTableWithRowField() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        metaStore.registerProvider(new TextTableProvider());
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE person (\n" + (((((((((((((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\', \n") + "age int COMMENT \'age\', \n") + "address ROW ( \n") + "  street VARCHAR, \n") + "  country VARCHAR \n") + "  ), \n") + "addressAngular ROW< \n") + "  street VARCHAR, \n") + "  country VARCHAR \n") + "  >, \n") + "isRobot BOOLEAN") + ") \n") + "TYPE \'text\' \n") + "COMMENT '' LOCATION '/home/admin/orders'")));
        Table table = metaStore.getTables().get("person");
        Assert.assertNotNull(table);
        Assert.assertEquals(Stream.of(Field.of("id", CalciteUtils.INTEGER).withDescription("id").withNullable(true), Field.of("name", CalciteUtils.VARCHAR).withDescription("name").withNullable(true), Field.of("age", CalciteUtils.INTEGER).withDescription("age").withNullable(true), Field.of("address", FieldType.row(Schema.builder().addNullableField("street", CalciteUtils.VARCHAR).addNullableField("country", CalciteUtils.VARCHAR).build())).withNullable(true), Field.of("addressAngular", FieldType.row(Schema.builder().addNullableField("street", CalciteUtils.VARCHAR).addNullableField("country", CalciteUtils.VARCHAR).build())).withNullable(true), Field.of("isRobot", CalciteUtils.BOOLEAN).withNullable(true)).collect(toSchema()), table.getSchema());
    }

    @Test
    public void testExecute_dropTable() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        metaStore.registerProvider(new TextTableProvider());
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE person (\n" + (((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\', \n") + "age int COMMENT \'age\') \n") + "TYPE \'text\' \n") + "COMMENT '' LOCATION '/home/admin/orders'")));
        Table table = metaStore.getTables().get("person");
        Assert.assertNotNull(table);
        cli.execute("drop table person");
        table = metaStore.getTables().get("person");
        Assert.assertNull(table);
    }

    @Test(expected = ParseException.class)
    public void testExecute_dropTable_assertTableRemovedFromPlanner() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        metaStore.registerProvider(new TextTableProvider());
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE person (\n" + (((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\', \n") + "age int COMMENT \'age\') \n") + "TYPE \'text\' \n") + "COMMENT '' LOCATION '/home/admin/orders'")));
        cli.execute("drop table person");
        cli.explainQuery("select * from person");
    }

    @Test
    public void testExplainQuery() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        metaStore.registerProvider(new TextTableProvider());
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE person (\n" + (((("id int COMMENT \'id\', \n" + "name varchar COMMENT \'name\', \n") + "age int COMMENT \'age\') \n") + "TYPE \'text\' \n") + "COMMENT '' LOCATION '/home/admin/orders'")));
        String plan = cli.explainQuery("select * from person");
        Assert.assertThat(plan, Matchers.equalTo("BeamIOSourceRel(table=[[beam, person]])\n"));
    }

    @Test
    public void test_time_types() throws Exception {
        InMemoryMetaStore metaStore = new InMemoryMetaStore();
        TestTableProvider testTableProvider = new TestTableProvider();
        metaStore.registerProvider(testTableProvider);
        BeamSqlCli cli = new BeamSqlCli().metaStore(metaStore);
        cli.execute(("CREATE EXTERNAL TABLE test_table (\n" + (((("f_date DATE, \n" + "f_time TIME, \n") + "f_ts TIMESTAMP") + ") \n") + "TYPE 'test'")));
        cli.execute(("INSERT INTO test_table VALUES (" + (("DATE '2018-11-01', " + "TIME '15:23:59', ") + "TIMESTAMP '2018-07-01 21:26:07.123' )")));
        Table table = metaStore.getTables().get("test_table");
        Assert.assertNotNull(table);
        TestTableProvider.TableWithRows tableWithRows = testTableProvider.tables().get(table.getName());
        Assert.assertEquals(1, tableWithRows.getRows().size());
        Row row = tableWithRows.getRows().get(0);
        Assert.assertEquals(3, row.getFieldCount());
        // test DATE field
        Assert.assertEquals("2018-11-01", row.getDateTime("f_date").toString("yyyy-MM-dd"));
        // test TIME field
        Assert.assertEquals("15:23:59.000", row.getDateTime("f_time").toString("HH:mm:ss.SSS"));
        // test TIMESTAMP field
        Assert.assertEquals(DateTimeUtils.parseTimestampWithUTCTimeZone("2018-07-01 21:26:07.123"), row.getDateTime("f_ts"));
    }
}

