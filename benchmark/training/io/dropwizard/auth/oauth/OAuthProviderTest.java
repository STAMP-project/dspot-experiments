package io.dropwizard.auth.oauth;


import OAuthCredentialAuthFilter.OAUTH_ACCESS_TOKEN_PARAM;
import io.dropwizard.auth.AbstractAuthResourceConfig;
import io.dropwizard.auth.AuthBaseTest;
import io.dropwizard.auth.AuthResource;
import io.dropwizard.auth.util.AuthUtil;
import java.util.Arrays;
import javax.ws.rs.container.ContainerRequestFilter;
import org.junit.jupiter.api.Test;


public class OAuthProviderTest extends AuthBaseTest<OAuthProviderTest.OAuthTestResourceConfig> {
    public static class OAuthTestResourceConfig extends AbstractAuthResourceConfig {
        public OAuthTestResourceConfig() {
            register(AuthResource.class);
        }

        @Override
        protected ContainerRequestFilter getAuthFilter() {
            return new OAuthCredentialAuthFilter.Builder<>().setAuthenticator(AuthUtil.getMultiplyUsersOAuthAuthenticator(Arrays.asList(AuthBaseTest.ADMIN_USER, AuthBaseTest.ORDINARY_USER))).setAuthorizer(AuthUtil.getTestAuthorizer(AuthBaseTest.ADMIN_USER, AuthBaseTest.ADMIN_ROLE)).setPrefix(AuthBaseTest.BEARER_PREFIX).buildAuthFilter();
        }
    }

    @Test
    public void checksQueryStringAccessTokenIfAuthorizationHeaderMissing() {
        assertThat(target("/test/profile").queryParam(OAUTH_ACCESS_TOKEN_PARAM, getOrdinaryGuyValidToken()).request().get(String.class)).isEqualTo((("'" + (AuthBaseTest.ORDINARY_USER)) + "' has user privileges"));
    }
}

