package org.whispersystems.textsecuregcm.tests.controllers;


import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.FixtureHelpers;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.util.Arrays;
import java.util.HashSet;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;
import org.whispersystems.textsecuregcm.controllers.VoiceVerificationController;
import org.whispersystems.textsecuregcm.mappers.RateLimitExceededExceptionMapper;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.SystemMapper;


public class VoiceVerificationControllerTest {
    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).addProvider(new RateLimitExceededExceptionMapper()).setMapper(SystemMapper.getMapper()).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new VoiceVerificationController("https://foo.com/bar", new HashSet(Arrays.asList("pt-BR", "ru")))).build();

    @Test
    public void testTwimlLocale() {
        Response response = resources.getJerseyTest().target("/v1/voice/description/123456").queryParam("l", "pt-BR").request().post(null);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isXmlEqualTo(FixtureHelpers.fixture("fixtures/voice_verification_pt_br.xml"));
    }

    @Test
    public void testTwimlSplitLocale() {
        Response response = resources.getJerseyTest().target("/v1/voice/description/123456").queryParam("l", "ru-RU").request().post(null);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isXmlEqualTo(FixtureHelpers.fixture("fixtures/voice_verification_ru.xml"));
    }

    @Test
    public void testTwimlUnsupportedLocale() {
        Response response = resources.getJerseyTest().target("/v1/voice/description/123456").queryParam("l", "es-MX").request().post(null);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isXmlEqualTo(FixtureHelpers.fixture("fixtures/voice_verification_en_us.xml"));
    }

    @Test
    public void testTwimlMissingLocale() {
        Response response = resources.getJerseyTest().target("/v1/voice/description/123456").request().post(null);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isXmlEqualTo(FixtureHelpers.fixture("fixtures/voice_verification_en_us.xml"));
    }

    @Test
    public void testTwimlMalformedCode() {
        Response response = resources.getJerseyTest().target("/v1/voice/description/1234...56").request().post(null);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isXmlEqualTo(FixtureHelpers.fixture("fixtures/voice_verification_en_us.xml"));
    }

    @Test
    public void testTwimlBadCodeLength() {
        Response response = resources.getJerseyTest().target("/v1/voice/description/1234567").request().post(null);
        assertThat(response.getStatus()).isEqualTo(400);
    }
}

