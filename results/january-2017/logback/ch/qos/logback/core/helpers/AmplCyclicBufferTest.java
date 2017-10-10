/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */


package ch.qos.logback.core.helpers;


public class AmplCyclicBufferTest {
    void assertSize(ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> cb, int size) {
        org.junit.Assert.assertEquals(size, cb.length());
    }

    @org.junit.Test
    public void smoke() {
        ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> cb = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(2);
        assertSize(cb, 0);
        cb.add("zero");
        assertSize(cb, 1);
        cb.add("one");
        assertSize(cb, 2);
        cb.add("two");
        assertSize(cb, 2);
        org.junit.Assert.assertEquals("one", cb.get());
        assertSize(cb, 1);
        org.junit.Assert.assertEquals("two", cb.get());
        assertSize(cb, 0);
    }

    @org.junit.Test
    public void cloning() {
        ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> cb = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(2);
        cb.add("zero");
        cb.add("one");
        ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> clone = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(cb);
        assertSize(clone, 2);
        cb.clear();
        assertSize(cb, 0);
        java.util.List<java.lang.String> witness = java.util.Arrays.asList("zero", "one");
        org.junit.Assert.assertEquals(witness, clone.asList());
    }

    /* amplification of ch.qos.logback.core.helpers.CyclicBufferTest#cloning */
    @org.junit.Test(timeout = 10000)
    public void cloning_cf39_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> cb = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(2);
            cb.add("zero");
            cb.add("one");
            ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> clone = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(cb);
            assertSize(clone, 2);
            cb.clear();
            assertSize(cb, 0);
            java.util.List<java.lang.String> witness = java.util.Arrays.asList("zero", "one");
            // StatementAdderOnAssert create random local variable
            int vc_19 = -705254390;
            // StatementAdderMethod cloned existing statement
            cb.resize(vc_19);
            // MethodAssertGenerator build local variable
            Object o_16_0 = clone.asList();
            org.junit.Assert.fail("cloning_cf39 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.helpers.CyclicBufferTest#cloning */
    @org.junit.Test(timeout = 10000)
    public void cloning_cf24_failAssert8_literalMutation100_literalMutation425_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> cb = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(0);
                cb.add("zero");
                cb.add("one");
                ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> clone = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(cb);
                assertSize(clone, 2);
                cb.clear();
                assertSize(cb, 0);
                java.util.List<java.lang.String> witness = java.util.Arrays.asList("zero", "one");
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.helpers.CyclicBuffer vc_9 = (ch.qos.logback.core.helpers.CyclicBuffer)null;
                // StatementAdderMethod cloned existing statement
                vc_9.asList();
                // MethodAssertGenerator build local variable
                Object o_16_0 = clone.asList();
                org.junit.Assert.fail("cloning_cf24 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cloning_cf24_failAssert8_literalMutation100_literalMutation425 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.helpers.CyclicBufferTest#smoke */
    @org.junit.Test(timeout = 10000)
    public void smoke_cf773_failAssert24_literalMutation887_literalMutation1398_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String> cb = new ch.qos.logback.core.helpers.CyclicBuffer<java.lang.String>(0);
                assertSize(cb, 0);
                cb.add("zero");
                assertSize(cb, 1);
                cb.add("one");
                assertSize(cb, 2);
                cb.add("two");
                assertSize(cb, 2);
                // MethodAssertGenerator build local variable
                Object o_10_0 = cb.get();
                assertSize(cb, 1);
                // StatementAdderOnAssert create literal from method
                int int_vc_3 = 2;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.helpers.CyclicBuffer vc_37 = (ch.qos.logback.core.helpers.CyclicBuffer)null;
                // StatementAdderMethod cloned existing statement
                vc_37.resize(int_vc_3);
                // MethodAssertGenerator build local variable
                Object o_19_0 = cb.get();
                assertSize(cb, 0);
                org.junit.Assert.fail("smoke_cf773 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("smoke_cf773_failAssert24_literalMutation887_literalMutation1398 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

