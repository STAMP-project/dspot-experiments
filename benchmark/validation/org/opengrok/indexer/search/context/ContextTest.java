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


import java.io.CharArrayReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.search.QueryBuilder;

import static Context.MAXFILEREAD;


public class ContextTest {
    /**
     * The value returned by {@link RuntimeEnvironment#isQuickContextScan()}
     * before the test is run. Will be used to restore the flag after each test
     * case.
     */
    private boolean savedQuickContextScanFlag;

    /**
     * Tests for the isEmpty() method.
     *
     * @throws org.apache.lucene.queryparser.classic.ParseException
     * 		parse exception
     */
    @Test
    public void testIsEmpty() throws ParseException {
        String term = "qwerty";
        // Definition search should be used
        QueryBuilder qb = new QueryBuilder().setDefs(term);
        Context c = new Context(qb.build(), qb);
        Assert.assertFalse(c.isEmpty());
        // Symbol search should be used
        qb = new QueryBuilder().setRefs(term);
        c = new Context(qb.build(), qb);
        Assert.assertFalse(c.isEmpty());
        // Full search should be used
        qb = new QueryBuilder().setFreetext(term);
        c = new Context(qb.build(), qb);
        Assert.assertFalse(c.isEmpty());
        // History search should not be used
        qb = new QueryBuilder().setHist(term);
        c = new Context(qb.build(), qb);
        Assert.assertTrue(c.isEmpty());
        // Path search should not be used
        qb = new QueryBuilder().setPath(term);
        c = new Context(qb.build(), qb);
        Assert.assertTrue(c.isEmpty());
        // Combined search should be fine
        qb = new QueryBuilder().setHist(term).setFreetext(term);
        c = new Context(qb.build(), qb);
        Assert.assertFalse(c.isEmpty());
    }

    /**
     * Tests for the getContext() method.
     *
     * @throws org.apache.lucene.queryparser.classic.ParseException
     * 		parse exception
     */
    @Test
    public void testGetContext() throws ParseException {
        testGetContext(true, true);// limited scan, output to list

        testGetContext(false, true);// unlimited scan, output to list

        testGetContext(true, false);// limited scan, output to writer

        testGetContext(false, false);// unlimited scan, output to writer

    }

    /**
     * Test that we don't get an {@code ArrayIndexOutOfBoundsException} when a
     * long (&gt;100 characters) line which contains a match is not terminated
     * with a newline character before the buffer boundary. Bug #383.
     *
     * @throws org.apache.lucene.queryparser.classic.ParseException
     * 		parse exception
     */
    @Test
    public void testLongLineNearBufferBoundary() throws ParseException {
        char[] chars = new char[MAXFILEREAD];
        Arrays.fill(chars, 'a');
        char[] substring = " this is a test ".toCharArray();
        System.arraycopy(substring, 0, chars, ((MAXFILEREAD) - (substring.length)), substring.length);
        Reader in = new CharArrayReader(chars);
        QueryBuilder qb = new QueryBuilder().setFreetext("test");
        Context c = new Context(qb.build(), qb);
        StringWriter out = new StringWriter();
        boolean match = c.getContext(in, out, "", "", "", null, true, qb.isDefSearch(), null);
        Assert.assertTrue("No match found", match);
        String s = out.toString();
        Assert.assertTrue("Match not written to Writer", s.contains(" this is a <b>test</b>"));
        Assert.assertTrue("No match on line #1", s.contains("href=\"#1\""));
    }

    /**
     * Test that we get the [all...] link if a very long line crosses the buffer
     * boundary. Bug 383.
     *
     * @throws org.apache.lucene.queryparser.classic.ParseException
     * 		parse exception
     */
    @Test
    public void testAllLinkWithLongLines() throws ParseException {
        // Create input which consists of one single line longer than
        // Context.MAXFILEREAD.
        StringBuilder sb = new StringBuilder();
        sb.append("search_for_me");
        while ((sb.length()) <= (MAXFILEREAD)) {
            sb.append(" more words");
        } 
        Reader in = new StringReader(sb.toString());
        StringWriter out = new StringWriter();
        QueryBuilder qb = new QueryBuilder().setFreetext("search_for_me");
        Context c = new Context(qb.build(), qb);
        boolean match = c.getContext(in, out, "", "", "", null, true, qb.isDefSearch(), null);
        Assert.assertTrue("No match found", match);
        String s = out.toString();
        Assert.assertTrue("No [all...] link", s.contains(">[all...]</a>"));
    }

