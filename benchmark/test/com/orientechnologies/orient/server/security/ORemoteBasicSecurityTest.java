package com.orientechnologies.orient.server.security;


import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


public class ORemoteBasicSecurityTest {
    private OServer server;

    @Test
    public void testCreateAndConnectWriter() {
        // CREATE A SEPARATE CONTEXT TO MAKE SURE IT LOAD STAFF FROM SCRATCH
        try (OrientDB writerOrient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig())) {
            try (ODatabaseSession writer = writerOrient.open("test", "writer", "writer")) {
                writer.save(new ODocument("one"));
                try (OResultSet rs = writer.query("select from one")) {
                    Assert.assertEquals(rs.stream().count(), 2);
                }
            }
        }
    }

    @Test
    public void testCreateAndConnectReader() {
        // CREATE A SEPARATE CONTEXT TO MAKE SURE IT LOAD STAFF FROM SCRATCH
        try (OrientDB writerOrient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig())) {
            try (ODatabaseSession writer = writerOrient.open("test", "reader", "reader")) {
                try (OResultSet rs = writer.query("select from one")) {
                    Assert.assertEquals(rs.stream().count(), 1);
                }
            }
        }
    }
}

