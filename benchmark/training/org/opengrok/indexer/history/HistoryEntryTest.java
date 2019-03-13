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
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.history;


import java.util.Date;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author austvik
 */
public class HistoryEntryTest {
    private HistoryEntry instance;

    private Date historyDate = new Date();

    private String historyRevision = "1.0";

    private String historyAuthor = "test author";

    private String historyMessage = "history entry message";

    public HistoryEntryTest() {
    }

    /**
     * Test of getLine method, of class HistoryEntry.
     */
    @Test
    public void getLine() {
        Assert.assertTrue(instance.getLine().contains(historyRevision));
        Assert.assertTrue(instance.getLine().contains(historyAuthor));
    }

    /**
     * Test of dump method, of class HistoryEntry.
     */
    @Test
    public void dump() {
        instance.dump();
        instance.setActive(false);
        instance.addFile("testFile1.txt");
        instance.addFile("testFile2.txt");
        instance.dump();
    }

    /**
     * Test of getAuthor method, of class HistoryEntry.
     */
    @Test
    public void getAuthor() {
        String result = instance.getAuthor();
        Assert.assertEquals(historyAuthor, result);
    }

    /**
     * Test of getDate method, of class HistoryEntry.
     */
    @Test
    public void getDate() {
        Assert.assertEquals(historyDate, instance.getDate());
        instance.setDate(null);
        Assert.assertNull(instance.getDate());
    }

    /**
     * Test of getMessage method, of class HistoryEntry.
     */
    @Test
    public void getMessage() {
        Assert.assertEquals(historyMessage, instance.getMessage());
    }

    /**
     * Test of getRevision method, of class HistoryEntry.
     */
    @Test
    public void getRevision() {
        Assert.assertEquals(historyRevision, instance.getRevision());
    }

    /**
     * Test of setAuthor method, of class HistoryEntry.
     */
    @Test
    public void setAuthor() {
        String newAuthor = "New Author";
        instance.setAuthor(newAuthor);
        Assert.assertEquals(newAuthor, instance.getAuthor());
    }

    /**
     * Test of setDate method, of class HistoryEntry.
     */
    @Test
    public void setDate() {
        Date date = new Date();
        instance.setDate(date);
        Assert.assertEquals(date, instance.getDate());
    }

    /**
     * Test of isActive method, of class HistoryEntry.
     */
    @Test
    public void isActive() {
        Assert.assertEquals(true, instance.isActive());
        instance.setActive(false);
        Assert.assertEquals(false, instance.isActive());
    }

    /**
     * Test of setActive method, of class HistoryEntry.
     */
    @Test
    public void setActive() {
        instance.setActive(true);
        Assert.assertEquals(true, instance.isActive());
        instance.setActive(false);
        Assert.assertEquals(false, instance.isActive());
    }

    /**
     * Test of setMessage method, of class HistoryEntry.
     */
    @Test
    public void setMessage() {
        String message = "Something";
        instance.setMessage(message);
        Assert.assertEquals(message, instance.getMessage());
    }

    /**
     * Test of setRevision method, of class HistoryEntry.
     */
    @Test
    public void setRevision() {
        String revision = "1.2";
        instance.setRevision(revision);
        Assert.assertEquals(revision, instance.getRevision());
    }

    /**
     * Test of appendMessage method, of class HistoryEntry.
     */
    @Test
    public void appendMessage() {
        String message = "Something Added";
        instance.appendMessage(message);
        Assert.assertTrue(instance.getMessage().contains(message));
    }

    /**
     * Test of addFile method, of class HistoryEntry.
     */
    @Test
    public void addFile() {
        String fileName = "test.file";
        HistoryEntry instance = new HistoryEntry();
        Assert.assertFalse(hasFileList());
        instance.addFile(fileName);
        Assert.assertTrue(instance.getFiles().contains(fileName));
        Assert.assertTrue(hasFileList());
    }

    /**
     * Test of getFiles method, of class HistoryEntry.
     */
    @Test
    public void getFiles() {
        String fileName = "test.file";
        instance.addFile(fileName);
        Assert.assertTrue(instance.getFiles().contains(fileName));
        Assert.assertEquals(1, instance.getFiles().size());
        instance.addFile("other.file");
        Assert.assertEquals(2, instance.getFiles().size());
    }

    /**
     * Test of setFiles method, of class HistoryEntry.
     */
    @Test
    public void setFiles() {
        TreeSet<String> files = new TreeSet<String>();
        files.add("file1.file");
        files.add("file2.file");
        instance.setFiles(files);
        Assert.assertEquals(2, instance.getFiles().size());
    }

    /**
     * Test of toString method, of class HistoryEntry.
     */
    @Test
    public void testToString() {
        Assert.assertTrue(instance.toString().contains(historyRevision));
        Assert.assertTrue(instance.toString().contains(historyAuthor));
    }

    /**
     * Test of strip method, of class HistoryEntry.
     */
    @Test
    public void strip() {
        TreeSet<String> files = new TreeSet<String>();
        files.add("file1.file");
        files.add("file2.file");
        instance.setFiles(files);
        instance.setTags("test tag");
        instance.strip();
        Assert.assertEquals(0, instance.getFiles().size());
        Assert.assertEquals(null, instance.getTags());
    }
}

