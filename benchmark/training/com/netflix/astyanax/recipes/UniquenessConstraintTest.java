package com.netflix.astyanax.recipes;


import ConsistencyLevel.CL_ONE;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Cluster;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore
public class UniquenessConstraintTest {
    private static Logger LOG = LoggerFactory.getLogger(UniquenessConstraintTest.class);

    private static AstyanaxContext<Cluster> clusterContext;

    private static final String TEST_CLUSTER_NAME = "TestCluster";

    private static final String TEST_KEYSPACE_NAME = "UniqueIndexTest";

    private static final String TEST_DATA_CF = "UniqueRowKeyTest";

    private static final boolean TEST_INIT_KEYSPACE = true;

    private static ColumnFamily<Long, String> CF_DATA = ColumnFamily.newColumnFamily(UniquenessConstraintTest.TEST_DATA_CF, LongSerializer.get(), StringSerializer.get());

    @Test
    public void testUniqueness() throws Exception {
        UniquenessConstraintTest.LOG.info("Starting");
        Keyspace keyspace = UniquenessConstraintTest.clusterContext.getEntity().getKeyspace(UniquenessConstraintTest.TEST_KEYSPACE_NAME);
        UniquenessConstraintWithPrefix<Long> unique = new UniquenessConstraintWithPrefix<Long>(keyspace, UniquenessConstraintTest.CF_DATA).setTtl(2).setPrefix("unique_").setConsistencyLevel(CL_ONE).setMonitor(new UniquenessConstraintViolationMonitor<Long, String>() {
            @Override
            public void onViolation(Long key, String column) {
                UniquenessConstraintTest.LOG.info(((("Violated: " + key) + " column: ") + column));
            }
        });
        try {
            String column = unique.isUnique(1234L);
            Assert.assertNotNull(column);
            UniquenessConstraintTest.LOG.info(column);
            column = unique.isUnique(1234L);
            Assert.assertNull(column);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            column = unique.isUnique(1234L);
            Assert.assertNotNull(column);
            UniquenessConstraintTest.LOG.info(column);
        } catch (ConnectionException e) {
            UniquenessConstraintTest.LOG.error(e.getMessage());
            Assert.fail(e.getMessage());
        }
    }
}

