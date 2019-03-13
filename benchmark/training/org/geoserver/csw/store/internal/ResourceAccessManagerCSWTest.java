/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.store.internal;


import MockData.CITE_URI;
import java.util.List;
import java.util.logging.Logger;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.csw.CSWTestSupport;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Performs integration tests using a mock {@link ResourceAccessManager}
 *
 * @author Andrea Aime - GeoSolutions
 */
public class ResourceAccessManagerCSWTest extends CSWTestSupport {
    static final Logger LOGGER = Logging.getLogger(ResourceAccessManagerCSWTest.class);

    XpathEngine xpath;

    @Test
    public void testAllRecordsCite() throws Exception {
        authenticate("cite", "cite");
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=csw:Record&resultType=results&maxRecords=100";
        Document d = getAsDOM(request);
        // print(d);
        // expected number
        List<ResourceInfo> citeResources = getCatalog().getResourcesByNamespace(CITE_URI, ResourceInfo.class);
        Assert.assertEquals(citeResources.size(), xpath.getMatchingNodes("//csw:SummaryRecord", d).getLength());
        // check they indeed all start by cite:
        for (ResourceInfo ri : citeResources) {
            Assert.assertEquals(1, xpath.getMatchingNodes(String.format("//csw:SummaryRecord[dc:identifier='%s']", ri.prefixedName()), d).getLength());
        }
    }

    @Test
    public void testAllRecordsCiteChallenge() throws Exception {
        // grab before auth to get the full list
        List<ResourceInfo> citeResources = getCatalog().getResources(ResourceInfo.class);
        // authenticate
        authenticate("citeChallenge", "citeChallenge");
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=csw:Record&resultType=results&maxRecords=100";
        Document d = getAsDOM(request);
        // print(d);
        // expected number
        Assert.assertEquals(citeResources.size(), xpath.getMatchingNodes("//csw:SummaryRecord", d).getLength());
        // check they indeed all start by cite:
        for (ResourceInfo ri : citeResources) {
            Assert.assertEquals(1, xpath.getMatchingNodes(String.format("//csw:SummaryRecord[dc:identifier='%s']", ri.prefixedName()), d).getLength());
        }
    }
}

