/**
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Kohsuke Kawaguchi,
 * Daniel Dyer, Erik Ramfelt, Richard Bair, id:cactusman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson;


import hudson.os.WindowsUtil;
import hudson.util.StreamTaskListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class UtilTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testReplaceMacro() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("A", "a");
        m.put("A.B", "a-b");
        m.put("AA", "aa");
        m.put("B", "B");
        m.put("DOLLAR", "$");
        m.put("ENCLOSED", "a${A}");
        // longest match
        Assert.assertEquals("aa", Util.replaceMacro("$AA", m));
        // invalid keys are ignored
        Assert.assertEquals("$AAB", Util.replaceMacro("$AAB", m));
        Assert.assertEquals("aaB", Util.replaceMacro("${AA}B", m));
        Assert.assertEquals("${AAB}", Util.replaceMacro("${AAB}", m));
        // $ escaping
        Assert.assertEquals("asd$${AA}dd", Util.replaceMacro("asd$$$${AA}dd", m));
        Assert.assertEquals("$", Util.replaceMacro("$$", m));
        Assert.assertEquals("$$", Util.replaceMacro("$$$$", m));
        // dots
        Assert.assertEquals("a.B", Util.replaceMacro("$A.B", m));
        Assert.assertEquals("a-b", Util.replaceMacro("${A.B}", m));
        // test that more complex scenarios work
        Assert.assertEquals("/a/B/aa", Util.replaceMacro("/$A/$B/$AA", m));
        Assert.assertEquals("a-aa", Util.replaceMacro("$A-$AA", m));
        Assert.assertEquals("/a/foo/can/B/you-believe_aa~it?", Util.replaceMacro("/$A/foo/can/$B/you-believe_$AA~it?", m));
        Assert.assertEquals("$$aa$Ba${A}$it", Util.replaceMacro("$$$DOLLAR${AA}$$B${ENCLOSED}$it", m));
    }

    @Test
    public void testTimeSpanString() {
        // Check that amounts less than 365 days are not rounded up to a whole year.
        // In the previous implementation there were 360 days in a year.
        // We're still working on the assumption that a month is 30 days, so there will
        // be 5 days at the end of the year that will be "12 months" but not "1 year".
        // First check 359 days.
        Assert.assertEquals(Messages.Util_month(11), Util.getTimeSpanString(31017600000L));
        // And 362 days.
        Assert.assertEquals(Messages.Util_month(12), Util.getTimeSpanString(31276800000L));
        // 11.25 years - Check that if the first unit has 2 or more digits, a second unit isn't used.
        Assert.assertEquals(Messages.Util_year(11), Util.getTimeSpanString(354780000000L));
        // 9.25 years - Check that if the first unit has only 1 digit, a second unit is used.
        Assert.assertEquals((((Messages.Util_year(9)) + " ") + (Messages.Util_month(3))), Util.getTimeSpanString(291708000000L));
        // 67 seconds
        Assert.assertEquals((((Messages.Util_minute(1)) + " ") + (Messages.Util_second(7))), Util.getTimeSpanString(67000L));
        // 17 seconds - Check that times less than a minute only use seconds.
        Assert.assertEquals(Messages.Util_second(17), Util.getTimeSpanString(17000L));
        // 1712ms -> 1.7sec
        Assert.assertEquals(Messages.Util_second(1.7), Util.getTimeSpanString(1712L));
        // 171ms -> 0.17sec
        Assert.assertEquals(Messages.Util_second(0.17), Util.getTimeSpanString(171L));
        // 101ms -> 0.10sec
        Assert.assertEquals(Messages.Util_second(0.1), Util.getTimeSpanString(101L));
        // 17ms
        Assert.assertEquals(Messages.Util_millisecond(17), Util.getTimeSpanString(17L));
        // 1ms
        Assert.assertEquals(Messages.Util_millisecond(1), Util.getTimeSpanString(1L));
        // Test HUDSON-2843 (locale with comma as fraction separator got exception for <10 sec)
        Locale saveLocale = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);
        try {
            // Just verifying no exception is thrown:
            Assert.assertNotNull("German locale", Util.getTimeSpanString(1234));
            Assert.assertNotNull("German locale <1 sec", Util.getTimeSpanString(123));
        } finally {
            Locale.setDefault(saveLocale);
        }
    }

    /**
     * Test that Strings that contain spaces are correctly URL encoded.
     */
    @Test
    public void testEncodeSpaces() {
        final String urlWithSpaces = "http://hudson/job/Hudson Job";
        String encoded = Util.encode(urlWithSpaces);
        Assert.assertEquals(encoded, "http://hudson/job/Hudson%20Job");
    }

    /**
     * Test the rawEncode() method.
     */
    @Test
    public void testRawEncode() {
        String[] data = new String[]{ // Alternating raw,encoded
        "abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "01234567890!@$&*()-_=+',.", "01234567890!@$&*()-_=+',.", " \"#%/:;<>?", "%20%22%23%25%2F%3A%3B%3C%3E%3F", "[\\]^`{|}~", "%5B%5C%5D%5E%60%7B%7C%7D%7E", "d\u00e9velopp\u00e9s", "d%C3%A9velopp%C3%A9s" };
        for (int i = 0; i < (data.length); i += 2) {
            Assert.assertEquals(("test " + i), data[(i + 1)], Util.rawEncode(data[i]));
        }
    }

    /**
     * Test the tryParseNumber() method.
     */
    @Test
    public void testTryParseNumber() {
        Assert.assertEquals("Successful parse did not return the parsed value", 20, Util.tryParseNumber("20", 10).intValue());
        Assert.assertEquals("Failed parse did not return the default value", 10, Util.tryParseNumber("ss", 10).intValue());
        Assert.assertEquals("Parsing empty string did not return the default value", 10, Util.tryParseNumber("", 10).intValue());
        Assert.assertEquals("Parsing null string did not return the default value", 10, Util.tryParseNumber(null, 10).intValue());
    }

    @Test
    public void testSymlink() throws Exception {
        Assume.assumeFalse(Functions.isWindows());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamTaskListener l = new StreamTaskListener(baos);
        File d = tmp.getRoot();
        try {
            new FilePath(new File(d, "a")).touch(0);
            Assert.assertNull(Util.resolveSymlink(new File(d, "a")));
            Util.createSymlink(d, "a", "x", l);
            Assert.assertEquals("a", Util.resolveSymlink(new File(d, "x")));
            // test a long name
            StringBuilder buf = new StringBuilder(768);
            for (int i = 0; i < 768; i++)
                buf.append(((char) ('0' + (i % 10))));

            Util.createSymlink(d, buf.toString(), "x", l);
            String log = baos.toString();
            if ((log.length()) > 0)
                System.err.println(("log output: " + log));

            Assert.assertEquals(buf.toString(), Util.resolveSymlink(new File(d, "x")));
            // test linking from another directory
            File anotherDir = new File(d, "anotherDir");
            Assert.assertTrue(("Couldn't create " + anotherDir), anotherDir.mkdir());
            Util.createSymlink(d, "a", "anotherDir/link", l);
            Assert.assertEquals("a", Util.resolveSymlink(new File(d, "anotherDir/link")));
            // JENKINS-12331: either a bug in createSymlink or this isn't supposed to work:
            // assertTrue(Util.isSymlink(new File(d,"anotherDir/link")));
            File external = File.createTempFile("something", "");
            try {
                Util.createSymlink(d, external.getAbsolutePath(), "outside", l);
                Assert.assertEquals(external.getAbsolutePath(), Util.resolveSymlink(new File(d, "outside")));
            } finally {
                Assert.assertTrue(external.delete());
            }
        } finally {
            Util.deleteRecursive(d);
        }
    }

    @Test
    public void testIsSymlink() throws IOException, InterruptedException {
        Assume.assumeFalse(Functions.isWindows());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamTaskListener l = new StreamTaskListener(baos);
        File d = tmp.getRoot();
        try {
            new FilePath(new File(d, "original")).touch(0);
            Assert.assertFalse(Util.isSymlink(new File(d, "original")));
            Util.createSymlink(d, "original", "link", l);
            Assert.assertTrue(Util.isSymlink(new File(d, "link")));
            // test linking to another directory
            File dir = new File(d, "dir");
            Assert.assertTrue(("Couldn't create " + dir), dir.mkdir());
            Assert.assertFalse(Util.isSymlink(new File(d, "dir")));
            File anotherDir = new File(d, "anotherDir");
            Assert.assertTrue(("Couldn't create " + anotherDir), anotherDir.mkdir());
            Util.createSymlink(d, "dir", "anotherDir/symlinkDir", l);
            // JENKINS-12331: either a bug in createSymlink or this isn't supposed to work:
            // assertTrue(Util.isSymlink(new File(d,"anotherDir/symlinkDir")));
        } finally {
            Util.deleteRecursive(d);
        }
    }

    @Test
    public void testIsSymlink_onWindows_junction() throws Exception {
        Assume.assumeTrue("Uses Windows-specific features", Functions.isWindows());
        File targetDir = tmp.newFolder("targetDir");
        File d = tmp.newFolder("dir");
        File junction = WindowsUtil.createJunction(new File(d, "junction"), targetDir);
        Assert.assertTrue(Util.isSymlink(junction));
    }

    @Test
    @Issue("JENKINS-55448")
    public void testIsSymlink_ParentIsJunction() throws IOException, InterruptedException {
        Assume.assumeTrue("Uses Windows-specific features", Functions.isWindows());
        File targetDir = tmp.newFolder();
        File file = new File(targetDir, "test-file");
        new FilePath(file).touch(System.currentTimeMillis());
        File dir = tmp.newFolder();
        File junction = WindowsUtil.createJunction(new File(dir, "junction"), targetDir);
        Assert.assertTrue(Util.isSymlink(junction));
        Assert.assertFalse(Util.isSymlink(file));
    }

    @Test
    @Issue("JENKINS-55448")
    public void testIsSymlink_ParentIsSymlink() throws IOException, InterruptedException {
        File folder = tmp.newFolder();
        File file = new File(folder, "test-file");
        new FilePath(file).touch(System.currentTimeMillis());
        Path link = tmp.getRoot().toPath().resolve("sym-link");
        Path pathWithSymlinkParent = Files.createSymbolicLink(link, folder.toPath()).resolve("test-file");
        Assert.assertTrue(Util.isSymlink(link));
        Assert.assertFalse(Util.isSymlink(pathWithSymlinkParent));
    }

    @Test
    public void testHtmlEscape() {
        Assert.assertEquals("<br>", Util.escape("\n"));
        Assert.assertEquals("&lt;a&gt;", Util.escape("<a>"));
        Assert.assertEquals("&#039;&quot;", Util.escape("\'\""));
        Assert.assertEquals("&nbsp; ", Util.escape("  "));
    }

    /**
     * Compute 'known-correct' digests and see if I still get them when computed concurrently
     * to another digest.
     */
    @Issue("JENKINS-10346")
    @Test
    public void testDigestThreadSafety() throws InterruptedException {
        String a = "abcdefgh";
        String b = "123456789";
        String digestA = Util.getDigestOf(a);
        String digestB = Util.getDigestOf(b);
        UtilTest.DigesterThread t1 = new UtilTest.DigesterThread(a, digestA);
        UtilTest.DigesterThread t2 = new UtilTest.DigesterThread(b, digestB);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        if ((t1.error) != null) {
            Assert.fail(t1.error);
        }
        if ((t2.error) != null) {
            Assert.fail(t2.error);
        }
    }

    private static class DigesterThread extends Thread {
        private String string;

        private String expectedDigest;

        private String error;

        public DigesterThread(String string, String expectedDigest) {
            this.string = string;
            this.expectedDigest = expectedDigest;
        }

        public void run() {
            for (int i = 0; i < 1000; i++) {
                String digest = Util.getDigestOf(this.string);
                if (!(this.expectedDigest.equals(digest))) {
                    this.error = (("Expected " + (this.expectedDigest)) + ", but got ") + digest;
                    break;
                }
            }
        }
    }

    @Test
    public void testIsAbsoluteUri() {
        Assert.assertTrue(Util.isAbsoluteUri("http://foobar/"));
        Assert.assertTrue(Util.isAbsoluteUri("mailto:kk@kohsuke.org"));
        Assert.assertTrue(Util.isAbsoluteUri("d123://test/"));
        Assert.assertFalse(Util.isAbsoluteUri("foo/bar/abc:def"));
        Assert.assertFalse(Util.isAbsoluteUri("foo?abc:def"));
        Assert.assertFalse(Util.isAbsoluteUri("foo#abc:def"));
        Assert.assertFalse(Util.isAbsoluteUri("foo/bar"));
    }

    @Test
    @Issue("SECURITY-276")
    public void testIsSafeToRedirectTo() {
        Assert.assertFalse(Util.isSafeToRedirectTo("http://foobar/"));
        Assert.assertFalse(Util.isSafeToRedirectTo("mailto:kk@kohsuke.org"));
        Assert.assertFalse(Util.isSafeToRedirectTo("d123://test/"));
        Assert.assertFalse(Util.isSafeToRedirectTo("//google.com"));
        Assert.assertTrue(Util.isSafeToRedirectTo("foo/bar/abc:def"));
        Assert.assertTrue(Util.isSafeToRedirectTo("foo?abc:def"));
        Assert.assertTrue(Util.isSafeToRedirectTo("foo#abc:def"));
        Assert.assertTrue(Util.isSafeToRedirectTo("foo/bar"));
        Assert.assertTrue(Util.isSafeToRedirectTo("/"));
        Assert.assertTrue(Util.isSafeToRedirectTo("/foo"));
        Assert.assertTrue(Util.isSafeToRedirectTo(".."));
        Assert.assertTrue(Util.isSafeToRedirectTo("../.."));
        Assert.assertTrue(Util.isSafeToRedirectTo("/#foo"));
        Assert.assertTrue(Util.isSafeToRedirectTo("/?foo"));
    }

    @Test
    public void loadProperties() throws IOException {
        Assert.assertEquals(0, Util.loadProperties("").size());
        Properties p = Util.loadProperties("k.e.y=va.l.ue");
        Assert.assertEquals(p.toString(), "va.l.ue", p.get("k.e.y"));
        Assert.assertEquals(p.toString(), 1, p.size());
    }

    @Test
    public void isRelativePathUnix() {
        Assert.assertThat("/", CoreMatchers.not(UtilTest.aRelativePath()));
        Assert.assertThat("/foo/bar", CoreMatchers.not(UtilTest.aRelativePath()));
        Assert.assertThat("/foo/../bar", CoreMatchers.not(UtilTest.aRelativePath()));
        Assert.assertThat("", UtilTest.aRelativePath());
        Assert.assertThat(".", UtilTest.aRelativePath());
        Assert.assertThat("..", UtilTest.aRelativePath());
        Assert.assertThat("./foo", UtilTest.aRelativePath());
        Assert.assertThat("./foo/bar", UtilTest.aRelativePath());
        Assert.assertThat("./foo/bar/", UtilTest.aRelativePath());
    }

    @Test
    public void isRelativePathWindows() {
        Assert.assertThat("\\", UtilTest.aRelativePath());
        Assert.assertThat("\\foo\\bar", UtilTest.aRelativePath());
        Assert.assertThat("\\foo\\..\\bar", UtilTest.aRelativePath());
        Assert.assertThat("", UtilTest.aRelativePath());
        Assert.assertThat(".", UtilTest.aRelativePath());
        Assert.assertThat(".\\foo", UtilTest.aRelativePath());
        Assert.assertThat(".\\foo\\bar", UtilTest.aRelativePath());
        Assert.assertThat(".\\foo\\bar\\", UtilTest.aRelativePath());
        Assert.assertThat("\\\\foo", UtilTest.aRelativePath());
        Assert.assertThat("\\\\foo\\", CoreMatchers.not(UtilTest.aRelativePath()));
        Assert.assertThat("\\\\foo\\c", CoreMatchers.not(UtilTest.aRelativePath()));
        Assert.assertThat("C:", UtilTest.aRelativePath());
        Assert.assertThat("z:", UtilTest.aRelativePath());
        Assert.assertThat("0:", UtilTest.aRelativePath());
        Assert.assertThat("c:.", UtilTest.aRelativePath());
        Assert.assertThat("c:\\", CoreMatchers.not(UtilTest.aRelativePath()));
        Assert.assertThat("c:/", CoreMatchers.not(UtilTest.aRelativePath()));
    }

    private static class RelativePathMatcher extends BaseMatcher<String> {
        @Override
        public boolean matches(Object item) {
            return Util.isRelativePath(((String) (item)));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a relative path");
        }
    }

    @Test
    public void testIsDescendant() throws IOException {
        File root;
        File other;
        if (Functions.isWindows()) {
            root = new File("C:\\Temp");
            other = new File("C:\\Windows");
        } else {
            root = new File("/tmp");
            other = new File("/usr");
        }
        Assert.assertTrue(Util.isDescendant(root, new File(root, "child")));
        Assert.assertTrue(Util.isDescendant(root, new File(new File(root, "child"), "grandchild")));
        Assert.assertFalse(Util.isDescendant(root, other));
        Assert.assertFalse(Util.isDescendant(root, new File(other, "child")));
        Assert.assertFalse(Util.isDescendant(new File(root, "child"), root));
        Assert.assertFalse(Util.isDescendant(new File(new File(root, "child"), "grandchild"), root));
        // .. whithin root
        File convoluted = new File(root, "child");
        convoluted = new File(convoluted, "..");
        convoluted = new File(convoluted, "child");
        Assert.assertTrue(Util.isDescendant(root, convoluted));
        // .. going outside of root
        convoluted = new File(root, "..");
        convoluted = new File(convoluted, other.getName());
        convoluted = new File(convoluted, "child");
        Assert.assertFalse(Util.isDescendant(root, convoluted));
        // . on root
        Assert.assertTrue(Util.isDescendant(new File(root, "."), new File(root, "child")));
        // . on both
        Assert.assertTrue(Util.isDescendant(new File(root, "."), new File(new File(root, "child"), ".")));
    }

    @Test
    public void testModeToPermissions() throws Exception {
        Assert.assertEquals(PosixFilePermissions.fromString("rwxrwxrwx"), Util.modeToPermissions(511));
        Assert.assertEquals(PosixFilePermissions.fromString("rwxr-xrwx"), Util.modeToPermissions(495));
        Assert.assertEquals(PosixFilePermissions.fromString("rwxr-x---"), Util.modeToPermissions(488));
        Assert.assertEquals(PosixFilePermissions.fromString("r-xr-x---"), Util.modeToPermissions(360));
        Assert.assertEquals(PosixFilePermissions.fromString("r-xr-----"), Util.modeToPermissions(352));
        Assert.assertEquals(PosixFilePermissions.fromString("--xr-----"), Util.modeToPermissions(96));
        Assert.assertEquals(PosixFilePermissions.fromString("--xr---w-"), Util.modeToPermissions(98));
        Assert.assertEquals(PosixFilePermissions.fromString("--xr--rw-"), Util.modeToPermissions(102));
        Assert.assertEquals(PosixFilePermissions.fromString("-wxr--rw-"), Util.modeToPermissions(230));
        Assert.assertEquals(PosixFilePermissions.fromString("---------"), Util.modeToPermissions(0));
        Assert.assertEquals("Non-permission bits should be ignored", PosixFilePermissions.fromString("r-xr-----"), Util.modeToPermissions(33120));
        expectedException.expectMessage(CoreMatchers.startsWith("Invalid mode"));
        Util.modeToPermissions(1023);
    }

    @Test
    public void testPermissionsToMode() throws Exception {
        Assert.assertEquals(511, Util.permissionsToMode(PosixFilePermissions.fromString("rwxrwxrwx")));
        Assert.assertEquals(495, Util.permissionsToMode(PosixFilePermissions.fromString("rwxr-xrwx")));
        Assert.assertEquals(488, Util.permissionsToMode(PosixFilePermissions.fromString("rwxr-x---")));
        Assert.assertEquals(360, Util.permissionsToMode(PosixFilePermissions.fromString("r-xr-x---")));
        Assert.assertEquals(352, Util.permissionsToMode(PosixFilePermissions.fromString("r-xr-----")));
        Assert.assertEquals(96, Util.permissionsToMode(PosixFilePermissions.fromString("--xr-----")));
        Assert.assertEquals(98, Util.permissionsToMode(PosixFilePermissions.fromString("--xr---w-")));
        Assert.assertEquals(102, Util.permissionsToMode(PosixFilePermissions.fromString("--xr--rw-")));
        Assert.assertEquals(230, Util.permissionsToMode(PosixFilePermissions.fromString("-wxr--rw-")));
        Assert.assertEquals(0, Util.permissionsToMode(PosixFilePermissions.fromString("---------")));
    }

    @Test
    public void testDifferenceDays() throws Exception {
        Date may_6_10am = parseDate("2018-05-06 10:00:00");
        Date may_6_11pm55 = parseDate("2018-05-06 23:55:00");
        Date may_7_01am = parseDate("2018-05-07 01:00:00");
        Date may_7_11pm = parseDate("2018-05-07 11:00:00");
        Date may_8_08am = parseDate("2018-05-08 08:00:00");
        Date june_3_08am = parseDate("2018-06-03 08:00:00");
        Date june_9_08am = parseDate("2018-06-09 08:00:00");
        Date june_9_08am_nextYear = parseDate("2019-06-09 08:00:00");
        Assert.assertEquals(0, Util.daysBetween(may_6_10am, may_6_11pm55));
        Assert.assertEquals(1, Util.daysBetween(may_6_10am, may_7_01am));
        Assert.assertEquals(1, Util.daysBetween(may_6_11pm55, may_7_01am));
        Assert.assertEquals(2, Util.daysBetween(may_6_10am, may_8_08am));
        Assert.assertEquals(1, Util.daysBetween(may_7_11pm, may_8_08am));
        // larger scale
        Assert.assertEquals(28, Util.daysBetween(may_6_10am, june_3_08am));
        Assert.assertEquals(34, Util.daysBetween(may_6_10am, june_9_08am));
        Assert.assertEquals((365 + 34), Util.daysBetween(may_6_10am, june_9_08am_nextYear));
        // reverse order
        Assert.assertEquals((-1), Util.daysBetween(may_8_08am, may_7_11pm));
    }
}

