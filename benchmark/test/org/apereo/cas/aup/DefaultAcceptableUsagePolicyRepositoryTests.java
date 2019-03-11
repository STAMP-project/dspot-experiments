package org.apereo.cas.aup;


import AcceptableUsagePolicyProperties.Scope.AUTHENTICATION;
import AcceptableUsagePolicyProperties.Scope.GLOBAL;
import lombok.Getter;
import lombok.val;
import org.apereo.cas.configuration.model.support.aup.AcceptableUsagePolicyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * This is {@link DefaultAcceptableUsagePolicyRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
public class DefaultAcceptableUsagePolicyRepositoryTests extends BaseAcceptableUsagePolicyRepositoryTests {
    @Autowired
    @Qualifier("acceptableUsagePolicyRepository")
    protected AcceptableUsagePolicyRepository acceptableUsagePolicyRepository;

    @Test
    public void verifyActionDefaultGlobal() {
        val properties = new AcceptableUsagePolicyProperties();
        properties.setScope(GLOBAL);
        DefaultAcceptableUsagePolicyRepositoryTests.verifyAction(properties);
    }

    @Test
    public void verifyActionDefaultAuthentication() {
        val properties = new AcceptableUsagePolicyProperties();
        properties.setScope(AUTHENTICATION);
        DefaultAcceptableUsagePolicyRepositoryTests.verifyAction(properties);
    }
}

