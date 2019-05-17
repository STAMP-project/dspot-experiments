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

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486_add12031() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486_add12031__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486_add12031__6)));
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486_add12031__6)));
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486__6)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486_literalMutationNumber9489() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486_literalMutationString9471() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Deskto\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Deskto\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6486__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Deskto\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594__4)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594_add25488() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594_add25488__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594_add25488__4)));
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594_add25488__4)));
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594_literalMutationString20674() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 btes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 btes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19594__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 btes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354_failAssert0_literalMutationNumber34556_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354_failAssert0_literalMutationNumber34556 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354_failAssert0() throws Exception {
        try {
            final String lines = "";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354_failAssert0_literalMutationNumber34552_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354_failAssert0_literalMutationNumber34552 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354_failAssert0_add34685_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34354_failAssert0_add34685 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095_failAssert0() throws Exception {
        try {
            final String lines = "\n\n";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32090_failAssert0_literalMutationString32344_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(-1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32090 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32090_failAssert0_literalMutationString32344 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095_failAssert0_add32430_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095_failAssert0_add32430 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095_failAssert0_literalMutationString32351_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095_failAssert0_literalMutationString32351 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095_failAssert0_literalMutationNumber32357_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32095_failAssert0_literalMutationNumber32357 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409_failAssert0() throws Exception {
        try {
            final String lines = "BlueScreenOfDeath";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409_failAssert0_literalMutationNumber12658_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409_failAssert0_literalMutationNumber12658 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409_failAssert0_add12846_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12409_failAssert0_add12846 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12399_failAssert0_literalMutationString12827_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScr-enOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12399 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12399_failAssert0_literalMutationString12827 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410_failAssert0_literalMutationString12792_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410_failAssert0_literalMutationString12792 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber12406_failAssert0_literalMutationString12722_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber12406 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber12406_failAssert0_literalMutationString12722 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32676_failAssert0_literalMutationString34028_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + ((("\u0000" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32676 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32676_failAssert0_literalMutationString34028 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698_failAssert0_add34103_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698_failAssert0_add34103 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32688_failAssert0_literalMutationString33159_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + "VEn.^yvcpOC9j3chwSNAFs!]U*e&]?PH0f%J&npCF-kZ<") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32688 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32688_failAssert0_literalMutationString33159 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698_failAssert0_literalMutationString33909_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume S[erial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32698_failAssert0_literalMutationString33909 should have thrown IOException");
        } catch (IOException expected) {
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

