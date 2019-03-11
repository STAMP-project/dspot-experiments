/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.realtime.firehose;


import AllowAllAuthenticator.ALLOW_ALL_RESULT;
import AuthConfig.DRUID_ALLOW_UNSECURED_PATH;
import AuthConfig.DRUID_AUTHENTICATION_RESULT;
import AuthConfig.DRUID_AUTHORIZATION_CHECKED;
import EventReceiverFirehoseFactory.EventReceiverFirehose;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.druid.server.metrics.EventReceiverFirehoseRegister;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class EventReceiverFirehoseIdleTest {
    private static final int CAPACITY = 300;

    private static final long MAX_IDLE_TIME = 5000L;

    private static final String SERVICE_NAME = "test_firehose";

    private final String inputRow = "[{\n" + (("  \"timestamp\":123,\n" + "  \"d1\":\"v1\"\n") + "}]");

    private EventReceiverFirehoseFactory eventReceiverFirehoseFactory;

    private EventReceiverFirehose firehose;

    private EventReceiverFirehoseRegister register = new EventReceiverFirehoseRegister();

    private HttpServletRequest req;

    @Test(timeout = 40000L)
    public void testIdle() throws Exception {
        awaitFirehoseClosed();
        awaitDelayedExecutorThreadTerminated();
    }

    @Test(timeout = 40000L)
    public void testNotIdle() throws Exception {
        EasyMock.expect(req.getAttribute(DRUID_AUTHORIZATION_CHECKED)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_ALLOW_UNSECURED_PATH)).andReturn(null).anyTimes();
        EasyMock.expect(req.getAttribute(DRUID_AUTHENTICATION_RESULT)).andReturn(ALLOW_ALL_RESULT).anyTimes();
        EasyMock.expect(req.getHeader("X-Firehose-Producer-Id")).andReturn(null).anyTimes();
        EasyMock.expect(req.getContentType()).andReturn("application/json").anyTimes();
        req.setAttribute(DRUID_AUTHORIZATION_CHECKED, true);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(req);
        final int checks = 5;
        for (int i = 0; i < checks; i++) {
            Assert.assertFalse(firehose.isClosed());
            System.out.printf(Locale.ENGLISH, "Check %d/%d passed\n", (i + 1), checks);
            firehose.addAll(IOUtils.toInputStream(inputRow), req);
            Thread.sleep(3000L);
        }
        awaitFirehoseClosed();
        awaitDelayedExecutorThreadTerminated();
    }
}

