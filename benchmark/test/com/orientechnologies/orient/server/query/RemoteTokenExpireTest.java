package com.orientechnologies.orient.server.query;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.enterprise.channel.binary.OTokenSecurityException;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by wolf4ood on 1/03/19.
 */
public class RemoteTokenExpireTest {
    private static final String SERVER_DIRECTORY = "./target/token";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument session;

    private int oldPageSize;

    private long expireTimeout = 500;

    @Test
    public void itShouldNotFailWithQuery() {
        waitAndClean();
        session.activateOnCurrentThread();
        try (OResultSet res = session.query("select from Some")) {
            Assert.assertEquals(0, res.stream().count());
        } catch (OTokenSecurityException e) {
            Assert.fail("It should not get the exception");
        }
    }

    @Test
    public void itShouldNotFailWithCommand() {
        waitAndClean();
        session.activateOnCurrentThread();
        try (OResultSet res = session.command("insert into V set name = 'foo'")) {
            Assert.assertEquals(1, res.stream().count());
        } catch (OTokenSecurityException e) {
            Assert.fail("It should not get the exception");
        }
    }

    @Test
    public void itShouldNotFailWithScript() {
        waitAndClean();
        session.activateOnCurrentThread();
        try (OResultSet res = session.execute("sql", "insert into V set name = 'foo'")) {
            Assert.assertEquals(1, res.stream().count());
        } catch (OTokenSecurityException e) {
            Assert.fail("It should not get the exception");
        }
    }

    @Test
    public void itShouldFailWithQueryNext() throws InterruptedException {
        QUERY_REMOTE_RESULTSET_PAGE_SIZE.setValue(1);
        try (OResultSet res = session.query("select from OUser")) {
            waitAndClean();
            session.activateOnCurrentThread();
            Assert.assertEquals(3, res.stream().count());
        } catch (OTokenSecurityException e) {
            return;
        } finally {
            QUERY_REMOTE_RESULTSET_PAGE_SIZE.setValue(10);
        }
        Assert.fail("It should get an exception");
    }

    @Test
    public void itShouldNotFailWithNewTXAndQuery() {
        waitAndClean();
        session.activateOnCurrentThread();
        session.begin();
        session.save(session.newElement("Some"));
        try (OResultSet res = session.query("select from Some")) {
            Assert.assertEquals(1, res.stream().count());
        } catch (OTokenSecurityException e) {
            Assert.fail("It should not get the expire exception");
        } finally {
            session.rollback();
        }
    }

    @Test
    public void itShouldNotFailAtCommit() {
        session.begin();
        session.save(session.newElement("Some"));
        waitAndClean();
        session.activateOnCurrentThread();
        try {
            session.commit();
        } catch (OTokenSecurityException e) {
            Assert.fail("It should not get the expire exception");
        }
    }

    @Test
    public void itShouldFailAtBeingAndQuery() {
        session.begin();
        session.save(session.newElement("Some"));
        try (OResultSet resultSet = session.query("select from Some")) {
            Assert.assertEquals(1, resultSet.stream().count());
        }
        waitAndClean();
        session.activateOnCurrentThread();
        try {
            session.query("select from Some");
        } catch (OTokenSecurityException e) {
            session.rollback();
            return;
        }
        Assert.fail("It should not get the expire exception");
    }

    @Test
    public void itShouldNotFailWithIndexGet() {
        OIndex<?> index = session.getMetadata().getIndexManager().getIndex("OUser.name");
        waitAndClean();
        session.activateOnCurrentThread();
        try {
            index.get("admin");
        } catch (OTokenSecurityException e) {
            Assert.fail("It should not get the expire exception");
        }
    }

    @Test
    public void itShouldNotFailWithIndexPut() {
        OIndex<?> index = session.getMetadata().getIndexManager().getIndex("OUser.name");
        waitAndClean();
        session.activateOnCurrentThread();
        try {
            index.put("test", new ORecordId(5, 0));
        } catch (OTokenSecurityException e) {
            Assert.fail("It should  get the expire exception");
        }
    }
}

