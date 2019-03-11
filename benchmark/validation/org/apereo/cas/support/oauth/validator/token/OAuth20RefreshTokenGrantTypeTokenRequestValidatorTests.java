package org.apereo.cas.support.oauth.validator.token;


import Authenticators.CAS_OAUTH_CLIENT_BASIC_AUTHN;
import OAuth20Constants.CLIENT_ID;
import OAuth20Constants.CLIENT_SECRET;
import OAuth20Constants.GRANT_TYPE;
import OAuth20GrantTypes.REFRESH_TOKEN;
import Pac4jConstants.USER_PROFILES;
import lombok.val;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OAuth20RefreshTokenGrantTypeTokenRequestValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class OAuth20RefreshTokenGrantTypeTokenRequestValidatorTests {
    private static final String SUPPORTING_SERVICE_TICKET = "RT-SUPPORTING";

    private static final String NON_SUPPORTING_SERVICE_TICKET = "RT-NON-SUPPORTING";

    private static final String PROMISCUOUS_SERVICE_TICKET = "RT-PROMISCUOUS";

    private TicketRegistry ticketRegistry;

    private OAuth20TokenRequestValidator validator;

    @Test
    public void verifyOperation() {
        val request = new MockHttpServletRequest();
        val profile = new CommonProfile();
        profile.setClientName(CAS_OAUTH_CLIENT_BASIC_AUTHN);
        profile.setId(RequestValidatorTestUtils.SUPPORTING_CLIENT_ID);
        val session = request.getSession(true);
        session.setAttribute(USER_PROFILES, profile);
        val response = new MockHttpServletResponse();
        request.setParameter(GRANT_TYPE, REFRESH_TOKEN.getType());
        request.setParameter(CLIENT_ID, RequestValidatorTestUtils.SUPPORTING_CLIENT_ID);
        request.setParameter(CLIENT_SECRET, RequestValidatorTestUtils.SHARED_SECRET);
        request.setParameter(OAuth20Constants.REFRESH_TOKEN, OAuth20RefreshTokenGrantTypeTokenRequestValidatorTests.SUPPORTING_SERVICE_TICKET);
        Assertions.assertTrue(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        profile.setId(RequestValidatorTestUtils.NON_SUPPORTING_CLIENT_ID);
        session.setAttribute(USER_PROFILES, profile);
        request.setParameter(CLIENT_ID, RequestValidatorTestUtils.NON_SUPPORTING_CLIENT_ID);
        request.setParameter(CLIENT_SECRET, RequestValidatorTestUtils.SHARED_SECRET);
        request.setParameter(OAuth20Constants.REFRESH_TOKEN, OAuth20RefreshTokenGrantTypeTokenRequestValidatorTests.NON_SUPPORTING_SERVICE_TICKET);
        Assertions.assertFalse(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
        profile.setId(RequestValidatorTestUtils.PROMISCUOUS_CLIENT_ID);
        session.setAttribute(USER_PROFILES, profile);
        request.setParameter(CLIENT_ID, RequestValidatorTestUtils.PROMISCUOUS_CLIENT_ID);
        request.setParameter(CLIENT_SECRET, RequestValidatorTestUtils.SHARED_SECRET);
        request.setParameter(OAuth20Constants.REFRESH_TOKEN, OAuth20RefreshTokenGrantTypeTokenRequestValidatorTests.PROMISCUOUS_SERVICE_TICKET);
        Assertions.assertTrue(this.validator.validate(new org.pac4j.core.context.J2EContext(request, response)));
    }
}

