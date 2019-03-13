/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.wfs;


import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.locationtech.geogig.model.NodeRef;
import org.locationtech.geogig.model.RevCommit;
import org.locationtech.geogig.plumbing.FindTreeChild;
import org.locationtech.geogig.plumbing.LsTreeOp;
import org.locationtech.geogig.porcelain.LogOp;
import org.locationtech.geogig.repository.Context;
import org.locationtech.geogig.repository.impl.GeoGIG;
import org.locationtech.geogig.repository.impl.GlobalContextBuilder;
import org.locationtech.geogig.test.TestPlatform;
import org.locationtech.geogig.test.integration.RepositoryTestCase;
import org.w3c.dom.Document;


// zz_testCommitsSurviveShutDown run the latest
@TestSetup(run = TestSetupFrequency.ONCE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WFSIntegrationTest extends WFSTestSupport {
    private static final String NAMESPACE = "http://geogig.org";

    private static final String WORKSPACE = "geogig";

    private static final String STORE = "geogigstore";

    private static WFSIntegrationTest.TestHelper helper;

    private static class TestHelper extends RepositoryTestCase {
        @Override
        protected Context createInjector() {
            TestPlatform testPlatform = ((TestPlatform) (createPlatform()));
            GlobalContextBuilder.builder(new org.locationtech.geogig.cli.test.functional.CLITestContextBuilder(testPlatform));
            return build();
        }

        @Override
        protected void setUpInternal() throws Exception {
        }

        File getRepositoryDirectory() {
            return super.repositoryDirectory;
        }
    }

    @Test
    public void testInsert() throws Exception {
        Document dom;
        dom = getAsDOM("wfs?version=1.1.0&request=getfeature&typename=geogig:Lines&srsName=EPSG:4326&");
        int initial = dom.getElementsByTagName("geogig:Lines").getLength();
        dom = insert();
        Assert.assertEquals("wfs:TransactionResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("1", getFirstElementByTagName(dom, "wfs:totalInserted").getFirstChild().getNodeValue());
        dom = getAsDOM("wfs?version=1.1.0&request=getfeature&typename=geogig:Lines&srsName=EPSG:4326&");
        // print(dom);
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        Assert.assertEquals((1 + initial), dom.getElementsByTagName("geogig:Lines").getLength());
    }

    @Test
    public void testUpdate() throws Exception {
        Document dom = update();
        Assert.assertEquals("wfs:TransactionResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("1", getFirstElementByTagName(dom, "wfs:totalUpdated").getFirstChild().getNodeValue());
        dom = getAsDOM(("wfs?version=1.1.0&request=getfeature&typename=geogig:Lines" + ("&" + "cql_filter=ip%3D1000")));
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(1, dom.getElementsByTagName("geogig:Lines").getLength());
    }

    /**
     * Test case to expose issue https://github.com/boundlessgeo/geogig/issues/310 "Editing Features
     * changes the feature type"
     *
     * @see #testUpdateDoesntChangeFeatureType()
     */
    @Test
    public void testInsertDoesntChangeFeatureType() throws Exception {
        String xml = (((((((((((((("<wfs:Transaction service=\"WFS\" version=\"1.1.0\" "// 
         + ((" xmlns:wfs=\"http://www.opengis.net/wfs\" "// 
         + " xmlns:gml=\"http://www.opengis.net/gml\" ")// 
         + " xmlns:geogig=\"")) + (WFSIntegrationTest.NAMESPACE)) + "\">")// 
         + "<wfs:Insert>")// 
         + "<geogig:Lines gml:id=\"Lines.1000\">")// 
         + "    <geogig:sp>added</geogig:sp>")// 
         + "    <geogig:ip>7</geogig:ip>")// 
         + "    <geogig:pp>")// 
         + "        <gml:LineString srsDimension=\"2\" srsName=\"EPSG:4326\">")// 
         + "            <gml:posList>1 2 3 4</gml:posList>")// 
         + "        </gml:LineString>")// 
         + "    </geogig:pp>")// 
         + "</geogig:Lines>")// 
         + "</wfs:Insert>")// 
         + "</wfs:Transaction>";
        GeoGIG geogig = getGeogig();
        final NodeRef initialTypeTreeRef = geogig.command(FindTreeChild.class).setChildPath("Lines").call().get();
        Assert.assertFalse(initialTypeTreeRef.getMetadataId().isNull());
        Document dom = postAsDOM("wfs", xml);
        try {
            Assert.assertEquals("wfs:TransactionResponse", dom.getDocumentElement().getNodeName());
        } catch (AssertionError e) {
            print(dom);
            throw e;
        }
        try {
            Assert.assertEquals("1", getFirstElementByTagName(dom, "wfs:totalInserted").getFirstChild().getNodeValue());
        } catch (AssertionError e) {
            print(dom);
            throw e;
        }
        final NodeRef finalTypeTreeRef = geogig.command(FindTreeChild.class).setChildPath("Lines").call().get();
        Assert.assertFalse(initialTypeTreeRef.equals(finalTypeTreeRef));
        Assert.assertFalse(finalTypeTreeRef.getMetadataId().isNull());
        Assert.assertEquals("Feature type tree metadataId shouuldn't change upon edits", initialTypeTreeRef.getMetadataId(), finalTypeTreeRef.getMetadataId());
        Iterator<NodeRef> featureRefs = geogig.command(LsTreeOp.class).setReference("Lines").call();
        while (featureRefs.hasNext()) {
            NodeRef ref = featureRefs.next();
            Assert.assertEquals(finalTypeTreeRef.getMetadataId(), ref.getMetadataId());
            Assert.assertFalse(ref.toString(), ref.getNode().getMetadataId().isPresent());
        } 
    }

    /**
     * Test case to expose issue https://github.com/boundlessgeo/geogig/issues/310 "Editing Features
     * changes the feature type"
     *
     * @see #testInsertDoesntChangeFeatureType()
     */
    @Test
    public void testUpdateDoesntChangeFeatureType() throws Exception {
        String xml = (((((((((((((((((((((("<wfs:Transaction service=\"WFS\" version=\"1.1.0\""// 
         + " xmlns:geogig=\"") + (WFSIntegrationTest.NAMESPACE)) + "\"")// 
         + " xmlns:ogc=\"http://www.opengis.net/ogc\"")// 
         + " xmlns:gml=\"http://www.opengis.net/gml\"")// 
         + " xmlns:wfs=\"http://www.opengis.net/wfs\">")// 
         + " <wfs:Update typeName=\"geogig:Lines\">")// 
         + "   <wfs:Property>")// 
         + "     <wfs:Name>geogig:pp</wfs:Name>")// 
         + "     <wfs:Value>") + "        <gml:LineString srsDimension=\"2\" srsName=\"EPSG:4326\">")// 
         + "            <gml:posList>3 4 5 6</gml:posList>")// 
         + "        </gml:LineString>")// 
         + "     </wfs:Value>")// 
         + "   </wfs:Property>")// 
         + "   <ogc:Filter>")// 
         + "     <ogc:PropertyIsEqualTo>")// 
         + "       <ogc:PropertyName>ip</ogc:PropertyName>")// 
         + "       <ogc:Literal>1000</ogc:Literal>")// 
         + "     </ogc:PropertyIsEqualTo>")// 
         + "   </ogc:Filter>")// 
         + " </wfs:Update>")// 
         + "</wfs:Transaction>";
        GeoGIG geogig = getGeogig();
        final NodeRef initialTypeTreeRef = geogig.command(FindTreeChild.class).setChildPath("Lines").call().get();
        Assert.assertFalse(initialTypeTreeRef.getMetadataId().isNull());
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:TransactionResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("1", getFirstElementByTagName(dom, "wfs:totalUpdated").getFirstChild().getNodeValue());
        final NodeRef finalTypeTreeRef = geogig.command(FindTreeChild.class).setChildPath("Lines").call().get();
        Assert.assertFalse(initialTypeTreeRef.equals(finalTypeTreeRef));
        Assert.assertFalse(finalTypeTreeRef.getMetadataId().isNull());
        Assert.assertEquals("Feature type tree metadataId shouuldn't change upon edits", initialTypeTreeRef.getMetadataId(), finalTypeTreeRef.getMetadataId());
        Iterator<NodeRef> featureRefs = geogig.command(LsTreeOp.class).setReference("Lines").call();
        while (featureRefs.hasNext()) {
            NodeRef ref = featureRefs.next();
            Assert.assertEquals(finalTypeTreeRef.getMetadataId(), ref.getMetadataId());
            Assert.assertFalse(ref.toString(), ref.getNode().getMetadataId().isPresent());
        } 
    }

    // HACK: forcing this test to run the latest through
    // @FixMethodOrder(MethodSorters.NAME_ASCENDING) as it calls destroyGeoserver() and we really
    // want to use TestSetupFrequency.ONCE
    @Test
    public void zz_testCommitsSurviveShutDown() throws Exception {
        GeoGIG geogig = getGeogig();
        insert();
        update();
        List<RevCommit> expected = ImmutableList.copyOf(geogig.command(LogOp.class).call());
        File repoDir = WFSIntegrationTest.helper.getRepositoryDirectory();
        Assert.assertTrue(((repoDir.exists()) && (repoDir.isDirectory())));
        // shut down server
        destroyGeoServer();
        TestPlatform testPlatform = new TestPlatform(repoDir);
        Context context = build();
        GeoGIG geogig2 = new GeoGIG(context);
        try {
            Assert.assertNotNull(geogig2.getRepository());
            List<RevCommit> actual = ImmutableList.copyOf(geogig2.command(LogOp.class).call());
            Assert.assertEquals(expected, actual);
        } finally {
            geogig2.close();
        }
    }
}

