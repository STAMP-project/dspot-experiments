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
 */
package org.opengrok.indexer.history;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.util.TestRepository;


/**
 *
 *
 * @author austvik
 */
public class GitHistoryParserTest {
    private final String gitISODatePattern = "yyyy-MM-dd'T'HH:mm:ssXXX";

    GitHistoryParser instance;

    private static TestRepository repository = new TestRepository();

    public GitHistoryParserTest() {
    }

    /**
     * Test of parse method, of class GitHistoryParser.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void parseEmpty() throws Exception {
        History result = instance.parse("");
        Assert.assertNotNull(result);
        Assert.assertTrue("Should not contain any history entries", (0 == (result.getHistoryEntries().size())));
    }

    /**
     * Parse something that could come out from the Memcached repository
     *
     * @throws java.lang.Exception
     * 		exception
     */
    @Test
    public void parseALaMemcached() throws Exception {
        String commitId1 = "1a23456789abcdef123456789abcderf123456789";
        String commitId2 = "2a2323487092314kjsdafsad7829342kjhsdf3289";
        String commitId3 = "3asdfq234242871934g2sadfsa327894234sa2389";
        String author1 = "username <username@asfdsaf-23412-sadf-cxvdsfg3123-sfasdf>";
        String author2 = "username2 <username2@as345af-23412-sadf-cxvdsfg3123-sfasdf>";
        String date1 = "2008-04-01T15:12:51+00:00";
        String date2 = "2006-05-22T15:23:15+00:00";
        String output = (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("commit " + commitId1) + "\n") + "Author:     ") + author1) + "\n") + "AuthorDate: ") + date1) + "\n") + "Commit:     ") + author1) + "\n") + "CommitDate: ") + date1) + "\n") + "\n") + "    patch from somebody <user.name@example.com>:\n") + "    \n") + "    commit message.\n") + "    \n") + "    \n") + "    git-svn-id: http://host.example.com/svn/product/trunk/server@324-fdws-2342-fsdaf-gds-234\n") + "\n") + "commit ") + commitId2) + "\n") + "Author:     ") + author2) + "\n") + "AuthorDate: ") + date2) + "\n") + "Commit:     ") + author2) + "\n") + "CommitDate: ") + date2) + "\n") + "\n") + "     r123@branch:  username | some date\n") + "     some comment\n") + "    \n") + "    \n") + "    git-svn-id: http://host.example.com/svn/product/trunk/server@324-fdws-2342-fsdaf-gds-234\n") + "\n") + "commit ") + commitId3) + "\n") + "Author:     ") + author1) + "\n") + "AuthorDate: ") + date1) + "\n") + "Commit:     ") + author2) + "\n") + "CommitDate: ") + date2) + "\n") + "\n") + "    some comment\n") + "    \n") + "    git-svn-id: http://host.example.com/svn/product/trunk/server@324-fdws-2342-fsdaf-gds-234\n";
        History result = instance.parse(output);
        Assert.assertNotNull(result);
        Assert.assertTrue("Should contain three history entries", (3 == (result.getHistoryEntries().size())));
        HistoryEntry e0 = result.getHistoryEntries().get(0);
        Assert.assertEquals(commitId1, e0.getRevision());
        Assert.assertEquals(author1, e0.getAuthor());
        Assert.assertEquals(new SimpleDateFormat(gitISODatePattern).parse(date1), e0.getDate());
        Assert.assertEquals(0, e0.getFiles().size());
        HistoryEntry e1 = result.getHistoryEntries().get(1);
        Assert.assertEquals(commitId2, e1.getRevision());
        Assert.assertEquals(author2, e1.getAuthor());
        Assert.assertEquals(new SimpleDateFormat(gitISODatePattern).parse(date2), e1.getDate());
        Assert.assertEquals(0, e1.getFiles().size());
        HistoryEntry e2 = result.getHistoryEntries().get(2);
        Assert.assertEquals(commitId3, e2.getRevision());
        Assert.assertEquals(author1, e2.getAuthor());
        Assert.assertEquals(new SimpleDateFormat(gitISODatePattern).parse(date1), e2.getDate());
        Assert.assertEquals(0, e2.getFiles().size());
    }

    /**
     * Parse something that could come out from the git repository
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void parseALaGit() throws Exception {
        String commitId1 = "1a23456789abcdef123456789abcderf123456789";
        String commitId2 = "2a2323487092314kjsdafsad7829342kjhsdf3289";
        String author1 = "username <username@example.com>";
        String author2 = "username2 <username2@example.com>";
        String date1 = "2008-01-13T01:12:05-07:00";
        String filename = "filename.c";
        String output = ((((((((((((((((((((((((((((((((((((((((((((((((((("commit " + commitId1) + "\n") + "Author:     ") + author1) + "\n") + "AuthorDate: ") + date1) + "\n") + "Commit:     ") + author2) + "\n") + "CommitDate: ") + date1) + "\n") + "\n") + "    Some heading\n") + "    \n") + "    First paragraph of text.\n") + "    \n") + "    Second paragraph\n") + "    of text.\n") + "    \n") + "    Signed-off-by: Somebody <username@example.com>\n") + "\n") + filename) + "\n") + "\n") + "commit ") + commitId2) + "\n") + "Author:     ") + author2) + "\n") + "AuthorDate: ") + date1) + "\n") + "Commit:     ") + author2) + "\n") + "CommitDate: ") + date1) + "\n") + "\n") + "    Make \"--somethind\" do something.\n") + "    \n") + "    This is a full\n") + "    paragraph of text\n") + "    \n") + "    Signed-off-by: Somebody <username@example.com>\n") + "\n") + filename) + "\n";
        History result = instance.parse(output);
        Assert.assertNotNull(result);
        Assert.assertTrue("Should contain two history entries", (2 == (result.getHistoryEntries().size())));
        HistoryEntry e0 = result.getHistoryEntries().get(0);
        Assert.assertEquals(commitId1, e0.getRevision());
        Assert.assertEquals(author1, e0.getAuthor());
        Assert.assertEquals(new SimpleDateFormat(gitISODatePattern).parse(date1), e0.getDate());
        Assert.assertEquals(1, e0.getFiles().size());
        Assert.assertEquals(((File.separator) + filename), e0.getFiles().first());
        Assert.assertTrue(e0.getMessage().contains("Some heading"));
        Assert.assertTrue(e0.getMessage().contains("Signed-off-by"));
        HistoryEntry e1 = result.getHistoryEntries().get(1);
        Assert.assertEquals(commitId2, e1.getRevision());
        Assert.assertEquals(author2, e1.getAuthor());
        Assert.assertEquals(new SimpleDateFormat(gitISODatePattern).parse(date1), e1.getDate());
        Assert.assertEquals(1, e1.getFiles().size());
        Assert.assertEquals(((File.separator) + filename), e1.getFiles().first());
        Assert.assertTrue(e1.getMessage().contains("paragraph of text"));
        Assert.assertTrue(e1.getMessage().contains("Signed-off-by"));
    }

    /**
     * Parse something that could come out from the linux kernel repository
     *
     * @throws java.lang.Exception
     * 		exception
     */
    @Test
    public void parseALaLK() throws Exception {
        String commitId1 = "1a23456789abcdef123456789abcderf123456789";
        String commitId2 = "2a2323487092314kjsdafsad7829342kjhsdf3289";
        String author1 = "username <username@example.com>";
        String author2 = "username2 <username2@example.com>";
        String committer = "committer <committer@example.com>";
        String date1 = "2008-01-13T01:12:05-07:00";
        String date2 = "2008-01-14T01:12:05-08:00";
        String filename1 = "directory/filename.c";
        String filename2 = "directory/filename.h";
        String output = (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("commit " + commitId1) + "\n") + "Author:     ") + author1) + "\n") + "AuthorDate: ") + date1) + "\n") + "Commit:     ") + committer) + "\n") + "CommitDate: ") + date2) + "\n") + "\n") + "    Subject: subject title\n") + "    \n") + "    sdj fasodjfads jfa.kdsmf asdknf sadlfkm sad fma\n") + "    dpojfv adsjv a,s.kdnvl\u00f8 aok \u00e5pwaiorjf aldjfg ladijfg adkgf\n") + "    jsdkgfj sadhkjfgs dlkjfg dksjgfh.\n") + "    \n") + "    djkhfgv ksadhg kdajhg ,dsn \n") + "    x,nv ,xmcnvkadsjfnv,. zxmcnv edfhsgdksgf.\n") + "    Dsdn ,dn ,dsng .,xcmnvefjhgiorfhgdskhg fdsg dfh sdf\n") + "    skdjfas djskdjf ksadjhfn sa.,df .\n") + "    \n") + "    Zkjd flsdj flksadj f\u00f8dsakjf asd jfsadijfosdhva.\n") + "    \n") + "    [user@example.com: something or another]\n") + "    Signed-off-by: First Last <username@example.com>\n") + "    Cc: Firstr Last <username@example.com>\n") + "    Signed-off-by: First Last <username3@example.com>\n") + "    Signed-off-by: Pinguin <pingu@example.com>\n") + "\n") + filename1) + "\n") + "\n") + "commit ") + commitId2) + "\n") + "Author:     ") + author2) + "\n") + "AuthorDate: ") + date1) + "\n") + "Commit:     ") + committer) + "\n") + "CommitDate: ") + date2) + "\n") + "\n") + "    [PATCH] Subject heading.\n") + "    \n") + "    Some description of what is to come:\n") + "    \n") + "      * item 1\n") + "      * item 2\n") + "      * ...\n") + "      * item n\n") + "    \n") + "    Signed-off-by: User <user@example.com>\n") + "    Cc: \"First.Last\" <user2@example.com>\n") + "    Signed-off-by: First Last <username3@example.com>\n") + "    Signed-off-by: Pinguin <pingu@example.com>\n") + "\n") + filename1) + "\n") + filename2) + "\n";
        History result = instance.parse(output);
        Assert.assertNotNull(result);
        Assert.assertTrue("Should contain two history entries", (2 == (result.getHistoryEntries().size())));
        HistoryEntry e0 = result.getHistoryEntries().get(0);
        Assert.assertEquals(commitId1, e0.getRevision());
        Assert.assertEquals(author1, e0.getAuthor());
        Assert.assertEquals(new SimpleDateFormat(gitISODatePattern).parse(date1), e0.getDate());
        Assert.assertEquals(1, e0.getFiles().size());
        Assert.assertEquals(((File.separator) + (filename1.replace('/', File.separatorChar))), e0.getFiles().first());
        Assert.assertTrue(e0.getMessage().contains("subject title"));
        Assert.assertTrue(e0.getMessage().contains("Signed-off-by"));
        HistoryEntry e1 = result.getHistoryEntries().get(1);
        Assert.assertEquals(commitId2, e1.getRevision());
        Assert.assertEquals(author2, e1.getAuthor());
        Assert.assertEquals(new SimpleDateFormat(gitISODatePattern).parse(date1), e1.getDate());
        Assert.assertEquals(2, e1.getFiles().size());
        Assert.assertEquals(((File.separator) + (filename1.replace('/', File.separatorChar))), e1.getFiles().first());
        Assert.assertEquals(((File.separator) + (filename2.replace('/', File.separatorChar))), e1.getFiles().last());
        Assert.assertTrue(e1.getMessage().contains("[PATCH]"));
        Assert.assertTrue(e1.getMessage().contains("Signed-off-by"));
    }

    @Test
    public void testDateFormats() {
        String[][] dates = new String[][]{ new String[]{ "2017-07-25T13:17:44+02:00", gitISODatePattern } };
        for (int i = 0; i < (dates.length); i++) {
            try {
                String commitId = "1a23456789abcdef123456789abcderf123456789";
                String author = "username <username@asfdsaf-23412-sadf-cxvdsfg3123-sfasdf>";
                String date = dates[i][0];
                String format = dates[i][1];
                Date parsedDate = new SimpleDateFormat(format).parse(date);
                String output = (((((((((((((((((((("commit " + commitId) + "\n") + "Author:     ") + author) + "\n") + "AuthorDate: ") + date) + "\n") + "Commit:     ") + author) + "\n") + "CommitDate: ") + date) + "\n") + "\n") + "    patch from somebody <user.name@example.com>:\n") + "    \n") + "    commit message.\n") + "    \n") + "    \n") + "    git-svn-id: http://host.example.com/svn/product/trunk/server@324-fdws-2342-fsdaf-gds-234\n";
                History result = instance.parse(output);
                Assert.assertNotNull(result);
                Assert.assertTrue("Should contain one history entry", (1 == (result.getHistoryEntries().size())));
                HistoryEntry e0 = result.getHistoryEntries().get(0);
                Assert.assertEquals(commitId, e0.getRevision());
                Assert.assertEquals(author, e0.getAuthor());
                Assert.assertEquals(((("The date " + parsedDate) + " should be equal to the parsed date ") + (e0.getDate())), parsedDate, e0.getDate());
                Assert.assertEquals(0, e0.getFiles().size());
            } catch (ParseException ex) {
                Assert.fail(("Should not throw a parse exception" + ex));
            } catch (IOException ex) {
                Assert.fail(("Should not throw an IO exception" + ex));
            }
        }
    }
}

