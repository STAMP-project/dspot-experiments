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
package org.apache.shardingsphere.core.parsing.parser.token;


import DefaultKeyword.SET;
import com.google.common.base.Optional;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.shardingsphere.core.parsing.lexer.token.DefaultKeyword;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLExpression;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLNumberExpression;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLPlaceholderExpression;
import org.apache.shardingsphere.core.parsing.parser.expression.SQLTextExpression;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InsertValuesTokenTest {
    private InsertValuesToken insertValuesTokeWithSet = new InsertValuesToken(1, DefaultKeyword.SET);

    private InsertValuesToken insertValuesTokeWithValue = new InsertValuesToken(1, DefaultKeyword.VALUES);

    @Test
    public void assertAddInsertColumnValueWithSet() {
        List<SQLExpression> expressions = new LinkedList<>();
        expressions.add(new SQLNumberExpression(1));
        expressions.add(new SQLPlaceholderExpression(1));
        expressions.add(new SQLTextExpression("test"));
        insertValuesTokeWithSet.addInsertColumnValue(expressions, Collections.singletonList(((Object) ("parameter"))));
        Assert.assertThat(insertValuesTokeWithSet.getColumnName(0), CoreMatchers.is("id"));
        Assert.assertThat(insertValuesTokeWithSet.getColumnNames().size(), CoreMatchers.is(3));
        Assert.assertThat(insertValuesTokeWithSet.getType(), CoreMatchers.is(SET));
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).getValues(), CoreMatchers.is(expressions));
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).getParameters().get(0), CoreMatchers.is(((Object) ("parameter"))));
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).getDataNodes().size(), CoreMatchers.is(0));
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).getColumnValue(1), CoreMatchers.is(((Object) ("parameter"))));
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).getColumnValue("status"), CoreMatchers.is(Optional.of(((Object) ("test")))));
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).toString(), CoreMatchers.is("id = 1, value = ?, status = 'test'"));
        insertValuesTokeWithSet.getColumnValues().get(0).setColumnValue(0, 2);
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).getColumnValue(0), CoreMatchers.is(((Object) (2))));
        insertValuesTokeWithSet.getColumnValues().get(0).setColumnValue(1, "parameter1");
        Assert.assertThat(insertValuesTokeWithSet.getColumnValues().get(0).getColumnValue(1), CoreMatchers.is(((Object) ("parameter1"))));
    }

    @Test
    public void assertAddInsertColumnValueWithValues() {
        List<SQLExpression> expressions = new LinkedList<>();
        expressions.add(new SQLNumberExpression(1));
        expressions.add(new SQLPlaceholderExpression(1));
        expressions.add(new SQLTextExpression("test"));
        insertValuesTokeWithValue.addInsertColumnValue(expressions, Collections.singletonList(((Object) ("parameter"))));
        Assert.assertThat(insertValuesTokeWithValue.getColumnValues().get(0).toString(), CoreMatchers.is("(1, ?, 'test')"));
    }
}

