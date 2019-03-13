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
package bt.it;


import bt.it.fixture.BaseBtTest;
import bt.it.fixture.SharedTrackerModule;
import bt.it.fixture.Swarm;
import bt.protocol.crypto.EncryptionPolicy;
import bt.runtime.BtClient;
import bt.runtime.Config;
import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class Swarm_MagnetIT extends BaseBtTest {
    private static final int NUMBER_OF_SEEDERS = 5;

    private static final int NUMBER_OF_LEECHERS = 2;

    private static final int NUMBER_OF_MAGNET_LEECHERS = 3;

    private static final Config CONFIG = new Config() {
        @Override
        public InetAddress getAcceptorAddress() {
            return InetAddress.getLoopbackAddress();
        }

        @Override
        public Duration getTrackerQueryInterval() {
            return Duration.ofSeconds(5);
        }

        @Override
        public EncryptionPolicy getEncryptionPolicy() {
            return EncryptionPolicy.REQUIRE_PLAINTEXT;
        }
    };

    @Rule
    public Swarm swarm = buildSwarm().config(Swarm_MagnetIT.CONFIG).seeders(Swarm_MagnetIT.NUMBER_OF_SEEDERS).leechers(Swarm_MagnetIT.NUMBER_OF_LEECHERS, Swarm_MagnetIT.NUMBER_OF_MAGNET_LEECHERS).module(new SharedTrackerModule()).useInMemoryFileSystem().build();

    @Test
    public void testSwarm_OneSeederManyLeechers() {
        BtClient seeder = swarm.getSeederHandles().iterator().next();
        List<BtClient> leechers = swarm.getLeecherHandles();
        CompletableFuture<?>[] leecherFutures = leechers.stream().map(BtClient::startAsync).toArray(CompletableFuture<?>[]::new);
        seeder.startAsync();
        CompletableFuture.allOf(leecherFutures).join();
        seeder.stop();
        Assert.assertEquals(((Swarm_MagnetIT.NUMBER_OF_SEEDERS) * 2), swarm.getSeeders().size());
        Assert.assertEquals(0, swarm.getLeechers().size());
    }

    @Test
    public void testSwarm_ManySeedersManyLeechers() {
        List<BtClient> seeders = swarm.getSeederHandles();
        List<BtClient> leechers = swarm.getLeecherHandles();
        CompletableFuture<?>[] leecherFutures = leechers.stream().map(BtClient::startAsync).toArray(CompletableFuture<?>[]::new);
        seeders.forEach(BtClient::startAsync);
        CompletableFuture.allOf(leecherFutures).join();
        seeders.forEach(BtClient::stop);
        Assert.assertEquals(((Swarm_MagnetIT.NUMBER_OF_SEEDERS) * 2), swarm.getSeeders().size());
        Assert.assertEquals(0, swarm.getLeechers().size());
    }
}

