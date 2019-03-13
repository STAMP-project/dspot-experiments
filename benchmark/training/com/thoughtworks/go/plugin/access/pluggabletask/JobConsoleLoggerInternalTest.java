/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.access.pluggabletask;


import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.util.ReflectionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class JobConsoleLoggerInternalTest {
    @Test
    public void shouldSetAndUnsetContext() {
        final TaskExecutionContext context = Mockito.mock(TaskExecutionContext.class);
        JobConsoleLoggerInternal.setContext(context);
        Assert.assertThat(ReflectionUtil.getStaticField(JobConsoleLogger.class, "context"), Matchers.is(context));
        JobConsoleLoggerInternal.unsetContext();
        Assert.assertThat(ReflectionUtil.getStaticField(JobConsoleLogger.class, "context"), Matchers.is(Matchers.nullValue()));
    }
}

