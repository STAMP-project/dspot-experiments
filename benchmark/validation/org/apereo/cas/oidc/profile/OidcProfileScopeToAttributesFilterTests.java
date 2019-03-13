package org.apereo.cas.oidc.profile;


import OidcConstants.StandardScopes.ADDRESS;
import OidcConstants.StandardScopes.CUSTOM;
import OidcConstants.StandardScopes.EMAIL;
import OidcConstants.StandardScopes.OFFLINE_ACCESS;
import OidcConstants.StandardScopes.OPENID;
import OidcConstants.StandardScopes.PHONE;
import OidcConstants.StandardScopes.PROFILE;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.services.ChainingAttributeReleasePolicy;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OidcProfileScopeToAttributesFilterTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcProfileScopeToAttributesFilterTests extends AbstractOidcTests {
    @Test
    public void verifyOperationFilterWithoutOpenId() {
        val service = AbstractOidcTests.getOidcRegisteredService();
        val accessToken = Mockito.mock(AccessToken.class);
        val context = new org.pac4j.core.context.J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        val original = CoreAuthenticationTestUtils.getPrincipal();
        val principal = profileScopeToAttributesFilter.filter(CoreAuthenticationTestUtils.getService(), original, service, context, accessToken);
        Assertions.assertEquals(original, principal);
    }

    @Test
    public void verifyOperationFilterWithOpenId() {
        val service = AbstractOidcTests.getOidcRegisteredService();
        val accessToken = Mockito.mock(AccessToken.class);
        Mockito.when(accessToken.getScopes()).thenReturn(CollectionUtils.wrapList(OPENID.getScope(), PHONE.getScope(), PROFILE.getScope(), ADDRESS.getScope(), EMAIL.getScope()));
        service.getScopes().add(EMAIL.getScope());
        service.getScopes().add(ADDRESS.getScope());
        service.getScopes().add(PHONE.getScope());
        service.getScopes().add(PROFILE.getScope());
        val context = new org.pac4j.core.context.J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        val original = CoreAuthenticationTestUtils.getPrincipal(CollectionUtils.wrap("email", "casuser@example.org", "address", "1234 Main Street", "phone", "123445677", "name", "CAS", "gender", "male"));
        val principal = profileScopeToAttributesFilter.filter(CoreAuthenticationTestUtils.getService(), original, service, context, accessToken);
        Assertions.assertTrue(principal.getAttributes().containsKey("name"));
        Assertions.assertTrue(principal.getAttributes().containsKey("address"));
        Assertions.assertTrue(principal.getAttributes().containsKey("gender"));
        Assertions.assertTrue(principal.getAttributes().containsKey("email"));
        Assertions.assertEquals(4, principal.getAttributes().size());
    }

    @Test
    public void verifyOperationRecon() {
        val service = AbstractOidcTests.getOidcRegisteredService();
        service.getScopes().add(ADDRESS.getScope());
        service.getScopes().add(CUSTOM.getScope());
        service.getScopes().add(EMAIL.getScope());
        service.getScopes().add(OFFLINE_ACCESS.getScope());
        service.getScopes().add(OPENID.getScope());
        service.getScopes().add(PHONE.getScope());
        service.getScopes().add(PROFILE.getScope());
        profileScopeToAttributesFilter.reconcile(service);
        val policy = service.getAttributeReleasePolicy();
        Assertions.assertTrue((policy instanceof ChainingAttributeReleasePolicy));
        val chain = ((ChainingAttributeReleasePolicy) (policy));
        Assertions.assertEquals(5, chain.size());
    }
}

