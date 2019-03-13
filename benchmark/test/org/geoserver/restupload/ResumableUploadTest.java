/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.restupload;


import RESTUtils.ROOT_KEY;
import ResumableUploadCatalogResource.RESUME_INCOMPLETE;
import Status.CLIENT_ERROR_NOT_FOUND;
import Status.SUCCESS_OK;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.catalog.rest.CatalogRESTTestSupport;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.impl.SettingsInfoImpl;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Test class for checking REST resumable upload
 *
 * @author Nicola Lagomarsini
 */
public class ResumableUploadTest extends CatalogRESTTestSupport {
    /**
     * Resource used for storing temporary uploads
     */
    private Resource tmpUploadFolder;

    /**
     * Size for partial uploads
     */
    private long partialSize = 50;

    /**
     * Relative path of the file to upload
     */
    private String fileName = "/relative/resumableUploadTest.shp";

    /**
     * Root folder
     */
    private String root;

    @Test
    public void testPostRequest() throws Exception {
        String uploadId = sendPostRequest();
        Assert.assertNotNull(uploadId);
        File uploadedFile = getTempPath(uploadId);
        Assert.assertTrue(uploadedFile.exists());
        Assert.assertEquals(0, uploadedFile.length());
    }

    @Test
    public void testSuccessivePostRequest() throws Exception {
        String uploadId = sendPostRequest();
        String secondUploadId = sendPostRequest();
        Assert.assertNotNull(secondUploadId);
        Assert.assertNotEquals(uploadId, secondUploadId);
    }

