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
package io.confluent.ksql.planner;


import DataSource.DataSourceType.KTABLE;
import DataSourceType.KSTREAM;
import Schema.Type.FLOAT64;
import Schema.Type.INT64;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.metastore.StructuredDataSource;
import io.confluent.ksql.planner.plan.AggregateNode;
import io.confluent.ksql.planner.plan.FilterNode;
import io.confluent.ksql.planner.plan.JoinNode;
import io.confluent.ksql.planner.plan.PlanNode;
import io.confluent.ksql.planner.plan.ProjectNode;
import io.confluent.ksql.planner.plan.StructuredDataSourceNode;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class LogicalPlannerTest {
    private MetaStore metaStore;

    @Test
    public void shouldCreatePlanWithTableAsSource() {
        final PlanNode planNode = buildLogicalPlan("select col0 from TEST2 limit 5;");
        MatcherAssert.assertThat(planNode.getSources().size(), CoreMatchers.equalTo(1));
        final StructuredDataSource structuredDataSource = getStructuredDataSource();
        MatcherAssert.assertThat(structuredDataSource.getDataSourceType(), CoreMatchers.equalTo(KTABLE));
        MatcherAssert.assertThat(structuredDataSource.getName(), CoreMatchers.equalTo("TEST2"));
    }

    @Test
    public void testSimpleQueryLogicalPlan() {
        final String simpleQuery = "SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getSources().get(0), CoreMatchers.instanceOf(ProjectNode.class));
        MatcherAssert.assertThat(logicalPlan.getSources().get(0).getSources().get(0), CoreMatchers.instanceOf(FilterNode.class));
        MatcherAssert.assertThat(logicalPlan.getSources().get(0).getSources().get(0).getSources().get(0), CoreMatchers.instanceOf(StructuredDataSourceNode.class));
        MatcherAssert.assertThat(logicalPlan.getSchema().fields().size(), CoreMatchers.equalTo(3));
        Assert.assertNotNull(getPredicate());
    }

    @Test
    public void testSimpleLeftJoinLogicalPlan() {
        final String simpleQuery = "SELECT t1.col1, t2.col1, t1.col4, t2.col2 FROM test1 t1 LEFT JOIN test2 t2 ON t1.col1 = t2.col1;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getSources().get(0), CoreMatchers.instanceOf(ProjectNode.class));
        MatcherAssert.assertThat(logicalPlan.getSources().get(0).getSources().get(0), CoreMatchers.instanceOf(JoinNode.class));
        MatcherAssert.assertThat(logicalPlan.getSources().get(0).getSources().get(0).getSources().get(0), CoreMatchers.instanceOf(StructuredDataSourceNode.class));
        MatcherAssert.assertThat(logicalPlan.getSources().get(0).getSources().get(0).getSources().get(1), CoreMatchers.instanceOf(StructuredDataSourceNode.class));
        MatcherAssert.assertThat(logicalPlan.getSchema().fields().size(), CoreMatchers.equalTo(4));
    }

    @Test
    public void testSimpleLeftJoinFilterLogicalPlan() {
        final String simpleQuery = "SELECT t1.col1, t2.col1, col5, t2.col4, t2.col2 FROM test1 t1 LEFT JOIN test2 t2 ON " + "t1.col1 = t2.col1 WHERE t1.col1 > 10 AND t2.col4 = 10.8;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getSources().get(0), CoreMatchers.instanceOf(ProjectNode.class));
        final ProjectNode projectNode = ((ProjectNode) (logicalPlan.getSources().get(0)));
        MatcherAssert.assertThat(projectNode.getKeyField().name(), CoreMatchers.equalTo("t1.col1".toUpperCase()));
        MatcherAssert.assertThat(projectNode.getSchema().fields().size(), CoreMatchers.equalTo(5));
        MatcherAssert.assertThat(projectNode.getSources().get(0), CoreMatchers.instanceOf(FilterNode.class));
        final FilterNode filterNode = ((FilterNode) (projectNode.getSources().get(0)));
        MatcherAssert.assertThat(filterNode.getPredicate().toString(), CoreMatchers.equalTo("((T1.COL1 > 10) AND (T2.COL4 = 10.8))"));
        MatcherAssert.assertThat(filterNode.getSources().get(0), CoreMatchers.instanceOf(JoinNode.class));
        final JoinNode joinNode = ((JoinNode) (filterNode.getSources().get(0)));
        MatcherAssert.assertThat(joinNode.getSources().get(0), CoreMatchers.instanceOf(StructuredDataSourceNode.class));
        MatcherAssert.assertThat(joinNode.getSources().get(1), CoreMatchers.instanceOf(StructuredDataSourceNode.class));
    }

    @Test
    public void testSimpleAggregateLogicalPlan() {
        final String simpleQuery = "SELECT col0, sum(col3), count(col3) FROM test1 window TUMBLING ( size 2 " + ("second) " + "WHERE col0 > 100 GROUP BY col0;");
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getSources().get(0), CoreMatchers.instanceOf(AggregateNode.class));
        final AggregateNode aggregateNode = ((AggregateNode) (logicalPlan.getSources().get(0)));
        MatcherAssert.assertThat(aggregateNode.getFunctionCalls().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(aggregateNode.getFunctionCalls().get(0).getName().getSuffix(), CoreMatchers.equalTo("SUM"));
        MatcherAssert.assertThat(aggregateNode.getWindowExpression().getKsqlWindowExpression().toString(), CoreMatchers.equalTo(" TUMBLING ( SIZE 2 SECONDS ) "));
        MatcherAssert.assertThat(aggregateNode.getGroupByExpressions().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(aggregateNode.getGroupByExpressions().get(0).toString(), CoreMatchers.equalTo("TEST1.COL0"));
        MatcherAssert.assertThat(aggregateNode.getRequiredColumns().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(aggregateNode.getSchema().fields().get(1).schema().type(), CoreMatchers.equalTo(FLOAT64));
        MatcherAssert.assertThat(aggregateNode.getSchema().fields().get(2).schema().type(), CoreMatchers.equalTo(INT64));
        MatcherAssert.assertThat(logicalPlan.getSources().get(0).getSchema().fields().size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testComplexAggregateLogicalPlan() {
        final String simpleQuery = "SELECT col0, sum(floor(col3)*100)/count(col3) FROM test1 window " + ("HOPPING ( size 2 second, advance by 1 second) " + "WHERE col0 > 100 GROUP BY col0;");
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getSources().get(0), CoreMatchers.instanceOf(AggregateNode.class));
        final AggregateNode aggregateNode = ((AggregateNode) (logicalPlan.getSources().get(0)));
        MatcherAssert.assertThat(aggregateNode.getFunctionCalls().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(aggregateNode.getFunctionCalls().get(0).getName().getSuffix(), CoreMatchers.equalTo("SUM"));
        MatcherAssert.assertThat(aggregateNode.getWindowExpression().getKsqlWindowExpression().toString(), CoreMatchers.equalTo(" HOPPING ( SIZE 2 SECONDS , ADVANCE BY 1 SECONDS ) "));
        MatcherAssert.assertThat(aggregateNode.getGroupByExpressions().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(aggregateNode.getGroupByExpressions().get(0).toString(), CoreMatchers.equalTo("TEST1.COL0"));
        MatcherAssert.assertThat(aggregateNode.getRequiredColumns().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(aggregateNode.getSchema().fields().get(1).schema().type(), CoreMatchers.equalTo(FLOAT64));
        MatcherAssert.assertThat(logicalPlan.getSources().get(0).getSchema().fields().size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void shouldCreateTableOutputForAggregateQuery() {
        final String simpleQuery = "SELECT col0, sum(floor(col3)*100)/count(col3) FROM test1 window " + ("HOPPING ( size 2 second, advance by 1 second) " + "WHERE col0 > 100 GROUP BY col0;");
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(DataSourceType.KTABLE));
    }

    @Test
    public void shouldCreateStreamOutputForStreamTableJoin() {
        final String simpleQuery = "SELECT t1.col1, t2.col1, col5, t2.col4, t2.col2 FROM test1 t1 LEFT JOIN test2 t2 ON " + "t1.col1 = t2.col1 WHERE t1.col1 > 10 AND t2.col4 = 10.8;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(KSTREAM));
    }

    @Test
    public void shouldCreateStreamOutputForStreamFilter() {
        final String simpleQuery = "SELECT * FROM test1 WHERE col0 > 100;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(KSTREAM));
    }

    @Test
    public void shouldCreateTableOutputForTableFilter() {
        final String simpleQuery = "SELECT * FROM test2 WHERE col4 = 10.8;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(DataSourceType.KTABLE));
    }

    @Test
    public void shouldCreateStreamOutputForStreamProjection() {
        final String simpleQuery = "SELECT col0 FROM test1;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(KSTREAM));
    }

    @Test
    public void shouldCreateTableOutputForTableProjection() {
        final String simpleQuery = "SELECT col4 FROM test2;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(DataSourceType.KTABLE));
    }

    @Test
    public void shouldCreateStreamOutputForStreamStreamJoin() {
        final String simpleQuery = "SELECT * FROM ORDERS INNER JOIN TEST1 ON ORDERS.ORDERID=TEST1.COL0;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(KSTREAM));
    }

    @Test
    public void shouldCreateTableOutputForTableTableJoin() {
        final String simpleQuery = "SELECT * FROM TEST2 INNER JOIN TEST3 ON TEST2.COL0=TEST3.COL0;";
        final PlanNode logicalPlan = buildLogicalPlan(simpleQuery);
        MatcherAssert.assertThat(logicalPlan.getNodeOutputType(), CoreMatchers.equalTo(DataSourceType.KTABLE));
    }
}

