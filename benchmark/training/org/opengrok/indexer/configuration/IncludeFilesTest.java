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
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.configuration;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;

import static Configuration.BODY_INCLUDE_FILE;
import static Configuration.E_FORBIDDEN_INCLUDE_FILE;
import static Configuration.FOOTER_INCLUDE_FILE;
import static Configuration.HEADER_INCLUDE_FILE;


/**
 * Test include file functionality for web application.
 *
 * @author Vladimir Kotal
 */
public class IncludeFilesTest {
    static Path includeRoot;

    static final String CONTENT_1 = "foo";

    static final String CONTENT_2 = "bar";

    static RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    static final String LINE_SEP = System.lineSeparator();

    @Test
    public void testGetHeaderIncludeFileContent() throws IOException {
        File file = new File(IncludeFilesTest.includeRoot.toFile(), HEADER_INCLUDE_FILE);
        writeStringToFile(file, IncludeFilesTest.CONTENT_1);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_1) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getHeaderIncludeFileContent(false));
        writeStringToFile(file, IncludeFilesTest.CONTENT_2);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_2) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getHeaderIncludeFileContent(true));
    }

    @Test
    public void testGetBodyIncludeFileContent() throws IOException {
        File file = new File(IncludeFilesTest.includeRoot.toFile(), BODY_INCLUDE_FILE);
        writeStringToFile(file, IncludeFilesTest.CONTENT_1);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_1) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getBodyIncludeFileContent(false));
        writeStringToFile(file, IncludeFilesTest.CONTENT_2);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_2) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getBodyIncludeFileContent(true));
    }

    @Test
    public void testGetFooterIncludeFileContent() throws IOException {
        File file = new File(IncludeFilesTest.includeRoot.toFile(), FOOTER_INCLUDE_FILE);
        writeStringToFile(file, IncludeFilesTest.CONTENT_1);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_1) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getFooterIncludeFileContent(false));
        writeStringToFile(file, IncludeFilesTest.CONTENT_2);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_2) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getFooterIncludeFileContent(true));
    }

    @Test
    public void testGetForbiddenIncludeFileContent() throws IOException {
        File file = new File(IncludeFilesTest.includeRoot.toFile(), E_FORBIDDEN_INCLUDE_FILE);
        writeStringToFile(file, IncludeFilesTest.CONTENT_1);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_1) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getForbiddenIncludeFileContent(false));
        writeStringToFile(file, IncludeFilesTest.CONTENT_2);
        Assert.assertEquals(((IncludeFilesTest.CONTENT_2) + (IncludeFilesTest.LINE_SEP)), IncludeFilesTest.env.includeFiles.getForbiddenIncludeFileContent(true));
    }
}

