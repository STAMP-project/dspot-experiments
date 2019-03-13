/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.yarn.scripts;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.apache.drill.yarn.scripts.ScriptUtils.DrillbitRun.DRILLBIT_RESTART;
import static org.apache.drill.yarn.scripts.ScriptUtils.DrillbitRun.DRILLBIT_RUN;
import static org.apache.drill.yarn.scripts.ScriptUtils.DrillbitRun.DRILLBIT_START;
import static org.apache.drill.yarn.scripts.ScriptUtils.DrillbitRun.DRILLBIT_STATUS;
import static org.apache.drill.yarn.scripts.ScriptUtils.DrillbitRun.DRILLBIT_STOP;


/**
 * Unit tests to test the many ways that the Drill shell scripts can run.
 * Since it would be difficult to test options using the actual Drillbit, the
 * scripts make use of a special test fixture in runbit: the ability to pass
 * a "wrapper" script to run in place of the Drillit. That script probes stderr,
 * stdout and log files, and writes its arguments (which is the Drillbit launch
 * command) to a file. As a result, we can capture this output and analyze it
 * to ensure we are passing the right arguments to the Drillbit, and that output
 * is going to the right destinations.
 */
// Turned of by default: works only in a developer setup
@Ignore
public class TestScripts {
    static ScriptUtils context;

