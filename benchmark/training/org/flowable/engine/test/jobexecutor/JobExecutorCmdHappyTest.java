/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.test.jobexecutor;


import java.util.Date;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.api.Job;
import org.flowable.job.service.impl.asyncexecutor.AcquiredTimerJobEntities;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Tom Baeyens
 */
public class JobExecutorCmdHappyTest extends JobExecutorTestCase {
    @Test
    public void testJobCommandsWithMessage() {
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        String jobId = commandExecutor.execute(new org.flowable.common.engine.impl.interceptor.Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                JobEntity message = createTweetMessage("i'm coding a test");
                CommandContextUtil.getJobService(commandContext).scheduleAsyncJob(message);
                return message.getId();
            }
        });
        Job job = managementService.createJobQuery().singleResult();
        assertNotNull(job);
        assertEquals(jobId, job.getId());
        assertEquals(0, tweetHandler.getMessages().size());
        managementService.executeJob(job.getId());
        assertEquals("i'm coding a test", tweetHandler.getMessages().get(0));
        assertEquals(1, tweetHandler.getMessages().size());
    }

    static final long SOME_TIME = 928374923546L;

    static final long SECOND = 1000;

    @Test
    public void testJobCommandsWithTimer() {
        // clock gets automatically reset in LogTestCase.runTest
        processEngineConfiguration.getClock().setCurrentTime(new Date(JobExecutorCmdHappyTest.SOME_TIME));
        AsyncExecutor asyncExecutor = processEngineConfiguration.getAsyncExecutor();
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        String jobId = commandExecutor.execute(new org.flowable.common.engine.impl.interceptor.Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                TimerJobEntity timer = createTweetTimer("i'm coding a test", new Date(((JobExecutorCmdHappyTest.SOME_TIME) + (10 * (JobExecutorCmdHappyTest.SECOND)))));
                CommandContextUtil.getTimerJobService(commandContext).scheduleTimerJob(timer);
                return timer.getId();
            }
        });
        AcquiredTimerJobEntities acquiredJobs = commandExecutor.execute(new org.flowable.job.service.impl.cmd.AcquireTimerJobsCmd(asyncExecutor));
        assertEquals(0, acquiredJobs.size());
        processEngineConfiguration.getClock().setCurrentTime(new Date(((JobExecutorCmdHappyTest.SOME_TIME) + (20 * (JobExecutorCmdHappyTest.SECOND)))));
        acquiredJobs = commandExecutor.execute(new org.flowable.job.service.impl.cmd.AcquireTimerJobsCmd(asyncExecutor));
        assertEquals(1, acquiredJobs.size());
        TimerJobEntity job = acquiredJobs.getJobs().iterator().next();
        assertEquals(jobId, job.getId());
        assertEquals(0, tweetHandler.getMessages().size());
        Job executableJob = managementService.moveTimerToExecutableJob(jobId);
        commandExecutor.execute(new org.flowable.job.service.impl.cmd.ExecuteAsyncJobCmd(executableJob.getId()));
        assertEquals("i'm coding a test", tweetHandler.getMessages().get(0));
        assertEquals(1, tweetHandler.getMessages().size());
    }
}

