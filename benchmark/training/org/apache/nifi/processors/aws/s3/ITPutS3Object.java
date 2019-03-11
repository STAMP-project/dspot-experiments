/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.aws.s3;


import CoreAttributes.FILENAME;
import DataUnit.B;
import ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION;
import ProvenanceEventType.SEND;
import PutS3Object.AWS_CREDENTIALS_PROVIDER_SERVICE;
import PutS3Object.BUCKET;
import PutS3Object.CONTENT_TYPE;
import PutS3Object.CREDENTIALS_FILE;
import PutS3Object.ENDPOINT_OVERRIDE;
import PutS3Object.FULL_CONTROL_USER_LIST;
import PutS3Object.KEY;
import PutS3Object.MULTIPART_PART_SIZE;
import PutS3Object.MULTIPART_THRESHOLD;
import PutS3Object.MultipartState;
import PutS3Object.NO_SERVER_SIDE_ENCRYPTION;
import PutS3Object.OBJECT_TAGS_PREFIX;
import PutS3Object.REGION;
import PutS3Object.REL_FAILURE;
import PutS3Object.REL_SUCCESS;
import PutS3Object.REMOVE_TAG_PREFIX;
import PutS3Object.S3_API_METHOD_ATTR_KEY;
import PutS3Object.S3_API_METHOD_MULTIPARTUPLOAD;
import PutS3Object.S3_API_METHOD_PUTOBJECT;
import PutS3Object.S3_BUCKET_KEY;
import PutS3Object.S3_CONTENT_TYPE;
import PutS3Object.S3_ETAG_ATTR_KEY;
import PutS3Object.S3_OBJECT_KEY;
import PutS3Object.S3_SSE_ALGORITHM;
import PutS3Object.S3_STORAGECLASS_ATTR_KEY;
import PutS3Object.S3_USERMETA_ATTR_KEY;
import PutS3Object.SERVER_SIDE_ENCRYPTION;
import PutS3Object.STORAGE_CLASS;
import StorageClass.ReducedRedundancy;
import StorageClass.Standard;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.Tag;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.aws.credentials.provider.service.AWSCredentialsProviderControllerService;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


/**
 * Provides integration level testing with actual AWS S3 resources for {@link PutS3Object} and requires additional configuration and resources to work.
 */
public class ITPutS3Object extends AbstractS3IT {
    static final String TEST_ENDPOINT = "https://endpoint.com";

    // final static String TEST_TRANSIT_URI = "https://" + BUCKET_NAME + ".endpoint.com";
    static final String TEST_PARTSIZE_STRING = "50 mb";

    static final Long TEST_PARTSIZE_LONG = (50L * 1024L) * 1024L;

    static final Long S3_MINIMUM_PART_SIZE = (50L * 1024L) * 1024L;

    static final Long S3_MAXIMUM_OBJECT_SIZE = ((5L * 1024L) * 1024L) * 1024L;

    static final Pattern reS3ETag = Pattern.compile("[0-9a-fA-f]{32,32}(-[0-9]+)?");

