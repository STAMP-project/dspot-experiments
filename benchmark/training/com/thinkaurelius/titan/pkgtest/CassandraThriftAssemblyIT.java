package com.thinkaurelius.titan.pkgtest;


import org.junit.Test;


public class CassandraThriftAssemblyIT extends AbstractTitanAssemblyIT {
    @Test
    public void testCassandraThriftSimpleSession() throws Exception {
        testSimpleGremlinSession("conf/titan-cassandra.properties", "cassandrathrift");
    }
}

