package org.apereo.cas.consent;


import ResultCode.SUCCESS;
import SearchScope.SUB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.ldap.sdk.ModificationType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.config.CasConsentLdapConfiguration;
import org.apereo.cas.services.AbstractRegisteredService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link BaseLdapConsentRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { CasConsentLdapConfiguration.class, RefreshAutoConfiguration.class })
@Tag("Ldap")
@Getter
public abstract class BaseLdapConsentRepositoryTests extends BaseConsentRepositoryTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final String ATTR_NAME = "description";

    private static final String USER_CN = "casuser";

    private static final String USER_DN = "cn=casuser,ou=people,dc=example,dc=org";

    private static final String USER2_CN = "casuser2";

    private static final String USER2_DN = "cn=casuser2,ou=people,dc=example,dc=org";

    private static final Service SVC2 = RegisteredServiceTestUtils.getService2();

    private static final AbstractRegisteredService REG_SVC2 = RegisteredServiceTestUtils.getRegisteredService(BaseLdapConsentRepositoryTests.SVC2.getId());

    private static final String DEF_FILTER = "(objectClass=*)";

    @Autowired
    @Qualifier("consentRepository")
    protected ConsentRepository repository;

    @Test
    @SneakyThrows
    public void verifyConsentDecisionIsNotMistaken() {
        val decision = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER_CN, ATTR);
        decision.setId(1);
        val mod = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER_DN, mod).getResultCode());
        val d = this.repository.findConsentDecision(SVC, REG_SVC, CoreAuthenticationTestUtils.getAuthentication("unknownUser"));
        Assertions.assertNull(d);
        val d2 = this.repository.findConsentDecision(RegisteredServiceTestUtils.getService2(), REG_SVC, CoreAuthenticationTestUtils.getAuthentication(BaseLdapConsentRepositoryTests.USER_CN));
        Assertions.assertNull(d2);
    }

    @Test
    @SneakyThrows
    public void verifyAllConsentDecisionsAreFoundForSingleUser() {
        val decision = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER_CN, ATTR);
        decision.setId(1);
        val mod = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER_DN, mod).getResultCode());
        val decision2 = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER2_CN, ATTR);
        decision2.setId(2);
        val mod2 = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision2));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER2_DN, mod2).getResultCode());
        val d = this.repository.findConsentDecisions(BaseLdapConsentRepositoryTests.USER_CN);
        Assertions.assertNotNull(d);
        Assertions.assertEquals(1, d.size());
        Assertions.assertEquals(BaseLdapConsentRepositoryTests.USER_CN, d.iterator().next().getPrincipal());
    }

    @Test
    @SneakyThrows
    public void verifyAllConsentDecisionsAreFoundForAllUsers() {
        val decision = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER_CN, ATTR);
        decision.setId(1);
        val mod = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER_DN, mod).getResultCode());
        val decision2 = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER2_CN, ATTR);
        decision2.setId(2);
        val mod2 = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision2));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER2_DN, mod2).getResultCode());
        val d = this.repository.findConsentDecisions();
        Assertions.assertNotNull(d);
        Assertions.assertFalse(d.isEmpty());
        Assertions.assertEquals(2, d.size());
    }

    @Test
    @SneakyThrows
    public void verifyConsentDecisionIsStored() {
        val decision = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER_CN, ATTR);
        Assertions.assertTrue(this.repository.storeConsentDecision(decision));
        val r = getConnection().search(BaseLdapConsentRepositoryTests.USER_DN, SUB, BaseLdapConsentRepositoryTests.DEF_FILTER, BaseLdapConsentRepositoryTests.ATTR_NAME);
        Assertions.assertTrue(((r.getEntryCount()) > 0));
        val d = BaseLdapConsentRepositoryTests.MAPPER.readValue(r.getSearchEntry(BaseLdapConsentRepositoryTests.USER_DN).getAttributeValue(BaseLdapConsentRepositoryTests.ATTR_NAME), ConsentDecision.class);
        Assertions.assertNotNull(d);
        Assertions.assertEquals(BaseLdapConsentRepositoryTests.USER_CN, d.getPrincipal());
    }

    @Test
    @SneakyThrows
    public void verifyConsentDecisionIsUpdated() {
        val decision = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER_CN, ATTR);
        decision.setId(1);
        val mod = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER_DN, mod).getResultCode());
        val t = LocalDateTime.now();
        Assertions.assertNotEquals(t, decision.getCreatedDate());
        decision.setCreatedDate(t);
        this.repository.storeConsentDecision(decision);
        val r2 = getConnection().search(BaseLdapConsentRepositoryTests.USER_DN, SUB, BaseLdapConsentRepositoryTests.DEF_FILTER, BaseLdapConsentRepositoryTests.ATTR_NAME);
        Assertions.assertTrue(((r2.getEntryCount()) > 0));
        val d = BaseLdapConsentRepositoryTests.MAPPER.readValue(r2.getSearchEntry(BaseLdapConsentRepositoryTests.USER_DN).getAttributeValue(BaseLdapConsentRepositoryTests.ATTR_NAME), ConsentDecision.class);
        Assertions.assertNotNull(d);
        Assertions.assertEquals(d.getId(), decision.getId());
        Assertions.assertEquals(d.getCreatedDate(), t);
    }

    @Test
    @SneakyThrows
    public void verifyConsentDecisionIsDeleted() {
        val decision = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER_CN, ATTR);
        decision.setId(1);
        val mod = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER_DN, mod).getResultCode());
        val decision2 = BUILDER.build(BaseLdapConsentRepositoryTests.SVC2, BaseLdapConsentRepositoryTests.REG_SVC2, BaseLdapConsentRepositoryTests.USER_CN, ATTR);
        decision2.setId(2);
        val mod2 = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision2));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER_DN, mod2).getResultCode());
        val decision3 = BUILDER.build(SVC, REG_SVC, BaseLdapConsentRepositoryTests.USER2_CN, ATTR);
        decision3.setId(3);
        val mod3 = new com.unboundid.ldap.sdk.Modification(ModificationType.ADD, BaseLdapConsentRepositoryTests.ATTR_NAME, BaseLdapConsentRepositoryTests.MAPPER.writeValueAsString(decision3));
        Assertions.assertEquals(SUCCESS, getConnection().modify(BaseLdapConsentRepositoryTests.USER2_DN, mod3).getResultCode());
        Assertions.assertTrue(this.repository.deleteConsentDecision(decision2.getId(), BaseLdapConsentRepositoryTests.USER_CN));
        val r = getConnection().search(BaseLdapConsentRepositoryTests.USER_DN, SUB, BaseLdapConsentRepositoryTests.DEF_FILTER, BaseLdapConsentRepositoryTests.ATTR_NAME);
        Assertions.assertTrue(((r.getEntryCount()) > 0));
        Assertions.assertEquals(1, r.getSearchEntry(BaseLdapConsentRepositoryTests.USER_DN).getAttributeValues(BaseLdapConsentRepositoryTests.ATTR_NAME).length);
    }
}

