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
package org.pentaho.di.job.entries.http;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class JobEntryHTTPTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private JobEntryHTTP jobEntryHttp = new JobEntryHTTP();

    private KettleDatabaseRepository ktlDbRepMock = Mockito.mock(KettleDatabaseRepository.class);

    private ObjectId objIdMock = Mockito.mock(ObjectId.class);

    @Test
    public void testDateTimeAddedFieldIsSetInTrue_WhenRepoReturnsTrue() throws KettleException {
        Mockito.when(ktlDbRepMock.getJobEntryAttributeBoolean(objIdMock, "date_time_added")).thenReturn(true);
        jobEntryHttp.loadRep(ktlDbRepMock, ktlDbRepMock.getMetaStore(), objIdMock, null, null);
        Mockito.verify(ktlDbRepMock, Mockito.never()).getJobEntryAttributeString(objIdMock, "date_time_added");
        Mockito.verify(ktlDbRepMock).getJobEntryAttributeBoolean(objIdMock, "date_time_added");
        Assert.assertTrue("DateTimeAdded field should be TRUE.", jobEntryHttp.isDateTimeAdded());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeprecatedTargetFilenameExtension() {
        jobEntryHttp.setTargetFilenameExtention("txt");
        Assert.assertTrue("txt".equals(jobEntryHttp.getTargetFilenameExtension()));
        jobEntryHttp.setTargetFilenameExtension("zip");
        Assert.assertTrue("zip".equals(jobEntryHttp.getTargetFilenameExtention()));
    }
}

