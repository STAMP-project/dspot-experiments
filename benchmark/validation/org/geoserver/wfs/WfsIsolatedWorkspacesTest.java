/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import java.util.HashMap;
import java.util.Map;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.IsolatedWorkspacesTest;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Contains tests related to isolated workspaces, this tests exercise WFS operations. An workspace
 * in GeoServer is composed of the workspace information and a namespace which has a special
 * relevance in WFS.
 */
public final class WfsIsolatedWorkspacesTest extends IsolatedWorkspacesTest {
    // WFS 1.1.0 namespaces
    private static final Map<String, String> NAMESPACES_WFS11 = new HashMap<>();

    // init WFS 1.1.0 namespaces
    static {
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("wfs", "http://www.opengis.net/wfs");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("ows", "http://www.opengis.net/ows");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("ogc", "http://www.opengis.net/ogc");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("xs", "http://www.w3.org/2001/XMLSchema");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("xsd", "http://www.w3.org/2001/XMLSchema");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("gml", "http://www.opengis.net/gml");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("xlink", "http://www.w3.org/1999/xlink");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS11.put("gs", "http://geoserver.org");
    }

    // WFS 2.0 namespaces
    private static final Map<String, String> NAMESPACES_WFS20 = new HashMap<>();

    // init WFS 2.0 namespaces
    static {
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("wfs", "http://www.opengis.net/wfs/2.0");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("ows", "http://www.opengis.net/ows/1.1");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("fes", "http://www.opengis.net/fes/2.0");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("gml", "http://www.opengis.net/gml/3.2");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("ogc", "http://www.opengis.net/ogc");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("xs", "http://www.w3.org/2001/XMLSchema");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("xsd", "http://www.w3.org/2001/XMLSchema");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("xlink", "http://www.w3.org/1999/xlink");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        WfsIsolatedWorkspacesTest.NAMESPACES_WFS20.put("gs", "http://geoserver.org");
    }

    @Test
    public void getFeatureInfoOnLayerFromIsolatedWorkspaces() throws Exception {
        Catalog catalog = getCatalog();
        // adding two workspaces with the same URI, one of them is isolated
        createWorkspace("test_a1", "https://www.test_a.com", false);
        createWorkspace("test_a2", "https://www.test_a.com", true);
        // get created workspaces and associated namespaces
        WorkspaceInfo workspace1 = catalog.getWorkspaceByName("test_a1");
        NamespaceInfo namespace1 = catalog.getNamespaceByPrefix("test_a1");
        WorkspaceInfo workspace2 = catalog.getWorkspaceByName("test_a2");
        NamespaceInfo namespace2 = catalog.getNamespaceByPrefix("test_a2");
        // add a layer with the same name to both workspaces, layers have different content
        LayerInfo clonedLayer1 = cloneVectorLayerIntoWorkspace(workspace1, namespace1, "Lines", "layer_e");
        LayerInfo clonedLayer2 = cloneVectorLayerIntoWorkspace(workspace2, namespace2, "Points", "layer_e");
        Assert.assertThat(clonedLayer1.getId(), Matchers.not(clonedLayer2.getId()));
        // test get feature requests for WFS 1.1.0
        MockHttpServletResponse response = getAsServletResponse("test_a1/wfs?SERVICE=wfs&VERSION=1.1.0&REQUEST=getFeature&typeName=layer_e&maxFeatures=1");
        evaluateAndCheckXpath(mergeNamespaces(WfsIsolatedWorkspacesTest.NAMESPACES_WFS11, "test_a1", "https://www.test_a.com"), response, "count(//wfs:FeatureCollection/gml:featureMembers/test_a1:layer_e/test_a1:lineStringProperty)", "1");
        response = getAsServletResponse("test_a2/wfs?SERVICE=wfs&VERSION=1.1.0&REQUEST=getFeature&typeName=layer_e&maxFeatures=1");
        evaluateAndCheckXpath(mergeNamespaces(WfsIsolatedWorkspacesTest.NAMESPACES_WFS11, "test_a2", "https://www.test_a.com"), response, "count(//wfs:FeatureCollection/gml:featureMembers/test_a2:layer_e/test_a2:pointProperty)", "1");
    }
}

