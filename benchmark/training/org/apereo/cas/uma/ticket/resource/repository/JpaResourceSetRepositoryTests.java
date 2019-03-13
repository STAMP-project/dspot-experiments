package org.apereo.cas.uma.ticket.resource.repository;


import lombok.val;
import org.apereo.cas.config.CasOAuthUmaJpaConfiguration;
import org.apereo.cas.uma.ticket.resource.ResourceSetPolicy;
import org.apereo.cas.uma.ticket.resource.ResourceSetPolicyPermission;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JpaResourceSetRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Import(CasOAuthUmaJpaConfiguration.class)
@TestPropertySource(properties = "cas.authn.uma.resourceSet.jpa.url=jdbc:hsqldb:mem:cas-uma-resourceset")
public class JpaResourceSetRepositoryTests extends BaseUmaEndpointControllerTests {
    @Autowired
    @Qualifier("umaResourceSetRepository")
    protected ResourceSetRepository umaResourceSetRepository;

    @Test
    public void verifyOperation() {
        var r = JpaResourceSetRepositoryTests.buildTestResource();
        Assertions.assertTrue(umaResourceSetRepository.getAll().isEmpty());
        Assertions.assertFalse(umaResourceSetRepository.getById(r.getId()).isPresent());
        r = umaResourceSetRepository.save(r);
        Assertions.assertFalse(umaResourceSetRepository.getAll().isEmpty());
        Assertions.assertTrue(umaResourceSetRepository.getById(r.getId()).isPresent());
        val perms = new ResourceSetPolicyPermission();
        perms.setSubject("casuser");
        perms.setScopes(CollectionUtils.wrapHashSet("read", "write"));
        perms.setClaims(new java.util.LinkedHashMap(CollectionUtils.wrap("givenName", "CAS")));
        val policy = new ResourceSetPolicy();
        policy.setPermissions(CollectionUtils.wrapHashSet(perms));
        r.setOwner("UMA");
        r.setPolicies(CollectionUtils.wrapHashSet(policy));
        r = umaResourceSetRepository.save(r);
        Assertions.assertEquals("UMA", r.getOwner());
        Assertions.assertFalse(r.getPolicies().isEmpty());
        umaResourceSetRepository.removeAll();
        Assertions.assertTrue(umaResourceSetRepository.getAll().isEmpty());
    }
}

