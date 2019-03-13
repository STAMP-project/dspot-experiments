package com.orientechnologies.orient.core.storage.impl.local.paginated;


import OOneEntryPerKeyLockManager.LOCK.EXCLUSIVE;
import com.orientechnologies.common.concur.lock.OOneEntryPerKeyLockManager;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
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
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 6/24/13
 */
public class localPaginatedStorageUpdateCrashRestoreCT {
    private ODatabaseDocumentTx baseDocumentTx;

    private ODatabaseDocumentTx testDocumentTx;

    private File buildDir;

    private AtomicInteger idGen = new AtomicInteger(0);

    private OOneEntryPerKeyLockManager<Integer> idLockManager = new OOneEntryPerKeyLockManager<Integer>(true, 1000, 10000);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Process process;

    @Test
    public void testDocumentUpdate() throws Exception {
        createSchema(baseDocumentTx);
        createSchema(testDocumentTx);
        System.out.println("Schema was created.");
        System.out.println("Document creation was started.");
        createDocuments();
        System.out.println("Document creation was finished.");
        System.out.println("Start documents update.");
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 8; i++) {
            futures.add(executorService.submit(new localPaginatedStorageUpdateCrashRestoreCT.DataUpdateTask(baseDocumentTx, testDocumentTx)));
        }
        System.out.println("Wait for 5 minutes");
        TimeUnit.MINUTES.sleep(5);
        long lastTs = System.currentTimeMillis();
        System.out.println("Wait for process to destroy");
        process.destroyForcibly();
        process.waitFor();
        System.out.println("Process was destroyed");
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Documents update was stopped.");
        testDocumentTx = new ODatabaseDocumentTx(("plocal:" + (new File(new File(buildDir, "databases"), "testLocalPaginatedStorageUpdateCrashRestore").getCanonicalPath())));
        long startRestoreTime = System.currentTimeMillis();
        testDocumentTx.open("admin", "admin");
        long endRestoreTime = System.currentTimeMillis();
        System.out.println(("Restore time : " + (endRestoreTime - startRestoreTime)));
        testDocumentTx.close();
        testDocumentTx.open("admin", "admin");
        System.out.println("Start documents comparison.");
        compareDocuments(lastTs);
    }

    public static final class RemoteDBRunner {
        public static void main(String[] args) throws Exception {
            OServer server = OServerMain.create();
            server.startup(localPaginatedStorageUpdateCrashRestoreCT.RemoteDBRunner.class.getResourceAsStream("/com/orientechnologies/orient/core/storage/impl/local/paginated/db-update-config.xml"));
            server.activate();
            final String mutexFile = System.getProperty("mutexFile");
            final RandomAccessFile mutex = new RandomAccessFile(mutexFile, "rw");
            mutex.seek(0);
            mutex.write(1);
            mutex.close();
        }
    }

    public class DataUpdateTask implements Callable<Void> {
        private ODatabaseDocumentTx baseDB;

        private ODatabaseDocumentTx testDB;

        public DataUpdateTask(ODatabaseDocumentTx baseDB, ODatabaseDocumentTx testDocumentTx) {
            this.baseDB = new ODatabaseDocumentTx(baseDB.getURL());
            this.testDB = new ODatabaseDocumentTx(testDocumentTx.getURL());
        }

        @Override
        public Void call() throws Exception {
            Random random = new Random();
            baseDB.open("admin", "admin");
            testDB.open("admin", "admin");
            int counter = 0;
            try {
                while (true) {
                    final int idToUpdate = random.nextInt(idGen.get());
                    idLockManager.acquireLock(idToUpdate, EXCLUSIVE);
                    try {
                        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(("select from TestClass where id  = " + idToUpdate));
                        final List<ODocument> result = baseDB.query(query);
                        Assert.assertTrue((!(result.isEmpty())));
                        final ODocument document = result.get(0);
                        document.field("timestamp", System.currentTimeMillis());
                        document.field("stringValue", ("vde" + (random.nextLong())));
                        saveDoc(document, baseDB, testDB);
                        counter++;
                        if ((counter % 50000) == 0)
                            System.out.println((counter + " records were updated."));

                    } finally {
                        idLockManager.releaseLock(Thread.currentThread(), idToUpdate, EXCLUSIVE);
                    }
                } 
            } finally {
                baseDB.activateOnCurrentThread();
                baseDB.close();
                testDB.activateOnCurrentThread();
                testDB.close();
            }
        }
    }
}

