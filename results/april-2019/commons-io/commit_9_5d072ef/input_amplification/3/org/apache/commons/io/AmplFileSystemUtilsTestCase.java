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

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801__6)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24800_literalMutationString29946() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24800__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_add30327() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_add30327__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_add30327__6)));
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_add30327__6)));
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_literalMutationString26436() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 byte\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 byte\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 byte\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_add30327null45885_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_add30327__6 = fsu.freeSpaceWindows("\u0000", (-1));
            long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801__6 = fsu.freeSpaceWindows(null, (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString24801_add30327null45885 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944_literalMutationString81701_literalMutationString91797() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/(2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/(2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/(2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980__4)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980_literalMutationString77345() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "\u0000") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n\u0000               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n\u0000               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944_literalMutationString81701() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/(2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/(2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/(2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944_literalMutationString81701_add99767() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/(2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/(2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944_literalMutationString81701_add99767__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944_literalMutationString81701_add99767__4)));
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/(2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76944_literalMutationString81701_add99767__4)));
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980_add83405() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980_add83405__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980_add83405__4)));
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString76980_add83405__4)));
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_add123624_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_add123624 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_add123623_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_add123623 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_literalMutationString116128_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>         .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_literalMutationString116128 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + ((((((((("=aYEcKsiz{QJ&>E4>w.jmGyTS}JQs:r1&rf" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
            long o_testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655__4 = fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100713_failAssert0_literalMutationString106284_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a! /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100713 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100713_failAssert0_literalMutationString106284 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a[! /-c \"C:\"]> but was:<dir /a[ /-c ]>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_literalMutationString116154_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)       +  180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_literalMutationString116154 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0null123820_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows(null, (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0null123820 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_literalMutationString121856_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "\u0000");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_literalMutationString121856 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_add123465_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_add123465 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_add123467_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107227_failAssert0_add123467 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_literalMutationString107005_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_literalMutationString107005 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821_failAssert0_literalMutationString116391_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + ((((((((("=aYEcKsiz{QJ&>E4>w.jmGyTS}JQs:r1&rf" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "K]#yu>9[(NVpj7.(UT8GK3W[B]7A1!6J&KmSlKMCsu/v") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                long o_testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821_failAssert0_literalMutationString116391 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
            fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100715_failAssert0_literalMutationString106898_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100715 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100715_failAssert0_literalMutationString106898 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821_failAssert0_add123474_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + ((((((((("=aYEcKsiz{QJ&>E4>w.jmGyTS}JQs:r1&rf" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                long o_testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821_failAssert0_add123474 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821_failAssert0_add123473_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + ((((((((("=aYEcKsiz{QJ&>E4>w.jmGyTS}JQs:r1&rf" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                long o_testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100655_literalMutationString104821_failAssert0_add123473 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_literalMutationString121827_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          \n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NormalResponse_literalMutationString100716_failAssert0_add107228_failAssert0_literalMutationString121827 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239_failAssert0_literalMutationNumber68456_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "8zq7pu9>p%(9kCK]IdBU?R8p?0Cfq]XabTEhB`cC%AtK#hB8Ov");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines, "dir /a /-c \"C:\\somedir\"");
                long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239_failAssert0_literalMutationNumber68456 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239_failAssert0_add76073_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "8zq7pu9>p%(9kCK]IdBU?R8p?0Cfq]XabTEhB`cC%AtK#hB8Ov");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239_failAssert0_add76073 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 buJld.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194__4 = fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "8zq7pu9>p%(9kCK]IdBU?R8p?0Cfq]XabTEhB`cC%AtK#hB8Ov");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214__4 = fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52214_literalMutationString54239 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366_failAssert0_add76240_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 buJld.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366_failAssert0_add76240 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.roperties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534_failAssert0_literalMutationString69232_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + ">=u,>DOB@ZEp%{4p$PQ[0,kx{1)t`y:x/kbr8MzC)<xd;fFl1P^r") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534_failAssert0_literalMutationString69232 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366_failAssert0_add76239_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 buJld.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366_failAssert0_add76239 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534_failAssert0_add76093_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.roperties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_literalMutationString58534_failAssert0_add76093 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            fsu.freeSpaceWindows("\u0000", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366_failAssert0_literalMutationString74560_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 buJld.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "-o$m$ {p;>vXLGpU|4ep`Dz");
                long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52194_literalMutationString57366_failAssert0_literalMutationString74560 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<[-o$m$ {p;>vXLGpU|4ep`Dz]> but was:<[dir /a /-c ]>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52170_add59137_literalMutationString63299_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + "") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
            long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52170_add59137__4 = fsu.freeSpaceWindows("\u0000", (-1));
            long o_testGetFreeSpaceWindows_String_StripDrive_literalMutationString52170__4 = fsu.freeSpaceWindows("C:\\somedir", (-1));
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52170_add59137_literalMutationString63299 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_add59218_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_add59218 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_add59219_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_StripDrive_literalMutationString52227_failAssert0_add59219 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0null24274_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows(null, (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0null24274 should have thrown ComparisonFailure");
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

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString2_literalMutationString1585_failAssert0_literalMutationString16513_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir v/a /-c \"C:\\somedir\"");
                long o_testGetFreeSpaceWindows_String_quoted_literalMutationString2__4 = fsu.freeSpaceWindows("\u0000", (-1));
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString2_literalMutationString1585 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString2_literalMutationString1585_failAssert0_literalMutationString16513 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir [v/a /-c \"C:\\somedir\"]> but was:<dir [/a /-c ]>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_add23857_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_add23857 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_add23858_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_add23858 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_literalMutationString14442_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_literalMutationString14442 should have thrown ComparisonFailure");
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

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_literalMutationString14469_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dkir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7049_failAssert0_literalMutationString14469 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<d[kir /a /-c \"C:\\somedir\"]> but was:<d[ir /a /-c ]>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_literalMutationString21017_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "J(D%*jZWHT=L/uz&eOA*}&r#t,2o6`qN9Hy!bV5") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_literalMutationString21017 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_add24038_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_add24038 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_add24037_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_add24037 should have thrown ComparisonFailure");
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

    public void testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_literalMutationString21010_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c \"C:\\somedir\"");
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74 should have thrown ComparisonFailure");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048 should have thrown ComparisonFailure");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_quoted_literalMutationString74_failAssert0_add7048_failAssert0_literalMutationString21010 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            TestCase.assertEquals("expected:<dir /a /-c [\"C:\\somedir\"]> but was:<dir /a /-c []>", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_add139816_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_add139816 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_add139817_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_add139817 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139589_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139589 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139588_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139588 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139494_failAssert0_literalMutationString139756_failAssert0_literalMutationString142972_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = ".";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139494 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139494_failAssert0_literalMutationString139756 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139494_failAssert0_literalMutationString139756_failAssert0_literalMutationString142972 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139484_failAssert0_literalMutationString139703_failAssert0_literalMutationString141266_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "j";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139484 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139484_failAssert0_literalMutationString139703 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139484_failAssert0_literalMutationString139703_failAssert0_literalMutationString141266 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139482_failAssert0_literalMutationString139612_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139482 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139482_failAssert0_literalMutationString139612 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139483_failAssert0_literalMutationString139687_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139483 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139483_failAssert0_literalMutationString139687 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139594_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139594 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139593_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139593 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748_failAssert0_literalMutationNumber142492_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 2);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748_failAssert0_literalMutationNumber142492 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139492_failAssert0_literalMutationString139643_failAssert0_add143480_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139492 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139492_failAssert0_literalMutationString139643 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139492_failAssert0_literalMutationString139643_failAssert0_add143480 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139599_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 2);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139599 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139590_failAssert0_literalMutationNumber143283_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "O";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 2);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139590 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139590_failAssert0_literalMutationNumber143283 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139601_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationNumber139601 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139492_failAssert0_literalMutationString139643_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139492 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139492_failAssert0_literalMutationString139643 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496_failAssert0_add139833_failAssert0_literalMutationString140600_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496_failAssert0_add139833 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496_failAssert0_add139833_failAssert0_literalMutationString140600 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139494_failAssert0_literalMutationString139763_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139494 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139494_failAssert0_literalMutationString139763 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0() throws Exception {
        try {
            final String lines = "";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_add139497_failAssert0_literalMutationString139545_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("C:", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_add139497 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_add139497_failAssert0_literalMutationString139545 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748_failAssert0_add143691_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139488_failAssert0_literalMutationString139748_failAssert0_add143691 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139590_failAssert0() throws Exception {
        try {
            {
                final String lines = "O";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString139491_failAssert0_literalMutationString139590 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496_failAssert0_add139832_failAssert0_literalMutationString140575_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                        fsu.freeSpaceWindows("C:", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496_failAssert0_add139832 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationNumber139496_failAssert0_add139832_failAssert0_literalMutationString140575 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124326_failAssert0_literalMutationString124520_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124326 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124326_failAssert0_literalMutationString124520 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124454_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124454 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124455_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124455 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124324_failAssert0_literalMutationString124566_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124324 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124324_failAssert0_literalMutationString124566 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_add124329_failAssert0_literalMutationString124377_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("C:", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_add124329 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_add124329_failAssert0_literalMutationString124377 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124322_failAssert0_literalMutationNumber124594_failAssert0_literalMutationString127409_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124322 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124322_failAssert0_literalMutationNumber124594 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124322_failAssert0_literalMutationNumber124594_failAssert0_literalMutationString127409 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124314_failAssert0_literalMutationString124400_failAssert0_literalMutationString127889_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124314 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124314_failAssert0_literalMutationString124400 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124314_failAssert0_literalMutationString124400_failAssert0_literalMutationString127889 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124463_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124463 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124465_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationNumber124465 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124316_failAssert0_add124656_failAssert0_literalMutationString125365_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "6";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("C:", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124316 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124316_failAssert0_add124656 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124316_failAssert0_add124656_failAssert0_literalMutationString125365 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_add124653_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_add124653 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_add124652_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_add124652 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124467_failAssert0_literalMutationString126216_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124467 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124467_failAssert0_literalMutationString126216 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124325_failAssert0_literalMutationString124536_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 2);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124325 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124325_failAssert0_literalMutationString124536 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124324_failAssert0_add124667_failAssert0_literalMutationString125427_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124324 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124324_failAssert0_add124667 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124324_failAssert0_add124667_failAssert0_literalMutationString125427 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationString124451_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0_literalMutationString124451 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124317_failAssert0_literalMutationString124506_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124317 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124317_failAssert0_literalMutationString124506 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124321_failAssert0_literalMutationString124581_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124321 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124321_failAssert0_literalMutationString124581 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475_failAssert0_add128609_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475_failAssert0_add128609 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323_failAssert0() throws Exception {
        try {
            final String lines = "\n\n";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString124323 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475_failAssert0_literalMutationString127943_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber124328_failAssert0_literalMutationString124475_failAssert0_literalMutationString127943 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502_literalMutationString46577_literalMutationNumber47538_failAssert0() throws Exception {
        try {
            final String lines = "t(@HWHE4!=EIwPr$f";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                long o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502__5 = fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502_literalMutationString46577_literalMutationNumber47538 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46515_failAssert0_literalMutationString46885_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46515 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46515_failAssert0_literalMutationString46885 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationString46789_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationString46789 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_add46961_failAssert0_add50993_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_add46961 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_add46961_failAssert0_add50993 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46514_failAssert0_literalMutationString46742_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 2);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46514 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46514_failAssert0_literalMutationString46742 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46800_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46800 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505_failAssert0_literalMutationNumber46652_failAssert0_literalMutationString49041_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueSceenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505_failAssert0_literalMutationNumber46652 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505_failAssert0_literalMutationNumber46652_failAssert0_literalMutationString49041 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502_literalMutationString46577() throws Exception {
        final String lines = "t(@HWHE4!=EIwPr$f";
        TestCase.assertEquals("t(@HWHE4!=EIwPr$f", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        {
            long o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502__5 = fsu.freeSpaceWindows("\u0000", (-1));
        }
        TestCase.assertEquals("t(@HWHE4!=EIwPr$f", lines);
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46801_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46801 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505_failAssert0_add46944_failAssert0_literalMutationString48344_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueSceenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("C:", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505_failAssert0_add46944 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46505_failAssert0_add46944_failAssert0_literalMutationString48344 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794_failAssert0_add51293_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794_failAssert0_add51293 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationString46790_failAssert0() throws Exception {
        try {
            {
                final String lines = "BluXeScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationString46790 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_add46961_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_add46961 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46517_failAssert0_literalMutationNumber46868_failAssert0_literalMutationString48506_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 1);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46517 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46517_failAssert0_literalMutationNumber46868 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46517_failAssert0_literalMutationNumber46868_failAssert0_literalMutationString48506 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_add46960_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_add46960 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779_failAssert0_literalMutationNumber50716_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779_failAssert0_literalMutationNumber50716 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794_failAssert0_literalMutationNumber50293_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46794_failAssert0_literalMutationNumber50293 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46517_failAssert0_literalMutationString46866_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46517 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46517_failAssert0_literalMutationString46866 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0() throws Exception {
        try {
            final String lines = "BlueScreenOfDeath";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779_failAssert0_add51342_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46510_failAssert0_literalMutationString46779_failAssert0_add51342 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46507_failAssert0_literalMutationString46691_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46507 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber46507_failAssert0_literalMutationString46691 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792_failAssert0_literalMutationNumber49804_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792_failAssert0_literalMutationNumber49804 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792_failAssert0_add51236_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46512_failAssert0_literalMutationNumber46792_failAssert0_add51236 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502_literalMutationString46577_add50952() throws Exception {
        final String lines = "t(@HWHE4!=EIwPr$f";
        TestCase.assertEquals("t(@HWHE4!=EIwPr$f", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        {
            long o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502_literalMutationString46577_add50952__5 = fsu.freeSpaceWindows("\u0000", (-1));
            TestCase.assertEquals(4L, ((long) (o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502_literalMutationString46577_add50952__5)));
            long o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502__5 = fsu.freeSpaceWindows("\u0000", (-1));
            TestCase.assertEquals(4L, ((long) (o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString46502_literalMutationString46577_add50952__5)));
        }
        TestCase.assertEquals("t(@HWHE4!=EIwPr$f", lines);
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129451_failAssert0_literalMutationNumber130256_failAssert0_literalMutationString136940_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129451 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129451_failAssert0_literalMutationNumber130256 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129451_failAssert0_literalMutationNumber130256_failAssert0_literalMutationString136940 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129422_failAssert0_add130861_failAssert0_literalMutationString135255_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129422 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129422_failAssert0_add130861 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129422_failAssert0_add130861_failAssert0_literalMutationString135255 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + ";8-/[}HF[z]R248h&RMTO]BVw#CF(lc+&1NR))u(ehfvw") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationNumber129452_failAssert0_literalMutationString130365_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 2);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationNumber129452 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationNumber129452_failAssert0_literalMutationString130365 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713_failAssert0_literalMutationString138340_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + ((("\u0000" + "\n") + ";8-/[}HF[z]R248h&RMTO]BVw#CF(lc+&1NR))u(ehfvw") + "\n");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713_failAssert0_literalMutationString138340 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713_failAssert0_add138873_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + ";8-/[}HF[z]R248h&RMTO]BVw#CF(lc+&1NR))u(ehfvw") + "\n");
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129441_failAssert0_literalMutationString130713_failAssert0_add138873 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_literalMutationNumber130467_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_literalMutationNumber130467 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_literalMutationString130453_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YhYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_literalMutationString130453 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationNumber129446_failAssert0_literalMutationString130024_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationNumber129446 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationNumber129446_failAssert0_literalMutationString130024 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_add130875_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_add130875 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129442_failAssert0_literalMutationString129881_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129442 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129442_failAssert0_literalMutationString129881 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_add130874_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString129450_failAssert0_add130874 should have thrown IOException");
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

