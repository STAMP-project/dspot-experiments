/**
 * Copyright 2011 Micheal Swiggs
 * Copyright 2015 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.net.discovery;


import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SeedPeersTest {
    private static final NetworkParameters MAINNET = MainNetParams.get();

    @Test
    public void getPeer_one() throws Exception {
        SeedPeers seedPeers = new SeedPeers(SeedPeersTest.MAINNET);
        Assert.assertThat(seedPeers.getPeer(), CoreMatchers.notNullValue());
    }

    @Test
    public void getPeer_all() throws Exception {
        SeedPeers seedPeers = new SeedPeers(SeedPeersTest.MAINNET);
        for (int i = 0; i < (SeedPeersTest.MAINNET.getAddrSeeds().length); ++i) {
            Assert.assertThat(("Failed on index: " + i), seedPeers.getPeer(), CoreMatchers.notNullValue());
        }
        Assert.assertThat(seedPeers.getPeer(), CoreMatchers.equalTo(null));
    }

    @Test
    public void getPeers_length() throws Exception {
        SeedPeers seedPeers = new SeedPeers(SeedPeersTest.MAINNET);
        InetSocketAddress[] addresses = seedPeers.getPeers(0, 0, TimeUnit.SECONDS);
        Assert.assertThat(addresses.length, CoreMatchers.equalTo(SeedPeersTest.MAINNET.getAddrSeeds().length));
    }
}

