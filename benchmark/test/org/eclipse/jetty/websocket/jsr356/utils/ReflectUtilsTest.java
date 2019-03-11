/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.jsr356.utils;


import org.eclipse.jetty.websocket.common.util.ReflectUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ReflectUtilsTest {
    public static interface Fruit<T> {}

    public static interface Color<T> {}

    public static interface Food<T> extends ReflectUtilsTest.Fruit<T> {}

    public abstract static class Apple<T extends Object> implements ReflectUtilsTest.Color<String> , ReflectUtilsTest.Fruit<T> {}

    public abstract static class Cherry<A extends Object, B extends Number> implements ReflectUtilsTest.Color<B> , ReflectUtilsTest.Fruit<A> {}

    public abstract static class Banana implements ReflectUtilsTest.Color<String> , ReflectUtilsTest.Fruit<String> {}

    public static class Washington<Z extends Number, X extends Object> extends ReflectUtilsTest.Cherry<X, Z> {}

    public static class Rainier extends ReflectUtilsTest.Washington<Float, Short> {}

    public static class Pizza implements ReflectUtilsTest.Food<Integer> {}

    public static class Cavendish extends ReflectUtilsTest.Banana {}

    public static class GrannySmith extends ReflectUtilsTest.Apple<Long> {}

    public static class Pear implements ReflectUtilsTest.Color<Double> , ReflectUtilsTest.Fruit<String> {}

    public static class Kiwi implements ReflectUtilsTest.Fruit<Character> {}

    @Test
    public void testFindGeneric_PearFruit() {
        assertFindGenericClass(ReflectUtilsTest.Pear.class, ReflectUtilsTest.Fruit.class, String.class);
    }

    @Test
    public void testFindGeneric_PizzaFruit() {
        assertFindGenericClass(ReflectUtilsTest.Pizza.class, ReflectUtilsTest.Fruit.class, Integer.class);
    }

    @Test
    public void testFindGeneric_KiwiFruit() {
        assertFindGenericClass(ReflectUtilsTest.Kiwi.class, ReflectUtilsTest.Fruit.class, Character.class);
    }

    @Test
    public void testFindGeneric_PearColor() {
        assertFindGenericClass(ReflectUtilsTest.Pear.class, ReflectUtilsTest.Color.class, Double.class);
    }

    @Test
    public void testFindGeneric_GrannySmithFruit() {
        assertFindGenericClass(ReflectUtilsTest.GrannySmith.class, ReflectUtilsTest.Fruit.class, Long.class);
    }

    @Test
    public void testFindGeneric_CavendishFruit() {
        assertFindGenericClass(ReflectUtilsTest.Cavendish.class, ReflectUtilsTest.Fruit.class, String.class);
    }

    @Test
    public void testFindGeneric_RainierFruit() {
        assertFindGenericClass(ReflectUtilsTest.Rainier.class, ReflectUtilsTest.Fruit.class, Short.class);
    }

    @Test
    public void testFindGeneric_WashingtonFruit() {
        // Washington does not have a concrete implementation
        // of the Fruit interface, this should return null
        Class<?> impl = ReflectUtils.findGenericClassFor(ReflectUtilsTest.Washington.class, ReflectUtilsTest.Fruit.class);
        MatcherAssert.assertThat("Washington -> Fruit implementation", impl, Matchers.nullValue());
    }
}

