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
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.search;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * Do basic sanity testing of the Hit class
 *
 * @author Trond Norbye
 */
public class HitTest {
    @Test
    public void testFilename() {
        Hit instance = new Hit();
        Assert.assertNull(instance.getFilename());
        String expResult = "foobar";
        instance.setFilename(expResult);
        Assert.assertEquals(expResult, instance.getFilename());
    }

    @Test
    public void testPath() {
        Hit instance = new Hit("/foo/bar", null, null, false, false);
        Assert.assertEquals("/foo/bar", instance.getPath());
        Assert.assertEquals(((File.separator) + "foo"), instance.getDirectory());
    }

    @Test
    public void testLine() {
        Hit instance = new Hit();
        Assert.assertNull(instance.getLine());
        String expResult = "This is a line of text";
        instance.setLine(expResult);
        Assert.assertEquals(expResult, instance.getLine());
    }

    @Test
    public void testLineno() {
        Hit instance = new Hit();
        Assert.assertNull(instance.getLineno());
        String expResult = "12";
        instance.setLineno(expResult);
        Assert.assertEquals(expResult, instance.getLineno());
    }

    @Test
    public void testCompareTo() {
        Hit o1 = new Hit("/foo", null, null, false, false);
        Hit o2 = new Hit("/foo", "hi", "there", false, false);
        Assert.assertEquals(o2.compareTo(o1), o1.compareTo(o2));
        o1.setFilename("bar");
        Assert.assertFalse(((o2.compareTo(o1)) == (o1.compareTo(o2))));
    }

    @Test
    public void testBinary() {
        Hit instance = new Hit();
        Assert.assertFalse(instance.isBinary());
        instance.setBinary(true);
        Assert.assertTrue(instance.isBinary());
    }

    @Test
    public void testTag() {
        Hit instance = new Hit();
        Assert.assertNull(instance.getTag());
        String expResult = "foobar";
        instance.setTag(expResult);
        Assert.assertEquals(expResult, instance.getTag());
    }

    @Test
    public void testAlt() {
        Hit instance = new Hit();
        Assert.assertFalse(instance.getAlt());
        Hit o2 = new Hit(null, null, null, false, true);
        Assert.assertTrue(o2.getAlt());
    }

    @Test
    public void testEquals() {
        Hit o1 = new Hit("/foo", null, null, false, false);
        Hit o2 = new Hit("/foo", "hi", "there", false, false);
        Assert.assertEquals(o2.equals(o1), o1.equals(o2));
        o1.setFilename("bar");
        Assert.assertFalse(o2.equals(o1));
        Assert.assertFalse(o1.equals(o2));
        Assert.assertFalse(o1.equals(new Object()));
    }

    @Test
    public void testHashCode() {
        String filename = "bar";
        Hit instance = new Hit(filename, null, null, false, false);
        Assert.assertEquals(filename.hashCode(), instance.hashCode());
    }
}

