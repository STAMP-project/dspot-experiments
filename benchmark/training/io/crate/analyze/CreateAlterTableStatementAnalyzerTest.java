/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import ColumnPolicy.STRICT;
import DataTypes.LONG;
import IndexMetaData.INDEX_NUMBER_OF_REPLICAS_SETTING;
import TableParameterInfo.ALLOCATION_MAX_RETRIES;
import TableParameterInfo.BLOCKS_METADATA;
import TableParameterInfo.BLOCKS_READ;
import TableParameterInfo.BLOCKS_WRITE;
import TableParameterInfo.COLUMN_POLICY;
import TableParameterInfo.FLUSH_THRESHOLD_SIZE;
import TableParameterInfo.MAPPING_TOTAL_FIELDS_LIMIT;
import TableParameterInfo.MAX_NGRAM_DIFF;
import TableParameterInfo.MAX_SHINGLE_DIFF;
import TableParameterInfo.NUMBER_OF_SHARDS;
import TableParameterInfo.READ_ONLY;
import TableParameterInfo.READ_ONLY_ALLOW_DELETE;
import TableParameterInfo.REFRESH_INTERVAL;
import TableParameterInfo.ROUTING_ALLOCATION_ENABLE;
import TableParameterInfo.TRANSLOG_DURABILITY;
import TableParameterInfo.TRANSLOG_SYNC_INTERVAL;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import io.crate.action.sql.Option;
import io.crate.auth.user.User;
import io.crate.data.Row;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.exceptions.InvalidColumnNameException;
import io.crate.exceptions.InvalidRelationName;
import io.crate.exceptions.InvalidSchemaNameException;
import io.crate.exceptions.OperationOnInaccessibleRelationException;
import io.crate.exceptions.RelationAlreadyExists;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.RelationName;
import io.crate.metadata.Schemas;
import io.crate.sql.parser.ParsingException;
import io.crate.sql.parser.SqlParser;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.TestingHelpers;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.elasticsearch.common.Randomness;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CreateAlterTableStatementAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testCreateTableInSystemSchemasIsProhibited() {
        for (String schema : Schemas.READ_ONLY_SCHEMAS) {
            try {
                e.analyze(String.format("CREATE TABLE %s.%s (ordinal INTEGER, name STRING)", schema, "my_table"));
                fail("create table in read-only schema must fail");
            } catch (IllegalArgumentException e) {
                assertThat(e.getLocalizedMessage(), Matchers.startsWith(("Cannot create relation in read-only schema: " + schema)));
            }
        }
    }

    @Test
    public void testCreateTableWithAlternativePrimaryKeySyntax() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer, name string, primary key (id, name))");
        String[] primaryKeys = analysis.primaryKeys().toArray(new String[0]);
        assertThat(primaryKeys.length, Matchers.is(2));
        assertThat(primaryKeys[0], Matchers.is("id"));
        assertThat(primaryKeys[1], Matchers.is("name"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleCreateTable() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table foo (id integer primary key, name string not null) " + "clustered into 3 shards with (number_of_replicas=0)"));
        assertThat(analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()), Matchers.is("3"));
        assertThat(analysis.tableParameter().settings().get(INDEX_NUMBER_OF_REPLICAS_SETTING.getKey()), Matchers.is("0"));
        Map<String, Object> metaMapping = ((Map) (analysis.mapping().get("_meta")));
        assertNull(metaMapping.get("columns"));
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> idMapping = ((Map<String, Object>) (mappingProperties.get("id")));
        assertThat(idMapping.get("type"), Matchers.is("integer"));
        Map<String, Object> nameMapping = ((Map<String, Object>) (mappingProperties.get("name")));
        assertThat(nameMapping.get("type"), Matchers.is("keyword"));
        String[] primaryKeys = analysis.primaryKeys().toArray(new String[0]);
        assertThat(primaryKeys.length, Matchers.is(1));
        assertThat(primaryKeys[0], Matchers.is("id"));
        String[] notNullColumns = analysis.notNullColumns().toArray(new String[0]);
        assertThat(notNullColumns.length, Matchers.is(1));
        assertThat(notNullColumns[0], Matchers.is("name"));
    }

    @Test
    public void testCreateTableWithDefaultNumberOfShards() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, name string)");
        assertThat(analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()), Matchers.is("6"));
    }

    @Test
    public void testCreateTableWithDefaultNumberOfShardsWithClusterByClause() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key) clustered by (id)");
        assertThat(analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()), Matchers.is("6"));
    }

    @Test
    public void testCreateTableNumberOfShardsProvidedInClusteredClause() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table foo (id integer primary key) " + "clustered by (id) into 8 shards"));
        assertThat(analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()), Matchers.is("8"));
    }

    @Test
    public void testCreateTableWithTotalFieldsLimit() {
        CreateTableAnalyzedStatement analysis = e.analyze(("CREATE TABLE foo (id int primary key) " + "with (\"mapping.total_fields.limit\"=5000)"));
        assertThat(analysis.tableParameter().settings().get(MAPPING_TOTAL_FIELDS_LIMIT.getKey()), Matchers.is("5000"));
    }

    @Test
    public void testCreateTableWithRefreshInterval() {
        CreateTableAnalyzedStatement analysis = e.analyze(("CREATE TABLE foo (id int primary key, content string) " + "with (refresh_interval='5000ms')"));
        assertThat(analysis.tableParameter().settings().get(REFRESH_INTERVAL.getKey()), Matchers.is("5s"));
    }

    @Test
    public void testCreateTableWithNumberOfShardsOnWithClauseIsInvalid() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid property \"number_of_shards\" passed to [ALTER | CREATE] TABLE statement");
        e.analyze(("CREATE TABLE foo (id int primary key, content string) " + "with (number_of_shards=8)"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithRefreshIntervalWrongNumberFormat() {
        e.analyze(("CREATE TABLE foo (id int primary key, content string) " + "with (refresh_interval='1asdf')"));
    }

    @Test
    public void testAlterTableWithRefreshInterval() {
        // alter t set
        AlterTableAnalyzedStatement analysisSet = e.analyze(("ALTER TABLE user_refresh_interval " + "SET (refresh_interval = '5000ms')"));
        assertEquals("5s", analysisSet.tableParameter().settings().get(REFRESH_INTERVAL.getKey()));
        // alter t reset
        AlterTableAnalyzedStatement analysisReset = e.analyze(("ALTER TABLE user_refresh_interval " + "RESET (refresh_interval)"));
        assertEquals("1s", analysisReset.tableParameter().settings().get(REFRESH_INTERVAL.getKey()));
    }

    @Test
    public void testTotalFieldsLimitCanBeUsedWithAlterTable() {
        AlterTableAnalyzedStatement analysisSet = e.analyze(("ALTER TABLE users " + "SET (\"mapping.total_fields.limit\" = \'5000\')"));
        assertEquals("5000", analysisSet.tableParameter().settings().get(MAPPING_TOTAL_FIELDS_LIMIT.getKey()));
        // Check if resetting total_fields results in default value
        AlterTableAnalyzedStatement analysisReset = e.analyze(("ALTER TABLE users " + "RESET (\"mapping.total_fields.limit\")"));
        assertEquals("1000", analysisReset.tableParameter().settings().get(MAPPING_TOTAL_FIELDS_LIMIT.getKey()));
    }

    @Test
    public void testAlterTableWithColumnPolicy() {
        AlterTableAnalyzedStatement analysisSet = e.analyze(("ALTER TABLE user_refresh_interval " + "SET (column_policy = 'strict')"));
        assertEquals(STRICT.mappingValue(), analysisSet.tableParameter().mappings().get(COLUMN_POLICY.getKey()));
    }

    @Test
    public void testAlterTableWithInvalidColumnPolicy() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid value for argument 'column_policy'");
        e.analyze(("ALTER TABLE user_refresh_interval " + "SET (column_policy = 'ignored')"));
    }

    @Test
    public void testAlterTableWithMaxNGramDiffSetting() {
        AlterTableAnalyzedStatement analysisSet = e.analyze(("ALTER TABLE users " + "SET (max_ngram_diff = 42)"));
        assertThat(analysisSet.tableParameter().settings().get(MAX_NGRAM_DIFF.getKey()), Matchers.is("42"));
    }

    @Test
    public void testAlterTableWithMaxShingleDiffSetting() {
        AlterTableAnalyzedStatement analysisSet = e.analyze(("ALTER TABLE users " + "SET (max_shingle_diff = 43)"));
        assertThat(analysisSet.tableParameter().settings().get(MAX_SHINGLE_DIFF.getKey()), Matchers.is("43"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithClusteredBy() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer, name string) clustered by(id)");
        Map<String, Object> meta = ((Map) (analysis.mapping().get("_meta")));
        assertNotNull(meta);
        assertThat(meta.get("routing"), Matchers.is("id"));
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testCreateTableWithClusteredByNotInPrimaryKeys() {
        e.analyze("create table foo (id integer primary key, name string) clustered by(name)");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithObjects() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, details object as (name string, age integer))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> details = ((Map<String, Object>) (mappingProperties.get("details")));
        assertThat(details.get("type"), Matchers.is("object"));
        assertThat(details.get("dynamic"), Matchers.is("true"));
        Map<String, Object> detailsProperties = ((Map<String, Object>) (details.get("properties")));
        Map<String, Object> nameProperties = ((Map<String, Object>) (detailsProperties.get("name")));
        assertThat(nameProperties.get("type"), Matchers.is("keyword"));
        Map<String, Object> ageProperties = ((Map<String, Object>) (detailsProperties.get("age")));
        assertThat(ageProperties.get("type"), Matchers.is("integer"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithStrictObject() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, details object(strict) as (name string, age integer))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> details = ((Map<String, Object>) (mappingProperties.get("details")));
        assertThat(details.get("type"), Matchers.is("object"));
        assertThat(details.get("dynamic"), Matchers.is("strict"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithIgnoredObject() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, details object(ignored))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> details = ((Map<String, Object>) (mappingProperties.get("details")));
        assertThat(details.get("type"), Matchers.is("object"));
        assertThat(details.get("dynamic"), Matchers.is("false"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithSubscriptInFulltextIndexDefinition() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table1g (" + ((((("title string, " + "author object(dynamic) as ( ") + "name string, ") + "birthday timestamp ") + "), ") + "INDEX author_title_ft using fulltext(title, author['name']))")));
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> details = ((Map<String, Object>) (mappingProperties.get("author")));
        Map<String, Object> nameMapping = ((Map<String, Object>) (((Map<String, Object>) (details.get("properties"))).get("name")));
        assertThat(((List<String>) (nameMapping.get("copy_to"))).get(0), Matchers.is("author_title_ft"));
    }

    @Test(expected = ColumnUnknownException.class)
    public void testCreateTableWithInvalidFulltextIndexDefinition() {
        e.analyze(("create table my_table1g (" + ((((("title string, " + "author object(dynamic) as ( ") + "name string, ") + "birthday timestamp ") + "), ") + "INDEX author_title_ft using fulltext(title, author['name']['foo']['bla']))")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTableWithArray() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, details array(string))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> details = ((Map<String, Object>) (mappingProperties.get("details")));
        assertThat(details.get("type"), Matchers.is("array"));
        Map<String, Object> inner = ((Map<String, Object>) (details.get("inner")));
        assertThat(inner.get("type"), Matchers.is("keyword"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithObjectsArray() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, details array(object as (name string, age integer, tags array(string))))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        assertThat(TestingHelpers.mapToSortedString(mappingProperties), Matchers.is(("details={inner={dynamic=true, properties={age={type=integer}, name={type=keyword}, " + "tags={inner={type=keyword}, type=array}}, type=object}, type=array}, id={type=integer}")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithAnalyzer() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, content string INDEX using fulltext with (analyzer='german'))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> contentMapping = ((Map<String, Object>) (mappingProperties.get("content")));
        assertThat(contentMapping.get("index"), Matchers.nullValue());
        assertThat(contentMapping.get("analyzer"), Matchers.is("german"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithAnalyzerParameter() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, content string INDEX using fulltext with (analyzer=?))", new Object[]{ "german" });
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> contentMapping = ((Map<String, Object>) (mappingProperties.get("content")));
        assertThat(contentMapping.get("index"), Matchers.nullValue());
        assertThat(contentMapping.get("analyzer"), Matchers.is("german"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void textCreateTableWithCustomAnalyzerInNestedColumn() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table ft_search (" + ((("\"user\" object (strict) as (" + "name string index using fulltext with (analyzer='ft_search') ") + ")") + ")")));
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> details = ((Map<String, Object>) (mappingProperties.get("user")));
        Map<String, Object> nameMapping = ((Map<String, Object>) (((Map<String, Object>) (details.get("properties"))).get("name")));
        assertThat(nameMapping.get("index"), Matchers.nullValue());
        assertThat(nameMapping.get("analyzer"), Matchers.is("ft_search"));
        assertThat(analysis.tableParameter().settings().get("search"), Matchers.is("foobar"));
    }

    @Test
    public void testCreateTableWithSchemaName() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table something.foo (id integer primary key)");
        RelationName relationName = analysis.tableIdent();
        assertThat(relationName.schema(), Matchers.is("something"));
        assertThat(relationName.name(), Matchers.is("foo"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithIndexColumn() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, content string, INDEX content_ft using fulltext (content))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> contentMapping = ((Map<String, Object>) (mappingProperties.get("content")));
        assertThat(((String) (contentMapping.get("index"))), Matchers.isEmptyOrNullString());
        assertThat(((List<String>) (contentMapping.get("copy_to"))).get(0), Matchers.is("content_ft"));
        Map<String, Object> ft_mapping = ((Map<String, Object>) (mappingProperties.get("content_ft")));
        assertThat(ft_mapping.get("index"), Matchers.nullValue());
        assertThat(ft_mapping.get("analyzer"), Matchers.is("standard"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTableWithPlainIndexColumn() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (id integer primary key, content string, INDEX content_ft using plain (content))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> contentMapping = ((Map<String, Object>) (mappingProperties.get("content")));
        assertThat(((String) (contentMapping.get("index"))), Matchers.isEmptyOrNullString());
        assertThat(((List<String>) (contentMapping.get("copy_to"))).get(0), Matchers.is("content_ft"));
        Map<String, Object> ft_mapping = ((Map<String, Object>) (mappingProperties.get("content_ft")));
        assertThat(ft_mapping.get("index"), Matchers.nullValue());
        assertThat(ft_mapping.get("analyzer"), Matchers.is("keyword"));
    }

    @Test
    public void testCreateTableWithIndexColumnOverNonString() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("INDEX definition only support 'string' typed source columns");
        e.analyze("create table foo (id integer, id2 integer, INDEX id_ft using fulltext (id, id2))");
    }

    @Test
    public void testCreateTableWithIndexColumnOverNonString2() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("INDEX definition only support 'string' typed source columns");
        e.analyze("create table foo (id integer, name string, INDEX id_ft using fulltext (id, name))");
    }

    @Test
    public void testChangeNumberOfReplicas() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (number_of_replicas=2)");
        assertThat(analysis.table().ident().name(), Matchers.is("users"));
        assertThat(analysis.tableParameter().settings().get(INDEX_NUMBER_OF_REPLICAS_SETTING.getKey()), Matchers.is("2"));
    }

    @Test
    public void testResetNumberOfReplicas() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users reset (number_of_replicas)");
        assertThat(analysis.table().ident().name(), Matchers.is("users"));
        assertThat(analysis.tableParameter().settings().get(INDEX_NUMBER_OF_REPLICAS_SETTING.getKey()), Matchers.is("0"));
        assertThat(analysis.tableParameter().settings().get(SETTING.getKey()), Matchers.is("0-1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlterTableWithInvalidProperty() {
        e.analyze("alter table users set (foobar='2')");
    }

    @Test
    public void testAlterSystemTable() {
        expectedException.expect(OperationOnInaccessibleRelationException.class);
        expectedException.expectMessage(("The relation \"sys.shards\" doesn\'t support or allow ALTER " + "operations, as it is read-only."));
        e.analyze("alter table sys.shards reset (number_of_replicas)");
    }

    @Test
    public void testCreateTableWithMultiplePrimaryKeys() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table test (id integer primary key, name string primary key)");
        String[] primaryKeys = analysis.primaryKeys().toArray(new String[0]);
        assertThat(primaryKeys.length, Matchers.is(2));
        assertThat(primaryKeys[0], Matchers.is("id"));
        assertThat(primaryKeys[1], Matchers.is("name"));
    }

    @Test
    public void testCreateTableWithMultiplePrimaryKeysAndClusteredBy() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table test (id integer primary key, name string primary key) " + "clustered by(name)"));
        String[] primaryKeys = analysis.primaryKeys().toArray(new String[0]);
        assertThat(primaryKeys.length, Matchers.is(2));
        assertThat(primaryKeys[0], Matchers.is("id"));
        assertThat(primaryKeys[1], Matchers.is("name"));
        // noinspection unchecked
        Map<String, Object> meta = ((Map) (analysis.mapping().get("_meta")));
        assertNotNull(meta);
        assertThat(meta.get("routing"), Matchers.is("name"));
    }

    @Test
    public void testCreateTableWithObjectAndUnderscoreColumnPrefix() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table test (o object as (_id integer), name string)");
        assertThat(analysis.analyzedTableElements().columns().size(), Matchers.is(2));// id pk column is also added

        AnalyzedColumnDefinition column = analysis.analyzedTableElements().columns().get(0);
        assertEquals(column.ident(), new ColumnIdent("o"));
        assertThat(column.children().size(), Matchers.is(1));
        AnalyzedColumnDefinition xColumn = column.children().get(0);
        assertEquals(xColumn.ident(), new ColumnIdent("o", Collections.singletonList("_id")));
    }

    @Test(expected = InvalidColumnNameException.class)
    public void testCreateTableWithUnderscoreColumnPrefix() {
        e.analyze("create table test (_id integer, name string)");
    }

    @Test(expected = ParsingException.class)
    public void testCreateTableWithColumnDot() {
        e.analyze("create table test (dot.column integer)");
    }

    @Test(expected = InvalidRelationName.class)
    public void testCreateTableIllegalTableName() {
        e.analyze("create table \"abc.def\" (id integer primary key, name string)");
    }

    @Test
    public void testTableStartWithUnderscore() {
        expectedException.expect(InvalidRelationName.class);
        expectedException.expectMessage("Relation name \"doc._invalid\" is invalid.");
        e.analyze("create table _invalid (id integer primary key)");
    }

    @Test
    public void testHasColumnDefinition() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table (" + ((((((((("  id integer primary key, " + "  name string, ") + "  indexed string index using fulltext with (analyzer='german'),") + "  arr array(object as(") + "    nested float,") + "    nested_object object as (id byte)") + "  )),") + "  obj object as ( content string ),") + "  index ft using fulltext(name, obj['content']) with (analyzer='standard')") + ")")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("id")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("name")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("indexed")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("arr")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("arr.nested")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("arr.nested_object.id")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("obj")));
        assertTrue(analysis.hasColumnDefinition(ColumnIdent.fromPath("obj.content")));
        assertFalse(analysis.hasColumnDefinition(ColumnIdent.fromPath("arr.nested.wrong")));
        assertFalse(analysis.hasColumnDefinition(ColumnIdent.fromPath("ft")));
        assertFalse(analysis.hasColumnDefinition(ColumnIdent.fromPath("obj.content.ft")));
    }

    @Test
    public void testCreateTableWithGeoPoint() {
        CreateTableAnalyzedStatement analyze = e.analyze(("create table geo_point_table (\n" + (("    id integer primary key,\n" + "    my_point geo_point\n") + ")\n")));
        Map my_point = ((Map) (analyze.mappingProperties().get("my_point")));
        assertEquals("geo_point", my_point.get("type"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClusteredIntoZeroShards() {
        e.analyze(("create table my_table (" + (("  id integer," + "  name string") + ") clustered into 0 shards")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlobTableClusteredIntoZeroShards() {
        e.analyze(("create blob table my_table " + "clustered into 0 shards"));
    }

    @Test
    public void testEarlyPrimaryKeyConstraint() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table (" + ((("primary key (id1, id2)," + "id1 integer,") + "id2 long") + ")")));
        assertThat(analysis.primaryKeys().size(), Matchers.is(2));
        assertThat(analysis.primaryKeys(), Matchers.hasItems("id1", "id2"));
    }

    @Test(expected = ColumnUnknownException.class)
    public void testPrimaryKeyConstraintNonExistingColumns() {
        e.analyze(("create table my_table (" + ((("primary key (id1, id2)," + "title string,") + "name string") + ")")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEarlyIndexDefinition() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table my_table (" + ((("index ft using fulltext(title, name) with (analyzer='snowball')," + "title string,") + "name string") + ")")));
        Map<String, Object> metaMap = ((Map) (analysis.mapping().get("_meta")));
        assertThat(metaMap.get("indices").toString(), Matchers.is("{ft={}}"));
        assertThat(((List<String>) (((Map<String, Object>) (analysis.mappingProperties().get("title"))).get("copy_to"))), Matchers.hasItem("ft"));
        assertThat(((List<String>) (((Map<String, Object>) (analysis.mappingProperties().get("name"))).get("copy_to"))), Matchers.hasItem("ft"));
    }

    @Test(expected = ColumnUnknownException.class)
    public void testIndexDefinitionNonExistingColumns() {
        e.analyze(("create table my_table (" + ((("index ft using fulltext(id1, id2) with (analyzer='snowball')," + "title string,") + "name string") + ")")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzerOnInvalidType() {
        e.analyze("create table my_table (x integer INDEX using fulltext with (analyzer='snowball'))");
    }

    @Test
    public void createTableNegativeReplicas() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Failed to parse value [-1] for setting [number_of_replicas] must be >= 0");
        e.analyze("create table t (id int, name string) with (number_of_replicas=-1)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableSameColumn() {
        e.analyze("create table my_table (title string, title integer)");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateTableWithArrayPrimaryKeyUnsupported() {
        e.analyze("create table t (id array(int) primary key)");
    }

    @Test
    public void testCreateTableWithClusteredIntoShardsParameter() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table t (id int primary key) clustered into ? shards", new Object[]{ 2 });
        assertThat(analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()), Matchers.is("2"));
    }

    @Test
    public void testCreateTableWithClusteredIntoShardsParameterNonNumeric() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid number 'foo'");
        e.analyze("create table t (id int primary key) clustered into ? shards", new Object[]{ "foo" });
    }

    @Test
    public void testCreateTableWithParitionedColumnInClusteredBy() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot use CLUSTERED BY column in PARTITIONED BY clause");
        e.analyze("create table t(id int primary key) partitioned by (id) clustered by (id)");
    }

    @Test
    public void testCreateTableUsesDefaultSchema() {
        SQLExecutor sqlExecutor = SQLExecutor.builder(clusterService, 1, Randomness.get()).setSearchPath("firstSchema", "secondSchema").build();
        CreateTableAnalyzedStatement analysis = sqlExecutor.analyze("create table t (id int)");
        assertThat(analysis.tableIdent().schema(), Matchers.is(sqlExecutor.getSessionContext().searchPath().currentSchema()));
    }

    @Test
    public void testCreateTableWithEmptySchema() {
        expectedException.expect(InvalidSchemaNameException.class);
        expectedException.expectMessage("schema name \"\" is invalid.");
        e.analyze(("create table \"\".my_table (" + ("id long primary key" + ")")));
    }

    @Test
    public void testCreateTableWithIllegalSchema() {
        expectedException.expect(InvalidSchemaNameException.class);
        expectedException.expectMessage("schema name \"with.\" is invalid.");
        e.analyze(("create table \"with.\".my_table (" + ("id long primary key" + ")")));
    }

    @Test
    public void testCreateTableWithInvalidColumnName() {
        expectedException.expect(InvalidColumnNameException.class);
        expectedException.expectMessage("\"_test\" conflicts with system column pattern");
        e.analyze("create table my_table (\"_test\" string)");
    }

    @Test
    public void testCreateTableShouldRaiseErrorIfItExists() {
        expectedException.expect(RelationAlreadyExists.class);
        e.analyze("create table users (\"\'test\" string)");
    }

    @Test
    public void testExplicitSchemaHasPrecedenceOverDefaultSchema() {
        CreateTableAnalyzedStatement statement = ((CreateTableAnalyzedStatement) (e.analyzer.boundAnalyze(SqlParser.createStatement("create table foo.bar (x string)"), new CoordinatorTxnCtx(new io.crate.action.sql.SessionContext(0, Option.NONE, User.CRATE_USER, ( s) -> {
        }, ( t) -> {
        }, "hoschi")), new ParameterContext(Row.EMPTY, Collections.emptyList())).analyzedStatement()));
        // schema from statement must take precedence
        assertThat(statement.tableIdent().schema(), Matchers.is("foo"));
    }

    @Test
    public void testDefaultSchemaIsAddedToTableIdentIfNoEplicitSchemaExistsInTheStatement() {
        CreateTableAnalyzedStatement statement = ((CreateTableAnalyzedStatement) (e.analyzer.boundAnalyze(SqlParser.createStatement("create table bar (x string)"), new CoordinatorTxnCtx(new io.crate.action.sql.SessionContext(0, Option.NONE, User.CRATE_USER, ( s) -> {
        }, ( t) -> {
        }, "hoschi")), new ParameterContext(Row.EMPTY, Collections.emptyList())).analyzedStatement()));
        assertThat(statement.tableIdent().schema(), Matchers.is("hoschi"));
    }

    @Test
    public void testChangeReadBlock() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"blocks.read\"=true)");
        assertThat(analysis.tableParameter().settings().get(BLOCKS_READ.getKey()), Matchers.is("true"));
    }

    @Test
    public void testChangeWriteBlock() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"blocks.write\"=true)");
        assertThat(analysis.tableParameter().settings().get(BLOCKS_WRITE.getKey()), Matchers.is("true"));
    }

    @Test
    public void testChangeMetadataBlock() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"blocks.metadata\"=true)");
        assertThat(analysis.tableParameter().settings().get(BLOCKS_METADATA.getKey()), Matchers.is("true"));
    }

    @Test
    public void testChangeReadOnlyBlock() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"blocks.read_only\"=true)");
        assertThat(analysis.tableParameter().settings().get(READ_ONLY.getKey()), Matchers.is("true"));
    }

    @Test
    public void testChangeBlockReadOnlyAllowDelete() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"blocks.read_only_allow_delete\"=true)");
        assertThat(analysis.tableParameter().settings().get(READ_ONLY_ALLOW_DELETE.getKey()), Matchers.is("true"));
    }

    @Test
    public void testChangeBlockReadOnlyAllowedDeletePartitionedTable() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table parted set (\"blocks.read_only_allow_delete\"=true)");
        assertThat(analysis.tableParameter().settings().get(READ_ONLY_ALLOW_DELETE.getKey()), Matchers.is("true"));
    }

    @Test
    public void testChangeFlushThresholdSize() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"translog.flush_threshold_size\"=\'300b\')");
        assertThat(analysis.tableParameter().settings().get(FLUSH_THRESHOLD_SIZE.getKey()), Matchers.is("300b"));
    }

    @Test
    public void testChangeTranslogInterval() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"translog.sync_interval\"=\'100ms\')");
        assertThat(analysis.tableParameter().settings().get(TRANSLOG_SYNC_INTERVAL.getKey()), Matchers.is("100ms"));
    }

    @Test
    public void testChangeTranslogDurability() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"translog.durability\"=\'ASYNC\')");
        assertThat(analysis.tableParameter().settings().get(TRANSLOG_DURABILITY.getKey()), Matchers.is("ASYNC"));
    }

    @Test
    public void testRoutingAllocationEnable() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"routing.allocation.enable\"=\"none\")");
        assertThat(analysis.tableParameter().settings().get(ROUTING_ALLOCATION_ENABLE.getKey()), Matchers.is("none"));
    }

    @Test
    public void testRoutingAllocationValidation() {
        expectedException.expect(IllegalArgumentException.class);
        e.analyze("alter table users set (\"routing.allocation.enable\"=\"foo\")");
    }

    @Test
    public void testAlterTableSetShards() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"number_of_shards\"=1)");
        assertThat(analysis.table().ident().name(), Matchers.is("users"));
        assertThat(analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()), Matchers.is("1"));
    }

    @Test
    public void testAlterTableResetShards() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users reset (\"number_of_shards\")");
        assertThat(analysis.table().ident().name(), Matchers.is("users"));
        assertThat(analysis.tableParameter().settings().get(NUMBER_OF_SHARDS.getKey()), Matchers.is("5"));
    }

    @Test
    public void testTranslogSyncInterval() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"translog.sync_interval\"=\'1s\')");
        assertThat(analysis.table().ident().name(), Matchers.is("users"));
        assertThat(analysis.tableParameter().settings().get(TRANSLOG_SYNC_INTERVAL.getKey()), Matchers.is("1s"));
    }

    @Test
    public void testAllocationMaxRetriesValidation() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"allocation.max_retries\"=1)");
        assertThat(analysis.tableParameter().settings().get(ALLOCATION_MAX_RETRIES.getKey()), Matchers.is("1"));
    }

    @Test
    public void testCreateReadOnlyTable() {
        CreateTableAnalyzedStatement analysis = e.analyze(("create table foo (id integer primary key, name string) " + "clustered into 3 shards with (\"blocks.read_only\"=true)"));
        assertThat(analysis.tableParameter().settings().get(READ_ONLY.getKey()), Matchers.is("true"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTableWithGeneratedColumn() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (ts timestamp, day as date_trunc('day', ts))");
        Map<String, Object> metaMapping = ((Map) (analysis.mapping().get("_meta")));
        Map<String, String> generatedColumnsMapping = ((Map<String, String>) (metaMapping.get("generated_columns")));
        assertThat(generatedColumnsMapping.size(), Matchers.is(1));
        assertThat(generatedColumnsMapping.get("day"), Matchers.is("date_trunc('day', ts)"));
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> dayMapping = ((Map<String, Object>) (mappingProperties.get("day")));
        assertThat(dayMapping.get("type"), Matchers.is("date"));
        Map<String, Object> tsMapping = ((Map<String, Object>) (mappingProperties.get("ts")));
        assertThat(tsMapping.get("type"), Matchers.is("date"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTableGeneratedColumnWithCast() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (ts timestamp, day timestamp GENERATED ALWAYS as ts + 1)");
        Map<String, Object> metaMapping = ((Map) (analysis.mapping().get("_meta")));
        Map<String, String> generatedColumnsMapping = ((Map<String, String>) (metaMapping.get("generated_columns")));
        assertThat(generatedColumnsMapping.get("day"), Matchers.is("cast((cast(ts AS long) + 1) AS timestamp)"));
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        Map<String, Object> dayMapping = ((Map<String, Object>) (mappingProperties.get("day")));
        assertThat(dayMapping.get("type"), Matchers.is("date"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTableWithCurrentTimestampAsGeneratedColumnIsntNormalized() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (ts timestamp GENERATED ALWAYS as current_timestamp)");
        Map<String, Object> metaMapping = ((Map) (analysis.mapping().get("_meta")));
        Map<String, String> generatedColumnsMapping = ((Map<String, String>) (metaMapping.get("generated_columns")));
        assertThat(generatedColumnsMapping.size(), Matchers.is(1));
        // current_timestamp used to get evaluated and then this contained the actual timestamp instead of the function name
        assertThat(generatedColumnsMapping.get("ts"), Matchers.is("current_timestamp(3)"));// 3 is the default precision

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTableGeneratedColumnWithSubscript() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (\"user\" object as (name string), name as concat(\"user\"[\'name\'], \'foo\'))");
        Map<String, Object> metaMapping = ((Map) (analysis.mapping().get("_meta")));
        Map<String, String> generatedColumnsMapping = ((Map<String, String>) (metaMapping.get("generated_columns")));
        assertThat(generatedColumnsMapping.get("name"), Matchers.is("concat(\"user\"[\'name\'], \'foo\')"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTableGeneratedColumnParameter() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table foo (\"user\" object as (name string), name as concat(\"user\"[\'name\'], ?))", RandomizedTest.$("foo"));
        Map<String, Object> metaMapping = ((Map) (analysis.mapping().get("_meta")));
        Map<String, String> generatedColumnsMapping = ((Map<String, String>) (metaMapping.get("generated_columns")));
        assertThat(generatedColumnsMapping.get("name"), Matchers.is("concat(\"user\"[\'name\'], \'foo\')"));
    }

    @Test
    public void testCreateTableGeneratedColumnWithInvalidType() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("generated expression value type 'timestamp' not supported for conversion to 'ip'");
        e.analyze("create table foo (ts timestamp, day ip GENERATED ALWAYS as date_trunc('day', ts))");
    }

    @Test
    public void testCreateTableGeneratedColumnWithMatch() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("can only MATCH on columns, not on name");
        e.analyze("create table foo (name string, bar as match(name, 'crate'))");
    }

    @Test
    public void testCreateTableGeneratedColumnBasedOnGeneratedColumn() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("A generated column cannot be based on a generated column");
        e.analyze("create table foo (ts timestamp, day as date_trunc('day', ts), date_string as cast(day as string))");
    }

    @Test
    public void testCreateTableGeneratedColumnBasedOnUnknownColumn() {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column unknown_col unknown");
        e.analyze("create table foo (ts timestamp, day as date_trunc('day', ts), date_string as cast(unknown_col as string))");
    }

    @Test
    public void testCreateTableWithObjectAsPrimaryKey() {
        expectedException.expectMessage("Cannot use columns of type \"object\" as primary key");
        expectedException.expect(UnsupportedOperationException.class);
        e.analyze("create table t (obj object as (x int) primary key)");
    }

    @Test
    public void testCreateTableWithGeoPointAsPrimaryKey() {
        expectedException.expectMessage("Cannot use columns of type \"geo_point\" as primary key");
        expectedException.expect(UnsupportedOperationException.class);
        e.analyze("create table t (c geo_point primary key)");
    }

    @Test
    public void testCreateTableWithGeoShapeAsPrimaryKey() {
        expectedException.expectMessage("Cannot use columns of type \"geo_shape\" as primary key");
        expectedException.expect(UnsupportedOperationException.class);
        e.analyze("create table t (c geo_shape primary key)");
    }

    @Test
    public void testCreateTableWithDuplicatePrimaryKey() {
        assertDuplicatePrimaryKey("create table t (id int, primary key (id, id))");
        assertDuplicatePrimaryKey("create table t (obj object as (id int), primary key (obj['id'], obj['id']))");
        assertDuplicatePrimaryKey("create table t (id int primary key, primary key (id))");
        assertDuplicatePrimaryKey("create table t (obj object as (id int primary key), primary key (obj['id']))");
    }

    @Test
    public void testCreateTableWithPrimaryKeyConstraintInArrayItem() {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot use column \"id\" as primary key within an array object");
        e.analyze("create table test (arr array(object as (id long primary key)))");
    }

    @Test
    public void testCreateTableWithDeepNestedPrimaryKeyConstraintInArrayItem() {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot use column \"name\" as primary key within an array object");
        e.analyze("create table test (arr array(object as (\"user\" object as (name string primary key), id long)))");
    }

    @Test
    public void testCreateTableWithInvalidIndexConstraint() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("INDEX constraint cannot be used on columns of type \"object\"");
        e.analyze("create table test (obj object index off)");
    }

    @Test
    public void testCreateTableWithColumnStoreDisabled() {
        CreateTableAnalyzedStatement analysis = e.analyze("create table columnstore_disabled (s string STORAGE WITH (columnstore = false))");
        Map<String, Object> mappingProperties = analysis.mappingProperties();
        assertThat(TestingHelpers.mapToSortedString(mappingProperties), Matchers.is("s={doc_values=false, type=keyword}"));
    }

    @Test
    public void testCreateTableWithColumnStoreDisabledOnInvalidDataType() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid storage option \"columnstore\" for data type \"integer\"");
        e.analyze("create table columnstore_disabled (s int STORAGE WITH (columnstore = false))");
    }

    @Test
    public void testCreateTableFailsIfNameConflictsWithView() {
        SQLExecutor executor = SQLExecutor.builder(clusterService).addView(RelationName.fromIndexName("v1"), "Select * from t1").build();
        expectedException.expect(RelationAlreadyExists.class);
        expectedException.expectMessage("Relation 'doc.v1' already exists");
        executor.analyze("create table v1 (x int) clustered into 1 shards with (number_of_replicas = 0)");
    }

    @Test
    public void testGeneratedColumnInsideObjectIsProcessed() {
        CreateTableAnalyzedStatement stmt = e.analyze("create table t (obj object as (c as 1 + 1))");
        AnalyzedColumnDefinition obj = stmt.analyzedTableElements().columns().get(0);
        AnalyzedColumnDefinition c = obj.children().get(0);
        assertThat(c.dataType(), Matchers.is(LONG));
        assertThat(c.formattedGeneratedExpression(), Matchers.is("2"));
        assertThat(stmt.analyzedTableElements().toMapping().toString(), Matchers.is(("{_meta={generated_columns={obj.c=2}}, " + "properties={obj={dynamic=true, type=object, properties={c={type=long}}}}}")));
    }

    @Test
    public void testNumberOfRoutingShardsCanBeSetAtCreateTable() {
        CreateTableAnalyzedStatement stmt = e.analyze("create table t (x int) with (number_of_routing_shards = 10)");
        assertThat(stmt.tableParameter().settings().get("index.number_of_routing_shards"), Matchers.is("10"));
    }

    @Test
    public void testNumberOfRoutingShardsCanBeSetAtCreateTableForPartitionedTables() {
        CreateTableAnalyzedStatement stmt = e.analyze(("create table t (p int, x int) partitioned by (p) " + "with (number_of_routing_shards = 10)"));
        assertThat(stmt.tableParameter().settings().get("index.number_of_routing_shards"), Matchers.is("10"));
    }

    @Test
    public void testAlterTableSetDynamicSetting() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users set (\"routing.allocation.exclude.foo\"=\'bar\')");
        assertThat(analysis.tableParameter().settings().get(((INDEX_ROUTING_EXCLUDE_GROUP_SETTING.getKey()) + "foo")), Matchers.is("bar"));
    }

    @Test
    public void testAlterTableResetDynamicSetting() {
        AlterTableAnalyzedStatement analysis = e.analyze("alter table users reset (\"routing.allocation.exclude.foo\")");
        assertThat(analysis.tableParameter().settings().get(((INDEX_ROUTING_EXCLUDE_GROUP_SETTING.getKey()) + "foo")), Matchers.nullValue());
    }
}

