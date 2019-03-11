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
package org.eclipse.jetty.util;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AtomicBiIntegerTest {
    @Test
    public void testBitOperations() {
        long encoded;
        encoded = AtomicBiInteger.encode(0, 0);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(0));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(0));
        encoded = AtomicBiInteger.encode(1, 2);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(1));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(2));
        encoded = AtomicBiInteger.encode(Integer.MAX_VALUE, (-1));
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(Integer.MAX_VALUE));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is((-1)));
        encoded = AtomicBiInteger.encodeLo(encoded, 42);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(Integer.MAX_VALUE));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(42));
        encoded = AtomicBiInteger.encode((-1), Integer.MAX_VALUE);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is((-1)));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(Integer.MAX_VALUE));
        encoded = AtomicBiInteger.encodeHi(encoded, 42);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(42));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(Integer.MAX_VALUE));
        encoded = AtomicBiInteger.encode(Integer.MIN_VALUE, 1);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(Integer.MIN_VALUE));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(1));
        encoded = AtomicBiInteger.encodeLo(encoded, Integer.MAX_VALUE);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(Integer.MIN_VALUE));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(Integer.MAX_VALUE));
        encoded = AtomicBiInteger.encode(1, Integer.MIN_VALUE);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(1));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(Integer.MIN_VALUE));
        encoded = AtomicBiInteger.encodeHi(encoded, Integer.MAX_VALUE);
        MatcherAssert.assertThat(AtomicBiInteger.getHi(encoded), Matchers.is(Integer.MAX_VALUE));
        MatcherAssert.assertThat(AtomicBiInteger.getLo(encoded), Matchers.is(Integer.MIN_VALUE));
    }

    @Test
    public void testSet() {
        AtomicBiInteger abi = new AtomicBiInteger();
        MatcherAssert.assertThat(abi.getHi(), Matchers.is(0));
        MatcherAssert.assertThat(abi.getLo(), Matchers.is(0));
        abi.getAndSetHi(Integer.MAX_VALUE);
        MatcherAssert.assertThat(abi.getHi(), Matchers.is(Integer.MAX_VALUE));
        MatcherAssert.assertThat(abi.getLo(), Matchers.is(0));
        abi.getAndSetLo(Integer.MIN_VALUE);
        MatcherAssert.assertThat(abi.getHi(), Matchers.is(Integer.MAX_VALUE));
        MatcherAssert.assertThat(abi.getLo(), Matchers.is(Integer.MIN_VALUE));
    }

    @Test
    public void testCompareAndSet() {
        AtomicBiInteger abi = new AtomicBiInteger();
        MatcherAssert.assertThat(abi.getHi(), Matchers.is(0));
        MatcherAssert.assertThat(abi.getLo(), Matchers.is(0));
        Assertions.assertFalse(abi.compareAndSetHi(1, 42));
        Assertions.assertTrue(abi.compareAndSetHi(0, 42));
        MatcherAssert.assertThat(abi.getHi(), Matchers.is(42));
        MatcherAssert.assertThat(abi.getLo(), Matchers.is(0));
        Assertions.assertFalse(abi.compareAndSetLo(1, (-42)));
        Assertions.assertTrue(abi.compareAndSetLo(0, (-42)));
        MatcherAssert.assertThat(abi.getHi(), Matchers.is(42));
        MatcherAssert.assertThat(abi.getLo(), Matchers.is((-42)));
    }
}

