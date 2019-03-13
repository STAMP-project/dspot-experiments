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
package org.opengrok.indexer.analysis.archive;


import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.analysis.AnalyzerFactory;
import org.opengrok.indexer.analysis.AnalyzerGuru;


/**
 * Represents a container for tests of {@link ZipAnalyzerFactory}.
 */
public class ZipAnalyzerFactoryTest {
    /**
     * Tests a ZIP file.
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test
    public void testZipWrtAnalyzerGuru() throws IOException {
        InputStream res = getClass().getClassLoader().getResourceAsStream("analysis/archive/zip.bin");
        Assert.assertNotNull("zip.bin should be available,", res);
        // assert that it is matched
        AnalyzerFactory fac = AnalyzerGuru.find(res);
        Assert.assertNotNull("zip.bin should have factory", fac);
        Assert.assertSame("should be ZipAnalyzerFactory", fac.getClass(), ZipAnalyzerFactory.class);
    }
}

