/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;


/**
 * Tests for {@link WFSWorkspaceQualifier}.
 *
 * @author awaterme
 */
public class WFSWorkspaceQualifierTest {
    private static final String WORKSPACE_URI = "http://workspace/namespace";

    private Catalog mockCatalog = createMock(Catalog.class);

    private WorkspaceInfo mockWorkspaceInfo = createMock(WorkspaceInfo.class);

    private NamespaceInfo mockNamespaceInfo = createMock(NamespaceInfo.class);

    private FeatureType mockFeatureType = createMock(FeatureType.class);

    private Feature mockFeature = createMock(Feature.class);

    private Name mockName = createMock(Name.class);

    private WFSWorkspaceQualifier sut = new WFSWorkspaceQualifier(mockCatalog);

    /**
     * Test for {@link WFSWorkspaceQualifier#qualifyRequest(WorkspaceInfo,
     * org.geoserver.catalog.LayerInfo, Operation, org.geoserver.ows.Request)} .Simulates a WFS-T
     * Replace, having one Feature. The namespaceURI of the workspace and the feature match. Result:
     * No exception.
     */
    @Test
    public void testQualifyRequestWithReplaceNamespaceValidationHavingMatchingNamespaces() {
        expect(mockName.getNamespaceURI()).andReturn(WFSWorkspaceQualifierTest.WORKSPACE_URI).anyTimes();
        invokeQualifyRequest();
    }

    /**
     * Test for {@link WFSWorkspaceQualifier#qualifyRequest(WorkspaceInfo,
     * org.geoserver.catalog.LayerInfo, Operation, org.geoserver.ows.Request)} . Simulates a WFS-T
     * Replace, having one Feature. The namespaceURI of the workspace and the feature do not match.
     * Result: Exception.
     */
    @Test(expected = WFSException.class)
    public void testQualifyRequestWithReplaceNamespaceValidationHavingNonMatchingNamespaces() {
        expect(mockName.getNamespaceURI()).andReturn("http://foo").anyTimes();
        invokeQualifyRequest();
    }
}

