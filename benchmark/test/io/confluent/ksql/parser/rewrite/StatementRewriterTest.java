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
package io.confluent.ksql.parser.rewrite;


import DdlConfig.KAFKA_TOPIC_NAME_PROPERTY;
import DdlConfig.TOPIC_NAME_PROPERTY;
import DdlConfig.VALUE_FORMAT_PROPERTY;
import SqlType.STRING;
import SqlType.STRUCT;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.parser.tree.ComparisonExpression;
import io.confluent.ksql.parser.tree.CreateStream;
import io.confluent.ksql.parser.tree.CreateStreamAsSelect;
import io.confluent.ksql.parser.tree.CreateTable;
import io.confluent.ksql.parser.tree.InsertInto;
import io.confluent.ksql.parser.tree.Join;
import io.confluent.ksql.parser.tree.Query;
import io.confluent.ksql.parser.tree.SingleColumn;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.parser.tree.Struct;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class StatementRewriterTest {
    private MetaStore metaStore;

    @Test
    public void testProjection() {
        final String queryStr = "SELECT col0, col2, col3 FROM test1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().get(0), IsInstanceOf.instanceOf(SingleColumn.class));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        MatcherAssert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("COL0"));
        MatcherAssert.assertThat(column0.getExpression().toString(), CoreMatchers.equalTo("TEST1.COL0"));
    }

    @Test
    public void testProjectionWithArrayMap() {
        final String queryStr = "SELECT col0, col2, col3, col4[0], col5['key1'] FROM test1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(5));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().get(0), IsInstanceOf.instanceOf(SingleColumn.class));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        MatcherAssert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("COL0"));
        MatcherAssert.assertThat(column0.getExpression().toString(), CoreMatchers.equalTo("TEST1.COL0"));
        final SingleColumn column3 = ((SingleColumn) (query.getSelect().getSelectItems().get(3)));
        final SingleColumn column4 = ((SingleColumn) (query.getSelect().getSelectItems().get(4)));
        MatcherAssert.assertThat(column3.getExpression().toString(), CoreMatchers.equalTo("TEST1.COL4[0]"));
        MatcherAssert.assertThat(column4.getExpression().toString(), CoreMatchers.equalTo("TEST1.COL5['key1']"));
    }

    @Test
    public void testProjectFilter() {
        final String queryStr = "SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getWhere().get(), IsInstanceOf.instanceOf(ComparisonExpression.class));
        final ComparisonExpression comparisonExpression = ((ComparisonExpression) (query.getWhere().get()));
        MatcherAssert.assertThat(comparisonExpression.toString(), CoreMatchers.equalTo("(TEST1.COL0 > 100)"));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testBinaryExpression() {
        final String queryStr = "SELECT col0+10, col2, col3-col1 FROM test1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        MatcherAssert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_0"));
        MatcherAssert.assertThat(column0.getExpression().toString(), CoreMatchers.equalTo("(TEST1.COL0 + 10)"));
    }

    @Test
    public void testBooleanExpression() {
        final String queryStr = "SELECT col0 = 10, col2, col3 > col1 FROM test1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        MatcherAssert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_0"));
        MatcherAssert.assertThat(column0.getExpression().toString(), CoreMatchers.equalTo("(TEST1.COL0 = 10)"));
        MatcherAssert.assertThat(column0.getExpression(), IsInstanceOf.instanceOf(ComparisonExpression.class));
    }

    @Test
    public void testLiterals() {
        final String queryStr = "SELECT 10, col2, 'test', 2.5, true, -5 FROM test1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        MatcherAssert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_0"));
        MatcherAssert.assertThat(column0.getExpression().toString(), CoreMatchers.equalTo("10"));
        final SingleColumn column1 = ((SingleColumn) (query.getSelect().getSelectItems().get(1)));
        MatcherAssert.assertThat(column1.getAlias().get(), CoreMatchers.equalTo("COL2"));
        MatcherAssert.assertThat(column1.getExpression().toString(), CoreMatchers.equalTo("TEST1.COL2"));
        final SingleColumn column2 = ((SingleColumn) (query.getSelect().getSelectItems().get(2)));
        MatcherAssert.assertThat(column2.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_2"));
        MatcherAssert.assertThat(column2.getExpression().toString(), CoreMatchers.equalTo("'test'"));
        final SingleColumn column3 = ((SingleColumn) (query.getSelect().getSelectItems().get(3)));
        MatcherAssert.assertThat(column3.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_3"));
        MatcherAssert.assertThat(column3.getExpression().toString(), CoreMatchers.equalTo("2.5"));
        final SingleColumn column4 = ((SingleColumn) (query.getSelect().getSelectItems().get(4)));
        MatcherAssert.assertThat(column4.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_4"));
        MatcherAssert.assertThat(column4.getExpression().toString(), CoreMatchers.equalTo("true"));
        final SingleColumn column5 = ((SingleColumn) (query.getSelect().getSelectItems().get(5)));
        MatcherAssert.assertThat(column5.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_5"));
        MatcherAssert.assertThat(column5.getExpression().toString(), CoreMatchers.equalTo("-5"));
    }

    @Test
    public void testBooleanLogicalExpression() {
        final String queryStr = "SELECT 10, col2, 'test', 2.5, true, -5 FROM test1 WHERE col1 = 10 AND col2 LIKE 'val' OR col4 > 2.6 ;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        MatcherAssert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_0"));
        MatcherAssert.assertThat(column0.getExpression().toString(), CoreMatchers.equalTo("10"));
        final SingleColumn column1 = ((SingleColumn) (query.getSelect().getSelectItems().get(1)));
        MatcherAssert.assertThat(column1.getAlias().get(), CoreMatchers.equalTo("COL2"));
        MatcherAssert.assertThat(column1.getExpression().toString(), CoreMatchers.equalTo("TEST1.COL2"));
        final SingleColumn column2 = ((SingleColumn) (query.getSelect().getSelectItems().get(2)));
        MatcherAssert.assertThat(column2.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_2"));
        MatcherAssert.assertThat(column2.getExpression().toString(), CoreMatchers.equalTo("'test'"));
    }

    @Test
    public void testSimpleLeftJoin() {
        final String queryStr = "SELECT t1.col1, t2.col1, t2.col4, col5, t2.col2 FROM test1 t1 LEFT JOIN test2 t2 ON " + "t1.col1 = t2.col1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        MatcherAssert.assertThat(join.getType().toString(), CoreMatchers.equalTo("LEFT"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("T1"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("T2"));
    }

    @Test
    public void testLeftJoinWithFilter() {
        final String queryStr = "SELECT t1.col1, t2.col1, t2.col4, t2.col2 FROM test1 t1 LEFT JOIN test2 t2 ON t1.col1 = " + "t2.col1 WHERE t2.col2 = 'test';";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        MatcherAssert.assertThat(join.getType().toString(), CoreMatchers.equalTo("LEFT"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("T1"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("T2"));
        MatcherAssert.assertThat(query.getWhere().get().toString(), CoreMatchers.equalTo("(T2.COL2 = 'test')"));
    }

    @Test
    public void testSelectAll() {
        final String queryStr = "SELECT * FROM test1 t1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(8));
    }

    @Test
    public void testSelectAllJoin() {
        final String queryStr = "SELECT * FROM test1 t1 LEFT JOIN test2 t2 ON t1.col1 = t2.col1 WHERE t2.col2 = 'test';";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getFrom(), IsInstanceOf.instanceOf(Join.class));
        final Join join = ((Join) (query.getFrom()));
        MatcherAssert.assertThat(query.getSelect().getSelectItems(), Matchers.hasSize(15));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("T1"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("T2"));
    }

    @Test
    public void testUDF() {
        final String queryStr = "SELECT lcase(col1), concat(col2,'hello'), floor(abs(col3)) FROM test1 t1;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        final SingleColumn column0 = ((SingleColumn) (query.getSelect().getSelectItems().get(0)));
        MatcherAssert.assertThat(column0.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_0"));
        MatcherAssert.assertThat(column0.getExpression().toString(), CoreMatchers.equalTo("LCASE(T1.COL1)"));
        final SingleColumn column1 = ((SingleColumn) (query.getSelect().getSelectItems().get(1)));
        MatcherAssert.assertThat(column1.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_1"));
        MatcherAssert.assertThat(column1.getExpression().toString(), CoreMatchers.equalTo("CONCAT(T1.COL2, 'hello')"));
        final SingleColumn column2 = ((SingleColumn) (query.getSelect().getSelectItems().get(2)));
        MatcherAssert.assertThat(column2.getAlias().get(), CoreMatchers.equalTo("KSQL_COL_2"));
        MatcherAssert.assertThat(column2.getExpression().toString(), CoreMatchers.equalTo("FLOOR(ABS(T1.COL3))"));
    }

    @Test
    public void testCreateStreamWithTopic() {
        final String queryStr = "CREATE STREAM orders (ordertime bigint, orderid varchar, itemid varchar, orderunits " + "double) WITH (registered_topic = 'orders_topic' , key='ordertime');";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(CreateStream.class));
        final CreateStream createStream = ((CreateStream) (rewrittenStatement));
        MatcherAssert.assertThat(createStream.getName().toString(), CoreMatchers.equalTo("ORDERS"));
        MatcherAssert.assertThat(createStream.getElements().size(), CoreMatchers.equalTo(4));
        MatcherAssert.assertThat(createStream.getElements().get(0).getName(), CoreMatchers.equalTo("ORDERTIME"));
        MatcherAssert.assertThat(createStream.getProperties().get(TOPIC_NAME_PROPERTY).toString(), CoreMatchers.equalTo("'orders_topic'"));
    }

    @Test
    public void testCreateStreamWithTopicWithStruct() {
        final String queryStr = "CREATE STREAM orders (ordertime bigint, orderid varchar, itemid varchar, orderunits " + (("double, arraycol array<double>, mapcol map<varchar, double>, " + "order_address STRUCT< number VARCHAR, street VARCHAR, zip INTEGER, city ") + "VARCHAR, state VARCHAR >) WITH (registered_topic = 'orders_topic' , key='ordertime');");
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(CreateStream.class));
        final CreateStream createStream = ((CreateStream) (rewrittenStatement));
        MatcherAssert.assertThat(createStream.getName().toString().toUpperCase(), CoreMatchers.equalTo("ORDERS"));
        MatcherAssert.assertThat(createStream.getElements().size(), CoreMatchers.equalTo(7));
        MatcherAssert.assertThat(createStream.getElements().get(0).getName().toLowerCase(), CoreMatchers.equalTo("ordertime"));
        MatcherAssert.assertThat(createStream.getElements().get(6).getType().getSqlType(), CoreMatchers.equalTo(STRUCT));
        final Struct struct = ((Struct) (createStream.getElements().get(6).getType()));
        MatcherAssert.assertThat(struct.getFields(), Matchers.hasSize(5));
        MatcherAssert.assertThat(struct.getFields().get(0).getType().getSqlType(), CoreMatchers.equalTo(STRING));
        MatcherAssert.assertThat(createStream.getProperties().get(TOPIC_NAME_PROPERTY).toString().toLowerCase(), CoreMatchers.equalTo("'orders_topic'"));
    }

    @Test
    public void testCreateStream() {
        final String queryStr = "CREATE STREAM orders " + ("(ordertime bigint, orderid varchar, itemid varchar, orderunits double) " + "WITH (value_format = 'avro',kafka_topic='orders_topic');");
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(CreateStream.class));
        final CreateStream createStream = ((CreateStream) (rewrittenStatement));
        MatcherAssert.assertThat(createStream.getName().toString(), CoreMatchers.equalTo("ORDERS"));
        MatcherAssert.assertThat(createStream.getElements().size(), CoreMatchers.equalTo(4));
        MatcherAssert.assertThat(createStream.getElements().get(0).getName(), CoreMatchers.equalTo("ORDERTIME"));
        MatcherAssert.assertThat(createStream.getProperties().get(KAFKA_TOPIC_NAME_PROPERTY).toString(), CoreMatchers.equalTo("'orders_topic'"));
        MatcherAssert.assertThat(createStream.getProperties().get(VALUE_FORMAT_PROPERTY).toString(), CoreMatchers.equalTo("'avro'"));
    }

    @Test
    public void testCreateTableWithTopic() {
        final String queryStr = "CREATE TABLE users (usertime bigint, userid varchar, regionid varchar, gender varchar) WITH (registered_topic = 'users_topic', key='userid', statestore='user_statestore');";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat("testRegisterTopic failed.", (rewrittenStatement instanceof CreateTable));
        final CreateTable createTable = ((CreateTable) (rewrittenStatement));
        MatcherAssert.assertThat(createTable.getName().toString(), CoreMatchers.equalTo("USERS"));
        MatcherAssert.assertThat(createTable.getElements().size(), CoreMatchers.equalTo(4));
        MatcherAssert.assertThat(createTable.getElements().get(0).getName(), CoreMatchers.equalTo("USERTIME"));
        MatcherAssert.assertThat(createTable.getProperties().get(TOPIC_NAME_PROPERTY).toString(), CoreMatchers.equalTo("'users_topic'"));
    }

    @Test
    public void testCreateTable() {
        final String queryStr = "CREATE TABLE users (usertime bigint, userid varchar, regionid varchar, gender varchar) " + "WITH (kafka_topic = 'users_topic', value_format='json', key = 'userid');";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat("testRegisterTopic failed.", (rewrittenStatement instanceof CreateTable));
        final CreateTable createTable = ((CreateTable) (rewrittenStatement));
        MatcherAssert.assertThat(createTable.getName().toString(), CoreMatchers.equalTo("USERS"));
        MatcherAssert.assertThat(createTable.getElements().size(), CoreMatchers.equalTo(4));
        MatcherAssert.assertThat(createTable.getElements().get(0).getName(), CoreMatchers.equalTo("USERTIME"));
        MatcherAssert.assertThat(createTable.getProperties().get(KAFKA_TOPIC_NAME_PROPERTY).toString(), CoreMatchers.equalTo("'users_topic'"));
        MatcherAssert.assertThat(createTable.getProperties().get(VALUE_FORMAT_PROPERTY).toString(), CoreMatchers.equalTo("'json'"));
    }

    @Test
    public void testCreateStreamAsSelect() {
        final String queryStr = "CREATE STREAM bigorders_json WITH (value_format = 'json', " + "kafka_topic='bigorders_topic') AS SELECT * FROM orders WHERE orderunits > 5 ;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat("testCreateStreamAsSelect failed.", (rewrittenStatement instanceof CreateStreamAsSelect));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (rewrittenStatement));
        MatcherAssert.assertThat(createStreamAsSelect.getName().toString(), CoreMatchers.equalTo("BIGORDERS_JSON"));
        final Query query = createStreamAsSelect.getQuery();
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(8));
        MatcherAssert.assertThat(query.getWhere().get().toString(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS > 5)"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("ORDERS"));
    }

    @Test
    public void testSelectTumblingWindow() {
        final String queryStr = "select itemid, sum(orderunits) from orders window TUMBLING ( size 30 second) where orderunits > 5 group by itemid;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(query.getWhere().get().toString(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS > 5)"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("ORDERS"));
        Assert.assertTrue(query.getWindowExpression().isPresent());
        MatcherAssert.assertThat(query.getWindowExpression().get().toString(), CoreMatchers.equalTo(" WINDOW STREAMWINDOW  TUMBLING ( SIZE 30 SECONDS ) "));
    }

    @Test
    public void testSelectHoppingWindow() {
        final String queryStr = "select itemid, sum(orderunits) from orders window HOPPING ( size 30 second, advance by 5" + (((" seconds) " + "where ") + "orderunits") + " > 5 group by itemid;");
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(query.getWhere().get().toString(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS > 5)"));
        MatcherAssert.assertThat(getAlias().toUpperCase(), CoreMatchers.equalTo("ORDERS"));
        MatcherAssert.assertThat("window expression isn't present", query.getWindowExpression().isPresent());
        MatcherAssert.assertThat(query.getWindowExpression().get().toString().toUpperCase(), CoreMatchers.equalTo(" WINDOW STREAMWINDOW  HOPPING ( SIZE 30 SECONDS , ADVANCE BY 5 SECONDS ) "));
    }

    @Test
    public void testSelectSessionWindow() {
        final String queryStr = "select itemid, sum(orderunits) from orders window SESSION ( 30 second) where " + "orderunits > 5 group by itemid;";
        final Statement statement = parse(queryStr);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(Query.class));
        final Query query = ((Query) (rewrittenStatement));
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(query.getWhere().get().toString(), CoreMatchers.equalTo("(ORDERS.ORDERUNITS > 5)"));
        MatcherAssert.assertThat(getAlias(), CoreMatchers.equalTo("ORDERS"));
        Assert.assertTrue(query.getWindowExpression().isPresent());
        MatcherAssert.assertThat(query.getWindowExpression().get().toString(), CoreMatchers.equalTo((" WINDOW STREAMWINDOW  SESSION " + "( 30 SECONDS ) ")));
    }

    @Test
    public void testSelectSinkProperties() {
        final String simpleQuery = "create stream s1 with (timestamp='orderid', partitions = 3) as select " + (("col1, col2" + " from orders where col2 is null and col3 is not null or (col3*col2 = ") + "12);");
        final Statement statement = parse(simpleQuery);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(CreateStreamAsSelect.class));
        final CreateStreamAsSelect createStreamAsSelect = ((CreateStreamAsSelect) (rewrittenStatement));
        final Query query = createStreamAsSelect.getQuery();
        MatcherAssert.assertThat(query.getWhere().toString(), CoreMatchers.equalTo("Optional[(((ORDERS.COL2 IS NULL) AND (ORDERS.COL3 IS NOT NULL)) OR ((ORDERS.COL3 * ORDERS.COL2) = 12))]"));
    }

    @Test
    public void testInsertInto() {
        final String insertIntoString = "INSERT INTO test0 " + "SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;";
        final Statement statement = parse(insertIntoString);
        final StatementRewriter statementRewriter = new StatementRewriter();
        final Statement rewrittenStatement = ((Statement) (statementRewriter.process(statement, null)));
        MatcherAssert.assertThat(rewrittenStatement, IsInstanceOf.instanceOf(InsertInto.class));
        final InsertInto insertInto = ((InsertInto) (rewrittenStatement));
        MatcherAssert.assertThat(insertInto.getTarget().toString(), CoreMatchers.equalTo("TEST0"));
        final Query query = insertInto.getQuery();
        MatcherAssert.assertThat(query.getSelect().getSelectItems().size(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(query.getFrom(), IsNot.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(query.getWhere().isPresent(), CoreMatchers.equalTo(true));
        MatcherAssert.assertThat(query.getWhere().get(), IsInstanceOf.instanceOf(ComparisonExpression.class));
        final ComparisonExpression comparisonExpression = ((ComparisonExpression) (query.getWhere().get()));
        MatcherAssert.assertThat(comparisonExpression.getType().getValue(), CoreMatchers.equalTo(">"));
    }
}

