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

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488__6)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488_add12046() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488_add12046__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488_add12046__6)));
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488_add12046__6)));
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488_literalMutationString10393() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "\u0000") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n\u000017/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n\u000017/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488_literalMutationString10408() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        18w,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        18w,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString6488__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        18w,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19614() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19614__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString19614__4)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_literalMutationNumber34698_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_literalMutationNumber34698 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0() throws Exception {
        try {
            final String lines = "";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_literalMutationNumber34694_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_literalMutationNumber34694 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_add34781_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_add34781 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_add34780_failAssert0() throws Exception {
        try {
            {
                final String lines = "";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString34433_failAssert0_add34780 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line did not return any info for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32126_failAssert0_literalMutationString32268_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", 0);
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32126 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationNumber32126_failAssert0_literalMutationString32268 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_add32470_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_add32470 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_literalMutationNumber32382_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_literalMutationNumber32382 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_add32469_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_add32469 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_literalMutationNumber32386_failAssert0() throws Exception {
        try {
            {
                final String lines = "\n\n";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0_literalMutationNumber32386 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122_failAssert0() throws Exception {
        try {
            final String lines = "\n\n";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString32122 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return any info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416_failAssert0_literalMutationString12591_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScr=eenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416_failAssert0_literalMutationString12591 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416_failAssert0() throws Exception {
        try {
            final String lines = "BlueScreenOfDeath";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber12414_failAssert0_literalMutationString12815_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(2, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber12414 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationNumber12414_failAssert0_literalMutationString12815 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'2\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416_failAssert0_add12855_failAssert0() throws Exception {
        try {
            {
                final String lines = "BlueScreenOfDeath";
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString12416_failAssert0_add12855 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32727_failAssert0_literalMutationString33270_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + "") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32727 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32727_failAssert0_literalMutationString33270 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741_failAssert0_add34127_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741_failAssert0_add34127 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32729_failAssert0_literalMutationString33168_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Setvings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32729 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32729_failAssert0_literalMutationString33168 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line returned OS error code \'1\' for command [cmd.exe, /C, dir /a /-c ]", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741_failAssert0_literalMutationNumber33095_failAssert0() throws Exception {
        try {
            {
                final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
                final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
                {
                    fsu.freeSpaceWindows("\u0000", (-1));
                }
                junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741 should have thrown IOException");
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741_failAssert0_literalMutationNumber33095 should have thrown IOException");
        } catch (IOException expected) {
            TestCase.assertEquals("Command line \'dir /-c\' did not return valid info for path \'\'", expected.getMessage());
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString32741 should have thrown IOException");
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

