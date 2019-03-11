/**
 * Copyright (C) 2013 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.config;


import Arch.arm64;
import Arch.x86;
import Arch.x86_64;
import IOSTarget.TYPE;
import OS.linux;
import OS.macosx;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.config.Config.Builder;
import org.robovm.compiler.config.Config.Home;
import org.robovm.compiler.config.Config.Lib;


/**
 * Tests {@link Config}.
 */
public class ConfigTest {
    String savedUserDir;

    File tmp;

    File wd;

    Home fakeHome;

    @Test
    public void testReadConsole() throws Exception {
        Config.Builder builder = new Config.Builder();
        builder.read(new InputStreamReader(getClass().getResourceAsStream("ConfigTest.console.xml"), "utf-8"), wd);
        Config config = builder.config;
        Assert.assertEquals(Arrays.asList(new File(wd, "foo1.jar"), new File(tmp, "foo2.jar")), config.getClasspath());
        Assert.assertEquals(Arrays.asList("Foundation", "AppKit"), config.getFrameworks());
        Assert.assertEquals(Arrays.asList(new Config.Lib("dl", true), new Config.Lib("/tmp/wd/libs/libmy.a", true), new Config.Lib("/tmp/wd/libs/foo.o", true), new Config.Lib("/usr/lib/libbar.a", false)), config.getLibs());
        Assert.assertEquals(Arrays.asList(new Resource(new File(wd, "resources")), new Resource(new File("/usr/share/resources")), new Resource(null, null).include("data/**/*"), new Resource(null, null).include("videos/**/*.avi"), new Resource(new File(wd, "resources"), "data").include("**/*.png").exclude("**/foo.png").flatten(true)), config.getResources());
        Assert.assertEquals(Arrays.asList("javax.**.*"), config.getForceLinkClasses());
        Assert.assertEquals(macosx, config.getOs());
        Assert.assertEquals(2, config.getArchs().size());
        Assert.assertEquals(x86, config.getArchs().get(0));
        Assert.assertEquals(x86_64, config.getArchs().get(1));
    }

    @Test
    public void testReadOldConsole() throws Exception {
        Config.Builder builder = new Config.Builder();
        builder.read(new InputStreamReader(getClass().getResourceAsStream("ConfigTest.old.console.xml"), "utf-8"), wd);
        Config config = builder.config;
        Assert.assertEquals(Arrays.asList(new File(wd, "foo1.jar"), new File(tmp, "foo2.jar")), config.getClasspath());
        Assert.assertEquals(Arrays.asList("Foundation", "AppKit"), config.getFrameworks());
        Assert.assertEquals(Arrays.asList(new Config.Lib("dl", true), new Config.Lib("/tmp/wd/libs/libmy.a", true), new Config.Lib("/tmp/wd/libs/foo.o", true), new Config.Lib("/usr/lib/libbar.a", false)), config.getLibs());
        Assert.assertEquals(Arrays.asList(new Resource(new File("/tmp/wd/resources")), new Resource(new File("/usr/share/resources"))), config.getResources());
        Assert.assertEquals(Arrays.asList("javax.**.*"), config.getForceLinkClasses());
        Assert.assertEquals(macosx, config.getOs());
        Assert.assertEquals(1, config.getArchs().size());
        Assert.assertEquals(x86, config.getArchs().get(0));
    }

