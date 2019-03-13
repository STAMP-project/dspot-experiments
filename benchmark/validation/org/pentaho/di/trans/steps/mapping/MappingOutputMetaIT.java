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
package org.pentaho.di.trans.steps.mapping;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mappingoutput.MappingOutputMeta;
import org.pentaho.metastore.api.IMetaStore;


public class MappingOutputMetaIT {
    private Repository repository = Mockito.mock(Repository.class);

    private IMetaStore metaStore = Mockito.mock(IMetaStore.class);

    private VariableSpace space = Mockito.mock(VariableSpace.class);

    private StepMeta nextStep = Mockito.mock(StepMeta.class);

    private RowMetaInterface[] info = new RowMetaInterface[]{ Mockito.mock(RowMetaInterface.class) };

    @Test
    public void testGetFields_OutputValueRenames_WillRenameOutputIfValueMetaExist() throws KettleStepException {
        ValueMetaInterface valueMeta1 = new ValueMetaBase("valueMeta1");
        ValueMetaInterface valueMeta2 = new ValueMetaBase("valueMeta2");
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(valueMeta1);
        rowMeta.addValueMeta(valueMeta2);
        List<MappingValueRename> outputValueRenames = new ArrayList<MappingValueRename>();
        outputValueRenames.add(new MappingValueRename("valueMeta2", "valueMeta1"));
        MappingOutputMeta meta = new MappingOutputMeta();
        meta.setOutputValueRenames(outputValueRenames);
        meta.getFields(rowMeta, null, info, nextStep, space, repository, metaStore);
        // we must not add additional field
        Assert.assertEquals(2, rowMeta.getValueMetaList().size());
        // we must not keep the first value meta since we want to rename second
        Assert.assertEquals(valueMeta1, rowMeta.getValueMeta(0));
        // the second value meta must be other than we want to rename since we already have value meta with such name
        Assert.assertFalse("valueMeta1".equals(rowMeta.getValueMeta(1).getName()));
        // the second value meta must be other than we want to rename since we already have value meta with such name.
        // It must be renamed according the rules from the #RowMeta
        Assert.assertTrue("valueMeta1_1".equals(rowMeta.getValueMeta(1).getName()));
    }
}

