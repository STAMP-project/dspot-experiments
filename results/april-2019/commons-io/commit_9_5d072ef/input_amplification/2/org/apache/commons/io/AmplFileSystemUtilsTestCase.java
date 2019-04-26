package org.apache.commons.io;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;
import org.apache.commons.io.testtools.FileBasedTestCase;


public class AmplFileSystemUtilsTestCase extends FileBasedTestCase {
    public AmplFileSystemUtilsTestCase(final String name) {
        super(name);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705_add13260() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705_add13260__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705_add13260__6)));
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705_add13260__6)));
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7705__6)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7681_literalMutationString9581() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/0005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/0005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7681__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/0005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7644_literalMutationString11072() throws Exception {
        final String lines = " Volume in drive C i# HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C i# HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString7644__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C i# HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523__4)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523_literalMutationString26996() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "\u0000") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n\u0000              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n\u0000              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523_literalMutationString26984() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "\u0000") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n\u000017/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n\u000017/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523_add29001() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523_add29001__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523_add29001__4)));
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString22523_add29001__4)));
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
            fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705_failAssert0_literalMutationString35952_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10aDir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705_failAssert0_literalMutationString35952 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705_failAssert0_add36215_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705_failAssert0_add36215 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705_failAssert0_add36214_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString29705_failAssert0_add36214 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString14827_literalMutationString16012_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Docuqments and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString14827__4 = fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14827_literalMutationString16012 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationNumber14886_literalMutationString17053_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationNumber14886__4 = fsu.freeSpaceWindows("\u0000", 2);
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationNumber14886_literalMutationString17053 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882_failAssert0_add21858_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882_failAssert0_add21858 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882_failAssert0_add21859_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882_failAssert0_add21859 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString14830_literalMutationString18974_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString14830__4 = fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14830_literalMutationString18974 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882_failAssert0_literalMutationString20642_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "- ,AvQ<}0&X[dbF&y](GGWkpE]qLf[R!t]!sG3ZjoCsA$i") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString14882_failAssert0_literalMutationString20642 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_literalMutationNumber5780_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", 0);
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_literalMutationNumber5780 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_literalMutationNumber5768_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_literalMutationNumber5768 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39588_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39588 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39589_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39589 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39583_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39583 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39504_failAssert0_literalMutationString39645_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39504 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39504_failAssert0_literalMutationString39645 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0() throws Exception {
        try {
            final String lines = "";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationString39576_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationString39576 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationString39577_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationString39577 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_add39831_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_add39831 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_add39830_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_add39830 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39590_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39590 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39500_failAssert0_literalMutationString39782_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39500 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39500_failAssert0_literalMutationString39782 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39579_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39509_failAssert0_literalMutationNumber39579 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39503_failAssert0_literalMutationString39705_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39503 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39503_failAssert0_literalMutationString39705 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39502_failAssert0_literalMutationString39691_failAssert0() throws Exception {
        try {
            {
                final String lines = "!";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39502 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString39502_failAssert0_literalMutationString39691 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39512_failAssert0_literalMutationString39675_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber39512_failAssert0_literalMutationString39675 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationString36999_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationString36999 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationString37001_failAssert0() throws Exception {
        try {
            {
                final String lines = "j";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationString37001 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationString37000_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationString37000 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_add37138_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_add37138 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_add37137_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_add37137 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36795_failAssert0_literalMutationString37039_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36795 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36795_failAssert0_literalMutationString37039 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationNumber37011_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationNumber37011 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationNumber37010_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 2);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0_literalMutationNumber37010 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36802_failAssert0_literalMutationString37085_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36802 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36802_failAssert0_literalMutationString37085 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36794_failAssert0_literalMutationString36919_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36794 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36794_failAssert0_literalMutationString36919 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36801_failAssert0_literalMutationString36992_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36801 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36801_failAssert0_literalMutationString36992 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36793_failAssert0_literalMutationString36903_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36793 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber36793_failAssert0_literalMutationString36903 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799_failAssert0() throws Exception {
        try {
            final String lines = "\n\n";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString36799 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13885_failAssert0_literalMutationString14232_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13885 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13885_failAssert0_literalMutationString14232 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13891_failAssert0_literalMutationString14125_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13891 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13891_failAssert0_literalMutationString14125 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13900_failAssert0_literalMutationString14107_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13900 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13900_failAssert0_literalMutationString14107 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13895_failAssert0_literalMutationString14053_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13895 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13895_failAssert0_literalMutationString14053 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0() throws Exception {
        try {
            final String lines = "BlueScreenOfDeath";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13894_failAssert0_literalMutationString14089_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13894 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13894_failAssert0_literalMutationString14089 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationString14171_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationString14171 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationString14170_failAssert0() throws Exception {
        try {
            {
                final String lines = "Bl(ueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationString14170 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationNumber14174_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationNumber14174 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationNumber14178_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationNumber14178 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_add14344_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_add14344 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationString14168_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_literalMutationString14168 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13901_failAssert0_literalMutationString14216_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13901 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber13901_failAssert0_literalMutationString14216 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_add14345_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString13896_failAssert0_add14345 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_literalMutationString38521_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + "") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_literalMutationString38521 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_literalMutationString38507_failAssert0() throws Exception {
        try {
            {
                final String lines = "7}Rp7P{U,yQ5e4?2!WSoV4]r?]" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_literalMutationString38507 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_literalMutationString38509_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in driNe C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_literalMutationString38509 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_add39036_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_add39036 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_add39035_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString37617_failAssert0_add39035 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    static class MockFileSystemUtils extends FileSystemUtils {
        private final int exitCode;

        private final byte[] bytes;

        private final String cmd;

        public MockFileSystemUtils(final int exitCode, final String lines) {
            this(exitCode, lines, null);
        }

        public MockFileSystemUtils(final int exitCode, final String lines, final String cmd) {
            this.exitCode = exitCode;
            this.bytes = lines.getBytes();
            this.cmd = cmd;
        }

        @Override
        Process openProcess(final String[] params) {
            if ((cmd) != null) {
                TestCase.assertEquals(cmd, params[((params.length) - 1)]);
            }
            return new Process() {
                @Override
                public InputStream getErrorStream() {
                    return null;
                }

                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream(bytes);
                }

                @Override
                public OutputStream getOutputStream() {
                    return null;
                }

                @Override
                public int waitFor() throws InterruptedException {
                    return exitCode;
                }

                @Override
                public int exitValue() {
                    return exitCode;
                }

                @Override
                public void destroy() {
                }
            };
        }
    }
}

