package org.apereo.cas.web.flow;


import TokenConstants.PARAMETER_NAME_TOKEN;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.AbstractCentralAuthenticationServiceTests;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.TokenAuthenticationConfiguration;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.gen.DefaultRandomStringGenerator;
import org.apereo.cas.util.gen.RandomStringGenerator;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasMultifactorAuthenticationWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.flow.config.TokenAuthenticationWebflowConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link TokenAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Import({ CasCoreWebflowConfiguration.class, CasWebflowContextConfiguration.class, CasMultifactorAuthenticationWebflowConfiguration.class, TokenAuthenticationConfiguration.class, TokenAuthenticationWebflowConfiguration.class })
public class TokenAuthenticationActionTests extends AbstractCentralAuthenticationServiceTests {
    private static final RandomStringGenerator RANDOM_STRING_GENERATOR = new DefaultRandomStringGenerator();

    private static final String SIGNING_SECRET = TokenAuthenticationActionTests.RANDOM_STRING_GENERATOR.getNewString(256);

    private static final String ENCRYPTION_SECRET = TokenAuthenticationActionTests.RANDOM_STRING_GENERATOR.getNewString(48);

    @Autowired
    @Qualifier("tokenAuthenticationAction")
    private Action action;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Test
    @SneakyThrows
    public void verifyAction() {
        val g = new org.pac4j.jwt.profile.JwtGenerator<CommonProfile>();
        g.setSignatureConfiguration(new org.pac4j.jwt.config.signature.SecretSignatureConfiguration(TokenAuthenticationActionTests.SIGNING_SECRET, JWSAlgorithm.HS256));
        g.setEncryptionConfiguration(new org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration(TokenAuthenticationActionTests.ENCRYPTION_SECRET, JWEAlgorithm.DIR, EncryptionMethod.A192CBC_HS384));
        val profile = new CommonProfile();
        profile.setId("casuser");
        profile.addAttribute("uid", "uid");
        profile.addAttribute("givenName", "CASUser");
        profile.addAttribute("memberOf", CollectionUtils.wrapSet("system", "cas", "admin"));
        val token = g.generate(profile);
        val request = new MockHttpServletRequest();
        request.addHeader(PARAMETER_NAME_TOKEN, token);
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putServiceIntoFlowScope(context, CoreAuthenticationTestUtils.getWebApplicationService("https://example.token.org"));
        Assertions.assertEquals("success", this.action.execute(context).getId());
    }
}

