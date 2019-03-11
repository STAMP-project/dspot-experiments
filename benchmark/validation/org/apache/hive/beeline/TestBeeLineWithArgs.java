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


import BeeLine.BEELINE_DEFAULT_JDBC_DRIVER;
import BeeLineOpts.Env;
import SeparatedValuesOutputFormat.DISABLE_QUOTING_FOR_SV;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.hive.jdbc.Utils;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


/**
 * TestBeeLineWithArgs - executes tests of the command-line arguments to BeeLine
 */
public class TestBeeLineWithArgs {
    private enum OutStream {

        ERR,
        OUT;}

    // Default location of HiveServer2
    private static final String tableName = "TestBeelineTable1";

    private static final String tableComment = "Test table comment";

    private static MiniHS2 miniHS2;

    private static final String userName = System.getProperty("user.name");

    /* We are testing for both type of modes always so not passing that as a parameter for now */
    enum Modes {

        INIT() {
            @Override
            String output(File scriptFile, List<String> argList, TestBeeLineWithArgs.OutStream streamType) throws Throwable {
                List<String> copy = new ArrayList<>(argList);
                copy.add("-i");
                copy.add(scriptFile.getAbsolutePath());
                return TestBeeLineWithArgs.testCommandLineScript(copy, new StringBufferInputStream("!quit\n"), streamType);
            }
        },
        SCRIPT() {
            @Override
            String output(File scriptFile, List<String> argList, TestBeeLineWithArgs.OutStream streamType) throws Throwable {
                List<String> copy = new ArrayList<>(argList);
                copy.add("-f");
                copy.add(scriptFile.getAbsolutePath());
                return TestBeeLineWithArgs.testCommandLineScript(copy, null, streamType);
            }
        };
        abstract String output(File scriptFile, List<String> argList, TestBeeLineWithArgs.OutStream streamType) throws Throwable;
    }

    /**
     * Test that BeeLine will read comment lines that start with whitespace
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testWhitespaceBeforeCommentScriptFile() throws Throwable {
        final String SCRIPT_TEXT = " \t \t-- comment has spaces and tabs before it\n \t \t# comment has spaces and tabs before it\n";
        final String EXPECTED_PATTERN = "cannot recognize input near '<EOF>'";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, false);
    }

    /**
     * Attempt to execute a simple script file with the -f option to BeeLine
     * Test for presence of an expected pattern
     * in the output (stdout or stderr), fail if not found
     * Print PASSED or FAILED
     */
    @Test
    public void testPositiveScriptFile() throws Throwable {
        final String SCRIPT_TEXT = "show databases;\n";
        final String EXPECTED_PATTERN = " default ";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Fix to HIVE-10541: Beeline requires a newline at the end of each query in a file.
     * Otherwise, the last line of cmd in the script will be ignored.
     */
    @Test
    public void testLastLineCmdInScriptFile() throws Throwable {
        final String SCRIPT_TEXT = "show databases;\nshow tables;";
        final String EXPECTED_PATTERN = " testbeelinetable1 ";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test Beeline -hivevar option. User can specify --hivevar name=value on Beeline command line.
     * In the script, user should be able to use it in the form of ${name}, which will be substituted with
     * the value.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testBeelineHiveVariable() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--hivevar");
        argList.add("DUMMY_TBL=dummy");
        final String SCRIPT_TEXT = "create table ${DUMMY_TBL} (d int);\nshow tables;\n drop table  ${DUMMY_TBL};";
        final String EXPECTED_PATTERN = "dummy";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    @Test
    public void testBeelineHiveConfVariable() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--hiveconf");
        argList.add("test.hive.table.name=dummy");
        final String SCRIPT_TEXT = "create table ${hiveconf:test.hive.table.name} (d int);\nshow tables;\n" + " drop table ${hiveconf:test.hive.table.name};\n";
        final String EXPECTED_PATTERN = "dummy";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test Beeline -hivevar option. User can specify --hivevar name=value on Beeline command line.
     * This test defines multiple variables using repeated --hivevar or --hiveconf flags.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testBeelineMultiHiveVariable() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--hivevar");
        argList.add("TABLE_NAME=dummy2");
        argList.add("--hiveconf");
        argList.add("COLUMN_NAME=d");
        argList.add("--hivevar");
        argList.add("COMMAND=create");
        argList.add("--hivevar");
        argList.add("OBJECT=table");
        argList.add("--hiveconf");
        argList.add("COLUMN_TYPE=int");
        final String SCRIPT_TEXT = "${COMMAND} ${OBJECT} ${TABLE_NAME} " + ("(${hiveconf:COLUMN_NAME} ${hiveconf:COLUMN_TYPE});" + "\nshow tables;\n drop ${OBJECT} ${TABLE_NAME};\n");
        final String EXPECTED_PATTERN = "dummy2";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Attempt to execute a simple script file with the -f option to BeeLine
     * The first command should fail and the second command should not execute
     * Print PASSED or FAILED
     */
    @Test
    public void testBreakOnErrorScriptFile() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        final String SCRIPT_TEXT = "select * from abcdefg01;\nshow databases;\n";
        final String EXPECTED_PATTERN = " default ";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, false);
    }

