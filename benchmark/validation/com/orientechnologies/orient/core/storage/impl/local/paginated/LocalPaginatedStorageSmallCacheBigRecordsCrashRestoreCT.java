package com.orientechnologies.orient.core.storage.impl.local.paginated;


import OGlobalConfiguration.DISK_CACHE_SIZE;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 6/26/13
 */
public class LocalPaginatedStorageSmallCacheBigRecordsCrashRestoreCT {
    private final AtomicLong idGen = new AtomicLong();

    private ODatabaseDocumentTx baseDocumentTx;

    private ODatabaseDocumentTx testDocumentTx;

    private File buildDir;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Process process;

    @Test
    public void testDocumentCreation() throws Exception {
        createSchema(baseDocumentTx);
        createSchema(testDocumentTx);
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 8; i++) {
            futures.add(executorService.submit(new LocalPaginatedStorageSmallCacheBigRecordsCrashRestoreCT.DataPropagationTask(baseDocumentTx, testDocumentTx)));
        }
        System.out.println("Wait for 5 minutes");
        TimeUnit.MINUTES.sleep(5);
        long lastTs = System.currentTimeMillis();
        process.destroyForcibly();
        process.waitFor();
        System.out.println("OrientDB server process was destroyed");
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        testDocumentTx = new ODatabaseDocumentTx(("plocal:" + (new File(new File(buildDir, "databases"), "testLocalPaginatedStorageSmallCacheBigRecordsCrashRestore"))));
        testDocumentTx.open("admin", "admin");
        testDocumentTx.close();
        testDocumentTx.open("admin", "admin");
        compareDocuments(lastTs);
    }

    public static final class RemoteDBRunner {
        public static void main(String[] args) throws Exception {
            DISK_CACHE_SIZE.setValue(512);
            OServer server = OServerMain.create();
            server.startup(LocalPaginatedStorageSmallCacheBigRecordsCrashRestoreCT.RemoteDBRunner.class.getResourceAsStream("/com/orientechnologies/orient/core/storage/impl/local/paginated/db-create-big-records-config.xml"));
            server.activate();
            final String mutexFile = System.getProperty("mutexFile");
            final RandomAccessFile mutex = new RandomAccessFile(mutexFile, "rw");
            mutex.seek(0);
            mutex.write(1);
            mutex.close();
        }
    }

    public class DataPropagationTask implements Callable<Void> {
        private ODatabaseDocumentTx baseDB;

        private ODatabaseDocumentTx testDB;

        public DataPropagationTask(ODatabaseDocumentTx baseDB, ODatabaseDocumentTx testDocumentTx) {
            this.baseDB = new ODatabaseDocumentTx(baseDB.getURL());
            this.testDB = new ODatabaseDocumentTx(testDocumentTx.getURL());
        }

        @Override
        public Void call() throws Exception {
            Random random = new Random();
            baseDB.open("admin", "admin");
            testDB.open("admin", "admin");
            try {
                while (true) {
                    final ODocument document = new ODocument("TestClass");
                    document.field("id", idGen.getAndIncrement());
                    document.field("timestamp", System.currentTimeMillis());
                    document.field("stringValue", ("sfe" + (random.nextLong())));
                    byte[] binaryValue = new byte[(random.nextInt((2 * 65536))) + 65537];
                    random.nextBytes(binaryValue);
                    document.field("binaryValue", binaryValue);
                    saveDoc(document);
                } 
            } finally {
                baseDB.activateOnCurrentThread();
                baseDB.close();
                testDB.activateOnCurrentThread();
                testDB.close();
            }
        }

        private void saveDoc(ODocument document) {
            baseDB.activateOnCurrentThread();
            ODocument testDoc = new ODocument();
            document.copyTo(testDoc);
            document.save();
            testDB.activateOnCurrentThread();
            testDoc.save();
        }
    }
}

