package com.netflix.astyanax.recipes;


import AllRowsReader.Builder;
import ConsistencyLevel.CL_ONE;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.recipes.functions.ColumnCounterFunction;
import com.netflix.astyanax.recipes.functions.RowCopierFunction;
import com.netflix.astyanax.recipes.functions.RowCounterFunction;
import com.netflix.astyanax.recipes.functions.TraceFunction;
import com.netflix.astyanax.recipes.locks.ColumnPrefixDistributedRowLock;
import com.netflix.astyanax.recipes.locks.StaleLockException;
import com.netflix.astyanax.recipes.reader.AllRowsReader;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.TimeUUIDSerializer;
import com.netflix.astyanax.util.TimeUUIDUtils;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MiscUnitTest {
    private static Logger LOG = LoggerFactory.getLogger(MiscUnitTest.class);

    /**
     * Constants
     */
    private static final long CASSANDRA_WAIT_TIME = 3000;

    private static final int TTL = 20;

    private static final String TEST_CLUSTER_NAME = "cass_sandbox";

    private static final String TEST_KEYSPACE_NAME = "AstyanaxUnitTests_MiscRecipes";

    private static final String SEEDS = "localhost:9160";

    private static final int ALL_ROWS_COUNT = 10000;

    /**
     * Column Family definitions
     */
    public static ColumnFamily<String, UUID> CF_USER_UNIQUE_UUID = ColumnFamily.newColumnFamily("UserUniqueUUID", StringSerializer.get(), TimeUUIDSerializer.get());

    public static ColumnFamily<String, UUID> CF_EMAIL_UNIQUE_UUID = ColumnFamily.newColumnFamily("EmailUniqueUUID", StringSerializer.get(), TimeUUIDSerializer.get());

    private static ColumnFamily<String, String> LOCK_CF_LONG = ColumnFamily.newColumnFamily("LockCfLong", StringSerializer.get(), StringSerializer.get(), LongSerializer.get());

    private static ColumnFamily<String, String> LOCK_CF_STRING = ColumnFamily.newColumnFamily("LockCfString", StringSerializer.get(), StringSerializer.get(), StringSerializer.get());

    private static ColumnFamily<String, String> UNIQUE_CF = ColumnFamily.newColumnFamily("UniqueCf", StringSerializer.get(), StringSerializer.get());

    public static ColumnFamily<String, String> CF_STANDARD1 = ColumnFamily.newColumnFamily("Standard1", StringSerializer.get(), StringSerializer.get());

    public static ColumnFamily<String, String> CF_STANDARD1_COPY = ColumnFamily.newColumnFamily("Standard1_COPY", StringSerializer.get(), StringSerializer.get());

    public static ColumnFamily<Integer, Integer> CF_ALL_ROWS = ColumnFamily.newColumnFamily("AllRowsMiscUnitTest", IntegerSerializer.get(), IntegerSerializer.get());

    public static ColumnFamily<Integer, Integer> CF_ALL_ROWS_COPY = ColumnFamily.newColumnFamily("AllRowsMiscUnitTestCopy", IntegerSerializer.get(), IntegerSerializer.get());

    /**
     * Interal
     */
    private static Keyspace keyspace;

    private static AstyanaxContext<Keyspace> keyspaceContext;

    @Test
    public void testMultiRowUniqueness() {
        DedicatedMultiRowUniquenessConstraint<UUID> constraint = new DedicatedMultiRowUniquenessConstraint<UUID>(MiscUnitTest.keyspace, TimeUUIDUtils.getUniqueTimeUUIDinMicros()).withConsistencyLevel(CL_ONE).withRow(MiscUnitTest.CF_USER_UNIQUE_UUID, "user1").withRow(MiscUnitTest.CF_EMAIL_UNIQUE_UUID, "user1@domain.com");
        DedicatedMultiRowUniquenessConstraint<UUID> constraint2 = new DedicatedMultiRowUniquenessConstraint<UUID>(MiscUnitTest.keyspace, TimeUUIDUtils.getUniqueTimeUUIDinMicros()).withConsistencyLevel(CL_ONE).withRow(MiscUnitTest.CF_USER_UNIQUE_UUID, "user1").withRow(MiscUnitTest.CF_EMAIL_UNIQUE_UUID, "user1@domain.com");
        try {
            Column<UUID> c = constraint.getUniqueColumn();
            Assert.fail();
        } catch (Exception e) {
            MiscUnitTest.LOG.info(e.getMessage());
        }
        try {
            constraint.acquire();
            Column<UUID> c = constraint.getUniqueColumn();
            MiscUnitTest.LOG.info(("Unique column is " + (c.getName())));
            try {
                constraint2.acquire();
                Assert.fail("Should already be acquired");
            } catch (NotUniqueException e) {
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            } finally {
                try {
                    constraint2.release();
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            try {
                constraint.release();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
        try {
            constraint2.acquire();
            Column<UUID> c = constraint.getUniqueColumn();
            MiscUnitTest.LOG.info(("Unique column is " + (c.getName())));
        } catch (NotUniqueException e) {
            Assert.fail("Should already be unique");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            try {
                constraint2.release();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
    }

    // @Test
    // public void testAllRowsReaderConcurrency() throws Exception {
    // final AtomicLong counter = new AtomicLong(0);
    // 
    // boolean result = new AllRowsReader.Builder<String, String>(keyspace, CF_STANDARD1)
    // .withConcurrencyLevel(4)
    // .forEachRow(new Function<Row<String, String>, Boolean>() {
    // @Override
    // public Boolean apply(@Nullable Row<String, String> row) {
    // counter.incrementAndGet();
    // LOG.info("Got a row: " + row.getKey().toString());
    // return true;
    // }
    // })
    // .build()
    // .call();
    // 
    // Assert.assertTrue(result);
    // Assert.assertEquals(28, counter.get());
    // }
    @Test
    public void testTtl() throws Exception {
        ColumnPrefixDistributedRowLock<String> lock = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_LONG, "testTtl").withTtl(2).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        try {
            lock.acquire();
            Assert.assertEquals(1, lock.readLockColumns().size());
            Thread.sleep(3000);
            Assert.assertEquals(0, lock.readLockColumns().size());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            lock.release();
        }
        Assert.assertEquals(0, lock.readLockColumns().size());
    }

    @Test
    public void testTtlString() throws Exception {
        ColumnPrefixDistributedRowLock<String> lock = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_STRING, "testTtl").withTtl(2).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        try {
            lock.acquire();
            Assert.assertEquals(1, lock.readLockColumns().size());
            Thread.sleep(3000);
            Assert.assertEquals(0, lock.readLockColumns().size());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            lock.release();
        }
        Assert.assertEquals(0, lock.readLockColumns().size());
    }

    @Test
    public void testStaleLockWithFail() throws Exception {
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_LONG, "testStaleLock").withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_LONG, "testStaleLock").withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
        try {
            lock1.acquire();
            Thread.sleep(5000);
            try {
                lock2.acquire();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            } finally {
                lock2.release();
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            lock1.release();
        }
    }

    @Test
    public void testStaleLockWithFail_String() throws Exception {
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_STRING, "testStaleLock").withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_STRING, "testStaleLock").withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
        try {
            lock1.acquire();
            Thread.sleep(5000);
            try {
                lock2.acquire();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            } finally {
                lock2.release();
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            lock1.release();
        }
    }

    @Test
    public void testStaleLock() throws Exception {
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_LONG, "testStaleLock").withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
        try {
            lock1.acquire();
            Thread.sleep(2000);
            try {
                lock2.acquire();
                Assert.fail();
            } catch (StaleLockException e) {
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            } finally {
                lock2.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            lock1.release();
        }
    }

    @Test
    public void testStaleLock_String() throws Exception {
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(MiscUnitTest.keyspace, MiscUnitTest.LOCK_CF_STRING, "testStaleLock").withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = withTtl(MiscUnitTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
        try {
            lock1.acquire();
            Thread.sleep(2000);
            try {
                lock2.acquire();
                Assert.fail();
            } catch (StaleLockException e) {
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            } finally {
                lock2.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            lock1.release();
        }
    }

    @Test
    public void testMultiLock() {
        MultiRowUniquenessConstraint unique = withTtl(60).withLockId("abc").withColumnPrefix("prefix_").withRow(MiscUnitTest.UNIQUE_CF, "testMultiLock_A").withRow(MiscUnitTest.UNIQUE_CF, "testMultiLock_B");
        ColumnPrefixUniquenessConstraint<String> singleUnique = new ColumnPrefixUniquenessConstraint<String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, "testMultiLock_A").withConsistencyLevel(CL_ONE).withPrefix("prefix_");
        try {
            unique.acquire();
            String uniqueColumn = singleUnique.readUniqueColumn();
            Assert.assertEquals("abc", uniqueColumn);
            MiscUnitTest.LOG.info(("UniqueColumn: " + uniqueColumn));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        MultiRowUniquenessConstraint unique2 = withTtl(60).withConsistencyLevel(CL_ONE).withColumnPrefix("prefix_").withRow(MiscUnitTest.UNIQUE_CF, "testMultiLock_B");
        try {
            unique2.acquire();
            Assert.fail();
        } catch (Exception e) {
            MiscUnitTest.LOG.info(e.getMessage());
        }
        try {
            Assert.assertEquals("abc", singleUnique.readUniqueColumn());
            unique.release();
        } catch (Exception e) {
            MiscUnitTest.LOG.error(e.getMessage());
            Assert.fail();
        }
        try {
            unique2.acquire();
        } catch (Exception e) {
            MiscUnitTest.LOG.error(e.getMessage());
            Assert.fail();
        }
        try {
            unique2.release();
        } catch (Exception e) {
            MiscUnitTest.LOG.error(e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void testRowUniquenessConstraint() throws Exception {
        RowUniquenessConstraint<String, String> unique = new RowUniquenessConstraint<String, String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, "testRowUniquenessConstraint", UUIDStringSupplier.getInstance()).withConsistencyLevel(CL_ONE);
        RowUniquenessConstraint<String, String> unique2 = new RowUniquenessConstraint<String, String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, "testRowUniquenessConstraint", UUIDStringSupplier.getInstance()).withConsistencyLevel(CL_ONE);
        try {
            unique.withData("abc").acquire();
            try {
                unique2.acquire();
                Assert.fail();
            } catch (Exception e) {
                MiscUnitTest.LOG.info(e.getMessage());
            }
            String data = unique.readDataAsString();
            Assert.assertNotNull(data);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            MiscUnitTest.LOG.error(e.getMessage());
        } finally {
            unique.release();
        }
        try {
            String data = unique.readDataAsString();
            Assert.fail();
        } catch (Exception e) {
            MiscUnitTest.LOG.info("", e);
        }
    }

    @Test
    public void testPrefixUniquenessConstraint() throws Exception {
        ColumnPrefixUniquenessConstraint<String> unique = new ColumnPrefixUniquenessConstraint<String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, "testPrefixUniquenessConstraint").withConsistencyLevel(CL_ONE);
        ColumnPrefixUniquenessConstraint<String> unique2 = new ColumnPrefixUniquenessConstraint<String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, "testPrefixUniquenessConstraint").withConsistencyLevel(CL_ONE);
        try {
            unique.acquire();
            String column = unique.readUniqueColumn();
            MiscUnitTest.LOG.info(("Unique Column: " + column));
            try {
                unique2.acquire();
                Assert.fail();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            MiscUnitTest.LOG.error(e.getMessage());
        } finally {
            unique.release();
        }
        try {
            String column = unique.readUniqueColumn();
            MiscUnitTest.LOG.info(column);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testPrefixUniquenessConstraintWithColumn() throws Exception {
        ColumnPrefixUniquenessConstraint<String> unique = new ColumnPrefixUniquenessConstraint<String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, "testPrefixUniquenessConstraintWithColumn").withConsistencyLevel(CL_ONE).withUniqueId("abc");
        ColumnPrefixUniquenessConstraint<String> unique2 = new ColumnPrefixUniquenessConstraint<String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, "testPrefixUniquenessConstraintWithColumn").withConsistencyLevel(CL_ONE).withUniqueId("def");
        try {
            unique.acquire();
            String column = unique.readUniqueColumn();
            MiscUnitTest.LOG.info(("Unique Column: " + column));
            Assert.assertEquals("abc", column);
            try {
                unique2.acquire();
                Assert.fail();
            } catch (Exception e) {
            }
            column = unique.readUniqueColumn();
            MiscUnitTest.LOG.info(("Unique Column: " + column));
            Assert.assertEquals("abc", column);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            MiscUnitTest.LOG.error(e.getMessage());
        } finally {
            unique.release();
        }
    }

    @Test
    public void testAcquireAndMutate() throws Exception {
        final String row = "testAcquireAndMutate";
        final String dataColumn = "data";
        final String value = "test";
        ColumnPrefixUniquenessConstraint<String> unique = new ColumnPrefixUniquenessConstraint<String>(MiscUnitTest.keyspace, MiscUnitTest.UNIQUE_CF, row).withConsistencyLevel(CL_ONE).withUniqueId("def");
        try {
            unique.acquireAndApplyMutation(new Function<MutationBatch, Boolean>() {
                @Override
                public Boolean apply(MutationBatch m) {
                    m.withRow(MiscUnitTest.UNIQUE_CF, row).putColumn(dataColumn, value, null);
                    return true;
                }
            });
            String column = unique.readUniqueColumn();
            Assert.assertNotNull(column);
        } catch (Exception e) {
            e.printStackTrace();
            MiscUnitTest.LOG.error("", e);
            Assert.fail();
        } finally {
        }
        ColumnList<String> columns = MiscUnitTest.keyspace.prepareQuery(MiscUnitTest.UNIQUE_CF).getKey(row).execute().getResult();
        Assert.assertEquals(2, columns.size());
        Assert.assertEquals(value, columns.getStringValue(dataColumn, null));
        unique.release();
        columns = MiscUnitTest.keyspace.prepareQuery(MiscUnitTest.UNIQUE_CF).getKey(row).execute().getResult();
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals(value, columns.getStringValue(dataColumn, null));
    }

    // @Test
    // public void testAllRowsReader() throws Exception {
    // final AtomicLong counter = new AtomicLong(0);
    // 
    // boolean result = new AllRowsReader.Builder<String, String>(keyspace, CF_STANDARD1)
    // .forEachRow(new Function<Row<String, String>, Boolean>() {
    // @Override
    // public Boolean apply(@Nullable Row<String, String> row) {
    // counter.incrementAndGet();
    // LOG.info("Got a row: " + row.getKey().toString());
    // return true;
    // }
    // })
    // .build()
    // .call();
    // 
    // Assert.assertTrue(result);
    // Assert.assertEquals(28, counter.get());
    // }
    @Test
    public void testAllRowsReader() throws Exception {
        final AtomicLong counter = new AtomicLong(0);
        AllRowsReader<String, String> reader = // .withPartitioner(new Murmur3Partitioner())
        new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1).withPageSize(3).withConcurrencyLevel(2).forEachRow(new Function<Row<String, String>, Boolean>() {
            @Override
            public Boolean apply(Row<String, String> row) {
                counter.incrementAndGet();
                MiscUnitTest.LOG.info(("Got a row: " + (row.getKey().toString())));
                return true;
            }
        }).build();
        try {
            boolean result = reader.call();
            Assert.assertEquals(counter.get(), 27);
            Assert.assertTrue(result);
        } catch (Exception e) {
            MiscUnitTest.LOG.info(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAllRowsReaderCopier() throws Exception {
        final ColumnCounterFunction columnCounter = new ColumnCounterFunction();
        final RowCounterFunction rowCounter = new RowCounterFunction();
        new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1).withPageSize(3).withConcurrencyLevel(2).forEachRow(columnCounter).build().call();
        MiscUnitTest.LOG.info(("Column count = " + (columnCounter.getCount())));
        new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1).withPageSize(3).withConcurrencyLevel(2).forEachRow(rowCounter).build().call();
        MiscUnitTest.LOG.info(("Row count = " + (rowCounter.getCount())));
        new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1).withPageSize(3).withConcurrencyLevel(2).forEachRow(RowCopierFunction.builder(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1_COPY).build()).build().call();
        rowCounter.reset();
        new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1_COPY).withPageSize(3).withConcurrencyLevel(2).forEachRow(rowCounter).build().call();
        MiscUnitTest.LOG.info(("Copied row count = " + (rowCounter.getCount())));
        MiscUnitTest.LOG.info("CF_STANDARD1");
        new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1).withPageSize(3).withConcurrencyLevel(2).forEachRow(TraceFunction.builder(MiscUnitTest.CF_STANDARD1_COPY).build()).build().call();
        MiscUnitTest.LOG.info("CF_STANDARD1_COPY");
        new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1_COPY).withPageSize(3).withConcurrencyLevel(2).forEachRow(TraceFunction.builder(MiscUnitTest.CF_STANDARD1_COPY).build()).build().call();
    }

    @Test
    public void testAllRowsReaderConcurrency12() throws Exception {
        final AtomicLong counter = new AtomicLong(0);
        final Map<Long, AtomicLong> threadIds = Maps.newHashMap();
        AllRowsReader<Integer, Integer> reader = new Builder<Integer, Integer>(MiscUnitTest.keyspace, MiscUnitTest.CF_ALL_ROWS).withPageSize(100).withConcurrencyLevel(12).withColumnSlice(0).forEachRow(new Function<Row<Integer, Integer>, Boolean>() {
            @Override
            public synchronized Boolean apply(Row<Integer, Integer> row) {
                long threadId = Thread.currentThread().getId();
                AtomicLong threadCounter = threadIds.get(threadId);
                if (threadCounter == null) {
                    threadCounter = new AtomicLong(0);
                    threadIds.put(threadId, threadCounter);
                }
                threadCounter.incrementAndGet();
                counter.incrementAndGet();
                return true;
            }
        }).build();
        try {
            Stopwatch sw = Stopwatch.createStarted();
            boolean result = reader.call();
            long runtimeMillis = sw.stop().elapsed(TimeUnit.MILLISECONDS);
            MiscUnitTest.LOG.info(((("Count = " + (counter.get())) + " runtime=") + runtimeMillis));
            MiscUnitTest.LOG.info(((("ThreadIds (" + (threadIds.size())) + ") ") + threadIds));
            Assert.assertEquals(threadIds.size(), 12);
            Assert.assertEquals(counter.get(), MiscUnitTest.ALL_ROWS_COUNT);
            Assert.assertTrue(result);
        } catch (Exception e) {
            MiscUnitTest.LOG.info(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAllRowsReaderWithCancel() throws Exception {
        final AtomicLong counter = new AtomicLong(0);
        AllRowsReader<String, String> reader = new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1).withPageSize(3).withConcurrencyLevel(2).forEachRow(new Function<Row<String, String>, Boolean>() {
            @Override
            public Boolean apply(Row<String, String> row) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                counter.incrementAndGet();
                MiscUnitTest.LOG.info(("Got a row: " + (row.getKey().toString())));
                return true;
            }
        }).build();
        Future<Boolean> future = Executors.newSingleThreadExecutor().submit(reader);
        Thread.sleep(1000);
        reader.cancel();
        try {
            boolean result = future.get();
            Assert.assertEquals(false, result);
        } catch (Exception e) {
            MiscUnitTest.LOG.info("Failed to execute", e);
        }
        MiscUnitTest.LOG.info(("Before: " + (counter.get())));
        Assert.assertNotSame(28, counter.get());
        Thread.sleep(2000);
        MiscUnitTest.LOG.info(("After: " + (counter.get())));
        Assert.assertNotSame(28, counter.get());
    }

    @Test
    public void testAllRowsReaderWithException() throws Exception {
        AllRowsReader<String, String> reader = new Builder<String, String>(MiscUnitTest.keyspace, MiscUnitTest.CF_STANDARD1).withPageSize(3).withConcurrencyLevel(2).forEachRow(new Function<Row<String, String>, Boolean>() {
            @Override
            public Boolean apply(Row<String, String> row) {
                throw new RuntimeException("Very bad");
            }
        }).build();
        Future<Boolean> future = Executors.newSingleThreadExecutor().submit(reader);
        try {
            boolean result = future.get();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Very bad"));
            MiscUnitTest.LOG.info("Failed to execute", e);
        }
    }
}

