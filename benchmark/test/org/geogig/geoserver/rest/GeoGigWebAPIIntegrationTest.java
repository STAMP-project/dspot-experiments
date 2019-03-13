/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.rest;


import ContentType.APPLICATION_OCTET_STREAM;
import Ref.HEAD;
import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.custommonkey.xmlunit.XMLUnit;
import org.geogig.geoserver.GeoGigTestData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.locationtech.geogig.model.ObjectId;
import org.locationtech.geogig.model.Ref;
import org.locationtech.geogig.model.RevCommit;
import org.locationtech.geogig.model.RevObject;
import org.locationtech.geogig.plumbing.RefParse;
import org.locationtech.geogig.plumbing.ResolveTreeish;
import org.locationtech.geogig.repository.Repository;
import org.locationtech.geogig.repository.impl.GeoGIG;
import org.locationtech.geogig.storage.impl.ObjectSerializingFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


@TestSetup(run = TestSetupFrequency.ONCE)
public class GeoGigWebAPIIntegrationTest extends GeoServerSystemTestSupport {
    /**
     * {@code /geogig/repos/<repoId>}
     */
    private static String BASE_URL;

    private static final Random rnd = new Random();

    @ClassRule
    public static GeoGigTestData geogigData = new GeoGigTestData();

    /**
     * Test for resource {@code /rest/<repository>/repo/manifest}
     */
    @Test
    public void testGetManifest() throws Exception {
        final String url = (GeoGigWebAPIIntegrationTest.BASE_URL) + "/repo/manifest";
        MockHttpServletResponse sr = getAsServletResponse(url);
        Assert.assertEquals(200, sr.getStatus());
        String contentType = sr.getContentType();
        Assert.assertTrue(contentType, sr.getContentType().startsWith("text/plain"));
        String responseBody = sr.getContentAsString();
        Assert.assertNotNull(responseBody);
        Assert.assertTrue(responseBody, responseBody.startsWith("HEAD refs/heads/master"));
    }

    /**
     * Test for resource {@code /rest/<repository>/repo/exists?oid=...}
     */
    @Test
    public void testRevObjectExists() throws Exception {
        final String resource = (GeoGigWebAPIIntegrationTest.BASE_URL) + "/repo/exists?oid=";
        GeoGIG geogig = GeoGigWebAPIIntegrationTest.geogigData.getGeogig();
        Ref head = geogig.command(RefParse.class).setName(HEAD).call().get();
        ObjectId commitId = head.getObjectId();
        String url;
        url = resource + (commitId.toString());
        assertResponse(url, "1");
        ObjectId treeId = geogig.command(ResolveTreeish.class).setTreeish(commitId).call().get();
        url = resource + (treeId.toString());
        assertResponse(url, "1");
        url = resource + (hashString("fake"));
        assertResponse(url, "0");
    }

    /**
     * Test for resource {@code /rest/<repository>/repo/objects/<oid>}
     */
    @Test
    public void testGetObject() throws Exception {
        GeoGIG geogig = GeoGigWebAPIIntegrationTest.geogigData.getGeogig();
        Ref head = geogig.command(RefParse.class).setName(HEAD).call().get();
        ObjectId commitId = head.getObjectId();
        ObjectId treeId = geogig.command(ResolveTreeish.class).setTreeish(commitId).call().get();
        testGetRemoteObject(commitId);
        testGetRemoteObject(treeId);
    }

    /**
     * Test for resource {@code /rest/<repository>/repo/batchobjects}
     */
    @Test
    public void testGetBatchedObjects() throws Exception {
        GeoGIG geogig = GeoGigWebAPIIntegrationTest.geogigData.getGeogig();
        Ref head = geogig.command(RefParse.class).setName(HEAD).call().get();
        ObjectId commitId = head.getObjectId();
        testGetBatchedRemoteObjects(commitId);
    }

    private class ObjectStreamIterator extends AbstractIterator<RevObject> {
        private final InputStream bytes;

        private final ObjectSerializingFactory formats;

        public ObjectStreamIterator(InputStream input, ObjectSerializingFactory formats) {
            this.bytes = input;
            this.formats = formats;
        }

