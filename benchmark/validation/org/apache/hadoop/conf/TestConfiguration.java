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
package org.apache.hadoop.conf;


import Configuration.ParsedTimeDuration;
import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration.IntegerRanges;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.alias.CredentialProvider;
import org.apache.hadoop.security.alias.CredentialProviderFactory;
import org.apache.hadoop.security.alias.LocalJavaKeyStoreProvider;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.PlatformName;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class TestConfiguration {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final double DOUBLE_DELTA = 1.0E-9F;

    private Configuration conf;

    static final String CONFIG = new File("./test-config-TestConfiguration.xml").getAbsolutePath();

    static final String CONFIG2 = new File("./test-config2-TestConfiguration.xml").getAbsolutePath();

    static final String CONFIG_CORE = new File("./core-site.xml").getAbsolutePath();

    static final String CONFIG_FOR_ENUM = new File("./test-config-enum-TestConfiguration.xml").getAbsolutePath();

    static final String CONFIG_FOR_URI = new File("./test-config-uri-TestConfiguration.xml").toURI().toString();

    private static final String CONFIG_MULTI_BYTE = new File("./test-config-multi-byte-TestConfiguration.xml").getAbsolutePath();

    private static final String CONFIG_MULTI_BYTE_SAVED = new File("./test-config-multi-byte-saved-TestConfiguration.xml").getAbsolutePath();

    static final Random RAN = new Random();

    static final String XMLHEADER = (PlatformName.IBM_JAVA) ? "<?xml version=\"1.0\" encoding=\"UTF-8\"?><configuration>" : "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><configuration>";

    /**
     * Four apostrophes.
     */
    public static final String ESCAPED = "&apos;&#39;&#0039;&#x27;";

    private static final String SENSITIVE_CONFIG_KEYS = CommonConfigurationKeysPublic.HADOOP_SECURITY_SENSITIVE_CONFIG_KEYS;

    private BufferedWriter out;

    @Test
    public void testInputStreamResource() throws Exception {
        StringWriter writer = new StringWriter();
        out = new BufferedWriter(writer);
        startConfig();
        declareProperty("prop", "A", "A");
        endConfig();
        InputStream in1 = Mockito.spy(new ByteArrayInputStream(writer.toString().getBytes()));
        Configuration conf = new Configuration(false);
        conf.addResource(in1);
        Assert.assertEquals("A", conf.get("prop"));
        Mockito.verify(in1, Mockito.times(1)).close();
        InputStream in2 = new ByteArrayInputStream(writer.toString().getBytes());
        conf.addResource(in2);
        Assert.assertEquals("A", conf.get("prop"));
    }

    @Test
    public void testFinalWarnings() throws Exception {
        // Make a configuration file with a final property
        StringWriter writer = new StringWriter();
        out = new BufferedWriter(writer);
        startConfig();
        declareProperty("prop", "A", "A", true);
        endConfig();
        byte[] bytes = writer.toString().getBytes();
        InputStream in1 = new ByteArrayInputStream(bytes);
        // Make a second config file with a final property with a different value
        writer = new StringWriter();
        out = new BufferedWriter(writer);
        startConfig();
        declareProperty("prop", "BB", "BB", true);
        endConfig();
        byte[] bytes2 = writer.toString().getBytes();
        InputStream in2 = new ByteArrayInputStream(bytes2);
        // Attach our own log appender so we can verify output
        TestConfiguration.TestAppender appender = new TestConfiguration.TestAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        try {
            // Add the 2 different resources - this should generate a warning
            conf.addResource(in1);
            conf.addResource(in2);
            Assert.assertEquals("should see the first value", "A", conf.get("prop"));
            List<LoggingEvent> events = appender.getLog();
            Assert.assertEquals("overriding a final parameter should cause logging", 1, events.size());
            LoggingEvent loggingEvent = events.get(0);
            String renderedMessage = loggingEvent.getRenderedMessage();
            Assert.assertTrue(("did not see expected string inside message " + renderedMessage), renderedMessage.contains(("an attempt to override final parameter: " + "prop;  Ignoring.")));
        } finally {
            // Make sure the appender is removed
            logger.removeAppender(appender);
        }
    }

    @Test
    public void testNoFinalWarnings() throws Exception {
        // Make a configuration file with a final property
        StringWriter writer = new StringWriter();
        out = new BufferedWriter(writer);
        startConfig();
        declareProperty("prop", "A", "A", true);
        endConfig();
        byte[] bytes = writer.toString().getBytes();
        // The 2 input streams both have the same config file
        InputStream in1 = new ByteArrayInputStream(bytes);
        InputStream in2 = new ByteArrayInputStream(bytes);
        // Attach our own log appender so we can verify output
        TestConfiguration.TestAppender appender = new TestConfiguration.TestAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        try {
            // Add the resource twice from a stream - should not generate warnings
            conf.addResource(in1);
            conf.addResource(in2);
            Assert.assertEquals("A", conf.get("prop"));
            List<LoggingEvent> events = appender.getLog();
            for (LoggingEvent loggingEvent : events) {
                System.out.println(("Event = " + (loggingEvent.getRenderedMessage())));
            }
            Assert.assertTrue("adding same resource twice should not cause logging", events.isEmpty());
        } finally {
            // Make sure the appender is removed
            logger.removeAppender(appender);
        }
    }

    @Test
    public void testFinalWarningsMultiple() throws Exception {
        // Make a configuration file with a repeated final property
        StringWriter writer = new StringWriter();
        out = new BufferedWriter(writer);
        startConfig();
        declareProperty("prop", "A", "A", true);
        declareProperty("prop", "A", "A", true);
        endConfig();
        byte[] bytes = writer.toString().getBytes();
        InputStream in1 = new ByteArrayInputStream(bytes);
        // Attach our own log appender so we can verify output
        TestConfiguration.TestAppender appender = new TestConfiguration.TestAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        try {
            // Add the resource - this should not produce a warning
            conf.addResource(in1);
            Assert.assertEquals("should see the value", "A", conf.get("prop"));
            List<LoggingEvent> events = appender.getLog();
            for (LoggingEvent loggingEvent : events) {
                System.out.println(("Event = " + (loggingEvent.getRenderedMessage())));
            }
            Assert.assertTrue("adding same resource twice should not cause logging", events.isEmpty());
        } finally {
            // Make sure the appender is removed
            logger.removeAppender(appender);
        }
    }

    @Test
    public void testFinalWarningsMultipleOverride() throws Exception {
        // Make a configuration file with 2 final properties with different values
        StringWriter writer = new StringWriter();
        out = new BufferedWriter(writer);
        startConfig();
        declareProperty("prop", "A", "A", true);
        declareProperty("prop", "BB", "BB", true);
        endConfig();
        byte[] bytes = writer.toString().getBytes();
        InputStream in1 = new ByteArrayInputStream(bytes);
        // Attach our own log appender so we can verify output
        TestConfiguration.TestAppender appender = new TestConfiguration.TestAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        try {
            // Add the resource - this should produce a warning
            conf.addResource(in1);
            Assert.assertEquals("should see the value", "A", conf.get("prop"));
            List<LoggingEvent> events = appender.getLog();
            Assert.assertEquals("overriding a final parameter should cause logging", 1, events.size());
            LoggingEvent loggingEvent = events.get(0);
            String renderedMessage = loggingEvent.getRenderedMessage();
            Assert.assertTrue(("did not see expected string inside message " + renderedMessage), renderedMessage.contains(("an attempt to override final parameter: " + "prop;  Ignoring.")));
        } finally {
            // Make sure the appender is removed
            logger.removeAppender(appender);
        }
    }

    /**
     * A simple appender for white box testing.
     */
    private static class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> log = new ArrayList<>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            log.add(loggingEvent);
        }

        @Override
        public void close() {
        }

        public List<LoggingEvent> getLog() {
            return new ArrayList<>(log);
        }
    }

    /**
     * Tests use of multi-byte characters in property names and values.  This test
     * round-trips multi-byte string literals through saving and loading of config
     * and asserts that the same values were read.
     */
    @Test
    public void testMultiByteCharacters() throws IOException {
        String priorDefaultEncoding = System.getProperty("file.encoding");
        try {
            System.setProperty("file.encoding", "US-ASCII");
            String name = "multi_byte_\u611b_name";
            String value = "multi_byte_\u0641_value";
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TestConfiguration.CONFIG_MULTI_BYTE), "UTF-8"));
            startConfig();
            declareProperty(name, value, value);
            endConfig();
            Configuration conf = new Configuration(false);
            conf.addResource(new Path(TestConfiguration.CONFIG_MULTI_BYTE));
            Assert.assertEquals(value, conf.get(name));
            try (FileOutputStream fos = new FileOutputStream(TestConfiguration.CONFIG_MULTI_BYTE_SAVED)) {
                conf.writeXml(fos);
            }
            conf = new Configuration(false);
            conf.addResource(new Path(TestConfiguration.CONFIG_MULTI_BYTE_SAVED));
            Assert.assertEquals(value, conf.get(name));
        } finally {
            System.setProperty("file.encoding", priorDefaultEncoding);
        }
    }

    @Test
    public void testVariableSubstitution() throws IOException {
        // stubbing only environment dependent functions
        Configuration mock = Mockito.spy(conf);
        Mockito.when(mock.getProperty("user.name")).thenReturn("hadoop_user");
        Mockito.when(mock.getenv("FILE_NAME")).thenReturn("hello");
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        declareProperty("my.int", "${intvar}", "42");
        declareProperty("intvar", "42", "42");
        declareProperty("my.base", "/tmp/${user.name}", "/tmp/hadoop_user");
        declareProperty("my.file", "${env.FILE_NAME}", "hello");
        declareProperty("my.suffix", ".txt", ".txt");
        declareProperty("my.relfile", "${my.file}${my.suffix}", "hello.txt");
        declareProperty("my.fullfile", "${my.base}/${my.file}${my.suffix}", "/tmp/hadoop_user/hello.txt");
        // check that undefined variables are returned as-is
        declareProperty("my.failsexpand", "a${my.undefvar}b", "a${my.undefvar}b");
        // check that multiple variable references are resolved
        declareProperty("my.user.group", "${user.name} ${user.name}", "hadoop_user hadoop_user");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        mock.addResource(fileResource);
        for (TestConfiguration.Prop p : props) {
            System.out.println(("p=" + (p.name)));
            String gotVal = mock.get(p.name);
            String gotRawVal = mock.getRaw(p.name);
            TestConfiguration.assertEq(p.val, gotRawVal);
            TestConfiguration.assertEq(p.expectEval, gotVal);
        }
        // check that expansion also occurs for getInt()
        Assert.assertTrue(((mock.getInt("intvar", (-1))) == 42));
        Assert.assertTrue(((mock.getInt("my.int", (-1))) == 42));
    }

    @Test
    public void testEnvDefault() throws IOException {
        Configuration mock = Mockito.spy(conf);
        Mockito.when(mock.getenv("NULL_VALUE")).thenReturn(null);
        Mockito.when(mock.getenv("EMPTY_VALUE")).thenReturn("");
        Mockito.when(mock.getenv("SOME_VALUE")).thenReturn("some value");
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        // if var is unbound, literal ${var} is returned
        declareProperty("null1", "${env.NULL_VALUE}", "${env.NULL_VALUE}");
        declareProperty("null2", "${env.NULL_VALUE-a}", "a");
        declareProperty("null3", "${env.NULL_VALUE:-b}", "b");
        declareProperty("empty1", "${env.EMPTY_VALUE}", "");
        declareProperty("empty2", "${env.EMPTY_VALUE-c}", "");
        declareProperty("empty3", "${env.EMPTY_VALUE:-d}", "d");
        declareProperty("some1", "${env.SOME_VALUE}", "some value");
        declareProperty("some2", "${env.SOME_VALUE-e}", "some value");
        declareProperty("some3", "${env.SOME_VALUE:-f}", "some value");
        // some edge cases
        declareProperty("edge1", "${env.NULL_VALUE-g-h}", "g-h");
        declareProperty("edge2", "${env.NULL_VALUE:-i:-j}", "i:-j");
        declareProperty("edge3", "${env.NULL_VALUE-}", "");
        declareProperty("edge4", "${env.NULL_VALUE:-}", "");
        declareProperty("edge5", "${env.NULL_VALUE:}", "${env.NULL_VALUE:}");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        mock.addResource(fileResource);
        for (TestConfiguration.Prop p : props) {
            System.out.println(("p=" + (p.name)));
            String gotVal = mock.get(p.name);
            String gotRawVal = mock.getRaw(p.name);
            TestConfiguration.assertEq(p.val, gotRawVal);
            TestConfiguration.assertEq(p.expectEval, gotVal);
        }
    }

    @Test
    public void testFinalParam() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        declareProperty("my.var", "", "", true);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        Configuration conf1 = new Configuration();
        conf1.addResource(fileResource);
        Assert.assertNull("my var is not null", conf1.get("my.var"));
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        declareProperty("my.var", "myval", "myval", false);
        endConfig();
        fileResource = new Path(TestConfiguration.CONFIG2);
        Configuration conf2 = new Configuration(conf1);
        conf2.addResource(fileResource);
        Assert.assertNull("my var is not final", conf2.get("my.var"));
    }

    @Test
    public void testCompactFormat() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendCompactFormatProperty("a", "b");
        appendCompactFormatProperty("c", "d", true);
        appendCompactFormatProperty("e", "f", false, "g");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        Configuration conf = new Configuration(false);
        conf.addResource(fileResource);
        Assert.assertEquals("b", conf.get("a"));
        Assert.assertEquals("d", conf.get("c"));
        Set<String> s = conf.getFinalParameters();
        Assert.assertEquals(1, s.size());
        Assert.assertTrue(s.contains("c"));
        Assert.assertEquals("f", conf.get("e"));
        String[] sources = conf.getPropertySources("e");
        Assert.assertEquals(2, sources.length);
        Assert.assertEquals("g", sources[0]);
        Assert.assertEquals(fileResource.toString(), sources[1]);
    }

    static class Prop {
        String name;

        String val;

        String expectEval;
    }

    ArrayList<TestConfiguration.Prop> props = new ArrayList<TestConfiguration.Prop>();

    @Test
    public void testOverlay() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("a", "b");
        appendProperty("b", "c");
        appendProperty("d", "e");
        appendProperty("e", "f", true);
        endConfig();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        appendProperty("a", "b");
        appendProperty("b", "d");
        appendProperty("e", "e");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        // set dynamically something
        conf.set("c", "d");
        conf.set("a", "d");
        Configuration clone = new Configuration(conf);
        clone.addResource(new Path(TestConfiguration.CONFIG2));
        Assert.assertEquals(clone.get("a"), "d");
        Assert.assertEquals(clone.get("b"), "d");
        Assert.assertEquals(clone.get("c"), "d");
        Assert.assertEquals(clone.get("d"), "e");
        Assert.assertEquals(clone.get("e"), "f");
    }

    @Test
    public void testCommentsInValue() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("my.comment", "this <!--comment here--> contains a comment");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        // two spaces one after "this", one before "contains"
        Assert.assertEquals("this  contains a comment", conf.get("my.comment"));
    }

    @Test
    public void testEscapedCharactersInValue() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("my.comment", TestConfiguration.ESCAPED);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        // two spaces one after "this", one before "contains"
        Assert.assertEquals("''''", conf.get("my.comment"));
    }

    @Test
    public void testTrim() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        String[] whitespaces = new String[]{ "", " ", "\n", "\t" };
        String[] name = new String[100];
        for (int i = 0; i < (name.length); i++) {
            name[i] = "foo" + i;
            StringBuilder prefix = new StringBuilder();
            StringBuilder postfix = new StringBuilder();
            for (int j = 0; j < 3; j++) {
                prefix.append(whitespaces[TestConfiguration.RAN.nextInt(whitespaces.length)]);
                postfix.append(whitespaces[TestConfiguration.RAN.nextInt(whitespaces.length)]);
            }
            appendProperty(((prefix + (name[i])) + postfix), ((name[i]) + ".value"));
        }
        endConfig();
        conf.addResource(new Path(TestConfiguration.CONFIG));
        for (String n : name) {
            Assert.assertEquals((n + ".value"), conf.get(n));
        }
    }

    @Test
    public void testGetLocalPath() throws IOException {
        Configuration conf = new Configuration();
        String[] dirs = new String[]{ "a", "b", "c" };
        for (int i = 0; i < (dirs.length); i++) {
            dirs[i] = new Path(GenericTestUtils.getTempPath(dirs[i])).toString();
        }
        conf.set("dirs", StringUtils.join(dirs, ","));
        for (int i = 0; i < 1000; i++) {
            String localPath = conf.getLocalPath("dirs", ("dir" + i)).toString();
            Assert.assertTrue(("Path doesn't end in specified dir: " + localPath), localPath.endsWith(("dir" + i)));
            Assert.assertFalse(("Path has internal whitespace: " + localPath), localPath.contains(" "));
        }
    }

    @Test
    public void testGetFile() throws IOException {
        Configuration conf = new Configuration();
        String[] dirs = new String[]{ "a", "b", "c" };
        for (int i = 0; i < (dirs.length); i++) {
            dirs[i] = new Path(GenericTestUtils.getTempPath(dirs[i])).toString();
        }
        conf.set("dirs", StringUtils.join(dirs, ","));
        for (int i = 0; i < 1000; i++) {
            String localPath = conf.getFile("dirs", ("dir" + i)).toString();
            Assert.assertTrue(("Path doesn't end in specified dir: " + localPath), localPath.endsWith(("dir" + i)));
            Assert.assertFalse(("Path has internal whitespace: " + localPath), localPath.contains(" "));
        }
    }

    @Test
    public void testToString() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        String expectedOutput = "Configuration: core-default.xml, core-site.xml, " + (fileResource.toString());
        Assert.assertEquals(expectedOutput, conf.toString());
    }

    @Test
    public void testWriteXml() throws IOException {
        Configuration conf = new Configuration();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        conf.writeXml(baos);
        String result = baos.toString();
        Assert.assertTrue("Result has proper header", result.startsWith(TestConfiguration.XMLHEADER));
        Assert.assertTrue("Result has proper footer", result.endsWith("</configuration>"));
    }

    @Test
    public void testIncludes() throws Exception {
        tearDown();
        System.out.println("XXX testIncludes");
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        appendProperty("a", "b");
        appendProperty("c", "d");
        endConfig();
        File fileUri = new File(new URI(TestConfiguration.CONFIG_FOR_URI));
        out = new BufferedWriter(new FileWriter(fileUri));
        startConfig();
        appendProperty("e", "f");
        appendProperty("g", "h");
        endConfig();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        startInclude(TestConfiguration.CONFIG2);
        endInclude();
        startInclude(TestConfiguration.CONFIG_FOR_URI);
        endInclude();
        appendProperty("i", "j");
        appendProperty("k", "l");
        endConfig();
        // verify that the includes file contains all properties
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(conf.get("a"), "b");
        Assert.assertEquals(conf.get("c"), "d");
        Assert.assertEquals(conf.get("e"), "f");
        Assert.assertEquals(conf.get("g"), "h");
        Assert.assertEquals(conf.get("i"), "j");
        Assert.assertEquals(conf.get("k"), "l");
        tearDown();
    }

    @Test
    public void testCharsetInDocumentEncoding() throws Exception {
        tearDown();
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TestConfiguration.CONFIG), StandardCharsets.ISO_8859_1));
        writeHeader(StandardCharsets.ISO_8859_1.displayName());
        writeConfiguration();
        appendProperty("a", "b");
        appendProperty("c", "M?ller");
        endConfig();
        // verify that the includes file contains all properties
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(conf.get("a"), "b");
        Assert.assertEquals(conf.get("c"), "M?ller");
        tearDown();
    }

    @Test
    public void testEntityReference() throws Exception {
        tearDown();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        writeHeader();
        declareEntity("configuration", "d", "d");
        writeConfiguration();
        appendProperty("a", "b");
        appendProperty("c", "&d;");
        endConfig();
        // verify that the includes file contains all properties
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(conf.get("a"), "b");
        Assert.assertEquals(conf.get("c"), "d");
        tearDown();
    }

    @Test
    public void testSystemEntityReference() throws Exception {
        tearDown();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        out.write("d");
        out.close();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        writeHeader();
        declareSystemEntity("configuration", "d", new Path(TestConfiguration.CONFIG2).toUri().toString());
        writeConfiguration();
        appendProperty("a", "b");
        appendProperty("c", "&d;");
        endConfig();
        // verify that the includes file contains all properties
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(conf.get("a"), "b");
        Assert.assertEquals(conf.get("c"), "d");
        tearDown();
    }

    @Test
    public void testIncludesWithFallback() throws Exception {
        tearDown();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        appendProperty("a", "b");
        appendProperty("c", "d");
        endConfig();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        startInclude(TestConfiguration.CONFIG2);
        startFallback();
        appendProperty("a", "b.fallback");
        appendProperty("c", "d.fallback", true);
        endFallback();
        endInclude();
        appendProperty("e", "f");
        appendProperty("g", "h");
        startInclude("MissingConfig.xml");
        startFallback();
        appendProperty("i", "j.fallback");
        appendProperty("k", "l.fallback", true);
        endFallback();
        endInclude();
        endConfig();
        // verify that the includes file contains all properties
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals("b", conf.get("a"));
        Assert.assertEquals("d", conf.get("c"));
        Assert.assertEquals("f", conf.get("e"));
        Assert.assertEquals("h", conf.get("g"));
        Assert.assertEquals("j.fallback", conf.get("i"));
        Assert.assertEquals("l.fallback", conf.get("k"));
        tearDown();
    }

    // When a resource is parsed as an input stream the first time, included
    // properties are saved within the config. However, the included properties
    // are not cached in the resource object. So, if an additional resource is
    // added after the config is parsed the first time, the config loses the
    // prperties that were included from the first resource.
    @Test
    public void testIncludesFromInputStreamWhenResourceAdded() throws Exception {
        tearDown();
        // CONFIG includes CONFIG2. CONFIG2 includes CONFIG_FOR_ENUM
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG_FOR_ENUM));
        startConfig();
        appendProperty("e", "SecondLevelInclude");
        appendProperty("f", "SecondLevelInclude");
        endConfig();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        startInclude(TestConfiguration.CONFIG_FOR_ENUM);
        endInclude();
        appendProperty("c", "FirstLevelInclude");
        appendProperty("d", "FirstLevelInclude");
        endConfig();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        startInclude(TestConfiguration.CONFIG2);
        endInclude();
        appendProperty("a", "1");
        appendProperty("b", "2");
        endConfig();
        // Add CONFIG as an InputStream resource.
        File file = new File(TestConfiguration.CONFIG);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        conf.addResource(bis);
        // The first time the conf is parsed, verify that all properties were read
        // from all levels of includes.
        Assert.assertEquals("1", conf.get("a"));
        Assert.assertEquals("2", conf.get("b"));
        Assert.assertEquals("FirstLevelInclude", conf.get("c"));
        Assert.assertEquals("FirstLevelInclude", conf.get("d"));
        Assert.assertEquals("SecondLevelInclude", conf.get("e"));
        Assert.assertEquals("SecondLevelInclude", conf.get("f"));
        // Add another resource to the conf.
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG_MULTI_BYTE));
        startConfig();
        appendProperty("g", "3");
        appendProperty("h", "4");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG_MULTI_BYTE);
        conf.addResource(fileResource);
        // Verify that all properties were read from all levels of includes the
        // second time the conf is parsed.
        Assert.assertEquals("1", conf.get("a"));
        Assert.assertEquals("2", conf.get("b"));
        Assert.assertEquals("FirstLevelInclude", conf.get("c"));
        Assert.assertEquals("FirstLevelInclude", conf.get("d"));
        Assert.assertEquals("SecondLevelInclude", conf.get("e"));
        Assert.assertEquals("SecondLevelInclude", conf.get("f"));
        Assert.assertEquals("3", conf.get("g"));
        Assert.assertEquals("4", conf.get("h"));
        tearDown();
    }

    @Test
    public void testOrderOfDuplicatePropertiesWithInclude() throws Exception {
        tearDown();
        // Property "a" is set to different values inside and outside of includes.
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        appendProperty("a", "a-InsideInclude");
        appendProperty("b", "b-InsideInclude");
        endConfig();
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("a", "a-OutsideInclude");
        startInclude(TestConfiguration.CONFIG2);
        endInclude();
        appendProperty("b", "b-OutsideInclude");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals("a-InsideInclude", conf.get("a"));
        Assert.assertEquals("b-OutsideInclude", conf.get("b"));
        tearDown();
    }

    @Test
    public void testRelativeIncludes() throws Exception {
        tearDown();
        String relConfig = new File("./tmp/test-config.xml").getAbsolutePath();
        String relConfig2 = new File("./tmp/test-config2.xml").getAbsolutePath();
        new File(new File(relConfig).getParent()).mkdirs();
        out = new BufferedWriter(new FileWriter(relConfig2));
        startConfig();
        appendProperty("a", "b");
        endConfig();
        out = new BufferedWriter(new FileWriter(relConfig));
        startConfig();
        // Add the relative path instead of the absolute one.
        startInclude(new File(relConfig2).getName());
        endInclude();
        appendProperty("c", "d");
        endConfig();
        // verify that the includes file contains all properties
        Path fileResource = new Path(relConfig);
        conf.addResource(fileResource);
        Assert.assertEquals(conf.get("a"), "b");
        Assert.assertEquals(conf.get("c"), "d");
        // Cleanup
        new File(relConfig).delete();
        new File(relConfig2).delete();
        new File(new File(relConfig).getParent()).delete();
    }

    @Test
    public void testIntegerRanges() {
        Configuration conf = new Configuration();
        conf.set("first", "-100");
        conf.set("second", "4-6,9-10,27");
        conf.set("third", "34-");
        Configuration.IntegerRanges range = conf.getRange("first", null);
        System.out.println(("first = " + range));
        Assert.assertEquals(true, range.isIncluded(0));
        Assert.assertEquals(true, range.isIncluded(1));
        Assert.assertEquals(true, range.isIncluded(100));
        Assert.assertEquals(false, range.isIncluded(101));
        range = conf.getRange("second", null);
        System.out.println(("second = " + range));
        Assert.assertEquals(false, range.isIncluded(3));
        Assert.assertEquals(true, range.isIncluded(4));
        Assert.assertEquals(true, range.isIncluded(6));
        Assert.assertEquals(false, range.isIncluded(7));
        Assert.assertEquals(false, range.isIncluded(8));
        Assert.assertEquals(true, range.isIncluded(9));
        Assert.assertEquals(true, range.isIncluded(10));
        Assert.assertEquals(false, range.isIncluded(11));
        Assert.assertEquals(false, range.isIncluded(26));
        Assert.assertEquals(true, range.isIncluded(27));
        Assert.assertEquals(false, range.isIncluded(28));
        range = conf.getRange("third", null);
        System.out.println(("third = " + range));
        Assert.assertEquals(false, range.isIncluded(33));
        Assert.assertEquals(true, range.isIncluded(34));
        Assert.assertEquals(true, range.isIncluded(100000000));
    }

    @Test
    public void testGetRangeIterator() throws Exception {
        Configuration config = new Configuration(false);
        IntegerRanges ranges = config.getRange("Test", "");
        Assert.assertFalse("Empty range has values", ranges.iterator().hasNext());
        ranges = config.getRange("Test", "5");
        Set<Integer> expected = new HashSet<Integer>(Arrays.asList(5));
        Set<Integer> found = new HashSet<Integer>();
        for (Integer i : ranges) {
            found.add(i);
        }
        Assert.assertEquals(expected, found);
        ranges = config.getRange("Test", "5-10,13-14");
        expected = new HashSet<Integer>(Arrays.asList(5, 6, 7, 8, 9, 10, 13, 14));
        found = new HashSet<Integer>();
        for (Integer i : ranges) {
            found.add(i);
        }
        Assert.assertEquals(expected, found);
        ranges = config.getRange("Test", "8-12, 5- 7");
        expected = new HashSet<Integer>(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12));
        found = new HashSet<Integer>();
        for (Integer i : ranges) {
            found.add(i);
        }
        Assert.assertEquals(expected, found);
    }

    @Test
    public void testHexValues() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.hex1", "0x10");
        appendProperty("test.hex2", "0xF");
        appendProperty("test.hex3", "-0x10");
        // Invalid?
        appendProperty("test.hex4", "-0x10xyz");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(16, conf.getInt("test.hex1", 0));
        Assert.assertEquals(16, conf.getLong("test.hex1", 0));
        Assert.assertEquals(15, conf.getInt("test.hex2", 0));
        Assert.assertEquals(15, conf.getLong("test.hex2", 0));
        Assert.assertEquals((-16), conf.getInt("test.hex3", 0));
        Assert.assertEquals((-16), conf.getLong("test.hex3", 0));
        try {
            conf.getLong("test.hex4", 0);
            Assert.fail("Property had invalid long value, but was read successfully.");
        } catch (NumberFormatException e) {
            // pass
        }
        try {
            conf.getInt("test.hex4", 0);
            Assert.fail("Property had invalid int value, but was read successfully.");
        } catch (NumberFormatException e) {
            // pass
        }
    }

    @Test
    public void testIntegerValues() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.int1", "20");
        appendProperty("test.int2", "020");
        appendProperty("test.int3", "-20");
        appendProperty("test.int4", " -20 ");
        appendProperty("test.int5", " -20xyz ");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(20, conf.getInt("test.int1", 0));
        Assert.assertEquals(20, conf.getLong("test.int1", 0));
        Assert.assertEquals(20, conf.getLongBytes("test.int1", 0));
        Assert.assertEquals(20, conf.getInt("test.int2", 0));
        Assert.assertEquals(20, conf.getLong("test.int2", 0));
        Assert.assertEquals(20, conf.getLongBytes("test.int2", 0));
        Assert.assertEquals((-20), conf.getInt("test.int3", 0));
        Assert.assertEquals((-20), conf.getLong("test.int3", 0));
        Assert.assertEquals((-20), conf.getLongBytes("test.int3", 0));
        Assert.assertEquals((-20), conf.getInt("test.int4", 0));
        Assert.assertEquals((-20), conf.getLong("test.int4", 0));
        Assert.assertEquals((-20), conf.getLongBytes("test.int4", 0));
        try {
            conf.getInt("test.int5", 0);
            Assert.fail("Property had invalid int value, but was read successfully.");
        } catch (NumberFormatException e) {
            // pass
        }
    }

    @Test
    public void testHumanReadableValues() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.humanReadableValue1", "1m");
        appendProperty("test.humanReadableValue2", "1M");
        appendProperty("test.humanReadableValue5", "1MBCDE");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(1048576, conf.getLongBytes("test.humanReadableValue1", 0));
        Assert.assertEquals(1048576, conf.getLongBytes("test.humanReadableValue2", 0));
        try {
            conf.getLongBytes("test.humanReadableValue5", 0);
            Assert.fail("Property had invalid human readable value, but was read successfully.");
        } catch (NumberFormatException e) {
            // pass
        }
    }

    @Test
    public void testBooleanValues() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.bool1", "true");
        appendProperty("test.bool2", "false");
        appendProperty("test.bool3", "  true ");
        appendProperty("test.bool4", " false ");
        appendProperty("test.bool5", "foo");
        appendProperty("test.bool6", "TRUE");
        appendProperty("test.bool7", "FALSE");
        appendProperty("test.bool8", "");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(true, conf.getBoolean("test.bool1", false));
        Assert.assertEquals(false, conf.getBoolean("test.bool2", true));
        Assert.assertEquals(true, conf.getBoolean("test.bool3", false));
        Assert.assertEquals(false, conf.getBoolean("test.bool4", true));
        Assert.assertEquals(true, conf.getBoolean("test.bool5", true));
        Assert.assertEquals(true, conf.getBoolean("test.bool6", false));
        Assert.assertEquals(false, conf.getBoolean("test.bool7", true));
        Assert.assertEquals(false, conf.getBoolean("test.bool8", false));
    }

    @Test
    public void testFloatValues() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.float1", "3.1415");
        appendProperty("test.float2", "003.1415");
        appendProperty("test.float3", "-3.1415");
        appendProperty("test.float4", " -3.1415 ");
        appendProperty("test.float5", "xyz-3.1415xyz");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(3.1415F, conf.getFloat("test.float1", 0.0F), TestConfiguration.DOUBLE_DELTA);
        Assert.assertEquals(3.1415F, conf.getFloat("test.float2", 0.0F), TestConfiguration.DOUBLE_DELTA);
        Assert.assertEquals((-3.1415F), conf.getFloat("test.float3", 0.0F), TestConfiguration.DOUBLE_DELTA);
        Assert.assertEquals((-3.1415F), conf.getFloat("test.float4", 0.0F), TestConfiguration.DOUBLE_DELTA);
        try {
            conf.getFloat("test.float5", 0.0F);
            Assert.fail("Property had invalid float value, but was read successfully.");
        } catch (NumberFormatException e) {
            // pass
        }
    }

    @Test
    public void testDoubleValues() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.double1", "3.1415");
        appendProperty("test.double2", "003.1415");
        appendProperty("test.double3", "-3.1415");
        appendProperty("test.double4", " -3.1415 ");
        appendProperty("test.double5", "xyz-3.1415xyz");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals(3.1415, conf.getDouble("test.double1", 0.0), TestConfiguration.DOUBLE_DELTA);
        Assert.assertEquals(3.1415, conf.getDouble("test.double2", 0.0), TestConfiguration.DOUBLE_DELTA);
        Assert.assertEquals((-3.1415), conf.getDouble("test.double3", 0.0), TestConfiguration.DOUBLE_DELTA);
        Assert.assertEquals((-3.1415), conf.getDouble("test.double4", 0.0), TestConfiguration.DOUBLE_DELTA);
        try {
            conf.getDouble("test.double5", 0.0);
            Assert.fail("Property had invalid double value, but was read successfully.");
        } catch (NumberFormatException e) {
            // pass
        }
    }

    @Test
    public void testGetClass() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.class1", "java.lang.Integer");
        appendProperty("test.class2", " java.lang.Integer ");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals("java.lang.Integer", conf.getClass("test.class1", null).getCanonicalName());
        Assert.assertEquals("java.lang.Integer", conf.getClass("test.class2", null).getCanonicalName());
    }

    @Test
    public void testGetClasses() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.classes1", "java.lang.Integer,java.lang.String");
        appendProperty("test.classes2", " java.lang.Integer , java.lang.String ");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        String[] expectedNames = new String[]{ "java.lang.Integer", "java.lang.String" };
        Class<?>[] defaultClasses = new Class<?>[]{  };
        Class<?>[] classes1 = conf.getClasses("test.classes1", defaultClasses);
        Class<?>[] classes2 = conf.getClasses("test.classes2", defaultClasses);
        Assert.assertArrayEquals(expectedNames, TestConfiguration.extractClassNames(classes1));
        Assert.assertArrayEquals(expectedNames, TestConfiguration.extractClassNames(classes2));
    }

    @Test
    public void testGetStringCollection() {
        Configuration c = new Configuration();
        c.set("x", " a, b\n,\nc ");
        Collection<String> strs = c.getTrimmedStringCollection("x");
        Assert.assertEquals(3, strs.size());
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, strs.toArray(new String[0]));
        // Check that the result is mutable
        strs.add("z");
        // Make sure same is true for missing config
        strs = c.getStringCollection("does-not-exist");
        Assert.assertEquals(0, strs.size());
        strs.add("z");
    }

    @Test
    public void testGetTrimmedStringCollection() {
        Configuration c = new Configuration();
        c.set("x", "a, b, c");
        Collection<String> strs = c.getStringCollection("x");
        Assert.assertEquals(3, strs.size());
        Assert.assertArrayEquals(new String[]{ "a", " b", " c" }, strs.toArray(new String[0]));
        // Check that the result is mutable
        strs.add("z");
        // Make sure same is true for missing config
        strs = c.getStringCollection("does-not-exist");
        Assert.assertEquals(0, strs.size());
        strs.add("z");
    }

    enum Dingo {

        FOO,
        BAR;}

    enum Yak {

        RAB,
        FOO;}

    @Test
    public void testEnum() {
        Configuration conf = new Configuration();
        conf.setEnum("test.enum", TestConfiguration.Dingo.FOO);
        Assert.assertSame(TestConfiguration.Dingo.FOO, conf.getEnum("test.enum", TestConfiguration.Dingo.BAR));
        Assert.assertSame(TestConfiguration.Yak.FOO, conf.getEnum("test.enum", TestConfiguration.Yak.RAB));
        conf.setEnum("test.enum", TestConfiguration.Dingo.FOO);
        boolean fail = false;
        try {
            conf.setEnum("test.enum", TestConfiguration.Dingo.BAR);
            conf.getEnum("test.enum", TestConfiguration.Yak.FOO);
        } catch (IllegalArgumentException e) {
            fail = true;
        }
        Assert.assertTrue(fail);
    }

    @Test
    public void testEnumFromXml() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG_FOR_ENUM));
        startConfig();
        appendProperty("test.enum", " \t \n   FOO \t \n");
        appendProperty("test.enum2", " \t \n   Yak.FOO \t \n");
        endConfig();
        Configuration conf = new Configuration();
        Path fileResource = new Path(TestConfiguration.CONFIG_FOR_ENUM);
        conf.addResource(fileResource);
        Assert.assertSame(TestConfiguration.Yak.FOO, conf.getEnum("test.enum", TestConfiguration.Yak.FOO));
        boolean fail = false;
        try {
            conf.getEnum("test.enum2", TestConfiguration.Yak.FOO);
        } catch (IllegalArgumentException e) {
            fail = true;
        }
        Assert.assertTrue(fail);
    }

    @Test
    public void testTimeDuration() {
        Configuration conf = new Configuration(false);
        conf.setTimeDuration("test.time.a", 7L, TimeUnit.SECONDS);
        Assert.assertEquals("7s", conf.get("test.time.a"));
        Assert.assertEquals(0L, conf.getTimeDuration("test.time.a", 30, TimeUnit.MINUTES));
        Assert.assertEquals(7L, conf.getTimeDuration("test.time.a", 30, TimeUnit.SECONDS));
        Assert.assertEquals(7000L, conf.getTimeDuration("test.time.a", 30, TimeUnit.MILLISECONDS));
        Assert.assertEquals(7000000L, conf.getTimeDuration("test.time.a", 30, TimeUnit.MICROSECONDS));
        Assert.assertEquals(7000000000L, conf.getTimeDuration("test.time.a", 30, TimeUnit.NANOSECONDS));
        conf.setTimeDuration("test.time.b", 1, TimeUnit.DAYS);
        Assert.assertEquals("1d", conf.get("test.time.b"));
        Assert.assertEquals(1, conf.getTimeDuration("test.time.b", 1, TimeUnit.DAYS));
        Assert.assertEquals(24, conf.getTimeDuration("test.time.b", 1, TimeUnit.HOURS));
        Assert.assertEquals(TimeUnit.MINUTES.convert(1, TimeUnit.DAYS), conf.getTimeDuration("test.time.b", 1, TimeUnit.MINUTES));
        // check default
        Assert.assertEquals(30L, conf.getTimeDuration("test.time.X", 30, TimeUnit.SECONDS));
        conf.set("test.time.X", "30");
        Assert.assertEquals(30L, conf.getTimeDuration("test.time.X", 40, TimeUnit.SECONDS));
        Assert.assertEquals(10L, conf.getTimeDuration("test.time.c", "10", TimeUnit.SECONDS));
        Assert.assertEquals(30L, conf.getTimeDuration("test.time.c", "30s", TimeUnit.SECONDS));
        Assert.assertEquals(120L, conf.getTimeDuration("test.time.c", "2m", TimeUnit.SECONDS));
        conf.set("test.time.c", "30");
        Assert.assertEquals(30L, conf.getTimeDuration("test.time.c", "40s", TimeUnit.SECONDS));
        // check suffix insensitive
        conf.set("test.time.d", "30S");
        Assert.assertEquals(30L, conf.getTimeDuration("test.time.d", 40, TimeUnit.SECONDS));
        for (Configuration.ParsedTimeDuration ptd : ParsedTimeDuration.values()) {
            conf.setTimeDuration("test.time.unit", 1, ptd.unit());
            Assert.assertEquals((1 + (ptd.suffix())), conf.get("test.time.unit"));
            Assert.assertEquals(1, conf.getTimeDuration("test.time.unit", 2, ptd.unit()));
        }
    }

    @Test
    public void testStorageUnit() {
        final String key = "valid.key";
        final String nonKey = "not.a.key";
        Configuration conf = new Configuration(false);
        conf.setStorageSize(key, 10, StorageUnit.MB);
        // This call returns the value specified in the Key as a double in MBs.
        Assert.assertThat(conf.getStorageSize(key, "1GB", StorageUnit.MB), Is.is(10.0));
        // Since this key is missing, This call converts the default value of  1GB
        // to MBs are returns that value.
        Assert.assertThat(conf.getStorageSize(nonKey, "1GB", StorageUnit.MB), Is.is(1024.0));
        conf.setStorageSize(key, 1024, StorageUnit.BYTES);
        Assert.assertThat(conf.getStorageSize(key, 100, StorageUnit.KB), Is.is(1.0));
        Assert.assertThat(conf.getStorageSize(nonKey, 100.0, StorageUnit.KB), Is.is(100.0));
        // We try out different kind of String formats to see if they work and
        // during read, we also try to read using a different Storage Units.
        conf.setStrings(key, "1TB");
        Assert.assertThat(conf.getStorageSize(key, "1PB", StorageUnit.GB), Is.is(1024.0));
        conf.setStrings(key, "1bytes");
        Assert.assertThat(conf.getStorageSize(key, "1PB", StorageUnit.KB), Is.is(0.001));
        conf.setStrings(key, "2048b");
        Assert.assertThat(conf.getStorageSize(key, "1PB", StorageUnit.KB), Is.is(2.0));
        conf.setStrings(key, "64 GB");
        Assert.assertThat(conf.getStorageSize(key, "1PB", StorageUnit.GB), Is.is(64.0));
        // Match the parsing patterns of getLongBytes, which takes single char
        // suffix.
        conf.setStrings(key, "1T");
        Assert.assertThat(conf.getStorageSize(key, "1GB", StorageUnit.TB), Is.is(1.0));
        conf.setStrings(key, "1k");
        Assert.assertThat(conf.getStorageSize(key, "1GB", StorageUnit.KB), Is.is(1.0));
        conf.setStrings(key, "10m");
        Assert.assertThat(conf.getStorageSize(key, "1GB", StorageUnit.MB), Is.is(10.0));
        // Missing format specification, this should throw.
        conf.setStrings(key, "100");
        thrown.expect(IllegalArgumentException.class);
        conf.getStorageSize(key, "1PB", StorageUnit.GB);
        // illegal format specification, this should throw.
        conf.setStrings(key, "1HB");
        thrown.expect(IllegalArgumentException.class);
        conf.getStorageSize(key, "1PB", StorageUnit.GB);
        // Illegal number  specification, this should throw.
        conf.setStrings(key, "HadoopGB");
        thrown.expect(IllegalArgumentException.class);
        conf.getStorageSize(key, "1PB", StorageUnit.GB);
    }

    @Test
    public void testTimeDurationWarning() {
        // check warn for possible loss of precision
        final String warnFormat = "Possible loss of precision converting %s" + " to %s for test.time.warn";
        final ArrayList<String> warnchk = new ArrayList<>();
        Configuration wconf = new Configuration(false) {
            @Override
            void logDeprecation(String message) {
                warnchk.add(message);
            }
        };
        String[] convDAYS = new String[]{ "23h", "30m", "40s", "10us", "40000ms" };
        for (String s : convDAYS) {
            wconf.set("test.time.warn", s);
            Assert.assertEquals(0, wconf.getTimeDuration("test.time.warn", 1, TimeUnit.DAYS));
        }
        for (int i = 0; i < (convDAYS.length); ++i) {
            String wchk = String.format(warnFormat, convDAYS[i], "DAYS");
            Assert.assertEquals(wchk, warnchk.get(i));
        }
        warnchk.clear();
        wconf.setTimeDuration("test.time.warn", 1441, TimeUnit.MINUTES);
        Assert.assertEquals(1, wconf.getTimeDuration("test.time.warn", 0, TimeUnit.DAYS));
        Assert.assertEquals(24, wconf.getTimeDuration("test.time.warn", 0, TimeUnit.HOURS));
        String dchk = String.format(warnFormat, "1441m", "DAYS");
        Assert.assertEquals(dchk, warnchk.get(0));
        String hchk = String.format(warnFormat, "1441m", "HOURS");
        Assert.assertEquals(hchk, warnchk.get(1));
        Assert.assertEquals(1441, wconf.getTimeDuration("test.time.warn", 0, TimeUnit.MINUTES));
        // no warning
        Assert.assertEquals(2, warnchk.size());
        Assert.assertEquals(86460, wconf.getTimeDuration("test.time.warn", 0, TimeUnit.SECONDS));
        // no warning
        Assert.assertEquals(2, warnchk.size());
    }

    @Test
    public void testPattern() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.pattern1", "");
        appendProperty("test.pattern2", "(");
        appendProperty("test.pattern3", "a+b");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Pattern defaultPattern = Pattern.compile("x+");
        // Return default if missing
        Assert.assertEquals(defaultPattern.pattern(), conf.getPattern("xxxxx", defaultPattern).pattern());
        // Return null if empty and default is null
        Assert.assertNull(conf.getPattern("test.pattern1", null));
        // Return default for empty
        Assert.assertEquals(defaultPattern.pattern(), conf.getPattern("test.pattern1", defaultPattern).pattern());
        // Return default for malformed
        Assert.assertEquals(defaultPattern.pattern(), conf.getPattern("test.pattern2", defaultPattern).pattern());
        // Works for correct patterns
        Assert.assertEquals("a+b", conf.getPattern("test.pattern3", defaultPattern).pattern());
    }

    @Test
    public void testPropertySource() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.foo", "bar");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        conf.set("fs.defaultFS", "value");
        String[] sources = conf.getPropertySources("test.foo");
        Assert.assertEquals(1, sources.length);
        Assert.assertEquals(("Resource string returned for a file-loaded property" + " must be a proper absolute path"), fileResource, new Path(sources[0]));
        Assert.assertArrayEquals(("Resource string returned for a set() property must be " + "\"programmatically\""), new String[]{ "programmatically" }, conf.getPropertySources("fs.defaultFS"));
        Assert.assertArrayEquals(("Resource string returned for an unset property must " + "be null"), null, conf.getPropertySources("fs.defaultFoo"));
    }

    @Test
    public void testMultiplePropertySource() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.foo", "bar", false, "a", "b", "c");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        String[] sources = conf.getPropertySources("test.foo");
        Assert.assertEquals(4, sources.length);
        Assert.assertEquals("a", sources[0]);
        Assert.assertEquals("b", sources[1]);
        Assert.assertEquals("c", sources[2]);
        Assert.assertEquals(("Resource string returned for a file-loaded property" + " must be a proper absolute path"), fileResource, new Path(sources[3]));
    }

    @Test
    public void testSocketAddress() {
        Configuration conf = new Configuration();
        final String defaultAddr = "host:1";
        final int defaultPort = 2;
        InetSocketAddress addr = null;
        addr = conf.getSocketAddr("myAddress", defaultAddr, defaultPort);
        Assert.assertEquals(defaultAddr, NetUtils.getHostPortString(addr));
        conf.set("myAddress", "host2");
        addr = conf.getSocketAddr("myAddress", defaultAddr, defaultPort);
        Assert.assertEquals(("host2:" + defaultPort), NetUtils.getHostPortString(addr));
        conf.set("myAddress", "host2:3");
        addr = conf.getSocketAddr("myAddress", defaultAddr, defaultPort);
        Assert.assertEquals("host2:3", NetUtils.getHostPortString(addr));
        conf.set("myAddress", " \n \t    host4:5     \t \n   ");
        addr = conf.getSocketAddr("myAddress", defaultAddr, defaultPort);
        Assert.assertEquals("host4:5", NetUtils.getHostPortString(addr));
        boolean threwException = false;
        conf.set("myAddress", "bad:-port");
        try {
            addr = conf.getSocketAddr("myAddress", defaultAddr, defaultPort);
        } catch (IllegalArgumentException iae) {
            threwException = true;
            Assert.assertEquals(("Does not contain a valid host:port authority: " + "bad:-port (configuration property 'myAddress')"), iae.getMessage());
        } finally {
            Assert.assertTrue(threwException);
        }
    }

    @Test
    public void testSetSocketAddress() {
        Configuration conf = new Configuration();
        NetUtils.addStaticResolution("host", "127.0.0.1");
        final String defaultAddr = "host:1";
        InetSocketAddress addr = NetUtils.createSocketAddr(defaultAddr);
        conf.setSocketAddr("myAddress", addr);
        Assert.assertEquals(defaultAddr, NetUtils.getHostPortString(addr));
    }

    @Test
    public void testUpdateSocketAddress() throws IOException {
        InetSocketAddress addr = NetUtils.createSocketAddrForHost("host", 1);
        InetSocketAddress connectAddr = conf.updateConnectAddr("myAddress", addr);
        Assert.assertEquals(connectAddr.getHostName(), addr.getHostName());
        addr = new InetSocketAddress(1);
        connectAddr = conf.updateConnectAddr("myAddress", addr);
        Assert.assertEquals(connectAddr.getHostName(), InetAddress.getLocalHost().getHostName());
    }

    @Test
    public void testReload() throws IOException {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.key1", "final-value1", true);
        appendProperty("test.key2", "value2");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        appendProperty("test.key1", "value1");
        appendProperty("test.key3", "value3");
        endConfig();
        Path fileResource1 = new Path(TestConfiguration.CONFIG2);
        conf.addResource(fileResource1);
        // add a few values via set.
        conf.set("test.key3", "value4");
        conf.set("test.key4", "value5");
        Assert.assertEquals("final-value1", conf.get("test.key1"));
        Assert.assertEquals("value2", conf.get("test.key2"));
        Assert.assertEquals("value4", conf.get("test.key3"));
        Assert.assertEquals("value5", conf.get("test.key4"));
        // change values in the test file...
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.key1", "final-value1");
        appendProperty("test.key3", "final-value3", true);
        endConfig();
        conf.reloadConfiguration();
        Assert.assertEquals("value1", conf.get("test.key1"));
        // overlayed property overrides.
        Assert.assertEquals("value4", conf.get("test.key3"));
        Assert.assertEquals(null, conf.get("test.key2"));
        Assert.assertEquals("value5", conf.get("test.key4"));
    }

    @Test
    public void testSize() {
        Configuration conf = new Configuration(false);
        conf.set("a", "A");
        conf.set("b", "B");
        Assert.assertEquals(2, conf.size());
    }

    @Test
    public void testClear() {
        Configuration conf = new Configuration(false);
        conf.set("a", "A");
        conf.set("b", "B");
        conf.clear();
        Assert.assertEquals(0, conf.size());
        Assert.assertFalse(conf.iterator().hasNext());
    }

    public static class Fake_ClassLoader extends ClassLoader {}

    @Test
    public void testClassLoader() {
        Configuration conf = new Configuration(false);
        conf.setQuietMode(false);
        conf.setClassLoader(new TestConfiguration.Fake_ClassLoader());
        Configuration other = new Configuration(conf);
        Assert.assertTrue(((other.getClassLoader()) instanceof TestConfiguration.Fake_ClassLoader));
    }

    static class JsonConfiguration {
        TestConfiguration.JsonProperty[] properties;

        public TestConfiguration.JsonProperty[] getProperties() {
            return properties;
        }

        public void setProperties(TestConfiguration.JsonProperty[] properties) {
            this.properties = properties;
        }
    }

    static class SingleJsonConfiguration {
        private TestConfiguration.JsonProperty property;

        public TestConfiguration.JsonProperty getProperty() {
            return property;
        }

        public void setProperty(TestConfiguration.JsonProperty property) {
            this.property = property;
        }
    }

    static class JsonProperty {
        String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean getIsFinal() {
            return isFinal;
        }

        public void setIsFinal(boolean isFinal) {
            this.isFinal = isFinal;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        String value;

        boolean isFinal;

        String resource;
    }

    @Test
    public void testGetSetTrimmedNames() throws IOException {
        Configuration conf = new Configuration(false);
        conf.set(" name", "value");
        Assert.assertEquals("value", conf.get("name"));
        Assert.assertEquals("value", conf.get(" name"));
        Assert.assertEquals("value", conf.getRaw("  name  "));
    }

    @Test
    public void testDumpProperty() throws IOException {
        StringWriter outWriter = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        String xmlStr = null;
        try {
            Configuration testConf = new Configuration(false);
            out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
            startConfig();
            appendProperty("test.key1", "value1");
            appendProperty("test.key2", "value2", true);
            appendProperty("test.key3", "value3");
            endConfig();
            Path fileResource = new Path(TestConfiguration.CONFIG);
            testConf.addResource(fileResource);
            out.close();
            // case 1: dump an existing property
            // test json format
            outWriter = new StringWriter();
            Configuration.dumpConfiguration(testConf, "test.key2", outWriter);
            jsonStr = outWriter.toString();
            outWriter.close();
            mapper = new ObjectMapper();
            TestConfiguration.SingleJsonConfiguration jconf1 = mapper.readValue(jsonStr, TestConfiguration.SingleJsonConfiguration.class);
            TestConfiguration.JsonProperty jp1 = jconf1.getProperty();
            Assert.assertEquals("test.key2", jp1.getKey());
            Assert.assertEquals("value2", jp1.getValue());
            Assert.assertEquals(true, jp1.isFinal);
            Assert.assertEquals(fileResource.toString(), jp1.getResource());
            // test xml format
            outWriter = new StringWriter();
            testConf.writeXml("test.key2", outWriter);
            xmlStr = outWriter.toString();
            outWriter.close();
            Configuration actualConf1 = getActualConf(xmlStr);
            Assert.assertEquals(1, actualConf1.size());
            Assert.assertEquals("value2", actualConf1.get("test.key2"));
            Assert.assertTrue(actualConf1.getFinalParameters().contains("test.key2"));
            Assert.assertEquals(fileResource.toString(), actualConf1.getPropertySources("test.key2")[0]);
            // case 2: dump an non existing property
            // test json format
            try {
                outWriter = new StringWriter();
                Configuration.dumpConfiguration(testConf, "test.unknown.key", outWriter);
                outWriter.close();
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IllegalArgumentException));
                Assert.assertTrue(((e.getMessage().contains("test.unknown.key")) && (e.getMessage().contains("not found"))));
            }
            // test xml format
            try {
                outWriter = new StringWriter();
                testConf.writeXml("test.unknown.key", outWriter);
                outWriter.close();
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IllegalArgumentException));
                Assert.assertTrue(((e.getMessage().contains("test.unknown.key")) && (e.getMessage().contains("not found"))));
            }
            // case 3: specify a null property, ensure all configurations are dumped
            outWriter = new StringWriter();
            Configuration.dumpConfiguration(testConf, null, outWriter);
            jsonStr = outWriter.toString();
            mapper = new ObjectMapper();
            TestConfiguration.JsonConfiguration jconf3 = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
            Assert.assertEquals(3, jconf3.getProperties().length);
            outWriter = new StringWriter();
            testConf.writeXml(null, outWriter);
            xmlStr = outWriter.toString();
            outWriter.close();
            Configuration actualConf3 = getActualConf(xmlStr);
            Assert.assertEquals(3, actualConf3.size());
            Assert.assertTrue((((actualConf3.getProps().containsKey("test.key1")) && (actualConf3.getProps().containsKey("test.key2"))) && (actualConf3.getProps().containsKey("test.key3"))));
            // case 4: specify an empty property, ensure all configurations are dumped
            outWriter = new StringWriter();
            Configuration.dumpConfiguration(testConf, "", outWriter);
            jsonStr = outWriter.toString();
            mapper = new ObjectMapper();
            TestConfiguration.JsonConfiguration jconf4 = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
            Assert.assertEquals(3, jconf4.getProperties().length);
            outWriter = new StringWriter();
            testConf.writeXml("", outWriter);
            xmlStr = outWriter.toString();
            outWriter.close();
            Configuration actualConf4 = getActualConf(xmlStr);
            Assert.assertEquals(3, actualConf4.size());
            Assert.assertTrue((((actualConf4.getProps().containsKey("test.key1")) && (actualConf4.getProps().containsKey("test.key2"))) && (actualConf4.getProps().containsKey("test.key3"))));
        } finally {
            if (outWriter != null) {
                outWriter.close();
            }
            if ((out) != null) {
                out.close();
            }
        }
    }

    @Test
    public void testDumpConfiguration() throws IOException {
        StringWriter outWriter = new StringWriter();
        Configuration.dumpConfiguration(conf, outWriter);
        String jsonStr = outWriter.toString();
        ObjectMapper mapper = new ObjectMapper();
        TestConfiguration.JsonConfiguration jconf = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
        int defaultLength = jconf.getProperties().length;
        // add 3 keys to the existing configuration properties
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.key1", "value1");
        appendProperty("test.key2", "value2", true);
        appendProperty("test.key3", "value3");
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        out.close();
        outWriter = new StringWriter();
        Configuration.dumpConfiguration(conf, outWriter);
        jsonStr = outWriter.toString();
        mapper = new ObjectMapper();
        jconf = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
        int length = jconf.getProperties().length;
        // check for consistency in the number of properties parsed in Json format.
        Assert.assertEquals(length, (defaultLength + 3));
        // change few keys in another resource file
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG2));
        startConfig();
        appendProperty("test.key1", "newValue1");
        appendProperty("test.key2", "newValue2");
        endConfig();
        Path fileResource1 = new Path(TestConfiguration.CONFIG2);
        conf.addResource(fileResource1);
        out.close();
        outWriter = new StringWriter();
        Configuration.dumpConfiguration(conf, outWriter);
        jsonStr = outWriter.toString();
        mapper = new ObjectMapper();
        jconf = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
        // put the keys and their corresponding attributes into a hashmap for their
        // efficient retrieval
        HashMap<String, TestConfiguration.JsonProperty> confDump = new HashMap<String, TestConfiguration.JsonProperty>();
        for (TestConfiguration.JsonProperty prop : jconf.getProperties()) {
            confDump.put(prop.getKey(), prop);
        }
        // check if the value and resource of test.key1 is changed
        Assert.assertEquals("newValue1", confDump.get("test.key1").getValue());
        Assert.assertEquals(false, confDump.get("test.key1").getIsFinal());
        Assert.assertEquals(fileResource1.toString(), confDump.get("test.key1").getResource());
        // check if final parameter test.key2 is not changed, since it is first
        // loaded as final parameter
        Assert.assertEquals("value2", confDump.get("test.key2").getValue());
        Assert.assertEquals(true, confDump.get("test.key2").getIsFinal());
        Assert.assertEquals(fileResource.toString(), confDump.get("test.key2").getResource());
        // check for other keys which are not modified later
        Assert.assertEquals("value3", confDump.get("test.key3").getValue());
        Assert.assertEquals(false, confDump.get("test.key3").getIsFinal());
        Assert.assertEquals(fileResource.toString(), confDump.get("test.key3").getResource());
        // check for resource to be "Unknown" for keys which are loaded using 'set'
        // and expansion of properties
        conf.set("test.key4", "value4");
        conf.set("test.key5", "value5");
        conf.set("test.key6", "${test.key5}");
        outWriter = new StringWriter();
        Configuration.dumpConfiguration(conf, outWriter);
        jsonStr = outWriter.toString();
        mapper = new ObjectMapper();
        jconf = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
        confDump = new HashMap<String, TestConfiguration.JsonProperty>();
        for (TestConfiguration.JsonProperty prop : jconf.getProperties()) {
            confDump.put(prop.getKey(), prop);
        }
        Assert.assertEquals("value5", confDump.get("test.key6").getValue());
        Assert.assertEquals("programmatically", confDump.get("test.key4").getResource());
        outWriter.close();
    }

    @Test
    public void testDumpConfiguratioWithoutDefaults() throws IOException {
        // check for case when default resources are not loaded
        Configuration config = new Configuration(false);
        StringWriter outWriter = new StringWriter();
        Configuration.dumpConfiguration(config, outWriter);
        String jsonStr = outWriter.toString();
        ObjectMapper mapper = new ObjectMapper();
        TestConfiguration.JsonConfiguration jconf = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
        // ensure that no properties are loaded.
        Assert.assertEquals(0, jconf.getProperties().length);
        // add 2 keys
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.key1", "value1");
        appendProperty("test.key2", "value2", true);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        config.addResource(fileResource);
        out.close();
        outWriter = new StringWriter();
        Configuration.dumpConfiguration(config, outWriter);
        jsonStr = outWriter.toString();
        mapper = new ObjectMapper();
        jconf = mapper.readValue(jsonStr, TestConfiguration.JsonConfiguration.class);
        HashMap<String, TestConfiguration.JsonProperty> confDump = new HashMap<String, TestConfiguration.JsonProperty>();
        for (TestConfiguration.JsonProperty prop : jconf.getProperties()) {
            confDump.put(prop.getKey(), prop);
        }
        // ensure only 2 keys are loaded
        Assert.assertEquals(2, jconf.getProperties().length);
        // ensure the values are consistent
        Assert.assertEquals(confDump.get("test.key1").getValue(), "value1");
        Assert.assertEquals(confDump.get("test.key2").getValue(), "value2");
        // check the final tag
        Assert.assertEquals(false, confDump.get("test.key1").getIsFinal());
        Assert.assertEquals(true, confDump.get("test.key2").getIsFinal());
        // check the resource for each property
        for (TestConfiguration.JsonProperty prop : jconf.getProperties()) {
            Assert.assertEquals(fileResource.toString(), prop.getResource());
        }
    }

    @Test
    public void testDumpSensitiveProperty() throws IOException {
        final String myPassword = "ThisIsMyPassword";
        Configuration testConf = new Configuration(false);
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.password", myPassword);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        testConf.addResource(fileResource);
        try (StringWriter outWriter = new StringWriter()) {
            testConf.set(TestConfiguration.SENSITIVE_CONFIG_KEYS, "password$");
            Configuration.dumpConfiguration(testConf, "test.password", outWriter);
            Assert.assertFalse(outWriter.toString().contains(myPassword));
        }
    }

    @Test
    public void testDumpSensitiveConfiguration() throws IOException {
        final String myPassword = "ThisIsMyPassword";
        Configuration testConf = new Configuration(false);
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("test.password", myPassword);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        testConf.addResource(fileResource);
        try (StringWriter outWriter = new StringWriter()) {
            testConf.set(TestConfiguration.SENSITIVE_CONFIG_KEYS, "password$");
            Configuration.dumpConfiguration(testConf, outWriter);
            Assert.assertFalse(outWriter.toString().contains(myPassword));
        }
    }

    @Test
    public void testGetValByRegex() {
        Configuration conf = new Configuration();
        String key1 = "t.abc.key1";
        String key2 = "t.abc.key2";
        String key3 = "tt.abc.key3";
        String key4 = "t.abc.ey3";
        conf.set(key1, "value1");
        conf.set(key2, "value2");
        conf.set(key3, "value3");
        conf.set(key4, "value3");
        Map<String, String> res = conf.getValByRegex("^t\\..*\\.key\\d");
        Assert.assertTrue(("Conf didn't get key " + key1), res.containsKey(key1));
        Assert.assertTrue(("Conf didn't get key " + key2), res.containsKey(key2));
        Assert.assertTrue(("Picked out wrong key " + key3), (!(res.containsKey(key3))));
        Assert.assertTrue(("Picked out wrong key " + key4), (!(res.containsKey(key4))));
    }

    @Test
    public void testGetClassesShouldReturnDefaultValue() throws Exception {
        Configuration config = new Configuration();
        Class<?>[] classes = config.getClasses("testClassName", Configuration.class);
        Assert.assertEquals(("Not returning expected number of classes. Number of returned classes =" + (classes.length)), 1, classes.length);
        Assert.assertEquals("Not returning the default class Name", Configuration.class, classes[0]);
    }

    @Test
    public void testGetClassesShouldReturnEmptyArray() throws Exception {
        Configuration config = new Configuration();
        config.set("testClassName", "");
        Class<?>[] classes = config.getClasses("testClassName", Configuration.class);
        Assert.assertEquals(("Not returning expected number of classes. Number of returned classes =" + (classes.length)), 0, classes.length);
    }

    @Test
    public void testSettingValueNull() throws Exception {
        Configuration config = new Configuration();
        try {
            config.set("testClassName", null);
            Assert.fail("Should throw an IllegalArgumentException exception ");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
            Assert.assertEquals(e.getMessage(), "The value of property testClassName must not be null");
        }
    }

    @Test
    public void testSettingKeyNull() throws Exception {
        Configuration config = new Configuration();
        try {
            config.set(null, "test");
            Assert.fail("Should throw an IllegalArgumentException exception ");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
            Assert.assertEquals(e.getMessage(), "Property name must not be null");
        }
    }

    @Test
    public void testInvalidSubstitution() {
        final Configuration configuration = new Configuration(false);
        // 2-var loops
        // 
        final String key = "test.random.key";
        for (String keyExpression : Arrays.asList((("${" + key) + "}"), (("foo${" + key) + "}"), (("foo${" + key) + "}bar"), (("${" + key) + "}bar"))) {
            configuration.set(key, keyExpression);
            Assert.assertEquals("Unexpected value", keyExpression, configuration.get(key));
        }
    }

    @Test
    public void testIncompleteSubbing() {
        Configuration configuration = new Configuration(false);
        String key = "test.random.key";
        for (String keyExpression : Arrays.asList("{}", "${}", ("{" + key), ("${" + key), ("foo${" + key), (("foo${" + key) + "bar"), (("foo{" + key) + "}bar"), (("${" + key) + "bar"))) {
            configuration.set(key, keyExpression);
            String value = configuration.get(key);
            Assert.assertTrue(("Unexpected value " + value), value.equals(keyExpression));
        }
    }

    @Test
    public void testBoolean() {
        boolean value = true;
        Configuration configuration = new Configuration();
        configuration.setBoolean("value", value);
        Assert.assertEquals(value, configuration.getBoolean("value", false));
    }

    @Test
    public void testBooleanIfUnset() {
        boolean value = true;
        Configuration configuration = new Configuration();
        configuration.setBooleanIfUnset("value", value);
        Assert.assertEquals(value, configuration.getBoolean("value", false));
        configuration.setBooleanIfUnset("value", false);
        Assert.assertEquals(value, configuration.getBoolean("value", false));
    }

    @Test
    public void testFloat() {
        float value = 1.0F;
        Configuration configuration = new Configuration();
        configuration.setFloat("value", value);
        Assert.assertEquals(value, configuration.getFloat("value", 0.0F), TestConfiguration.DOUBLE_DELTA);
    }

    @Test
    public void testDouble() {
        double value = 1.0;
        Configuration configuration = new Configuration();
        configuration.setDouble("value", value);
        Assert.assertEquals(value, configuration.getDouble("value", 0.0), TestConfiguration.DOUBLE_DELTA);
    }

    @Test
    public void testInt() {
        int value = 1;
        Configuration configuration = new Configuration();
        configuration.setInt("value", value);
        Assert.assertEquals(value, configuration.getInt("value", 0));
    }

    @Test
    public void testLong() {
        long value = 1L;
        Configuration configuration = new Configuration();
        configuration.setLong("value", value);
        Assert.assertEquals(value, configuration.getLong("value", 0L));
    }

    @Test
    public void testStrings() {
        String[] strings = new String[]{ "FOO", "BAR" };
        Configuration configuration = new Configuration();
        configuration.setStrings("strings", strings);
        String[] returnStrings = configuration.getStrings("strings");
        for (int i = 0; i < (returnStrings.length); i++) {
            Assert.assertEquals(strings[i], returnStrings[i]);
        }
    }

    @Test
    public void testSetPattern() {
        Pattern testPattern = Pattern.compile("a+b");
        Configuration configuration = new Configuration();
        configuration.setPattern("testPattern", testPattern);
        Assert.assertEquals(testPattern.pattern(), configuration.getPattern("testPattern", Pattern.compile("")).pattern());
    }

    @Test
    public void testGetClassByNameOrNull() throws Exception {
        Configuration config = new Configuration();
        Class<?> clazz = config.getClassByNameOrNull("java.lang.Object");
        Assert.assertNotNull(clazz);
    }

    @Test
    public void testGetFinalParameters() throws Exception {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        declareProperty("my.var", "x", "x", true);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        Configuration conf = new Configuration();
        Set<String> finalParameters = conf.getFinalParameters();
        Assert.assertFalse("my.var already exists", finalParameters.contains("my.var"));
        conf.addResource(fileResource);
        Assert.assertEquals("my.var is undefined", "x", conf.get("my.var"));
        Assert.assertFalse("finalparams not copied", finalParameters.contains("my.var"));
        finalParameters = conf.getFinalParameters();
        Assert.assertTrue("my.var is not final", finalParameters.contains("my.var"));
    }

    /**
     * A test to check whether this thread goes into infinite loop because of
     * destruction of data structure by resize of Map. This problem was reported
     * by SPARK-2546.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConcurrentAccesses() throws Exception {
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        declareProperty("some.config", "xyz", "xyz", false);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        Configuration conf = new Configuration();
        conf.addResource(fileResource);
        class ConfigModifyThread extends Thread {
            private final Configuration config;

            private final String prefix;

            public ConfigModifyThread(Configuration conf, String prefix) {
                config = conf;
                this.prefix = prefix;
            }

            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    config.set((("some.config.value-" + (prefix)) + i), "value");
                }
            }
        }
        ArrayList<ConfigModifyThread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threads.add(new ConfigModifyThread(conf, String.valueOf(i)));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    @Test
    public void testNullValueProperties() throws Exception {
        Configuration conf = new Configuration();
        conf.setAllowNullValueProperties(true);
        out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG));
        startConfig();
        appendProperty("attr", "value", true);
        appendProperty("attr", "", true);
        endConfig();
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        Assert.assertEquals("value", conf.get("attr"));
    }

    @Test
    public void testGetPasswordDeprecatedKeyStored() throws Exception {
        final String oldKey = "test.password.old.key";
        final String newKey = "test.password.new.key";
        final String password = "MyPasswordForDeprecatedKey";
        final File tmpDir = GenericTestUtils.getRandomizedTestDir();
        tmpDir.mkdirs();
        final String ourUrl = new URI(LocalJavaKeyStoreProvider.SCHEME_NAME, "file", new File(tmpDir, "test.jks").toURI().getPath(), null).toString();
        conf = new Configuration(false);
        conf.set(CREDENTIAL_PROVIDER_PATH, ourUrl);
        CredentialProvider provider = CredentialProviderFactory.getProviders(conf).get(0);
        provider.createCredentialEntry(oldKey, password.toCharArray());
        provider.flush();
        Configuration.addDeprecation(oldKey, newKey);
        Assert.assertThat(conf.getPassword(newKey), CoreMatchers.is(password.toCharArray()));
        Assert.assertThat(conf.getPassword(oldKey), CoreMatchers.is(password.toCharArray()));
        FileUtil.fullyDelete(tmpDir);
    }

    @Test
    public void testGetPasswordByDeprecatedKey() throws Exception {
        final String oldKey = "test.password.old.key";
        final String newKey = "test.password.new.key";
        final String password = "MyPasswordForDeprecatedKey";
        final File tmpDir = GenericTestUtils.getRandomizedTestDir();
        tmpDir.mkdirs();
        final String ourUrl = new URI(LocalJavaKeyStoreProvider.SCHEME_NAME, "file", new File(tmpDir, "test.jks").toURI().getPath(), null).toString();
        conf = new Configuration(false);
        conf.set(CREDENTIAL_PROVIDER_PATH, ourUrl);
        CredentialProvider provider = CredentialProviderFactory.getProviders(conf).get(0);
        provider.createCredentialEntry(newKey, password.toCharArray());
        provider.flush();
        Configuration.addDeprecation(oldKey, newKey);
        Assert.assertThat(conf.getPassword(newKey), CoreMatchers.is(password.toCharArray()));
        Assert.assertThat(conf.getPassword(oldKey), CoreMatchers.is(password.toCharArray()));
        FileUtil.fullyDelete(tmpDir);
    }

    @Test
    public void testGettingPropertiesWithPrefix() throws Exception {
        Configuration conf = new Configuration();
        for (int i = 0; i < 10; i++) {
            conf.set((("prefix." + "name") + i), ("value" + i));
        }
        conf.set(("different.prefix" + ".name"), "value");
        Map<String, String> prefixedProps = conf.getPropsWithPrefix("prefix.");
        Assert.assertEquals(prefixedProps.size(), 10);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(("value" + i), prefixedProps.get(("name" + i)));
        }
        // Repeat test with variable substitution
        conf.set("foo", "bar");
        for (int i = 0; i < 10; i++) {
            conf.set((("subprefix." + "subname") + i), ("value_${foo}" + i));
        }
        prefixedProps = conf.getPropsWithPrefix("subprefix.");
        Assert.assertEquals(prefixedProps.size(), 10);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(("value_bar" + i), prefixedProps.get(("subname" + i)));
        }
        // test call with no properties for a given prefix
        prefixedProps = conf.getPropsWithPrefix("none");
        Assert.assertNotNull(prefixedProps.isEmpty());
        Assert.assertTrue(prefixedProps.isEmpty());
    }

    @Test
    public void testGetAllPropertiesByTags() throws Exception {
        try {
            out = new BufferedWriter(new FileWriter(TestConfiguration.CONFIG_CORE));
            startConfig();
            appendProperty("hadoop.tags.system", "YARN,HDFS,NAMENODE");
            appendProperty("hadoop.tags.custom", "MYCUSTOMTAG");
            appendPropertyByTag("dfs.cblock.trace.io", "false", "YARN");
            appendPropertyByTag("dfs.replication", "1", "HDFS");
            appendPropertyByTag("dfs.namenode.logging.level", "INFO", "NAMENODE");
            appendPropertyByTag("dfs.random.key", "XYZ", "MYCUSTOMTAG");
            endConfig();
            Path fileResource = new Path(TestConfiguration.CONFIG_CORE);
            conf.addResource(fileResource);
            conf.getProps();
        } finally {
            out.close();
        }
        System.out.println(Files.readAllLines(Paths.get(TestConfiguration.CONFIG_CORE)));
        List<String> tagList = new ArrayList<>();
        tagList.add("YARN");
        tagList.add("HDFS");
        tagList.add("NAMENODE");
        Properties properties = conf.getAllPropertiesByTags(tagList);
        String[] sources = conf.getPropertySources("dfs.replication");
        Assert.assertTrue(((sources.length) == 1));
        Assert.assertTrue(Arrays.toString(sources).contains("core-site.xml"));
        TestConfiguration.assertEq(3, properties.size());
        TestConfiguration.assertEq(true, properties.containsKey("dfs.namenode.logging.level"));
        TestConfiguration.assertEq(true, properties.containsKey("dfs.replication"));
        TestConfiguration.assertEq(true, properties.containsKey("dfs.cblock.trace.io"));
        TestConfiguration.assertEq(false, properties.containsKey("namenode.host"));
        properties = conf.getAllPropertiesByTag("DEBUG");
        TestConfiguration.assertEq(0, properties.size());
        TestConfiguration.assertEq(false, properties.containsKey("dfs.namenode.logging.level"));
        TestConfiguration.assertEq(true, conf.isPropertyTag("YARN"));
        TestConfiguration.assertEq(true, conf.isPropertyTag("HDFS"));
        TestConfiguration.assertEq(true, conf.isPropertyTag("NAMENODE"));
        TestConfiguration.assertEq(true, conf.isPropertyTag("MYCUSTOMTAG"));
        TestConfiguration.assertEq(false, conf.isPropertyTag("CMYCUSTOMTAG2"));
    }

    @Test
    public void testInvalidTags() throws Exception {
        Path fileResource = new Path(TestConfiguration.CONFIG);
        conf.addResource(fileResource);
        conf.getProps();
        Assert.assertFalse(conf.isPropertyTag("BADTAG"));
        Assert.assertFalse(conf.isPropertyTag("CUSTOM_TAG"));
        Assert.assertTrue(conf.isPropertyTag("DEBUG"));
        Assert.assertTrue(conf.isPropertyTag("HDFS"));
    }

    /**
     * Test race conditions between clone() and getProps().
     * Test for race conditions in the way Hadoop handles the Configuration
     * class. The scenario is the following. Let's assume that there are two
     * threads sharing the same Configuration class. One adds some resources
     * to the configuration, while the other one clones it. Resources are
     * loaded lazily in a deferred call to loadResources(). If the cloning
     * happens after adding the resources but before parsing them, some temporary
     * resources like input stream pointers are cloned. Eventually both copies
     * will load the same input stream resources.
     * One parses the input stream XML and closes it updating it's own copy of
     * the resource. The other one has another pointer to the same input stream.
     * When it tries to load it, it will crash with a stream closed exception.
     */
    @Test
    public void testResourceRace() {
        InputStream is = new BufferedInputStream(new ByteArrayInputStream("<configuration></configuration>".getBytes()));
        Configuration config = new Configuration();
        // Thread 1
        config.addResource(is);
        // Thread 2
        Configuration confClone = new Configuration(conf);
        // Thread 2
        confClone.get("firstParse");
        // Thread 1
        config.get("secondParse");
    }
}

