package org.whispersystems.textsecuregcm.tests.controllers;


import Family.SUCCESSFUL;
import MediaType.APPLICATION_JSON_TYPE;
import Status.NOT_FOUND;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.auth.DirectoryCredentials;
import org.whispersystems.textsecuregcm.auth.DirectoryCredentialsGenerator;
import org.whispersystems.textsecuregcm.entities.ClientContactTokens;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.DirectoryManager;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.Base64;


public class DirectoryControllerTest {
    private final RateLimiters rateLimiters = Mockito.mock(RateLimiters.class);

    private final RateLimiter rateLimiter = Mockito.mock(RateLimiter.class);

    private final DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);

    private final DirectoryCredentialsGenerator directoryCredentialsGenerator = Mockito.mock(DirectoryCredentialsGenerator.class);

    private final DirectoryCredentials validCredentials = new DirectoryCredentials("username", "password");

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.DirectoryController(rateLimiters, directoryManager, directoryCredentialsGenerator)).build();

    @Test
    public void testFeedbackOk() {
        Response response = resources.getJerseyTest().target("/v1/directory/feedback/ok").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.text(""));
        assertThat(response.getStatusInfo().getFamily()).isEqualTo(SUCCESSFUL);
    }

    @Test
    public void testNotFoundFeedback() {
        Response response = resources.getJerseyTest().target("/v1/directory/feedback/test-not-found").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.text(""));
        assertThat(response.getStatusInfo()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void testGetAuthToken() {
        DirectoryCredentials token = resources.getJerseyTest().target("/v1/directory/auth").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(DirectoryCredentials.class);
        assertThat(token.getUsername()).isEqualTo(validCredentials.getUsername());
        assertThat(token.getPassword()).isEqualTo(validCredentials.getPassword());
    }

    @Test
    public void testContactIntersection() throws Exception {
        List<String> tokens = new LinkedList<String>() {
            {
                add(Base64.encodeBytes("foo".getBytes()));
                add(Base64.encodeBytes("bar".getBytes()));
                add(Base64.encodeBytes("baz".getBytes()));
            }
        };
        List<String> expectedResponse = new LinkedList<>(tokens);
        expectedResponse.remove(0);
        Response response = resources.getJerseyTest().target("/v1/directory/tokens/").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(new ClientContactTokens(tokens), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(ClientContactTokens.class).getContacts()).isEqualTo(expectedResponse);
    }
}

