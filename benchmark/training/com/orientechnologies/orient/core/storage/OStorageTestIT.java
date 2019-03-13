package com.orientechnologies.orient.core.storage;


import OChecksumMode.StoreAndSwitchReadOnlyMode;
import OChecksumMode.StoreAndVerify;
import ODatabase.ATTRIBUTES.MINIMUMCLUSTERS;
import ODatabaseType.PLOCAL;
import OGlobalConfiguration.STORAGE_CHECKSUM_MODE;
import com.orientechnologies.orient.core.OConstants;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OSharedContext;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.exception.OPageIsBrokenException;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.storage.cache.OWriteCache;
import com.orientechnologies.orient.core.storage.disk.OLocalPaginatedStorage;
import com.orientechnologies.orient.core.storage.fs.OFileClassic;
import com.orientechnologies.orient.core.storage.impl.local.paginated.base.ODurablePage;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;


public class OStorageTestIT {
    private OrientDB orientDB;

    private static Path buildPath;

    @Test
    public void testCheckSumFailureReadOnly() throws Exception {
        OrientDBConfig config = OrientDBConfig.builder().addConfig(STORAGE_CHECKSUM_MODE, StoreAndSwitchReadOnlyMode).addAttribute(MINIMUMCLUSTERS, 1).build();
        orientDB = new OrientDB(("embedded:" + (OStorageTestIT.buildPath.toFile().getAbsolutePath())), config);
        orientDB.create(OStorageTestIT.class.getSimpleName(), PLOCAL, config);
        ODatabaseSession session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin", config);
        OMetadata metadata = session.getMetadata();
        OSchema schema = metadata.getSchema();
        schema.createClass("PageBreak");
        for (int i = 0; i < 10; i++) {
            ODocument document = new ODocument("PageBreak");
            document.field("value", "value");
            document.save();
        }
        OLocalPaginatedStorage storage = ((OLocalPaginatedStorage) (getStorage()));
        OWriteCache wowCache = storage.getWriteCache();
        OSharedContext ctx = getSharedContext();
        session.close();
        final Path storagePath = storage.getStoragePath();
        long fileId = wowCache.fileIdByName("pagebreak.pcl");
        String nativeFileName = wowCache.nativeFileNameById(fileId);
        storage.close(true, false);
        ctx.close();
        int position = 3 * 1024;
        RandomAccessFile file = new RandomAccessFile(storagePath.resolve(nativeFileName).toFile(), "rw");
        file.seek(position);
        int bt = file.read();
        file.seek(position);
        file.write((bt + 1));
        file.close();
        session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin");
        session.query("select from PageBreak").close();
        Thread.sleep(100);// lets wait till event will be propagated

        ODocument document = new ODocument("PageBreak");
        document.field("value", "value");
        try {
            document.save();
            Assert.fail();
        } catch (OPageIsBrokenException e) {
            Assert.assertTrue(true);
        }
        session.close();
    }

    @Test
    public void testCheckMagicNumberReadOnly() throws Exception {
        OrientDBConfig config = OrientDBConfig.builder().addConfig(STORAGE_CHECKSUM_MODE, StoreAndSwitchReadOnlyMode).addAttribute(MINIMUMCLUSTERS, 1).build();
        orientDB = new OrientDB(("embedded:" + (OStorageTestIT.buildPath.toFile().getAbsolutePath())), config);
        orientDB.create(OStorageTestIT.class.getSimpleName(), PLOCAL, config);
        ODatabaseSession session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin", config);
        OMetadata metadata = session.getMetadata();
        OSchema schema = metadata.getSchema();
        schema.createClass("PageBreak");
        for (int i = 0; i < 10; i++) {
            ODocument document = new ODocument("PageBreak");
            document.field("value", "value");
            document.save();
        }
        OLocalPaginatedStorage storage = ((OLocalPaginatedStorage) (getStorage()));
        OWriteCache wowCache = storage.getWriteCache();
        OSharedContext ctx = getSharedContext();
        session.close();
        final Path storagePath = storage.getStoragePath();
        long fileId = wowCache.fileIdByName("pagebreak.pcl");
        String nativeFileName = wowCache.nativeFileNameById(fileId);
        storage.close(true, false);
        ctx.close();
        int position = (OFileClassic.HEADER_SIZE) + (ODurablePage.MAGIC_NUMBER_OFFSET);
        RandomAccessFile file = new RandomAccessFile(storagePath.resolve(nativeFileName).toFile(), "rw");
        file.seek(position);
        file.write(1);
        file.close();
        session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin");
        session.query("select from PageBreak").close();
        Thread.sleep(100);// lets wait till event will be propagated

        ODocument document = new ODocument("PageBreak");
        document.field("value", "value");
        try {
            document.save();
            Assert.fail();
        } catch (OPageIsBrokenException e) {
            Assert.assertTrue(true);
        }
        session.close();
    }

