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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MainTest {
    @Test
    public void testBasicProcessing() throws Exception {
        List<String> cmdLineArgs = new ArrayList<>();
        Path testJettyHome = MavenTestingUtils.getTestResourceDir("dist-home").toPath().toRealPath();
        cmdLineArgs.add(("user.dir=" + testJettyHome));
        cmdLineArgs.add(("jetty.home=" + testJettyHome));
        // cmdLineArgs.add("jetty.http.port=9090");
        Main main = new Main();
        StartArgs args = main.processCommandLine(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));
        BaseHome baseHome = main.getBaseHome();
        // System.err.println(args);
        ConfigurationAssert.assertConfiguration(baseHome, args, "assert-home.txt");
        // System.err.println("StartArgs.props:");
        // args.getProperties().forEach(p->System.err.println(p));
        // System.err.println("BaseHome.props:");
        // baseHome.getConfigSources().getProps().forEach(p->System.err.println(p));
        Props props = args.getProperties();
        MatcherAssert.assertThat("Props(jetty.home)", props.getString("jetty.home"), Matchers.is(baseHome.getHome()));
        MatcherAssert.assertThat("Props(jetty.home)", props.getString("jetty.home"), Matchers.is(Matchers.not(Matchers.startsWith("file:"))));
        MatcherAssert.assertThat("Props(jetty.home.uri)", ((props.getString("jetty.home.uri")) + "/"), Matchers.is(baseHome.getHomePath().toUri().toString()));
        MatcherAssert.assertThat("Props(jetty.base)", props.getString("jetty.base"), Matchers.is(baseHome.getBase()));
        MatcherAssert.assertThat("Props(jetty.base)", props.getString("jetty.base"), Matchers.is(Matchers.not(Matchers.startsWith("file:"))));
        MatcherAssert.assertThat("Props(jetty.base.uri)", ((props.getString("jetty.base.uri")) + "/"), Matchers.is(baseHome.getBasePath().toUri().toString()));
        MatcherAssert.assertThat("System.getProperty(jetty.home)", System.getProperty("jetty.home"), Matchers.is(baseHome.getHome()));
        MatcherAssert.assertThat("System.getProperty(jetty.home)", System.getProperty("jetty.home"), Matchers.is(Matchers.not(Matchers.startsWith("file:"))));
        MatcherAssert.assertThat("System.getProperty(jetty.base)", System.getProperty("jetty.base"), Matchers.is(baseHome.getBase()));
        MatcherAssert.assertThat("System.getProperty(jetty.base)", System.getProperty("jetty.base"), Matchers.is(Matchers.not(Matchers.startsWith("file:"))));
    }

    @Test
    public void testStopProcessing() throws Exception {
        List<String> cmdLineArgs = new ArrayList<>();
        cmdLineArgs.add("--stop");
        cmdLineArgs.add("STOP.PORT=10000");
        cmdLineArgs.add("STOP.KEY=foo");
        cmdLineArgs.add("STOP.WAIT=300");
        Main main = new Main();
        StartArgs args = main.processCommandLine(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));
        // System.err.println(args);
        // assertEquals(0, args.getEnabledModules().size(), "--stop should not build module tree");
        Assertions.assertEquals("10000", args.getProperties().getString("STOP.PORT"), "--stop missing port");
        Assertions.assertEquals("foo", args.getProperties().getString("STOP.KEY"), "--stop missing key");
        Assertions.assertEquals("300", args.getProperties().getString("STOP.WAIT"), "--stop missing wait");
    }

    @Test
    public void testWithCommandLine() throws Exception {
        List<String> cmdLineArgs = new ArrayList<>();
        Path homePath = MavenTestingUtils.getTestResourceDir("dist-home").toPath().toRealPath();
        cmdLineArgs.add(("jetty.home=" + (homePath.toString())));
        cmdLineArgs.add(("user.dir=" + (homePath.toString())));
        // JVM args
        cmdLineArgs.add("--exec");
        cmdLineArgs.add("-Xms1024m");
        cmdLineArgs.add("-Xmx1024m");
        // Arbitrary Libs
        Path extraJar = MavenTestingUtils.getTestResourceFile("extra-libs/example.jar").toPath().toRealPath();
        Path extraDir = MavenTestingUtils.getTestResourceDir("extra-resources").toPath().toRealPath();
        MatcherAssert.assertThat(("Extra Jar exists: " + extraJar), Files.exists(extraJar), Matchers.is(true));
        MatcherAssert.assertThat(("Extra Dir exists: " + extraDir), Files.exists(extraDir), Matchers.is(true));
        StringBuilder lib = new StringBuilder();
        lib.append("--lib=");
        lib.append(extraJar.toString());
        lib.append(File.pathSeparator);
        lib.append(extraDir.toString());
        cmdLineArgs.add(lib.toString());
        // Arbitrary XMLs
        cmdLineArgs.add("config.xml");
        cmdLineArgs.add("config-foo.xml");
        cmdLineArgs.add("config-bar.xml");
        Main main = new Main();
        StartArgs args = main.processCommandLine(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));
        BaseHome baseHome = main.getBaseHome();
        MatcherAssert.assertThat("jetty.home", baseHome.getHome(), Matchers.is(homePath.toString()));
        MatcherAssert.assertThat("jetty.base", baseHome.getBase(), Matchers.is(homePath.toString()));
        ConfigurationAssert.assertConfiguration(baseHome, args, "assert-home-with-jvm.txt");
    }

    @Test
    public void testWithModules() throws Exception {
        List<String> cmdLineArgs = new ArrayList<>();
        Path homePath = MavenTestingUtils.getTestResourceDir("dist-home").toPath().toRealPath();
        cmdLineArgs.add(("jetty.home=" + homePath));
        cmdLineArgs.add(("user.dir=" + homePath));
        cmdLineArgs.add("java.version=1.8.0_31");
        // Modules
        cmdLineArgs.add("--module=optional,extra");
        Main main = new Main();
        StartArgs args = main.processCommandLine(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));
        BaseHome baseHome = main.getBaseHome();
        MatcherAssert.assertThat("jetty.home", baseHome.getHome(), Matchers.is(homePath.toString()));
        MatcherAssert.assertThat("jetty.base", baseHome.getBase(), Matchers.is(homePath.toString()));
        ConfigurationAssert.assertConfiguration(baseHome, args, "assert-home-with-module.txt");
    }

    @Test
    public void testJettyHomeWithSpaces() throws Exception {
        Path distPath = MavenTestingUtils.getTestResourceDir("dist-home").toPath().toRealPath();
        Path homePath = MavenTestingUtils.getTargetTestingPath().resolve("dist home with spaces");
        IO.copy(distPath.toFile(), homePath.toFile());
        homePath.resolve("lib/a library.jar").toFile().createNewFile();
        List<String> cmdLineArgs = new ArrayList<>();
        cmdLineArgs.add(("user.dir=" + homePath));
        cmdLineArgs.add(("jetty.home=" + homePath));
        cmdLineArgs.add("--lib=lib/a library.jar");
        Main main = new Main();
        StartArgs args = main.processCommandLine(cmdLineArgs.toArray(new String[cmdLineArgs.size()]));
        BaseHome baseHome = main.getBaseHome();
        MatcherAssert.assertThat("jetty.home", baseHome.getHome(), Matchers.is(homePath.toString()));
        MatcherAssert.assertThat("jetty.base", baseHome.getBase(), Matchers.is(homePath.toString()));
        ConfigurationAssert.assertConfiguration(baseHome, args, "assert-home-with-spaces.txt");
    }
}

