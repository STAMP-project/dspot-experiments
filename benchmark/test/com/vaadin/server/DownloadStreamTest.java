package com.vaadin.server;


import DownloadStream.CONTENT_DISPOSITION;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DownloadStreamTest {
    private String filename = "A ??.png";

    private String encodedFileName = "A" + ((("%20"// space
     + "%c3%a5")// ?
     + "%e6%97%a5")// ?
     + ".png");

    private DownloadStream stream;

    @Test
    public void contentDispositionFilenameIsUtf8Encoded() throws IOException {
        VaadinResponse response = Mockito.mock(VaadinResponse.class);
        stream.writeResponse(Mockito.mock(VaadinRequest.class), response);
        Mockito.verify(response).setHeader(ArgumentMatchers.eq(CONTENT_DISPOSITION), ArgumentMatchers.contains(String.format("filename=\"%s\";", encodedFileName)));
        Mockito.verify(response).setHeader(ArgumentMatchers.eq(CONTENT_DISPOSITION), ArgumentMatchers.contains(String.format("filename*=utf-8''%s", encodedFileName)));
    }
}

