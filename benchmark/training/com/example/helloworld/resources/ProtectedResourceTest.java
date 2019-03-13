package com.example.helloworld.resources;


import HttpHeaders.AUTHORIZATION;
import HttpHeaders.WWW_AUTHENTICATE;
import com.example.helloworld.auth.ExampleAuthenticator;
import com.example.helloworld.auth.ExampleAuthorizer;
import com.example.helloworld.core.User;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class ProtectedResourceTest {
    private static final BasicCredentialAuthFilter<User> BASIC_AUTH_HANDLER = new BasicCredentialAuthFilter.Builder<User>().setAuthenticator(new ExampleAuthenticator()).setAuthorizer(new ExampleAuthorizer()).setPrefix("Basic").setRealm("SUPER SECRET STUFF").buildAuthFilter();

    public static final ResourceExtension RULE = ResourceExtension.builder().addProvider(RolesAllowedDynamicFeature.class).addProvider(new io.dropwizard.auth.AuthDynamicFeature(ProtectedResourceTest.BASIC_AUTH_HANDLER)).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addProvider(ProtectedResource.class).build();

    @Test
    public void testProtectedEndpoint() {
        String secret = ProtectedResourceTest.RULE.target("/protected").request().header(AUTHORIZATION, "Basic Z29vZC1ndXk6c2VjcmV0").get(String.class);
        assertThat(secret).startsWith("Hey there, good-guy. You know the secret!");
    }

    @Test
    public void testProtectedEndpointNoCredentials401() {
        try {
            ProtectedResourceTest.RULE.target("/protected").request().get(String.class);
            failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
        } catch (NotAuthorizedException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
            assertThat(e.getResponse().getHeaders().get(WWW_AUTHENTICATE)).containsOnly("Basic realm=\"SUPER SECRET STUFF\"");
        }
    }

    @Test
    public void testProtectedEndpointBadCredentials401() {
        try {
            ProtectedResourceTest.RULE.target("/protected").request().header(AUTHORIZATION, "Basic c25lYWt5LWJhc3RhcmQ6YXNkZg==").get(String.class);
            failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
        } catch (NotAuthorizedException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
            assertThat(e.getResponse().getHeaders().get(WWW_AUTHENTICATE)).containsOnly("Basic realm=\"SUPER SECRET STUFF\"");
        }
    }

    @Test
    public void testProtectedAdminEndpoint() {
        String secret = ProtectedResourceTest.RULE.target("/protected/admin").request().header(AUTHORIZATION, "Basic Y2hpZWYtd2l6YXJkOnNlY3JldA==").get(String.class);
        assertThat(secret).startsWith("Hey there, chief-wizard. It looks like you are an admin.");
    }

    @Test
    public void testProtectedAdminEndpointPrincipalIsNotAuthorized403() {
        try {
            ProtectedResourceTest.RULE.target("/protected/admin").request().header(AUTHORIZATION, "Basic Z29vZC1ndXk6c2VjcmV0").get(String.class);
            failBecauseExceptionWasNotThrown(ForbiddenException.class);
        } catch (ForbiddenException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(403);
        }
    }
}

