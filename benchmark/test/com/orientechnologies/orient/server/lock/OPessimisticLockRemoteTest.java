package com.orientechnologies.orient.server.lock;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import org.junit.Test;


public class OPessimisticLockRemoteTest {
    private static final String SERVER_DIRECTORY = "./target/lock";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument session;

    @Test
    public void lockHappyPathNoCrashNoTx() {
        ORecord rid = session.save(new ODocument("ToLock"));
        OElement record = session.lock(rid.getIdentity());
        record.setProperty("one", "value");
        session.save(record);
        session.unlock(record.getIdentity());
    }

    @Test
    public void lockHappyPathNoCrashTx() {
        ORecord rid = session.save(new ODocument("ToLock"));
        session.begin();
        ODocument record = session.lock(rid.getIdentity());
        record.setProperty("one", "value");
        session.save(record);
        session.commit();
    }
}

