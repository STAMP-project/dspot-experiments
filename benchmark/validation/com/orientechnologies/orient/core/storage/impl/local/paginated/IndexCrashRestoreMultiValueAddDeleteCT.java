package com.orientechnologies.orient.core.storage.impl.local.paginated;


import OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import OGlobalConfiguration.WAL_FUZZY_CHECKPOINT_INTERVAL;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
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
 * @since 9/25/14
 */
public class IndexCrashRestoreMultiValueAddDeleteCT {
    private final AtomicLong idGen = new AtomicLong();

    private ODatabaseDocumentTx baseDocumentTx;

    private ODatabaseDocumentTx testDocumentTx;

    private File buildDir;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Process process;

    @Test
    public void testEntriesAddition() throws Exception {
        createSchema(baseDocumentTx);
        createSchema(testDocumentTx);
        System.out.println("Start data propagation");
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 8; i++) {
            futures.add(executorService.submit(new IndexCrashRestoreMultiValueAddDeleteCT.DataPropagationTask(baseDocumentTx, testDocumentTx)));
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
        testDocumentTx = new ODatabaseDocumentTx(("plocal:" + (new File(new File(buildDir.getAbsolutePath(), "databases"), "testIndexCrashRestoreMultivalueAddDelete").getCanonicalPath())));
        testDocumentTx.open("admin", "admin");
        testDocumentTx.close();
        testDocumentTx.open("admin", "admin");
        System.out.println("Start data comparison.");
        compareIndexes();
    }

    public static final class RemoteDBRunner {
        public static void main(String[] args) throws Exception {
            WAL_FUZZY_CHECKPOINT_INTERVAL.setValue(5);
            RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(3);
            OServer server = OServerMain.create();
            server.startup(IndexCrashRestoreMultiValueAddDeleteCT.RemoteDBRunner.class.getResourceAsStream("/com/orientechnologies/orient/core/storage/impl/local/paginated/index-crash-multivalue-value-add-delete-config.xml"));
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
            baseDB.open("admin", "admin");
            testDB.open("admin", "admin");
            try {
                while (true) {
                    long id = idGen.getAndIncrement();
                    long ts = System.currentTimeMillis();
                    baseDB.activateOnCurrentThread();
                    ODocument doc = new ODocument();
                    doc.field("ts", ts);
                    doc.save();
                    baseDB.command(new OCommandSQL((((("insert into index:mi (key, rid) values (" + id) + ", ") + (doc.getIdentity())) + ")"))).execute();
                    testDB.activateOnCurrentThread();
                    for (int i = 0; i < 15; i++) {
                        testDB.command(new OCommandSQL((((("insert into index:mi (key, rid) values (" + id) + ", #0:") + i) + ")"))).execute();
                    }
                    for (int i = 10; i < 15; i++) {
                        testDB.command(new OCommandSQL(((("delete from index:mi where key = " + id) + " and rid = #0:") + i))).execute();
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

