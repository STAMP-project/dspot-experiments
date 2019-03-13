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


import NilPolicy.DASH;
import NilPolicy.OMIT;
import ParseSyslog5424.INCLUDE_BODY_IN_ATTRIBUTES;
import ParseSyslog5424.NIL_POLICY;
import ParseSyslog5424.REL_FAILURE;
import ParseSyslog5424.REL_SUCCESS;
import SyslogAttributes.SYSLOG_BODY;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestParseSyslog5424 {
    private static final String SYSLOG_LINE_ALL = "<14>1 2014-06-20T09:14:07+00:00 loggregator" + ((" d0602076-b14a-4c55-852a-981e7afeed38 DEA MSG-01" + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]") + " [exampleSDID@32480 iut=\"4\" eventSource=\"Other Application\" eventID=\"2022\"] Removing instance");

    private static final String SYSLOG_LINE_NILS = "<14>1 2014-06-20T09:14:07+00:00 -" + ((" d0602076-b14a-4c55-852a-981e7afeed38 - -" + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]") + " [exampleSDID@32480 iut=\"4\" eventSource=\"Other Application\" eventID=\"2022\"] Removing instance");

    @Test
    public void testValidMessage() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog5424());
        runner.setProperty(NIL_POLICY, DASH.name());
        runner.enqueue(TestParseSyslog5424.SYSLOG_LINE_ALL.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testValidMessageWithNils() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog5424());
        runner.setProperty(NIL_POLICY, DASH.name());
        runner.enqueue(TestParseSyslog5424.SYSLOG_LINE_NILS.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testInvalidMessage() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog5424());
        runner.setProperty(NIL_POLICY, OMIT.name());
        runner.enqueue("<hello> yesterday localhost\n".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testDefaultHasBodyAttribute() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog5424());
        runner.setProperty(NIL_POLICY, DASH.name());
        runner.enqueue(TestParseSyslog5424.SYSLOG_LINE_NILS.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertNotNull(results.get(0).getAttribute(SYSLOG_BODY.key()));
    }

    @Test
    public void testIncludeBodyAttributeTrue() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog5424());
        runner.setProperty(NIL_POLICY, DASH.name());
        runner.setProperty(INCLUDE_BODY_IN_ATTRIBUTES, "true");
        runner.enqueue(TestParseSyslog5424.SYSLOG_LINE_NILS.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertNotNull(results.get(0).getAttribute(SYSLOG_BODY.key()));
    }

    @Test
    public void testIncludeBodyAttributeFalse() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog5424());
        runner.setProperty(NIL_POLICY, DASH.name());
        runner.setProperty(INCLUDE_BODY_IN_ATTRIBUTES, "false");
        runner.enqueue(TestParseSyslog5424.SYSLOG_LINE_NILS.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertNull(results.get(0).getAttribute(SYSLOG_BODY.key()));
    }
}

