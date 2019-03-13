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
package org.apache.camel.language.simple;


import Exchange.AUTHENTICATION;
import Exchange.AUTHENTICATION_FAILURE_POLICY_ID;
import Exchange.EXCEPTION_CAUGHT;
import Exchange.FILE_NAME;
import Exchange.LOOP_INDEX;
import Exchange.STEP_ID;
import ExchangePattern.InOut;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.ExchangeTestSupport;
import org.apache.camel.Expression;
import org.apache.camel.ExpressionIllegalSyntaxException;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.LanguageTestSupport;
import org.apache.camel.Predicate;
import org.apache.camel.TestSupport;
import org.apache.camel.component.bean.MethodNotFoundException;
import org.apache.camel.language.bean.RuntimeBeanExpressionException;
import org.apache.camel.language.simple.types.SimpleIllegalSyntaxException;
import org.junit.Assert;
import org.junit.Test;


public class SimpleTest extends LanguageTestSupport {
    private static final String JAVA8_INDEX_OUT_OF_BOUNDS_ERROR_MSG = "Index: 2, Size: 2";

    private static final String INDEX_OUT_OF_BOUNDS_ERROR_MSG = "Index 2 out-of-bounds for length 2";

    @Test
    public void testSimpleExpressionOrPredicate() throws Exception {
        Predicate predicate = SimpleLanguage.predicate("${header.bar} == 123");
        Assert.assertTrue(predicate.matches(exchange));
        predicate = SimpleLanguage.predicate("${header.bar} == 124");
        Assert.assertFalse(predicate.matches(exchange));
        Expression expression = SimpleLanguage.expression("${body}");
        Assert.assertEquals("<hello id='m123'>world!</hello>", expression.evaluate(exchange, String.class));
        expression = SimpleLanguage.simple("${body}");
        Assert.assertEquals("<hello id='m123'>world!</hello>", expression.evaluate(exchange, String.class));
        expression = SimpleLanguage.simple("${body}", String.class);
        Assert.assertEquals("<hello id='m123'>world!</hello>", expression.evaluate(exchange, String.class));
        expression = SimpleLanguage.simple("${header.bar} == 123", boolean.class);
        Assert.assertEquals(Boolean.TRUE, expression.evaluate(exchange, Object.class));
        expression = SimpleLanguage.simple("${header.bar} == 124", boolean.class);
        Assert.assertEquals(Boolean.FALSE, expression.evaluate(exchange, Object.class));
        expression = SimpleLanguage.simple("${header.bar} == 123", Boolean.class);
        Assert.assertEquals(Boolean.TRUE, expression.evaluate(exchange, Object.class));
        expression = SimpleLanguage.simple("${header.bar} == 124", Boolean.class);
        Assert.assertEquals(Boolean.FALSE, expression.evaluate(exchange, Object.class));
    }

    @Test
    public void testResultType() throws Exception {
        Assert.assertEquals(123, SimpleLanguage.simple("${header.bar}", int.class).evaluate(exchange, Object.class));
        Assert.assertEquals("123", SimpleLanguage.simple("${header.bar}", String.class).evaluate(exchange, Object.class));
        // should not be possible
        Assert.assertEquals(null, SimpleLanguage.simple("${header.bar}", Date.class).evaluate(exchange, Object.class));
        Assert.assertEquals(null, SimpleLanguage.simple("${header.unknown}", String.class).evaluate(exchange, Object.class));
    }

    @Test
    public void testRefExpression() throws Exception {
        assertExpressionResultInstanceOf("${ref:myAnimal}", SimpleTest.Animal.class);
        assertExpression("${ref:myAnimal}", "Donkey");
        assertExpression("${ref:unknown}", null);
        assertExpression("Hello ${ref:myAnimal}", "Hello Donkey");
        assertExpression("Hello ${ref:unknown}", "Hello ");
    }

    @Test
    public void testConstantExpression() throws Exception {
        assertExpression("Hello World", "Hello World");
    }

    @Test
    public void testNull() throws Exception {
        Assert.assertNull(SimpleLanguage.simple("${null}").evaluate(exchange, Object.class));
    }

    @Test
    public void testSimpleFileDir() throws Exception {
        assertExpression("file:mydir", "file:mydir");
    }

    @Test
    public void testEmptyExpression() throws Exception {
        assertExpression("", "");
        assertExpression(" ", " ");
        try {
            assertExpression(null, null);
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("expression must be specified", e.getMessage());
        }
        assertPredicate("", false);
        assertPredicate(" ", false);
        try {
            assertPredicate(null, false);
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("expression must be specified", e.getMessage());
        }
    }

    @Test
    public void testExchangeExpression() throws Exception {
        Expression exp = SimpleLanguage.simple("${exchange}");
        Assert.assertNotNull(exp);
        Assert.assertEquals(exchange, exp.evaluate(exchange, Object.class));
        assertExpression("${exchange}", exchange);
    }

    @Test
    public void testExchangeOgnlExpression() throws Exception {
        Expression exp = SimpleLanguage.simple("${exchange.exchangeId}");
        Assert.assertNotNull(exp);
        Assert.assertEquals(exchange.getExchangeId(), exp.evaluate(exchange, Object.class));
        assertExpression("${exchange.exchangeId}", exchange.getExchangeId());
        assertExpression("${exchange.class.name}", "org.apache.camel.support.DefaultExchange");
    }

    @Test
    public void testBodyExpression() throws Exception {
        Expression exp = SimpleLanguage.simple("${body}");
        Assert.assertNotNull(exp);
    }

    @Test
    public void testBodyOgnlExpression() throws Exception {
        Expression exp = SimpleLanguage.simple("${body.xxx}");
        Assert.assertNotNull(exp);
        // must start with a dot
        try {
            SimpleLanguage.simple("${bodyxxx}");
            Assert.fail("Should throw exception");
        } catch (SimpleIllegalSyntaxException e) {
            // expected
        }
    }

    @Test
    public void testBodyExpressionUsingAlternativeStartToken() throws Exception {
        Expression exp = SimpleLanguage.simple("$simple{body}");
        Assert.assertNotNull(exp);
    }

    @Test
    public void testBodyExpressionNotStringType() throws Exception {
        exchange.getIn().setBody(123);
        Expression exp = SimpleLanguage.simple("${body}");
        Assert.assertNotNull(exp);
        Object val = exp.evaluate(exchange, Object.class);
        TestSupport.assertIsInstanceOf(Integer.class, val);
        Assert.assertEquals(123, val);
    }

    @Test
    public void testBodyExpressionWithArray() throws Exception {
        exchange.getIn().setBody(new SimpleTest.MyClass());
        Expression exp = SimpleLanguage.simple("${body.myArray}");
        Assert.assertNotNull(exp);
        Object val = exp.evaluate(exchange, Object.class);
        TestSupport.assertIsInstanceOf(Object[].class, val);
        exp = SimpleLanguage.simple("${body.myArray.length}");
        Assert.assertNotNull(exp);
        val = exp.evaluate(exchange, Object.class);
        TestSupport.assertIsInstanceOf(Integer.class, val);
        Assert.assertEquals(3, val);
    }

    @Test
    public void testSimpleExpressions() throws Exception {
        assertExpression("${exchangeId}", exchange.getExchangeId());
        assertExpression("${id}", exchange.getIn().getMessageId());
        assertExpression("${body}", "<hello id='m123'>world!</hello>");
        assertExpression("${in.body}", "<hello id='m123'>world!</hello>");
        assertExpression("${in.header.foo}", "abc");
        assertExpression("${in.headers.foo}", "abc");
        assertExpression("${header.foo}", "abc");
        assertExpression("${headers.foo}", "abc");
        assertExpression("${routeId}", exchange.getFromRouteId());
        exchange.setFromRouteId("myRouteId");
        assertExpression("${routeId}", "myRouteId");
    }

    @Test
    public void testTrimSimpleExpressions() throws Exception {
        assertExpression(" \t${exchangeId}\n".trim(), exchange.getExchangeId());
        assertExpression("\n${id}\r".trim(), exchange.getIn().getMessageId());
        assertExpression("\t\r ${body}".trim(), "<hello id='m123'>world!</hello>");
        assertExpression("\n${in.body}\r".trim(), "<hello id='m123'>world!</hello>");
    }

