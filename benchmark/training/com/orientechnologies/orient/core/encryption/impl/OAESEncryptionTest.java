package com.orientechnologies.orient.core.encryption.impl;


import OAESEncryption.NAME;
import OGlobalConfiguration.STORAGE_ENCRYPTION_KEY;
import OGlobalConfiguration.STORAGE_ENCRYPTION_METHOD;
import com.orientechnologies.common.io.OFileUtils;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.OrientDBInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OSecurityException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.storage.OStorage;
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
public class OAESEncryptionTest extends AbstractEncryptionTest {
    private static final String DBNAME_DATABASETEST = "testCreatedAESEncryptedDatabase";

    private static final String DBNAME_CLUSTERTEST = "testCreatedAESEncryptedCluster";

    @Test
    public void testOAESEncryptedCompressionNoKey() {
        try {
            testEncryption(NAME);
            Assert.fail();
        } catch (OSecurityException e) {
        }
    }

    @Test
    public void testOAESEncryptedInvalidKey() {
        try {
            testEncryption(NAME, "ee");
            Assert.fail();
        } catch (OSecurityException e) {
        }
    }

    @Test
    public void testOAESEncrypted() {
        testEncryption(NAME, "T1JJRU5UREJfSVNfQ09PTA==");
    }

    @Test
    public void testCreatedAESEncryptedDatabase() {
        String buildDirectory = System.getProperty("buildDirectory", ".");
        final String dbPath = (buildDirectory + (File.separator)) + (OAESEncryptionTest.DBNAME_DATABASETEST);
        OFileUtils.deleteRecursively(new File(dbPath));
        final ODatabase db = new ODatabaseDocumentTx(("plocal:" + dbPath));
        db.setProperty(STORAGE_ENCRYPTION_METHOD.getKey(), "aes");
        db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        db.create();
        try {
            db.command(new OCommandSQL("create class TestEncryption")).execute();
            db.command(new OCommandSQL("insert into TestEncryption set name = 'Jay'")).execute();
            List result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            db.close();
            db.open("admin", "admin");
            OStorage storage = getStorage();
            OrientDBInternal orientDB = getSharedContext().getOrientDB();
            orientDB.forceDatabaseClose(db.getName());
            db.close();
            // storage.close(true, false);
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            storage = ((com.orientechnologies.orient.core.db.ODatabaseDocumentInternal) (db)).getStorage();
            getSharedContext().getOrientDB().forceDatabaseClose(db.getName());
            db.close();
            // storage.close(true, false);
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "invalidPassword");
            try {
                db.open("admin", "admin");
                storage = ((com.orientechnologies.orient.core.db.ODatabaseDocumentInternal) (db)).getStorage();
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                db.activateOnCurrentThread();
                db.close();
                orientDB.forceDatabaseClose(db.getName());
                // storage.close(true, false);
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA=-");
            try {
                db.open("admin", "admin");
                storage = ((com.orientechnologies.orient.core.db.ODatabaseDocumentInternal) (db)).getStorage();
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                db.activateOnCurrentThread();
                db.close();
                orientDB.forceDatabaseClose(db.getName());
                // storage.close(true, false);
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
        } finally {
            db.activateOnCurrentThread();
            if (db.isClosed()) {
                db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
                db.open("admin", "admin");
            }
            db.drop();
        }
    }

    @Test
    public void testCreatedAESEncryptedCluster() {
        final String buildDirectory = System.getProperty("buildDirectory", ".");
        final String dbPath = (buildDirectory + (File.separator)) + (OAESEncryptionTest.DBNAME_CLUSTERTEST);
        OFileUtils.deleteRecursively(new File(dbPath));
        final ODatabase db = new ODatabaseDocumentTx(("plocal:" + dbPath));
        db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        db.create();
        try {
            db.command(new OCommandSQL("create class TestEncryption")).execute();
            db.command(new OCommandSQL("alter class TestEncryption encryption aes")).execute();
            db.command(new OCommandSQL("insert into TestEncryption set name = 'Jay'")).execute();
            List result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            db.close();
            db.open("admin", "admin");
            OStorage storage = getStorage();
            OrientDBInternal orientdb = getSharedContext().getOrientDB();
            orientdb.forceDatabaseClose(db.getName());
            db.close();
            // storage.close(true, false);
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
            storage = ((com.orientechnologies.orient.core.db.ODatabaseDocumentInternal) (db)).getStorage();
            orientdb.forceDatabaseClose(db.getName());
            db.close();
            // storage.close(true, false);
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "invalidPassword");
            try {
                db.open("admin", "admin");
                storage = ((com.orientechnologies.orient.core.db.ODatabaseDocumentInternal) (db)).getStorage();
                db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
                result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from OUser"));
                Assert.assertFalse(result.isEmpty());
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                orientdb.forceDatabaseClose(db.getName());
                db.close();
                // storage.close(true, false);
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA=-");
            try {
                db.open("admin", "admin");
                storage = ((com.orientechnologies.orient.core.db.ODatabaseDocumentInternal) (db)).getStorage();
                db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
                Assert.fail();
            } catch (OSecurityException e) {
                Assert.assertTrue(true);
            } finally {
                db.activateOnCurrentThread();
                orientdb.forceDatabaseClose(db.getName());
                db.close();
                // storage.close(true, false);
            }
            db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            db.open("admin", "admin");
            result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.orientechnologies.orient.core.record.impl.ODocument>("select from TestEncryption"));
            Assert.assertEquals(result.size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.activateOnCurrentThread();
            if (db.isClosed()) {
                db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
                db.open("admin", "admin");
            }
            db.drop();
        }
    }
}

