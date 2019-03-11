package com.tinkerpop.blueprints.impls.orient;


import OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import com.orientechnologies.orient.core.id.ORID;
import com.tinkerpop.blueprints.Vertex;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;


public class OrientCommitMTTestIT {
    public static final String DB_URL = "plocal:./avltreetest";

    public static final String DB_USER = "admin";

    public static final String DB_PASSWORD = "admin";

    private static final String TEST_CLASS = "ORIENT_COMMIT_TEST";

    private static final String THREAD_ID = "ThreadId";

    private static final String ID = "IdField";

    private String failureMessage = "";

    private boolean isValidData;

    private OrientCommitMTTestIT.TestExecutor[] threads;

    final int threadCount = 5;

    final int maxSleepTime = 100;

    final int maxOpCount = 6;

    final int initialCacheSize = 10;

    final AtomicInteger idGenerator = new AtomicInteger(1);

    private static Random random = new Random();

    private static OrientGraphFactory factory;

    @Test
    public void testSingleThreadWithTransactionEmbeddedRidBag() {
        RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(Integer.MAX_VALUE);
        try {
            System.setOut(new PrintStream(new File("target/log/CommitTestTransactionalSingleThreadEmbeddedRidBag.txt")));
        } catch (FileNotFoundException e) {
        }
        // set to run 5 minutes with the transaction set to true
        executeTest(1, this.maxSleepTime, this.maxOpCount, this.initialCacheSize, 2);
    }

    @Test
    public void testSingleThreadWithTransactionSBTreeRidBag() {
        RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue((-1));
        try {
            System.setOut(new PrintStream(new File("target/log/CommitTestTransactionalSingleThreadSBTreeRidBag.txt")));
        } catch (FileNotFoundException e) {
        }
        // set to run 5 minutes with the transaction set to true
        executeTest(1, this.maxSleepTime, this.maxOpCount, this.initialCacheSize, 2);
    }

    class TestExecutor implements Runnable {
        private int maxSleepTime;

        private final CountDownLatch endLatch;

        private boolean shutdown;

        private int maxOpCount;

        private final List<OrientCommitMTTestIT.IdPair> cache;

        private final int threadId;

        public TestExecutor(final int threadId, final CountDownLatch endLatch, final int maxSleepTime, final int maxOpCount) {
            this.endLatch = endLatch;
            this.maxSleepTime = maxSleepTime;
            this.maxOpCount = maxOpCount;
            this.shutdown = false;
            this.cache = new ArrayList<OrientCommitMTTestIT.IdPair>();
            this.threadId = threadId;
        }

        public void seedData(final int initialCacheSize) {
            for (int i = 0; i < initialCacheSize; i++) {
                OrientCommitMTTestIT.IdPair newNode = insertNewNode(null);
                ORID recordId = newNode.getOrid();
                Integer id = newNode.getCustomId();
                this.cache.add(new OrientCommitMTTestIT.IdPair(recordId, id));
            }
        }

        public void run() {
            try {
                Thread.sleep(((long) ((Math.random()) * (this.maxSleepTime))));
            } catch (InterruptedException e) {
                // swallow - irrelevant
            }
            try {
                while (!(this.shutdown)) {
                    commitOperations();
                } 
            } finally {
                this.endLatch.countDown();
            }
        }

        /**
         * Perform a set of insert or delete operations (picked at random) with variable transaction flag
         */
        private void commitOperations() {
            OrientGraph graph = OrientCommitMTTestIT.factory.getTx();// new OrientGraph(DB_URL, DB_USER, DB_PASSWORD);

            try {
                List<OrientCommitMTTestIT.TestExecutor.TempCacheObject> tempCache = new ArrayList<OrientCommitMTTestIT.TestExecutor.TempCacheObject>();
                try {
                    // generate random operation list
                    List<OrientCommitMTTestIT.Operation> operations = generateOperations(this.maxOpCount);
                    for (OrientCommitMTTestIT.Operation operation : operations) {
                        if (OrientCommitMTTestIT.Operation.INSERT.equals(operation)) {
                            // perform insert operation
                            OrientCommitMTTestIT.IdPair insertedNode = insertNewNode(graph);
                            ORID insertId = insertedNode.getOrid();
                            // add inserted id to temp cache
                            tempCache.add(new OrientCommitMTTestIT.TestExecutor.TempCacheObject(operation, insertId, insertedNode.getCustomId()));
                        } else
                            if (OrientCommitMTTestIT.Operation.DELETE.equals(operation)) {
                                // get delete id
                                ORID deleteId = getRandomIdForThread(graph);
                                if (deleteId != null) {
                                    // perform delete operation
                                    Integer customId = deleteExistingNode(deleteId, graph);
                                    // add deleted id to temp cache
                                    tempCache.add(new OrientCommitMTTestIT.TestExecutor.TempCacheObject(operation, deleteId, customId));
                                } else {
                                    System.out.println((("ThreadId: " + (this.threadId)) + " no ids in database for thread to delete."));
                                }
                            }

                    }
                    graph.commit();
                } catch (Exception e) {
                    graph.rollback();
                    tempCache.clear();
                    System.out.println(((((("ThreadId: " + (this.threadId)) + " Rolling back transaction due to ") + (e.getClass().getSimpleName())) + " ") + (e.getMessage())));
                    e.printStackTrace(System.out);
                }
                // update permanent cache from temp cache
                updateCache(tempCache);
                validateCustomIdsAgainstDatabase(graph);
                validateDatabase(this.cache, graph);
            } catch (Exception e) {
                System.out.println(((("ThreadId: " + (this.threadId)) + " threw a validation exception: ") + (e.getMessage())));
                e.printStackTrace(System.out);
                // validation failed - set failure message
                setFailureMessage(e.getMessage());
                this.shutdown = true;
            } finally {
                graph.shutdown();
            }
        }