    @Test
    public void testWriteConsole() throws Exception {
        Config.Builder builder = new Config.Builder();
        builder.addClasspathEntry(new File("foo1.jar"));
        builder.addClasspathEntry(new File(tmp, "foo2.jar"));
        builder.addFramework("Foundation");
        builder.addFramework("AppKit");
        builder.addLib(new Config.Lib("dl", true));
        builder.addLib(new Config.Lib("libs/libmy.a", true));
        builder.addLib(new Config.Lib("libs/foo.o", true));
        builder.addLib(new Config.Lib("/usr/lib/libbar.a", false));
        builder.addResource(new Resource(new File("/tmp/wd/resources")));
        builder.addResource(new Resource(new File("/usr/share/resources")));
        builder.addResource(new Resource(new File("/tmp/wd"), null).include("data/**/*"));
        builder.addResource(new Resource(null, null).include("videos/**/*.avi"));
        builder.addResource(new Resource(new File("/tmp/wd/resources"), "data").include("**/*.png").exclude("**/foo.png").flatten(true));
        builder.addForceLinkClass("javax.**.*");
        builder.os(macosx);
        builder.archs(x86, x86_64);
        StringWriter out = new StringWriter();
        builder.write(out, wd);
        Assert.assertEquals(IOUtils.toString(getClass().getResourceAsStream("ConfigTest.console.xml")), out.toString());
    }

    @Test
    public void testReadIOS() throws Exception {
        Config.Builder builder = new Config.Builder();
        builder.read(new InputStreamReader(getClass().getResourceAsStream("ConfigTest.ios.xml"), "utf-8"), wd);
        Config config = builder.config;
        Assert.assertEquals("6.1", config.getIosSdkVersion());
        Assert.assertEquals(new File(wd, "Info.plist"), config.getIosInfoPList().getFile());
        Assert.assertEquals(new File(wd, "entitlements.plist"), config.getIosEntitlementsPList());
    }

    @Test
    public void testWriteIOS() throws Exception {
        Config.Builder builder = new Config.Builder();
        builder.iosSdkVersion("6.1");
        builder.iosInfoPList(new File("Info.plist"));
        builder.iosEntitlementsPList(new File("entitlements.plist"));
        builder.targetType(TYPE);
        StringWriter out = new StringWriter();
        builder.write(out, wd);
        Assert.assertEquals(IOUtils.toString(getClass().getResourceAsStream("ConfigTest.ios.xml")), out.toString());
    }

