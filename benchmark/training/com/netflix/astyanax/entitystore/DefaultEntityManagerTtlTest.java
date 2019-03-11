package com.netflix.astyanax.entitystore;


import DefaultEntityManager.Builder;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.junit.Assert;
import org.junit.Test;


public class DefaultEntityManagerTtlTest {
    private static Keyspace keyspace;

    private static AstyanaxContext<Keyspace> keyspaceContext;

    private static String TEST_CLUSTER_NAME = "junit_cass_sandbox";

    private static String TEST_KEYSPACE_NAME = "EntityPersisterTestKeyspace";

    private static final String SEEDS = "localhost:9160";

    public static ColumnFamily<String, String> CF_SAMPLE_ENTITY = ColumnFamily.newColumnFamily("SampleEntityColumnFamily", StringSerializer.get(), StringSerializer.get());

    public static ColumnFamily<String, String> CF_SIMPLE_ENTITY = ColumnFamily.newColumnFamily("SimpleEntityColumnFamily", StringSerializer.get(), StringSerializer.get());

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Entity
    @TTL(2)
    private static class TtlEntity {
        @Id
        private String id;

        @Column
        private String column;

        public TtlEntity() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            DefaultEntityManagerTtlTest.TtlEntity other = ((DefaultEntityManagerTtlTest.TtlEntity) (obj));
            if ((id.equals(other.id)) && (column.equals(other.column)))
                return true;
            else
                return false;

        }

        @Override
        public String toString() {
            return ((("SimpleEntity [id=" + (id)) + ", column=") + (column)) + "]";
        }
    }

    @Test
    public void testTtlClassAnnotation() throws Exception {
        final String id = "testTtlClassAnnotation";
        EntityManager<DefaultEntityManagerTtlTest.TtlEntity, String> entityPersister = new Builder<DefaultEntityManagerTtlTest.TtlEntity, String>().withEntityType(DefaultEntityManagerTtlTest.TtlEntity.class).withKeyspace(DefaultEntityManagerTtlTest.keyspace).withColumnFamily(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).build();
        DefaultEntityManagerTtlTest.TtlEntity origEntity = createTtlEntity(id);
        entityPersister.put(origEntity);
        // use low-level astyanax API to confirm the write
        {
            ColumnList<String> cl = DefaultEntityManagerTtlTest.keyspace.prepareQuery(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            // test column number
            Assert.assertEquals(1, cl.size());
            // test column value
            Assert.assertEquals(origEntity.getColumn(), cl.getColumnByName("column").getStringValue());
            // custom ttl
            Assert.assertEquals(2, cl.getColumnByName("column").getTtl());
        }
        DefaultEntityManagerTtlTest.TtlEntity getEntity = entityPersister.get(id);
        Assert.assertEquals(id, getEntity.getId());
        Assert.assertEquals(origEntity, getEntity);
        // entity should expire after 3s since TTL is 2s in annotation
        Thread.sleep((1000 * 3));
        // use low-level astyanax API to confirm the TTL expiration
        {
            ColumnList<String> cl = DefaultEntityManagerTtlTest.keyspace.prepareQuery(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            Assert.assertEquals(0, cl.size());
        }
    }

    @Test
    public void testConstructorTtlOverride() throws Exception {
        final String id = "testConstructorTtlOverride";
        EntityManager<DefaultEntityManagerTtlTest.TtlEntity, String> entityPersister = new Builder<DefaultEntityManagerTtlTest.TtlEntity, String>().withEntityType(DefaultEntityManagerTtlTest.TtlEntity.class).withKeyspace(DefaultEntityManagerTtlTest.keyspace).withColumnFamily(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).withTTL(5).build();
        DefaultEntityManagerTtlTest.TtlEntity origEntity = createTtlEntity(id);
        entityPersister.put(origEntity);
        // use low-level astyanax API to confirm the write
        {
            ColumnList<String> cl = DefaultEntityManagerTtlTest.keyspace.prepareQuery(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            // test column number
            Assert.assertEquals(1, cl.size());
            // test column value
            Assert.assertEquals(origEntity.getColumn(), cl.getColumnByName("column").getStringValue());
            // custom ttl
            Assert.assertEquals(5, cl.getColumnByName("column").getTtl());
        }
        DefaultEntityManagerTtlTest.TtlEntity getEntity = entityPersister.get(id);
        Assert.assertEquals(origEntity, getEntity);
        // entity should still be alive after 3s since TTL is overriden to 5s
        Thread.sleep((1000 * 3));
        getEntity = entityPersister.get(id);
        Assert.assertEquals(origEntity, getEntity);
        // entity should expire after 3s since 6s have passed with 5s TTL
        Thread.sleep((1000 * 3));
        // use low-level astyanax API to confirm the TTL expiration
        {
            ColumnList<String> cl = DefaultEntityManagerTtlTest.keyspace.prepareQuery(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            Assert.assertEquals(0, cl.size());
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Entity
    private static class MethodTtlEntity {
        @Id
        private String id;

        @Column
        private String column;

        public MethodTtlEntity() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        @SuppressWarnings("unused")
        @TTL
        public Integer getTTL() {
            return 2;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            DefaultEntityManagerTtlTest.MethodTtlEntity other = ((DefaultEntityManagerTtlTest.MethodTtlEntity) (obj));
            if ((id.equals(other.id)) && (column.equals(other.column)))
                return true;
            else
                return false;

        }

        @Override
        public String toString() {
            return ((("MethodTtlEntity [id=" + (id)) + ", column=") + (column)) + "]";
        }
    }

    @Test
    public void testMethodTtlOverride() throws Exception {
        final String id = "testMethodTtlOverride";
        EntityManager<DefaultEntityManagerTtlTest.MethodTtlEntity, String> entityPersister = // constructor TTL value is 60s
        new Builder<DefaultEntityManagerTtlTest.MethodTtlEntity, String>().withEntityType(DefaultEntityManagerTtlTest.MethodTtlEntity.class).withKeyspace(DefaultEntityManagerTtlTest.keyspace).withColumnFamily(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).withTTL(60).build();
        DefaultEntityManagerTtlTest.MethodTtlEntity origEntity = createMethodTtlEntity(id);
        entityPersister.put(origEntity);
        // use low-level astyanax API to confirm the write
        {
            ColumnList<String> cl = DefaultEntityManagerTtlTest.keyspace.prepareQuery(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            // test column number
            Assert.assertEquals(1, cl.size());
            // test column value
            Assert.assertEquals(origEntity.getColumn(), cl.getColumnByName("column").getStringValue());
            // custom ttl
            Assert.assertEquals(2, cl.getColumnByName("column").getTtl());
        }
        DefaultEntityManagerTtlTest.MethodTtlEntity getEntity = entityPersister.get(id);
        Assert.assertEquals(id, getEntity.getId());
        Assert.assertEquals(origEntity, getEntity);
        // entity should still be alive after 4s since TTL is overridden to 2s
        Thread.sleep((1000 * 4));
        // use low-level astyanax API to confirm the TTL expiration
        {
            ColumnList<String> cl = DefaultEntityManagerTtlTest.keyspace.prepareQuery(DefaultEntityManagerTtlTest.CF_SAMPLE_ENTITY).getKey(id).execute().getResult();
            Assert.assertEquals(0, cl.size());
        }
    }
}

