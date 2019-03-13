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
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.util;


import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for org.opengrok.indexer.util.Getopt
 */
public class GetoptTest {
    public GetoptTest() {
    }

    @Test
    public void testParseNormal() throws Exception {
        String[] argv = new String[]{ "-a", "foo", "-bc", "--", "-f" };
        Getopt instance = new Getopt(argv, "a:bcr:f");
        instance.parse();
        Assert.assertEquals('a', ((char) (instance.getOpt())));
        Assert.assertEquals("foo", instance.getOptarg());
        Assert.assertEquals('b', ((char) (instance.getOpt())));
        Assert.assertNull(instance.getOptarg());
        Assert.assertEquals('c', ((char) (instance.getOpt())));
        Assert.assertNull(instance.getOptarg());
        Assert.assertEquals((-1), instance.getOpt());
        Assert.assertEquals(4, instance.getOptind());
        Assert.assertTrue(((instance.getOptind()) < (argv.length)));
        Assert.assertEquals("-f", argv[instance.getOptind()]);
    }

    @Test
    public void reset() throws ParseException {
        String[] argv = new String[]{ "-a", "foo", "-bc", "argument1" };
        Getopt instance = new Getopt(argv, "a:bc");
        instance.parse();
        Assert.assertEquals('a', ((char) (instance.getOpt())));
        Assert.assertEquals("foo", instance.getOptarg());
        Assert.assertEquals('b', ((char) (instance.getOpt())));
        Assert.assertNull(instance.getOptarg());
        Assert.assertEquals('c', ((char) (instance.getOpt())));
        Assert.assertNull(instance.getOptarg());
        Assert.assertEquals((-1), instance.getOpt());
        Assert.assertEquals(3, instance.getOptind());
        Assert.assertTrue(((instance.getOptind()) < (argv.length)));
        Assert.assertEquals("argument1", argv[instance.getOptind()]);
        instance.reset();
        Assert.assertEquals('a', ((char) (instance.getOpt())));
        Assert.assertEquals("foo", instance.getOptarg());
        Assert.assertEquals('b', ((char) (instance.getOpt())));
        Assert.assertNull(instance.getOptarg());
        Assert.assertEquals('c', ((char) (instance.getOpt())));
        Assert.assertNull(instance.getOptarg());
        Assert.assertEquals((-1), instance.getOpt());
        Assert.assertEquals(3, instance.getOptind());
        Assert.assertTrue(((instance.getOptind()) < (argv.length)));
        Assert.assertEquals("argument1", argv[instance.getOptind()]);
    }/* Test of reset method, of class Getopt. */


    @Test
    public void testParseFailure() throws Exception {
        String[] argv = new String[]{ "-a" };
        Getopt instance = new Getopt(argv, "a:");
        try {
            instance.parse();
            Assert.fail("Parse shall not allow missing arguments");
        } catch (ParseException exp) {
            if (!(exp.getMessage().contains("requires an argument"))) {
                // not the exception we expected
                throw exp;
            }
        }
        instance = new Getopt(argv, "b");
        try {
            instance.parse();
            Assert.fail("Parse shall not allow unknown arguments");
        } catch (ParseException exp) {
            if (!(exp.getMessage().contains("Unknown argument: "))) {
                // not the exception we expected
                throw exp;
            }
        }
    }
}

