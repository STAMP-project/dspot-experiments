/**
 * Copyright (C) 2018 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.tests.storage;


import io.dropwizard.metrics.MetricsFactory;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.ActiveUserCounter;
import org.whispersystems.textsecuregcm.storage.Device;
import redis.clients.jedis.Jedis;


public class ActiveUserCounterTest {
    private final String NUMBER_IOS = "+15551234567";

    private final String NUMBER_ANDROID = "+5511987654321";

    private final String NUMBER_NODEVICE = "+5215551234567";

    private final String TALLY_KEY = "active_user_tally";

    private final Device iosDevice = Mockito.mock(Device.class);

    private final Device androidDevice = Mockito.mock(Device.class);

    private final Account androidAccount = Mockito.mock(Account.class);

    private final Account iosAccount = Mockito.mock(Account.class);

    private final Account noDeviceAccount = Mockito.mock(Account.class);

    private final Jedis jedis = Mockito.mock(Jedis.class);

    private final ReplicatedJedisPool jedisPool = Mockito.mock(ReplicatedJedisPool.class);

    private final MetricsFactory metricsFactory = Mockito.mock(MetricsFactory.class);

    private final ActiveUserCounter activeUserCounter = new ActiveUserCounter(metricsFactory, jedisPool);

    @Test
    public void testCrawlStart() {
        activeUserCounter.onCrawlStart();
        Mockito.verify(jedisPool, Mockito.times(1)).getWriteResource();
        Mockito.verify(jedis, Mockito.times(1)).del(ArgumentMatchers.any(String.class));
        Mockito.verify(jedis, Mockito.times(1)).close();
        Mockito.verifyZeroInteractions(iosDevice);
        Mockito.verifyZeroInteractions(iosAccount);
        Mockito.verifyZeroInteractions(androidDevice);
        Mockito.verifyZeroInteractions(androidAccount);
        Mockito.verifyZeroInteractions(noDeviceAccount);
        Mockito.verifyZeroInteractions(metricsFactory);
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verifyNoMoreInteractions(jedisPool);
    }

    @Test
    public void testCrawlEnd() {
        activeUserCounter.onCrawlEnd(Optional.empty());
        Mockito.verify(jedisPool, Mockito.times(1)).getReadResource();
        Mockito.verify(jedis, Mockito.times(1)).get(ArgumentMatchers.any(String.class));
        Mockito.verify(jedis, Mockito.times(1)).close();
        Mockito.verify(metricsFactory, Mockito.times(1)).getReporters();
        Mockito.verifyZeroInteractions(iosDevice);
        Mockito.verifyZeroInteractions(iosAccount);
        Mockito.verifyZeroInteractions(androidDevice);
        Mockito.verifyZeroInteractions(androidAccount);
        Mockito.verifyZeroInteractions(noDeviceAccount);
        Mockito.verifyNoMoreInteractions(metricsFactory);
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verifyNoMoreInteractions(jedisPool);
    }

    @Test
    public void testCrawlChunkValidAccount() {
        activeUserCounter.onCrawlChunk(Optional.of(NUMBER_IOS), Arrays.asList(iosAccount));
        Mockito.verify(iosAccount, Mockito.times(1)).getMasterDevice();
        Mockito.verify(iosAccount, Mockito.times(1)).getNumber();
        Mockito.verify(iosDevice, Mockito.times(1)).getLastSeen();
        Mockito.verify(iosDevice, Mockito.times(1)).getApnId();
        Mockito.verify(iosDevice, Mockito.times(0)).getGcmId();
        Mockito.verify(jedisPool, Mockito.times(1)).getWriteResource();
        Mockito.verify(jedis, Mockito.times(1)).get(ArgumentMatchers.any(String.class));
        Mockito.verify(jedis, Mockito.times(1)).set(ArgumentMatchers.any(String.class), ArgumentMatchers.eq((("{\"fromNumber\":\"" + (NUMBER_IOS)) + "\",\"platforms\":{\"ios\":[1,1,1,1,1]},\"countries\":{\"1\":[1,1,1,1,1]}}")));
        Mockito.verify(jedis, Mockito.times(1)).close();
        Mockito.verify(metricsFactory, Mockito.times(0)).getReporters();
        Mockito.verifyZeroInteractions(androidDevice);
        Mockito.verifyZeroInteractions(androidAccount);
        Mockito.verifyZeroInteractions(noDeviceAccount);
        Mockito.verifyZeroInteractions(metricsFactory);
        Mockito.verifyNoMoreInteractions(iosDevice);
        Mockito.verifyNoMoreInteractions(iosAccount);
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verifyNoMoreInteractions(jedisPool);
    }

    @Test
    public void testCrawlChunkNoDeviceAccount() {
        activeUserCounter.onCrawlChunk(Optional.of(NUMBER_NODEVICE), Arrays.asList(noDeviceAccount));
        Mockito.verify(noDeviceAccount, Mockito.times(1)).getMasterDevice();
        Mockito.verify(jedisPool, Mockito.times(1)).getWriteResource();
        Mockito.verify(jedis, Mockito.times(1)).get(ArgumentMatchers.eq(TALLY_KEY));
        Mockito.verify(jedis, Mockito.times(1)).set(ArgumentMatchers.any(String.class), ArgumentMatchers.eq((("{\"fromNumber\":\"" + (NUMBER_NODEVICE)) + "\",\"platforms\":{},\"countries\":{}}")));
        Mockito.verify(jedis, Mockito.times(1)).close();
        Mockito.verify(metricsFactory, Mockito.times(0)).getReporters();
        Mockito.verifyZeroInteractions(iosDevice);
        Mockito.verifyZeroInteractions(iosAccount);
        Mockito.verifyZeroInteractions(androidDevice);
        Mockito.verifyZeroInteractions(androidAccount);
        Mockito.verifyZeroInteractions(noDeviceAccount);
        Mockito.verifyZeroInteractions(metricsFactory);
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verifyNoMoreInteractions(jedisPool);
    }

    @Test
    public void testCrawlChunkMixedAccount() {
        activeUserCounter.onCrawlChunk(Optional.of(NUMBER_IOS), Arrays.asList(iosAccount, androidAccount, noDeviceAccount));
        Mockito.verify(iosAccount, Mockito.times(1)).getMasterDevice();
        Mockito.verify(iosAccount, Mockito.times(1)).getNumber();
        Mockito.verify(androidAccount, Mockito.times(1)).getMasterDevice();
        Mockito.verify(androidAccount, Mockito.times(1)).getNumber();
        Mockito.verify(noDeviceAccount, Mockito.times(1)).getMasterDevice();
        Mockito.verify(iosDevice, Mockito.times(1)).getLastSeen();
        Mockito.verify(iosDevice, Mockito.times(1)).getApnId();
        Mockito.verify(iosDevice, Mockito.times(0)).getGcmId();
        Mockito.verify(androidDevice, Mockito.times(1)).getLastSeen();
        Mockito.verify(androidDevice, Mockito.times(1)).getApnId();
        Mockito.verify(androidDevice, Mockito.times(1)).getGcmId();
        Mockito.verify(jedisPool, Mockito.times(1)).getWriteResource();
        Mockito.verify(jedis, Mockito.times(1)).get(ArgumentMatchers.eq(TALLY_KEY));
        Mockito.verify(jedis, Mockito.times(1)).set(ArgumentMatchers.any(String.class), ArgumentMatchers.eq((("{\"fromNumber\":\"" + (NUMBER_IOS)) + "\",\"platforms\":{\"android\":[0,0,0,1,1],\"ios\":[1,1,1,1,1]},\"countries\":{\"55\":[0,0,0,1,1],\"1\":[1,1,1,1,1]}}")));
        Mockito.verify(jedis, Mockito.times(1)).close();
        Mockito.verify(metricsFactory, Mockito.times(0)).getReporters();
        Mockito.verifyZeroInteractions(metricsFactory);
        Mockito.verifyNoMoreInteractions(iosDevice);
        Mockito.verifyNoMoreInteractions(iosAccount);
        Mockito.verifyNoMoreInteractions(androidDevice);
        Mockito.verifyNoMoreInteractions(androidAccount);
        Mockito.verifyNoMoreInteractions(noDeviceAccount);
        Mockito.verifyNoMoreInteractions(jedis);
        Mockito.verifyNoMoreInteractions(jedisPool);
    }
}

