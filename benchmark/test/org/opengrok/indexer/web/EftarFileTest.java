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
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.web;


import java.io.File;
import java.io.IOException;
import org.junit.Test;


/**
 * JUnit test to test the EftarFile-system
 */
public class EftarFileTest {
    private static File tsv;

    private static File eftar;

    public EftarFileTest() {
    }

    private static final String PATH_STRING = "/path";

    /**
     * Test usage of an EftarFile
     *
     * @throws IOException
     * 		if an error occurs while accessing the eftar file
     */
    @Test
    public void searchEftarFile() throws IOException {
        searchEftarFile(new EftarFileReader(EftarFileTest.eftar));
        searchEftarFile(new EftarFileReader(EftarFileTest.eftar.getAbsolutePath()));
    }
}