        private void validateCustomIdsAgainstDatabase(OrientGraph graph) throws Exception {
            List<Vertex> recordsInDb = new ArrayList<Vertex>();
            for (Vertex v : graph.getVerticesOfClass(OrientCommitMTTestIT.TEST_CLASS))
                recordsInDb.add(v);

            for (OrientCommitMTTestIT.IdPair cacheInstance : this.cache) {
                Integer customId = cacheInstance.getCustomId();
                boolean found = false;
                for (Vertex vertex : recordsInDb) {
                    if (vertex.getProperty(OrientCommitMTTestIT.ID).equals(customId)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new Exception((("Custom id: " + customId) + " exists in cache but was not found in db."));
                }
            }
        }

        public boolean isShutdown() {
            return this.shutdown;
        }

        /**
         * Verify that all ids in the permanent cache are in the db. Verify that all ids (for a given thread) in the db are in the
         * permanent cache.
         */
        private void validateDatabase(final List<OrientCommitMTTestIT.IdPair> cache, OrientGraph graph) throws Exception {
            for (OrientCommitMTTestIT.IdPair idPair : cache) {
                ORID id = idPair.getOrid();
                if (!(isInDatabase(id, graph))) {
                    throw new Exception((("Insert issue: expected record " + id) + " was not found in database."));
                }
            }
            for (Vertex vertex : graph.getVerticesOfClass(OrientCommitMTTestIT.TEST_CLASS)) {
                if (Integer.valueOf(this.threadId).equals(vertex.getProperty(OrientCommitMTTestIT.THREAD_ID))) {
                    ORID dbId = getIdentity();
                    Integer customId = vertex.getProperty(OrientCommitMTTestIT.ID);
                    if (!(cache.contains(new OrientCommitMTTestIT.IdPair(dbId, customId)))) {
                        throw new Exception((((("Delete issue: record id " + dbId) + " for thread id ") + (this.threadId)) + " was not found in cache."));
                    }
                }
            }
        }

        /**
         * Checks to see if an id for a given thread exist in the db.
         */
        private boolean isInDatabase(final ORID id, OrientGraph orientGraph) throws Exception {
            final OrientVertex vertex = orientGraph.getVertex(id);
            if (vertex != null) {
                if (!(Integer.valueOf(this.threadId).equals(vertex.getProperty(OrientCommitMTTestIT.THREAD_ID)))) {
                    return false;
                }
            }
            return vertex != null;
        }

        /**
         * Add id from the temp cache with insert operation to permanent cache. Remove id from permanent cache that has a delete
         * operation in the temp cache.
         *
         * @param tempCache
         * 		cached objects
         */
        private void updateCache(final List<OrientCommitMTTestIT.TestExecutor.TempCacheObject> tempCache) {
            for (OrientCommitMTTestIT.TestExecutor.TempCacheObject tempCacheObject : tempCache) {
                ORID id = tempCacheObject.getOrientId();
                OrientCommitMTTestIT.Operation operation = tempCacheObject.getOperation();
                Integer customId = tempCacheObject.getCustomId();
                if (OrientCommitMTTestIT.Operation.INSERT.equals(operation)) {
                    this.cache.add(new OrientCommitMTTestIT.IdPair(id, customId));
                } else
                    if (OrientCommitMTTestIT.Operation.DELETE.equals(operation)) {
                        this.cache.remove(new OrientCommitMTTestIT.IdPair(id, customId));
                    }

            }
        }

        /**
         * Insert new node and create edge with the random node in the db.
         */
        private OrientCommitMTTestIT.IdPair insertNewNode(OrientGraph graph) {
            boolean closeDb = false;
            if (graph == null) {
                graph = OrientCommitMTTestIT.factory.getTx();
                closeDb = true;
            }
            try {
                Integer id = OrientCommitMTTestIT.this.idGenerator.getAndIncrement();
                OrientVertex vertex = graph.addVertex(("class:" + (OrientCommitMTTestIT.TEST_CLASS)), OrientCommitMTTestIT.THREAD_ID, Integer.valueOf(this.threadId), OrientCommitMTTestIT.ID, id);
                ORID randomId = getRandomIdForThread(graph);
                if (randomId != null) {
                    OrientVertex v = graph.getVertex(randomId);
                    graph.addEdge(null, vertex, v, "contains");
                }
                ORID newRecordId = vertex.getIdentity();
                return new OrientCommitMTTestIT.IdPair(newRecordId, id);
            } finally {
                if (closeDb)
                    graph.shutdown();

            }
        }

        /**
         * Delete all edges connected to given vertex and then delete vertex.
         */
        private Integer deleteExistingNode(final ORID recordId, OrientGraph graph) {
            OrientVertex vertex = graph.getVertex(recordId);
            Integer customId = vertex.getProperty(OrientCommitMTTestIT.ID);
            vertex.remove();
            return customId;
        }

        /**
         * Get all of the ids from the db for that class for a given thread id. Return id from the list at random.
         */
        private ORID getRandomIdForThread(OrientGraph graph) {
            boolean closeDb = false;
            if (graph == null) {
                graph = OrientCommitMTTestIT.factory.getTx();
                closeDb = true;
            }
            try {
                List<ORID> idsInDb = new ArrayList<ORID>();
                for (Vertex v : graph.getVerticesOfClass(OrientCommitMTTestIT.TEST_CLASS)) {
                    if (Integer.valueOf(this.threadId).equals(v.getProperty(OrientCommitMTTestIT.THREAD_ID))) {
                        idsInDb.add(((OrientVertex) (v)).getIdentity());
                    }
                }
                int size = idsInDb.size();
                if (size == 0) {
                    return null;
                }
                int index = OrientCommitMTTestIT.random.nextInt(size);
                return idsInDb.get(index);
            } finally {
                if (closeDb)
                    graph.shutdown();

            }
        }

        private List<OrientCommitMTTestIT.Operation> generateOperations(final int maxOpCount) {
            List<OrientCommitMTTestIT.Operation> operationsList = new ArrayList<OrientCommitMTTestIT.Operation>();
            int opCount = ((int) ((((Math.random()) * maxOpCount) / 2) + (maxOpCount / 2)));
            for (int index = 0; index < opCount; index++) {
                OrientCommitMTTestIT.Operation op = OrientCommitMTTestIT.Operation.getRandom();
                operationsList.add(op);
            }
            return operationsList;
        }

        private void shutdown() {
            this.shutdown = true;
        }

        private class TempCacheObject {
            private OrientCommitMTTestIT.Operation operation;

            private ORID orientId;

            private Integer customId;

            public TempCacheObject(final OrientCommitMTTestIT.Operation operation, final ORID orientId, final Integer customId) {
                this.operation = operation;
                this.orientId = orientId;
                this.customId = customId;
            }

            public OrientCommitMTTestIT.Operation getOperation() {
                return this.operation;
            }

            public ORID getOrientId() {
                return this.orientId;
            }

            public Integer getCustomId() {
                return this.customId;
            }

            public String toString() {
                return (((("Operation:" + (this.operation)) + ", ORID:") + (this.orientId)) + ", CustomId:") + (this.customId);
            }
        }
    }

    /**
     * Defines two operations types
     */
    private static enum Operation {

        INSERT,
        DELETE;
        /**
         * Picks operation at random
         */
        public static OrientCommitMTTestIT.Operation getRandom() {
            if (0.55 > (Math.random())) {
                return OrientCommitMTTestIT.Operation.INSERT;
            } else {
                return OrientCommitMTTestIT.Operation.DELETE;
            }
        }
    }

    private static class IdPair {
        private ORID orid;

        private Integer customId;

        public IdPair(final ORID orid, final Integer customId) {
            super();
            this.orid = orid;
            this.customId = customId;
        }

        public ORID getOrid() {
            return this.orid;
        }

        public Integer getCustomId() {
            return this.customId;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof OrientCommitMTTestIT.IdPair)) {
                return false;
            }
            OrientCommitMTTestIT.IdPair idPair = ((OrientCommitMTTestIT.IdPair) (obj));
            if (!(idPair.orid.equals(this.orid))) {
                return false;
            }
            if (!(idPair.customId.equals(this.customId))) {
                return false;
            }
            return true;
        }
    }
}

