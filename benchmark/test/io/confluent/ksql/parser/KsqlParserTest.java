/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.parser;


import ArithmeticUnaryExpression.Sign.MINUS;
import DdlConfig.KAFKA_TOPIC_NAME_PROPERTY;
import DdlConfig.TOPIC_NAME_PROPERTY;
import DdlConfig.VALUE_FORMAT_PROPERTY;
import Join.Type.INNER;
import Join.Type.LEFT;
import Join.Type.OUTER;
import SqlType.STRING;
import SqlType.STRUCT;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.exception.ParseFailedException;
import io.confluent.ksql.parser.tree.ArithmeticUnaryExpression;
import io.confluent.ksql.parser.tree.ComparisonExpression;
import io.confluent.ksql.parser.tree.CreateStream;
import io.confluent.ksql.parser.tree.CreateStreamAsSelect;
import io.confluent.ksql.parser.tree.CreateTable;
import io.confluent.ksql.parser.tree.DropStream;
import io.confluent.ksql.parser.tree.DropTable;
import io.confluent.ksql.parser.tree.FunctionCall;
import io.confluent.ksql.parser.tree.InsertInto;
import io.confluent.ksql.parser.tree.IntegerLiteral;
import io.confluent.ksql.parser.tree.Join;
import io.confluent.ksql.parser.tree.ListProperties;
import io.confluent.ksql.parser.tree.ListQueries;
import io.confluent.ksql.parser.tree.ListStreams;
import io.confluent.ksql.parser.tree.ListTables;
import io.confluent.ksql.parser.tree.ListTopics;
import io.confluent.ksql.parser.tree.LongLiteral;
import io.confluent.ksql.parser.tree.Query;
import io.confluent.ksql.parser.tree.RegisterTopic;
import io.confluent.ksql.parser.tree.SearchedCaseExpression;
import io.confluent.ksql.parser.tree.SelectItem;
import io.confluent.ksql.parser.tree.SetProperty;
import io.confluent.ksql.parser.tree.SingleColumn;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.parser.tree.Struct;
import io.confluent.ksql.parser.tree.WithinExpression;
import io.confluent.ksql.util.KsqlException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class KsqlParserTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private MutableMetaStore metaStore;

    @Test
    public void testSimpleQuery() {
        final String simpleQuery = "SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;";
        final PreparedStatement<?> statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore);
        Assert.assertThat(statement.getStatementText(), CoreMatchers.is(simpleQuery));
        Assert.assertTrue("testSimpleQuery fails", ((statement.getStatement()) instanceof Query));
        final Query query = ((Query) (statement.getStatement()));
        Assert.assertTrue("testSimpleQuery fails", ((query.getSelect().getSelectItems().size()) == 3));
        Assert.assertThat(query.getFrom(), IsNot.not(CoreMatchers.nullValue()));
        Assert.assertTrue("testSimpleQuery fails", query.getWhere().isPresent());
        Assert.assertTrue("testSimpleQuery fails", ((query.getWhere().get()) instanceof ComparisonExpression));
        final ComparisonExpression comparisonExpression = ((ComparisonExpression) (query.getWhere().get()));
        Assert.assertTrue("testSimpleQuery fails", getValue().equalsIgnoreCase(">"));
    }

    @Test
    public void testProjection() {
        final String queryStr = "SELECT col0, col2, col3 FROM test1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testProjection fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testProjection fails", ((query.getSelect().getSelectItems().size()) == 3));
        Assert.assertTrue("testProjection fails", ((query.getSelect().getSelectItems().get(0)) instanceof SingleColumn));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertTrue("testProjection fails", column0.getAlias().get().equalsIgnoreCase("COL0"));
        Assert.assertTrue("testProjection fails", column0.getExpression().toString().equalsIgnoreCase("TEST1.COL0"));
    }

    @Test
    public void testProjectionWithArrayMap() {
        final String queryStr = "SELECT col0, col2, col3, col4[0], col5['key1'] FROM test1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testProjectionWithArrayMap fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testProjectionWithArrayMap fails", ((query.getSelect().getSelectItems().size()) == 5));
        Assert.assertTrue("testProjectionWithArrayMap fails", ((query.getSelect().getSelectItems().get(0)) instanceof SingleColumn));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertTrue("testProjectionWithArrayMap fails", column0.getAlias().get().equalsIgnoreCase("COL0"));
        Assert.assertTrue("testProjectionWithArrayMap fails", column0.getExpression().toString().equalsIgnoreCase("TEST1.COL0"));
        final SingleColumn column3 = ((SingleColumn) (query.getSelect().getSelectItems().get(3)));
        final SingleColumn column4 = ((SingleColumn) (query.getSelect().getSelectItems().get(4)));
        Assert.assertTrue("testProjectionWithArrayMap fails", column3.getExpression().toString().equalsIgnoreCase("TEST1.COL4[0]"));
        Assert.assertTrue("testProjectionWithArrayMap fails", column4.getExpression().toString().equalsIgnoreCase("TEST1.COL5['key1']"));
    }

    @Test
    public void testProjectFilter() {
        final String queryStr = "SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSimpleQuery fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testProjectFilter fails", ((query.getWhere().get()) instanceof ComparisonExpression));
        final ComparisonExpression comparisonExpression = ((ComparisonExpression) (query.getWhere().get()));
        Assert.assertTrue("testProjectFilter fails", comparisonExpression.toString().equalsIgnoreCase("(TEST1.COL0 > 100)"));
        Assert.assertTrue("testProjectFilter fails", ((query.getSelect().getSelectItems().size()) == 3));
    }

    @Test
    public void testBinaryExpression() {
        final String queryStr = "SELECT col0+10, col2, col3-col1 FROM test1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testBinaryExpression fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertTrue("testBinaryExpression fails", column0.getAlias().get().equalsIgnoreCase("KSQL_COL_0"));
        Assert.assertTrue("testBinaryExpression fails", column0.getExpression().toString().equalsIgnoreCase("(TEST1.COL0 + 10)"));
    }

    @Test
    public void testBooleanExpression() {
        final String queryStr = "SELECT col0 = 10, col2, col3 > col1 FROM test1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testBooleanExpression fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertTrue("testBooleanExpression fails", column0.getAlias().get().equalsIgnoreCase("KSQL_COL_0"));
        Assert.assertTrue("testBooleanExpression fails", column0.getExpression().toString().equalsIgnoreCase("(TEST1.COL0 = 10)"));
    }

    @Test
    public void testLiterals() {
        final String queryStr = "SELECT 10, col2, 'test', 2.5, true, -5 FROM test1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testLiterals fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertTrue("testLiterals fails", column0.getAlias().get().equalsIgnoreCase("KSQL_COL_0"));
        Assert.assertTrue("testLiterals fails", column0.getExpression().toString().equalsIgnoreCase("10"));
        final SingleColumn column1 = ((SingleColumn) (query.getSelect().getSelectItems().get(1)));
        Assert.assertTrue("testLiterals fails", column1.getAlias().get().equalsIgnoreCase("COL2"));
        Assert.assertTrue("testLiterals fails", column1.getExpression().toString().equalsIgnoreCase("TEST1.COL2"));
        final SingleColumn column2 = ((SingleColumn) (query.getSelect().getSelectItems().get(2)));
        Assert.assertTrue("testLiterals fails", column2.getAlias().get().equalsIgnoreCase("KSQL_COL_2"));
        Assert.assertTrue("testLiterals fails", column2.getExpression().toString().equalsIgnoreCase("'test'"));
        final SingleColumn column3 = ((SingleColumn) (query.getSelect().getSelectItems().get(3)));
        Assert.assertTrue("testLiterals fails", column3.getAlias().get().equalsIgnoreCase("KSQL_COL_3"));
        Assert.assertTrue("testLiterals fails", column3.getExpression().toString().equalsIgnoreCase("2.5"));
        final SingleColumn column4 = ((SingleColumn) (query.getSelect().getSelectItems().get(4)));
        Assert.assertTrue("testLiterals fails", column4.getAlias().get().equalsIgnoreCase("KSQL_COL_4"));
        Assert.assertTrue("testLiterals fails", column4.getExpression().toString().equalsIgnoreCase("true"));
        final SingleColumn column5 = ((SingleColumn) (query.getSelect().getSelectItems().get(5)));
        Assert.assertTrue("testLiterals fails", column5.getAlias().get().equalsIgnoreCase("KSQL_COL_5"));
        Assert.assertTrue("testLiterals fails", column5.getExpression().toString().equalsIgnoreCase("-5"));
    }

    @Test
    public void shouldParseIntegerLiterals() {
        shouldParseNumericLiteral(0, new IntegerLiteral(0));
        shouldParseNumericLiteral(10, new IntegerLiteral(10));
        shouldParseNumericLiteral(Integer.MAX_VALUE, new IntegerLiteral(Integer.MAX_VALUE));
    }

    @Test
    public void shouldParseLongLiterals() {
        shouldParseNumericLiteral(((Integer.MAX_VALUE) + 100L), new LongLiteral(((Integer.MAX_VALUE) + 100L)));
    }

    @Test
    public void shouldParseNegativeInteger() {
        final String queryStr = String.format("SELECT -12345 FROM test1;");
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (statement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_0"));
        Assert.assertThat(column0.getExpression(), IsInstanceOf.instanceOf(ArithmeticUnaryExpression.class));
        final ArithmeticUnaryExpression aue = ((ArithmeticUnaryExpression) (column0.getExpression()));
        Assert.assertThat(aue.getValue(), IsInstanceOf.instanceOf(IntegerLiteral.class));
        Assert.assertThat(getValue(), CoreMatchers.equalTo(12345));
        Assert.assertThat(aue.getSign(), CoreMatchers.equalTo(MINUS));
    }

    @Test
    public void testBooleanLogicalExpression() {
        final String queryStr = "SELECT 10, col2, 'test', 2.5, true, -5 FROM test1 WHERE col1 = 10 AND col2 LIKE 'val' OR col4 > 2.6 ;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSimpleQuery fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertTrue("testProjection fails", column0.getAlias().get().equalsIgnoreCase("KSQL_COL_0"));
        Assert.assertTrue("testProjection fails", column0.getExpression().toString().equalsIgnoreCase("10"));
        final SingleColumn column1 = ((SingleColumn) (query.getSelect().getSelectItems().get(1)));
        Assert.assertTrue("testProjection fails", column1.getAlias().get().equalsIgnoreCase("COL2"));
        Assert.assertTrue("testProjection fails", column1.getExpression().toString().equalsIgnoreCase("TEST1.COL2"));
        final SingleColumn column2 = ((SingleColumn) (query.getSelect().getSelectItems().get(2)));
        Assert.assertTrue("testProjection fails", column2.getAlias().get().equalsIgnoreCase("KSQL_COL_2"));
        Assert.assertTrue("testProjection fails", column2.getExpression().toString().equalsIgnoreCase("'test'"));
    }

    @Test
    public void shouldParseStructFieldAccessCorrectly() {
        final String simpleQuery = "SELECT iteminfo->category->name, address->street FROM orders WHERE address->state = 'CA';";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertTrue("testSimpleQuery fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertThat("testSimpleQuery fails", query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        final SingleColumn singleColumn0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        final SingleColumn singleColumn1 = ((SingleColumn) (query.getSelect().getSelectItems().get(1)));
        Assert.assertThat(singleColumn0.getExpression(), IsInstanceOf.instanceOf(FunctionCall.class));
        final FunctionCall functionCall0 = ((FunctionCall) (singleColumn0.getExpression()));
        Assert.assertThat(functionCall0.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(FETCH_FIELD_FROM_STRUCT(ORDERS.ITEMINFO, 'CATEGORY'), 'NAME')"));
        final FunctionCall functionCall1 = ((FunctionCall) (singleColumn1.getExpression()));
        Assert.assertThat(functionCall1.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(ORDERS.ADDRESS, 'STREET')"));
    }

    @Test
    public void testSimpleLeftJoin() {
        final String queryStr = "SELECT t1.col1, t2.col1, t2.col4, col5, t2.col2 FROM test1 t1 LEFT JOIN test2 t2 ON " + "t1.col1 = t2.col1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSimpleQuery fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testSimpleLeftJoin fails", ((query.getFrom()) instanceof Join));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertTrue("testSimpleLeftJoin fails", join.getType().toString().equalsIgnoreCase("LEFT"));
        Assert.assertTrue("testSimpleLeftJoin fails", getAlias().equalsIgnoreCase("T1"));
        Assert.assertTrue("testSimpleLeftJoin fails", getAlias().equalsIgnoreCase("T2"));
    }

    @Test
    public void testLeftJoinWithFilter() {
        final String queryStr = "SELECT t1.col1, t2.col1, t2.col4, t2.col2 FROM test1 t1 LEFT JOIN test2 t2 ON t1.col1 = " + "t2.col1 WHERE t2.col2 = 'test';";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSimpleQuery fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testLeftJoinWithFilter fails", ((query.getFrom()) instanceof Join));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertTrue("testLeftJoinWithFilter fails", join.getType().toString().equalsIgnoreCase("LEFT"));
        Assert.assertTrue("testLeftJoinWithFilter fails", getAlias().equalsIgnoreCase("T1"));
        Assert.assertTrue("testLeftJoinWithFilter fails", getAlias().equalsIgnoreCase("T2"));
        Assert.assertTrue("testLeftJoinWithFilter fails", query.getWhere().get().toString().equalsIgnoreCase("(T2.COL2 = 'test')"));
    }

    @Test
    public void testSelectAll() {
        final String queryStr = "SELECT * FROM test1 t1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSelectAll fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testSelectAll fails", ((query.getSelect().getSelectItems().size()) == 8));
    }

    @Test
    public void testReservedColumnIdentifers() {
        assertQuerySucceeds("SELECT ROWTIME as ROWTIME FROM test1 t1;");
        assertQuerySucceeds("SELECT ROWKEY as ROWKEY FROM test1 t1;");
    }

    @Test
    public void testReservedRowTimeAlias() {
        expectedException.expect(ParseFailedException.class);
        expectedException.expectMessage(CoreMatchers.containsString("ROWTIME is a reserved token for implicit column. You cannot use it as an alias for a column."));
        KsqlParserTestUtil.buildSingleAst("SELECT C1 as ROWTIME FROM test1 t1;", metaStore);
    }

    @Test
    public void testReservedRowKeyAlias() {
        expectedException.expect(ParseFailedException.class);
        expectedException.expectMessage(CoreMatchers.containsString("ROWKEY is a reserved token for implicit column. You cannot use it as an alias for a column."));
        KsqlParserTestUtil.buildSingleAst("SELECT C2 as ROWKEY FROM test1 t1;", metaStore);
    }

    @Test
    public void testSelectAllJoin() {
        final String queryStr = "SELECT * FROM test1 t1 LEFT JOIN test2 t2 ON t1.col1 = t2.col1 WHERE t2.col2 = 'test';";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSimpleQuery fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testSelectAllJoin fails", ((query.getFrom()) instanceof Join));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertTrue("testSelectAllJoin fails", ((query.getSelect().getSelectItems().size()) == 15));
        Assert.assertTrue("testLeftJoinWithFilter fails", getAlias().equalsIgnoreCase("T1"));
        Assert.assertTrue("testLeftJoinWithFilter fails", getAlias().equalsIgnoreCase("T2"));
    }

    @Test
    public void testUDF() {
        final String queryStr = "SELECT lcase(col1), concat(col2,'hello'), floor(abs(col3)) FROM test1 t1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSelectAll fails", (statement instanceof Query));
        final Query query = ((Query) (statement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        Assert.assertTrue("testProjection fails", column0.getAlias().get().equalsIgnoreCase("KSQL_COL_0"));
        Assert.assertTrue("testProjection fails", column0.getExpression().toString().equalsIgnoreCase("LCASE(T1.COL1)"));
        final SingleColumn column1 = ((SingleColumn) (query.getSelect().getSelectItems().get(1)));
        Assert.assertTrue("testProjection fails", column1.getAlias().get().equalsIgnoreCase("KSQL_COL_1"));
        Assert.assertTrue("testProjection fails", column1.getExpression().toString().equalsIgnoreCase("CONCAT(T1.COL2, 'hello')"));
        final SingleColumn column2 = ((SingleColumn) (query.getSelect().getSelectItems().get(2)));
        Assert.assertTrue("testProjection fails", column2.getAlias().get().equalsIgnoreCase("KSQL_COL_2"));
        Assert.assertTrue("testProjection fails", column2.getExpression().toString().equalsIgnoreCase("FLOOR(ABS(T1.COL3))"));
    }

    @Test
    public void testRegisterTopic() {
        final String queryStr = "REGISTER TOPIC orders_topic WITH (value_format = 'avro',kafka_topic='orders_topic');";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testRegisterTopic failed.", (statement instanceof RegisterTopic));
        final RegisterTopic registerTopic = ((RegisterTopic) (statement));
        Assert.assertTrue("testRegisterTopic failed.", registerTopic.getName().toString().equalsIgnoreCase("ORDERS_TOPIC"));
        Assert.assertTrue("testRegisterTopic failed.", ((registerTopic.getProperties().size()) == 2));
        Assert.assertTrue("testRegisterTopic failed.", registerTopic.getProperties().get(VALUE_FORMAT_PROPERTY).toString().equalsIgnoreCase("'avro'"));
    }

    @Test
    public void testCreateStreamWithTopic() {
        final String queryStr = "CREATE STREAM orders (ordertime bigint, orderid varchar, itemid varchar, orderunits " + "double) WITH (registered_topic = 'orders_topic' , key='ordertime');";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testCreateStream failed.", (statement instanceof CreateStream));
        final CreateStream createStream = ((CreateStream) (statement));
        Assert.assertTrue("testCreateStream failed.", createStream.getName().toString().equalsIgnoreCase("ORDERS"));
        Assert.assertTrue("testCreateStream failed.", ((createStream.getElements().size()) == 4));
        Assert.assertTrue("testCreateStream failed.", createStream.getElements().get(0).getName().toString().equalsIgnoreCase("ordertime"));
        Assert.assertTrue("testCreateStream failed.", createStream.getProperties().get(TOPIC_NAME_PROPERTY).toString().equalsIgnoreCase("'orders_topic'"));
    }

    @Test
    public void testCreateStreamWithTopicWithStruct() {
        final String queryStr = "CREATE STREAM orders (ordertime bigint, orderid varchar, itemid varchar, orderunits " + (("double, arraycol array<double>, mapcol map<varchar, double>, " + "order_address STRUCT< number VARCHAR, street VARCHAR, zip INTEGER, city ") + "VARCHAR, state VARCHAR >) WITH (registered_topic = 'orders_topic' , key='ordertime');");
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testCreateStream failed.", (statement instanceof CreateStream));
        final CreateStream createStream = ((CreateStream) (statement));
        Assert.assertThat(createStream.getName().toString().toUpperCase(), CoreMatchers.equalTo("ORDERS"));
        Assert.assertThat(createStream.getElements().size(), CoreMatchers.equalTo(7));
        Assert.assertThat(createStream.getElements().get(0).getName().toString().toLowerCase(), CoreMatchers.equalTo("ordertime"));
        Assert.assertThat(createStream.getElements().get(6).getType().getSqlType(), CoreMatchers.equalTo(STRUCT));
        final Struct struct = ((Struct) (createStream.getElements().get(6).getType()));
        Assert.assertThat(struct.getFields(), Matchers.hasSize(5));
        Assert.assertThat(struct.getFields().get(0).getType().getSqlType(), CoreMatchers.equalTo(STRING));
        Assert.assertThat(createStream.getProperties().get(TOPIC_NAME_PROPERTY).toString().toLowerCase(), CoreMatchers.equalTo("'orders_topic'"));
    }

    @Test
    public void testCreateStream() {
        final String queryStr = "CREATE STREAM orders (ordertime bigint, orderid varchar, itemid varchar, orderunits " + "double) WITH (value_format = 'avro', kafka_topic='orders_topic');";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testCreateStream failed.", (statement instanceof CreateStream));
        final CreateStream createStream = ((CreateStream) (statement));
        Assert.assertTrue("testCreateStream failed.", createStream.getName().toString().equalsIgnoreCase("ORDERS"));
        Assert.assertTrue("testCreateStream failed.", ((createStream.getElements().size()) == 4));
        Assert.assertTrue("testCreateStream failed.", createStream.getElements().get(0).getName().toString().equalsIgnoreCase("ordertime"));
        Assert.assertTrue("testCreateStream failed.", createStream.getProperties().get(KAFKA_TOPIC_NAME_PROPERTY).toString().equalsIgnoreCase("'orders_topic'"));
        Assert.assertTrue("testCreateStream failed.", createStream.getProperties().get(VALUE_FORMAT_PROPERTY).toString().equalsIgnoreCase("'avro'"));
    }

    @Test
    public void testCreateTableWithTopic() {
        final String queryStr = "CREATE TABLE users (usertime bigint, userid varchar, regionid varchar, gender varchar) WITH (registered_topic = 'users_topic', key='userid', statestore='user_statestore');";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testRegisterTopic failed.", (statement instanceof CreateTable));
        final CreateTable createTable = ((CreateTable) (statement));
        Assert.assertTrue("testCreateTable failed.", createTable.getName().toString().equalsIgnoreCase("USERS"));
        Assert.assertTrue("testCreateTable failed.", ((createTable.getElements().size()) == 4));
        Assert.assertTrue("testCreateTable failed.", createTable.getElements().get(0).getName().toString().equalsIgnoreCase("usertime"));
        Assert.assertTrue("testCreateTable failed.", createTable.getProperties().get(TOPIC_NAME_PROPERTY).toString().equalsIgnoreCase("'users_topic'"));
    }

    @Test
    public void testCreateTable() {
        final String queryStr = "CREATE TABLE users (usertime bigint, userid varchar, regionid varchar, gender varchar) " + "WITH (kafka_topic = 'users_topic', value_format='json', key = 'userid');";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testRegisterTopic failed.", (statement instanceof CreateTable));
        final CreateTable createTable = ((CreateTable) (statement));
        Assert.assertTrue("testCreateTable failed.", createTable.getName().toString().equalsIgnoreCase("USERS"));
        Assert.assertTrue("testCreateTable failed.", ((createTable.getElements().size()) == 4));
        Assert.assertTrue("testCreateTable failed.", createTable.getElements().get(0).getName().toString().equalsIgnoreCase("usertime"));
        Assert.assertTrue("testCreateTable failed.", createTable.getProperties().get(KAFKA_TOPIC_NAME_PROPERTY).toString().equalsIgnoreCase("'users_topic'"));
        Assert.assertTrue("testCreateTable failed.", createTable.getProperties().get(VALUE_FORMAT_PROPERTY).toString().equalsIgnoreCase("'json'"));
    }

    @Test
    public void testCreateStreamAsSelect() {
        final String queryStr = "CREATE STREAM bigorders_json WITH (value_format = 'json', " + "kafka_topic='bigorders_topic') AS SELECT * FROM orders WHERE orderunits > 5 ;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        Assert.assertThat(createStreamAsSelect.getName().toString().toLowerCase(), CoreMatchers.equalTo("bigorders_json"));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(8));
        Assert.assertThat(query.getWhere().get().toString().toUpperCase(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS > 5)"));
        Assert.assertThat(getAlias().toUpperCase(), CoreMatchers.equalTo("ORDERS"));
    }

    @Test
    public void testShouldFailIfWrongKeyword() {
        try {
            final String simpleQuery = "SELLECT col0, col2, col3 FROM test1 WHERE col0 > 100;";
            KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore);
            Assert.fail(String.format("Expected query: %s to fail", simpleQuery));
        } catch (final ParseFailedException e) {
            final String errorMessage = e.getMessage();
            Assert.assertTrue(errorMessage.toLowerCase().contains(("line 1:1: mismatched input 'SELLECT'" + " expecting").toLowerCase()));
        }
    }

    @Test
    public void testSelectTumblingWindow() {
        final String queryStr = "select itemid, sum(orderunits) from orders window TUMBLING ( size 30 second) where orderunits > 5 group by itemid;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSelectTumblingWindow failed.", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testCreateTable failed.", ((query.getSelect().getSelectItems().size()) == 2));
        Assert.assertTrue("testSelectTumblingWindow failed.", query.getWhere().get().toString().equalsIgnoreCase("(ORDERS.ORDERUNITS > 5)"));
        Assert.assertTrue("testSelectTumblingWindow failed.", getAlias().equalsIgnoreCase("ORDERS"));
        Assert.assertTrue("testSelectTumblingWindow failed.", query.getWindowExpression().isPresent());
        Assert.assertTrue("testSelectTumblingWindow failed.", query.getWindowExpression().get().toString().equalsIgnoreCase(" WINDOW STREAMWINDOW  TUMBLING ( SIZE 30 SECONDS ) "));
    }

    @Test
    public void testSelectHoppingWindow() {
        final String queryStr = "select itemid, sum(orderunits) from orders window HOPPING ( size 30 second, advance by 5" + (((" seconds) " + "where ") + "orderunits") + " > 5 group by itemid;");
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (statement));
        Assert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(query.getWhere().get().toString(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS > 5)"));
        Assert.assertThat(getAlias().toUpperCase(), CoreMatchers.equalTo("ORDERS"));
        Assert.assertTrue("window expression isn't present", query.getWindowExpression().isPresent());
        Assert.assertThat(query.getWindowExpression().get().toString().toUpperCase(), CoreMatchers.equalTo(" WINDOW STREAMWINDOW  HOPPING ( SIZE 30 SECONDS , ADVANCE BY 5 SECONDS ) "));
    }

    @Test
    public void testSelectSessionWindow() {
        final String queryStr = "select itemid, sum(orderunits) from orders window SESSION ( 30 second) where " + "orderunits > 5 group by itemid;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(queryStr, metaStore).getStatement();
        Assert.assertTrue("testSelectSessionWindow failed.", (statement instanceof Query));
        final Query query = ((Query) (statement));
        Assert.assertTrue("testCreateTable failed.", ((query.getSelect().getSelectItems().size()) == 2));
        Assert.assertTrue("testSelectSessionWindow failed.", query.getWhere().get().toString().equalsIgnoreCase("(ORDERS.ORDERUNITS > 5)"));
        Assert.assertTrue("testSelectSessionWindow failed.", getAlias().equalsIgnoreCase("ORDERS"));
        Assert.assertTrue("testSelectSessionWindow failed.", query.getWindowExpression().isPresent());
        Assert.assertTrue("testSelectSessionWindow failed.", query.getWindowExpression().get().toString().equalsIgnoreCase((" WINDOW STREAMWINDOW  SESSION " + "( 30 SECONDS ) ")));
    }

    @Test
    public void testShowTopics() {
        final String simpleQuery = "SHOW TOPICS;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertTrue((statement instanceof ListTopics));
        final ListTopics listTopics = ((ListTopics) (statement));
        Assert.assertTrue(listTopics.toString().equalsIgnoreCase("ListTopics{}"));
    }

    @Test
    public void testShowStreams() {
        final String simpleQuery = "SHOW STREAMS;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertTrue((statement instanceof ListStreams));
        final ListStreams listStreams = ((ListStreams) (statement));
        Assert.assertTrue(listStreams.toString().equalsIgnoreCase("ListStreams{}"));
        Assert.assertThat(listStreams.getShowExtended(), CoreMatchers.is(false));
    }

    @Test
    public void testShowTables() {
        final String simpleQuery = "SHOW TABLES;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertTrue((statement instanceof ListTables));
        final ListTables listTables = ((ListTables) (statement));
        Assert.assertTrue(listTables.toString().equalsIgnoreCase("ListTables{}"));
        Assert.assertThat(listTables.getShowExtended(), CoreMatchers.is(false));
    }

    @Test
    public void shouldReturnListQueriesForShowQueries() {
        final String statementString = "SHOW QUERIES;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(ListQueries.class));
        final ListQueries listQueries = ((ListQueries) (statement));
        Assert.assertThat(listQueries.getShowExtended(), CoreMatchers.is(false));
    }

    @Test
    public void testShowProperties() {
        final String simpleQuery = "SHOW PROPERTIES;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertTrue((statement instanceof ListProperties));
        final ListProperties listProperties = ((ListProperties) (statement));
        Assert.assertTrue(listProperties.toString().equalsIgnoreCase("ListProperties{}"));
    }

    @Test
    public void testSetProperties() {
        final String simpleQuery = "set 'auto.offset.reset'='earliest';";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertTrue((statement instanceof SetProperty));
        final SetProperty setProperty = ((SetProperty) (statement));
        Assert.assertTrue(setProperty.toString().equalsIgnoreCase("SetProperty{}"));
        Assert.assertTrue(setProperty.getPropertyName().equalsIgnoreCase("auto.offset.reset"));
        Assert.assertTrue(setProperty.getPropertyValue().equalsIgnoreCase("earliest"));
    }

    @Test
    public void testSelectSinkProperties() {
        final String simpleQuery = "create stream s1 with (timestamp='orderid', partitions = 3) as select " + (("col1, col2" + " from orders where col2 is null and col3 is not null or (col3*col2 = ") + "12);");
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertTrue("testSelectTumblingWindow failed.", (statement instanceof CreateStreamAsSelect));
        final Query query = getQuery();
        Assert.assertTrue(query.getWhere().toString().equalsIgnoreCase("Optional[(((ORDERS.COL2 IS NULL) AND (ORDERS.COL3 IS NOT NULL)) OR ((ORDERS.COL3 * ORDERS.COL2) = 12))]"));
    }

    @Test
    public void shouldParseDropStream() {
        final String simpleQuery = "DROP STREAM STREAM1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(DropStream.class));
        final DropStream dropStream = ((DropStream) (statement));
        Assert.assertThat(dropStream.getName().toString().toUpperCase(), CoreMatchers.equalTo("STREAM1"));
        Assert.assertThat(dropStream.getIfExists(), CoreMatchers.is(false));
    }

    @Test
    public void shouldParseDropTable() {
        final String simpleQuery = "DROP TABLE TABLE1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(DropTable.class));
        final DropTable dropTable = ((DropTable) (statement));
        Assert.assertThat(dropTable.getName().toString().toUpperCase(), CoreMatchers.equalTo("TABLE1"));
        Assert.assertThat(dropTable.getIfExists(), CoreMatchers.is(false));
    }

    @Test
    public void shouldParseDropStreamIfExists() {
        final String simpleQuery = "DROP STREAM IF EXISTS STREAM1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(DropStream.class));
        final DropStream dropStream = ((DropStream) (statement));
        Assert.assertThat(dropStream.getName().toString().toUpperCase(), CoreMatchers.equalTo("STREAM1"));
        Assert.assertThat(dropStream.getIfExists(), CoreMatchers.is(true));
    }

    @Test
    public void shouldParseDropTableIfExists() {
        final String simpleQuery = "DROP TABLE IF EXISTS TABLE1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(DropTable.class));
        final DropTable dropTable = ((DropTable) (statement));
        Assert.assertThat(dropTable.getName().toString().toUpperCase(), CoreMatchers.equalTo("TABLE1"));
        Assert.assertThat(dropTable.getIfExists(), CoreMatchers.is(true));
    }

    @Test
    public void testInsertInto() {
        final String insertIntoString = "INSERT INTO test0 " + "SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(insertIntoString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(InsertInto.class));
        final InsertInto insertInto = ((InsertInto) (statement));
        Assert.assertThat(insertInto.getTarget().toString(), CoreMatchers.equalTo("TEST0"));
        final Query query = insertInto.getQuery();
        Assert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(3));
        Assert.assertThat(query.getFrom(), IsNot.not(CoreMatchers.nullValue()));
        Assert.assertThat(query.getWhere().isPresent(), CoreMatchers.equalTo(true));
        Assert.assertThat(query.getWhere().get(), IsInstanceOf.instanceOf(ComparisonExpression.class));
        final ComparisonExpression comparisonExpression = ((ComparisonExpression) (query.getWhere().get()));
        Assert.assertThat(getValue(), CoreMatchers.equalTo(">"));
    }

    @Test
    public void shouldSetShowDescriptionsForShowStreamsDescriptions() {
        final String statementString = "SHOW STREAMS EXTENDED;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(ListStreams.class));
        final ListStreams listStreams = ((ListStreams) (statement));
        Assert.assertThat(listStreams.getShowExtended(), CoreMatchers.is(true));
    }

    @Test
    public void shouldSetShowDescriptionsForShowTablesDescriptions() {
        final String statementString = "SHOW TABLES EXTENDED;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(ListTables.class));
        final ListTables listTables = ((ListTables) (statement));
        Assert.assertThat(listTables.getShowExtended(), CoreMatchers.is(true));
    }

    @Test
    public void shouldSetShowDescriptionsForShowQueriesDescriptions() {
        final String statementString = "SHOW QUERIES EXTENDED;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(ListQueries.class));
        final ListQueries listQueries = ((ListQueries) (statement));
        Assert.assertThat(listQueries.getShowExtended(), CoreMatchers.is(true));
    }

    @Test
    public void shouldSetWithinExpressionWithSingleWithin() {
        final String statementString = "CREATE STREAM foobar as SELECT * from TEST1 JOIN ORDERS WITHIN " + "10 SECONDS ON TEST1.col1 = ORDERS.ORDERID ;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertTrue(join.getWithinExpression().isPresent());
        final WithinExpression withinExpression = join.getWithinExpression().get();
        Assert.assertEquals(10L, withinExpression.getBefore());
        Assert.assertEquals(10L, withinExpression.getAfter());
        Assert.assertEquals(TimeUnit.SECONDS, withinExpression.getBeforeTimeUnit());
        Assert.assertEquals(INNER, join.getType());
    }

    @Test
    public void shouldSetWithinExpressionWithBeforeAndAfter() {
        final String statementString = "CREATE STREAM foobar as SELECT * from TEST1 JOIN ORDERS " + ("WITHIN (10 seconds, 20 minutes) " + "ON TEST1.col1 = ORDERS.ORDERID ;");
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertTrue(join.getWithinExpression().isPresent());
        final WithinExpression withinExpression = join.getWithinExpression().get();
        Assert.assertEquals(10L, withinExpression.getBefore());
        Assert.assertEquals(20L, withinExpression.getAfter());
        Assert.assertEquals(TimeUnit.SECONDS, withinExpression.getBeforeTimeUnit());
        Assert.assertEquals(TimeUnit.MINUTES, withinExpression.getAfterTimeUnit());
        Assert.assertEquals(INNER, join.getType());
    }

    @Test
    public void shouldHaveInnerJoinTypeWithExplicitInnerKeyword() {
        final String statementString = "CREATE STREAM foobar as SELECT * from TEST1 INNER JOIN TEST2 " + "ON TEST1.col1 = TEST2.col1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertEquals(INNER, join.getType());
    }

    @Test
    public void shouldHaveLeftJoinTypeWhenOuterIsSpecified() {
        final String statementString = "CREATE STREAM foobar as SELECT * from TEST1 LEFT OUTER JOIN " + "TEST2 ON TEST1.col1 = TEST2.col1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertEquals(LEFT, join.getType());
    }

    @Test
    public void shouldHaveLeftJoinType() {
        final String statementString = "CREATE STREAM foobar as SELECT * from TEST1 LEFT JOIN " + "TEST2 ON TEST1.col1 = TEST2.col1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertEquals(LEFT, join.getType());
    }

    @Test
    public void shouldHaveOuterJoinType() {
        final String statementString = "CREATE STREAM foobar as SELECT * from TEST1 FULL JOIN " + "TEST2 ON TEST1.col1 = TEST2.col1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertEquals(OUTER, join.getType());
    }

    @Test
    public void shouldHaveOuterJoinTypeWhenOuterKeywordIsSpecified() {
        final String statementString = "CREATE STREAM foobar as SELECT * from TEST1 FULL OUTER JOIN " + "TEST2 ON TEST1.col1 = TEST2.col1;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (statement));
        final Query query = createStreamAsSelect.getQuery();
        Assert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        Assert.assertEquals(OUTER, join.getType());
    }

    @Test
    public void shouldAddPrefixEvenIfColumnNameIsTheSameAsStream() {
        final String statementString = "CREATE STREAM S AS SELECT address FROM address a;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final Query query = getQuery();
        Assert.assertThat(query.getSelect().getSelectItems().get(0), KsqlParserTest.equalToColumn("A.ADDRESS", "ADDRESS"));
    }

    @Test
    public void shouldNotAddPrefixIfStreamNameIsPrefix() {
        final String statementString = "CREATE STREAM S AS SELECT address.orderid FROM address a;";
        KsqlParserTestUtil.buildSingleAst(statementString, metaStore);
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final Query query = getQuery();
        Assert.assertThat(query.getSelect().getSelectItems().get(0), KsqlParserTest.equalToColumn("ADDRESS.ORDERID", "ORDERID"));
    }

    @Test
    public void shouldPassIfStreamColumnNameWithAliasIsNotAmbiguous() {
        final String statementString = "CREATE STREAM S AS SELECT a.address->city FROM address a;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final Query query = getQuery();
        Assert.assertThat(query.getSelect().getSelectItems().get(0), KsqlParserTest.equalToColumn("FETCH_FIELD_FROM_STRUCT(A.ADDRESS, 'CITY')", "ADDRESS__CITY"));
    }

    @Test
    public void shouldPassIfStreamColumnNameIsNotAmbiguous() {
        final String statementString = "CREATE STREAM S AS SELECT address.address->city FROM address a;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final Query query = getQuery();
        final SelectItem item = query.getSelect().getSelectItems().get(0);
        Assert.assertThat(item, KsqlParserTest.equalToColumn("FETCH_FIELD_FROM_STRUCT(ADDRESS.ADDRESS, 'CITY')", "ADDRESS__CITY"));
    }

    @Test(expected = KsqlException.class)
    public void shouldFailJoinQueryParseIfStreamColumnNameWithNoAliasIsAmbiguous() {
        final String statementString = "CREATE STREAM S AS SELECT itemid FROM address a JOIN itemid on a.itemid = itemid.itemid;";
        KsqlParserTestUtil.buildSingleAst(statementString, metaStore);
    }

    @Test
    public void shouldPassJoinQueryParseIfStreamColumnNameWithAliasIsNotAmbiguous() {
        final String statementString = "CREATE STREAM S AS SELECT itemid.itemid FROM address a JOIN itemid on a.itemid = itemid.itemid;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        Assert.assertThat(statement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final Query query = getQuery();
        Assert.assertThat(query.getSelect().getSelectItems().get(0), KsqlParserTest.equalToColumn("ITEMID.ITEMID", "ITEMID_ITEMID"));
    }

    @Test
    public void testSelectWithOnlyColumns() {
        expectedException.expect(ParseFailedException.class);
        expectedException.expectMessage("line 1:21: extraneous input ';' expecting {',', 'FROM'}");
        final String simpleQuery = "SELECT ONLY, COLUMNS;";
        KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore);
    }

    @Test
    public void testSelectWithMissingComma() {
        expectedException.expect(ParseFailedException.class);
        expectedException.expectMessage(CoreMatchers.containsString("line 1:12: extraneous input 'C' expecting"));
        final String simpleQuery = "SELECT A B C FROM address;";
        KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore);
    }

    @Test
    public void testSelectWithMultipleFroms() {
        expectedException.expect(ParseFailedException.class);
        expectedException.expectMessage(CoreMatchers.containsString("line 1:22: mismatched input ',' expecting"));
        final String simpleQuery = "SELECT * FROM address, itemid;";
        KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore);
    }

    @Test
    public void shouldParseSimpleComment() {
        final String statementString = "--this is a comment.\n" + "SHOW STREAMS;";
        final List<PreparedStatement<?>> statements = KsqlParserTestUtil.buildAst(statementString, metaStore);
        Assert.assertThat(statements, Matchers.hasSize(1));
        Assert.assertThat(statements.get(0).getStatement(), CoreMatchers.is(IsInstanceOf.instanceOf(ListStreams.class)));
    }

    @Test
    public void shouldParseBracketedComment() {
        final String statementString = "/* this is a bracketed comment. */\n" + ("SHOW STREAMS;" + "/*another comment!*/");
        final List<PreparedStatement<?>> statements = KsqlParserTestUtil.buildAst(statementString, metaStore);
        Assert.assertThat(statements, Matchers.hasSize(1));
        Assert.assertThat(statements.get(0).getStatement(), CoreMatchers.is(IsInstanceOf.instanceOf(ListStreams.class)));
    }

    @Test
    public void shouldParseMultiLineWithInlineComments() {
        final String statementString = "SHOW -- inline comment\n" + "STREAMS;";
        final List<PreparedStatement<?>> statements = KsqlParserTestUtil.buildAst(statementString, metaStore);
        Assert.assertThat(statements, Matchers.hasSize(1));
        Assert.assertThat(statements.get(0).getStatement(), CoreMatchers.is(IsInstanceOf.instanceOf(ListStreams.class)));
    }

    @Test
    public void shouldParseMultiLineWithInlineBracketedComments() {
        final String statementString = "SHOW /* inline\n" + ("comment */\n" + "STREAMS;");
        final List<PreparedStatement<?>> statements = KsqlParserTestUtil.buildAst(statementString, metaStore);
        Assert.assertThat(statements, Matchers.hasSize(1));
        Assert.assertThat(statements.get(0).getStatement(), CoreMatchers.is(IsInstanceOf.instanceOf(ListStreams.class)));
    }

    @Test
    public void shouldBuildSearchedCaseStatement() {
        // Given:
        final String statementString = "CREATE STREAM S AS SELECT CASE WHEN orderunits < 10 THEN 'small' WHEN orderunits < 100 THEN 'medium' ELSE 'large' END FROM orders;";
        // When:
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        // Then:
        final SearchedCaseExpression searchedCaseExpression = KsqlParserTest.getSearchedCaseExpressionFromCsas(statement);
        Assert.assertThat(searchedCaseExpression.getWhenClauses().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(searchedCaseExpression.getWhenClauses().get(0).getOperand().toString(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS < 10)"));
        Assert.assertThat(searchedCaseExpression.getWhenClauses().get(0).getResult().toString(), CoreMatchers.equalTo("'small'"));
        Assert.assertThat(searchedCaseExpression.getWhenClauses().get(1).getOperand().toString(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS < 100)"));
        Assert.assertThat(searchedCaseExpression.getWhenClauses().get(1).getResult().toString(), CoreMatchers.equalTo("'medium'"));
        Assert.assertTrue(searchedCaseExpression.getDefaultValue().isPresent());
        Assert.assertThat(searchedCaseExpression.getDefaultValue().get().toString(), CoreMatchers.equalTo("'large'"));
    }

    @Test
    public void shouldBuildSearchedCaseWithoutDefaultStatement() {
        // Given:
        final String statementString = "CREATE STREAM S AS SELECT CASE WHEN orderunits < 10 THEN 'small' WHEN orderunits < 100 THEN 'medium' END FROM orders;";
        // When:
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        // Then:
        final SearchedCaseExpression searchedCaseExpression = KsqlParserTest.getSearchedCaseExpressionFromCsas(statement);
        Assert.assertThat(searchedCaseExpression.getDefaultValue().isPresent(), CoreMatchers.equalTo(false));
    }
}

