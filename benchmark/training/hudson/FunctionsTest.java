/**
 * The MIT License
 *
 * Copyright 2011, OHTAKE Tomohiro.
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


import hudson.model.Computer;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewGroup;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class FunctionsTest {
    @Test
    public void testGetActionUrl_absoluteUriWithAuthority() {
        String[] uris = new String[]{ "http://example.com/foo/bar", "https://example.com/foo/bar", "ftp://example.com/foo/bar", "svn+ssh://nobody@example.com/foo/bar" };
        for (String uri : uris) {
            String result = Functions.getActionUrl(null, FunctionsTest.createMockAction(uri));
            Assert.assertEquals(uri, result);
        }
    }

    @Test
    @Issue("JENKINS-7725")
    public void testGetActionUrl_absoluteUriWithoutAuthority() {
        String[] uris = new String[]{ "mailto:nobody@example.com", "mailto:nobody@example.com?subject=hello", "javascript:alert('hello')" };
        for (String uri : uris) {
            String result = Functions.getActionUrl(null, FunctionsTest.createMockAction(uri));
            Assert.assertEquals(uri, result);
        }
    }

    @Test
    @PrepareForTest(Stapler.class)
    public void testGetActionUrl_absolutePath() throws Exception {
        String contextPath = "/jenkins";
        StaplerRequest req = FunctionsTest.createMockRequest(contextPath);
        String[] paths = new String[]{ "/", "/foo/bar" };
        mockStatic(Stapler.class);
        when(Stapler.getCurrentRequest()).thenReturn(req);
        for (String path : paths) {
            String result = Functions.getActionUrl(null, FunctionsTest.createMockAction(path));
            Assert.assertEquals((contextPath + path), result);
        }
    }

    @Test
    @PrepareForTest(Stapler.class)
    public void testGetActionUrl_relativePath() throws Exception {
        String contextPath = "/jenkins";
        String itUrl = "iturl/";
        StaplerRequest req = FunctionsTest.createMockRequest(contextPath);
        String[] paths = new String[]{ "foo/bar", "./foo/bar", "../foo/bar" };
        mockStatic(Stapler.class);
        when(Stapler.getCurrentRequest()).thenReturn(req);
        for (String path : paths) {
            String result = Functions.getActionUrl(itUrl, FunctionsTest.createMockAction(path));
            Assert.assertEquals((((contextPath + "/") + itUrl) + path), result);
        }
    }

    @Test
    @PrepareForTest({ Stapler.class, Jenkins.class })
    public void testGetRelativeLinkTo_JobContainedInView() throws Exception {
        Jenkins j = createMockJenkins();
        ItemGroup parent = j;
        String contextPath = "/jenkins";
        StaplerRequest req = FunctionsTest.createMockRequest(contextPath);
        mockStatic(Stapler.class);
        when(Stapler.getCurrentRequest()).thenReturn(req);
        View view = mock(View.class);
        when(view.getOwner()).thenReturn(j);
        when(j.getItemGroup()).thenReturn(j);
        createMockAncestors(req, FunctionsTest.createAncestor(view, "."), FunctionsTest.createAncestor(j, "../.."));
        TopLevelItem i = createMockItem(parent, "job/i/");
        when(view.getItems()).thenReturn(Arrays.asList(i));
        String result = Functions.getRelativeLinkTo(i);
        Assert.assertEquals("job/i/", result);
    }

    @Test
    @PrepareForTest({ Stapler.class, Jenkins.class })
    public void testGetRelativeLinkTo_JobFromComputer() throws Exception {
        Jenkins j = createMockJenkins();
        ItemGroup parent = j;
        String contextPath = "/jenkins";
        StaplerRequest req = FunctionsTest.createMockRequest(contextPath);
        mockStatic(Stapler.class);
        when(Stapler.getCurrentRequest()).thenReturn(req);
        Computer computer = mock(Computer.class);
        createMockAncestors(req, FunctionsTest.createAncestor(computer, "."), FunctionsTest.createAncestor(j, "../.."));
        TopLevelItem i = createMockItem(parent, "job/i/");
        String result = Functions.getRelativeLinkTo(i);
        Assert.assertEquals("/jenkins/job/i/", result);
    }

    private interface TopLevelItemAndItemGroup<T extends TopLevelItem> extends ItemGroup<T> , TopLevelItem , ViewGroup {}

    @Test
    @PrepareForTest({ Stapler.class, Jenkins.class })
    public void testGetRelativeLinkTo_JobContainedInViewWithinItemGroup() throws Exception {
        Jenkins j = createMockJenkins();
        FunctionsTest.TopLevelItemAndItemGroup parent = mock(FunctionsTest.TopLevelItemAndItemGroup.class);
        when(parent.getShortUrl()).thenReturn("parent/");
        String contextPath = "/jenkins";
        StaplerRequest req = FunctionsTest.createMockRequest(contextPath);
        mockStatic(Stapler.class);
        when(Stapler.getCurrentRequest()).thenReturn(req);
        View view = mock(View.class);
        when(view.getOwner()).thenReturn(parent);
        when(parent.getItemGroup()).thenReturn(parent);
        createMockAncestors(req, FunctionsTest.createAncestor(j, "../../.."), FunctionsTest.createAncestor(parent, "../.."), FunctionsTest.createAncestor(view, "."));
        TopLevelItem i = createMockItem(parent, "job/i/", "parent/job/i/");
        when(view.getItems()).thenReturn(Arrays.asList(i));
        String result = Functions.getRelativeLinkTo(i);
        Assert.assertEquals("job/i/", result);
    }

    @Issue("JENKINS-17713")
    @PrepareForTest({ Stapler.class, Jenkins.class })
    @Test
    public void getRelativeLinkTo_MavenModules() throws Exception {
        Jenkins j = createMockJenkins();
        StaplerRequest req = FunctionsTest.createMockRequest("/jenkins");
        mockStatic(Stapler.class);
        when(Stapler.getCurrentRequest()).thenReturn(req);
        FunctionsTest.TopLevelItemAndItemGroup ms = mock(FunctionsTest.TopLevelItemAndItemGroup.class);
        when(ms.getShortUrl()).thenReturn("job/ms/");
        // TODO "." (in second ancestor) is what Stapler currently fails to do. Could edit test to use ".." but set a different request path?
        createMockAncestors(req, FunctionsTest.createAncestor(j, "../.."), FunctionsTest.createAncestor(ms, "."));
        Item m = mock(Item.class);
        when(m.getParent()).thenReturn(ms);
        when(m.getShortUrl()).thenReturn("grp$art/");
        Assert.assertEquals("grp$art/", Functions.getRelativeLinkTo(m));
    }

    @Test
    public void testGetRelativeDisplayName() {
        Item i = mock(Item.class);
        when(i.getName()).thenReturn("jobName");
        when(i.getFullDisplayName()).thenReturn("displayName");
        Assert.assertEquals("displayName", Functions.getRelativeDisplayNameFrom(i, null));
    }

    @Test
    public void testGetRelativeDisplayNameInsideItemGroup() {
        Item i = mock(Item.class);
        when(i.getName()).thenReturn("jobName");
        when(i.getDisplayName()).thenReturn("displayName");
        FunctionsTest.TopLevelItemAndItemGroup ig = mock(FunctionsTest.TopLevelItemAndItemGroup.class);
        ItemGroup j = mock(Jenkins.class);
        when(ig.getName()).thenReturn("parent");
        when(ig.getDisplayName()).thenReturn("parentDisplay");
        when(ig.getParent()).thenReturn(j);
        when(i.getParent()).thenReturn(ig);
        Item i2 = mock(Item.class);
        when(i2.getDisplayName()).thenReturn("top");
        when(i2.getParent()).thenReturn(j);
        Assert.assertEquals("displayName", Functions.getRelativeDisplayNameFrom(i, ig));
        Assert.assertEquals("parentDisplay ? displayName", Functions.getRelativeDisplayNameFrom(i, j));
        Assert.assertEquals(".. ? top", Functions.getRelativeDisplayNameFrom(i2, ig));
    }

    @Test
    @PrepareForTest(Stapler.class)
    public void testGetActionUrl_unparseable() throws Exception {
        Assert.assertEquals(null, Functions.getActionUrl(null, FunctionsTest.createMockAction("http://nowhere.net/stuff?something=^woohoo")));
    }

    @Test
    @Issue("JENKINS-16630")
    public void testHumanReadableFileSize() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            Assert.assertEquals("0 B", Functions.humanReadableByteSize(0));
            Assert.assertEquals("1023 B", Functions.humanReadableByteSize(1023));
            Assert.assertEquals("1.00 KB", Functions.humanReadableByteSize(1024));
            Assert.assertEquals("1.50 KB", Functions.humanReadableByteSize(1536));
            Assert.assertEquals("20.00 KB", Functions.humanReadableByteSize(20480));
            Assert.assertEquals("1023.00 KB", Functions.humanReadableByteSize(1047552));
            Assert.assertEquals("1.00 MB", Functions.humanReadableByteSize(1048576));
            Assert.assertEquals("1.50 GB", Functions.humanReadableByteSize(1610612700));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Issue("JENKINS-17030")
    @Test
    public void testBreakableString() {
        assertBrokenAs("Hello world!", "Hello world!");
        assertBrokenAs("Hello-world!", "Hello", "-world!");
        assertBrokenAs("ALongStringThatCanNotBeBrokenByDefaultAndNeedsToUseTheBreakableElement", "ALongStringThatCanNo", "tBeBrokenByDefaultAn", "dNeedsToUseTheBreaka", "bleElement");
        assertBrokenAs("DontBreakShortStringBefore-Hyphen", "DontBreakShortStringBefore", "-Hyphen");
        assertBrokenAs("jenkins_main_trunk", "jenkins", "_main", "_trunk");
        assertBrokenAs("&lt;&lt;&lt;&lt;&lt;", "", "&lt;", "&lt;", "&lt;", "&lt;", "&lt;");
        assertBrokenAs("&amp;&amp;&amp;&amp;&amp;", "", "&amp;", "&amp;", "&amp;", "&amp;", "&amp;");
        assertBrokenAs("&thetasym;&thetasym;&thetasym;", "", "&thetasym;", "&thetasym;", "&thetasym;");
        assertBrokenAs("Crazy &lt;ha ha&gt;", "Crazy ", "&lt;ha ha", "&gt;");
        assertBrokenAs("A;String>Full]Of)Weird}Punctuation", "A;String", ">Full", "]Of", ")Weird", "}Punctuation");
        assertBrokenAs("&lt;&lt;a&lt;bc&lt;def&lt;ghi&lt;", "", "&lt;", "&lt;a", "&lt;bc", "&lt;def", "&lt;ghi", "&lt;");
        assertBrokenAs("H,e.l/l:o=w_o+|d", "H", ",e", ".l", "/l", ":o", "=w", "_o", "+|d");
        assertBrokenAs("a???a???a???a???a???a???a???a???", "a???a???a???a???a???", "a???a???a???");
        Assert.assertNull(Functions.breakableString(null));
    }

    @Issue("JENKINS-20800")
    @Test
    public void printLogRecordHtml() throws Exception {
        LogRecord lr = new LogRecord(Level.INFO, "Bad input <xml/>");
        lr.setLoggerName("test");
        Assert.assertEquals("Bad input &lt;xml/&gt;\n", Functions.printLogRecordHtml(lr, null)[3]);
    }

    @Issue("JDK-6507809")
    @Test
    public void printThrowable() throws Exception {
        // Basics: a single exception. No change.
        FunctionsTest.assertPrintThrowable(new FunctionsTest.Stack("java.lang.NullPointerException: oops", "p.C.method1:17", "m.Main.main:1"), ("java.lang.NullPointerException: oops\n" + ("\tat p.C.method1(C.java:17)\n" + "\tat m.Main.main(Main.java:1)\n")), ("java.lang.NullPointerException: oops\n" + ("\tat p.C.method1(C.java:17)\n" + "\tat m.Main.main(Main.java:1)\n")));
        // try {?} catch (Exception x) {throw new IllegalStateException(x);}
        FunctionsTest.assertPrintThrowable(new FunctionsTest.Stack("java.lang.IllegalStateException: java.lang.NullPointerException: oops", "p.C.method1:19", "m.Main.main:1").cause(new FunctionsTest.Stack("java.lang.NullPointerException: oops", "p.C.method2:23", "p.C.method1:17", "m.Main.main:1")), ("java.lang.IllegalStateException: java.lang.NullPointerException: oops\n" + ((((("\tat p.C.method1(C.java:19)\n" + "\tat m.Main.main(Main.java:1)\n") + "Caused by: java.lang.NullPointerException: oops\n") + "\tat p.C.method2(C.java:23)\n") + "\tat p.C.method1(C.java:17)\n") + "\t... 1 more\n")), ("java.lang.NullPointerException: oops\n" + (((("\tat p.C.method2(C.java:23)\n" + "\tat p.C.method1(C.java:17)\n") + "Caused: java.lang.IllegalStateException\n") + "\tat p.C.method1(C.java:19)\n") + "\tat m.Main.main(Main.java:1)\n")));
        // try {?} catch (Exception x) {throw new IllegalStateException("more info");}
        FunctionsTest.assertPrintThrowable(new FunctionsTest.Stack("java.lang.IllegalStateException: more info", "p.C.method1:19", "m.Main.main:1").cause(new FunctionsTest.Stack("java.lang.NullPointerException: oops", "p.C.method2:23", "p.C.method1:17", "m.Main.main:1")), ("java.lang.IllegalStateException: more info\n" + ((((("\tat p.C.method1(C.java:19)\n" + "\tat m.Main.main(Main.java:1)\n") + "Caused by: java.lang.NullPointerException: oops\n") + "\tat p.C.method2(C.java:23)\n") + "\tat p.C.method1(C.java:17)\n") + "\t... 1 more\n")), ("java.lang.NullPointerException: oops\n" + (((("\tat p.C.method2(C.java:23)\n" + "\tat p.C.method1(C.java:17)\n") + "Caused: java.lang.IllegalStateException: more info\n") + "\tat p.C.method1(C.java:19)\n") + "\tat m.Main.main(Main.java:1)\n")));
        // try {?} catch (Exception x) {throw new IllegalStateException("more info: " + x);}
        FunctionsTest.assertPrintThrowable(new FunctionsTest.Stack("java.lang.IllegalStateException: more info: java.lang.NullPointerException: oops", "p.C.method1:19", "m.Main.main:1").cause(new FunctionsTest.Stack("java.lang.NullPointerException: oops", "p.C.method2:23", "p.C.method1:17", "m.Main.main:1")), ("java.lang.IllegalStateException: more info: java.lang.NullPointerException: oops\n" + ((((("\tat p.C.method1(C.java:19)\n" + "\tat m.Main.main(Main.java:1)\n") + "Caused by: java.lang.NullPointerException: oops\n") + "\tat p.C.method2(C.java:23)\n") + "\tat p.C.method1(C.java:17)\n") + "\t... 1 more\n")), ("java.lang.NullPointerException: oops\n" + (((("\tat p.C.method2(C.java:23)\n" + "\tat p.C.method1(C.java:17)\n") + "Caused: java.lang.IllegalStateException: more info\n") + "\tat p.C.method1(C.java:19)\n") + "\tat m.Main.main(Main.java:1)\n")));
        // Synthetic stack showing an exception made elsewhere, such as happens with hudson.remoting.Channel.attachCallSiteStackTrace.
        Throwable t = new FunctionsTest.Stack("remote.Exception: oops", "remote.Place.method:17", "remote.Service.run:9");
        StackTraceElement[] callSite = new FunctionsTest.Stack("wrapped.Exception", "local.Side.call:11", "local.Main.main:1").getStackTrace();
        StackTraceElement[] original = t.getStackTrace();
        StackTraceElement[] combined = new StackTraceElement[((original.length) + 1) + (callSite.length)];
        System.arraycopy(original, 0, combined, 0, original.length);
        combined[original.length] = new StackTraceElement(".....", "remote call", null, (-2));
        System.arraycopy(callSite, 0, combined, ((original.length) + 1), callSite.length);
        t.setStackTrace(combined);
        FunctionsTest.assertPrintThrowable(t, ("remote.Exception: oops\n" + (((("\tat remote.Place.method(Place.java:17)\n" + "\tat remote.Service.run(Service.java:9)\n") + "\tat ......remote call(Native Method)\n") + "\tat local.Side.call(Side.java:11)\n") + "\tat local.Main.main(Main.java:1)\n")), ("remote.Exception: oops\n" + (((("\tat remote.Place.method(Place.java:17)\n" + "\tat remote.Service.run(Service.java:9)\n") + "\tat ......remote call(Native Method)\n") + "\tat local.Side.call(Side.java:11)\n") + "\tat local.Main.main(Main.java:1)\n")));
        // Same but now using a cause on the remote side.
        t = new FunctionsTest.Stack("remote.Wrapper: remote.Exception: oops", "remote.Place.method2:19", "remote.Service.run:9").cause(new FunctionsTest.Stack("remote.Exception: oops", "remote.Place.method1:11", "remote.Place.method2:17", "remote.Service.run:9"));
        callSite = new FunctionsTest.Stack("wrapped.Exception", "local.Side.call:11", "local.Main.main:1").getStackTrace();
        original = t.getStackTrace();
        combined = new StackTraceElement[((original.length) + 1) + (callSite.length)];
        System.arraycopy(original, 0, combined, 0, original.length);
        combined[original.length] = new StackTraceElement(".....", "remote call", null, (-2));
        System.arraycopy(callSite, 0, combined, ((original.length) + 1), callSite.length);
        t.setStackTrace(combined);
        FunctionsTest.assertPrintThrowable(t, ("remote.Wrapper: remote.Exception: oops\n" + (((((((("\tat remote.Place.method2(Place.java:19)\n" + "\tat remote.Service.run(Service.java:9)\n") + "\tat ......remote call(Native Method)\n") + "\tat local.Side.call(Side.java:11)\n") + "\tat local.Main.main(Main.java:1)\n") + "Caused by: remote.Exception: oops\n") + "\tat remote.Place.method1(Place.java:11)\n") + "\tat remote.Place.method2(Place.java:17)\n") + "\tat remote.Service.run(Service.java:9)\n")), ("remote.Exception: oops\n" + (((((((("\tat remote.Place.method1(Place.java:11)\n" + "\tat remote.Place.method2(Place.java:17)\n") + "\tat remote.Service.run(Service.java:9)\n")// we do not know how to elide the common part in this case
         + "Caused: remote.Wrapper\n") + "\tat remote.Place.method2(Place.java:19)\n") + "\tat remote.Service.run(Service.java:9)\n") + "\tat ......remote call(Native Method)\n") + "\tat local.Side.call(Side.java:11)\n") + "\tat local.Main.main(Main.java:1)\n")));
        // Suppressed exceptions:
        FunctionsTest.assertPrintThrowable(new FunctionsTest.Stack("java.lang.IllegalStateException: java.lang.NullPointerException: oops", "p.C.method1:19", "m.Main.main:1").cause(new FunctionsTest.Stack("java.lang.NullPointerException: oops", "p.C.method2:23", "p.C.method1:17", "m.Main.main:1")).suppressed(new FunctionsTest.Stack("java.io.IOException: could not close", "p.C.close:99", "p.C.method1:18", "m.Main.main:1"), new FunctionsTest.Stack("java.io.IOException: java.lang.NullPointerException", "p.C.flush:77", "p.C.method1:18", "m.Main.main:1").cause(new FunctionsTest.Stack("java.lang.NullPointerException", "p.C.findFlushee:70", "p.C.flush:75", "p.C.method1:18", "m.Main.main:1"))), ("java.lang.IllegalStateException: java.lang.NullPointerException: oops\n" + ((((((((((((((((("\tat p.C.method1(C.java:19)\n" + "\tat m.Main.main(Main.java:1)\n") + "\tSuppressed: java.io.IOException: could not close\n") + "\t\tat p.C.close(C.java:99)\n") + "\t\tat p.C.method1(C.java:18)\n") + "\t\t... 1 more\n") + "\tSuppressed: java.io.IOException: java.lang.NullPointerException\n") + "\t\tat p.C.flush(C.java:77)\n") + "\t\tat p.C.method1(C.java:18)\n") + "\t\t... 1 more\n") + "\tCaused by: java.lang.NullPointerException\n") + "\t\tat p.C.findFlushee(C.java:70)\n") + "\t\tat p.C.flush(C.java:75)\n") + "\t\t... 2 more\n") + "Caused by: java.lang.NullPointerException: oops\n") + "\tat p.C.method2(C.java:23)\n") + "\tat p.C.method1(C.java:17)\n") + "\t... 1 more\n")), ("java.lang.NullPointerException: oops\n" + ((((((((((((("\tat p.C.method2(C.java:23)\n" + "\tat p.C.method1(C.java:17)\n") + "Also:   java.io.IOException: could not close\n") + "\t\tat p.C.close(C.java:99)\n") + "\t\tat p.C.method1(C.java:18)\n") + "Also:   java.lang.NullPointerException\n") + "\t\tat p.C.findFlushee(C.java:70)\n") + "\t\tat p.C.flush(C.java:75)\n") + "\tCaused: java.io.IOException\n") + "\t\tat p.C.flush(C.java:77)\n") + "\t\tat p.C.method1(C.java:18)\n") + "Caused: java.lang.IllegalStateException\n") + "\tat p.C.method1(C.java:19)\n") + "\tat m.Main.main(Main.java:1)\n")));
        // Custom printStackTrace implementations:
        FunctionsTest.assertPrintThrowable(new Throwable() {
            @Override
            public void printStackTrace(PrintWriter s) {
                s.println("Some custom exception");
            }
        }, "Some custom exception\n", "Some custom exception\n");
        // Circular references:
        FunctionsTest.Stack stack1 = new FunctionsTest.Stack("p.Exc1", "p.C.method1:17");
        FunctionsTest.Stack stack2 = new FunctionsTest.Stack("p.Exc2", "p.C.method2:27");
        stack1.cause(stack2);
        stack2.cause(stack1);
        FunctionsTest.assertPrintThrowable(stack1, ("p.Exc1\n" + ((("\tat p.C.method1(C.java:17)\n" + "Caused by: p.Exc2\n") + "\tat p.C.method2(C.java:27)\n") + "\t[CIRCULAR REFERENCE:p.Exc1]\n")), ("<cycle to p.Exc1>\n" + ((("Caused: p.Exc2\n" + "\tat p.C.method2(C.java:27)\n") + "Caused: p.Exc1\n") + "\tat p.C.method1(C.java:17)\n")));
    }

    private static final class Stack extends Throwable {
        private static final Pattern LINE = Pattern.compile("(.+)[.](.+)[.](.+):(\\d+)");

        private final String toString;

        Stack(String toString, String... stack) {
            this.toString = toString;
            StackTraceElement[] lines = new StackTraceElement[stack.length];
            for (int i = 0; i < (stack.length); i++) {
                Matcher m = FunctionsTest.Stack.LINE.matcher(stack[i]);
                Assert.assertTrue(m.matches());
                lines[i] = new StackTraceElement((((m.group(1)) + ".") + (m.group(2))), m.group(3), ((m.group(2)) + ".java"), Integer.parseInt(m.group(4)));
            }
            setStackTrace(lines);
        }

        @Override
        public String toString() {
            return toString;
        }

        synchronized FunctionsTest.Stack cause(Throwable cause) {
            return ((FunctionsTest.Stack) (initCause(cause)));
        }

        synchronized FunctionsTest.Stack suppressed(Throwable... suppressed) {
            for (Throwable t : suppressed) {
                addSuppressed(t);
            }
            return this;
        }
    }
}

