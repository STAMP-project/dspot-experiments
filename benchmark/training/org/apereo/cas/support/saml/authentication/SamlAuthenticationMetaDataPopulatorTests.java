package org.apereo.cas.support.saml.authentication;


import SamlAuthenticationMetaDataPopulator.ATTRIBUTE_AUTHENTICATION_METHOD;
import SamlAuthenticationMetaDataPopulator.AUTHN_METHOD_PASSWORD;
import java.util.HashMap;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.DefaultAuthenticationTransaction;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public class SamlAuthenticationMetaDataPopulatorTests {
    private SamlAuthenticationMetaDataPopulator populator;

    @Test
    public void verifyAuthenticationTypeFound() {
        val credentials = new UsernamePasswordCredential();
        val builder = CoreAuthenticationTestUtils.getAuthenticationBuilder();
        this.populator.populateAttributes(builder, DefaultAuthenticationTransaction.of(credentials));
        val auth = builder.build();
        Assertions.assertEquals(AUTHN_METHOD_PASSWORD, auth.getAttributes().get(ATTRIBUTE_AUTHENTICATION_METHOD));
    }

    @Test
    public void verifyAuthenticationTypeFoundByDefault() {
        val credentials = new SamlAuthenticationMetaDataPopulatorTests.CustomCredential();
        val builder = CoreAuthenticationTestUtils.getAuthenticationBuilder();
        this.populator.populateAttributes(builder, DefaultAuthenticationTransaction.of(credentials));
        val auth = builder.build();
        Assertions.assertNotNull(auth.getAttributes().get(ATTRIBUTE_AUTHENTICATION_METHOD));
    }

    @Test
    public void verifyAuthenticationTypeFoundCustom() {
        val credentials = new SamlAuthenticationMetaDataPopulatorTests.CustomCredential();
        val added = new HashMap<String, String>();
        added.put(SamlAuthenticationMetaDataPopulatorTests.CustomCredential.class.getName(), "FF");
        this.populator.setUserDefinedMappings(added);
        val builder = CoreAuthenticationTestUtils.getAuthenticationBuilder();
        this.populator.populateAttributes(builder, DefaultAuthenticationTransaction.of(credentials));
        val auth = builder.build();
        Assertions.assertEquals("FF", auth.getAttributes().get(ATTRIBUTE_AUTHENTICATION_METHOD));
    }

    private static class CustomCredential implements Credential {
        private static final long serialVersionUID = 8040541789035593268L;

        @Override
        public String getId() {
            return "nobody";
        }
    }
}