    @Test
    public void testTabInScriptFile() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        final String SCRIPT_TEXT = "CREATE\tTABLE IF NOT EXISTS testTabInScriptFile\n(id\tint);\nSHOW TABLES;" + "\ndrop table testTabInScriptFile";
        final String EXPECTED_PATTERN = "testTabInScriptFile";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, true);
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, EXPECTED_PATTERN, false);
    }

    @Test
    public void testBeelineShellCommand() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        final String SCRIPT_TEXT = "!sh echo \"hello world.\" > hw.txt\n!sh cat hw.txt\n!rm hw.txt";
        final String EXPECTED_PATTERN = "hello world";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, Collections.singletonList(new TestBeeLineWithArgs.Tuple<>(EXPECTED_PATTERN, true)), Collections.singletonList(TestBeeLineWithArgs.Modes.SCRIPT));
    }

    /**
     * Select null from table , check how null is printed
     * Print PASSED or FAILED
     */
    @Test
    public void testNullDefault() throws Throwable {
        final String SCRIPT_TEXT = (("set hive.support.concurrency = false;\n" + "select null from ") + (TestBeeLineWithArgs.tableName)) + " limit 1 ;\n";
        final String EXPECTED_PATTERN = "NULL";
        testScriptFile(SCRIPT_TEXT, getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()), EXPECTED_PATTERN, true);
    }

    /**
     * Select null from table , check if default null is printed differently
     * Print PASSED or FAILED
     */
    @Test
    public void testNullNonEmpty() throws Throwable {
        final String SCRIPT_TEXT = (("set hive.support.concurrency = false;\n" + "!set nullemptystring false\n select null from ") + (TestBeeLineWithArgs.tableName)) + " limit 1 ;\n";
        final String EXPECTED_PATTERN = "NULL";
        testScriptFile(SCRIPT_TEXT, getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()), EXPECTED_PATTERN, true);
    }

    @Test
    public void testGetVariableValue() throws Throwable {
        final String SCRIPT_TEXT = "set env:TERM;";
        final String EXPECTED_PATTERN = "env:TERM";
        testScriptFile(SCRIPT_TEXT, getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()), TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, true);
    }

    /**
     * Select null from table , check if setting null to empty string works.
     * Original beeline/sqlline used to print nulls as empty strings.
     * Also test csv2 output format
     * Print PASSED or FAILED
     */
    @Test
    public void testNullEmpty() throws Throwable {
        final String SCRIPT_TEXT = (("set hive.support.concurrency = false;\n" + "!set nullemptystring true\n select \'abc\',null,\'def\' from ") + (TestBeeLineWithArgs.tableName)) + " limit 1 ;\n";
        final String EXPECTED_PATTERN = "abc,,def";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=csv2");
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test writing output using DSV format, with custom delimiter ";"
     */
    @Test
    public void testDSVOutput() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQuery();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=dsv");
        argList.add("--delimiterForDSV=;");
        final String EXPECTED_PATTERN = "1;NULL;defg;ab\"c;1.0";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test writing output using TSV (new) format
     */
    @Test
    public void testTSV2Output() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQuery();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv2");
        final String EXPECTED_PATTERN = "1\tNULL\tdefg\tab\"c\t1.0";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test writing output using TSV deprecated format
     */
    @Test
    public void testTSVOutput() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQuery();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv");
        final String EXPECTED_PATTERN = "\'1\'\t\'NULL\'\t\'defg\'\t\'ab\"c\'\t\'1.0\'";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test writing output using new TSV format
     */
    @Test
    public void testTSV2OutputWithDoubleQuotes() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQueryForEableQuotes();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv2");
        System.setProperty(DISABLE_QUOTING_FOR_SV, "false");
        final String EXPECTED_PATTERN = "1\tNULL\tdefg\t\"ab\"\"c\"\t\"\"\"aa\"\"\"\t1.0";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        System.setProperty(DISABLE_QUOTING_FOR_SV, "true");
    }

    /**
     * Test writing output using TSV deprecated format
     */
    @Test
    public void testTSVOutputWithDoubleQuotes() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQueryForEableQuotes();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv");
        System.setProperty(DISABLE_QUOTING_FOR_SV, "false");
        final String EXPECTED_PATTERN = "\'1\'\t\'NULL\'\t\'defg\'\t\'ab\"c\'\t\'\"aa\"\'\t\'1.0\'";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        System.setProperty(DISABLE_QUOTING_FOR_SV, "true");
    }

    /**
     * Test writing output using new CSV format
     */
    @Test
    public void testCSV2OutputWithDoubleQuotes() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQueryForEableQuotes();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=csv2");
        System.setProperty(DISABLE_QUOTING_FOR_SV, "false");
        final String EXPECTED_PATTERN = "1,NULL,defg,\"ab\"\"c\",\"\"\"aa\"\"\",1.0";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        System.setProperty(DISABLE_QUOTING_FOR_SV, "true");
    }

    /**
     * Test writing output using CSV deprecated format
     */
    @Test
    public void testCSVOutputWithDoubleQuotes() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQueryForEableQuotes();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=csv");
        System.setProperty(DISABLE_QUOTING_FOR_SV, "false");
        final String EXPECTED_PATTERN = "\'1\',\'NULL\',\'defg\',\'ab\"c\',\'\"aa\"\',\'1.0\'";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        System.setProperty(DISABLE_QUOTING_FOR_SV, "true");
    }

    /**
     * Test writing output using DSV format, with custom delimiter ";"
     */
    @Test
    public void testDSVOutputWithDoubleQuotes() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQueryForEableQuotes();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=dsv");
        argList.add("--delimiterForDSV=;");
        System.setProperty(DISABLE_QUOTING_FOR_SV, "false");
        final String EXPECTED_PATTERN = "1;NULL;defg;\"ab\"\"c\";\"\"\"aa\"\"\";1.0";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        System.setProperty(DISABLE_QUOTING_FOR_SV, "true");
    }

    /**
     * Test writing output using TSV deprecated format
     * Check for deprecation message
     */
    @Test
    public void testTSVOutputDeprecation() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQuery();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv");
        final String EXPECTED_PATTERN = "Format tsv is deprecated, please use tsv2";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, true);
    }

    /**
     * Test writing output using CSV deprecated format
     * Check for deprecation message
     */
    @Test
    public void testCSVOutputDeprecation() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQuery();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=csv");
        final String EXPECTED_PATTERN = "Format csv is deprecated, please use csv2";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.ERR, Collections.singletonList(new TestBeeLineWithArgs.Tuple<>(EXPECTED_PATTERN, true)));
    }

    /**
     * Test writing output using CSV deprecated format
     */
    @Test
    public void testCSVOutput() throws Throwable {
        String SCRIPT_TEXT = getFormatTestQuery();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=csv");
        final String EXPECTED_PATTERN = "\'1\',\'NULL\',\'defg\',\'ab\"c\',\'1.0\'";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Select null from table , check if setting null to empty string works - Using beeling cmd line
     *  argument.
     * Original beeline/sqlline used to print nulls as empty strings
     * Print PASSED or FAILED
     */
    @Test
    public void testNullEmptyCmdArg() throws Throwable {
        final String SCRIPT_TEXT = (("set hive.support.concurrency = false;\n" + "select 'abc',null,'def' from ") + (TestBeeLineWithArgs.tableName)) + " limit 1 ;\n";
        final String EXPECTED_PATTERN = "'abc','','def'";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--nullemptystring=true");
        argList.add("--outputformat=csv");
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Attempt to execute a missing script file with the -f option to BeeLine
     */
    @Test
    public void testNegativeScriptFile() throws Throwable {
        final String EXPECTED_PATTERN = " default ";
        // Create and delete a temp file
        File scriptFile = File.createTempFile("beelinenegative", "temp");
        scriptFile.delete();
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("-f");
        argList.add(scriptFile.getAbsolutePath());
        try {
            String output = TestBeeLineWithArgs.testCommandLineScript(argList, null, TestBeeLineWithArgs.OutStream.OUT);
            if (output.contains(EXPECTED_PATTERN)) {
                Assert.fail(((("Output: " + output) + " Negative pattern: ") + EXPECTED_PATTERN));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * HIVE-4566
     *
     * @throws UnsupportedEncodingException
     * 		
     */
    @Test
    public void testNPE() throws UnsupportedEncodingException {
        BeeLine beeLine = new BeeLine();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream beelineOutputStream = new PrintStream(os);
        beeLine.setOutputStream(beelineOutputStream);
        beeLine.setErrorStream(beelineOutputStream);
        beeLine.runCommands(new String[]{ "!typeinfo" });
        String output = os.toString("UTF8");
        Assert.assertFalse(output.contains("java.lang.NullPointerException"));
        Assert.assertTrue(output.contains("No current connection"));
        beeLine.runCommands(new String[]{ "!nativesql" });
        output = os.toString("UTF8");
        Assert.assertFalse(output.contains("java.lang.NullPointerException"));
        Assert.assertTrue(output.contains("No current connection"));
        System.out.println((">>> PASSED " + "testNPE"));
    }

    @Test
    public void testHiveVarSubstitution() throws Throwable {
        List<String> argList = getBaseArgs(((TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()) + "#D_TBL=dummy_t"));
        final String SCRIPT_TEXT = "create table ${D_TBL} (d int);\nshow tables;\ndrop  table ${D_TBL};\n";
        final String EXPECTED_PATTERN = "dummy_t";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    @Test
    public void testEmbeddedBeelineConnection() throws Throwable {
        String embeddedJdbcURL = (Utils.URL_PREFIX) + "/Default";
        List<String> argList = getBaseArgs(embeddedJdbcURL);
        argList.add("--hivevar");
        argList.add("DUMMY_TBL=embedded_table");
        // Set to non-zk lock manager to avoid trying to connect to zookeeper
        final String SCRIPT_TEXT = "set hive.lock.manager=org.apache.hadoop.hive.ql.lockmgr.EmbeddedLockManager;\n" + "create table ${DUMMY_TBL} (d int);\nshow tables;\n drop table ${DUMMY_TBL};\n";
        final String EXPECTED_PATTERN = "embedded_table";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test Beeline will hide the query progress when silent option is set.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testQueryProgressHidden() throws Throwable {
        final String SCRIPT_TEXT = (("set hive.support.concurrency = false;\n" + ("!set silent true\n" + "select count(*) from ")) + (TestBeeLineWithArgs.tableName)) + ";\n";
        final String EXPECTED_PATTERN = "Executing command";
        testScriptFile(SCRIPT_TEXT, getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()), TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, false);
    }

    @Test
    public void testQueryProgressWithHiveServer2ProgressBarDisabled() throws Throwable {
        final String SCRIPT_TEXT = (("set hive.support.concurrency = false;\nset hive.server2.in.place.progress=false;\n" + "select count(*) from ") + (TestBeeLineWithArgs.tableName)) + ";\n";
        // Check for part of log message as well as part of progress information
        final String EXPECTED_PATTERN = "(?=Reducer 2\\:).*(?=Map 1\\:)";
        testScriptFile(SCRIPT_TEXT, getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()), TestBeeLineWithArgs.OutStream.ERR, Arrays.asList(new TestBeeLineWithArgs.Tuple(EXPECTED_PATTERN, true), new TestBeeLineWithArgs.Tuple("ELAPSED TIME", false)));
    }

    @Test
    public void testMultiCommandsInOneline() throws Throwable {
        final String SCRIPT_TEXT = "drop table if exists multiCmdTbl;create table multiCmdTbl " + "(key int);show tables; --multicommands in one line";
        final String EXPECTED_PATTERN = " multicmdtbl ";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        final String SCRIPT_TEXT_DROP = "drop table multiCmdTbl;show tables;";
        testScriptFile(SCRIPT_TEXT_DROP, argList, EXPECTED_PATTERN, false);
    }

    @Test
    public void testMultiCommandsInOneEnclosedQuery() throws Throwable {
        final String QUERY_TEXT = "drop table if exists multiCmdTbl;create table multiCmdTbl " + "(key int);show tables; --multicommands in one line";
        final String EXPECTED_PATTERN = " multicmdtbl ";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testCommandEnclosedQuery(QUERY_TEXT, EXPECTED_PATTERN, true, argList, TestBeeLineWithArgs.OutStream.OUT);
        final String QUERY_TEXT_DROP = "drop table multiCmdTbl;show tables;";
        testCommandEnclosedQuery(QUERY_TEXT_DROP, EXPECTED_PATTERN, false, argList, TestBeeLineWithArgs.OutStream.OUT);
    }

    @Test
    public void testOneCommandInMultiLines() throws Throwable {
        final String SCRIPT_TEXT = "drop table if exists multiCmdTbl;create table \nmultiCmdTbl " + "(key int);show tables; --one command in multiple lines";
        final String EXPECTED_PATTERN = " multicmdtbl ";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        final String SCRIPT_TEXT_DROP = "drop table\nmultiCmdTbl;show tables;";
        testScriptFile(SCRIPT_TEXT_DROP, argList, EXPECTED_PATTERN, false);
    }

    @Test
    public void testEscapeSemiColonInQueries() throws Throwable {
        final String SCRIPT_TEXT = "drop table if exists multiCmdTbl;create table multiCmdTbl " + ("(key int, value string) ROW FORMAT DELIMITED FIELDS TERMINATED BY \'\\;\' LINES " + " TERMINATED BY \'\\n\';show tables; --one command in multiple lines");
        final String EXPECTED_PATTERN = " multicmdtbl ";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        final String SCRIPT_TEXT_DROP = "drop table\nmultiCmdTbl;show tables;";
        testScriptFile(SCRIPT_TEXT_DROP, argList, EXPECTED_PATTERN, false);
    }

    @Test
    public void testEscapeSemiColonInEnclosedQuery() throws Throwable {
        final String QUERY_TEXT = "drop table if exists multiCmdTbl;create table multiCmdTbl " + ("(key int, value string) ROW FORMAT DELIMITED FIELDS TERMINATED BY \'\\;\' LINES " + " TERMINATED BY \'\\n\';show tables;");
        final String EXPECTED_PATTERN = " multicmdtbl ";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testCommandEnclosedQuery(QUERY_TEXT, EXPECTED_PATTERN, true, argList, TestBeeLineWithArgs.OutStream.OUT);
        final String QUERY_TEXT_DROP = "drop table multiCmdTbl;show tables;";
        testCommandEnclosedQuery(QUERY_TEXT_DROP, EXPECTED_PATTERN, false, argList, TestBeeLineWithArgs.OutStream.OUT);
    }

    @Test
    public void testEmbeddedBeelineOutputs() throws Throwable {
        String embeddedJdbcURL = (Utils.URL_PREFIX) + "/Default";
        List<String> argList = getBaseArgs(embeddedJdbcURL);
        // Set to non-zk lock manager to avoid trying to connect to zookeeper
        final String SCRIPT_TEXT = "set hive.lock.manager=org.apache.hadoop.hive.ql.lockmgr.EmbeddedLockManager;\n" + ((("set hive.compute.query.using.stats=false;\n" + "create table if not exists embeddedBeelineOutputs(d int);\n") + "set a=1;\nselect count(*) from embeddedBeelineOutputs;\n") + "drop table embeddedBeelineOutputs;\n");
        final String EXPECTED_PATTERN = "Stage-1 map =";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, true);
    }

    @Test
    public void testConnectionUrlWithSemiColon() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getJdbcURL("default", "sess_var_list?var1=value1"));
        final String SCRIPT_TEXT = "set var1";
        final String EXPECTED_PATTERN = "var1=value1";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test Beeline !connect with beeline saved vars
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testBeelineConnectEnvVar() throws Throwable {
        final String jdbcUrl = TestBeeLineWithArgs.miniHS2.getBaseJdbcURL();
        List<String> argList = new ArrayList<>();
        argList.add("-u");
        argList.add("blue");
        argList.add("-d");
        argList.add(BEELINE_DEFAULT_JDBC_DRIVER);
        final String SCRIPT_TEXT = "create table blueconnecttest (d int);\nshow tables;\ndrop table blueconnecttest;\n";
        final String EXPECTED_PATTERN = "blueconnecttest";
        // We go through these hijinxes because java considers System.getEnv
        // to be read-only, and offers no way to set an env var from within
        // a process, only for processes that we sub-spawn.
        final BeeLineOpts.Env baseEnv = BeeLineOpts.getEnv();
        BeeLineOpts.Env newEnv = new BeeLineOpts.Env() {
            @Override
            public String get(String envVar) {
                if (envVar.equalsIgnoreCase("BEELINE_URL_BLUE")) {
                    return jdbcUrl;
                } else {
                    return baseEnv.get(envVar);
                }
            }
        };
        BeeLineOpts.setEnv(newEnv);
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, Collections.singletonList(new TestBeeLineWithArgs.Tuple<>(EXPECTED_PATTERN, true)), Collections.singletonList(TestBeeLineWithArgs.Modes.SCRIPT));
    }

    /**
     * Test that if we !close, we can still !reconnect
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testBeelineReconnect() throws Throwable {
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        final String SCRIPT_TEXT = "!close\n" + ("!reconnect\n\n\n" + "create table reconnecttest (d int);\nshow tables;\ndrop table reconnecttest;\n");
        final String EXPECTED_PATTERN = "reconnecttest";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, Collections.singletonList(new TestBeeLineWithArgs.Tuple<>(EXPECTED_PATTERN, true)), Collections.singletonList(TestBeeLineWithArgs.Modes.SCRIPT));
    }

    /**
     * Attempt to execute a simple script file with the usage of user & password variables in URL.
     * Test for presence of an expected pattern
     * in the output (stdout or stderr), fail if not found
     * Print PASSED or FAILED
     */
    @Test
    public void testConnectionWithURLParams() throws Throwable {
        final String EXPECTED_PATTERN = " hivetest ";
        List<String> argList = new ArrayList<>();
        argList.add("-d");
        argList.add(BEELINE_DEFAULT_JDBC_DRIVER);
        argList.add("-u");
        argList.add(((TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()) + ";user=hivetest;password=hive"));
        String SCRIPT_TEXT = "select current_user();";
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test that Beeline queries don't treat semicolons inside quotations as query-ending characters.
     */
    @Test
    public void testQueryNonEscapedSemiColon() throws Throwable {
        String SCRIPT_TEXT = "drop table if exists nonEscapedSemiColon;create table nonEscapedSemiColon " + "(key int, value int) ROW FORMAT DELIMITED FIELDS TERMINATED BY ';';show tables;";
        String EXPECTED_PATTERN = "nonescapedsemicolon";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
        // look for the " nonEscapedSemiColon " in the query text not the table name which comes
        // in the result
        EXPECTED_PATTERN = " nonEscapedSemiColon ";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, true);
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, EXPECTED_PATTERN, false);
    }

    @Test
    public void testSelectQueryWithNonEscapedSemiColon() throws Throwable {
        String SCRIPT_TEXT = ("select \';\', \"\';\'\", \'\";\"\', \'\\\';\', \';\\\'\', \'\\\";\', \';\\\"\' from " + (TestBeeLineWithArgs.tableName)) + ";";
        final String EXPECTED_PATTERN = ";\t\';\'\t\";\"\t\';\t;\'\t\";\t;\"";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv2");
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Attempt to execute a simple script file with the usage of user & password variables in URL.
     * Test for presence of an expected pattern
     * in the output (stdout or stderr), fail if not found
     * Print PASSED or FAILED
     */
    @Test
    public void testShowDbInPrompt() throws Throwable {
        final String EXPECTED_PATTERN = " \\(default\\)>";
        List<String> argList = new ArrayList<>();
        argList.add("--showDbInPrompt");
        argList.add("-u");
        argList.add(((TestBeeLineWithArgs.miniHS2.getBaseJdbcURL()) + ";user=hivetest;password=hive"));
        String SCRIPT_TEXT = "select current_user();";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, true);
    }

    @Test
    public void testBeelineShellCommandWithoutConn() throws Throwable {
        List<String> argList = new ArrayList<>();
        final String SCRIPT_TEXT = "!sh echo hello world";
        final String EXPECTED_PATTERN = "hello world";
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, Collections.singletonList(new TestBeeLineWithArgs.Tuple<>(EXPECTED_PATTERN, true)), Collections.singletonList(TestBeeLineWithArgs.Modes.SCRIPT));
    }

    /**
     * Attempt to execute Beeline with force option to continue running script even after errors.
     * Test for presence of an expected pattern to match the output of a valid command at the end.
     */
    @Test
    public void testBeelineWithForce() throws Throwable {
        final String SCRIPT_TEXT = "drop table does_not_exist;\ncreate table incomplete_syntax(a, string, );\n " + ("drop table if exists new_table;\n create table new_table(foo int, bar string);\n " + "desc new_table;\n");
        final String EXPECTED_PATTERN = "2 rows selected";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--force");
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.ERR, EXPECTED_PATTERN, true);
    }

    private static class Tuple<K> {
        final K pattern;

        final boolean shouldMatch;

        Tuple(K pattern, boolean shouldMatch) {
            this.pattern = pattern;
            this.shouldMatch = shouldMatch;
        }
    }

    /**
     * Test that Beeline can handle \\ characters within a string literal. Either at the beginning, middle, or end of the
     * literal.
     */
    @Test
    public void testBackslashInLiteral() throws Throwable {
        String SCRIPT_TEXT = "select \'hello\\\\\', \'\\\\hello\', \'hel\\\\lo\', \'\\\\\' as literal;";
        final String EXPECTED_PATTERN = "hello\\\\\t\\\\hello\thel\\\\lo\t\\\\";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv2");
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    @Test
    public void testCustomDelimiter() throws Throwable {
        String SCRIPT_TEXT = "select 'hello', 'hello', 'hello'$";
        final String EXPECTED_PATTERN = "hello\thello\thello";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--delimiter=$");
        argList.add("--outputformat=tsv2");
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    @Test
    public void testCustomMultiCharDelimiter() throws Throwable {
        String SCRIPT_TEXT = "select 'hello', 'hello', 'hello'$$";
        final String EXPECTED_PATTERN = "hello\thello\thello";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--delimiter=$$");
        argList.add("--outputformat=tsv2");
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    @Test
    public void testCustomDelimiterWithMultiQuery() throws Throwable {
        String SCRIPT_TEXT = "select 'hello', 'hello', 'hello'$select 'world', 'world', 'world'$";
        final String EXPECTED_PATTERN1 = "hello\thello\thello";
        final String EXPECTED_PATTERN2 = "world\tworld\tworld";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--delimiter=$");
        argList.add("--outputformat=tsv2");
        List<TestBeeLineWithArgs.Tuple<String>> expectedMatches = Arrays.asList(new TestBeeLineWithArgs.Tuple<>(EXPECTED_PATTERN1, true), new TestBeeLineWithArgs.Tuple<>(EXPECTED_PATTERN2, true));
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, expectedMatches);
    }

    @Test
    public void testCustomDelimiterBeelineCmd() throws Throwable {
        String SCRIPT_TEXT = "!delimiter $\n select \'hello\', \'hello\', \'hello\'$";
        final String EXPECTED_PATTERN = "hello\thello\thello";
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        argList.add("--outputformat=tsv2");
        testScriptFile(SCRIPT_TEXT, argList, EXPECTED_PATTERN, true);
    }

    /**
     * Test 'describe extended' on tables that have special white space characters in the row format.
     */
    @Test
    public void testDescribeExtended() throws Throwable {
        String SCRIPT_TEXT = "drop table if exists describeDelim;" + (("create table describeDelim (orderid int, orderdate string, customerid int)" + " ROW FORMAT DELIMITED FIELDS terminated by \'\\t\' LINES terminated by \'\\n\';") + "describe extended describeDelim;");
        List<String> argList = getBaseArgs(TestBeeLineWithArgs.miniHS2.getBaseJdbcURL());
        testScriptFile(SCRIPT_TEXT, argList, TestBeeLineWithArgs.OutStream.OUT, Arrays.asList(new TestBeeLineWithArgs.Tuple<>("Detailed Table Information.*line.delim=\\\\n", true), new TestBeeLineWithArgs.Tuple<>("Detailed Table Information.*field.delim=\\\\t", true)));
    }
}

