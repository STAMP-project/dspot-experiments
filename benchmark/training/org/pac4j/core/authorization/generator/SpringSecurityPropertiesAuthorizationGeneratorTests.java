package org.pac4j.core.authorization.generator;


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import static SpringSecurityPropertiesAuthorizationGenerator.DISABLED;
import static SpringSecurityPropertiesAuthorizationGenerator.ENABLED;


/**
 * This class tests {@link SpringSecurityPropertiesAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class SpringSecurityPropertiesAuthorizationGeneratorTests implements TestsConstants {
    private static final String SEPARATOR = ",";

    private static final String ROLE2 = "role2";

    @Test
    public void testOnlyPassword() {
        final Set<String> roles = test("");
        Assert.assertEquals(0, roles.size());
    }

    @Test
    public void testEnabled() {
        final Set<String> roles = test(((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (ENABLED)));
        Assert.assertEquals(0, roles.size());
    }

    @Test
    public void testDisabled() {
        final Set<String> roles = test(((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (DISABLED)));
        Assert.assertEquals(0, roles.size());
    }

    @Test
    public void testOneRole() {
        final Set<String> roles = test(((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (TestsConstants.ROLE)));
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.contains(TestsConstants.ROLE));
    }

    @Test
    public void testOneRoleEnabled() {
        final Set<String> roles = test(((((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (TestsConstants.ROLE)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR)) + (ENABLED)));
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.contains(TestsConstants.ROLE));
    }

    @Test
    public void testOneRoleDisabled() {
        final Set<String> roles = test(((((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (TestsConstants.ROLE)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR)) + (DISABLED)));
        Assert.assertEquals(0, roles.size());
    }

    @Test
    public void testTwoRoles() {
        final Set<String> roles = test(((((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (TestsConstants.ROLE)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.ROLE2)));
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains(TestsConstants.ROLE));
        Assert.assertTrue(roles.contains(SpringSecurityPropertiesAuthorizationGeneratorTests.ROLE2));
    }

    @Test
    public void testTwoRolesEnabled() {
        final Set<String> roles = test(((((((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (TestsConstants.ROLE)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.ROLE2)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR)) + (ENABLED)));
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains(TestsConstants.ROLE));
        Assert.assertTrue(roles.contains(SpringSecurityPropertiesAuthorizationGeneratorTests.ROLE2));
    }

    @Test
    public void testTwoRolesDisabled() {
        final Set<String> roles = test(((((((SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR) + (TestsConstants.ROLE)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.ROLE2)) + (SpringSecurityPropertiesAuthorizationGeneratorTests.SEPARATOR)) + (DISABLED)));
        Assert.assertEquals(0, roles.size());
    }
}

