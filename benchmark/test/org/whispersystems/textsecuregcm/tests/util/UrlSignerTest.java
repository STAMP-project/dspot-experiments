package org.whispersystems.textsecuregcm.tests.util;


import HttpMethod.GET;
import java.net.URL;
import org.junit.Test;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.configuration.AttachmentsConfiguration;
import org.whispersystems.textsecuregcm.s3.UrlSigner;


public class UrlSignerTest {
    @Test
    public void testTransferAcceleration() {
        AttachmentsConfiguration configuration = Mockito.mock(AttachmentsConfiguration.class);
        Mockito.when(configuration.getAccessKey()).thenReturn("foo");
        Mockito.when(configuration.getAccessSecret()).thenReturn("bar");
        Mockito.when(configuration.getBucket()).thenReturn("attachments-test");
        UrlSigner signer = new UrlSigner(configuration);
        URL url = signer.getPreSignedUrl(1234, GET, false);
        assertThat(url).hasHost("attachments-test.s3-accelerate.amazonaws.com");
    }

    @Test
    public void testTransferUnaccelerated() {
        AttachmentsConfiguration configuration = Mockito.mock(AttachmentsConfiguration.class);
        Mockito.when(configuration.getAccessKey()).thenReturn("foo");
        Mockito.when(configuration.getAccessSecret()).thenReturn("bar");
        Mockito.when(configuration.getBucket()).thenReturn("attachments-test");
        UrlSigner signer = new UrlSigner(configuration);
        URL url = signer.getPreSignedUrl(1234, GET, true);
        assertThat(url).hasHost("s3.amazonaws.com");
    }
}

