package com.thinkaurelius.titan.pkgtest;


import org.junit.Test;


public class CassandraThriftESAssemblyIT extends AbstractTitanAssemblyIT {
    public static final String ES_HOME = "../../titan-es";

    @Test
    public void testCassandraGettingStarted() throws Exception {
        testGettingStartedGremlinSession("conf/titan-cassandra-es.properties", "cassandrathrift");
    }
}