    /**
     * Test that a line with more than 100 characters after the first match is
     * truncated, and that &hellip; is appended to show that the line is
     * truncated. Bug 383.
     *
     * @throws org.apache.lucene.queryparser.classic.ParseException
     * 		parse exception
     */
    @Test
    public void testLongTruncatedLine() throws ParseException {
        StringBuilder sb = new StringBuilder();
        sb.append("search_for_me");
        while ((sb.length()) <= 100) {
            sb.append(" more words");
        } 
        sb.append("should not be found");
        Reader in = new StringReader(sb.toString());
        StringWriter out = new StringWriter();
        QueryBuilder qb = new QueryBuilder().setFreetext("search_for_me");
        Context c = new Context(qb.build(), qb);
        boolean match = c.getContext(in, out, "", "", "", null, true, qb.isDefSearch(), null);
        Assert.assertTrue("No match found", match);
        String s = out.toString();
        Assert.assertTrue("Match not listed", s.contains("<b>search_for_me</b>"));
        Assert.assertFalse("Line not truncated", s.contains("should not be found"));
        Assert.assertTrue("Ellipsis not found", s.contains("&hellip;"));
    }

    /**
     * Test that valid HTML is generated for a match that spans multiple lines.
     * It used to nest the tags incorrectly. Bug #15632.
     *
     * @throws java.lang.Exception
     * 		exception
     */
    @Test
    public void testMultiLineMatch() throws Exception {
        StringReader in = new StringReader("a\nb\nc\n");
        StringWriter out = new StringWriter();
        // XML boilerplate
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.append("<document>\n");
        // Search for a multi-token phrase that spans multiple lines in the
        // input file. The generated HTML fragment is inserted inside a root
        // element so that the StringWriter contains a valid XML document.
        QueryBuilder qb = new QueryBuilder().setFreetext("\"a b c\"");
        Context c = new Context(qb.build(), qb);
        Assert.assertTrue("No match found", c.getContext(in, out, "", "", "", null, true, qb.isDefSearch(), null));
        // Close the XML document body
        out.append("\n</document>");
        // Check that valid XML was generated. This call used to fail with
        // SAXParseException: [Fatal Error] :3:55: The element type "b" must
        // be terminated by the matching end-tag "</b>".
        Assert.assertNotNull(parseXML(out.toString()));
    }

    /**
     * Verify that the matching lines are shown in their original form and not
     * lower-cased (bug #16848).
     *
     * @throws java.lang.Exception
     * 		exception
     */
    @Test
    public void bug16848() throws Exception {
        StringReader in = new StringReader("Mixed case: abc AbC dEf\n");
        StringWriter out = new StringWriter();
        QueryBuilder qb = new QueryBuilder().setFreetext("mixed");
        Context c = new Context(qb.build(), qb);
        Assert.assertTrue(c.getContext(in, out, "", "", "", null, false, qb.isDefSearch(), null));
        Assert.assertEquals(("<a class=\"s\" href=\"#1\"><span class=\"l\">1</span> " + "<b>Mixed</b> case: abc AbC dEf</a><br/>"), out.toString());
    }

    /**
     * The results from mixed-case symbol search should contain tags.
     *
     * @throws java.lang.Exception
     * 		exception
     */
    @Test
    public void bug17582() throws Exception {
        // Freetext search should match regardless of case
        bug17582(new QueryBuilder().setFreetext("Bug17582"), new int[]{ 2, 3 }, new String[]{ "type1", "type2" });
        // Defs search should only match if case matches
        bug17582(new QueryBuilder().setDefs("Bug17582"), new int[]{ 3 }, new String[]{ "type2" });
        // Refs search should only match if case matches
        bug17582(new QueryBuilder().setRefs("Bug17582"), new int[]{ 3 }, new String[]{ "type2" });
        // Path search shouldn't match anything in source
        bug17582(new QueryBuilder().setPath("Bug17582"), new int[0], new String[0]);
        // Refs should only match if case matches, but freetext will match
        // regardless of case
        bug17582(new QueryBuilder().setRefs("Bug17582").setFreetext("Bug17582"), new int[]{ 2, 3 }, new String[]{ "type1", "type2" });
        // Refs should only match if case matches, hist shouldn't match
        // anything in source
        bug17582(new QueryBuilder().setRefs("Bug17582").setHist("bug17582"), new int[]{ 3 }, new String[]{ "type2" });
    }

    /**
     * Test that regexp search has matched words in context.
     *
     * @throws ParseException
     * 		parse exception
     */
    @Test
    public void regexpSearchContextTest() throws ParseException {
        // regex match is returned in context
        searchContextTestHelper("one two three", "/t.o|three/", "two");
        // regex match is returned in context
        searchContextTestHelper("one two three", "/t.o|three/", "three");
        // not matching regexp will not return anything
        searchContextTestHelper("one two three", "/z..z/", null);
    }
}

