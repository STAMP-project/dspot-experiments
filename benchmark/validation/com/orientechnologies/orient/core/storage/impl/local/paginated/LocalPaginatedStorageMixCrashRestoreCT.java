package com.orientechnologies.orient.core.storage.impl.local.paginated;


import OOneEntryPerKeyLockManager.LOCK.EXCLUSIVE;
import com.orientechnologies.common.concur.lock.OOneEntryPerKeyLockManager;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 6/25/13
 */
public class LocalPaginatedStorageMixCrashRestoreCT {
    private ODatabaseDocumentTx baseDocumentTx;

    private ODatabaseDocumentTx testDocumentTx;

    private File buildDir;

    private AtomicInteger idGen = new AtomicInteger();

    private OOneEntryPerKeyLockManager<Integer> idLockManager = new OOneEntryPerKeyLockManager<Integer>(true, 1000, 10000);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Process process;

    private ConcurrentSkipListSet<Integer> addedIds = new ConcurrentSkipListSet<Integer>();

    private ConcurrentSkipListSet<Integer> updatedIds = new ConcurrentSkipListSet<Integer>();

    private ConcurrentHashMap<Integer, Long> deletedIds = new ConcurrentHashMap<Integer, Long>();

    private final AtomicLong lastOperation = new AtomicLong();

