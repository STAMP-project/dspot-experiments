package org.pac4j.core.profile;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static CommonProfile.SEPARATOR;


/**
 * This class tests the {@link CommonProfile} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class CommonProfileTests implements TestsConstants {
    private static final String ID = "id";

    private static final String ROLE1 = "role1";

    private static final String PERMISSION = "onePermission";

    @Test
    public void testSetId() {
        final CommonProfile userProfile = new CommonProfile();
        Assert.assertNull(userProfile.getId());
        userProfile.setId(CommonProfileTests.ID);
        Assert.assertEquals(CommonProfileTests.ID, userProfile.getId());
    }

    @Test
    public void testAddAttribute() {
        final CommonProfile userProfile = new CommonProfile();
        Assert.assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(TestsConstants.KEY, TestsConstants.VALUE);
        Assert.assertEquals(1, userProfile.getAttributes().size());
        Assert.assertEquals(TestsConstants.VALUE, userProfile.getAttributes().get(TestsConstants.KEY));
    }

    @Test
    public void testAddAttributeMultipleValues() {
        final CommonProfile userProfile = new CommonProfile(true);
        userProfile.addAttribute(TestsConstants.KEY, Arrays.asList("Value1"));
        userProfile.addAttribute(TestsConstants.KEY, Arrays.asList("Value2", "Value3"));
        Assert.assertEquals(1, userProfile.getAttributes().size());
        Assert.assertEquals(Arrays.asList("Value1", "Value2", "Value3"), userProfile.getAttribute(TestsConstants.KEY));
    }

    @Test
    public void testAddAttributeMultipleValuesOldBehaviour() {
        final CommonProfile userProfile = new CommonProfile(false);
        userProfile.addAttribute(TestsConstants.KEY, Arrays.asList("Value1"));
        userProfile.addAttribute(TestsConstants.KEY, Arrays.asList("Value2", "Value3"));
        Assert.assertEquals(1, userProfile.getAttributes().size());
        Assert.assertEquals(Arrays.asList("Value2", "Value3"), userProfile.getAttribute(TestsConstants.KEY));
    }

    @Test
    public void testAddAuthenticationAttribute() {
        final CommonProfile userProfile = new CommonProfile();
        Assert.assertEquals(0, userProfile.getAuthenticationAttributes().size());
        userProfile.addAuthenticationAttribute(TestsConstants.KEY, TestsConstants.VALUE);
        Assert.assertEquals(1, userProfile.getAuthenticationAttributes().size());
        Assert.assertEquals(TestsConstants.VALUE, userProfile.getAuthenticationAttributes().get(TestsConstants.KEY));
    }

    @Test
    public void testAddAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(TestsConstants.KEY, TestsConstants.VALUE);
        final CommonProfile userProfile = new CommonProfile();
        Assert.assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttributes(attributes);
        Assert.assertEquals(1, userProfile.getAttributes().size());
        Assert.assertEquals(TestsConstants.VALUE, userProfile.getAttributes().get(TestsConstants.KEY));
    }

    @Test
    public void testAddAuthenticationAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(TestsConstants.KEY, TestsConstants.VALUE);
        final CommonProfile userProfile = new CommonProfile();
        Assert.assertEquals(0, userProfile.getAuthenticationAttributes().size());
        userProfile.addAuthenticationAttributes(attributes);
        Assert.assertEquals(1, userProfile.getAuthenticationAttributes().size());
        Assert.assertEquals(TestsConstants.VALUE, userProfile.getAuthenticationAttributes().get(TestsConstants.KEY));
    }

    @Test
    public void testUnsafeAddAttribute() {
        final CommonProfile userProfile = new CommonProfile();
        try {
            userProfile.getAttributes().put(TestsConstants.KEY, TestsConstants.VALUE);
        } catch (final UnsupportedOperationException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUnsafeAddAuthenticationAttribute() {
        final CommonProfile userProfile = new CommonProfile();
        try {
            userProfile.getAuthenticationAttributes().put(TestsConstants.KEY, TestsConstants.VALUE);
        } catch (final UnsupportedOperationException e) {
            Assert.fail();
        }
    }

    @Test
    public void testRoles() {
        final CommonProfile profile = new CommonProfile();
        Assert.assertEquals(0, profile.getRoles().size());
        profile.addRole(CommonProfileTests.ROLE1);
        Assert.assertEquals(1, profile.getRoles().size());
        Assert.assertTrue(profile.getRoles().contains(CommonProfileTests.ROLE1));
    }

    @Test
    public void testPermissions() {
        final CommonProfile profile = new CommonProfile();
        Assert.assertEquals(0, profile.getPermissions().size());
        profile.addPermission(CommonProfileTests.PERMISSION);
        Assert.assertEquals(1, profile.getPermissions().size());
        Assert.assertTrue(profile.getPermissions().contains(CommonProfileTests.PERMISSION));
    }

    @Test
    public void testRme() {
        final CommonProfile profile = new CommonProfile();
        Assert.assertFalse(profile.isRemembered());
        profile.setRemembered(true);
        Assert.assertTrue(profile.isRemembered());
    }

    @Test
    public void testTypeId() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(CommonProfileTests.ID);
        Assert.assertEquals((("org.pac4j.core.profile.CommonProfile" + (SEPARATOR)) + (CommonProfileTests.ID)), profile.getTypedId());
    }

    @Test
    public void testNullId() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.setId(null), TechnicalException.class, "id cannot be blank");
    }

    @Test
    public void testBlankRole() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addRole(""), TechnicalException.class, "role cannot be blank");
    }

    @Test
    public void testNullRoles() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addRoles(null), TechnicalException.class, "roles cannot be null");
    }

    @Test
    public void testBlankPermission() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addPermission(""), TechnicalException.class, "permission cannot be blank");
    }

    @Test
    public void testNullPermissions() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addPermissions(null), TechnicalException.class, "permissions cannot be null");
    }

    @Test
    public void serializeProfile() {
        final JavaSerializationHelper helper = new JavaSerializationHelper();
        final CommonProfile profile = new CommonProfile();
        final String s = helper.serializeToBase64(profile);
        final CommonProfile profile2 = ((CommonProfile) (helper.deserializeFromBase64(s)));
        Assert.assertNotNull(profile2);
    }

    @Test
    public void testSetNullLinkedIdWhenAlreadySet() {
        final CommonProfile profile = new CommonProfile();
        profile.setLinkedId("dummyLinkecId");
        profile.setLinkedId(null);
        Assert.assertNull(profile.getLinkedId());
    }

    @Test
    public void testSetNullLinkedIdWhenNotAlreadySet() {
        final CommonProfile profile = new CommonProfile();
        profile.setLinkedId(null);
        Assert.assertNull(profile.getLinkedId());
    }

    @Test
    public void stringifyProfile() {
        try {
            ProfileHelper.getInternalAttributeHandler().setStringify(true);
            final CommonProfile profile = new CommonProfile();
            profile.setId(CommonProfileTests.ID);
            profile.addAttribute(TestsConstants.KEY, true);
            profile.addAttribute(TestsConstants.NAME, 1);
            Assert.assertEquals(true, profile.getAttribute(TestsConstants.KEY));
            Assert.assertEquals(1, profile.getAttributes().get(TestsConstants.NAME));
        } finally {
            ProfileHelper.getInternalAttributeHandler().setStringify(false);
        }
    }
}

