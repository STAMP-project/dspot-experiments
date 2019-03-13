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
import RollingLevelDBTimelineStore.LEVELDB_DIR_UMASK;
import TimelineDataManager.DEFAULT_DOMAIN_ID;
import TimelinePutError.FORBIDDEN_RELATION;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_READ_CACHE_SIZE;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE;
import YarnConfiguration.TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS;
import YarnConfiguration.TIMELINE_SERVICE_TTL_MS;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.service.ServiceStateException;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.records.Version;
import org.junit.Assert;
import org.junit.Test;

import static RollingLevelDBTimelineStore.FILENAME;


/**
 * Test class to verify RollingLevelDBTimelineStore.
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TestRollingLevelDBTimelineStore extends TimelineStoreTestUtils {
    private FileContext fsContext;

    private File fsPath;

    private Configuration config = new YarnConfiguration();

    @Test
    public void testRootDirPermission() throws IOException {
        FileSystem fs = FileSystem.getLocal(new YarnConfiguration());
        FileStatus file = fs.getFileStatus(new org.apache.hadoop.fs.Path(fsPath.getAbsolutePath(), FILENAME));
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
        // feature not supported
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
        Assert.assertEquals(10000, RollingLevelDBTimelineStore.getStartTimeReadCacheSize(conf));
        Assert.assertEquals(10000, RollingLevelDBTimelineStore.getStartTimeWriteCacheSize(conf));
        conf.setInt(TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE, 10001);
        Assert.assertEquals(10001, RollingLevelDBTimelineStore.getStartTimeReadCacheSize(conf));
        conf = new Configuration();
        conf.setInt(TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE, 10002);
        Assert.assertEquals(10002, RollingLevelDBTimelineStore.getStartTimeWriteCacheSize(conf));
    }

    @Test
    public void testCheckVersion() throws IOException {
        RollingLevelDBTimelineStore dbStore = ((RollingLevelDBTimelineStore) (store));
        // default version
        Version defaultVersion = dbStore.getCurrentVersion();
        Assert.assertEquals(defaultVersion, dbStore.loadVersion());
        // compatible version
        Version compatibleVersion = Version.newInstance(defaultVersion.getMajorVersion(), ((defaultVersion.getMinorVersion()) + 2));
        dbStore.storeVersion(compatibleVersion);
        Assert.assertEquals(compatibleVersion, dbStore.loadVersion());
        restartTimelineStore();
        dbStore = ((RollingLevelDBTimelineStore) (store));
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
    public void testRelatingToEntityInSamePut() throws IOException {
        TimelineEntity entityToRelate = new TimelineEntity();
        entityToRelate.setEntityType("TEST_ENTITY_TYPE_2");
        entityToRelate.setEntityId("TEST_ENTITY_ID_2");
        entityToRelate.setDomainId("TEST_DOMAIN");
        TimelineEntity entityToStore = new TimelineEntity();
        entityToStore.setEntityType("TEST_ENTITY_TYPE_1");
        entityToStore.setEntityId("TEST_ENTITY_ID_1");
        entityToStore.setDomainId("TEST_DOMAIN");
        entityToStore.addRelatedEntity("TEST_ENTITY_TYPE_2", "TEST_ENTITY_ID_2");
        TimelineEntities entities = new TimelineEntities();
        entities.addEntity(entityToStore);
        entities.addEntity(entityToRelate);
        store.put(entities);
        TimelineEntity entityToGet = store.getEntity("TEST_ENTITY_ID_2", "TEST_ENTITY_TYPE_2", null);
        Assert.assertNotNull(entityToGet);
        Assert.assertEquals("TEST_DOMAIN", entityToGet.getDomainId());
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
        Assert.assertEquals("DEFAULT", entityToGet.getDomainId());
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
        Assert.assertEquals("DEFAULT", entityToGet.getDomainId());
        // Still have one related entity
        Assert.assertEquals(1, entityToGet.getRelatedEntities().keySet().size());
        Assert.assertEquals(1, entityToGet.getRelatedEntities().values().iterator().next().size());
    }
}

