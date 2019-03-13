/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.tests.util;


import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.util.FileUtil;
import org.syncany.util.LimitedDosFileAttributes;


public class LimitedDosFileAttributesTest {
    @Test
    public void testFileDosAttrs() throws Exception {
        DosFileAttributes dosAttrsRHAS = FileUtil.dosAttrsFromString("rhas");
        Assert.assertTrue(dosAttrsRHAS.isReadOnly());
        Assert.assertTrue(dosAttrsRHAS.isHidden());
        Assert.assertTrue(dosAttrsRHAS.isArchive());
        Assert.assertTrue(dosAttrsRHAS.isSystem());
        DosFileAttributes dosAttrsRHA = FileUtil.dosAttrsFromString("rha-");
        Assert.assertTrue(dosAttrsRHA.isReadOnly());
        Assert.assertTrue(dosAttrsRHA.isHidden());
        Assert.assertTrue(dosAttrsRHA.isArchive());
        Assert.assertFalse(dosAttrsRHA.isSystem());
        DosFileAttributes dosAttrsRH = FileUtil.dosAttrsFromString("rh--");
        Assert.assertTrue(dosAttrsRH.isReadOnly());
        Assert.assertTrue(dosAttrsRH.isHidden());
        Assert.assertFalse(dosAttrsRH.isArchive());
        Assert.assertFalse(dosAttrsRH.isSystem());
        DosFileAttributes dosAttrsR = FileUtil.dosAttrsFromString("r---");
        Assert.assertTrue(dosAttrsR.isReadOnly());
        Assert.assertFalse(dosAttrsR.isHidden());
        Assert.assertFalse(dosAttrsR.isArchive());
        Assert.assertFalse(dosAttrsR.isSystem());
        DosFileAttributes dosAttrsNone = FileUtil.dosAttrsFromString("----");
        Assert.assertFalse(dosAttrsNone.isReadOnly());
        Assert.assertFalse(dosAttrsNone.isHidden());
        Assert.assertFalse(dosAttrsNone.isArchive());
        Assert.assertFalse(dosAttrsNone.isSystem());
        DosFileAttributes dosAttrsH = FileUtil.dosAttrsFromString("-h--");
        Assert.assertFalse(dosAttrsH.isReadOnly());
        Assert.assertTrue(dosAttrsH.isHidden());
        Assert.assertFalse(dosAttrsH.isArchive());
        Assert.assertFalse(dosAttrsH.isSystem());
        DosFileAttributes dosAttrsCorrect = FileUtil.dosAttrsFromString("NONO");
        Assert.assertFalse(dosAttrsCorrect.isReadOnly());
        Assert.assertFalse(dosAttrsCorrect.isHidden());
        Assert.assertFalse(dosAttrsCorrect.isArchive());
        Assert.assertFalse(dosAttrsCorrect.isSystem());
        // Can't do all ...
    }

    @Test
    public void testFileDosAttrsToString() throws Exception {
        Assert.assertEquals("rhas", LimitedDosFileAttributes.toString(FileUtil.dosAttrsFromString("rhas")));
        Assert.assertEquals("rh--", LimitedDosFileAttributes.toString(new DosFileAttributes() {
            public long size() {
                return 0;
            }

            public FileTime lastModifiedTime() {
                return null;
            }

            public FileTime lastAccessTime() {
                return null;
            }

            public boolean isSymbolicLink() {
                return false;
            }

            public boolean isRegularFile() {
                return false;
            }

            public boolean isOther() {
                return false;
            }

            public boolean isDirectory() {
                return false;
            }

            public Object fileKey() {
                return null;
            }

            public FileTime creationTime() {
                return null;
            }

            public boolean isReadOnly() {
                return true;
            }// r


            public boolean isHidden() {
                return true;
            }// h


            public boolean isArchive() {
                return false;
            }// -


            public boolean isSystem() {
                return false;
            }// -

        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileDosAttrsInvalid1() throws Exception {
        FileUtil.dosAttrsFromString("illegal");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileDosAttrsInvalid2() throws Exception {
        FileUtil.dosAttrsFromString(null);
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalSize() throws Exception {
        FileUtil.dosAttrsFromString("rhas").size();
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalLastModified() throws Exception {
        FileUtil.dosAttrsFromString("rhas").lastModifiedTime();
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalLastAccess() throws Exception {
        FileUtil.dosAttrsFromString("rhas").lastAccessTime();
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalIsSymlink() throws Exception {
        FileUtil.dosAttrsFromString("rhas").isSymbolicLink();
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalIsRegular() throws Exception {
        FileUtil.dosAttrsFromString("rhas").isRegularFile();
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalIsDirectory() throws Exception {
        FileUtil.dosAttrsFromString("rhas").isDirectory();
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalFileKey() throws Exception {
        FileUtil.dosAttrsFromString("rhas").fileKey();
    }

    @Test(expected = RuntimeException.class)
    public void testFileDosAttrsIllegalCreationTime() throws Exception {
        FileUtil.dosAttrsFromString("rhas").creationTime();
    }
}

