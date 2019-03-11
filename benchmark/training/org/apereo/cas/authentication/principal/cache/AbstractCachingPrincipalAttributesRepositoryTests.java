package org.apereo.cas.authentication.principal.cache;


import AttributeMergingStrategy.ADD;
import AttributeMergingStrategy.MULTIVALUED;
import AttributeMergingStrategy.REPLACE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Parent class for test cases around {@link PrincipalAttributesRepository}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
public abstract class AbstractCachingPrincipalAttributesRepositoryTests {
    private static final String MAIL = "mail";

    protected IPersonAttributeDao dao;

    private final PrincipalFactory principalFactory = new DefaultPrincipalFactory();

    private Map<String, List<Object>> attributes;

    private Principal principal;

    @Test
    @SneakyThrows
    public void checkExpiredCachedAttributes() {
        val svc = CoreAuthenticationTestUtils.getRegisteredService();
        Assertions.assertEquals(1, this.principal.getAttributes().size());
        try (val repository = getPrincipalAttributesRepository(TimeUnit.MILLISECONDS.name(), 100)) {
            var repoAttrs = repository.getAttributes(this.principal, svc);
            Assertions.assertEquals(1, repoAttrs.size());
            Assertions.assertTrue(repoAttrs.containsKey(AbstractCachingPrincipalAttributesRepositoryTests.MAIL));
            Thread.sleep(500);
            repository.setMergingStrategy(REPLACE);
            repository.setAttributeRepositoryIds(Arrays.stream(this.dao.getId()).collect(Collectors.toSet()));
            repoAttrs = repository.getAttributes(this.principal, svc);
            Assertions.assertEquals(5, repoAttrs.size());
            Assertions.assertTrue(repoAttrs.containsKey("a2"));
            Assertions.assertEquals("final@example.com", repoAttrs.get(AbstractCachingPrincipalAttributesRepositoryTests.MAIL));
        }
    }

    @Test
    @SneakyThrows
    public void ensureCachedAttributesWithUpdate() {
        val svc = CoreAuthenticationTestUtils.getRegisteredService();
        try (val repository = getPrincipalAttributesRepository(TimeUnit.SECONDS.name(), 5)) {
            Assertions.assertEquals(1, repository.getAttributes(this.principal, svc).size());
            Assertions.assertTrue(repository.getAttributes(this.principal, svc).containsKey(AbstractCachingPrincipalAttributesRepositoryTests.MAIL));
            attributes.clear();
            Assertions.assertTrue(repository.getAttributes(this.principal, svc).containsKey(AbstractCachingPrincipalAttributesRepositoryTests.MAIL));
        }
    }

    @Test
    @SneakyThrows
    public void verifyMergingStrategyWithNoncollidingAttributeAdder() {
        val svc = CoreAuthenticationTestUtils.getRegisteredService();
        try (val repository = getPrincipalAttributesRepository(TimeUnit.SECONDS.name(), 5)) {
            repository.setMergingStrategy(ADD);
            repository.setAttributeRepositoryIds(Collections.singleton("Stub"));
            val repositoryAttributes = repository.getAttributes(this.principal, svc);
            Assertions.assertTrue(repositoryAttributes.containsKey(AbstractCachingPrincipalAttributesRepositoryTests.MAIL));
            Assertions.assertEquals("final@school.com", repositoryAttributes.get(AbstractCachingPrincipalAttributesRepositoryTests.MAIL).toString());
        }
    }

    @Test
    @SneakyThrows
    public void verifyMergingStrategyWithReplacingAttributeAdder() {
        val svc = CoreAuthenticationTestUtils.getRegisteredService();
        try (val repository = getPrincipalAttributesRepository(TimeUnit.SECONDS.name(), 5)) {
            repository.setAttributeRepositoryIds(Collections.singleton("Stub"));
            repository.setMergingStrategy(REPLACE);
            val repositoryAttributes = repository.getAttributes(this.principal, svc);
            Assertions.assertTrue(repositoryAttributes.containsKey(AbstractCachingPrincipalAttributesRepositoryTests.MAIL));
            Assertions.assertEquals("final@example.com", repositoryAttributes.get(AbstractCachingPrincipalAttributesRepositoryTests.MAIL).toString());
        }
    }

    @Test
    @SneakyThrows
    public void verifyMergingStrategyWithMultivaluedAttributeMerger() {
        try (val repository = getPrincipalAttributesRepository(TimeUnit.SECONDS.name(), 5)) {
            repository.setAttributeRepositoryIds(Collections.singleton("Stub"));
            repository.setMergingStrategy(MULTIVALUED);
            val repoAttr = repository.getAttributes(this.principal, CoreAuthenticationTestUtils.getRegisteredService());
            val mailAttr = repoAttr.get(AbstractCachingPrincipalAttributesRepositoryTests.MAIL);
            Assertions.assertTrue((mailAttr instanceof List));
            val values = ((List) (mailAttr));
            Assertions.assertTrue(values.contains("final@example.com"));
            Assertions.assertTrue(values.contains("final@school.com"));
        }
    }
}

