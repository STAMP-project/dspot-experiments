package org.apereo.cas.authentication.principal;


import CoreAuthenticationTestUtils.CONST_USERNAME;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.resolvers.ChainingPrincipalResolver;
import org.apereo.cas.authentication.principal.resolvers.EchoingPrincipalResolver;
import org.apereo.cas.authentication.principal.resolvers.PersonDirectoryPrincipalResolver;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link PersonDirectoryPrincipalResolver}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@SuppressWarnings("OptionalAssignedToNull")
public class PersonDirectoryPrincipalResolverTests {
    private static final String ATTR_1 = "attr1";

    @Test
    public void verifyNullPrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver();
        val p = resolver.resolve(() -> null, Optional.of(CoreAuthenticationTestUtils.getPrincipal()), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertNull(p);
    }

    @Test
    public void verifyNullAttributes() {
        val resolver = new PersonDirectoryPrincipalResolver(true, CoreAuthenticationTestUtils.CONST_USERNAME);
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(c, null);
        Assertions.assertNull(p);
    }

    @Test
    public void verifyNoAttributesWithPrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(), CoreAuthenticationTestUtils.CONST_USERNAME);
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(c, null);
        Assertions.assertNotNull(p);
    }

    @Test
    public void verifyAttributesWithPrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(), "cn");
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(c, null);
        Assertions.assertNotNull(p);
        Assertions.assertNotEquals(p.getId(), CONST_USERNAME);
        Assertions.assertTrue(p.getAttributes().containsKey("memberOf"));
    }

    @Test
    public void verifyChainingResolverOverwrite() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val chain = new ChainingPrincipalResolver();
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver));
        val attributes = new HashMap<String, Object>();
        attributes.put("cn", "originalCN");
        attributes.put(PersonDirectoryPrincipalResolverTests.ATTR_1, "value1");
        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), Optional.of(CoreAuthenticationTestUtils.getPrincipal(CONST_USERNAME, attributes)), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertEquals(p.getAttributes().size(), ((CoreAuthenticationTestUtils.getAttributeRepository().getPossibleUserAttributeNames().size()) + 1));
        Assertions.assertTrue(p.getAttributes().containsKey(PersonDirectoryPrincipalResolverTests.ATTR_1));
        Assertions.assertTrue(p.getAttributes().containsKey("cn"));
        Assertions.assertNotEquals("originalCN", p.getAttributes().get("cn"));
    }

    @Test
    public void verifyChainingResolver() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val chain = new ChainingPrincipalResolver();
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver));
        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), Optional.of(CoreAuthenticationTestUtils.getPrincipal(CONST_USERNAME, Collections.singletonMap(PersonDirectoryPrincipalResolverTests.ATTR_1, "value"))), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertEquals(p.getAttributes().size(), ((CoreAuthenticationTestUtils.getAttributeRepository().getPossibleUserAttributeNames().size()) + 1));
        Assertions.assertTrue(p.getAttributes().containsKey(PersonDirectoryPrincipalResolverTests.ATTR_1));
    }

    @Test
    public void verifyChainingResolverOverwritePrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val resolver2 = new PersonDirectoryPrincipalResolver(new org.apereo.services.persondir.support.StubPersonAttributeDao(Collections.singletonMap("principal", CollectionUtils.wrap("changedPrincipal"))), "principal");
        val chain = new ChainingPrincipalResolver();
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver, resolver2));
        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), Optional.of(CoreAuthenticationTestUtils.getPrincipal("somethingelse", Collections.singletonMap(PersonDirectoryPrincipalResolverTests.ATTR_1, "value"))), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertNotNull(p);
        Assertions.assertEquals("changedPrincipal", p.getId());
        Assertions.assertEquals(6, p.getAttributes().size());
        Assertions.assertTrue(p.getAttributes().containsKey(PersonDirectoryPrincipalResolverTests.ATTR_1));
        Assertions.assertTrue(p.getAttributes().containsKey("principal"));
    }

    @Test
    public void verifyMultiplePrincipalAttributeNames() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val resolver2 = new PersonDirectoryPrincipalResolver(new org.apereo.services.persondir.support.StubPersonAttributeDao(Collections.singletonMap("something", CollectionUtils.wrap("principal-id"))), " invalid, something");
        val chain = new ChainingPrincipalResolver();
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver, resolver2));
        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), Optional.of(CoreAuthenticationTestUtils.getPrincipal("somethingelse", Collections.singletonMap(PersonDirectoryPrincipalResolverTests.ATTR_1, "value"))), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertNotNull(p);
        Assertions.assertEquals("principal-id", p.getId());
    }

    @Test
    public void verifyMultiplePrincipalAttributeNamesNotFound() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val resolver2 = new PersonDirectoryPrincipalResolver(new org.apereo.services.persondir.support.StubPersonAttributeDao(Collections.singletonMap("something", CollectionUtils.wrap("principal-id"))), " invalid, ");
        val chain = new ChainingPrincipalResolver();
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver, resolver2));
        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), Optional.of(CoreAuthenticationTestUtils.getPrincipal("somethingelse", Collections.singletonMap(PersonDirectoryPrincipalResolverTests.ATTR_1, "value"))), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertNotNull(p);
        Assertions.assertEquals("test", p.getId());
    }
}

