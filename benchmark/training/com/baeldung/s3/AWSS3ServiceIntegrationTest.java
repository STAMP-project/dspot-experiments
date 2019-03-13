package com.baeldung.s3;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.io.File;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AWSS3ServiceIntegrationTest {
    private static final String BUCKET_NAME = "bucket_name";

    private static final String KEY_NAME = "key_name";

    private static final String BUCKET_NAME2 = "bucket_name2";

    private static final String KEY_NAME2 = "key_name2";

    private AmazonS3 s3;

    private AWSS3Service service;

    @Test
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertThat(new AWSS3Service()).isNotNull();
    }

    @Test
    public void whenVerifyingIfS3BucketExist_thenCorrect() {
        service.doesBucketExist(AWSS3ServiceIntegrationTest.BUCKET_NAME);
        Mockito.verify(s3).doesBucketExist(AWSS3ServiceIntegrationTest.BUCKET_NAME);
    }

    @Test
    public void whenVerifyingCreationOfS3Bucket_thenCorrect() {
        service.createBucket(AWSS3ServiceIntegrationTest.BUCKET_NAME);
        Mockito.verify(s3).createBucket(AWSS3ServiceIntegrationTest.BUCKET_NAME);
    }

    @Test
    public void whenVerifyingListBuckets_thenCorrect() {
        service.listBuckets();
        Mockito.verify(s3).listBuckets();
    }

    @Test
    public void whenDeletingBucket_thenCorrect() {
        service.deleteBucket(AWSS3ServiceIntegrationTest.BUCKET_NAME);
        Mockito.verify(s3).deleteBucket(AWSS3ServiceIntegrationTest.BUCKET_NAME);
    }

    @Test
    public void whenVerifyingPutObject_thenCorrect() {
        File file = Mockito.mock(File.class);
        PutObjectResult result = Mockito.mock(PutObjectResult.class);
        Mockito.when(s3.putObject(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ((File) (ArgumentMatchers.any())))).thenReturn(result);
        assertThat(service.putObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME, file)).isEqualTo(result);
        Mockito.verify(s3).putObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME, file);
    }

    @Test
    public void whenVerifyingListObjects_thenCorrect() {
        service.listObjects(AWSS3ServiceIntegrationTest.BUCKET_NAME);
        Mockito.verify(s3).listObjects(AWSS3ServiceIntegrationTest.BUCKET_NAME);
    }

    @Test
    public void whenVerifyingGetObject_thenCorrect() {
        service.getObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME);
        Mockito.verify(s3).getObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME);
    }

    @Test
    public void whenVerifyingCopyObject_thenCorrect() {
        CopyObjectResult result = Mockito.mock(CopyObjectResult.class);
        Mockito.when(s3.copyObject(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(result);
        assertThat(service.copyObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME, AWSS3ServiceIntegrationTest.BUCKET_NAME2, AWSS3ServiceIntegrationTest.KEY_NAME2)).isEqualTo(result);
        Mockito.verify(s3).copyObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME, AWSS3ServiceIntegrationTest.BUCKET_NAME2, AWSS3ServiceIntegrationTest.KEY_NAME2);
    }

    @Test
    public void whenVerifyingDeleteObject_thenCorrect() {
        service.deleteObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME);
        Mockito.verify(s3).deleteObject(AWSS3ServiceIntegrationTest.BUCKET_NAME, AWSS3ServiceIntegrationTest.KEY_NAME);
    }

    @Test
    public void whenVerifyingDeleteObjects_thenCorrect() {
        DeleteObjectsRequest request = Mockito.mock(DeleteObjectsRequest.class);
        DeleteObjectsResult result = Mockito.mock(DeleteObjectsResult.class);
        Mockito.when(s3.deleteObjects(((DeleteObjectsRequest) (ArgumentMatchers.any())))).thenReturn(result);
        assertThat(service.deleteObjects(request)).isEqualTo(result);
        Mockito.verify(s3).deleteObjects(request);
    }
}

