package io.dropwizard.auth;


import HttpHeaders.AUTHORIZATION;
import HttpHeaders.WWW_AUTHENTICATE;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.BootstrapLogging;
import javax.ws.rs.WebApplicationException;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;


public abstract class AuthBaseTest<T extends DropwizardResourceConfig> extends JerseyTest {
    protected static final String ADMIN_ROLE = "ADMIN";

    protected static final String ADMIN_USER = "good-guy";

    protected static final String ORDINARY_USER = "ordinary-guy";

    protected static final String BADGUY_USER = "bad-guy";

    protected static final String CUSTOM_PREFIX = "Custom";

    protected static final String BEARER_PREFIX = "Bearer";

    protected static final String BASIC_PREFIX = "Basic";

    protected static final String ORDINARY_USER_ENCODED_TOKEN = "b3JkaW5hcnktZ3V5OnNlY3JldA==";

    protected static final String GOOD_USER_ENCODED_TOKEN = "Z29vZC1ndXk6c2VjcmV0";

    protected static final String BAD_USER_ENCODED_TOKEN = "YmFkLWd1eTpzZWNyZXQ=";

    static {
        BootstrapLogging.bootstrap();
    }

    @Test
    public void respondsToMissingCredentialsWith401() throws Exception {
        try {
            AuthBaseTest.target("/test/admin").request().get(String.class);
            AuthBaseTest.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
            assertThat(e.getResponse().getHeaders().get(WWW_AUTHENTICATE)).containsOnly(((getPrefix()) + " realm=\"realm\""));
        }
    }

    @Test
    public void resourceWithoutAuth200() {
        assertThat(AuthBaseTest.target("/test/noauth").request().get(String.class)).isEqualTo("hello");
    }

    @Test
    public void resourceWithAuthenticationWithoutAuthorizationWithCorrectCredentials200() {
        assertThat(AuthBaseTest.target("/test/profile").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getOrdinaryGuyValidToken()))).get(String.class)).isEqualTo((("'" + (AuthBaseTest.ORDINARY_USER)) + "' has user privileges"));
    }

    @Test
    public void resourceWithAuthenticationWithoutAuthorizationNoCredentials401() {
        try {
            AuthBaseTest.target("/test/profile").request().get(String.class);
            AuthBaseTest.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
            assertThat(e.getResponse().getHeaders().get(WWW_AUTHENTICATE)).containsOnly(((getPrefix()) + " realm=\"realm\""));
        }
    }

    @Test
    public void resourceWithValidOptionalAuthentication200() {
        assertThat(AuthBaseTest.target("/test/optional").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getOrdinaryGuyValidToken()))).get(String.class)).isEqualTo("principal was present");
    }

    @Test
    public void resourceWithInvalidOptionalAuthentication200() {
        assertThat(AuthBaseTest.target("/test/optional").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getBadGuyToken()))).get(String.class)).isEqualTo("principal was not present");
    }

    @Test
    public void resourceWithoutOptionalAuthentication200() {
        assertThat(AuthBaseTest.target("/test/optional").request().get(String.class)).isEqualTo("principal was not present");
    }

    @Test
    public void resourceWithAuthorizationPrincipalIsNotAuthorized403() {
        try {
            AuthBaseTest.target("/test/admin").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getOrdinaryGuyValidToken()))).get(String.class);
            AuthBaseTest.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(403);
        }
    }

    @Test
    public void resourceWithDenyAllAndNoAuth401() {
        try {
            AuthBaseTest.target("/test/denied").request().get(String.class);
            AuthBaseTest.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
        }
    }

    @Test
    public void resourceWithDenyAllAndAuth403() {
        try {
            AuthBaseTest.target("/test/denied").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getGoodGuyValidToken()))).get(String.class);
            AuthBaseTest.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(403);
        }
    }

    @Test
    public void transformsCredentialsToPrincipals() throws Exception {
        assertThat(AuthBaseTest.target("/test/admin").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getGoodGuyValidToken()))).get(String.class)).isEqualTo((("'" + (AuthBaseTest.ADMIN_USER)) + "' has admin privileges"));
    }

    @Test
    public void transformsCredentialsToPrincipalsWhenAuthAnnotationExistsWithoutMethodAnnotation() throws Exception {
        assertThat(AuthBaseTest.target("/test/implicit-permitall").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getGoodGuyValidToken()))).get(String.class)).isEqualTo((("'" + (AuthBaseTest.ADMIN_USER)) + "' has user privileges"));
    }

    @Test
    public void respondsToNonBasicCredentialsWith401() throws Exception {
        try {
            AuthBaseTest.target("/test/admin").request().header(AUTHORIZATION, "Derp irrelevant").get(String.class);
            AuthBaseTest.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
            assertThat(e.getResponse().getHeaders().get(WWW_AUTHENTICATE)).containsOnly(((getPrefix()) + " realm=\"realm\""));
        }
    }

    @Test
    public void respondsToExceptionsWith500() throws Exception {
        try {
            AuthBaseTest.target("/test/admin").request().header(AUTHORIZATION, (((getPrefix()) + " ") + (getBadGuyToken()))).get(String.class);
            AuthBaseTest.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(500);
        }
    }
}