    @Test
    public void testCheckMagicNumberVerify() throws Exception {
        OrientDBConfig config = OrientDBConfig.builder().addConfig(STORAGE_CHECKSUM_MODE, StoreAndVerify).addAttribute(MINIMUMCLUSTERS, 1).build();
        orientDB = new OrientDB(("embedded:" + (OStorageTestIT.buildPath.toFile().getAbsolutePath())), config);
        orientDB.create(OStorageTestIT.class.getSimpleName(), PLOCAL, config);
        ODatabaseSession session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin", config);
        OMetadata metadata = session.getMetadata();
        OSchema schema = metadata.getSchema();
        schema.createClass("PageBreak");
        for (int i = 0; i < 10; i++) {
            ODocument document = new ODocument("PageBreak");
            document.field("value", "value");
            document.save();
        }
        OLocalPaginatedStorage storage = ((OLocalPaginatedStorage) (getStorage()));
        OWriteCache wowCache = storage.getWriteCache();
        OSharedContext ctx = getSharedContext();
        session.close();
        final Path storagePath = storage.getStoragePath();
        long fileId = wowCache.fileIdByName("pagebreak.pcl");
        String nativeFileName = wowCache.nativeFileNameById(fileId);
        storage.close(true, false);
        ctx.close();
        int position = (OFileClassic.HEADER_SIZE) + (ODurablePage.MAGIC_NUMBER_OFFSET);
        RandomAccessFile file = new RandomAccessFile(storagePath.resolve(nativeFileName).toFile(), "rw");
        file.seek(position);
        file.write(1);
        file.close();
        session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin");
        session.query("select from PageBreak").close();
        Thread.sleep(100);// lets wait till event will be propagated

        ODocument document = new ODocument("PageBreak");
        document.field("value", "value");
        document.save();
        session.close();
    }

    @Test
    public void testCheckSumFailureVerifyAndLog() throws Exception {
        OrientDBConfig config = OrientDBConfig.builder().addConfig(STORAGE_CHECKSUM_MODE, StoreAndVerify).addAttribute(MINIMUMCLUSTERS, 1).build();
        orientDB = new OrientDB(("embedded:" + (OStorageTestIT.buildPath.toFile().getAbsolutePath())), config);
        orientDB.create(OStorageTestIT.class.getSimpleName(), PLOCAL, config);
        ODatabaseSession session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin", config);
        OMetadata metadata = session.getMetadata();
        OSchema schema = metadata.getSchema();
        schema.createClass("PageBreak");
        for (int i = 0; i < 10; i++) {
            ODocument document = new ODocument("PageBreak");
            document.field("value", "value");
            document.save();
        }
        OLocalPaginatedStorage storage = ((OLocalPaginatedStorage) (getStorage()));
        OWriteCache wowCache = storage.getWriteCache();
        OSharedContext ctx = getSharedContext();
        session.close();
        final Path storagePath = storage.getStoragePath();
        long fileId = wowCache.fileIdByName("pagebreak.pcl");
        String nativeFileName = wowCache.nativeFileNameById(fileId);
        storage.close(true, false);
        ctx.close();
        int position = 3 * 1024;
        RandomAccessFile file = new RandomAccessFile(storagePath.resolve(nativeFileName).toFile(), "rw");
        file.seek(position);
        int bt = file.read();
        file.seek(position);
        file.write((bt + 1));
        file.close();
        session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin");
        session.query("select from PageBreak").close();
        Thread.sleep(100);// lets wait till event will be propagated

        ODocument document = new ODocument("PageBreak");
        document.field("value", "value");
        document.save();
        session.close();
    }

    @Test
    public void testCreatedVersionIsStored() {
        orientDB = new OrientDB(("embedded:" + (OStorageTestIT.buildPath.toFile().getAbsolutePath())), OrientDBConfig.defaultConfig());
        orientDB.create(OStorageTestIT.class.getSimpleName(), PLOCAL, OrientDBConfig.defaultConfig());
        final ODatabaseSession session = orientDB.open(OStorageTestIT.class.getSimpleName(), "admin", "admin");
        try (OResultSet resultSet = session.query("SELECT FROM metadata:storage")) {
            Assert.assertTrue(resultSet.hasNext());
            final OResult result = resultSet.next();
            Assert.assertEquals(OConstants.getVersion(), result.getProperty("createdAtVersion"));
        }
    }
}

