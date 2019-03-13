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
package org.opengrok.indexer.util;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.analysis.StreamSource;


/**
 * Represents a container for tests of {@link LineBreaker}.
 */
public class LineBreakerTest {
    private static LineBreaker brkr;

    @Test
    public void shouldSplitEmptyStringIntoOneLine() throws IOException {
        StreamSource src = StreamSource.fromString("");
        LineBreakerTest.brkr.reset(src);
        Assert.assertEquals("split count", 1, LineBreakerTest.brkr.count());
        Assert.assertEquals("split position", 0, LineBreakerTest.brkr.getPosition(0));
    }

    @Test
    public void shouldSplitEndingLFsIntoOneMoreLine() throws IOException {
        StreamSource src = StreamSource.fromString("abc\ndef\n");
        LineBreakerTest.brkr.reset(src);
        Assert.assertEquals("split count", 3, LineBreakerTest.brkr.count());
        Assert.assertEquals("split position", 0, LineBreakerTest.brkr.getPosition(0));
        Assert.assertEquals("split position", 4, LineBreakerTest.brkr.getPosition(1));
        Assert.assertEquals("split position", 8, LineBreakerTest.brkr.getPosition(2));
    }

    @Test
    public void shouldSplitDocsWithNoLastLF() throws IOException {
        StreamSource src = StreamSource.fromString("abc\r\ndef");
        LineBreakerTest.brkr.reset(src);
        Assert.assertEquals("split count", 2, LineBreakerTest.brkr.count());
        Assert.assertEquals("split position", 0, LineBreakerTest.brkr.getPosition(0));
        Assert.assertEquals("split position", 5, LineBreakerTest.brkr.getPosition(1));
    }

    @Test
    public void shouldHandleDocsOfLongerLength() throws IOException {
        // 0             0
        // 0-- -  5-- - -1--- - 5--- - 2-
        final String INPUT = "ab\r\ncde\r\nefgh\r\nijk\r\nlm";
        StreamSource src = StreamSource.fromString(INPUT);
        LineBreakerTest.brkr.reset(src);
        Assert.assertEquals("split count", 5, LineBreakerTest.brkr.count());
        Assert.assertEquals("split position", 0, LineBreakerTest.brkr.getPosition(0));
        Assert.assertEquals("split position", 4, LineBreakerTest.brkr.getPosition(1));
        Assert.assertEquals("split position", 9, LineBreakerTest.brkr.getPosition(2));
        Assert.assertEquals("split position", 15, LineBreakerTest.brkr.getPosition(3));
        Assert.assertEquals("split position", 20, LineBreakerTest.brkr.getPosition(4));
    }
}

