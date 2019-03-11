/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.TestTools;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit testing for {@link TimedCallable}.
 */
@Category({ SlowTest.class })
public class TestTimedCallable extends DrillTest {
    private static final Logger logger = LoggerFactory.getLogger(TestTimedCallable.class);

    @Rule
    public final TestRule TIMEOUT = TestTools.getTimeoutRule(180000);// 3mins


    private static class TestTask extends TimedCallable {
        final long sleepTime;// sleep time in ms


        public TestTask(final long sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Override
        protected Void runInner() throws Exception {
            Thread.sleep(sleepTime);
            return null;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("sleepTime", sleepTime).toString();
        }
    }

    @Test
    public void withoutAnyTasksTriggeringTimeout() throws Exception {
        int count = 100;
        List<TimedCallable<TestTimedCallable.TestTask>> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tasks.add(new TestTimedCallable.TestTask(2000));
        }
        TimedCallable.run("Execution without triggering timeout", TestTimedCallable.logger, tasks, 16);
    }

    @Test
    public void withTasksExceedingTimeout() throws Exception {
        try {
            int count = 100;
            List<TimedCallable<TestTimedCallable.TestTask>> tasks = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                if ((i & (i + 1)) == 0) {
                    tasks.add(new TestTimedCallable.TestTask(2000));
                } else {
                    tasks.add(new TestTimedCallable.TestTask(20000));
                }
            }
            TimedCallable.run("Execution with some tasks triggering timeout", TestTimedCallable.logger, tasks, 16);
            Assert.fail("Expected a UserException");
        } catch (UserException e) {
            Assert.assertThat(e.getMessage(), StringContains.containsString(("Waited for 105000 ms, but only 87 tasks for 'Execution with some tasks triggering timeout' are " + "complete. Total number of tasks 100, parallelism 16.")));
        }
    }

    @Test
    public void withManyTasks() throws Exception {
        int count = 150000;
        List<TimedCallable<TestTimedCallable.TestTask>> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tasks.add(new TestTimedCallable.TestTask(0));
        }
        TimedCallable.run("Execution with lots of tasks", TestTimedCallable.logger, tasks, 16);
    }
}

