/**
 * Copyright 2017 Remko Popma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package picocli;


import CommandLine.VERSION;
import Help.Ansi.OFF;
import Help.DEFAULT_COMMAND_NAME;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.rules.TestRule;
import picocli.CommandLine.Range;

import static picocli.Demo.GitStatusMode.no;


/**
 * Tests for the CommandLine argument parsing interpreter functionality.
 */
// TODO arity ignored for single-value types (non-array, non-collection)
// TODO document that if arity>1 and args="-opt=val1 val2", arity overrules the "=": both values are assigned
// TODO test superclass bean and child class bean where child class field shadows super class and have same annotation Option name
// TODO test superclass bean and child class bean where child class field shadows super class and have different annotation Option name
public class CommandLineTest {
    @Rule
    public final ProvideSystemProperty ansiOFF = new ProvideSystemProperty("picocli.ansi", "false");

    // allows tests to set any kind of properties they like, without having to individually roll them back
    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Test(expected = NullPointerException.class)
    public void testConstructorRejectsNullObject() {
        new CommandLine(null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorRejectsNullFactory() {
        new CommandLine(new CommandLineTest.CompactFields(), null);
    }

    static class BadConverter implements CommandLine.ITypeConverter<Long> {
        public BadConverter() {
            throw new IllegalStateException("bad class");
        }

        public Long convert(String value) throws Exception {
            return null;
        }
    }

    @Test
    public void testFailingConverterWithDefaultFactory() {
        class App {
            @CommandLine.Option(names = "-x", converter = CommandLineTest.BadConverter.class)
            long bad;
        }
        try {
            new CommandLine(new App());
        } catch (CommandLine.InitializationException ex) {
            Assert.assertEquals(("Could not instantiate class " + "picocli.CommandLineTest$BadConverter: java.lang.reflect.InvocationTargetException"), ex.getMessage());
        }
    }

    static class BadVersionProvider implements CommandLine.IVersionProvider {
        public BadVersionProvider() {
            throw new IllegalStateException("bad class");
        }

        public String[] getVersion() throws Exception {
            return new String[0];
        }
    }

    @Test
    public void testFailingVersionProviderWithDefaultFactory() {
        @CommandLine.Command(versionProvider = CommandLineTest.BadVersionProvider.class)
        class App {}
        try {
            new CommandLine(new App());
        } catch (CommandLine.InitializationException ex) {
            Assert.assertEquals(("Could not instantiate class " + "picocli.CommandLineTest$BadVersionProvider: java.lang.reflect.InvocationTargetException"), ex.getMessage());
        }
    }

    @Test
    public void testVersion() {
        Assert.assertEquals("4.0.0-alpha-1-SNAPSHOT", VERSION);
    }

    @Test
    public void testArrayPositionalParametersAreReplacedNotAppendedTo() {
        class ArrayPositionalParams {
            @CommandLine.Parameters
            int[] array;
        }
        ArrayPositionalParams params = new ArrayPositionalParams();
        params.array = new int[3];
        int[] array = params.array;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotSame(array, params.array);
        Assert.assertArrayEquals(new int[]{ 3, 2, 1 }, params.array);
    }

    @Test
    public void testArrayPositionalParametersAreFilledWithValues() {
        @CommandLine.Command
        class ArrayPositionalParams {
            String string;

            @CommandLine.Parameters
            void setString(String[] array) {
                StringBuilder sb = new StringBuilder();
                for (String s : array) {
                    sb.append(s);
                }
                string = sb.toString();
            }
        }
        ArrayPositionalParams params = new ArrayPositionalParams();
        new CommandLine(params).parse("foo", "bar", "baz");
        Assert.assertEquals("foobarbaz", params.string);
    }

    @Test
    public void testArrayOptionsAreFilledWithValues() {
        @CommandLine.Command
        class ArrayPositionalParams {
            String string;

            @CommandLine.Option(names = "-s")
            void setString(String[] array) {
                StringBuilder sb = new StringBuilder();
                for (String s : array) {
                    sb.append(s);
                }
                string = sb.toString();
            }
        }
        ArrayPositionalParams params = new ArrayPositionalParams();
        new CommandLine(params).parse("-s", "foo", "-s", "bar", "-s", "baz");
        Assert.assertEquals("foobarbaz", params.string);
    }

    private class ListPositionalParams {
        @CommandLine.Parameters(type = Integer.class)
        List<Integer> list;
    }

    @Test
    public void testListPositionalParametersAreInstantiatedIfNull() {
        CommandLineTest.ListPositionalParams params = new CommandLineTest.ListPositionalParams();
        Assert.assertNull(params.list);
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotNull(params.list);
        Assert.assertEquals(Arrays.asList(3, 2, 1), params.list);
    }

    @Test
    public void testListPositionalParametersAreReusedIfNonNull() {
        CommandLineTest.ListPositionalParams params = new CommandLineTest.ListPositionalParams();
        params.list = new ArrayList<Integer>();
        List<Integer> list = params.list;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertSame(list, params.list);
        Assert.assertEquals(Arrays.asList(3, 2, 1), params.list);
    }

    @Test
    public void testListPositionalParametersAreReplacedIfNonNull() {
        CommandLineTest.ListPositionalParams params = new CommandLineTest.ListPositionalParams();
        params.list = new ArrayList<Integer>();
        params.list.add(234);
        List<Integer> list = params.list;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotSame(list, params.list);
        Assert.assertEquals(Arrays.asList(3, 2, 1), params.list);
    }

    class SortedSetPositionalParams {
        @CommandLine.Parameters(type = Integer.class)
        SortedSet<Integer> sortedSet;
    }

    @Test
    public void testSortedSetPositionalParametersAreInstantiatedIfNull() {
        CommandLineTest.SortedSetPositionalParams params = new CommandLineTest.SortedSetPositionalParams();
        Assert.assertNull(params.sortedSet);
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotNull(params.sortedSet);
        Assert.assertEquals(Arrays.asList(1, 2, 3), new ArrayList<Integer>(params.sortedSet));
    }

    @Test
    public void testSortedSetPositionalParametersAreReusedIfNonNull() {
        CommandLineTest.SortedSetPositionalParams params = new CommandLineTest.SortedSetPositionalParams();
        params.sortedSet = new TreeSet<Integer>();
        SortedSet<Integer> list = params.sortedSet;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertSame(list, params.sortedSet);
        Assert.assertEquals(Arrays.asList(1, 2, 3), new ArrayList<Integer>(params.sortedSet));
    }

    @Test
    public void testSortedSetPositionalParametersAreReplacedIfNonNull() {
        CommandLineTest.SortedSetPositionalParams params = new CommandLineTest.SortedSetPositionalParams();
        params.sortedSet = new TreeSet<Integer>();
        params.sortedSet.add(234);
        SortedSet<Integer> list = params.sortedSet;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotSame(list, params.sortedSet);
        Assert.assertEquals(Arrays.asList(1, 2, 3), new ArrayList<Integer>(params.sortedSet));
    }

    class SetPositionalParams {
        @CommandLine.Parameters(type = Integer.class)
        Set<Integer> set;
    }

    @Test
    public void testSetPositionalParametersAreInstantiatedIfNull() {
        CommandLineTest.SetPositionalParams params = new CommandLineTest.SetPositionalParams();
        Assert.assertNull(params.set);
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotNull(params.set);
        Assert.assertEquals(new HashSet<Integer>(Arrays.asList(1, 2, 3)), params.set);
    }

    @Test
    public void testSetPositionalParametersAreReusedIfNonNull() {
        CommandLineTest.SetPositionalParams params = new CommandLineTest.SetPositionalParams();
        params.set = new TreeSet<Integer>();
        Set<Integer> list = params.set;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertSame(list, params.set);
        Assert.assertEquals(new HashSet<Integer>(Arrays.asList(1, 2, 3)), params.set);
    }

    @Test
    public void testSetPositionalParametersAreReplacedIfNonNull() {
        CommandLineTest.SetPositionalParams params = new CommandLineTest.SetPositionalParams();
        params.set = new TreeSet<Integer>();
        params.set.add(234);
        Set<Integer> list = params.set;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotSame(list, params.set);
        Assert.assertEquals(new HashSet<Integer>(Arrays.asList(3, 2, 1)), params.set);
    }

    class QueuePositionalParams {
        @CommandLine.Parameters(type = Integer.class)
        Queue<Integer> queue;
    }

    @Test
    public void testQueuePositionalParametersAreInstantiatedIfNull() {
        CommandLineTest.QueuePositionalParams params = new CommandLineTest.QueuePositionalParams();
        Assert.assertNull(params.queue);
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotNull(params.queue);
        Assert.assertEquals(new LinkedList<Integer>(Arrays.asList(3, 2, 1)), params.queue);
    }

    @Test
    public void testQueuePositionalParametersAreReusedIfNonNull() {
        CommandLineTest.QueuePositionalParams params = new CommandLineTest.QueuePositionalParams();
        params.queue = new LinkedList<Integer>();
        Queue<Integer> list = params.queue;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertSame(list, params.queue);
        Assert.assertEquals(new LinkedList<Integer>(Arrays.asList(3, 2, 1)), params.queue);
    }

    @Test
    public void testQueuePositionalParametersAreReplacedIfNonNull() {
        CommandLineTest.QueuePositionalParams params = new CommandLineTest.QueuePositionalParams();
        params.queue = new LinkedList<Integer>();
        params.queue.add(234);
        Queue<Integer> list = params.queue;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotSame(list, params.queue);
        Assert.assertEquals(new LinkedList<Integer>(Arrays.asList(3, 2, 1)), params.queue);
    }

    class CollectionPositionalParams {
        @CommandLine.Parameters(type = Integer.class)
        Collection<Integer> collection;
    }

    @Test
    public void testCollectionPositionalParametersAreInstantiatedIfNull() {
        CommandLineTest.CollectionPositionalParams params = new CommandLineTest.CollectionPositionalParams();
        Assert.assertNull(params.collection);
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotNull(params.collection);
        Assert.assertEquals(Arrays.asList(3, 2, 1), params.collection);
    }

    @Test
    public void testCollectionPositionalParametersAreReusedIfNonNull() {
        CommandLineTest.CollectionPositionalParams params = new CommandLineTest.CollectionPositionalParams();
        params.collection = new ArrayList<Integer>();
        Collection<Integer> list = params.collection;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertSame(list, params.collection);
        Assert.assertEquals(Arrays.asList(3, 2, 1), params.collection);
    }

    @Test
    public void testCollectionPositionalParametersAreReplacedIfNonNull() {
        CommandLineTest.CollectionPositionalParams params = new CommandLineTest.CollectionPositionalParams();
        params.collection = new ArrayList<Integer>();
        params.collection.add(234);
        Collection<Integer> list = params.collection;
        new CommandLine(params).parse("3", "2", "1");
        Assert.assertNotSame(list, params.collection);
        Assert.assertEquals(Arrays.asList(3, 2, 1), params.collection);
    }

    @Test(expected = CommandLine.DuplicateOptionAnnotationsException.class)
    public void testDuplicateOptionsAreRejected() {
        /**
         * Duplicate parameter names are invalid.
         */
        class DuplicateOptions {
            @CommandLine.Option(names = "-duplicate")
            public int value1;

            @CommandLine.Option(names = "-duplicate")
            public int value2;
        }
        new CommandLine(new DuplicateOptions());
    }

    @Test(expected = CommandLine.DuplicateOptionAnnotationsException.class)
    public void testClashingAnnotationsAreRejected() {
        class ClashingAnnotation {
            @CommandLine.Option(names = "-o")
            @CommandLine.Parameters
            public String[] bothOptionAndParameters;
        }
        new CommandLine(new ClashingAnnotation());
    }

    private static class PrivateFinalOptionFields {
        @CommandLine.Option(names = "-f")
        private final String field = null;

        @CommandLine.Option(names = "-p")
        private final int primitive = 43;
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testPopulateRejectsPrivateFinalFields() {
        CommandLine.populateCommand(new CommandLineTest.PrivateFinalOptionFields(), "-f", "reference value");
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testConstructorRejectsPrivateFinalFields() {
        new CommandLine(new CommandLineTest.PrivateFinalOptionFields());
    }

    @Test
    public void testLastValueSelectedIfOptionSpecifiedMultipleTimes() {
        class App {
            @CommandLine.Option(names = "-f")
            String field = null;

            @CommandLine.Option(names = "-p")
            int primitive = 43;
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new App()).setOverwrittenOptionsAllowed(true);
        cmd.parse("-f", "111", "-f", "222");
        App ff = cmd.getCommand();
        Assert.assertEquals("222", ff.field);
    }

    private static class PrivateFinalParameterFields {
        @CommandLine.Parameters(index = "0")
        private final String field = null;

        @CommandLine.Parameters(index = "1", arity = "0..1")
        private final int primitive = 43;
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testPopulateRejectsInitializePrivateFinalParameterFields() {
        CommandLine.populateCommand(new CommandLineTest.PrivateFinalParameterFields(), "ref value");
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testConstructorRejectsPrivateFinalPrimitiveParameterFields() {
        new CommandLine(new CommandLineTest.PrivateFinalParameterFields());
    }

    private static class PrivateFinalAllowedFields {
        @CommandLine.Option(names = "-d")
        private final Date date = null;

        @CommandLine.Option(names = "-u")
        private final TimeUnit enumValue = TimeUnit.SECONDS;

        @CommandLine.Parameters(index = "0")
        private final Integer integer = null;

        @CommandLine.Parameters(index = "1")
        private final Long longValue = Long.valueOf(9876L);
    }

    @Test
    public void testPrivateFinalNonPrimitiveNonStringFieldsAreAllowed() throws Exception {
        CommandLineTest.PrivateFinalAllowedFields fields = new CommandLineTest.PrivateFinalAllowedFields();
        new CommandLine(fields).parse("-d=2017-11-02", "-u=MILLISECONDS", "123", "123456");
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-02"), fields.date);
        Assert.assertSame(TimeUnit.MILLISECONDS, fields.enumValue);
        Assert.assertEquals(Integer.valueOf(123), fields.integer);
        Assert.assertEquals(Long.valueOf(123456), fields.longValue);
    }

    private static class PojoWithEnumOptions {
        @CommandLine.Option(names = "-u")
        private final TimeUnit enumValue = TimeUnit.SECONDS;
    }

    @Test
    public void testParserCaseInsensitiveEnumValuesAllowed_falseByDefault() throws Exception {
        CommandLineTest.PojoWithEnumOptions fields = new CommandLineTest.PojoWithEnumOptions();
        CommandLine cmd = new CommandLine(fields);
        Assert.assertFalse(cmd.isCaseInsensitiveEnumValuesAllowed());
        try {
            cmd.parse("-u=milliseconds");
            Assert.fail("Expected exception");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().startsWith("Invalid value for option '-u': expected one of "));
        }
    }

    @Test
    public void testParserCaseInsensitiveEnumValuesAllowed_enabled() throws Exception {
        CommandLineTest.PojoWithEnumOptions fields = new CommandLineTest.PojoWithEnumOptions();
        new CommandLine(fields).setCaseInsensitiveEnumValuesAllowed(true).parse("-u=milliseconds");
        Assert.assertSame(TimeUnit.MILLISECONDS, fields.enumValue);
    }

    @Test
    public void testParserCaseInsensitiveEnumValuesAllowed_invalidInput() throws Exception {
        CommandLineTest.PojoWithEnumOptions fields = new CommandLineTest.PojoWithEnumOptions();
        CommandLine cmd = new CommandLine(fields).setCaseInsensitiveEnumValuesAllowed(true);
        try {
            cmd.parse("-u=millisecondINVALID");
            Assert.fail("Expected exception");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().startsWith("Invalid value for option '-u': expected one of "));
        }
    }

    private static class RequiredField {
        @CommandLine.Option(names = { "-?", "/?" }, help = true)
        boolean isHelpRequested;

        @CommandLine.Option(names = { "-V", "--version" }, versionHelp = true)
        boolean versionHelp;

        @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true)
        boolean usageHelp;

        @CommandLine.Option(names = "--required", required = true)
        private String required;

        @CommandLine.Parameters
        private String[] remainder;
    }

    @Test
    public void testErrorIfRequiredOptionNotSpecified() {
        try {
            CommandLine.populateCommand(new CommandLineTest.RequiredField(), "arg1", "arg2");
            Assert.fail("Missing required field should have thrown exception");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required option '--required=<required>'", ex.getMessage());
        }
    }

    @Test
    public void testNoErrorIfRequiredOptionSpecified() {
        CommandLine.populateCommand(new CommandLineTest.RequiredField(), "--required", "arg1", "arg2");
    }

    @Test
    public void testNoErrorIfRequiredOptionNotSpecifiedWhenHelpRequested() {
        CommandLineTest.RequiredField requiredField = CommandLine.populateCommand(new CommandLineTest.RequiredField(), "-?");
        Assert.assertTrue("help requested", requiredField.isHelpRequested);
    }

    @Test
    public void testNoErrorIfRequiredOptionNotSpecifiedWhenUsageHelpRequested() {
        CommandLineTest.RequiredField requiredField = CommandLine.populateCommand(new CommandLineTest.RequiredField(), "--help");
        Assert.assertTrue("usage help requested", requiredField.usageHelp);
    }

    @Test
    public void testNoErrorIfRequiredOptionNotSpecifiedWhenVersionHelpRequested() {
        CommandLineTest.RequiredField requiredField = CommandLine.populateCommand(new CommandLineTest.RequiredField(), "--version");
        Assert.assertTrue("version info requested", requiredField.versionHelp);
    }

    @Test
    public void testCommandLine_isUsageHelpRequested_trueWhenSpecified() {
        List<CommandLine> parsedCommands = new CommandLine(new CommandLineTest.RequiredField()).parse("--help");
        Assert.assertTrue("usage help requested", parsedCommands.get(0).isUsageHelpRequested());
    }

    @Test
    public void testCommandLine_isVersionHelpRequested_trueWhenSpecified() {
        List<CommandLine> parsedCommands = new CommandLine(new CommandLineTest.RequiredField()).parse("--version");
        Assert.assertTrue("version info requested", parsedCommands.get(0).isVersionHelpRequested());
    }

    @Test
    public void testCommandLine_isUsageHelpRequested_falseWhenNotSpecified() {
        List<CommandLine> parsedCommands = new CommandLine(new CommandLineTest.RequiredField()).parse("--version");
        Assert.assertFalse("usage help requested", parsedCommands.get(0).isUsageHelpRequested());
    }

    @Test
    public void testCommandLine_isVersionHelpRequested_falseWhenNotSpecified() {
        List<CommandLine> parsedCommands = new CommandLine(new CommandLineTest.RequiredField()).parse("--help");
        Assert.assertFalse("version info requested", parsedCommands.get(0).isVersionHelpRequested());
    }

    @Test
    public void testHelpRequestedFlagResetWhenParsing_staticMethod() {
        CommandLineTest.RequiredField requiredField = CommandLine.populateCommand(new CommandLineTest.RequiredField(), "-?");
        Assert.assertTrue("help requested", requiredField.isHelpRequested);
        requiredField.isHelpRequested = false;
        // should throw error again on second pass (no help was requested here...)
        try {
            CommandLine.populateCommand(requiredField, "arg1", "arg2");
            Assert.fail("Missing required field should have thrown exception");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required option '--required=<required>'", ex.getMessage());
        }
    }

    @Test
    public void testHelpRequestedFlagResetWhenParsing_instanceMethod() {
        CommandLineTest.RequiredField requiredField = new CommandLineTest.RequiredField();
        CommandLine commandLine = new CommandLine(requiredField);
        commandLine.parse("-?");
        Assert.assertTrue("help requested", requiredField.isHelpRequested);
        requiredField.isHelpRequested = false;
        // should throw error again on second pass (no help was requested here...)
        try {
            commandLine.parse("arg1", "arg2");
            Assert.fail("Missing required field should have thrown exception");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required option '--required=<required>'", ex.getMessage());
        }
    }

    static class CompactFields {
        @CommandLine.Option(names = "-v")
        boolean verbose;

        @CommandLine.Option(names = "-r")
        boolean recursive;

        @CommandLine.Option(names = "-o")
        File outputFile;

        @CommandLine.Parameters
        File[] inputFiles;
    }

    @Test
    public void testCompactFieldsAnyOrder() {
        // cmd -a -o arg path path
        // cmd -o arg -a path path
        // cmd -a -o arg -- path path
        // cmd -a -oarg path path
        // cmd -aoarg path path
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rvoout");
        CommandLineTest.verifyCompact(compact, true, true, "out", null);
        // change order within compact group
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-vroout");
        CommandLineTest.verifyCompact(compact, true, true, "out", null);
        // compact group with separator
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-vro=out");
        CommandLineTest.verifyCompact(compact, true, true, "out", null);
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rv p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, null, fileArray("p1", "p2"));
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-voout p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, false, "out", fileArray("p1", "p2"));
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-voout -r p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-r -v -oout p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rv -o out p1 p2".split(" "));// #233

        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-oout -r -v p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
        compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rvo out p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
        try {
            CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-oout -r -vp1 p2".split(" "));
            Assert.fail("should fail: -v does not take an argument");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: -p1 (while processing option: '-vp1')", ex.getMessage());
        }
    }

    @Test
    public void testCompactFieldsWithUnmatchedArguments() {
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new CommandLineTest.CompactFields()).setUnmatchedArgumentsAllowed(true);
        cmd.parse("-oout -r -vp1 p2".split(" "));
        Assert.assertEquals(Arrays.asList("-p1"), cmd.getUnmatchedArguments());
    }

