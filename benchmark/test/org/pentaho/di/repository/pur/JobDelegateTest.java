/**
 * !
 * Copyright 2010 - 2018 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.repository.pur;


import AttributesMapUtil.NODE_ATTRIBUTE_GROUPS;
import JobDelegate.PROP_ATTRIBUTES_JOB_ENTRY_COPY;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.logging.JobLogTable;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.data.node.DataNode;


public class JobDelegateTest {
    private PurRepository mockPurRepository;

    private abstract class JobEntryBaseAndInterface extends JobEntryBase implements JobEntryInterface {}

    @Test
    public void testElementToDataNodeSavesCopyAttributes() throws KettleException {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        IUnifiedRepository mockUnifiedRepository = Mockito.mock(IUnifiedRepository.class);
        JobDelegate jobDelegate = new JobDelegate(mockPurRepository, mockUnifiedRepository);
        JobLogTable mockJobLogTable = Mockito.mock(JobLogTable.class);
        JobEntryCopy mockJobEntryCopy = Mockito.mock(JobEntryCopy.class);
        Map<String, Map<String, String>> attributes = new HashMap<>();
        Map<String, String> group = new HashMap<>();
        final String mockGroup = "MOCK_GROUP";
        final String mockProperty = "MOCK_PROPERTY";
        final String mockValue = "MOCK_VALUE";
        group.put(mockProperty, mockValue);
        attributes.put(mockGroup, group);
        Mockito.when(mockJobEntryCopy.getAttributesMap()).thenReturn(attributes);
        JobDelegateTest.JobEntryBaseAndInterface mockJobEntry = Mockito.mock(JobDelegateTest.JobEntryBaseAndInterface.class);
        Mockito.when(mockJobMeta.listParameters()).thenReturn(new String[]{  });
        Mockito.when(mockJobMeta.getJobLogTable()).thenReturn(mockJobLogTable);
        Mockito.when(mockJobMeta.nrJobEntries()).thenReturn(1);
        Mockito.when(mockJobMeta.getJobEntry(0)).thenReturn(mockJobEntryCopy);
        Mockito.when(mockJobEntryCopy.getName()).thenReturn("MOCK_NAME");
        Mockito.when(mockJobEntryCopy.getLocation()).thenReturn(new Point(0, 0));
        Mockito.when(mockJobEntryCopy.getEntry()).thenReturn(mockJobEntry);
        DataNode dataNode = jobDelegate.elementToDataNode(mockJobMeta);
        DataNode groups = dataNode.getNode("entries").getNodes().iterator().next().getNode(PROP_ATTRIBUTES_JOB_ENTRY_COPY);
        DataNode mockGroupNode = groups.getNode(mockGroup);
        Assert.assertEquals(mockValue, mockGroupNode.getProperty(mockProperty).getString());
    }

    @Test
    public void testElementToDataNodeSavesAttributes() throws KettleException {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        IUnifiedRepository mockUnifiedRepository = Mockito.mock(IUnifiedRepository.class);
        JobDelegate jobDelegate = new JobDelegate(mockPurRepository, mockUnifiedRepository);
        JobLogTable mockJobLogTable = Mockito.mock(JobLogTable.class);
        JobEntryCopy mockJobEntryCopy = Mockito.mock(JobEntryCopy.class);
        Map<String, Map<String, String>> attributes = new HashMap<String, Map<String, String>>();
        Map<String, String> group = new HashMap<String, String>();
        final String mockGroup = "MOCK_GROUP";
        final String mockProperty = "MOCK_PROPERTY";
        final String mockValue = "MOCK_VALUE";
        group.put(mockProperty, mockValue);
        attributes.put(mockGroup, group);
        JobDelegateTest.JobEntryBaseAndInterface mockJobEntry = Mockito.mock(JobDelegateTest.JobEntryBaseAndInterface.class);
        Mockito.when(getAttributesMap()).thenReturn(attributes);
        Mockito.when(mockJobMeta.listParameters()).thenReturn(new String[]{  });
        Mockito.when(mockJobMeta.getJobLogTable()).thenReturn(mockJobLogTable);
        Mockito.when(mockJobMeta.nrJobEntries()).thenReturn(1);
        Mockito.when(mockJobMeta.getJobEntry(0)).thenReturn(mockJobEntryCopy);
        Mockito.when(mockJobEntryCopy.getName()).thenReturn("MOCK_NAME");
        Mockito.when(mockJobEntryCopy.getLocation()).thenReturn(new Point(0, 0));
        Mockito.when(mockJobEntryCopy.getEntry()).thenReturn(mockJobEntry);
        DataNode dataNode = jobDelegate.elementToDataNode(mockJobMeta);
        DataNode groups = dataNode.getNode("entries").getNodes().iterator().next().getNode(NODE_ATTRIBUTE_GROUPS);
        DataNode mockGroupNode = groups.getNode(mockGroup);
        Assert.assertEquals(mockValue, mockGroupNode.getProperty(mockProperty).getString());
    }
}

