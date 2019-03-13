/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bt.tracker.udp;


import EventType.START;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class UdpMessageWorkerTest {
    @Rule
    public SingleClientUdpTracker tracker;

    @Rule
    public UdpTrackerConnection connection;

    @Rule
    public UdpTrackerTestExecutor client;

    private volatile int interval;

    private volatile int leechers;

    private volatile int seeders;

    @Test
    public void testAnnounce() throws Exception {
        AnnounceRequest request = createAnnounceRequest(START);
        client.execute(() -> connection.getWorker().sendMessage(request, AnnounceResponseHandler.handler()), ( response) -> {
            Assert.assertFalse(response.getError().isPresent());
            Assert.assertNull(response.getErrorMessage());
            Assert.assertNull(response.getWarningMessage());
            Assert.assertEquals(interval, response.getInterval());
            Assert.assertEquals(leechers, response.getLeecherCount());
            Assert.assertEquals(seeders, response.getSeederCount());
        });
    }
}

