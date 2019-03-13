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


import java.io.StringReader;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author austvik
 */
public class PerforceHistoryParserTest {
    public PerforceHistoryParserTest() {
    }

    /**
     * Test of parseChanges method, of class PerforceHistoryParser.
     */
    @Test
    public void parseChanges() throws Exception {
        String output = "Change 1234 on 2008/10/13 11:30:00 by ADMIN@UserWorkspaceName \'Comment given to changelist within single qoutes, this is change one\'\n" + (("Change 6543 on 2008/10/08 18:25:38 by USER@USER_WS \'Comment given to changelist within single qoutes\'\n" + "Change 7654 on 2008/09/30 01:00:01 by USER@USER_WS \'Comment given to changelist within single qoutes\'\n") + "Change 2345 on 2008/09/30 17:45:33 by ADMIN@Workspace2 \'Comment given to changelist within single qoutes\'\n");
        History result = PerforceHistoryParser.parseChanges(new StringReader(output));
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.getHistoryEntries().size());
        HistoryEntry e1 = result.getHistoryEntries().get(0);
        Assert.assertEquals("1234", e1.getRevision());
        Assert.assertEquals("ADMIN", e1.getAuthor());
        Assert.assertEquals(0, e1.getFiles().size());
        Assert.assertTrue(e1.getMessage().contains("change one"));
        HistoryEntry e2 = result.getHistoryEntries().get(1);
        Assert.assertNotNull(e2);
        HistoryEntry e3 = result.getHistoryEntries().get(2);
        Assert.assertNotNull(e3);
        HistoryEntry e4 = result.getHistoryEntries().get(3);
        Assert.assertEquals("2345", e4.getRevision());
        // Bug #16660: Months used to be off by one. Verify that they match
        // the dates in the sample output above.
        PerforceHistoryParserTest.assertDate(e1, 2008, Calendar.OCTOBER, 13);
        PerforceHistoryParserTest.assertDate(e2, 2008, Calendar.OCTOBER, 8);
        PerforceHistoryParserTest.assertDate(e3, 2008, Calendar.SEPTEMBER, 30);
        PerforceHistoryParserTest.assertDate(e4, 2008, Calendar.SEPTEMBER, 30);
    }

    /**
     * Test of parseFileLog method, of class PerforceHistoryParser.
     */
    @Test
    public void parseFileLog() throws Exception {
        String output = "//Path/To/Folder/In/Workspace/Filename\n" + ((((((((((((((("\n" + "... #4 change 1234 edit on 2008/08/19 11:30:00 by User@UserWorkspaceName (text)\n") + "\n") + "        Comment for the change number 4\n") + "\n") + "... #3 change 5678 edit on 2008/08/19 18:25:38 by ADMIN@UserWorkspaceName (text)\n") + "\n") + "        Comment for the change\n") + "\n") + "... #2 change 8765 edit on 2008/08/01 01:00:01 by ADMIN@UserWorkspaceName (text)\n") + "\n") + "        Comment for the change\n") + "\n") + "... #1 change 1 add on 2008/07/30 17:45:33 by ADMIN@UserWorkspaceName (text)\n") + "\n") + "        Comment for the change");
        History result = PerforceHistoryParser.parseFileLog(new StringReader(output));
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.getHistoryEntries().size());
        HistoryEntry e1 = result.getHistoryEntries().get(0);
        Assert.assertEquals("1234", e1.getRevision());
        Assert.assertEquals("User", e1.getAuthor());
        Assert.assertEquals(0, e1.getFiles().size());
        Assert.assertTrue(e1.getMessage().contains("number 4"));
        HistoryEntry e2 = result.getHistoryEntries().get(1);
        Assert.assertNotNull(e2);
        HistoryEntry e3 = result.getHistoryEntries().get(2);
        Assert.assertNotNull(e3);
        HistoryEntry e4 = result.getHistoryEntries().get(3);
        Assert.assertEquals("1", e4.getRevision());
        // Bug #16660: Months used to be off by one. Verify that they match
        // the dates in the sample output above.
        PerforceHistoryParserTest.assertDate(e1, 2008, Calendar.AUGUST, 19);
        PerforceHistoryParserTest.assertDate(e2, 2008, Calendar.AUGUST, 19);
        PerforceHistoryParserTest.assertDate(e3, 2008, Calendar.AUGUST, 1);
        PerforceHistoryParserTest.assertDate(e4, 2008, Calendar.JULY, 30);
    }
}

