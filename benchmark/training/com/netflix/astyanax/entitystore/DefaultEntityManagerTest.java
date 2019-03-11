package com.netflix.astyanax.entitystore;


import DefaultEntityManager.Builder;
import com.google.common.collect.Maps;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DefaultEntityManagerTest {
    private static Keyspace keyspace;

    private static AstyanaxContext<Keyspace> keyspaceContext;

    private static String TEST_CLUSTER_NAME = "junit_cass_sandbox";

    private static String TEST_KEYSPACE_NAME = "EntityPersisterTestKeyspace";

    private static final String SEEDS = "localhost:9160";

    public static ColumnFamily<String, String> CF_SAMPLE_ENTITY = ColumnFamily.newColumnFamily("sampleentity", StringSerializer.get(), StringSerializer.get());

    public static ColumnFamily<String, String> CF_SIMPLE_ENTITY = ColumnFamily.newColumnFamily("simpleentity", StringSerializer.get(), StringSerializer.get());

    @Test
    public void basicLifecycle() throws Exception {
        final String id = "basicLifecycle";
        EntityManager<SampleEntity, String> entityPersister = new Builder<SampleEntity, String>().withEntityType(SampleEntity.class).withKeyspace(DefaultEntityManagerTest.keyspace).build();
        SampleEntity origEntity = createSampleEntity(id);
        entityPersister.put(origEntity);
        // use low-level astyanax API to confirm the write
        {
            ColumnList<String> cl = DefaultEntityManagerTest.keyspace.prepareQuery(DefaultEntityManagerTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            // 19 simple columns
            // 2 one-level-deep nested columns from Bar
            // 2 two-level-deep nested columns from BarBar
            // Assert.assertEquals(31, cl.size());
            for (Column<String> c : cl) {
                System.out.println(("Got column : " + (c.getName())));
            }
            // simple columns
            Assert.assertEquals(origEntity.getString(), cl.getColumnByName("STRING").getStringValue());
            Assert.assertArrayEquals(origEntity.getByteArray(), cl.getColumnByName("BYTE_ARRAY").getByteArrayValue());
            // nested fields
            Assert.assertEquals(origEntity.getBar().i, cl.getColumnByName("BAR.i").getIntegerValue());
            Assert.assertEquals(origEntity.getBar().s, cl.getColumnByName("BAR.s").getStringValue());
            Assert.assertEquals(origEntity.getBar().barbar.i, cl.getColumnByName("BAR.barbar.i").getIntegerValue());
            Assert.assertEquals(origEntity.getBar().barbar.s, cl.getColumnByName("BAR.barbar.s").getStringValue());
        }
        SampleEntity getEntity = entityPersister.get(id);
        System.out.println(getEntity.toString());
        Assert.assertEquals(origEntity, getEntity);
        entityPersister.delete(id);
        // use low-level astyanax API to confirm the delete
        {
            ColumnList<String> cl = DefaultEntityManagerTest.keyspace.prepareQuery(DefaultEntityManagerTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            Assert.assertEquals(0, cl.size());
        }
    }

    @Test
    public void testMultiCalls() throws Exception {
        EntityManager<SimpleEntity, String> entityPersister = new Builder<SimpleEntity, String>().withEntityType(SimpleEntity.class).withKeyspace(DefaultEntityManagerTest.keyspace).build();
        final Map<String, SimpleEntity> entities = Maps.newHashMap();
        for (int i = 0; i < 10; i++) {
            String str = Integer.toString(i);
            entities.put(str, new SimpleEntity(str, str));
        }
        // Add multiple
        entityPersister.put(entities.values());
        {
            final Map<String, SimpleEntity> entities2 = DefaultEntityManagerTest.collectionToMap(entityPersister.get(entities.keySet()));
            Assert.assertEquals(entities.keySet(), entities2.keySet());
        }
        // Read all
        {
            final Map<String, SimpleEntity> entities2 = DefaultEntityManagerTest.collectionToMap(entityPersister.getAll());
            Assert.assertEquals(entities.keySet(), entities2.keySet());
        }
        // Delete multiple
        {
            System.out.println(entities.keySet());
            entityPersister.delete(entities.keySet());
            final Map<String, SimpleEntity> entities3 = DefaultEntityManagerTest.collectionToMap(entityPersister.get(entities.keySet()));
            System.out.println(entities3);
            Assert.assertTrue(entities3.isEmpty());
            final Map<String, SimpleEntity> entities4 = DefaultEntityManagerTest.collectionToMap(entityPersister.getAll());
            System.out.println(entities4);
            Assert.assertTrue(entities4.isEmpty());
        }
    }

    @Test
    public void testBuilder() {
        new Builder<DoubleIdColumnEntity, String>().withColumnFamily(DefaultEntityManagerTest.CF_SAMPLE_ENTITY);
        try {
            new Builder<DoubleIdColumnEntity, String>().withColumnFamily("Test").withColumnFamily(DefaultEntityManagerTest.CF_SAMPLE_ENTITY);
            Assert.fail();
        } catch (Exception e) {
        }
        try {
            new Builder<DoubleIdColumnEntity, String>().withColumnFamily(DefaultEntityManagerTest.CF_SAMPLE_ENTITY).withColumnFamily("Test");
            Assert.fail();
        } catch (Exception e) {
        }
        new Builder<DoubleIdColumnEntity, String>().withColumnFamily("test");
    }

    @Test
    public void doubleIdColumnAnnotation() throws Exception {
        final String id = "doubleIdColumnAnnotation";
        EntityManager<DoubleIdColumnEntity, String> entityPersister = new Builder<DoubleIdColumnEntity, String>().withEntityType(DoubleIdColumnEntity.class).withKeyspace(DefaultEntityManagerTest.keyspace).withColumnFamily(DefaultEntityManagerTest.CF_SAMPLE_ENTITY).build();
        DoubleIdColumnEntity origEntity = createDoubleIdColumnEntity(id);
        entityPersister.put(origEntity);
        // use low-level astyanax API to confirm the write
        {
            ColumnList<String> cl = DefaultEntityManagerTest.keyspace.prepareQuery(DefaultEntityManagerTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            // test column number
            Assert.assertEquals(3, cl.size());
            // test column value
            Assert.assertEquals(origEntity.getId(), cl.getColumnByName("id").getStringValue());
            Assert.assertEquals(origEntity.getNum(), cl.getColumnByName("num").getIntegerValue());
            Assert.assertEquals(origEntity.getStr(), cl.getColumnByName("str").getStringValue());
        }
        DoubleIdColumnEntity getEntity = entityPersister.get(id);
        Assert.assertEquals(origEntity, getEntity);
        entityPersister.delete(id);
        // use low-level astyanax API to confirm the delete
        {
            ColumnList<String> cl = DefaultEntityManagerTest.keyspace.prepareQuery(DefaultEntityManagerTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            Assert.assertEquals(0, cl.size());
        }
    }
}

