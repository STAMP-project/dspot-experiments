package org.apereo.cas.web.view;


import CasProtocolConstants.VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_AUTHENTICATION_DATE;
import CasProtocolConstants.VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_FROM_NEW_LOGIN;
import CasProtocolConstants.VALIDATION_REMEMBER_ME_ATTRIBUTE_NAME;
import CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL;
import CasViewConstants.MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.EncodingUtils;
import org.apereo.cas.web.AbstractServiceValidateControllerTests;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.apereo.services.persondir.support.StubPersonAttributeDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.View;


/**
 * Unit tests for {@link Cas30ResponseView}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@DirtiesContext
@TestPropertySource(properties = { "cas.clearpass.cacheCredential=true", "cas.clearpass.crypto.enabled=false" })
@Slf4j
@Import(Cas30ResponseViewTests.AttributeRepositoryTestConfiguration.class)
public class Cas30ResponseViewTests extends AbstractServiceValidateControllerTests {
    @Autowired
    @Qualifier("servicesManager")
    protected ServicesManager servicesManager;

    @Autowired
    @Qualifier("cas3ServiceJsonView")
    private View cas3ServiceJsonView;

    @Autowired
    @Qualifier("cas3SuccessView")
    private View cas3SuccessView;

    @Autowired
    @Qualifier("cas3ServiceFailureView")
    private View cas3ServiceFailureView;

    @Test
    public void verifyViewAuthnAttributes() throws Exception {
        val attributes = renderView();
        Assertions.assertTrue(attributes.containsKey(VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_AUTHENTICATION_DATE));
        Assertions.assertTrue(attributes.containsKey(VALIDATION_CAS_MODEL_ATTRIBUTE_NAME_FROM_NEW_LOGIN));
        Assertions.assertTrue(attributes.containsKey(VALIDATION_REMEMBER_ME_ATTRIBUTE_NAME));
    }

    @Test
    public void verifyPasswordAsAuthenticationAttributeCanDecrypt() throws Exception {
        val attributes = renderView();
        Assertions.assertTrue(attributes.containsKey(MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL));
        val encodedPsw = ((String) (attributes.get(MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL)));
        val password = Cas30ResponseViewTests.decryptCredential(encodedPsw);
        val creds = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        Assertions.assertEquals(password, creds.getPassword());
    }

    @Test
    public void verifyProxyGrantingTicketAsAuthenticationAttributeCanDecrypt() throws Exception {
        val attributes = renderView();
        LOGGER.warn("Attributes are [{}]", attributes.keySet());
        Assertions.assertTrue(attributes.containsKey(MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET));
        val encodedPgt = ((String) (attributes.get(MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET)));
        val pgt = Cas30ResponseViewTests.decryptCredential(encodedPgt);
        Assertions.assertNotNull(pgt);
    }

    @Test
    public void verifyViewBinaryAttributes() throws Exception {
        val attributes = renderView();
        Assertions.assertTrue(attributes.containsKey("binaryAttribute"));
        val binaryAttr = attributes.get("binaryAttribute");
        Assertions.assertEquals("binaryAttributeValue", EncodingUtils.decodeBase64ToString(binaryAttr.toString()));
    }

    @TestConfiguration
    public static class AttributeRepositoryTestConfiguration {
        @Bean
        public IPersonAttributeDao attributeRepository() {
            val attrs = CollectionUtils.wrap("uid", CollectionUtils.wrap("uid"), "eduPersonAffiliation", CollectionUtils.wrap("developer"), "groupMembership", CollectionUtils.wrap("adopters"), "binaryAttribute", CollectionUtils.wrap("binaryAttributeValue".getBytes(StandardCharsets.UTF_8)));
            return new StubPersonAttributeDao(((Map) (attrs)));
        }
    }
}

