/**
 * Licensed to Crate.io Inc. or its affiliates ("Crate.io") under one or
 * more contributor license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Crate.io licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * However, if you have executed another commercial license agreement with
 * Crate.io these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.sql.parser;


import ArrayComparisonExpression.Quantifier.ALL;
import ArrayComparisonExpression.Quantifier.ANY;
import SetStatement.Scope.LICENSE;
import SetStatement.SettingType.PERSISTENT;
import io.crate.sql.Literals;
import io.crate.sql.tree.ArrayComparisonExpression;
import io.crate.sql.tree.ArrayLikePredicate;
import io.crate.sql.tree.ArrayLiteral;
import io.crate.sql.tree.Assignment;
import io.crate.sql.tree.DeallocateStatement;
import io.crate.sql.tree.EscapedCharStringLiteral;
import io.crate.sql.tree.Expression;
import io.crate.sql.tree.InsertFromValues;
import io.crate.sql.tree.KillStatement;
import io.crate.sql.tree.LongLiteral;
import io.crate.sql.tree.MatchPredicate;
import io.crate.sql.tree.NegativeExpression;
import io.crate.sql.tree.ObjectLiteral;
import io.crate.sql.tree.ParameterExpression;
import io.crate.sql.tree.QualifiedName;
import io.crate.sql.tree.QualifiedNameReference;
import io.crate.sql.tree.SetStatement;
import io.crate.sql.tree.ShowCreateTable;
import io.crate.sql.tree.Statement;
import io.crate.sql.tree.StringLiteral;
import io.crate.sql.tree.SubqueryExpression;
import io.crate.sql.tree.SubscriptExpression;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestStatementBuilder {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testBegin() {
        TestStatementBuilder.printStatement("BEGIN");
        TestStatementBuilder.printStatement("BEGIN WORK");
        TestStatementBuilder.printStatement("BEGIN WORK DEFERRABLE");
        TestStatementBuilder.printStatement("BEGIN TRANSACTION");
        TestStatementBuilder.printStatement("BEGIN TRANSACTION DEFERRABLE");
        TestStatementBuilder.printStatement(("BEGIN ISOLATION LEVEL SERIALIZABLE, " + ("      READ WRITE," + "      NOT DEFERRABLE")));
    }

    @Test
    public void testEmptyOverClauseAfterFunction() {
        TestStatementBuilder.printStatement("SELECT avg(x) OVER () FROM t");
    }

    @Test
    public void testOverClauseWithOrderBy() {
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (ORDER BY x) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (ORDER BY x DESC) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (ORDER BY x DESC NULLS FIRST) FROM t");
    }

    @Test
    public void testOverClauseWithPartition() {
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (PARTITION BY p) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (PARTITION BY p1, p2, p3) FROM t");
    }

    @Test
    public void testOverClauseWithPartitionAndOrderBy() {
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (PARTITION BY p ORDER BY x ASC) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (PARTITION BY p1, p2, p3 ORDER BY x, y) FROM t");
    }

    @Test
    public void testOverClauseWithFrameClause() {
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (ROWS BETWEEN 5 PRECEDING AND 10 FOLLOWING) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (RANGE BETWEEN 5 PRECEDING AND 10 FOLLOWING) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (ROWS UNBOUND PRECEDING) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (ROWS BETWEEN UNBOUND PRECEDING AND CURRENT ROW) FROM t");
        TestStatementBuilder.printStatement("SELECT avg(x) OVER (ROWS BETWEEN 10 PRECEDING AND UNBOUND FOLLOWING) FROM t");
    }

    @Test
    public void testCommit() {
        TestStatementBuilder.printStatement("COMMIT");
    }

    @Test
    public void testNullNotAllowedAsArgToExtractField() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage("viable alternative at input 'select extract(null'");
        TestStatementBuilder.printStatement("select extract(null from x)");
    }

    @Test
    public void testShowCreateTableStmtBuilder() {
        TestStatementBuilder.printStatement("show create table test");
        TestStatementBuilder.printStatement("show create table foo.test");
        TestStatementBuilder.printStatement("show create table \"select\"");
    }

    @Test
    public void testDropTableStmtBuilder() {
        TestStatementBuilder.printStatement("drop table test");
        TestStatementBuilder.printStatement("drop table if exists test");
        TestStatementBuilder.printStatement("drop table bar.foo");
    }

    @Test
    public void testSwapTable() {
        TestStatementBuilder.printStatement("ALTER CLUSTER SWAP TABLE t1 TO t2");
        TestStatementBuilder.printStatement("ALTER CLUSTER SWAP TABLE t1 TO t2 WITH (prune_second = true)");
    }

    @Test
    public void testAlterClusterGCDanglingArtifacts() {
        TestStatementBuilder.printStatement("ALTER CLUSTER GC DANGLING ARTIFACTS");
    }

    @Test
    public void testAlterClusterDecommissionNode() {
        TestStatementBuilder.printStatement("ALTER CLUSTER DECOMMISSION 'node1'");
    }

    @Test
    public void testStmtWithSemicolonBuilder() {
        TestStatementBuilder.printStatement("select 1;");
    }

    @Test
    public void testShowTablesStmtBuilder() {
        TestStatementBuilder.printStatement("show tables");
        TestStatementBuilder.printStatement("show tables like '.*'");
        TestStatementBuilder.printStatement("show tables from table_schema");
        TestStatementBuilder.printStatement("show tables from \"tableSchema\"");
        TestStatementBuilder.printStatement("show tables in table_schema");
        TestStatementBuilder.printStatement("show tables from foo like '.*'");
        TestStatementBuilder.printStatement("show tables in foo like '.*'");
        TestStatementBuilder.printStatement("show tables from table_schema like '.*'");
        TestStatementBuilder.printStatement("show tables in table_schema like '*'");
        TestStatementBuilder.printStatement("show tables in table_schema where name = 'foo'");
        TestStatementBuilder.printStatement("show tables in table_schema where name > 'foo'");
        TestStatementBuilder.printStatement("show tables in table_schema where name != 'foo'");
    }

    @Test
    public void testShowColumnsStmtBuilder() {
        TestStatementBuilder.printStatement("show columns from table_name");
        TestStatementBuilder.printStatement("show columns in table_name");
        TestStatementBuilder.printStatement("show columns from table_name from table_schema");
        TestStatementBuilder.printStatement("show columns in table_name from table_schema");
        TestStatementBuilder.printStatement("show columns in foo like '*'");
        TestStatementBuilder.printStatement("show columns from foo like '*'");
        TestStatementBuilder.printStatement("show columns from table_name from table_schema like '*'");
        TestStatementBuilder.printStatement("show columns in table_name from table_schema like '*'");
        TestStatementBuilder.printStatement("show columns from table_name where column_name = 'foo'");
        TestStatementBuilder.printStatement("show columns from table_name from table_schema where column_name = 'foo'");
    }

    @Test
    public void testDeleteFromStmtBuilder() {
        TestStatementBuilder.printStatement("delete from foo as alias");
        TestStatementBuilder.printStatement("delete from foo");
        TestStatementBuilder.printStatement("delete from schemah.foo where foo.a=foo.b and a is not null");
        TestStatementBuilder.printStatement("delete from schemah.foo as alias where foo.a=foo.b and a is not null");
    }

    @Test
    public void testShowSchemasStmtBuilder() {
        TestStatementBuilder.printStatement("show schemas");
        TestStatementBuilder.printStatement("show schemas like 'doc%'");
        TestStatementBuilder.printStatement("show schemas where schema_name='doc'");
        TestStatementBuilder.printStatement("show schemas where schema_name LIKE 'd%'");
    }

    @Test
    public void testShowParameterStmtBuilder() {
        TestStatementBuilder.printStatement("show search_path");
        TestStatementBuilder.printStatement("show all");
    }

    @Test
    public void testUpdateStmtBuilder() {
        TestStatementBuilder.printStatement("update foo set \"column[\'looks_like_nested\']\"=1");
        TestStatementBuilder.printStatement("update foo set foo.a='b'");
        TestStatementBuilder.printStatement("update bar.foo set bar.foo.t=3");
        TestStatementBuilder.printStatement("update foo set col['x'] = 3");
        TestStatementBuilder.printStatement("update foo set col['x'] = 3 where foo['x'] = 2");
        TestStatementBuilder.printStatement("update schemah.foo set foo.a='b', foo.b=foo.a");
        TestStatementBuilder.printStatement("update schemah.foo set foo.a=abs(-6.3334), x=true where x=false");
    }

    @Test
    public void testExplainStmtBuilder() {
        TestStatementBuilder.printStatement("explain drop table foo");
        TestStatementBuilder.printStatement("explain analyze drop table foo");
    }

    @Test
    public void testSetStmtBuiler() throws Exception {
        TestStatementBuilder.printStatement("set session some_setting = 1, ON");
        TestStatementBuilder.printStatement("set session some_setting = false");
        TestStatementBuilder.printStatement("set session some_setting = DEFAULT");
        TestStatementBuilder.printStatement("set session some_setting = 1, 2, 3");
        TestStatementBuilder.printStatement("set session some_setting = ON");
        TestStatementBuilder.printStatement("set session some_setting = 'value'");
        TestStatementBuilder.printStatement("set session some_setting TO DEFAULT");
        TestStatementBuilder.printStatement("set session some_setting TO 'value'");
        TestStatementBuilder.printStatement("set session some_setting TO 1, 2, 3");
        TestStatementBuilder.printStatement("set session some_setting TO ON");
        TestStatementBuilder.printStatement("set session some_setting TO true");
        TestStatementBuilder.printStatement("set session some_setting TO 1, ON");
        TestStatementBuilder.printStatement("set local some_setting = DEFAULT");
        TestStatementBuilder.printStatement("set local some_setting = 'value'");
        TestStatementBuilder.printStatement("set local some_setting = 1, 2, 3");
        TestStatementBuilder.printStatement("set local some_setting = 1, ON");
        TestStatementBuilder.printStatement("set local some_setting = ON");
        TestStatementBuilder.printStatement("set local some_setting = false");
        TestStatementBuilder.printStatement("set local some_setting TO DEFAULT");
        TestStatementBuilder.printStatement("set local some_setting TO 'value'");
        TestStatementBuilder.printStatement("set local some_setting TO 1, 2, 3");
        TestStatementBuilder.printStatement("set local some_setting TO ON");
        TestStatementBuilder.printStatement("set local some_setting TO true");
        TestStatementBuilder.printStatement("set local some_setting TO ALWAYS");
        TestStatementBuilder.printStatement("set some_setting TO 1, 2, 3");
        TestStatementBuilder.printStatement("set some_setting TO ON");
        TestStatementBuilder.printStatement("set session characteristics as transaction isolation level read uncommitted");
    }

    @Test
    public void testKillStmtBuilder() {
        TestStatementBuilder.printStatement("kill all");
        TestStatementBuilder.printStatement("kill '6a3d6fb6-1401-4333-933d-b38c9322fca7'");
        TestStatementBuilder.printStatement("kill ?");
        TestStatementBuilder.printStatement("kill $1");
    }

    @Test
    public void testKillJob() {
        KillStatement stmt = ((KillStatement) (SqlParser.createStatement("KILL $1")));
        Assert.assertThat(stmt.jobId().isPresent(), Is.is(true));
    }

    @Test
    public void testKillAll() throws Exception {
        Statement stmt = SqlParser.createStatement("KILL ALL");
        Assert.assertTrue(stmt.equals(new KillStatement()));
    }

    @Test
    public void testDeallocateStmtBuilder() {
        TestStatementBuilder.printStatement("deallocate all");
        TestStatementBuilder.printStatement("deallocate prepare all");
        TestStatementBuilder.printStatement("deallocate 'myStmt'");
        TestStatementBuilder.printStatement("deallocate myStmt");
        TestStatementBuilder.printStatement("deallocate test.prep.stmt");
    }

    @Test
    public void testDeallocateWithoutParamThrowsParsingException() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage("line 1:11: no viable alternative at input '<EOF>'");
        TestStatementBuilder.printStatement("deallocate");
    }

    @Test
    public void testDeallocate() {
        DeallocateStatement stmt = ((DeallocateStatement) (SqlParser.createStatement("DEALLOCATE test_prep_stmt")));
        Assert.assertThat(stmt.preparedStmt().toString(), Is.is("'test_prep_stmt'"));
        stmt = ((DeallocateStatement) (SqlParser.createStatement("DEALLOCATE 'test_prep_stmt'")));
        Assert.assertThat(stmt.preparedStmt().toString(), Is.is("'test_prep_stmt'"));
    }

    @Test
    public void testDeallocateAll() {
        Statement stmt = SqlParser.createStatement("DEALLOCATE ALL");
        Assert.assertTrue(stmt.equals(new DeallocateStatement()));
    }

    @Test
    public void testRefreshStmtBuilder() {
        TestStatementBuilder.printStatement("refresh table t");
        TestStatementBuilder.printStatement("refresh table t partition (pcol='val'), tableh partition (pcol='val')");
        TestStatementBuilder.printStatement("refresh table schemah.tableh");
        TestStatementBuilder.printStatement("refresh table tableh partition (pcol='val')");
        TestStatementBuilder.printStatement("refresh table tableh partition (pcol=?)");
        TestStatementBuilder.printStatement("refresh table tableh partition (pcol['nested'] = ?)");
    }

    @Test
    public void testOptimize() throws Exception {
        TestStatementBuilder.printStatement("optimize table t");
        TestStatementBuilder.printStatement("optimize table t1, t2");
        TestStatementBuilder.printStatement("optimize table schema.t");
        TestStatementBuilder.printStatement("optimize table schema.t1, schema.t2");
        TestStatementBuilder.printStatement("optimize table t partition (pcol='val')");
        TestStatementBuilder.printStatement("optimize table t partition (pcol=?)");
        TestStatementBuilder.printStatement("optimize table t partition (pcol['nested'] = ?)");
        TestStatementBuilder.printStatement("optimize table t partition (pcol='val') with (param1=val1, param2=val2)");
        TestStatementBuilder.printStatement("optimize table t1 partition (pcol1='val1'), t2 partition (pcol2='val2')");
        TestStatementBuilder.printStatement(("optimize table t1 partition (pcol1='val1'), t2 partition (pcol2='val2') " + "with (param1=val1, param2=val2, param3='val3')"));
    }

    @Test
    public void testSetSessionInvalidSetting() throws Exception {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("no viable alternative"));
        TestStatementBuilder.printStatement("set session 'some_setting' TO 1, ON");
    }

    @Test
    public void testSetGlobal() throws Exception {
        TestStatementBuilder.printStatement("set global sys.cluster['some_settings']['3'] = '1'");
        TestStatementBuilder.printStatement("set global sys.cluster['some_settings'] = '1', other_setting = 2");
        TestStatementBuilder.printStatement("set global transient sys.cluster['some_settings'] = '1'");
        TestStatementBuilder.printStatement("set global persistent sys.cluster['some_settings'] = '1'");
    }

    @Test
    public void testSetLicenseStmtBuilder() throws Exception {
        TestStatementBuilder.printStatement("set license 'LICENSE_KEY'");
    }

    @Test
    public void testSetLicenseInputWithoutQuotesThrowsParsingException() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("no viable alternative at input"));
        TestStatementBuilder.printStatement("set license LICENSE_KEY");
    }

    @Test
    public void testSetLicenseWithoutParamThrowsParsingException() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("no viable alternative at input 'set license'"));
        TestStatementBuilder.printStatement("set license");
    }

    @Test
    public void testSetLicenseLikeAnExpressionThrowsParsingException() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("no viable alternative at input"));
        TestStatementBuilder.printStatement("set license key='LICENSE_KEY'");
    }

    @Test
    public void testSetLicenseMultipleInputThrowsParsingException() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("extraneous input ''LICENSE_KEY2'' expecting <EOF>"));
        TestStatementBuilder.printStatement("set license 'LICENSE_KEY' 'LICENSE_KEY2'");
    }

    @Test
    public void testSetLicense() {
        SetStatement stmt = ((SetStatement) (SqlParser.createStatement("set license 'LICENSE_KEY'")));
        Assert.assertThat(stmt.scope(), Is.is(LICENSE));
        Assert.assertThat(stmt.settingType(), Is.is(PERSISTENT));
        Assert.assertThat(stmt.assignments().size(), Is.is(1));
        Assignment assignment = stmt.assignments().get(0);
        Assert.assertThat(assignment.expressions().size(), Is.is(1));
        Assert.assertThat(assignment.expressions().get(0).toString(), Is.is("'LICENSE_KEY'"));
    }

    @Test
    public void testResetGlobalStmtBuilder() {
        TestStatementBuilder.printStatement("reset global some_setting['nested'], other_setting");
    }

    @Test
    public void testAlterTableStmtBuilder() {
        TestStatementBuilder.printStatement("alter table t add foo integer");
        TestStatementBuilder.printStatement("alter table t add foo['1']['2'] integer");
        TestStatementBuilder.printStatement("alter table t set (number_of_replicas=4)");
        TestStatementBuilder.printStatement("alter table schema.t set (number_of_replicas=4)");
        TestStatementBuilder.printStatement("alter table t reset (number_of_replicas)");
        TestStatementBuilder.printStatement("alter table t reset (property1, property2, property3)");
        TestStatementBuilder.printStatement("alter table t add foo integer");
        TestStatementBuilder.printStatement("alter table t add column foo integer");
        TestStatementBuilder.printStatement("alter table t add foo integer primary key");
        TestStatementBuilder.printStatement("alter table t add foo string index using fulltext");
        TestStatementBuilder.printStatement("alter table t add column foo['x'] integer");
        TestStatementBuilder.printStatement("alter table t add column foo integer");
        TestStatementBuilder.printStatement("alter table t add column foo['x'] integer");
        TestStatementBuilder.printStatement("alter table t add column foo['x']['y'] object as (z integer)");
        TestStatementBuilder.printStatement("alter table t partition (partitioned_col=1) set (number_of_replicas=4)");
        TestStatementBuilder.printStatement("alter table only t set (number_of_replicas=4)");
    }

    @Test
    public void testCreateTableStmtBuilder() {
        TestStatementBuilder.printStatement("create table if not exists t (id integer primary key, name string)");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string)");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) clustered into 3 shards");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) clustered into ? shards");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) clustered by (id)");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) clustered by (id) into 4 shards");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) clustered by (id) into ? shards");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) with (number_of_replicas=4)");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) with (number_of_replicas=?)");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) clustered by (id) with (number_of_replicas=4)");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) clustered by (id) into 999 shards with (number_of_replicas=4)");
        TestStatementBuilder.printStatement("create table t (id integer primary key, name string) with (number_of_replicas=-4)");
        TestStatementBuilder.printStatement("create table t (o object(dynamic) as (i integer, d double))");
        TestStatementBuilder.printStatement("create table t (id integer, name string, primary key (id))");
        TestStatementBuilder.printStatement(("create table t (" + (((((((((((("  \"_i\" integer, " + "  \"in\" int,") + "  \"Name\" string, ") + "  bo boolean,") + "  \"by\" byte,") + "  sh short,") + "  lo long,") + "  fl float,") + "  do double,") + "  \"ip_\" ip,") + "  ti timestamp,") + "  ob object") + ")")));
        TestStatementBuilder.printStatement("create table \"TABLE\" (o object(dynamic))");
        TestStatementBuilder.printStatement("create table \"TABLE\" (o object(strict))");
        TestStatementBuilder.printStatement("create table \"TABLE\" (o object(ignored))");
        TestStatementBuilder.printStatement("create table \"TABLE\" (o object(strict) as (inner_col object as (sub_inner_col timestamp, another_inner_col string)))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 timestamp not null)");
        TestStatementBuilder.printStatement("create table test (col1 int primary key not null, col2 timestamp)");
        TestStatementBuilder.printStatement(("create table t (" + (((("name string index off, " + "another string index using plain, ") + "\"full\" string index using fulltext,") + "analyzed string index using fulltext with (analyzer='german', param=?, list=[1,2,3])") + ")")));
        TestStatementBuilder.printStatement(("create table test (col1 string, col2 string," + "index \"_col1_ft\" using fulltext(col1))"));
        TestStatementBuilder.printStatement(("create table test (col1 string, col2 string," + "index col1_col2_ft using fulltext(col1, col2) with (analyzer='custom'))"));
        TestStatementBuilder.printStatement("create table test (prime long, primes array(long), unique_dates set(timestamp))");
        TestStatementBuilder.printStatement("create table test (nested set(set(array(boolean))))");
        TestStatementBuilder.printStatement("create table test (object_array array(object(dynamic) as (i integer, s set(string))))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 timestamp) partitioned by (col1)");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 timestamp) partitioned by (col1, col2)");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 timestamp) partitioned by (col1) clustered by (col2)");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 timestamp) clustered by (col2) partitioned by (col1)");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 object as (col3 timestamp)) partitioned by (col2['col3'])");
        TestStatementBuilder.printStatement("create table test (col1 string storage with (columnstore = false))");
    }

    @Test
    public void testCreateTableOptionsMultipleTimesNotAllowed() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage("mismatched input 'partitioned' expecting <EOF>");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 timestamp) partitioned by (col1) partitioned by (col2)");
    }

    @Test
    public void testBlobTable() throws Exception {
        TestStatementBuilder.printStatement("drop blob table screenshots");
        TestStatementBuilder.printStatement("create blob table screenshots");
        TestStatementBuilder.printStatement("create blob table screenshots clustered into 5 shards");
        TestStatementBuilder.printStatement("create blob table screenshots with (number_of_replicas=3)");
        TestStatementBuilder.printStatement("create blob table screenshots with (number_of_replicas='0-all')");
        TestStatementBuilder.printStatement("create blob table screenshots clustered into 5 shards with (number_of_replicas=3)");
        TestStatementBuilder.printStatement("alter blob table screenshots set (number_of_replicas=3)");
        TestStatementBuilder.printStatement("alter blob table screenshots set (number_of_replicas='0-all')");
        TestStatementBuilder.printStatement("alter blob table screenshots reset (number_of_replicas)");
    }

    @Test
    public void testCreateAnalyzerStmtBuilder() {
        TestStatementBuilder.printStatement("create analyzer myAnalyzer ( tokenizer german )");
        TestStatementBuilder.printStatement(("create analyzer my_analyzer (" + ((((((((((((((((((" token_filters (" + "   filter_1,") + "   filter_2,") + "   filter_3 WITH (") + "     \"key\"=?") + "   )") + " ),") + " tokenizer my_tokenizer WITH (") + "   property='value',") + "   property_list=['l', 'i', 's', 't']") + " ),") + " char_filters (") + "   filter_1,") + "   filter_2 WITH (") + "     key='property'") + "   ),") + "   filter_3") + " )") + ")")));
        TestStatementBuilder.printStatement(("create analyzer \"My_Builtin\" extends builtin WITH (" + ("  over='write'" + ")")));
    }

    @Test
    public void testDropAnalyzer() {
        TestStatementBuilder.printStatement("drop analyzer my_analyzer");
    }

    @Test
    public void testCreateUserStmtBuilder() {
        TestStatementBuilder.printStatement("create user \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("create user root");
        TestStatementBuilder.printStatement("create user foo with (password = 'foo')");
    }

    @Test
    public void testDropUserStmtBuilder() {
        TestStatementBuilder.printStatement("drop user \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("drop user root");
        TestStatementBuilder.printStatement("drop user if exists root");
    }

    @Test
    public void testGrantPrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("grant DML To \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("grant DQL, DDL to root");
        TestStatementBuilder.printStatement("grant DQL, DDL to root, wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL PRIVILEGES to wolfie");
        TestStatementBuilder.printStatement("grant ALL PRIVILEGES to wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL to wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL to anna");
    }

    @Test
    public void testGrantOnSchemaPrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("grant DML ON SCHEMA my_schema To \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("grant DQL, DDL ON SCHEMA my_schema to root");
        TestStatementBuilder.printStatement("grant DQL, DDL ON SCHEMA my_schema to root, wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL PRIVILEGES ON SCHEMA my_schema to wolfie");
        TestStatementBuilder.printStatement("grant ALL PRIVILEGES ON SCHEMA my_schema to wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL ON SCHEMA my_schema to wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL ON SCHEMA my_schema to anna");
        TestStatementBuilder.printStatement("grant ALL ON SCHEMA my_schema, banana, tree to anna, nyan, cat");
    }

    @Test
    public void testGrantOnTablePrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("grant DML ON TABLE my_schema.t To \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("grant DQL, DDL ON TABLE my_schema.t to root");
        TestStatementBuilder.printStatement("grant DQL, DDL ON TABLE my_schema.t to root, wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL PRIVILEGES ON TABLE my_schema.t to wolfie");
        TestStatementBuilder.printStatement("grant ALL PRIVILEGES ON TABLE my_schema.t to wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL ON TABLE my_schema.t to wolfie, anna");
        TestStatementBuilder.printStatement("grant ALL ON TABLE my_schema.t to anna");
        TestStatementBuilder.printStatement("grant ALL ON TABLE my_schema.t, banana.b, tree to anna, nyan, cat");
    }

    @Test
    public void testDenyPrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("deny DML To \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("deny DQL, DDL to root");
        TestStatementBuilder.printStatement("deny DQL, DDL to root, wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL PRIVILEGES to wolfie");
        TestStatementBuilder.printStatement("deny ALL PRIVILEGES to wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL to wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL to anna");
        TestStatementBuilder.printStatement("deny dml to anna");
        TestStatementBuilder.printStatement("deny ddl, dql to anna");
    }

    @Test
    public void testDenyOnSchemaPrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("deny DML ON SCHEMA my_schema To \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("deny DQL, DDL ON SCHEMA my_schema to root");
        TestStatementBuilder.printStatement("deny DQL, DDL ON SCHEMA my_schema to root, wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL PRIVILEGES ON SCHEMA my_schema to wolfie");
        TestStatementBuilder.printStatement("deny ALL PRIVILEGES ON SCHEMA my_schema to wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL ON SCHEMA my_schema to wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL ON SCHEMA my_schema to anna");
        TestStatementBuilder.printStatement("deny ALL ON SCHEMA my_schema, banana, tree to anna, nyan, cat");
    }

    @Test
    public void testDenyOnTablePrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("deny DML ON TABLE my_schema.t To \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("deny DQL, DDL ON TABLE my_schema.t to root");
        TestStatementBuilder.printStatement("deny DQL, DDL ON TABLE my_schema.t to root, wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL PRIVILEGES ON TABLE my_schema.t to wolfie");
        TestStatementBuilder.printStatement("deny ALL PRIVILEGES ON TABLE my_schema.t to wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL ON TABLE my_schema.t to wolfie, anna");
        TestStatementBuilder.printStatement("deny ALL ON TABLE my_schema.t to anna");
        TestStatementBuilder.printStatement("deny ALL ON TABLE my_schema.t, banana.b, tree to anna, nyan, cat");
    }

    @Test
    public void testRevokePrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("revoke DML from \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("revoke DQL, DDL from root");
        TestStatementBuilder.printStatement("revoke DQL, DDL from root, wolfie, anna");
        TestStatementBuilder.printStatement("revoke ALL PRIVILEGES from wolfie");
        TestStatementBuilder.printStatement("revoke ALL from wolfie");
        TestStatementBuilder.printStatement("revoke ALL PRIVILEGES from wolfie, anna, herald");
        TestStatementBuilder.printStatement("revoke ALL from wolfie, anna, herald");
    }

    @Test
    public void testRevokeOnSchemaPrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("revoke DML ON SCHEMA my_schema from \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("revoke DQL, DDL ON SCHEMA my_schema from root");
        TestStatementBuilder.printStatement("revoke DQL, DDL ON SCHEMA my_schema from root, wolfie, anna");
        TestStatementBuilder.printStatement("revoke ALL PRIVILEGES ON SCHEMA my_schema from wolfie");
        TestStatementBuilder.printStatement("revoke ALL ON SCHEMA my_schema from wolfie");
        TestStatementBuilder.printStatement("revoke ALL PRIVILEGES ON SCHEMA my_schema from wolfie, anna, herald");
        TestStatementBuilder.printStatement("revoke ALL ON SCHEMA my_schema from wolfie, anna, herald");
        TestStatementBuilder.printStatement("revoke ALL ON SCHEMA my_schema, banana, tree from anna, nyan, cat");
    }

    @Test
    public void testRevokeOnTablePrivilegeStmtBuilder() {
        TestStatementBuilder.printStatement("revoke DML ON TABLE my_schema.t from \"G\u00fcnter\"");
        TestStatementBuilder.printStatement("revoke DQL, DDL ON TABLE my_schema.t from root");
        TestStatementBuilder.printStatement("revoke DQL, DDL ON TABLE my_schema.t from root, wolfie, anna");
        TestStatementBuilder.printStatement("revoke ALL PRIVILEGES ON TABLE my_schema.t from wolfie");
        TestStatementBuilder.printStatement("revoke ALL ON TABLE my_schema.t from wolfie");
        TestStatementBuilder.printStatement("revoke ALL PRIVILEGES ON TABLE my_schema.t from wolfie, anna, herald");
        TestStatementBuilder.printStatement("revoke ALL ON TABLE my_schema.t from wolfie, anna, herald");
        TestStatementBuilder.printStatement("revoke ALL ON TABLE my_schema.t, banana.b, tree from anna, nyan, cat");
    }

    @Test
    public void testCreateFunctionStmtBuilder() {
        TestStatementBuilder.printStatement("create function foo.bar() returns boolean language ? as ?");
        TestStatementBuilder.printStatement("create function foo.bar() returns boolean language $1 as $2");
        // create or replace function
        TestStatementBuilder.printStatement(("create function foo.bar(int, long)" + ((" returns int" + " language javascript") + " as 'function(a, b) {return a + b}'")));
        TestStatementBuilder.printStatement(("create function bar(array(int))" + ((" returns array(int) " + " language javascript") + " as 'function(a) {return [a]}'")));
        TestStatementBuilder.printStatement(("create function bar()" + ((" returns string" + " language javascript") + " as \'function() {return \"\"}\'")));
        TestStatementBuilder.printStatement(("create or replace function bar()" + (" returns string" + " language javascript as \'function() {return \"1\"}\'")));
        // argument with names
        TestStatementBuilder.printStatement(("create function foo.bar(\"f\" int, s object)" + (" returns object" + " language javascript as \'function(f, s) {return {\"a\": 1}}\'")));
        TestStatementBuilder.printStatement(("create function foo.bar(location geo_point, geo_shape)" + (" returns boolean" + " language javascript as 'function(location, b) {return true;}'")));
    }

    @Test
    public void testCreateFunctionStmtBuilderWithIncorrectFunctionName() {
        expectedException.expectMessage(CoreMatchers.containsString(("[foo.bar.a] does not conform the " + "[[schema_name .] function_name] format")));
        expectedException.expect(IllegalArgumentException.class);
        TestStatementBuilder.printStatement(("create function foo.bar.a()" + (" returns object" + " language sql as 'select 1'")));
    }

    @Test
    public void testDropFunctionStmtBuilder() {
        TestStatementBuilder.printStatement("drop function bar(int)");
        TestStatementBuilder.printStatement("drop function foo.bar(obj object)");
        TestStatementBuilder.printStatement("drop function if exists foo.bar(obj object)");
    }

    @Test
    public void testSelectStmtBuilder() throws Exception {
        TestStatementBuilder.printStatement(("select ab" + (((" from (select (ii + y) as iiy, concat(a, b) as ab" + " from (select t1.a, t2.b, t2.y, (t1.i + t2.i) as ii ") + " from t1, t2 where t1.a='a' or t2.b='aa') as t)") + " as tt order by iiy")));
        TestStatementBuilder.printStatement("select extract(day from x) from y");
        TestStatementBuilder.printStatement("select * from foo order by 1, 2 limit 1 offset ?");
        TestStatementBuilder.printStatement("select * from foo a (x, y, z)");
        TestStatementBuilder.printStatement("select *, 123, * from foo");
        TestStatementBuilder.printStatement("select show from foo");
        TestStatementBuilder.printStatement("select extract(day from x), extract('day' from x) from y");
        TestStatementBuilder.printStatement("select 1 + 13 || '15' from foo");
        TestStatementBuilder.printStatement("select \"test\" from foo");
        TestStatementBuilder.printStatement("select col['x'] + col['y'] from foo");
        TestStatementBuilder.printStatement("select col['x'] - col['y'] from foo");
        TestStatementBuilder.printStatement("select col['y'] / col[2 / 1] from foo");
        TestStatementBuilder.printStatement("select col[1] from foo");
        TestStatementBuilder.printStatement("select - + 10");
        TestStatementBuilder.printStatement("select - ( - - 10)");
        TestStatementBuilder.printStatement("select - ( + - 10) * - ( - 10 - + 10)");
        TestStatementBuilder.printStatement("select - - col['x']");
        // expressions as subscript index are only supported by the parser
        TestStatementBuilder.printStatement("select col[1 + 2] - col['y'] from foo");
        TestStatementBuilder.printStatement("select x is distinct from y from foo where a is not distinct from b");
        TestStatementBuilder.printStatement("select * from information_schema.tables");
        TestStatementBuilder.printStatement("select * from a.b.c@d");
        TestStatementBuilder.printStatement("select \"TOTALPRICE\" \"my price\" from \"orders\"");
        TestStatementBuilder.printStatement("select * from foo limit 100 offset 20");
        TestStatementBuilder.printStatement("select * from foo offset 20");
        TestStatementBuilder.printStatement("select * from t where 'value' LIKE ANY (col)");
        TestStatementBuilder.printStatement("select * from t where 'value' NOT LIKE ANY (col)");
        TestStatementBuilder.printStatement("select * from t where 'source' ~ 'pattern'");
        TestStatementBuilder.printStatement("select * from t where 'source' !~ 'pattern'");
        TestStatementBuilder.printStatement("select * from t where source_column ~ pattern_column");
        TestStatementBuilder.printStatement("select * from t where ? !~ ?");
    }

    @Test
    public void testEscapedStringLiteralBuilder() throws Exception {
        TestStatementBuilder.printStatement("select E'aValue'");
        TestStatementBuilder.printStatement("select E\'\\141Value\'");
        TestStatementBuilder.printStatement("select e\'aa\\\'bb\'");
    }

    @Test
    public void testThatEscapedStringLiteralContainingDoubleBackSlashAndSingleQuoteThrowsException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid Escaped String Literal");
        TestStatementBuilder.printStatement("select e\'aa\\\\\'bb\' as col1");
    }

    @Test
    public void testSystemInformationFunctionsStmtBuilder() {
        TestStatementBuilder.printStatement("select current_schema");
        TestStatementBuilder.printStatement("select current_schema()");
        TestStatementBuilder.printStatement("select * from information_schema.tables where table_schema = current_schema");
        TestStatementBuilder.printStatement("select * from information_schema.tables where table_schema = current_schema()");
        TestStatementBuilder.printStatement("select current_user");
        TestStatementBuilder.printStatement("select user");
        TestStatementBuilder.printStatement("select session_user");
    }

    @Test
    public void testStatementBuilderTpch() throws Exception {
        TestStatementBuilder.printTpchQuery(1, 3);
        TestStatementBuilder.printTpchQuery(2, 33, "part type like", "region name");
        TestStatementBuilder.printTpchQuery(3, "market segment", "2013-03-05");
        TestStatementBuilder.printTpchQuery(4, "2013-03-05");
        TestStatementBuilder.printTpchQuery(5, "region name", "2013-03-05");
        TestStatementBuilder.printTpchQuery(6, "2013-03-05", 33, 44);
        TestStatementBuilder.printTpchQuery(7, "nation name 1", "nation name 2");
        TestStatementBuilder.printTpchQuery(8, "nation name", "region name", "part type");
        TestStatementBuilder.printTpchQuery(9, "part name like");
        TestStatementBuilder.printTpchQuery(10, "2013-03-05");
        TestStatementBuilder.printTpchQuery(11, "nation name", 33);
        TestStatementBuilder.printTpchQuery(12, "ship mode 1", "ship mode 2", "2013-03-05");
        TestStatementBuilder.printTpchQuery(13, "comment like 1", "comment like 2");
        TestStatementBuilder.printTpchQuery(14, "2013-03-05");
        // query 15: views not supported
        TestStatementBuilder.printTpchQuery(16, "part brand", "part type like", 3, 4, 5, 6, 7, 8, 9, 10);
        TestStatementBuilder.printTpchQuery(17, "part brand", "part container");
        TestStatementBuilder.printTpchQuery(18, 33);
        TestStatementBuilder.printTpchQuery(19, "part brand 1", "part brand 2", "part brand 3", 11, 22, 33);
        TestStatementBuilder.printTpchQuery(20, "part name like", "2013-03-05", "nation name");
        TestStatementBuilder.printTpchQuery(21, "nation name");
        TestStatementBuilder.printTpchQuery(22, "phone 1", "phone 2", "phone 3", "phone 4", "phone 5", "phone 6", "phone 7");
    }

    @Test
    public void testShowTransactionLevel() throws Exception {
        TestStatementBuilder.printStatement("show transaction isolation level");
    }

    @Test
    public void testArrayConstructorStmtBuilder() {
        TestStatementBuilder.printStatement("select []");
        TestStatementBuilder.printStatement("select [ARRAY[1]]");
        TestStatementBuilder.printStatement("select ARRAY[]");
        TestStatementBuilder.printStatement("select ARRAY[1, 2]");
        TestStatementBuilder.printStatement("select ARRAY[ARRAY[1,2], ARRAY[2]]");
        TestStatementBuilder.printStatement("select ARRAY[ARRAY[1,2], [2]]");
        TestStatementBuilder.printStatement("select ARRAY[ARRAY[1,2], ?]");
        TestStatementBuilder.printStatement("select ARRAY[1 + 2]");
        TestStatementBuilder.printStatement("select ARRAY[ARRAY[col, 2 + 3], [col]]");
        TestStatementBuilder.printStatement("select [ARRAY[1 + 2, ?], [1 + 2]]");
        TestStatementBuilder.printStatement("select ARRAY[col_a IS NULL, col_b IS NOT NULL]");
    }

    @Test
    public void testArrayConstructorSubSelectBuilder() throws Exception {
        TestStatementBuilder.printStatement("select array(select foo from f1) from f2");
        TestStatementBuilder.printStatement("select array(select * from f1) as array1 from f2");
        TestStatementBuilder.printStatement("select count(*) from f1 where f1.array1 = array(select foo from f2)");
    }

    @Test
    public void testArrayConstructorSubSelectBuilderNoParenthesisThrowsParsingException() throws Exception {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("no viable alternative at input 'select array from'"));
        TestStatementBuilder.printStatement("select array from f2");
    }

    @Test
    public void testArrayConstructorSubSelectBuilderNoSubQueryThrowsParsingException() throws Exception {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("no viable alternative at input 'select array()'"));
        TestStatementBuilder.printStatement("select array() as array1 from f2");
    }

    @Test
    public void testTableFunctions() throws Exception {
        TestStatementBuilder.printStatement("select * from unnest([1, 2], ['Arthur', 'Marvin'])");
        TestStatementBuilder.printStatement("select * from unnest(?, ?)");
        TestStatementBuilder.printStatement("select * from open('/tmp/x')");
    }

    @Test
    public void testStatementSubscript() throws Exception {
        TestStatementBuilder.printStatement("select a['x'] from foo where a['x']['y']['z'] = 1");
        TestStatementBuilder.printStatement("select a['x'] from foo where a[1 + 2]['y'] = 1");
    }

    @Test
    public void testCopy() throws Exception {
        TestStatementBuilder.printStatement("copy foo partition (a='x') from ?");
        TestStatementBuilder.printStatement("copy foo partition (a={key='value'}) from ?");
        TestStatementBuilder.printStatement("copy foo from '/folder/file.extension'");
        TestStatementBuilder.printStatement("copy foo from ?");
        TestStatementBuilder.printStatement("copy foo from ? with (some_property=1)");
        TestStatementBuilder.printStatement("copy foo from ? with (some_property=false)");
        TestStatementBuilder.printStatement("copy schemah.foo from '/folder/file.extension'");
        TestStatementBuilder.printStatement("copy schemah.foo from '/folder/file.extension' return summary");
        TestStatementBuilder.printStatement("copy schemah.foo from '/folder/file.extension' with (some_property=1) return summary");
        TestStatementBuilder.printStatement("copy foo (nae) to '/folder/file.extension'");
        TestStatementBuilder.printStatement("copy foo to '/folder/file.extension'");
        TestStatementBuilder.printStatement("copy foo to DIRECTORY '/folder'");
        TestStatementBuilder.printStatement("copy foo to DIRECTORY ?");
        TestStatementBuilder.printStatement("copy foo to DIRECTORY '/folder' with (some_param=4)");
        TestStatementBuilder.printStatement("copy foo partition (a='x') to DIRECTORY '/folder' with (some_param=4)");
        TestStatementBuilder.printStatement("copy foo partition (a=?) to DIRECTORY '/folder' with (some_param=4)");
        TestStatementBuilder.printStatement("copy foo where a = 'x' to DIRECTORY '/folder'");
    }

    @Test
    public void testInsertStmtBuilder() throws Exception {
        // insert from values
        TestStatementBuilder.printStatement("insert into foo (id, name) values ('string', 1.2)");
        TestStatementBuilder.printStatement("insert into foo values ('string', NULL)");
        TestStatementBuilder.printStatement("insert into foo (id, name) values ('string', 1.2), (abs(-4), 4+?)");
        TestStatementBuilder.printStatement("insert into schemah.foo (id, name) values ('string', 1.2)");
        TestStatementBuilder.printStatement("insert into t (a, b) values (1, 2) on conflict do nothing");
        TestStatementBuilder.printStatement("insert into t (a, b) values (1, 2) on conflict (a,b) do nothing");
        TestStatementBuilder.printStatement("insert into t (a, b) values (1, 2) on conflict (a) do update set b = b + 1");
        TestStatementBuilder.printStatement("insert into t (a, b, c) values (1, 2, 3) on conflict (a, b) do update set a = a + 1, b = 3");
        TestStatementBuilder.printStatement("insert into t (a, b, c) values (1, 2), (3, 4) on conflict (c) do update set a = excluded.a + 1, b = 4");
        TestStatementBuilder.printStatement("insert into t (a, b, c) values (1, 2), (3, 4) on conflict (c) do update set a = excluded.a + 1, b = excluded.b - 2");
        InsertFromValues insert = ((InsertFromValues) (SqlParser.createStatement("insert into test_generated_column (id, ts) values (?, ?) on conflict (id) do update set ts = ?")));
        Assignment onDuplicateAssignment = insert.getDuplicateKeyContext().getAssignments().get(0);
        Assert.assertThat(onDuplicateAssignment.expression(), IsInstanceOf.instanceOf(ParameterExpression.class));
        Assert.assertThat(onDuplicateAssignment.expressions().get(0).toString(), Is.is("$3"));
        // insert from query
        TestStatementBuilder.printStatement("insert into foo (id, name) select id, name from bar order by id");
        TestStatementBuilder.printStatement("insert into foo (id, name) select * from bar limit 3 offset 10");
        TestStatementBuilder.printStatement("insert into foo (wealth, name) select sum(money), name from bar group by name");
        TestStatementBuilder.printStatement("insert into foo select sum(money), name from bar group by name");
        TestStatementBuilder.printStatement("insert into foo (id, name) (select id, name from bar order by id)");
        TestStatementBuilder.printStatement("insert into foo (id, name) (select * from bar limit 3 offset 10)");
        TestStatementBuilder.printStatement("insert into foo (wealth, name) (select sum(money), name from bar group by name)");
        TestStatementBuilder.printStatement("insert into foo (select sum(money), name from bar group by name)");
    }

    @Test
    public void testParameterExpressionLimitOffset() throws Exception {
        // ORMs like SQLAlchemy generate these kind of queries.
        TestStatementBuilder.printStatement("select * from foo limit ? offset ?");
    }

    @Test
    public void testMatchPredicateStmtBuilder() throws Exception {
        TestStatementBuilder.printStatement("select * from foo where match (a['1']['2'], 'abc')");
        TestStatementBuilder.printStatement("select * from foo where match (a, 'abc')");
        TestStatementBuilder.printStatement("select * from foo where match ((a, b 2.0), 'abc')");
        TestStatementBuilder.printStatement("select * from foo where match ((a ?, b 2.0), ?)");
        TestStatementBuilder.printStatement("select * from foo where match ((a ?, b 2.0), {type= 'Point', coordinates= [0.0,0.0] })");
        TestStatementBuilder.printStatement("select * from foo where match ((a 1, b 2.0), 'abc') using best_fields");
        TestStatementBuilder.printStatement("select * from foo where match ((a 1, b 2.0), 'abc') using best_fields with (prop=val, foo=1)");
        TestStatementBuilder.printStatement("select * from foo where match (a, (select shape from countries limit 1))");
    }

    @Test
    public void testRepositoryStmtBuilder() throws Exception {
        TestStatementBuilder.printStatement("create repository my_repo type hdfs");
        TestStatementBuilder.printStatement("CREATE REPOSITORY \"myRepo\" TYPE \"fs\"");
        TestStatementBuilder.printStatement("CREATE REPOSITORY \"myRepo\" TYPE \"fs\" with (location=\'/mount/backups/my_backup\', compress=True)");
        Statement statement = SqlParser.createStatement("CREATE REPOSITORY my_repo type hdfs with (location='/mount/backups/my_backup')");
        Assert.assertThat(statement.toString(), Is.is(("CreateRepository{" + (("repository=my_repo, " + "type=hdfs, ") + "properties={location='/mount/backups/my_backup'}}"))));
        TestStatementBuilder.printStatement("DROP REPOSITORY my_repo");
        statement = SqlParser.createStatement("DROP REPOSITORY \"myRepo\"");
        Assert.assertThat(statement.toString(), Is.is(("DropRepository{" + "repository=myRepo}")));
    }

    @Test
    public void testSnapshotStmtBuilder() throws Exception {
        TestStatementBuilder.printStatement("CREATE SNAPSHOT my_repo.my_snapshot ALL");
        TestStatementBuilder.printStatement("CREATE SNAPSHOT my_repo.my_snapshot TABLE authors, books");
        TestStatementBuilder.printStatement("CREATE SNAPSHOT my_repo.my_snapshot TABLE authors, books with (wait_for_completion=True)");
        TestStatementBuilder.printStatement("CREATE SNAPSHOT my_repo.my_snapshot ALL with (wait_for_completion=True)");
        Statement statement = SqlParser.createStatement("CREATE SNAPSHOT my_repo.my_snapshot TABLE authors PARTITION (year=2015, year=2014), books");
        Assert.assertThat(statement.toString(), Is.is(("CreateSnapshot{" + ((((("name=my_repo.my_snapshot, " + "properties={}, ") + "tableList=[Table{only=false, authors, partitionProperties=[") + "Assignment{column=\"year\", expressions=[2015]}, ") + "Assignment{column=\"year\", expressions=[2014]}]}, ") + "Table{only=false, books, partitionProperties=[]}]}"))));
        statement = SqlParser.createStatement("DROP SNAPSHOT my_repo.my_snapshot");
        Assert.assertThat(statement.toString(), Is.is("DropSnapshot{name=my_repo.my_snapshot}"));
        TestStatementBuilder.printStatement("RESTORE SNAPSHOT my_repo.my_snapshot ALL");
        TestStatementBuilder.printStatement("RESTORE SNAPSHOT my_repo.my_snapshot TABLE authors, books");
        TestStatementBuilder.printStatement("RESTORE SNAPSHOT my_repo.my_snapshot TABLE authors, books with (wait_for_completion=True)");
        TestStatementBuilder.printStatement("RESTORE SNAPSHOT my_repo.my_snapshot ALL with (wait_for_completion=True)");
        TestStatementBuilder.printStatement("RESTORE SNAPSHOT my_repo.my_snapshot TABLE authors PARTITION (year=2015, year=2014), books");
        statement = SqlParser.createStatement("RESTORE SNAPSHOT my_repo.my_snapshot TABLE authors PARTITION (year=2015, year=2014), books with (wait_for_completion=True)");
        Assert.assertThat(statement.toString(), Is.is(("RestoreSnapshot{" + ((((((("name=my_repo.my_snapshot, " + "properties={wait_for_completion=true}, ") + "tableList=Optional[") + "[Table{only=false, authors, partitionProperties=[") + "") + "Assignment{column=\"year\", expressions=[2015]}, ") + "Assignment{column=\"year\", expressions=[2014]}]}, ") + "Table{only=false, books, partitionProperties=[]}]]}"))));
        statement = SqlParser.createStatement("RESTORE SNAPSHOT my_repo.my_snapshot ALL");
        Assert.assertThat(statement.toString(), Is.is(("RestoreSnapshot{" + (("name=my_repo.my_snapshot, " + "properties={}, ") + "tableList=Optional.empty}"))));
    }

    @Test
    public void testGeoShapeStmtBuilder() throws Exception {
        TestStatementBuilder.printStatement(("create table test (" + (("    col1 geo_shape," + "    col2 geo_shape index using geohash") + ")")));
        TestStatementBuilder.printStatement(("create table test(" + ("    col1 geo_shape index using quadtree with (precision='1m')" + ")")));
        TestStatementBuilder.printStatement(("create table test(" + (("    col1 geo_shape," + "    index geo_shape_i using quadtree(col1) with (precision='1m')") + ")")));
        TestStatementBuilder.printStatement(("create table test(" + (("    col1 geo_shape INDEX OFF," + "    index geo_shape_i using quadtree(col1) with (precision='1m')") + ")")));
    }

    @Test
    public void testCastStmtBuilder() throws Exception {
        // double colon cast
        TestStatementBuilder.printStatement("select 1+4::integer");
        TestStatementBuilder.printStatement("select '2'::integer");
        TestStatementBuilder.printStatement("select 1+3::string");
        TestStatementBuilder.printStatement("select [0,1,5]::array(boolean)");
        TestStatementBuilder.printStatement("select field::boolean");
        TestStatementBuilder.printStatement("select port['http']::boolean");
        TestStatementBuilder.printStatement("select '4'::integer + 4");
        TestStatementBuilder.printStatement("select 4::string || ' apples'");
        TestStatementBuilder.printStatement("select '-4'::integer");
        TestStatementBuilder.printStatement("select -4::string");
        TestStatementBuilder.printStatement("select '-4'::integer + 10");
        TestStatementBuilder.printStatement("select -4::string || ' apples'");
        // cast
        TestStatementBuilder.printStatement("select cast(1+4 as integer) from foo");
        TestStatementBuilder.printStatement("select cast('2' as integer) from foo");
        // try cast
        TestStatementBuilder.printStatement("select try_cast(y as integer) from foo");
    }

    @Test
    public void testSubscriptExpression() throws Exception {
        Expression expression = SqlParser.createExpression("a['sub']");
        Assert.assertThat(expression, IsInstanceOf.instanceOf(SubscriptExpression.class));
        SubscriptExpression subscript = ((SubscriptExpression) (expression));
        Assert.assertThat(subscript.index(), IsInstanceOf.instanceOf(StringLiteral.class));
        Assert.assertThat(getValue(), Is.is("sub"));
        Assert.assertThat(subscript.name(), IsInstanceOf.instanceOf(QualifiedNameReference.class));
        expression = SqlParser.createExpression("[1,2,3][1]");
        Assert.assertThat(expression, IsInstanceOf.instanceOf(SubscriptExpression.class));
        subscript = ((SubscriptExpression) (expression));
        Assert.assertThat(subscript.index(), IsInstanceOf.instanceOf(LongLiteral.class));
        Assert.assertThat(getValue(), Is.is(1L));
        Assert.assertThat(subscript.name(), IsInstanceOf.instanceOf(ArrayLiteral.class));
    }

    @Test
    public void testSafeSubscriptExpression() {
        MatchPredicate matchPredicate = ((MatchPredicate) (SqlParser.createExpression("match (a['1']['2'], 'abc')")));
        Assert.assertThat(matchPredicate.idents().get(0).columnIdent().toString(), Is.is("\"a\"[\'1\'][\'2\']"));
        matchPredicate = ((MatchPredicate) (SqlParser.createExpression("match (a['1']['2']['4'], 'abc')")));
        Assert.assertThat(matchPredicate.idents().get(0).columnIdent().toString(), Is.is("\"a\"[\'1\'][\'2\'][\'4\']"));
        expectedException.expect(ParsingException.class);
        SqlParser.createExpression("match ([1]['1']['2'], 'abc')");
    }

    @Test
    public void testCaseSensitivity() throws Exception {
        Expression expression = SqlParser.createExpression("\"firstName\" = \'myName\'");
        QualifiedNameReference nameRef = ((QualifiedNameReference) (getLeft()));
        StringLiteral myName = ((StringLiteral) (getRight()));
        Assert.assertThat(nameRef.getName().getSuffix(), Is.is("firstName"));
        Assert.assertThat(myName.getValue(), Is.is("myName"));
        expression = SqlParser.createExpression("FIRSTNAME = 'myName'");
        nameRef = ((QualifiedNameReference) (getLeft()));
        Assert.assertThat(nameRef.getName().getSuffix(), Is.is("firstname"));
        expression = SqlParser.createExpression("ABS(1)");
        QualifiedName functionName = getName();
        Assert.assertThat(functionName.getSuffix(), Is.is("abs"));
    }

    @Test
    public void testArrayComparison() throws Exception {
        Expression anyExpression = SqlParser.createExpression("1 = ANY (arrayColumnRef)");
        Assert.assertThat(anyExpression, IsInstanceOf.instanceOf(ArrayComparisonExpression.class));
        ArrayComparisonExpression arrayComparisonExpression = ((ArrayComparisonExpression) (anyExpression));
        Assert.assertThat(arrayComparisonExpression.quantifier(), Is.is(ANY));
        Assert.assertThat(arrayComparisonExpression.getLeft(), IsInstanceOf.instanceOf(LongLiteral.class));
        Assert.assertThat(arrayComparisonExpression.getRight(), IsInstanceOf.instanceOf(QualifiedNameReference.class));
        Expression someExpression = SqlParser.createExpression("1 = SOME (arrayColumnRef)");
        Assert.assertThat(someExpression, IsInstanceOf.instanceOf(ArrayComparisonExpression.class));
        ArrayComparisonExpression someArrayComparison = ((ArrayComparisonExpression) (someExpression));
        Assert.assertThat(someArrayComparison.quantifier(), Is.is(ANY));
        Assert.assertThat(someArrayComparison.getLeft(), IsInstanceOf.instanceOf(LongLiteral.class));
        Assert.assertThat(someArrayComparison.getRight(), IsInstanceOf.instanceOf(QualifiedNameReference.class));
        Expression allExpression = SqlParser.createExpression("'StringValue' = ALL (arrayColumnRef)");
        Assert.assertThat(allExpression, IsInstanceOf.instanceOf(ArrayComparisonExpression.class));
        ArrayComparisonExpression allArrayComparison = ((ArrayComparisonExpression) (allExpression));
        Assert.assertThat(allArrayComparison.quantifier(), Is.is(ALL));
        Assert.assertThat(allArrayComparison.getLeft(), IsInstanceOf.instanceOf(StringLiteral.class));
        Assert.assertThat(allArrayComparison.getRight(), IsInstanceOf.instanceOf(QualifiedNameReference.class));
    }

    @Test
    public void testArrayComparisonSubselect() throws Exception {
        Expression anyExpression = SqlParser.createExpression("1 = ANY ((SELECT 5))");
        Assert.assertThat(anyExpression, IsInstanceOf.instanceOf(ArrayComparisonExpression.class));
        ArrayComparisonExpression arrayComparisonExpression = ((ArrayComparisonExpression) (anyExpression));
        Assert.assertThat(arrayComparisonExpression.quantifier(), Is.is(ANY));
        Assert.assertThat(arrayComparisonExpression.getLeft(), IsInstanceOf.instanceOf(LongLiteral.class));
        Assert.assertThat(arrayComparisonExpression.getRight(), IsInstanceOf.instanceOf(SubqueryExpression.class));
        // It's possible to ommit the parenthesis
        anyExpression = SqlParser.createExpression("1 = ANY (SELECT 5)");
        Assert.assertThat(anyExpression, IsInstanceOf.instanceOf(ArrayComparisonExpression.class));
        arrayComparisonExpression = ((ArrayComparisonExpression) (anyExpression));
        Assert.assertThat(arrayComparisonExpression.quantifier(), Is.is(ANY));
        Assert.assertThat(arrayComparisonExpression.getLeft(), IsInstanceOf.instanceOf(LongLiteral.class));
        Assert.assertThat(arrayComparisonExpression.getRight(), IsInstanceOf.instanceOf(SubqueryExpression.class));
    }

    @Test
    public void testArrayLikeExpression() {
        Expression expression = SqlParser.createExpression("'books%' LIKE ANY(race['interests'])");
        Assert.assertThat(expression, IsInstanceOf.instanceOf(ArrayLikePredicate.class));
        ArrayLikePredicate arrayLikePredicate = ((ArrayLikePredicate) (expression));
        Assert.assertThat(arrayLikePredicate.inverse(), Is.is(false));
        Assert.assertThat(arrayLikePredicate.getEscape(), Is.is(CoreMatchers.nullValue()));
        Assert.assertThat(arrayLikePredicate.getPattern().toString(), Is.is("'books%'"));
        Assert.assertThat(arrayLikePredicate.getValue().toString(), Is.is("\"race\"[\'interests\']"));
        expression = SqlParser.createExpression("'b%' NOT LIKE ANY(race)");
        Assert.assertThat(expression, IsInstanceOf.instanceOf(ArrayLikePredicate.class));
        arrayLikePredicate = ((ArrayLikePredicate) (expression));
        Assert.assertThat(arrayLikePredicate.inverse(), Is.is(true));
        Assert.assertThat(arrayLikePredicate.getEscape(), Is.is(CoreMatchers.nullValue()));
        Assert.assertThat(arrayLikePredicate.getPattern().toString(), Is.is("'b%'"));
        Assert.assertThat(arrayLikePredicate.getValue().toString(), Is.is("\"race\""));
    }

    @Test
    public void testStringLiteral() throws Exception {
        String[] testString = new String[]{ "foo' or 1='1", "foo''bar", "foo\\bar", "foo\'bar", "''''", "''", "" };
        for (String s : testString) {
            Expression expr = SqlParser.createExpression(Literals.quoteStringLiteral(s));
            Assert.assertThat(getValue(), Is.is(s));
        }
    }

    @Test
    public void testEscapedStringLiteral() throws Exception {
        String input = "this is a triple-a:\\141\\x61\\u0061";
        String expectedValue = "this is a triple-a:aaa";
        Expression expr = SqlParser.createExpression(Literals.quoteEscapedStringLiteral(input));
        EscapedCharStringLiteral escapedCharStringLiteral = ((EscapedCharStringLiteral) (expr));
        Assert.assertThat(escapedCharStringLiteral.getRawValue(), Is.is(input));
        Assert.assertThat(escapedCharStringLiteral.getValue(), Is.is(expectedValue));
    }

    @Test
    public void testObjectLiteral() throws Exception {
        Expression emptyObjectLiteral = SqlParser.createExpression("{}");
        Assert.assertThat(emptyObjectLiteral, IsInstanceOf.instanceOf(ObjectLiteral.class));
        Assert.assertThat(values().size(), Is.is(0));
        ObjectLiteral objectLiteral = ((ObjectLiteral) (SqlParser.createExpression("{a=1, aa=-1, b='str', c=[], d={}}")));
        Assert.assertThat(objectLiteral.values().size(), Is.is(5));
        Assert.assertThat(objectLiteral.values().get("a").iterator().next(), IsInstanceOf.instanceOf(LongLiteral.class));
        Assert.assertThat(objectLiteral.values().get("aa").iterator().next(), IsInstanceOf.instanceOf(NegativeExpression.class));
        Assert.assertThat(objectLiteral.values().get("b").iterator().next(), IsInstanceOf.instanceOf(StringLiteral.class));
        Assert.assertThat(objectLiteral.values().get("c").iterator().next(), IsInstanceOf.instanceOf(ArrayLiteral.class));
        Assert.assertThat(objectLiteral.values().get("d").iterator().next(), IsInstanceOf.instanceOf(ObjectLiteral.class));
        ObjectLiteral quotedObjectLiteral = ((ObjectLiteral) (SqlParser.createExpression("{\"AbC\"=123}")));
        Assert.assertThat(quotedObjectLiteral.values().size(), Is.is(1));
        Assert.assertThat(quotedObjectLiteral.values().get("AbC").iterator().next(), IsInstanceOf.instanceOf(LongLiteral.class));
        Assert.assertThat(quotedObjectLiteral.values().get("abc").isEmpty(), Is.is(true));
        Assert.assertThat(quotedObjectLiteral.values().get("ABC").isEmpty(), Is.is(true));
        SqlParser.createExpression("{a=func('abc')}");
        SqlParser.createExpression("{b=identifier}");
        SqlParser.createExpression("{c=1+4}");
        SqlParser.createExpression("{d=sub['script']}");
    }

    @Test
    public void testArrayLiteral() throws Exception {
        ArrayLiteral emptyArrayLiteral = ((ArrayLiteral) (SqlParser.createExpression("[]")));
        Assert.assertThat(emptyArrayLiteral.values().size(), Is.is(0));
        ArrayLiteral singleArrayLiteral = ((ArrayLiteral) (SqlParser.createExpression("[1]")));
        Assert.assertThat(singleArrayLiteral.values().size(), Is.is(1));
        Assert.assertThat(singleArrayLiteral.values().get(0), IsInstanceOf.instanceOf(LongLiteral.class));
        ArrayLiteral multipleArrayLiteral = ((ArrayLiteral) (SqlParser.createExpression("['str', -12.56, {}, ['another', 'array']]")));
        Assert.assertThat(multipleArrayLiteral.values().size(), Is.is(4));
        Assert.assertThat(multipleArrayLiteral.values().get(0), IsInstanceOf.instanceOf(StringLiteral.class));
        Assert.assertThat(multipleArrayLiteral.values().get(1), IsInstanceOf.instanceOf(NegativeExpression.class));
        Assert.assertThat(multipleArrayLiteral.values().get(2), IsInstanceOf.instanceOf(ObjectLiteral.class));
        Assert.assertThat(multipleArrayLiteral.values().get(3), IsInstanceOf.instanceOf(ArrayLiteral.class));
    }

    @Test
    public void testParameterNode() throws Exception {
        TestStatementBuilder.printStatement("select foo, $1 from foo where a = $2 or a = $3");
        final AtomicInteger counter = new AtomicInteger(0);
        Expression inExpression = SqlParser.createExpression("x in (?, ?, ?)");
        inExpression.accept(new io.crate.sql.tree.DefaultTraversalVisitor<Object, Object>() {
            @Override
            public Object visitParameterExpression(ParameterExpression node, Object context) {
                Assert.assertEquals(counter.incrementAndGet(), node.position());
                return super.visitParameterExpression(node, context);
            }
        }, null);
        Assert.assertEquals(3, counter.get());
        counter.set(0);
        Expression andExpression = SqlParser.createExpression("a = ? and b = ? and c = $3");
        andExpression.accept(new io.crate.sql.tree.DefaultTraversalVisitor<Object, Object>() {
            @Override
            public Object visitParameterExpression(ParameterExpression node, Object context) {
                Assert.assertEquals(counter.incrementAndGet(), node.position());
                return super.visitParameterExpression(node, context);
            }
        }, null);
        Assert.assertEquals(3, counter.get());
    }

    @Test
    public void testShowCreateTable() throws Exception {
        Statement stmt = SqlParser.createStatement("SHOW CREATE TABLE foo");
        Assert.assertTrue((stmt instanceof ShowCreateTable));
        Assert.assertEquals(((ShowCreateTable) (stmt)).table().getName().toString(), "foo");
        stmt = SqlParser.createStatement("SHOW CREATE TABLE my_schema.foo");
        Assert.assertEquals(((ShowCreateTable) (stmt)).table().getName().toString(), "my_schema.foo");
    }

    @Test
    public void testCreateTableWithGeneratedColumn() throws Exception {
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS date_trunc('day', col1))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS (date_trunc('day', col1)))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS date_trunc('day', col1) INDEX OFF)");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 GENERATED ALWAYS AS date_trunc('day', col1))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 GENERATED ALWAYS AS (date_trunc('day', col1)))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 string GENERATED ALWAYS AS date_trunc('day', col1))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 string GENERATED ALWAYS AS (date_trunc('day', col1)))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS cast(col1 as string))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS (cast(col1 as string)))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS col1 + 1)");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS (col1 + 1))");
        TestStatementBuilder.printStatement("create table test (col1 int, col2 AS col1['name'] + 1)");
    }

    @Test
    public void testAddGeneratedColumn() throws Exception {
        TestStatementBuilder.printStatement("alter table t add col2 AS date_trunc('day', col1)");
        TestStatementBuilder.printStatement("alter table t add col2 AS date_trunc('day', col1) INDEX USING PLAIN");
        TestStatementBuilder.printStatement("alter table t add col2 AS (date_trunc('day', col1))");
        TestStatementBuilder.printStatement("alter table t add col2 GENERATED ALWAYS AS date_trunc('day', col1)");
        TestStatementBuilder.printStatement("alter table t add col2 GENERATED ALWAYS AS (date_trunc('day', col1))");
        TestStatementBuilder.printStatement("alter table t add col2 string GENERATED ALWAYS AS date_trunc('day', col1)");
        TestStatementBuilder.printStatement("alter table t add col2 string GENERATED ALWAYS AS (date_trunc('day', col1))");
        TestStatementBuilder.printStatement("alter table t add col2 AS cast(col1 as string)");
        TestStatementBuilder.printStatement("alter table t add col2 AS (cast(col1 as string))");
        TestStatementBuilder.printStatement("alter table t add col2 AS col1 + 1");
        TestStatementBuilder.printStatement("alter table t add col2 AS (col1 + 1)");
        TestStatementBuilder.printStatement("alter table t add col2 AS col1['name'] + 1");
    }

    @Test
    public void testAlterTableOpenClose() throws Exception {
        TestStatementBuilder.printStatement("alter table t close");
        TestStatementBuilder.printStatement("alter table t open");
        TestStatementBuilder.printStatement("alter table t partition (partitioned_col=1) close");
        TestStatementBuilder.printStatement("alter table t partition (partitioned_col=1) open");
    }

    @Test
    public void testAlterTableRename() throws Exception {
        TestStatementBuilder.printStatement("alter table t rename to t2");
    }

    @Test
    public void testAlterTableReroute() throws Exception {
        TestStatementBuilder.printStatement("alter table t reroute move shard 1 from 'node1' to 'node2'");
        TestStatementBuilder.printStatement("alter table t partition (parted_col = ?) reroute move shard ? from ? to ?");
        TestStatementBuilder.printStatement("alter table t reroute allocate replica shard 1 on 'node1'");
        TestStatementBuilder.printStatement("alter table t reroute cancel shard 1 on 'node1'");
        TestStatementBuilder.printStatement("alter table t reroute cancel shard 1 on 'node1' with (allow_primary = true)");
    }

    @Test
    public void testAlterUser() throws Exception {
        TestStatementBuilder.printStatement("alter user crate set (password = 'password')");
        TestStatementBuilder.printStatement("alter user crate set (password = null)");
    }

    @Test
    public void testAlterUserWithMissingProperties() {
        expectedException.expect(ParsingException.class);
        expectedException.expectMessage(CoreMatchers.containsString("mismatched input '<EOF>' expecting 'SET'"));
        TestStatementBuilder.printStatement("alter user crate");
    }

    @Test
    public void testSubSelects() throws Exception {
        TestStatementBuilder.printStatement("select * from (select * from foo) as f");
        TestStatementBuilder.printStatement("select * from (select * from (select * from foo) as f1) as f2");
        TestStatementBuilder.printStatement("select * from (select * from foo) f");
        TestStatementBuilder.printStatement("select * from (select * from (select * from foo) f1) f2");
    }

    @Test
    public void testJoins() throws Exception {
        TestStatementBuilder.printStatement("select * from foo inner join bar on foo.id = bar.id");
        TestStatementBuilder.printStatement("select * from foo left outer join bar on foo.id = bar.id");
        TestStatementBuilder.printStatement("select * from foo left join bar on foo.id = bar.id");
        TestStatementBuilder.printStatement("select * from foo right outer join bar on foo.id = bar.id");
        TestStatementBuilder.printStatement("select * from foo right join bar on foo.id = bar.id");
        TestStatementBuilder.printStatement("select * from foo full outer join bar on foo.id = bar.id");
        TestStatementBuilder.printStatement("select * from foo full join bar on foo.id = bar.id");
    }

    @Test
    public void testConditionals() throws Exception {
        TestStatementBuilder.printStatement(("SELECT a," + (((("       CASE WHEN a=1 THEN 'one'" + "            WHEN a=2 THEN 'two'") + "            ELSE 'other'") + "       END") + "    FROM test")));
        TestStatementBuilder.printStatement(("SELECT a," + (((("       CASE a WHEN 1 THEN 'one'" + "              WHEN 2 THEN 'two'") + "              ELSE 'other'") + "       END") + "    FROM test")));
        TestStatementBuilder.printStatement("SELECT a WHERE CASE WHEN x <> 0 THEN y/x > 1.5 ELSE false END");
    }

    @Test
    public void testUnions() throws Exception {
        TestStatementBuilder.printStatement("select * from foo union select * from bar");
        TestStatementBuilder.printStatement("select * from foo union all select * from bar");
        TestStatementBuilder.printStatement("select * from foo union distinct select * from bar");
        TestStatementBuilder.printStatement(("select 1 " + (("union select 2 " + "union distinct select 3 ") + "union all select 4")));
        TestStatementBuilder.printStatement(("select 1 union " + ((((("select 2 union all " + "select 3 union ") + "select 4 union all ") + "select 5 union distinct ") + "select 6 ") + "order by 1")));
    }

    @Test
    public void testCreateViewParsing() {
        TestStatementBuilder.printStatement("CREATE VIEW myView AS SELECT * FROM foobar");
        TestStatementBuilder.printStatement("CREATE OR REPLACE VIEW myView AS SELECT * FROM foobar");
    }

    @Test
    public void testDropViewParsing() {
        TestStatementBuilder.printStatement("DROP VIEW myView");
        TestStatementBuilder.printStatement("DROP VIEW v1, v2, x.v3");
        TestStatementBuilder.printStatement("DROP VIEW IF EXISTS myView");
        TestStatementBuilder.printStatement("DROP VIEW IF EXISTS v1, x.v2, y.v3");
    }
}

