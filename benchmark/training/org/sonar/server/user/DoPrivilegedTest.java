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


import org.junit.Test;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.tester.MockUserSession;


public class DoPrivilegedTest {
    private static final String LOGIN = "dalailaHidou!";

    private ThreadLocalUserSession threadLocalUserSession = new ThreadLocalUserSession();

    private MockUserSession session = new MockUserSession(DoPrivilegedTest.LOGIN);

    @Test
    public void allow_everything_in_privileged_block_only() {
        DoPrivilegedTest.UserSessionCatcherTask catcher = new DoPrivilegedTest.UserSessionCatcherTask();
        DoPrivileged.execute(catcher);
        // verify the session used inside Privileged task
        assertThat(catcher.userSession.isLoggedIn()).isFalse();
        assertThat(catcher.userSession.hasComponentPermission("any permission", new ComponentDto())).isTrue();
        assertThat(catcher.userSession.isSystemAdministrator()).isTrue();
        assertThat(catcher.userSession.hasMembership(OrganizationTesting.newOrganizationDto())).isTrue();
        // verify session in place after task is done
        assertThat(threadLocalUserSession.get()).isSameAs(session);
    }

    @Test
    public void loose_privileges_on_exception() {
        DoPrivilegedTest.UserSessionCatcherTask catcher = new DoPrivilegedTest.UserSessionCatcherTask() {
            @Override
            protected void doPrivileged() {
                super.doPrivileged();
                throw new RuntimeException("Test to lose privileges");
            }
        };
        try {
            DoPrivileged.execute(catcher);
            fail("An exception should have been raised!");
        } catch (Throwable ignored) {
            // verify session in place after task is done
            assertThat(threadLocalUserSession.get()).isSameAs(session);
            // verify the session used inside Privileged task
            assertThat(catcher.userSession.isLoggedIn()).isFalse();
            assertThat(catcher.userSession.hasComponentPermission("any permission", new ComponentDto())).isTrue();
            assertThat(catcher.userSession.hasMembership(OrganizationTesting.newOrganizationDto())).isTrue();
        }
    }

    private class UserSessionCatcherTask extends DoPrivileged.Task {
        UserSession userSession;

        public UserSessionCatcherTask() {
            super(DoPrivilegedTest.this.threadLocalUserSession);
        }

        @Override
        protected void doPrivileged() {
            userSession = threadLocalUserSession.get();
        }
    }
}

