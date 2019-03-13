package org.whispersystems.textsecuregcm.tests.controllers;


import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.configuration.ProfilesConfiguration;
import org.whispersystems.textsecuregcm.controllers.RateLimitExceededException;
import org.whispersystems.textsecuregcm.entities.Profile;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.SystemMapper;


public class ProfileControllerTest {
    private static AccountsManager accountsManager = Mockito.mock(AccountsManager.class);

    private static RateLimiters rateLimiters = Mockito.mock(RateLimiters.class);

    private static RateLimiter rateLimiter = Mockito.mock(RateLimiter.class);

    private static ProfilesConfiguration configuration = Mockito.mock(ProfilesConfiguration.class);

    static {
        Mockito.when(ProfileControllerTest.configuration.getAccessKey()).thenReturn("accessKey");
        Mockito.when(ProfileControllerTest.configuration.getAccessSecret()).thenReturn("accessSecret");
        Mockito.when(ProfileControllerTest.configuration.getRegion()).thenReturn("us-east-1");
        Mockito.when(ProfileControllerTest.configuration.getBucket()).thenReturn("profile-bucket");
    }

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setMapper(SystemMapper.getMapper()).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.ProfileController(ProfileControllerTest.rateLimiters, ProfileControllerTest.accountsManager, ProfileControllerTest.configuration)).build();

    @Test
    public void testProfileGet() throws RateLimitExceededException {
        Profile profile = ProfileControllerTest.resources.getJerseyTest().target(("/v1/profile/" + (AuthHelper.VALID_NUMBER_TWO))).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(Profile.class);
        assertThat(profile.getIdentityKey()).isEqualTo("bar");
        assertThat(profile.getName()).isEqualTo("baz");
        assertThat(profile.getAvatar()).isEqualTo("profiles/bang");
        Mockito.verify(ProfileControllerTest.accountsManager, Mockito.times(1)).get(AuthHelper.VALID_NUMBER_TWO);
        Mockito.verify(ProfileControllerTest.rateLimiters, Mockito.times(1)).getProfileLimiter();
        Mockito.verify(ProfileControllerTest.rateLimiter, Mockito.times(1)).validate(AuthHelper.VALID_NUMBER);
    }

    @Test
    public void testProfileGetUnauthorized() throws Exception {
        Response response = ProfileControllerTest.resources.getJerseyTest().target(("/v1/profile/" + (AuthHelper.VALID_NUMBER_TWO))).request().get();
        assertThat(response.getStatus()).isEqualTo(401);
    }
}

