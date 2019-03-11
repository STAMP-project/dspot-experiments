/**
 * Capsule
 * Copyright (c) 2014-2015, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are licensed under the terms
 * of the Eclipse Public License v1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */


import co.paralleluniverse.capsule.Jar;
import co.paralleluniverse.capsule.test.CapsuleTestUtils.StringPrintStream;
import co.paralleluniverse.common.ZipFS;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarInputStream;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


// import static org.mockito.Mockito.*;
// </editor-fold>
public class CapsuleTest {
    /* As a general rule, we prefer system tests, and only create unit tests for particular methods that,
    while tested for integration, whose arguments don't get enough coverage in the system tests (like parsing methods and the like).

    All the tests in this test suite use an in-memory file system, and don't write to the disk at all.
     */
    private final FileSystem fs = Jimfs.newFileSystem(Configuration.unix());

    private final Path cache = fs.getPath("/cache");

    private final Path tmp = fs.getPath("/tmp");

    private static final ClassLoader MY_CLASSLOADER = Capsule.class.getClassLoader();

    private Properties props;

    // <editor-fold desc="System Tests">
    // ///////// System Tests ///////////////////////////////////
    @Test
    public void testSimpleExtract() throws Exception {
        Jar jar = // test with Windows path
        newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").addEntry("foo.jar", emptyInputStream()).addEntry("a.class", emptyInputStream()).addEntry("b.txt", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.class", emptyInputStream()).addEntry("q/w/x.txt", emptyInputStream()).addEntry("d\\f\\y.txt", emptyInputStream()).addEntry("META-INF/x.txt", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        // dumpFileSystem(fs);
        Assert.assertEquals(args, getAppArgs(pb));
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertEquals("com.acme.Foo", getProperty(pb, "capsule.app"));
        Assert.assertEquals("com.acme.Foo", getEnv(pb, "CAPSULE_APP"));
        Assert.assertEquals(appCache, path(getProperty(pb, "capsule.dir")));
        Assert.assertEquals(absolutePath("capsule.jar"), path(getProperty(pb, "capsule.jar")));
        Assert.assertEquals(appCache, path(getEnv(pb, "CAPSULE_DIR")));
        Assert.assertEquals(absolutePath("capsule.jar"), path(getEnv(pb, "CAPSULE_JAR")));
        Assert.assertEquals(CapsuleTest.list("com.acme.Foo", "hi", "there"), CapsuleTest.getMainAndArgs(pb));
        Assert.assertTrue(Files.isDirectory(cache));
        Assert.assertTrue(Files.isDirectory(cache.resolve("apps")));
        Assert.assertTrue(Files.isDirectory(appCache));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve(".extracted")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("foo.jar")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("b.txt")));
        Assert.assertTrue(Files.isDirectory(appCache.resolve("lib")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("lib").resolve("a.jar")));
        Assert.assertTrue((!(Files.isRegularFile(appCache.resolve("a.class")))));
        Assert.assertTrue((!(Files.isRegularFile(appCache.resolve("lib").resolve("b.class")))));
        Assert.assertTrue((!(Files.isDirectory(appCache.resolve("META-INF")))));
        Assert.assertTrue((!(Files.isRegularFile(appCache.resolve("META-INF").resolve("x.txt")))));
        Assert.assertTrue(Files.isDirectory(appCache.resolve("q").resolve("w")));
        Assert.assertTrue(Files.isDirectory(appCache.resolve("d").resolve("f")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("q").resolve("w").resolve("x.txt")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("d").resolve("f").resolve("y.txt")));
        // assert_().that(getClassPath(pb)).has().item(absolutePath("capsule.jar"));
        assert_().that(getClassPath(pb)).has().item(appCache.resolve("foo.jar"));
        assert_().that(getClassPath(pb)).has().noneOf(appCache.resolve("lib").resolve("a.jar"));
    }

    @Test
    public void testNoExtract() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").addEntry("foo.txt", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals(args, getAppArgs(pb));
        assert_().that(getClassPath(pb)).has().item(absolutePath("capsule.jar"));
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertTrue((!(Files.isDirectory(appCache))));
    }

    @Test
    public void testJDKClassPath() throws Exception {
        Assume.assumeTrue((!(isCI())));
        Jar jar = // .setAttribute("Extract-Capsule", "false")
        newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("JDK-Required", "true").setListAttribute("App-Class-Path", CapsuleTest.list("$JAVA_HOME/lib/tools.jar", "lib/*")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path javaHome = path(capsule.getJavaHome().toString());// different FS

        Assert.assertEquals(args, getAppArgs(pb));
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        final String h = javaHome.toString();
        assert_().that(((!(h.contains("jre"))) && ((h.contains("jdk")) || (Files.exists(javaHome.resolve("include").resolve("jni.h"))))));
        assert_().that(h).doesNotContain("jre");
        assert_().that(getClassPath(pb)).has().allOf(javaHome.resolve("lib/tools.jar"), appCache.resolve("foo.jar"), appCache.resolve("lib").resolve("a.jar"), appCache.resolve("lib").resolve("b.jar"));
    }

    @Test
    public void testLogLevel() throws Exception {
        setSTDERR(DEVNULL);
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Extract-Capsule", "false").setAttribute("Capsule-Log-Level", "verbose");
        newCapsule(jar);
        Assert.assertTrue(Capsule.isLogging(2));
        Assert.assertTrue((!(Capsule.isLogging(3))));
        props.setProperty("capsule.log", "none");
        newCapsule(jar);
        Assert.assertTrue(Capsule.isLogging(0));
        Assert.assertTrue((!(Capsule.isLogging(1))));
        props.setProperty("capsule.log", "quiet");
        newCapsule(jar);
        Assert.assertTrue(Capsule.isLogging(1));
        Assert.assertTrue((!(Capsule.isLogging(2))));
        props.setProperty("capsule.log", "");
        newCapsule(jar);
        Assert.assertTrue(Capsule.isLogging(1));
        Assert.assertTrue((!(Capsule.isLogging(2))));
        props.setProperty("capsule.log", "verbose");
        newCapsule(jar);
        Assert.assertTrue(Capsule.isLogging(2));
        Assert.assertTrue((!(Capsule.isLogging(3))));
        props.setProperty("capsule.log", "debug");
        newCapsule(jar);
        Assert.assertTrue(Capsule.isLogging(3));
    }

    @Test
    public void testCapsuleJavaHome() throws Exception {
        props.setProperty("capsule.java.home", "/my/1.7.0.jdk/home");
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Extract-Capsule", "false").addEntry("foo.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals(("/my/1.7.0.jdk/home/bin/java" + (Capsule.isWindows() ? ".exe" : "")), pb.command().get(0));
    }

    @Test
    public void testCapsuleJavaCmd() throws Exception {
        props.setProperty("capsule.java.cmd", "/my/java/home/gogo");
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Extract-Capsule", "false").addEntry("foo.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals("/my/java/home/gogo", pb.command().get(0));
    }

    @Test
    public void testClassPath() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("App-Class-Path", CapsuleTest.list("lib/a.jar", "lib/b.jar", "lib2/*.jar")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream()).addEntry("lib2/c.jar", emptyInputStream()).addEntry("lib2/d.jar", emptyInputStream()).addEntry("lib2/e.txt", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertTrue(Files.isDirectory(appCache.resolve("lib")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("lib").resolve("a.jar")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("lib2").resolve("c.jar")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("lib2").resolve("d.jar")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("lib2").resolve("e.txt")));
        // assert_().that(getClassPath(pb)).has().item(absolutePath("capsule.jar"));
        assert_().that(getClassPath(pb)).has().item(appCache.resolve("foo.jar"));
        assert_().that(getClassPath(pb)).has().item(appCache.resolve("lib").resolve("a.jar"));
        assert_().that(getClassPath(pb)).has().item(appCache.resolve("lib").resolve("b.jar"));
        assert_().that(getClassPath(pb)).has().item(appCache.resolve("lib2").resolve("c.jar"));
        assert_().that(getClassPath(pb)).has().item(appCache.resolve("lib2").resolve("d.jar"));
        assert_().that(getClassPath(pb)).has().noneOf(appCache.resolve("lib2").resolve("e.txt"));
    }

    @Test
    public void testNatives1() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Library-Path-A", CapsuleTest.list("lib/a.so")).setListAttribute("Library-Path-P", CapsuleTest.list("lib/b.so")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.so", emptyInputStream()).addEntry("lib/b.so", emptyInputStream()).addEntry("lib/c.jar", emptyInputStream()).addEntry("lib/d.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        int len = paths(getProperty(pb, "java.library.path")).size();
        assert_().that(paths(getProperty(pb, "java.library.path")).get(0)).isEqualTo(appCache.resolve("lib").resolve("b.so"));
        assert_().that(paths(getProperty(pb, "java.library.path")).get((len - 2))).isEqualTo(appCache.resolve("lib").resolve("a.so"));
        assert_().that(paths(getProperty(pb, "java.library.path")).get((len - 1))).isEqualTo(appCache);
    }

    @Test
    public void testNatives2() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Library-Path-A", CapsuleTest.list("lib/a.so")).setListAttribute("Library-Path-P", CapsuleTest.list("lib/b.so")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.so", emptyInputStream()).addEntry("lib/b.so", emptyInputStream()).addEntry("lib/c.jar", emptyInputStream()).addEntry("lib/d.jar", emptyInputStream());
        props.setProperty("java.library.path", "/foo/bar");
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(paths(getProperty(pb, "java.library.path"))).isEqualTo(CapsuleTest.list(appCache.resolve("lib").resolve("b.so"), path("/foo", "bar"), appCache.resolve("lib").resolve("a.so"), appCache));
    }

    @Test
    public void testNativesWithDeps() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Linux", "Native-Dependencies", CapsuleTest.list("com.acme:baz-linux:3.4=libbaz.so")).setListAttribute("Windows", "Native-Dependencies", CapsuleTest.list("com.acme:baz-win:3.4=libbaz.dll")).setListAttribute("MacOS", "Native-Dependencies", CapsuleTest.list("com.acme:baz-macos:3.4=libbaz.dylib")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.so", emptyInputStream()).addEntry("lib/b.so", emptyInputStream()).addEntry("lib/c.jar", emptyInputStream()).addEntry("lib/d.jar", emptyInputStream());
        Path bazLinuxPath = mockDep("com.acme:baz-linux:3.4", "so");
        Path bazWindowsPath = mockDep("com.acme:baz-win:3.4", "dll");
        Path bazMacPath = mockDep("com.acme:baz-macos:3.4", "dylib");
        Files.createDirectories(bazLinuxPath.getParent());
        Files.createFile(bazLinuxPath);
        Files.createFile(bazWindowsPath);
        Files.createFile(bazMacPath);
        props.setProperty("java.library.path", "/foo/bar");
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(paths(getProperty(pb, "java.library.path"))).has().item(appCache);
        if (Capsule.isUnix())
            Assert.assertTrue(Files.isRegularFile(appCache.resolve("libbaz.so")));
        else
            if (Capsule.isWindows())
                Assert.assertTrue(Files.isRegularFile(appCache.resolve("libbaz.dll")));
            else
                if (Capsule.isMac())
                    Assert.assertTrue(Files.isRegularFile(appCache.resolve("libbaz.dylib")));



    }

    @Test
    public void testBootClassPath1() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Boot-Class-Path-A", CapsuleTest.list("lib/a.jar")).setListAttribute("Boot-Class-Path-P", CapsuleTest.list("lib/b.jar")).setListAttribute("Boot-Class-Path", CapsuleTest.list("lib/c.jar", "lib/d.jar")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream()).addEntry("lib/c.jar", emptyInputStream()).addEntry("lib/d.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().item(appCache.resolve("lib").resolve("c.jar"));
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().item(appCache.resolve("lib").resolve("d.jar"));
        assert_().that(paths(getOption(pb, "-Xbootclasspath/a"))).isEqualTo(CapsuleTest.list(appCache.resolve("lib").resolve("a.jar")));
        assert_().that(paths(getOption(pb, "-Xbootclasspath/p"))).isEqualTo(CapsuleTest.list(appCache.resolve("lib").resolve("b.jar")));
    }

    @Test
    public void testBootClassPath2() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Boot-Class-Path-A", CapsuleTest.list("lib/a.jar")).setListAttribute("Boot-Class-Path-P", CapsuleTest.list("lib/b.jar")).setListAttribute("Boot-Class-Path", CapsuleTest.list("lib/c.jar", "lib/d.jar")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream()).addEntry("lib/c.jar", emptyInputStream()).addEntry("lib/d.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Xbootclasspath:/foo/bar");
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(getOption(pb, "-Xbootclasspath")).isEqualTo("/foo/bar");
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().noneOf(appCache.resolve("lib").resolve("c.jar"));
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().noneOf(appCache.resolve("lib").resolve("d.jar"));
        assert_().that(paths(getOption(pb, "-Xbootclasspath/a"))).isEqualTo(CapsuleTest.list(appCache.resolve("lib").resolve("a.jar")));
        assert_().that(paths(getOption(pb, "-Xbootclasspath/p"))).isEqualTo(CapsuleTest.list(appCache.resolve("lib").resolve("b.jar")));
    }

    @Test
    public void testBootClassPathWithDeps() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Boot-Class-Path-A", CapsuleTest.list("com.acme:baz:3.4")).setListAttribute("Boot-Class-Path-P", CapsuleTest.list("lib/b.jar")).setListAttribute("Boot-Class-Path", CapsuleTest.list("lib/c.jar", "com.acme:bar:1.2")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream()).addEntry("lib/c.jar", emptyInputStream());
        List<Path> barPath = mockDep("com.acme:bar:1.2", "jar", "com.acme:bar:1.2");
        List<Path> bazPath = mockDep("com.acme:baz:3.4", "jar", "com.acme:baz:3.4");
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().item(appCache.resolve("lib").resolve("c.jar"));
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().allFrom(barPath);
        assert_().that(paths(getOption(pb, "-Xbootclasspath/a"))).has().allFrom(bazPath);
        assert_().that(paths(getOption(pb, "-Xbootclasspath/p"))).isEqualTo(CapsuleTest.list(appCache.resolve("lib").resolve("b.jar")));
    }

    @Test
    public void testBootClassPathWithEmbeddedDeps() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Boot-Class-Path-P", CapsuleTest.list("lib/b.jar")).setListAttribute("Boot-Class-Path", CapsuleTest.list("lib/c.jar", "com.acme:bar:1.2")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream()).addEntry("lib/c.jar", emptyInputStream()).addEntry("bar-1.2.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().item(appCache.resolve("lib").resolve("c.jar"));
        assert_().that(paths(getOption(pb, "-Xbootclasspath"))).has().item(appCache.resolve("bar-1.2.jar"));
        assert_().that(paths(getOption(pb, "-Xbootclasspath/p"))).isEqualTo(CapsuleTest.list(appCache.resolve("lib").resolve("b.jar")));
    }

    @Test
    public void testDependencies1() throws Exception {
        List<String> deps = CapsuleTest.list("com.acme:bar:1.2", "com.acme:baz:3.4:jdk8");
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("Dependencies", deps).setListAttribute("App-Class-Path", CapsuleTest.list("com.acme:wat:5.8", "com.acme:woo")).addEntry("foo.jar", emptyInputStream());
        final List<Path> paths = new ArrayList<>();
        paths.add(mockDep("com.acme:bar:1.2", "jar"));
        paths.addAll(mockDep("com.acme:baz:3.4:jdk8", "jar", "com.google:guava:18.0"));
        paths.add(mockDep("com.acme:wat:5.8", "jar"));
        paths.addAll(mockDep("com.acme:woo", "jar", "org.apache:tomcat:8.0", "io.jetty:jetty:123.0"));
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        assert_().that(getClassPath(pb)).has().allFrom(paths);
    }

    @Test
    public void testCapsuleInClassPath() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setListAttribute("App-Class-Path", CapsuleTest.list("lib/a.jar", "lib/b.jar")).setAttribute("Capsule-In-Class-Path", "false").addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertTrue(Files.isDirectory(appCache.resolve("lib")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("lib").resolve("a.jar")));
        assert_().that(getClassPath(pb)).has().noneOf(absolutePath("capsule.jar"));
        assert_().that(getClassPath(pb)).has().allOf(appCache.resolve("foo.jar"), appCache.resolve("lib").resolve("a.jar"), appCache.resolve("lib").resolve("b.jar"));
    }

    @Test
    public void testSystemProperties() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "bar baz=33 foo=y").addEntry("foo.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Dfoo=x", "-Dzzz");
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals("x", getProperty(pb, "foo"));
        Assert.assertEquals("", getProperty(pb, "bar"));
        Assert.assertEquals("", getProperty(pb, "zzz"));
        Assert.assertEquals("33", getProperty(pb, "baz"));
    }

    @Test
    public void testPlatformSepcific() throws Exception {
        props.setProperty("capsule.java.home", "/my/1.8.0.jdk/home");
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Linux", "System-Properties", "bar baz=33 foo=y os=lin").setAttribute("MacOS", "System-Properties", "bar baz=33 foo=y os=mac").setAttribute("Windows", "System-Properties", "bar baz=33 foo=y os=win").setAttribute("Java-8", "System-Properties", "jjj=8").setAttribute("Java-7", "System-Properties", "jjj=7").addEntry("foo.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Dfoo=x", "-Dzzz");
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals("x", getProperty(pb, "foo"));
        Assert.assertEquals("", getProperty(pb, "bar"));
        Assert.assertEquals("", getProperty(pb, "zzz"));
        Assert.assertEquals("33", getProperty(pb, "baz"));
        Assert.assertEquals("8", getProperty(pb, "jjj"));
        if (Capsule.isWindows())
            Assert.assertEquals("win", getProperty(pb, "os"));
        else
            if (Capsule.isUnix())
                Assert.assertEquals("lin", getProperty(pb, "os"));


        if (Capsule.isMac())
            Assert.assertEquals("mac", getProperty(pb, "os"));

    }

    @Test
    public void testJVMArgs() throws Exception {
        props.setProperty("capsule.jvm.args", "-Xfoo500 -Xbar:120");
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("JVM-Args", "-Xmx100 -Xms10 -Xfoo400").addEntry("foo.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Xms15");
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertTrue(getJvmArgs(pb).contains("-Xmx100"));
        Assert.assertTrue(getJvmArgs(pb).contains("-Xms15"));
        Assert.assertTrue((!(getJvmArgs(pb).contains("-Xms10"))));
        Assert.assertTrue(getJvmArgs(pb).contains("-Xfoo500"));
        Assert.assertTrue(getJvmArgs(pb).contains("-Xbar:120"));
    }

    @Test
    public void testAgents() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Java-Agents", "ja1.jar ja2.jar=a=1,b=2 com.acme:bar=x=hi").setAttribute("Native-Agents", "na1=c=3,d=4 na2").addEntry("foo.jar", emptyInputStream());
        Path barPath = mockDep("com.acme:bar", "jar", "com.acme:bar:1.2").get(0);
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(getJvmArgs(pb)).has().allOf(("-javaagent:" + (appCache.resolve("ja1.jar"))));
        assert_().that(getJvmArgs(pb)).has().item((("-javaagent:" + (appCache.resolve("ja2.jar"))) + "=a=1,b=2"));
        assert_().that(getJvmArgs(pb)).has().item((("-javaagent:" + barPath) + "=x=hi"));
        assert_().that(getJvmArgs(pb)).has().item((("-agentpath:" + (appCache.resolve(("na1." + (Capsule.getNativeLibExtension()))))) + "=c=3,d=4"));
        assert_().that(getJvmArgs(pb)).has().item(("-agentpath:" + (appCache.resolve(("na2." + (Capsule.getNativeLibExtension()))))));
    }

    @Test
    public void testMode() throws Exception {
        props.setProperty("capsule.mode", "ModeX");
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "bar baz=33 foo=y").setAttribute("ModeX", "System-Properties", "bar baz=55 foo=w").setAttribute("ModeX", "Description", "This is a secret mode").setAttribute("ModeX-Linux", "System-Properties", "bar baz=55 foo=w os=lin").setAttribute("ModeX-MacOS", "System-Properties", "bar baz=55 foo=w os=mac").setAttribute("ModeX-Windows", "System-Properties", "bar baz=55 foo=w os=win").addEntry("foo.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Dfoo=x", "-Dzzz");
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals("x", getProperty(pb, "foo"));
        Assert.assertEquals("", getProperty(pb, "bar"));
        Assert.assertEquals("", getProperty(pb, "zzz"));
        Assert.assertEquals("55", getProperty(pb, "baz"));
        Assert.assertEquals(new HashSet<String>(CapsuleTest.list("ModeX")), capsule.getModes());
        Assert.assertEquals("This is a secret mode", capsule.getModeDescription("ModeX"));
        if (Capsule.isWindows())
            Assert.assertEquals("win", getProperty(pb, "os"));
        else
            if (Capsule.isUnix())
                Assert.assertEquals("lin", getProperty(pb, "os"));


        if (Capsule.isMac())
            Assert.assertEquals("mac", getProperty(pb, "os"));

    }

    @Test(expected = Exception.class)
    public void testMode2() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "bar baz=33 foo=y").setAttribute("ModeX", "Application-Class", "com.acme.Bar").addEntry("foo.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        newCapsule(jar).prepareForLaunch(cmdLine, args);
    }

    @Test
    public void testApplicationArtifact() throws Exception {
        Jar bar = new Jar().setAttribute("Main-Class", "com.acme.Bar").addEntry("com/acme/Bar.class", emptyInputStream());
        Path barPath = mockDep("com.acme:bar:1.2", "jar");
        Files.createDirectories(barPath.getParent());
        bar.write(barPath);
        Jar jar = newCapsuleJar().setAttribute("Application", "com.acme:bar:1.2");
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        assert_().that(getClassPath(pb)).has().item(barPath);
        Assert.assertEquals("com.acme.Bar", getMainClass(pb));
    }

    @Test
    public void testEmbeddedArtifact() throws Exception {
        Jar bar = new Jar().setAttribute("Main-Class", "com.acme.Bar").setAttribute("Class-Path", "lib/liba.jar lib/libb.jar").addEntry("com/acme/Bar.class", emptyInputStream());
        Jar jar = newCapsuleJar().setAttribute("Application", "bar.jar").setAttribute("Application-Name", "AcmeFoo").setAttribute("Application-Version", "1.0").addEntry("bar.jar", bar.toByteArray()).addEntry("lib/liba.jar", emptyInputStream()).addEntry("lib/libb.jar", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("AcmeFoo_1.0");
        assert_().that(getClassPath(pb)).has().item(appCache.resolve("bar.jar"));
        // assert_().that(getClassPath(pb)).has().item(appCache.resolve("lib").resolve("liba.jar"));
        // assert_().that(getClassPath(pb)).has().item(appCache.resolve("lib").resolve("libb.jar"));
        Assert.assertEquals("com.acme.Bar", getMainClass(pb));
    }

    @Test
    public void testScript() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Dependencies", "com.acme:bar:1.2").setAttribute("Linux", "Application-Script", "scr.sh").setAttribute("MacOS", "Application-Script", "scr.sh").setAttribute("Windows", "Application-Script", "scr.bat").addEntry("scr.sh", emptyInputStream()).addEntry("scr.bat", emptyInputStream()).addEntry("foo.jar", emptyInputStream());
        Path barPath = mockDep("com.acme:bar:1.2", "jar");
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(jar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertEquals(CapsuleTest.list(appCache.resolve((Capsule.isWindows() ? "scr.bat" : "scr.sh")).toString(), "hi", "there"), pb.command());
        assert_().that(getEnv(pb, "CLASSPATH")).contains((((appCache.resolve("foo.jar")) + (CapsuleTest.PS)) + barPath));
    }

    @Test
    public void testReallyExecutableCapsule() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Main-Class", "MyCapsule").setAttribute("Premain-Class", "MyCapsule").setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "bar baz=33 foo=y").setAttribute("JVM-Args", "-Xmx100 -Xms10").setReallyExecutable(true).addEntry("a.class", emptyInputStream());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Dfoo=x", "-Dzzz", "-Xms15");
        Path capsuleJar = absolutePath("capsule.jar");
        jar.write(capsuleJar);
        Capsule.newCapsule(CapsuleTest.MY_CLASSLOADER, capsuleJar).prepareForLaunch(cmdLine, args);
    }

    @Test
    public void testSimpleCaplet1() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Main-Class", "MyCapsule").setAttribute("Premain-Class", "MyCapsule").setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "bar baz=33 foo=y").setAttribute("JVM-Args", "-Xmx100 -Xms10").addClass(MyCapsule.class);
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Dfoo=x", "-Dzzz", "-Xms15");
        Path capsuleJar = absolutePath("capsule.jar");
        jar.write(capsuleJar);
        Capsule capsule = Capsule.newCapsule(CapsuleTest.MY_CLASSLOADER, capsuleJar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals("x", getProperty(pb, "foo"));
        Assert.assertEquals("", getProperty(pb, "bar"));
        Assert.assertEquals("", getProperty(pb, "zzz"));
        Assert.assertEquals("44", getProperty(pb, "baz"));
        Assert.assertTrue(getJvmArgs(pb).contains("-Xmx3000"));
        Assert.assertTrue((!(getJvmArgs(pb).contains("-Xmx100"))));
        Assert.assertTrue(getJvmArgs(pb).contains("-Xms15"));
        Assert.assertTrue((!(getJvmArgs(pb).contains("-Xms10"))));
    }

    @Test
    public void testSimpleCaplet2() throws Exception {
        Jar jar = newCapsuleJar().setListAttribute("Caplets", CapsuleTest.list("MyCapsule")).setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "bar baz=33 foo=y").setAttribute("JVM-Args", "-Xmx100 -Xms10").setListAttribute("App-Class-Path", CapsuleTest.list("lib/*")).addEntry("foo.jar", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.jar", emptyInputStream()).addClass(MyCapsule.class);
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Dfoo=x", "-Dzzz", "-Xms15");
        Path capsuleJar = absolutePath("capsule.jar");
        jar.write(capsuleJar);
        Capsule capsule = Capsule.newCapsule(CapsuleTest.MY_CLASSLOADER, capsuleJar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        assert_().that(getProperty(pb, "foo")).isEqualTo("x");
        assert_().that(getProperty(pb, "bar")).isEqualTo("");
        assert_().that(getProperty(pb, "zzz")).isEqualTo("");
        assert_().that(getProperty(pb, "baz")).isEqualTo("44");
        assert_().that(getJvmArgs(pb)).has().item("-Xmx3000");
        assert_().that(getJvmArgs(pb)).has().noneOf("-Xmx100");
        assert_().that(getJvmArgs(pb)).has().item("-Xms15");
        assert_().that(getJvmArgs(pb)).has().noneOf("-Xms10");
        assert_().that(getClassPath(pb)).has().allOf(fs.getPath("/foo/bar"), appCache.resolve("foo.jar"), appCache.resolve("lib").resolve("a.jar"), appCache.resolve("lib").resolve("b.jar"));
    }

    @Test
    public void testEmbeddedCaplet() throws Exception {
        Jar bar = newCapsuleJar().setListAttribute("Caplets", CapsuleTest.list("MyCapsule")).addClass(MyCapsule.class);
        Jar jar = newCapsuleJar().setListAttribute("Caplets", CapsuleTest.list("com.acme:mycapsule:0.9")).setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "bar baz=33 foo=y").setAttribute("JVM-Args", "-Xmx100 -Xms10").addEntry("mycapsule-0.9.jar", bar.toByteArray());
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list("-Dfoo=x", "-Dzzz", "-Xms15");
        Path capsuleJar = absolutePath("capsule.jar");
        jar.write(capsuleJar);
        Capsule capsule = Capsule.newCapsule(CapsuleTest.MY_CLASSLOADER, capsuleJar);
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertEquals("x", getProperty(pb, "foo"));
        Assert.assertEquals("", getProperty(pb, "bar"));
        Assert.assertEquals("", getProperty(pb, "zzz"));
        Assert.assertEquals("44", getProperty(pb, "baz"));
        Assert.assertTrue(getJvmArgs(pb).contains("-Xmx3000"));
        Assert.assertTrue((!(getJvmArgs(pb).contains("-Xmx100"))));
        Assert.assertTrue(getJvmArgs(pb).contains("-Xms15"));
        Assert.assertTrue((!(getJvmArgs(pb).contains("-Xms10"))));
    }

    @Test
    public void testWrapperCapsule() throws Exception {
        Jar wrapper = newCapsuleJar().setAttribute("Caplets", "MyCapsule").setAttribute("System-Properties", "p1=555").addClass(Capsule.class).addClass(MyCapsule.class);
        Jar app = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "p1=111").setListAttribute("App-Class-Path", CapsuleTest.list("lib/a.jar")).addClass(Capsule.class).addEntry("foo.jar", emptyInputStream()).addEntry("a.class", emptyInputStream()).addEntry("b.txt", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.class", emptyInputStream()).addEntry("META-INF/x.txt", emptyInputStream());
        Path fooPath = mockDep("com.acme:foo", "jar", "com.acme:foo:1.0").get(0);
        Files.createDirectories(fooPath.getParent());
        app.write(fooPath);
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(wrapper).setTarget("com.acme:foo");
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertTrue(capsule.hasCaplet("MyCapsule"));
        Assert.assertTrue(((capsule.toString()) != null));// exercise toString

        // dumpFileSystem(fs);
        Assert.assertTrue((pb != null));
        String appId = capsule.getAppId();
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertEquals("com.acme.Foo", getProperty(pb, "capsule.app"));
        Assert.assertEquals(appCache.toString(), getProperty(pb, "capsule.dir"));
        Assert.assertEquals(CapsuleTest.list("com.acme.Foo", "hi", "there"), CapsuleTest.getMainAndArgs(pb));
        Assert.assertTrue(Files.isDirectory(cache));
        Assert.assertTrue(Files.isDirectory(cache.resolve("apps")));
        Assert.assertTrue(Files.isDirectory(appCache));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve(".extracted")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("foo.jar")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("b.txt")));
        Assert.assertTrue(Files.isDirectory(appCache.resolve("lib")));
        Assert.assertTrue(Files.isRegularFile(appCache.resolve("lib").resolve("a.jar")));
        Assert.assertTrue((!(Files.isRegularFile(appCache.resolve("a.class")))));
        Assert.assertTrue((!(Files.isRegularFile(appCache.resolve("lib").resolve("b.class")))));
        Assert.assertTrue((!(Files.isDirectory(appCache.resolve("META-INF")))));
        Assert.assertTrue((!(Files.isRegularFile(appCache.resolve("META-INF").resolve("x.txt")))));
        assert_().that(getClassPath(pb)).has().allOf(appCache.resolve("foo.jar"), appCache.resolve("lib").resolve("a.jar"));
        Assert.assertEquals("111", getProperty(pb, "p1"));
    }

    @Test
    public void testWrapperCapsuleNonCapsuleApp() throws Exception {
        Jar wrapper = newCapsuleJar().setAttribute("Main-Class", "MyCapsule").setAttribute("Premain-Class", "MyCapsule").setAttribute("System-Properties", "p1=555").addClass(Capsule.class).addClass(MyCapsule.class);
        Jar app = new Jar().setAttribute("Main-Class", "com.acme.Foo").setAttribute("System-Properties", "p1=111").setAttribute("Class-Path", "lib/a.jar lib/b.jar").addEntry("a.class", emptyInputStream()).addEntry("b.txt", emptyInputStream()).addEntry("META-INF/x.txt", emptyInputStream());
        Path fooPath = path("foo-1.0.jar");
        app.write(fooPath);
        List<String> args = CapsuleTest.list("hi", "there");
        List<String> cmdLine = CapsuleTest.list();
        Capsule capsule = newCapsule(wrapper).setTarget(fooPath.toString());
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        // dumpFileSystem(fs);
        Assert.assertTrue((pb != null));
        String appId = capsule.getAppId();
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertEquals(CapsuleTest.list("com.acme.Foo", "hi", "there"), CapsuleTest.getMainAndArgs(pb));
        // assertTrue(!Files.exists(appCache));
        Assert.assertTrue((!(Files.exists(appCache.resolve("b.txt")))));
        Assert.assertTrue((!(Files.exists(appCache.resolve("a.class")))));
        assert_().that(getClassPath(pb)).has().item(fooPath.toAbsolutePath());
        // assert_().that(getClassPath(pb)).has().allOf(
        // path("lib").resolve("a.jar").toAbsolutePath(),
        // path("lib").resolve("b.jar").toAbsolutePath());
        assert_().that(getClassPath(pb)).has().noneOf(absolutePath("capsule.jar"), appCache.resolve("lib").resolve("a.jar"), appCache.resolve("lib").resolve("b.jar"));
        Assert.assertEquals("555", getProperty(pb, "p1"));
    }

    @Test
    public void testCapsuleJvmArgsParsing() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Main-Class", "MyCapsule").setAttribute("Premain-Class", "MyCapsule").setAttribute("Application-Class", "com.acme.Foo").addClass(MyCapsule.class);
        Path capsuleJar = absolutePath("capsule.jar");
        jar.write(capsuleJar);
        Capsule capsule = Capsule.newCapsule(CapsuleTest.MY_CLASSLOADER, capsuleJar);
        List<String> args = CapsuleTest.list();
        List<String> cmdLine = CapsuleTest.list();
        Capsule.setProperty("capsule.jvm.args", ("-Ddouble.quoted.arg=\"escape me\" " + "-Dsingle.quoted.arg='escape me'"));
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        assert_().that(getProperty(pb, "double.quoted.arg")).isEqualTo("escape me");
        assert_().that(getProperty(pb, "single.quoted.arg")).isEqualTo("escape me");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrapperCapsuleNoMain() throws Exception {
        Jar wrapper = newCapsuleJar().setAttribute("Main-Class", "MyCapsule").setAttribute("Premain-Class", "MyCapsule").setAttribute("System-Properties", "p1=555").addClass(Capsule.class).addClass(MyCapsule.class);
        Jar app = new Jar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "p1=111").setListAttribute("App-Class-Path", CapsuleTest.list("lib/a.jar")).addClass(Capsule.class).addEntry("foo.jar", emptyInputStream());
        Path fooPath = path("foo-1.0.jar");
        app.write(fooPath);
        newCapsule(wrapper).setTarget(fooPath.toString());
    }

    @Test
    public void testProcessCommandLineOptions() throws Exception {
        List<String> args = new ArrayList<>(CapsuleTest.list("-java-home", "/foo/bar", "-reset", "-jvm-args=a b c", "-java-cmd", "gogo", "hi", "there"));
        List<String> jvmArgs = CapsuleTest.list("-Dcapsule.java.cmd=wow");
        CapsuleTest.processCmdLineOptions(args, jvmArgs);
        Assert.assertEquals("/foo/bar", props.getProperty("capsule.java.home"));
        Assert.assertEquals("true", props.getProperty("capsule.reset"));
        Assert.assertEquals("a b c", props.getProperty("capsule.jvm.args"));
        Assert.assertEquals(null, props.getProperty("capsule.java.cmd"));// overriden

        Assert.assertEquals(CapsuleTest.list("hi", "there"), args);
    }

    @Test
    public void testTrampoline() throws Exception {
        props.setProperty("capsule.java.home", "/my/1.7.0.jdk/home");
        props.setProperty("capsule.trampoline", "true");
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Extract-Capsule", "false").addEntry("foo.jar", emptyInputStream());
        Class<?> capsuleClass = loadCapsule(jar);
        setProperties(capsuleClass, props);
        StringPrintStream out = setSTDOUT(capsuleClass, new StringPrintStream());
        int exit = CapsuleTest.main0(capsuleClass, "hi", "there!");
        Assert.assertEquals(0, exit);
        String res = out.toString();
        assert_().that(res).matches("[^\n]+\n\\z");// a single line, teminated with a newline

        assert_().that(res).startsWith(((("\"" + "/my/1.7.0.jdk/home/bin/java") + (Capsule.isWindows() ? ".exe" : "")) + "\""));
        assert_().that(res).endsWith("\"com.acme.Foo\" \"hi\" \"there!\"\n");
    }

    @Test
    public void testPrintHelp() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Application-Version", "12.34").addEntry("foo.jar", emptyInputStream());
        props.setProperty("capsule.help", "");
        Class<?> capsuleClass = loadCapsule(jar);
        setProperties(capsuleClass, props);
        StringPrintStream out = setSTDERR(capsuleClass, new StringPrintStream());
        Object capsule = newCapsule(capsuleClass);
        boolean found = CapsuleTest.runActions(capsule, null);
        String res = out.toString();
        assert_().that(found).isTrue();
        assert_().that(res).contains("USAGE: ");
    }

    @Test
    public void testPrintCapsuleVersion() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Application-Version", "12.34").addEntry("foo.jar", emptyInputStream());
        props.setProperty("capsule.version", "");
        Class<?> capsuleClass = loadCapsule(jar);
        setProperties(capsuleClass, props);
        StringPrintStream out = setSTDOUT(capsuleClass, new StringPrintStream());
        Object capsule = newCapsule(capsuleClass);
        boolean found = CapsuleTest.runActions(capsule, null);
        String res = out.toString();
        assert_().that(found).isTrue();
        assert_().that(res).contains("Application com.acme.Foo_12.34");
        assert_().that(res).contains("Version: 12.34");
    }

    @Test
    public void testPrintModes() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Application-Version", "12.34").setAttribute("ModeX", "System-Properties", "bar baz=55 foo=w").setAttribute("ModeX", "Description", "This is a secret mode").setAttribute("ModeY", "Description", "This is another secret mode").setAttribute("ModeZ", "Foo", "xxx").setAttribute("ModeX-Linux", "System-Properties", "bar baz=55 foo=w os=lin").setAttribute("ModeX-MacOS", "System-Properties", "bar baz=55 foo=w os=mac").setAttribute("ModeX-Windows", "System-Properties", "bar baz=55 foo=w os=win").setAttribute("ModeY-Java-15", "Description", "This is a secret mode").addEntry("foo.jar", emptyInputStream());
        props.setProperty("capsule.modes", "");
        Class<?> capsuleClass = loadCapsule(jar);
        setProperties(capsuleClass, props);
        StringPrintStream out = setSTDOUT(capsuleClass, new StringPrintStream());
        Object capsule = newCapsule(capsuleClass);
        boolean found = CapsuleTest.runActions(capsule, null);
        String res = out.toString();
        assert_().that(found).isTrue();
        assert_().that(res).contains("* ModeX: This is a secret mode");
        assert_().that(res).contains("* ModeY: This is another secret mode");
        assert_().that(res).contains("* ModeZ");
        assert_().that(res).doesNotContain("* ModeX-Linux");
        assert_().that(res).doesNotContain("* ModeY-Java-15");
    }

    @Test
    public void testMerge() throws Exception {
        Jar wrapper = newCapsuleJar().setAttribute("Caplets", "MyCapsule").setAttribute("System-Properties", "p1=555").addClass(MyCapsule.class);
        Jar app = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("System-Properties", "p1=111").setListAttribute("App-Class-Path", CapsuleTest.list("lib/a.jar")).addClass(Capsule.class).addEntry("foo.jar", emptyInputStream()).addEntry("a.class", emptyInputStream()).addEntry("b.txt", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.class", emptyInputStream()).addEntry("META-INF/x.txt", emptyInputStream());
        Class<?> capsuleClass = loadCapsule(wrapper);
        // setProperties(capsuleClass, props);
        Path fooPath = mockDep(capsuleClass, "com.acme:foo", "jar", "com.acme:foo:1.0").get(0);
        Files.createDirectories(fooPath.getParent());
        app.write(fooPath);
        props.setProperty("capsule.merge", "out.jar");
        // props.setProperty("capsule.log", "verbose");
        int exit = CapsuleTest.main0(capsuleClass, "com.acme:foo");
        Assert.assertEquals(0, exit);
        Assert.assertTrue(Files.isRegularFile(path("out.jar")));
        Jar out = new Jar(path("out.jar"));
        assert_().that(out.getAttribute("Main-Class")).isEqualTo("TestCapsule");
        assert_().that(out.getAttribute("Premain-Class")).isEqualTo("TestCapsule");
        assert_().that(out.getListAttribute("Caplets")).isEqualTo(CapsuleTest.list("MyCapsule"));
        assert_().that(out.getMapAttribute("System-Properties", "")).isEqualTo(CapsuleTest.map("p1", "111"));
        FileSystem jar = ZipFS.newZipFileSystem(path("out.jar"));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("Capsule.class")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("TestCapsule.class")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("MyCapsule.class")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("foo.jar")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("a.class")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("b.txt")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("lib/a.jar")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("lib/b.class")));
        Assert.assertTrue(Files.isRegularFile(jar.getPath("META-INF/x.txt")));
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Unit Tests">
    // ///////// Unit Tests ///////////////////////////////////
    @Test
    public void testParseJavaVersion() {
        int[] ver;
        ver = Capsule.parseJavaVersion("8");
        Assert.assertArrayEquals(ver, CapsuleTest.ints(1, 8, 0, 0, 0));
        Assert.assertEquals("1.8.0", Capsule.toJavaVersionString(ver));
        ver = Capsule.parseJavaVersion("1.8.0");
        Assert.assertArrayEquals(ver, CapsuleTest.ints(1, 8, 0, 0, 0));
        Assert.assertEquals("1.8.0", Capsule.toJavaVersionString(ver));
        ver = Capsule.parseJavaVersion("1.8");
        Assert.assertArrayEquals(ver, CapsuleTest.ints(1, 8, 0, 0, 0));
        Assert.assertEquals("1.8.0", Capsule.toJavaVersionString(ver));
        ver = Capsule.parseJavaVersion("1.8.0_30");
        Assert.assertArrayEquals(ver, CapsuleTest.ints(1, 8, 0, 30, 0));
        Assert.assertEquals("1.8.0_30", Capsule.toJavaVersionString(ver));
        ver = Capsule.parseJavaVersion("1.8.0-rc");
        Assert.assertArrayEquals(ver, CapsuleTest.ints(1, 8, 0, 0, (-1)));
        Assert.assertEquals("1.8.0-rc", Capsule.toJavaVersionString(ver));
        ver = Capsule.parseJavaVersion("1.8.0_30-ea");
        Assert.assertArrayEquals(ver, CapsuleTest.ints(1, 8, 0, 30, (-3)));
        Assert.assertEquals("1.8.0_30-ea", Capsule.toJavaVersionString(ver));
    }

    @Test
    public void testCompareVersions() {
        Assert.assertTrue(((Capsule.compareVersions("1.8.0_30-ea", "1.8.0_30")) < 0));
        Assert.assertTrue(((Capsule.compareVersions("1.8.0_30-ea", "1.8.0_20")) > 0));
        Assert.assertTrue(((Capsule.compareVersions("1.8.0-ea", "1.8.0_20")) < 0));
        Assert.assertTrue(((Capsule.compareVersions("1.8.0-ea", "1.8.0")) < 0));
        Assert.assertTrue(((Capsule.compareVersions("1.8.0-ea", "1.7.0")) > 0));
    }

    @Test
    public void testShortJavaVersion() {
        Assert.assertEquals("1.8.0", Capsule.shortJavaVersion("8"));
        Assert.assertEquals("1.8.0", Capsule.shortJavaVersion("1.8"));
        Assert.assertEquals("1.8.0", Capsule.shortJavaVersion("1.8.0"));
    }

    @Test
    public void isJavaDir() {
        Assert.assertEquals("1.7.0", Capsule.isJavaDir("jre7"));
        Assert.assertEquals("1.7.0_45", Capsule.isJavaDir("jdk1.7.0_45"));
        Assert.assertEquals("1.7.0_51", Capsule.isJavaDir("jdk1.7.0_51.jdk"));
        Assert.assertEquals("1.7.0", Capsule.isJavaDir("1.7.0.jdk"));
        Assert.assertEquals("1.8.0", Capsule.isJavaDir("jdk1.8.0.jdk"));
        Assert.assertEquals("1.7.0", Capsule.isJavaDir("java-7-openjdk-amd64"));
        Assert.assertEquals("1.7.0", Capsule.isJavaDir("java-1.7.0-openjdk-amd64"));
        Assert.assertEquals("1.7.0", Capsule.isJavaDir("java-1.7.0-openjdk-1.7.0.79.x86_64"));
        Assert.assertEquals("1.8.0", Capsule.isJavaDir("java-8-oracle"));
        Assert.assertEquals("1.8.0", Capsule.isJavaDir("jdk-8-oracle"));
        Assert.assertEquals("1.8.0", Capsule.isJavaDir("jre-8-oracle"));
        Assert.assertEquals("1.8.0", Capsule.isJavaDir("jdk-8-oracle-x64"));
        Assert.assertEquals("1.8.0", Capsule.isJavaDir("jdk-1.8.0"));
        Assert.assertEquals("1.8.0", Capsule.isJavaDir("jre-1.8.0"));
    }

    @Test
    public void testDelete() throws Exception {
        Files.createDirectories(path("a", "b", "c"));
        Files.createDirectories(path("a", "b1"));
        Files.createDirectories(path("a", "b", "c1"));
        Files.createFile(path("a", "x"));
        Files.createFile(path("a", "b", "x"));
        Files.createFile(path("a", "b1", "x"));
        Files.createFile(path("a", "b", "c", "x"));
        Files.createFile(path("a", "b", "c1", "x"));
        Assert.assertTrue(Files.exists(path("a")));
        Assert.assertTrue(Files.isDirectory(path("a")));
        // Files.delete(path("a"));
        Capsule.delete(path("a"));
        Assert.assertTrue((!(Files.exists(path("a")))));
    }

    @Test
    public void testGlobToRegex() throws Exception {
        Assert.assertEquals(true, "abc/def".matches(Capsule.globToRegex("abc/def")));
        Assert.assertEquals(true, "abc/def".matches(Capsule.globToRegex("*/d*")));
        Assert.assertEquals(true, "abc/def".matches(Capsule.globToRegex("a*/d*")));
        Assert.assertEquals(true, "abc/def".matches(Capsule.globToRegex("*/*")));
        Assert.assertEquals(false, "abc/def".matches(Capsule.globToRegex("abc/d")));
        Assert.assertEquals(false, "abc/def".matches(Capsule.globToRegex("*")));
        Assert.assertEquals(false, "abc/def".matches(Capsule.globToRegex("d*")));
        Assert.assertEquals(false, "abc/def".matches(Capsule.globToRegex("abc?d*")));
    }

    @Test
    public void testParseCommandLineArguments() throws Exception {
        Assert.assertEquals(CapsuleTest.list("x", "y", "z"), Capsule.parseCommandLineArguments("x y z"));
        Assert.assertEquals(CapsuleTest.list("x", "y z"), Capsule.parseCommandLineArguments("x 'y z'"));
        Assert.assertEquals(CapsuleTest.list("x y", "z"), Capsule.parseCommandLineArguments("\"x y\" z"));
    }

    @Test
    public void testMove() throws Exception {
        Assert.assertEquals(Paths.get("/c/d"), Capsule.move(Paths.get("/a/b"), Paths.get("/a/b"), Paths.get("/c/d/")));
        Assert.assertEquals(Paths.get("/c/d/e"), Capsule.move(Paths.get("/a/b/e"), Paths.get("/a/b"), Paths.get("/c/d/")));
    }

    @Test
    public void testDependencyToLocalJar() throws Exception {
        Path jar = fs.getPath("foo.jar");
        String file;
        file = "lib/com.acme/foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
        file = "lib/com.acme-foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
        file = "lib/com.acme-foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
        file = "lib/com.acme-foo-3.1-big.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1:big", "mmm"));
        file = "lib/foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
        file = "com.acme/foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
        file = "com.acme-foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
        file = "com.acme-foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
        file = "com.acme-foo-3.1-big.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1:big", "mmm"));
        file = "foo-3.1.mmm";
        writeJarWithFile(jar, file);
        Assert.assertEquals(file, CapsuleTest.dependencyToLocalJar(jar, "com.acme:foo:3.1", "mmm"));
    }

    @Test
    public void testExpandVars1() throws Exception {
        Jar jar = newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo");
        props.setProperty("a.b.c", "777");
        props.setProperty("my.prop", "888");
        Capsule capsule = newCapsule(jar);
        capsule.prepareForLaunch(null, null);
        String cj = absolutePath("capsule.jar").toString();
        String cd = cache.resolve("apps").resolve("com.acme.Foo").toString();
        String cid = capsule.getAppId();
        Assert.assertEquals(((((cj + "abc") + cj) + "def ") + cj), expand(capsule, ("${CAPSULE_JAR}" + ((("abc" + "${CAPSULE_JAR}") + "def") + " $CAPSULE_JAR"))));
        Assert.assertEquals(((((cid + " abc") + cid) + "def") + cid), expand(capsule, ("$CAPSULE_APP" + (((" abc" + "${CAPSULE_APP}") + "def") + "${CAPSULE_APP}"))));
        Assert.assertEquals(((((cd + "abc ") + cd) + " def") + cd), expand(capsule, ("${CAPSULE_DIR}" + ((("abc " + "$CAPSULE_DIR") + " def") + "${CAPSULE_DIR}"))));
        Assert.assertEquals((((((cd + "abc") + cid) + "def") + cj) + "888777"), expand(capsule, ("${CAPSULE_DIR}" + ((("abc" + "${CAPSULE_APP}") + "def") + "${CAPSULE_JAR}${my.prop}${a.b.c}"))));
        Assert.assertEquals("888", expand(capsule, "${my.prop}"));
        Assert.assertEquals(cj, expand(capsule, "$CAPSULE_JAR"));
        try {
            expand(capsule, "${foo.bar.baz}");
            Assert.fail();
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void testExpandArgs() throws Exception {
        Assert.assertEquals(CapsuleTest.list("x", "y", "z"), Capsule.expandArgs(CapsuleTest.list("x", "y", "z"), CapsuleTest.<String>list()));
        Assert.assertEquals(CapsuleTest.list("a", "b", "c"), Capsule.expandArgs(CapsuleTest.<String>list(), CapsuleTest.list("a", "b", "c")));
        Assert.assertEquals(CapsuleTest.list("x", "y", "z", "a", "b", "c"), Capsule.expandArgs(CapsuleTest.list("x", "y", "z"), CapsuleTest.list("a", "b", "c")));
        Assert.assertEquals(CapsuleTest.list("x", "a", "b", "c", "z"), Capsule.expandArgs(CapsuleTest.list("x", "$*", "z"), CapsuleTest.list("a", "b", "c")));
        Assert.assertEquals(CapsuleTest.list("b", "a", "c"), Capsule.expandArgs(CapsuleTest.list("$2", "$1", "$3"), CapsuleTest.list("a", "b", "c")));
    }

    @Test
    public void testParseAttribute() {
        Assert.assertEquals("abcd 123", Capsule.parse("abcd 123", Capsule.T_STRING(), null));
        Assert.assertEquals(true, Capsule.parse("TRUE", Capsule.T_BOOL(), null));
        Assert.assertEquals(true, Capsule.parse("true", Capsule.T_BOOL(), null));
        Assert.assertEquals(false, Capsule.parse("FALSE", Capsule.T_BOOL(), null));
        Assert.assertEquals(false, Capsule.parse("false", Capsule.T_BOOL(), null));
        Assert.assertEquals(15L, ((long) (Capsule.parse("15", Capsule.T_LONG(), null))));
        try {
            Capsule.parse("15abs", Capsule.T_LONG(), null);
            Assert.fail();
        } catch (RuntimeException e) {
        }
        Assert.assertEquals(1.2, Capsule.parse("1.2", Capsule.T_DOUBLE(), null), 1.0E-4);
        try {
            Capsule.parse("1.2a", Capsule.T_DOUBLE(), null);
            Assert.fail();
        } catch (RuntimeException e) {
        }
        Assert.assertEquals(CapsuleTest.list("abcd", "123"), Capsule.parse("abcd 123", Capsule.T_LIST(Capsule.T_STRING()), null));
        Assert.assertEquals(CapsuleTest.list("ab", "cd", "ef", "g", "hij", "kl"), Capsule.parse("ab cd  ef g hij kl  ", Capsule.T_LIST(Capsule.T_STRING()), null));
        Assert.assertEquals(CapsuleTest.list(true, false, true, false), Capsule.parse("TRUE false true FALSE", Capsule.T_LIST(Capsule.T_BOOL()), null));
        Assert.assertEquals(CapsuleTest.list(123L, 456L, 7L), Capsule.parse("123 456  7", Capsule.T_LIST(Capsule.T_LONG()), null));
        Assert.assertEquals(CapsuleTest.list(1.23, 3.45), Capsule.parse("1.23 3.45", Capsule.T_LIST(Capsule.T_DOUBLE()), null));
        Assert.assertEquals(CapsuleTest.map("ab", "1", "cd", "xx", "ef", "32", "g", "xx", "hij", "", "kl", ""), Capsule.parse("ab=1 cd  ef=32 g hij= kl=  ", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_STRING(), "xx"), null));
        try {
            Capsule.parse("ab=1 cd  ef=32 g hij= kl=  ", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_STRING(), null), null);
            Assert.fail();
        } catch (Exception e) {
        }
        Assert.assertEquals(CapsuleTest.map("ab", true, "cd", true, "ef", false, "g", true), Capsule.parse("ab=true cd  ef=false  g", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_BOOL(), true), null));
        try {
            Capsule.parse("ab=true cd  ef=false  g", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_BOOL(), null), null);
            Assert.fail();
        } catch (Exception e) {
        }
        Assert.assertEquals(CapsuleTest.map("ab", 12L, "cd", 17L, "ef", 54L, "g", 17L), Capsule.parse("ab=12 cd  ef=54  g", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_LONG(), 17), null));
        try {
            Capsule.parse("ab=12 cd  ef=54  g", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_LONG(), null), null);
            Assert.fail();
        } catch (Exception e) {
        }
        try {
            Capsule.parse("ab=12 cd=xy  ef=54  g=z", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_LONG(), 17), null);
            Assert.fail();
        } catch (Exception e) {
        }
        Assert.assertEquals(CapsuleTest.map("ab", 12.0, "cd", 100.0, "ef", 5.4, "g", 100.0), Capsule.parse("ab=12 cd  ef=5.4  g", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_DOUBLE(), 100), null));
        try {
            Capsule.parse("ab=12.1 cd  ef=5.4  g", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_DOUBLE(), null), null);
            Assert.fail();
        } catch (Exception e) {
        }
        try {
            Capsule.parse("ab=12 cd=xy ef=54  g=z", Capsule.T_MAP(Capsule.T_STRING(), Capsule.T_DOUBLE(), 17.0), null);
            Assert.fail();
        } catch (Exception e) {
        }
        Assert.assertEquals(CapsuleTest.map(12.3, 12L, 1.01, 17L, 2.05, 54L, 4.0, 17L), Capsule.parse("12.3=12 1.01  2.05=54  4.0", Capsule.T_MAP(Capsule.T_DOUBLE(), Capsule.T_LONG(), 17), null));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPathingJar() throws Exception {
        Files.createDirectories(tmp);
        List<Path> cp = CapsuleTest.list(path("/a.jar"), path("/b.jar"), path("/c.jar"));
        Path pathingJar = Capsule.createPathingJar(tmp, cp);
        try {
            List<Path> cp2;
            try (JarInputStream jis = new JarInputStream(Files.newInputStream(pathingJar))) {
                cp2 = toPath(Arrays.asList(jis.getManifest().getMainAttributes().getValue("Class-Path").split(" ")));
            }
            Assert.assertEquals(cp, toAbsolutePath(cp2));
            for (Path p : cp2)
                Assert.assertTrue((!(p.isAbsolute())));

        } finally {
            Files.delete(pathingJar);
        }
    }

    private static final String PS = System.getProperty("path.separator");
}

