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
package org.pentaho.di.job.entries.unzip;


import java.lang.reflect.Method;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.job.entry.loadSave.JobEntryLoadSaveTestSupport;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class JobEntryUnZipTest extends JobEntryLoadSaveTestSupport<JobEntryUnZip> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void unzipPostProcessingTest() throws Exception {
        JobEntryUnZip jobEntryUnZip = new JobEntryUnZip();
        Method unzipPostprocessingMethod = jobEntryUnZip.getClass().getDeclaredMethod("doUnzipPostProcessing", FileObject.class, FileObject.class, String.class);
        unzipPostprocessingMethod.setAccessible(true);
        FileObject sourceFileObject = Mockito.mock(FileObject.class);
        Mockito.doReturn(Mockito.mock(FileName.class)).when(sourceFileObject).getName();
        // delete
        jobEntryUnZip.afterunzip = 1;
        unzipPostprocessingMethod.invoke(jobEntryUnZip, sourceFileObject, Mockito.mock(FileObject.class), "");
        Mockito.verify(sourceFileObject, Mockito.times(1)).delete();
        // move
        jobEntryUnZip.afterunzip = 2;
        unzipPostprocessingMethod.invoke(jobEntryUnZip, sourceFileObject, Mockito.mock(FileObject.class), "");
        Mockito.verify(sourceFileObject, Mockito.times(1)).moveTo(Mockito.anyObject());
    }
}

