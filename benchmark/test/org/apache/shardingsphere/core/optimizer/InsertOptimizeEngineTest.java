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


import java.util.List;
import org.apache.shardingsphere.core.keygen.GeneratedKey;
import org.apache.shardingsphere.core.optimizer.condition.ShardingConditions;
import org.apache.shardingsphere.core.parsing.lexer.token.DefaultKeyword;
import org.apache.shardingsphere.core.parsing.parser.context.condition.Column;
import org.apache.shardingsphere.core.parsing.parser.context.insertvalue.InsertValue;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLNumberExpression;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLPlaceholderExpression;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLTextExpression;
import org.apache.shardingsphere.core.parsing.parser.sql.dml.insert.InsertStatement;
import org.apache.shardingsphere.core.routing.value.ListRouteValue;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class InsertOptimizeEngineTest {
    private ShardingRule shardingRule;

    private InsertStatement insertStatementWithValuesWithPlaceHolder;

    private InsertStatement insertStatementWithValuesWithoutPlaceHolder;

    private InsertStatement insertStatementWithoutValuesWithPlaceHolder;

    private InsertStatement insertStatementWithoutValuesWithoutPlaceHolder;

    private InsertStatement insertStatementWithValuesWithPlaceHolderWithEncrypt;

    private InsertStatement insertStatementWithoutValuesWithoutPlaceHolderWithEncrypt;

    private InsertStatement insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt;

    private InsertStatement insertStatementWithValuesWithoutPlaceHolderWithQueryEncrypt;

    private List<Object> parametersWithValues;

    private List<Object> parametersWithoutValues;

    @Test
    public void assertOptimizeWithValuesWithPlaceHolderWithGeneratedKey() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_order"));
        generatedKey.getGeneratedKeys().add(1);
        generatedKey.getGeneratedKeys().add(2);
        ShardingConditions actual = optimize();
        Assert.assertFalse(actual.isAlwaysFalse());
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(2));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(3));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).getParameters().size(), CoreMatchers.is(3));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(0), CoreMatchers.<Object>is(10));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(1), CoreMatchers.<Object>is("init"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(2), CoreMatchers.<Object>is(1));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).getParameters().get(0), CoreMatchers.<Object>is(11));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).getParameters().get(1), CoreMatchers.<Object>is("init"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).getParameters().get(2), CoreMatchers.<Object>is(2));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("(?, ?, ?)"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).toString(), CoreMatchers.is("(?, ?, ?)"));
        Assert.assertThat(actual.getShardingConditions().get(0).getShardingValues().size(), CoreMatchers.is(2));
        Assert.assertThat(actual.getShardingConditions().get(1).getShardingValues().size(), CoreMatchers.is(2));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 10);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(1))), 1);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(1).getShardingValues().get(0))), 11);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(1).getShardingValues().get(1))), 2);
        Assert.assertTrue(insertStatementWithValuesWithPlaceHolder.isContainGenerateKey());
    }

    @Test
    public void assertOptimizeWithValuesWithPlaceHolderWithGeneratedKeyWithEncrypt() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_encrypt"));
        generatedKey.getGeneratedKeys().add(1);
        generatedKey.getGeneratedKeys().add(2);
        ShardingConditions actual = optimize();
        Assert.assertFalse(actual.isAlwaysFalse());
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(2));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(1).getParameters().size(), CoreMatchers.is(3));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().get(0), CoreMatchers.<Object>is(10));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().get(1), CoreMatchers.<Object>is("init"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().get(2), CoreMatchers.<Object>is("encryptValue"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(1).getParameters().get(0), CoreMatchers.<Object>is(11));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(1).getParameters().get(1), CoreMatchers.<Object>is("init"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(1).getParameters().get(2), CoreMatchers.<Object>is("encryptValue"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("(?, ?, ?)"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(1).toString(), CoreMatchers.is("(?, ?, ?)"));
        Assert.assertThat(actual.getShardingConditions().get(0).getShardingValues().size(), CoreMatchers.is(2));
        Assert.assertThat(actual.getShardingConditions().get(1).getShardingValues().size(), CoreMatchers.is(2));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 10);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(1))), 1);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(1).getShardingValues().get(0))), 11);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(1).getShardingValues().get(1))), 2);
        Assert.assertTrue(insertStatementWithValuesWithPlaceHolderWithEncrypt.isContainGenerateKey());
    }

    @Test
    public void assertOptimizeWithValuesWithPlaceHolderWithoutGeneratedKey() {
        insertStatementWithValuesWithPlaceHolder.setGenerateKeyColumnIndex(1);
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_order"));
        generatedKey.getGeneratedKeys().add(1);
        generatedKey.getGeneratedKeys().add(1);
        ShardingConditions actual = optimize();
        Assert.assertFalse(actual.isAlwaysFalse());
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(2));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(3));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).getParameters().size(), CoreMatchers.is(3));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(0), CoreMatchers.<Object>is(10));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(1), CoreMatchers.<Object>is("init"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).getParameters().get(0), CoreMatchers.<Object>is(11));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).getParameters().get(1), CoreMatchers.<Object>is("init"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("(?, ?, ?)"));
        Assert.assertThat(insertStatementWithValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(1).toString(), CoreMatchers.is("(?, ?, ?)"));
        Assert.assertThat(actual.getShardingConditions().get(0).getShardingValues().size(), CoreMatchers.is(2));
        Assert.assertThat(actual.getShardingConditions().get(1).getShardingValues().size(), CoreMatchers.is(2));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 10);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(1).getShardingValues().get(0))), 11);
    }

    @Test
    public void assertOptimizeWithValuesWithoutPlaceHolderWithGeneratedKeyWithQueryEncrypt() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_encrypt_query"));
        generatedKey.getGeneratedKeys().add(1);
        insertStatementWithValuesWithoutPlaceHolderWithQueryEncrypt.getColumns().add(new Column("user_id", "t_encrypt_query"));
        insertStatementWithValuesWithoutPlaceHolderWithQueryEncrypt.getColumns().add(new Column("status", "t_encrypt_query"));
        InsertValue insertValue = new InsertValue(DefaultKeyword.VALUES, "(12,'a')", 0);
        insertValue.getColumnValues().add(new SQLNumberExpression(12));
        insertValue.getColumnValues().add(new SQLTextExpression("a"));
        insertStatementWithValuesWithoutPlaceHolderWithQueryEncrypt.getInsertValues().getInsertValues().add(insertValue);
        ShardingConditions actual = optimize();
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(1));
        Assert.assertThat(insertStatementWithValuesWithoutPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(0));
        Assert.assertThat(insertStatementWithValuesWithoutPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("('encryptValue', 'a', 1, 'assistedEncryptValue')"));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 12);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(1))), 1);
        Assert.assertTrue(insertStatementWithValuesWithoutPlaceHolderWithQueryEncrypt.isContainGenerateKey());
    }

    @Test
    public void assertOptimizeWithValuesWithoutPlaceHolderWithGeneratedKey() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_order"));
        generatedKey.getGeneratedKeys().add(1);
        insertStatementWithValuesWithoutPlaceHolder.getColumns().add(new Column("user_id", "t_order"));
        insertStatementWithValuesWithoutPlaceHolder.getColumns().add(new Column("status", "t_order"));
        InsertValue insertValue = new InsertValue(DefaultKeyword.VALUES, "(12,'a')", 0);
        insertValue.getColumnValues().add(new SQLNumberExpression(12));
        insertValue.getColumnValues().add(new SQLTextExpression("a"));
        insertStatementWithValuesWithoutPlaceHolder.getInsertValues().getInsertValues().add(insertValue);
        ShardingConditions actual = optimize();
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(1));
        Assert.assertThat(insertStatementWithValuesWithoutPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(0));
        Assert.assertThat(insertStatementWithValuesWithoutPlaceHolder.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("(12, 'a', 1)"));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 12);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(1))), 1);
        Assert.assertTrue(insertStatementWithValuesWithoutPlaceHolder.isContainGenerateKey());
    }

    @Test
    public void assertOptimizeWithoutValuesWithPlaceHolderWithGeneratedKey() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_order"));
        generatedKey.getGeneratedKeys().add(1);
        InsertValue insertValue = new InsertValue(DefaultKeyword.SET, "user_id = ?, status = ?", 2);
        insertValue.getColumnValues().add(new SQLPlaceholderExpression(0));
        insertValue.getColumnValues().add(new SQLPlaceholderExpression(1));
        insertStatementWithoutValuesWithPlaceHolder.getInsertValues().getInsertValues().add(insertValue);
        ShardingConditions actual = optimize();
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(1));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(3));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(0), CoreMatchers.<Object>is(12));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(1), CoreMatchers.<Object>is("a"));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().get(2), CoreMatchers.<Object>is(1));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolder.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("user_id = ?, status = ?, order_id = ?"));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 12);
    }

    @Test
    public void assertOptimizeWithoutValuesWithPlaceHolderWithGeneratedKeyWithQueryEncrypt() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_encrypt_query"));
        generatedKey.getGeneratedKeys().add(1);
        InsertValue insertValue = new InsertValue(DefaultKeyword.SET, "user_id = ?, status = ?", 2);
        insertValue.getColumnValues().add(new SQLPlaceholderExpression(0));
        insertValue.getColumnValues().add(new SQLPlaceholderExpression(1));
        insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt.getInsertValues().getInsertValues().add(insertValue);
        ShardingConditions actual = optimize();
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(1));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(4));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().get(0), CoreMatchers.<Object>is("encryptValue"));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().get(1), CoreMatchers.<Object>is("a"));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().get(2), CoreMatchers.<Object>is(1));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().get(3), CoreMatchers.<Object>is("assistedEncryptValue"));
        Assert.assertThat(insertStatementWithoutValuesWithPlaceHolderWithQueryEncrypt.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("user_id = ?, status = ?, order_id = ?, assisted_user_id = ?"));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 12);
    }

    @Test
    public void assertOptimizeWithoutValuesWithoutPlaceHolderWithGeneratedKey() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_order"));
        generatedKey.getGeneratedKeys().add(1);
        InsertValue insertValue = new InsertValue(DefaultKeyword.SET, "user_id = 12, status = 'a'", 0);
        insertValue.getColumnValues().add(new SQLNumberExpression(12));
        insertValue.getColumnValues().add(new SQLTextExpression("a"));
        insertStatementWithoutValuesWithoutPlaceHolder.getInsertValues().getInsertValues().add(insertValue);
        ShardingConditions actual = optimize();
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(1));
        Assert.assertThat(insertStatementWithoutValuesWithoutPlaceHolder.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(0));
        Assert.assertThat(insertStatementWithoutValuesWithoutPlaceHolder.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("user_id = 12, status = 'a', order_id = 1"));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 12);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(1))), 1);
        Assert.assertTrue(insertStatementWithoutValuesWithoutPlaceHolder.isContainGenerateKey());
    }

    @Test
    public void assertOptimizeWithoutValuesWithoutPlaceHolderWithGeneratedKeyWithEncrypt() {
        GeneratedKey generatedKey = new GeneratedKey(new Column("order_id", "t_encrypt"));
        generatedKey.getGeneratedKeys().add(1);
        InsertValue insertValue = new InsertValue(DefaultKeyword.SET, "user_id = 12, status = 'a'", 0);
        insertValue.getColumnValues().add(new SQLNumberExpression(12));
        insertValue.getColumnValues().add(new SQLTextExpression("a"));
        insertStatementWithoutValuesWithoutPlaceHolderWithEncrypt.getInsertValues().getInsertValues().add(insertValue);
        ShardingConditions actual = optimize();
        Assert.assertThat(actual.getShardingConditions().size(), CoreMatchers.is(1));
        Assert.assertThat(insertStatementWithoutValuesWithoutPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(0).getParameters().size(), CoreMatchers.is(0));
        Assert.assertThat(insertStatementWithoutValuesWithoutPlaceHolderWithEncrypt.getInsertValuesToken().getColumnValues().get(0).toString(), CoreMatchers.is("user_id = 12, status = 'a', order_id = 'encryptValue'"));
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(0))), 12);
        assertShardingValue(((ListRouteValue) (actual.getShardingConditions().get(0).getShardingValues().get(1))), 1);
        Assert.assertTrue(insertStatementWithoutValuesWithoutPlaceHolderWithEncrypt.isContainGenerateKey());
    }
}

