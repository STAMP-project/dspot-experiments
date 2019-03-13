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
 * Copyright (c) 2006, 2018, Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.history;


import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author austvik
 */
public class SubversionHistoryParserTest {
    private SubversionHistoryParser instance;

    public SubversionHistoryParserTest() {
    }

    /**
     * Test of parse method, of class SubversionHistoryParser.
     */
    @Test
    public void parseEmpty() throws Exception {
        // Empty repository shoud produce at least valid XML.
        History result = instance.parse(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<log>\n" + "</log>")));
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getHistoryEntries());
        Assert.assertTrue("Should not contain any history entries", (0 == (result.getHistoryEntries().size())));
    }

    /**
     * Test of parsing output similar to that in subversions own svn repository.
     */
    @Test
    public void ParseALaSvn() throws Exception {
        String revId1 = "12345";
        String author1 = "username1";
        String date1 = "2007-09-11T11:48:56.123456Z";
        String file1 = "trunk/project/filename.ext";
        String revId2 = "23456";
        String author2 = "username2";
        String date2 = "2006-08-10T11:38:56.123456Z";
        String file2 = "trunk/project/path/filename2.ext2";
        String revId3 = "765432";
        String author3 = "username3";
        String date3 = "2006-08-09T10:38:56.123456Z";
        String output = ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\"?>\n" + (("<log>\n" + "<logentry\n") + "   revision=\"")) + revId1) + "\">\n") + "<author>") + author1) + "</author>\n") + "<date>") + date1) + "</date>\n") + "<paths>\n") + "<path\n") + "   action=\"M\">") + file1) + "</path>\n") + "</paths>\n") + "<msg>* ") + file1) + "\n") + "  Description.\n") + "</msg>\n") + "</logentry>\n") + "<logentry\n") + "   revision=\"") + revId2) + "\">\n") + "<author>") + author2) + "</author>\n") + "<date>") + date2) + "</date>\n") + "<paths>\n") + "<path\n") + "   action=\"M\">") + file2) + "</path>\n") + "</paths>\n") + "<msg>* ") + file2) + "\n") + "  some comment\n") + "  over several lines.\n") + "</msg>\n") + "</logentry>\n") + "<logentry\n") + "   revision=\"") + revId3) + "\">\n") + "<author>") + author3) + "</author>\n") + "<date>") + date3) + "</date>\n") + "<paths>\n") + "<path\n") + "   action=\"M\">") + file1) + "</path>\n") + "<path\n") + "   action=\"A\">") + file2) + "</path>\n") + "</paths>\n") + "<msg>this is a longer comment - line1\n") + "    that spans some lines,\n") + "    three in fact - line3.\n") + "</msg>\n") + "</logentry>\n") + "</log>";
        History result = instance.parse(output);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getHistoryEntries());
        Assert.assertEquals(3, result.getHistoryEntries().size());
        String file1pref = Paths.get(Paths.get(("/" + file1)).toUri()).toFile().toString();
        String file2pref = Paths.get(Paths.get(("/" + file2)).toUri()).toFile().toString();
        HistoryEntry e1 = result.getHistoryEntries().get(0);
        Assert.assertEquals(revId1, e1.getRevision());
        Assert.assertEquals(author1, e1.getAuthor());
        Assert.assertEquals(1, e1.getFiles().size());
        Assert.assertEquals(file1pref, e1.getFiles().first());
        HistoryEntry e2 = result.getHistoryEntries().get(1);
        Assert.assertEquals(revId2, e2.getRevision());
        Assert.assertEquals(author2, e2.getAuthor());
        Assert.assertEquals(1, e2.getFiles().size());
        Assert.assertEquals(file2pref, e2.getFiles().first());
        HistoryEntry e3 = result.getHistoryEntries().get(2);
        Assert.assertEquals(revId3, e3.getRevision());
        Assert.assertEquals(author3, e3.getAuthor());
        Assert.assertEquals(2, e3.getFiles().size());
        Assert.assertEquals(file1pref, e3.getFiles().first());
        Assert.assertEquals(file2pref, e3.getFiles().last());
        Assert.assertTrue(e3.getMessage().contains("line1"));
        Assert.assertTrue(e3.getMessage().contains("line3"));
    }

    @Test
    public void testDateFormats() {
        String[][] dates = new String[][]{ new String[]{ "2007-09-11T11:48:56.123456Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null }, new String[]{ "2007-09-11T11:48:56.000000Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", null }, new String[]{ "2007-09-11T11:48:56.Z", "yyyy-MM-dd'T'HH:mm:ss.'Z'", null }, new String[]{ "2007-09-11 11:48:56Z", null, "throws exception" }, new String[]{ "2007-09-11T11:48:56", null, "throws exception" }, new String[]{ "2007-09-11T11:48:56.123456", null, "throws exception" }, new String[]{ "2007-09-11T11:48:56.000000", null, "throws exception" } };
        for (int i = 0; i < (dates.length); i++) {
            String revId = "12345";
            String author = "username1";
            String date = dates[i][0];
            String format = dates[i][1];
            boolean expectedException = (dates[i][2]) != null;
            String file = "trunk/project/filename.ext";
            try {
                String output = ((((((((((((((((((((("<?xml version=\"1.0\"?>\n" + (("<log>\n" + "<logentry\n") + "   revision=\"")) + revId) + "\">\n") + "<author>") + author) + "</author>\n") + "<date>") + date) + "</date>\n") + "<paths>\n") + "<path\n") + "   action=\"M\">") + file) + "</path>\n") + "</paths>\n") + "<msg>* ") + file) + "\n") + "  Description.\n") + "</msg>\n") + "</logentry>\n") + "</log>";
                History result = instance.parse(output);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getHistoryEntries());
                Assert.assertEquals(1, result.getHistoryEntries().size());
                HistoryEntry e = result.getHistoryEntries().get(0);
                Assert.assertEquals(revId, e.getRevision());
                Assert.assertEquals(author, e.getAuthor());
                Assert.assertEquals(new SimpleDateFormat(format).parse(date), e.getDate());
                Assert.assertEquals(1, e.getFiles().size());
                Assert.assertEquals(Paths.get(Paths.get(("/" + file)).toUri()).toFile().toString(), e.getFiles().first());
            } catch (IOException ex) {
                if (!expectedException) {
                    Assert.fail(("Should not throw an IO exception for " + date));
                }
            } catch (ParseException ex) {
                Assert.fail((("Parsing the date " + date) + " should not throw a parse exception"));
            }
        }
    }
}

