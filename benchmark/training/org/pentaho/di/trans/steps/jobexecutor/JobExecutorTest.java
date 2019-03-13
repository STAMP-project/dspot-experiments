/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.jobexecutor;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;


/**
 *
 *
 * @author Mikhail_Chen-Len-Son
 */
public class JobExecutorTest {
    private JobExecutor executor;

    private JobExecutorMeta meta;

    private JobExecutorData data;

    /**
     * Given an input data and a job executor with specified field to group rows on.
     * <br/>
     * When job executor is processing rows of an input data,
     * then rows should be accumulated in a group as long as the specified field value stays the same.
     */
    @Test
    public void shouldAccumulateRowsWhenGroupFieldIsSpecified() throws KettleException {
        prepareMultipleRowsForExecutor();
        data.groupField = "groupField";
        executor.init(meta, data);
        Mockito.when(executor.getExecutorJob()).thenReturn(Mockito.mock(Job.class));
        Mockito.when(executor.getExecutorJob().getJobMeta()).thenReturn(Mockito.mock(JobMeta.class));
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("groupField"));
        executor.setInputRowMeta(rowMeta);
        // start processing
        executor.processRow(meta, data);// 1st row - 'value1'

        // should be added to group buffer
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);
        executor.processRow(meta, data);
        executor.processRow(meta, data);// 4th row - still 'value1'

        // first 4 rows should be added to the same group
        Assert.assertEquals(4, data.groupBuffer.size());
        executor.processRow(meta, data);// 5th row - value has been changed - 'value12'

        // previous group buffer should be flushed
        // and a new group should be started
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);// 6th row - 'value12'

        executor.processRow(meta, data);// 7th row - 'value12'

        // the rest rows should be added to another group
        Assert.assertEquals(3, data.groupBuffer.size());
        executor.processRow(meta, data);// end of file

        // group buffer should be flushed in the end
        Assert.assertEquals(0, data.groupBuffer.size());
    }

    /**
     * Given an input data and a job executor
     * with specified number of rows to send to the transformation (X).
     * <br/>
     * When job executor is processing rows of an input data,
     * then every X rows should be accumulated in a group.
     */
    @Test
    public void shouldAccumulateRowsByCount() throws KettleException {
        prepareMultipleRowsForExecutor();
        data.groupSize = 5;
        executor.init(meta, data);
        Mockito.when(executor.getExecutorJob()).thenReturn(Mockito.mock(Job.class));
        Mockito.when(executor.getExecutorJob().getJobMeta()).thenReturn(Mockito.mock(JobMeta.class));
        // start processing
        executor.processRow(meta, data);// 1st row

        // should be added to group buffer
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);
        executor.processRow(meta, data);
        executor.processRow(meta, data);// 4th row

        // first 4 rows should be added to the same group
        Assert.assertEquals(4, data.groupBuffer.size());
        executor.processRow(meta, data);// 5th row

        // once the 5th row is processed, the transformation executor should be triggered
        // and thus, group buffer should be flushed
        Assert.assertEquals(0, data.groupBuffer.size());
        executor.processRow(meta, data);// 6th row

        // previous group buffer should be flushed
        // and a new group should be started
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);// 7th row

        // the rest rows should be added to another group
        Assert.assertEquals(2, data.groupBuffer.size());
        executor.processRow(meta, data);// end of file

        // group buffer should be flushed in the end
        Assert.assertEquals(0, data.groupBuffer.size());
    }
}

