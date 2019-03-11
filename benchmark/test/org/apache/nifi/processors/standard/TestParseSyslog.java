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


import ParseSyslog.REL_FAILURE;
import ParseSyslog.REL_SUCCESS;
import SyslogAttributes.SYSLOG_BODY;
import SyslogAttributes.SYSLOG_FACILITY;
import SyslogAttributes.SYSLOG_HOSTNAME;
import SyslogAttributes.SYSLOG_PRIORITY;
import SyslogAttributes.SYSLOG_SEVERITY;
import SyslogAttributes.SYSLOG_TIMESTAMP;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestParseSyslog {
    static final String PRI = "34";

    static final String SEV = "2";

    static final String FAC = "4";

    static final String TIME = "Oct 13 15:43:23";

    static final String HOST = "localhost.home";

    static final String IPV6SRC = "fe80::216:3300:eeaa:eeaa";

    static final String IPV4SRC = "8.8.4.4";

    static final String BODY = "some message";

    static final String VALID_MESSAGE_RFC3164_0 = ((((((("<" + (TestParseSyslog.PRI)) + ">") + (TestParseSyslog.TIME)) + " ") + (TestParseSyslog.HOST)) + " ") + (TestParseSyslog.BODY)) + "\n";

    static final String VALID_MESSAGE_RFC3164_1 = ((((((("<" + (TestParseSyslog.PRI)) + ">") + (TestParseSyslog.TIME)) + " ") + (TestParseSyslog.IPV6SRC)) + " ") + (TestParseSyslog.BODY)) + "\n";

    static final String VALID_MESSAGE_RFC3164_2 = ((((((("<" + (TestParseSyslog.PRI)) + ">") + (TestParseSyslog.TIME)) + " ") + (TestParseSyslog.IPV4SRC)) + " ") + (TestParseSyslog.BODY)) + "\n";

    @Test
    public void testSuccessfulParse3164() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog());
        runner.enqueue(TestParseSyslog.VALID_MESSAGE_RFC3164_0.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertAttributeEquals(SYSLOG_BODY.key(), TestParseSyslog.BODY);
        mff.assertAttributeEquals(SYSLOG_FACILITY.key(), TestParseSyslog.FAC);
        mff.assertAttributeEquals(SYSLOG_HOSTNAME.key(), TestParseSyslog.HOST);
        mff.assertAttributeEquals(SYSLOG_PRIORITY.key(), TestParseSyslog.PRI);
        mff.assertAttributeEquals(SYSLOG_SEVERITY.key(), TestParseSyslog.SEV);
        mff.assertAttributeEquals(SYSLOG_TIMESTAMP.key(), TestParseSyslog.TIME);
    }

    @Test
    public void testValidIPv6Source() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog());
        runner.enqueue(TestParseSyslog.VALID_MESSAGE_RFC3164_1.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertAttributeEquals(SYSLOG_BODY.key(), TestParseSyslog.BODY);
        mff.assertAttributeEquals(SYSLOG_FACILITY.key(), TestParseSyslog.FAC);
        mff.assertAttributeEquals(SYSLOG_HOSTNAME.key(), TestParseSyslog.IPV6SRC);
        mff.assertAttributeEquals(SYSLOG_PRIORITY.key(), TestParseSyslog.PRI);
        mff.assertAttributeEquals(SYSLOG_SEVERITY.key(), TestParseSyslog.SEV);
        mff.assertAttributeEquals(SYSLOG_TIMESTAMP.key(), TestParseSyslog.TIME);
    }

    @Test
    public void testValidIPv4Source() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog());
        runner.enqueue(TestParseSyslog.VALID_MESSAGE_RFC3164_2.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertAttributeEquals(SYSLOG_BODY.key(), TestParseSyslog.BODY);
        mff.assertAttributeEquals(SYSLOG_FACILITY.key(), TestParseSyslog.FAC);
        mff.assertAttributeEquals(SYSLOG_HOSTNAME.key(), TestParseSyslog.IPV4SRC);
        mff.assertAttributeEquals(SYSLOG_PRIORITY.key(), TestParseSyslog.PRI);
        mff.assertAttributeEquals(SYSLOG_SEVERITY.key(), TestParseSyslog.SEV);
        mff.assertAttributeEquals(SYSLOG_TIMESTAMP.key(), TestParseSyslog.TIME);
    }

    @Test
    public void testInvalidMessage() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseSyslog());
        runner.enqueue("<hello> yesterday localhost\n".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }
}

