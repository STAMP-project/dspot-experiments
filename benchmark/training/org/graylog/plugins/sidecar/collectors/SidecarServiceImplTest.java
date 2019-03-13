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
package org.graylog.plugins.sidecar.collectors;


import com.lordofthejars.nosqlunit.annotation.CustomComparisonStrategy;
import com.lordofthejars.nosqlunit.annotation.IgnorePropertyValue;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoFlexibleComparisonStrategy;
import java.util.List;
import org.graylog.plugins.sidecar.database.MongoConnectionRule;
import org.graylog.plugins.sidecar.rest.models.NodeDetails;
import org.graylog.plugins.sidecar.rest.models.Sidecar;
import org.graylog.plugins.sidecar.services.CollectorService;
import org.graylog.plugins.sidecar.services.ConfigurationService;
import org.graylog.plugins.sidecar.services.SidecarService;
import org.graylog2.shared.bindings.ObjectMapperModule;
import org.graylog2.shared.bindings.ValidatorModule;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JukitoRunner.class)
@UseModules({ ObjectMapperModule.class, ValidatorModule.class })
@CustomComparisonStrategy(comparisonStrategy = MongoFlexibleComparisonStrategy.class)
public class SidecarServiceImplTest {
    @Mock
    private CollectorService collectorService;

    @Mock
    private ConfigurationService configurationService;

    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    private SidecarService sidecarService;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testCountEmptyCollection() throws Exception {
        final long result = this.sidecarService.count();
        Assert.assertEquals(0, result);
    }

    @Test
    @UsingDataSet(locations = "collectorsMultipleDocuments.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testCountNonEmptyCollection() throws Exception {
        final long result = this.sidecarService.count();
        Assert.assertEquals(3, result);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    @ShouldMatchDataSet(location = "collectorsSingleDataset.json")
    @IgnorePropertyValue(properties = { "_id", "last_seen" })
    public void testSaveFirstRecord() throws Exception {
        final Sidecar sidecar = Sidecar.create("nodeId", "nodeName", NodeDetails.create("DummyOS 1.0", null, null, null, null), "0.0.1");
        final Sidecar result = this.sidecarService.save(sidecar);
        Assert.assertNotNull(result);
    }

    @Test
    @UsingDataSet(locations = "collectorsMultipleDocuments.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testAll() throws Exception {
        final List<Sidecar> sidecars = this.sidecarService.all();
        Assert.assertNotNull(sidecars);
        Assert.assertEquals(3, sidecars.size());
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testAllEmptyCollection() throws Exception {
        final List<Sidecar> sidecars = this.sidecarService.all();
        Assert.assertNotNull(sidecars);
        Assert.assertEquals(0, sidecars.size());
    }

    @Test
    @UsingDataSet(locations = "collectorsMultipleDocuments.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindById() throws Exception {
        final String collector1id = "uniqueid1";
        final Sidecar sidecar = this.sidecarService.findByNodeId(collector1id);
        Assert.assertNotNull(sidecar);
        Assert.assertEquals(collector1id, sidecar.nodeId());
    }

    @Test
    @UsingDataSet(locations = "collectorsMultipleDocuments.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindByIdNonexisting() throws Exception {
        final String collector1id = "nonexisting";
        final Sidecar sidecar = this.sidecarService.findByNodeId(collector1id);
        Assert.assertNull(sidecar);
    }

    @Test
    @UsingDataSet(locations = "collectorsMultipleDocuments.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @ShouldMatchDataSet
    public void testDestroy() throws Exception {
        final Sidecar sidecar = Mockito.mock(Sidecar.class);
        Mockito.when(sidecar.id()).thenReturn("581b3bff8e4dc4270055dfcb");
        final int result = this.sidecarService.delete(sidecar.id());
        Assert.assertEquals(1, result);
    }
}

