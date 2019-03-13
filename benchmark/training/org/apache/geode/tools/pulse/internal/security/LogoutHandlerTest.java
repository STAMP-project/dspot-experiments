/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.tools.pulse.internal.security;


import org.apache.geode.test.junit.categories.LoggingTest;
import org.apache.geode.test.junit.categories.PulseTest;
import org.apache.geode.test.junit.categories.SecurityTest;
import org.apache.geode.test.junit.runners.CategoryWithParameterizedRunnerFactory;
import org.apache.geode.tools.pulse.internal.data.Repository;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Repository.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PowerMockIgnore({ "javax.management.*", "javax.security.*", "*.UnitTest" })
@Parameterized.UseParametersRunnerFactory(CategoryWithParameterizedRunnerFactory.class)
@Category({ PulseTest.class, SecurityTest.class, LoggingTest.class })
public class LogoutHandlerTest {
    private static final String mockUser = "admin";

    private Repository repository;

    private LogoutHandler handler;

    @Parameterized.Parameter
    public static Authentication authentication;

    @Test
    public void testNullAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.onLogoutSuccess(request, response, null);
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeader("Location")).isEqualTo("/defaultTargetUrl");
    }

    @Test
    public void testNotNullAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.onLogoutSuccess(request, response, LogoutHandlerTest.authentication);
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeader("Location")).isEqualTo("/defaultTargetUrl");
        Mockito.verify(repository, Mockito.times(1)).logoutUser(LogoutHandlerTest.mockUser);
    }
}

