package org.apereo.cas.pm;


import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.LdapPasswordManagementConfiguration;
import org.apereo.cas.pm.config.PasswordManagementConfiguration;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link LdapPasswordManagementServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Ldap")
@SpringBootTest(classes = { RefreshAutoConfiguration.class, LdapPasswordManagementConfiguration.class, PasswordManagementConfiguration.class, CasCoreAuditConfiguration.class, CasCoreUtilConfiguration.class })
@DirtiesContext
@EnabledIfContinuousIntegration
@TestPropertySource(locations = { "classpath:/ldap-pm.properties" })
public class LdapPasswordManagementServiceTests {
    private static final int LDAP_PORT = 10389;

    @Autowired
    @Qualifier("passwordChangeService")
    private PasswordManagementService passwordChangeService;

    @Test
    public void verifyTokenCreationAndParsing() {
        val token = passwordChangeService.createToken("casuser");
        Assertions.assertNotNull(token);
        val result = passwordChangeService.parseToken(token);
        Assertions.assertEquals("casuser", result);
    }

    @Test
    public void verifyPasswordChangedFails() {
        val credential = new UsernamePasswordCredential("caspm", "123456");
        val bean = new PasswordChangeBean();
        bean.setConfirmedPassword("Mellon");
        bean.setPassword("Mellon");
        Assertions.assertFalse(passwordChangeService.change(credential, bean));
    }

    @Test
    public void verifyFindEmail() {
        val email = passwordChangeService.findEmail("caspm");
        Assertions.assertEquals("caspm@example.org", email);
    }

    @Test
    public void verifyFindSecurityQuestions() {
        val questions = passwordChangeService.getSecurityQuestions("caspm");
        Assertions.assertEquals(2, questions.size());
        Assertions.assertTrue(questions.containsKey("RegisteredAddressQuestion"));
        Assertions.assertEquals("666", questions.get("RegisteredAddressQuestion"));
        Assertions.assertTrue(questions.containsKey("PostalCodeQuestion"));
        Assertions.assertEquals("1776", questions.get("PostalCodeQuestion"));
    }
}

