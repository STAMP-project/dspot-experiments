package org.kairosdb.datastore.cassandra;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;


public class CassandraConfigurationTest {
    @Test
    public void test_setHostList() throws ParseException {
        ClusterConfiguration config = setHosts(ImmutableList.of("localhost:9000"));
        Assert.assertEquals(ImmutableMap.of("localhost", 9000), config.getHostList());
        config = setHosts(ImmutableList.of("localhost:9000", "otherhost:8000"));
        Assert.assertEquals(ImmutableMap.of("localhost", 9000, "otherhost", 8000), config.getHostList());
        config = setHosts(ImmutableList.of("localhost", "otherhost"));
        Assert.assertEquals(ImmutableMap.of("localhost", 9042, "otherhost", 9042), config.getHostList());
        config = setHosts(ImmutableList.of("localhost:", "otherhost"));
        Assert.assertEquals(ImmutableMap.of("localhost", 9042, "otherhost", 9042), config.getHostList());
        config = setHosts(ImmutableList.of("localhost:"));
        Assert.assertEquals(ImmutableMap.of("localhost", 9042), config.getHostList());
        config = setHosts(ImmutableList.of("localhost"));
        Assert.assertEquals(ImmutableMap.of("localhost", 9042), config.getHostList());
    }
}

