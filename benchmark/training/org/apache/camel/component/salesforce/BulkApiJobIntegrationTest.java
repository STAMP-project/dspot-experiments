/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce;


import com.googlecode.junittoolbox.ParallelParameterized;
import org.apache.camel.component.salesforce.api.dto.bulk.JobInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(ParallelParameterized.class)
public class BulkApiJobIntegrationTest extends AbstractBulkApiTestBase {
    @Parameterized.Parameter(0)
    public JobInfo jobInfo;

    @Parameterized.Parameter(1)
    public String operationName;

    @Test
    public void testJobLifecycle() throws Exception {
        log.info("Testing Job lifecycle for {} of type {}", jobInfo.getOperation(), jobInfo.getContentType());
        // test create
        jobInfo = createJob(jobInfo);
        // test get
        jobInfo = template().requestBody("direct:getJob", jobInfo, JobInfo.class);
        assertSame("Job should be OPEN", JobStateEnum.OPEN, jobInfo.getState());
        // test close
        jobInfo = template().requestBody("direct:closeJob", jobInfo, JobInfo.class);
        assertSame("Job should be CLOSED", JobStateEnum.CLOSED, jobInfo.getState());
        // test abort
        jobInfo = template().requestBody("direct:abortJob", jobInfo, JobInfo.class);
        assertSame("Job should be ABORTED", JobStateEnum.ABORTED, jobInfo.getState());
    }
}

