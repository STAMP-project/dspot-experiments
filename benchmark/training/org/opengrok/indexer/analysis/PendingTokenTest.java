/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis;


import org.junit.Assert;
import org.junit.Test;


/**
 * Represents a container for tests of {@link PendingToken}.
 */
public class PendingTokenTest {
    @Test
    public void testEquals1() {
        PendingToken instance = new PendingToken("", 0, 0);
        boolean result = instance.equals(instance);
        Assert.assertTrue("PendingToken instance equals itself", result);
    }

    @Test
    public void testEquals2() {
        PendingToken instance1 = new PendingToken("a", 0, 1);
        Assert.assertEquals("PendingToken default nonpos", false, instance1.nonpos);
        PendingToken instance2 = new PendingToken("a", 0, 1);
        instance2.nonpos = true;
        boolean result = instance1.equals(instance2);
        Assert.assertTrue("PendingToken instance equivalence ignores nonpos", result);
    }

    @Test
    public void testNotEquals1() {
        PendingToken instance1 = new PendingToken("", 0, 0);
        PendingToken instance2 = new PendingToken("", 0, 1);// nonsense but ok

        boolean result = instance1.equals(instance2);
        Assert.assertFalse("PendingToken equals() only 2 immutables match", result);
    }

    @Test
    public void testNotEquals2() {
        PendingToken instance1 = new PendingToken("", 0, 0);
        PendingToken instance2 = new PendingToken("", 1, 0);// nonsense but ok

        boolean result = instance1.equals(instance2);
        Assert.assertFalse("PendingToken equals() only 2 immutables match", result);
    }

    @Test
    public void testNotEquals3() {
        PendingToken instance1 = new PendingToken("", 0, 0);
        PendingToken instance2 = new PendingToken("a", 0, 0);// nonsense but ok

        boolean result = instance1.equals(instance2);
        Assert.assertFalse("PendingToken equals() only 2 immutables match", result);
    }

    @Test
    public void testSameHashCodes() {
        PendingToken instance1 = new PendingToken("a", 0, 1);
        Assert.assertEquals("PendingToken default nonpos", false, instance1.nonpos);
        PendingToken instance2 = new PendingToken("a", 0, 1);
        instance2.nonpos = true;
        Assert.assertEquals("PendingToken instance HashCode ignores nonpos", instance1.hashCode(), instance2.hashCode());
    }

    @Test
    public void testDifferentHashCodes1() {
        PendingToken instance1 = new PendingToken("", 0, 0);
        PendingToken instance2 = new PendingToken("", 0, 1);// nonsense but ok

        Assert.assertNotEquals("PendingToken hashCode() only 2 immutables match", instance1.hashCode(), instance2.hashCode());
    }

    @Test
    public void testDifferentHashCodes2() {
        PendingToken instance1 = new PendingToken("", 0, 0);
        PendingToken instance2 = new PendingToken("", 1, 0);// nonsense but ok

        Assert.assertNotEquals("PendingToken hashCode() only 2 immutables match", instance1.hashCode(), instance2.hashCode());
    }

    @Test
    public void testDifferentHashCodes3() {
        PendingToken instance1 = new PendingToken("", 0, 0);
        PendingToken instance2 = new PendingToken("a", 0, 0);// nonsense but ok

        Assert.assertNotEquals("PendingToken hashCode() only 2 immutables match", instance1.hashCode(), instance2.hashCode());
    }

    @Test
    public void testToString() {
        PendingToken instance = new PendingToken("abc", 0, 4);
        String expResult = "PendingToken{abc<<< start=0,end=4,nonpos=false}";
        String result = instance.toString();
        Assert.assertEquals("PendingToken toString()", expResult, result);
        instance.nonpos = true;
        expResult = "PendingToken{abc<<< start=0,end=4,nonpos=true}";
        result = instance.toString();
        Assert.assertEquals("PendingToken toString()", expResult, result);
    }
}

