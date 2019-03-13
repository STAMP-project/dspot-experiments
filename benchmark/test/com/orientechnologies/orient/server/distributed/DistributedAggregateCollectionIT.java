package com.orientechnologies.orient.server.distributed;


import org.junit.Test;


public class DistributedAggregateCollectionIT extends AbstractServerClusterTest {
    private static final int SERVERS = 1;

    @Test
    public void test() throws Exception {
        init(DistributedAggregateCollectionIT.SERVERS);
        prepare(false);
        execute();
    }
}

