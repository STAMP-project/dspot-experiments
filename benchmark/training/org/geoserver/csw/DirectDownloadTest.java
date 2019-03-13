/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw;


import DirectDownload.LIMIT_MESSAGE;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.csw.DownloadLinkHandler.CloseableLinksIterator;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.data.CloseableIterator;
import org.geotools.data.FileGroupProvider.FileGroup;
import org.geotools.data.FileResourceInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static DownloadLinkHandler.LINK;


public class DirectDownloadTest extends GeoServerSystemTestSupport {
    private static Map<String, String> TEST_NAMESPACES;

    private XpathEngine xpathEngine;

    public static String CSW_PREFIX = "csw";

    public static String CSW_URI = "http://www.opengis.net/csw/2.0.2";

    public static QName WATTEMP = new QName(DirectDownloadTest.CSW_URI, "watertemp", DirectDownloadTest.CSW_PREFIX);

    private static final String GET_RECORD_REQUEST = "csw?service=csw&version=2.0.2&request=GetRecords" + ("&elementsetname=full&typeNames=csw:Record&resultType=results" + "&constraint=title=%27watertemp%27");

    @Test
    public void testGetRecordWithDirectDownloadLink() throws Exception {
        Document dom = getAsDOM(DirectDownloadTest.GET_RECORD_REQUEST);
        print(dom);
        // check we have the expected results
        assertXpathEvaluatesTo("1", "count(//csw:Record/dc:identifier)", dom);
        assertXpathEvaluatesTo("6", "count(//csw:Record/dct:references)", dom);
        assertXpathEvaluatesTo("csw:watertemp", "//csw:Record/dc:identifier", dom);
        NodeList nodes = getMatchingNodes("//csw:Record/dct:references", dom);
        int size = nodes.getLength();
        // Get direct download links
        Set<String> links = new HashSet<String>();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            String link = node.getTextContent();
            if ((link.toUpperCase().contains("DIRECTDOWNLOAD")) && (link.contains("TIME"))) {
                links.add(link);
            }
        }
        // Get links from the reader
        final Catalog cat = getCatalog();
        String name = "watertemp";
        final CoverageInfo coverageInfo = cat.getCoverageByName(name);
        GridCoverage2DReader reader = null;
        CloseableLinksIterator<String> iterator = null;
        Set<String> generatedLinks = new HashSet<String>();
        try {
            reader = ((GridCoverage2DReader) (coverageInfo.getGridCoverageReader(null, null)));
            FileResourceInfo resourceInfo = ((FileResourceInfo) (reader.getInfo(name)));
            CloseableIterator<FileGroup> files = resourceInfo.getFiles(null);
            String baseLink = LINK;
            MockHttpServletRequest request = createRequest(baseLink);
            baseLink = ((request.getRequestURL()) + "?") + (request.getQueryString());
            baseLink = baseLink.replace("${nameSpace}", coverageInfo.getNamespace().getName()).replace("${layerName}", coverageInfo.getName()).replace("${version}", "2.0.2");
            iterator = new CloseableLinksIterator<String>(baseLink, files);
            while (iterator.hasNext()) {
                generatedLinks.add(iterator.next());
            } 
        } finally {
            if (iterator != null) {
                iterator.close();
            }
            if (reader != null) {
                try {
                    reader.dispose();
                } catch (Throwable t) {
                    // Ignore on disposal
                }
            }
        }
        // Look for links matching
        Iterator<String> it = links.iterator();
        int matches = 0;
        while (it.hasNext()) {
            String cswLink = it.next();
            if (generatedLinks.contains(cswLink)) {
                matches++;
            }
        } 
        TestCase.assertEquals(4, matches);
    }

    @Test
    public void testDirectDownloadExceedingLimit() throws Exception {
        Document dom = getAsDOM(DirectDownloadTest.GET_RECORD_REQUEST);
        NodeList nodes = getMatchingNodes("//csw:Record/dct:references", dom);
        int size = nodes.getLength();
        String downloadLink = null;
        // Getting the fullDataset Download link
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            String link = node.getTextContent();
            if ((link.toUpperCase().contains("DIRECTDOWNLOAD")) && (!(link.contains("TIME")))) {
                downloadLink = link;
                break;
            }
        }
        downloadLink = downloadLink.substring(downloadLink.indexOf("ows?"));
        // The output will not be a zip.
        // The download will exceed the limit so it returns an exception
        MockHttpServletResponse response = getAsServletResponse(downloadLink);
        TestCase.assertEquals("application/xml", response.getContentType());
        Document domResponse = dom(new java.io.ByteArrayInputStream(response.getContentAsString().getBytes()));
        Element root = domResponse.getDocumentElement();
        TestCase.assertEquals("ows:ExceptionReport", root.getNodeName());
        String exceptionText = evaluate("//ows:ExceptionReport/ows:Exception/ows:ExceptionText", domResponse);
        Assert.assertTrue(exceptionText.contains(LIMIT_MESSAGE));
    }

    @Test
    public void testDirectDownloadFile() throws Exception {
        Document dom = getAsDOM(DirectDownloadTest.GET_RECORD_REQUEST);
        NodeList nodes = getMatchingNodes("//csw:Record/dct:references", dom);
        int size = nodes.getLength();
        // Getting a file downloadLink
        String link = null;
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            link = node.getTextContent();
            if ((link.toUpperCase().contains("DIRECTDOWNLOAD")) && (link.contains("TIME"))) {
                break;
            }
        }
        link = link.substring(link.indexOf("ows?"));
        // The output will be a zip.
        MockHttpServletResponse response = getAsServletResponse(link);
        TestCase.assertEquals("application/zip", response.getContentType());
    }
}

