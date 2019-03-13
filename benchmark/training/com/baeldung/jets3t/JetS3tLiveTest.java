package com.baeldung.jets3t;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.StorageObject;
import org.junit.Assert;
import org.junit.Test;


public class JetS3tLiveTest {
    private Log log = LogFactory.getLog(JetS3tLiveTest.class);

    private static final String BucketName = "baeldung-barfoo";

    private static final String TestString = "test string";

    private static final String TestStringName = "string object";

    private static final String TgtBucket = "baeldung-tgtbucket";

    private static S3Service s3Service;

    @Test
    public void givenCreate_AndDeleteBucket_CountGoesUpThenDown() throws Exception {
        // List buckets, get a count
        S3Bucket[] myBuckets = JetS3tLiveTest.s3Service.listAllBuckets();
        int count = Arrays.stream(myBuckets).map(S3Bucket::getName).collect(Collectors.toList()).size();
        // Create a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        // List again
        myBuckets = JetS3tLiveTest.s3Service.listAllBuckets();
        int newCount = Arrays.stream(myBuckets).map(S3Bucket::getName).collect(Collectors.toList()).size();
        // We should have one more
        TestCase.assertEquals((count + 1), newCount);
        // Delete so next test doesn't fail
        deleteBucket();
        // Check the count again, just for laughs
        myBuckets = JetS3tLiveTest.s3Service.listAllBuckets();
        newCount = Arrays.stream(myBuckets).map(S3Bucket::getName).collect(Collectors.toList()).size();
        TestCase.assertEquals(count, newCount);
    }

    @Test
    public void givenString_Uploaded_StringInfoIsAvailable() throws Exception {
        // Create a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        // Upload a string
        uploadStringData();
        // Get the details
        StorageObject objectDetailsOnly = JetS3tLiveTest.s3Service.getObjectDetails(JetS3tLiveTest.BucketName, JetS3tLiveTest.TestStringName);
        log.info(((("Content type: " + (objectDetailsOnly.getContentType())) + " length: ") + (objectDetailsOnly.getContentLength())));
        // Delete it
        deleteObject(JetS3tLiveTest.TestStringName);
        // For next test
        deleteBucket();
    }

    @Test
    public void givenStringUploaded_StringIsDownloaded() throws Exception {
        // Get a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        uploadStringData();
        // Download
        S3Object stringObject = JetS3tLiveTest.s3Service.getObject(JetS3tLiveTest.BucketName, JetS3tLiveTest.TestStringName);
        // Process stream into a string
        String downloadedString = new BufferedReader(new InputStreamReader(stringObject.getDataInputStream())).lines().collect(Collectors.joining("\n"));
        // Verify
        Assert.assertTrue(JetS3tLiveTest.TestString.equals(downloadedString));
        // Clean up for next test
        deleteObject(JetS3tLiveTest.TestStringName);
        deleteBucket();
    }