    @Test
    public void testMergeConfigsFromClasspath() throws Exception {
        File tmpDir = createTempDir();
        File cacheDir = new File(tmpDir, "cache");
        File p1 = createMergeConfig(tmpDir, "p1", "Foo", macosx, x86, false);
        File p2 = createMergeConfig(tmpDir, "p2", "Wooz", linux, x86, false);
        // Create a jar file with both x86 and x86_64 by first creating a folder for x86 in p3/
        // and then passing p3/ again but this time compress it to a jar.
        createMergeConfig(tmpDir, "p3", "Baaz", macosx, x86, false);
        File p3 = createMergeConfig(tmpDir, "p3", "Raaz", macosx, x86_64, true);
        Config.Builder builder = new Config.Builder();
        builder.cacheDir(cacheDir);
        builder.os(macosx);
        builder.arch(x86);
        builder.targetType(ConsoleTarget.TYPE);
        builder.mainClass("Main");
        builder.addClasspathEntry(p1);
        builder.addClasspathEntry(p2);
        builder.addClasspathEntry(p3);
        builder.addExportedSymbol("YADA*");
        builder.addFrameworkPath(new File(p1, "yada"));
        builder.addFramework("Yada");
        builder.addForceLinkClass("org.yada.**");
        builder.addLib(new Lib("yada", true));
        builder.addResource(new Resource(new File(p1, "resources")));
        builder.addWeakFramework("WeakYada");
        builder.home(fakeHome);
        Config config = builder.build();
        File p1X86Root = new File(p1, "META-INF/robovm/macosx/x86");
        File p3X86Cache = config.getCacheDir(config.getClazzes().getClasspathPaths().get(2));
        File p3X86Root = new File(p3X86Cache.getParentFile(), ((p3X86Cache.getName()) + ".extracted/META-INF/robovm/macosx/x86"));
        Assert.assertEquals(Arrays.asList("FOO*", "BAAZ*", "YADA*"), config.getExportedSymbols());
        Assert.assertEquals(Arrays.asList("com.foo.**", "com.baaz.**", "org.yada.**"), config.getForceLinkClasses());
        Assert.assertEquals(Arrays.asList(new File(p1X86Root, "foo/bar"), new File(p3X86Root, "baaz/bar"), new File(p1, "yada")), config.getFrameworkPaths());
        Assert.assertEquals(Arrays.asList("Foo", "Baaz", "Yada"), config.getFrameworks());
        Assert.assertEquals(Arrays.asList(new Lib("foo", true), new Lib(new File(p1X86Root, "libfoo.a").getAbsolutePath(), true), new Lib("baaz", true), new Lib(new File(p3X86Root, "libbaaz.a").getAbsolutePath(), true), new Lib("yada", true)), config.getLibs());
        Assert.assertEquals(Arrays.asList(new Resource(new File(p1X86Root, "resources")), new Resource(new File(p3X86Root, "resources")), new Resource(new File(p1, "resources"))), config.getResources());
        Assert.assertEquals(Arrays.asList("WeakFoo", "WeakBaaz", "WeakYada"), config.getWeakFrameworks());
        // Make sure builder() returns a config which merges in x86_64 configs instead
        config = config.builder().arch(x86_64).build();
        File p3X86_64Cache = config.getCacheDir(config.getClazzes().getClasspathPaths().get(2));
        File p3X86_64Root = new File(p3X86_64Cache.getParentFile(), ((p3X86_64Cache.getName()) + ".extracted/META-INF/robovm/macosx/x86_64"));
        Assert.assertEquals(Arrays.asList("RAAZ*", "YADA*"), config.getExportedSymbols());
        Assert.assertEquals(Arrays.asList("com.raaz.**", "org.yada.**"), config.getForceLinkClasses());
        Assert.assertEquals(Arrays.asList(new File(p3X86_64Root, "raaz/bar"), new File(p1, "yada")), config.getFrameworkPaths());
        Assert.assertEquals(Arrays.asList("Raaz", "Yada"), config.getFrameworks());
        Assert.assertEquals(Arrays.asList(new Lib("raaz", true), new Lib(new File(p3X86_64Root, "libraaz.a").getAbsolutePath(), true), new Lib("yada", true)), config.getLibs());
        Assert.assertEquals(Arrays.asList(new Resource(new File(p3X86_64Root, "resources")), new Resource(new File(p1, "resources"))), config.getResources());
        Assert.assertEquals(Arrays.asList("WeakRaaz", "WeakYada"), config.getWeakFrameworks());
    }

