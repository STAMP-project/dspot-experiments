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
package org.pentaho.di.ui.job.entries.trans;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vadim_Polynkov
 */
public class JobEntryTransDialogTest {
    private static final String FILE_NAME = "TestTrans.ktr";

    JobEntryTransDialog dialog;

    @Test
    public void testEntryName() {
        dialog = Mockito.mock(JobEntryTransDialog.class);
        Mockito.doCallRealMethod().when(dialog).getEntryName(ArgumentMatchers.any());
        Assert.assertEquals(dialog.getEntryName(JobEntryTransDialogTest.FILE_NAME), ("${Internal.Entry.Current.Directory}/" + (JobEntryTransDialogTest.FILE_NAME)));
    }
}

