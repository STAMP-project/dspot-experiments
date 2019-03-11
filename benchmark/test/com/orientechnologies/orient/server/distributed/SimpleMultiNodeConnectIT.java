package com.orientechnologies.orient.server.distributed;


import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


public class SimpleMultiNodeConnectIT {
    private OServer server0;

    private OServer server1;

    @Test
    public void testLiveQueryDifferentNode() {
        OrientDB remote1 = new OrientDB("remote:localhost:2424;localhost:2425", "root", "test", OrientDBConfig.defaultConfig());
        ODatabaseSession session = remote1.open("test", "admin", "admin");
        try (OResultSet result = session.query("select from test")) {
            Assert.assertEquals(1, result.stream().count());
        }
        server0.shutdown();
        try (OResultSet result = session.query("select from test")) {
            Assert.assertEquals(1, result.stream().count());
        }
        remote1.close();
    }
}

