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
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.web;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * JUnit test to test that the DirectoryListing produce the expected result
 */
public class DirectoryListingTest {
    /**
     * Indication of that the file was a directory and so that the size given by
     * the FS is platform dependent.
     */
    private static final int DIRECTORY_INTERNAL_SIZE = -2;

    /**
     * Indication of unparseable file size.
     */
    private static final int INVALID_SIZE = -1;

    private File directory;

    private DirectoryListingTest.FileEntry[] entries;

    private SimpleDateFormat dateFormatter;

    class FileEntry implements Comparable<DirectoryListingTest.FileEntry> {
        String name;

        String href;

        long lastModified;

        /**
         * May be:
         * <pre>
         * positive integer - for a file
         * -2 - for a directory
         * -1 - for an unparseable size
         * </pre>
         */
        int size;

        List<DirectoryListingTest.FileEntry> subdirs;

        FileEntry() {
            dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
        }

        private FileEntry(String name, String href, long lastModified, int size, List<DirectoryListingTest.FileEntry> subdirs) {
            this();
            this.name = name;
            this.href = href;
            this.lastModified = lastModified;
            this.size = size;
            this.subdirs = subdirs;
        }

        /**
         * Creating the directory entry.
         *
         * @param name
         * 		name of the file
         * @param href
         * 		href to the file
         * @param lastModified
         * 		date of last modification
         * @param subdirs
         * 		list of sub entries (may be empty)
         */
        FileEntry(String name, String href, long lastModified, List<DirectoryListingTest.FileEntry> subdirs) {
            this(name, href, lastModified, DirectoryListingTest.DIRECTORY_INTERNAL_SIZE, subdirs);
            Assert.assertNotNull(subdirs);
        }

        /**
         * Creating a regular file entry.
         *
         * @param name
         * 		name of the file
         * @param href
         * 		href to the file
         * @param lastModified
         * 		date of last modification
         * @param size
         * 		the desired size of the file on the disc
         */
        FileEntry(String name, String href, long lastModified, int size) {
            this(name, href, lastModified, size, null);
        }

        private void create() throws Exception {
            File file = new File(directory, name);
            if (((subdirs) != null) && ((subdirs.size()) > 0)) {
                // this is a directory
                Assert.assertTrue("Failed to create a directory", file.mkdirs());
                for (DirectoryListingTest.FileEntry entry : subdirs) {
                    entry.name = ((name) + (File.separator)) + (entry.name);
                    entry.create();
                }
            } else {
                Assert.assertTrue("Failed to create file", file.createNewFile());
            }
            long val = lastModified;
            if (val == (Long.MAX_VALUE)) {
                val = System.currentTimeMillis();
            }
            Assert.assertTrue("Failed to set modification time", file.setLastModified(val));
            if (((subdirs) == null) && ((size) > 0)) {
                try (FileOutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[size];
                    out.write(buffer);
                }
            }
        }

        @Override
        public int compareTo(DirectoryListingTest.FileEntry fe) {
            int ret = -1;
            // @todo verify all attributes!
            if (((name.compareTo(fe.name)) == 0) && ((href.compareTo(fe.href)) == 0)) {
                // this is a file so the size must be exact
                if ((((subdirs) == null) && ((size) == (fe.size))) || // this is a directory so the size must have been "-" char
                (((subdirs) != null) && ((size) == (DirectoryListingTest.DIRECTORY_INTERNAL_SIZE)))) {
                    ret = 0;
                }
            }
            return ret;
        }
    }

    /**
     * Test directory listing
     *
     * @throws java.lang.Exception
     * 		if an error occurs while generating the list.
     */
    @Test
    public void directoryListing() throws Exception {
        StringWriter out = new StringWriter();
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<start>\n");
        DirectoryListing instance = new DirectoryListing();
        instance.listTo("ctx", directory, out, directory.getPath(), Arrays.asList(directory.list()));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Assert.assertNotNull("DocumentBuilderFactory is null", factory);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Assert.assertNotNull("DocumentBuilder is null", builder);
        out.append("</start>\n");
        String str = out.toString();
        Document document = builder.parse(new ByteArrayInputStream(str.getBytes()));
        NodeList nl = document.getElementsByTagName("tr");
        int len = nl.getLength();
        // Add one extra for header and one for parent directory link.
        Assert.assertEquals(((entries.length) + 2), len);
        // Skip the the header and parent link.
        for (int i = 2; i < len; ++i) {
            validateEntry(((Element) (nl.item(i))));
        }
    }
}

