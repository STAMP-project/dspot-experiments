/**
 * Capsule
 * Copyright (c) 2014-2015, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are licensed under the terms
 * of the Eclipse Public License v1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package co.paralleluniverse.capsule;


import com.google.common.jimfs.Jimfs;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pron
 */
// </editor-fold>
public class CapsuleLauncherTest {
    private final FileSystem fs = Jimfs.newFileSystem();

    private final Path cache = fs.getPath("/cache");

    private final Path tmp = fs.getPath("/tmp");

    @Test
    public void testSimpleExtract() throws Exception {
        Jar jar = // test with Windows path
        newCapsuleJar().setAttribute("Application-Class", "com.acme.Foo").setAttribute("Unregisterd-Attribute", "just a string").addEntry("foo.jar", emptyInputStream()).addEntry("a.class", emptyInputStream()).addEntry("b.txt", emptyInputStream()).addEntry("lib/a.jar", emptyInputStream()).addEntry("lib/b.class", emptyInputStream()).addEntry("q/w/x.txt", emptyInputStream()).addEntry("d\\f\\y.txt", emptyInputStream()).addEntry("META-INF/x.txt", emptyInputStream());
        List<String> args = CapsuleLauncherTest.list("hi", "there");
        List<String> cmdLine = CapsuleLauncherTest.list();
        Properties props = new Properties(System.getProperties());
        props.setProperty("my.foo.prop", "zzzz");
        Capsule capsule = newCapsuleLauncher(jar).setProperties(props).newCapsule();
        ProcessBuilder pb = capsule.prepareForLaunch(cmdLine, args);
        Assert.assertTrue(capsule.hasAttribute(Attribute.named("Application-Class")));
        Assert.assertEquals("com.acme.Foo", capsule.getAttribute(Attribute.named("Application-Class")));
        Assert.assertTrue(capsule.hasAttribute(Attribute.named("Unregisterd-Attribute")));
        Assert.assertEquals("just a string", capsule.getAttribute(Attribute.named("Unregisterd-Attribute")));
        // dumpFileSystem(fs);
        Assert.assertEquals(capsule.getProperties().getProperty("my.foo.prop"), "zzzz");
        Assert.assertEquals(args, getAppArgs(pb));
        Path appCache = cache.resolve("apps").resolve("com.acme.Foo");
        Assert.assertEquals("com.acme.Foo", getProperty(pb, "capsule.app"));
        Assert.assertEquals("com.acme.Foo", getEnv(pb, "CAPSULE_APP"));
        Assert.assertEquals(appCache, path(getProperty(pb, "capsule.dir")));
        Assert.assertEquals(absolutePath("capsule.jar"), path(getProperty(pb, "capsule.jar")));
        Assert.assertEquals(appCache, path(getEnv(pb, "CAPSULE_DIR")));
        Assert.assertEquals(absolutePath("capsule.jar"), path(getEnv(pb, "CAPSULE_JAR")));
        Assert.assertEquals(CapsuleLauncherTest.list("com.acme.Foo", "hi", "there"), CapsuleLauncherTest.getMainAndArgs(pb));
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
    public void testEnableJMX() throws Exception {
        assert_().that(CapsuleLauncher.enableJMX(CapsuleLauncherTest.list("a", "b"))).has().item("-Dcom.sun.management.jmxremote");
        assert_().that(CapsuleLauncher.enableJMX(CapsuleLauncherTest.list("a", "-Dcom.sun.management.jmxremote", "b"))).isEqualTo(CapsuleLauncherTest.list("a", "-Dcom.sun.management.jmxremote", "b"));
    }
}

