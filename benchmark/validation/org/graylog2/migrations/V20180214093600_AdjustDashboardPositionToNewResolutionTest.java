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
package org.graylog2.migrations;


import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.graylog2.dashboards.Dashboard;
import org.graylog2.dashboards.DashboardService;
import org.graylog2.dashboards.widgets.WidgetPosition;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class V20180214093600_AdjustDashboardPositionToNewResolutionTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private V20180214093600_AdjustDashboardPositionToNewResolution adjustDashboardResolutionMigration;

    @Mock
    private ClusterConfigService clusterConfigService;

    @Mock
    private DashboardService dashboardService;

    @Test
    public void doNotMigrateAnythingWithoutDashboards() throws Exception {
        Mockito.when(this.dashboardService.all()).thenReturn(Collections.emptyList());
        this.adjustDashboardResolutionMigration.upgrade();
        Mockito.verify(this.dashboardService, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void doNotMigrateAnythingWithDashboardsWithoutPositions() throws Exception {
        final Dashboard dashboard = Mockito.mock(Dashboard.class);
        Mockito.when(dashboard.getId()).thenReturn("uuu-iii-ddd");
        Mockito.when(dashboard.getPositions()).thenReturn(Collections.emptyList());
        Mockito.when(this.dashboardService.all()).thenReturn(ImmutableList.of(dashboard));
        this.adjustDashboardResolutionMigration.upgrade();
        Mockito.verify(this.dashboardService, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void doMigrateOneDashboardsPositions() throws Exception {
        final List<WidgetPosition> oldPositions = new ArrayList<>(1);
        oldPositions.add(WidgetPosition.builder().id("my-position-id").width(5).height(4).col(2).row(2).build());
        final List<WidgetPosition> newPositions = new ArrayList<>(1);
        newPositions.add(WidgetPosition.builder().id("my-position-id").width(10).height(8).col(3).row(3).build());
        final Dashboard dashboard = Mockito.mock(Dashboard.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(dashboard.getPositions()).thenReturn(oldPositions);
        Mockito.when(dashboard.getId()).thenReturn("uuu-iii-ddd");
        Mockito.when(this.dashboardService.all()).thenReturn(ImmutableList.of(dashboard));
        this.adjustDashboardResolutionMigration.upgrade();
        Mockito.verify(dashboard).setPositions(newPositions);
        Mockito.verify(this.dashboardService, Mockito.times(1)).save(dashboard);
    }
}

