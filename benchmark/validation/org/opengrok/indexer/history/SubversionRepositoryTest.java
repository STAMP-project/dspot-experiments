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
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.history;


import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;


public class SubversionRepositoryTest {
    @Test
    public void testDateFormats() {
        String[][] tests = new String[][]{ new String[]{ "abcd", "expected exception" }, new String[]{ "2016-01-01 10:00:00", "expected exception" }, new String[]{ "2016 Sat, 1 Apr 2008 15:12:51 +0000", "expected exception" }, new String[]{ "Sat, 1 Dub 2008 15:12:51 +0000", "expected exception" }, new String[]{ "2016_01_01T10:00:00Z", "expected exception" }, new String[]{ "2016-01-01T10:00:00T", "expected exception" }, new String[]{ "2016-01-01T10:00:00.200", "expected exception" }, new String[]{ "2016-01-01 10:00:00Z", "expected exception" }, new String[]{ "2016-01-01T40:00:00Z", null }// lenient - wrong hour
        // lenient - wrong hour
        // lenient - wrong hour
        , new String[]{ "2016-01-01T00:70:00Z", null }// lenient - wrong minute
        // lenient - wrong minute
        // lenient - wrong minute
        , new String[]{ "2016-01-01T00:00:99Z", null }// lenient - wrong second
        // lenient - wrong second
        // lenient - wrong second
        , new String[]{ "2016-03-40T00:00:00Z", null }// lenient - wrong day
        // lenient - wrong day
        // lenient - wrong day
        , new String[]{ "2016-01-01T10:00:00.200Z", null }, new String[]{ "2016-01-01T11:00:00.200Z", null }, new String[]{ "2016-01-01T10:00:00.Z", null }, new String[]{ "2017-01-01T10:00:00.Z", null }, new String[]{ "2016-01-01T10:00:00Z", null } };
        final SubversionRepository repository = new SubversionRepository();
        for (String[] test : tests) {
            try {
                repository.parse(test[0]);
                if ((test[1]) != null) {
                    Assert.fail(("Shouldn't be able to parse the date: " + (test[0])));
                }
            } catch (ParseException ex) {
                if ((test[1]) == null) {
                    // no exception
                    Assert.fail(("Shouldn't throw a parsing exception for date: " + (test[0])));
                }
            }
        }
    }
}