    /**
     * Test the simplest case: use the $DRILL_HOME/conf directory and default log
     * location. Non-existent drill-env.sh and drill-config.sh files. Everything
     * is at its Drill-provided defaults. Then, try overriding each user-settable
     * environment variable in the environment (which simulates what YARN might
     * do.)
     */
    @Test
    public void testStockCombined() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        // No drill-env.sh, no distrib-env.sh
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateStockArgs();
            result.validateClassPath(ScriptUtils.stdCp);
            result.validateStdOut();
            result.validateStdErr();
            result.validateDrillLog();
        }
        // As above, but pass an argument.
        {
            String propArg = "-Dproperty=value";
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withArg(propArg).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateStdOut();
            result.validateArg(propArg);
        }
        // Custom Java opts to achieve the same result
        {
            String propArg = "-Dproperty=value";
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_JAVA_OPTS", propArg).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateStockArgs();// Should not lose standard JVM args

            result.validateStdOut();
            result.validateArg(propArg);
        }
        // Custom Drillbit Java Opts to achieve the same result
        {
            String propArg = "-Dproperty2=value2";
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILLBIT_JAVA_OPTS", propArg).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateStockArgs();// Should not lose standard JVM args

            result.validateStdOut();
            result.validateArg(propArg);
        }
        // Both sets of options
        {
            String propArg = "-Dproperty=value";
            String propArg2 = "-Dproperty2=value2";
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_JAVA_OPTS", propArg).addEnv("DRILLBIT_JAVA_OPTS", propArg2).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(new String[]{ propArg, propArg2 });
        }
        // Custom heap memory
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_HEAP", "5G").run();
            result.validateArgs(new String[]{ "-Xms5G", "-Xmx5G" });
        }
        // Custom direct memory
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_MAX_DIRECT_MEMORY", "7G").run();
            result.validateArg("-XX:MaxDirectMemorySize=7G");
        }
        // Enable GC logging
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("SERVER_LOG_GC", "1").run();
            String logTail = (TestScripts.context.testLogDir.getName()) + "/drillbit.gc";
            result.validateArgRegex(("-Xloggc:.*/" + logTail));
        }
        // Code cache size
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILLBIT_CODE_CACHE_SIZE", "2G").run();
            result.validateArg("-XX:ReservedCodeCacheSize=2G");
        }
    }

    /**
     * Use the "stock" setup, but add each custom bit of the class path to ensure
     * it is passed to the Drillbit.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testClassPath() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        File extrasDir = TestScripts.context.createDir(new File(TestScripts.context.testDir, "extras"));
        File hadoopJar = TestScripts.context.makeDummyJar(extrasDir, "hadoop");
        File hbaseJar = TestScripts.context.makeDummyJar(extrasDir, "hbase");
        File prefixJar = TestScripts.context.makeDummyJar(extrasDir, "prefix");
        File cpJar = TestScripts.context.makeDummyJar(extrasDir, "cp");
        File extnJar = TestScripts.context.makeDummyJar(extrasDir, "extn");
        File toolsJar = TestScripts.context.makeDummyJar(extrasDir, "tools");
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_CLASSPATH_PREFIX", prefixJar.getAbsolutePath()).run();
            result.validateClassPath(prefixJar.getAbsolutePath());
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_TOOL_CP", toolsJar.getAbsolutePath()).run();
            result.validateClassPath(toolsJar.getAbsolutePath());
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("HADOOP_CLASSPATH", hadoopJar.getAbsolutePath()).run();
            result.validateClassPath(hadoopJar.getAbsolutePath());
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("HBASE_CLASSPATH", hbaseJar.getAbsolutePath()).run();
            result.validateClassPath(hbaseJar.getAbsolutePath());
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("EXTN_CLASSPATH", extnJar.getAbsolutePath()).run();
            result.validateClassPath(extnJar.getAbsolutePath());
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_CLASSPATH", cpJar.getAbsolutePath()).run();
            result.validateClassPath(cpJar.getAbsolutePath());
        }
        // Site jars not on path if not created
        File siteJars = new File(siteDir, "jars");
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).run();
            Assert.assertFalse(result.classPathContains(siteJars.getAbsolutePath()));
        }
        // Site/jars on path if exists
        TestScripts.context.createDir(siteJars);
        TestScripts.context.makeDummyJar(siteJars, "site");
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).run();
            result.validateClassPath(((siteJars.getAbsolutePath()) + "/*"));
        }
    }

    /**
     * Create a custom log folder location.
     */
    @Test
    public void testLogDir() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        File logsDir = TestScripts.context.createDir(new File(TestScripts.context.testDir, "logs"));
        TestScripts.context.removeDir(new File(TestScripts.context.testDrillHome, "log"));
        {
            String logPath = logsDir.getAbsolutePath();
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_LOG_DIR", logPath).withLogDir(logsDir).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(new String[]{ ("-Dlog.path=" + logPath) + "/drillbit.log", ("-Dlog.query.path=" + logPath) + "/drillbit_queries.json" });
            result.validateStdOut();
            result.validateStdErr();
            result.validateDrillLog();
        }
    }

    /**
     * Create a custom Java lib path. This uses the new DRILL_JAVA_LIB_PATH
     * variable.
     */
    @Test
    public void testLibPath() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        File logsDir = TestScripts.context.createDir(new File(TestScripts.context.testDir, "logs"));
        TestScripts.context.removeDir(new File(TestScripts.context.testDrillHome, "log"));
        {
            String logPath = logsDir.getAbsolutePath();
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_LOG_DIR", logPath).withLogDir(logsDir).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(new String[]{ ("-Dlog.path=" + logPath) + "/drillbit.log", ("-Dlog.query.path=" + logPath) + "/drillbit_queries.json" });
            result.validateStdOut();
            result.validateStdErr();
            result.validateDrillLog();
        }
    }

    /**
     * Try setting custom environment variable values in drill-env.sh in the
     * $DRILL_HOME/conf location.
     */
    @Test
    public void testDrillEnv() throws IOException {
        doEnvFileTest("drill-env.sh");
    }

    /**
     * Repeat the above test using distrib-env.sh in the $DRILL_HOME/conf
     * location.
     */
    @Test
    public void testDistribEnv() throws IOException {
        doEnvFileTest("distrib-env.sh");
    }

    @Test
    public void testDistribEnvWithNegativeCond() throws IOException {
        // Construct condition map
        final Map<String, String> conditions = new HashMap<>();
        conditions.put("DRILLBIT_CONTEXT", "0");
        final String[] expectedArgs = new String[]{ "-XX:ReservedCodeCacheSize=1G" };
        doEnvFileWithConditionTest("distrib-env.sh", conditions, expectedArgs);
    }

    @Test
    public void testDistribEnvWithPositiveCond() throws IOException {
        // Construct condition map
        final Map<String, String> conditions = new HashMap<>();
        conditions.put("DRILLBIT_CONTEXT", "1");
        final String[] expectedArgs = new String[]{ "-XX:ReservedCodeCacheSize=2G" };
        doEnvFileWithConditionTest("distrib-env.sh", conditions, expectedArgs);
    }

    /**
     * Test that drill-env.sh overrides distrib-env.sh, and that the environment
     * overrides both. Assumes the basics were tested above.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDrillAndDistribEnv() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        Map<String, String> distribEnv = new HashMap<>();
        distribEnv.put("DRILL_HEAP", "5G");
        distribEnv.put("DRILL_MAX_DIRECT_MEMORY", "7G");
        TestScripts.context.createEnvFile(new File(siteDir, "distrib-env.sh"), distribEnv, false);
        Map<String, String> drillEnv = new HashMap<>();
        drillEnv.put("DRILL_HEAP", "6G");
        drillEnv.put("DRILL_MAX_DIRECT_MEMORY", "9G");
        TestScripts.context.createEnvFile(new File(siteDir, "drill-env.sh"), drillEnv, false);
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).run();
            Assert.assertEquals(0, result.returnCode);
            String[] expectedArgs = new String[]{ "-Xms6G", "-Xmx6G", "-XX:MaxDirectMemorySize=9G", "-XX:ReservedCodeCacheSize=1024m"// Default
             };
            result.validateArgs(expectedArgs);
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_MAX_DIRECT_MEMORY", "5G").run();
            Assert.assertEquals(0, result.returnCode);
            String[] expectedArgs = new String[]{ "-Xms6G", "-Xmx6G", "-XX:MaxDirectMemorySize=5G", "-XX:ReservedCodeCacheSize=1024m"// Default
             };
            result.validateArgs(expectedArgs);
        }
    }

    @Test
    public void testBadSiteDir() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.removeDir(siteDir);
        // Directory does not exist.
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).run();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stderr.contains("Config dir does not exist"));
        }
        // Not a directory
        TestScripts.context.writeFile(siteDir, "dummy");
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).run();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stderr.contains("Config dir does not exist"));
        }
        // Directory exists, but drill-override.conf does not
        siteDir.delete();
        TestScripts.context.createDir(siteDir);
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).run();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stderr.contains("Drill config file missing"));
        }
    }

    /**
     * Move configuration to a site folder out of $DRILL_HOME/conf. The site
     * folder can contain code (which is why we call it "site" and not "config".)
     * The site directory can be passed to the Drillbit in several ways.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSiteDir() throws IOException {
        TestScripts.context.createMockDistrib();
        File confDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createDir(confDir);
        File siteDir = new File(TestScripts.context.testDir, "site");
        TestScripts.context.createMockConf(siteDir);
        // Dummy drill-env.sh to simulate the shipped "example" file.
        TestScripts.context.writeFile(new File(confDir, "drill-env.sh"), ("#!/bin/bash\n" + "# Example file"));
        File siteJars = new File(siteDir, "jars");
        Map<String, String> distribEnv = new HashMap<>();
        distribEnv.put("DRILL_HEAP", "5G");
        distribEnv.put("DRILL_MAX_DIRECT_MEMORY", "7G");
        TestScripts.context.createEnvFile(new File(confDir, "distrib-env.sh"), distribEnv, false);
        Map<String, String> drillEnv = new HashMap<>();
        drillEnv.put("DRILL_HEAP", "6G");
        drillEnv.put("DRILL_MAX_DIRECT_MEMORY", "9G");
        TestScripts.context.createEnvFile(new File(siteDir, "drill-env.sh"), drillEnv, false);
        String[] expectedArgs = new String[]{ "-Xms6G", "-Xmx6G", "-XX:MaxDirectMemorySize=9G", "-XX:ReservedCodeCacheSize=1024m"// Default
         };
        // Site set using argument
        {
            // Use --config explicitly
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withArg("--config").withArg(siteDir.getAbsolutePath()).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(expectedArgs);
            result.validateClassPath(siteDir.getAbsolutePath());
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun().withArg("--config").withArg(siteDir.getAbsolutePath()).withArg(DRILLBIT_RUN).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(expectedArgs);
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(expectedArgs);
        }
        // Site argument and argument to Drillbit
        {
            String propArg = "-Dproperty=value";
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).withArg(propArg).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(expectedArgs);
            result.validateArg(propArg);
        }
        // Set as an environment variable
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).addEnv("DRILL_CONF_DIR", siteDir.getAbsolutePath()).run();
            Assert.assertEquals(0, result.returnCode);
            result.validateArgs(expectedArgs);
        }
        // Site jars not on path if not created
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).run();
            Assert.assertFalse(result.classPathContains(siteJars.getAbsolutePath()));
        }
        // Site/jars on path if exists
        TestScripts.context.createDir(siteJars);
        TestScripts.context.makeDummyJar(siteJars, "site");
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).run();
            Assert.assertTrue(result.classPathContains(((siteJars.getAbsolutePath()) + "/*")));
        }
    }

    /**
     * Test the Java library path. Three sources:
     * <ol>
     * <li>DRILL_JAVA_LIB_PATH Set in drill-env.sh</li>
     * <li>DOY_JAVA_LIB_PATH passed in from an env. var.</li>
     * <li>$DRILL_SITE/lib, if it exists.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testJavaLibDir() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        // Stock run: no lib dir.
        String prefix = "-Djava.library.path=";
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).run();
            Assert.assertFalse(result.containsArgRegex((prefix + ".*")));
            Assert.assertNull(result.libPath);
        }
        // Old-style argument in DRILL_JAVA_OPTS
        {
            Map<String, String> env = new HashMap<>();
            env.put("DRILL_JAVA_OPTS", (prefix + "/foo/bar:/foo/mumble"));
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withEnvironment(env).run();
            Assert.assertTrue(result.containsArgRegex((prefix + ".*")));
            Assert.assertNotNull(result.libPath);
            Assert.assertEquals(2, result.libPath.length);
            Assert.assertEquals("/foo/bar", result.libPath[0]);
            Assert.assertEquals("/foo/mumble", result.libPath[1]);
        }
        // New-style argument in DRILL_JAVA_LIB_PATH
        {
            Map<String, String> env = new HashMap<>();
            env.put("DRILL_JAVA_LIB_PATH", "/foo/bar:/foo/mumble");
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withEnvironment(env).run();
            Assert.assertTrue(result.containsArgRegex((prefix + ".*")));
            Assert.assertNotNull(result.libPath);
            Assert.assertEquals(2, result.libPath.length);
            Assert.assertEquals("/foo/bar", result.libPath[0]);
            Assert.assertEquals("/foo/mumble", result.libPath[1]);
        }
        // YARN argument in DOY_JAVA_LIB_PATH
        {
            Map<String, String> env = new HashMap<>();
            env.put("DOY_JAVA_LIB_PATH", "/foo/bar:/foo/mumble");
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withEnvironment(env).run();
            Assert.assertTrue(result.containsArgRegex((prefix + ".*")));
            Assert.assertNotNull(result.libPath);
            Assert.assertEquals(2, result.libPath.length);
            Assert.assertEquals("/foo/bar", result.libPath[0]);
            Assert.assertEquals("/foo/mumble", result.libPath[1]);
        }
        // Both DRILL_JAVA_LIB_PATH and DOY_JAVA_LIB_PATH
        {
            Map<String, String> env = new HashMap<>();
            env.put("DRILL_JAVA_LIB_PATH", "/foo/bar:/foo/mumble");
            env.put("DOY_JAVA_LIB_PATH", "/doy/bar:/doy/mumble");
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withEnvironment(env).run();
            Assert.assertTrue(result.containsArgRegex((prefix + ".*")));
            Assert.assertNotNull(result.libPath);
            Assert.assertEquals(4, result.libPath.length);
            Assert.assertEquals("/doy/bar", result.libPath[0]);
            Assert.assertEquals("/doy/mumble", result.libPath[1]);
            Assert.assertEquals("/foo/bar", result.libPath[2]);
            Assert.assertEquals("/foo/mumble", result.libPath[3]);
        }
        // Site directory with a lib folder
        siteDir = new File(TestScripts.context.testDir, "site");
        TestScripts.context.createMockConf(siteDir);
        File libDir = new File(siteDir, "lib");
        TestScripts.context.createDir(libDir);
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).run();
            Assert.assertTrue(result.containsArgRegex((prefix + ".*")));
            Assert.assertNotNull(result.libPath);
            Assert.assertEquals(1, result.libPath.length);
            Assert.assertEquals(libDir.getAbsolutePath(), result.libPath[0]);
        }
        // The whole enchilada: all three settings.
        {
            Map<String, String> env = new HashMap<>();
            env.put("DRILL_JAVA_LIB_PATH", "/foo/bar:/foo/mumble");
            env.put("DOY_JAVA_LIB_PATH", "/doy/bar:/doy/mumble");
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RUN).withSite(siteDir).withEnvironment(env).run();
            Assert.assertTrue(result.containsArgRegex((prefix + ".*")));
            Assert.assertNotNull(result.libPath);
            Assert.assertEquals(5, result.libPath.length);
            Assert.assertEquals(libDir.getAbsolutePath(), result.libPath[0]);
            Assert.assertEquals("/doy/bar", result.libPath[1]);
            Assert.assertEquals("/doy/mumble", result.libPath[2]);
            Assert.assertEquals("/foo/bar", result.libPath[3]);
            Assert.assertEquals("/foo/mumble", result.libPath[4]);
        }
    }

    /**
     * Test running a (simulated) Drillbit as a daemon with start, status, stop.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testStockDaemon() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        // No drill-env.sh, no distrib-env.sh
        File pidFile;
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_START).start();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateStockArgs();
            result.validateClassPath(ScriptUtils.stdCp);
            Assert.assertTrue(result.stdout.contains("Starting drillbit, logging"));
            Assert.assertTrue(result.log.contains("Starting drillbit on"));
            Assert.assertTrue(result.log.contains("Drill Log Message"));
            Assert.assertTrue(result.out.contains("Drill Stdout Message"));
            Assert.assertTrue(result.out.contains("Stderr Message"));
            pidFile = result.pidFile;
        }
        // Save the pid file for reuse.
        Assert.assertTrue(pidFile.exists());
        File saveDir = new File(TestScripts.context.testDir, "save");
        TestScripts.context.createDir(saveDir);
        File savedPidFile = new File(saveDir, pidFile.getName());
        TestScripts.context.copyFile(pidFile, savedPidFile);
        // Status should be running
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STATUS).run();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertTrue(result.stdout.contains("drillbit is running"));
        }
        // Start should refuse to start a second Drillbit.
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_START).start();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stdout.contains("drillbit is already running as process"));
        }
        // Normal start, allow normal shutdown
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STOP).run();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertTrue(result.log.contains("Terminating drillbit pid"));
            Assert.assertTrue(result.stdout.contains("Stopping drillbit"));
        }
        // Status should report no drillbit (no pid file)
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STATUS).run();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stdout.contains("drillbit is not running"));
        }
        // Stop should report no pid file
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STOP).run();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stdout.contains("No drillbit to stop because no pid file"));
        }
        // Get nasty. Put the pid file back. But, there is no process with that pid.
        TestScripts.context.copyFile(savedPidFile, pidFile);
        // Status should now complain.
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STATUS).run();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stdout.contains("file is present but drillbit is not running"));
        }
        // As should stop.
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STOP).run();
            Assert.assertEquals(1, result.returnCode);
            Assert.assertTrue(result.stdout.contains("No drillbit to stop because kill -0 of pid"));
        }
    }

    @Test
    public void testStockDaemonWithArg() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        // As above, but pass an argument.
        {
            String propArg = "-Dproperty=value";
            ScriptUtils.DrillbitRun runner = new ScriptUtils.DrillbitRun(DRILLBIT_START);
            runner.withArg(propArg);
            ScriptUtils.RunResult result = runner.start();
            Assert.assertEquals(0, result.returnCode);
            result.validateArg(propArg);
        }
        validateAndCloseDaemon(null);
    }

    /**
     * The Daemon process creates a pid file. Verify that the DRILL_PID_DIR can be
     * set to put the pid file in a custom location. The test is done with the
     * site (conf) dir in the default location.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testPidDir() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        File pidDir = TestScripts.context.createDir(new File(TestScripts.context.testDir, "pid"));
        Map<String, String> drillEnv = new HashMap<>();
        drillEnv.put("DRILL_PID_DIR", pidDir.getAbsolutePath());
        TestScripts.context.createEnvFile(new File(siteDir, "drill-env.sh"), drillEnv, false);
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_START).withPidDir(pidDir).start();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertTrue(result.pidFile.getParentFile().equals(pidDir));
            Assert.assertTrue(result.pidFile.exists());
        }
        validateAndCloseDaemon(null);
    }

    /**
     * Test a custom site directory with the Drill daemon process. The custom
     * directory contains a drill-env.sh with a custom option. Verify that that
     * option is picked up when starting Drill.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSiteDirWithDaemon() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDir, "site");
        TestScripts.context.createMockConf(siteDir);
        Map<String, String> drillEnv = new HashMap<>();
        drillEnv.put("DRILL_MAX_DIRECT_MEMORY", "9G");
        TestScripts.context.createEnvFile(new File(siteDir, "drill-env.sh"), drillEnv, false);
        // Use the -site (--config) option.
        {
            ScriptUtils.DrillbitRun runner = new ScriptUtils.DrillbitRun(DRILLBIT_START);
            runner.withSite(siteDir);
            ScriptUtils.RunResult result = runner.start();
            Assert.assertEquals(0, result.returnCode);
            result.validateArg("-XX:MaxDirectMemorySize=9G");
        }
        validateAndCloseDaemon(siteDir);
        // Set an env var.
        {
            ScriptUtils.DrillbitRun runner = new ScriptUtils.DrillbitRun(DRILLBIT_START);
            runner.addEnv("DRILL_CONF_DIR", siteDir.getAbsolutePath());
            ScriptUtils.RunResult result = runner.start();
            Assert.assertEquals(0, result.returnCode);
            result.validateArg("-XX:MaxDirectMemorySize=9G");
        }
        validateAndCloseDaemon(siteDir);
    }

    /**
     * Launch the Drill daemon using a custom log file location. The config is in
     * the default location.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testLogDirWithDaemon() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        File logsDir = TestScripts.context.createDir(new File(TestScripts.context.testDir, "logs"));
        TestScripts.context.removeDir(new File(TestScripts.context.testDrillHome, "log"));
        Map<String, String> drillEnv = new HashMap<>();
        drillEnv.put("DRILL_LOG_DIR", logsDir.getAbsolutePath());
        TestScripts.context.createEnvFile(new File(siteDir, "drill-env.sh"), drillEnv, false);
        {
            ScriptUtils.DrillbitRun runner = new ScriptUtils.DrillbitRun(DRILLBIT_START);
            runner.withLogDir(logsDir);
            ScriptUtils.RunResult result = runner.start();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertNotNull(result.logFile);
            Assert.assertTrue(result.logFile.getParentFile().equals(logsDir));
            Assert.assertTrue(result.logFile.exists());
            Assert.assertNotNull(result.outFile);
            Assert.assertTrue(result.outFile.getParentFile().equals(logsDir));
            Assert.assertTrue(result.outFile.exists());
        }
        validateAndCloseDaemon(null);
    }

    /**
     * Some distributions create symlinks to drillbit.sh in standard locations
     * such as /usr/bin. Because drillbit.sh uses its own location to compute
     * DRILL_HOME, it must handle symlinks. This test verifies that process.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDrillbitSymlink() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        File drillbitFile = new File(TestScripts.context.testDrillHome, "bin/drillbit.sh");
        File linksDir = TestScripts.context.createDir(new File(TestScripts.context.testDir, "links"));
        File link = new File(linksDir, drillbitFile.getName());
        try {
            Files.createSymbolicLink(link.toPath(), drillbitFile.toPath());
        } catch (UnsupportedOperationException e) {
            // Well. This is a system without symlinks, so we won't be testing
            // syminks here...
            return;
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_START).start();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertEquals(result.pidFile.getParentFile(), TestScripts.context.testDrillHome);
        }
        validateAndCloseDaemon(null);
    }

    /**
     * Test the restart command of drillbit.sh
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testRestart() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        int firstPid;
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_START).start();
            Assert.assertEquals(0, result.returnCode);
            firstPid = result.getPid();
        }
        // Make sure it is running.
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STATUS).withSite(siteDir).run();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertTrue(result.stdout.contains("drillbit is running"));
        }
        // Restart. Should get new pid.
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_RESTART).start();
            Assert.assertEquals(0, result.returnCode);
            int secondPid = result.getPid();
            Assert.assertNotEquals(firstPid, secondPid);
        }
        validateAndCloseDaemon(null);
    }

    /**
     * Simulate a Drillbit that refuses to die. The stop script wait a while, then
     * forces killing.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testForcedKill() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        {
            ScriptUtils.DrillbitRun runner = new ScriptUtils.DrillbitRun(DRILLBIT_START);
            runner.addEnv("PRETEND_HUNG", "1");
            ScriptUtils.RunResult result = runner.start();
            Assert.assertEquals(0, result.returnCode);
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STATUS).preserveLogs().run();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertTrue(result.stdout.contains("drillbit is running"));
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.DrillbitRun(DRILLBIT_STOP).addEnv("DRILL_STOP_TIMEOUT", "5").preserveLogs().run();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertTrue(result.stdout.contains("drillbit did not complete after"));
        }
    }

    /**
     * Verify the basics of the sqlline script, including the env vars that can be
     * customized. Also validate running in embedded mode (using drillbit memory
     * and other options.)
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSqlline() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        int stockArgCount;
        {
            // Out-of-the-box sqlline
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateClassPath(ScriptUtils.stdCp);
            Assert.assertTrue(result.containsArgsRegex(ScriptUtils.sqlLineArgs));
            stockArgCount = result.echoArgs.size();
        }
        {
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").withArg("arg1").withArg("arg2").run();
            Assert.assertTrue(result.containsArg("arg1"));
            Assert.assertTrue(result.containsArg("arg2"));
        }
        {
            // Change drill memory and other drill-specific
            // settings: should not affect sqlline
            Map<String, String> drillEnv = new HashMap<>();
            drillEnv.put("DRILL_JAVA_OPTS", "-Dprop=value");
            drillEnv.put("DRILL_HEAP", "5G");
            drillEnv.put("DRILL_MAX_DIRECT_MEMORY", "7G");
            drillEnv.put("SERVER_LOG_GC", "1");
            drillEnv.put("DRILLBIT_CODE_CACHE_SIZE", "2G");
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").withEnvironment(drillEnv).run();
            Assert.assertTrue(result.containsArgsRegex(ScriptUtils.sqlLineArgs));
            // Nothing new should have been added
            Assert.assertEquals(stockArgCount, result.echoArgs.size());
        }
        {
            // Change client memory: should affect sqlline
            Map<String, String> shellEnv = new HashMap<>();
            shellEnv.put("CLIENT_GC_OPTS", "-XX:+UseG1GC");
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").withEnvironment(shellEnv).run();
            Assert.assertTrue(result.containsArg("-XX:+UseG1GC"));
        }
        {
            // Change drill memory and other drill-specific
            // settings: then set the "magic" variable that says
            // that Drill is embedded. The scripts should now use
            // the Drillbit options.
            Map<String, String> drillEnv = new HashMap<>();
            drillEnv.put("DRILL_JAVA_OPTS", "-Dprop=value");
            drillEnv.put("DRILL_HEAP", "5G");
            drillEnv.put("DRILL_MAX_DIRECT_MEMORY", "7G");
            drillEnv.put("SERVER_LOG_GC", "1");
            drillEnv.put("DRILLBIT_CODE_CACHE_SIZE", "2G");
            drillEnv.put("DRILL_EMBEDDED", "1");
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").withEnvironment(drillEnv).run();
            String[] expectedArgs = new String[]{ "-Dprop=value", "-Xms5G", "-Xmx5G", "-XX:MaxDirectMemorySize=7G", "-XX:ReservedCodeCacheSize=2G" };
            result.validateArgs(expectedArgs);
            Assert.assertTrue(result.containsArg("sqlline.SqlLine"));
        }
    }

    /**
     * Test to verify no effect of DRILLBIT_CONTEXT for Sqlline.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSqllineWithDrillbitContextEnv() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        // Test when SQLLINE_JAVA_OPTS is overriden inside a condition for
        // DRILLBIT_CONTEXT = 0, then there is no effect
        {
            // Create a condition variable to be placed in distrib-env.sh
            Map<String, String> conditions = new HashMap<>();
            conditions.put("DRILLBIT_CONTEXT", "0");
            // Create environment variable to be placed inside a condition in distrib-env.sh
            Map<String, String> drillEnv = new HashMap<>();
            drillEnv.put("SQLLINE_JAVA_OPTS", "-XX:MaxPermSize=256M");
            // Create the environment variable file overriding SQLLINE_JAVA_OPTS
            TestScripts.context.createEnvFileWithCondition(new File(siteDir, "distrib-env.sh"), conditions, drillEnv, true);
            // Expected value of the property
            String[] expectedArgs = new String[]{ "-XX:MaxPermSize=256M" };
            // Run the test and match the output with expectedArgs
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateClassPath(ScriptUtils.stdCp);
            // Since by default MaxPermSize is not set anymore for Sqlline. It's removed in 1.13
            Assert.assertFalse(result.containsArgsRegex(expectedArgs));
        }
        // Test when SQLLINE_JAVA_OPTS is overriden inside a condition for
        // DRILLBIT_CONTEXT = 1, then there is no effect
        {
            Map<String, String> conditions = new HashMap<>();
            conditions.put("DRILLBIT_CONTEXT", "1");
            Map<String, String> drillEnv = new HashMap<>();
            drillEnv.put("SQLLINE_JAVA_OPTS", "-XX:MaxPermSize=256M");
            String[] expectedArgs = new String[]{ "-XX:MaxPermSize=256M" };
            // Create the environment variable file overriding SQLLINE_JAVA_OPTS
            TestScripts.context.createEnvFileWithCondition(new File(siteDir, "distrib-env.sh"), conditions, drillEnv, true);
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateClassPath(ScriptUtils.stdCp);
            // Since by default MaxPermSize is not set anymore for Sqlline. It's removed in 1.13
            Assert.assertFalse(result.containsArgsRegex(expectedArgs));
        }
        // Test when SQLLINE_JAVA_OPTS is overriden without condition for
        // DRILLBIT_CONTEXT then the environment variable is updated
        {
            Map<String, String> drillEnv = new HashMap<>();
            drillEnv.put("SQLLINE_JAVA_OPTS", "-XX:MaxPermSize=256M");
            // Create the environment variable file overriding SQLLINE_JAVA_OPTS without any condition
            // around it.
            String[] expectedArgs = new String[]{ "-XX:MaxPermSize=256M" };
            TestScripts.context.createEnvFile(new File(siteDir, "distrib-env.sh"), drillEnv, true);
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateClassPath(ScriptUtils.stdCp);
            Assert.assertTrue(result.containsArgsRegex(expectedArgs));
        }
    }

    /**
     * Verify that the sqlline client works with the --site option by customizing
     * items in the site directory.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSqllineSiteDir() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDir, "site");
        TestScripts.context.createMockConf(siteDir);
        // Dummy drill-env.sh to simulate the shipped "example" file
        // with some client-specific changes.
        TestScripts.context.writeFile(new File(siteDir, "drill-env.sh"), ("#!/bin/bash\n" + ("# Example file\n" + "export SQLLINE_JAVA_OPTS=\"-XX:MaxPermSize=256M\"\n")));
        File siteJars = new File(siteDir, "jars");
        TestScripts.context.createDir(siteJars);
        TestScripts.context.makeDummyJar(siteJars, "site");
        {
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("sqlline").withSite(siteDir).run();
            Assert.assertEquals(0, result.returnCode);
            Assert.assertTrue(result.containsArg("-XX:MaxPermSize=256M"));
            result.validateClassPath(((siteJars.getAbsolutePath()) + "/*"));
        }
    }

    /**
     * Tests the three scripts that wrap sqlline for specific purposes:
     * <ul>
     * <li>drill-conf ? Wrapper for sqlline, uses drill config to find Drill.
     * Seems this one needs fixing to use a config other than the hard-coded
     * $DRILL_HOME/conf location.</li>
     * <li>drill-embedded ? Starts a drill "embedded" in SqlLine, using a local
     * ZK.</li>
     * <li>drill-localhost ? Wrapper for sqlline, uses a local ZK.</li>
     * </ul>
     *
     * Of these, drill-embedded runs an embedded Drillbit and so should use the
     * Drillbit memory options. The other two are clients, but with simple default
     * options for finding the Drillbit.
     * <p>
     * Because the scripts are simple wrappers, all we do is verify that the right
     * "extra" options are set, not the fundamentals (which were already covered
     * in the sqlline tests.)
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSqllineWrappers() throws IOException {
        TestScripts.context.createMockDistrib();
        File siteDir = new File(TestScripts.context.testDrillHome, "conf");
        TestScripts.context.createMockConf(siteDir);
        {
            // drill-conf: just adds a stub JDBC connect string.
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("drill-conf").withArg("arg1").run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateClassPath(ScriptUtils.stdCp);
            Assert.assertTrue(result.containsArgsRegex(ScriptUtils.sqlLineArgs));
            Assert.assertTrue(result.containsArg("-u"));
            Assert.assertTrue(result.containsArg("jdbc:drill:"));
            Assert.assertTrue(result.containsArg("arg1"));
        }
        {
            // drill-localhost: Adds a JDBC connect string to a drillbit
            // on the localhost
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("drill-localhost").withArg("arg1").run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateClassPath(ScriptUtils.stdCp);
            Assert.assertTrue(result.containsArgsRegex(ScriptUtils.sqlLineArgs));
            Assert.assertTrue(result.containsArg("-u"));
            Assert.assertTrue(result.containsArg("jdbc:drill:drillbit=localhost"));
            Assert.assertTrue(result.containsArg("arg1"));
        }
        {
            // drill-embedded: Uses drillbit startup options and
            // connects to the embedded drillbit.
            ScriptUtils.RunResult result = new ScriptUtils.ScriptRunner("drill-embedded").withArg("arg1").run();
            Assert.assertEquals(0, result.returnCode);
            result.validateJava();
            result.validateClassPath(ScriptUtils.stdCp);
            Assert.assertTrue(result.containsArgsRegex(ScriptUtils.sqlLineArgs));
            Assert.assertTrue(result.containsArg("-u"));
            Assert.assertTrue(result.containsArg("jdbc:drill:zk=local"));
            Assert.assertTrue(result.containsArg("-Xms4G"));
            Assert.assertTrue(result.containsArg("-XX:MaxDirectMemorySize=8G"));
            Assert.assertTrue(result.containsArg("arg1"));
        }
    }
}

