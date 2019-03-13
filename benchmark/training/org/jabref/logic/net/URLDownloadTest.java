package org.jabref.logic.net;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.jabref.support.DisabledOnCIServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class URLDownloadTest {
    @Test
    public void testStringDownloadWithSetEncoding() throws IOException {
        URLDownload dl = new URLDownload(new URL("http://www.google.com"));
        Assertions.assertTrue(dl.asString().contains("Google"), "google.com should contain google");
    }

    @Test
    public void testStringDownload() throws IOException {
        URLDownload dl = new URLDownload(new URL("http://www.google.com"));
        Assertions.assertTrue(dl.asString(StandardCharsets.UTF_8).contains("Google"), "google.com should contain google");
    }

    @Test
    public void testFileDownload() throws IOException {
        File destination = File.createTempFile("jabref-test", ".html");
        try {
            URLDownload dl = new URLDownload(new URL("http://www.google.com"));
            dl.toFile(destination.toPath());
            Assertions.assertTrue(destination.exists(), "file must exist");
        } finally {
            // cleanup
            if (!(destination.delete())) {
                System.err.println("Cannot delete downloaded file");
            }
        }
    }

    @Test
    public void testDetermineMimeType() throws IOException {
        URLDownload dl = new URLDownload(new URL("http://www.google.com"));
        Assertions.assertTrue(dl.getMimeType().startsWith("text/html"));
    }

    @Test
    public void downloadToTemporaryFilePathWithoutFileSavesAsTmpFile() throws IOException {
        URLDownload google = new URLDownload(new URL("http://www.google.com"));
        String path = google.toTemporaryFile().toString();
        Assertions.assertTrue(path.endsWith(".tmp"), path);
    }

    @Test
    public void downloadToTemporaryFileKeepsName() throws IOException {
        URLDownload google = new URLDownload(new URL("https://github.com/JabRef/jabref/blob/master/LICENSE.md"));
        String path = google.toTemporaryFile().toString();
        Assertions.assertTrue(((path.contains("LICENSE")) && (path.endsWith(".md"))), path);
    }

    @Test
    @DisabledOnCIServer("CI Server is apparently blocked")
    public void downloadOfFTPSucceeds() throws IOException {
        URLDownload ftp = new URLDownload(new URL("ftp://ftp.informatik.uni-stuttgart.de/pub/library/ncstrl.ustuttgart_fi/INPROC-2016-15/INPROC-2016-15.pdf"));
        Path path = ftp.toTemporaryFile();
        Assertions.assertNotNull(path);
    }

    @Test
    public void downloadOfHttpSucceeds() throws IOException {
        URLDownload ftp = new URLDownload(new URL("http://www.jabref.org"));
        Path path = ftp.toTemporaryFile();
        Assertions.assertNotNull(path);
    }

    @Test
    public void downloadOfHttpsSucceeds() throws IOException {
        URLDownload ftp = new URLDownload(new URL("https://www.jabref.org"));
        Path path = ftp.toTemporaryFile();
        Assertions.assertNotNull(path);
    }
}

