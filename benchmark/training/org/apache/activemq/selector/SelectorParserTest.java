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
package org.apache.activemq.selector;


import javax.jms.InvalidSelectorException;
import junit.framework.TestCase;
import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.filter.BooleanFunctionCallExpr;
import org.apache.activemq.filter.ComparisonExpression;
import org.apache.activemq.filter.Expression;
import org.apache.activemq.filter.LogicExpression;
import org.apache.activemq.filter.XPathExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class SelectorParserTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(SelectorParserTest.class);

    public void testFunctionCall() throws Exception {
        Object filter = parse("REGEX('sales.*', group)");
        TestCase.assertTrue("expected type", (filter instanceof BooleanFunctionCallExpr));
        SelectorParserTest.LOG.info(("function exp:" + filter));
        // non existent function
        try {
            parse("DoesNotExist('sales.*', group)");
            TestCase.fail("expect ex on non existent function");
        } catch (InvalidSelectorException expected) {
        }
    }

    public void testParseXPath() throws Exception {
        BooleanExpression filter = parse("XPATH '//title[@lang=''eng'']'");
        TestCase.assertTrue("Created XPath expression", (filter instanceof XPathExpression));
        SelectorParserTest.LOG.info(("Expression: " + filter));
    }

    public void testParseWithParensAround() throws Exception {
        String[] values = new String[]{ "x = 1 and y = 2", "(x = 1) and (y = 2)", "((x = 1) and (y = 2))" };
        for (int i = 0; i < (values.length); i++) {
            String value = values[i];
            SelectorParserTest.LOG.info(("Parsing: " + value));
            BooleanExpression andExpression = parse(value);
            TestCase.assertTrue("Created LogicExpression expression", (andExpression instanceof LogicExpression));
            LogicExpression logicExpression = ((LogicExpression) (andExpression));
            Expression left = logicExpression.getLeft();
            Expression right = logicExpression.getRight();
            TestCase.assertTrue("Left is a binary filter", (left instanceof ComparisonExpression));
            TestCase.assertTrue("Right is a binary filter", (right instanceof ComparisonExpression));
            ComparisonExpression leftCompare = ((ComparisonExpression) (left));
            ComparisonExpression rightCompare = ((ComparisonExpression) (right));
            assertPropertyExpression("left", leftCompare.getLeft(), "x");
            assertPropertyExpression("right", rightCompare.getLeft(), "y");
        }
    }
}

