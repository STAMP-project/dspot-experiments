package org.apereo.cas.support.oauth.web.audit;


import CasProtocolConstants.PARAMETER_SERVICE;
import OAuth20Constants.CLIENT_ID;
import OAuth20UserProfileViewRenderer.MODEL_ATTRIBUTE_ID;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.util.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * This is {@link OAuth20UserProfileDataAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OAuth20UserProfileDataAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        val r = new OAuth20UserProfileDataAuditResourceResolver();
        val token = Mockito.mock(AccessToken.class);
        Mockito.when(token.getId()).thenReturn("CODE");
        Mockito.when(token.getService()).thenReturn(RegisteredServiceTestUtils.getService());
        val service = new OAuthRegisteredService();
        service.setClientId("CLIENTID");
        service.setName("OAUTH");
        service.setId(123);
        val jp = Mockito.mock(JoinPoint.class);
        Mockito.when(jp.getArgs()).thenReturn(new Object[]{ token });
        val result = r.resolveFrom(jp, CollectionUtils.wrap(MODEL_ATTRIBUTE_ID, "id", CLIENT_ID, "clientid", PARAMETER_SERVICE, "service", "scopes", CollectionUtils.wrapSet("email"), "attributes", CollectionUtils.wrap("attributeName", "attributeValue")));
        Assertions.assertTrue(((result.length) > 0));
    }
}

