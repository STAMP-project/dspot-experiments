/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.rest.resources.dashboards;


import java.security.Principal;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.UriBuilder;
import org.apache.shiro.subject.Subject;
import org.graylog2.dashboards.Dashboard;
import org.graylog2.dashboards.DashboardService;
import org.graylog2.plugin.database.users.User;
import org.graylog2.rest.models.dashboards.requests.CreateDashboardRequest;
import org.graylog2.shared.bindings.GuiceInjectorHolder;
import org.graylog2.shared.system.activities.ActivityWriter;
import org.graylog2.shared.users.UserService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class DashboardsResourceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private DashboardService dashboardService;

    @Mock
    private ActivityWriter activityWriter;

    @Mock
    private Subject subject;

    @Mock
    private Principal principal;

    @Mock
    private UserService userService;

    @Mock
    private User user;

    private DashboardsResourceTest.DashboardsTestResource dashboardsResource;

    private static class DashboardsTestResource extends DashboardsResource {
        private final Subject subject;

        DashboardsTestResource(DashboardService dashboardService, ActivityWriter activityWriter, Subject subject, UserService userService) {
            super(dashboardService, activityWriter);
            this.subject = subject;
            this.userService = userService;
        }

        @Override
        protected void checkPermission(String permission) {
        }

        @Override
        protected void checkPermission(String permission, String instanceId) {
        }

        @Override
        protected void checkAnyPermission(String[] permissions, String instanceId) {
        }

        @Override
        protected Subject getSubject() {
            return subject;
        }

        @Override
        protected UriBuilder getUriBuilderToSelf() {
            return UriBuilder.fromUri("http://testserver/api");
        }
    }

    public DashboardsResourceTest() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Test
    public void creatingADashboardAddsRequiredPermissionsForNonAdmin() throws Exception {
        final Dashboard dashboard = Mockito.mock(Dashboard.class);
        Mockito.when(dashboardService.create(ArgumentMatchers.eq("foo"), ArgumentMatchers.eq("bar"), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(dashboard);
        final String dashboardId = "dashboardId";
        Mockito.when(dashboardService.save(dashboard)).thenReturn(dashboardId);
        this.dashboardsResource.create(CreateDashboardRequest.create("foo", "bar"));
        final ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userService, Mockito.times(1)).save(userArgument.capture());
        @SuppressWarnings("unchecked")
        final ArgumentCaptor<List<String>> permissionsArgument = ArgumentCaptor.forClass(List.class);
        Mockito.verify(user, Mockito.times(1)).setPermissions(permissionsArgument.capture());
        final User updatedUser = userArgument.getValue();
        assertThat(updatedUser).isNotNull();
        final List<String> updatedPermissions = permissionsArgument.getValue();
        assertThat(updatedPermissions).containsExactly(("dashboards:read:" + dashboardId), ("dashboards:edit:" + dashboardId));
    }

    @Test
    public void creatingADashboardDoesNotAddPermissionsForAdmin() throws Exception {
        Mockito.when(user.isLocalAdmin()).thenReturn(true);
        final Dashboard dashboard = Mockito.mock(Dashboard.class);
        Mockito.when(dashboardService.create(ArgumentMatchers.eq("foo"), ArgumentMatchers.eq("bar"), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(dashboard);
        final String dashboardId = "dashboardId";
        Mockito.when(dashboardService.save(dashboard)).thenReturn(dashboardId);
        this.dashboardsResource.create(CreateDashboardRequest.create("foo", "bar"));
        Mockito.verify(userService, Mockito.never()).save(user);
    }
}

