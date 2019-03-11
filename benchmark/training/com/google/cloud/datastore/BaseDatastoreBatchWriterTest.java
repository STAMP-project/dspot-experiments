/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import com.google.common.collect.ImmutableList;
import com.google.datastore.v1.Mutation;
import java.util.LinkedList;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class BaseDatastoreBatchWriterTest {
    private static final Key KEY1 = Key.newBuilder("dataset1", "kind1", "name1").build();

    private static final Key KEY2 = Key.newBuilder(BaseDatastoreBatchWriterTest.KEY1, 1).build();

    private static final Key KEY3 = Key.newBuilder(BaseDatastoreBatchWriterTest.KEY1, 2).build();

    private static final IncompleteKey INCOMPLETE_KEY = IncompleteKey.newBuilder(BaseDatastoreBatchWriterTest.KEY1).build();

    private static final Entity ENTITY1 = Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY1).build();

    private static final Entity ENTITY2 = Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY2).set("bak", true).build();

    private static final Entity ENTITY3 = Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY3).set("bak", true).build();

    private static final FullEntity<IncompleteKey> INCOMPLETE_ENTITY_1 = Entity.newBuilder(BaseDatastoreBatchWriterTest.INCOMPLETE_KEY).build();

    private static final FullEntity<IncompleteKey> INCOMPLETE_ENTITY_2 = Entity.newBuilder(BaseDatastoreBatchWriterTest.INCOMPLETE_KEY).set("name", "dan").build();

    private BaseDatastoreBatchWriterTest.DatastoreBatchWriter batchWriter;

    private class DatastoreBatchWriter extends BaseDatastoreBatchWriter {
        private final Datastore datastore;

        protected DatastoreBatchWriter() {
            super("test");
            datastore = EasyMock.createMock(Datastore.class);
            IncompleteKey[] expected = new IncompleteKey[]{ BaseDatastoreBatchWriterTest.INCOMPLETE_KEY, BaseDatastoreBatchWriterTest.INCOMPLETE_KEY };
            List<Key> result = ImmutableList.of(BaseDatastoreBatchWriterTest.KEY2, BaseDatastoreBatchWriterTest.KEY3);
            expect(datastore.allocateId(expected)).andReturn(result).times(0, 1);
            replay(datastore);
        }

        @Override
        protected Datastore getDatastore() {
            return datastore;
        }

        void finish() {
            verify(datastore);
        }
    }

    @Test
    public void testAdd() throws Exception {
        Entity entity2 = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY2).setKey(Key.newBuilder(BaseDatastoreBatchWriterTest.KEY1).setName("name2").build()).build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(BaseDatastoreBatchWriterTest.ENTITY1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY2, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1).build().toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY3, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2).build().toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(entity2.toPb()).build());
        List<Entity> entities = batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2, entity2);
        Assert.assertEquals(pbs, toMutationPbList());
        Assert.assertEquals(BaseDatastoreBatchWriterTest.ENTITY1, entities.get(0));
        Assert.assertEquals(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY2, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1).build(), entities.get(1));
        Assert.assertEquals(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY3, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2).build(), entities.get(2));
        Assert.assertEquals(entity2, entities.get(3));
    }

    @Test
    public void testAddAfterDelete() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(BaseDatastoreBatchWriterTest.ENTITY1.toPb()).build());
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1);
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test(expected = DatastoreException.class)
    public void testAddDuplicate() throws Exception {
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
    }

    @Test(expected = DatastoreException.class)
    public void testAddAfterPut() throws Exception {
        batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
    }

    @Test(expected = DatastoreException.class)
    public void testAddAfterUpdate() throws Exception {
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
    }

    @Test(expected = DatastoreException.class)
    public void testAddWhenNotActive() throws Exception {
        deactivate();
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
    }

    @Test
    public void testAddWithDeferredAllocation() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(BaseDatastoreBatchWriterTest.ENTITY1.toPb()).build());
        batchWriter.addWithDeferredIdAllocation(BaseDatastoreBatchWriterTest.ENTITY1, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1);
        batchWriter.addWithDeferredIdAllocation(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test(expected = DatastoreException.class)
    public void testAddWithDeferredAllocationWhenNotActive() throws Exception {
        deactivate();
        batchWriter.addWithDeferredIdAllocation(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1);
    }

    @Test
    public void testUpdate() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpdate(BaseDatastoreBatchWriterTest.ENTITY1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpdate(BaseDatastoreBatchWriterTest.ENTITY2.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpdate(BaseDatastoreBatchWriterTest.ENTITY3.toPb()).build());
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY1, BaseDatastoreBatchWriterTest.ENTITY2);
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY3);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testUpdateAfterUpdate() throws Exception {
        Entity entity = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY1).set("foo", "bar").build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpdate(entity.toPb()).build());
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.update(entity);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testUpdateAfterAdd() throws Exception {
        Entity entity = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY1).set("foo", "bar").build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(entity.toPb()).build());
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.update(entity);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testUpdateAfterPut() throws Exception {
        Entity entity = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY1).set("foo", "bar").build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(entity.toPb()).build());
        batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.update(entity);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test(expected = DatastoreException.class)
    public void testUpdateAfterDelete() throws Exception {
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1);
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY1, BaseDatastoreBatchWriterTest.ENTITY2);
    }

    @Test(expected = DatastoreException.class)
    public void testUpdateWhenNotActive() throws Exception {
        deactivate();
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY1);
    }

    @Test
    public void testPut() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(BaseDatastoreBatchWriterTest.ENTITY1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(BaseDatastoreBatchWriterTest.ENTITY2.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(BaseDatastoreBatchWriterTest.ENTITY3.toPb()).build());
        List<Entity> putEntities = batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1, BaseDatastoreBatchWriterTest.ENTITY2);
        Entity putEntity = batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY3);
        Assert.assertEquals(BaseDatastoreBatchWriterTest.ENTITY1, putEntities.get(0));
        Assert.assertEquals(BaseDatastoreBatchWriterTest.ENTITY2, putEntities.get(1));
        Assert.assertEquals(BaseDatastoreBatchWriterTest.ENTITY3, putEntity);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testPutIncompleteKey() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(BaseDatastoreBatchWriterTest.ENTITY1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY2, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1).build().toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY3, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2).build().toPb()).build());
        Entity putEntity = batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1);
        List<Entity> putEntities = batchWriter.put(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2);
        Assert.assertEquals(BaseDatastoreBatchWriterTest.ENTITY1, putEntity);
        Assert.assertEquals(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY2, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1).build(), putEntities.get(0));
        Assert.assertEquals(Entity.newBuilder(BaseDatastoreBatchWriterTest.KEY3, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2).build(), putEntities.get(1));
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testPutWithDeferredAllocation() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(BaseDatastoreBatchWriterTest.ENTITY1.toPb()).build());
        batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.putWithDeferredIdAllocation(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1, BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_2);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testPutAfterPut() throws Exception {
        Entity entity = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY1).set("foo", "bar").build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(entity.toPb()).build());
        Entity putEntity1 = batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1);
        Entity putEntity2 = batchWriter.put(entity);
        Assert.assertEquals(BaseDatastoreBatchWriterTest.ENTITY1, putEntity1);
        Assert.assertEquals(entity, putEntity2);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testPutAfterAdd() throws Exception {
        Entity entity = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY1).set("foo", "bar").build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(entity.toPb()).build());
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.put(entity);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testPutAfterUpdate() throws Exception {
        Entity entity = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY1).set("foo", "bar").build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(entity.toPb()).build());
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY1);
        Entity putEntity = batchWriter.put(entity);
        Assert.assertEquals(entity, putEntity);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testPutAfterDelete() throws Exception {
        Entity entity = Entity.newBuilder(BaseDatastoreBatchWriterTest.ENTITY1).set("foo", "bar").build();
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setUpsert(entity.toPb()).build());
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1);
        Entity putEntity = batchWriter.put(entity);
        Assert.assertEquals(entity, putEntity);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test(expected = DatastoreException.class)
    public void testPutWhenNotActive() throws Exception {
        deactivate();
        batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1);
    }

    @Test
    public void testDelete() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setDelete(BaseDatastoreBatchWriterTest.KEY1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setDelete(BaseDatastoreBatchWriterTest.KEY2.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setDelete(BaseDatastoreBatchWriterTest.KEY3.toPb()).build());
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1, BaseDatastoreBatchWriterTest.KEY2);
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY3);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testDeleteAfterAdd() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setInsert(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1.toPb()).build());
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setDelete(BaseDatastoreBatchWriterTest.KEY1.toPb()).build());
        batchWriter.add(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.addWithDeferredIdAllocation(BaseDatastoreBatchWriterTest.INCOMPLETE_ENTITY_1);
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testDeleteAfterUpdate() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setDelete(BaseDatastoreBatchWriterTest.KEY1.toPb()).build());
        batchWriter.update(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test
    public void testDeleteAfterPut() throws Exception {
        List<Mutation> pbs = new LinkedList<>();
        pbs.add(com.google.datastore.v1.Mutation.newBuilder().setDelete(BaseDatastoreBatchWriterTest.KEY1.toPb()).build());
        batchWriter.put(BaseDatastoreBatchWriterTest.ENTITY1);
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1);
        Assert.assertEquals(pbs, toMutationPbList());
    }

    @Test(expected = DatastoreException.class)
    public void testDeleteWhenNotActive() throws Exception {
        deactivate();
        batchWriter.delete(BaseDatastoreBatchWriterTest.KEY1);
    }
}

