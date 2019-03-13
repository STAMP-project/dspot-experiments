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


import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the SCCSget class
 *
 * @author Trond Norbye
 */
public class SCCSgetTest {
    private static boolean haveSccs = true;

    private File sccsfile;

    private File sccsdir;

    public SCCSgetTest() {
    }

    /**
     * Test of getRevision method, of class SCCSget.
     */
    @Test
    public void getRevision() throws Exception {
        if (!(SCCSgetTest.haveSccs)) {
            System.out.println("sccs not available. Skipping test");
            return;
        }
        ZipInputStream zstream = new ZipInputStream(getClass().getResourceAsStream("/history/sccs-revisions.zip"));
        ZipEntry entry;
        while ((entry = zstream.getNextEntry()) != null) {
            String expected = readInput(zstream);
            InputStream sccs = SCCSget.getRevision("sccs", sccsfile, entry.getName());
            String got = readInput(sccs);
            sccs.close();
            zstream.closeEntry();
            Assert.assertEquals(expected, got);
        } 
        zstream.close();
    }
}

