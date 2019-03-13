package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link LdapPasswordSynchronizationAuthenticationPostProcessorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
@Tag("Ldap")
@TestPropertySource(properties = { "cas.authn.passwordSync.ldap[0].ldapUrl=ldap://localhost:10389", "cas.authn.passwordSync.ldap[0].useSsl=false", "cas.authn.passwordSync.ldap[0].baseDn=dc=example,dc=org", "cas.authn.passwordSync.ldap[0].searchFilter=cn={user}", "cas.authn.passwordSync.ldap[0].bindDn=cn=Directory Manager", "cas.authn.passwordSync.ldap[0].bindCredential=password" })
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnabledIfContinuousIntegration
public class LdapPasswordSynchronizationAuthenticationPostProcessorTests {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyOperation() {
        /* Mock the name of the attribute to not interfere with normal ldap ops. */
        val sync = new LdapPasswordSynchronizationAuthenticationPostProcessor(casProperties.getAuthn().getPasswordSync().getLdap().get(0)) {
            @Override
            protected LdapAttribute getLdapPasswordAttribute(final UsernamePasswordCredential credential) {
                return new LdapAttribute("st", credential.getPassword());
            }
        };
        val credentials = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("admin", "password");
        Assertions.assertTrue(sync.supports(credentials));
        sync.process(CoreAuthenticationTestUtils.getAuthenticationBuilder(), DefaultAuthenticationTransaction.of(credentials));
    }
}

