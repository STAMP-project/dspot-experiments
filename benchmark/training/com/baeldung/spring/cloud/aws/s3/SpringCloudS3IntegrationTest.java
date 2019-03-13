package com.baeldung.spring.cloud.aws.s3;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class SpringCloudS3IntegrationTest {
    @Autowired
    private SpringCloudS3 springCloudS3;

    private static String bucketName;

    private static String testFileToDownload;

    private static String testFileToUpload;

    private static String[] filesWithSimilarName;

    private static List<File> similarNameFiles;

    @Test
    public void whenS3ObjectDownloaded_thenSuccess() throws IOException {
        String s3Url = (("s3://" + (SpringCloudS3IntegrationTest.bucketName)) + "/") + (SpringCloudS3IntegrationTest.testFileToDownload);
        springCloudS3.downloadS3Object(s3Url);
        assertThat(new File(SpringCloudS3IntegrationTest.testFileToDownload)).exists();
    }

    @Test
    public void whenS3ObjectUploaded_thenSuccess() throws IOException {
        String s3Url = (("s3://" + (SpringCloudS3IntegrationTest.bucketName)) + "/") + (SpringCloudS3IntegrationTest.testFileToUpload);
        File file = new File(SpringCloudS3IntegrationTest.testFileToUpload);
        springCloudS3.uploadFileToS3(file, s3Url);
    }

    @Test
    public void whenMultipleS3ObjectsDownloaded_thenSuccess() throws IOException {
        String s3Url = ("s3://" + (SpringCloudS3IntegrationTest.bucketName)) + "/**/hello-*.txt";
        springCloudS3.downloadMultipleS3Objects(s3Url);
        SpringCloudS3IntegrationTest.similarNameFiles.forEach(( f) -> assertThat(f).exists());
    }
}

