package io.dropwizard.auth.principal;


import HttpHeaders.AUTHORIZATION;
import io.dropwizard.auth.AbstractAuthResourceConfig;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.util.Maps;
import io.dropwizard.util.Sets;
import java.security.Principal;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;


/**
 * Testing that polymorphic principal entity injection works.
 */
public class PolymorphicPrincipalEntityTest extends JerseyTest {
    private static final String JSON_USERNAME = "good-guy";

    private static final String NULL_USERNAME = "bad-guy";

    private static final String JSON_USERNAME_ENCODED_TOKEN = "Z29vZC1ndXk6c2VjcmV0";

    private static final String NULL_USERNAME_ENCODED_TOKEN = "YmFkLWd1eTpzZWNyZXQ=";

    static {
        BootstrapLogging.bootstrap();
    }

    public static class PolymorphicPrincipalInjectedResourceConfig extends AbstractAuthResourceConfig {
        public PolymorphicPrincipalInjectedResourceConfig() {
            register(PolymorphicPrincipalEntityResource.class);
            packages("io.dropwizard.jersey.jackson");
        }

        @Override
        protected Class<? extends Principal> getPrincipalClass() {
            throw new AssertionError("getPrincipalClass must not be invoked");
        }

        @Override
        protected ContainerRequestFilter getAuthFilter() {
            return ( requestContext) -> {
                throw new AssertionError("getAuthFilter result must not be invoked");
            };
        }

        @Override
        protected AbstractBinder getAuthBinder() {
            return new PolymorphicAuthValueFactoryProvider.Binder<>(Sets.of(.class, .class));
        }

        @Override
        protected DynamicFeature getAuthDynamicFeature(ContainerRequestFilter authFilter) {
            final Authenticator<BasicCredentials, JsonPrincipal> jsonAuthenticator = ( credentials) -> {
                if (credentials.getUsername().equals(io.dropwizard.auth.principal.JSON_USERNAME)) {
                    return Optional.of(new JsonPrincipal(credentials.getUsername()));
                } else {
                    return Optional.empty();
                }
            };
            final Authenticator<BasicCredentials, NullPrincipal> nullAuthenticator = ( credentials) -> {
                if (credentials.getUsername().equals(io.dropwizard.auth.principal.NULL_USERNAME)) {
                    return Optional.of(new NullPrincipal());
                } else {
                    return Optional.empty();
                }
            };
            final BasicCredentialAuthFilter<?> jsonAuthFilter = new BasicCredentialAuthFilter.Builder<JsonPrincipal>().setAuthenticator(jsonAuthenticator).buildAuthFilter();
            final BasicCredentialAuthFilter<?> nullAuthFilter = new BasicCredentialAuthFilter.Builder<NullPrincipal>().setAuthenticator(nullAuthenticator).buildAuthFilter();
            return new io.dropwizard.auth.PolymorphicAuthDynamicFeature(Maps.of(JsonPrincipal.class, jsonAuthFilter, NullPrincipal.class, nullAuthFilter));
        }
    }

    @Test
    public void jsonPrincipalEntityResourceAuth200() {
        assertThat(target("/auth-test/json-principal-entity").request().header(AUTHORIZATION, ("Basic " + (PolymorphicPrincipalEntityTest.JSON_USERNAME_ENCODED_TOKEN))).get(String.class)).isEqualTo(PolymorphicPrincipalEntityTest.JSON_USERNAME);
    }

    @Test
    public void jsonPrincipalEntityResourceNoAuth401() {
        try {
            target("/auth-test/json-principal-entity").request().get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
        }
    }

    @Test
    public void nullPrincipalEntityResourceAuth200() {
        assertThat(target("/auth-test/null-principal-entity").request().header(AUTHORIZATION, ("Basic " + (PolymorphicPrincipalEntityTest.NULL_USERNAME_ENCODED_TOKEN))).get(String.class)).isEqualTo("null");
    }

    @Test
    public void nullPrincipalEntityResourceNoAuth401() {
        try {
            target("/auth-test/null-principal-entity").request().get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
        }
    }

    @Test
    public void resourceWithValidOptionalAuthentication200() {
        assertThat(target("/auth-test/optional").request().header(AUTHORIZATION, ("Basic " + (PolymorphicPrincipalEntityTest.NULL_USERNAME_ENCODED_TOKEN))).get(String.class)).isEqualTo("principal was present");
    }

    @Test
    public void resourceWithInvalidOptionalAuthentication200() {
        assertThat(target("/auth-test/optional").request().header(AUTHORIZATION, "Basic cats").get(String.class)).isEqualTo("principal was not present");
    }

    @Test
    public void resourceWithoutOptionalAuthentication200() {
        assertThat(target("/auth-test/optional").request().get(String.class)).isEqualTo("principal was not present");
    }

    @Test
    public void resourceWithDifferentOptionalAuthentication200() {
        assertThat(target("/auth-test/optional").request().header(AUTHORIZATION, ("Basic " + (PolymorphicPrincipalEntityTest.JSON_USERNAME_ENCODED_TOKEN))).get(String.class)).isEqualTo("principal was not present");
    }
}

