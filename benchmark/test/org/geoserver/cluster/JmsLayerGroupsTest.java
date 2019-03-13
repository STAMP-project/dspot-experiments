/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster;


import MockData.BRIDGES;
import MockData.ROAD_SEGMENTS;
import java.util.List;
import java.util.function.Function;
import javax.jms.Message;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.event.CatalogEvent;
import org.geoserver.cluster.impl.handlers.catalog.CatalogUtils;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests related with layer groups events.
 */
public final class JmsLayerGroupsTest extends GeoServerSystemTestSupport {
    private static final String TEST_LAYER_GROUP_NAME = "test_layer_group";

    private static final String CATALOG_ADD_EVENT_HANDLER_KEY = "JMSCatalogAddEventHandlerSPI";

    private static JMSEventHandler<String, CatalogEvent> addEventHandler;

    @Test
    public void testAddLayerGroup() throws Exception {
        // create the layer group
        createTetLayerGroup();
        // wait for a catalog add event
        List<Message> messages = JmsEventsListener.getMessagesByHandlerKey(5000, ( selected) -> (selected.size()) >= 2, JmsLayerGroupsTest.CATALOG_ADD_EVENT_HANDLER_KEY);
        // remove the test layer group to force a complete deserialization
        removeTestLayerGroup();
        // let's see if we got the correct event
        Assert.assertThat(messages.size(), Is.is(1));
        List<CatalogEvent> layerGroupAddEvent = JmsEventsListener.getMessagesForHandler(messages, JmsLayerGroupsTest.CATALOG_ADD_EVENT_HANDLER_KEY, JmsLayerGroupsTest.addEventHandler);
        Assert.assertThat(layerGroupAddEvent.size(), Is.is(1));
        Assert.assertThat(layerGroupAddEvent.get(0).getSource(), CoreMatchers.instanceOf(LayerGroupInfo.class));
        LayerGroupInfo layerGroup = ((LayerGroupInfo) (layerGroupAddEvent.get(0).getSource()));
        CatalogUtils.localizeLayerGroup(layerGroup, getCatalog());
        // checking the published layer group
        Assert.assertThat(layerGroup.getName(), Is.is(JmsLayerGroupsTest.TEST_LAYER_GROUP_NAME));
        List<PublishedInfo> content = layerGroup.getLayers();
        Assert.assertThat(content.size(), Is.is(2));
        // checking that the layer group contains the correct layers
        for (PublishedInfo item : content) {
            Assert.assertThat(item, CoreMatchers.instanceOf(LayerInfo.class));
            LayerInfo layer = ((LayerInfo) (item));
            Assert.assertThat(layer.getName(), CoreMatchers.anyOf(Is.is(ROAD_SEGMENTS.getLocalPart()), Is.is(BRIDGES.getLocalPart())));
            FeatureTypeInfo resource = ((FeatureTypeInfo) (layer.getResource()));
            // check that the transient catalog variable has initiated properly
            Assert.assertThat(getCatalog(), CoreMatchers.notNullValue());
        }
    }
}

