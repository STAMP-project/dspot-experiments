package org.apereo.cas.support.oauth.validator.token;


import Authenticators.CAS_OAUTH_CLIENT_BASIC_AUTHN;
import OAuth20Constants.CODE;
import OAuth20Constants.GRANT_TYPE;
import OAuth20Constants.REDIRECT_URI;
import OAuth20GrantTypes.AUTHORIZATION_CODE;
import OAuth20GrantTypes.PASSWORD;
import Pac4jConstants.USER_PROFILES;
import RegisteredServiceTestUtils.CONST_TEST_URL;
import RegisteredServiceTestUtils.CONST_TEST_URL2;
import RegisteredServiceTestUtils.CONST_TEST_URL3;
import lombok.val;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OAuth20AuthorizationCodeGrantTypeTokenRequestValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class OAuth20AuthorizationCodeGrantTypeTokenRequestValidatorTests {
    private static final String SUPPORTING_SERVICE_TICKET = "OC-SUPPORTING";

    private static final String NON_SUPPORTING_SERVICE_TICKET = "OC-NON-SUPPORTING";

    private static final String PROMISCUOUS_SERVICE_TICKET = "OC-PROMISCUOUS";

    private OAuth20TokenRequestValidator validator;

    private TicketRegistry ticketRegistry;

    @Test
    public void verifyOperation() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val profile = new CommonProfile();
        profile.setClientName(CAS_OAUTH_CLIENT_BASIC_AUTHN);
        profile.setId(RequestValidatorTestUtils.SUPPORTING_CLIENT_ID);
        val session = request.getSession(true);
        session.setAttribute(USER_PROFILES, profile);
        request.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.getType());
        request.setParameter(REDIRECT_URI, CONST_TEST_URL);
        request.setParameter(CODE, OAuth20AuthorizationCodeGrantTypeTokenRequestValidatorTests.SUPPORTING_SERVICE_TICKET);
        Assertions.assertTrue(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.setParameter(GRANT_TYPE, "unsupported");
        Assertions.assertFalse(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.setParameter(GRANT_TYPE, PASSWORD.getType());
        Assertions.assertFalse(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.setParameter(GRANT_TYPE, AUTHORIZATION_CODE.getType());
        request.setParameter(CODE, OAuth20AuthorizationCodeGrantTypeTokenRequestValidatorTests.NON_SUPPORTING_SERVICE_TICKET);
        request.setParameter(REDIRECT_URI, CONST_TEST_URL2);
        profile.setId(RequestValidatorTestUtils.NON_SUPPORTING_CLIENT_ID);
        session.setAttribute(USER_PROFILES, profile);
        Assertions.assertFalse(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        request.setParameter(CODE, OAuth20AuthorizationCodeGrantTypeTokenRequestValidatorTests.PROMISCUOUS_SERVICE_TICKET);
        profile.setId(RequestValidatorTestUtils.PROMISCUOUS_CLIENT_ID);
        request.setParameter(REDIRECT_URI, CONST_TEST_URL3);
        session.setAttribute(USER_PROFILES, profile);
        Assertions.assertTrue(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
    }
}

