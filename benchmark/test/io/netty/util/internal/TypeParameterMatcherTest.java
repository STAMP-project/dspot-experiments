/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class TypeParameterMatcherTest {
    @Test
    public void testConcreteClass() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.TypeQ(), TypeParameterMatcherTest.TypeX.class, "A");
        Assert.assertFalse(m.match(new Object()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.A()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.AA()));
        Assert.assertTrue(m.match(new TypeParameterMatcherTest.AAA()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.B()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.BB()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.BBB()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.C()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.CC()));
    }

    @Test(expected = IllegalStateException.class)
    public void testUnsolvedParameter() throws Exception {
        TypeParameterMatcher.find(new TypeParameterMatcherTest.TypeQ(), TypeParameterMatcherTest.TypeX.class, "B");
    }

    @Test
    public void testAnonymousClass() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.TypeQ<TypeParameterMatcherTest.BBB>() {}, TypeParameterMatcherTest.TypeX.class, "B");
        Assert.assertFalse(m.match(new Object()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.A()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.AA()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.AAA()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.B()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.BB()));
        Assert.assertTrue(m.match(new TypeParameterMatcherTest.BBB()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.C()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.CC()));
    }

    @Test
    public void testAbstractClass() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.TypeQ(), TypeParameterMatcherTest.TypeX.class, "C");
        Assert.assertFalse(m.match(new Object()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.A()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.AA()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.AAA()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.B()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.BB()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.BBB()));
        Assert.assertFalse(m.match(new TypeParameterMatcherTest.C()));
        Assert.assertTrue(m.match(new TypeParameterMatcherTest.CC()));
    }

    public static class TypeX<A, B, C> {
        A a;

        B b;

        C c;
    }

    public static class TypeY<D extends TypeParameterMatcherTest.C, E extends TypeParameterMatcherTest.A, F extends TypeParameterMatcherTest.B> extends TypeParameterMatcherTest.TypeX<E, F, D> {}

    public abstract static class TypeZ<G extends TypeParameterMatcherTest.AA, H extends TypeParameterMatcherTest.BB> extends TypeParameterMatcherTest.TypeY<TypeParameterMatcherTest.CC, G, H> {}

    public static class TypeQ<I extends TypeParameterMatcherTest.BBB> extends TypeParameterMatcherTest.TypeZ<TypeParameterMatcherTest.AAA, I> {}

    public static class A {}

    public static class AA extends TypeParameterMatcherTest.A {}

    public static class AAA extends TypeParameterMatcherTest.AA {}

    public static class B {}

    public static class BB extends TypeParameterMatcherTest.B {}

    public static class BBB extends TypeParameterMatcherTest.BB {}

    public static class C {}

    public static class CC extends TypeParameterMatcherTest.C {}

    @Test
    public void testInaccessibleClass() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.U<TypeParameterMatcherTest.T>() {}, TypeParameterMatcherTest.U.class, "E");
        Assert.assertFalse(m.match(new Object()));
        Assert.assertTrue(m.match(new TypeParameterMatcherTest.T()));
    }

    private static class T {}

    private static class U<E> {
        E a;
    }

    @Test
    public void testArrayAsTypeParam() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.U<byte[]>() {}, TypeParameterMatcherTest.U.class, "E");
        Assert.assertFalse(m.match(new Object()));
        Assert.assertTrue(m.match(new byte[1]));
    }

    @Test
    public void testRawType() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.U() {}, TypeParameterMatcherTest.U.class, "E");
        Assert.assertTrue(m.match(new Object()));
    }

    private static class V<E> {
        TypeParameterMatcherTest.U<E> u = new TypeParameterMatcherTest.U<E>() {};
    }

    @Test
    public void testInnerClass() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.V<String>().u, TypeParameterMatcherTest.U.class, "E");
        Assert.assertTrue(m.match(new Object()));
    }

    private abstract static class W<E> {
        E e;
    }

    private static class X<T, E> extends TypeParameterMatcherTest.W<E> {
        T t;
    }

    @Test(expected = IllegalStateException.class)
    public void testErasure() throws Exception {
        TypeParameterMatcher m = TypeParameterMatcher.find(new TypeParameterMatcherTest.X<String, Date>(), TypeParameterMatcherTest.W.class, "E");
        Assert.assertTrue(m.match(new Date()));
        Assert.assertFalse(m.match(new Object()));
    }
}

