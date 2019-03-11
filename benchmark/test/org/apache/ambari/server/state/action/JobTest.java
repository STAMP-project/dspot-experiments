/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.state.action;


import ActionState.INIT;
import org.junit.Assert;
import org.junit.Test;


public class JobTest {
    @Test
    public void testNewJob() {
        long currentTime = System.currentTimeMillis();
        Action job = createNewJob(1, "JobNameFoo", currentTime);
        verifyNewJob(job, currentTime);
    }

    @Test
    public void testJobProgressUpdates() throws Exception {
        long currentTime = 1;
        Action job = createNewJob(1, "JobNameFoo", currentTime);
        verifyNewJob(job, currentTime);
        verifyProgressUpdate(job, (++currentTime));
        verifyProgressUpdate(job, (++currentTime));
        verifyProgressUpdate(job, (++currentTime));
    }

    @Test
    public void testJobSuccessfulCompletion() throws Exception {
        long currentTime = 1;
        Action job = getRunningJob(1, "JobNameFoo", currentTime);
        completeJob(job, false, (++currentTime));
    }

    @Test
    public void testJobFailedCompletion() throws Exception {
        long currentTime = 1;
        Action job = getRunningJob(1, "JobNameFoo", currentTime);
        completeJob(job, true, (++currentTime));
    }

    @Test
    public void completeNewJob() throws Exception {
        long currentTime = 1;
        Action job = createNewJob(1, "JobNameFoo", currentTime);
        verifyNewJob(job, currentTime);
        completeJob(job, false, (++currentTime));
    }

    @Test
    public void failNewJob() throws Exception {
        long currentTime = 1;
        Action job = createNewJob(1, "JobNameFoo", currentTime);
        verifyNewJob(job, currentTime);
        completeJob(job, true, (++currentTime));
    }

    @Test
    public void reInitCompletedJob() throws Exception {
        Action job = getCompletedJob(1, "JobNameFoo", 1, false);
        ActionId jId = new ActionId(2, new ActionType("JobNameFoo"));
        ActionInitEvent e = new ActionInitEvent(jId, 100);
        job.handleEvent(e);
        Assert.assertEquals(INIT, job.getState());
        Assert.assertEquals(100, job.getStartTime());
        Assert.assertEquals((-1), job.getLastUpdateTime());
        Assert.assertEquals((-1), job.getCompletionTime());
        Assert.assertEquals(2, job.getId().actionId);
    }
}

