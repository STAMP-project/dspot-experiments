package com.baeldung.guava;


import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GuavaMiscUtilsUnitTest {
    @Test
    public void whenHashingData_shouldReturnCorrectHashCode() throws Exception {
        int receivedData = 123;
        HashCode hashCode = Hashing.crc32c().hashInt(receivedData);
        Assert.assertThat(hashCode.toString(), CoreMatchers.equalTo("495be649"));
    }

    @Test
    public void whenDecrementingIpAddress_shouldReturnOneLessIpAddress() throws Exception {
        InetAddress address = InetAddress.getByName("127.0.0.5");
        InetAddress decrementedAddress = InetAddresses.decrement(address);
        Assert.assertThat(decrementedAddress.toString(), CoreMatchers.equalTo("/127.0.0.4"));
    }

    @Test
    public void whenExecutingRunnableInThread_shouldLogThreadExecution() throws Exception {
        ConcurrentHashMap<String, Boolean> threadExecutions = new ConcurrentHashMap<>();
        Runnable logThreadRun = () -> threadExecutions.put(Thread.currentThread().getName(), true);
        Thread t = new Thread(logThreadRun);
        t.run();
        Assert.assertTrue(threadExecutions.get("main"));
    }
}

