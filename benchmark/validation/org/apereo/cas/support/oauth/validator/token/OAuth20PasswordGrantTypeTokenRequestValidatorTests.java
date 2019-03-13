package org.apereo.cas.support.oauth.validator.token;


import Authenticators.CAS_OAUTH_CLIENT_BASIC_AUTHN;
import OAuth20Constants.CLIENT_ID;
import OAuth20Constants.GRANT_TYPE;
import Pac4jConstants.USER_PROFILES;
import lombok.val;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OAuth20PasswordGrantTypeTokenRequestValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class OAuth20PasswordGrantTypeTokenRequestValidatorTests {
    private OAuth20TokenRequestValidator validator;

    private OAuthRegisteredService supportingService;

    private OAuthRegisteredService nonSupportingService;

    private OAuthRegisteredService promiscuousService;

    @Test
    public void verifyOperation() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        request.setParameter(GRANT_TYPE, "unsupported");
        Assertions.assertFalse(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        val profile = new CommonProfile();
        profile.setClientName(CAS_OAUTH_CLIENT_BASIC_AUTHN);
        profile.setId(RequestValidatorTestUtils.SUPPORTING_CLIENT_ID);
        val session = request.getSession(true);
        session.setAttribute(USER_PROFILES, profile);
        request.setParameter(GRANT_TYPE, getGrantType().getType());
        request.setParameter(CLIENT_ID, supportingService.getClientId());
        Assertions.assertTrue(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.setParameter(CLIENT_ID, nonSupportingService.getClientId());
        profile.setId(RequestValidatorTestUtils.NON_SUPPORTING_CLIENT_ID);
        session.setAttribute(USER_PROFILES, profile);
        Assertions.assertFalse(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.setParameter(CLIENT_ID, promiscuousService.getClientId());
        profile.setId(RequestValidatorTestUtils.PROMISCUOUS_CLIENT_ID);
        session.setAttribute(USER_PROFILES, profile);
        Assertions.assertTrue(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
    }
}

