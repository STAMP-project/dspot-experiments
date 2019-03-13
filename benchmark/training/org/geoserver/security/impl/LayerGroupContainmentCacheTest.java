/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import Mode.NAMED;
import Mode.OPAQUE_CONTAINER;
import Mode.SINGLE;
import java.util.Collection;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.security.impl.LayerGroupContainmentCache.LayerGroupSummary;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link LayerGroupContainmentCache} udpates in face of catalog setup and changes
 */
public class LayerGroupContainmentCacheTest {
    private static final String WS = "ws";

    private static final String ANOTHER_WS = "anotherWs";

    private static final String NATURE_GROUP = "nature";

    private static final String CONTAINER_GROUP = "containerGroup";

    private LayerGroupContainmentCache cc;

    private LayerGroupInfo nature;

    private LayerGroupInfo container;

    private static Catalog catalog;

    @Test
    public void testInitialSetup() throws Exception {
        // nature
        Collection<LayerGroupSummary> natureContainers = cc.getContainerGroupsFor(nature);
        Assert.assertEquals(1, natureContainers.size());
        Assert.assertThat(natureContainers, contains(new LayerGroupSummary(container)));
        LayerGroupSummary summary = natureContainers.iterator().next();
        Assert.assertNull(summary.getWorkspace());
        Assert.assertEquals(LayerGroupContainmentCacheTest.CONTAINER_GROUP, summary.getName());
        Assert.assertThat(summary.getContainerGroups(), empty());
        // container has no contaning groups
        Assert.assertThat(cc.getContainerGroupsFor(container), empty());
        // now check the groups containing the layers (nature being SINGLE, not a container)
        Assert.assertThat(containerNamesForResource(MockData.LAKES), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.FORESTS), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.ROAD_SEGMENTS), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
    }

    @Test
    public void testAddLayerToNature() throws Exception {
        LayerInfo neatline = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.MAP_NEATLINE));
        nature.getLayers().add(neatline);
        nature.getStyles().add(null);
        LayerGroupContainmentCacheTest.catalog.save(nature);
        Assert.assertThat(containerNamesForResource(MockData.MAP_NEATLINE), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
    }

    @Test
    public void testAddLayerToContainer() throws Exception {
        LayerInfo neatline = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.MAP_NEATLINE));
        container.getLayers().add(neatline);
        container.getStyles().add(null);
        LayerGroupContainmentCacheTest.catalog.save(container);
        Assert.assertThat(containerNamesForResource(MockData.MAP_NEATLINE), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
    }

    @Test
    public void testRemoveLayerFromNature() throws Exception {
        LayerInfo lakes = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.LAKES));
        nature.getLayers().remove(lakes);
        nature.getStyles().remove(0);
        LayerGroupContainmentCacheTest.catalog.save(nature);
        Assert.assertThat(containerNamesForResource(MockData.LAKES), empty());
        Assert.assertThat(containerNamesForResource(MockData.FORESTS), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.ROAD_SEGMENTS), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
    }

    @Test
    public void testRemoveLayerFromContainer() throws Exception {
        LayerInfo roads = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.ROAD_SEGMENTS));
        container.getLayers().remove(roads);
        container.getStyles().remove(0);
        LayerGroupContainmentCacheTest.catalog.save(container);
        Assert.assertThat(containerNamesForResource(MockData.LAKES), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.FORESTS), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.ROAD_SEGMENTS), empty());
    }

    @Test
    public void testRemoveNatureFromContainer() throws Exception {
        container.getLayers().remove(nature);
        container.getStyles().remove(0);
        LayerGroupContainmentCacheTest.catalog.save(container);
        Assert.assertThat(containerNamesForGroup(nature), empty());
        Assert.assertThat(containerNamesForResource(MockData.LAKES), empty());
        Assert.assertThat(containerNamesForResource(MockData.FORESTS), empty());
        Assert.assertThat(containerNamesForResource(MockData.ROAD_SEGMENTS), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
    }

    @Test
    public void testRemoveAllGrups() throws Exception {
        LayerGroupContainmentCacheTest.catalog.remove(container);
        LayerGroupContainmentCacheTest.catalog.remove(nature);
        Assert.assertThat(containerNamesForGroup(nature), empty());
        Assert.assertThat(containerNamesForResource(MockData.LAKES), empty());
        Assert.assertThat(containerNamesForResource(MockData.FORESTS), empty());
        Assert.assertThat(containerNamesForResource(MockData.ROAD_SEGMENTS), empty());
    }

    @Test
    public void testAddRemoveNamed() throws Exception {
        final String NAMED_GROUP = "named";
        LayerInfo neatline = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.MAP_NEATLINE));
        LayerInfo lakes = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.LAKES));
        // add and check containment
        LayerGroupInfo named = addLayerGroup(NAMED_GROUP, NAMED, null, lakes, neatline);
        Assert.assertThat(containerNamesForResource(MockData.LAKES), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP, NAMED_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.MAP_NEATLINE), equalTo(set(NAMED_GROUP)));
        Assert.assertThat(containerNamesForGroup(named), empty());
        // delete and check containment
        LayerGroupContainmentCacheTest.catalog.remove(named);
        Assert.assertThat(containerNamesForResource(MockData.LAKES), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.MAP_NEATLINE), empty());
        Assert.assertThat(containerNamesForGroup(named), empty());
    }

    @Test
    public void testAddRemoveNestedNamed() throws Exception {
        final String NESTED_NAMED = "nestedNamed";
        LayerInfo neatline = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.MAP_NEATLINE));
        LayerInfo lakes = LayerGroupContainmentCacheTest.catalog.getLayerByName(getLayerId(MockData.LAKES));
        // add, nest, and check containment
        LayerGroupInfo nestedNamed = addLayerGroup(NESTED_NAMED, NAMED, null, lakes, neatline);
        container.getLayers().add(nestedNamed);
        container.getStyles().add(null);
        LayerGroupContainmentCacheTest.catalog.save(container);
        Assert.assertThat(containerNamesForResource(MockData.LAKES), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP, NESTED_NAMED)));
        Assert.assertThat(containerNamesForResource(MockData.MAP_NEATLINE), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP, NESTED_NAMED)));
        Assert.assertThat(containerNamesForGroup(nestedNamed), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        // delete and check containment
        new CascadeDeleteVisitor(LayerGroupContainmentCacheTest.catalog).visit(nestedNamed);
        Assert.assertThat(containerNamesForResource(MockData.LAKES), equalTo(set(LayerGroupContainmentCacheTest.CONTAINER_GROUP)));
        Assert.assertThat(containerNamesForResource(MockData.MAP_NEATLINE), empty());
        Assert.assertThat(containerNamesForGroup(nestedNamed), empty());
    }

    @Test
    public void testRenameGroup() throws Exception {
        nature.setName("renamed");
        LayerGroupContainmentCacheTest.catalog.save(nature);
        LayerGroupSummary summary = cc.groupCache.get(nature.getId());
        Assert.assertEquals("renamed", summary.getName());
        Assert.assertEquals(LayerGroupContainmentCacheTest.WS, summary.getWorkspace());
    }

    @Test
    public void testRenameWorkspace() throws Exception {
        WorkspaceInfo ws = LayerGroupContainmentCacheTest.catalog.getDefaultWorkspace();
        ws.setName("renamed");
        try {
            LayerGroupContainmentCacheTest.catalog.save(ws);
            LayerGroupSummary summary = cc.groupCache.get(nature.getId());
            Assert.assertEquals(LayerGroupContainmentCacheTest.NATURE_GROUP, summary.getName());
            Assert.assertEquals("renamed", summary.getWorkspace());
        } finally {
            ws.setName(LayerGroupContainmentCacheTest.WS);
            LayerGroupContainmentCacheTest.catalog.save(ws);
        }
    }

    @Test
    public void testChangeWorkspace() throws Exception {
        DataStoreInfo store = LayerGroupContainmentCacheTest.catalog.getDataStores().get(0);
        try {
            WorkspaceInfo aws = LayerGroupContainmentCacheTest.catalog.getWorkspaceByName(LayerGroupContainmentCacheTest.ANOTHER_WS);
            store.setWorkspace(aws);
            LayerGroupContainmentCacheTest.catalog.save(store);
            nature.setWorkspace(aws);
            LayerGroupContainmentCacheTest.catalog.save(nature);
            LayerGroupSummary summary = cc.groupCache.get(nature.getId());
            Assert.assertEquals(LayerGroupContainmentCacheTest.NATURE_GROUP, summary.getName());
            Assert.assertEquals(LayerGroupContainmentCacheTest.ANOTHER_WS, summary.getWorkspace());
        } finally {
            WorkspaceInfo ws = LayerGroupContainmentCacheTest.catalog.getWorkspaceByName(LayerGroupContainmentCacheTest.WS);
            store.setWorkspace(ws);
            LayerGroupContainmentCacheTest.catalog.save(store);
        }
    }

    @Test
    public void testChangeGroupMode() throws Exception {
        LayerGroupSummary summary = cc.groupCache.get(nature.getId());
        Assert.assertEquals(SINGLE, summary.getMode());
        nature.setMode(OPAQUE_CONTAINER);
        LayerGroupContainmentCacheTest.catalog.save(nature);
        summary = cc.groupCache.get(nature.getId());
        Assert.assertEquals(OPAQUE_CONTAINER, summary.getMode());
    }
}

