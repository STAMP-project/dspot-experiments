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

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484_add11990() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484_add11990__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484_add11990__6)));
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484_add11990__6)));
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6443_literalMutationString9705() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6443__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484__6)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484_literalMutationString6724() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "x") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\nx Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\nx Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484_literalMutationString6747() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "\u0000") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n\u000017/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6484__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n\u000017/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19588() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19588__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19588__4)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19558_literalMutationString22837() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIxR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIxR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19558__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIxR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationNumber34608_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationNumber34608 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0() throws Exception {
        try {
            final String lines = "";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationString34605_failAssert0() throws Exception {
        try {
            {
                final String lines = "1";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationString34605 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationNumber34616_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationNumber34616 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationString34604_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_literalMutationString34604 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_add34678_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34347_failAssert0_add34678 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0() throws Exception {
        try {
            final String lines = "\n\n";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32088_failAssert0_literalMutationString32169_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32088 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32088_failAssert0_literalMutationString32169 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_literalMutationNumber32263_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_literalMutationNumber32263 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32084_failAssert0_literalMutationString32214_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32084 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32084_failAssert0_literalMutationString32214 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_add32413_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_add32413 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_literalMutationString32252_failAssert0() throws Exception {
        try {
            {
                final String lines = "d";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_literalMutationString32252 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_literalMutationString32251_failAssert0() throws Exception {
        try {
            {
                final String lines = "\u0000";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32087_failAssert0_literalMutationString32251 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12399_failAssert0_literalMutationString12722_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12399 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12399_failAssert0_literalMutationString12722 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410_failAssert0() throws Exception {
        try {
            final String lines = "BlueScreenOfDeath";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410_failAssert0_add12846_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12410_failAssert0_add12846 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32663_failAssert0_literalMutationString33155_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is H[D\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32663 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32663_failAssert0_literalMutationString33155 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32691_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32691 should have thrown IOException");
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

