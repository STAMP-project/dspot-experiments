package com.netflix.astyanax.recipes;


import ConsistencyLevel.CL_ONE;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.recipes.locks.ColumnPrefixDistributedRowLock;
import com.netflix.astyanax.recipes.locks.StaleLockException;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.test.EmbeddedCassandra;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Ignore for now because of issues with running embedded cassandra from multiple unit tests
 *
 * @author elandau
 */
@Ignore
public class LockRecipeTest {
    private static ColumnFamily<String, String> LOCK_CF_LONG = ColumnFamily.newColumnFamily("LockCfLong", StringSerializer.get(), StringSerializer.get(), LongSerializer.get());

    private static ColumnFamily<String, String> LOCK_CF_STRING = ColumnFamily.newColumnFamily("LockCfString", StringSerializer.get(), StringSerializer.get(), StringSerializer.get());

    private static final int TTL = 20;

    private static final int TIMEOUT = 10;

    private static final String SEEDS = "localhost:9160";

    private static final long CASSANDRA_WAIT_TIME = 3000;

    private static Keyspace keyspace;

    private static AstyanaxContext<Keyspace> keyspaceContext;

    private static EmbeddedCassandra cassandra;

    private static String TEST_CLUSTER_NAME = "cass_sandbox";

    private static String TEST_KEYSPACE_NAME = "LockUnitTest";

    @Test
    public void testTtl() throws Exception {
        ColumnPrefixDistributedRowLock<String> lock = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_LONG, "testTtl").withTtl(2).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
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
        ColumnPrefixDistributedRowLock<String> lock = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_STRING, "testTtl").withTtl(2).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
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
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_LONG, "testStaleLock").withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_LONG, "testStaleLock").withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
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
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_STRING, "testStaleLock").withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_STRING, "testStaleLock").withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
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
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_LONG, "testStaleLock").withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_LONG, "testStaleLock").failOnStaleLock(true).withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
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
        ColumnPrefixDistributedRowLock<String> lock1 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_STRING, "testStaleLock").withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(1, TimeUnit.SECONDS);
        ColumnPrefixDistributedRowLock<String> lock2 = new ColumnPrefixDistributedRowLock<String>(LockRecipeTest.keyspace, LockRecipeTest.LOCK_CF_STRING, "testStaleLock").failOnStaleLock(true).withTtl(LockRecipeTest.TTL).withConsistencyLevel(CL_ONE).expireLockAfter(9, TimeUnit.SECONDS);
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
    public void testLockAndMutate() throws Exception {
        // String rowKey     = "testLockAndMutate";
        // String dataColumn = "SomeDataColumn";
        // Integer value     = 1;
        // // Write some data
        // try {
        // MutationBatch m = keyspace.prepareMutationBatch().setConsistencyLevel(ConsistencyLevel.CL_ONE);
        // m.withRow(LOCK_CF_LONG, rowKey)
        // .putColumn(dataColumn, value, null);
        // m.execute();
        // }
        // catch (Exception e) {
        // e.printStackTrace();
        // Assert.fail(e.getMessage());
        // }
        // 
        // // Take a lock
        // ColumnPrefixDistributedRowLock<String> lock =
        // new ColumnPrefixDistributedRowLock<String>(keyspace, LOCK_CF_LONG, rowKey)
        // .expireLockAfter(1, TimeUnit.SECONDS);
        // 
        // try {
        // ColumnMap<String> columns = lock
        // .withColumnPrefix("$lock$_")
        // .withLockId("myLockId")
        // .withConsistencyLevel(ConsistencyLevel.CL_ONE)
        // .acquireLockAndReadRow();
        // 
        // // Read data and update
        // Assert.assertNotNull(columns);
        // Assert.assertEquals(1, columns.size());
        // 
        // value = columns.get(dataColumn).getIntegerValue() + 1;
        // MutationBatch m = keyspace.prepareMutationBatch();
        // m.withRow(LOCK_CF_LONG, rowKey)
        // .putColumn(dataColumn, value, null);
        // 
        // // Write data and release the lock
        // lock.releaseWithMutation(m);
        // }
        // catch (Exception e) {
        // e.printStackTrace();
        // Assert.fail(e.getMessage());
        // lock.release();
        // }
        // 
        // ColumnList<String> columns = keyspace
        // .prepareQuery(LOCK_CF_LONG)
        // .setConsistencyLevel(ConsistencyLevel.CL_ONE)
        // .getKey(rowKey)
        // .execute()
        // .getResult();
        // Assert.assertEquals(1, columns.size());
        // Assert.assertEquals(value, columns.getIntegerValue(dataColumn, 0));
    }
}

