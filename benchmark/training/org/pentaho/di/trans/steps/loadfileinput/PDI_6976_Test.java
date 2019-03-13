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
package org.pentaho.di.trans.steps.loadfileinput;


import java.util.List;
import javax.tools.FileObject;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;


/**
 * Tests for LoadFileInputMeta class
 *
 * @author Pavel Sakun
 * @see LoadFileInputMeta
 */
public class PDI_6976_Test {
    @Test
    public void testVerifyNoPreviousStep() {
        LoadFileInputMeta spy = Mockito.spy(new LoadFileInputMeta());
        FileInputList fileInputList = Mockito.mock(FileInputList.class);
        List<FileObject> files = Mockito.when(Mockito.mock(List.class).size()).thenReturn(1).getMock();
        Mockito.doReturn(files).when(fileInputList).getFiles();
        Mockito.doReturn(fileInputList).when(spy).getFiles(ArgumentMatchers.any(VariableSpace.class));
        @SuppressWarnings("unchecked")
        List<CheckResultInterface> validationResults = Mockito.mock(List.class);
        // Check we do not get validation errors
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if ((getType()) != (CheckResultInterface.TYPE_RESULT_OK)) {
                    TestCase.fail("We've got validation error");
                }
                return null;
            }
        }).when(validationResults).add(ArgumentMatchers.any(CheckResultInterface.class));
        spy.check(validationResults, Mockito.mock(TransMeta.class), Mockito.mock(StepMeta.class), Mockito.mock(RowMetaInterface.class), new String[]{  }, new String[]{ "File content", "File size" }, Mockito.mock(RowMetaInterface.class), Mockito.mock(VariableSpace.class), Mockito.mock(Repository.class), Mockito.mock(IMetaStore.class));
    }
}

