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
package org.pentaho.di.trans.steps.mergerows;


import CheckResultInterface.TYPE_RESULT_ERROR;
import CheckResultInterface.TYPE_RESULT_OK;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;


public class MergeRowsMetaCheckTest {
    private TransMeta transMeta;

    private MergeRowsMeta meta;

    private StepMeta stepMeta;

    private static final String STEP_NAME = "MERGE_ROWS_META_CHECK_TEST_STEP_NAME";

    private static final String REFERENCE_STEP_NAME = "REFERENCE_STEP";

    private static final String COMPARISON_STEP_NAME = "COMPARISON_STEP";

    private StepMeta referenceStepMeta;

    private StepMeta comparisonStepMeta;

    private List<CheckResultInterface> remarks;

    @Test
    public void testCheckInputRowsBothEmpty() throws KettleStepException {
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.REFERENCE_STEP_NAME)).thenReturn(generateRowMetaEmpty());
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.COMPARISON_STEP_NAME)).thenReturn(generateRowMetaEmpty());
        meta.check(remarks, transMeta, stepMeta, ((RowMeta) (null)), new String[0], new String[0], ((RowMeta) (null)), new Variables(), ((Repository) (null)), ((IMetaStore) (null)));
        Assert.assertNotNull(remarks);
        Assert.assertTrue(((remarks.size()) >= 2));
        Assert.assertEquals(remarks.get(1).getType(), TYPE_RESULT_OK);
    }

    @Test
    public void testCheckInputRowsBothNonEmpty() throws KettleStepException {
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.REFERENCE_STEP_NAME)).thenReturn(generateRowMeta10Strings());
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.COMPARISON_STEP_NAME)).thenReturn(generateRowMeta10Strings());
        meta.check(remarks, transMeta, stepMeta, ((RowMeta) (null)), new String[0], new String[0], ((RowMeta) (null)), new Variables(), ((Repository) (null)), ((IMetaStore) (null)));
        Assert.assertNotNull(remarks);
        Assert.assertTrue(((remarks.size()) >= 2));
        Assert.assertEquals(remarks.get(1).getType(), TYPE_RESULT_OK);
    }

    @Test
    public void testCheckInputRowsEmptyAndNonEmpty() throws KettleStepException {
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.REFERENCE_STEP_NAME)).thenReturn(generateRowMetaEmpty());
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.COMPARISON_STEP_NAME)).thenReturn(generateRowMeta10Strings());
        meta.check(remarks, transMeta, stepMeta, ((RowMeta) (null)), new String[0], new String[0], ((RowMeta) (null)), new Variables(), ((Repository) (null)), ((IMetaStore) (null)));
        Assert.assertNotNull(remarks);
        Assert.assertTrue(((remarks.size()) >= 2));
        Assert.assertEquals(remarks.get(1).getType(), TYPE_RESULT_ERROR);
    }

    @Test
    public void testCheckInputRowsDifferentRowMetaTypes() throws KettleStepException {
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.REFERENCE_STEP_NAME)).thenReturn(generateRowMeta10MixedTypes());
        Mockito.when(transMeta.getPrevStepFields(MergeRowsMetaCheckTest.COMPARISON_STEP_NAME)).thenReturn(generateRowMeta10Strings());
        meta.check(remarks, transMeta, stepMeta, ((RowMeta) (null)), new String[0], new String[0], ((RowMeta) (null)), new Variables(), ((Repository) (null)), ((IMetaStore) (null)));
        Assert.assertNotNull(remarks);
        Assert.assertTrue(((remarks.size()) >= 2));
        Assert.assertEquals(remarks.get(1).getType(), TYPE_RESULT_ERROR);
    }
}

