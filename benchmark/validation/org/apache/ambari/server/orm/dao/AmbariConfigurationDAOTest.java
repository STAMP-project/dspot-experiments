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
package org.apache.ambari.server.orm.dao;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.orm.entities.AmbariConfigurationEntity;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Test;


public class AmbariConfigurationDAOTest extends EasyMockSupport {
    private static final String CATEGORY_NAME = "test-category";

    private static Method methodMerge;

    private static Method methodRemove;

    private static Method methodCreate;

    private static Method methodFindByCategory;

    private static Field fieldEntityManagerProvider;

    @Test
    public void testReconcileCategoryNewCategory() throws Exception {
        Capture<AmbariConfigurationEntity> capturedEntities = newCapture(CaptureType.ALL);
        AmbariConfigurationDAO dao = createDao();
        expect(dao.findByCategory(AmbariConfigurationDAOTest.CATEGORY_NAME)).andReturn(null).once();
        dao.create(capture(capturedEntities));
        expectLastCall().anyTimes();
        replayAll();
        Map<String, String> properties;
        properties = new HashMap<>();
        properties.put("property1", "value1");
        properties.put("property2", "value2");
        dao.reconcileCategory(AmbariConfigurationDAOTest.CATEGORY_NAME, properties, true);
        verifyAll();
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, properties, capturedEntities);
    }

    @Test
    public void testReconcileCategoryReplaceCategory() throws Exception {
        Map<String, String> existingProperties;
        existingProperties = new HashMap<>();
        existingProperties.put("property1", "value1");
        existingProperties.put("property2", "value2");
        Capture<AmbariConfigurationEntity> capturedCreatedEntities = newCapture(CaptureType.ALL);
        Capture<AmbariConfigurationEntity> capturedRemovedEntities = newCapture(CaptureType.ALL);
        AmbariConfigurationDAO dao = createDao();
        expect(dao.findByCategory(AmbariConfigurationDAOTest.CATEGORY_NAME)).andReturn(toEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, existingProperties)).once();
        dao.remove(capture(capturedRemovedEntities));
        expectLastCall().anyTimes();
        dao.create(capture(capturedCreatedEntities));
        expectLastCall().anyTimes();
        replayAll();
        Map<String, String> newProperties;
        newProperties = new HashMap<>();
        newProperties.put("property1_new", "value1");
        newProperties.put("property2_new", "value2");
        dao.reconcileCategory(AmbariConfigurationDAOTest.CATEGORY_NAME, newProperties, true);
        verifyAll();
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, newProperties, capturedCreatedEntities);
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, existingProperties, capturedRemovedEntities);
    }

    @Test
    public void testReconcileCategoryUpdateCategoryKeepNotSpecified() throws Exception {
        Map<String, String> existingProperties;
        existingProperties = new HashMap<>();
        existingProperties.put("property1", "value1");
        existingProperties.put("property2", "value2");
        Capture<AmbariConfigurationEntity> capturedCreatedEntities = newCapture(CaptureType.ALL);
        Capture<AmbariConfigurationEntity> capturedMergedEntities = newCapture(CaptureType.ALL);
        AmbariConfigurationDAO dao = createDao();
        expect(dao.findByCategory(AmbariConfigurationDAOTest.CATEGORY_NAME)).andReturn(toEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, existingProperties)).once();
        expect(dao.merge(capture(capturedMergedEntities))).andReturn(createNiceMock(AmbariConfigurationEntity.class)).anyTimes();
        dao.create(capture(capturedCreatedEntities));
        expectLastCall().anyTimes();
        replayAll();
        Map<String, String> newProperties;
        newProperties = new HashMap<>();
        newProperties.put("property1", "new_value1");
        newProperties.put("property2_new", "value2");
        newProperties.put("property3", "value3");
        dao.reconcileCategory(AmbariConfigurationDAOTest.CATEGORY_NAME, newProperties, false);
        verifyAll();
        Map<String, String> expectedProperties;
        expectedProperties = new HashMap<>();
        expectedProperties.put("property2_new", "value2");
        expectedProperties.put("property3", "value3");
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, expectedProperties, capturedCreatedEntities);
        expectedProperties = new HashMap<>();
        expectedProperties.put("property1", "new_value1");
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, expectedProperties, capturedMergedEntities);
    }

    @Test
    public void testReconcileCategoryUpdateCategoryRemoveNotSpecified() throws Exception {
        Map<String, String> existingProperties;
        existingProperties = new HashMap<>();
        existingProperties.put("property1", "value1");
        existingProperties.put("property2", "value2");
        Capture<AmbariConfigurationEntity> capturedCreatedEntities = newCapture(CaptureType.ALL);
        Capture<AmbariConfigurationEntity> capturedRemovedEntities = newCapture(CaptureType.ALL);
        Capture<AmbariConfigurationEntity> capturedMergedEntities = newCapture(CaptureType.ALL);
        AmbariConfigurationDAO dao = createDao();
        expect(dao.findByCategory(AmbariConfigurationDAOTest.CATEGORY_NAME)).andReturn(toEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, existingProperties)).once();
        expect(dao.merge(capture(capturedMergedEntities))).andReturn(createNiceMock(AmbariConfigurationEntity.class)).anyTimes();
        dao.remove(capture(capturedRemovedEntities));
        expectLastCall().anyTimes();
        dao.create(capture(capturedCreatedEntities));
        expectLastCall().anyTimes();
        replayAll();
        Map<String, String> newProperties;
        newProperties = new HashMap<>();
        newProperties.put("property1", "new_value1");
        newProperties.put("property2_new", "value2");
        newProperties.put("property3", "value3");
        dao.reconcileCategory(AmbariConfigurationDAOTest.CATEGORY_NAME, newProperties, true);
        verifyAll();
        Map<String, String> expectedProperties;
        expectedProperties = new HashMap<>();
        expectedProperties.put("property2_new", "value2");
        expectedProperties.put("property3", "value3");
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, expectedProperties, capturedCreatedEntities);
        expectedProperties = new HashMap<>();
        expectedProperties.put("property2", "value2");
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, expectedProperties, capturedRemovedEntities);
        expectedProperties = new HashMap<>();
        expectedProperties.put("property1", "new_value1");
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, expectedProperties, capturedMergedEntities);
    }

    @Test
    public void testReconcileCategoryAppendCategory() throws Exception {
        Map<String, String> existingProperties;
        existingProperties = new HashMap<>();
        existingProperties.put("property1", "value1");
        existingProperties.put("property2", "value2");
        Capture<AmbariConfigurationEntity> capturedCreatedEntities = newCapture(CaptureType.ALL);
        AmbariConfigurationDAO dao = createDao();
        expect(dao.findByCategory(AmbariConfigurationDAOTest.CATEGORY_NAME)).andReturn(toEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, existingProperties)).once();
        dao.create(capture(capturedCreatedEntities));
        expectLastCall().anyTimes();
        replayAll();
        Map<String, String> newProperties;
        newProperties = new HashMap<>();
        newProperties.put("property3", "value3");
        newProperties.put("property4", "value3");
        dao.reconcileCategory(AmbariConfigurationDAOTest.CATEGORY_NAME, newProperties, false);
        verifyAll();
        validateCapturedEntities(AmbariConfigurationDAOTest.CATEGORY_NAME, newProperties, capturedCreatedEntities);
    }
}

