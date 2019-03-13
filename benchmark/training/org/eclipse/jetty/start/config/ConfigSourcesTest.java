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
package org.eclipse.jetty.start.config;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.eclipse.jetty.start.TestEnv;
import org.eclipse.jetty.start.UsageException;
import org.eclipse.jetty.toolchain.test.FS;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


@ExtendWith(WorkDirExtension.class)
public class ConfigSourcesTest {
    public WorkDir testdir;

    @Test
    public void testOrder_BasicConfig() throws IOException {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1");
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[0];
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyBaseConfigSource(base));
        sources.add(new JettyHomeConfigSource(home));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", "${jetty.home}");
    }

    @Test
    public void testOrder_With1ExtraConfig() throws IOException {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common.toFile());
        common = common.toRealPath();
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("--include-jetty-dir=" + (common.toString())));
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[0];
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home.toRealPath()));
        sources.add(new JettyBaseConfigSource(base.toRealPath()));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", common.toString(), "${jetty.home}");
    }

    @Test
    public void testCommandLine_1Extra_FromSimpleProp() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        TestEnv.makeFile(common, "start.ini", "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1");
        ConfigSources sources = new ConfigSources();
        // Simple command line reference to include-jetty-dir via property (also on command line)
        String[] cmdLine = new String[]{ // property
        "my.common=" + (common.toString()), // reference via property
        "--include-jetty-dir=${my.common}" };
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", "${my.common}", "${jetty.home}");
        assertDirOrder(sources, base, common, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "8080");// from 'common'

    }

    @Test
    public void testCommandLine_1Extra_FromPropPrefix() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create opt
        Path opt = testdir.getPathFile("opt");
        FS.ensureEmpty(opt);
        // Create common
        Path common = opt.resolve("common");
        FS.ensureEmpty(common);
        TestEnv.makeFile(common, "start.ini", "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1");
        String dirRef = ("${my.opt}" + (File.separator)) + "common";
        ConfigSources sources = new ConfigSources();
        // Simple command line reference to include-jetty-dir via property (also on command line)
        String[] cmdLine = new String[]{ // property to 'opt' dir
        "my.opt=" + (opt.toString()), // reference via property prefix
        "--include-jetty-dir=" + dirRef };
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", dirRef, "${jetty.home}");
        assertDirOrder(sources, base, common, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "8080");// from 'common'

    }

    @Test
    public void testCommandLine_1Extra_FromCompoundProp() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create opt
        Path opt = testdir.getPathFile("opt");
        FS.ensureEmpty(opt);
        // Create common
        Path common = opt.resolve("common");
        FS.ensureEmpty(common);
        TestEnv.makeFile(common, "start.ini", "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1");
        String dirRef = ("${my.opt}" + (File.separator)) + "${my.dir}";
        ConfigSources sources = new ConfigSources();
        // Simple command line reference to include-jetty-dir via property (also on command line)
        String[] cmdLine = new String[]{ // property to 'opt' dir
        "my.opt=" + (opt.toString()), // property to commmon dir name
        "my.dir=common", // reference via property prefix
        "--include-jetty-dir=" + dirRef };
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", dirRef, "${jetty.home}");
        assertDirOrder(sources, base, common, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "8080");// from 'common'

    }

    @Test
    public void testRefCommon() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        TestEnv.makeFile(common, "start.ini", "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("--include-jetty-dir=" + (common.toString())));
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[0];
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", common.toString(), "${jetty.home}");
        assertDirOrder(sources, base, common, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "8080");// from 'common'

    }

    @Test
    public void testRefCommonAndCorp() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        TestEnv.makeFile(common, "start.ini", "jetty.http.port=8080");
        // Create corp
        Path corp = testdir.getPathFile("corp");
        FS.ensureEmpty(corp);
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("--include-jetty-dir=" + (common.toString())), ("--include-jetty-dir=" + (corp.toString())));
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[0];
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", common.toString(), corp.toString(), "${jetty.home}");
        assertDirOrder(sources, base, common, corp, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "8080");// from 'common'

    }

    @Test
    public void testRefCommonRefCorp() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create corp
        Path corp = testdir.getPathFile("corp");
        FS.ensureEmpty(corp);
        // 
        TestEnv.makeFile(corp, "start.ini", "jetty.http.port=9090");
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        // 
        // 
        TestEnv.makeFile(common, "start.ini", ("--include-jetty-dir=" + (corp.toString())), "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("--include-jetty-dir=" + (common.toString())));
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[0];
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", common.toString(), corp.toString(), "${jetty.home}");
        assertDirOrder(sources, base, common, corp, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "8080");// from 'common'

    }

    @Test
    public void testRefCommonRefCorp_FromSimpleProps() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create corp
        Path corp = testdir.getPathFile("corp");
        FS.ensureEmpty(corp);
        // 
        TestEnv.makeFile(corp, "start.ini", "jetty.http.port=9090");
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        // 
        // 
        // 
        TestEnv.makeFile(common, "start.ini", ("my.corp=" + (corp.toString())), "--include-jetty-dir=${my.corp}", "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("my.common=" + (common.toString())), "--include-jetty-dir=${my.common}");
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[0];
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", "${my.common}", "${my.corp}", "${jetty.home}");
        assertDirOrder(sources, base, common, corp, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "8080");// from 'common'

    }

    @Test
    public void testRefCommonRefCorp_CmdLineRef() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create devops
        Path devops = testdir.getPathFile("devops");
        FS.ensureEmpty(devops);
        // 
        // 
        TestEnv.makeFile(devops, "start.ini", "--module=logging", "jetty.http.port=2222");
        // Create corp
        Path corp = testdir.getPathFile("corp");
        FS.ensureEmpty(corp);
        // 
        TestEnv.makeFile(corp, "start.ini", "jetty.http.port=9090");
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        // 
        // 
        TestEnv.makeFile(common, "start.ini", ("--include-jetty-dir=" + (corp.toString())), "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("--include-jetty-dir=" + (common.toString())));
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[]{ // command line provided include-jetty-dir ref
        "--include-jetty-dir=" + (devops.toString()) };
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", devops.toString(), common.toString(), corp.toString(), "${jetty.home}");
        assertDirOrder(sources, base, devops, common, corp, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "2222");// from 'common'

    }

    @Test
    public void testRefCommonRefCorp_CmdLineProp() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create corp
        Path corp = testdir.getPathFile("corp");
        FS.ensureEmpty(corp);
        // 
        TestEnv.makeFile(corp, "start.ini", "jetty.http.port=9090");
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        // 
        // 
        TestEnv.makeFile(common, "start.ini", ("--include-jetty-dir=" + (corp.toString())), "jetty.http.port=8080");
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("--include-jetty-dir=" + (common.toString())));
        ConfigSources sources = new ConfigSources();
        String[] cmdLine = new String[]{ // command line property should override all others
        "jetty.http.port=7070" };
        sources.add(new CommandLineConfigSource(cmdLine));
        sources.add(new JettyHomeConfigSource(home));
        sources.add(new JettyBaseConfigSource(base));
        assertIdOrder(sources, "<command-line>", "${jetty.base}", common.toString(), corp.toString(), "${jetty.home}");
        assertDirOrder(sources, base, common, corp, home);
        assertProperty(sources, "jetty.http.host", "127.0.0.1");
        assertProperty(sources, "jetty.http.port", "7070");// from <command-line>

    }

    @Test
    public void testBadDoubleRef() throws Exception {
        // Create home
        Path home = testdir.getPathFile("home");
        FS.ensureEmpty(home);
        TestEnv.copyTestDir("dist-home", home);
        // Create common
        Path common = testdir.getPathFile("common");
        FS.ensureEmpty(common);
        // Create corp
        Path corp = testdir.getPathFile("corp");
        FS.ensureEmpty(corp);
        // standard property
        // INTENTIONAL BAD Reference (duplicate)
        TestEnv.makeFile(corp, "start.ini", "jetty.http.port=9090", ("--include-jetty-dir=" + (common.toString())));
        // Populate common
        // standard property
        // reference to corp
        TestEnv.makeFile(common, "start.ini", "jetty.http.port=8080", ("--include-jetty-dir=" + (corp.toString())));
        // Create base
        Path base = testdir.getPathFile("base");
        FS.ensureEmpty(base);
        // 
        // 
        TestEnv.makeFile(base, "start.ini", "jetty.http.host=127.0.0.1", ("--include-jetty-dir=" + (common.toString())));
        ConfigSources sources = new ConfigSources();
        UsageException e = Assertions.assertThrows(UsageException.class, () -> {
            String[] cmdLine = new String[0];
            sources.add(new CommandLineConfigSource(cmdLine));
            sources.add(new JettyHomeConfigSource(home));
            sources.add(new JettyBaseConfigSource(base));
        });
        MatcherAssert.assertThat("UsageException", e.getMessage(), Matchers.containsString("Duplicate"));
    }
}

