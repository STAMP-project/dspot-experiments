/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.repository.pur;


import AttributesMapUtil.NODE_ATTRIBUTE_GROUPS;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.pentaho.di.core.AttributesInterface;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.platform.api.repository2.unified.data.node.DataNode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@PrepareForTest(AttributesMapUtil.class)
@RunWith(PowerMockRunner.class)
public class AttributesMapUtilTest {
    private static final String SAVE_ATTRIBUTES_MAP_METHOD = "saveAttributesMap";

    private static final String LOAD_ATTRIBUTES_MAP_METHOD = "loadAttributesMap";

    private static final String CUSTOM_TAG = "customTag";

    private static final String A_KEY = "aKEY";

    private static final String A_VALUE = "aVALUE";

    private static final String A_GROUP = "aGROUP";

    public static final String CNST_DUMMY = "dummy";

    @Test
    public void testSaveAttributesMap_DefaultTag() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class));
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        jobEntryCopy.setAttributesMap(new HashMap());
        jobEntryCopy.setAttributes(AttributesMapUtilTest.A_GROUP, new HashMap());
        jobEntryCopy.setAttribute(AttributesMapUtilTest.A_GROUP, AttributesMapUtilTest.A_KEY, AttributesMapUtilTest.A_VALUE);
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        AttributesMapUtil.saveAttributesMap(dataNode, jobEntryCopy);
        Assert.assertNotNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS));
        Assert.assertNotNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS).getNode(AttributesMapUtilTest.A_GROUP));
        Assert.assertNotNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS).getNode(AttributesMapUtilTest.A_GROUP).getProperty(AttributesMapUtilTest.A_KEY));
        Assert.assertEquals(AttributesMapUtilTest.A_VALUE, dataNode.getNode(NODE_ATTRIBUTE_GROUPS).getNode(AttributesMapUtilTest.A_GROUP).getProperty(AttributesMapUtilTest.A_KEY).getString());
    }

    @Test
    public void testSaveAttributesMap_CustomTag() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        jobEntryCopy.setAttributesMap(new HashMap());
        jobEntryCopy.setAttributes(AttributesMapUtilTest.A_GROUP, new HashMap());
        jobEntryCopy.setAttribute(AttributesMapUtilTest.A_GROUP, AttributesMapUtilTest.A_KEY, AttributesMapUtilTest.A_VALUE);
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        AttributesMapUtil.saveAttributesMap(dataNode, jobEntryCopy, AttributesMapUtilTest.CUSTOM_TAG);
        Assert.assertNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS));
        Assert.assertNotNull(dataNode.getNode(AttributesMapUtilTest.CUSTOM_TAG));
        Assert.assertNotNull(dataNode.getNode(AttributesMapUtilTest.CUSTOM_TAG).getNode(AttributesMapUtilTest.A_GROUP));
        Assert.assertNotNull(dataNode.getNode(AttributesMapUtilTest.CUSTOM_TAG).getNode(AttributesMapUtilTest.A_GROUP).getProperty(AttributesMapUtilTest.A_KEY));
        Assert.assertEquals(AttributesMapUtilTest.A_VALUE, dataNode.getNode(AttributesMapUtilTest.CUSTOM_TAG).getNode(AttributesMapUtilTest.A_GROUP).getProperty(AttributesMapUtilTest.A_KEY).getString());
    }

    @Test
    public void testSaveAttributesMap_DefaultTag_NullParameter() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class));
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        AttributesMapUtil.saveAttributesMap(dataNode, null);
        Assert.assertNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS));
    }

    @Test
    public void testSaveAttributesMap_CustomTag_NullParameter() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        AttributesMapUtil.saveAttributesMap(dataNode, null, AttributesMapUtilTest.CUSTOM_TAG);
        Assert.assertNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS));
        Assert.assertNull(dataNode.getNode(AttributesMapUtilTest.CUSTOM_TAG));
    }

    @Test
    public void testSaveAttributesMap_DefaultTag_NoAttributes() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class));
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.saveAttributesMap(dataNode, jobEntryCopy);
        Assert.assertNotNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS));
        Iterable<DataNode> dataNodeIterable = dataNode.getNode(NODE_ATTRIBUTE_GROUPS).getNodes();
        Assert.assertFalse(dataNodeIterable.iterator().hasNext());
    }

    @Test
    public void testSaveAttributesMap_CustomTag_NoAttributes() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.SAVE_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.saveAttributesMap(dataNode, jobEntryCopy, AttributesMapUtilTest.CUSTOM_TAG);
        Assert.assertNull(dataNode.getNode(NODE_ATTRIBUTE_GROUPS));
        Assert.assertNotNull(dataNode.getNode(AttributesMapUtilTest.CUSTOM_TAG));
        Iterable<DataNode> dataNodeIterable = dataNode.getNode(AttributesMapUtilTest.CUSTOM_TAG).getNodes();
        Assert.assertFalse(dataNodeIterable.iterator().hasNext());
    }

    @Test
    public void testLoadAttributesMap_DefaultTag() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class));
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        DataNode groupsDataNode = dataNode.addNode(NODE_ATTRIBUTE_GROUPS);
        DataNode aGroupDataNode = groupsDataNode.addNode(AttributesMapUtilTest.A_GROUP);
        aGroupDataNode.setProperty(AttributesMapUtilTest.A_KEY, AttributesMapUtilTest.A_VALUE);
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.loadAttributesMap(dataNode, jobEntryCopy);
        Assert.assertNotNull(jobEntryCopy.getAttributesMap());
        Assert.assertNotNull(jobEntryCopy.getAttributes(AttributesMapUtilTest.A_GROUP));
        Assert.assertEquals(AttributesMapUtilTest.A_VALUE, jobEntryCopy.getAttribute(AttributesMapUtilTest.A_GROUP, AttributesMapUtilTest.A_KEY));
    }

    @Test
    public void testLoadAttributesMap_CustomTag() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class));
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        DataNode groupsDataNode = dataNode.addNode(AttributesMapUtilTest.CUSTOM_TAG);
        DataNode aGroupDataNode = groupsDataNode.addNode(AttributesMapUtilTest.A_GROUP);
        aGroupDataNode.setProperty(AttributesMapUtilTest.A_KEY, AttributesMapUtilTest.A_VALUE);
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.loadAttributesMap(dataNode, jobEntryCopy, AttributesMapUtilTest.CUSTOM_TAG);
        Assert.assertNotNull(jobEntryCopy.getAttributesMap());
        Assert.assertNotNull(jobEntryCopy.getAttributes(AttributesMapUtilTest.A_GROUP));
        Assert.assertEquals(AttributesMapUtilTest.A_VALUE, jobEntryCopy.getAttribute(AttributesMapUtilTest.A_GROUP, AttributesMapUtilTest.A_KEY));
    }

    @Test
    public void testLoadAttributesMap_DefaultTag_NullParameter() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class));
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.loadAttributesMap(null, jobEntryCopy);
    }

    @Test
    public void testLoadAttributesMap_CustomTag_NullParameter() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.loadAttributesMap(null, jobEntryCopy, AttributesMapUtilTest.CUSTOM_TAG);
    }

    @Test
    public void testLoadAttributesMap_DefaultTag_EmptyDataNode() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class));
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.loadAttributesMap(dataNode, jobEntryCopy);
        Assert.assertNotNull(jobEntryCopy.getAttributesMap());
    }

    @Test
    public void testLoadAttributesMap_CustomTag_EmptyDataNode() throws Exception {
        PowerMockito.doCallRealMethod().when(AttributesMapUtil.class, AttributesMapUtilTest.LOAD_ATTRIBUTES_MAP_METHOD, ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(AttributesInterface.class), ArgumentMatchers.anyString());
        DataNode dataNode = new DataNode(AttributesMapUtilTest.CNST_DUMMY);
        JobEntryCopy jobEntryCopy = new JobEntryCopy();
        AttributesMapUtil.loadAttributesMap(dataNode, jobEntryCopy, AttributesMapUtilTest.CUSTOM_TAG);
        Assert.assertNotNull(jobEntryCopy.getAttributesMap());
    }
}

