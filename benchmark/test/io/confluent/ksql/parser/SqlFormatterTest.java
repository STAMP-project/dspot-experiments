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


import DdlConfig.KAFKA_TOPIC_NAME_PROPERTY;
import DdlConfig.TOPIC_NAME_PROPERTY;
import Join.Type;
import Schema.FLOAT64_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import SqlType.STRING;
import io.confluent.ksql.function.TestFunctionRegistry;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.tree.AliasedRelation;
import io.confluent.ksql.parser.tree.CreateStream;
import io.confluent.ksql.parser.tree.Join;
import io.confluent.ksql.parser.tree.JoinCriteria;
import io.confluent.ksql.parser.tree.NodeLocation;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.QualifiedName;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.parser.tree.StringLiteral;
import io.confluent.ksql.parser.tree.TableElement;
import io.confluent.ksql.parser.tree.WithinExpression;
import io.confluent.ksql.util.MetaStoreFixture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;


public class SqlFormatterTest {
    private AliasedRelation leftAlias;

    private AliasedRelation rightAlias;

    private JoinCriteria criteria;

    private NodeLocation location;

    private MutableMetaStore metaStore;

    private static final Schema addressSchema = SchemaBuilder.struct().field("NUMBER", OPTIONAL_INT64_SCHEMA).field("STREET", OPTIONAL_STRING_SCHEMA).field("CITY", OPTIONAL_STRING_SCHEMA).field("STATE", OPTIONAL_STRING_SCHEMA).field("ZIPCODE", OPTIONAL_INT64_SCHEMA).optional().build();

    private static final Schema categorySchema = SchemaBuilder.struct().field("ID", OPTIONAL_INT64_SCHEMA).field("NAME", OPTIONAL_STRING_SCHEMA).optional().build();

    private static final Schema itemInfoSchema = SchemaBuilder.struct().field("ITEMID", INT64_SCHEMA).field("NAME", STRING_SCHEMA).field("CATEGORY", SqlFormatterTest.categorySchema).optional().build();

    private static final SchemaBuilder schemaBuilder = SchemaBuilder.struct();

    private static final Schema schemaBuilderOrders = SqlFormatterTest.schemaBuilder.field("ORDERTIME", INT64_SCHEMA).field("ORDERID", OPTIONAL_INT64_SCHEMA).field("ITEMID", OPTIONAL_STRING_SCHEMA).field("ITEMINFO", SqlFormatterTest.itemInfoSchema).field("ORDERUNITS", INT32_SCHEMA).field("ARRAYCOL", SchemaBuilder.array(FLOAT64_SCHEMA).optional().build()).field("MAPCOL", SchemaBuilder.map(STRING_SCHEMA, FLOAT64_SCHEMA).optional().build()).field("ADDRESS", SqlFormatterTest.addressSchema).build();

    @Test
    public void testFormatSql() {
        final ArrayList<TableElement> tableElements = new ArrayList<>();
        tableElements.add(new TableElement("GROUP", PrimitiveType.of(STRING)));
        tableElements.add(new TableElement("NOLIT", PrimitiveType.of(STRING)));
        tableElements.add(new TableElement("Having", PrimitiveType.of(STRING)));
        final CreateStream createStream = new CreateStream(QualifiedName.of("TEST"), tableElements, false, Collections.singletonMap(TOPIC_NAME_PROPERTY, new StringLiteral("topic_test")));
        final String sql = SqlFormatter.formatSql(createStream);
        MatcherAssert.assertThat("literal escaping failure", sql, StringContains.containsString("`GROUP` STRING"));
        MatcherAssert.assertThat("not literal escaping failure", sql, StringContains.containsString("NOLIT STRING"));
        MatcherAssert.assertThat("lowercase literal escaping failure", sql, StringContains.containsString("`Having` STRING"));
        final List<PreparedStatement<?>> statements = KsqlParserTestUtil.buildAst(sql, MetaStoreFixture.getNewMetaStore(new TestFunctionRegistry()));
        Assert.assertFalse("formatted sql parsing error", statements.isEmpty());
    }

    @Test
    public void shouldFormatCreateWithEmptySchema() {
        final CreateStream createStream = new CreateStream(QualifiedName.of("TEST"), Collections.emptyList(), false, Collections.singletonMap(KAFKA_TOPIC_NAME_PROPERTY, new StringLiteral("topic_test")));
        final String sql = SqlFormatter.formatSql(createStream);
        final String expectedSql = "CREATE STREAM TEST  WITH (KAFKA_TOPIC='topic_test');";
        MatcherAssert.assertThat(sql, CoreMatchers.equalTo(expectedSql));
    }

    @Test
    public void shouldFormatLeftJoinWithWithin() {
        final Join join = new Join(location, Type.LEFT, leftAlias, rightAlias, Optional.of(criteria), Optional.of(new WithinExpression(10, TimeUnit.SECONDS)));
        final String expected = "left L\nLEFT OUTER JOIN right R WITHIN 10 SECONDS ON " + "(('left.col0' = 'right.col0'))";
        Assert.assertEquals(expected, SqlFormatter.formatSql(join));
    }

