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
package org.pentaho.di.engine.configuration.impl.pentaho.scheduler;


import java.io.UnsupportedEncodingException;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoryDirectoryInterface;


public class SchedulerRequestTest {
    // input file
    private static final String TEST_REPOSITORY_DIRECTORY = "/home/admin";

    private static final String TEST_JOB_NAME = "jobName";

    private static final String JOB_EXTENSION = "kjb";

    // job parameters
    private static final String STRING_PARAM_TYPE = "string";

    private static final String LOG_LEVEL_PARAM_NAME = "logLevel";

    private static final String TEST_LOG_LEVEL_PARAM_VALUE = "Rowlevel";

    private static final String CLEAR_LOG_PARAM_NAME = "clearLog";

    private static final String TEST_CLEAR_LOG_PARAM_VALUE = "true";

    private static final String RUN_SAFE_MODE_PARAM_NAME = "runSafeMode";

    private static final String TEST_RUN_SAFE_MODE_PARAM_VALUE = "false";

    private static final String GATHERING_METRICS_PARAM_NAME = "gatheringMetrics";

    private static final String TEST_GATHERING_METRICS_PARAM_VALUE = "false";

    private static final String START_COPY_NAME_PARAM_NAME = "startCopyName";

    private static final String TEST_START_COPY_NAME_PARAM_VALUE = "stepName";

    private static final String EXPANDING_REMOTE_JOB_PARAM_NAME = "expandingRemoteJob";

    private static final String TEST_EXPANDING_REMOTE_JOB_PARAM_VALUE = "false";

    // pdi parameters
    private static final String TEST_PDI_PARAM_NAME = "paramName";

    private static final String TEST_PDI_PARAM_VALUE = "paramValue";

    private static final String[] ARRAY_WITH_TEST_PDI_PARAM_NAME = new String[]{ SchedulerRequestTest.TEST_PDI_PARAM_NAME };

    private static final String REFERENCE_TEST_REQUEST = String.format(("<jobScheduleRequest>\n" + ((((((((((((((((((((((((((((((((((((("<inputFile>%s/%s.%s</inputFile>\n" + "<jobParameters>\n") + "<name>%s</name>\n") + "<type>%s</type>\n") + "<stringValue>%s</stringValue>\n") + "</jobParameters>\n") + "<jobParameters>\n") + "<name>%s</name>\n") + "<type>%s</type>\n") + "<stringValue>%s</stringValue>\n") + "</jobParameters>\n") + "<jobParameters>\n") + "<name>%s</name>\n") + "<type>%s</type>\n") + "<stringValue>%s</stringValue>\n") + "</jobParameters>\n") + "<jobParameters>\n") + "<name>%s</name>\n") + "<type>%s</type>\n") + "<stringValue>%s</stringValue>\n") + "</jobParameters>\n") + "<jobParameters>\n") + "<name>%s</name>\n") + "<type>%s</type>\n") + "<stringValue>%s</stringValue>\n") + "</jobParameters>\n") + "<jobParameters>\n") + "<name>%s</name>\n") + "<type>%s</type>\n") + "<stringValue>%s</stringValue>\n") + "</jobParameters>\n") + "<pdiParameters>\n") + "<entry>\n") + "<key>%s</key>\n") + "<value>%s</value>\n") + "</entry>\n") + "</pdiParameters>\n") + "</jobScheduleRequest>")), SchedulerRequestTest.TEST_REPOSITORY_DIRECTORY, SchedulerRequestTest.TEST_JOB_NAME, SchedulerRequestTest.JOB_EXTENSION, SchedulerRequestTest.LOG_LEVEL_PARAM_NAME, SchedulerRequestTest.STRING_PARAM_TYPE, SchedulerRequestTest.TEST_LOG_LEVEL_PARAM_VALUE, SchedulerRequestTest.CLEAR_LOG_PARAM_NAME, SchedulerRequestTest.STRING_PARAM_TYPE, SchedulerRequestTest.TEST_CLEAR_LOG_PARAM_VALUE, SchedulerRequestTest.RUN_SAFE_MODE_PARAM_NAME, SchedulerRequestTest.STRING_PARAM_TYPE, SchedulerRequestTest.TEST_RUN_SAFE_MODE_PARAM_VALUE, SchedulerRequestTest.GATHERING_METRICS_PARAM_NAME, SchedulerRequestTest.STRING_PARAM_TYPE, SchedulerRequestTest.TEST_GATHERING_METRICS_PARAM_VALUE, SchedulerRequestTest.START_COPY_NAME_PARAM_NAME, SchedulerRequestTest.STRING_PARAM_TYPE, SchedulerRequestTest.TEST_START_COPY_NAME_PARAM_VALUE, SchedulerRequestTest.EXPANDING_REMOTE_JOB_PARAM_NAME, SchedulerRequestTest.STRING_PARAM_TYPE, SchedulerRequestTest.TEST_EXPANDING_REMOTE_JOB_PARAM_VALUE, SchedulerRequestTest.TEST_PDI_PARAM_NAME, SchedulerRequestTest.TEST_PDI_PARAM_VALUE);

    private SchedulerRequest schedulerRequest;

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testBuildSchedulerRequestEntity() throws UnsupportedEncodingException, UnknownParamException {
        AbstractMeta meta = Mockito.mock(JobMeta.class);
        RepositoryDirectoryInterface repositoryDirectory = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(repositoryDirectory).when(meta).getRepositoryDirectory();
        Mockito.doReturn(SchedulerRequestTest.TEST_REPOSITORY_DIRECTORY).when(repositoryDirectory).getPath();
        Mockito.doReturn(SchedulerRequestTest.TEST_JOB_NAME).when(meta).getName();
        Mockito.doReturn(SchedulerRequestTest.JOB_EXTENSION).when(meta).getDefaultExtension();
        Mockito.doReturn(LogLevel.getLogLevelForCode(SchedulerRequestTest.TEST_LOG_LEVEL_PARAM_VALUE)).when(meta).getLogLevel();
        Mockito.doReturn(Boolean.valueOf(SchedulerRequestTest.TEST_CLEAR_LOG_PARAM_VALUE)).when(meta).isClearingLog();
        Mockito.doReturn(Boolean.valueOf(SchedulerRequestTest.TEST_RUN_SAFE_MODE_PARAM_VALUE)).when(meta).isSafeModeEnabled();
        Mockito.doReturn(Boolean.valueOf(SchedulerRequestTest.TEST_GATHERING_METRICS_PARAM_VALUE)).when(meta).isGatheringMetrics();
        Mockito.doReturn(SchedulerRequestTest.TEST_START_COPY_NAME_PARAM_VALUE).when(((JobMeta) (meta))).getStartCopyName();
        Mockito.doReturn(Boolean.valueOf(SchedulerRequestTest.TEST_EXPANDING_REMOTE_JOB_PARAM_VALUE)).when(((JobMeta) (meta))).isExpandingRemoteJob();
        Mockito.doReturn(SchedulerRequestTest.ARRAY_WITH_TEST_PDI_PARAM_NAME).when(meta).listParameters();
        Mockito.doReturn(SchedulerRequestTest.TEST_PDI_PARAM_VALUE).when(meta).getParameterValue(SchedulerRequestTest.TEST_PDI_PARAM_NAME);
        Mockito.doCallRealMethod().when(schedulerRequest).buildSchedulerRequestEntity(meta);
        Assert.assertTrue(compareContentOfStringEntities(schedulerRequest.buildSchedulerRequestEntity(meta), new StringEntity(SchedulerRequestTest.REFERENCE_TEST_REQUEST)));
    }
}

