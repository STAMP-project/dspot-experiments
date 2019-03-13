/**
 * *****************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
 *
 *  *******************************************************************************
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License. You may obtain a copy of the
 *  License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.engine.configuration.impl.extension;


import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.attributes.metastore.EmbeddedMetaStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.engine.configuration.api.RunConfiguration;
import org.pentaho.di.engine.configuration.impl.RunConfigurationManager;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionExtension;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.job.JobEntryJob;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransExecutionConfiguration;


/**
 * Created by bmorrise on 5/4/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RunConfigurationInjectExtensionPointTest {
    RunConfigurationInjectExtensionPoint runConfigurationInjectExtensionPoint;

    private final String runConfName = "RUN_CONF";

    @Mock
    private RunConfigurationManager runConfigurationManager;

    @Mock
    private TransExecutionConfiguration transExecutionConfiguration;

    @Mock
    private AbstractMeta abstractMeta;

    @Mock
    private LogChannelInterface log;

    @Mock
    private EmbeddedMetaStore embeddedMetaStore;

    @Mock
    private RunConfiguration runConfiguration;

    @Mock
    private JobExecutionExtension executionExt;

    @Mock
    private JobEntryCopy jobEntryCopy;

    @Mock
    private Result result;

    @Mock
    private Job job;

    @Mock
    private JobMeta jobMeta;

    private Map<JobEntryCopy, JobEntryJob> jobs = new HashMap<>();

    private Map<JobEntryCopy, JobEntryTrans> trans = new HashMap<>();

    @Mock
    private JobEntryTrans jobEntryTrans;

    @Mock
    private JobEntryJob jobEntryJobs;

    @Test
    public void testCallExtensionPoint() throws Exception {
        runConfigurationInjectExtensionPoint.callExtensionPoint(log, executionExt);
        Mockito.verify(runConfigurationManager, Mockito.times(2)).load(ArgumentMatchers.eq(runConfName));
    }
}

