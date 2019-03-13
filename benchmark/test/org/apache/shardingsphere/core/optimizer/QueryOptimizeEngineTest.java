/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.optimizer;


import com.google.common.collect.Range;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.shardingsphere.core.optimizer.condition.ShardingCondition;
import org.apache.shardingsphere.core.optimizer.condition.ShardingConditions;
import org.apache.shardingsphere.core.parsing.parser.context.condition.AndCondition;
import org.apache.shardingsphere.core.parsing.parser.context.condition.Column;
import org.apache.shardingsphere.core.parsing.parser.context.condition.Condition;
import org.apache.shardingsphere.core.parsing.parser.context.condition.OrCondition;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLExpression;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLNumberExpression;
import org.apache.shardingsphere.core.strategy.route.value.BetweenRouteValue;
import org.apache.shardingsphere.core.strategy.route.value.ListRouteValue;
import org.apache.shardingsphere.core.strategy.route.value.RouteValue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class QueryOptimizeEngineTest {
    @Test
    public void assertOptimizeAlwaysFalseListConditions() {
        Condition condition1 = new Condition(new Column("column", "tbl"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(3));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = optimize();
        Assert.assertTrue(shardingConditions.isAlwaysFalse());
    }

    @Test
    public void assertOptimizeAlwaysFalseRangeConditions() {
        Condition condition1 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(1), new SQLNumberExpression(2));
        Condition condition2 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(3), new SQLNumberExpression(4));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = optimize();
        Assert.assertTrue(shardingConditions.isAlwaysFalse());
    }

    @Test
    public void assertOptimizeAlwaysFalseListConditionsAndRangeConditions() {
        Condition condition1 = new Condition(new Column("column", "tbl"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(3), new SQLNumberExpression(4));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = optimize();
        Assert.assertTrue(shardingConditions.isAlwaysFalse());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertOptimizeListConditions() {
        Condition condition1 = new Condition(new Column("column", "tbl"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(1));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = optimize();
        Assert.assertFalse(shardingConditions.isAlwaysFalse());
        ShardingCondition shardingCondition = shardingConditions.getShardingConditions().get(0);
        RouteValue shardingValue = shardingCondition.getShardingValues().get(0);
        Collection<Comparable<?>> values = ((ListRouteValue<Comparable<?>>) (shardingValue)).getValues();
        Assert.assertThat(values.size(), CoreMatchers.is(1));
        Assert.assertTrue(values.containsAll(Collections.singleton(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertOptimizeRangeConditions() {
        Condition condition1 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(1), new SQLNumberExpression(2));
        Condition condition2 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(1), new SQLNumberExpression(3));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = optimize();
        Assert.assertFalse(shardingConditions.isAlwaysFalse());
        ShardingCondition shardingCondition = shardingConditions.getShardingConditions().get(0);
        RouteValue shardingValue = shardingCondition.getShardingValues().get(0);
        Range<Comparable<?>> values = ((BetweenRouteValue<Comparable<?>>) (shardingValue)).getValueRange();
        Assert.assertThat(values.lowerEndpoint(), CoreMatchers.<Comparable>is(1));
        Assert.assertThat(values.upperEndpoint(), CoreMatchers.<Comparable>is(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertOptimizeListConditionsAndRangeConditions() {
        Condition condition1 = new Condition(new Column("column", "tbl"), Arrays.<SQLExpression>asList(new SQLNumberExpression(1), new SQLNumberExpression(2)));
        Condition condition2 = new Condition(new Column("column", "tbl"), new SQLNumberExpression(1), new SQLNumberExpression(2));
        AndCondition andCondition = new AndCondition();
        andCondition.getConditions().add(condition1);
        andCondition.getConditions().add(condition2);
        OrCondition orCondition = new OrCondition();
        orCondition.getAndConditions().add(andCondition);
        ShardingConditions shardingConditions = optimize();
        Assert.assertFalse(shardingConditions.isAlwaysFalse());
        ShardingCondition shardingCondition = shardingConditions.getShardingConditions().get(0);
        RouteValue shardingValue = shardingCondition.getShardingValues().get(0);
        Collection<Comparable<?>> values = ((ListRouteValue<Comparable<?>>) (shardingValue)).getValues();
        Assert.assertThat(values.size(), CoreMatchers.is(2));
        Assert.assertTrue(values.containsAll(Arrays.asList(1, 2)));
    }
}

