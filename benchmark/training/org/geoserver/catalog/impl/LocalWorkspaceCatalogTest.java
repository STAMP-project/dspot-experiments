/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.ows.LocalWorkspace;
import org.geotools.feature.NameImpl;
import org.junit.Assert;
import org.junit.Test;


public class LocalWorkspaceCatalogTest {
    LocalWorkspaceCatalog catalog;

    @Test
    public void testGetStyleByName() throws Exception {
        Assert.assertNull(catalog.getStyleByName("s1"));
        Assert.assertNull(catalog.getStyleByName("s2"));
        WorkspaceInfo ws1 = catalog.getWorkspaceByName("ws1");
        WorkspaceInfo ws2 = catalog.getWorkspaceByName("ws2");
        LocalWorkspace.set(ws1);
        Assert.assertNotNull(catalog.getStyleByName("s1"));
        Assert.assertNull(catalog.getStyleByName("s2"));
        LocalWorkspace.remove();
        Assert.assertNull(catalog.getStyleByName("s1"));
        Assert.assertNull(catalog.getStyleByName("s2"));
        LocalWorkspace.set(ws2);
        Assert.assertNull(catalog.getStyleByName("s1"));
        Assert.assertNotNull(catalog.getStyleByName("s2"));
        LocalWorkspace.remove();
        Assert.assertNull(catalog.getStyleByName("s1"));
        Assert.assertNull(catalog.getStyleByName("s2"));
    }

    @Test
    public void testGetLayerGroupByName() throws Exception {
        Assert.assertNull(catalog.getLayerGroupByName("lg1"));
        Assert.assertNull(catalog.getLayerGroupByName("lg2"));
        WorkspaceInfo ws1 = catalog.getWorkspaceByName("ws1");
        WorkspaceInfo ws2 = catalog.getWorkspaceByName("ws2");
        LocalWorkspace.set(ws1);
        Assert.assertNotNull(catalog.getLayerGroupByName("lg1"));
        Assert.assertNotNull(catalog.getLayerGroupByName("ws1:lg1"));
        Assert.assertNull(catalog.getLayerGroupByName("lg2"));
        LocalWorkspace.remove();
        Assert.assertNull(catalog.getLayerGroupByName("lg1"));
        Assert.assertNull(catalog.getLayerGroupByName("lg2"));
        LocalWorkspace.set(ws2);
        Assert.assertNull(catalog.getLayerGroupByName("lg1"));
        Assert.assertNotNull(catalog.getLayerGroupByName("ws2:lg2"));
        Assert.assertNotNull(catalog.getLayerGroupByName("lg2"));
        LocalWorkspace.remove();
        Assert.assertNull(catalog.getLayerGroupByName("lg1"));
        Assert.assertNull(catalog.getLayerGroupByName("lg2"));
    }

    @Test
    public void testGetLayerByName() throws Exception {
        Assert.assertNull(catalog.getLayerByName("l1"));
        Assert.assertNull(catalog.getLayerByName("l2"));
        WorkspaceInfo ws1 = catalog.getWorkspaceByName("ws1");
        WorkspaceInfo ws2 = catalog.getWorkspaceByName("ws2");
        LocalWorkspace.set(ws1);
        Assert.assertNotNull(catalog.getLayerByName("l1"));
        Assert.assertNull(catalog.getLayerByName("l2"));
        LocalWorkspace.remove();
        LocalWorkspace.set(ws2);
        Assert.assertNull(catalog.getLayerByName("l1"));
        Assert.assertNotNull(catalog.getLayerByName("l2"));
        LocalWorkspace.remove();
        Assert.assertNull(catalog.getLayerByName("l1"));
        Assert.assertNull(catalog.getLayerByName("l2"));
    }

    @Test
    public void testGetLayersWithSameName() throws Exception {
        LayerInfo layerInfo1 = catalog.getLayerByName(new NameImpl("ws1", "lc"));
        ResourceInfo resource1 = layerInfo1.getResource();
        NamespaceInfo namespace1 = resource1.getNamespace();
        String nsPrefix1 = namespace1.getPrefix();
        LayerInfo layerInfo2 = catalog.getLayerByName(new NameImpl("ws2", "lc"));
        ResourceInfo resource2 = layerInfo2.getResource();
        NamespaceInfo namespace2 = resource2.getNamespace();
        String nsPrefix2 = namespace2.getPrefix();
        Assert.assertEquals("Invalid namespace prefix", "ws1", nsPrefix1);
        Assert.assertEquals("Invalid namespace prefix", "ws2", nsPrefix2);
    }

    /**
     * The setting says to not include the prefix. This is default behaviour
     */
    @Test
    public void testGetNonPrefixedLayerNames() {
        boolean includePrefix = false;
        boolean setLocalWorkspace = true;
        boolean createGeoServer = true;
        assertPrefixInclusion(includePrefix, setLocalWorkspace, createGeoServer);
    }

    /**
     * No geoserver instance has been set. This means there is no access to geoserver. this should
     * not happen but we want to protect against this consideration. In this case we have a local
     * workspace set and we will use the default behaviour (no prefix)
     */
    @Test
    public void testGetNoGeoserverPrefixedLayerNameBehaviour() {
        boolean includePrefix = false;
        boolean setLocalWorkspace = true;
        boolean createGeoServer = false;
        assertPrefixInclusion(includePrefix, setLocalWorkspace, createGeoServer);
    }

    /**
     * No local workspace is set this means the prefix should be included since the global
     * capabilities is probably being created.
     *
     * <p>The No Geoserver part is just to verify there are no nullpointer exceptions because of a
     * coding error
     */
    @Test
    public void testGetNoGeoserverLocalWorkspacePrefixedLayerNameBehaviour() {
        boolean includePrefix = true;
        boolean setLocalWorkspace = false;
        boolean createGeoServer = false;
        assertPrefixInclusion(includePrefix, setLocalWorkspace, createGeoServer);
    }

    /**
     * No localworkspace so prefix should be included since the global capabilities is probably
     * being created.
     */
    @Test
    public void testGetNoLocalWorkspacePrefixedLayerNameBehaviour() {
        boolean includePrefix = true;
        boolean setLocalWorkspace = false;
        boolean createGeoServer = true;
        assertPrefixInclusion(includePrefix, setLocalWorkspace, createGeoServer);
    }

    /**
     * The setting is set to include the prefixes.
     */
    @Test
    public void testGetPrefixedLayerNames() {
        boolean includePrefix = true;
        boolean setLocalWorkspace = true;
        boolean createGeoServer = true;
        assertPrefixInclusion(includePrefix, setLocalWorkspace, createGeoServer);
    }
}

