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
import java.util.function.IntPredicate;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.analysis.StreamSource;


/**
 * Represents a container for tests of {@link SourceSplitter}.
 */
public class SourceSplitterTest {
    @Test
    public void shouldSplitEmptyStringIntoOneLine() {
        SourceSplitter splitter = new SourceSplitter();
        splitter.reset("");
        Assert.assertEquals("split count", 1, splitter.count());
        Assert.assertEquals("split position", 0, splitter.getPosition(0));
        Assert.assertEquals("split position", 0, splitter.getPosition(1));
        Assert.assertEquals("split find-offset", 0, splitter.findLineOffset(0));
        Assert.assertEquals("split find-offset", (-1), splitter.findLineOffset(1));
    }

    @Test
    public void shouldSplitEndingLFsIntoOneMoreLine() {
        SourceSplitter splitter = new SourceSplitter();
        splitter.reset("abc\ndef\n");
        Assert.assertEquals("split count", 3, splitter.count());
        Assert.assertEquals("split position", 0, splitter.getPosition(0));
        Assert.assertEquals("split position", 4, splitter.getPosition(1));
        Assert.assertEquals("split position", 8, splitter.getPosition(2));
        Assert.assertEquals("split position", 8, splitter.getPosition(3));
    }

    @Test
    public void shouldSplitDocsWithNoLastLF() {
        SourceSplitter splitter = new SourceSplitter();
        splitter.reset("abc\r\ndef");
        Assert.assertEquals("split count", 2, splitter.count());
        Assert.assertEquals("split position", 0, splitter.getPosition(0));
        Assert.assertEquals("split position", 5, splitter.getPosition(1));
        Assert.assertEquals("split position", 8, splitter.getPosition(2));
        Assert.assertEquals("split find-offset", 0, splitter.findLineOffset(0));
        Assert.assertEquals("split find-offset", 0, splitter.findLineOffset(1));
        Assert.assertEquals("split find-offset", 0, splitter.findLineOffset(4));
        Assert.assertEquals("split find-offset", 1, splitter.findLineOffset(5));
        Assert.assertEquals("split find-offset", 1, splitter.findLineOffset(6));
    }

    @Test
    public void shouldHandleDocsOfLongerLength() {
        // 0             0
        // 0-- -  5-- - -1--- - 5--- - 2-
        final String INPUT = "ab\r\ncde\r\nefgh\r\nijk\r\nlm";
        SourceSplitter splitter = new SourceSplitter();
        splitter.reset(INPUT);
        Assert.assertEquals("split count", 5, splitter.count());
        Assert.assertEquals("split position", 0, splitter.getPosition(0));
        Assert.assertEquals("split position", 4, splitter.getPosition(1));
        Assert.assertEquals("split position", 9, splitter.getPosition(2));
        Assert.assertEquals("split position", 15, splitter.getPosition(3));
        Assert.assertEquals("split position", 20, splitter.getPosition(4));
        Assert.assertEquals("split position", 22, splitter.getPosition(5));
        /* Test findLineOffset() for every character with an alternate
        computation that counts every LFs.
         */
        for (int i = 0; i < (splitter.originalLength()); ++i) {
            char c = INPUT.charAt(i);
            int off = splitter.findLineOffset(i);
            long numLF = INPUT.substring(0, (i + 1)).chars().filter(( ch) -> ch == '\n').count();
            long exp = numLF - (c == '\n' ? 1 : 0);
            Assert.assertEquals(("split find-offset of " + i), exp, off);
        }
    }

    @Test
    public void shouldHandleStreamedDocsOfLongerLength() throws IOException {
        // 0             0
        // 0-- -  5-- - -1--- - 5--- - 2-
        final String INPUT = "ab\r\ncde\r\nefgh\r\nijk\r\nlm";
        StreamSource src = StreamSource.fromString(INPUT);
        SourceSplitter splitter = new SourceSplitter();
        splitter.reset(src);
        Assert.assertEquals("split count", 5, splitter.count());
        Assert.assertEquals("split position", 0, splitter.getPosition(0));
        Assert.assertEquals("split position", 4, splitter.getPosition(1));
        Assert.assertEquals("split position", 9, splitter.getPosition(2));
        Assert.assertEquals("split position", 15, splitter.getPosition(3));
        Assert.assertEquals("split position", 20, splitter.getPosition(4));
        Assert.assertEquals("split position", 22, splitter.getPosition(5));
        /* Test findLineOffset() for every character with an alternate
        computation that counts every LFs.
         */
        for (int i = 0; i < (splitter.originalLength()); ++i) {
            char c = INPUT.charAt(i);
            int off = splitter.findLineOffset(i);
            long numLF = INPUT.substring(0, (i + 1)).chars().filter(( ch) -> ch == '\n').count();
            long exp = numLF - (c == '\n' ? 1 : 0);
            Assert.assertEquals(("split find-offset of " + i), exp, off);
        }
    }
}

