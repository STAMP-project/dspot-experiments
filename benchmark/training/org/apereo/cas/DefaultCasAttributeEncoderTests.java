package org.apereo.cas;


import CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL;
import CasViewConstants.MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET;
import java.util.Map;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * This is test cases for {@link DefaultCasProtocolAttributeEncoder}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class DefaultCasAttributeEncoderTests extends BaseCasCoreTests {
    private Map<String, Object> attributes;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Test
    public void checkNoPublicKeyDefined() {
        val service = RegisteredServiceTestUtils.getService("testDefault");
        val encoder = new org.apereo.cas.authentication.support.DefaultCasProtocolAttributeEncoder(this.servicesManager, CipherExecutor.noOpOfStringToString());
        val encoded = encoder.encodeAttributes(this.attributes, this.servicesManager.findServiceBy(service));
        Assertions.assertEquals(((this.attributes.size()) - 2), encoded.size());
    }

    @Test
    public void checkAttributesEncodedCorrectly() {
        val service = RegisteredServiceTestUtils.getService("testencryption");
        val encoder = new org.apereo.cas.authentication.support.DefaultCasProtocolAttributeEncoder(this.servicesManager, CipherExecutor.noOpOfStringToString());
        val encoded = encoder.encodeAttributes(this.attributes, this.servicesManager.findServiceBy(service));
        Assertions.assertEquals(encoded.size(), this.attributes.size());
        checkEncryptedValues(MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL, encoded);
        checkEncryptedValues(MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET, encoded);
    }
}

