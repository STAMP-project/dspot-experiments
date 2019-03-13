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
package org.quartz;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.quartz.impl.jdbcjobstore.JdbcQuartzTestUtilities;
import org.quartz.utils.ConnectionProvider;


@RunWith(Parameterized.class)
public class FlakyJdbcSchedulerTest extends AbstractSchedulerTest {
    private final Random rndm;

    private final float createFailureProb;

    private final float preCommitFailureProb;

    private final float postCommitFailureProb;

    public FlakyJdbcSchedulerTest(float createFailureProb, float preCommitFailureProb, float postCommitFailureProb) {
        this.createFailureProb = createFailureProb;
        this.preCommitFailureProb = preCommitFailureProb;
        this.postCommitFailureProb = postCommitFailureProb;
        this.rndm = new Random();
    }

    @Test
    public void testTriggerFiring() throws Exception {
        final int jobCount = 100;
        final int execCount = 5;
        Scheduler scheduler = createScheduler("testTriggerFiring", 2);
        try {
            for (int i = 0; i < jobCount; i++) {
                String jobName = "myJob" + i;
                JobDetail jobDetail = JobBuilder.newJob(FlakyJdbcSchedulerTest.TestJob.class).withIdentity(jobName, "myJobGroup").usingJobData("data", 0).storeDurably().requestRecovery().build();
                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(("triggerName" + i), "triggerGroup").withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount((execCount - 1))).build();
                if (!(scheduler.checkExists(jobDetail.getKey()))) {
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            }
            scheduler.start();
            for (int i = 0; i < (TimeUnit.MINUTES.toSeconds(5)); i++) {
                int doneCount = 0;
                for (int j = 0; j < jobCount; j++) {
                    JobDetail jobDetail = scheduler.getJobDetail(new JobKey(("myJob" + i), "myJobGroup"));
                    if ((jobDetail.getJobDataMap().getInt("data")) >= execCount) {
                        doneCount++;
                    }
                }
                if (doneCount == jobCount) {
                    return;
                }
                TimeUnit.SECONDS.sleep(1);
            }
            Assert.fail();
        } finally {
            scheduler.shutdown(true);
        }
    }

    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    public static class TestJob implements Job {
        public void execute(JobExecutionContext context) {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            int val = (dataMap.getInt("data")) + 1;
            dataMap.put("data", val);
        }
    }

    private class FlakyConnectionProvider implements ConnectionProvider {
        private final Thread safeThread;

        private final String delegateName;

        private FlakyConnectionProvider(String name) throws SQLException {
            this.delegateName = "delegate_" + name;
            this.safeThread = Thread.currentThread();
            JdbcQuartzTestUtilities.createDatabase(delegateName);
        }

        @Override
        public Connection getConnection() throws SQLException {
            if ((Thread.currentThread()) == (safeThread)) {
                return org.quartz.utils.DBConnectionManager.getInstance().getConnection(delegateName);
            } else {
                createFailure();
                return ((Connection) (Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{ Connection.class }, new FlakyJdbcSchedulerTest.FlakyConnectionInvocationHandler(org.quartz.utils.DBConnectionManager.getInstance().getConnection(delegateName)))));
            }
        }

        @Override
        public void shutdown() throws SQLException {
            org.quartz.utils.DBConnectionManager.getInstance().shutdown(delegateName);
            JdbcQuartzTestUtilities.destroyDatabase(delegateName);
            JdbcQuartzTestUtilities.shutdownDatabase();
        }

        @Override
        public void initialize() throws SQLException {
            // no-op
        }
    }

    private class FlakyConnectionInvocationHandler implements InvocationHandler {
        private final Connection delegate;

        public FlakyConnectionInvocationHandler(Connection delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("commit".equals(method.getName())) {
                preCommitFailure();
                method.invoke(delegate, args);
                postCommitFailure();
                return null;
            } else {
                return method.invoke(delegate, args);
            }
        }
    }
}

