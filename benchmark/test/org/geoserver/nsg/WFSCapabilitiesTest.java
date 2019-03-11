/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.nsg;


import java.util.Arrays;
import org.geoserver.wfs.v2_0.WFS20TestSupport;
import org.junit.Test;
import org.w3c.dom.Document;

import static NSGWFSExtendedCapabilitiesProvider.GML32_FORMAT;
import static NSGWFSExtendedCapabilitiesProvider.IMPLEMENTS_ENHANCED_PAGING;
import static NSGWFSExtendedCapabilitiesProvider.IMPLEMENTS_FEATURE_VERSIONING;
import static NSGWFSExtendedCapabilitiesProvider.NSG_BASIC;
import static NSGWFSExtendedCapabilitiesProvider.SRS_OPERATIONS;
import static NSGWFSExtendedCapabilitiesProvider.TIMEOUT_OPERATIONS;


public class WFSCapabilitiesTest extends WFS20TestSupport {
    @Test
    public void testCapabilitiesExtras() throws Exception {
        Document dom = getAsDOM("wfs?service=WFS&version=2.0.0&request=GetCapabilities");
        print(dom);
        // profiles
        assertXpathExists((("//ows:ServiceIdentification[ows:Profile='" + (NSG_BASIC)) + "']"), dom);
        // constraints
        assertXpathExists((("//ows:OperationsMetadata/ows:Constraint[@name='" + (IMPLEMENTS_FEATURE_VERSIONING)) + "' and ows:DefaultValue='TRUE']"), dom);
        assertXpathExists((("//ows:OperationsMetadata/ows:Constraint[@name='" + (IMPLEMENTS_ENHANCED_PAGING)) + "' and ows:DefaultValue='TRUE']"), dom);
        // ensure "version" is there for all operations besides capabilities (10 basic plus paged
        // results)
        assertXpathEvaluatesTo("11", ("count(//ows:OperationsMetadata/ows:Operation[@name!='GetCapabilities']/ows" + ":Parameter[@name='version']/ows:AllowedValues[ows:Value[1]='2.0.0'])"), dom);
        // ensure the srsName parameter is available on GetFeature, GetFeatureWithLock, Transaction
        for (String operation : SRS_OPERATIONS) {
            // the two configured SRSs plus one coming from the data
            for (Integer srsCode : Arrays.asList(4326, 3395, 32615)) {
                String xpath = String.format(("//ows:OperationsMetadata/ows:Operation[@name = " + ("'%s']/ows:Parameter[@name='srsName' " + "and ows:AllowedValues/ows:Value='urn:ogc:def:crs:EPSG::%d']")), operation, srsCode);
                assertXpathExists(xpath, dom);
            }
        }
        // ensure the timeout parameter is configured on expected operations
        for (String operation : TIMEOUT_OPERATIONS) {
            String xpath = String.format(("//ows:OperationsMetadata/ows:Operation[@name = " + "'%s']/ows:Parameter[@name='Timeout' and ows:DefaultValue='300']"), operation);
            assertXpathExists(xpath, dom);
        }
        // check the PageResults operation is there
        assertXpathExists("//ows:OperationsMetadata/ows:Operation[@name = 'PageResults']", dom);
        assertXpathExists(((("//ows:OperationsMetadata/ows:Operation[@name = " + "'PageResults']/ows:Parameter[@name='outputFormat' and ows:DefaultValue='") + (GML32_FORMAT)) + "']"), dom);
    }
}

