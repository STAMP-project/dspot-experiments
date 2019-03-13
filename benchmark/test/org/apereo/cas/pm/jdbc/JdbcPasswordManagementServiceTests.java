package org.apereo.cas.pm.jdbc;


import javax.sql.DataSource;
import lombok.val;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.pm.JdbcPasswordManagementConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.pm.PasswordChangeBean;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.pm.config.PasswordManagementConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JdbcPasswordManagementServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreAuditConfiguration.class, CasCoreHttpConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreWebConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreServicesConfiguration.class, CasCoreUtilConfiguration.class, JdbcPasswordManagementConfiguration.class, PasswordManagementConfiguration.class })
@TestPropertySource(locations = { "classpath:/pm.properties" })
public class JdbcPasswordManagementServiceTests {
    @Autowired
    @Qualifier("passwordChangeService")
    private PasswordManagementService passwordChangeService;

    @Autowired
    @Qualifier("jdbcPasswordManagementDataSource")
    private DataSource jdbcPasswordManagementDataSource;

    @Test
    public void verifyUserEmailCanBeFound() {
        val email = passwordChangeService.findEmail("casuser");
        Assertions.assertEquals("casuser@example.org", email);
    }

    @Test
    public void verifyNullReturnedIfUserEmailCannotBeFound() {
        val email = passwordChangeService.findEmail("unknown");
        Assertions.assertNull(email);
    }

    @Test
    public void verifyUserQuestionsCanBeFound() {
        val questions = passwordChangeService.getSecurityQuestions("casuser");
        Assertions.assertEquals(2, questions.size());
        Assertions.assertTrue(questions.containsKey("question1"));
        Assertions.assertTrue(questions.containsKey("question2"));
    }

    @Test
    public void verifyUserPasswordChange() {
        val c = new UsernamePasswordCredential("casuser", "password");
        val bean = new PasswordChangeBean();
        bean.setConfirmedPassword("newPassword1");
        bean.setPassword("newPassword1");
        val res = passwordChangeService.change(c, bean);
        Assertions.assertTrue(res);
    }
}

