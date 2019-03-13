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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.start.config.CommandLineConfigSource;
import org.eclipse.jetty.start.config.ConfigSources;
import org.eclipse.jetty.start.config.JettyBaseConfigSource;
import org.eclipse.jetty.start.config.JettyHomeConfigSource;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(WorkDirExtension.class)
public class ModulesTest {
    private static final String TEST_SOURCE = "<test>";

    public WorkDir testdir;

    @Test
    public void testLoadAllModules() throws IOException {
        // Test Env
        File homeDir = MavenTestingUtils.getTestResourceDir("dist-home");
        File baseDir = testdir.getEmptyPathDir().toFile();
        String[] cmdLine = new String[]{ "jetty.version=TEST" };
        // Configuration
        CommandLineConfigSource cmdLineSource = new CommandLineConfigSource(cmdLine);
        ConfigSources config = new ConfigSources();
        config.add(cmdLineSource);
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        config.add(new JettyBaseConfigSource(baseDir.toPath()));
        // Initialize
        BaseHome basehome = new BaseHome(config);
        StartArgs args = new StartArgs(basehome);
        args.parse(config);
        // Test Modules
        Modules modules = new Modules(basehome, args);
        modules.registerAll();
        // Check versions
        String platformProperty = args.getProperties().getString("java.version.platform");
        MatcherAssert.assertThat("java.version.platform", Integer.parseInt(platformProperty), Matchers.greaterThanOrEqualTo(8));
        List<String> moduleNames = new ArrayList<>();
        for (Module mod : modules) {
            // skip alpn-boot in this test (as its behavior is jdk specific)
            if (mod.getName().equals("alpn-boot")) {
                continue;
            }
            moduleNames.add(mod.getName());
        }
        List<String> expected = new ArrayList<>();
        expected.add("base");
        expected.add("extra");
        expected.add("main");
        expected.add("optional");
        ConfigurationAssert.assertContainsUnordered("All Modules", expected, moduleNames);
    }

    /**
     * Test loading of only shallow modules, not deep references.
     * In other words. ${search-dir}/modules/*.mod should be the only
     * valid references, but ${search-dir}/alt/foo/modules/*.mod should
     * not be considered valid.
     *
     * @throws IOException
     * 		on test failures
     */
    @Test
    public void testLoadShallowModulesOnly() throws IOException {
        // Test Env
        File homeDir = MavenTestingUtils.getTestResourceDir("jetty home with spaces");
        // intentionally setup top level resources dir (as this would have many
        // deep references)
        File baseDir = MavenTestingUtils.getTestResourcesDir();
        String[] cmdLine = new String[]{ "jetty.version=TEST" };
        // Configuration
        CommandLineConfigSource cmdLineSource = new CommandLineConfigSource(cmdLine);
        ConfigSources config = new ConfigSources();
        config.add(cmdLineSource);
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        config.add(new JettyBaseConfigSource(baseDir.toPath()));
        // Initialize
        BaseHome basehome = new BaseHome(config);
        StartArgs args = new StartArgs(basehome);
        args.parse(config);
        // Test Modules
        Modules modules = new Modules(basehome, args);
        modules.registerAll();
        List<String> moduleNames = new ArrayList<>();
        for (Module mod : modules) {
            moduleNames.add(mod.getName());
        }
        List<String> expected = new ArrayList<>();
        expected.add("base");
        ConfigurationAssert.assertContainsUnordered("All Modules", expected, moduleNames);
    }

    @Test
    public void testResolve_ServerHttp() throws IOException {
        // Test Env
        File homeDir = MavenTestingUtils.getTestResourceDir("dist-home");
        File baseDir = testdir.getEmptyPathDir().toFile();
        String[] cmdLine = new String[]{ "jetty.version=TEST" };
        // Configuration
        CommandLineConfigSource cmdLineSource = new CommandLineConfigSource(cmdLine);
        ConfigSources config = new ConfigSources();
        config.add(cmdLineSource);
        config.add(new JettyHomeConfigSource(homeDir.toPath()));
        config.add(new JettyBaseConfigSource(baseDir.toPath()));
        // Initialize
        BaseHome basehome = new BaseHome(config);
        StartArgs args = new StartArgs(basehome);
        args.parse(config);
        // Test Modules
        Modules modules = new Modules(basehome, args);
        modules.registerAll();
        // Enable 2 modules
        modules.enable("base", ModulesTest.TEST_SOURCE);
        modules.enable("optional", ModulesTest.TEST_SOURCE);
        // Collect active module list
        List<Module> active = modules.getEnabled();
        // Assert names are correct, and in the right order
        List<String> expectedNames = new ArrayList<>();
        expectedNames.add("optional");
        expectedNames.add("base");
        List<String> actualNames = new ArrayList<>();
        for (Module actual : active) {
            actualNames.add(actual.getName());
        }
        MatcherAssert.assertThat(("Resolved Names: " + actualNames), actualNames, Matchers.contains(expectedNames.toArray()));
        // Assert Library List
        List<String> expectedLibs = new ArrayList<>();
        expectedLibs.add("lib/optional.jar");
        expectedLibs.add("lib/base.jar");
        List<String> actualLibs = normalizeLibs(active);
        MatcherAssert.assertThat(("Resolved Libs: " + actualLibs), actualLibs, Matchers.contains(expectedLibs.toArray()));
        // Assert XML List
        List<String> expectedXmls = new ArrayList<>();
        expectedXmls.add("etc/optional.xml");
        expectedXmls.add("etc/base.xml");
        List<String> actualXmls = normalizeXmls(active);
        MatcherAssert.assertThat(("Resolved XMLs: " + actualXmls), actualXmls, Matchers.contains(expectedXmls.toArray()));
    }
}