    @Test
    public void givenBinaryFileUploaded_FileIsDownloaded() throws Exception {
        // get a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        // Put a binary file
        S3Object fileObject = new S3Object(new File("src/test/resources/test.jpg"));
        JetS3tLiveTest.s3Service.putObject(JetS3tLiveTest.BucketName, fileObject);
        // Print info about type and name
        log.info(("Content type:" + (fileObject.getContentType())));
        log.info(("File object name is " + (fileObject.getName())));
        // Download
        S3Object newFileObject = JetS3tLiveTest.s3Service.getObject(JetS3tLiveTest.BucketName, "test.jpg");
        // Save to a different name
        File newFile = new File("src/test/resources/newtest.jpg");
        Files.copy(newFileObject.getDataInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Get hashes and compare
        String origMD5 = getFileMD5("src/test/resources/test.jpg");
        String newMD5 = getFileMD5("src/test/resources/newtest.jpg");
        Assert.assertTrue(origMD5.equals(newMD5));
        // Clean up
        deleteObject("test.jpg");
        deleteBucket();
    }

    @Test
    public void givenStreamDataUploaded_StreamDataIsDownloaded() throws Exception {
        // get a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(2);
        numbers.add(3);
        numbers.add(5);
        numbers.add(7);
        // Serialize ArrayList
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytes);
        objectOutputStream.writeObject(numbers);
        // Wrap bytes
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.toByteArray());
        // Create and populate object
        S3Object streamObject = new S3Object("stream");
        streamObject.setDataInputStream(byteArrayInputStream);
        streamObject.setContentLength(byteArrayInputStream.available());
        streamObject.setContentType("binary/octet-stream");
        // Put it
        JetS3tLiveTest.s3Service.putObject(JetS3tLiveTest.BucketName, streamObject);
        // Get it
        S3Object newStreamObject = JetS3tLiveTest.s3Service.getObject(JetS3tLiveTest.BucketName, "stream");
        // Convert back to ArrayList
        ObjectInputStream objectInputStream = new ObjectInputStream(newStreamObject.getDataInputStream());
        ArrayList<Integer> newNumbers = ((ArrayList<Integer>) (objectInputStream.readObject()));
        TestCase.assertEquals(2, ((int) (newNumbers.get(0))));
        TestCase.assertEquals(3, ((int) (newNumbers.get(1))));
        TestCase.assertEquals(5, ((int) (newNumbers.get(2))));
        TestCase.assertEquals(7, ((int) (newNumbers.get(3))));
        // Clean up
        deleteObject("stream");
        deleteBucket();
    }

    @Test
    public void whenFileCopied_CopyIsSame() throws Exception {
        // get a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        // Put a binary file
        S3Object fileObject = new S3Object(new File("src/test/resources/test.jpg"));
        JetS3tLiveTest.s3Service.putObject(JetS3tLiveTest.BucketName, fileObject);
        // Copy it
        S3Object targetObject = new S3Object("testcopy.jpg");
        JetS3tLiveTest.s3Service.copyObject(JetS3tLiveTest.BucketName, "test.jpg", JetS3tLiveTest.BucketName, targetObject, false);
        // Download
        S3Object newFileObject = JetS3tLiveTest.s3Service.getObject(JetS3tLiveTest.BucketName, "testcopy.jpg");
        // Save to a different name
        File newFile = new File("src/test/resources/testcopy.jpg");
        Files.copy(newFileObject.getDataInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Get hashes and compare
        String origMD5 = getFileMD5("src/test/resources/test.jpg");
        String newMD5 = getFileMD5("src/test/resources/testcopy.jpg");
        Assert.assertTrue(origMD5.equals(newMD5));
        // Clean up
        deleteObject("test.jpg");
        deleteObject("testcopy.jpg");
        deleteBucket();
    }

    @Test
    public void whenFileRenamed_NewNameIsSame() throws Exception {
        // get a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        // Put a binary file
        S3Object fileObject = new S3Object(new File("src/test/resources/test.jpg"));
        JetS3tLiveTest.s3Service.putObject(JetS3tLiveTest.BucketName, fileObject);
        // Copy it
        JetS3tLiveTest.s3Service.renameObject(JetS3tLiveTest.BucketName, "test.jpg", new S3Object("spidey.jpg"));
        // Download
        S3Object newFileObject = JetS3tLiveTest.s3Service.getObject(JetS3tLiveTest.BucketName, "spidey.jpg");
        // Save to a different name
        File newFile = new File("src/test/resources/spidey.jpg");
        Files.copy(newFileObject.getDataInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Get hashes and compare
        String origMD5 = getFileMD5("src/test/resources/test.jpg");
        String newMD5 = getFileMD5("src/test/resources/spidey.jpg");
        Assert.assertTrue(origMD5.equals(newMD5));
        // Clean up
        deleteObject("test.jpg");
        deleteObject("spidey.jpg");
        deleteBucket();
    }

    @Test
    public void whenFileMoved_NewInstanceIsSame() throws Exception {
        // get a bucket
        S3Bucket bucket = createBucket();
        Assert.assertNotNull(bucket);
        // create another bucket
        S3Bucket tgtBucket = JetS3tLiveTest.s3Service.createBucket(JetS3tLiveTest.TgtBucket);
        // Put a binary file
        S3Object fileObject = new S3Object(new File("src/test/resources/test.jpg"));
        JetS3tLiveTest.s3Service.putObject(JetS3tLiveTest.BucketName, fileObject);
        // Copy it
        JetS3tLiveTest.s3Service.moveObject(JetS3tLiveTest.BucketName, "test.jpg", JetS3tLiveTest.TgtBucket, new S3Object("spidey.jpg"), false);
        // Download
        S3Object newFileObject = JetS3tLiveTest.s3Service.getObject(JetS3tLiveTest.TgtBucket, "spidey.jpg");
        // Save to a different name
        File newFile = new File("src/test/resources/spidey.jpg");
        Files.copy(newFileObject.getDataInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Get hashes and compare
        String origMD5 = getFileMD5("src/test/resources/test.jpg");
        String newMD5 = getFileMD5("src/test/resources/spidey.jpg");
        Assert.assertTrue(origMD5.equals(newMD5));
        // Clean up
        deleteBucket();
        JetS3tLiveTest.s3Service.deleteObject(JetS3tLiveTest.TgtBucket, "spidey.jpg");
        JetS3tLiveTest.s3Service.deleteBucket(JetS3tLiveTest.TgtBucket);
    }
}

