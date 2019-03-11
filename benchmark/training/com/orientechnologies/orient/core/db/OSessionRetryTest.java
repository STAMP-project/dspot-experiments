package com.orientechnologies.orient.core.db;


import com.orientechnologies.common.types.OModifiableInteger;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.OElement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;


public class OSessionRetryTest {
    private OrientDB orientDB;

    @Test
    public void testRetry() {
        ODatabaseSession session = orientDB.open(OSessionRetryTest.class.getSimpleName(), "admin", "admin");
        session.createClass("Test");
        OElement doc = session.newElement("Test");
        doc.setProperty("one", "tas");
        ORID id = session.save(doc).getIdentity();
        CountDownLatch wrote = new CountDownLatch(1);
        CountDownLatch read = new CountDownLatch(1);
        Executors.newCachedThreadPool().execute(() -> {
            ODatabaseSession session1 = orientDB.open(OSessionRetryTest.class.getSimpleName(), "admin", "admin");
            OElement loaded = session1.load(id);
            try {
                read.await();
            } catch (InterruptedException e) {
                // 
            }
            loaded.setProperty("one", "two");
            session1.save(loaded);
            wrote.countDown();
            session1.close();
        });
        OModifiableInteger integer = new OModifiableInteger(0);
        session.executeWithRetry(2, ( session1) -> {
            integer.increment();
            session1.begin();
            OElement loaded = session1.load(id);
            read.countDown();
            try {
                wrote.await();
            } catch ( e) {
                e.printStackTrace();
            }
            loaded.setProperty("two", "three");
            session1.save(loaded);
            session1.commit();
            return null;
        });
        Assert.assertEquals(integer.getValue(), 2);
        session.close();
    }
}

