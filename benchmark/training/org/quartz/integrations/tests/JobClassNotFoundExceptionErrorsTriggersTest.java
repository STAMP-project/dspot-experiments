/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
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
package org.quartz.integrations.tests;


import Trigger.TriggerState.ERROR;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.simpl.CascadingClassLoadHelper;


public class JobClassNotFoundExceptionErrorsTriggersTest extends QuartzDatabaseTestSupport {
    private static final String BARRIER_KEY = "BARRIER";

    public static class BadJob implements Job {
        public void execute(JobExecutionContext context) {
            // no-op
        }
    }

    public static class GoodJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                ((CyclicBarrier) (context.getScheduler().getContext().get(JobClassNotFoundExceptionErrorsTriggersTest.BARRIER_KEY))).await(20, TimeUnit.SECONDS);
            } catch (SchedulerException ex) {
                throw new JobExecutionException(ex);
            } catch (InterruptedException ex) {
                throw new JobExecutionException(ex);
            } catch (BrokenBarrierException ex) {
                throw new JobExecutionException(ex);
            } catch (TimeoutException ex) {
                throw new JobExecutionException(ex);
            }
        }
    }

    public static class SpecialClassLoadHelper extends CascadingClassLoadHelper {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (JobClassNotFoundExceptionErrorsTriggersTest.BadJob.class.getName().equals(name)) {
                throw new ClassNotFoundException();
            } else {
                return super.loadClass(name);
            }
        }
    }

    @Test
    public void testJobClassNotFoundDoesntBlock() throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(2);
        scheduler.getContext().put(JobClassNotFoundExceptionErrorsTriggersTest.BARRIER_KEY, barrier);
        JobDetail goodJob = JobBuilder.newJob(JobClassNotFoundExceptionErrorsTriggersTest.GoodJob.class).withIdentity("good").build();
        JobDetail badJob = JobBuilder.newJob(JobClassNotFoundExceptionErrorsTriggersTest.BadJob.class).withIdentity("bad").build();
        long now = System.currentTimeMillis();
        Trigger goodTrigger = TriggerBuilder.newTrigger().withIdentity("good").forJob(goodJob).startAt(new Date((now + 1))).build();
        Trigger badTrigger = TriggerBuilder.newTrigger().withIdentity("bad").forJob(badJob).startAt(new Date(now)).build();
        Map<JobDetail, Set<? extends Trigger>> toSchedule = new HashMap<JobDetail, Set<? extends Trigger>>();
        toSchedule.put(badJob, Collections.singleton(badTrigger));
        toSchedule.put(goodJob, Collections.singleton(goodTrigger));
        scheduler.scheduleJobs(toSchedule, true);
        barrier.await(20, TimeUnit.SECONDS);
        Assert.assertThat(scheduler.getTriggerState(badTrigger.getKey()), Is.is(ERROR));
    }
}

