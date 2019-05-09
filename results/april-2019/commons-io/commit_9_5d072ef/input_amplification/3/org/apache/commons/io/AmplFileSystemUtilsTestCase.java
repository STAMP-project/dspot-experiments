package org.apache.commons.io;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.TestCase;
import org.apache.commons.io.testtools.FileBasedTestCase;


public class AmplFileSystemUtilsTestCase extends FileBasedTestCase {
    public AmplFileSystemUtilsTestCase(final String name) {
        super(name);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString14627_literalMutationString18932() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "\u0000") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n\u0000              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString14627__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n\u0000              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString14584_literalMutationString15785() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + ((((((((("" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString14584__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString14644() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString14644__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString14644__6)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923__4)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923_add51862() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923_add51862__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923_add51862__4)));
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923_add51862__4)));
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923_literalMutationString50428() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Vlume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Vlume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString45923__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Vlume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82874_failAssert0_literalMutationNumber82969_failAssert0_literalMutationString84391_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "i";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82874 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82874_failAssert0_literalMutationNumber82969 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82874_failAssert0_literalMutationNumber82969_failAssert0_literalMutationString84391 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214_failAssert0_add85120_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214_failAssert0_add85120 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214_failAssert0_literalMutationString84112_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83214_failAssert0_literalMutationString84112 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_literalMutationNumber83600_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-2, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_literalMutationNumber83600 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0() throws Exception {
        try {
            final String lines = "";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82872_failAssert0_add83228_failAssert0_literalMutationString84984_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82872 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82872_failAssert0_add83228 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82872_failAssert0_add83228_failAssert0_literalMutationString84984 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83042_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 2);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83042 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_add85043_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_add85043 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83213_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_add83213 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_add85044_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_add85044 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_literalMutationNumber83608_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString82881_failAssert0_literalMutationNumber83035_failAssert0_literalMutationNumber83608 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_literalMutationNumber75940_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_literalMutationNumber75940 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729_failAssert0null76917_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows(null, (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729_failAssert0null76917 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_add76554_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_add76554 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_literalMutationNumber75120_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_literalMutationNumber75120 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730_failAssert0_literalMutationNumber75622_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730_failAssert0_literalMutationNumber75622 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_add76555_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_add76555 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_add76672_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_add76672 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_add76673_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_add76673 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0() throws Exception {
        try {
            final String lines = "\n\n";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729_failAssert0_add76734_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729_failAssert0_add76734 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74393_failAssert0_literalMutationString74652_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74393 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74393_failAssert0_literalMutationString74652 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74392_failAssert0_literalMutationString74470_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74392 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74392_failAssert0_literalMutationString74470 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729_failAssert0_literalMutationNumber76374_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", 2);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74729_failAssert0_literalMutationNumber76374 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_literalMutationNumber75949_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\u0000";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationString74522_failAssert0_literalMutationNumber75949 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_literalMutationNumber75127_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74400_failAssert0_literalMutationString74501_failAssert0_literalMutationNumber75127 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationNumber74528_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_literalMutationNumber74528 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber74396_failAssert0_add74727_failAssert0_literalMutationString75812_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("C:", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber74396 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber74396_failAssert0_add74727 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber74396_failAssert0_add74727_failAssert0_literalMutationString75812 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'-1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730_failAssert0_add76629_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "\n\n";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString74401_failAssert0_add74730_failAssert0_add76629 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273_failAssert0_add28731_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273_failAssert0_add28731 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273_failAssert0() throws Exception {
        try {
            final String lines = "BlueScreenOfDeath";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber28278_failAssert0_add28711_failAssert0_literalMutationString29665_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", 0);
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber28278 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber28278_failAssert0_add28711 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber28278_failAssert0_add28711_failAssert0_literalMutationString29665 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273_failAssert0_literalMutationNumber28636_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28273_failAssert0_literalMutationNumber28636 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28264_add28697_literalMutationString29019() throws Exception {
        final String lines = "eU/7cB&Y&r8efh1=%";
        TestCase.assertEquals("eU/7cB&Y&r8efh1=%", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        {
            long o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28264_add28697__5 = fsu.freeSpaceWindows("C:", (-1));
            long o_testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28264__5 = fsu.freeSpaceWindows("\u0000", (-1));
        }
        TestCase.assertEquals("eU/7cB&Y&r8efh1=%", lines);
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28272_failAssert0_add28715_failAssert0_literalMutationString29900_failAssert0() throws Exception {
        try {
            {
                {
                    final String lines = "BlueScreenOfDeath";
                    final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                    {
                        fsu.freeSpaceWindows("\u0000", (-1));
                    }
                    junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28272 should have thrown IOException");
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28272_failAssert0_add28715 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28272_failAssert0_add28715_failAssert0_literalMutationString29900 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28266_failAssert0_literalMutationString28387_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlCueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28266 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString28266_failAssert0_literalMutationString28387 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167_failAssert0_literalMutationString77929_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Docuents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167_failAssert0_literalMutationString77929 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77145_failAssert0_literalMutationString78073_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + ((("\u0000" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77145 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77145_failAssert0_literalMutationString78073 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167_failAssert0_add78577_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString77167_failAssert0_add78577 should have thrown IOException");
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

