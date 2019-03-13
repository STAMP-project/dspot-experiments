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
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.history;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author austvik
 */
public class AnnotationTest {
    public AnnotationTest() {
    }

    /**
     * Test of getRevision method, of class Annotation.
     */
    @Test
    public void getRevision() {
        Annotation instance = new Annotation("testfile.tst");
        Assert.assertEquals(instance.getRevision(1), "");
        instance.addLine("1.0", "Author", true);
        Assert.assertEquals(instance.getRevision(1), "1.0");
        instance.addLine("1.1.0", "Author 2", false);
        Assert.assertEquals(instance.getRevision(2), "1.1.0");
    }

    /**
     * Test of getAuthor method, of class Annotation.
     */
    @Test
    public void getAuthor() {
        Annotation instance = new Annotation("testfile.tst");
        Assert.assertEquals(instance.getAuthor(1), "");
        instance.addLine("1.0", "Author", true);
        Assert.assertEquals(instance.getAuthor(1), "Author");
        instance.addLine("1.1.0", "Author 2", false);
        Assert.assertEquals(instance.getAuthor(2), "Author 2");
    }

    /**
     * Test of isEnabled method, of class Annotation.
     */
    @Test
    public void isEnabled() {
        Annotation instance = new Annotation("testfile.tst");
        Assert.assertEquals(instance.isEnabled(1), false);
        instance.addLine("1.0", "Author", true);
        Assert.assertEquals(instance.isEnabled(1), true);
        instance.addLine("1.1.0", "Author 2", false);
        Assert.assertEquals(instance.isEnabled(2), false);
    }

    /**
     * Test of size method, of class Annotation.
     */
    @Test
    public void size() {
        Annotation instance = new Annotation("testfile.tst");
        Assert.assertEquals(instance.size(), 0);
        instance.addLine("1.0", "Author", true);
        Assert.assertEquals(instance.size(), 1);
        instance.addLine("1.1", "Author 2", true);
        Assert.assertEquals(instance.size(), 2);
    }

    /**
     * Test of getWidestRevision method, of class Annotation.
     */
    @Test
    public void getWidestRevision() {
        Annotation instance = new Annotation("testfile.tst");
        Assert.assertEquals(instance.getWidestRevision(), 0);
        instance.addLine("1.0", "Author", true);
        Assert.assertEquals(instance.getWidestRevision(), 3);
        instance.addLine("1.1.0", "Author 2", true);
        Assert.assertEquals(instance.getWidestRevision(), 5);
    }

    /**
     * Test of getWidestAuthor method, of class Annotation.
     */
    @Test
    public void getWidestAuthor() {
        Annotation instance = new Annotation("testfile.tst");
        Assert.assertEquals(instance.getWidestAuthor(), 0);
        instance.addLine("1.0", "Author", true);
        Assert.assertEquals(instance.getWidestAuthor(), 6);
        instance.addLine("1.1.0", "Author 2", false);
        Assert.assertEquals(instance.getWidestAuthor(), 8);
    }

    /**
     * Test of addLine method, of class Annotation.
     */
    @Test
    public void addLine() {
        Annotation instance = new Annotation("testfile.tst");
        instance.addLine("1.0", "Author", true);
        Assert.assertEquals(instance.size(), 1);
        instance.addLine(null, null, true);
    }

    /**
     * Test of getFilename method, of class Annotation.
     */
    @Test
    public void getFilename() {
        Annotation instance = new Annotation("testfile.tst");
        Assert.assertEquals("testfile.tst", instance.getFilename());
    }
}