    @Test
    public void testUploadFull() throws Exception {
        String uploadId = sendPostRequest();
        File uploadedFile = getTempPath(uploadId);
        Assert.assertTrue(uploadedFile.exists());
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] bigFile = generateFileAsBytes();
        request.setContent(bigFile);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(SUCCESS_OK.getCode(), response.getStatus());
        Assert.assertFalse(uploadedFile.exists());
        File destinationFile = new File(FilenameUtils.concat(root, fileName.replaceAll("^/", "")));
        Assert.assertTrue(destinationFile.exists());
        Assert.assertEquals(bigFile.length, destinationFile.length());
        // Check uploaded file byte by byte
        boolean checkBytes = Arrays.equals(bigFile, toBytes(new FileInputStream(destinationFile)));
        Assert.assertTrue(checkBytes);
        // Check response content
        String restUrl = response.getContentAsString();
        Assert.assertEquals(fileName.replaceAll("^/", ""), restUrl);
    }

    @Test
    public void testUploadFullWithoutRestDir() throws Exception {
        // Set ROOT_KEY to null
        GeoServerInfo global = getGeoServer().getGlobal();
        SettingsInfoImpl info = ((SettingsInfoImpl) (ModificationProxy.unwrap(global.getSettings())));
        MetadataMap map = info.getMetadata();
        map.remove(ROOT_KEY);
        global.setSettings(info);
        getGeoServer().save(global);
        // Set root
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        Resource data = loader.get("data");
        root = data.dir().getAbsolutePath();
        testUploadFull();
        before();
    }

    @Test
    public void testPartialUpload() throws Exception {
        String uploadId = sendPostRequest();
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] bigFile = generateFileAsBytes();
        byte[] partialFile = ArrayUtils.subarray(bigFile, 0, ((int) (partialSize)));
        request.setContent(partialFile);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(RESUME_INCOMPLETE.getCode(), response.getStatus());
        Assert.assertEquals(null, response.getHeader("Content-Length"));
        Assert.assertEquals(("0-" + ((partialSize) - 1)), response.getHeader("Range"));
        File uploadedFile = getTempPath(uploadId);
        Assert.assertTrue(uploadedFile.exists());
        Assert.assertEquals(partialSize, uploadedFile.length());
        boolean checkBytes = Arrays.equals(partialFile, toBytes(new FileInputStream(uploadedFile)));
        Assert.assertTrue(checkBytes);
    }

    @Test
    public void testUploadPartialResume() throws Exception {
        String uploadId = sendPostRequest();
        byte[] bigFile = generateFileAsBytes();
        byte[] partialFile1 = ArrayUtils.subarray(bigFile, 0, ((int) (partialSize)));
        // First upload
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        request.setContent(partialFile1);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        dispatch(request);
        // Resume upload
        request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] partialFile2 = ArrayUtils.subarray(bigFile, ((int) (partialSize)), (((int) (partialSize)) * 2));
        request.setContent(partialFile2);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(partialFile2.length));
        request.addHeader("Content-Range", ((((("bytes " + (partialSize)) + "-") + ((partialSize) * 2)) + "/") + (bigFile.length)));
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(RESUME_INCOMPLETE.getCode(), response.getStatus());
        Assert.assertEquals(null, response.getHeader("Content-Length"));
        Assert.assertEquals(("0-" + (((partialSize) * 2) - 1)), response.getHeader("Range"));
        File uploadedFile = getTempPath(uploadId);
        Assert.assertTrue(uploadedFile.exists());
        Assert.assertEquals(((partialSize) * 2), uploadedFile.length());
        // Check uploaded file byte by byte
        boolean checkBytes = Arrays.equals(ArrayUtils.addAll(partialFile1, partialFile2), toBytes(new FileInputStream(uploadedFile)));
        Assert.assertTrue(checkBytes);
    }

    @Test
    public void testUploadFullResume() throws Exception {
        String uploadId = sendPostRequest();
        byte[] bigFile = generateFileAsBytes();
        byte[] partialFile1 = ArrayUtils.subarray(bigFile, 0, ((int) (partialSize)));
        // First upload
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        request.setContent(partialFile1);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        dispatch(request);
        // Resume upload
        request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] partialFile2 = ArrayUtils.subarray(bigFile, ((int) (partialSize)), bigFile.length);
        request.setContent(partialFile2);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(partialFile2.length));
        request.addHeader("Content-Range", ((((("bytes " + (partialSize)) + "-") + (bigFile.length)) + "/") + (bigFile.length)));
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(SUCCESS_OK.getCode(), response.getStatus());
        File uploadedFile = getTempPath(uploadId);
        Assert.assertFalse(uploadedFile.exists());
        File destinationFile = new File(FilenameUtils.concat(root, fileName.replaceAll("^/", "")));
        Assert.assertTrue(destinationFile.exists());
        Assert.assertEquals(bigFile.length, destinationFile.length());
        // Check uploaded file byte by byte
        boolean checkBytes = Arrays.equals(bigFile, toBytes(new FileInputStream(destinationFile)));
        Assert.assertTrue(checkBytes);
        // Check response content
        String restUrl = response.getContentAsString();
        Assert.assertEquals(fileName.replaceAll("^/", ""), restUrl);
    }

    @Test
    public void testPartialCleanup() throws Exception {
        // Change cleanup expirationDelay
        ResumableUploadResourceCleaner cleaner = ((ResumableUploadResourceCleaner) (applicationContext.getBean("resumableUploadStorageCleaner")));
        cleaner.setExpirationDelay(1000);
        // Upload file
        String uploadId = sendPostRequest();
        byte[] bigFile = generateFileAsBytes();
        byte[] partialFile = ArrayUtils.subarray(bigFile, 0, ((int) (partialSize)));
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        request.setContent(partialFile);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        dispatch(request);
        File uploadedFile = getTempPath(uploadId);
        Assert.assertTrue(uploadedFile.exists());
        // Wait to cleanup, max 2 minutes
        long startTime = new Date().getTime();
        while ((uploadedFile.exists()) && (((new Date().getTime()) - startTime) < 120000)) {
            Thread.sleep(1000);
        } 
        Assert.assertTrue((!(uploadedFile.exists())));
        cleaner.setExpirationDelay(300000);
    }

    @Test
    public void testSidecarCleanup() throws Exception {
        // Change cleanup expirationDelay
        ResumableUploadResourceCleaner cleaner = ((ResumableUploadResourceCleaner) (applicationContext.getBean("resumableUploadStorageCleaner")));
        cleaner.setExpirationDelay(1000);
        // Upload file
        String uploadId = sendPostRequest();
        byte[] bigFile = generateFileAsBytes();
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        request.setContent(bigFile);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        dispatch(request);
        File uploadedFile = getTempPath(uploadId);
        Assert.assertFalse(uploadedFile.exists());
        File sidecarFile = new File(FilenameUtils.concat(tmpUploadFolder.dir().getCanonicalPath(), (uploadId + ".sidecar")));
        Assert.assertTrue(sidecarFile.exists());
        // Wait to cleanup, max 2 minutes
        long startTime = new Date().getTime();
        while ((sidecarFile.exists()) && (((new Date().getTime()) - startTime) < 120000)) {
            Thread.sleep(1000);
        } 
        Assert.assertFalse(sidecarFile.exists());
        // Test GET after sidecar cleanup
        MockHttpServletResponse response = getAsServletResponse(("/rest/resumableupload/" + uploadId), "text/plain");
        Assert.assertEquals(CLIENT_ERROR_NOT_FOUND.getCode(), response.getStatus());
        cleaner.setExpirationDelay(300000);
    }

    @Test
    public void testGetAfterPartial() throws Exception {
        String uploadId = sendPostRequest();
        byte[] bigFile = generateFileAsBytes();
        byte[] partialFile = ArrayUtils.subarray(bigFile, 0, ((int) (partialSize)));
        // First upload
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        request.setContent(partialFile);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        dispatch(request);
        File uploadedFile = getTempPath(uploadId);
        Assert.assertTrue(uploadedFile.exists());
        MockHttpServletResponse response = getAsServletResponse(("/rest/resumableupload/" + uploadId), "text/plain");
        Assert.assertEquals(RESUME_INCOMPLETE.getCode(), response.getStatus());
        Assert.assertEquals(null, response.getHeader("Content-Length"));
        Assert.assertEquals(("0-" + ((partialSize) - 1)), response.getHeader("Range"));
    }

    @Test
    public void testGetAfterFull() throws Exception {
        String uploadId = sendPostRequest();
        File uploadedFile = getTempPath(uploadId);
        Assert.assertTrue(uploadedFile.exists());
        MockHttpServletRequest request = createRequest(("/rest/resumableupload/" + uploadId));
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] bigFile = generateFileAsBytes();
        request.setContent(bigFile);
        request.addHeader("Content-type", "application/octet-stream");
        request.addHeader("Content-Length", String.valueOf(bigFile.length));
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(SUCCESS_OK.getCode(), response.getStatus());
        File sidecarFile = new File(FilenameUtils.concat(tmpUploadFolder.dir().getCanonicalPath(), (uploadId + ".sidecar")));
        Assert.assertTrue(sidecarFile.exists());
    }
}

