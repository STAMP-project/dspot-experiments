package io.dropwizard.auth.chained;


import HttpHeaders.AUTHORIZATION;
import io.dropwizard.auth.AuthBaseTest;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthResource;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.auth.util.AuthUtil;
import io.dropwizard.jersey.DropwizardResourceConfig;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.junit.jupiter.api.Test;


public class ChainedAuthProviderTest extends AuthBaseTest<ChainedAuthProviderTest.ChainedAuthTestResourceConfig> {
    private static final String BEARER_USER = "A12B3C4D";

    public static class ChainedAuthTestResourceConfig extends DropwizardResourceConfig {
        public ChainedAuthTestResourceConfig() {
            super();
            final Authorizer<Principal> authorizer = AuthUtil.getTestAuthorizer(AuthBaseTest.ADMIN_USER, AuthBaseTest.ADMIN_ROLE);
            final AuthFilter<BasicCredentials, Principal> basicAuthFilter = new BasicCredentialAuthFilter.Builder<>().setAuthenticator(AuthUtil.getBasicAuthenticator(Arrays.asList(AuthBaseTest.ADMIN_USER, AuthBaseTest.ORDINARY_USER))).setAuthorizer(authorizer).buildAuthFilter();
            final AuthFilter<String, Principal> oAuthFilter = new OAuthCredentialAuthFilter.Builder<>().setAuthenticator(AuthUtil.getSingleUserOAuthAuthenticator(ChainedAuthProviderTest.BEARER_USER, AuthBaseTest.ADMIN_USER)).setPrefix(AuthBaseTest.BEARER_PREFIX).setAuthorizer(authorizer).buildAuthFilter();
            property(TestProperties.CONTAINER_PORT, "0");
            register(new AuthValueFactoryProvider.Binder<>(.class));
            register(new io.dropwizard.auth.AuthDynamicFeature(new ChainedAuthFilter(buildHandlerList(basicAuthFilter, oAuthFilter))));
            register(RolesAllowedDynamicFeature.class);
            register(AuthResource.class);
        }

        @SuppressWarnings("rawtypes")
        public List<AuthFilter> buildHandlerList(AuthFilter<BasicCredentials, Principal> basicAuthFilter, AuthFilter<String, Principal> oAuthFilter) {
            return Arrays.asList(basicAuthFilter, oAuthFilter);
        }
    }

    @Test
    public void transformsBearerCredentialsToPrincipals() throws Exception {
        assertThat(target("/test/admin").request().header(AUTHORIZATION, (((AuthBaseTest.BEARER_PREFIX) + " ") + (ChainedAuthProviderTest.BEARER_USER))).get(String.class)).isEqualTo((("'" + (AuthBaseTest.ADMIN_USER)) + "' has admin privileges"));
    }
}