    @Test
    public void testCreateBuilderFromConfig() throws Exception {
        File tmpDir = createTempDir();
        File cacheDir = new File(tmpDir, "cache");
        Config.Builder builder = new Config.Builder();
        builder.tmpDir(tmpDir);
        builder.cacheDir(cacheDir);
        builder.os(macosx);
        builder.arch(x86);
        builder.targetType(ConsoleTarget.TYPE);
        builder.mainClass("Main");
        builder.addBootClasspathEntry(new File(tmpDir, "bcp1"));
        builder.addBootClasspathEntry(new File(tmpDir, "bcp2"));
        builder.addBootClasspathEntry(new File(tmpDir, "bcp3"));
        builder.addClasspathEntry(new File(tmpDir, "cp1"));
        builder.addClasspathEntry(new File(tmpDir, "cp2"));
        builder.addClasspathEntry(new File(tmpDir, "cp3"));
        builder.addExportedSymbol("YADA*");
        builder.addFrameworkPath(new File(tmpDir, "yada"));
        builder.addFramework("Yada");
        builder.addForceLinkClass("org.yada.**");
        builder.addLib(new Lib("yada", true));
        builder.addResource(new Resource(new File(tmpDir, "resources")));
        builder.addWeakFramework("WeakYada");
        builder.addPluginArgument("foo:bar=yada");
        builder.home(fakeHome);
        Config config = builder.build();
        Builder builder2 = config.builder();
        builder2.arch(arm64);
        Config config2 = builder2.build();
        Assert.assertNotSame(config, config2);
        Assert.assertEquals(config.getTmpDir(), config2.getTmpDir());
        Assert.assertEquals(config.getCacheDir().getParentFile().getParentFile(), config2.getCacheDir().getParentFile().getParentFile());
        Assert.assertEquals(config.getOs(), config2.getOs());
        Assert.assertEquals(config.getMainClass(), config2.getMainClass());
        Assert.assertEquals(config.getBootclasspath(), config2.getBootclasspath());
        Assert.assertNotSame(config.getBootclasspath(), config2.getBootclasspath());
        Assert.assertEquals(config.getClasspath(), config2.getClasspath());
        Assert.assertNotSame(config.getClasspath(), config2.getClasspath());
        Assert.assertEquals(config.getExportedSymbols(), config2.getExportedSymbols());
        Assert.assertNotSame(config.getExportedSymbols(), config2.getExportedSymbols());
        Assert.assertEquals(config.getFrameworkPaths(), config2.getFrameworkPaths());
        Assert.assertNotSame(config.getFrameworkPaths(), config2.getFrameworkPaths());
        Assert.assertEquals(config.getFrameworks(), config2.getFrameworks());
        Assert.assertNotSame(config.getFrameworks(), config2.getFrameworks());
        Assert.assertEquals(config.getForceLinkClasses(), config2.getForceLinkClasses());
        Assert.assertNotSame(config.getForceLinkClasses(), config2.getForceLinkClasses());
        Assert.assertEquals(config.getLibs(), config2.getLibs());
        Assert.assertNotSame(config.getLibs(), config2.getLibs());
        Assert.assertEquals(config.getResources(), config2.getResources());
        Assert.assertNotSame(config.getResources(), config2.getResources());
        Assert.assertEquals(config.getPluginArguments(), config2.getPluginArguments());
        Assert.assertNotSame(config.getPluginArguments(), config2.getPluginArguments());
        Assert.assertEquals(arm64, config2.getArch());
        Assert.assertFalse(config.getPlugins().equals(config2.getPlugins()));
        Assert.assertNotSame(config.getTarget(), config2.getTarget());
        Assert.assertNotSame(config.getClazzes(), config2.getClazzes());
    }

    @Test
    public void testGetFileName() throws Exception {
        Assert.assertEquals("201a6b3053cc1422d2c3670b62616221d2290929.class.o", Config.getFileName("Foo", "class.o", 0));
        Assert.assertEquals("201a6b3053cc1422d2c3670b62616221d2290929.class.o", Config.getFileName("Foo", "class.o", 1));
        Assert.assertEquals("201a6b3053cc1422d2c3670b62616221d2290929.class.o", Config.getFileName("Foo", "class.o", 10));
        Assert.assertEquals("Foo.class.o", Config.getFileName("Foo", "class.o", 11));
        Assert.assertEquals("com/example/201a6b3053cc1422d2c3670b62616221d2290929.class.o", Config.getFileName("com/example/Foo", "class.o", 0));
        Assert.assertEquals("com/example/201a6b3053cc1422d2c3670b62616221d2290929.class.o", Config.getFileName("com/example/Foo", "class.o", 1));
        Assert.assertEquals("com/example/201a6b3053cc1422d2c3670b62616221d2290929.class.o", Config.getFileName("com/example/Foo", "class.o", 10));
        Assert.assertEquals("com/example/Foo.class.o", Config.getFileName("com/example/Foo", "class.o", 11));
        Assert.assertEquals("com/example/AB9ca44297c0e0d22df654119dce73ee52d3d51c71.class.o", Config.getFileName("com/example/ABCDEFGIHJABCDEFGIHJABCDEFGIHJABCDEFGIHJABCDEFGIHJ", "class.o", 50));
    }
}

