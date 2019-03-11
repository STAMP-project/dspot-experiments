package com.orientechnologies.orient.core.storage.impl.local.paginated;


import OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import OGlobalConfiguration.RID_BAG_SBTREEBONSAI_TO_EMBEDDED_THRESHOLD;
import OGlobalConfiguration.WAL_FUZZY_CHECKPOINT_INTERVAL;
import OOneEntryPerKeyLockManager.LOCK.EXCLUSIVE;
import com.orientechnologies.common.concur.lock.OOneEntryPerKeyLockManager;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentHelper;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class LocalPaginatedStorageLinkBagCrashRestoreCT {
    private static String URL_BASE;

    private static String URL_TEST;

    private final OOneEntryPerKeyLockManager<ORID> lockManager = new OOneEntryPerKeyLockManager<ORID>(true, 30000, 10000);

    private final AtomicInteger positionCounter = new AtomicInteger();

    private final OPartitionedDatabasePoolFactory poolFactory = new OPartitionedDatabasePoolFactory();

    private File buildDir;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private Process process;

    private int defaultClusterId;

    private volatile long lastClusterPosition;

    @Test
    public void testDocumentCreation() throws Exception {
        final OServerAdmin serverAdmin = new OServerAdmin("remote:localhost:3500");
        serverAdmin.connect("root", "root");
        serverAdmin.createDatabase("testLocalPaginatedStorageLinkBagCrashRestore", "graph", "plocal");
        serverAdmin.close();
        ODatabaseDocumentTx base_db = new ODatabaseDocumentTx((("plocal:" + (buildDir)) + "/baseLocalPaginatedStorageLinkBagCrashRestore"));
        if (base_db.exists()) {
            base_db.open("admin", "admin");
            base_db.drop();
        }
        base_db.create();
        LocalPaginatedStorageLinkBagCrashRestoreCT.URL_BASE = base_db.getURL();
        defaultClusterId = base_db.getDefaultClusterId();
        base_db.close();
        LocalPaginatedStorageLinkBagCrashRestoreCT.URL_TEST = "remote:localhost:3500/testLocalPaginatedStorageLinkBagCrashRestore";
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        futures.add(executorService.submit(new LocalPaginatedStorageLinkBagCrashRestoreCT.DocumentAdder()));
        TimeUnit.SECONDS.sleep(1);
        for (int i = 0; i < 5; i++)
            futures.add(executorService.submit(new LocalPaginatedStorageLinkBagCrashRestoreCT.RidAdder()));

        for (int i = 0; i < 5; i++)
            futures.add(executorService.submit(new LocalPaginatedStorageLinkBagCrashRestoreCT.RidDeleter()));

        System.out.println("Wait for 5 minutes");
        TimeUnit.MINUTES.sleep(5);
        long lastTs = System.currentTimeMillis();
        ODatabaseDocumentTx test_db = poolFactory.get(LocalPaginatedStorageLinkBagCrashRestoreCT.URL_TEST, "admin", "admin").acquire();
        System.out.println(test_db.countClusterElements(test_db.getDefaultClusterId()));
        test_db.close();
        System.out.println("Wait for process to destroy");
        process.destroyForcibly();
        process.waitFor();
        System.out.println("Process was destroyed");
        for (Future<Void> future : futures)
            try {
                future.get();
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
            }

        compareDocuments(lastTs);
    }

    public static final class RemoteDBRunner {
        public static void main(String[] args) throws Exception {
            WAL_FUZZY_CHECKPOINT_INTERVAL.setValue(5);
            RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(30);
            RID_BAG_SBTREEBONSAI_TO_EMBEDDED_THRESHOLD.setValue(20);
            OServer server = OServerMain.create();
            server.startup(LocalPaginatedStorageLinkBagCrashRestoreCT.RemoteDBRunner.class.getResourceAsStream("/com/orientechnologies/orient/core/storage/impl/local/paginated/db-linkbag-crash-config.xml"));
            server.activate();
            final String mutexFile = System.getProperty("mutexFile");
            final RandomAccessFile mutex = new RandomAccessFile(mutexFile, "rw");
            mutex.seek(0);
            mutex.write(1);
            mutex.close();
        }
    }

    public final class DocumentAdder implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            while (true) {
                final long ts = System.currentTimeMillis();
                try {
                    ODatabaseDocumentTx base_db = poolFactory.get(LocalPaginatedStorageLinkBagCrashRestoreCT.URL_BASE, "admin", "admin").acquire();
                    base_db.activateOnCurrentThread();
                    ODocument base_document = addDocument(ts);
                    ODatabaseDocumentTx test_db = poolFactory.get(LocalPaginatedStorageLinkBagCrashRestoreCT.URL_TEST, "admin", "admin").acquire();
                    test_db.activateOnCurrentThread();
                    ODocument test_document = addDocument(ts);
                    Assert.assertTrue(ODocumentHelper.hasSameContentOf(base_document, base_db, test_document, test_db, null));
                    base_db.activateOnCurrentThread();
                    base_db.close();
                    test_db.activateOnCurrentThread();
                    test_db.close();
                    lastClusterPosition = base_document.getIdentity().getClusterPosition();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    throw e;
                }
            } 
        }

        private ODocument addDocument(long ts) {
            ODocument document = new ODocument();
            ORidBag ridBag = new ORidBag();
            document.field("ridBag", ridBag);
            document.field("ts", ts);
            document.save();
            return document;
        }
    }

    public class RidAdder implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            final Random random = new Random();
            while (true) {
                final long ts = System.currentTimeMillis();
                final int position = random.nextInt(((int) (lastClusterPosition)));
                final ORID orid = new ORecordId(defaultClusterId, position);
                lockManager.acquireLock(orid, EXCLUSIVE);
                try {
                    try {
                        final List<ORID> ridsToAdd = new ArrayList<ORID>(10);
                        for (int i = 0; i < 10; i++)
                            ridsToAdd.add(new ORecordId(0, positionCounter.incrementAndGet()));

                        ODatabaseDocumentTx base_db = poolFactory.get(LocalPaginatedStorageLinkBagCrashRestoreCT.URL_BASE, "admin", "admin").acquire();
                        addRids(orid, base_db, ridsToAdd, ts);
                        base_db.close();
                        ODatabaseDocumentTx test_db = poolFactory.get(LocalPaginatedStorageLinkBagCrashRestoreCT.URL_TEST, "admin", "admin").acquire();
                        addRids(orid, test_db, ridsToAdd, ts);
                        test_db.close();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        throw e;
                    }
                } finally {
                    lockManager.releaseLock(this, orid, EXCLUSIVE);
                }
            } 
        }

        private void addRids(ORID docRid, ODatabaseDocumentTx db, List<ORID> ridsToAdd, long ts) {
            ODocument document = db.load(docRid);
            document.field("ts", ts);
            document.setLazyLoad(false);
            ORidBag ridBag = document.field("ridBag");
            for (ORID rid : ridsToAdd)
                ridBag.add(rid);

            document.save();
        }
    }

    public class RidDeleter implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            final Random random = new Random();
            try {
                while (true) {
                    if ((lastClusterPosition) <= 0)
                        continue;

                    final long ts = System.currentTimeMillis();
                    final long position = random.nextInt(((int) (lastClusterPosition)));
                    final ORID orid = new ORecordId(defaultClusterId, position);
                    lockManager.acquireLock(orid, EXCLUSIVE);
                    try {
                        ODatabaseDocumentTx base_db = poolFactory.get(LocalPaginatedStorageLinkBagCrashRestoreCT.URL_BASE, "admin", "admin").acquire();
                        final List<ORID> ridsToRemove = new ArrayList<ORID>();
                        ODocument document = base_db.load(orid);
                        document.setLazyLoad(false);
                        ORidBag ridBag = document.field("ridBag");
                        for (OIdentifiable identifiable : ridBag) {
                            if (random.nextBoolean())
                                ridsToRemove.add(identifiable.getIdentity());

                            if ((ridsToRemove.size()) >= 5)
                                break;

                        }
                        for (ORID ridToRemove : ridsToRemove)
                            ridBag.remove(ridToRemove);

                        document.field("ts", ts);
                        document.save();
                        base_db.close();
                        ODatabaseDocumentTx test_db = poolFactory.get(LocalPaginatedStorageLinkBagCrashRestoreCT.URL_TEST, "admin", "admin").acquire();
                        document = test_db.load(orid);
                        document.setLazyLoad(false);
                        ridBag = document.field("ridBag");
                        for (ORID ridToRemove : ridsToRemove)
                            ridBag.remove(ridToRemove);

                        document.field("ts", ts);
                        document.save();
                        test_db.close();
                    } finally {
                        lockManager.releaseLock(this, orid, EXCLUSIVE);
                    }
                } 
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}

