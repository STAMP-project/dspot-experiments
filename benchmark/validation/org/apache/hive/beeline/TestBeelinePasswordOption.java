/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.beeline;


import java.util.ArrayList;
import java.util.List;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBeelinePasswordOption {
    private static final Logger LOG = LoggerFactory.getLogger(TestBeelinePasswordOption.class);

    private static final String tableName = "TestBeelineTable1";

    private static final String tableComment = "Test table comment";

    private static MiniHS2 miniHS2;

    /**
     * Test if beeline prompts for a password when optional password option is at the beginning of
     * arguments
     */
    @Test
    public void testPromptPasswordOptionAsFirst() throws Throwable {
        List<String> argList = new ArrayList<>();
        argList.add("-p");
        argList.addAll(getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL()));
        argList.add("-n");
        argList.add("hive");
        connectBeelineWithUserPrompt(argList, "hivepassword");
    }

    /**
     * Test if beeline prompts for a password when optional password option is at the end of arguments
     */
    @Test
    public void testPromptPasswordOptionLast() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-n");
        argList.add("hive");
        argList.add("-p");
        connectBeelineWithUserPrompt(argList, "hivepassword");
    }

    /**
     * Test if beeline prompts for a password when optional password option is at the middle of
     * arguments
     */
    @Test
    public void testPromptPasswordOptionMiddle() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-p");
        argList.add("-n");
        argList.add("hive");
        connectBeelineWithUserPrompt(argList, "hivepassword");
    }

    /**
     * Test if beeline prompts for a password when optional password option is used in conjunction
     * with additional commandLine options after -p
     */
    @Test
    public void testPromptPasswordOptionWithOtherOptions() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-p");
        argList.add("-n");
        argList.add("hive");
        argList.add("-e");
        argList.add("show tables;");
        String output = connectBeelineWithUserPrompt(argList, "hivepassword");
        Assert.assertTrue((("Table name " + (TestBeelinePasswordOption.tableName)) + " not found in the output"), output.contains(TestBeelinePasswordOption.tableName.toLowerCase()));
    }

    /**
     * Test if beeline prompts for a password when optional password option is used in conjunction
     * with additional BeeLineOpts options after -p
     */
    @Test
    public void testPromptPasswordOptionWithBeelineOpts() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-n");
        argList.add("hive");
        argList.add("-p");
        argList.add("--force=true");
        argList.add("-e");
        argList.add("show tables;");
        String output = connectBeelineWithUserPrompt(argList, "hivepassword");
        Assert.assertTrue((("Table name " + (TestBeelinePasswordOption.tableName)) + " not found in the output"), output.contains(TestBeelinePasswordOption.tableName.toLowerCase()));
    }

    /**
     * Test if beeline prompts for a password when optional password option is used in conjunction
     * with additional BeeLineOpts options after -p. Also, verifies the beelineOpt value is set as
     * expected
     */
    @Test
    public void testPromptPasswordVerifyBeelineOpts() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-n");
        argList.add("hive");
        argList.add("-p");
        argList.add("--maxColumnWidth=57");
        argList.add("-e");
        argList.add("show tables;");
        String output = connectWithPromptAndVerify(argList, "hivepassword", true, 57, null, null);
        Assert.assertTrue((("Table name " + (TestBeelinePasswordOption.tableName)) + " not found in the output"), output.contains(TestBeelinePasswordOption.tableName.toLowerCase()));
    }

    /**
     * Tests if beeline prompts for a password and also confirms that --hiveconf
     * argument works when given immediately after -p with no password
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPromptPasswordWithHiveConf() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-n");
        argList.add("hive");
        argList.add("-p");
        argList.add("--hiveconf");
        argList.add("hive.cli.print.header=true");
        argList.add("-e");
        argList.add("show tables;");
        String output = connectWithPromptAndVerify(argList, "hivepassword", false, null, "hive.cli.print.header", "true");
        Assert.assertTrue((("Table name " + (TestBeelinePasswordOption.tableName)) + " not found in the output"), output.contains(TestBeelinePasswordOption.tableName.toLowerCase()));
    }

    /**
     * Tests if beeline doesn't prompt for a password and connects with empty password
     * when no password option provided
     */
    @Test
    public void testNoPasswordPrompt() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-n");
        argList.add("hive");
        argList.add("--force=true");
        argList.add("-e");
        argList.add("show tables;");
        String output = connectBeelineWithUserPrompt(argList);
        Assert.assertTrue((("Table name " + (TestBeelinePasswordOption.tableName)) + " not found in the output"), output.contains(TestBeelinePasswordOption.tableName.toLowerCase()));
    }

    /**
     * Tests if beeline doesn't prompt for a password and connects with no password/username option
     * provided
     */
    @Test
    public void testNoPasswordPrompt2() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("--force=true");
        argList.add("-e");
        argList.add("show tables;");
        String output = connectBeelineWithUserPrompt(argList);
        Assert.assertTrue((("Table name " + (TestBeelinePasswordOption.tableName)) + " not found in the output"), output.contains(TestBeelinePasswordOption.tableName.toLowerCase()));
    }

    /**
     * Tests if Beeline prompts for password when -p is the last argument and argList has CommandLine
     * options as well as BeelineOpts
     */
    @Test
    public void testPromptPassOptionLastWithBeelineOpts() throws Exception {
        List<String> argList = getBaseArgs(TestBeelinePasswordOption.miniHS2.getBaseJdbcURL());
        argList.add("-n");
        argList.add("hive");
        argList.add("--force=true");
        argList.add("-e");
        argList.add("show tables;");
        argList.add("-p");
        String output = connectBeelineWithUserPrompt(argList, "hivepassword");
        Assert.assertTrue((("Table name " + (TestBeelinePasswordOption.tableName)) + " not found in the output"), output.contains(TestBeelinePasswordOption.tableName.toLowerCase()));
    }
}

