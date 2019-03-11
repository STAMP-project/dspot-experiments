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
package org.graylog2.dashboards;


import com.google.common.collect.ImmutableSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.List;
import org.graylog2.dashboards.widgets.DashboardWidgetCreator;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.database.NotFoundException;
import org.graylog2.plugin.Tools;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class DashboardServiceImplTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private DashboardService dashboardService;

    @Mock
    private DashboardWidgetCreator dashboardWidgetCreator;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testCreate() {
        final String title = "Dashboard Title";
        final String description = "This is the dashboard description";
        final String creatorUserId = "foobar";
        final DateTime createdAt = Tools.nowUTC();
        final Dashboard dashboard = dashboardService.create(title, description, creatorUserId, createdAt);
        Assert.assertNotNull(dashboard);
        Assert.assertEquals(title, dashboard.getTitle());
        Assert.assertEquals(description, dashboard.getDescription());
        Assert.assertNotNull(dashboard.getId());
        Assert.assertEquals(0, dashboardService.count());
    }

    @Test(expected = NotFoundException.class)
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testLoadNonExistentDashboard() throws NotFoundException {
        this.dashboardService.load("54e3deadbeefdeadbeefaffe");
    }

    @Test
    @UsingDataSet(locations = "singleDashboard.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testLoad() throws NotFoundException {
        final String exampleDashboardId = "54e3deadbeefdeadbeefaffe";
        final Dashboard dashboard = dashboardService.load(exampleDashboardId);
        Assert.assertNotNull("Dashboard should have been found", dashboard);
        Assert.assertEquals("Dashboard id should be the one that was retrieved", exampleDashboardId, dashboard.getId());
    }

    @Test
    @UsingDataSet(locations = "singleDashboard.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testAll() {
        final List<Dashboard> dashboards = dashboardService.all();
        final Dashboard dashboard = dashboards.get(0);
        Assert.assertEquals("Should have returned exactly 1 document", 1, dashboards.size());
        Assert.assertEquals("Example dashboard", dashboard.getTitle());
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testLoadByIds() {
        assertThat(dashboardService.loadByIds(ImmutableSet.of())).isEmpty();
        assertThat(dashboardService.loadByIds(ImmutableSet.of("54e300000000000000000000"))).isEmpty();
        assertThat(dashboardService.loadByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001"))).hasSize(1);
        assertThat(dashboardService.loadByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001", "54e3deadbeefdeadbeef0001"))).hasSize(1);
        assertThat(dashboardService.loadByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001", "54e3deadbeefdeadbeef0002", "54e300000000000000000000"))).hasSize(2);
    }

    @Test
    @UsingDataSet(locations = "singleDashboard.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testCountSingleDashboard() throws Exception {
        Assert.assertEquals(1, this.dashboardService.count());
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testCountEmptyCollection() throws Exception {
        Assert.assertEquals(0, this.dashboardService.count());
    }
}

