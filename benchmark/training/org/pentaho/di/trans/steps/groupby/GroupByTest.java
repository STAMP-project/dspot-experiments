/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.groupby;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.BlockingRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.metastore.api.IMetaStore;

import static GroupByMeta.TYPE_GROUP_CONCAT_COMMA;
import static GroupByMeta.TYPE_GROUP_CONCAT_STRING;
import static GroupByMeta.TYPE_GROUP_COUNT_ALL;
import static GroupByMeta.TYPE_GROUP_COUNT_ANY;
import static GroupByMeta.TYPE_GROUP_COUNT_DISTINCT;
import static GroupByMeta.TYPE_GROUP_MEDIAN;
import static GroupByMeta.TYPE_GROUP_PERCENTILE;
import static GroupByMeta.TYPE_GROUP_STANDARD_DEVIATION;


public class GroupByTest {
    private StepMockHelper<GroupByMeta, GroupByData> mockHelper;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testProcessRow() throws KettleException {
        GroupByMeta groupByMeta = Mockito.mock(GroupByMeta.class);
        GroupByData groupByData = Mockito.mock(GroupByData.class);
        GroupBy groupBySpy = Mockito.spy(new GroupBy(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans));
        Mockito.doReturn(null).when(groupBySpy).getRow();
        Mockito.doReturn(null).when(groupBySpy).getInputRowMeta();
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaInteger("ROWNR"));
        List<RowSet> outputRowSets = new ArrayList<RowSet>();
        BlockingRowSet rowSet = new BlockingRowSet(1);
        rowSet.putRow(rowMeta, new Object[]{ new Long(0) });
        outputRowSets.add(rowSet);
        groupBySpy.setOutputRowSets(outputRowSets);
        final String[] sub = new String[]{ "b" };
        Mockito.doReturn(sub).when(groupByMeta).getSubjectField();
        final String[] groupField = new String[]{ "a" };
        Mockito.doReturn(groupField).when(groupByMeta).getGroupField();
        final String[] aggFields = new String[]{ "b_g" };
        Mockito.doReturn(aggFields).when(groupByMeta).getAggregateField();
        final int[] aggType = new int[]{ TYPE_GROUP_CONCAT_COMMA };
        Mockito.doReturn(aggType).when(groupByMeta).getAggregateType();
        Mockito.when(mockHelper.transMeta.getPrevStepFields(mockHelper.stepMeta)).thenReturn(new RowMeta());
        groupBySpy.processRow(groupByMeta, groupByData);
        Assert.assertTrue(groupBySpy.getOutputRowSets().get(0).isDone());
    }

    @Test
    public void testGetFields() {
        RowMeta outputFields = new RowMeta();
        outputFields.addValueMeta(new ValueMetaString("group_by_field"));
        outputFields.addValueMeta(new ValueMetaInteger("raw_integer"));
        outputFields.addValueMeta(new ValueMetaString("raw_string"));
        GroupByMeta meta = new GroupByMeta();
        meta.allocate(1, 8);
        meta.setGroupField(new String[]{ "group_by_field" });
        meta.setAggregateField(new String[]{ "perc_field", "stddev_field", "median_field", "count_distinct_field", "count_any_field", "count_all_field", "concat_comma_field", "concat_custom_field" });
        meta.setSubjectField(new String[]{ "raw_integer", "raw_integer", "raw_integer", "raw_integer", "raw_integer", "raw_integer", "raw_string", "raw_string" });
        meta.setAggregateType(new int[]{ TYPE_GROUP_PERCENTILE, TYPE_GROUP_STANDARD_DEVIATION, TYPE_GROUP_MEDIAN, TYPE_GROUP_COUNT_DISTINCT, TYPE_GROUP_COUNT_ANY, TYPE_GROUP_COUNT_ALL, TYPE_GROUP_CONCAT_COMMA, TYPE_GROUP_CONCAT_STRING });
        meta.getFields(outputFields, "Group By Step", ((RowMetaInterface[]) (null)), ((StepMeta) (null)), ((Variables) (null)), ((Repository) (null)), ((IMetaStore) (null)));
        Assert.assertEquals(outputFields.getValueMetaList().size(), 9);
        Assert.assertTrue(((outputFields.getValueMeta(0).getType()) == (ValueMetaInterface.TYPE_STRING)));
        Assert.assertTrue(outputFields.getValueMeta(0).getName().equals("group_by_field"));
        Assert.assertTrue(((outputFields.getValueMeta(1).getType()) == (ValueMetaInterface.TYPE_NUMBER)));
        Assert.assertTrue(outputFields.getValueMeta(1).getName().equals("perc_field"));
        Assert.assertTrue(((outputFields.getValueMeta(2).getType()) == (ValueMetaInterface.TYPE_NUMBER)));
        Assert.assertTrue(outputFields.getValueMeta(2).getName().equals("stddev_field"));
        Assert.assertTrue(((outputFields.getValueMeta(3).getType()) == (ValueMetaInterface.TYPE_NUMBER)));
        Assert.assertTrue(outputFields.getValueMeta(3).getName().equals("median_field"));
        Assert.assertTrue(((outputFields.getValueMeta(4).getType()) == (ValueMetaInterface.TYPE_INTEGER)));
        Assert.assertTrue(outputFields.getValueMeta(4).getName().equals("count_distinct_field"));
        Assert.assertTrue(((outputFields.getValueMeta(5).getType()) == (ValueMetaInterface.TYPE_INTEGER)));
        Assert.assertTrue(outputFields.getValueMeta(5).getName().equals("count_any_field"));
        Assert.assertTrue(((outputFields.getValueMeta(6).getType()) == (ValueMetaInterface.TYPE_INTEGER)));
        Assert.assertTrue(outputFields.getValueMeta(6).getName().equals("count_all_field"));
        Assert.assertTrue(((outputFields.getValueMeta(7).getType()) == (ValueMetaInterface.TYPE_STRING)));
        Assert.assertTrue(outputFields.getValueMeta(7).getName().equals("concat_comma_field"));
        Assert.assertTrue(((outputFields.getValueMeta(8).getType()) == (ValueMetaInterface.TYPE_STRING)));
        Assert.assertTrue(outputFields.getValueMeta(8).getName().equals("concat_custom_field"));
    }

    @Test
    public void testTempFileIsDeleted_AfterCallingDisposeMethod() throws Exception {
        GroupByData groupByData = new GroupByData();
        groupByData.tempFile = File.createTempFile("test", ".txt");
        // emulate connections to file are opened
        groupByData.fosToTempFile = new FileOutputStream(groupByData.tempFile);
        groupByData.fisToTmpFile = new FileInputStream(groupByData.tempFile);
        GroupBy groupBySpy = Mockito.spy(new GroupBy(mockHelper.stepMeta, groupByData, 0, mockHelper.transMeta, mockHelper.trans));
        Assert.assertTrue(groupByData.tempFile.exists());
        groupBySpy.dispose(Mockito.mock(StepMetaInterface.class), groupByData);
        // check file is deleted
        Assert.assertFalse(groupByData.tempFile.exists());
    }

    @Test
    public void testAddToBuffer() throws FileSystemException, KettleException {
        GroupByData groupByData = new GroupByData();
        ArrayList listMock = Mockito.mock(ArrayList.class);
        Mockito.when(listMock.size()).thenReturn(5001);
        groupByData.bufferList = listMock;
        groupByData.rowsOnFile = 0;
        RowMetaInterface inputRowMetaMock = Mockito.mock(RowMetaInterface.class);
        groupByData.inputRowMeta = inputRowMetaMock;
        GroupBy groupBySpy = Mockito.spy(new GroupBy(mockHelper.stepMeta, groupByData, 0, mockHelper.transMeta, mockHelper.trans));
        GroupByMeta groupByMetaMock = Mockito.mock(GroupByMeta.class);
        Mockito.when(groupByMetaMock.getPrefix()).thenReturn("group-by-test-temp-file-");
        Mockito.when(groupBySpy.getMeta()).thenReturn(groupByMetaMock);
        String userDir = System.getProperty("user.dir");
        String vfsFilePath = "file:///" + userDir;
        Mockito.when(groupBySpy.environmentSubstitute(ArgumentMatchers.anyString())).thenReturn(vfsFilePath);
        Object[] row = new Object[]{ "abc" };
        // tested method itself
        groupBySpy.addToBuffer(row);
        // check if file is created
        Assert.assertTrue(groupByData.tempFile.exists());
        groupBySpy.dispose(groupByMetaMock, groupByData);
        // check file is deleted
        Assert.assertFalse(groupByData.tempFile.exists());
        // since path started with "file:///"
        Mockito.verify(groupBySpy, Mockito.times(1)).retrieveVfsPath(ArgumentMatchers.anyString());
    }
}

