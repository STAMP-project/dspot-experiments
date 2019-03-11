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
public class ModuleTest {
    public WorkDir testdir;

    @Test
    public void testLoadMain() throws IOException {
        // Test Env
        Path homeDir = MavenTestingUtils.getTestResourcePathDir("dist-home");
        Path baseDir = testdir.getEmptyPathDir();
        String[] cmdLine = new String[]{ "jetty.version=TEST" };
        // Configuration
        CommandLineConfigSource cmdLineSource = new CommandLineConfigSource(cmdLine);
        ConfigSources config = new ConfigSources();
        config.add(cmdLineSource);
        config.add(new JettyHomeConfigSource(homeDir));
        config.add(new JettyBaseConfigSource(baseDir));
        // Initialize
        BaseHome basehome = new BaseHome(config);
        File file = MavenTestingUtils.getTestResourceFile("dist-home/modules/main.mod");
        Module module = new Module(basehome, file.toPath());
        MatcherAssert.assertThat("Module Name", module.getName(), Matchers.is("main"));
        MatcherAssert.assertThat("Module Depends Size", module.getDepends().size(), Matchers.is(1));
        MatcherAssert.assertThat("Module Depends", module.getDepends(), Matchers.containsInAnyOrder("base"));
        MatcherAssert.assertThat("Module Xmls Size", module.getXmls().size(), Matchers.is(1));
        MatcherAssert.assertThat("Module Lib Size", module.getLibs().size(), Matchers.is(2));
        MatcherAssert.assertThat("Module Lib", module.getLibs(), Matchers.contains("lib/main.jar", "lib/other.jar"));
    }
}

