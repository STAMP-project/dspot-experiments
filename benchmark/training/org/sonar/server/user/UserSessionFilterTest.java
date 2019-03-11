/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.user;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.core.platform.ComponentContainer;
import org.sonar.db.DBSessions;
import org.sonar.server.authentication.UserSessionInitializer;
import org.sonar.server.organization.DefaultOrganizationCache;
import org.sonar.server.platform.Platform;
import org.sonar.server.setting.ThreadLocalSettings;


public class UserSessionFilterTest {
    private UserSessionInitializer userSessionInitializer = Mockito.mock(UserSessionInitializer.class);

    private ComponentContainer container = new ComponentContainer();

    private Platform platform = Mockito.mock(Platform.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private DBSessions dbSessions = Mockito.mock(DBSessions.class);

    private ThreadLocalSettings settings = Mockito.mock(ThreadLocalSettings.class);

    private DefaultOrganizationCache defaultOrganizationCache = Mockito.mock(DefaultOrganizationCache.class);

    private UserSessionFilter underTest = new UserSessionFilter(platform);

    @Test
    public void cleanup_user_session_after_request_handling() throws IOException, ServletException {
        mockUserSessionInitializer(true);
        underTest.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);
        Mockito.verify(userSessionInitializer).initUserSession(request, response);
    }

    @Test
    public void stop_when_user_session_return_false() throws Exception {
        mockUserSessionInitializer(false);
        underTest.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.never()).doFilter(request, response);
        Mockito.verify(userSessionInitializer).initUserSession(request, response);
    }

    @Test
    public void does_nothing_when_not_initialized() throws Exception {
        underTest.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);
        Mockito.verifyZeroInteractions(userSessionInitializer);
    }

    @Test
    public void doFilter_loads_and_unloads_settings() throws Exception {
        underTest.doFilter(request, response, chain);
        InOrder inOrder = Mockito.inOrder(settings);
        inOrder.verify(settings).load();
        inOrder.verify(settings).unload();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void doFilter_unloads_Settings_even_if_chain_throws_exception() throws Exception {
        RuntimeException thrown = mockChainDoFilterError();
        try {
            underTest.doFilter(request, response, chain);
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(thrown);
            Mockito.verify(settings).unload();
        }
    }

    @Test
    public void doFilter_enables_and_disables_caching_in_DbSessions() throws Exception {
        underTest.doFilter(request, response, chain);
        InOrder inOrder = Mockito.inOrder(dbSessions);
        inOrder.verify(dbSessions).enableCaching();
        inOrder.verify(dbSessions).disableCaching();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void doFilter_disables_caching_in_DbSessions_even_if_chain_throws_exception() throws Exception {
        RuntimeException thrown = mockChainDoFilterError();
        try {
            underTest.doFilter(request, response, chain);
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(thrown);
            Mockito.verify(dbSessions).disableCaching();
        }
    }

    @Test
    public void doFilter_unloads_Settings_even_if_DefaultOrganizationCache_unload_fails() throws Exception {
        RuntimeException thrown = new RuntimeException("Faking DefaultOrganizationCache.unload failing");
        Mockito.doThrow(thrown).when(defaultOrganizationCache).unload();
        try {
            underTest.doFilter(request, response, chain);
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(thrown);
            Mockito.verify(settings).unload();
        }
    }

    @Test
    public void doFilter_unloads_Settings_even_if_UserSessionInitializer_removeUserSession_fails() throws Exception {
        RuntimeException thrown = mockUserSessionInitializerRemoveUserSessionFailing();
        try {
            underTest.doFilter(request, response, chain);
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(thrown);
            Mockito.verify(settings).unload();
        }
    }

    @Test
    public void doFilter_loads_and_unloads_DefaultOrganizationCache() throws Exception {
        underTest.doFilter(request, response, chain);
        InOrder inOrder = Mockito.inOrder(defaultOrganizationCache);
        inOrder.verify(defaultOrganizationCache).load();
        inOrder.verify(defaultOrganizationCache).unload();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void doFilter_unloads_DefaultOrganizationCache_even_if_chain_throws_exception() throws Exception {
        RuntimeException thrown = mockChainDoFilterError();
        try {
            underTest.doFilter(request, response, chain);
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(thrown);
            Mockito.verify(defaultOrganizationCache).unload();
        }
    }

    @Test
    public void doFilter_unloads_DefaultOrganizationCache_even_if_Settings_unload_fails() throws Exception {
        RuntimeException thrown = new RuntimeException("Faking Settings.unload failing");
        Mockito.doThrow(thrown).when(settings).unload();
        try {
            underTest.doFilter(request, response, chain);
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(thrown);
            Mockito.verify(defaultOrganizationCache).unload();
        }
    }

    @Test
    public void doFilter_unloads_DefaultOrganizationCache_even_if_UserSessionInitializer_removeUserSession_fails() throws Exception {
        RuntimeException thrown = mockUserSessionInitializerRemoveUserSessionFailing();
        try {
            underTest.doFilter(request, response, chain);
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(thrown);
            Mockito.verify(defaultOrganizationCache).unload();
        }
    }

    @Test
    public void just_for_fun_and_coverage() throws ServletException {
        UserSessionFilter filter = new UserSessionFilter();
        filter.init(Mockito.mock(FilterConfig.class));
        filter.destroy();
        // do not fail
    }
}