    @Test
    public void testSimpleThreadName() throws Exception {
        String name = Thread.currentThread().getName();
        assertExpression("${threadName}", name);
        assertExpression("The name is ${threadName}", ("The name is " + name));
    }

    @Test
    public void testSimpleStepId() throws Exception {
        assertExpression("${stepId}", null);
        exchange.setProperty(STEP_ID, "foo");
        assertExpression("${stepId}", "foo");
    }

    @Test
    public void testSimpleOutExpressions() throws Exception {
        exchange.getOut().setBody("Bye World");
        exchange.getOut().setHeader("quote", "Camel rocks");
        assertExpression("${out.body}", "Bye World");
        assertExpression("${out.header.quote}", "Camel rocks");
        assertExpression("${out.headers.quote}", "Camel rocks");
    }

    @Test
    public void testSimpleExchangePropertyExpressions() throws Exception {
        exchange.setProperty("medal", "gold");
        assertExpression("${exchangeProperty.medal}", "gold");
    }

    @Test
    public void testSimpleSystemPropertyExpressions() throws Exception {
        System.setProperty("who", "I was here");
        assertExpression("${sys.who}", "I was here");
    }

    @Test
    public void testSimpleSystemEnvironmentExpressions() throws Exception {
        String path = System.getenv("PATH");
        if (path != null) {
            assertExpression("${sysenv.PATH}", path);
        }
    }

    @Test
    public void testSimpleSystemEnvironmentExpressionsIfDash() throws Exception {
        String foo = System.getenv("FOO_SERVICE_HOST");
        if (foo != null) {
            assertExpression("${sysenv.FOO-SERVICE-HOST}", foo);
        }
    }

    @Test
    public void testSimpleSystemEnvironmentExpressionsIfLowercase() throws Exception {
        String path = System.getenv("PATH");
        if (path != null) {
            assertExpression("${sysenv.path}", path);
        }
    }

    @Test
    public void testSimpleCamelId() throws Exception {
        assertExpression("${camelId}", context.getName());
    }

