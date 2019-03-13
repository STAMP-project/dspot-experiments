/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package io.aeron.driver;


import CommonContext.IPC_CHANNEL;
import io.aeron.DriverProxy;
import org.agrona.concurrent.status.Position;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static Configuration.TERM_BUFFER_LENGTH_DEFAULT;


public class IpcPublicationTest {
    private static final long CLIENT_ID = 7L;

    private static final int STREAM_ID = 10;

    private static final int TERM_BUFFER_LENGTH = TERM_BUFFER_LENGTH_DEFAULT;

    private static final int BUFFER_LENGTH = 16 * 1024;

    private Position publisherLimit;

    private IpcPublication ipcPublication;

    private DriverProxy driverProxy;

    private DriverConductor driverConductor;

    private long currentTime = 0;

    private final NanoClock nanoClock = () -> currentTime;

    @Test
    public void shouldStartWithPublisherLimitSetToZero() {
        MatcherAssert.assertThat(publisherLimit.get(), Matchers.is(0L));
    }

    @Test
    public void shouldKeepPublisherLimitZeroOnNoSubscriptionUpdate() {
        ipcPublication.updatePublisherLimit();
        MatcherAssert.assertThat(publisherLimit.get(), Matchers.is(0L));
    }

    @Test
    public void shouldHaveJoiningPositionZeroWhenNoSubscriptions() {
        MatcherAssert.assertThat(ipcPublication.joinPosition(), Matchers.is(0L));
    }

    @Test
    public void shouldIncrementPublisherLimitOnSubscription() {
        driverProxy.addSubscription(IPC_CHANNEL, IpcPublicationTest.STREAM_ID);
        driverConductor.doWork();
        MatcherAssert.assertThat(publisherLimit.get(), Matchers.is(Matchers.greaterThan(0L)));
    }
}

