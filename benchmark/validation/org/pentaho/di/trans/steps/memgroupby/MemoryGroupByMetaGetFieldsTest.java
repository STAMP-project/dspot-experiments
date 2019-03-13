/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.memgroupby;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Luis Martins
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ValueMetaFactory.class })
public class MemoryGroupByMetaGetFieldsTest {
    private MemoryGroupByMeta memoryGroupByMeta;

    private RowMetaInterface rowMeta;

    private RowMetaInterface[] mockInfo;

    private StepMeta mockNextStep;

    private VariableSpace mockSpace;

    private IMetaStore mockIMetaStore;

    @Test
    public void getFieldsWithSubject_WithFormat() {
        ValueMetaDate valueMeta = new ValueMetaDate();
        valueMeta.setConversionMask("yyyy-MM-dd");
        valueMeta.setName("date");
        Mockito.doReturn(valueMeta).when(rowMeta).searchValueMeta("date");
        memoryGroupByMeta.setSubjectField(new String[]{ "date" });
        memoryGroupByMeta.setGroupField(new String[]{  });
        memoryGroupByMeta.setAggregateField(new String[]{ "maxDate" });
        memoryGroupByMeta.setAggregateType(new int[]{ MemoryGroupByMeta.TYPE_GROUP_MAX });
        memoryGroupByMeta.getFields(rowMeta, "Memory Group by", mockInfo, mockNextStep, mockSpace, null, mockIMetaStore);
        Mockito.verify(rowMeta, Mockito.times(1)).clear();
        Mockito.verify(rowMeta, Mockito.times(1)).addRowMeta(ArgumentMatchers.any());
        Assert.assertEquals("yyyy-MM-dd", rowMeta.searchValueMeta("maxDate").getConversionMask());
    }

    @Test
    public void getFieldsWithSubject_NoFormat() {
        ValueMetaDate valueMeta = new ValueMetaDate();
        valueMeta.setName("date");
        Mockito.doReturn(valueMeta).when(rowMeta).searchValueMeta("date");
        memoryGroupByMeta.setSubjectField(new String[]{ "date" });
        memoryGroupByMeta.setGroupField(new String[]{  });
        memoryGroupByMeta.setAggregateField(new String[]{ "minDate" });
        memoryGroupByMeta.setAggregateType(new int[]{ MemoryGroupByMeta.TYPE_GROUP_MIN });
        memoryGroupByMeta.getFields(rowMeta, "Group by", mockInfo, mockNextStep, mockSpace, null, mockIMetaStore);
        Mockito.verify(rowMeta, Mockito.times(1)).clear();
        Mockito.verify(rowMeta, Mockito.times(1)).addRowMeta(ArgumentMatchers.any());
        Assert.assertEquals(null, rowMeta.searchValueMeta("minDate").getConversionMask());
    }

    @Test
    public void getFieldsWithoutSubject() {
        ValueMetaDate valueMeta = new ValueMetaDate();
        valueMeta.setName("date");
        Mockito.doReturn(valueMeta).when(rowMeta).searchValueMeta("date");
        memoryGroupByMeta.setSubjectField(new String[]{ null });
        memoryGroupByMeta.setGroupField(new String[]{ "date" });
        memoryGroupByMeta.setAggregateField(new String[]{ "countDate" });
        memoryGroupByMeta.setAggregateType(new int[]{ MemoryGroupByMeta.TYPE_GROUP_COUNT_ANY });
        memoryGroupByMeta.getFields(rowMeta, "Group by", mockInfo, mockNextStep, mockSpace, null, mockIMetaStore);
        Mockito.verify(rowMeta, Mockito.times(1)).clear();
        Mockito.verify(rowMeta, Mockito.times(1)).addRowMeta(ArgumentMatchers.any());
        Assert.assertNotNull(rowMeta.searchValueMeta("countDate"));
    }
}

