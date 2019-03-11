package com.orientechnologies.orient.server.distributed.asynch;


import com.orientechnologies.orient.core.Orient;
import junit.framework.TestCase;


public abstract class BareBoneBase1ClientTest extends TestCase {
    protected static final String CONFIG_DIR = "src/test/resources";

    protected static final String DB1_DIR = "target/db1";

    protected volatile Throwable exceptionInThread;

    public void testReplication() throws Throwable {
        Orient.setRegisterDatabaseByPath(true);
        final BareBonesServer[] servers = new BareBonesServer[1];
        // Start the first DB server.
        Thread dbServer1 = new Thread() {
            @Override
            public void run() {
                servers[0] = dbServer(BareBoneBase1ClientTest.DB1_DIR, getLocalURL(), "asynch-dserver-config-0.xml");
            }
        };
        dbServer1.start();
        dbServer1.join();
        // Start the first DB client.
        Thread dbClient1 = new Thread() {
            @Override
            public void run() {
                dbClient1(servers);
            }
        };
        dbClient1.start();
        dbClient1.join();
        endTest(servers);
    }
}

