/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.builder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.TestSupport;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


public class ExpressionBuilderTest extends TestSupport {
    protected CamelContext camelContext = new DefaultCamelContext();

    protected Exchange exchange = new org.apache.camel.support.DefaultExchange(camelContext);

    @Test
    public void testRegexTokenize() throws Exception {
        Expression expression = ExpressionBuilder.regexTokenizeExpression(ExpressionBuilder.headerExpression("location"), ",");
        List<String> expected = new ArrayList<>(Arrays.asList(new String[]{ "Islington", "London", "UK" }));
        TestSupport.assertExpression(expression, exchange, expected);
        Predicate predicate = PredicateBuilder.contains(ExpressionBuilder.regexTokenizeExpression(ExpressionBuilder.headerExpression("location"), ","), ExpressionBuilder.constantExpression("London"));
        TestSupport.assertPredicate(predicate, exchange, true);
        predicate = PredicateBuilder.contains(ExpressionBuilder.regexTokenizeExpression(ExpressionBuilder.headerExpression("location"), ","), ExpressionBuilder.constantExpression("Manchester"));
        TestSupport.assertPredicate(predicate, exchange, false);
    }

    @Test
    public void testRegexReplaceAll() throws Exception {
        Expression expression = ExpressionBuilder.regexReplaceAll(ExpressionBuilder.headerExpression("location"), "London", "Westminster");
        TestSupport.assertExpression(expression, exchange, "Islington,Westminster,UK");
        expression = ExpressionBuilder.regexReplaceAll(ExpressionBuilder.headerExpression("location"), "London", ExpressionBuilder.headerExpression("name"));
        TestSupport.assertExpression(expression, exchange, "Islington,James,UK");
    }

    @Test
    public void testTokenize() throws Exception {
        Expression expression = ExpressionBuilder.tokenizeExpression(ExpressionBuilder.headerExpression("location"), ",");
        List<String> expected = new ArrayList<>(Arrays.asList(new String[]{ "Islington", "London", "UK" }));
        TestSupport.assertExpression(expression, exchange, expected);
        Predicate predicate = PredicateBuilder.contains(ExpressionBuilder.tokenizeExpression(ExpressionBuilder.headerExpression("location"), ","), ExpressionBuilder.constantExpression("London"));
        TestSupport.assertPredicate(predicate, exchange, true);
        predicate = PredicateBuilder.contains(ExpressionBuilder.tokenizeExpression(ExpressionBuilder.headerExpression("location"), ","), ExpressionBuilder.constantExpression("Manchester"));
        TestSupport.assertPredicate(predicate, exchange, false);
    }

    @Test
    public void testTokenizeLines() throws Exception {
        Expression expression = ExpressionBuilder.regexTokenizeExpression(ExpressionBuilder.bodyExpression(), "[\r|\n]");
        exchange.getIn().setBody("Hello World\nBye World\rSee you again");
        List<String> expected = new ArrayList<>(Arrays.asList(new String[]{ "Hello World", "Bye World", "See you again" }));
        TestSupport.assertExpression(expression, exchange, expected);
    }

    @Test
    public void testSortLines() throws Exception {
        Expression expression = ExpressionBuilder.sortExpression(TestSupport.body().tokenize(",").getExpression(), new ExpressionBuilderTest.SortByName());
        exchange.getIn().setBody("Jonathan,Claus,James,Hadrian");
        List<String> expected = new ArrayList<>(Arrays.asList(new String[]{ "Claus", "Hadrian", "James", "Jonathan" }));
        TestSupport.assertExpression(expression, exchange, expected);
    }

    @Test
    public void testCamelContextPropertiesExpression() throws Exception {
        camelContext.getGlobalOptions().put("CamelTestKey", "CamelTestValue");
        Expression expression = ExpressionBuilder.camelContextPropertyExpression("CamelTestKey");
        TestSupport.assertExpression(expression, exchange, "CamelTestValue");
        expression = ExpressionBuilder.camelContextPropertiesExpression();
        Map<?, ?> properties = expression.evaluate(exchange, Map.class);
        Assert.assertEquals("Get a wrong properties size", properties.size(), 1);
    }

    @Test
    public void testParseSimpleOrFallbackToConstantExpression() throws Exception {
        Assert.assertEquals("world", ExpressionBuilder.parseSimpleOrFallbackToConstantExpression("world", camelContext).evaluate(exchange, String.class));
        Assert.assertEquals("Hello there!", ExpressionBuilder.parseSimpleOrFallbackToConstantExpression("${body}", camelContext).evaluate(exchange, String.class));
        Assert.assertEquals("Hello there!", ExpressionBuilder.parseSimpleOrFallbackToConstantExpression("$simple{body}", camelContext).evaluate(exchange, String.class));
    }

    @Test
    public void testFunction() throws Exception {
        TestSupport.assertExpression(ExpressionBuilder.messageExpression(( m) -> m.getExchange().getIn().getHeader("name")), exchange, "James");
        TestSupport.assertExpression(ExpressionBuilder.messageExpression(( m) -> m.getHeader("name")), exchange, "James");
    }

    private static class SortByName implements Comparator<String> {
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }
}