    @Test
    public void shouldFormatLeftJoinWithoutJoinWindow() {
        final Join join = new Join(location, Type.LEFT, leftAlias, rightAlias, Optional.of(criteria), Optional.empty());
        final String result = SqlFormatter.formatSql(join);
        final String expected = "left L\nLEFT OUTER JOIN right R ON ((\'left.col0\' = \'right.col0\'))";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void shouldFormatInnerJoin() {
        final Join join = new Join(location, Type.INNER, leftAlias, rightAlias, Optional.of(criteria), Optional.of(new WithinExpression(10, TimeUnit.SECONDS)));
        final String expected = "left L\nINNER JOIN right R WITHIN 10 SECONDS ON " + "(('left.col0' = 'right.col0'))";
        Assert.assertEquals(expected, SqlFormatter.formatSql(join));
    }

    @Test
    public void shouldFormatInnerJoinWithoutJoinWindow() {
        final Join join = new Join(location, Type.INNER, leftAlias, rightAlias, Optional.of(criteria), Optional.empty());
        final String expected = "left L\nINNER JOIN right R ON ((\'left.col0\' = \'right.col0\'))";
        Assert.assertEquals(expected, SqlFormatter.formatSql(join));
    }

    @Test
    public void shouldFormatOuterJoin() {
        final Join join = new Join(location, Type.OUTER, leftAlias, rightAlias, Optional.of(criteria), Optional.of(new WithinExpression(10, TimeUnit.SECONDS)));
        final String expected = "left L\nFULL OUTER JOIN right R WITHIN 10 SECONDS ON" + " (('left.col0' = 'right.col0'))";
        Assert.assertEquals(expected, SqlFormatter.formatSql(join));
    }

    @Test
    public void shouldFormatOuterJoinWithoutJoinWindow() {
        final Join join = new Join(location, Type.OUTER, leftAlias, rightAlias, Optional.of(criteria), Optional.empty());
        final String expected = "left L\nFULL OUTER JOIN right R ON ((\'left.col0\' = \'right.col0\'))";
        Assert.assertEquals(expected, SqlFormatter.formatSql(join));
    }

    @Test
    public void shouldFormatSelectQueryCorrectly() {
        final String statementString = "CREATE STREAM S AS SELECT a.address->city FROM address a;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        MatcherAssert.assertThat(SqlFormatter.formatSql(statement), CoreMatchers.equalTo(("CREATE STREAM S AS SELECT FETCH_FIELD_FROM_STRUCT(A.ADDRESS, \'CITY\') \"ADDRESS__CITY\"\n" + "FROM ADDRESS A")));
    }

    @Test
    public void shouldFormatSelectStarCorrectly() {
        final String statementString = "CREATE STREAM S AS SELECT * FROM address;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        MatcherAssert.assertThat(SqlFormatter.formatSql(statement), CoreMatchers.equalTo(("CREATE STREAM S AS SELECT *\n" + "FROM ADDRESS ADDRESS")));
    }

    @Test
    public void shouldFormatSelectStarCorrectlyWithOtherFields() {
        final String statementString = "CREATE STREAM S AS SELECT *, address AS city FROM address;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        MatcherAssert.assertThat(SqlFormatter.formatSql(statement), CoreMatchers.equalTo(("CREATE STREAM S AS SELECT\n" + (("  *\n" + ", ADDRESS.ADDRESS \"CITY\"\n") + "FROM ADDRESS ADDRESS"))));
    }

    @Test
    public void shouldFormatSelectStarCorrectlyWithJoin() {
        final String statementString = "CREATE STREAM S AS SELECT address.*, itemid.* " + "FROM address INNER JOIN itemid ON address.address = itemid.address->address;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        MatcherAssert.assertThat(SqlFormatter.formatSql(statement), CoreMatchers.equalTo(("CREATE STREAM S AS SELECT\n" + ((("  ADDRESS.*\n" + ", ITEMID.*\n") + "FROM ADDRESS ADDRESS\n") + "INNER JOIN ITEMID ITEMID ON ((ADDRESS.ADDRESS = ITEMID.ADDRESS->ADDRESS))"))));
    }

    @Test
    public void shouldFormatSelectStarCorrectlyWithJoinOneSidedStar() {
        final String statementString = "CREATE STREAM S AS SELECT address.*, itemid.ordertime " + "FROM address INNER JOIN itemid ON address.address = itemid.address->address;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        MatcherAssert.assertThat(SqlFormatter.formatSql(statement), CoreMatchers.equalTo(("CREATE STREAM S AS SELECT\n" + ((("  ADDRESS.*\n" + ", ITEMID.ORDERTIME \"ORDERTIME\"\n") + "FROM ADDRESS ADDRESS\n") + "INNER JOIN ITEMID ITEMID ON ((ADDRESS.ADDRESS = ITEMID.ADDRESS->ADDRESS))"))));
    }

    @Test
    public void shouldFormatSelectCorrectlyWithDuplicateFields() {
        final String statementString = "CREATE STREAM S AS SELECT address AS one, address AS two FROM address;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(statementString, metaStore).getStatement();
        MatcherAssert.assertThat(SqlFormatter.formatSql(statement), CoreMatchers.equalTo(("CREATE STREAM S AS SELECT\n" + (("  ADDRESS.ADDRESS \"ONE\"\n" + ", ADDRESS.ADDRESS \"TWO\"\n") + "FROM ADDRESS ADDRESS"))));
    }
}

