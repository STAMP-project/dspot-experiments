package com.baeldung.download;


import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class FileDownloadIntegrationTest {
    static String FILE_URL = "http://ovh.net/files/1Mio.dat";

    static String FILE_NAME = "file.dat";

    static String FILE_MD5_HASH = "6cb91af4ed4c60c11613b75cd1fc6116";

    @Test
    public void givenJavaIO_whenDownloadingFile_thenDownloadShouldBeCorrect() throws IOException, NoSuchAlgorithmException {
        FileDownload.downloadWithJavaIO(FileDownloadIntegrationTest.FILE_URL, FileDownloadIntegrationTest.FILE_NAME);
        Assert.assertTrue(checkMd5Hash(FileDownloadIntegrationTest.FILE_NAME));
    }

    @Test
    public void givenJavaNIO_whenDownloadingFile_thenDownloadShouldBeCorrect() throws IOException, NoSuchAlgorithmException {
        FileDownload.downloadWithJavaNIO(FileDownloadIntegrationTest.FILE_URL, FileDownloadIntegrationTest.FILE_NAME);
        Assert.assertTrue(checkMd5Hash(FileDownloadIntegrationTest.FILE_NAME));
    }

    @Test
    public void givenJava7IO_whenDownloadingFile_thenDownloadShouldBeCorrect() throws IOException, NoSuchAlgorithmException {
        FileDownload.downloadWithJava7IO(FileDownloadIntegrationTest.FILE_URL, FileDownloadIntegrationTest.FILE_NAME);
        Assert.assertTrue(checkMd5Hash(FileDownloadIntegrationTest.FILE_NAME));
    }

    @Test
    public void givenAHCLibrary_whenDownloadingFile_thenDownloadShouldBeCorrect() throws IOException, InterruptedException, NoSuchAlgorithmException, ExecutionException {
        FileDownload.downloadWithAHC(FileDownloadIntegrationTest.FILE_URL, FileDownloadIntegrationTest.FILE_NAME);
        Assert.assertTrue(checkMd5Hash(FileDownloadIntegrationTest.FILE_NAME));
    }

    @Test
    public void givenApacheCommonsIO_whenDownloadingFile_thenDownloadShouldBeCorrect() throws IOException, NoSuchAlgorithmException {
        FileDownload.downloadWithApacheCommons(FileDownloadIntegrationTest.FILE_URL, FileDownloadIntegrationTest.FILE_NAME);
        Assert.assertTrue(checkMd5Hash(FileDownloadIntegrationTest.FILE_NAME));
    }

    @Test
    public void givenJavaIO_whenDownloadingFileStops_thenDownloadShouldBeResumedCorrectly() throws IOException, URISyntaxException, NoSuchAlgorithmException {
        ResumableDownload.downloadFileWithResume(FileDownloadIntegrationTest.FILE_URL, FileDownloadIntegrationTest.FILE_NAME);
        Assert.assertTrue(checkMd5Hash(FileDownloadIntegrationTest.FILE_NAME));
    }
}

