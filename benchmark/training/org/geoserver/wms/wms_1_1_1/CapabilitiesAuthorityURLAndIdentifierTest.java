/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import MockData.PRIMITIVEGEOFEATURE;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;


public class CapabilitiesAuthorityURLAndIdentifierTest extends WMSTestSupport {
    @Test
    public void testRootLayer() throws Exception {
        WMSInfo serviceInfo = getWMS().getServiceInfo();
        addAuthUrl("rootAuth1", "http://geoserver/wms/auth1", serviceInfo.getAuthorityURLs());
        addAuthUrl("rootAuth2", "http://geoserver/wms/auth2", serviceInfo.getAuthorityURLs());
        addIdentifier("rootAuth1", "rootId1", serviceInfo.getIdentifiers());
        addIdentifier("rootAuth2", "rootId2", serviceInfo.getIdentifiers());
        getGeoServer().save(serviceInfo);
        Document doc = getAsDOM("/wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/AuthorityURL[@name = 'rootAuth1']", doc);
        assertXpathEvaluatesTo("http://geoserver/wms/auth1", "/WMT_MS_Capabilities/Capability/Layer/AuthorityURL[@name = 'rootAuth1']/OnlineResource/@xlink:href", doc);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/AuthorityURL[@name = 'rootAuth2']", doc);
        assertXpathEvaluatesTo("http://geoserver/wms/auth2", "/WMT_MS_Capabilities/Capability/Layer/AuthorityURL[@name = 'rootAuth2']/OnlineResource/@xlink:href", doc);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Identifier[@authority = 'rootAuth1']", doc);
        assertXpathEvaluatesTo("rootId1", "/WMT_MS_Capabilities/Capability/Layer/Identifier[@authority = 'rootAuth1']", doc);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Identifier[@authority = 'rootAuth2']", doc);
        assertXpathEvaluatesTo("rootId2", "/WMT_MS_Capabilities/Capability/Layer/Identifier[@authority = 'rootAuth2']", doc);
    }

    @Test
    public void testLayer() throws Exception {
        String layerId = getLayerId(PRIMITIVEGEOFEATURE);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        addAuthUrl("layerAuth1", "http://geoserver/wms/auth1", layer.getAuthorityURLs());
        addIdentifier("layerAuth1", "layerId1", layer.getIdentifiers());
        getCatalog().save(layer);
        String layerName = PRIMITIVEGEOFEATURE.getLocalPart();
        Document doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.1.0", true);
        assertXpathExists((("//Layer[Name='" + layerName) + "']/AuthorityURL[@name = 'layerAuth1']"), doc);
        assertXpathEvaluatesTo("http://geoserver/wms/auth1", (("//Layer[Name='" + layerName) + "']/AuthorityURL[@name = 'layerAuth1']/OnlineResource/@xlink:href"), doc);
        assertXpathExists((("//Layer[Name='" + layerName) + "']/Identifier[@authority = 'layerAuth1']"), doc);
        assertXpathEvaluatesTo("layerId1", (("//Layer[Name='" + layerName) + "']/Identifier[@authority = 'layerAuth1']"), doc);
    }
}

