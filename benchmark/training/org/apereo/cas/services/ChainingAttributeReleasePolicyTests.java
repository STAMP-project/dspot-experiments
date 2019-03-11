package org.apereo.cas.services;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ChainingAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class ChainingAttributeReleasePolicyTests {
    private ChainingAttributeReleasePolicy chain;

    @Test
    public void verifyOperationWithReplaceAndOrder() {
        configureChainingReleasePolicy(10, 1);
        chain.setMergingPolicy("replace");
        val results = chain.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertTrue(results.containsKey("givenName"));
        val values = CollectionUtils.toCollection(results.get("givenName"));
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("CasUserPolicy1", values.iterator().next().toString());
    }

    @Test
    public void verifyOperationWithReplace() {
        chain.setMergingPolicy("replace");
        val results = chain.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertTrue(results.containsKey("givenName"));
        val values = CollectionUtils.toCollection(results.get("givenName"));
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("CasUserPolicy2", values.iterator().next().toString());
    }

    @Test
    public void verifyOperationWithAdd() {
        chain.setMergingPolicy("add");
        val results = chain.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertTrue(results.containsKey("givenName"));
        val values = CollectionUtils.toCollection(results.get("givenName"));
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("CasUserPolicy1", values.iterator().next().toString());
    }

    @Test
    public void verifyOperationWithMultivalued() {
        chain.setMergingPolicy("multivalued");
        val results = chain.getAttributes(CoreAuthenticationTestUtils.getPrincipal(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getRegisteredService());
        Assertions.assertTrue(results.containsKey("givenName"));
        val values = CollectionUtils.toCollection(results.get("givenName"));
        Assertions.assertEquals(2, values.size());
        Assertions.assertTrue(values.contains("CasUserPolicy1"));
        Assertions.assertTrue(values.contains("CasUserPolicy2"));
    }
}

