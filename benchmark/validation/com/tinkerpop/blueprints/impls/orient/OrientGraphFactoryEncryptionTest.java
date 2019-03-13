package com.tinkerpop.blueprints.impls.orient;


import ODatabaseType.PLOCAL;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.storage.OStorage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Created by frank on 18/07/2016.
 */
public class OrientGraphFactoryEncryptionTest {
    @Rule
    public TestName name = new TestName();

    private String dbPath;

    private String dbDir;

    private String dbName;

    @Test
    public void testCreatedAESEncryptedCluster() {
        OrientGraphFactory graphFactory = new OrientGraphFactory(("plocal:" + (dbPath)));
        graphFactory.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        ODatabaseDocumentInternal db = graphFactory.getDatabase();
        // noinspection deprecation
        assertThat(db.getProperty(STORAGE_ENCRYPTION_KEY.getKey())).isEqualTo("T1JJRU5UREJfSVNfQ09PTA==");
        db.close();
        db = graphFactory.getNoTx().getDatabase();
        // noinspection deprecation
        assertThat(db.getProperty(STORAGE_ENCRYPTION_KEY.getKey())).isEqualTo("T1JJRU5UREJfSVNfQ09PTA==");
        db.close();
        db = graphFactory.getNoTx().getRawGraph();
        // noinspection deprecation
        assertThat(db.getProperty(STORAGE_ENCRYPTION_KEY.getKey())).isEqualTo("T1JJRU5UREJfSVNfQ09PTA==");
        db.close();
        graphFactory.close();
    }

    @Test
    public void shouldQueryDESEncryptedDatabase() {
        OrientGraphFactory graphFactory = new OrientGraphFactory(("plocal:" + (dbPath)));
        graphFactory.setProperty(STORAGE_ENCRYPTION_METHOD.getKey(), "des");
        graphFactory.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        ODatabaseSession db = graphFactory.getDatabase();
        db.command("create class TestEncryption");
        db.command("insert into TestEncryption set name = 'Jay'");
        try (OResultSet result = db.query("select from TestEncryption")) {
            assertThat(result).hasSize(1);
        }
        db.close();
        graphFactory.close();
    }

    @Test
    public void shouldFailWitWrongKey() {
        try (OrientDB orientDB = new OrientDB(("embedded:" + (dbDir)), OrientDBConfig.defaultConfig())) {
            orientDB.create(dbName, PLOCAL);
            try (ODatabaseSession db = orientDB.open(dbName, "admin", "admin")) {
                // noinspection deprecation
                db.setProperty(STORAGE_ENCRYPTION_METHOD.getKey(), "des");
                // noinspection deprecation
                db.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
            }
        }
        OrientGraphFactory graphFactory = new OrientGraphFactory(("plocal:" + (dbPath)));
        graphFactory.setProperty(STORAGE_ENCRYPTION_METHOD.getKey(), "des");
        graphFactory.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        ODatabaseSession db = graphFactory.getDatabase();
        db.command("create class TestEncryption");
        db.command("insert into TestEncryption set name = 'Jay'");
        db.close();
        OStorage storage = getStorage();
        graphFactory.close();
        storage.close();
        graphFactory = new OrientGraphFactory(("plocal:" + (dbPath)));
        graphFactory.setProperty(STORAGE_ENCRYPTION_METHOD.getKey(), "des");
        graphFactory.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        db = graphFactory.getDatabase();
        try (OResultSet result = db.query("select from TestEncryption")) {
            assertThat(result).hasSize(1);
        }
        db.close();
        graphFactory.close();
    }

    @Test
    public void testCreatedDESEncryptedCluster() {
        OrientGraphFactory graphFactory = new OrientGraphFactory(("plocal:" + (dbPath)));
        graphFactory.setProperty(STORAGE_ENCRYPTION_KEY.getKey(), "T1JJRU5UREJfSVNfQ09PTA==");
        ODatabaseSession db = graphFactory.getDatabase();
        // verifyClusterEncryption(db, "des");
        db.close();
        graphFactory.close();
    }
}

