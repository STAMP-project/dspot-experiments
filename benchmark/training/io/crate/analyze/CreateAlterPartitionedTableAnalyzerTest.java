/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.analyze;


import AutoExpandReplicas.SETTING;
import TableParameterInfo.NUMBER_OF_SHARDS;
import TableParameterInfo.SETTING_WAIT_FOR_ACTIVE_SHARDS;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.metadata.RelationName;
import io.crate.sql.parser.ParsingException;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.TestingHelpers;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CreateAlterPartitionedTableAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testPartitionedBy() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table (" + (((("  id integer," + "  no_index string index off,") + "  name string,") + "  date timestamp") + ") partitioned by (name)")));
        assertThat(analysis.partitionedBy().size(), Matchers.is(1));
        assertThat(analysis.partitionedBy().get(0), Matchers.contains("name", "keyword"));
        // partitioned columns must be not indexed in mapping
        Map<String, Object> nameMapping = ((Map<String, Object>) (analysis.mappingProperties().get("name")));
        assertThat(TestingHelpers.mapToSortedString(nameMapping), Matchers.is("index=false, type=keyword"));
        Map<String, Object> metaMapping = ((Map) (analysis.mapping().get("_meta")));
        assertThat(((Map<String, Object>) (metaMapping.get("columns"))), Matchers.not(Matchers.hasKey("name")));
        List<List<String>> partitionedByMeta = ((List<List<String>>) (metaMapping.get("partitioned_by")));
        assertTrue(analysis.isPartitioned());
        assertThat(partitionedByMeta.size(), Matchers.is(1));
        assertThat(partitionedByMeta.get(0).get(0), Matchers.is("name"));
        assertThat(partitionedByMeta.get(0).get(1), Matchers.is("keyword"));
    }

    @Test
    public void testPartitionedByMultipleColumns() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table (" + (("  name string," + "  date timestamp") + ") partitioned by (name, date)")));
        assertThat(analysis.partitionedBy().size(), Matchers.is(2));
        Map<String, Object> properties = analysis.mappingProperties();
        assertThat(TestingHelpers.mapToSortedString(properties), Matchers.is(("date={format=epoch_millis||strict_date_optional_time, index=false, type=date}, " + "name={index=false, type=keyword}")));
        assertThat(((Map<String, Object>) (((Map) (analysis.mapping().get("_meta"))).get("columns"))), Matchers.allOf(Matchers.not(Matchers.hasKey("name")), Matchers.not(Matchers.hasKey("date"))));
        assertThat(analysis.partitionedBy().get(0), Matchers.contains("name", "keyword"));
        assertThat(analysis.partitionedBy().get(1), Matchers.contains("date", "date"));
    }

    @Test
    public void testPartitionedByNestedColumns() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table (" + (((((("  id integer," + "  no_index string index off,") + "  o object as (") + "    name string") + "  ),") + "  date timestamp") + ") partitioned by (date, o['name'])")));
        assertThat(analysis.partitionedBy().size(), Matchers.is(2));
        Map<String, Object> oMapping = ((Map<String, Object>) (analysis.mappingProperties().get("o")));
        assertThat(TestingHelpers.mapToSortedString(oMapping), Matchers.is("dynamic=true, properties={name={index=false, type=keyword}}, type=object"));
        assertThat(((Map<String, Object>) (((Map) (analysis.mapping().get("_meta"))).get("columns"))), Matchers.not(Matchers.hasKey("date")));
        Map metaColumns = ((Map) (((Map) (analysis.mapping().get("_meta"))).get("columns")));
        assertNull(metaColumns);
        assertThat(analysis.partitionedBy().get(0), Matchers.contains("date", "date"));
        assertThat(analysis.partitionedBy().get(1), Matchers.contains("o.name", "keyword"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartitionedByArrayNestedColumns() {
        e.analyze(("create table my_table (" + (((("  a array(object as (" + "    name string") + "  )),") + "  date timestamp") + ") partitioned by (date, a['name'])")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartitionedByArray() {
        e.analyze(("create table my_table (" + (("  a array(string)," + "  date timestamp") + ") partitioned by (a)")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartitionedByInnerArray() {
        e.analyze(("create table my_table (" + (("  a object as (names array(string))," + "  date timestamp") + ") partitioned by (a['names'])")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartitionedByObject() {
        e.analyze(("create table my_table (" + (("  a object as(name string)," + "  date timestamp") + ") partitioned by (a)")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartitionedByInnerObject() {
        e.analyze(("create table my_table (" + (("  a object as(b object as(name string))," + "  date timestamp") + ") partitioned by (a['b'])")));
    }

    @Test
    public void testPartitionByUnknownColumn() {
        expectedException.expect(ColumnUnknownException.class);
        e.analyze("create table my_table (p string) partitioned by (a)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartitionedByNotPartOfPrimaryKey() {
        e.analyze(("create table my_table (" + (((("  id1 integer," + "  id2 integer,") + "  date timestamp,") + "  primary key (id1, id2)") + ") partitioned by (id1, date)")));
    }

    @Test
    public void testPartitionedByPartOfPrimaryKey() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table (" + (((("  id1 integer," + "  id2 integer,") + "  date timestamp,") + "  primary key (id1, id2)") + ") partitioned by (id1)")));
        assertThat(analysis.partitionedBy().size(), Matchers.is(1));
        assertThat(analysis.partitionedBy().get(0), Matchers.contains("id1", "integer"));
        Map<String, Object> oMapping = ((Map<String, Object>) (analysis.mappingProperties().get("id1")));
        assertThat(TestingHelpers.mapToSortedString(oMapping), Matchers.is("index=false, type=integer"));
        assertThat(((Map<String, Object>) (((Map) (analysis.mapping().get("_meta"))).get("columns"))), Matchers.not(Matchers.hasKey("id1")));
    }

    @Test
    public void testPartitionedByIndexed() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot use column name with fulltext index in PARTITIONED BY clause");
        e.analyze(("create table my_table(" + ((((("  name string index using fulltext," + "  no_index string index off,") + "  stuff string,") + "  o object as (s string),") + "  index ft using fulltext(stuff, o['s']) with (analyzer='snowball')") + ") partitioned by (name)")));
    }

    @Test
    public void testPartitionedByCompoundIndex() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot use column ft with fulltext index in PARTITIONED BY clause");
        e.analyze(("create table my_table(" + ((((("  name string index using fulltext," + "  no_index string index off,") + "  stuff string,") + "  o object as (s string),") + "  index ft using fulltext(stuff, o['s']) with (analyzer='snowball')") + ") partitioned by (ft)")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartitionedByClusteredBy() {
        e.analyze(("create table my_table (" + ((("  id integer," + "  name string") + ") partitioned by (id)") + "  clustered by (id) into 5 shards")));
    }

    @Test
    public void testAlterPartitionedTable() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table parted set (number_of_replicas='0-all')");
        assertThat(analysis.partitionName().isPresent(), Matchers.is(false));
        assertThat(analysis.table().isPartitioned(), Matchers.is(true));
        assertEquals("0-all", analysis.tableParameter().settings().get(SETTING.getKey()));
    }

    @Test
    public void testAlterPartitionedTablePartition() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table parted partition (date=1395874800000) set (number_of_replicas='0-all')");
        assertThat(analysis.partitionName().isPresent(), Matchers.is(true));
        assertThat(analysis.partitionName().get(), Matchers.is(new io.crate.metadata.PartitionName(new RelationName("doc", "parted"), Collections.singletonList("1395874800000"))));
        assertEquals("0-all", analysis.tableParameter().settings().get(SETTING.getKey()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlterPartitionedTableNonExistentPartition() {
        e.analyze("alter table parted partition (date='1970-01-01') set (number_of_replicas='0-all')");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlterPartitionedTableInvalidPartitionColumns() {
        e.analyze("alter table parted partition (a=1) set (number_of_replicas='0-all')");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlterPartitionedTableInvalidNumber() {
        e.analyze("alter table multi_parted partition (date=1395874800000) set (number_of_replicas='0-all')");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlterNonPartitionedTableWithPartitionClause() {
        e.analyze("alter table users partition (date='1970-01-01') reset (number_of_replicas)");
    }

    @Test
    public void testAlterPartitionedTableShards() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table parted set (number_of_shards=10)");
        assertThat(analysis.partitionName().isPresent(), Matchers.is(false));
        assertThat(analysis.table().isPartitioned(), Matchers.is(true));
        assertEquals("10", analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()));
    }

    @Test
    public void testAlterTablePartitionWithNumberOfShards() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table parted partition (date=1395874800000) set (number_of_shards=1)");
        assertThat(analysis.partitionName().isPresent(), Matchers.is(true));
        assertThat(analysis.table().isPartitioned(), Matchers.is(true));
        assertEquals("1", analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()));
    }

    @Test
    public void testAlterTablePartitionResetShards() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table parted partition (date=1395874800000) reset (number_of_shards)");
        assertThat(analysis.partitionName().isPresent(), Matchers.is(true));
        assertThat(analysis.table().isPartitioned(), Matchers.is(true));
        assertEquals("5", analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()));
    }

    @Test
    public void testAlterPartitionedTablePartitionColumnPolicy() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid property \"column_policy\" passed to [ALTER | CREATE] TABLE statement");
        e.analyze("alter table parted partition (date=1395874800000) set (column_policy='strict')");
    }

    @Test
    public void testAlterPartitionedTableOnlyWithPartition() {
        expectedException.expect(ParsingException.class);
        e.analyze("alter table ONLY parted partition (date=1395874800000) set (column_policy='strict')");
    }

    @Test
    public void testCreatePartitionedByGeoShape() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot use column shape of type geo_shape in PARTITIONED BY clause");
        e.analyze("create table shaped (id int, shape geo_shape) partitioned by (shape)");
    }

    @Test
    public void testAlterTableWithWaitForActiveShards() {
        AlterTableAnalyzedStatement analyzedStatement = e.analyze(("ALTER TABLE parted " + "SET (\"write.wait_for_active_shards\"=1)"));
        assertThat(analyzedStatement.tableParameter().settings().get(SETTING_WAIT_FOR_ACTIVE_SHARDS.getKey()), Matchers.is("1"));
        analyzedStatement = e.analyze("ALTER TABLE parted RESET (\"write.wait_for_active_shards\")");
        assertThat(analyzedStatement.tableParameter().settings().get(SETTING_WAIT_FOR_ACTIVE_SHARDS.getKey()), Matchers.is("ALL"));
    }
}

