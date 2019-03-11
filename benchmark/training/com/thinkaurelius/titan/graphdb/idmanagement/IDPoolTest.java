package com.thinkaurelius.titan.graphdb.idmanagement;


import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.IDAuthority;
import com.thinkaurelius.titan.diskstorage.IDBlock;
import com.thinkaurelius.titan.diskstorage.TemporaryBackendException;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRange;
import com.thinkaurelius.titan.graphdb.database.idassigner.IDBlockSizer;
import com.thinkaurelius.titan.graphdb.database.idassigner.IDPoolExhaustedException;
import com.thinkaurelius.titan.graphdb.database.idassigner.StandardIDPool;
import java.time.Duration;
import java.util.List;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class IDPoolTest {
    @Test
    public void testStandardIDPool1() throws InterruptedException {
        final MockIDAuthority idauth = new MockIDAuthority(200);
        testIDPoolWith(new IDPoolTest.IDPoolFactory() {
            @Override
            public StandardIDPool get(int partitionID) {
                return new StandardIDPool(idauth, partitionID, partitionID, Integer.MAX_VALUE, Duration.ofMillis(2000L), 0.2);
            }
        }, 1000, 6, 100000);
    }

    @Test
    public void testStandardIDPool2() throws InterruptedException {
        final MockIDAuthority idauth = new MockIDAuthority(10000, Integer.MAX_VALUE, 2000);
        testIDPoolWith(new IDPoolTest.IDPoolFactory() {
            @Override
            public StandardIDPool get(int partitionID) {
                return new StandardIDPool(idauth, partitionID, partitionID, Integer.MAX_VALUE, Duration.ofMillis(4000), 0.1);
            }
        }, 2, 5, 10000);
    }

    @Test
    public void testStandardIDPool3() throws InterruptedException {
        final MockIDAuthority idauth = new MockIDAuthority(200);
        testIDPoolWith(new IDPoolTest.IDPoolFactory() {
            @Override
            public StandardIDPool get(int partitionID) {
                return new StandardIDPool(idauth, partitionID, partitionID, Integer.MAX_VALUE, Duration.ofMillis(2000), 0.2);
            }
        }, 10, 20, 100000);
    }

    @Test
    public void testAllocationTimeout() {
        final MockIDAuthority idauth = new MockIDAuthority(10000, Integer.MAX_VALUE, 5000);
        StandardIDPool pool = new StandardIDPool(idauth, 1, 1, Integer.MAX_VALUE, Duration.ofMillis(4000), 0.1);
        try {
            pool.nextID();
            Assert.fail();
        } catch (TitanException e) {
        }
    }

    @Test
    public void testAllocationTimeoutAndRecovery() throws BackendException {
        IMocksControl ctrl = EasyMock.createStrictControl();
        final int partition = 42;
        final int idNamespace = 777;
        final Duration timeout = Duration.ofSeconds(1L);
        final IDAuthority mockAuthority = ctrl.createMock(IDAuthority.class);
        // Sleep for two seconds, then throw a backendexception
        // this whole delegate could be deleted if we abstracted StandardIDPool's internal executor and stopwatches
        expect(mockAuthority.getIDBlock(partition, idNamespace, timeout)).andDelegateTo(new IDAuthority() {
            @Override
            public IDBlock getIDBlock(int partition, int idNamespace, Duration timeout) throws BackendException {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    Assert.fail();
                }
                throw new TemporaryBackendException("slow backend");
            }

            @Override
            public List<KeyRange> getLocalIDPartition() throws BackendException {
                throw new IllegalArgumentException();
            }

            @Override
            public void setIDBlockSizer(IDBlockSizer sizer) {
                throw new IllegalArgumentException();
            }

            @Override
            public void close() throws BackendException {
                throw new IllegalArgumentException();
            }

            @Override
            public String getUniqueID() {
                throw new IllegalArgumentException();
            }

            @Override
            public boolean supportsInterruption() {
                return true;
            }
        });
        expect(mockAuthority.getIDBlock(partition, idNamespace, timeout)).andReturn(new IDBlock() {
            @Override
            public long numIds() {
                return 2;
            }

            @Override
            public long getId(long index) {
                return 200;
            }
        });
        expect(mockAuthority.supportsInterruption()).andStubReturn(true);
        ctrl.replay();
        StandardIDPool pool = new StandardIDPool(mockAuthority, partition, idNamespace, Integer.MAX_VALUE, timeout, 0.1);
        try {
            pool.nextID();
            Assert.fail();
        } catch (TitanException e) {
        }
        long nextID = pool.nextID();
        Assert.assertEquals(200, nextID);
        ctrl.verify();
    }

    @Test
    public void testPoolExhaustion1() {
        MockIDAuthority idauth = new MockIDAuthority(200);
        int idUpper = 10000;
        StandardIDPool pool = new StandardIDPool(idauth, 0, 1, idUpper, Duration.ofMillis(2000), 0.2);
        for (int i = 1; i < (idUpper * 2); i++) {
            try {
                long id = pool.nextID();
                Assert.assertTrue((id < idUpper));
            } catch (IDPoolExhaustedException e) {
                Assert.assertEquals(idUpper, i);
                break;
            }
        }
    }

    @Test
    public void testPoolExhaustion2() {
        int idUpper = 10000;
        MockIDAuthority idauth = new MockIDAuthority(200, idUpper);
        StandardIDPool pool = new StandardIDPool(idauth, 0, 1, Integer.MAX_VALUE, Duration.ofMillis(2000), 0.2);
        for (int i = 1; i < (idUpper * 2); i++) {
            try {
                long id = pool.nextID();
                Assert.assertTrue((id < idUpper));
            } catch (IDPoolExhaustedException e) {
                Assert.assertEquals(idUpper, i);
                break;
            }
        }
    }

    interface IDPoolFactory {
        public StandardIDPool get(int partitionID);
    }
}

