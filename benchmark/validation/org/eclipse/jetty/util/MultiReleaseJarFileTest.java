/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Collectors;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;


public class MultiReleaseJarFileTest {
    private File example = MavenTestingUtils.getTestResourceFile("example.jar");

    @Test
    public void testExampleJarIsMR() throws Exception {
        try (MultiReleaseJarFile jarFile = new MultiReleaseJarFile(example)) {
            Assertions.assertTrue(jarFile.isMultiRelease(), (("Expected " + (example)) + " to be MultiRelease JAR File"));
        }
    }

    @Test
    public void testBase() throws Exception {
        try (MultiReleaseJarFile jarFile = new MultiReleaseJarFile(example, 8, false)) {
            MatcherAssert.assertThat(jarFile.getEntry("META-INF/MANIFEST.MF").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/OnlyInBase.class").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/InBoth$InnerBase.class").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/InBoth$InnerBoth.class").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/InBoth.class").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.stream().map(VersionedJarEntry::getName).collect(Collectors.toSet()), Matchers.containsInAnyOrder("META-INF/MANIFEST.MF", "org/example/OnlyInBase.class", "org/example/InBoth$InnerBase.class", "org/example/InBoth$InnerBoth.class", "org/example/InBoth.class", "WEB-INF/web.xml", "WEB-INF/classes/App.class", "WEB-INF/lib/depend.jar"));
        }
    }

    @Test
    public void test9() throws Exception {
        try (MultiReleaseJarFile jarFile = new MultiReleaseJarFile(example, 9, false)) {
            MatcherAssert.assertThat(jarFile.getEntry("META-INF/MANIFEST.MF").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/OnlyInBase.class").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/InBoth$InnerBoth.class").getVersion(), Matchers.is(9));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/InBoth.class").getVersion(), Matchers.is(9));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/OnlyIn9.class").getVersion(), Matchers.is(9));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/onlyIn9/OnlyIn9.class").getVersion(), Matchers.is(9));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/InBoth$Inner9.class").getVersion(), Matchers.is(9));
            MatcherAssert.assertThat(jarFile.stream().map(VersionedJarEntry::getName).collect(Collectors.toSet()), Matchers.containsInAnyOrder("META-INF/MANIFEST.MF", "org/example/OnlyInBase.class", "org/example/InBoth$InnerBoth.class", "org/example/InBoth.class", "org/example/OnlyIn9.class", "org/example/onlyIn9/OnlyIn9.class", "org/example/InBoth$Inner9.class", "WEB-INF/web.xml", "WEB-INF/classes/App.class", "WEB-INF/lib/depend.jar"));
        }
    }

    @Test
    public void test10() throws Exception {
        try (MultiReleaseJarFile jarFile = new MultiReleaseJarFile(example, 10, false)) {
            MatcherAssert.assertThat(jarFile.getEntry("META-INF/MANIFEST.MF").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/OnlyInBase.class").getVersion(), Matchers.is(0));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/InBoth.class").getVersion(), Matchers.is(10));
            MatcherAssert.assertThat(jarFile.getEntry("org/example/In10Only.class").getVersion(), Matchers.is(10));
            MatcherAssert.assertThat(jarFile.stream().map(VersionedJarEntry::getName).collect(Collectors.toSet()), Matchers.containsInAnyOrder("META-INF/MANIFEST.MF", "org/example/OnlyInBase.class", "org/example/InBoth.class", "org/example/In10Only.class", "WEB-INF/web.xml", "WEB-INF/classes/App.class", "WEB-INF/lib/depend.jar"));
        }
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testClassLoaderJava9() throws Exception {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{ example.toURI().toURL() })) {
            MatcherAssert.assertThat(IO.toString(loader.getResource("org/example/OnlyInBase.class").openStream()), Matchers.is("org/example/OnlyInBase.class"));
            MatcherAssert.assertThat(IO.toString(loader.getResource("org/example/OnlyIn9.class").openStream()), Matchers.is("META-INF/versions/9/org/example/OnlyIn9.class"));
            MatcherAssert.assertThat(IO.toString(loader.getResource("WEB-INF/web.xml").openStream()), Matchers.is("META-INF/versions/9/WEB-INF/web.xml"));
            MatcherAssert.assertThat(IO.toString(loader.getResource("WEB-INF/classes/App.class").openStream()), Matchers.is("META-INF/versions/9/WEB-INF/classes/App.class"));
            MatcherAssert.assertThat(IO.toString(loader.getResource("WEB-INF/lib/depend.jar").openStream()), Matchers.is("META-INF/versions/9/WEB-INF/lib/depend.jar"));
        }
    }
}

