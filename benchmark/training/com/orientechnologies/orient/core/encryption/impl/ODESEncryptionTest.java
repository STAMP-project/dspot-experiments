package com.orientechnologies.orient.core.encryption.impl;


import ODESEncryption.NAME;
import OGlobalConfiguration.STORAGE_ENCRYPTION_KEY;
import OGlobalConfiguration.STORAGE_ENCRYPTION_METHOD;
import com.orientechnologies.common.io.OFileUtils;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.OrientDBInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OSecurityException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author giastfader@github
 * @author Luca Garulli (l.garulli--(at)--orientdb.com)
 * @since 22.04.2015
 */
public class ODESEncryptionTest extends AbstractEncryptionTest {
    private static final String DBNAME_CLUSTERTEST = "testCreatedDESEncryptedCluster";

    private static final String DBNAME_DATABASETEST = "testCreatedDESEncryptedDatabase";

    @Test
    public void testODESEncryptedCompressionNoKey() {
        try {
            testEncryption(NAME);
            Assert.fail();
        } catch (OSecurityException e) {
        }
    }

    @Test
    public void testODESEncryptedCompressionInvalidKey() {
        try {
            testEncryption(NAME, "no");
            Assert.fail();
        } catch (OSecurityException e) {
        }
    }

    @Test
    public void testODESEncryptedCompression() {
        testEncryption(NAME, "T1JJRU5UREI=");
    }

    @Test
    public void testCreatedDESEncryptedDatabase() {
        OFileUtils.deleteRecursively(new File(("target/" + (ODESEncryptionTest.DBNAME_DATABASETEST))));
        final ODatabaseInternal db = new ODatabaseDocumentTx(("plocal:target/" + (ODESEncryptionTest.DBNAME_DATABASETEST)));
        db.setProperty(STORAGE_ENCRYPTION_METHOD.getKey(), "des");
        db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        db.create();
        try {
            db.command(new OCommandSQL("create class TestEncryption")).execute();
            db.command(new OCommandSQL("insert into TestEncryption set name = 'Jay'")).execute();
            List result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            db.close();
            db.open("admin", "admin");
            OrientDBInternal orientDB = getSharedContext().getOrientDB();
            db.close();
            orientDB.forceDatabaseClose(db.getName());
            // db.getStorage().close(true, false);
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            db.close();
            // db.getStorage().close(true, false);
            orientDB.forceDatabaseClose(db.getName());
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "invalidPassword");
            try {
                db.open("admin", "admin");
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                db.activateOnCurrentThread();
                db.close();
                // db.getStorage().close(true, false);
                orientDB.forceDatabaseClose(db.getName());
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA=-");
            try {
                db.open("admin", "admin");
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                db.activateOnCurrentThread();
                db.close();
                // db.getStorage().close(true, false);
                orientDB.forceDatabaseClose(db.getName());
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
        } finally {
            db.activateOnCurrentThread();
            db.drop();
        }
    }

    @Test
    public void testCreatedDESEncryptedCluster() {
        OFileUtils.deleteRecursively(new File(("target/" + (ODESEncryptionTest.DBNAME_CLUSTERTEST))));
        final ODatabaseInternal db = new ODatabaseDocumentTx(("plocal:target/" + (ODESEncryptionTest.DBNAME_CLUSTERTEST)));
        db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        db.create();
        try {
            db.command(new OCommandSQL("create class TestEncryption")).execute();
            db.command(new OCommandSQL("alter class TestEncryption encryption des")).execute();
            db.command(new OCommandSQL("insert into TestEncryption set name = 'Jay'")).execute();
            List result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            db.close();
            db.open("admin", "admin");
            OrientDBInternal orientDB = getSharedContext().getOrientDB();
            db.close();
            orientDB.forceDatabaseClose(db.getName());
            // db.getStorage().close(true, false);
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            db.close();
            // db.getStorage().close(true, false);
            orientDB.forceDatabaseClose(db.getName());
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "invalidPassword");
            try {
                db.open("admin", "admin");
                db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
                result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from OUser"));
                Assert.assertFalse(result.isEmpty());
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                db.activateOnCurrentThread();
                db.close();
                // db.getStorage().close(true, false);
                orientDB.forceDatabaseClose(db.getName());
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA=-");
            try {
                db.open("admin", "admin");
                db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                db.activateOnCurrentThread();
                db.close();
                // db.getStorage().close(true, false);
                orientDB.forceDatabaseClose(db.getName());
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
        } finally {
            db.activateOnCurrentThread();
            if (db.exists())
                db.drop();

        }
    }
}

