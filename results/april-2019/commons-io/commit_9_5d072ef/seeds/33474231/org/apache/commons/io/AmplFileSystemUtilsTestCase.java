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

    public void testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString383() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)        180,260 bytes\n") + "              10 Dir(s)  41,411,551,232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
        long o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString383__6 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_ParseCommaFormatBytes_literalMutationString383__6)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)        180,260 bytes\n              10 Dir(s)  41,411,551,232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyPath_literalMutationString1062() throws Exception {
        final String lines = " Volume in drive C is HDD\n" + (((((((((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\Xxxx\n") + "\n") + "19/08/2005  22:43    <DIR>          .\n") + "19/08/2005  22:43    <DIR>          ..\n") + "11/08/2005  01:07                81 build.properties\n") + "17/08/2005  21:44    <DIR>          Desktop\n") + "               7 File(s)         180260 bytes\n") + "              10 Dir(s)     41411551232 bytes free");
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
        final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines, "dir /a /-c ");
        long o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString1062__4 = fsu.freeSpaceWindows("\u0000", (-1));
        TestCase.assertEquals(41411551232L, ((long) (o_testGetFreeSpaceWindows_String_EmptyPath_literalMutationString1062__4)));
        TestCase.assertEquals(" Volume in drive C is HDD\n Volume Serial Number is XXXX-YYYY\n\n Directory of C:\\Documents and Settings\\Xxxx\n\n19/08/2005  22:43    <DIR>          .\n19/08/2005  22:43    <DIR>          ..\n11/08/2005  01:07                81 build.properties\n17/08/2005  21:44    <DIR>          Desktop\n               7 File(s)         180260 bytes\n              10 Dir(s)     41411551232 bytes free", lines);
    }

    public void testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString1780_failAssert0() throws Exception {
        try {
            final String lines = "";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyResponse_literalMutationString1780 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString1615_failAssert0() throws Exception {
        try {
            final String lines = "\n\n";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_EmptyMultiLineResponse_literalMutationString1615 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString621_failAssert0() throws Exception {
        try {
            final String lines = "BlueScreenOfDeath";
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(0, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_InvalidTextResponse_literalMutationString621 should have thrown IOException");
        } catch (IOException expected) {
        }
    }

    public void testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString1689_failAssert0() throws Exception {
        try {
            final String lines = " Volume in drive C is HDD\n" + (((" Volume Serial Number is XXXX-YYYY\n" + "\n") + " Directory of C:\\Documents and Settings\\empty") + "\n");
            final FileSystemUtils fsu = new AmplFileSystemUtilsTestCase.MockFileSystemUtils(1, lines);
            {
                fsu.freeSpaceWindows("\u0000", (-1));
            }
            junit.framework.TestCase.fail("testGetFreeSpaceWindows_String_NoSuchDirectoryResponse_literalMutationString1689 should have thrown IOException");
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

