/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.yarn.server.timeline;


import TimelineReader.Field;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileContextTestHelper;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class TestLogInfo {
    private static final Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), TestLogInfo.class.getSimpleName());

    private static final String TEST_ATTEMPT_DIR_NAME = "test_app";

    private static final String TEST_ENTITY_FILE_NAME = "test_entity";

    private static final String TEST_DOMAIN_FILE_NAME = "test_domain";

    private static final String TEST_BROKEN_FILE_NAME = "test_broken";

    private Configuration config = new YarnConfiguration();

    private MiniDFSCluster hdfsCluster;

    private FileSystem fs;

    private FileContext fc;

    private FileContextTestHelper fileContextTestHelper = new FileContextTestHelper("/tmp/TestLogInfo");

    private ObjectMapper objMapper;

    private JsonFactory jsonFactory = new JsonFactory();

    private JsonGenerator jsonGenerator;

    private FSDataOutputStream outStream = null;

    private FSDataOutputStream outStreamDomain = null;

    private TimelineDomain testDomain;

    private static final short FILE_LOG_DIR_PERMISSIONS = 504;

    @Test
    public void testMatchesGroupId() throws Exception {
        String testGroupId = "app1_group1";
        // Match
        EntityLogInfo testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "app1_group1", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertTrue(testLogInfo.matchesGroupId(testGroupId));
        testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "test_app1_group1", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertTrue(testLogInfo.matchesGroupId(testGroupId));
        // Unmatch
        testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "app2_group1", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertFalse(testLogInfo.matchesGroupId(testGroupId));
        testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "app1_group2", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertFalse(testLogInfo.matchesGroupId(testGroupId));
        testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "app1_group12", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertFalse(testLogInfo.matchesGroupId(testGroupId));
        // Check delimiters
        testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "app1_group1_2", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertTrue(testLogInfo.matchesGroupId(testGroupId));
        testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "app1_group1.dat", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertTrue(testLogInfo.matchesGroupId(testGroupId));
        // Check file names shorter than group id
        testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, "app2", UserGroupInformation.getLoginUser().getUserName());
        Assert.assertFalse(testLogInfo.matchesGroupId(testGroupId));
    }

    @Test
    public void testParseEntity() throws Exception {
        // Load test data
        TimelineDataManager tdm = PluginStoreTestUtils.getTdmWithMemStore(config);
        EntityLogInfo testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, TestLogInfo.TEST_ENTITY_FILE_NAME, UserGroupInformation.getLoginUser().getUserName());
        testLogInfo.parseForStore(tdm, getTestRootPath(), true, jsonFactory, objMapper, fs);
        // Verify for the first batch
        PluginStoreTestUtils.verifyTestEntities(tdm);
        // Load new data
        TimelineEntity entityNew = PluginStoreTestUtils.createEntity("id_3", "type_3", 789L, null, null, null, null, "domain_id_1");
        TimelineEntities entityList = new TimelineEntities();
        entityList.addEntity(entityNew);
        writeEntitiesLeaveOpen(entityList, new Path(getTestRootPath(TestLogInfo.TEST_ATTEMPT_DIR_NAME), TestLogInfo.TEST_ENTITY_FILE_NAME));
        testLogInfo.parseForStore(tdm, getTestRootPath(), true, jsonFactory, objMapper, fs);
        // Verify the newly added data
        TimelineEntity entity3 = tdm.getEntity(entityNew.getEntityType(), entityNew.getEntityId(), EnumSet.allOf(Field.class), UserGroupInformation.getLoginUser());
        Assert.assertNotNull(entity3);
        Assert.assertEquals("Failed to read out entity new", entityNew.getStartTime(), entity3.getStartTime());
        tdm.close();
    }

    @Test
    public void testParseBrokenEntity() throws Exception {
        // Load test data
        TimelineDataManager tdm = PluginStoreTestUtils.getTdmWithMemStore(config);
        EntityLogInfo testLogInfo = new EntityLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, TestLogInfo.TEST_BROKEN_FILE_NAME, UserGroupInformation.getLoginUser().getUserName());
        DomainLogInfo domainLogInfo = new DomainLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, TestLogInfo.TEST_BROKEN_FILE_NAME, UserGroupInformation.getLoginUser().getUserName());
        // Try parse, should not fail
        testLogInfo.parseForStore(tdm, getTestRootPath(), true, jsonFactory, objMapper, fs);
        domainLogInfo.parseForStore(tdm, getTestRootPath(), true, jsonFactory, objMapper, fs);
        tdm.close();
    }

    @Test
    public void testParseDomain() throws Exception {
        // Load test data
        TimelineDataManager tdm = PluginStoreTestUtils.getTdmWithMemStore(config);
        DomainLogInfo domainLogInfo = new DomainLogInfo(TestLogInfo.TEST_ATTEMPT_DIR_NAME, TestLogInfo.TEST_DOMAIN_FILE_NAME, UserGroupInformation.getLoginUser().getUserName());
        domainLogInfo.parseForStore(tdm, getTestRootPath(), true, jsonFactory, objMapper, fs);
        // Verify domain data
        TimelineDomain resultDomain = tdm.getDomain("domain_1", UserGroupInformation.getLoginUser());
        Assert.assertNotNull(resultDomain);
        Assert.assertEquals(testDomain.getReaders(), resultDomain.getReaders());
        Assert.assertEquals(testDomain.getOwner(), resultDomain.getOwner());
        Assert.assertEquals(testDomain.getDescription(), resultDomain.getDescription());
    }
}

