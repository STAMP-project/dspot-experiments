package com.orientechnologies.orient.core.storage.impl.local.paginated;


import OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import OGlobalConfiguration.WAL_FUZZY_CHECKPOINT_INTERVAL;
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
public class IndexCrashRestoreMultiValueCT {
    private final AtomicLong idGen;

    private final String testIndexName;

    private final String baseIndexName;

    private ODatabaseDocumentTx baseDocumentTx;

    private ODatabaseDocumentTx testDocumentTx;

    private File buildDir;

    private ExecutorService executorService;

    private Process serverProcess;

    public IndexCrashRestoreMultiValueCT() {
        executorService = Executors.newCachedThreadPool();
        idGen = new AtomicLong();
        baseIndexName = "baseIndexCrashRestoreMultivalue";
        testIndexName = "testIndexCrashRestoreMultivalue";
    }

    @Test
    public void testEntriesAddition() throws Exception {
        createSchema(baseDocumentTx);
        createSchema(testDocumentTx);
        System.out.println("Start data propagation");
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 4; i++) {
            futures.add(executorService.submit(new IndexCrashRestoreMultiValueCT.DataPropagationTask(baseDocumentTx, testDocumentTx)));
        }
        System.out.println("Wait for 5 minutes");
        TimeUnit.MINUTES.sleep(5);
        System.out.println("Wait for process to destroy");
        serverProcess.destroyForcibly();
        serverProcess.waitFor();
        System.out.println("Process was destroyed");
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                future.cancel(true);
            }
        }
        System.out.println("All loaders done");
        System.out.println("Open remote crashed DB in plocal to recover");
        testDocumentTx = new ODatabaseDocumentTx(("plocal:" + (new File(new File(buildDir, "databases"), testIndexName).getCanonicalPath())));
        testDocumentTx.open("admin", "admin");
        testDocumentTx.close();
        System.out.println("Reopen cleaned db");
        testDocumentTx.open("admin", "admin");
        System.out.println("Start data comparison.");
        compareIndexes();
    }

    public static final class RemoteDBRunner {
        public static void main(String[] args) throws Exception {
            RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(3);
            WAL_FUZZY_CHECKPOINT_INTERVAL.setValue(100000000);
            OServer server = OServerMain.create();
            server.startup(IndexCrashRestoreMultiValueCT.RemoteDBRunner.class.getResourceAsStream("/com/orientechnologies/orient/core/storage/impl/local/paginated/index-crash-multivalue-value-config.xml"));
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
                    ODatabaseRecordThreadLocal.instance().set(baseDB);
                    ODocument doc = new ODocument();
                    doc.field("ts", ts);
                    doc.save();
                    if ((id % 1000) == 0)
                        System.out.println((((Thread.currentThread().getName()) + " inserted:: ") + id));

                    baseDB.command(new OCommandSQL((((("insert into index:mi (key, rid) values (" + id) + ", ") + (doc.getIdentity())) + ")"))).execute();
                    ODatabaseRecordThreadLocal.instance().set(testDB);
                    for (int i = 0; i < 10; i++) {
                        testDB.command(new OCommandSQL((((("insert into index:mi (key, rid) values (" + id) + ", #0:") + i) + ")"))).execute();
                    }
                } 
            } catch (Exception e) {
                throw e;
            } finally {
                try {
                    baseDB.activateOnCurrentThread();
                    baseDB.close();
                } catch (Exception e) {
                }
                try {
                    testDB.activateOnCurrentThread();
                    testDB.close();
                } catch (Exception e) {
                }
            }
        }
    }
}

