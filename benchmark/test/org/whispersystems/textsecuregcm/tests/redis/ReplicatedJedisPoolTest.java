package org.whispersystems.textsecuregcm.tests.redis;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.Test;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;


public class ReplicatedJedisPoolTest {
    @Test
    public void testWriteCheckoutNoSlaves() {
        JedisPool master = Mockito.mock(JedisPool.class);
        try {
            new ReplicatedJedisPool(master, new LinkedList());
            throw new AssertionError();
        } catch (Exception e) {
            // good
        }
    }

    @Test
    public void testWriteCheckoutWithSlaves() {
        JedisPool master = Mockito.mock(JedisPool.class);
        JedisPool slave = Mockito.mock(JedisPool.class);
        Jedis instance = Mockito.mock(Jedis.class);
        Mockito.when(master.getResource()).thenReturn(instance);
        ReplicatedJedisPool replicatedJedisPool = new ReplicatedJedisPool(master, Collections.singletonList(slave));
        Jedis writeResource = replicatedJedisPool.getWriteResource();
        assertThat(writeResource).isEqualTo(instance);
        Mockito.verify(master, Mockito.times(1)).getResource();
    }

    @Test
    public void testReadCheckouts() {
        JedisPool master = Mockito.mock(JedisPool.class);
        JedisPool slaveOne = Mockito.mock(JedisPool.class);
        JedisPool slaveTwo = Mockito.mock(JedisPool.class);
        Jedis instanceOne = Mockito.mock(Jedis.class);
        Jedis instanceTwo = Mockito.mock(Jedis.class);
        Mockito.when(slaveOne.getResource()).thenReturn(instanceOne);
        Mockito.when(slaveTwo.getResource()).thenReturn(instanceTwo);
        ReplicatedJedisPool replicatedJedisPool = new ReplicatedJedisPool(master, Arrays.asList(slaveOne, slaveTwo));
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceOne);
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceTwo);
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceOne);
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceTwo);
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceOne);
        Mockito.verifyNoMoreInteractions(master);
    }

    @Test
    public void testBrokenReadCheckout() {
        JedisPool master = Mockito.mock(JedisPool.class);
        JedisPool slaveOne = Mockito.mock(JedisPool.class);
        JedisPool slaveTwo = Mockito.mock(JedisPool.class);
        Jedis instanceTwo = Mockito.mock(Jedis.class);
        Mockito.when(slaveOne.getResource()).thenThrow(new JedisException("Connection failed!"));
        Mockito.when(slaveTwo.getResource()).thenReturn(instanceTwo);
        ReplicatedJedisPool replicatedJedisPool = new ReplicatedJedisPool(master, Arrays.asList(slaveOne, slaveTwo));
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceTwo);
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceTwo);
        assertThat(replicatedJedisPool.getReadResource()).isEqualTo(instanceTwo);
        Mockito.verifyNoMoreInteractions(master);
    }

    @Test
    public void testAllBrokenReadCheckout() {
        JedisPool master = Mockito.mock(JedisPool.class);
        JedisPool slaveOne = Mockito.mock(JedisPool.class);
        JedisPool slaveTwo = Mockito.mock(JedisPool.class);
        Mockito.when(slaveOne.getResource()).thenThrow(new JedisException("Connection failed!"));
        Mockito.when(slaveTwo.getResource()).thenThrow(new JedisException("Also failed!"));
        ReplicatedJedisPool replicatedJedisPool = new ReplicatedJedisPool(master, Arrays.asList(slaveOne, slaveTwo));
        try {
            replicatedJedisPool.getReadResource();
            throw new AssertionError();
        } catch (Exception e) {
            // good
        }
        Mockito.verifyNoMoreInteractions(master);
    }
}