    @Test
    public void testSimplePut() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        Assert.assertTrue(runner.setProperty("x-custom-prop", "hello").isValid());
        for (int i = 0; i < 3; i++) {
            final Map<String, String> attrs = new HashMap<>();
            attrs.put("filename", ((String.valueOf(i)) + ".txt"));
            runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        }
        runner.run(3);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
    }

    @Test
    public void testSimplePutEncrypted() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(SERVER_SIDE_ENCRYPTION, AES_256_SERVER_SIDE_ENCRYPTION);
        Assert.assertTrue(runner.setProperty("x-custom-prop", "hello").isValid());
        for (int i = 0; i < 3; i++) {
            final Map<String, String> attrs = new HashMap<>();
            attrs.put("filename", ((String.valueOf(i)) + ".txt"));
            runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        }
        runner.run(3);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        final List<MockFlowFile> ffs = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : ffs) {
            flowFile.assertAttributeEquals(S3_SSE_ALGORITHM, AES_256_SERVER_SIDE_ENCRYPTION);
        }
    }

    @Test
    public void testPutThenFetchWithoutSSE() throws IOException {
        testPutThenFetch(NO_SERVER_SIDE_ENCRYPTION);
    }

    @Test
    public void testPutThenFetchWithSSE() throws IOException {
        testPutThenFetch(AES_256_SERVER_SIDE_ENCRYPTION);
    }

    @Test
    public void testPutS3ObjectUsingCredentialsProviderService() throws Throwable {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        final AWSCredentialsProviderControllerService serviceImpl = new AWSCredentialsProviderControllerService();
        runner.addControllerService("awsCredentialsProvider", serviceImpl);
        runner.setProperty(serviceImpl, AbstractAWSCredentialsProviderProcessor.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.enableControllerService(serviceImpl);
        runner.assertValid(serviceImpl);
        runner.setProperty(AWS_CREDENTIALS_PROVIDER_SERVICE, "awsCredentialsProvider");
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        Assert.assertTrue(runner.setProperty("x-custom-prop", "hello").isValid());
        for (int i = 0; i < 3; i++) {
            final Map<String, String> attrs = new HashMap<>();
            attrs.put("filename", ((String.valueOf(i)) + ".txt"));
            runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        }
        runner.run(3);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
    }

    @Test
    public void testMetaData() throws IOException {
        PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        PropertyDescriptor prop1 = processor.getSupportedDynamicPropertyDescriptor("TEST-PROP-1");
        runner.setProperty(prop1, "TESTING-1-2-3");
        PropertyDescriptor prop2 = processor.getSupportedDynamicPropertyDescriptor("TEST-PROP-2");
        runner.setProperty(prop2, "TESTING-4-5-6");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "meta.txt");
        runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile ff1 = flowFiles.get(0);
        for (Map.Entry attrib : ff1.getAttributes().entrySet()) {
            System.out.println((((attrib.getKey()) + " = ") + (attrib.getValue())));
        }
    }

    @Test
    public void testContentType() throws IOException {
        PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(CONTENT_TYPE, "text/plain");
        runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile ff1 = flowFiles.get(0);
        ff1.assertAttributeEquals(S3_CONTENT_TYPE, "text/plain");
    }

    @Test
    public void testPutInFolder() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        Assert.assertTrue(runner.setProperty("x-custom-prop", "hello").isValid());
        runner.assertValid();
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/1.txt");
        runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testStorageClass() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(STORAGE_CLASS, ReducedRedundancy.name());
        int bytesNeeded = (55 * 1024) * 1024;
        StringBuilder bldr = new StringBuilder((bytesNeeded + 1000));
        for (int line = 0; line < 55; line++) {
            bldr.append(String.format("line %06d This is sixty-three characters plus the EOL marker!\n", line));
        }
        String data55mb = bldr.toString();
        Assert.assertTrue(runner.setProperty("x-custom-prop", "hello").isValid());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/2.txt");
        runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        attrs.put("filename", "folder/3.txt");
        runner.enqueue(data55mb.getBytes(), attrs);
        runner.run(2);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        FlowFile file1 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(ReducedRedundancy.toString(), file1.getAttribute(S3_STORAGECLASS_ATTR_KEY));
        FlowFile file2 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        Assert.assertEquals(ReducedRedundancy.toString(), file2.getAttribute(S3_STORAGECLASS_ATTR_KEY));
    }

    @Test
    public void testPermissions() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(FULL_CONTROL_USER_LIST, "28545acd76c35c7e91f8409b95fd1aa0c0914bfa1ac60975d9f48bc3c5e090b5");
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/4.txt");
        runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testDynamicProperty() throws IOException {
        final String DYNAMIC_ATTRIB_KEY = "fs.runTimestamp";
        final String DYNAMIC_ATTRIB_VALUE = "${now():toNumber()}";
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(MULTIPART_PART_SIZE, ITPutS3Object.TEST_PARTSIZE_STRING);
        PropertyDescriptor testAttrib = processor.getSupportedDynamicPropertyDescriptor(DYNAMIC_ATTRIB_KEY);
        runner.setProperty(testAttrib, DYNAMIC_ATTRIB_VALUE);
        final String FILE1_NAME = "file1";
        Map<String, String> attribs = new HashMap<>();
        attribs.put(FILENAME.key(), FILE1_NAME);
        runner.enqueue("123".getBytes(), attribs);
        runner.assertValid();
        processor.getPropertyDescriptor(DYNAMIC_ATTRIB_KEY);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles.size());
        MockFlowFile ff1 = successFiles.get(0);
        Long now = System.currentTimeMillis();
        String millisNow = Long.toString(now);
        String millisOneSecAgo = Long.toString((now - 1000L));
        String usermeta = ff1.getAttribute(S3_USERMETA_ATTR_KEY);
        String[] usermetaLine0 = usermeta.split(System.lineSeparator())[0].split("=");
        String usermetaKey0 = usermetaLine0[0];
        String usermetaValue0 = usermetaLine0[1];
        Assert.assertEquals(DYNAMIC_ATTRIB_KEY, usermetaKey0);
        Assert.assertTrue((((usermetaValue0.compareTo(millisOneSecAgo)) >= 0) && ((usermetaValue0.compareTo(millisNow)) <= 0)));
    }

    @Test
    public void testProvenance() throws InitializationException {
        final String PROV1_FILE = "provfile1";
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(KEY, "${filename}");
        Map<String, String> attribs = new HashMap<>();
        attribs.put(FILENAME.key(), PROV1_FILE);
        runner.enqueue("prov1 contents".getBytes(), attribs);
        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles.size());
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        ProvenanceEventRecord provRec1 = provenanceEvents.get(0);
        Assert.assertEquals(SEND, provRec1.getEventType());
        Assert.assertEquals(processor.getIdentifier(), provRec1.getComponentId());
        AbstractS3IT.client.setRegion(Region.fromValue(AbstractS3IT.REGION).toAWSRegion());
        String targetUri = AbstractS3IT.client.getUrl(AbstractS3IT.BUCKET_NAME, PROV1_FILE).toString();
        Assert.assertEquals(targetUri, provRec1.getTransitUri());
        Assert.assertEquals(7, provRec1.getUpdatedAttributes().size());
        Assert.assertEquals(AbstractS3IT.BUCKET_NAME, provRec1.getUpdatedAttributes().get(S3_BUCKET_KEY));
    }

    @Test
    public void testStateDefaults() {
        PutS3Object.MultipartState state1 = new PutS3Object.MultipartState();
        Assert.assertEquals(state1.getUploadId(), "");
        Assert.assertEquals(state1.getFilePosition(), ((Long) (0L)));
        Assert.assertEquals(state1.getPartETags().size(), 0L);
        Assert.assertEquals(state1.getPartSize(), ((Long) (0L)));
        Assert.assertEquals(state1.getStorageClass().toString(), Standard.toString());
        Assert.assertEquals(state1.getContentLength(), ((Long) (0L)));
    }

    @Test
    public void testStateToString() throws IOException, InitializationException {
        final String target = "UID-test1234567890#10001#1/PartETag-1,2/PartETag-2,3/PartETag-3,4/PartETag-4#20002#REDUCED_REDUNDANCY#30003#8675309";
        PutS3Object.MultipartState state2 = new PutS3Object.MultipartState();
        state2.setUploadId("UID-test1234567890");
        state2.setFilePosition(10001L);
        state2.setTimestamp(8675309L);
        for (Integer partNum = 1; partNum < 5; partNum++) {
            state2.addPartETag(new PartETag(partNum, ("PartETag-" + (partNum.toString()))));
        }
        state2.setPartSize(20002L);
        state2.setStorageClass(ReducedRedundancy);
        state2.setContentLength(30003L);
        Assert.assertEquals(target, state2.toString());
    }

    @Test
    public void testEndpointOverride() {
        // remove leading "/" from filename to avoid duplicate separators
        final String TESTKEY = AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME.substring(1);
        final PutS3Object processor = new ITPutS3Object.TestablePutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        final ProcessContext context = runner.getProcessContext();
        runner.setProperty(ENDPOINT_OVERRIDE, ITPutS3Object.TEST_ENDPOINT);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(KEY, TESTKEY);
        runner.run();
        Assert.assertEquals(AbstractS3IT.BUCKET_NAME, context.getProperty(BUCKET).toString());
        Assert.assertEquals(TESTKEY, context.getProperty(KEY).evaluateAttributeExpressions().toString());
        Assert.assertEquals(ITPutS3Object.TEST_ENDPOINT, context.getProperty(ENDPOINT_OVERRIDE).toString());
        String s3url = ((ITPutS3Object.TestablePutS3Object) (processor)).testable_getClient().getResourceUrl(AbstractS3IT.BUCKET_NAME, TESTKEY);
        Assert.assertEquals((((((ITPutS3Object.TEST_ENDPOINT) + "/") + (AbstractS3IT.BUCKET_NAME)) + "/") + TESTKEY), s3url);
    }

    @Test
    public void testMultipartProperties() throws IOException {
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        final ProcessContext context = runner.getProcessContext();
        runner.setProperty(FULL_CONTROL_USER_LIST, "28545acd76c35c7e91f8409b95fd1aa0c0914bfa1ac60975d9f48bc3c5e090b5");
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(MULTIPART_PART_SIZE, ITPutS3Object.TEST_PARTSIZE_STRING);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(KEY, AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME);
        Assert.assertEquals(AbstractS3IT.BUCKET_NAME, context.getProperty(BUCKET).toString());
        Assert.assertEquals(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME, context.getProperty(KEY).evaluateAttributeExpressions().toString());
        Assert.assertEquals(ITPutS3Object.TEST_PARTSIZE_LONG.longValue(), context.getProperty(MULTIPART_PART_SIZE).asDataSize(B).longValue());
    }

    @Test
    public void testLocalStatePersistence() throws IOException {
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        final String bucket = runner.getProcessContext().getProperty(BUCKET).getValue();
        final String key = runner.getProcessContext().getProperty(KEY).getValue();
        final String cacheKey1 = ((((runner.getProcessor().getIdentifier()) + "/") + bucket) + "/") + key;
        final String cacheKey2 = (((((runner.getProcessor().getIdentifier()) + "/") + bucket) + "/") + key) + "-v2";
        final String cacheKey3 = (((((runner.getProcessor().getIdentifier()) + "/") + bucket) + "/") + key) + "-v3";
        /* store 3 versions of state */
        PutS3Object.MultipartState state1orig = new PutS3Object.MultipartState();
        processor.persistLocalState(cacheKey1, state1orig);
        PutS3Object.MultipartState state2orig = new PutS3Object.MultipartState();
        state2orig.setUploadId("1234");
        state2orig.setContentLength(1234L);
        processor.persistLocalState(cacheKey2, state2orig);
        PutS3Object.MultipartState state3orig = new PutS3Object.MultipartState();
        state3orig.setUploadId("5678");
        state3orig.setContentLength(5678L);
        processor.persistLocalState(cacheKey3, state3orig);
        final List<MultipartUpload> uploadList = new ArrayList<>();
        final MultipartUpload upload1 = new MultipartUpload();
        upload1.setKey(key);
        upload1.setUploadId("");
        uploadList.add(upload1);
        final MultipartUpload upload2 = new MultipartUpload();
        upload2.setKey((key + "-v2"));
        upload2.setUploadId("1234");
        uploadList.add(upload2);
        final MultipartUpload upload3 = new MultipartUpload();
        upload3.setKey((key + "-v3"));
        upload3.setUploadId("5678");
        uploadList.add(upload3);
        final MultipartUploadListing uploadListing = new MultipartUploadListing();
        uploadListing.setMultipartUploads(uploadList);
        final ITPutS3Object.MockAmazonS3Client mockClient = new ITPutS3Object.MockAmazonS3Client();
        mockClient.setListing(uploadListing);
        /* reload and validate stored state */
        final PutS3Object.MultipartState state1new = processor.getLocalStateIfInS3(mockClient, bucket, cacheKey1);
        Assert.assertEquals("", state1new.getUploadId());
        Assert.assertEquals(0L, state1new.getFilePosition().longValue());
        Assert.assertEquals(new ArrayList<PartETag>(), state1new.getPartETags());
        Assert.assertEquals(0L, state1new.getPartSize().longValue());
        Assert.assertEquals(StorageClass.fromValue(Standard.toString()), state1new.getStorageClass());
        Assert.assertEquals(0L, state1new.getContentLength().longValue());
        final PutS3Object.MultipartState state2new = processor.getLocalStateIfInS3(mockClient, bucket, cacheKey2);
        Assert.assertEquals("1234", state2new.getUploadId());
        Assert.assertEquals(0L, state2new.getFilePosition().longValue());
        Assert.assertEquals(new ArrayList<PartETag>(), state2new.getPartETags());
        Assert.assertEquals(0L, state2new.getPartSize().longValue());
        Assert.assertEquals(StorageClass.fromValue(Standard.toString()), state2new.getStorageClass());
        Assert.assertEquals(1234L, state2new.getContentLength().longValue());
        final PutS3Object.MultipartState state3new = processor.getLocalStateIfInS3(mockClient, bucket, cacheKey3);
        Assert.assertEquals("5678", state3new.getUploadId());
        Assert.assertEquals(0L, state3new.getFilePosition().longValue());
        Assert.assertEquals(new ArrayList<PartETag>(), state3new.getPartETags());
        Assert.assertEquals(0L, state3new.getPartSize().longValue());
        Assert.assertEquals(StorageClass.fromValue(Standard.toString()), state3new.getStorageClass());
        Assert.assertEquals(5678L, state3new.getContentLength().longValue());
    }

    @Test
    public void testStatePersistsETags() throws IOException {
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        final String bucket = runner.getProcessContext().getProperty(BUCKET).getValue();
        final String key = runner.getProcessContext().getProperty(KEY).getValue();
        final String cacheKey1 = (((((runner.getProcessor().getIdentifier()) + "/") + bucket) + "/") + key) + "-bv1";
        final String cacheKey2 = (((((runner.getProcessor().getIdentifier()) + "/") + bucket) + "/") + key) + "-bv2";
        final String cacheKey3 = (((((runner.getProcessor().getIdentifier()) + "/") + bucket) + "/") + key) + "-bv3";
        /* store 3 versions of state */
        PutS3Object.MultipartState state1orig = new PutS3Object.MultipartState();
        processor.persistLocalState(cacheKey1, state1orig);
        PutS3Object.MultipartState state2orig = new PutS3Object.MultipartState();
        state2orig.setUploadId("1234");
        state2orig.setContentLength(1234L);
        processor.persistLocalState(cacheKey2, state2orig);
        PutS3Object.MultipartState state3orig = new PutS3Object.MultipartState();
        state3orig.setUploadId("5678");
        state3orig.setContentLength(5678L);
        processor.persistLocalState(cacheKey3, state3orig);
        /* persist state to caches so that
             1. v2 has 2 and then 4 tags
             2. v3 has 4 and then 2 tags
         */
        state2orig.getPartETags().add(new PartETag(1, "state 2 tag one"));
        state2orig.getPartETags().add(new PartETag(2, "state 2 tag two"));
        processor.persistLocalState(cacheKey2, state2orig);
        state2orig.getPartETags().add(new PartETag(3, "state 2 tag three"));
        state2orig.getPartETags().add(new PartETag(4, "state 2 tag four"));
        processor.persistLocalState(cacheKey2, state2orig);
        state3orig.getPartETags().add(new PartETag(1, "state 3 tag one"));
        state3orig.getPartETags().add(new PartETag(2, "state 3 tag two"));
        state3orig.getPartETags().add(new PartETag(3, "state 3 tag three"));
        state3orig.getPartETags().add(new PartETag(4, "state 3 tag four"));
        processor.persistLocalState(cacheKey3, state3orig);
        state3orig.getPartETags().remove(((state3orig.getPartETags().size()) - 1));
        state3orig.getPartETags().remove(((state3orig.getPartETags().size()) - 1));
        processor.persistLocalState(cacheKey3, state3orig);
        final List<MultipartUpload> uploadList = new ArrayList<>();
        final MultipartUpload upload1 = new MultipartUpload();
        upload1.setKey((key + "-bv2"));
        upload1.setUploadId("1234");
        uploadList.add(upload1);
        final MultipartUpload upload2 = new MultipartUpload();
        upload2.setKey((key + "-bv3"));
        upload2.setUploadId("5678");
        uploadList.add(upload2);
        final MultipartUploadListing uploadListing = new MultipartUploadListing();
        uploadListing.setMultipartUploads(uploadList);
        final ITPutS3Object.MockAmazonS3Client mockClient = new ITPutS3Object.MockAmazonS3Client();
        mockClient.setListing(uploadListing);
        /* load state and validate that
            1. v2 restore shows 4 tags
            2. v3 restore shows 2 tags
         */
        final PutS3Object.MultipartState state2new = processor.getLocalStateIfInS3(mockClient, bucket, cacheKey2);
        Assert.assertEquals("1234", state2new.getUploadId());
        Assert.assertEquals(4, state2new.getPartETags().size());
        final PutS3Object.MultipartState state3new = processor.getLocalStateIfInS3(mockClient, bucket, cacheKey3);
        Assert.assertEquals("5678", state3new.getUploadId());
        Assert.assertEquals(2, state3new.getPartETags().size());
    }

    @Test
    public void testStateRemove() throws IOException {
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        final String bucket = runner.getProcessContext().getProperty(BUCKET).getValue();
        final String key = runner.getProcessContext().getProperty(KEY).getValue();
        final String cacheKey = (((((runner.getProcessor().getIdentifier()) + "/") + bucket) + "/") + key) + "-sr";
        final List<MultipartUpload> uploadList = new ArrayList<>();
        final MultipartUpload upload1 = new MultipartUpload();
        upload1.setKey(key);
        upload1.setUploadId("1234");
        uploadList.add(upload1);
        final MultipartUploadListing uploadListing = new MultipartUploadListing();
        uploadListing.setMultipartUploads(uploadList);
        final ITPutS3Object.MockAmazonS3Client mockClient = new ITPutS3Object.MockAmazonS3Client();
        mockClient.setListing(uploadListing);
        /* store state, retrieve and validate, remove and validate */
        PutS3Object.MultipartState stateOrig = new PutS3Object.MultipartState();
        stateOrig.setUploadId("1234");
        stateOrig.setContentLength(1234L);
        processor.persistLocalState(cacheKey, stateOrig);
        PutS3Object.MultipartState state1 = processor.getLocalStateIfInS3(mockClient, bucket, cacheKey);
        Assert.assertEquals("1234", state1.getUploadId());
        Assert.assertEquals(1234L, state1.getContentLength().longValue());
        processor.persistLocalState(cacheKey, null);
        PutS3Object.MultipartState state2 = processor.getLocalStateIfInS3(mockClient, bucket, cacheKey);
        Assert.assertNull(state2);
    }

    @Test
    public void testMultipartSmallerThanMinimum() throws IOException {
        final String FILE1_NAME = "file1";
        final byte[] megabyte = new byte[1024 * 1024];
        final Path tempFile = Files.createTempFile("s3mulitpart", "tmp");
        final FileOutputStream tempOut = new FileOutputStream(tempFile.toFile());
        long tempByteCount = 0;
        for (int i = 0; i < 5; i++) {
            tempOut.write(megabyte);
            tempByteCount += megabyte.length;
        }
        tempOut.close();
        System.out.println(("file size: " + tempByteCount));
        Assert.assertTrue((tempByteCount < (ITPutS3Object.S3_MINIMUM_PART_SIZE)));
        Assert.assertTrue(((megabyte.length) < (ITPutS3Object.S3_MINIMUM_PART_SIZE)));
        Assert.assertTrue((((ITPutS3Object.TEST_PARTSIZE_LONG) >= (ITPutS3Object.S3_MINIMUM_PART_SIZE)) && ((ITPutS3Object.TEST_PARTSIZE_LONG) <= (ITPutS3Object.S3_MAXIMUM_OBJECT_SIZE))));
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(MULTIPART_PART_SIZE, ITPutS3Object.TEST_PARTSIZE_STRING);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(FILENAME.key(), FILE1_NAME);
        runner.enqueue(new FileInputStream(tempFile.toFile()), attributes);
        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles.size());
        final List<MockFlowFile> failureFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFiles.size());
        MockFlowFile ff1 = successFiles.get(0);
        Assert.assertEquals(S3_API_METHOD_PUTOBJECT, ff1.getAttribute(S3_API_METHOD_ATTR_KEY));
        Assert.assertEquals(FILE1_NAME, ff1.getAttribute(FILENAME.key()));
        Assert.assertEquals(AbstractS3IT.BUCKET_NAME, ff1.getAttribute(S3_BUCKET_KEY));
        Assert.assertEquals(FILE1_NAME, ff1.getAttribute(S3_OBJECT_KEY));
        Assert.assertTrue(ITPutS3Object.reS3ETag.matcher(ff1.getAttribute(S3_ETAG_ATTR_KEY)).matches());
        Assert.assertEquals(tempByteCount, ff1.getSize());
    }

    @Test
    public void testMultipartBetweenMinimumAndMaximum() throws IOException {
        final String FILE1_NAME = "file1";
        final byte[] megabyte = new byte[1024 * 1024];
        final Path tempFile = Files.createTempFile("s3mulitpart", "tmp");
        final FileOutputStream tempOut = new FileOutputStream(tempFile.toFile());
        long tempByteCount = 0;
        for (; tempByteCount < ((ITPutS3Object.TEST_PARTSIZE_LONG) + 1);) {
            tempOut.write(megabyte);
            tempByteCount += megabyte.length;
        }
        tempOut.close();
        System.out.println(("file size: " + tempByteCount));
        Assert.assertTrue(((tempByteCount > (ITPutS3Object.S3_MINIMUM_PART_SIZE)) && (tempByteCount < (ITPutS3Object.S3_MAXIMUM_OBJECT_SIZE))));
        Assert.assertTrue((tempByteCount > (ITPutS3Object.TEST_PARTSIZE_LONG)));
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(MULTIPART_THRESHOLD, ITPutS3Object.TEST_PARTSIZE_STRING);
        runner.setProperty(MULTIPART_PART_SIZE, ITPutS3Object.TEST_PARTSIZE_STRING);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(FILENAME.key(), FILE1_NAME);
        runner.enqueue(new FileInputStream(tempFile.toFile()), attributes);
        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles.size());
        final List<MockFlowFile> failureFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFiles.size());
        MockFlowFile ff1 = successFiles.get(0);
        Assert.assertEquals(S3_API_METHOD_MULTIPARTUPLOAD, ff1.getAttribute(S3_API_METHOD_ATTR_KEY));
        Assert.assertEquals(FILE1_NAME, ff1.getAttribute(FILENAME.key()));
        Assert.assertEquals(AbstractS3IT.BUCKET_NAME, ff1.getAttribute(S3_BUCKET_KEY));
        Assert.assertEquals(FILE1_NAME, ff1.getAttribute(S3_OBJECT_KEY));
        Assert.assertTrue(ITPutS3Object.reS3ETag.matcher(ff1.getAttribute(S3_ETAG_ATTR_KEY)).matches());
        Assert.assertEquals(tempByteCount, ff1.getSize());
    }

    @Test
    public void testObjectTags() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(OBJECT_TAGS_PREFIX, "tagS3");
        runner.setProperty(REMOVE_TAG_PREFIX, "true");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "tag-test.txt");
        attrs.put("tagS3PII", "true");
        runner.enqueue(getResourcePath(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), attrs);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        GetObjectTaggingResult result = AbstractS3IT.client.getObjectTagging(new GetObjectTaggingRequest(AbstractS3IT.BUCKET_NAME, "tag-test.txt"));
        List<Tag> objectTags = result.getTagSet();
        for (Tag tag : objectTags) {
            System.out.println(((("Tag Key : " + (tag.getKey())) + ", Tag Value : ") + (tag.getValue())));
        }
        Assert.assertTrue(((objectTags.size()) == 1));
        Assert.assertEquals("PII", objectTags.get(0).getKey());
        Assert.assertEquals("true", objectTags.get(0).getValue());
    }

    private class MockAmazonS3Client extends AmazonS3Client {
        MultipartUploadListing listing;

        public void setListing(MultipartUploadListing newlisting) {
            listing = newlisting;
        }

        @Override
        public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) throws AmazonClientException, AmazonServiceException {
            return listing;
        }
    }

    public class TestablePutS3Object extends PutS3Object {
        public AmazonS3Client testable_getClient() {
            return getClient();
        }
    }
}

