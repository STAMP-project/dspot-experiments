package com.orientechnologies.orient.server.query;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.server.OServer;
import org.junit.Test;


/**
 * Created by tglman on 03/01/17.
 */
public class RemoteDropClusterTest {
    private static final String SERVER_DIRECTORY = "./target/cluster";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument session;

    @Test
    public void simpleDropCluster() {
        int cl = session.addCluster("one");
        session.dropCluster(cl, false);
    }

    @Test
    public void simpleDropClusterTruncate() {
        int cl = session.addCluster("one");
        session.dropCluster(cl, true);
    }

    @Test
    public void simpleDropClusterName() {
        session.addCluster("one");
        session.dropCluster("one", false);
    }

    @Test
    public void simpleDropClusterNameTruncate() {
        session.addCluster("one");
        session.dropCluster("one", true);
    }
}

