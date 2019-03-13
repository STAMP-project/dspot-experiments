/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config.spring.status;


import Status.Level.ERROR;
import Status.Level.OK;
import Status.Level.UNKNOWN;
import org.apache.dubbo.common.status.Status;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;


public class SpringStatusCheckerTest {
    private SpringStatusChecker springStatusChecker;

    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void testWithoutApplicationContext() {
        Status status = springStatusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.is(UNKNOWN));
    }

    @Test
    public void testWithLifeCycleRunning() {
        SpringExtensionFactory.clearContexts();
        SpringStatusCheckerTest.ApplicationLifeCycle applicationLifeCycle = Mockito.mock(SpringStatusCheckerTest.ApplicationLifeCycle.class);
        new org.apache.dubbo.config.spring.ServiceBean<Object>().setApplicationContext(applicationLifeCycle);
        BDDMockito.given(applicationLifeCycle.getConfigLocations()).willReturn(new String[]{ "test1", "test2" });
        BDDMockito.given(isRunning()).willReturn(true);
        Status status = springStatusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.is(OK));
        MatcherAssert.assertThat(status.getMessage(), CoreMatchers.is("test1,test2"));
    }

    @Test
    public void testWithoutLifeCycleRunning() {
        SpringExtensionFactory.clearContexts();
        SpringStatusCheckerTest.ApplicationLifeCycle applicationLifeCycle = Mockito.mock(SpringStatusCheckerTest.ApplicationLifeCycle.class);
        new org.apache.dubbo.config.spring.ServiceBean<Object>().setApplicationContext(applicationLifeCycle);
        BDDMockito.given(isRunning()).willReturn(false);
        Status status = springStatusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.is(ERROR));
    }

    interface ApplicationLifeCycle extends ApplicationContext , Lifecycle {
        String[] getConfigLocations();
    }
}

