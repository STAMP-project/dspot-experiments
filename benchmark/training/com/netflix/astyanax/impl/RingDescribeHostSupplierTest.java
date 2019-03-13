package com.netflix.astyanax.impl;


import com.netflix.astyanax.test.TestKeyspace;
import org.junit.Test;


/**
 * User: mkoch
 * Date: 5/23/12
 */
public class RingDescribeHostSupplierTest {
    private static final String NODE1 = "127.0.0.1";

    private static final String NODE2 = "127.0.0.2";

    private static final String NODE3 = "127.0.0.3";

    private static final String RANGE_1_END_TOKEN = "0";

    private static final String RANGE_2_END_TOKEN = "2000";

    private static final String RANGE_3_END_TOKEN = "4000";

    private RingDescribeHostSupplier hostSupplier;

    private TestKeyspace keyspace;

    @Test
    public void testGet() throws Exception {
        // Map<BigInteger,List<Host>> hostMap = hostSupplier.get();
        // assertNotNull(hostMap);
        // assertEquals(3, hostMap.size());
        // 
        // List<Host> endpoints = hostMap.get(new BigInteger(RANGE_1_END_TOKEN));
        // assertEquals(1,endpoints.size());
        // assertEquals(NODE1, endpoints.get(0).getIpAddress());
        // 
        // endpoints = hostMap.get(new BigInteger(RANGE_2_END_TOKEN));
        // assertEquals(1,endpoints.size());
        // assertEquals(NODE2, endpoints.get(0).getIpAddress());
        // 
        // endpoints = hostMap.get(new BigInteger(RANGE_3_END_TOKEN));
        // assertEquals(1,endpoints.size());
        // assertEquals(NODE3,endpoints.get(0).getIpAddress());
    }
}

