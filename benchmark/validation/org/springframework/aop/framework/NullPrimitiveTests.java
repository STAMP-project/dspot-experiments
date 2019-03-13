/**
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.aop.framework;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.aop.AopInvocationException;


/**
 * Test for SPR-4675. A null value returned from around advice is very hard to debug if
 * the caller expects a primitive.
 *
 * @author Dave Syer
 */
public class NullPrimitiveTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    static interface Foo {
        int getValue();
    }

    @Test
    public void testNullPrimitiveWithJdkProxy() {
        class SimpleFoo implements NullPrimitiveTests.Foo {
            @Override
            public int getValue() {
                return 100;
            }
        }
        SimpleFoo target = new SimpleFoo();
        ProxyFactory factory = new ProxyFactory(target);
        factory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                return null;
            }
        });
        NullPrimitiveTests.Foo foo = ((NullPrimitiveTests.Foo) (factory.getProxy()));
        thrown.expect(AopInvocationException.class);
        thrown.expectMessage("Foo.getValue()");
        Assert.assertEquals(0, foo.getValue());
    }

    public static class Bar {
        public int getValue() {
            return 100;
        }
    }

    @Test
    public void testNullPrimitiveWithCglibProxy() {
        NullPrimitiveTests.Bar target = new NullPrimitiveTests.Bar();
        ProxyFactory factory = new ProxyFactory(target);
        factory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                return null;
            }
        });
        NullPrimitiveTests.Bar bar = ((NullPrimitiveTests.Bar) (factory.getProxy()));
        thrown.expect(AopInvocationException.class);
        thrown.expectMessage("Bar.getValue()");
        Assert.assertEquals(0, bar.getValue());
    }
}

