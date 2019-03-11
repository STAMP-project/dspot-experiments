package com.baeldung.ftp;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;


public class FtpClientIntegrationTest {
    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;

    @Test
    public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() throws IOException {
        Collection<String> files = ftpClient.listFiles("");
        assertThat(files).contains("foobar.txt");
    }

    @Test
    public void givenRemoteFile_whenDownloading_thenItIsOnTheLocalFilesystem() throws IOException {
        ftpClient.downloadFile("/foobar.txt", "downloaded_buz.txt");
        assertThat(new File("downloaded_buz.txt")).exists();
        new File("downloaded_buz.txt").delete();// cleanup

    }

    @Test
    public void givenLocalFile_whenUploadingIt_thenItExistsOnRemoteLocation() throws IOException, URISyntaxException {
        File file = new File(getClass().getClassLoader().getResource("ftp/baz.txt").toURI());
        ftpClient.putFileToPath(file, "/buz.txt");
        assertThat(fakeFtpServer.getFileSystem().exists("/buz.txt")).isTrue();
    }
}