    @Test
    public void testDocumentChanges() throws Exception {
        createSchema(baseDocumentTx);
        createSchema(testDocumentTx);
        System.out.println("Schema was created.");
        System.out.println("Document creation was started.");
        createDocuments();
        System.out.println("Document creation was finished.");
        System.out.println("Start data changes.");
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 8; i++) {
            futures.add(executorService.submit(new LocalPaginatedStorageMixCrashRestoreCT.DataChangeTask(baseDocumentTx, testDocumentTx)));
        }
        System.out.println("Wait for 5 minutes");
        TimeUnit.MINUTES.sleep(5);
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
        System.out.println("Data changes were stopped.");
        System.out.println(((((((addedIds.size()) + " records were added. ") + (updatedIds.size())) + " were updated. ") + (deletedIds.size())) + " were deleted."));
        testDocumentTx = new ODatabaseDocumentTx(("plocal:" + (new File(new File(buildDir, "databases"), "testLocalPaginatedStorageMixCrashRestore").getCanonicalPath())));
        testDocumentTx.open("admin", "admin");
        testDocumentTx.close();
        testDocumentTx.open("admin", "admin");
        System.out.println("Start documents comparison.");
        compareDocuments(lastOperation.get());
    }

    public static final class RemoteDBRunner {
        public static void main(String[] args) throws Exception {
            OServer server = OServerMain.create();
            server.startup(LocalPaginatedStorageMixCrashRestoreCT.RemoteDBRunner.class.getResourceAsStream("/com/orientechnologies/orient/core/storage/impl/local/paginated/db-mix-config.xml"));
            server.activate();
            final String mutexFile = System.getProperty("mutexFile");
            final RandomAccessFile mutex = new RandomAccessFile(mutexFile, "rw");
            mutex.seek(0);
            mutex.write(1);
            mutex.close();
        }
    }

    public class DataChangeTask implements Callable<Void> {
        private ODatabaseDocumentTx baseDB;

        private ODatabaseDocumentTx testDB;

        private Random random = new Random();

        public DataChangeTask(ODatabaseDocumentTx baseDB, ODatabaseDocumentTx testDocumentTx) {
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
                    double rndOutcome = random.nextDouble();
                    int actionType = -1;
                    if (rndOutcome <= 0.2) {
                        if (((addedIds.size()) + (updatedIds.size())) >= 100000)
                            actionType = 2;
                        else
                            actionType = 0;

                    } else
                        if ((rndOutcome > 0.2) && (rndOutcome <= 0.6))
                            actionType = 1;
                        else
                            if (rndOutcome > 0.6) {
                                if (((addedIds.size()) + (updatedIds.size())) <= 2000000)
                                    actionType = 0;
                                else
                                    actionType = 2;

                            }


                    long ts = -1;
                    switch (actionType) {
                        case 0 :
                            ts = createRecord();
                            break;
                        case 1 :
                            ts = updateRecord();
                            break;
                        case 2 :
                            ts = deleteRecord();
                            break;
                        default :
                            throw new IllegalStateException(("Invalid action type " + actionType));
                    }
                    long currentTs = lastOperation.get();
                    while ((currentTs < ts) && (!(lastOperation.compareAndSet(currentTs, ts)))) {
                        currentTs = lastOperation.get();
                    } 
                } 
            } finally {
                baseDB.activateOnCurrentThread();
                baseDB.close();
                testDB.activateOnCurrentThread();
                testDB.close();
            }
        }

        private long createRecord() {
            long ts = -1;
            final int idToCreate = idGen.getAndIncrement();
            idLockManager.acquireLock(idToCreate, EXCLUSIVE);
            try {
                final ODocument document = new ODocument("TestClass");
                document.field("id", idToCreate);
                ts = System.currentTimeMillis();
                document.field("timestamp", ts);
                document.field("stringValue", ("sfe" + (random.nextLong())));
                saveDoc(document, baseDB, testDB);
                addedIds.add(document.<Integer>field("id"));
            } finally {
                idLockManager.releaseLock(Thread.currentThread(), idToCreate, EXCLUSIVE);
            }
            return ts;
        }

        private long deleteRecord() {
            int closeId = random.nextInt(idGen.get());
            Integer idToDelete = addedIds.ceiling(closeId);
            if (idToDelete == null)
                idToDelete = addedIds.first();

            idLockManager.acquireLock(idToDelete, EXCLUSIVE);
            while (deletedIds.containsKey(idToDelete)) {
                idLockManager.releaseLock(Thread.currentThread(), idToDelete, EXCLUSIVE);
                closeId = random.nextInt(idGen.get());
                idToDelete = addedIds.ceiling(closeId);
                if (idToDelete == null)
                    idToDelete = addedIds.first();

                idLockManager.acquireLock(idToDelete, EXCLUSIVE);
            } 
            addedIds.remove(idToDelete);
            updatedIds.remove(idToDelete);
            ODatabaseRecordThreadLocal.instance().set(baseDB);
            int deleted = baseDB.command(new OCommandSQL(("delete from TestClass where id  = " + idToDelete))).execute();
            Assert.assertEquals(deleted, 1);
            ODatabaseRecordThreadLocal.instance().set(testDB);
            deleted = testDB.command(new OCommandSQL(("delete from TestClass where id  = " + idToDelete))).execute();
            Assert.assertEquals(deleted, 1);
            ODatabaseRecordThreadLocal.instance().set(baseDB);
            long ts = System.currentTimeMillis();
            deletedIds.put(idToDelete, ts);
            idLockManager.releaseLock(Thread.currentThread(), idToDelete, EXCLUSIVE);
            return ts;
        }

        private long updateRecord() {
            int closeId = random.nextInt(idGen.get());
            Integer idToUpdate = addedIds.ceiling(closeId);
            if (idToUpdate == null)
                idToUpdate = addedIds.first();

            idLockManager.acquireLock(idToUpdate, EXCLUSIVE);
            while (deletedIds.containsKey(idToUpdate)) {
                idLockManager.releaseLock(Thread.currentThread(), idToUpdate, EXCLUSIVE);
                closeId = random.nextInt(idGen.get());
                idToUpdate = addedIds.ceiling(closeId);
                if (idToUpdate == null)
                    idToUpdate = addedIds.first();

                idLockManager.acquireLock(idToUpdate, EXCLUSIVE);
            } 
            addedIds.remove(idToUpdate);
            baseDB.activateOnCurrentThread();
            List<ODocument> documentsToUpdate = baseDB.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(("select from TestClass where id  = " + idToUpdate)));
            Assert.assertTrue((!(documentsToUpdate.isEmpty())));
            final ODocument documentToUpdate = documentsToUpdate.get(0);
            long ts = System.currentTimeMillis();
            documentToUpdate.field("timestamp", ts);
            documentToUpdate.field("stringValue", ("vde" + (random.nextLong())));
            saveDoc(documentToUpdate, baseDB, testDB);
            updatedIds.add(idToUpdate);
            idLockManager.releaseLock(Thread.currentThread(), idToUpdate, EXCLUSIVE);
            return ts;
        }
    }
}

