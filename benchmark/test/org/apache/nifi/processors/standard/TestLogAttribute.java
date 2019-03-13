/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import LogAttribute.ATTRIBUTES_TO_IGNORE_CSV;
import LogAttribute.ATTRIBUTES_TO_IGNORE_REGEX;
import LogAttribute.ATTRIBUTES_TO_LOG_CSV;
import LogAttribute.ATTRIBUTES_TO_LOG_REGEX;
import LogAttribute.DebugLevels.info;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.util.MockComponentLog;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.Test;


public class TestLogAttribute {
    @Test
    public void testLogPropertyCSVNoIgnore() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        runner.setProperty(ATTRIBUTES_TO_LOG_CSV, "foo, bar");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foobaz-value")));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foo-value"));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("bar-value"));
    }

    @Test
    public void testLogPropertyRegexNoIgnore() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        runner.setProperty(ATTRIBUTES_TO_LOG_REGEX, "foo.*");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foobaz-value"));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foo-value"));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("bar-value")));
    }

    @Test
    public void testLogPropertyWithCSVAndRegexNoIgnore() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        // there's an AND relationship between like properties, so only foo should be logged in this case
        runner.setProperty(ATTRIBUTES_TO_LOG_CSV, "foo, bar");
        runner.setProperty(ATTRIBUTES_TO_LOG_REGEX, "foo*");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foobaz-value")));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foo-value"));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("bar-value")));
    }

    @Test
    public void testLogPropertyWithIgnoreCSV() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        runner.setProperty(ATTRIBUTES_TO_IGNORE_CSV, "bar");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foobaz-value"));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foo-value"));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("bar-value")));
    }

    @Test
    public void testLogPropertyWithIgnoreRegex() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        runner.setProperty(ATTRIBUTES_TO_IGNORE_REGEX, "foo.*");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foobaz-value")));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foo-value")));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("bar-value"));
    }

    @Test
    public void testLogPropertyWithIgnoreCSVAndRegex() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        // there's an OR relationship between like properties, so anything starting with foo or bar are removed. that's everything we're adding
        runner.setProperty(ATTRIBUTES_TO_IGNORE_CSV, "foo,bar");
        runner.setProperty(ATTRIBUTES_TO_IGNORE_REGEX, "foo.*");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foobaz-value")));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foo-value")));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("bar-value")));
    }

    @Test
    public void testLogPropertyCSVWithIgnoreRegex() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        // we're saying add and remove the same properties, so the net result should be nothing
        runner.setProperty(ATTRIBUTES_TO_LOG_CSV, "foo");
        runner.setProperty(ATTRIBUTES_TO_IGNORE_REGEX, "foo.*");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foobaz-value")));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foo-value")));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("bar-value")));
    }

    @Test
    public void testLogPropertyCSVWithIgnoreCSV() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        // add foo,foobaz and remove foobaz
        runner.setProperty(ATTRIBUTES_TO_LOG_CSV, "foo,foobaz");
        runner.setProperty(ATTRIBUTES_TO_IGNORE_CSV, "foobaz");
        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foobaz-value")));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foo-value"));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("bar-value")));
    }

    @Test
    public void testLogPropertyRegexWithIgnoreRegex() {
        final LogAttribute logAttribute = new LogAttribute();
        final TestRunner runner = TestRunners.newTestRunner(logAttribute);
        final ProcessContext context = runner.getProcessContext();
        final ProcessSession session = runner.getProcessSessionFactory().createSession();
        final MockComponentLog LOG = runner.getLogger();
        runner.setProperty(ATTRIBUTES_TO_LOG_REGEX, "foo.*");// includes foo,foobaz

        runner.setProperty(ATTRIBUTES_TO_IGNORE_REGEX, "foobaz.*");// includes foobaz

        final Map<String, String> attrs = Maps.newHashMap();
        attrs.put("foo", "foo-value");
        attrs.put("bar", "bar-value");
        attrs.put("foobaz", "foobaz-value");
        final MockFlowFile flowFile = runner.enqueue("content", attrs);
        final String logMessage = logAttribute.processFlowFile(LOG, info, flowFile, session, context);
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("foobaz-value")));
        MatcherAssert.assertThat(logMessage, CoreMatchers.containsString("foo-value"));
        MatcherAssert.assertThat(logMessage, IsNot.not(CoreMatchers.containsString("bar-value")));
    }
}

