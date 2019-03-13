package com.netflix.astyanax.entitystore;


import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CompositeEntityManagerTest {
    private static Logger LOG = LoggerFactory.getLogger(CompositeEntityManagerTest.class);

    private static Keyspace keyspace;

    private static AstyanaxContext<Keyspace> keyspaceContext;

    private static String TEST_CLUSTER_NAME = "junit_cass_sandbox";

    private static String TEST_KEYSPACE_NAME = "CompositeEntityManagerTest";

    private static final String SEEDS = "localhost:9160";

    @Entity
    public static class TestEntity {
        public TestEntity() {
        }

        public TestEntity(String rowKey, String part1, Long part2, Long value) {
            super();
            this.part1 = part1;
            this.part2 = part2;
            this.value = value;
            this.rowKey = rowKey;
        }

        @Id
        String rowKey;// This will be the row key


        @Column
        String part1;// This will be the first part of the composite


        @Column
        Long part2;// This will be the second part of the composite


        @Column
        Long value;// This will be the value of the composite


        @Override
        public String toString() {
            return (((((((("TestEntityChild [" + "key=") + (rowKey)) + ", part1=") + (part1)) + ", part2=") + (part2)) + ", value=") + (value)) + "]";
        }
    }

    private static CompositeEntityManager<CompositeEntityManagerTest.TestEntity, String> manager;

    @Test
    public void test() throws Exception {
        List<CompositeEntityManagerTest.TestEntity> cqlEntities;
        Collection<CompositeEntityManagerTest.TestEntity> entitiesNative;
        // Simple row query
        entitiesNative = CompositeEntityManagerTest.manager.createNativeQuery().whereId().in("A").getResultSet();
        Assert.assertEquals(20, entitiesNative.size());
        CompositeEntityManagerTest.LOG.info(("NATIVE: " + entitiesNative));
        // Multi row query
        cqlEntities = CompositeEntityManagerTest.manager.find("SELECT * from TestEntity WHERE KEY IN ('A', 'B')");
        Assert.assertEquals(40, cqlEntities.size());
        entitiesNative = CompositeEntityManagerTest.manager.createNativeQuery().whereId().in("A", "B").getResultSet();
        CompositeEntityManagerTest.LOG.info(("NATIVE: " + entitiesNative));
        Assert.assertEquals(40, entitiesNative.size());
        // Simple prefix
        entitiesNative = CompositeEntityManagerTest.manager.createNativeQuery().whereId().equal("A").whereColumn("part1").equal("a").getResultSet();
        CompositeEntityManagerTest.LOG.info(("NATIVE: " + entitiesNative));
        Assert.assertEquals(10, entitiesNative.size());
        cqlEntities = CompositeEntityManagerTest.manager.find("SELECT * from TestEntity WHERE KEY = 'A' AND column1='b' AND column2>=5 AND column2<8");
        Assert.assertEquals(3, cqlEntities.size());
        CompositeEntityManagerTest.LOG.info(cqlEntities.toString());
        CompositeEntityManagerTest.manager.remove(new CompositeEntityManagerTest.TestEntity("A", "b", 5L, null));
        cqlEntities = CompositeEntityManagerTest.manager.find("SELECT * from TestEntity WHERE KEY = 'A' AND column1='b' AND column2>=5 AND column2<8");
        Assert.assertEquals(2, cqlEntities.size());
        CompositeEntityManagerTest.LOG.info(cqlEntities.toString());
        CompositeEntityManagerTest.manager.delete("A");
        cqlEntities = CompositeEntityManagerTest.manager.find("SELECT * from TestEntity WHERE KEY = 'A' AND column1='b' AND column2>=5 AND column2<8");
        Assert.assertEquals(0, cqlEntities.size());
    }

    @Test
    public void testQuery() throws Exception {
        Collection<CompositeEntityManagerTest.TestEntity> entitiesNative;
        entitiesNative = CompositeEntityManagerTest.manager.createNativeQuery().whereId().in("B").whereColumn("part1").equal("b").whereColumn("part2").greaterThanEqual(5L).whereColumn("part2").lessThan(8L).getResultSet();
        CompositeEntityManagerTest.LOG.info(("NATIVE: " + (entitiesNative.toString())));
        Assert.assertEquals(3, entitiesNative.size());
    }

    // ... Not sure this use case makes sense since cassandra will end up returning
    // columns with part2 greater than 8 but less than b
    // @Test
    // public void testQueryComplexRange() throws Exception {
    // Collection<TestEntity> entitiesNative;
    // 
    // entitiesNative = manager.createNativeQuery()
    // .whereId().in("B")
    // .whereColumn("part1").lessThan("b")
    // .whereColumn("part2").lessThan(8L)
    // .getResultSet();
    // 
    // LOG.info("NATIVE: " + entitiesNative.toString());
    // logResultSet(manager.getAll(), "COMPLEX RANGE: ");
    // Assert.assertEquals(2, entitiesNative.size());
    // }
    @Test
    public void testBadFieldName() throws Exception {
        try {
            CompositeEntityManagerTest.manager.createNativeQuery().whereId().in("A").whereColumn("badfield").equal("b").getResultSet();
            Assert.fail();
        } catch (Exception e) {
            CompositeEntityManagerTest.LOG.info(e.getMessage(), e);
        }
    }
}

