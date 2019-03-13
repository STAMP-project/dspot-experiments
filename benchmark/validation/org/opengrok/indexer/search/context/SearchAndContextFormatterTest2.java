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
 * Copyright (c) 2008, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.search.context;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.search.SearchEngine;
import org.opengrok.indexer.util.CustomAssertions;
import org.opengrok.indexer.util.TestRepository;


/**
 * Represents a container for tests of {@link SearchEngine} with
 * {@link ContextFormatter} etc. with a non-zero tab-size.
 * <p>
 * Derived from Trond Norbye's {@code SearchEngineTest}
 */
@ConditionalRun(CtagsInstalled.class)
public class SearchAndContextFormatterTest2 {
    private static final int TABSIZE = 8;

    private static final List<File> TEMP_DIRS = new ArrayList<>();

    private static RuntimeEnvironment env;

    private static TestRepository repository1;

    private static TestRepository repository2;

    private static File configFile;

    private static boolean originalProjectsEnabled;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    @Test
    public void testSearch() throws IOException, InvalidTokenOffsetsException {
        SearchEngine instance = new SearchEngine();
        instance.setFreetext("Hello");
        instance.setFile("renamed2.c");
        int noHits = instance.search();
        Assert.assertTrue("noHits should be positive", (noHits > 0));
        String[] frags = getFirstFragments(instance);
        Assert.assertNotNull("getFirstFragments() should return something", frags);
        Assert.assertTrue("frags should have one element", ((frags.length) == 1));
        Assert.assertNotNull("frags[0] should be defined", frags[0]);
        final String CTX = "<a class=\"s\" href=\"/source/symlink1/git/moved2/renamed2.c#16\"><span class=\"l\">16</span> </a><br/>" + ("<a class=\"s\" href=\"/source/symlink1/git/moved2/renamed2.c#17\"><span class=\"l\">17</span>         printf ( &quot;<b>Hello</b>, world!\\n&quot; );</a><br/>" + "<a class=\"s\" href=\"/source/symlink1/git/moved2/renamed2.c#18\"><span class=\"l\">18</span> </a><br/>");
        CustomAssertions.assertLinesEqual("ContextFormatter output", CTX, frags[0]);
        instance.destroy();
    }
}

