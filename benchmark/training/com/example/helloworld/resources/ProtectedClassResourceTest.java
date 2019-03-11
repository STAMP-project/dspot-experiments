package com.example.helloworld.resources;


import HttpHeaders.AUTHORIZATION;
import com.example.helloworld.auth.ExampleAuthenticator;
import com.example.helloworld.auth.ExampleAuthorizer;
import com.example.helloworld.core.User;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import javax.ws.rs.ForbiddenException;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public final class ProtectedClassResourceTest {
    private static final BasicCredentialAuthFilter<User> BASIC_AUTH_HANDLER = new BasicCredentialAuthFilter.Builder<User>().setAuthenticator(new ExampleAuthenticator()).setAuthorizer(new ExampleAuthorizer()).setPrefix("Basic").setRealm("SUPER SECRET STUFF").buildAuthFilter();

    public static final ResourceExtension RULE = ResourceExtension.builder().addProvider(RolesAllowedDynamicFeature.class).addProvider(new io.dropwizard.auth.AuthDynamicFeature(ProtectedClassResourceTest.BASIC_AUTH_HANDLER)).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addProvider(ProtectedClassResource.class).build();

    @Test
    public void testProtectedAdminEndpoint() {
        String secret = ProtectedClassResourceTest.RULE.target("/protected/admin").request().header(AUTHORIZATION, "Basic Y2hpZWYtd2l6YXJkOnNlY3JldA==").get(String.class);
        assertThat(secret).startsWith("Hey there, chief-wizard. It looks like you are an admin.");
    }

    @Test
    public void testProtectedBasicUserEndpoint() {
        String secret = ProtectedClassResourceTest.RULE.target("/protected").request().header(AUTHORIZATION, "Basic Z29vZC1ndXk6c2VjcmV0").get(String.class);
        assertThat(secret).startsWith("Hey there, good-guy. You seem to be a basic user.");
    }

    @Test
    public void testProtectedBasicUserEndpointAsAdmin() {
        String secret = ProtectedClassResourceTest.RULE.target("/protected").request().header(AUTHORIZATION, "Basic Y2hpZWYtd2l6YXJkOnNlY3JldA==").get(String.class);
        assertThat(secret).startsWith("Hey there, chief-wizard. You seem to be a basic user.");
    }

    @Test
    public void testProtectedGuestEndpoint() {
        String secret = ProtectedClassResourceTest.RULE.target("/protected/guest").request().header(AUTHORIZATION, "Basic Z3Vlc3Q6c2VjcmV0").get(String.class);
        assertThat(secret).startsWith("Hey there, guest. You know the secret!");
    }

    @Test
    public void testProtectedBasicUserEndpointPrincipalIsNotAuthorized403() {
        try {
            ProtectedClassResourceTest.RULE.target("/protected").request().header(AUTHORIZATION, "Basic Z3Vlc3Q6c2VjcmV0").get(String.class);
            failBecauseExceptionWasNotThrown(ForbiddenException.class);
        } catch (ForbiddenException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(403);
        }
    }
}

