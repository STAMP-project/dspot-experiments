package org.apereo.cas.consent;


import java.util.Map;
import lombok.Getter;
import lombok.val;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.AbstractRegisteredService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link BaseConsentRepositoryTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
@Getter
public abstract class BaseConsentRepositoryTests {
    protected static final DefaultConsentDecisionBuilder BUILDER = new DefaultConsentDecisionBuilder(CipherExecutor.noOpOfSerializableToString());

    protected static final Service SVC = RegisteredServiceTestUtils.getService();

    protected static final AbstractRegisteredService REG_SVC = RegisteredServiceTestUtils.getRegisteredService(BaseConsentRepositoryTests.SVC.getId());

    protected static final Map<String, Object> ATTR = CollectionUtils.wrap("attribute", "value");

    protected static final String CASUSER_2 = "casuser2";

    @Test
    public void verifyConsentDecisionIsNotFound() {
        val repo = getRepository("verifyConsentDecisionIsNotFound");
        val decision = BaseConsentRepositoryTests.BUILDER.build(BaseConsentRepositoryTests.SVC, BaseConsentRepositoryTests.REG_SVC, "casuser", BaseConsentRepositoryTests.ATTR);
        decision.setId(1);
        repo.storeConsentDecision(decision);
        Assertions.assertNull(repo.findConsentDecision(BaseConsentRepositoryTests.SVC, BaseConsentRepositoryTests.REG_SVC, CoreAuthenticationTestUtils.getAuthentication()));
    }

    @Test
    public void verifyConsentDecisionIsFound() {
        val repo = getRepository("verifyConsentDecisionIsFound");
        val decision = BaseConsentRepositoryTests.BUILDER.build(BaseConsentRepositoryTests.SVC, BaseConsentRepositoryTests.REG_SVC, BaseConsentRepositoryTests.CASUSER_2, BaseConsentRepositoryTests.ATTR);
        decision.setId(100);
        repo.storeConsentDecision(decision);
        val d = repo.findConsentDecision(BaseConsentRepositoryTests.SVC, BaseConsentRepositoryTests.REG_SVC, CoreAuthenticationTestUtils.getAuthentication(BaseConsentRepositoryTests.CASUSER_2));
        Assertions.assertNotNull(d);
        Assertions.assertEquals(BaseConsentRepositoryTests.CASUSER_2, d.getPrincipal());
        Assertions.assertTrue(repo.deleteConsentDecision(d.getId(), d.getPrincipal()));
        Assertions.assertNull(repo.findConsentDecision(BaseConsentRepositoryTests.SVC, BaseConsentRepositoryTests.REG_SVC, CoreAuthenticationTestUtils.getAuthentication(BaseConsentRepositoryTests.CASUSER_2)));
    }
}

