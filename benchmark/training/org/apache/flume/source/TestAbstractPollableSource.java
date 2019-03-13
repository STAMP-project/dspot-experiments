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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.source;


import PollableSourceConstants.BACKOFF_SLEEP_INCREMENT;
import PollableSourceConstants.DEFAULT_BACKOFF_SLEEP_INCREMENT;
import PollableSourceConstants.DEFAULT_MAX_BACKOFF_SLEEP;
import PollableSourceConstants.MAX_BACKOFF_SLEEP;
import java.util.HashMap;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static PollableSourceConstants.DEFAULT_BACKOFF_SLEEP_INCREMENT;
import static PollableSourceConstants.DEFAULT_MAX_BACKOFF_SLEEP;
import static Status.BACKOFF;


public class TestAbstractPollableSource {
    private AbstractPollableSource source;

    @Test(expected = FlumeException.class)
    public void testExceptionStartup() throws Exception {
        source.configure(new Context());
    }

    @Test(expected = EventDeliveryException.class)
    public void testNotStarted() throws Exception {
        source.process();
    }

    @Test
    public void voidBackOffConfig() {
        source = Mockito.spy(new AbstractPollableSource() {
            @Override
            protected Status doProcess() throws EventDeliveryException {
                return BACKOFF;
            }

            @Override
            protected void doConfigure(Context context) throws FlumeException {
            }

            @Override
            protected void doStart() throws FlumeException {
            }

            @Override
            protected void doStop() throws FlumeException {
            }
        });
        HashMap<String, String> inputConfigs = new HashMap<String, String>();
        inputConfigs.put(BACKOFF_SLEEP_INCREMENT, "42");
        inputConfigs.put(MAX_BACKOFF_SLEEP, "4242");
        Context context = new Context(inputConfigs);
        source.configure(context);
        Assert.assertEquals(("BackOffSleepIncrement should equal 42 but it equals " + (source.getBackOffSleepIncrement())), 42L, source.getBackOffSleepIncrement());
        Assert.assertEquals(("BackOffSleepIncrement should equal 42 but it equals " + (source.getMaxBackOffSleepInterval())), 4242L, source.getMaxBackOffSleepInterval());
    }

    @Test
    public void voidBackOffConfigDefaults() {
        source = Mockito.spy(new AbstractPollableSource() {
            @Override
            protected Status doProcess() throws EventDeliveryException {
                return Status.BACKOFF;
            }

            @Override
            protected void doConfigure(Context context) throws FlumeException {
            }

            @Override
            protected void doStart() throws FlumeException {
            }

            @Override
            protected void doStop() throws FlumeException {
            }
        });
        HashMap<String, String> inputConfigs = new HashMap<String, String>();
        Assert.assertEquals(((("BackOffSleepIncrement should equal " + (DEFAULT_BACKOFF_SLEEP_INCREMENT)) + " but it equals ") + (source.getBackOffSleepIncrement())), DEFAULT_BACKOFF_SLEEP_INCREMENT, source.getBackOffSleepIncrement());
        Assert.assertEquals(((("BackOffSleepIncrement should equal " + (DEFAULT_MAX_BACKOFF_SLEEP)) + " but it equals ") + (source.getMaxBackOffSleepInterval())), DEFAULT_MAX_BACKOFF_SLEEP, source.getMaxBackOffSleepInterval());
    }
}

