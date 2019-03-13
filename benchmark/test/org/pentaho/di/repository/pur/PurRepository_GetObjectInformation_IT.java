/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
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


import RepositoryObjectType.JOB;
import java.io.Serializable;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class PurRepository_GetObjectInformation_IT extends PurRepositoryTestBase {
    public PurRepository_GetObjectInformation_IT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    @Test
    public void getObjectInformation_IsDeletedFlagSet_Job() throws Exception {
        testDeletedFlagForObject(new Callable<RepositoryElementInterface>() {
            @Override
            public RepositoryElementInterface call() throws Exception {
                JobMeta jobMeta = new JobMeta();
                jobMeta.setName("testJobMeta");
                return jobMeta;
            }
        });
    }

    @Test
    public void getObjectInformation_IsDeletedFlagSet_Trans() throws Exception {
        testDeletedFlagForObject(new Callable<RepositoryElementInterface>() {
            @Override
            public RepositoryElementInterface call() throws Exception {
                TransMeta transMeta = new TransMeta();
                transMeta.setName("testTransMeta");
                return transMeta;
            }
        });
    }

    @Test
    public void getObjectInformation_InvalidRepositoryId_ExceptionIsHandled() throws Exception {
        IUnifiedRepository unifiedRepository = Mockito.mock(IUnifiedRepository.class);
        Mockito.when(unifiedRepository.getFileById(ArgumentMatchers.any(Serializable.class))).thenThrow(new RuntimeException("unknown id"));
        purRepository.setTest(unifiedRepository);
        RepositoryObject information = purRepository.getObjectInformation(new StringObjectId("invalid id"), JOB);
        Assert.assertNull("Should return null if file was not found", information);
    }

    @Test
    public void getObjectInformation_InvalidRepositoryId_NullIsHandled() throws Exception {
        IUnifiedRepository unifiedRepository = Mockito.mock(IUnifiedRepository.class);
        Mockito.when(unifiedRepository.getFileById(ArgumentMatchers.any(Serializable.class))).thenReturn(null);
        purRepository.setTest(unifiedRepository);
        RepositoryObject information = purRepository.getObjectInformation(new StringObjectId("invalid id"), JOB);
        Assert.assertNull("Should return null if file was not found", information);
    }
}

