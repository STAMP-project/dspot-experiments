/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.metaprogramming.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;
import org.teavm.metaprogramming.CompileTime;


@CompileTime
@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class ProxyTest {
    @Test
    public void createsProxy() {
        ProxyTest.A proxy = ProxyTest.createProxy(ProxyTest.A.class, "!");
        Assert.assertEquals("foo!", proxy.foo());
        Assert.assertEquals("bar!", proxy.bar());
    }

    @Test
    public void defaultReturnValue() {
        StringBuilder log = new StringBuilder();
        ProxyTest.B proxy = ProxyTest.createProxyWithDefaultReturnValue(ProxyTest.B.class, log);
        Assert.assertNull(proxy.foo());
        Assert.assertEquals(0, proxy.bar());
        proxy.baz();
        Assert.assertEquals("foo;bar;baz;", log.toString());
    }

    @Test
    public void boxParameters() {
        ProxyTest.C proxy = ProxyTest.createProxyWithBoxedParameters(ProxyTest.C.class);
        Assert.assertEquals("foo(true,0,1,2,3,4,5)", proxy.foo(true, ((byte) (0)), '1', ((short) (2)), 3, 4, "5"));
    }

    @MetaprogrammingClass
    interface A {
        String foo();

        String bar();
    }

    @MetaprogrammingClass
    interface B {
        String foo();

        int bar();

        void baz();
    }

    @MetaprogrammingClass
    interface C {
        String foo(boolean a, byte b, char c, short d, int e, long f, String g);
    }
}

