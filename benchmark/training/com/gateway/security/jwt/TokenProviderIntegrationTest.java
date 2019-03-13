package com.gateway.security.jwt;


import io.github.jhipster.config.JHipsterProperties;
import java.util.Date;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;


public class TokenProviderIntegrationTest {
    private final String secretKey = "e5c9ee274ae87bc031adda32e27fa98b9290da83";

    private final long ONE_MINUTE = 60000;

    private JHipsterProperties jHipsterProperties;

    private TokenProvider tokenProvider;

    @Test
    public void testReturnFalseWhenJWThasInvalidSignature() {
        boolean isTokenValid = tokenProvider.validateToken(createTokenWithDifferentSignature());
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisMalformed() {
        Authentication authentication = createAuthentication();
        String token = tokenProvider.createToken(authentication, false);
        String invalidToken = token.substring(1);
        boolean isTokenValid = tokenProvider.validateToken(invalidToken);
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisExpired() {
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", (-(ONE_MINUTE)));
        Authentication authentication = createAuthentication();
        String token = tokenProvider.createToken(authentication, false);
        boolean isTokenValid = tokenProvider.validateToken(token);
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisUnsupported() {
        Date expirationDate = new Date(((new Date().getTime()) + (ONE_MINUTE)));
        Authentication authentication = createAuthentication();
        String unsupportedToken = createUnsupportedToken();
        boolean isTokenValid = tokenProvider.validateToken(unsupportedToken);
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisInvalid() {
        boolean isTokenValid = tokenProvider.validateToken("");
        assertThat(isTokenValid).isEqualTo(false);
    }
}