    @Test
    public void testCompactWithOptionParamSeparatePlusParameters() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-r -v -o out p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
    }

    @Test
    public void testCompactWithOptionParamAttachedEqualsSeparatorChar() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rvo=out p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
    }

    @Test
    public void testCompactWithOptionParamAttachedColonSeparatorChar() {
        CommandLineTest.CompactFields compact = new CommandLineTest.CompactFields();
        CommandLine cmd = new CommandLine(compact);
        cmd.setSeparator(":");
        cmd.parse("-rvo:out p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
    }

    /**
     * See {@link #testGnuLongOptionsWithVariousSeparators()}
     */
    @Test
    public void testDefaultSeparatorIsEquals() {
        Assert.assertEquals("=", new CommandLine(new CommandLineTest.CompactFields()).getSeparator());
    }

    @Test
    public void testTrimQuotesWhenPropertyTrue() {
        System.setProperty("picocli.trimQuotes", "true");
        @CommandLine.Command
        class TopLevel {}
        CommandLine commandLine = new CommandLine(new TopLevel());
        Assert.assertEquals(true, commandLine.isTrimQuotes());
    }

    @Test
    public void testTrimQuotesWhenPropertyEmpty() {
        System.setProperty("picocli.trimQuotes", "");
        @CommandLine.Command
        class TopLevel {}
        CommandLine commandLine = new CommandLine(new TopLevel());
        Assert.assertEquals(true, commandLine.isTrimQuotes());
    }

    @Test
    public void testTrimQuotesWhenPropertyFalse() {
        System.setProperty("picocli.trimQuotes", "false");
        @CommandLine.Command
        class TopLevel {}
        CommandLine commandLine = new CommandLine(new TopLevel());
        Assert.assertEquals(false, commandLine.isTrimQuotes());
    }

    @Test
    public void testParserUnmatchedOptionsArePositionalParams_False_unmatchedOptionThrowsUnmatchedArgumentException() {
        class App {
            @CommandLine.Option(names = "-a")
            String alpha;

            @CommandLine.Parameters
            String[] remainder;
        }
        CommandLine app = new CommandLine(new App());
        try {
            app.parseArgs("-x", "-a", "AAA");
            Assert.fail("Expected exception");
        } catch (CommandLine.UnmatchedArgumentException ok) {
            Assert.assertEquals("Unknown option: -x", ok.getMessage());
        }
    }

    @Test
    public void testParserUnmatchedOptionsArePositionalParams_True_unmatchedOptionIsPositionalParam() {
        class App {
            @CommandLine.Option(names = "-a")
            String alpha;

            @CommandLine.Parameters
            String[] remainder;
        }
        App app = new App();
        CommandLine cmd = new CommandLine(app);
        cmd.setUnmatchedOptionsArePositionalParams(true);
        CommandLine.ParseResult parseResult = cmd.parseArgs("-x", "-a", "AAA");
        Assert.assertTrue(parseResult.hasMatchedPositional(0));
        Assert.assertArrayEquals(new String[]{ "-x" }, parseResult.matchedPositionalValue(0, new String[0]));
        Assert.assertTrue(parseResult.hasMatchedOption("a"));
        Assert.assertEquals("AAA", parseResult.matchedOptionValue("a", null));
        Assert.assertArrayEquals(new String[]{ "-x" }, app.remainder);
        Assert.assertEquals("AAA", app.alpha);
    }

    @Test
    public void testOptionsMixedWithParameters() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-r -v p1 -o out p2".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "out", fileArray("p1", "p2"));
    }

    @Test
    public void testShortOptionsWithSeparatorButNoValueAssignsEmptyStringEvenIfNotLast() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-ro= -v".split(" "));
        CommandLineTest.verifyCompact(compact, false, true, "-v", null);
    }

    @Test
    public void testShortOptionsWithColonSeparatorButNoValueAssignsEmptyStringEvenIfNotLast() {
        CommandLineTest.CompactFields compact = new CommandLineTest.CompactFields();
        CommandLine cmd = new CommandLine(compact);
        cmd.setSeparator(":");
        cmd.parse("-ro: -v".split(" "));
        CommandLineTest.verifyCompact(compact, false, true, "-v", null);
    }

    @Test
    public void testShortOptionsWithSeparatorButNoValueFailsIfValueRequired() {
        try {
            CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rvo=".split(" "));
            Assert.fail("Expected exception");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals("Missing required parameter for option '-o' (<outputFile>)", ex.getMessage());
        }
    }

    @Test
    public void testShortOptionsWithSeparatorAndQuotedEmptyStringValueNotLast() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-ro=\"\" -v".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "\"\"", null);
    }

    @Test
    public void testShortOptionsWithColonSeparatorAndQuotedEmptyStringValueNotLast() {
        CommandLineTest.CompactFields compact = new CommandLineTest.CompactFields();
        CommandLine cmd = new CommandLine(compact);
        cmd.setSeparator(":");
        cmd.parse("-ro:\"\" -v".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "\"\"", null);
    }

    @Test
    public void testShortOptionsWithSeparatorQuotedEmptyStringValueIfLast() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rvo=\"\"".split(" "));
        CommandLineTest.verifyCompact(compact, true, true, "\"\"", null);
    }

    @Test
    public void testParserPosixClustedShortOptions_false_resultsInShortClusteredOptionsNotRecognized() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rvoFILE");
        CommandLineTest.verifyCompact(compact, true, true, "FILE", null);
        CommandLine cmd = new CommandLine(new CommandLineTest.CompactFields());
        cmd.getCommandSpec().parser().posixClusteredShortOptionsAllowed(false);
        try {
            cmd.parse("-rvoFILE");
            Assert.fail("Expected exception");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: -rvoFILE", ex.getMessage());
        }
    }

    @Test
    public void testParserPosixClustedShortOptions_false_disallowsShortOptionsAttachedToOptionParam() {
        String[] args = new String[]{ "-oFILE" };
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), args);
        CommandLineTest.verifyCompact(compact, false, false, "FILE", null);
        CommandLineTest.CompactFields unclustered = new CommandLineTest.CompactFields();
        CommandLine cmd = new CommandLine(unclustered);
        cmd.getCommandSpec().parser().posixClusteredShortOptionsAllowed(false);
        try {
            cmd.parse(args);
            Assert.fail("Expected exception");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: -oFILE", ex.getMessage());
        }
    }

    @Test
    public void testParserPosixClustedShortOptions_false_allowsUnclusteredShortOptions() {
        String[] args = "-r -v -o FILE".split(" ");
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), args);
        CommandLineTest.verifyCompact(compact, true, true, "FILE", null);
        CommandLineTest.CompactFields unclustered = new CommandLineTest.CompactFields();
        CommandLine cmd = new CommandLine(unclustered);
        cmd.getCommandSpec().parser().posixClusteredShortOptionsAllowed(false);
        cmd.parse(args);
        CommandLineTest.verifyCompact(unclustered, true, true, "FILE", null);
    }

    @Test
    public void testDoubleDashSeparatesPositionalParameters() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-oout -- -r -v p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, false, false, "out", fileArray("-r", "-v", "p1", "p2"));
    }

    @Test
    public void testEndOfOptionsSeparatorConfigurable() {
        CommandLineTest.CompactFields compact = new CommandLineTest.CompactFields();
        CommandLine cmd = new CommandLine(compact);
        cmd.setEndOfOptionsDelimiter(";;");
        cmd.parse("-oout ;; ;; -- -r -v p1 p2".split(" "));
        CommandLineTest.verifyCompact(compact, false, false, "out", fileArray(";;", "--", "-r", "-v", "p1", "p2"));
    }

    @Test
    public void testEndOfOptionsSeparatorCannotBeNull() {
        try {
            new CommandLine(new CommandLineTest.CompactFields()).setEndOfOptionsDelimiter(null);
            Assert.fail("Expected exception");
        } catch (Exception ok) {
            Assert.assertEquals("java.lang.NullPointerException: end-of-options delimiter", ok.toString());
        }
    }

    @Test
    public void testDebugOutputForDoubleDashSeparatesPositionalParameters() throws Exception {
        CommandLineTest.clearBuiltInTracingCache();
        PrintStream originalErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2500);
        System.setErr(new PrintStream(baos));
        final String PROPERTY = "picocli.trace";
        String old = System.getProperty(PROPERTY);
        System.setProperty(PROPERTY, "DEBUG");
        CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-oout -- -r -v p1 p2".split(" "));
        System.setErr(originalErr);
        if (old == null) {
            System.clearProperty(PROPERTY);
        } else {
            System.setProperty(PROPERTY, old);
        }
        String prefix8 = String.format(("" + ((((((((((((("[picocli DEBUG] Could not register converter for java.time.Duration: java.lang.ClassNotFoundException: java.time.Duration%n" + "[picocli DEBUG] Could not register converter for java.time.Instant: java.lang.ClassNotFoundException: java.time.Instant%n") + "[picocli DEBUG] Could not register converter for java.time.LocalDate: java.lang.ClassNotFoundException: java.time.LocalDate%n") + "[picocli DEBUG] Could not register converter for java.time.LocalDateTime: java.lang.ClassNotFoundException: java.time.LocalDateTime%n") + "[picocli DEBUG] Could not register converter for java.time.LocalTime: java.lang.ClassNotFoundException: java.time.LocalTime%n") + "[picocli DEBUG] Could not register converter for java.time.MonthDay: java.lang.ClassNotFoundException: java.time.MonthDay%n") + "[picocli DEBUG] Could not register converter for java.time.OffsetDateTime: java.lang.ClassNotFoundException: java.time.OffsetDateTime%n") + "[picocli DEBUG] Could not register converter for java.time.OffsetTime: java.lang.ClassNotFoundException: java.time.OffsetTime%n") + "[picocli DEBUG] Could not register converter for java.time.Period: java.lang.ClassNotFoundException: java.time.Period%n") + "[picocli DEBUG] Could not register converter for java.time.Year: java.lang.ClassNotFoundException: java.time.Year%n") + "[picocli DEBUG] Could not register converter for java.time.YearMonth: java.lang.ClassNotFoundException: java.time.YearMonth%n") + "[picocli DEBUG] Could not register converter for java.time.ZonedDateTime: java.lang.ClassNotFoundException: java.time.ZonedDateTime%n") + "[picocli DEBUG] Could not register converter for java.time.ZoneId: java.lang.ClassNotFoundException: java.time.ZoneId%n") + "[picocli DEBUG] Could not register converter for java.time.ZoneOffset: java.lang.ClassNotFoundException: java.time.ZoneOffset%n")));
        String prefix7 = String.format(("" + "[picocli DEBUG] Could not register converter for java.nio.file.Path: java.lang.ClassNotFoundException: java.nio.file.Path%n"));
        String expected = String.format(("" + ((((((((((((((((((((((((((((((((("[picocli DEBUG] Creating CommandSpec for object of class picocli.CommandLineTest$CompactFields with factory picocli.CommandLine$DefaultFactory%n" + "[picocli INFO] Picocli version: %3$s%n") + "[picocli INFO] Parsing 6 command line args [-oout, --, -r, -v, p1, p2]%n") + "[picocli DEBUG] Parser configuration: posixClusteredShortOptionsAllowed=true, stopAtPositional=false, stopAtUnmatched=false, separator=null, overwrittenOptionsAllowed=false, unmatchedArgumentsAllowed=false, expandAtFiles=true, atFileCommentChar=#, useSimplifiedAtFiles=false, endOfOptionsDelimiter=--, limitSplit=false, aritySatisfiedByAttachedOptionParam=false, toggleBooleanFlags=true, unmatchedOptionsArePositionalParams=false, collectErrors=false,caseInsensitiveEnumValuesAllowed=false, trimQuotes=false, splitQuotedStrings=false%n") + "[picocli DEBUG] (ANSI is disabled by default: isatty=...)%n") + "[picocli DEBUG] Set initial value for field boolean picocli.CommandLineTest$CompactFields.verbose of type boolean to false.%n") + "[picocli DEBUG] Set initial value for field boolean picocli.CommandLineTest$CompactFields.recursive of type boolean to false.%n") + "[picocli DEBUG] Set initial value for field java.io.File picocli.CommandLineTest$CompactFields.outputFile of type class java.io.File to null.%n") + "[picocli DEBUG] Set initial value for field java.io.File[] picocli.CommandLineTest$CompactFields.inputFiles of type class [Ljava.io.File; to null.%n") + "[picocli DEBUG] Initializing %1$s$CompactFields: 3 options, 1 positional parameters, 0 required, 0 subcommands.%n") + "[picocli DEBUG] Processing argument '-oout'. Remainder=[--, -r, -v, p1, p2]%n") + "[picocli DEBUG] '-oout' cannot be separated into <option>=<option-parameter>%n") + "[picocli DEBUG] Trying to process '-oout' as clustered short options%n") + "[picocli DEBUG] Found option '-o' in -oout: field java.io.File %1$s$CompactFields.outputFile, arity=1%n") + "[picocli DEBUG] Trying to process 'out' as option parameter%n") + "[picocli INFO] Setting field java.io.File picocli.CommandLineTest$CompactFields.outputFile to 'out' (was 'null') for option -o%n") + "[picocli DEBUG] Processing argument '--'. Remainder=[-r, -v, p1, p2]%n") + "[picocli INFO] Found end-of-options delimiter '--'. Treating remainder as positional parameters.%n") + "[picocli DEBUG] Processing next arg as a positional parameter at index=0. Remainder=[-r, -v, p1, p2]%n") + "[picocli DEBUG] Position 0 is in index range 0..*. Trying to assign args to field java.io.File[] %1$s$CompactFields.inputFiles, arity=0..1%n") + "[picocli INFO] Adding [-r] to field java.io.File[] picocli.CommandLineTest$CompactFields.inputFiles for args[0..*] at position 0%n") + "[picocli DEBUG] Consumed 1 arguments and 0 interactive values, moving position to index 1.%n") + "[picocli DEBUG] Processing next arg as a positional parameter at index=1. Remainder=[-v, p1, p2]%n") + "[picocli DEBUG] Position 1 is in index range 0..*. Trying to assign args to field java.io.File[] %1$s$CompactFields.inputFiles, arity=0..1%n") + "[picocli INFO] Adding [-v] to field java.io.File[] picocli.CommandLineTest$CompactFields.inputFiles for args[0..*] at position 1%n") + "[picocli DEBUG] Consumed 1 arguments and 0 interactive values, moving position to index 2.%n") + "[picocli DEBUG] Processing next arg as a positional parameter at index=2. Remainder=[p1, p2]%n") + "[picocli DEBUG] Position 2 is in index range 0..*. Trying to assign args to field java.io.File[] %1$s$CompactFields.inputFiles, arity=0..1%n") + "[picocli INFO] Adding [p1] to field java.io.File[] picocli.CommandLineTest$CompactFields.inputFiles for args[0..*] at position 2%n") + "[picocli DEBUG] Consumed 1 arguments and 0 interactive values, moving position to index 3.%n") + "[picocli DEBUG] Processing next arg as a positional parameter at index=3. Remainder=[p2]%n") + "[picocli DEBUG] Position 3 is in index range 0..*. Trying to assign args to field java.io.File[] %1$s$CompactFields.inputFiles, arity=0..1%n") + "[picocli INFO] Adding [p2] to field java.io.File[] picocli.CommandLineTest$CompactFields.inputFiles for args[0..*] at position 3%n") + "[picocli DEBUG] Consumed 1 arguments and 0 interactive values, moving position to index 4.%n")), CommandLineTest.class.getName(), new File("/home/rpopma/picocli"), CommandLine.versionString());
        String actual = new String(baos.toByteArray(), "UTF8");
        // System.out.println(actual);
        if ((System.getProperty("java.version").compareTo("1.7.0")) < 0) {
            expected = prefix7 + expected;
        }
        if ((System.getProperty("java.version").compareTo("1.8.0")) < 0) {
            expected = prefix8 + expected;
        }
        Assert.assertEquals(PicocliTestUtil.stripAnsiTrace(expected), PicocliTestUtil.stripAnsiTrace(actual));
    }

    @Test
    public void testTracerIsWarn() {
        final String PROPERTY = "picocli.trace";
        String old = System.getProperty(PROPERTY);
        try {
            System.clearProperty(PROPERTY);
            Assert.assertTrue("WARN enabled by default", new CommandLine.Tracer().isWarn());
            System.setProperty(PROPERTY, "OFF");
            Assert.assertFalse("WARN can be disabled by setting to OFF", new CommandLine.Tracer().isWarn());
            System.setProperty(PROPERTY, "WARN");
            Assert.assertTrue("WARN can be explicitly enabled", new CommandLine.Tracer().isWarn());
        } finally {
            if (old == null) {
                System.clearProperty(PROPERTY);
            } else {
                System.setProperty(PROPERTY, old);
            }
        }
    }

    @Test
    public void testAssertEquals() throws Exception {
        Method m = Class.forName("picocli.CommandLine$Assert").getDeclaredMethod("equals", Object.class, Object.class);
        m.setAccessible(true);
        Assert.assertTrue("null equals null", ((Boolean) (m.invoke(null, null, null))));
        Assert.assertFalse("String !equals null", ((Boolean) (m.invoke(null, "", null))));
        Assert.assertFalse("null !equals String", ((Boolean) (m.invoke(null, null, ""))));
        Assert.assertTrue("String equals String", ((Boolean) (m.invoke(null, "", ""))));
    }

    @Test
    public void testAssertAssertTrue() throws Exception {
        Method m = Class.forName("picocli.CommandLine$Assert").getDeclaredMethod("assertTrue", boolean.class, String.class);
        m.setAccessible(true);
        m.invoke(null, true, "not thrown");
        try {
            m.invoke(null, false, "thrown");
        } catch (InvocationTargetException ex) {
            IllegalStateException actual = ((IllegalStateException) (ex.getTargetException()));
            Assert.assertEquals("thrown", actual.getMessage());
        }
    }

    @Test
    public void testNonSpacedOptions() {
        CommandLineTest.CompactFields compact = CommandLine.populateCommand(new CommandLineTest.CompactFields(), "-rvo arg path path".split(" "));
        Assert.assertTrue("-r", compact.recursive);
        Assert.assertTrue("-v", compact.verbose);
        Assert.assertEquals("-o", new File("arg"), compact.outputFile);
        Assert.assertArrayEquals("args", new File[]{ new File("path"), new File("path") }, compact.inputFiles);
    }

    @Test
    public void testPrimitiveParameters() {
        class PrimitiveIntParameters {
            @CommandLine.Parameters
            int[] intParams;
        }
        PrimitiveIntParameters params = CommandLine.populateCommand(new PrimitiveIntParameters(), "1 2 3 4".split(" "));
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, params.intParams);
    }

    @Test(expected = CommandLine.MissingTypeConverterException.class)
    public void testMissingTypeConverter() {
        class MissingConverter {
            @CommandLine.Option(names = "--socket")
            Socket socket;
        }
        CommandLine.populateCommand(new MissingConverter(), "--socket anyString".split(" "));
    }

    @Test
    public void testParametersDeclaredOutOfOrderWithNoArgs() {
        class WithParams {
            @CommandLine.Parameters(index = "1")
            String param1;

            @CommandLine.Parameters(index = "0")
            String param0;
        }
        try {
            CommandLine.populateCommand(new WithParams(), new String[0]);
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required parameters: <param0>, <param1>", ex.getMessage());
        }
    }

    class VariousPrefixCharacters {
        @CommandLine.Option(names = { "-d", "--dash" })
        int dash;

        @CommandLine.Option(names = { "/S" })
        int slashS;

        @CommandLine.Option(names = { "/T" })
        int slashT;

        @CommandLine.Option(names = { "/4" })
        boolean fourDigit;

        @CommandLine.Option(names = { "/Owner", "--owner" })
        String owner;

        @CommandLine.Option(names = { "-SingleDash" })
        boolean singleDash;

        @CommandLine.Option(names = { "[CPM" })
        String cpm;

        @CommandLine.Option(names = { "(CMS" })
        String cms;
    }

    @Test
    public void testOptionsMayDefineAnyPrefixChar() {
        CommandLineTest.VariousPrefixCharacters params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "-d 123 /4 /S 765 /T=98 /Owner=xyz -SingleDash [CPM CP/M (CMS=cmsVal".split(" "));
        Assert.assertEquals("-d", 123, params.dash);
        Assert.assertEquals("/S", 765, params.slashS);
        Assert.assertEquals("/T", 98, params.slashT);
        Assert.assertTrue("/4", params.fourDigit);
        Assert.assertTrue("-SingleDash", params.singleDash);
        Assert.assertEquals("/Owner", "xyz", params.owner);
        Assert.assertEquals("[CPM", "CP/M", params.cpm);
        Assert.assertEquals("(CMS", "cmsVal", params.cms);
    }

    @Test
    public void testGnuLongOptionsWithVariousSeparators() {
        CommandLineTest.VariousPrefixCharacters params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--dash 123".split(" "));
        Assert.assertEquals("--dash val", 123, params.dash);
        params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--dash=234 --owner=x".split(" "));
        Assert.assertEquals("--dash=val", 234, params.dash);
        Assert.assertEquals("--owner=x", "x", params.owner);
        params = new CommandLineTest.VariousPrefixCharacters();
        CommandLine cmd = new CommandLine(params);
        cmd.setSeparator(":");
        cmd.parse("--dash:345");
        Assert.assertEquals("--dash:val", 345, params.dash);
        params = new CommandLineTest.VariousPrefixCharacters();
        cmd = new CommandLine(params);
        cmd.setSeparator(":");
        cmd.parse("--dash:345 --owner:y".split(" "));
        Assert.assertEquals("--dash:val", 345, params.dash);
        Assert.assertEquals("--owner:y", "y", params.owner);
    }

    @Test
    public void testSeparatorCanBeSetDeclaratively() {
        @CommandLine.Command(separator = ":")
        class App {
            @CommandLine.Option(names = "--opt", required = true)
            String opt;
        }
        try {
            CommandLine.populateCommand(new App(), "--opt=abc");
            Assert.fail("Expected failure with unknown separator");
        } catch (CommandLine.MissingParameterException ok) {
            Assert.assertEquals("Missing required option '--opt:<opt>'", ok.getMessage());
        }
    }

    @Test
    public void testIfSeparatorSetTheDefaultSeparatorIsNotRecognized() {
        @CommandLine.Command(separator = ":")
        class App {
            @CommandLine.Option(names = "--opt", required = true)
            String opt;
        }
        try {
            CommandLine.populateCommand(new App(), "--opt=abc");
            Assert.fail("Expected failure with unknown separator");
        } catch (CommandLine.MissingParameterException ok) {
            Assert.assertEquals("Missing required option '--opt:<opt>'", ok.getMessage());
        }
    }

    @Test
    public void testIfSeparatorSetTheDefaultSeparatorIsNotRecognizedWithUnmatchedArgsAllowed() {
        @CommandLine.Command(separator = ":")
        class App {
            @CommandLine.Option(names = "--opt", required = true)
            String opt;
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new App()).setUnmatchedArgumentsAllowed(true);
        try {
            cmd.parse("--opt=abc");
            Assert.fail("Expected MissingParameterException");
        } catch (CommandLine.MissingParameterException ok) {
            Assert.assertEquals("Missing required option '--opt:<opt>'", ok.getMessage());
            Assert.assertEquals(Arrays.asList("--opt=abc"), cmd.getUnmatchedArguments());
        }
    }

    @Test
    public void testGnuLongOptionsWithVariousSeparatorsOnlyAndNoValue() {
        CommandLineTest.VariousPrefixCharacters params;
        try {
            params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--dash".split(" "));
            Assert.fail("int option needs arg");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals("Missing required parameter for option '--dash' (<dash>)", ex.getMessage());
        }
        try {
            params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--owner".split(" "));
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals("Missing required parameter for option '--owner' (<owner>)", ex.getMessage());
        }
        params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--owner=".split(" "));
        Assert.assertEquals("--owner= (at end)", "", params.owner);
        params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--owner= /4".split(" "));
        Assert.assertEquals("--owner= (in middle)", "", params.owner);
        Assert.assertEquals("/4", true, params.fourDigit);
        try {
            params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--dash=".split(" "));
            Assert.fail("int option (with sep but no value) needs arg");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals("Invalid value for option '--dash': '' is not an int", ex.getMessage());
        }
        try {
            params = CommandLine.populateCommand(new CommandLineTest.VariousPrefixCharacters(), "--dash= /4".split(" "));
            Assert.fail("int option (with sep but no value, followed by other option) needs arg");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals("Invalid value for option '--dash': '' is not an int", ex.getMessage());
        }
    }

    @Test
    public void testOptionParameterSeparatorIsCustomizable() {
        CommandLineTest.VariousPrefixCharacters params = new CommandLineTest.VariousPrefixCharacters();
        CommandLine cmd = new CommandLine(params);
        cmd.setSeparator(":");
        cmd.parse("-d 123 /4 /S 765 /T:98 /Owner:xyz -SingleDash [CPM CP/M (CMS:cmsVal".split(" "));
        Assert.assertEquals("-d", 123, params.dash);
        Assert.assertEquals("/S", 765, params.slashS);
        Assert.assertEquals("/T", 98, params.slashT);
        Assert.assertTrue("/4", params.fourDigit);
        Assert.assertTrue("-SingleDash", params.singleDash);
        Assert.assertEquals("/Owner", "xyz", params.owner);
        Assert.assertEquals("[CPM", "CP/M", params.cpm);
        Assert.assertEquals("(CMS", "cmsVal", params.cms);
    }

    @Test(expected = NullPointerException.class)
    public void testOptionParameterSeparatorCannotBeSetToNull() {
        CommandLine cmd = new CommandLine(new CommandLineTest.VariousPrefixCharacters());
        cmd.setSeparator(null);
    }

    @Test
    public void testPotentiallyNestedOptionParsedCorrectly() {
        class MyOption {
            @CommandLine.Option(names = "-p")
            String path;
        }
        MyOption opt = CommandLine.populateCommand(new MyOption(), "-pa-p");
        Assert.assertEquals("a-p", opt.path);
        opt = CommandLine.populateCommand(new MyOption(), "-p-ap");
        Assert.assertEquals("-ap", opt.path);
    }

    @Test
    public void testArityGreaterThanOneForSingleValuedFields() {
        class Arity2 {
            @CommandLine.Option(names = "-p", arity = "2")
            String path;

            @CommandLine.Option(names = "-o", arity = "2")
            String[] otherPath;
        }
        Arity2 opt = CommandLine.populateCommand(new Arity2(), "-o a b".split(" "));
        try {
            opt = CommandLine.populateCommand(new Arity2(), "-p a b".split(" "));
            Assert.fail("expected exception");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched argument: b", ex.getMessage());
        }
    }

    @Test
    public void testOptionParameterQuotesNotRemovedFromValue() {
        class TextOption {
            @CommandLine.Option(names = "-t")
            String text;
        }
        TextOption opt = CommandLine.populateCommand(new TextOption(), "-t", "\"a text\"");
        Assert.assertEquals("\"a text\"", opt.text);
    }

    @Test
    public void testLongOptionAttachedQuotedParameterQuotesNotRemovedFromValue() {
        class TextOption {
            @CommandLine.Option(names = "--text")
            String text;
        }
        TextOption opt = CommandLine.populateCommand(new TextOption(), "--text=\"a text\"");
        Assert.assertEquals("\"a text\"", opt.text);
    }

    @Test
    public void testShortOptionAttachedQuotedParameterQuotesNotRemovedFromValue() {
        class TextOption {
            @CommandLine.Option(names = "-t")
            String text;
        }
        TextOption opt = CommandLine.populateCommand(new TextOption(), "-t\"a text\"");
        Assert.assertEquals("\"a text\"", opt.text);
        opt = CommandLine.populateCommand(new TextOption(), "-t=\"a text\"");
        Assert.assertEquals("\"a text\"", opt.text);
    }

    @Test
    public void testShortOptionAttachedQuotedParameterQuotesTrimmedIfRequested() {
        class TextOption {
            @CommandLine.Option(names = "-t")
            String text;
        }
        TextOption opt = new TextOption();
        new CommandLine(opt).parseArgs("-t\"a text\"");
        Assert.assertEquals("Not trimmed by default", "\"a text\"", opt.text);
        opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t\"a text\"");
        Assert.assertEquals("trimmed if requested", "a text", opt.text);
        opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t=\"a text\"");
        Assert.assertEquals("a text", opt.text);
        opt = new TextOption();
        new CommandLine(opt).parseArgs("-t=\"a text\"");
        Assert.assertEquals("Not trimmed by default", "\"a text\"", opt.text);
    }

    @Test
    public void testShortOptionQuotedParameterTypeConversion() {
        class TextOption {
            @CommandLine.Option(names = "-t")
            int[] number;

            @CommandLine.Option(names = "-v", arity = "1")
            boolean verbose;
        }
        TextOption opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t", "\"123\"", "-v", "\"true\"");
        Assert.assertEquals(123, opt.number[0]);
        Assert.assertTrue(opt.verbose);
        opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t\"123\"", "-v\"true\"");
        Assert.assertEquals(123, opt.number[0]);
        Assert.assertTrue(opt.verbose);
        opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t=\"345\"", "-v=\"true\"");
        Assert.assertEquals(345, opt.number[0]);
        Assert.assertTrue(opt.verbose);
    }

    @Test
    public void testOptionMultiParameterQuotesNotRemovedFromValue() {
        class TextOption {
            @CommandLine.Option(names = "-t")
            String[] text;
        }
        TextOption opt = CommandLine.populateCommand(new TextOption(), "-t", "\"a text\"", "-t", "\"another text\"", "-t", "\"x z\"");
        Assert.assertArrayEquals(new String[]{ "\"a text\"", "\"another text\"", "\"x z\"" }, opt.text);
        opt = CommandLine.populateCommand(new TextOption(), "-t\"a text\"", "-t\"another text\"", "-t\"x z\"");
        Assert.assertArrayEquals(new String[]{ "\"a text\"", "\"another text\"", "\"x z\"" }, opt.text);
        opt = CommandLine.populateCommand(new TextOption(), "-t=\"a text\"", "-t=\"another text\"", "-t=\"x z\"");
        Assert.assertArrayEquals(new String[]{ "\"a text\"", "\"another text\"", "\"x z\"" }, opt.text);
        try {
            opt = CommandLine.populateCommand(new TextOption(), "-t=\"a text\"", "-t=\"another text\"", "\"x z\"");
            Assert.fail("Expected UnmatchedArgumentException");
        } catch (CommandLine.UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched argument: \"x z\"", ok.getMessage());
        }
    }

    @Test
    public void testOptionMultiParameterQuotesTrimmedIfRequested() {
        class TextOption {
            @CommandLine.Option(names = "-t")
            String[] text;
        }
        TextOption opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t", "\"a text\"", "-t", "\"another text\"", "-t", "\"x z\"");
        Assert.assertArrayEquals(new String[]{ "a text", "another text", "x z" }, opt.text);
        opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t\"a text\"", "-t\"another text\"", "-t\"x z\"");
        Assert.assertArrayEquals(new String[]{ "a text", "another text", "x z" }, opt.text);
        opt = new TextOption();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("-t=\"a text\"", "-t=\"another text\"", "-t=\"x z\"");
        Assert.assertArrayEquals(new String[]{ "a text", "another text", "x z" }, opt.text);
    }

    @Test
    public void testPositionalParameterQuotesNotRemovedFromValue() {
        class TextParams {
            @CommandLine.Parameters
            String[] text;
        }
        TextParams opt = CommandLine.populateCommand(new TextParams(), "\"a text\"");
        Assert.assertEquals("\"a text\"", opt.text[0]);
    }

    @Test
    public void testPositionalParameterQuotesTrimmedIfRequested() {
        class TextParams {
            @CommandLine.Parameters
            String[] text;
        }
        TextParams opt = new TextParams();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("\"a text\"");
        Assert.assertEquals("a text", opt.text[0]);
    }

    @Test
    public void testPositionalMultiParameterQuotesNotRemovedFromValue() {
        class TextParams {
            @CommandLine.Parameters
            String[] text;
        }
        TextParams opt = CommandLine.populateCommand(new TextParams(), "\"a text\"", "\"another text\"", "\"x z\"");
        Assert.assertArrayEquals(new String[]{ "\"a text\"", "\"another text\"", "\"x z\"" }, opt.text);
    }

    @Test
    public void testPositionalMultiParameterQuotesTrimmedIfRequested() {
        class TextParams {
            @CommandLine.Parameters
            String[] text;
        }
        TextParams opt = new TextParams();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("\"a text\"", "\"another text\"", "\"x z\"");
        Assert.assertArrayEquals(new String[]{ "a text", "another text", "x z" }, opt.text);
    }

    @Test
    public void testPositionalMultiQuotedParameterTypeConversion() {
        class TextParams {
            @CommandLine.Parameters
            int[] numbers;
        }
        TextParams opt = new TextParams();
        new CommandLine(opt).setTrimQuotes(true).parseArgs("\"123\"", "\"456\"", "\"999\"");
        Assert.assertArrayEquals(new int[]{ 123, 456, 999 }, opt.numbers);
    }

    @Test
    public void testSubclassedOptions() {
        class ParentOption {
            @CommandLine.Option(names = "-p")
            String path;
        }
        class ChildOption extends ParentOption {
            @CommandLine.Option(names = "-t")
            String text;
        }
        ChildOption opt = CommandLine.populateCommand(new ChildOption(), "-p", "somePath", "-t", "\"a text\"");
        Assert.assertEquals("somePath", opt.path);
        Assert.assertEquals("\"a text\"", opt.text);
    }

    @Test
    public void testSubclassedOptionsWithShadowedOptionNameThrowsDuplicateOptionAnnotationsException() {
        class ParentOption {
            @CommandLine.Option(names = "-p")
            String path;
        }
        class ChildOption extends ParentOption {
            @CommandLine.Option(names = "-p")
            String text;
        }
        try {
            CommandLine.populateCommand(new ChildOption(), "");
            Assert.fail("expected CommandLine$DuplicateOptionAnnotationsException");
        } catch (CommandLine.DuplicateOptionAnnotationsException ex) {
            String expected = String.format("Option name '-p' is used by both field String %s.text and field String %s.path", ChildOption.class.getName(), ParentOption.class.getName());
            Assert.assertEquals(expected, ex.getMessage());
        }
    }

    @Test
    public void testSubclassedOptionsWithShadowedFieldInitializesChildField() {
        class ParentOption {
            @CommandLine.Option(names = "-parentPath")
            String path;
        }
        class ChildOption extends ParentOption {
            @CommandLine.Option(names = "-childPath")
            String path;
        }
        ChildOption opt = CommandLine.populateCommand(new ChildOption(), "-childPath", "somePath");
        Assert.assertEquals("somePath", opt.path);
        opt = CommandLine.populateCommand(new ChildOption(), "-parentPath", "somePath");
        Assert.assertNull(opt.path);
    }

    @Test
    public void testPositionalParamWithAbsoluteIndex() {
        class App {
            @CommandLine.Parameters(index = "0")
            File file0;

            @CommandLine.Parameters(index = "1")
            File file1;

            @CommandLine.Parameters(index = "2", arity = "0..1")
            File file2;

            @CommandLine.Parameters
            List<String> all;
        }
        App app1 = CommandLine.populateCommand(new App(), "000", "111", "222", "333");
        Assert.assertEquals("arg[0]", new File("000"), app1.file0);
        Assert.assertEquals("arg[1]", new File("111"), app1.file1);
        Assert.assertEquals("arg[2]", new File("222"), app1.file2);
        Assert.assertEquals("args", Arrays.asList("000", "111", "222", "333"), app1.all);
        App app2 = CommandLine.populateCommand(new App(), "000", "111");
        Assert.assertEquals("arg[0]", new File("000"), app2.file0);
        Assert.assertEquals("arg[1]", new File("111"), app2.file1);
        Assert.assertEquals("arg[2]", null, app2.file2);
        Assert.assertEquals("args", Arrays.asList("000", "111"), app2.all);
        try {
            CommandLine.populateCommand(new App(), "000");
            Assert.fail("Should fail with missingParamException");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <file1>", ex.getMessage());
        }
    }

    @Test
    public void testPositionalParamWithFixedIndexRange() {
        System.setProperty("picocli.trace", "OFF");
        class App {
            @CommandLine.Parameters(index = "0..1")
            File file0_1;

            @CommandLine.Parameters(index = "1..2", type = File.class)
            List<File> fileList1_2;

            @CommandLine.Parameters(index = "0..3")
            File[] fileArray0_3 = new File[4];

            @CommandLine.Parameters
            List<String> all;
        }
        App app1 = new App();
        new CommandLine(app1).setOverwrittenOptionsAllowed(true).parse("000", "111", "222", "333");
        Assert.assertEquals("field initialized with arg[0]", new File("111"), app1.file0_1);
        Assert.assertEquals("arg[1] and arg[2]", Arrays.asList(new File("111"), new File("222")), app1.fileList1_2);
        Assert.assertArrayEquals("arg[0-3]", new File[]{ // null, null, null, null, // #216 default values are replaced
        new File("000"), new File("111"), new File("222"), new File("333") }, app1.fileArray0_3);
        Assert.assertEquals("args", Arrays.asList("000", "111", "222", "333"), app1.all);
        App app2 = new App();
        new CommandLine(app2).setOverwrittenOptionsAllowed(true).parse("000", "111");
        Assert.assertEquals("field initialized with arg[0]", new File("111"), app2.file0_1);
        Assert.assertEquals("arg[1]", Arrays.asList(new File("111")), app2.fileList1_2);
        Assert.assertArrayEquals("arg[0-3]", new File[]{ // null, null, null, null,  // #216 default values are replaced
        new File("000"), new File("111") }, app2.fileArray0_3);
        Assert.assertEquals("args", Arrays.asList("000", "111"), app2.all);
        App app3 = CommandLine.populateCommand(new App(), "000");
        Assert.assertEquals("field initialized with arg[0]", new File("000"), app3.file0_1);
        Assert.assertEquals("arg[1]", null, app3.fileList1_2);
        Assert.assertArrayEquals("arg[0-3]", new File[]{ // null, null, null, null,  // #216 default values are replaced
        new File("000") }, app3.fileArray0_3);
        Assert.assertEquals("args", Arrays.asList("000"), app3.all);
        try {
            CommandLine.populateCommand(new App());
            Assert.fail("Should fail with missingParamException");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <file0_1>", ex.getMessage());
        }
    }

    @Test
    public void testPositionalParamWithFixedAndVariableIndexRanges() throws Exception {
        class App {
            @CommandLine.Parameters(index = "0")
            InetAddress host1;

            @CommandLine.Parameters(index = "1")
            int port1;

            @CommandLine.Parameters(index = "2")
            InetAddress host2;

            @CommandLine.Parameters(index = "3..4", arity = "1..2")
            int[] port2range;

            @CommandLine.Parameters(index = "4..*")
            String[] files;
        }
        App app1 = CommandLine.populateCommand(new App(), "localhost", "1111", "localhost", "2222", "3333", "file1", "file2");
        Assert.assertEquals(InetAddress.getByName("localhost"), app1.host1);
        Assert.assertEquals(1111, app1.port1);
        Assert.assertEquals(InetAddress.getByName("localhost"), app1.host2);
        Assert.assertArrayEquals(new int[]{ 2222, 3333 }, app1.port2range);
        Assert.assertArrayEquals(new String[]{ "file1", "file2" }, app1.files);
    }

    @Test
    public void testPositionalParamWithFixedIndexRangeAndVariableArity() throws Exception {
        // #70
        class App {
            @CommandLine.Parameters(index = "0")
            InetAddress host1;

            @CommandLine.Parameters(index = "1")
            int port1;

            @CommandLine.Parameters(index = "2")
            InetAddress host2;

            @CommandLine.Parameters(index = "3..4", arity = "1..2")
            int[] port2range;

            @CommandLine.Parameters(index = "4..*")
            String[] files;
        }
        App app2 = CommandLine.populateCommand(new App(), "localhost", "1111", "localhost", "2222", "file1", "file2");
        Assert.assertEquals(InetAddress.getByName("localhost"), app2.host1);
        Assert.assertEquals(1111, app2.port1);
        Assert.assertEquals(InetAddress.getByName("localhost"), app2.host2);
        Assert.assertArrayEquals(new int[]{ 2222 }, app2.port2range);
        Assert.assertArrayEquals(new String[]{ "file1", "file2" }, app2.files);
    }

    @Test
    public void test70MultiplePositionalsConsumeSamePosition() {
        class App {
            @CommandLine.Parameters(index = "0..3")
            String[] posA;

            @CommandLine.Parameters(index = "2..4")
            String[] posB;

            @CommandLine.Unmatched
            String[] unmatched;
        }
        App app = CommandLine.populateCommand(new App(), "A B C D E F".split(" "));
        Assert.assertArrayEquals(new String[]{ "A", "B", "C", "D" }, app.posA);
        Assert.assertArrayEquals(new String[]{ "C", "D", "E" }, app.posB);
        Assert.assertArrayEquals(new String[]{ "F" }, app.unmatched);
    }

    @Test
    public void test70PositionalOnlyConsumesPositionWhenTypeConversionSucceeds() {
        class App {
            @CommandLine.Parameters(index = "0..3")
            int[] posA;

            @CommandLine.Parameters(index = "2..4")
            String[] posB;

            @CommandLine.Unmatched
            String[] unmatched;
        }
        App app = CommandLine.populateCommand(new App(), "11 22 C D E F".split(" "));
        Assert.assertArrayEquals("posA cannot consume positions 2 and 3", new int[]{ 11, 22 }, app.posA);
        Assert.assertArrayEquals(new String[]{ "C", "D", "E" }, app.posB);
        Assert.assertArrayEquals(new String[]{ "F" }, app.unmatched);
    }

    @Test
    public void test70PositionalOnlyConsumesPositionWhenTypeConversionSucceeds2() {
        class App {
            @CommandLine.Parameters(index = "0..3")
            String[] posA;

            @CommandLine.Parameters(index = "2..4")
            int[] posB;

            @CommandLine.Unmatched
            String[] unmatched;
        }
        App app = CommandLine.populateCommand(new App(), "A B C 33 44 55".split(" "));
        Assert.assertArrayEquals(new String[]{ "A", "B", "C", "33" }, app.posA);
        Assert.assertArrayEquals(new int[]{ 33, 44 }, app.posB);
        Assert.assertArrayEquals(new String[]{ "55" }, app.unmatched);
    }

    @Test(expected = CommandLine.ParameterIndexGapException.class)
    public void testPositionalParamWithIndexGap_SkipZero() throws Exception {
        class SkipZero {
            @CommandLine.Parameters(index = "1")
            String str;
        }
        CommandLine.populateCommand(new SkipZero(), "val1", "val2");
    }

    @Test(expected = CommandLine.ParameterIndexGapException.class)
    public void testPositionalParamWithIndexGap_RangeSkipZero() throws Exception {
        class SkipZero {
            @CommandLine.Parameters(index = "1..*")
            String str;
        }
        CommandLine.populateCommand(new SkipZero(), "val1", "val2");
    }

    @Test(expected = CommandLine.ParameterIndexGapException.class)
    public void testPositionalParamWithIndexGap_FixedIndexGap() throws Exception {
        class SkipOne {
            @CommandLine.Parameters(index = "0")
            String str0;

            @CommandLine.Parameters(index = "2")
            String str2;
        }
        CommandLine.populateCommand(new SkipOne(), "val1", "val2");
    }

    @Test(expected = CommandLine.ParameterIndexGapException.class)
    public void testPositionalParamWithIndexGap_RangeIndexGap() throws Exception {
        class SkipTwo {
            @CommandLine.Parameters(index = "0..1")
            String str0;

            @CommandLine.Parameters(index = "3")
            String str2;
        }
        CommandLine.populateCommand(new SkipTwo(), "val0", "val1", "val2", "val3");
    }

    @Test
    public void testPositionalParamWithIndexGap_VariableRangeIndexNoGap() throws Exception {
        class NoGap {
            @CommandLine.Parameters(index = "0..*")
            String[] str0;

            @CommandLine.Parameters(index = "3")
            String str2;
        }
        NoGap noGap = CommandLine.populateCommand(new NoGap(), "val0", "val1", "val2", "val3");
        Assert.assertArrayEquals(new String[]{ "val0", "val1", "val2", "val3" }, noGap.str0);
        Assert.assertEquals("val3", noGap.str2);
    }

    @Test
    public void testPositionalParamWithIndexGap_RangeIndexNoGap() throws Exception {
        class NoGap {
            @CommandLine.Parameters(index = "0..1")
            String[] str0;

            @CommandLine.Parameters(index = "2")
            String str2;
        }
        NoGap noGap = CommandLine.populateCommand(new NoGap(), "val0", "val1", "val2");
        Assert.assertArrayEquals(new String[]{ "val0", "val1" }, noGap.str0);
        Assert.assertEquals("val2", noGap.str2);
    }

    @Test(expected = CommandLine.UnmatchedArgumentException.class)
    public void testPositionalParamsDisallowUnknownArgumentSingleValue() throws Exception {
        class SingleValue {
            @CommandLine.Parameters(index = "0")
            String str;
        }
        SingleValue single = CommandLine.populateCommand(new SingleValue(), "val1", "val2");
        Assert.assertEquals("val1", single.str);
    }

    @Test(expected = CommandLine.UnmatchedArgumentException.class)
    public void testPositionalParamsDisallowUnknownArgumentMultiValue() throws Exception {
        class SingleValue {
            @CommandLine.Parameters(index = "0..2")
            String[] str;
        }
        CommandLine.populateCommand(new SingleValue(), "val0", "val1", "val2", "val3");
    }

    @Test
    public void testPositionalParamsUnknownArgumentSingleValueWithUnmatchedArgsAllowed() throws Exception {
        class SingleValue {
            @CommandLine.Parameters(index = "0")
            String str;
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new SingleValue()).setUnmatchedArgumentsAllowed(true);
        cmd.parse("val1", "val2");
        Assert.assertEquals("val1", ((SingleValue) (cmd.getCommand())).str);
        Assert.assertEquals(Arrays.asList("val2"), cmd.getUnmatchedArguments());
    }

    @Test
    public void testPositionalParamsUnknownArgumentMultiValueWithUnmatchedArgsAllowed() throws Exception {
        class SingleValue {
            @CommandLine.Parameters(index = "0..2")
            String[] str;
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new SingleValue()).setUnmatchedArgumentsAllowed(true);
        cmd.parse("val0", "val1", "val2", "val3");
        Assert.assertArrayEquals(new String[]{ "val0", "val1", "val2" }, ((SingleValue) (cmd.getCommand())).str);
        Assert.assertEquals(Arrays.asList("val3"), cmd.getUnmatchedArguments());
    }

    @Test
    public void testUnmatchedArgsInitiallyEmpty() throws Exception {
        class SingleValue {
            @CommandLine.Parameters(index = "0..2")
            String[] str;
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new SingleValue());
        Assert.assertTrue(cmd.getUnmatchedArguments().isEmpty());
        CommandLine cmd2 = new CommandLine(new SingleValue()).setUnmatchedArgumentsAllowed(true);
        Assert.assertTrue(cmd2.getUnmatchedArguments().isEmpty());
    }

    @Test
    public void testPositionalParamSingleValueButWithoutIndex() throws Exception {
        class SingleValue {
            @CommandLine.Parameters
            String str;
        }
        try {
            CommandLine.populateCommand(new SingleValue(), "val1", "val2");
            Assert.fail("Expected OverwrittenOptionException");
        } catch (CommandLine.OverwrittenOptionException ex) {
            Assert.assertEquals("positional parameter at index 0..* (<str>) should be specified only once", ex.getMessage());
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new SingleValue()).setOverwrittenOptionsAllowed(true);
        cmd.parse("val1", "val2");
        Assert.assertEquals("val2", ((SingleValue) (cmd.getCommand())).str);
    }

    @Test
    public void testParseSubCommands() {
        CommandLine commandLine = Demo.mainCommand();
        List<CommandLine> parsed = commandLine.parse("--git-dir=/home/rpopma/picocli status -sbuno".split(" "));
        Assert.assertEquals("command count", 2, parsed.size());
        Assert.assertEquals(Demo.Git.class, parsed.get(0).getCommand().getClass());
        Assert.assertEquals(Demo.GitStatus.class, parsed.get(1).getCommand().getClass());
        Demo.Git git = ((Demo.Git) (parsed.get(0).getCommand()));
        Assert.assertEquals(new File("/home/rpopma/picocli"), git.gitDir);
        Demo.GitStatus status = ((Demo.GitStatus) (parsed.get(1).getCommand()));
        Assert.assertTrue("status -s", status.shortFormat);
        Assert.assertTrue("status -b", status.branchInfo);
        Assert.assertFalse("NOT status --showIgnored", status.showIgnored);
        Assert.assertEquals("status -u=no", no, status.mode);
    }

    @Test
    public void testTracingInfoWithSubCommands() throws Exception {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2500);
        System.setErr(new PrintStream(baos));
        final String PROPERTY = "picocli.trace";
        String old = System.getProperty(PROPERTY);
        System.setProperty(PROPERTY, "");
        CommandLine commandLine = Demo.mainCommand();
        commandLine.parse("--git-dir=/home/rpopma/picocli", "commit", "-m", "\"Fixed typos\"", "--", "src1.java", "src2.java", "src3.java");
        System.setErr(originalErr);
        if (old == null) {
            System.clearProperty(PROPERTY);
        } else {
            System.setProperty(PROPERTY, old);
        }
        String expected = String.format(("" + ((((((("[picocli INFO] Picocli version: %s%n" + "[picocli INFO] Parsing 8 command line args [--git-dir=/home/rpopma/picocli, commit, -m, \"Fixed typos\", --, src1.java, src2.java, src3.java]%n") + "[picocli INFO] Setting field java.io.File picocli.Demo$Git.gitDir to '%s' (was 'null') for option --git-dir%n") + "[picocli INFO] Adding [\"Fixed typos\"] to field java.util.List<String> picocli.Demo$GitCommit.message for option -m%n") + "[picocli INFO] Found end-of-options delimiter '--'. Treating remainder as positional parameters.%n") + "[picocli INFO] Adding [src1.java] to field java.util.List<java.io.File> picocli.Demo$GitCommit.files for args[0..*] at position 0%n") + "[picocli INFO] Adding [src2.java] to field java.util.List<java.io.File> picocli.Demo$GitCommit.files for args[0..*] at position 1%n") + "[picocli INFO] Adding [src3.java] to field java.util.List<java.io.File> picocli.Demo$GitCommit.files for args[0..*] at position 2%n")), CommandLine.versionString(), new File("/home/rpopma/picocli"));
        String actual = new String(baos.toByteArray(), "UTF8");
        // System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTracingDebugWithSubCommands() throws Exception {
        CommandLineTest.clearBuiltInTracingCache();
        PrintStream originalErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2500);
        System.setErr(new PrintStream(baos));
        final String PROPERTY = "picocli.trace";
        String old = System.getProperty(PROPERTY);
        System.setProperty(PROPERTY, "DEBUG");
        CommandLine commandLine = Demo.mainCommand();
        commandLine.parse("--git-dir=/home/rpopma/picocli", "commit", "-m", "\"Fixed typos\"", "--", "src1.java", "src2.java", "src3.java");
        System.setErr(originalErr);
        if (old == null) {
            System.clearProperty(PROPERTY);
        } else {
            System.setProperty(PROPERTY, old);
        }
        String prefix8 = String.format(("" + ((((((((((((("[picocli DEBUG] Could not register converter for java.time.Duration: java.lang.ClassNotFoundException: java.time.Duration%n" + "[picocli DEBUG] Could not register converter for java.time.Instant: java.lang.ClassNotFoundException: java.time.Instant%n") + "[picocli DEBUG] Could not register converter for java.time.LocalDate: java.lang.ClassNotFoundException: java.time.LocalDate%n") + "[picocli DEBUG] Could not register converter for java.time.LocalDateTime: java.lang.ClassNotFoundException: java.time.LocalDateTime%n") + "[picocli DEBUG] Could not register converter for java.time.LocalTime: java.lang.ClassNotFoundException: java.time.LocalTime%n") + "[picocli DEBUG] Could not register converter for java.time.MonthDay: java.lang.ClassNotFoundException: java.time.MonthDay%n") + "[picocli DEBUG] Could not register converter for java.time.OffsetDateTime: java.lang.ClassNotFoundException: java.time.OffsetDateTime%n") + "[picocli DEBUG] Could not register converter for java.time.OffsetTime: java.lang.ClassNotFoundException: java.time.OffsetTime%n") + "[picocli DEBUG] Could not register converter for java.time.Period: java.lang.ClassNotFoundException: java.time.Period%n") + "[picocli DEBUG] Could not register converter for java.time.Year: java.lang.ClassNotFoundException: java.time.Year%n") + "[picocli DEBUG] Could not register converter for java.time.YearMonth: java.lang.ClassNotFoundException: java.time.YearMonth%n") + "[picocli DEBUG] Could not register converter for java.time.ZonedDateTime: java.lang.ClassNotFoundException: java.time.ZonedDateTime%n") + "[picocli DEBUG] Could not register converter for java.time.ZoneId: java.lang.ClassNotFoundException: java.time.ZoneId%n") + "[picocli DEBUG] Could not register converter for java.time.ZoneOffset: java.lang.ClassNotFoundException: java.time.ZoneOffset%n")));
        String prefix7 = String.format(("" + "[picocli DEBUG] Could not register converter for java.nio.file.Path: java.lang.ClassNotFoundException: java.nio.file.Path%n"));
        String expected = String.format(("" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$Git with factory picocli.CommandLine$DefaultFactory%n" + "[picocli DEBUG] Creating CommandSpec for object of class picocli.CommandLine$AutoHelpMixin with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.CommandLine$HelpCommand with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'help' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitStatus with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'status' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitCommit with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'commit' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitAdd with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'add' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitBranch with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'branch' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitCheckout with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'checkout' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitClone with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'clone' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitDiff with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'diff' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitMerge with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'merge' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitPush with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'push' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitRebase with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'rebase' to 'git'%n") + "[picocli DEBUG] Creating CommandSpec for object of class picocli.Demo$GitTag with factory picocli.CommandLine$DefaultFactory%n") + "[picocli DEBUG] Adding subcommand 'tag' to 'git'%n") + "[picocli INFO] Picocli version: %3$s%n") + "[picocli INFO] Parsing 8 command line args [--git-dir=/home/rpopma/picocli, commit, -m, \"Fixed typos\", --, src1.java, src2.java, src3.java]%n") + "[picocli DEBUG] Parser configuration: posixClusteredShortOptionsAllowed=true, stopAtPositional=false, stopAtUnmatched=false, separator=null, overwrittenOptionsAllowed=false, unmatchedArgumentsAllowed=false, expandAtFiles=true, atFileCommentChar=#, useSimplifiedAtFiles=false, endOfOptionsDelimiter=--, limitSplit=false, aritySatisfiedByAttachedOptionParam=false, toggleBooleanFlags=true, unmatchedOptionsArePositionalParams=false, collectErrors=false,caseInsensitiveEnumValuesAllowed=false, trimQuotes=false, splitQuotedStrings=false%n") + "[picocli DEBUG] (ANSI is disabled by default: isatty=...)%n") + "[picocli DEBUG] Set initial value for field java.io.File picocli.Demo$Git.gitDir of type class java.io.File to null.%n") + "[picocli DEBUG] Set initial value for field boolean picocli.CommandLine$AutoHelpMixin.helpRequested of type boolean to false.%n") + "[picocli DEBUG] Set initial value for field boolean picocli.CommandLine$AutoHelpMixin.versionRequested of type boolean to false.%n") + "[picocli DEBUG] Initializing %1$s$Git: 3 options, 0 positional parameters, 0 required, 12 subcommands.%n") + "[picocli DEBUG] Processing argument \'--git-dir=/home/rpopma/picocli\'. Remainder=[commit, -m, \"Fixed typos\", --, src1.java, src2.java, src3.java]%n") + "[picocli DEBUG] Separated '--git-dir' option from '/home/rpopma/picocli' option parameter%n") + "[picocli DEBUG] Found option named '--git-dir': field java.io.File %1$s$Git.gitDir, arity=1%n") + "[picocli INFO] Setting field java.io.File picocli.Demo$Git.gitDir to '%2$s' (was 'null') for option --git-dir%n") + "[picocli DEBUG] Processing argument \'commit\'. Remainder=[-m, \"Fixed typos\", --, src1.java, src2.java, src3.java]%n") + "[picocli DEBUG] Found subcommand 'commit' (%1$s$GitCommit)%n") + "[picocli DEBUG] Set initial value for field boolean picocli.Demo$GitCommit.all of type boolean to false.%n") + "[picocli DEBUG] Set initial value for field boolean picocli.Demo$GitCommit.patch of type boolean to false.%n") + "[picocli DEBUG] Set initial value for field String picocli.Demo$GitCommit.reuseMessageCommit of type class java.lang.String to null.%n") + "[picocli DEBUG] Set initial value for field String picocli.Demo$GitCommit.reEditMessageCommit of type class java.lang.String to null.%n") + "[picocli DEBUG] Set initial value for field String picocli.Demo$GitCommit.fixupCommit of type class java.lang.String to null.%n") + "[picocli DEBUG] Set initial value for field String picocli.Demo$GitCommit.squashCommit of type class java.lang.String to null.%n") + "[picocli DEBUG] Set initial value for field java.io.File picocli.Demo$GitCommit.file of type class java.io.File to null.%n") + "[picocli DEBUG] Set initial value for field java.util.List<String> picocli.Demo$GitCommit.message of type interface java.util.List to [].%n") + "[picocli DEBUG] Set initial value for field java.util.List<java.io.File> picocli.Demo$GitCommit.files of type interface java.util.List to [].%n") + "[picocli DEBUG] Initializing %1$s$GitCommit: 8 options, 1 positional parameters, 0 required, 0 subcommands.%n") + "[picocli DEBUG] Processing argument \'-m\'. Remainder=[\"Fixed typos\", --, src1.java, src2.java, src3.java]%n") + "[picocli DEBUG] '-m' cannot be separated into <option>=<option-parameter>%n") + "[picocli DEBUG] Found option named '-m': field java.util.List<String> %1$s$GitCommit.message, arity=1%n") + "[picocli INFO] Adding [\"Fixed typos\"] to field java.util.List<String> picocli.Demo$GitCommit.message for option -m%n") + "[picocli DEBUG] Processing argument '--'. Remainder=[src1.java, src2.java, src3.java]%n") + "[picocli INFO] Found end-of-options delimiter '--'. Treating remainder as positional parameters.%n") + "[picocli DEBUG] Processing next arg as a positional parameter at index=0. Remainder=[src1.java, src2.java, src3.java]%n") + "[picocli DEBUG] Position 0 is in index range 0..*. Trying to assign args to field java.util.List<java.io.File> %1$s$GitCommit.files, arity=0..1%n") + "[picocli INFO] Adding [src1.java] to field java.util.List<java.io.File> picocli.Demo$GitCommit.files for args[0..*] at position 0%n") + "[picocli DEBUG] Consumed 1 arguments and 0 interactive values, moving position to index 1.%n") + "[picocli DEBUG] Processing next arg as a positional parameter at index=1. Remainder=[src2.java, src3.java]%n") + "[picocli DEBUG] Position 1 is in index range 0..*. Trying to assign args to field java.util.List<java.io.File> %1$s$GitCommit.files, arity=0..1%n") + "[picocli INFO] Adding [src2.java] to field java.util.List<java.io.File> picocli.Demo$GitCommit.files for args[0..*] at position 1%n") + "[picocli DEBUG] Consumed 1 arguments and 0 interactive values, moving position to index 2.%n") + "[picocli DEBUG] Processing next arg as a positional parameter at index=2. Remainder=[src3.java]%n") + "[picocli DEBUG] Position 2 is in index range 0..*. Trying to assign args to field java.util.List<java.io.File> %1$s$GitCommit.files, arity=0..1%n") + "[picocli INFO] Adding [src3.java] to field java.util.List<java.io.File> picocli.Demo$GitCommit.files for args[0..*] at position 2%n") + "[picocli DEBUG] Consumed 1 arguments and 0 interactive values, moving position to index 3.%n")), Demo.class.getName(), new File("/home/rpopma/picocli"), CommandLine.versionString());
        String actual = new String(baos.toByteArray(), "UTF8");
        // System.out.println(actual);
        if ((System.getProperty("java.version").compareTo("1.7.0")) < 0) {
            expected = prefix7 + expected;
        }
        if ((System.getProperty("java.version").compareTo("1.8.0")) < 0) {
            expected = prefix8 + expected;
        }
        Assert.assertEquals(PicocliTestUtil.stripAnsiTrace(expected), PicocliTestUtil.stripAnsiTrace(actual));
    }

    @Test
    public void testTraceWarningIfOptionOverwrittenWhenOverwrittenOptionsAllowed() throws Exception {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2500);
        System.setErr(new PrintStream(baos));
        HelpTestUtil.setTraceLevel("INFO");
        class App {
            @CommandLine.Option(names = "-f")
            String field = null;

            @CommandLine.Option(names = "-p")
            int primitive = 43;
        }
        CommandLine cmd = new CommandLine(new App()).setOverwrittenOptionsAllowed(true);
        cmd.parse("-f", "111", "-f", "222", "-f", "333");
        App ff = cmd.getCommand();
        Assert.assertEquals("333", ff.field);
        System.setErr(originalErr);
        String expected = String.format(("" + (((("[picocli INFO] Picocli version: %s%n" + "[picocli INFO] Parsing 6 command line args [-f, 111, -f, 222, -f, 333]%n") + "[picocli INFO] Setting field String %2$s.field to '111' (was 'null') for option -f%n") + "[picocli INFO] Overwriting field String %2$s.field value '111' with '222' for option -f%n") + "[picocli INFO] Overwriting field String %2$s.field value '222' with '333' for option -f%n")), CommandLine.versionString(), App.class.getName());
        String actual = new String(baos.toByteArray(), "UTF8");
        Assert.assertEquals(expected, actual);
        HelpTestUtil.setTraceLevel("WARN");
    }

    @Test
    public void testTraceWarningIfUnmatchedArgsWhenUnmatchedArgumentsAllowed() throws Exception {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2500);
        System.setErr(new PrintStream(baos));
        HelpTestUtil.setTraceLevel("INFO");
        class App {
            @CommandLine.Parameters(index = "0", arity = "2", split = "\\|", type = { Integer.class, String.class })
            Map<Integer, String> message;
        }
        CommandLine cmd = new CommandLine(new App()).setUnmatchedArgumentsAllowed(true).parse("1=a", "2=b", "3=c", "4=d").get(0);
        Assert.assertEquals(Arrays.asList("3=c", "4=d"), cmd.getUnmatchedArguments());
        System.setErr(originalErr);
        String expected = String.format(("" + (((("[picocli INFO] Picocli version: %s%n" + "[picocli INFO] Parsing 4 command line args [1=a, 2=b, 3=c, 4=d]%n") + "[picocli INFO] Putting [1 : a] in LinkedHashMap<Integer, String> field java.util.Map<Integer, String> %s.message for args[0] at position 0%n") + "[picocli INFO] Putting [2 : b] in LinkedHashMap<Integer, String> field java.util.Map<Integer, String> %s.message for args[0] at position 0%n") + "[picocli INFO] Unmatched arguments: [3=c, 4=d]%n")), CommandLine.versionString(), App.class.getName(), App.class.getName());
        String actual = new String(baos.toByteArray(), "UTF8");
        // System.out.println(actual);
        Assert.assertEquals(expected, actual);
        HelpTestUtil.setTraceLevel("WARN");
    }

    @Test
    public void testCommandListReturnsRegisteredCommands() {
        @CommandLine.Command
        class MainCommand {}
        @CommandLine.Command
        class Command1 {}
        @CommandLine.Command
        class Command2 {}
        CommandLine commandLine = new CommandLine(new MainCommand());
        commandLine.addSubcommand("cmd1", new Command1()).addSubcommand("cmd2", new Command2());
        Map<String, CommandLine> commandMap = commandLine.getSubcommands();
        Assert.assertEquals(2, commandMap.size());
        Assert.assertTrue("cmd1", ((commandMap.get("cmd1").getCommand()) instanceof Command1));
        Assert.assertTrue("cmd2", ((commandMap.get("cmd2").getCommand()) instanceof Command2));
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testPopulateCommandRequiresAnnotatedCommand() {
        class App {}
        CommandLine.populateCommand(new App());
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testUsageObjectPrintstreamRequiresAnnotatedCommand() {
        class App {}
        CommandLine.usage(new App(), System.out);
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testUsageObjectPrintstreamAnsiRequiresAnnotatedCommand() {
        class App {}
        CommandLine.usage(new App(), System.out, OFF);
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testUsageObjectPrintstreamColorschemeRequiresAnnotatedCommand() {
        class App {}
        CommandLine.usage(new App(), System.out, CommandLine.Help.defaultColorScheme(OFF));
    }

    @Test(expected = CommandLine.InitializationException.class)
    public void testConstructorRequiresAnnotatedCommand() {
        class App {}
        new CommandLine(new App());
    }

    @Test
    public void testOverwrittenOptionDisallowedByDefault() {
        class App {
            @CommandLine.Option(names = "-s")
            String string;

            @CommandLine.Option(names = "-v")
            boolean bool;
        }
        try {
            CommandLine.populateCommand(new App(), "-s", "1", "-s", "2");
            Assert.fail("expected exception");
        } catch (CommandLine.OverwrittenOptionException ex) {
            Assert.assertEquals("option '-s' (<string>) should be specified only once", ex.getMessage());
        }
        try {
            CommandLine.populateCommand(new App(), "-v", "-v");
            Assert.fail("expected exception");
        } catch (CommandLine.OverwrittenOptionException ex) {
            Assert.assertEquals("option '-v' (<bool>) should be specified only once", ex.getMessage());
        }
    }

    @Test
    public void testOverwrittenOptionDisallowedByDefaultRegardlessOfAlias() {
        class App {
            @CommandLine.Option(names = { "-s", "--str" })
            String string;

            @CommandLine.Option(names = { "-v", "--verbose" })
            boolean bool;
        }
        try {
            CommandLine.populateCommand(new App(), "-s", "1", "--str", "2");
            Assert.fail("expected exception");
        } catch (CommandLine.OverwrittenOptionException ex) {
            Assert.assertEquals("option '--str' (<string>) should be specified only once", ex.getMessage());
        }
        try {
            CommandLine.populateCommand(new App(), "-v", "--verbose");
            Assert.fail("expected exception");
        } catch (CommandLine.OverwrittenOptionException ex) {
            Assert.assertEquals("option '--verbose' (<bool>) should be specified only once", ex.getMessage());
        }
    }

    @Test
    public void testOverwrittenOptionExceptionContainsCorrectArgSpec() {
        class App {
            @CommandLine.Option(names = "-s")
            String string;

            @CommandLine.Option(names = "-v")
            boolean bool;
        }
        try {
            CommandLine.populateCommand(new App(), "-s", "1", "-s", "2");
            Assert.fail("expected exception");
        } catch (CommandLine.OverwrittenOptionException ex) {
            Assert.assertEquals(ex.getCommandLine().getCommandSpec().optionsMap().get("-s"), ex.getOverwritten());
        }
        try {
            CommandLine.populateCommand(new App(), "-v", "-v");
            Assert.fail("expected exception");
        } catch (CommandLine.OverwrittenOptionException ex) {
            Assert.assertEquals(ex.getCommandLine().getCommandSpec().optionsMap().get("-v"), ex.getOverwritten());
        }
    }

    @Test
    public void testOverwrittenOptionSetsLastValueIfAllowed() {
        class App {
            @CommandLine.Option(names = { "-s", "--str" })
            String string;

            @CommandLine.Option(names = { "-v", "--verbose" })
            boolean bool;
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine commandLine = new CommandLine(new App()).setOverwrittenOptionsAllowed(true);
        commandLine.parse("-s", "1", "--str", "2");
        Assert.assertEquals("2", ((App) (commandLine.getCommand())).string);
        commandLine = new CommandLine(new App()).setOverwrittenOptionsAllowed(true);
        commandLine.parse("-v", "--verbose", "-v");// F -> T -> F -> T

        Assert.assertEquals(true, ((App) (commandLine.getCommand())).bool);
    }

    @Test
    public void testOverwrittenOptionAppliesToRegisteredSubcommands() {
        @CommandLine.Command(name = "parent")
        class Parent {
            public Parent() {
            }

            @CommandLine.Option(names = "--parent")
            String parentString;
        }
        @CommandLine.Command
        class App {
            @CommandLine.Option(names = { "-s", "--str" })
            String string;
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine commandLine = new CommandLine(new App()).addSubcommand("parent", new Parent()).setOverwrittenOptionsAllowed(true);
        commandLine.parse("-s", "1", "--str", "2", "parent", "--parent", "parentVal", "--parent", "2ndVal");
        App app = commandLine.getCommand();
        Assert.assertEquals("2", app.string);
        Parent parent = commandLine.getSubcommands().get("parent").getCommand();
        Assert.assertEquals("2ndVal", parent.parentString);
    }

    @Test
    public void testIssue141Npe() {
        class A {
            @CommandLine.Option(names = { "-u", "--user" }, required = true, description = "user id")
            private String user;

            @CommandLine.Option(names = { "-p", "--password" }, required = true, description = "password")
            private String password;
        }
        A a = new A();
        CommandLine commandLine = new CommandLine(a);
        try {
            commandLine.parse("-u", "foo");
            Assert.fail("expected exception");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required option '--password=<password>'", ex.getLocalizedMessage());
        }
        commandLine.parse("-u", "foo", "-p", "abc");
    }

    @Test
    public void testToggleBooleanValue() {
        class App {
            @CommandLine.Option(names = "-a")
            boolean primitiveFalse = false;

            @CommandLine.Option(names = "-b")
            boolean primitiveTrue = true;

            @CommandLine.Option(names = "-c")
            Boolean objectFalse = false;

            @CommandLine.Option(names = "-d")
            Boolean objectTrue = true;

            @CommandLine.Option(names = "-e")
            Boolean objectNull = null;
        }
        App app = CommandLine.populateCommand(new App(), "-a -b -c -d -e".split(" "));
        Assert.assertTrue(app.primitiveFalse);
        Assert.assertFalse(app.primitiveTrue);
        Assert.assertTrue(app.objectFalse);
        Assert.assertFalse(app.objectTrue);
        Assert.assertTrue(app.objectNull);
    }

    @Test(timeout = 15000)
    public void testIssue148InfiniteLoop() throws Exception {
        // Default value needs to be at least 1 character larger than the "WRAP" column in TextTable(Ansi), which is
        // currently 51 characters. Going with 81 to be safe.
        @CommandLine.Command(showDefaultValues = true)
        class App {
            @CommandLine.Option(names = "--foo-bar-baz")
            String foo = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);
        CommandLine.usage(new App(), printStream);
        String content = new String(output.toByteArray(), "UTF-8").replaceAll("\r\n", "\n");// Normalize line endings.

        String expectedOutput = "Usage: <main class> [--foo-bar-baz=<foo>]\n" + (("      --foo-bar-baz=<foo>     Default:\n" + "                              aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n") + "                              aaaaaaaaaaaaaaaaaaaaaaaaaa\n");
        Assert.assertEquals(expectedOutput, content);
    }

    @Test
    public void testMapFieldHappyCase() {
        class App {
            @CommandLine.Option(names = { "-P", "-map" }, type = { String.class, String.class })
            Map<String, String> map = new HashMap<String, String>();

            private void validateMapField() {
                Assert.assertEquals(1, map.size());
                Assert.assertEquals(HashMap.class, map.getClass());
                Assert.assertEquals("BBB", map.get("AAA"));
            }
        }
        CommandLine.populateCommand(new App(), "-map", "AAA=BBB").validateMapField();
        CommandLine.populateCommand(new App(), "-map=AAA=BBB").validateMapField();
        CommandLine.populateCommand(new App(), "-P=AAA=BBB").validateMapField();
        CommandLine.populateCommand(new App(), "-PAAA=BBB").validateMapField();
        CommandLine.populateCommand(new App(), "-P", "AAA=BBB").validateMapField();
    }

    @Test
    public void testMapFieldHappyCaseWithMultipleValues() {
        class App {
            @CommandLine.Option(names = { "-P", "-map" }, split = ",", type = { String.class, String.class })
            Map<String, String> map;

            private void validateMapField3Values() {
                Assert.assertEquals(3, map.size());
                Assert.assertEquals(LinkedHashMap.class, map.getClass());
                Assert.assertEquals("BBB", map.get("AAA"));
                Assert.assertEquals("DDD", map.get("CCC"));
                Assert.assertEquals("FFF", map.get("EEE"));
            }
        }
        CommandLine.populateCommand(new App(), "-map=AAA=BBB,CCC=DDD,EEE=FFF").validateMapField3Values();
        CommandLine.populateCommand(new App(), "-PAAA=BBB,CCC=DDD,EEE=FFF").validateMapField3Values();
        CommandLine.populateCommand(new App(), "-P", "AAA=BBB,CCC=DDD,EEE=FFF").validateMapField3Values();
        CommandLine.populateCommand(new App(), "-map=AAA=BBB", "-map=CCC=DDD", "-map=EEE=FFF").validateMapField3Values();
        CommandLine.populateCommand(new App(), "-PAAA=BBB", "-PCCC=DDD", "-PEEE=FFF").validateMapField3Values();
        CommandLine.populateCommand(new App(), "-P", "AAA=BBB", "-P", "CCC=DDD", "-P", "EEE=FFF").validateMapField3Values();
        try {
            CommandLine.populateCommand(new App(), "-P", "AAA=BBB", "CCC=DDD", "EEE=FFF").validateMapField3Values();
            Assert.fail("Expected UnmatchedArgEx");
        } catch (CommandLine.UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched arguments: CCC=DDD, EEE=FFF", ok.getMessage());
        }
        try {
            CommandLine.populateCommand(new App(), "-map=AAA=BBB", "CCC=DDD", "EEE=FFF").validateMapField3Values();
            Assert.fail("Expected UnmatchedArgEx");
        } catch (CommandLine.UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched arguments: CCC=DDD, EEE=FFF", ok.getMessage());
        }
        try {
            CommandLine.populateCommand(new App(), "-PAAA=BBB", "-PCCC=DDD", "EEE=FFF").validateMapField3Values();
            Assert.fail("Expected UnmatchedArgEx");
        } catch (CommandLine.UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched argument: EEE=FFF", ok.getMessage());
        }
        try {
            CommandLine.populateCommand(new App(), "-P", "AAA=BBB", "-P", "CCC=DDD", "EEE=FFF").validateMapField3Values();
            Assert.fail("Expected UnmatchedArgEx");
        } catch (CommandLine.UnmatchedArgumentException ok) {
            Assert.assertEquals("Unmatched argument: EEE=FFF", ok.getMessage());
        }
    }

    @Test
    public void testMapField_InstantiatesConcreteMap() {
        class App {
            @CommandLine.Option(names = "-map", type = { String.class, String.class })
            TreeMap<String, String> map;
        }
        App app = CommandLine.populateCommand(new App(), "-map=AAA=BBB");
        Assert.assertEquals(1, app.map.size());
        Assert.assertEquals(TreeMap.class, app.map.getClass());
        Assert.assertEquals("BBB", app.map.get("AAA"));
    }

    @Test
    public void testMapFieldMissingTypeAttribute() {
        class App {
            @CommandLine.Option(names = "-map")
            TreeMap<String, String> map;
        }
        try {
            CommandLine.populateCommand(new App(), "-map=AAA=BBB");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals((("Field java.util.TreeMap " + (App.class.getName())) + ".map needs two types (one for the map key, one for the value) but only has 1 types configured."), ex.getMessage());
        }
    }

    @Test
    public void testMapFieldMissingTypeConverter() {
        class App {
            @CommandLine.Option(names = "-map", type = { Thread.class, Thread.class })
            TreeMap<String, String> map;
        }
        try {
            CommandLine.populateCommand(new App(), "-map=AAA=BBB");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals((("No TypeConverter registered for java.lang.Thread of field java.util.TreeMap<String, String> " + (App.class.getName())) + ".map"), ex.getMessage());
        }
    }

    @Test
    public void testMapPositionalParameterFieldMaxArity() {
        class App {
            @CommandLine.Parameters(index = "0", arity = "2", type = { Integer.class, String.class })
            Map<Integer, String> message;
        }
        try {
            CommandLine.populateCommand(new App(), "1=a", "2=b", "3=c", "4=d");
            Assert.fail("UnmatchedArgumentsException expected");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched arguments: 3=c, 4=d", ex.getMessage());
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new App()).setUnmatchedArgumentsAllowed(true);
        cmd.parse("1=a", "2=b", "3=c", "4=d");
        Assert.assertEquals(Arrays.asList("3=c", "4=d"), cmd.getUnmatchedArguments());
    }

    @Test
    public void testMapPositionalParameterFieldArity3() {
        class App {
            @CommandLine.Parameters(index = "0", arity = "3", type = { Integer.class, String.class })
            Map<Integer, String> message;
        }
        try {
            CommandLine.populateCommand(new App(), "1=a", "2=b", "3=c", "4=d");
            Assert.fail("UnmatchedArgumentsException expected");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unmatched argument: 4=d", ex.getMessage());
        }
        HelpTestUtil.setTraceLevel("OFF");
        CommandLine cmd = new CommandLine(new App()).setUnmatchedArgumentsAllowed(true);
        cmd.parse("1=a", "2=b", "3=c", "4=d");
        Assert.assertEquals(Arrays.asList("4=d"), cmd.getUnmatchedArguments());
    }

    @Test
    public void testMapAndCollectionFieldTypeInference() {
        class App {
            @CommandLine.Option(names = "-a")
            Map<Integer, URI> a;

            @CommandLine.Option(names = "-b")
            Map<TimeUnit, StringBuilder> b;

            @SuppressWarnings("unchecked")
            @CommandLine.Option(names = "-c")
            Map c;

            @CommandLine.Option(names = "-d")
            List<File> d;

            @CommandLine.Option(names = "-e")
            Map<? extends Integer, ? super Long> e;

            @CommandLine.Option(names = "-f", type = { Long.class, Float.class })
            Map<? extends Number, ? super Number> f;

            @SuppressWarnings("unchecked")
            @CommandLine.Option(names = "-g", type = { TimeUnit.class, Float.class })
            Map g;
        }
        App app = CommandLine.populateCommand(new App(), "-a", "8=/path", "-a", "98765432=/path/to/resource", "-b", "SECONDS=abc", "-c", "123=ABC", "-d", "/path/to/file", "-e", "12345=67890", "-f", "12345=67.89", "-g", "MILLISECONDS=12.34");
        Assert.assertEquals(app.a.size(), 2);
        Assert.assertEquals(URI.create("/path"), app.a.get(8));
        Assert.assertEquals(URI.create("/path/to/resource"), app.a.get(98765432));
        Assert.assertEquals(app.b.size(), 1);
        Assert.assertEquals(new StringBuilder("abc").toString(), app.b.get(TimeUnit.SECONDS).toString());
        Assert.assertEquals(app.c.size(), 1);
        Assert.assertEquals("ABC", app.c.get("123"));
        Assert.assertEquals(app.d.size(), 1);
        Assert.assertEquals(new File("/path/to/file"), app.d.get(0));
        Assert.assertEquals(app.e.size(), 1);
        Assert.assertEquals(new Long(67890), app.e.get(12345));
        Assert.assertEquals(app.f.size(), 1);
        Assert.assertEquals(67.89F, app.f.get(new Long(12345)));
        Assert.assertEquals(app.g.size(), 1);
        Assert.assertEquals(12.34F, app.g.get(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testUseTypeAttributeInsteadOfFieldType() {
        class App {
            // subclass of field type
            @CommandLine.Option(names = "--num", type = BigDecimal.class)
            Number[] number;// array type with abstract component class


            // concrete impl class
            @CommandLine.Parameters(type = StringBuilder.class)
            Appendable address;// type declared as interface

        }
        App app = CommandLine.populateCommand(new App(), "--num", "123.456", "ABC");
        Assert.assertEquals(1, app.number.length);
        Assert.assertEquals(new BigDecimal("123.456"), app.number[0]);
        Assert.assertEquals("ABC", app.address.toString());
        Assert.assertTrue(((app.address) instanceof StringBuilder));
    }

    @Test
    public void testMultipleMissingOptions() {
        class App {
            @CommandLine.Option(names = "-a", required = true)
            String first;

            @CommandLine.Option(names = "-b", required = true)
            String second;

            @CommandLine.Option(names = "-c", required = true)
            String third;
        }
        try {
            CommandLine.populateCommand(new App());
            Assert.fail("MissingParameterException expected");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required options [-a=<first>, -b=<second>, -c=<third>]", ex.getMessage());
        }
    }

    @Test
    public void test185MissingOptionsShouldUseLabel() {
        class App {
            @CommandLine.Parameters(arity = "1", paramLabel = "IN_FILE", description = "The input file")
            File foo;

            @CommandLine.Option(names = "-o", paramLabel = "OUT_FILE", description = "The output file", required = true)
            File bar;
        }
        try {
            CommandLine.populateCommand(new App());
            Assert.fail("MissingParameterException expected");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required options [-o=OUT_FILE, params[0..*]=IN_FILE]", ex.getMessage());
        }
    }

    @Test
    public void test185MissingMapOptionsShouldUseLabel() {
        class App {
            @CommandLine.Parameters(arity = "1", type = { Long.class, File.class }, description = "The input file mapping")
            Map<Long, File> foo;

            @CommandLine.Option(names = "-o", description = "The output file mapping", required = true)
            Map<String, String> bar;

            @CommandLine.Option(names = "-x", paramLabel = "KEY=VAL", description = "Some other mapping", required = true)
            Map<String, String> xxx;
        }
        try {
            CommandLine.populateCommand(new App());
            Assert.fail("MissingParameterException expected");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required options [-o=<String=String>, -x=KEY=VAL, params[0..*]=<Long=File>]", ex.getMessage());
        }
    }

    @Test
    public void testAnyExceptionWrappedInParameterException() {
        class App {
            @CommandLine.Option(names = "-queue", type = String.class, split = ",")
            ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);
        }
        try {
            CommandLine.populateCommand(new App(), "-queue a,b,c".split(" "));
            Assert.fail("ParameterException expected");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals("IllegalStateException: Queue full while processing argument at or before arg[1] 'a,b,c' in [-queue, a,b,c]: java.lang.IllegalStateException: Queue full", ex.getMessage());
        }
    }

    @Test
    public void testUnmatchedExceptionStringConstructor() {
        CommandLine.UnmatchedArgumentException ex = new CommandLine.UnmatchedArgumentException(new CommandLine(CommandSpec.create()), "aa");
        Assert.assertNotNull(ex.getUnmatched());
        Assert.assertTrue(ex.getUnmatched().isEmpty());
        Assert.assertTrue(ex.getSuggestions().isEmpty());
    }

    @Test
    public void testUnmatchedExceptionListConstructor() {
        CommandLine.UnmatchedArgumentException ex = new CommandLine.UnmatchedArgumentException(new CommandLine(CommandSpec.create()), new ArrayList<String>());
        Assert.assertNotNull(ex.getUnmatched());
        Assert.assertTrue(ex.getUnmatched().isEmpty());
        Assert.assertTrue(ex.getSuggestions().isEmpty());
        ex = new CommandLine.UnmatchedArgumentException(new CommandLine(CommandSpec.create()), Arrays.asList("a", "b"));
        Assert.assertEquals(Arrays.asList("a", "b"), ex.getUnmatched());
    }

    @Test
    public void testUnmatchedExceptionStackConstructor() {
        CommandLine.UnmatchedArgumentException ex = new CommandLine.UnmatchedArgumentException(new CommandLine(CommandSpec.create()), new Stack<String>());
        Assert.assertNotNull(ex.getUnmatched());
        Assert.assertTrue(ex.getUnmatched().isEmpty());
        Assert.assertTrue(ex.getSuggestions().isEmpty());
        Stack<String> stack = new Stack<String>();
        stack.push("x");
        stack.push("y");
        stack.push("z");
        ex = new CommandLine.UnmatchedArgumentException(new CommandLine(CommandSpec.create()), stack);
        Assert.assertEquals(Arrays.asList("z", "y", "x"), ex.getUnmatched());
    }

    @Test
    public void testUnmatchedExceptionIsUnknownOption() {
        CommandLine cmd = new CommandLine(CommandSpec.create());
        Assert.assertFalse("unmatch list is null", isUnknownOption());
        Assert.assertFalse("unmatch list is empty", isUnknownOption());
        List<String> likeAnOption = Arrays.asList("-x");
        Assert.assertTrue("first unmatched resembles option", isUnknownOption());
        List<String> unlikeOption = Arrays.asList("xxx");
        Assert.assertFalse("first unmatched doesn't resembles option", isUnknownOption());
    }

    @Test
    public void testParameterExceptionDisallowsArgSpecAndValueBothNull() {
        CommandLine cmd = new CommandLine(CommandSpec.create());
        try {
            new CommandLine.ParameterException(cmd, "", null, null);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("ArgSpec and value cannot both be null", ex.getMessage());
        }
        try {
            new CommandLine.ParameterException(cmd, "", new Throwable(), null, null);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("ArgSpec and value cannot both be null", ex.getMessage());
        }
    }

    @Test
    public void test149UnmatchedShortOptionsAreMisinterpretedAsOperands() {
        class App {
            @CommandLine.Option(names = "-a")
            String first;

            @CommandLine.Option(names = "-b")
            String second;

            @CommandLine.Option(names = { "-c", "--ccc" })
            String third;

            @CommandLine.Parameters
            String[] positional;
        }
        try {
            CommandLine.populateCommand(new App(), "-xx", "-a", "aValue");
            Assert.fail("UnmatchedArgumentException expected for -xx");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: -xx", ex.getMessage());
            Assert.assertEquals(Arrays.asList("-xx"), ex.getUnmatched());
        }
        try {
            CommandLine.populateCommand(new App(), "-x", "-a", "aValue");
            Assert.fail("UnmatchedArgumentException expected for -x");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: -x", ex.getMessage());
            Assert.assertEquals(Arrays.asList("-x"), ex.getUnmatched());
        }
        try {
            CommandLine.populateCommand(new App(), "--x", "-a", "aValue");
            Assert.fail("UnmatchedArgumentException expected for --x");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: --x", ex.getMessage());
            Assert.assertEquals(Arrays.asList("--x"), ex.getUnmatched());
        }
    }

    @Test
    public void test149NonOptionArgsShouldBeTreatedAsOperands() {
        class App {
            @CommandLine.Option(names = "/a")
            String first;

            @CommandLine.Option(names = "/b")
            String second;

            @CommandLine.Option(names = { "/c", "--ccc" })
            String third;

            @CommandLine.Parameters
            String[] positional;
        }
        App app = CommandLine.populateCommand(new App(), "-yy", "-a");
        Assert.assertArrayEquals(new String[]{ "-yy", "-a" }, app.positional);
        app = CommandLine.populateCommand(new App(), "-y", "-a");
        Assert.assertArrayEquals(new String[]{ "-y", "-a" }, app.positional);
        app = CommandLine.populateCommand(new App(), "--y", "-a");
        Assert.assertArrayEquals(new String[]{ "--y", "-a" }, app.positional);
    }

    @Test
    public void test149LongMatchWeighsWhenDeterminingOptionResemblance() {
        class App {
            @CommandLine.Option(names = "/a")
            String first;

            @CommandLine.Option(names = "/b")
            String second;

            @CommandLine.Option(names = { "/c", "--ccc" })
            String third;

            @CommandLine.Parameters
            String[] positional;
        }
        try {
            CommandLine.populateCommand(new App(), "--ccd", "-a");
            Assert.fail("UnmatchedArgumentException expected for --x");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: --ccd", ex.getMessage());
        }
    }

    @Test
    public void test149OnlyUnmatchedOptionStoredOthersParsed() throws Exception {
        class App {
            @CommandLine.Option(names = "-a")
            String first;

            @CommandLine.Option(names = "-b")
            String second;

            @CommandLine.Option(names = { "-c", "--ccc" })
            String third;

            @CommandLine.Parameters
            String[] positional;
        }
        HelpTestUtil.setTraceLevel("INFO");
        PrintStream originalErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2500);
        System.setErr(new PrintStream(baos));
        CommandLine cmd = new CommandLine(new App()).setUnmatchedArgumentsAllowed(true).parse("-yy", "-a=A").get(0);
        Assert.assertEquals(Arrays.asList("-yy"), cmd.getUnmatchedArguments());
        Assert.assertEquals("A", ((App) (cmd.getCommand())).first);
        cmd = new CommandLine(new App()).setUnmatchedArgumentsAllowed(true).parse("-y", "-b=B").get(0);
        Assert.assertEquals(Arrays.asList("-y"), cmd.getUnmatchedArguments());
        Assert.assertEquals("B", ((App) (cmd.getCommand())).second);
        cmd = new CommandLine(new App()).setUnmatchedArgumentsAllowed(true).parse("--y", "-c=C").get(0);
        Assert.assertEquals(Arrays.asList("--y"), cmd.getUnmatchedArguments());
        Assert.assertEquals("C", ((App) (cmd.getCommand())).third);
        String expected = String.format(("" + ((((((((((("[picocli INFO] Picocli version: %2$s%n" + "[picocli INFO] Parsing 2 command line args [-yy, -a=A]%n") + "[picocli INFO] Setting field String %1$s.first to 'A' (was 'null') for option -a%n") + "[picocli INFO] Unmatched arguments: [-yy]%n") + "[picocli INFO] Picocli version: %2$s%n") + "[picocli INFO] Parsing 2 command line args [-y, -b=B]%n") + "[picocli INFO] Setting field String %1$s.second to 'B' (was 'null') for option -b%n") + "[picocli INFO] Unmatched arguments: [-y]%n") + "[picocli INFO] Picocli version: %2$s%n") + "[picocli INFO] Parsing 2 command line args [--y, -c=C]%n") + "[picocli INFO] Setting field String %1$s.third to 'C' (was 'null') for option -c%n") + "[picocli INFO] Unmatched arguments: [--y]%n")), App.class.getName(), CommandLine.versionString());
        String actual = new String(baos.toByteArray(), "UTF8");
        Assert.assertEquals(expected, actual);
        HelpTestUtil.setTraceLevel("WARN");
    }

    @Test
    public void testIsStopAtUnmatched_FalseByDefault() {
        @CommandLine.Command
        class A {}
        Assert.assertFalse(new CommandLine(new A()).isStopAtUnmatched());
    }

    @Test
    public void testSetStopAtUnmatched_True_SetsUnmatchedOptionsAllowedToTrue() {
        @CommandLine.Command
        class A {}
        CommandLine commandLine = new CommandLine(new A());
        Assert.assertFalse(commandLine.isUnmatchedArgumentsAllowed());
        commandLine.setStopAtUnmatched(true);
        Assert.assertTrue(commandLine.isUnmatchedArgumentsAllowed());
    }

    @Test
    public void testSetStopAtUnmatched_False_LeavesUnmatchedOptionsAllowedUnchanged() {
        @CommandLine.Command
        class A {}
        CommandLine commandLine = new CommandLine(new A());
        Assert.assertFalse(commandLine.isUnmatchedArgumentsAllowed());
        commandLine.setStopAtUnmatched(false);
        Assert.assertFalse(commandLine.isUnmatchedArgumentsAllowed());
    }

    @Test
    public void testStopAtUnmatched_UnmatchedOption() {
        HelpTestUtil.setTraceLevel("OFF");
        class App {
            @CommandLine.Option(names = "-a")
            String first;

            @CommandLine.Parameters
            String[] positional;
        }
        App cmd1 = new App();
        CommandLine commandLine1 = new CommandLine(cmd1).setStopAtUnmatched(true);
        commandLine1.parse("--y", "-a=abc", "positional");
        Assert.assertEquals(Arrays.asList("--y", "-a=abc", "positional"), commandLine1.getUnmatchedArguments());
        Assert.assertNull(cmd1.first);
        Assert.assertNull(cmd1.positional);
        try {
            // StopAtUnmatched=false, UnmatchedArgumentsAllowed=false
            new CommandLine(new App()).parse("--y", "-a=abc", "positional");
        } catch (CommandLine.UnmatchedArgumentException ex) {
            Assert.assertEquals("Unknown option: --y", ex.getMessage());
        }
        App cmd2 = new App();
        CommandLine commandLine2 = new CommandLine(cmd2).setStopAtUnmatched(false).setUnmatchedArgumentsAllowed(true);
        commandLine2.parse("--y", "-a=abc", "positional");
        Assert.assertEquals(Arrays.asList("--y"), commandLine2.getUnmatchedArguments());
        Assert.assertEquals("abc", cmd2.first);
        Assert.assertArrayEquals(new String[]{ "positional" }, cmd2.positional);
        HelpTestUtil.setTraceLevel("WARN");
    }

    @Test
    public void testIsStopAtPositional_FalseByDefault() {
        @CommandLine.Command
        class A {}
        Assert.assertFalse(new CommandLine(new A()).isStopAtPositional());
    }

    @Test
    public void testSetStopAtPositional_True_SetsStopAtPositionalToTrue() {
        @CommandLine.Command
        class A {}
        CommandLine commandLine = new CommandLine(new A());
        Assert.assertFalse(commandLine.isStopAtPositional());
        commandLine.setStopAtPositional(true);
        Assert.assertTrue(commandLine.isStopAtPositional());
    }

    @Test
    public void testStopAtPositional_TreatsOptionsAfterPositionalAsPositional() {
        class App {
            @CommandLine.Option(names = "-a")
            String first;

            @CommandLine.Parameters
            String[] positional;
        }
        App cmd1 = new App();
        CommandLine commandLine1 = new CommandLine(cmd1).setStopAtPositional(true);
        commandLine1.parse("positional", "-a=abc", "positional");
        Assert.assertArrayEquals(new String[]{ "positional", "-a=abc", "positional" }, cmd1.positional);
        Assert.assertNull(cmd1.first);
    }

    @Test
    public void test176OptionModifier() {
        class Args {
            @CommandLine.Option(names = "-option", description = "the option value")
            String option;

            @CommandLine.Option(names = "-option:env", description = "the environment variable to look up for the actual value")
            String optionEnvKey;

            @CommandLine.Option(names = "-option:file", description = "path to the file containing the option value")
            File optionFile;
        }
        Args args = CommandLine.populateCommand(new Args(), "-option", "VAL", "-option:env", "KEY", "-option:file", "/path/to/file");
        Assert.assertEquals("VAL", args.option);
        Assert.assertEquals("KEY", args.optionEnvKey);
        Assert.assertEquals(new File("/path/to/file"), args.optionFile);
    }

    @Test
    public void test187GetCommandNameReturnsMainClassByDefault() {
        class Args {
            @CommandLine.Parameters
            String[] args;
        }
        Assert.assertEquals("<main class>", new CommandLine(new Args()).getCommandName());
        Assert.assertEquals("<main class>", DEFAULT_COMMAND_NAME);
    }

    @Test
    public void test187GetCommandNameReturnsCommandAnnotationNameAttribute() {
        @CommandLine.Command(name = "someCommand")
        class Args {
            @CommandLine.Parameters
            String[] args;
        }
        Assert.assertEquals("someCommand", new CommandLine(new Args()).getCommandName());
    }

    @Test
    public void test187SetCommandNameOverwritesCommandAnnotationNameAttribute() {
        @CommandLine.Command(name = "someCommand")
        class Args {
            @CommandLine.Parameters
            String[] args;
        }
        Assert.assertEquals("someCommand", new CommandLine(new Args()).getCommandName());
        String OTHER = "a different name";
        Assert.assertEquals(OTHER, new CommandLine(new Args()).setCommandName(OTHER).getCommandName());
    }

    @Test
    public void test187GetCommandReturnsSubclassName() {
        @CommandLine.Command(name = "parent")
        class Parent {}
        @CommandLine.Command(name = "child")
        class Child extends Parent {}
        Assert.assertEquals("child", new CommandLine(new Child()).getCommandName());
    }

    @Test
    public void testIssue203InconsistentExceptions() {
        class Example {
            @CommandLine.Option(names = { "-h", "--help" }, help = true// NOTE: this should be usageHelp = true
            , description = "Displays this help message and quits.")
            private boolean helpRequested;

            @CommandLine.Option(names = { "-o", "--out-dir" }, required = true, description = "The output directory")
            private File outputDir;

            @CommandLine.Parameters(arity = "1..*", description = "The input files")
            private File[] inputFiles;
        }
        try {
            // Comment from AshwinJay : "Should've failed as inputFiles were not provided".
            // 
            // RP: After removing `usageHelp = true`, the "-o /tmp" argument is parsed as '-o'
            // with attached option value ' /tmp' (note the leading space).
            // A MissingParameterException is thrown for the missing <inputFiles>, as expected.
            new CommandLine(new Example()).parse("-o /tmp");
            Assert.fail("Expected MissingParameterException");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <inputFiles>", ex.getMessage());
        }
        try {
            // Comment from AshwinJay : "Should've failed as inputFiles were not provided"
            // 
            // RP: After removing `usageHelp = true`, the ["-o", " /tmp"] arguments are parsed and
            // a MissingParameterException is thrown for the missing <inputFiles>, as expected.
            new CommandLine(new Example()).parse("-o", " /tmp");
            Assert.fail("Expected MissingParameterException");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter: <inputFiles>", ex.getMessage());
        }
        try {
            // a MissingParameterException is thrown for missing required option -o, as expected
            new CommandLine(new Example()).parse("inputfile1", "inputfile2");
            Assert.fail("Expected MissingParameterException");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required option '--out-dir=<outputDir>'", ex.getMessage());
        }
        // a single empty string parameter was specified: this becomes an <inputFile> value
        try {
            new CommandLine(new Example()).parse("");
            Assert.fail("Expected MissingParameterException");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required option '--out-dir=<outputDir>'", ex.getMessage());
        }
        // no parameters were specified
        try {
            new CommandLine(new Example()).parse();
            Assert.fail("Expected MissingParameterException");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required options [--out-dir=<outputDir>, params[0..*]=<inputFiles>]", ex.getMessage());
        }
        // finally, let's test the success scenario
        Example example = new Example();
        new CommandLine(example).parse("-o", "/tmp", "inputfile1", "inputfile2");
        Assert.assertEquals(new File("/tmp"), example.outputDir);
        Assert.assertEquals(2, example.inputFiles.length);
        Assert.assertEquals(new File("inputfile1"), example.inputFiles[0]);
        Assert.assertEquals(new File("inputfile2"), example.inputFiles[1]);
    }

    @Test
    public void testIssue207ParameterExceptionProvidesAccessToFailedCommand_Programmatic() {
        class Top {
            @CommandLine.Option(names = "-o", required = true)
            String option;
        }
        class Sub1 {
            @CommandLine.Option(names = "-x", required = true)
            String x;
        }
        class Sub2 {
            @CommandLine.Option(names = "-y", required = true)
            String y;
        }
        try {
            new CommandLine(new Top()).addSubcommand("sub1", new Sub1()).addSubcommand("sub2", new Sub2()).parse("sub1 -x abc".split(" "));
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(((ex.getCommandLine().getCommand()) instanceof Top));
        }
        try {
            new CommandLine(new Top()).addSubcommand("sub1", new Sub1()).addSubcommand("sub2", new Sub2()).parse("-o OPT sub1 -wrong ABC".split(" "));
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(((ex.getCommandLine().getCommand()) instanceof Sub1));
        }
        try {
            new CommandLine(new Top()).addSubcommand("sub1", new Sub1()).addSubcommand("sub2", new Sub2()).parse("-o OPT sub2 -wrong ABC".split(" "));
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(((ex.getCommandLine().getCommand()) instanceof Sub2));
        }
        List<CommandLine> parsed = new CommandLine(new Top()).addSubcommand("sub1", new Sub1()).addSubcommand("sub2", new Sub2()).parse("-o OPT sub1 -x ABC".split(" "));
        Assert.assertEquals(2, parsed.size());
        Assert.assertEquals("OPT", ((Top) (parsed.get(0).getCommand())).option);
        Assert.assertEquals("ABC", ((Sub1) (parsed.get(1).getCommand())).x);
    }

    @CommandLine.Command(name = "sub207A")
    private static class Sub207A {
        @CommandLine.Option(names = "-x", required = true)
        String x;
    }

    @CommandLine.Command(name = "sub207B")
    private static class Sub207B {
        @CommandLine.Option(names = "-y", required = true)
        String y;
    }

    @Test
    public void testIssue207ParameterExceptionProvidesAccessToFailedCommand_Declarative() {
        @CommandLine.Command(subcommands = { CommandLineTest.Sub207A.class, CommandLineTest.Sub207B.class })
        class Top {
            @CommandLine.Option(names = "-o", required = true)
            String option;
        }
        try {
            new CommandLine(new Top()).parse("sub207A -x abc".split(" "));
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(((ex.getCommandLine().getCommand()) instanceof Top));
        }
        try {
            new CommandLine(new Top()).parse("-o OPT sub207A -wrong ABC".split(" "));
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(((ex.getCommandLine().getCommand()) instanceof CommandLineTest.Sub207A));
        }
        try {
            new CommandLine(new Top()).parse("-o OPT sub207B -wrong ABC".split(" "));
        } catch (CommandLine.ParameterException ex) {
            Assert.assertTrue(((ex.getCommandLine().getCommand()) instanceof CommandLineTest.Sub207B));
        }
        List<CommandLine> parsed = new CommandLine(new Top()).parse("-o OPT sub207A -x ABC".split(" "));
        Assert.assertEquals(2, parsed.size());
        Assert.assertEquals("OPT", ((Top) (parsed.get(0).getCommand())).option);
        Assert.assertEquals("ABC", ((CommandLineTest.Sub207A) (parsed.get(1).getCommand())).x);
    }

    @Test
    public void testIssue226EmptyStackWithClusteredOptions() {
        class Options {
            @CommandLine.Option(names = "-b")
            private boolean buffered = false;

            @CommandLine.Option(names = "-o")
            private boolean overwriteOutput = true;

            @CommandLine.Option(names = "-v")
            private boolean verbose = false;
        }
        Options options = CommandLine.populateCommand(new Options(), "-bov");
        Assert.assertTrue(options.buffered);
        Assert.assertFalse(options.overwriteOutput);
        Assert.assertTrue(options.verbose);
    }

    @Test
    public void testIssue217BooleanOptionArrayWithParameter() {
        class App {
            @CommandLine.Option(names = "-a", split = ",")
            private boolean[] array;
        }
        App app;
        app = CommandLine.populateCommand(new App(), "-a=true");
        Assert.assertArrayEquals(new boolean[]{ true }, app.array);
        app = CommandLine.populateCommand(new App(), "-a=true", "-a=true", "-a=true");
        Assert.assertArrayEquals(new boolean[]{ true, true, true }, app.array);
        app = CommandLine.populateCommand(new App(), "-a=true,true,true");
        Assert.assertArrayEquals(new boolean[]{ true, true, true }, app.array);
    }

    @Test
    public void testIssue217BooleanOptionArray() {
        class App {
            @CommandLine.Option(names = "-a")
            private boolean[] array;
        }
        App app;
        app = CommandLine.populateCommand(new App(), "-a");
        Assert.assertArrayEquals(new boolean[]{ true }, app.array);
        app = CommandLine.populateCommand(new App(), "-a", "-a", "-a");
        Assert.assertArrayEquals(new boolean[]{ true, true, true }, app.array);
        app = CommandLine.populateCommand(new App(), "-aaa");
        Assert.assertArrayEquals(new boolean[]{ true, true, true }, app.array);
    }

    @Test
    public void testIssue217BooleanOptionArrayExplicitArity() {
        class App {
            @CommandLine.Option(names = "-a", arity = "0")
            private boolean[] array;
        }
        App app;
        app = CommandLine.populateCommand(new App(), "-a");
        Assert.assertArrayEquals(new boolean[]{ true }, app.array);
        app = CommandLine.populateCommand(new App(), "-a", "-a", "-a");
        Assert.assertArrayEquals(new boolean[]{ true, true, true }, app.array);
        HelpTestUtil.setTraceLevel("DEBUG");
        app = CommandLine.populateCommand(new App(), "-aaa");
        Assert.assertArrayEquals(new boolean[]{ true, true, true }, app.array);
    }

    @Test
    public void testIssue217BooleanOptionList() {
        class App {
            @CommandLine.Option(names = "-a")
            private List<Boolean> list;
        }
        App app;
        app = CommandLine.populateCommand(new App(), "-a");
        Assert.assertEquals(Arrays.asList(true), app.list);
        app = CommandLine.populateCommand(new App(), "-a", "-a", "-a");
        Assert.assertEquals(Arrays.asList(true, true, true), app.list);
        app = CommandLine.populateCommand(new App(), "-aaa");
        Assert.assertEquals(Arrays.asList(true, true, true), app.list);
    }

    @Test
    public void testAtFileExpandedAbsolute() {
        class App {
            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile1.txt");
        App app = CommandLine.populateCommand(new App(), ("@" + (file.getAbsolutePath())));
        Assert.assertTrue(app.verbose);
        Assert.assertEquals(Arrays.asList("1111", "2222", ";3333"), app.files);
    }

    @Test
    public void testAtFileExpansionIgnoresSingleAtCharacter() {
        class App {
            @CommandLine.Parameters
            private List<String> files;
        }
        App app = CommandLine.populateCommand(new App(), "@", "abc");
        Assert.assertEquals(Arrays.asList("@", "abc"), app.files);
    }

    private static class AtFileTestingApp {
        @CommandLine.Option(names = "--simpleArg")
        private boolean simple;

        @CommandLine.Option(names = "--argWithSpaces")
        private String withSpaces;

        @CommandLine.Option(names = "--quotedArg")
        private String quoted;

        @CommandLine.Option(names = "--multiArg", arity = "1..*")
        private List<String> strings;

        @CommandLine.Option(names = "--urlArg")
        private URL url;

        @CommandLine.Option(names = "--unescapedBackslashArg")
        private String unescaped;
    }

    @Test
    public void testUseSimplifiedAtFilesCanBeSetProgrammatically() {
        ParserSpec parser = new ParserSpec();
        Assert.assertFalse(parser.useSimplifiedAtFiles());
        parser.useSimplifiedAtFiles(true);
        Assert.assertTrue(parser.useSimplifiedAtFiles());
    }

    @Test
    public void testUseSimplifiedAtFilesFromSystemProperty() {
        ParserSpec parser = new ParserSpec();
        Assert.assertFalse(parser.useSimplifiedAtFiles());
        System.setProperty("picocli.useSimplifiedAtFiles", "true");
        Assert.assertTrue(parser.useSimplifiedAtFiles());
    }

    @Test
    public void testUseSimplifiedAtFilesFromSystemPropertyCaseInsensitive() {
        ParserSpec parser = new ParserSpec();
        Assert.assertFalse(parser.useSimplifiedAtFiles());
        System.setProperty("picocli.useSimplifiedAtFiles", "TRUE");
        Assert.assertTrue(parser.useSimplifiedAtFiles());
    }

    @Test
    public void testUseSimplifiedAtFilesFromEmptySystemProperty() {
        ParserSpec parser = new ParserSpec();
        Assert.assertFalse(parser.useSimplifiedAtFiles());
        System.setProperty("picocli.useSimplifiedAtFiles", "");
        Assert.assertTrue(parser.useSimplifiedAtFiles());
    }

    @Test
    public void testUseSimplifiedAtFilesIsOverriddenBySystemProperty() {
        ParserSpec parser = new ParserSpec();
        Assert.assertFalse(parser.useSimplifiedAtFiles());
        parser.useSimplifiedAtFiles(true);
        System.setProperty("picocli.useSimplifiedAtFiles", "false");
        Assert.assertFalse(parser.useSimplifiedAtFiles());
    }

    @Test
    public void testAtFileSimplified() throws IOException {
        System.setProperty("picocli.useSimplifiedAtFiles", "true");
        /* first copy the old file and ensure it has a newline at the end. we do it this way to ensure that editors
        can not mess up the file by removing the newline, therefore invalidating this test.
         */
        File oldFile = findFile("/argfile-simplified.txt");
        String contents = CommandLineTest.readFile(oldFile);// this is where we ensure the newline is there

        File newFile = File.createTempFile("picocli", "atfile");
        CommandLineTest.writeFile(newFile, contents);
        CommandLineTest.AtFileTestingApp app = CommandLine.populateCommand(new CommandLineTest.AtFileTestingApp(), ("@" + (newFile.getAbsolutePath())));
        Assert.assertTrue(app.simple);
        Assert.assertEquals("something with spaces", app.withSpaces);
        Assert.assertEquals("\"something else\"", app.quoted);
        Assert.assertEquals(Arrays.asList("something else", "yet something else"), app.strings);
        Assert.assertEquals("https://picocli.info/", app.url.toString());
        Assert.assertEquals("C:\\Program Files\\picocli.txt", app.unescaped);
    }

    @Test
    public void testAtFileEndingWithoutNewline() throws IOException {
        System.setProperty("picocli.useSimplifiedAtFiles", "true");
        /* first copy the old file and ensure it has no newline at the end. we do it this way to ensure that editors
        can not mess up the file by adding the newline, therefore invalidating this test.
         */
        File oldFile = findFile("/argfile-simplified.txt");
        String contents = CommandLineTest.readFile(oldFile).trim();// this is where we remove the newline

        File newFile = File.createTempFile("picocli", "atfile");
        CommandLineTest.writeFile(newFile, contents);
        // then use the new file as the CLI at-file
        CommandLineTest.AtFileTestingApp app = CommandLine.populateCommand(new CommandLineTest.AtFileTestingApp(), ("@" + (newFile.getAbsolutePath())));
        Assert.assertTrue(app.simple);
        Assert.assertEquals("something with spaces", app.withSpaces);
        Assert.assertEquals("\"something else\"", app.quoted);
        Assert.assertEquals(Arrays.asList("something else", "yet something else"), app.strings);
        Assert.assertEquals("https://picocli.info/", app.url.toString());
        Assert.assertEquals("C:\\Program Files\\picocli.txt", app.unescaped);
    }

    @Test
    public void testAtFileSimplifiedWithQuotesTrimmed() {
        System.setProperty("picocli.useSimplifiedAtFiles", "");
        System.setProperty("picocli.trimQuotes", "true");
        File file = findFile("/argfile-simplified-quoted.txt");
        CommandLineTest.AtFileTestingApp app = CommandLine.populateCommand(new CommandLineTest.AtFileTestingApp(), ("@" + (file.getAbsolutePath())));
        Assert.assertEquals("something else", app.quoted);
        Assert.assertEquals("https://picocli.info/", app.url.toString());
        Assert.assertEquals("C:\\Program Files\\picocli.txt", app.unescaped);
    }

    @Test
    public void testAtFileNotExpandedIfDisabled() {
        class App {
            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile1.txt");
        Assert.assertTrue(file.getAbsoluteFile().exists());
        App app = new App();
        new CommandLine(app).setExpandAtFiles(false).parse(("@" + (file.getAbsolutePath())));
        Assert.assertFalse(app.verbose);
        Assert.assertEquals(Arrays.asList(("@" + (file.getAbsolutePath()))), app.files);
    }

    @Test
    public void testAtFileExpansionEnabledByDefault() {
        @CommandLine.Command
        class App {}
        Assert.assertTrue(new CommandLine(new App()).isExpandAtFiles());
    }

    @Test
    public void testAtFileExpandedRelative() {
        class App {
            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile1.txt");
        if (!(file.getAbsolutePath().startsWith(System.getProperty("user.dir")))) {
            return;
        }
        String relative = file.getAbsolutePath().substring(System.getProperty("user.dir").length());
        if (relative.startsWith(File.separator)) {
            relative = relative.substring(File.separator.length());
        }
        App app = CommandLine.populateCommand(new App(), ("@" + relative));
        Assert.assertTrue(app.verbose);
        Assert.assertEquals(Arrays.asList("1111", "2222", ";3333"), app.files);
    }

    @Test
    public void testAtFileExpandedMixedWithOtherParams() {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile1.txt");
        App app = CommandLine.populateCommand(new App(), "-f", "fVal1", ("@" + (file.getAbsolutePath())), "-x", "-f", "fVal2");
        Assert.assertTrue(app.verbose);
        Assert.assertEquals(Arrays.asList("1111", "2222", ";3333"), app.files);
        Assert.assertTrue(app.xxx);
        Assert.assertArrayEquals(new String[]{ "fVal1", "fVal2" }, app.fff);
    }

    @Test
    public void testAtFileExpandedWithCommentsOff() {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile1.txt");
        App app = new App();
        CommandLine cmd = new CommandLine(app);
        cmd.setAtFileCommentChar(null);
        cmd.parse("-f", "fVal1", ("@" + (file.getAbsolutePath())), "-x", "-f", "fVal2");
        Assert.assertTrue(app.verbose);
        Assert.assertEquals(Arrays.asList("#", "first", "comment", "1111", "2222", "#another", "comment", ";3333"), app.files);
        Assert.assertTrue(app.xxx);
        Assert.assertArrayEquals(new String[]{ "fVal1", "fVal2" }, app.fff);
    }

    @Test
    public void testAtFileExpandedWithNonDefaultCommentChar() {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile1.txt");
        App app = new App();
        CommandLine cmd = new CommandLine(app);
        cmd.setAtFileCommentChar(';');
        cmd.parse("-f", "fVal1", ("@" + (file.getAbsolutePath())), "-x", "-f", "fVal2");
        Assert.assertTrue(app.verbose);
        Assert.assertEquals(Arrays.asList("#", "first", "comment", "1111", "2222", "#another", "comment"), app.files);
        Assert.assertTrue(app.xxx);
        Assert.assertArrayEquals(new String[]{ "fVal1", "fVal2" }, app.fff);
    }

    @Test
    public void testAtFileWithMultipleValuesPerLine() {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile3-multipleValuesPerLine.txt");
        App app = CommandLine.populateCommand(new App(), "-f", "fVal1", ("@" + (file.getAbsolutePath())), "-f", "fVal2");
        Assert.assertTrue(app.verbose);
        Assert.assertEquals(Arrays.asList("1111", "2222", "3333"), app.files);
        Assert.assertTrue(app.xxx);
        Assert.assertArrayEquals(new String[]{ "fVal1", "FFFF", "F2F2F2", "fVal2" }, app.fff);
    }

    @Test
    public void testAtFileWithQuotedValuesContainingWhitespace() {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        HelpTestUtil.setTraceLevel("OFF");
        File file = findFile("/argfile4-quotedValuesContainingWhitespace.txt");
        App app = CommandLine.populateCommand(new App(), "-f", "fVal1", ("@" + (file.getAbsolutePath())), "-f", "fVal2");
        Assert.assertTrue(app.verbose);
        Assert.assertEquals(Arrays.asList("11 11", "22\n22", "3333"), app.files);
        Assert.assertTrue(app.xxx);
        Assert.assertArrayEquals(new String[]{ "fVal1", "F F F F", "F2 F2 F2", "fVal2" }, app.fff);
    }

    @Test
    public void testAtFileWithExcapedAtValues() {
        class App {
            @CommandLine.Parameters
            private List<String> files;
        }
        HelpTestUtil.setTraceLevel("INFO");
        File file = findFile("/argfile5-escapedAtValues.txt");
        App app = CommandLine.populateCommand(new App(), "aa", ("@" + (file.getAbsolutePath())), "bb");
        Assert.assertEquals(Arrays.asList("aa", "@val1", "@argfile5-escapedAtValues.txt", "bb"), app.files);
        Assert.assertTrue(this.systemErrRule.getLog().contains("Not expanding @-escaped argument"));
    }

    @Test
    public void testEscapedAtFileIsUnescapedButNotExpanded() {
        class App {
            @CommandLine.Parameters
            private List<String> files;
        }
        HelpTestUtil.setTraceLevel("OFF");
        File file = findFile("/argfile1.txt");
        App app = CommandLine.populateCommand(new App(), "aa", ("@@" + (file.getAbsolutePath())), "bb");
        Assert.assertEquals(Arrays.asList("aa", ("@" + (file.getAbsolutePath())), "bb"), app.files);
    }

    @Test
    public void testMultipleAtFilesExpandedMixedWithOtherParams() {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile1.txt");
        File file2 = findFile("/argfile2.txt");
        HelpTestUtil.setTraceLevel("OFF");
        App app = new App();
        CommandLine commandLine = new CommandLine(app).setOverwrittenOptionsAllowed(true);
        commandLine.parse("-f", "fVal1", ("@" + (file.getAbsolutePath())), "-x", ("@" + (file2.getAbsolutePath())), "-f", "fVal2");
        Assert.assertFalse("invoked twice", app.verbose);
        Assert.assertEquals(Arrays.asList("1111", "2222", ";3333", "1111", "2222", "3333"), app.files);
        Assert.assertFalse("invoked twice", app.xxx);
        Assert.assertArrayEquals(new String[]{ "fVal1", "FFFF", "F2F2F2", "fVal2" }, app.fff);
    }

    @Test
    public void testNestedAtFile() throws IOException {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile-with-nested-at-file.txt");
        File file2 = findFile("/argfile2.txt");
        File nested = new File("argfile2.txt");
        nested.delete();
        Assert.assertFalse("does not exist yet", nested.exists());
        copyFile(file2, nested);
        HelpTestUtil.setTraceLevel("OFF");
        App app = new App();
        CommandLine commandLine = new CommandLine(app).setOverwrittenOptionsAllowed(true);
        commandLine.parse("-f", "fVal1", ("@" + (file.getAbsolutePath())), "-f", "fVal2");
        Assert.assertTrue("invoked in argFile2", app.verbose);
        Assert.assertEquals(Arrays.asList("abcdefg", "1111", "2222", "3333"), app.files);
        Assert.assertTrue("invoked in argFile2", app.xxx);
        Assert.assertArrayEquals(new String[]{ "fVal1", "FFFF", "F2F2F2", "fVal2" }, app.fff);
        Assert.assertTrue(("Deleted " + nested), nested.delete());
    }

    @Test
    public void testRecursiveNestedAtFileIgnored() throws IOException {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile-with-recursive-at-file.txt");
        File localCopy = new File("argfile-with-recursive-at-file.txt");
        localCopy.delete();
        Assert.assertFalse("does not exist yet", localCopy.exists());
        copyFile(file, localCopy);
        HelpTestUtil.setTraceLevel("INFO");
        App app = new App();
        CommandLine commandLine = new CommandLine(app).setOverwrittenOptionsAllowed(true);
        commandLine.parse("-f", "fVal1", ("@" + (localCopy.getAbsolutePath())), "-f", "fVal2");
        Assert.assertEquals(Arrays.asList("abc defg", "xyz"), app.files);
        Assert.assertArrayEquals(new String[]{ "fVal1", "fVal2" }, app.fff);
        Assert.assertFalse("not invoked", app.verbose);
        Assert.assertFalse("not invoked", app.xxx);
        Assert.assertTrue(("Deleted " + localCopy), localCopy.delete());
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] Parsing 5 command line args [-f, fVal1, @"));
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] Expanding argument file @"));
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] Expanding argument file @argfile-with-recursive-at-file.txt"));
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] Already visited file "));
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("; ignoring..."));
    }

    @Test
    public void testNestedAtFileNotFound() throws IOException {
        class App {
            @CommandLine.Option(names = "-x")
            private boolean xxx;

            @CommandLine.Option(names = "-f")
            private String[] fff;

            @CommandLine.Option(names = "-v")
            private boolean verbose;

            @CommandLine.Parameters
            private List<String> files;
        }
        File file = findFile("/argfile-with-nested-at-file.txt");
        File nested = new File("argfile2.txt");
        nested.delete();
        Assert.assertFalse((nested + " does not exist"), nested.exists());
        HelpTestUtil.setTraceLevel("INFO");
        App app = new App();
        CommandLine commandLine = new CommandLine(app).setOverwrittenOptionsAllowed(true);
        commandLine.parse("-f", "fVal1", ("@" + (file.getAbsolutePath())), "-f", "fVal2");
        Assert.assertEquals(Arrays.asList("abcdefg", ("@" + (nested.getName()))), app.files);
        Assert.assertArrayEquals(new String[]{ "fVal1", "fVal2" }, app.fff);
        Assert.assertFalse("never invoked", app.verbose);
        Assert.assertFalse("never invoked", app.xxx);
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] Parsing 5 command line args [-f, fVal1, @"));
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] Expanding argument file @"));
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] Expanding argument file @argfile2.txt"));
        Assert.assertThat(systemErrRule.getLog(), CoreMatchers.containsString("[picocli INFO] File argfile2.txt does not exist or cannot be read; treating argument literally"));
    }

    @Test
    public void testGetAtFileCommentChar_SharpByDefault() {
        @CommandLine.Command
        class A {}
        Assert.assertEquals(((Character) ('#')), new CommandLine(new A()).getAtFileCommentChar());
    }

    @Test
    public void testAtFileExpansionExceptionHandling() throws Exception {
        Class<?> interpreterClass = Class.forName("picocli.CommandLine$Interpreter");
        Method m = interpreterClass.getDeclaredMethod("expandValidArgumentFile", String.class, File.class, List.class, Set.class);
        m.setAccessible(true);
        class App {
            @CommandLine.Parameters
            private List<String> files;
        }
        App app = new App();
        CommandLine commandLine = new CommandLine(app);
        Field f = CommandLine.class.getDeclaredField("interpreter");
        f.setAccessible(true);
        Object interpreter = f.get(commandLine);
        try {
            m.invoke(interpreter, "fileName", null, new ArrayList<String>(), new HashSet<String>());
            Assert.fail("Expected exception");
        } catch (InvocationTargetException ex) {
            CommandLine.InitializationException actual = ((CommandLine.InitializationException) (ex.getCause()));
            Assert.assertEquals("Could not read argument file @fileName", actual.getMessage());
            Assert.assertTrue(String.valueOf(actual.getCause()), ((actual.getCause()) instanceof NullPointerException));
        }
    }

    @Test
    public void testUnmatchedAnnotationWithInstantiatedList() {
        HelpTestUtil.setTraceLevel("OFF");
        class App {
            @CommandLine.Unmatched
            List<String> unmatched = new ArrayList<String>();

            @CommandLine.Option(names = "-o")
            String option;
        }
        App app = new App();
        CommandLine commandLine = new CommandLine(app);
        commandLine.parse("-t", "-x", "abc");
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), commandLine.getUnmatchedArguments());
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), app.unmatched);
    }

    @Test
    public void testUnmatchedAnnotationInstantiatesList() {
        HelpTestUtil.setTraceLevel("OFF");
        class App {
            @CommandLine.Unmatched
            List<String> unmatched;

            @CommandLine.Option(names = "-o")
            String option;
        }
        App app = new App();
        CommandLine commandLine = new CommandLine(app);
        commandLine.parse("-t", "-x", "abc");
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), commandLine.getUnmatchedArguments());
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), app.unmatched);
    }

    @Test
    public void testUnmatchedAnnotationInstantiatesArray() {
        HelpTestUtil.setTraceLevel("OFF");
        class App {
            @CommandLine.Unmatched
            String[] unmatched;

            @CommandLine.Option(names = "-o")
            String option;
        }
        App app = new App();
        CommandLine commandLine = new CommandLine(app);
        commandLine.parse("-t", "-x", "abc");
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), commandLine.getUnmatchedArguments());
        Assert.assertArrayEquals(new String[]{ "-t", "-x", "abc" }, app.unmatched);
    }

    @Test
    public void testMultipleUnmatchedAnnotations() {
        HelpTestUtil.setTraceLevel("OFF");
        class App {
            @CommandLine.Unmatched
            String[] unmatched1;

            @CommandLine.Unmatched
            String[] unmatched2;

            @CommandLine.Unmatched
            List<String> unmatched3;

            @CommandLine.Unmatched
            List<String> unmatched4;

            @CommandLine.Option(names = "-o")
            String option;
        }
        App app = new App();
        CommandLine commandLine = new CommandLine(app);
        commandLine.parse("-t", "-x", "abc");
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), commandLine.getUnmatchedArguments());
        Assert.assertArrayEquals(new String[]{ "-t", "-x", "abc" }, app.unmatched1);
        Assert.assertArrayEquals(new String[]{ "-t", "-x", "abc" }, app.unmatched2);
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), app.unmatched3);
        Assert.assertEquals(Arrays.asList("-t", "-x", "abc"), app.unmatched4);
    }

    @Test
    public void testCommandAllowsOnlyUnmatchedAnnotation() {
        HelpTestUtil.setTraceLevel("OFF");
        class App {
            @CommandLine.Unmatched
            String[] unmatched;
        }
        CommandLine cmd = new CommandLine(new App());
        cmd.parse("a", "b");
        Assert.assertEquals(Arrays.asList("a", "b"), cmd.getUnmatchedArguments());
    }

    @Test
    public void testUnmatchedAnnotationWithInvalidType_ThrowsException() throws Exception {
        @CommandLine.Command
        class App {
            @CommandLine.Unmatched
            String unmatched;
        }
        try {
            new CommandLine(new App());
            Assert.fail("Expected exception");
        } catch (CommandLine.InitializationException ex) {
            String pattern = "Invalid type for %s: must be either String[] or List<String>";
            Field f = App.class.getDeclaredField("unmatched");
            Assert.assertEquals(String.format(pattern, f), ex.getMessage());
        }
    }

    @Test
    public void testUnmatchedAnnotationWithInvalidGenericType_ThrowsException() throws Exception {
        @CommandLine.Command
        class App {
            @CommandLine.Unmatched
            List<Object> unmatched;
        }
        try {
            new CommandLine(new App());
            Assert.fail("Expected exception");
        } catch (CommandLine.InitializationException ex) {
            String pattern = "Invalid type for %s: must be either String[] or List<String>";
            Field f = App.class.getDeclaredField("unmatched");
            Assert.assertEquals(String.format(pattern, f), ex.getMessage());
        }
    }

    @Test
    public void testUnmatchedAndOptionAnnotation_ThrowsException() throws Exception {
        @CommandLine.Command
        class App {
            @CommandLine.Unmatched
            @CommandLine.Option(names = "-x")
            List<Object> unmatched;
        }
        try {
            new CommandLine(new App());
            Assert.fail("Expected exception");
        } catch (CommandLine.InitializationException ex) {
            String pattern = "A member cannot have both @Unmatched and @Option or @Parameters annotations, but '%s' has both.";
            Field f = App.class.getDeclaredField("unmatched");
            Assert.assertEquals(String.format(pattern, f), ex.getMessage());
        }
    }

    @Test
    public void testUnmatchedAndParametersAnnotation_ThrowsException() throws Exception {
        @CommandLine.Command
        class App {
            @CommandLine.Unmatched
            @CommandLine.Parameters
            List<Object> unmatched;
        }
        try {
            new CommandLine(new App());
            Assert.fail("Expected exception");
        } catch (CommandLine.InitializationException ex) {
            String pattern = "A member cannot have both @Unmatched and @Option or @Parameters annotations, but '%s' has both.";
            Field f = App.class.getDeclaredField("unmatched");
            Assert.assertEquals(String.format(pattern, f), ex.getMessage());
        }
    }

    @Test
    public void testUnmatchedAndMixinAnnotation_ThrowsException() throws Exception {
        @CommandLine.Command
        class App {
            @CommandLine.Unmatched
            @CommandLine.Mixin
            List<Object> unmatched;
        }
        try {
            new CommandLine(new App());
            Assert.fail("Expected exception");
        } catch (CommandLine.InitializationException ex) {
            String pattern = "A member cannot be both a @Mixin command and an @Unmatched but '%s' is both.";
            Field f = App.class.getDeclaredField("unmatched");
            Assert.assertEquals(String.format(pattern, f), ex.getMessage());
        }
    }

    @Test
    public void testMixinAndOptionAnnotation_ThrowsException() throws Exception {
        @CommandLine.Command
        class App {
            @CommandLine.Mixin
            @CommandLine.Option(names = "-x")
            List<Object> unmatched;
        }
        try {
            new CommandLine(new App());
            Assert.fail("Expected exception");
        } catch (CommandLine.InitializationException ex) {
            String pattern = "A member cannot be both a @Mixin command and an @Option or @Parameters, but '%s' is both.";
            Field f = App.class.getDeclaredField("unmatched");
            Assert.assertEquals(String.format(pattern, f), ex.getMessage());
        }
    }

    @Test
    public void testMixinAndParametersAnnotation_ThrowsException() throws Exception {
        @CommandLine.Command
        class App {
            @CommandLine.Mixin
            @CommandLine.Parameters
            List<Object> unmatched;
        }
        try {
            new CommandLine(new App());
            Assert.fail("Expected exception");
        } catch (CommandLine.InitializationException ex) {
            String pattern = "A member cannot be both a @Mixin command and an @Option or @Parameters, but '%s' is both.";
            Field f = App.class.getDeclaredField("unmatched");
            Assert.assertEquals(String.format(pattern, f), ex.getMessage());
        }
    }

    @Test
    public void testAnyHelpCommandMakesRequiredOptionsOptional() {
        @CommandLine.Command(name = "help", helpCommand = true)
        class MyHelpCommand {
            @CommandLine.Option(names = "-o")
            String option;
        }
        @CommandLine.Command(subcommands = MyHelpCommand.class)
        class Parent {
            @CommandLine.Option(names = "-m", required = true)
            String mandatory;
        }
        CommandLine commandLine = new CommandLine(new Parent(), new InnerClassFactory(this));
        commandLine.parse("help");
    }

    @Test
    public void testBuiltInHelpCommandMakesRequiredOptionsOptional() {
        @CommandLine.Command(subcommands = CommandLine.HelpCommand.class)
        class Parent {
            @CommandLine.Option(names = "-m", required = true)
            String mandatory;
        }
        CommandLine commandLine = new CommandLine(new Parent(), new InnerClassFactory(this));
        commandLine.parse("help");
    }

    @Test
    public void testAutoHelpOptionMakesRequiredOptionsOptional() {
        @CommandLine.Command(mixinStandardHelpOptions = true)
        class Parent {
            @CommandLine.Option(names = "-m", required = true)
            String mandatory;
        }
        CommandLine commandLine = new CommandLine(new Parent(), new InnerClassFactory(this));
        commandLine.parse("--help");
        Assert.assertTrue("No exceptions", true);
    }

    @Test
    public void testToggleBooleanFlagsByDefault() {
        class Flags {
            @CommandLine.Option(names = "-a")
            boolean a;

            @CommandLine.Option(names = "-b")
            boolean b = true;

            @CommandLine.Parameters(index = "0")
            boolean p0;

            @CommandLine.Parameters(index = "1")
            boolean p1 = true;
        }
        Flags flags = new Flags();
        CommandLine commandLine = new CommandLine(flags);
        Assert.assertFalse(flags.a);
        Assert.assertFalse(flags.p0);
        Assert.assertTrue(flags.b);
        Assert.assertTrue(flags.p1);
        commandLine.parse("-a", "-b", "true", "false");
        Assert.assertFalse((!(flags.a)));
        Assert.assertTrue((!(flags.b)));
        Assert.assertFalse((!(flags.p0)));
        Assert.assertTrue((!(flags.p1)));
        commandLine.parse("-a", "-b", "true", "false");
        Assert.assertFalse((!(flags.a)));
        Assert.assertTrue((!(flags.b)));
        Assert.assertFalse((!(flags.p0)));
        Assert.assertTrue((!(flags.p1)));
    }

    @Test
    public void testNoToggleBooleanFlagsWhenSwitchedOff() {
        class Flags {
            @CommandLine.Option(names = "-a")
            boolean a;

            @CommandLine.Option(names = "-b")
            boolean b = true;

            @CommandLine.Parameters(index = "0")
            boolean p0;

            @CommandLine.Parameters(index = "1")
            boolean p1 = true;
        }
        Flags flags = new Flags();
        CommandLine commandLine = new CommandLine(flags);
        commandLine.setToggleBooleanFlags(false);
        // initial
        Assert.assertFalse(flags.a);
        Assert.assertFalse(flags.p0);
        Assert.assertTrue(flags.b);
        Assert.assertTrue(flags.p1);
        commandLine.parse("-a", "-b", "true", "false");
        Assert.assertTrue(flags.a);
        Assert.assertTrue(flags.b);
        Assert.assertTrue(flags.p0);
        Assert.assertFalse(flags.p1);
        commandLine.parse("-a", "-b", "true", "false");
        Assert.assertTrue(flags.a);
        Assert.assertTrue(flags.b);
        Assert.assertTrue(flags.p0);
        Assert.assertFalse(flags.p1);
    }

    @Test
    public void testMapValuesContainingSeparator() {
        class MyCommand {
            @CommandLine.Option(names = { "-p", "--parameter" })
            Map<String, String> parameters;
        }
        String[] args = new String[]{ "-p", "AppOptions=\"-Dspring.profiles.active=test -Dspring.mail.host=smtp.mailtrap.io\"" };
        MyCommand c = CommandLine.populateCommand(new MyCommand(), args);
        Assert.assertEquals("\"-Dspring.profiles.active=test -Dspring.mail.host=smtp.mailtrap.io\"", c.parameters.get("AppOptions"));
        c = new MyCommand();
        new CommandLine(c).setTrimQuotes(true).parseArgs(args);
        Assert.assertEquals("-Dspring.profiles.active=test -Dspring.mail.host=smtp.mailtrap.io", c.parameters.get("AppOptions"));
        args = new String[]{ "-p", "\"AppOptions=-Dspring.profiles.active=test -Dspring.mail.host=smtp.mailtrap.io\"" };
        try {
            c = CommandLine.populateCommand(new MyCommand(), args);
            Assert.fail("Expected exception");
        } catch (CommandLine.ParameterException ex) {
            Assert.assertEquals("Value for option option \'--parameter\' (<String=String>) should be in KEY=VALUE format but was \"AppOptions=-Dspring.profiles.active=test -Dspring.mail.host=smtp.mailtrap.io\"", ex.getMessage());
        }
        c = new MyCommand();
        new CommandLine(c).setTrimQuotes(true).parseArgs(args);
        Assert.assertEquals("-Dspring.profiles.active=test -Dspring.mail.host=smtp.mailtrap.io", c.parameters.get("AppOptions"));
    }

    // Enum required for testIssue402, can't be local
    public enum Choices {

        CHOICE1,
        CHOICE2;}

    @Test
    public void testIssue402() {
        class AppWithEnum {
            @CommandLine.Parameters(type = CommandLineTest.Choices.class)
            private CommandLineTest.Choices choice;
        }
        AppWithEnum app;
        try {
            app = CommandLine.populateCommand(new AppWithEnum(), "CHOICE3");
        } catch (CommandLine.ParameterException e) {
            Assert.assertEquals("<choice>", e.getArgSpec().paramLabel());
            Assert.assertEquals(2, e.getArgSpec().type().getEnumConstants().length);
            Assert.assertEquals(CommandLineTest.Choices.CHOICE1, e.getArgSpec().type().getEnumConstants()[0]);
            Assert.assertEquals(CommandLineTest.Choices.CHOICE2, e.getArgSpec().type().getEnumConstants()[1]);
            Assert.assertEquals("CHOICE3", e.getValue());
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUnmatchedArgumentSuggestsSubcommands() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Demo.mainCommand().parseWithHandler(((CommandLine.IParseResultHandler) (null)), new PrintStream(baos), new String[]{ "chekcout" });
        String expected = String.format(("" + ("Unmatched argument: chekcout%n" + "Did you mean: checkout or help or branch?%n")));
        Assert.assertEquals(expected, baos.toString());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUnmatchedArgumentSuggestsSubcommands2() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Demo.mainCommand().parseWithHandler(((CommandLine.IParseResultHandler) (null)), new PrintStream(baos), new String[]{ "me" });
        String expected = String.format(("" + ("Unmatched argument: me%n" + "Did you mean: merge?%n")));
        Assert.assertEquals(expected, baos.toString());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUnmatchedArgumentSuggestsOptions() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLine cmd = new CommandLine(new Demo.GitCommit());
        cmd.parseWithHandler(((CommandLine.IParseResultHandler) (null)), new PrintStream(baos), new String[]{ "-fi" });
        String expected = String.format(("" + ("Unknown option: -fi%n" + "Possible solutions: --fixup, --file%n")));
        Assert.assertEquals(expected, baos.toString());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUnmatchedArgumentDoesNotSuggestOptionsIfNoMatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CommandLine cmd = new CommandLine(new Demo.GitCommit());
        cmd.parseWithHandler(((CommandLine.IParseResultHandler) (null)), new PrintStream(baos), new String[]{ "-x" });
        String actual = baos.toString();
        Assert.assertTrue(actual, actual.startsWith("Unknown option: -x"));
        Assert.assertTrue(actual, actual.contains("Usage:"));
        Assert.assertFalse(actual, actual.contains("Possible solutions:"));
    }

    @Test
    public void testInteractiveOptionReadsFromStdIn() {
        class App {
            @CommandLine.Option(names = "-x", description = { "Pwd", "line2" }, interactive = true)
            int x;

            @CommandLine.Option(names = "-z")
            int z;
        }
        PrintStream out = System.out;
        InputStream in = System.in;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            System.setIn(new ByteArrayInputStream("123".getBytes()));
            App app = new App();
            CommandLine cmd = new CommandLine(app);
            cmd.parse("-x");
            Assert.assertEquals("Enter value for -x (Pwd): ", baos.toString());
            Assert.assertEquals(123, app.x);
            Assert.assertEquals(0, app.z);
            cmd.parse("-z", "678");
            Assert.assertEquals(0, app.x);
            Assert.assertEquals(678, app.z);
        } finally {
            System.setOut(out);
            System.setIn(in);
        }
    }

    @Test
    public void testInteractiveOptionReadsFromStdInMultiLinePrompt() {
        class App {
            @CommandLine.Option(names = "-x", description = { "Pwd%nline2", "ignored" }, interactive = true)
            int x;

            @CommandLine.Option(names = "-z")
            int z;
        }
        PrintStream out = System.out;
        InputStream in = System.in;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            System.setIn(new ByteArrayInputStream("123".getBytes()));
            App app = new App();
            CommandLine cmd = new CommandLine(app);
            cmd.parse("-x", "-z", "987");
            String expectedPrompt = String.format("Enter value for -x (Pwd%nline2): ");
            Assert.assertEquals(expectedPrompt, baos.toString());
            Assert.assertEquals(123, app.x);
            Assert.assertEquals(987, app.z);
        } finally {
            System.setOut(out);
            System.setIn(in);
        }
    }

    @Test
    public void testInteractivePositionalReadsFromStdIn() {
        class App {
            @CommandLine.Parameters(index = "0", description = { "Pwd%nline2", "ignored" }, interactive = true)
            int x;

            @CommandLine.Parameters(index = "1")
            int z;
        }
        PrintStream out = System.out;
        InputStream in = System.in;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            System.setIn(new ByteArrayInputStream("123".getBytes()));
            App app = new App();
            CommandLine cmd = new CommandLine(app);
            cmd.parse("987");
            String expectedPrompt = String.format("Enter value for position 0 (Pwd%nline2): ");
            Assert.assertEquals(expectedPrompt, baos.toString());
            Assert.assertEquals(123, app.x);
            Assert.assertEquals(987, app.z);
        } finally {
            System.setOut(out);
            System.setIn(in);
        }
    }

    @Test
    public void testInteractivePositional2ReadsFromStdIn() {
        class App {
            @CommandLine.Parameters(index = "0")
            int a;

            @CommandLine.Parameters(index = "1", description = { "Pwd%nline2", "ignored" }, interactive = true)
            int x;

            @CommandLine.Parameters(index = "2")
            int z;
        }
        PrintStream out = System.out;
        InputStream in = System.in;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            System.setIn(new ByteArrayInputStream("123".getBytes()));
            App app = new App();
            CommandLine cmd = new CommandLine(app);
            cmd.parse("333", "987");
            String expectedPrompt = String.format("Enter value for position 1 (Pwd%nline2): ");
            Assert.assertEquals(expectedPrompt, baos.toString());
            Assert.assertEquals(333, app.a);
            Assert.assertEquals(123, app.x);
            Assert.assertEquals(987, app.z);
        } finally {
            System.setOut(out);
            System.setIn(in);
        }
    }

    @Test
    public void testLoginExample() {
        class Login implements Callable<Object> {
            @CommandLine.Option(names = { "-u", "--user" }, description = "User name")
            String user;

            @CommandLine.Option(names = { "-p", "--password" }, description = "Password or passphrase", interactive = true)
            String password;

            public Object call() throws Exception {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes());
                System.out.printf("Hi %s, your password is hashed to %s.%n", user, base64(md.digest()));
                return null;
            }

            private String base64(byte[] arr) throws Exception {
                // return javax.xml.bind.DatatypeConverter.printBase64Binary(arr);
                try {
                    Object enc = Class.forName("java.util.Base64").getDeclaredMethod("getEncoder").invoke(null, new Object[0]);
                    return ((String) (Class.forName("java.util.Base64$Encoder").getDeclaredMethod("encodeToString", new Class[]{ byte[].class }).invoke(enc, new Object[]{ arr })));
                } catch (Exception beforeJava8) {
                    // return new sun.misc.BASE64Encoder().encode(arr);
                    return "75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=";// :-)

                }
            }
        }
        PrintStream out = System.out;
        InputStream in = System.in;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            System.setIn(new ByteArrayInputStream("password123".getBytes()));
            Login login = new Login();
            CommandLine.call(login, "-u", "user123", "-p");
            String expectedPrompt = String.format(("Enter value for --password (Password or passphrase): " + "Hi user123, your password is hashed to 75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=.%n"));
            Assert.assertEquals(expectedPrompt, baos.toString());
            Assert.assertEquals("user123", login.user);
            Assert.assertEquals("password123", login.password);
        } finally {
            System.setOut(out);
            System.setIn(in);
        }
    }

    @Test
    public void testEmptyObjectArray() throws Exception {
        Method m = CommandLine.class.getDeclaredMethod("empty", new Class[]{ Object[].class });
        m.setAccessible(true);
        Assert.assertTrue(((Boolean) (m.invoke(null, new Object[]{ null }))));
        Assert.assertTrue(((Boolean) (m.invoke(null, new Object[]{ new String[0] }))));
    }

    @Test
    public void testStr() throws Exception {
        Method m = CommandLine.class.getDeclaredMethod("str", String[].class, int.class);
        m.setAccessible(true);
        Assert.assertEquals("", m.invoke(null, null, 0));
        Assert.assertEquals("", m.invoke(null, new String[0], 1));
    }

    @Test
    public void testParseAmbiguousKeyValueOption() {
        class App {
            @CommandLine.Option(names = "-x")
            String x;

            @CommandLine.Option(names = "-x=abc")
            String xabc;
        }
        try {
            CommandLine.populateCommand(new App(), "-x=abc");
            Assert.fail("Expected exception");
        } catch (CommandLine.MissingParameterException ex) {
            Assert.assertEquals("Missing required parameter for option '-x=abc' (<xabc>)", ex.getMessage());
        }
        Assert.assertEquals(String.format("[picocli WARN] Both '-x=abc' and '-x' are valid option names in <main class>. Using '-x=abc'...%n"), systemErrRule.getLog());
    }

    @Test
    public void testParseAmbiguousKeyValueOption2() {
        class App {
            @CommandLine.Option(names = "-x")
            String x;

            @CommandLine.Option(names = "-x=abc")
            String xabc;
        }
        App app = CommandLine.populateCommand(new App(), "-x=abc=xyz");
        Assert.assertNull(app.xabc);
        Assert.assertEquals("abc=xyz", app.x);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInterpreterUnquote() throws Exception {
        Class c = Class.forName("picocli.CommandLine$Interpreter");
        Method unquote = c.getDeclaredMethod("unquote", String.class);
        unquote.setAccessible(true);
        CommandSpec spec = CommandSpec.create();
        spec.parser().trimQuotes(true);
        CommandLine cmd = new CommandLine(spec);
        Object interpreter = PicocliTestUtil.interpreter(cmd);
        Assert.assertNull(unquote.invoke(interpreter, new Object[]{ null }));
        Assert.assertEquals("abc", unquote.invoke(interpreter, "\"abc\""));
        Assert.assertEquals("", unquote.invoke(interpreter, "\"\""));
        Assert.assertEquals("only balanced quotes 1", "\"abc", unquote.invoke(interpreter, "\"abc"));
        Assert.assertEquals("only balanced quotes 2", "abc\"", unquote.invoke(interpreter, "abc\""));
        Assert.assertEquals("only balanced quotes 3", "\"", unquote.invoke(interpreter, "\""));
        Assert.assertEquals("no quotes", "X", unquote.invoke(interpreter, "X"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInterpreterApplyValueToSingleValuedField() throws Exception {
        Class c = Class.forName("picocli.CommandLine$Interpreter");
        Class lookBehindClass = Class.forName("picocli.CommandLine$LookBehind");
        Method applyValueToSingleValuedField = c.getDeclaredMethod("applyValueToSingleValuedField", ArgSpec.class, lookBehindClass, Range.class, Stack.class, Set.class, String.class);
        applyValueToSingleValuedField.setAccessible(true);
        CommandSpec spec = CommandSpec.create();
        spec.parser().trimQuotes(true);
        CommandLine cmd = new CommandLine(spec);
        Object interpreter = PicocliTestUtil.interpreter(cmd);
        Method clear = c.getDeclaredMethod("clear");
        clear.setAccessible(true);
        clear.invoke(interpreter);// initializes the interpreter instance

        PositionalParamSpec arg = PositionalParamSpec.builder().arity("1").build();
        Object SEPARATE = lookBehindClass.getDeclaredField("SEPARATE").get(null);
        int value = ((Integer) (applyValueToSingleValuedField.invoke(interpreter, arg, SEPARATE, Range.valueOf("1"), new Stack<String>(), new HashSet<String>(), "")));
        Assert.assertEquals(0, value);
    }

    @Test
    public void testInterpreterProcessClusteredShortOptions_complex() {
        class App {
            @CommandLine.Option(names = "-x", arity = "1", split = ",")
            String x;

            @CommandLine.Parameters
            List<String> remainder;
        }
        App app = new App();
        CommandLine cmd = new CommandLine(app);
        cmd.getCommandSpec().parser().aritySatisfiedByAttachedOptionParam(true);
        cmd.parseArgs("-xa,b,c", "d", "e");
        Assert.assertEquals("a,b,c", app.x);
        Assert.assertEquals(Arrays.asList("d", "e"), app.remainder);
    }

    @Test
    public void testAssertNoMissingParametersOption() {
        class App {
            @CommandLine.Option(names = "-x")
            int x;
        }
        CommandLine cmd = new CommandLine(new App());
        cmd.getCommandSpec().parser().collectErrors(true);
        CommandLine.ParseResult parseResult = cmd.parseArgs("-x");
        List<Exception> errors = parseResult.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Missing required parameter for option '-x' (<x>)", errors.get(0).getMessage());
    }

    @Test
    public void testAssertNoMissingParametersPositional() {
        class App {
            @CommandLine.Parameters(arity = "1")
            int x;
        }
        CommandLine cmd = new CommandLine(new App());
        cmd.getCommandSpec().parser().collectErrors(true);
        CommandLine.ParseResult parseResult = cmd.parseArgs();
        List<Exception> errors = parseResult.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Missing required parameter: <x>", errors.get(0).getMessage());
    }

    @Test
    public void testUpdateHelpRequested() {
        class App {
            @CommandLine.Option(names = "-x", help = true)
            boolean x;
        }
        System.setProperty("picocli.trace", "INFO");
        new CommandLine(new App()).parseArgs("-x");
        Assert.assertTrue(systemErrRule.getLog().contains("App.x has 'help' annotation: not validating required fields"));
    }

    @Test
    public void testVarargCanConsumeNextValue() {
        class App {
            @CommandLine.Parameters(arity = "*")
            List<String> all;
        }
        App app1 = CommandLine.populateCommand(new App(), "--", "a", "b");
        Assert.assertEquals(Arrays.asList("a", "b"), app1.all);
    }

    @Test
    public void testVarargCanConsumeNextValue2() {
        @CommandLine.Command(subcommands = CommandLine.HelpCommand.class)
        class App {
            @CommandLine.Option(names = "-x", arity = "*")
            List<String> x;

            @CommandLine.Option(names = "-y", arity = "*")
            List<Integer> y;

            @CommandLine.Unmatched
            List<String> unmatched;
        }
        App app = CommandLine.populateCommand(new App(), "--", "-x", "3", "a", "b");
        Assert.assertEquals(Arrays.asList("-x", "3", "a", "b"), app.unmatched);
        app = CommandLine.populateCommand(new App(), "-x", "3", "a", "b");
        Assert.assertEquals(Arrays.asList("3", "a", "b"), app.x);
        app = CommandLine.populateCommand(new App(), "-y", "3", "a", "b");
        Assert.assertNull(app.x);
        Assert.assertEquals(Arrays.asList(3), app.y);
        Assert.assertEquals(Arrays.asList("a", "b"), app.unmatched);
        app = CommandLine.populateCommand(new App(), "-y", "3", "-x", "a", "b");
        Assert.assertEquals(Arrays.asList("a", "b"), app.x);
        Assert.assertEquals(Arrays.asList(3), app.y);
        Assert.assertNull(app.unmatched);
        app = CommandLine.populateCommand(new App(), "-y", "3", "help", "a", "b");
        Assert.assertNull(app.x);
        Assert.assertEquals(Arrays.asList(3), app.y);
        Assert.assertNull(app.unmatched);
    }

    @Test(expected = CommandLine.MissingParameterException.class)
    public void testBooleanOptionDefaulting() {
        class App {
            @CommandLine.Option(names = "-h", usageHelp = true, defaultValue = "false")
            boolean helpAsked;

            @CommandLine.Option(names = "-V", versionHelp = true, defaultValue = "false")
            boolean versionAsked;

            @CommandLine.Parameters
            String compulsoryParameter;
        }
        System.setProperty("picocli.trace", "DEBUG");
        CommandLine commandLine = new CommandLine(new App());
        commandLine.parseArgs(new String[0]);
    }

    @Test
    public void testIssue613SingleDashPositionalParam() {
        @CommandLine.Command(name = "dashtest", mixinStandardHelpOptions = true)
        class App {
            @CommandLine.Parameters(index = "0")
            private String json;

            @CommandLine.Parameters(index = "1")
            private String template;
        }
        System.setProperty("picocli.trace", "DEBUG");
        App app = new App();
        CommandLine commandLine = new CommandLine(app);
        // commandLine.setUnmatchedOptionsArePositionalParams(true);
        commandLine.parseArgs("-", "~/hello.mustache");
        Assert.assertEquals("-", app.json);
        Assert.assertEquals("~/hello.mustache", app.template);
        Assert.assertTrue(systemErrRule.getLog().contains("Single-character arguments that don't match known options are considered positional parameters"));
    }
}