    @Test
    public void testOGNLBodyListAndMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("cool", "Camel rocks");
        map.put("dude", "Hey dude");
        map.put("code", 4321);
        List<Map<String, Object>> lines = new ArrayList<>();
        lines.add(map);
        exchange.getIn().setBody(lines);
        assertExpression("${in.body[0][cool]}", "Camel rocks");
        assertExpression("${body[0][cool]}", "Camel rocks");
        assertExpression("${in.body[0][code]}", 4321);
        assertExpression("${body[0][code]}", 4321);
    }

    @Test
    public void testOGNLBodyEmptyList() throws Exception {
        Map<String, List<String>> map = new HashMap<>();
        map.put("list", new ArrayList<String>());
        exchange.getIn().setBody(map);
        assertExpression("${in.body?.get('list')[0].toString}", null);
    }

    @Test
    public void testOGNLBodyExpression() throws Exception {
        exchange.getIn().setBody("hello world");
        assertPredicate("${body} == 'hello world'", true);
        assertPredicate("${body.toUpperCase()} == 'HELLO WORLD'", true);
    }

    @Test
    public void testOGNLBodyAsExpression() throws Exception {
        byte[] body = "hello world".getBytes();
        exchange.getIn().setBody(body);
        // there is no upper case method on byte array, but we can convert to String as below
        try {
            assertPredicate("${body.toUpperCase()} == 'HELLO WORLD'", true);
            Assert.fail("Should throw exception");
        } catch (RuntimeBeanExpressionException e) {
            MethodNotFoundException cause = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, e.getCause());
            Assert.assertEquals("toUpperCase()", cause.getMethodName());
        }
        assertPredicate("${bodyAs(String)} == 'hello world'", true);
        assertPredicate("${bodyAs(String).toUpperCase()} == 'HELLO WORLD'", true);
        // and body on exchange should not be changed
        Assert.assertSame(body, exchange.getIn().getBody());
    }

    @Test
    public void testOGNLMandatoryBodyAsExpression() throws Exception {
        byte[] body = "hello world".getBytes();
        exchange.getIn().setBody(body);
        // there is no upper case method on byte array, but we can convert to String as below
        try {
            assertPredicate("${body.toUpperCase()} == 'HELLO WORLD'", true);
            Assert.fail("Should throw exception");
        } catch (RuntimeBeanExpressionException e) {
            MethodNotFoundException cause = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, e.getCause());
            Assert.assertEquals("toUpperCase()", cause.getMethodName());
        }
        assertPredicate("${mandatoryBodyAs(String)} == 'hello world'", true);
        assertPredicate("${mandatoryBodyAs(String).toUpperCase()} == 'HELLO WORLD'", true);
        // and body on exchange should not be changed
        Assert.assertSame(body, exchange.getIn().getBody());
    }

    @Test
    public void testOGNLCallReplace() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("cool", "Camel rocks");
        map.put("dude", "Hey dude");
        exchange.getIn().setHeaders(map);
        assertExpression("${headers.cool.replaceAll(\"rocks\", \"is so cool\")}", "Camel is so cool");
    }

    @Test
    public void testOGNLBodyListAndMapAndMethod() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("camel", new SimpleTest.OrderLine(123, "Camel in Action"));
        map.put("amq", new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        List<Map<String, Object>> lines = new ArrayList<>();
        lines.add(map);
        exchange.getIn().setBody(lines);
        assertExpression("${in.body[0][camel].id}", 123);
        assertExpression("${in.body[0][camel].name}", "Camel in Action");
        assertExpression("${in.body[0][camel].getId}", 123);
        assertExpression("${in.body[0][camel].getName}", "Camel in Action");
        assertExpression("${body[0][camel].id}", 123);
        assertExpression("${body[0][camel].name}", "Camel in Action");
        assertExpression("${body[0][camel].getId}", 123);
        assertExpression("${body[0][camel].getName}", "Camel in Action");
    }

    @Test
    public void testOGNLPropertyList() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("Camel in Action");
        lines.add("ActiveMQ in Action");
        exchange.setProperty("wicket", lines);
        assertExpression("${exchangeProperty.wicket[0]}", "Camel in Action");
        assertExpression("${exchangeProperty.wicket[1]}", "ActiveMQ in Action");
        try {
            assertExpression("${exchangeProperty.wicket[2]}", "");
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            if ((TestSupport.getJavaMajorVersion()) <= 8) {
                Assert.assertEquals(SimpleTest.JAVA8_INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            } else {
                Assert.assertEquals(SimpleTest.INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            }
        }
        assertExpression("${exchangeProperty.unknown[cool]}", null);
    }

    @Test
    public void testOGNLPropertyLinesList() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        exchange.setProperty("wicket", lines);
        assertExpression("${exchangeProperty.wicket[0].getId}", 123);
        assertExpression("${exchangeProperty.wicket[1].getName}", "ActiveMQ in Action");
        try {
            assertExpression("${exchangeProperty.wicket[2]}", "");
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            if ((TestSupport.getJavaMajorVersion()) <= 8) {
                Assert.assertEquals(SimpleTest.JAVA8_INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            } else {
                Assert.assertEquals(SimpleTest.INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            }
        }
        assertExpression("${exchangeProperty.unknown[cool]}", null);
    }

    @Test
    public void testOGNLPropertyMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("cool", "Camel rocks");
        map.put("dude", "Hey dude");
        map.put("code", 4321);
        exchange.setProperty("wicket", map);
        assertExpression("${exchangeProperty.wicket[cool]}", "Camel rocks");
        assertExpression("${exchangeProperty.wicket[dude]}", "Hey dude");
        assertExpression("${exchangeProperty.wicket[unknown]}", null);
        assertExpression("${exchangeProperty.wicket[code]}", 4321);
        // no header named unknown
        assertExpression("${exchangeProperty?.unknown[cool]}", null);
        assertExpression("${exchangeProperty.unknown[cool]}", null);
    }

    @Test
    public void testOGNLExchangePropertyMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("cool", "Camel rocks");
        map.put("dude", "Hey dude");
        map.put("code", 4321);
        exchange.setProperty("wicket", map);
        assertExpression("${exchangeProperty.wicket[cool]}", "Camel rocks");
        assertExpression("${exchangeProperty.wicket[dude]}", "Hey dude");
        assertExpression("${exchangeProperty.wicket[unknown]}", null);
        assertExpression("${exchangeProperty.wicket[code]}", 4321);
        // no header named unknown
        assertExpression("${exchangeProperty?.unknown[cool]}", null);
        assertExpression("${exchangeProperty.unknown[cool]}", null);
    }

    @Test
    public void testOGNLPropertyMapWithDot() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("this.code", "This code");
        exchange.setProperty("wicket", map);
        assertExpression("${exchangeProperty.wicket[this.code]}", "This code");
    }

    @Test
    public void testOGNLPropertyMapNotMap() throws Exception {
        try {
            assertExpression("${exchangeProperty.foobar[bar]}", null);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            Assert.assertEquals("Key: bar not found in bean: cba of type: java.lang.String using OGNL path [[bar]]", cause.getMessage());
        }
    }

    @Test
    public void testOGNLPropertyMapIllegalSyntax() throws Exception {
        try {
            assertExpression("${exchangeProperty.foobar[bar}", null);
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Valid syntax: ${exchangeProperty.OGNL} was: exchangeProperty.foobar[bar at location 0"));
        }
    }

    @Test
    public void testOGNLExchangePropertyMapIllegalSyntax() throws Exception {
        try {
            assertExpression("${exchangeProperty.foobar[bar}", null);
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Valid syntax: ${exchangeProperty.OGNL} was: exchangeProperty.foobar[bar at location 0"));
        }
    }

    @Test
    public void testOGNLHeaderEmptyTest() throws Exception {
        exchange.getIn().setHeader("beer", "");
        assertPredicate("${header.beer} == ''", true);
        assertPredicate("${header.beer} == \"\"", true);
        assertPredicate("${header.beer} == ' '", false);
        assertPredicate("${header.beer} == \" \"", false);
        exchange.getIn().setHeader("beer", " ");
        assertPredicate("${header.beer} == ''", false);
        assertPredicate("${header.beer} == \"\"", false);
        assertPredicate("${header.beer} == ' '", true);
        assertPredicate("${header.beer} == \" \"", true);
        assertPredicate("${header.beer.toString().trim()} == ''", true);
        assertPredicate("${header.beer.toString().trim()} == \"\"", true);
        exchange.getIn().setHeader("beer", "   ");
        assertPredicate("${header.beer.trim()} == ''", true);
        assertPredicate("${header.beer.trim()} == \"\"", true);
    }

    @Test
    public void testDateExpressions() throws Exception {
        Calendar inHeaderCalendar = Calendar.getInstance();
        inHeaderCalendar.set(1974, Calendar.APRIL, 20);
        exchange.getIn().setHeader("birthday", inHeaderCalendar.getTime());
        Calendar outHeaderCalendar = Calendar.getInstance();
        outHeaderCalendar.set(1975, Calendar.MAY, 21);
        exchange.getOut().setHeader("birthday", outHeaderCalendar.getTime());
        Calendar propertyCalendar = Calendar.getInstance();
        propertyCalendar.set(1976, Calendar.JUNE, 22);
        exchange.setProperty("birthday", propertyCalendar.getTime());
        assertExpression("${date:header.birthday}", inHeaderCalendar.getTime());
        assertExpression("${date:header.birthday:yyyyMMdd}", "19740420");
        assertExpression("${date:header.birthday+24h:yyyyMMdd}", "19740421");
        assertExpression("${date:in.header.birthday}", inHeaderCalendar.getTime());
        assertExpression("${date:in.header.birthday:yyyyMMdd}", "19740420");
        assertExpression("${date:in.header.birthday+24h:yyyyMMdd}", "19740421");
        assertExpression("${date:out.header.birthday}", outHeaderCalendar.getTime());
        assertExpression("${date:out.header.birthday:yyyyMMdd}", "19750521");
        assertExpression("${date:out.header.birthday+24h:yyyyMMdd}", "19750522");
        assertExpression("${date:exchangeProperty.birthday}", propertyCalendar.getTime());
        assertExpression("${date:exchangeProperty.birthday:yyyyMMdd}", "19760622");
        assertExpression("${date:exchangeProperty.birthday+24h:yyyyMMdd}", "19760623");
        try {
            assertExpression("${date:yyyyMMdd}", "19740420");
            Assert.fail("Should thrown an exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Command not supported for dateExpression: yyyyMMdd", e.getMessage());
        }
    }

    @Test
    public void testDateAndTimeExpressions() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(1974, Calendar.APRIL, 20, 8, 55, 47);
        cal.set(Calendar.MILLISECOND, 123);
        exchange.getIn().setHeader("birthday", cal.getTime());
        assertExpression("${date:header.birthday - 10s:yyyy-MM-dd'T'HH:mm:ss:SSS}", "1974-04-20T08:55:37:123");
        assertExpression("${date:header.birthday:yyyy-MM-dd'T'HH:mm:ss:SSS}", "1974-04-20T08:55:47:123");
    }

    @Test
    public void testDateWithTimezone() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.set(1974, Calendar.APRIL, 20, 8, 55, 47);
        cal.set(Calendar.MILLISECOND, 123);
        exchange.getIn().setHeader("birthday", cal.getTime());
        assertExpression("${date-with-timezone:header.birthday:GMT+8:yyyy-MM-dd'T'HH:mm:ss:SSS}", "1974-04-20T08:55:47:123");
        assertExpression("${date-with-timezone:header.birthday:GMT:yyyy-MM-dd'T'HH:mm:ss:SSS}", "1974-04-20T00:55:47:123");
    }

    @Test
    public void testDateNow() throws Exception {
        Object out = evaluateExpression("${date:now:hh:mm:ss a}", null);
        Assert.assertNotNull(out);
    }

    @Test
    public void testDatePredicates() throws Exception {
        assertPredicate("${date:now} < ${date:now+60s}");
        assertPredicate("${date:now-2s+2s} == ${date:now}");
    }

    @Test
    public void testLanguagesInContext() throws Exception {
        // evaluate so we know there is 1 language in the context
        assertExpression("${id}", exchange.getIn().getMessageId());
        Assert.assertEquals(1, context.getLanguageNames().size());
        Assert.assertEquals("simple", context.getLanguageNames().get(0));
    }

    @Test
    public void testComplexExpressions() throws Exception {
        assertExpression("hey ${in.header.foo}", "hey abc");
        assertExpression("hey ${in.header.foo}!", "hey abc!");
        assertExpression("hey ${in.header.foo}-${in.header.foo}!", "hey abc-abc!");
        assertExpression("hey ${in.header.foo}${in.header.foo}", "hey abcabc");
        assertExpression("${in.header.foo}${in.header.foo}", "abcabc");
        assertExpression("${in.header.foo}", "abc");
        assertExpression("${in.header.foo}!", "abc!");
    }

    @Test
    public void testComplexExpressionsUsingAlternativeStartToken() throws Exception {
        assertExpression("hey $simple{in.header.foo}", "hey abc");
        assertExpression("hey $simple{in.header.foo}!", "hey abc!");
        assertExpression("hey $simple{in.header.foo}-$simple{in.header.foo}!", "hey abc-abc!");
        assertExpression("hey $simple{in.header.foo}$simple{in.header.foo}", "hey abcabc");
        assertExpression("$simple{in.header.foo}$simple{in.header.foo}", "abcabc");
        assertExpression("$simple{in.header.foo}", "abc");
        assertExpression("$simple{in.header.foo}!", "abc!");
    }

    @Test
    public void testInvalidComplexExpression() throws Exception {
        try {
            assertExpression("hey ${foo", "bad expression!");
            Assert.fail("Should have thrown an exception!");
        } catch (SimpleIllegalSyntaxException e) {
            Assert.assertEquals(8, e.getIndex());
        }
    }

    @Test
    public void testPredicates() throws Exception {
        assertPredicate("${body}");
        assertPredicate("${header.foo}");
        assertPredicate("${header.madeUpHeader}", false);
    }

    @Test
    public void testExceptionMessage() throws Exception {
        exchange.setException(new IllegalArgumentException("Just testing"));
        assertExpression("${exception.message}", "Just testing");
        assertExpression("Hello ${exception.message} World", "Hello Just testing World");
    }

    @Test
    public void testExceptionStacktrace() throws Exception {
        exchange.setException(new IllegalArgumentException("Just testing"));
        String out = SimpleLanguage.simple("${exception.stacktrace}").evaluate(exchange, String.class);
        Assert.assertNotNull(out);
        Assert.assertTrue(out.startsWith("java.lang.IllegalArgumentException: Just testing"));
        Assert.assertTrue(out.contains("at org.apache.camel.language."));
    }

    @Test
    public void testException() throws Exception {
        exchange.setException(new IllegalArgumentException("Just testing"));
        Exception out = SimpleLanguage.simple("${exception}").evaluate(exchange, Exception.class);
        Assert.assertNotNull(out);
        TestSupport.assertIsInstanceOf(IllegalArgumentException.class, out);
        Assert.assertEquals("Just testing", out.getMessage());
    }

    @Test
    public void testBodyAs() throws Exception {
        assertExpression("${bodyAs(String)}", "<hello id='m123'>world!</hello>");
        assertExpression("${bodyAs('String')}", "<hello id='m123'>world!</hello>");
        exchange.getIn().setBody(null);
        assertExpression("${bodyAs('String')}", null);
        exchange.getIn().setBody(456);
        assertExpression("${bodyAs(Integer)}", 456);
        assertExpression("${bodyAs(int)}", 456);
        assertExpression("${bodyAs('int')}", 456);
        try {
            assertExpression("${bodyAs(XXX)}", 456);
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(ClassNotFoundException.class, e.getCause());
        }
    }

    @Test
    public void testMandatoryBodyAs() throws Exception {
        assertExpression("${mandatoryBodyAs(String)}", "<hello id='m123'>world!</hello>");
        assertExpression("${mandatoryBodyAs('String')}", "<hello id='m123'>world!</hello>");
        exchange.getIn().setBody(null);
        try {
            assertExpression("${mandatoryBodyAs('String')}", "");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(InvalidPayloadException.class, e.getCause());
        }
        exchange.getIn().setBody(456);
        assertExpression("${mandatoryBodyAs(Integer)}", 456);
        assertExpression("${mandatoryBodyAs(int)}", 456);
        assertExpression("${mandatoryBodyAs('int')}", 456);
        try {
            assertExpression("${mandatoryBodyAs(XXX)}", 456);
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(ClassNotFoundException.class, e.getCause());
        }
    }

    @Test
    public void testHeaderEmptyBody() throws Exception {
        // set an empty body
        exchange.getIn().setBody(null);
        assertExpression("${header.foo}", "abc");
        assertExpression("${headers.foo}", "abc");
        assertExpression("${in.header.foo}", "abc");
        assertExpression("${in.headers.foo}", "abc");
    }

    @Test
    public void testHeadersWithBracket() throws Exception {
        assertExpression("${headers[foo]}", "abc");
        assertExpression("${in.headers[foo]}", "abc");
    }

    @Test
    public void testOnglOnHeadersWithBracket() throws Exception {
        assertOnglOnHeadersWithSquareBrackets("order");
        assertOnglOnHeadersWithSquareBrackets("purchase.order");
        assertOnglOnHeadersWithSquareBrackets("foo.bar.qux");
        assertOnglOnHeadersWithSquareBrackets("purchase order");
    }

    @Test
    public void testOnglOnExchangePropertiesWithBracket() throws Exception {
        assertOnglOnExchangePropertiesWithBracket("order");
        assertOnglOnExchangePropertiesWithBracket("purchase.order");
        assertOnglOnExchangePropertiesWithBracket("foo.bar.qux");
        assertOnglOnExchangePropertiesWithBracket("purchase order");
    }

    @Test
    public void testIsInstanceOfEmptyBody() throws Exception {
        // set an empty body
        exchange.getIn().setBody(null);
        try {
            assertPredicate("${body} is null", false);
            Assert.fail("Should have thrown an exception");
        } catch (SimpleIllegalSyntaxException e) {
            Assert.assertEquals(11, e.getIndex());
        }
    }

    @Test
    public void testHeaders() throws Exception {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        Assert.assertEquals(2, headers.size());
        assertExpression("${headers}", headers);
        assertExpression("${in.headers}", headers);
    }

    @Test
    public void testHeaderKeyWithSpace() throws Exception {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        headers.put("some key", "Some Value");
        Assert.assertEquals(3, headers.size());
        assertExpression("${headerAs(foo,String)}", "abc");
        assertExpression("${headerAs(some key,String)}", "Some Value");
        assertExpression("${headerAs('some key',String)}", "Some Value");
        assertExpression("${header[foo]}", "abc");
        assertExpression("${header[some key]}", "Some Value");
        assertExpression("${header['some key']}", "Some Value");
        assertExpression("${headers[foo]}", "abc");
        assertExpression("${headers[some key]}", "Some Value");
        assertExpression("${headers['some key']}", "Some Value");
    }

    @Test
    public void testHeaderAs() throws Exception {
        assertExpression("${headerAs(foo,String)}", "abc");
        assertExpression("${headerAs(bar,int)}", 123);
        assertExpression("${headerAs(bar, int)}", 123);
        assertExpression("${headerAs('bar', int)}", 123);
        assertExpression("${headerAs('bar','int')}", 123);
        assertExpression("${headerAs('bar','Integer')}", 123);
        assertExpression("${headerAs(\'bar\',\"int\")}", 123);
        assertExpression("${headerAs(bar,String)}", "123");
        assertExpression("${headerAs(unknown,String)}", null);
        try {
            assertExpression("${headerAs(unknown String)}", null);
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Valid syntax: ${headerAs(key, type)} was: headerAs(unknown String)"));
        }
        try {
            assertExpression("${headerAs(fool,String).test}", null);
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Valid syntax: ${headerAs(key, type)} was: headerAs(fool,String).test"));
        }
        try {
            assertExpression("${headerAs(bar,XXX)}", 123);
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(ClassNotFoundException.class, e.getCause());
        }
    }

    @Test
    public void testIllegalSyntax() throws Exception {
        try {
            assertExpression("hey ${xxx} how are you?", "");
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Unknown function: xxx at location 4"));
        }
        try {
            assertExpression("${xxx}", "");
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Unknown function: xxx at location 0"));
        }
        try {
            assertExpression("${bodyAs(xxx}", "");
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Valid syntax: ${bodyAs(type)} was: bodyAs(xxx"));
        }
    }

    @Test
    public void testOGNLHeaderList() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("Camel in Action");
        lines.add("ActiveMQ in Action");
        exchange.getIn().setHeader("wicket", lines);
        assertExpression("${header.wicket[0]}", "Camel in Action");
        assertExpression("${header.wicket[1]}", "ActiveMQ in Action");
        try {
            assertExpression("${header.wicket[2]}", "");
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            if ((TestSupport.getJavaMajorVersion()) <= 8) {
                Assert.assertEquals(SimpleTest.JAVA8_INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            } else {
                Assert.assertEquals(SimpleTest.INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            }
        }
        assertExpression("${header.unknown[cool]}", null);
    }

    @Test
    public void testOGNLHeaderLinesList() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        exchange.getIn().setHeader("wicket", lines);
        assertExpression("${header.wicket[0].getId}", 123);
        assertExpression("${header.wicket[1].getName}", "ActiveMQ in Action");
        try {
            assertExpression("${header.wicket[2]}", "");
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            if ((TestSupport.getJavaMajorVersion()) <= 8) {
                Assert.assertEquals(SimpleTest.JAVA8_INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            } else {
                Assert.assertEquals(SimpleTest.INDEX_OUT_OF_BOUNDS_ERROR_MSG, cause.getMessage());
            }
        }
        assertExpression("${header.unknown[cool]}", null);
    }

    @Test
    public void testOGNLHeaderMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("cool", "Camel rocks");
        map.put("dude", "Hey dude");
        map.put("code", 4321);
        exchange.getIn().setHeader("wicket", map);
        assertExpression("${header.wicket[cool]}", "Camel rocks");
        assertExpression("${header.wicket[dude]}", "Hey dude");
        assertExpression("${header.wicket[unknown]}", null);
        assertExpression("${header.wicket[code]}", 4321);
        // no header named unknown
        assertExpression("${header?.unknown[cool]}", null);
        assertExpression("${header.unknown[cool]}", null);
    }

    @Test
    public void testOGNLHeaderMapWithDot() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("this.code", "This code");
        exchange.getIn().setHeader("wicket", map);
        assertExpression("${header.wicket[this.code]}", "This code");
    }

    @Test
    public void testOGNLHeaderMapNotMap() throws Exception {
        try {
            assertExpression("${header.foo[bar]}", null);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            Assert.assertEquals("Key: bar not found in bean: abc of type: java.lang.String using OGNL path [[bar]]", cause.getMessage());
        }
    }

    @Test
    public void testOGNLHeaderMapIllegalSyntax() throws Exception {
        try {
            assertExpression("${header.foo[bar}", null);
            Assert.fail("Should have thrown an exception");
        } catch (ExpressionIllegalSyntaxException e) {
            Assert.assertTrue(e.getMessage().startsWith("Valid syntax: ${header.name[key]} was: header.foo[bar"));
        }
    }

    @Test
    public void testBodyOGNLAsMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "Camel");
        map.put("bar", 6);
        exchange.getIn().setBody(map);
        assertExpression("${in.body[foo]}", "Camel");
        assertExpression("${in.body[bar]}", 6);
    }

    @Test
    public void testBodyOGNLAsMapWithDot() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("foo.bar", "Camel");
        exchange.getIn().setBody(map);
        assertExpression("${in.body[foo.bar]}", "Camel");
    }

    @Test
    public void testBodyOGNLAsMapShorthand() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "Camel");
        map.put("bar", 6);
        exchange.getIn().setBody(map);
        assertExpression("${body[foo]}", "Camel");
        assertExpression("${body[bar]}", 6);
    }

    @Test
    public void testBodyOGNLSimple() throws Exception {
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        exchange.getIn().setBody(camel);
        assertExpression("${in.body.getName}", "Camel");
        assertExpression("${in.body.getAge}", 6);
    }

    @Test
    public void testExceptionOGNLSimple() throws Exception {
        exchange.getIn().setHeader(AUTHENTICATION_FAILURE_POLICY_ID, "myPolicy");
        exchange.setProperty(EXCEPTION_CAUGHT, new org.apache.camel.CamelAuthorizationException("The camel authorization exception", exchange));
        assertExpression("${exception.getPolicyId}", "myPolicy");
    }

    @Test
    public void testBodyOGNLSimpleShorthand() throws Exception {
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        exchange.getIn().setBody(camel);
        assertExpression("${in.body.name}", "Camel");
        assertExpression("${in.body.age}", 6);
    }

    @Test
    public void testBodyOGNLSimpleOperator() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        camel.setFriend(tiger);
        exchange.getIn().setBody(camel);
        assertPredicate("${in.body.getName} contains 'Camel'", true);
        assertPredicate("${in.body.getName} contains 'Tiger'", false);
        assertPredicate("${in.body.getAge} < 10", true);
        assertPredicate("${in.body.getAge} > 10", false);
        assertPredicate("${in.body.getAge} <= '6'", true);
        assertPredicate("${in.body.getAge} > '6'", false);
        assertPredicate("${in.body.getAge} < ${body.getFriend.getAge}", true);
        assertPredicate("${in.body.getFriend.isDangerous} == true", true);
    }

    @Test
    public void testBodyOGNLSimpleOperatorShorthand() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        camel.setFriend(tiger);
        exchange.getIn().setBody(camel);
        assertPredicate("${in.body.name} contains 'Camel'", true);
        assertPredicate("${in.body.name} contains 'Tiger'", false);
        assertPredicate("${in.body.age} < 10", true);
        assertPredicate("${in.body.age} > 10", false);
        assertPredicate("${in.body.age} <= '6'", true);
        assertPredicate("${in.body.age} > '6'", false);
        assertPredicate("${in.body.age} < ${body.friend.age}", true);
        assertPredicate("${in.body.friend.dangerous} == true", true);
    }

    @Test
    public void testBodyOGNLNested() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        camel.setFriend(tiger);
        exchange.getIn().setBody(camel);
        assertExpression("${in.body.getName}", "Camel");
        assertExpression("${in.body.getAge}", 6);
        assertExpression("${in.body.getFriend.getName}", "Tony the Tiger");
        assertExpression("${in.body.getFriend.getAge}", "13");
    }

    @Test
    public void testBodyOGNLNestedShorthand() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        camel.setFriend(tiger);
        exchange.getIn().setBody(camel);
        assertExpression("${in.body.name}", "Camel");
        assertExpression("${in.body.age}", 6);
        assertExpression("${in.body.friend.name}", "Tony the Tiger");
        assertExpression("${in.body.friend.age}", "13");
    }

    @Test
    public void testBodyOGNLOrderList() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        assertExpression("${in.body.getLines[0].getId}", 123);
        assertExpression("${in.body.getLines[0].getName}", "Camel in Action");
        assertExpression("${in.body.getLines[1].getId}", 456);
        assertExpression("${in.body.getLines[1].getName}", "ActiveMQ in Action");
        assertExpression("${in.body.getLines[last].getId}", 456);
        assertExpression("${in.body.getLines[last].getName}", "ActiveMQ in Action");
        assertExpression("${in.body.getLines[last-1].getId}", 123);
        assertExpression("${in.body.getLines[last-1].getName}", "Camel in Action");
    }

    @Test
    public void testBodyOGNLOrderListShorthand() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        assertExpression("${in.body.lines[0].id}", 123);
        assertExpression("${in.body.lines[0].name}", "Camel in Action");
        assertExpression("${in.body.lines[1].id}", 456);
        assertExpression("${in.body.lines[1].name}", "ActiveMQ in Action");
        assertExpression("${in.body.lines[last].id}", 456);
        assertExpression("${in.body.lines[last].name}", "ActiveMQ in Action");
        assertExpression("${in.body.lines[last-1].id}", 123);
        assertExpression("${in.body.lines[last-1].name}", "Camel in Action");
        assertExpression("${in.body.lines.size}", 2);
    }

    @Test
    public void testBodyOGNLListMap() throws Exception {
        List<Map<String, String>> grid = new ArrayList<>();
        Map<String, String> cells = new LinkedHashMap<>();
        cells.put("ABC", "123");
        cells.put("DEF", "456");
        grid.add(cells);
        Map<String, String> cells2 = new LinkedHashMap<>();
        cells2.put("HIJ", "789");
        grid.add(cells2);
        exchange.getIn().setBody(grid);
        assertExpression("${in.body[0][ABC]}", "123");
        assertExpression("${in.body[0][DEF]}", "456");
        assertExpression("${in.body[0]['ABC']}", "123");
        assertExpression("${in.body[0]['DEF']}", "456");
        assertExpression("${in.body[1][HIJ]}", "789");
        assertExpression("${in.body[1]['HIJ']}", "789");
    }

    @Test
    public void testBodyOGNLList() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        exchange.getIn().setBody(lines);
        assertExpression("${in.body[0].getId}", 123);
        assertExpression("${in.body[0].getName}", "Camel in Action");
        assertExpression("${in.body[1].getId}", 456);
        assertExpression("${in.body[1].getName}", "ActiveMQ in Action");
    }

    @Test
    public void testBodyOGNLListShorthand() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        exchange.getIn().setBody(lines);
        assertExpression("${in.body[0].id}", 123);
        assertExpression("${in.body[0].name}", "Camel in Action");
        assertExpression("${in.body[1].id}", 456);
        assertExpression("${in.body[1].name}", "ActiveMQ in Action");
    }

    @Test
    public void testBodyOGNLArray() throws Exception {
        SimpleTest.OrderLine[] lines = new SimpleTest.OrderLine[2];
        lines[0] = new SimpleTest.OrderLine(123, "Camel in Action");
        lines[1] = new SimpleTest.OrderLine(456, "ActiveMQ in Action");
        exchange.getIn().setBody(lines);
        assertExpression("${in.body[0].getId}", 123);
        assertExpression("${in.body[0].getName}", "Camel in Action");
        assertExpression("${in.body[1].getId}", 456);
        assertExpression("${in.body[1].getName}", "ActiveMQ in Action");
    }

    @Test
    public void testBodyOGNLArrayShorthand() throws Exception {
        SimpleTest.OrderLine[] lines = new SimpleTest.OrderLine[2];
        lines[0] = new SimpleTest.OrderLine(123, "Camel in Action");
        lines[1] = new SimpleTest.OrderLine(456, "ActiveMQ in Action");
        exchange.getIn().setBody(lines);
        assertExpression("${in.body[0].id}", 123);
        assertExpression("${in.body[0].name}", "Camel in Action");
        assertExpression("${in.body[1].id}", 456);
        assertExpression("${in.body[1].name}", "ActiveMQ in Action");
    }

    @Test
    public void testBodyOGNLOrderListOutOfBounds() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        try {
            assertExpression("${in.body.getLines[3].getId}", 123);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            Assert.assertTrue(cause.getMessage().startsWith("Index: 3, Size: 2 out of bounds with List from bean"));
        }
        try {
            assertExpression("${in.body.getLines[last-2].getId}", 123);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            Assert.assertTrue(cause.getMessage().startsWith("Index: -1, Size: 2 out of bounds with List from bean"));
        }
        try {
            assertExpression("${in.body.getLines[last - XXX].getId}", 123);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            ExpressionIllegalSyntaxException cause = TestSupport.assertIsInstanceOf(ExpressionIllegalSyntaxException.class, e.getCause());
            Assert.assertEquals("last - XXX", cause.getExpression());
        }
    }

    @Test
    public void testBodyOGNLOrderListOutOfBoundsShorthand() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        try {
            assertExpression("${in.body.lines[3].id}", 123);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            Assert.assertTrue(cause.getMessage().startsWith("Index: 3, Size: 2 out of bounds with List from bean"));
        }
        try {
            assertExpression("${in.body.lines[last - 2].id}", 123);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            IndexOutOfBoundsException cause = TestSupport.assertIsInstanceOf(IndexOutOfBoundsException.class, e.getCause());
            Assert.assertTrue(cause.getMessage().startsWith("Index: -1, Size: 2 out of bounds with List from bean"));
        }
        try {
            assertExpression("${in.body.lines[last - XXX].id}", 123);
            Assert.fail("Should have thrown an exception");
        } catch (RuntimeBeanExpressionException e) {
            ExpressionIllegalSyntaxException cause = TestSupport.assertIsInstanceOf(ExpressionIllegalSyntaxException.class, e.getCause());
            Assert.assertEquals("last - XXX", cause.getExpression());
        }
    }

    @Test
    public void testBodyOGNLOrderListOutOfBoundsWithNullSafe() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        assertExpression("${in.body?.getLines[3].getId}", null);
    }

    @Test
    public void testBodyOGNLOrderListOutOfBoundsWithNullSafeShorthand() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        assertExpression("${in.body?.lines[3].id}", null);
    }

    @Test
    public void testBodyOGNLOrderListNoMethodNameWithNullSafe() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        try {
            assertExpression("${in.body.getLines[0]?.getRating}", "");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeBeanExpressionException e) {
            MethodNotFoundException cause = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, e.getCause());
            Assert.assertEquals("getRating", cause.getMethodName());
        }
    }

    @Test
    public void testBodyOGNLOrderListNoMethodNameWithNullSafeShorthand() throws Exception {
        List<SimpleTest.OrderLine> lines = new ArrayList<>();
        lines.add(new SimpleTest.OrderLine(123, "Camel in Action"));
        lines.add(new SimpleTest.OrderLine(456, "ActiveMQ in Action"));
        SimpleTest.Order order = new SimpleTest.Order(lines);
        exchange.getIn().setBody(order);
        try {
            assertExpression("${in.body.lines[0]?.rating}", "");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeBeanExpressionException e) {
            MethodNotFoundException cause = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, e.getCause());
            Assert.assertEquals("rating", cause.getMethodName());
        }
    }

    @Test
    public void testBodyOGNLNullSafeToAvoidNPE() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        camel.setFriend(tiger);
        exchange.getIn().setBody(camel);
        assertExpression("${in.body.getName}", "Camel");
        assertExpression("${in.body.getAge}", 6);
        assertExpression("${in.body.getFriend.getName}", "Tony the Tiger");
        assertExpression("${in.body.getFriend.getAge}", "13");
        // using null safe to avoid the NPE
        assertExpression("${in.body.getFriend?.getFriend.getName}", null);
        try {
            // without null safe we get an NPE
            assertExpression("${in.body.getFriend.getFriend.getName}", "");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeBeanExpressionException e) {
            Assert.assertEquals(("Failed to invoke method: .getFriend.getFriend.getName on org.apache.camel.language.simple.SimpleTest.Animal" + " due last method returned null and therefore cannot continue to invoke method .getName on a null instance"), e.getMessage());
        }
    }

    @Test
    public void testBodyOGNLNullSafeToAvoidNPEShorthand() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        camel.setFriend(tiger);
        exchange.getIn().setBody(camel);
        assertExpression("${in.body.name}", "Camel");
        assertExpression("${in.body.age}", 6);
        // just to mix it a bit
        assertExpression("${in.body.friend.getName}", "Tony the Tiger");
        assertExpression("${in.body.getFriend.age}", "13");
        // using null safe to avoid the NPE
        assertExpression("${in.body.friend?.friend.name}", null);
        try {
            // without null safe we get an NPE
            assertExpression("${in.body.friend.friend.name}", "");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeBeanExpressionException e) {
            Assert.assertEquals(("Failed to invoke method: .friend.friend.name on org.apache.camel.language.simple.SimpleTest.Animal" + " due last method returned null and therefore cannot continue to invoke method .name on a null instance"), e.getMessage());
        }
    }

    @Test
    public void testBodyOGNLReentrant() throws Exception {
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal elephant = new SimpleTest.Animal("Big Ella", 48);
        camel.setFriend(tiger);
        tiger.setFriend(elephant);
        elephant.setFriend(camel);
        exchange.getIn().setBody(camel);
        assertExpression("${body.getFriend.getFriend.getFriend.getName}", "Camel");
        assertExpression("${body.getFriend.getFriend.getFriend.getFriend.getName}", "Tony the Tiger");
        assertExpression("${body.getFriend.getFriend.getFriend.getFriend.getFriend.getName}", "Big Ella");
    }

    @Test
    public void testBodyOGNLReentrantShorthand() throws Exception {
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        SimpleTest.Animal elephant = new SimpleTest.Animal("Big Ella", 48);
        camel.setFriend(tiger);
        tiger.setFriend(elephant);
        elephant.setFriend(camel);
        exchange.getIn().setBody(camel);
        assertExpression("${body.friend.friend.friend.name}", "Camel");
        assertExpression("${body.friend.friend.friend.friend.name}", "Tony the Tiger");
        assertExpression("${body.friend.friend.friend.friend.friend.name}", "Big Ella");
    }

    @Test
    public void testBodyOGNLBoolean() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        exchange.getIn().setBody(tiger);
        assertExpression("${body.isDangerous}", "true");
        assertExpression("${body.dangerous}", "true");
        SimpleTest.Animal camel = new SimpleTest.Animal("Camel", 6);
        exchange.getIn().setBody(camel);
        assertExpression("${body.isDangerous}", "false");
        assertExpression("${body.dangerous}", "false");
    }

    @Test
    public void testBodyOgnlOnString() throws Exception {
        exchange.getIn().setBody("Camel");
        assertExpression("${body.substring(2)}", "mel");
        assertExpression("${body.substring(2, 4)}", "me");
        assertExpression("${body.length()}", 5);
        assertExpression("${body.toUpperCase()}", "CAMEL");
        assertExpression("${body.toUpperCase()}", "CAMEL");
        assertExpression("${body.toUpperCase().substring(2)}", "MEL");
        assertExpression("${body.toLowerCase().length()}", 5);
    }

    @Test
    public void testBodyOgnlOnStringWithOgnlParams() throws Exception {
        exchange.getIn().setBody("Camel");
        exchange.getIn().setHeader("max", 4);
        exchange.getIn().setHeader("min", 2);
        assertExpression("${body.substring(${header.min}, ${header.max})}", "me");
    }

    @Test
    public void testHeaderOgnlOnStringWithOgnlParams() throws Exception {
        exchange.getIn().setBody(null);
        exchange.getIn().setHeader("name", "Camel");
        exchange.getIn().setHeader("max", 4);
        exchange.getIn().setHeader("min", 2);
        assertExpression("${header.name.substring(${header.min}, ${header.max})}", "me");
    }

    @Test
    public void testCamelContextStartRoute() throws Exception {
        exchange.getIn().setBody(null);
        assertExpression("${camelContext.getRouteController().startRoute('foo')}", null);
    }

    @Test
    public void testBodyOgnlReplace() throws Exception {
        exchange.getIn().setBody("Kamel is a cool Kamel");
        assertExpression("${body.replace(\"Kamel\", \"Camel\")}", "Camel is a cool Camel");
    }

    @Test
    public void testBodyOgnlReplaceEscapedChar() throws Exception {
        exchange.getIn().setBody("foo$bar$baz");
        assertExpression("${body.replace('$', '-')}", "foo-bar-baz");
    }

    @Test
    public void testBodyOgnlReplaceEscapedBackslashChar() throws Exception {
        exchange.getIn().setBody("foo\\bar\\baz");
        assertExpression("${body.replace(\'\\\', \'\\\\\')}", "foo\\\\bar\\\\baz");
    }

    @Test
    public void testBodyOgnlReplaceFirst() throws Exception {
        exchange.getIn().setBody("http:camel.apache.org");
        assertExpression("${body.replaceFirst('http:', 'http4:')}", "http4:camel.apache.org");
        assertExpression("${body.replaceFirst('http:', '')}", "camel.apache.org");
        assertExpression("${body.replaceFirst('http:', ' ')}", " camel.apache.org");
        assertExpression("${body.replaceFirst('http:',    ' ')}", " camel.apache.org");
        assertExpression("${body.replaceFirst('http:',' ')}", " camel.apache.org");
    }

    @Test
    public void testBodyOgnlReplaceSingleQuoteInDouble() throws Exception {
        exchange.getIn().setBody("Hello O'Conner");
        assertExpression("${body.replace(\"O\'C\", \"OC\")}", "Hello OConner");
        assertExpression("${body.replace(\"O\'C\", \"O C\")}", "Hello O Conner");
        assertExpression("${body.replace(\"O\'C\", \"O-C\")}", "Hello O-Conner");
        assertExpression("${body.replace(\"O\'C\", \"O\'\'C\")}", "Hello O''Conner");
        assertExpression("${body.replace(\"O\'C\", \"O\n\'C\")}", "Hello O\n\'Conner");
    }

    @Test
    public void testBodyOgnlSpaces() throws Exception {
        exchange.getIn().setBody("Hello World");
        // no quotes, which is discouraged to use
        assertExpression("${body.compareTo(Hello World)}", 0);
        assertExpression("${body.compareTo('Hello World')}", 0);
        assertExpression("${body.compareTo(${body})}", 0);
        assertExpression("${body.compareTo('foo')}", "Hello World".compareTo("foo"));
        assertExpression("${body.compareTo( 'Hello World' )}", 0);
        assertExpression("${body.compareTo( ${body} )}", 0);
        assertExpression("${body.compareTo( 'foo' )}", "Hello World".compareTo("foo"));
    }

    @Test
    public void testClassSimpleName() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        exchange.getIn().setBody(tiger);
        assertExpression("${body.getClass().getSimpleName()}", "Animal");
        assertExpression("${body.getClass.getSimpleName}", "Animal");
        assertExpression("${body.class.simpleName}", "Animal");
    }

    @Test
    public void testExceptionClassSimpleName() throws Exception {
        SimpleTest.Animal tiger = new SimpleTest.Animal("Tony the Tiger", 13);
        exchange.getIn().setBody(tiger);
        Exception cause = new IllegalArgumentException("Forced");
        exchange.setException(cause);
        assertExpression("${exception.getClass().getSimpleName()}", "IllegalArgumentException");
        assertExpression("${exception.getClass.getSimpleName}", "IllegalArgumentException");
        assertExpression("${exception.class.simpleName}", "IllegalArgumentException");
    }

    @Test
    public void testSlashBeforeHeader() throws Exception {
        assertExpression("foo/${header.foo}", "foo/abc");
        assertExpression("foo\\${header.foo}", "foo\\abc");
    }

    @Test
    public void testJSonLike() throws Exception {
        exchange.getIn().setBody("Something");
        assertExpression("{\n\"data\": \"${body}\"\n}", "{\n\"data\": \"Something\"\n}");
    }

    @Test
    public void testFunctionEnds() throws Exception {
        exchange.getIn().setBody("Something");
        assertExpression("{{", "{{");
        assertExpression("}}", "}}");
        assertExpression("{{}}", "{{}}");
        assertExpression("{{foo}}", "{{foo}}");
        assertExpression("{{${body}}}", "{{Something}}");
        assertExpression("{{${body}-${body}}}", "{{Something-Something}}");
    }

    @Test
    public void testEscape() throws Exception {
        exchange.getIn().setBody("Something");
        // slash foo
        assertExpression("\\foo", "\\foo");
        assertExpression("\\n${body}", "\nSomething");
        assertExpression("\\t${body}", "\tSomething");
        assertExpression("\\r${body}", "\rSomething");
        assertExpression("\\n\\r${body}", "\n\rSomething");
        assertExpression("\\n${body}\\n", "\nSomething\n");
        assertExpression("\\t${body}\\t", "\tSomething\t");
        assertExpression("\\r${body}\\r", "\rSomething\r");
        assertExpression("\\n\\r${body}\\n\\r", "\n\rSomething\n\r");
        assertExpression("$${body}", "$Something");
    }

    @Test
    public void testEscapeEndFunction() throws Exception {
        exchange.getIn().setBody("Something");
        assertExpression("{hello\\}", "{hello}");
        assertExpression("${body}{hello\\}", "Something{hello}");
    }

    @Test
    public void testCamelContextOGNL() throws Exception {
        assertExpression("${camelContext.getName()}", context.getName());
        assertExpression("${camelContext.version}", context.getVersion());
    }

    @Test
    public void testTypeConstant() throws Exception {
        assertExpression("${type:org.apache.camel.Exchange.FILE_NAME}", FILE_NAME);
        assertExpression("${type:org.apache.camel.ExchangePattern.InOut}", InOut);
        // non existing fields
        assertExpression("${type:org.apache.camel.ExchangePattern.}", null);
        assertExpression("${type:org.apache.camel.ExchangePattern.UNKNOWN}", null);
    }

    @Test
    public void testTypeConstantInnerClass() throws Exception {
        assertExpression("${type:org.apache.camel.language.simple.Constants$MyInnerStuff.FOO}", 123);
        assertExpression("${type:org.apache.camel.language.simple.Constants.BAR}", 456);
    }

    @Test
    public void testStringArrayLength() throws Exception {
        exchange.getIn().setBody(new String[]{ "foo", "bar" });
        assertExpression("${body[0]}", "foo");
        assertExpression("${body[1]}", "bar");
        assertExpression("${body.length}", 2);
        exchange.getIn().setBody(new String[]{ "foo", "bar", "beer" });
        assertExpression("${body.length}", 3);
    }

    @Test
    public void testByteArrayLength() throws Exception {
        exchange.getIn().setBody(new byte[]{ 65, 66, 67 });
        assertExpression("${body[0]}", 65);
        assertExpression("${body[1]}", 66);
        assertExpression("${body[2]}", 67);
        assertExpression("${body.length}", 3);
    }

    @Test
    public void testIntArrayLength() throws Exception {
        exchange.getIn().setBody(new int[]{ 1, 20, 300 });
        assertExpression("${body[0]}", 1);
        assertExpression("${body[1]}", 20);
        assertExpression("${body[2]}", 300);
        assertExpression("${body.length}", 3);
    }

    @Test
    public void testSimpleMapBoolean() throws Exception {
        Map<String, Object> map = new HashMap<>();
        exchange.getIn().setBody(map);
        map.put("isCredit", true);
        assertPredicate("${body[isCredit]} == true", true);
        assertPredicate("${body[isCredit]} == false", false);
        assertPredicate("${body['isCredit']} == true", true);
        assertPredicate("${body['isCredit']} == false", false);
        // wrong case
        assertPredicate("${body['IsCredit']} == true", false);
        map.put("isCredit", false);
        assertPredicate("${body[isCredit]} == true", false);
        assertPredicate("${body[isCredit]} == false", true);
        assertPredicate("${body['isCredit']} == true", false);
        assertPredicate("${body['isCredit']} == false", true);
    }

    @Test
    public void testSimpleRegexp() throws Exception {
        exchange.getIn().setBody("12345678");
        assertPredicate("${body} regex \'\\d+\'", true);
        assertPredicate("${body} regex \'\\w{1,4}\'", false);
        exchange.getIn().setBody("tel:+97444549697");
        assertPredicate("${body} regex \'^(tel:\\+)(974)(44)(\\d+)|^(974)(44)(\\d+)\'", true);
        exchange.getIn().setBody("97444549697");
        assertPredicate("${body} regex \'^(tel:\\+)(974)(44)(\\d+)|^(974)(44)(\\d+)\'", true);
        exchange.getIn().setBody("tel:+87444549697");
        assertPredicate("${body} regex \'^(tel:\\+)(974)(44)(\\d+)|^(974)(44)(\\d+)\'", false);
        exchange.getIn().setBody("87444549697");
        assertPredicate("${body} regex \'^(tel:\\+)(974)(44)(\\d+)|^(974)(44)(\\d+)\'", false);
    }

    @Test
    public void testCollateEven() throws Exception {
        List<Object> data = new ArrayList<>();
        data.add("A");
        data.add("B");
        data.add("C");
        data.add("D");
        data.add("E");
        data.add("F");
        exchange.getIn().setBody(data);
        Iterator it = ((Iterator) (evaluateExpression("${collate(3)}", null)));
        List chunk = ((List) (it.next()));
        List chunk2 = ((List) (it.next()));
        Assert.assertFalse(it.hasNext());
        Assert.assertEquals(3, chunk.size());
        Assert.assertEquals(3, chunk2.size());
        Assert.assertEquals("A", chunk.get(0));
        Assert.assertEquals("B", chunk.get(1));
        Assert.assertEquals("C", chunk.get(2));
        Assert.assertEquals("D", chunk2.get(0));
        Assert.assertEquals("E", chunk2.get(1));
        Assert.assertEquals("F", chunk2.get(2));
    }

    @Test
    public void testCollateOdd() throws Exception {
        List<Object> data = new ArrayList<>();
        data.add("A");
        data.add("B");
        data.add("C");
        data.add("D");
        data.add("E");
        data.add("F");
        data.add("G");
        exchange.getIn().setBody(data);
        Iterator it = ((Iterator) (evaluateExpression("${collate(3)}", null)));
        List chunk = ((List) (it.next()));
        List chunk2 = ((List) (it.next()));
        List chunk3 = ((List) (it.next()));
        Assert.assertFalse(it.hasNext());
        Assert.assertEquals(3, chunk.size());
        Assert.assertEquals(3, chunk2.size());
        Assert.assertEquals(1, chunk3.size());
        Assert.assertEquals("A", chunk.get(0));
        Assert.assertEquals("B", chunk.get(1));
        Assert.assertEquals("C", chunk.get(2));
        Assert.assertEquals("D", chunk2.get(0));
        Assert.assertEquals("E", chunk2.get(1));
        Assert.assertEquals("F", chunk2.get(2));
        Assert.assertEquals("G", chunk3.get(0));
    }

    @Test
    public void testRandomExpression() throws Exception {
        int min = 1;
        int max = 10;
        int iterations = 30;
        int i = 0;
        for (i = 0; i < iterations; i++) {
            Expression expression = SimpleLanguage.simple("${random(1,10)}", Integer.class);
            Assert.assertTrue(((min <= (expression.evaluate(exchange, Integer.class))) && ((expression.evaluate(exchange, Integer.class)) < max)));
        }
        for (i = 0; i < iterations; i++) {
            Expression expression = SimpleLanguage.simple("${random(10)}", Integer.class);
            Assert.assertTrue(((0 <= (expression.evaluate(exchange, Integer.class))) && ((expression.evaluate(exchange, Integer.class)) < max)));
        }
        Expression expression = SimpleLanguage.simple("${random(1, 10)}", Integer.class);
        Assert.assertTrue(((min <= (expression.evaluate(exchange, Integer.class))) && ((expression.evaluate(exchange, Integer.class)) < max)));
        Expression expression1 = SimpleLanguage.simple("${random( 10)}", Integer.class);
        Assert.assertTrue(((0 <= (expression1.evaluate(exchange, Integer.class))) && ((expression1.evaluate(exchange, Integer.class)) < max)));
        try {
            assertExpression("${random(10,21,30)}", null);
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            Assert.assertEquals("Valid syntax: ${random(min,max)} or ${random(max)} was: random(10,21,30)", e.getCause().getMessage());
        }
        try {
            assertExpression("${random()}", null);
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            Assert.assertEquals("Valid syntax: ${random(min,max)} or ${random(max)} was: random()", e.getCause().getMessage());
        }
        exchange.getIn().setHeader("max", 20);
        Expression expression3 = SimpleLanguage.simple("${random(10,${header.max})}", Integer.class);
        int num = expression3.evaluate(exchange, Integer.class);
        Assert.assertTrue("Should be 10..20", ((num >= 0) && (num < 20)));
    }

    @Test
    public void testListRemoveByInstance() throws Exception {
        List<Object> data = new ArrayList<>();
        data.add("A");
        data.add("B");
        exchange.getIn().setBody(data);
        Assert.assertEquals(2, data.size());
        Expression expression = SimpleLanguage.simple("${body.remove('A')}");
        expression.evaluate(exchange, Object.class);
        Assert.assertEquals(1, data.size());
        Assert.assertEquals("B", data.get(0));
    }

    @Test
    public void testListRemoveIndex() throws Exception {
        List<Object> data = new ArrayList<>();
        data.add("A");
        data.add("B");
        exchange.getIn().setBody(data);
        Assert.assertEquals(2, data.size());
        Expression expression = SimpleLanguage.simple("${body.remove(0)}");
        expression.evaluate(exchange, Object.class);
        Assert.assertEquals(1, data.size());
        Assert.assertEquals("B", data.get(0));
    }

    @Test
    public void testBodyOgnlOnAnimalWithOgnlParams() throws Exception {
        exchange.getIn().setBody(new SimpleTest.Animal("tiger", 13));
        exchange.getIn().setHeader("friend", new SimpleTest.Animal("donkey", 4));
        assertExpression("${body.setFriend(${header.friend})}", null);
        SimpleTest.Animal animal = exchange.getIn().getBody(SimpleTest.Animal.class);
        Assert.assertEquals("tiger", animal.getName());
        Assert.assertEquals(13, animal.getAge());
        Assert.assertNotNull("Should have a friend", animal.getFriend());
        Assert.assertEquals("donkey", animal.getFriend().getName());
        Assert.assertEquals(4, animal.getFriend().getAge());
    }

    @Test
    public void testNestedTypeFunction() throws Exception {
        // when using type: function we need special logic to not lazy evaluate it so its evaluated only once
        // and won't fool Camel to think its a nested OGNL method call expression instead (CAMEL-10664)
        exchange.setProperty(AUTHENTICATION, 123);
        String exp = "${exchangeProperty.${type:org.apache.camel.Exchange.AUTHENTICATION}.toString()}";
        assertExpression(exp, "123");
        exchange.getIn().setHeader("whichOne", "AUTHENTICATION");
        exchange.setProperty(AUTHENTICATION, 456);
        exp = "${exchangeProperty.${type:org.apache.camel.Exchange.${header.whichOne}}.toString()}";
        assertExpression(exp, "456");
    }

    @Test
    public void testListIndexByNestedFunction() throws Exception {
        List<String> alist = new ArrayList<>();
        alist.add("1");
        alist.add("99");
        exchange.getIn().setHeader("ITEMS", alist);
        exchange.getIn().setHeader("TOTAL_LOOPS", alist.size());
        String exp = "${header.ITEMS[${exchangeProperty.CamelLoopIndex}]}";
        exchange.setProperty(LOOP_INDEX, 0);
        assertExpression(exp, "1");
        exchange.setProperty(LOOP_INDEX, 1);
        assertExpression(exp, "99");
    }

    public static final class Animal {
        private String name;

        private int age;

        private SimpleTest.Animal friend;

        private Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public SimpleTest.Animal getFriend() {
            return friend;
        }

        public void setFriend(SimpleTest.Animal friend) {
            this.friend = friend;
        }

        public boolean isDangerous() {
            return name.contains("Tiger");
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final class Order {
        private List<SimpleTest.OrderLine> lines;

        public Order(List<SimpleTest.OrderLine> lines) {
            this.lines = lines;
        }

        public List<SimpleTest.OrderLine> getLines() {
            return lines;
        }

        public void setLines(List<SimpleTest.OrderLine> lines) {
            this.lines = lines;
        }
    }

    public static final class OrderLine {
        private int id;

        private String name;

        public OrderLine(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class MyClass {
        public Object[] getMyArray() {
            return new Object[]{ "Hallo", "World", "!" };
        }
    }
}

