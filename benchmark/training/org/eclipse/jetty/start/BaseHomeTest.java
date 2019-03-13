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
package org.eclipse.jetty.start;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.start.config.ConfigSources;
import org.eclipse.jetty.start.config.JettyBaseConfigSource;
import org.eclipse.jetty.start.config.JettyHomeConfigSource;
import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class BaseHomeTest {
    @Test
    public void testGetPath_OnlyHome() throws IOException {
        File homeDir = MavenTestingUtils.getTestResourceDir("hb.1/home");
        ConfigSources config = new ConfigSources();
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        BaseHome hb = new BaseHome(config);
        Path startIni = hb.getPath("start.ini");
        String ref = hb.toShortForm(startIni);
        MatcherAssert.assertThat("Reference", ref, Matchers.startsWith("${jetty.home}"));
        String contents = IO.readToString(startIni.toFile());
        MatcherAssert.assertThat("Contents", contents, Matchers.containsString("Home Ini"));
    }

    @Test
    public void testGetPaths_OnlyHome() throws IOException {
        File homeDir = MavenTestingUtils.getTestResourceDir("hb.1/home");
        ConfigSources config = new ConfigSources();
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        BaseHome hb = new BaseHome(config);
        List<Path> paths = hb.getPaths("start.d/*");
        List<String> expected = new ArrayList<>();
        expected.add("${jetty.home}/start.d/jmx.ini");
        expected.add("${jetty.home}/start.d/jndi.ini");
        expected.add("${jetty.home}/start.d/jsp.ini");
        expected.add("${jetty.home}/start.d/logging.ini");
        expected.add("${jetty.home}/start.d/ssl.ini");
        FSTest.toFsSeparators(expected);
        BaseHomeTest.assertPathList(hb, "Paths found", expected, paths);
    }

    @Test
    public void testGetPaths_OnlyHome_InisOnly() throws IOException {
        File homeDir = MavenTestingUtils.getTestResourceDir("hb.1/home");
        ConfigSources config = new ConfigSources();
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        BaseHome hb = new BaseHome(config);
        List<Path> paths = hb.getPaths("start.d/*.ini");
        List<String> expected = new ArrayList<>();
        expected.add("${jetty.home}/start.d/jmx.ini");
        expected.add("${jetty.home}/start.d/jndi.ini");
        expected.add("${jetty.home}/start.d/jsp.ini");
        expected.add("${jetty.home}/start.d/logging.ini");
        expected.add("${jetty.home}/start.d/ssl.ini");
        FSTest.toFsSeparators(expected);
        BaseHomeTest.assertPathList(hb, "Paths found", expected, paths);
    }

    @Test
    public void testGetPaths_Both() throws IOException {
        File homeDir = MavenTestingUtils.getTestResourceDir("hb.1/home");
        File baseDir = MavenTestingUtils.getTestResourceDir("hb.1/base");
        ConfigSources config = new ConfigSources();
        config.add(new JettyBaseConfigSource(baseDir.toPath()));
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        BaseHome hb = new BaseHome(config);
        List<Path> paths = hb.getPaths("start.d/*.ini");
        List<String> expected = new ArrayList<>();
        expected.add("${jetty.base}/start.d/jmx.ini");
        expected.add("${jetty.home}/start.d/jndi.ini");
        expected.add("${jetty.home}/start.d/jsp.ini");
        expected.add("${jetty.base}/start.d/logging.ini");
        expected.add("${jetty.home}/start.d/ssl.ini");
        expected.add("${jetty.base}/start.d/myapp.ini");
        FSTest.toFsSeparators(expected);
        BaseHomeTest.assertPathList(hb, "Paths found", expected, paths);
    }

    @Test
    public void testDefault() throws IOException {
        BaseHome bh = new BaseHome();
        MatcherAssert.assertThat("Home", bh.getHome(), Matchers.notNullValue());
        MatcherAssert.assertThat("Base", bh.getBase(), Matchers.notNullValue());
    }

    @Test
    public void testGetPath_Both() throws IOException {
        File homeDir = MavenTestingUtils.getTestResourceDir("hb.1/home");
        File baseDir = MavenTestingUtils.getTestResourceDir("hb.1/base");
        ConfigSources config = new ConfigSources();
        config.add(new JettyBaseConfigSource(baseDir.toPath()));
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        BaseHome hb = new BaseHome(config);
        Path startIni = hb.getPath("start.ini");
        String ref = hb.toShortForm(startIni);
        MatcherAssert.assertThat("Reference", ref, Matchers.startsWith("${jetty.base}"));
        String contents = IO.readToString(startIni.toFile());
        MatcherAssert.assertThat("Contents", contents, Matchers.containsString("Base Ini"));
    }
}

