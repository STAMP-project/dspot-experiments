/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.util;


import org.junit.Assert;
import org.junit.Test;


public class FileSizeTest {
    static long KB_CO = 1024;

    static long MB_CO = 1024 * 1024;

    static long GB_CO = 1024 * (FileSizeTest.MB_CO);

    @Test
    public void testValueOf() {
        {
            FileSize fs = FileSize.valueOf("8");
            Assert.assertEquals(8, fs.getSize());
        }
        {
            FileSize fs = FileSize.valueOf("8 kbs");
            Assert.assertEquals((8 * (FileSizeTest.KB_CO)), fs.getSize());
        }
        {
            FileSize fs = FileSize.valueOf("8 kb");
            Assert.assertEquals((8 * (FileSizeTest.KB_CO)), fs.getSize());
        }
        {
            FileSize fs = FileSize.valueOf("12 mb");
            Assert.assertEquals((12 * (FileSizeTest.MB_CO)), fs.getSize());
        }
        {
            FileSize fs = FileSize.valueOf("5 GBs");
            Assert.assertEquals((5 * (FileSizeTest.GB_CO)), fs.getSize());
        }
    }

    @Test
    public void testToString() {
        {
            FileSize fs = new FileSize(8);
            Assert.assertEquals("8 Bytes", fs.toString());
        }
        {
            FileSize fs = new FileSize(((8 * 1024) + 3));
            Assert.assertEquals("8 KB", fs.toString());
        }
        {
            FileSize fs = new FileSize((((8 * 1024) * 1024) + (3 * 1024)));
            Assert.assertEquals("8 MB", fs.toString());
        }
        {
            FileSize fs = new FileSize((((8 * 1024) * 1024) * 1024L));
            Assert.assertEquals("8 GB", fs.toString());
        }
    }
}

