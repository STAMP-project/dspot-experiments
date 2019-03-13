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


import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.apache.camel.TypeConversionException;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


public class SimpleBuilderTest extends TestSupport {
    protected Exchange exchange = new org.apache.camel.support.DefaultExchange(new DefaultCamelContext());

    @Test
    public void testPredicate() throws Exception {
        exchange.getIn().setBody("foo");
        Assert.assertTrue(SimpleBuilder.simple("${body} == 'foo'").matches(exchange));
        Assert.assertFalse(SimpleBuilder.simple("${body} == 'bar'").matches(exchange));
    }

    @Test
    public void testExpression() throws Exception {
        exchange.getIn().setBody("foo");
        Assert.assertEquals("foo", SimpleBuilder.simple("${body}").evaluate(exchange, String.class));
        Assert.assertNull(SimpleBuilder.simple("${header.cheese}").evaluate(exchange, String.class));
    }

    @Test
    public void testFormatExpression() throws Exception {
        exchange.getIn().setHeader("head", "foo");
        Assert.assertEquals("foo", SimpleBuilder.simpleF("${header.%s}", "head").evaluate(exchange, String.class));
        Assert.assertNull(SimpleBuilder.simple("${header.cheese}").evaluate(exchange, String.class));
    }

    @Test
    public void testFormatExpressionWithResultType() throws Exception {
        exchange.getIn().setHeader("head", "200");
        Assert.assertEquals(200, SimpleBuilder.simpleF("${header.%s}", Integer.class, "head").evaluate(exchange, Object.class));
    }

    @Test
    public void testResultType() throws Exception {
        exchange.getIn().setBody("foo");
        exchange.getIn().setHeader("cool", true);
        Assert.assertEquals("foo", SimpleBuilder.simple("${body}", String.class).evaluate(exchange, Object.class));
        try {
            // error during conversion
            SimpleBuilder.simple("${body}", int.class).evaluate(exchange, Object.class);
            Assert.fail("Should have thrown exception");
        } catch (TypeConversionException e) {
            TestSupport.assertIsInstanceOf(NumberFormatException.class, e.getCause().getCause());
        }
        Assert.assertEquals(true, SimpleBuilder.simple("${header.cool}", boolean.class).evaluate(exchange, Object.class));
        Assert.assertEquals("true", SimpleBuilder.simple("${header.cool}", String.class).evaluate(exchange, Object.class));
        // not possible
        Assert.assertEquals(null, SimpleBuilder.simple("${header.cool}", int.class).evaluate(exchange, Object.class));
        Assert.assertEquals(true, SimpleBuilder.simple("${header.cool}").resultType(Boolean.class).evaluate(exchange, Object.class));
        Assert.assertEquals("true", SimpleBuilder.simple("${header.cool}").resultType(String.class).evaluate(exchange, Object.class));
        // not possible
        Assert.assertEquals(null, SimpleBuilder.simple("${header.cool}").resultType(int.class).evaluate(exchange, Object.class));
        // should be convertable to integers
        Assert.assertEquals(11, SimpleBuilder.simple("11", int.class).evaluate(exchange, Object.class));
    }

    @Test
    public void testRegexAllWithPlaceHolders() {
        exchange.getIn().setHeader("activateUrl", "http://some/rest/api/(id)/activate");
        Assert.assertEquals("http://some/rest/api/12/activate", SimpleBuilder.simple("${header.activateUrl.replaceAll(\"\\(id\\)\",\"12\")}").evaluate(exchange, String.class));
        // passes when contains { only
        exchange.getIn().setHeader("activateUrl", "http://some/rest/api/{id/activate");
        Assert.assertEquals("http://some/rest/api/12/activate", SimpleBuilder.simple("${header.activateUrl.replaceAll(\"\\{id\",\"12\")}").evaluate(exchange, String.class));
        String replaced = "http://some/rest/api/{id}/activate".replaceAll("\\{id\\}", "12");
        Assert.assertEquals("http://some/rest/api/12/activate", replaced);
        // passes when contains { }
        exchange.getIn().setHeader("activateUrl", "http://some/rest/api/{id}/activate");
        Assert.assertEquals("http://some/rest/api/12/activate", SimpleBuilder.simple("${header.activateUrl.replaceAll(\"\\{id\\}\",\"12\")}").evaluate(exchange, String.class));
        // passes when contains { } and another ${body} function
        exchange.getIn().setBody("12");
        Assert.assertEquals("http://some/rest/api/12/activate", SimpleBuilder.simple("${header.activateUrl.replaceAll(\"\\{id\\}\",\"${body}\")}").evaluate(exchange, String.class));
        // passes when } is escaped with \}
        Assert.assertEquals("http://some/rest/api/{}/activate", SimpleBuilder.simple("${header.activateUrl.replaceAll(\"\\{id\\}\",\"{\\}\")}").evaluate(exchange, String.class));
    }
}

