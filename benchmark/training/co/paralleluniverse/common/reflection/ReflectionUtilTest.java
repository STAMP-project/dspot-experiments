/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.common.reflection;


import co.paralleluniverse.common.test.TestUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author pron
 */
public class ReflectionUtilTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    static class A<T> {}

    static class B<X, T> extends ReflectionUtilTest.A<T> {}

    static class C extends ReflectionUtilTest.B<Integer, String> {}

    static class D extends ReflectionUtilTest.A<String> {}

    static class E extends ReflectionUtilTest.A<String> {}

    static interface IA<T> {}

    static interface IB<X, T> extends ReflectionUtilTest.IA<T> {}

    static interface IC extends ReflectionUtilTest.IB<Integer, String> {}

    static interface ID extends ReflectionUtilTest.IA<String> {}

    static interface IE extends ReflectionUtilTest.IA<String> {}

    @Test
    public void testGetGenericParameter1() {
        Class<?> res = ((Class<?>) (ReflectionUtil.getGenericParameterType(ReflectionUtilTest.D.class, ReflectionUtilTest.A.class, 0)));
        Assert.assertEquals(res, String.class);
    }

    @Test
    public void testGetGenericParameter2() {
        Class<?> res = ((Class<?>) (ReflectionUtil.getGenericParameterType(new ReflectionUtilTest.A<String>() {}.getClass(), ReflectionUtilTest.A.class, 0)));
        Assert.assertEquals(res, String.class);
    }

    @Test
    public void testGetGenericParameter3() {
        Class<?> res = ((Class<?>) (ReflectionUtil.getGenericParameterType(ReflectionUtilTest.E.class, ReflectionUtilTest.A.class, 0)));
        Assert.assertEquals(res, String.class);
    }

    @Test
    public void testGetGenericParameterInterface1() {
        Class<?> res = ((Class<?>) (ReflectionUtil.getGenericParameterType(ReflectionUtilTest.ID.class, ReflectionUtilTest.IA.class, 0)));
        Assert.assertEquals(res, String.class);
    }

    @Test
    public void testGetGenericParameterInterface2() {
        Class<?> res = ((Class<?>) (ReflectionUtil.getGenericParameterType(new ReflectionUtilTest.IA<String>() {}.getClass(), ReflectionUtilTest.IA.class, 0)));
        Assert.assertEquals(res, String.class);
    }

    @Test
    public void testGetGenericParameterInterface3() {
        Class<?> res = ((Class<?>) (ReflectionUtil.getGenericParameterType(ReflectionUtilTest.IE.class, ReflectionUtilTest.IA.class, 0)));
        Assert.assertEquals(res, String.class);
    }
}