        @Override
        protected RevObject computeNext() {
            try {
                byte[] id = new byte[20];
                int len = bytes.read(id, 0, 20);
                if (len < 0)
                    return endOfData();

                if (len != 20)
                    throw new IllegalStateException("We need a 'readFully' operation!");

                return formats.read(new ObjectId(id), bytes);
            } catch (EOFException e) {
                return endOfData();
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Test
    public void testRemoteAdd() throws Exception {
        String remoteURL = "http://example.com/geogig/upstream";
        final String url = ((GeoGigWebAPIIntegrationTest.BASE_URL) + "/remote?remoteName=upstream&remoteURL=") + remoteURL;
        MockHttpServletResponse sr = getAsServletResponse(url);
        Assert.assertEquals(200, sr.getStatus());
        Document dom = dom(new java.io.ByteArrayInputStream(sr.getContentAsString().getBytes()), true);
        // <response><success>true</success><name>upstream</name></response>
        assertXpathEvaluatesTo("true", "/response/success", dom);
        assertXpathEvaluatesTo("upstream", "/response/name", dom);
        dom = getAsDOM(url);
        print(dom);
        // <response><success>false</success><error>REMOTE_ALREADY_EXISTS</error></response>
        assertXpathEvaluatesTo("false", "/response/success", dom);
        assertXpathEvaluatesTo("REMOTE_ALREADY_EXISTS", "/response/error", dom);
    }

    @Test
    public void testRemoteRemove() throws Exception {
        String remoteURL = "http://example.com/geogig/upstream";
        final String addUrl = (((GeoGigWebAPIIntegrationTest.BASE_URL) + "/remote?remoteURL=") + remoteURL) + "&remoteName=";
        final String removeUrl = (GeoGigWebAPIIntegrationTest.BASE_URL) + "/remote?remove=true&remoteName=";
        final String listUrl = (GeoGigWebAPIIntegrationTest.BASE_URL) + "/remote?list=true";
        MockHttpServletResponse sr;
        Document dom;
        dom = getAsDOM((addUrl + "upstream"));
        assertXpathEvaluatesTo("true", "/response/success", dom);
        assertXpathEvaluatesTo("upstream", "/response/name", dom);
        dom = getAsDOM((addUrl + "origin"));
        assertXpathEvaluatesTo("true", "/response/success", dom);
        assertXpathEvaluatesTo("origin", "/response/name", dom);
        dom = getAsDOM(listUrl);
        assertXpathExists("/response/Remote/name[text() = 'upstream']", dom);
        assertXpathExists("/response/Remote/name[text() = 'origin']", dom);
        dom = getAsDOM((removeUrl + "upstream"));
        assertXpathEvaluatesTo("true", "/response/success", dom);
        assertXpathEvaluatesTo("upstream", "/response/name", dom);
        dom = getAsDOM(listUrl);
        assertXpathNotExists("/response/Remote/name[text() = 'upstream']", dom);
        assertXpathExists("/response/Remote/name[text() = 'origin']", dom);
        dom = getAsDOM((removeUrl + "origin"));
        assertXpathEvaluatesTo("true", "/response/success", dom);
        assertXpathEvaluatesTo("origin", "/response/name", dom);
        dom = getAsDOM(listUrl);
        assertXpathNotExists("/response/Remote/name[text() = 'upstream']", dom);
        assertXpathNotExists("/response/Remote/name[text() = 'origin']", dom);
    }

    @Test
    public void testRemoteUpdate() throws Exception {
        String remoteURL = "http://example.com/geogig/upstream";
        String newURL = "http://new.example.com/geogig/upstream";
        final String addUrl = ((GeoGigWebAPIIntegrationTest.BASE_URL) + "/remote?remoteName=upstream&remoteURL=") + remoteURL;
        final String renameUrl = ((GeoGigWebAPIIntegrationTest.BASE_URL) + "/remote?update=true&remoteName=upstream&newName=new_name&remoteURL=") + newURL;
        Document dom = getAsDOM(addUrl);
        assertXpathEvaluatesTo("true", "/response/success", dom);
        assertXpathEvaluatesTo("upstream", "/response/name", dom);
        dom = getAsDOM(renameUrl);
        assertXpathEvaluatesTo("true", "/response/success", dom);
        assertXpathEvaluatesTo("new_name", "/response/name", dom);
    }

    @Test
    public void testGeoPackageImport() throws Exception {
        URL url = getClass().getResource("places.gpkg");
        // create transaction
        final String beginTransactionUrl = (GeoGigWebAPIIntegrationTest.BASE_URL) + "/beginTransaction";
        Document dom = getAsDOM(beginTransactionUrl);
        assertXpathEvaluatesTo("true", "/response/success", dom);
        String transactionId = XMLUnit.newXpathEngine().evaluate("/response/Transaction/ID", dom);
        // import geopackage
        final String importUrl = ((GeoGigWebAPIIntegrationTest.BASE_URL) + "/import?format=gpkg&message=Import%20GeoPackage&transactionId=") + transactionId;
        final String endTransactionUrl = ((GeoGigWebAPIIntegrationTest.BASE_URL) + "/endTransaction?transactionId=") + transactionId;
        // construct a multipart request with the fileUpload
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        File f = new File(url.getFile());
        builder.addBinaryBody("fileUpload", new FileInputStream(f), APPLICATION_OCTET_STREAM, f.getName());
        HttpEntity multipart = builder.build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        multipart.writeTo(outputStream);
        MockHttpServletResponse response = postAsServletResponse(importUrl, outputStream.toByteArray(), multipart.getContentType().getValue());
        Assert.assertEquals(200, response.getStatus());
        dom = dom(new java.io.ByteArrayInputStream(response.getContentAsString().getBytes()), true);
        String taskId = XMLUnit.newXpathEngine().evaluate("/task/id", dom);
        final String taskUrl = ("/geogig/tasks/" + taskId) + ".xml";
        while ("RUNNING".equals(XMLUnit.newXpathEngine().evaluate("/task/status", dom))) {
            Thread.sleep(100);
            dom = getAsDOM(taskUrl);
        } 
        assertXpathEvaluatesTo("FINISHED", "/task/status", dom);
        String commitId = XMLUnit.newXpathEngine().evaluate("//commit/id", dom);
        // close transaction
        dom = getAsDOM(endTransactionUrl);
        assertXpathEvaluatesTo("true", "/response/success", dom);
        // verify the repo contains the import
        Repository repository = GeoGigWebAPIIntegrationTest.geogigData.getGeogig().getRepository();
        RevCommit head = repository.getCommit(repository.getHead().get().getObjectId());
        Assert.assertEquals(commitId, head.getId().toString());
        Assert.assertEquals("Import GeoPackage", head.getMessage());
    }
}

