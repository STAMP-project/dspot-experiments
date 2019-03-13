/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.test.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link AopTestUtils}.
 *
 * @author Sam Brannen
 * @since 4.2
 */
public class AopTestUtilsTests {
    private final AopTestUtilsTests.FooImpl foo = new AopTestUtilsTests.FooImpl();

    @Test(expected = IllegalArgumentException.class)
    public void getTargetObjectForNull() {
        getTargetObject(null);
    }

    @Test
    public void getTargetObjectForNonProxiedObject() {
        AopTestUtilsTests.Foo target = getTargetObject(foo);
        Assert.assertSame(foo, target);
    }

    @Test
    public void getTargetObjectWrappedInSingleJdkDynamicProxy() {
        AopTestUtilsTests.Foo target = getTargetObject(jdkProxy(foo));
        Assert.assertSame(foo, target);
    }

    @Test
    public void getTargetObjectWrappedInSingleCglibProxy() {
        AopTestUtilsTests.Foo target = getTargetObject(cglibProxy(foo));
        Assert.assertSame(foo, target);
    }

    @Test
    public void getTargetObjectWrappedInDoubleJdkDynamicProxy() {
        AopTestUtilsTests.Foo target = getTargetObject(jdkProxy(jdkProxy(foo)));
        Assert.assertNotSame(foo, target);
    }

    @Test
    public void getTargetObjectWrappedInDoubleCglibProxy() {
        AopTestUtilsTests.Foo target = getTargetObject(cglibProxy(cglibProxy(foo)));
        Assert.assertNotSame(foo, target);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUltimateTargetObjectForNull() {
        getUltimateTargetObject(null);
    }

    @Test
    public void getUltimateTargetObjectForNonProxiedObject() {
        AopTestUtilsTests.Foo target = getUltimateTargetObject(foo);
        Assert.assertSame(foo, target);
    }

    @Test
    public void getUltimateTargetObjectWrappedInSingleJdkDynamicProxy() {
        AopTestUtilsTests.Foo target = getUltimateTargetObject(jdkProxy(foo));
        Assert.assertSame(foo, target);
    }

    @Test
    public void getUltimateTargetObjectWrappedInSingleCglibProxy() {
        AopTestUtilsTests.Foo target = getUltimateTargetObject(cglibProxy(foo));
        Assert.assertSame(foo, target);
    }

    @Test
    public void getUltimateTargetObjectWrappedInDoubleJdkDynamicProxy() {
        AopTestUtilsTests.Foo target = getUltimateTargetObject(jdkProxy(jdkProxy(foo)));
        Assert.assertSame(foo, target);
    }

    @Test
    public void getUltimateTargetObjectWrappedInDoubleCglibProxy() {
        AopTestUtilsTests.Foo target = getUltimateTargetObject(cglibProxy(cglibProxy(foo)));
        Assert.assertSame(foo, target);
    }

    @Test
    public void getUltimateTargetObjectWrappedInCglibProxyWrappedInJdkDynamicProxy() {
        AopTestUtilsTests.Foo target = getUltimateTargetObject(jdkProxy(cglibProxy(foo)));
        Assert.assertSame(foo, target);
    }

    @Test
    public void getUltimateTargetObjectWrappedInCglibProxyWrappedInDoubleJdkDynamicProxy() {
        AopTestUtilsTests.Foo target = getUltimateTargetObject(jdkProxy(jdkProxy(cglibProxy(foo))));
        Assert.assertSame(foo, target);
    }

    static interface Foo {}

    static class FooImpl implements AopTestUtilsTests.Foo {}
}

