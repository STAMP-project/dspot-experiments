package com.baeldung.s3;


import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import java.io.File;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MultipartUploadLiveTest {
    private static final String BUCKET_NAME = "bucket_name";

    private static final String KEY_NAME = "picture.jpg";

    private AmazonS3 amazonS3;

    private TransferManager tm;

    private ProgressListener progressListener;

    @Test
    public void whenUploadingFileWithTransferManager_thenVerifyUploadRequested() {
        File file = Mockito.mock(File.class);
        PutObjectResult s3Result = Mockito.mock(PutObjectResult.class);
        Mockito.when(amazonS3.putObject(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ((File) (ArgumentMatchers.any())))).thenReturn(s3Result);
        Mockito.when(file.getName()).thenReturn(MultipartUploadLiveTest.KEY_NAME);
        PutObjectRequest request = new PutObjectRequest(MultipartUploadLiveTest.BUCKET_NAME, MultipartUploadLiveTest.KEY_NAME, file);
        request.setGeneralProgressListener(progressListener);
        Upload upload = tm.upload(request);
        assertThat(upload).isNotNull();
        Mockito.verify(amazonS3).putObject(request);
    }
}

