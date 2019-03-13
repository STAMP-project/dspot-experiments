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
package org.apache.camel.language;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Header;
import org.apache.camel.LanguageTestSupport;
import org.apache.camel.Message;
import org.apache.camel.TestSupport;
import org.apache.camel.component.bean.MethodNotFoundException;
import org.apache.camel.language.bean.BeanLanguage;
import org.junit.Assert;
import org.junit.Test;


public class BeanTest extends LanguageTestSupport {
    @Test
    public void testSimpleExpressions() throws Exception {
        assertExpression("foo.echo('e::o')", "e::o");
        assertExpression("foo.echo('e.o')", "e.o");
        assertExpression("my.company.MyClass::echo('a')", "a");
        assertExpression("my.company.MyClass::echo('a.b')", "a.b");
        assertExpression("my.company.MyClass::echo('a::b')", "a::b");
        assertExpression("foo.cheese", "abc");
        assertExpression("foo?method=cheese", "abc");
        assertExpression("my.company.MyClass::cheese", "abc");
        assertExpression("foo?method=echo('e::o')", "e::o");
    }

    @Test
    public void testPredicates() throws Exception {
        assertPredicate("foo.isFooHeaderAbc");
        assertPredicate("foo?method=isFooHeaderAbc");
        assertPredicate("my.company.MyClass::isFooHeaderAbc");
    }

    @Test
    public void testDoubleColon() throws Exception {
        assertPredicate("foo::isFooHeaderAbc");
        assertPredicateFails("foo:isFooHeaderAbc");
    }

    @Test
    public void testBeanTypeExpression() throws Exception {
        Expression exp = BeanLanguage.bean(BeanTest.MyUser.class, null);
        Exchange exchange = createExchangeWithBody("Claus");
        Object result = exp.evaluate(exchange, Object.class);
        Assert.assertEquals("Hello Claus", result);
    }

    @Test
    public void testBeanTypeAndMethodExpression() throws Exception {
        Expression exp = BeanLanguage.bean(BeanTest.MyUser.class, "hello");
        Exchange exchange = createExchangeWithBody("Claus");
        Object result = exp.evaluate(exchange, Object.class);
        Assert.assertEquals("Hello Claus", result);
    }

    @Test
    public void testBeanInstanceAndMethodExpression() throws Exception {
        BeanTest.MyUser user = new BeanTest.MyUser();
        Expression exp = BeanLanguage.bean(user, "hello");
        Exchange exchange = createExchangeWithBody("Claus");
        Object result = exp.evaluate(exchange, Object.class);
        Assert.assertEquals("Hello Claus", result);
    }

    @Test
    public void testNoMethod() throws Exception {
        BeanTest.MyUser user = new BeanTest.MyUser();
        Expression exp = BeanLanguage.bean(user, "unknown");
        Exchange exchange = createExchangeWithBody("Claus");
        Object result = exp.evaluate(exchange, Object.class);
        Assert.assertNull(result);
        Assert.assertNotNull(exchange.getException());
        MethodNotFoundException e = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, exchange.getException());
        Assert.assertSame(user, e.getBean());
        Assert.assertEquals("unknown", e.getMethodName());
    }

    @Test
    public void testNoMethodBeanLookup() throws Exception {
        Expression exp = BeanLanguage.bean("foo.cake");
        Exchange exchange = createExchangeWithBody("Claus");
        Object result = exp.evaluate(exchange, Object.class);
        Assert.assertNull(result);
        Assert.assertNotNull(exchange.getException());
        MethodNotFoundException e = TestSupport.assertIsInstanceOf(MethodNotFoundException.class, exchange.getException());
        Assert.assertSame(context.getRegistry().lookupByName("foo"), e.getBean());
        Assert.assertEquals("cake", e.getMethodName());
    }

    public static class MyBean {
        public Object cheese(Exchange exchange) {
            Message in = exchange.getIn();
            return in.getHeader("foo");
        }

        public String echo(String echo) {
            return echo;
        }

        public boolean isFooHeaderAbc(@Header("foo")
        String foo) {
            return "abc".equals(foo);
        }
    }

    public static class MyUser {
        public String hello(String name) {
            return "Hello " + name;
        }
    }
}

