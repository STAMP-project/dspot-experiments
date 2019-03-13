package org.whispersystems.textsecuregcm.tests.controllers;


import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.net.MalformedURLException;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.configuration.AttachmentsConfiguration;
import org.whispersystems.textsecuregcm.entities.AttachmentDescriptor;
import org.whispersystems.textsecuregcm.entities.AttachmentUri;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.s3.UrlSigner;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.SystemMapper;


public class AttachmentControllerTest {
    private static AttachmentsConfiguration configuration = Mockito.mock(AttachmentsConfiguration.class);

    private static RateLimiters rateLimiters = Mockito.mock(RateLimiters.class);

    private static RateLimiter rateLimiter = Mockito.mock(RateLimiter.class);

    private static UrlSigner urlSigner;

    static {
        Mockito.when(AttachmentControllerTest.configuration.getAccessKey()).thenReturn("accessKey");
        Mockito.when(AttachmentControllerTest.configuration.getAccessSecret()).thenReturn("accessSecret");
        Mockito.when(AttachmentControllerTest.configuration.getBucket()).thenReturn("attachment-bucket");
        Mockito.when(AttachmentControllerTest.rateLimiters.getAttachmentLimiter()).thenReturn(AttachmentControllerTest.rateLimiter);
        AttachmentControllerTest.urlSigner = new UrlSigner(AttachmentControllerTest.configuration);
    }

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setMapper(SystemMapper.getMapper()).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.AttachmentController(AttachmentControllerTest.rateLimiters, AttachmentControllerTest.urlSigner)).build();

    @Test
    public void testAcceleratedPut() {
        AttachmentDescriptor descriptor = AttachmentControllerTest.resources.getJerseyTest().target("/v1/attachments/").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(AttachmentDescriptor.class);
        assertThat(descriptor.getLocation()).startsWith("https://attachment-bucket.s3-accelerate.amazonaws.com");
        assertThat(descriptor.getId()).isGreaterThan(0);
        assertThat(descriptor.getIdString()).isNotBlank();
    }

    @Test
    public void testUnacceleratedPut() {
        AttachmentDescriptor descriptor = AttachmentControllerTest.resources.getJerseyTest().target("/v1/attachments/").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER_TWO, AuthHelper.VALID_PASSWORD_TWO)).get(AttachmentDescriptor.class);
        assertThat(descriptor.getLocation()).startsWith("https://s3.amazonaws.com");
        assertThat(descriptor.getId()).isGreaterThan(0);
        assertThat(descriptor.getIdString()).isNotBlank();
    }

    @Test
    public void testAcceleratedGet() throws MalformedURLException {
        AttachmentUri uri = AttachmentControllerTest.resources.getJerseyTest().target("/v1/attachments/1234").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(AttachmentUri.class);
        assertThat(uri.getLocation().getHost()).isEqualTo("attachment-bucket.s3-accelerate.amazonaws.com");
    }

    @Test
    public void testUnacceleratedGet() throws MalformedURLException {
        AttachmentUri uri = AttachmentControllerTest.resources.getJerseyTest().target("/v1/attachments/1234").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER_TWO, AuthHelper.VALID_PASSWORD_TWO)).get(AttachmentUri.class);
        assertThat(uri.getLocation().getHost()).isEqualTo("s3.amazonaws.com");
    }
}

