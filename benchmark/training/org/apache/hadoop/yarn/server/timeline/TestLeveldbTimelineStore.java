/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timeline;


import InterfaceAudience.Private;
import InterfaceStability.Unstable;
import LeveldbTimelineStore.LEVELDB_DIR_UMASK;
import TimelineDataManager.DEFAULT_DOMAIN_ID;
import TimelinePutError.FORBIDDEN_RELATION;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_PATH;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_READ_CACHE_SIZE;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS;
import YarnConfiguration.TIMELINE_SERVICE_TTL_MS;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.service.ServiceStateException;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.records.Version;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.Options;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static LeveldbTimelineStore.BACKUP_EXT;
import static LeveldbTimelineStore.FILENAME;


@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TestLeveldbTimelineStore extends TimelineStoreTestUtils {
    private FileContext fsContext;

    private File fsPath;

    private Configuration config = new YarnConfiguration();

    @Test
    public void testRootDirPermission() throws IOException {
        FileSystem fs = FileSystem.getLocal(new YarnConfiguration());
        FileStatus file = fs.getFileStatus(new Path(fsPath.getAbsolutePath(), FILENAME));
        Assert.assertNotNull(file);
        Assert.assertEquals(LEVELDB_DIR_UMASK, file.getPermission());
    }

    @Test
    public void testGetSingleEntity() throws IOException {
        super.testGetSingleEntity();
        clearStartTimeCache();
        super.testGetSingleEntity();
        loadTestEntityData();
    }

    @Test
    public void testGetEntities() throws IOException {
        super.testGetEntities();
    }

    @Test
    public void testGetEntitiesWithFromId() throws IOException {
        super.testGetEntitiesWithFromId();
    }

    @Test
    public void testGetEntitiesWithFromTs() throws IOException {
        super.testGetEntitiesWithFromTs();
    }

    @Test
    public void testGetEntitiesWithPrimaryFilters() throws IOException {
        super.testGetEntitiesWithPrimaryFilters();
    }

    @Test
    public void testGetEntitiesWithSecondaryFilters() throws IOException {
        super.testGetEntitiesWithSecondaryFilters();
    }

    @Test
    public void testGetEvents() throws IOException {
        super.testGetEvents();
    }

    @Test
    public void testCacheSizes() {
        Configuration conf = new Configuration();
        Assert.assertEquals(10000, LeveldbTimelineStore.getStartTimeReadCacheSize(conf));
        Assert.assertEquals(10000, LeveldbTimelineStore.getStartTimeWriteCacheSize(conf));
        conf.setInt(TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE, 10001);
        Assert.assertEquals(10001, LeveldbTimelineStore.getStartTimeReadCacheSize(conf));
        conf = new Configuration();
        conf.setInt(TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE, 10002);
        Assert.assertEquals(10002, LeveldbTimelineStore.getStartTimeWriteCacheSize(conf));
    }

    @Test
    public void testGetEntityTypes() throws IOException {
        List<String> entityTypes = getEntityTypes();
        Assert.assertEquals(7, entityTypes.size());
        Assert.assertEquals("ACL_ENTITY_TYPE_1", entityTypes.get(0));
        Assert.assertEquals("OLD_ENTITY_TYPE_1", entityTypes.get(1));
        Assert.assertEquals(entityType1, entityTypes.get(2));
        Assert.assertEquals(entityType2, entityTypes.get(3));
        Assert.assertEquals(entityType4, entityTypes.get(4));
        Assert.assertEquals(entityType5, entityTypes.get(5));
    }

    @Test
    public void testDeleteEntities() throws IOException, InterruptedException {
        Assert.assertEquals(3, getEntities("type_1").size());
        Assert.assertEquals(1, getEntities("type_2").size());
        Assert.assertEquals(false, deleteNextEntity(entityType1, GenericObjectMapper.writeReverseOrderedLong(60L)));
        Assert.assertEquals(3, getEntities("type_1").size());
        Assert.assertEquals(1, getEntities("type_2").size());
        Assert.assertEquals(true, deleteNextEntity(entityType1, GenericObjectMapper.writeReverseOrderedLong(123L)));
        List<TimelineEntity> entities = getEntities("type_2");
        Assert.assertEquals(1, entities.size());
        TimelineStoreTestUtils.verifyEntityInfo(entityId2, entityType2, events2, Collections.singletonMap(entityType1, Collections.singleton(entityId1b)), TimelineStoreTestUtils.EMPTY_PRIMARY_FILTERS, TimelineStoreTestUtils.EMPTY_MAP, entities.get(0), domainId1);
        entities = getEntitiesWithPrimaryFilter("type_1", userFilter);
        Assert.assertEquals(2, entities.size());
        TimelineStoreTestUtils.verifyEntityInfo(entityId1b, entityType1, events1, TimelineStoreTestUtils.EMPTY_REL_ENTITIES, primaryFilters, otherInfo, entities.get(0), domainId1);
        // can retrieve entities across domains
        TimelineStoreTestUtils.verifyEntityInfo(entityId6, entityType1, TimelineStoreTestUtils.EMPTY_EVENTS, TimelineStoreTestUtils.EMPTY_REL_ENTITIES, primaryFilters, otherInfo, entities.get(1), domainId2);
        discardOldEntities(0L);
        Assert.assertEquals(2, getEntities("type_1").size());
        Assert.assertEquals(0, getEntities("type_2").size());
        Assert.assertEquals(6, ((LeveldbTimelineStore) (store)).getEntityTypes().size());
        discardOldEntities(123L);
        Assert.assertEquals(0, getEntities("type_1").size());
        Assert.assertEquals(0, getEntities("type_2").size());
        Assert.assertEquals(0, ((LeveldbTimelineStore) (store)).getEntityTypes().size());
        Assert.assertEquals(0, getEntitiesWithPrimaryFilter("type_1", userFilter).size());
    }

    @Test
    public void testDeleteEntitiesPrimaryFilters() throws IOException, InterruptedException {
        Map<String, Set<Object>> primaryFilter = Collections.singletonMap("user", Collections.singleton(((Object) ("otheruser"))));
        TimelineEntities atsEntities = new TimelineEntities();
        atsEntities.setEntities(Collections.singletonList(TimelineStoreTestUtils.createEntity(entityId1b, entityType1, 789L, Collections.singletonList(ev2), null, primaryFilter, null, domainId1)));
        TimelinePutResponse response = store.put(atsEntities);
        Assert.assertEquals(0, response.getErrors().size());
        NameValuePair pfPair = new NameValuePair("user", "otheruser");
        List<TimelineEntity> entities = getEntitiesWithPrimaryFilter("type_1", pfPair);
        Assert.assertEquals(1, entities.size());
        TimelineStoreTestUtils.verifyEntityInfo(entityId1b, entityType1, Collections.singletonList(ev2), TimelineStoreTestUtils.EMPTY_REL_ENTITIES, primaryFilter, TimelineStoreTestUtils.EMPTY_MAP, entities.get(0), domainId1);
        entities = getEntitiesWithPrimaryFilter("type_1", userFilter);
        Assert.assertEquals(3, entities.size());
        TimelineStoreTestUtils.verifyEntityInfo(entityId1, entityType1, events1, TimelineStoreTestUtils.EMPTY_REL_ENTITIES, primaryFilters, otherInfo, entities.get(0), domainId1);
        TimelineStoreTestUtils.verifyEntityInfo(entityId1b, entityType1, events1, TimelineStoreTestUtils.EMPTY_REL_ENTITIES, primaryFilters, otherInfo, entities.get(1), domainId1);
        TimelineStoreTestUtils.verifyEntityInfo(entityId6, entityType1, TimelineStoreTestUtils.EMPTY_EVENTS, TimelineStoreTestUtils.EMPTY_REL_ENTITIES, primaryFilters, otherInfo, entities.get(2), domainId2);
        discardOldEntities((-123L));
        Assert.assertEquals(1, getEntitiesWithPrimaryFilter("type_1", pfPair).size());
        Assert.assertEquals(3, getEntitiesWithPrimaryFilter("type_1", userFilter).size());
        discardOldEntities(123L);
        Assert.assertEquals(0, getEntities("type_1").size());
        Assert.assertEquals(0, getEntities("type_2").size());
        Assert.assertEquals(0, ((LeveldbTimelineStore) (store)).getEntityTypes().size());
        Assert.assertEquals(0, getEntitiesWithPrimaryFilter("type_1", pfPair).size());
        Assert.assertEquals(0, getEntitiesWithPrimaryFilter("type_1", userFilter).size());
    }

    @Test
    public void testFromTsWithDeletion() throws IOException, InterruptedException {
        long l = System.currentTimeMillis();
        Assert.assertEquals(3, getEntitiesFromTs("type_1", l).size());
        Assert.assertEquals(1, getEntitiesFromTs("type_2", l).size());
        Assert.assertEquals(3, getEntitiesFromTsWithPrimaryFilter("type_1", userFilter, l).size());
        discardOldEntities(123L);
        Assert.assertEquals(0, getEntitiesFromTs("type_1", l).size());
        Assert.assertEquals(0, getEntitiesFromTs("type_2", l).size());
        Assert.assertEquals(0, getEntitiesFromTsWithPrimaryFilter("type_1", userFilter, l).size());
        Assert.assertEquals(0, getEntities("type_1").size());
        Assert.assertEquals(0, getEntities("type_2").size());
        Assert.assertEquals(0, getEntitiesFromTsWithPrimaryFilter("type_1", userFilter, l).size());
        loadTestEntityData();
        Assert.assertEquals(0, getEntitiesFromTs("type_1", l).size());
        Assert.assertEquals(0, getEntitiesFromTs("type_2", l).size());
        Assert.assertEquals(0, getEntitiesFromTsWithPrimaryFilter("type_1", userFilter, l).size());
        Assert.assertEquals(3, getEntities("type_1").size());
        Assert.assertEquals(1, getEntities("type_2").size());
        Assert.assertEquals(3, getEntitiesWithPrimaryFilter("type_1", userFilter).size());
    }

    @Test
    public void testCheckVersion() throws IOException {
        LeveldbTimelineStore dbStore = ((LeveldbTimelineStore) (store));
        // default version
        Version defaultVersion = dbStore.getCurrentVersion();
        Assert.assertEquals(defaultVersion, dbStore.loadVersion());
        // compatible version
        Version compatibleVersion = Version.newInstance(defaultVersion.getMajorVersion(), ((defaultVersion.getMinorVersion()) + 2));
        dbStore.storeVersion(compatibleVersion);
        Assert.assertEquals(compatibleVersion, dbStore.loadVersion());
        restartTimelineStore();
        dbStore = ((LeveldbTimelineStore) (store));
        // overwrite the compatible version
        Assert.assertEquals(defaultVersion, dbStore.loadVersion());
        // incompatible version
        Version incompatibleVersion = Version.newInstance(((defaultVersion.getMajorVersion()) + 1), defaultVersion.getMinorVersion());
        dbStore.storeVersion(incompatibleVersion);
        try {
            restartTimelineStore();
            Assert.fail("Incompatible version, should expect fail here.");
        } catch (ServiceStateException e) {
            Assert.assertTrue("Exception message mismatch", e.getMessage().contains("Incompatible version for timeline store"));
        }
    }

    @Test
    public void testValidateConfig() throws IOException {
        Configuration copyConfig = new YarnConfiguration(config);
        try {
            Configuration newConfig = new YarnConfiguration(copyConfig);
            newConfig.setLong(TIMELINE_SERVICE_TTL_MS, 0);
            config = newConfig;
            restartTimelineStore();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_TTL_MS));
        }
        try {
            Configuration newConfig = new YarnConfiguration(copyConfig);
            newConfig.setLong(TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS, 0);
            config = newConfig;
            restartTimelineStore();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS));
        }
        try {
            Configuration newConfig = new YarnConfiguration(copyConfig);
            newConfig.setLong(TIMELINE_SERVICE_LEVELDB_READ_CACHE_SIZE, (-1));
            config = newConfig;
            restartTimelineStore();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_LEVELDB_READ_CACHE_SIZE));
        }
        try {
            Configuration newConfig = new YarnConfiguration(copyConfig);
            newConfig.setLong(TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE, 0);
            config = newConfig;
            restartTimelineStore();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE));
        }
        try {
            Configuration newConfig = new YarnConfiguration(copyConfig);
            newConfig.setLong(TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE, 0);
            config = newConfig;
            restartTimelineStore();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE));
        }
        config = copyConfig;
        restartTimelineStore();
    }

    @Test
    public void testGetDomain() throws IOException {
        super.testGetDomain();
    }

    @Test
    public void testGetDomains() throws IOException {
        super.testGetDomains();
    }

    @Test
    public void testRelatingToNonExistingEntity() throws IOException {
        TimelineEntity entityToStore = new TimelineEntity();
        entityToStore.setEntityType("TEST_ENTITY_TYPE_1");
        entityToStore.setEntityId("TEST_ENTITY_ID_1");
        entityToStore.setDomainId(DEFAULT_DOMAIN_ID);
        entityToStore.addRelatedEntity("TEST_ENTITY_TYPE_2", "TEST_ENTITY_ID_2");
        TimelineEntities entities = new TimelineEntities();
        entities.addEntity(entityToStore);
        store.put(entities);
        TimelineEntity entityToGet = store.getEntity("TEST_ENTITY_ID_2", "TEST_ENTITY_TYPE_2", null);
        Assert.assertNotNull(entityToGet);
        Assert.assertEquals("DEFAULT", entityToGet.getDomainId());
        Assert.assertEquals("TEST_ENTITY_TYPE_1", entityToGet.getRelatedEntities().keySet().iterator().next());
        Assert.assertEquals("TEST_ENTITY_ID_1", entityToGet.getRelatedEntities().values().iterator().next().iterator().next());
    }

    @Test
    public void testRelatingToOldEntityWithoutDomainId() throws IOException {
        // New entity is put in the default domain
        TimelineEntity entityToStore = new TimelineEntity();
        entityToStore.setEntityType("NEW_ENTITY_TYPE_1");
        entityToStore.setEntityId("NEW_ENTITY_ID_1");
        entityToStore.setDomainId(DEFAULT_DOMAIN_ID);
        entityToStore.addRelatedEntity("OLD_ENTITY_TYPE_1", "OLD_ENTITY_ID_1");
        TimelineEntities entities = new TimelineEntities();
        entities.addEntity(entityToStore);
        store.put(entities);
        TimelineEntity entityToGet = store.getEntity("OLD_ENTITY_ID_1", "OLD_ENTITY_TYPE_1", null);
        Assert.assertNotNull(entityToGet);
        Assert.assertNull(entityToGet.getDomainId());
        Assert.assertEquals("NEW_ENTITY_TYPE_1", entityToGet.getRelatedEntities().keySet().iterator().next());
        Assert.assertEquals("NEW_ENTITY_ID_1", entityToGet.getRelatedEntities().values().iterator().next().iterator().next());
        // New entity is not put in the default domain
        entityToStore = new TimelineEntity();
        entityToStore.setEntityType("NEW_ENTITY_TYPE_2");
        entityToStore.setEntityId("NEW_ENTITY_ID_2");
        entityToStore.setDomainId("NON_DEFAULT");
        entityToStore.addRelatedEntity("OLD_ENTITY_TYPE_1", "OLD_ENTITY_ID_1");
        entities = new TimelineEntities();
        entities.addEntity(entityToStore);
        TimelinePutResponse response = store.put(entities);
        Assert.assertEquals(1, response.getErrors().size());
        Assert.assertEquals(FORBIDDEN_RELATION, response.getErrors().get(0).getErrorCode());
        entityToGet = store.getEntity("OLD_ENTITY_ID_1", "OLD_ENTITY_TYPE_1", null);
        Assert.assertNotNull(entityToGet);
        Assert.assertNull(entityToGet.getDomainId());
        // Still have one related entity
        Assert.assertEquals(1, entityToGet.getRelatedEntities().keySet().size());
        Assert.assertEquals(1, entityToGet.getRelatedEntities().values().iterator().next().size());
    }

    /**
     * Test that LevelDb repair is attempted at least once during
     * serviceInit for LeveldbTimelineStore in case open fails the
     * first time.
     */
    @Test
    public void testLevelDbRepair() throws IOException {
        LeveldbTimelineStore store = new LeveldbTimelineStore();
        JniDBFactory factory = Mockito.mock(JniDBFactory.class);
        Mockito.when(factory.open(Mockito.any(File.class), Mockito.any(Options.class))).thenThrow(new IOException()).thenCallRealMethod();
        store.setFactory(factory);
        // Create the LevelDb in a different location
        File path = new File("target", ((this.getClass().getSimpleName()) + "-tmpDir1")).getAbsoluteFile();
        Configuration conf = new Configuration(this.config);
        conf.set(TIMELINE_SERVICE_LEVELDB_PATH, path.getAbsolutePath());
        try {
            store.init(conf);
            Mockito.verify(factory, Mockito.times(1)).repair(Mockito.any(File.class), Mockito.any(Options.class));
            FileFilter fileFilter = new WildcardFileFilter((("*" + (BACKUP_EXT)) + "*"));
            Assert.assertTrue(((path.listFiles(fileFilter).length) > 0));
        } finally {
            store.close();
            fsContext.delete(new Path(path.getAbsolutePath()), true);
        }
    }
}

